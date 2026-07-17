# Patrones de Arquitectura — Bittick

Este documento describe los **patrones de arquitectura esenciales** que la app Bittick ya implementa. No son "nuevos" — son los patrones que coordinan el sistema. Se documentan aquí para referencia cruzada desde los docs técnicos (05, 06, 07, 04).

---

## Índice de Patrones

1. [Verificación de Wallet por Nonce del Servidor](#1-verificación-de-wallet-por-nonce-del-servidor)
2. [Sesión 7 Días con Auditoría Semanal](#2-sesión-7-días-con-auditoría-semanal)
3. [Polling Dual Independiente](#3-polling-dual-independiente)
4. [Caché Dual de Imágenes](#4-caché-dual-de-imágenes)
5. [Detección de Free-Tier vía HTTP 300](#5-detección-de-free-tier-vía-http-300)

---

## 1. Verificación de Wallet por Nonce del Servidor (Server-Side Nonce Verification)

### Propósito
Autenticar la wallet del usuario **sin depender del callback de Unisat** (`unisat://response`), que **nunca se dispara** en Android móvil. El servidor genera un nonce, la app lo firma via Unisat (deep link `signMessage`), el usuario pega la dirección manualmente, y el servidor verifica ownership real consultando ordinals.com.

### Por Qué Es Esencial
- **Unisat móvil no implementa callback** — 33 intentos documentados en `attempt_history.md` confirman que `unisat://response` nunca llega
- **Ownership real = inscripciones en ordinals.com** — no la firma criptográfica sola; el servidor valida que la dirección posea un Bittick Agent
- **Dirección manual = única forma** — no hay API en Unisat para leer la dirección programáticamente

### Flujo Esencial (Código Clave)

```kotlin
// WalletViewModel.connectWallet()
fun connectWallet() {
    viewModelScope.launch {
        // 1. Nonce del servidor
        val nonceResponse = ApiClient.apiService.getNonce("sign")
        val nonce = nonceResponse.body()!!.data!!.nonce
        
        // 2. Guarda estado pendiente
        preferences.setPendingNonce(nonce)
        preferences.setPendingWalletType("unisat")
        
        // 3. Abre Unisat via deep link
        deepLinkHandler.requestSignature(nonce) { result ->
            result.onSuccess { signature ->
                // 4. Usuario firmó → muestra Dialog 1 (confirmación)
                _state.value = _state.value.copy(
                    pendingSignature = signature,
                    showConfirmationDialog = true
                )
            }
        }
    }
}

// WalletViewModel.onConnectWithAddress() — tras pegar dirección
fun onConnectWithAddress() {
    val address = _state.value.tempAddressInput.trim()
    viewModelScope.launch {
        // 5. Verifica en servidor (firma opcional en flujo manual)
        val response = ApiClient.apiService.verifyWallet(VerifyWalletRequest(address))
        // 6. Servidor consulta ordinals.com/address/{addr} → filtra 100 IDs Bittick
        // 7. Si encuentra → retorna inscriptions[], selectedInscriptionId, tier, botImageUrl
        val botImageBase64 = downloadAndCacheBotImage(botNum)  // Patrón 4
        preferences.saveWalletSession(address, inscriptionId, botNum, tier, botImageBase64)
    }
}
```

```javascript
// authRouter.js — servidor
router.post('/verify-wallet', async (req, res) => {
    const { address, signature, nonce } = req.body;
    
    // Nonce opcional en flujo manual
    if (signature && nonce && !validateAndConsumeNonce(address, nonce)) {
        return res.status(400).json({ exito: false, error: 'Invalid nonce' });
    }
    
    // Ownership REAL: consulta ordinals.com
    const ownership = await findAllBittickInscriptions(address);
    if (!ownership.verified) {
        return res.json({ exito: true, data: { verified: false, ... }});
    }
    
    // Guarda en tradingStore + retorna datos
    await tradingStore.setVerifiedOwner(address, ownership.inscriptions[0].num, ownership.inscriptions[0].inscriptionId);
    res.json({ exito: true, data: { verified: true, inscriptions: ownership.inscriptions, ... }});
});
```

### Coordinación con Otros Patrones

| Patrón | Relación |
|--------|----------|
| **Sesión 7 Días (2)** | Crea la `WalletSession` inicial que este patrón mantiene |
| **Caché Dual (4)** | `downloadAndCacheBotImage()` descarga y cachea imagen del bot tras verificación exitosa |
| **Free-Tier (5)** | Si servidor no encuentra inscripciones → wallet verificada pero sin bot → free tier en trading |

### Qué Pasa Si No Se Usa
- Usuario nunca queda "conectado" — callback Unisat nunca llega
- Sin ownership verificado → cualquiera podría fingir dirección
- Modelo de licencia (Bittick Agent = acceso premium) se rompe

### Referencias
- **Doc 05**: Flujo completo con diálogos
- **Doc 06**: Diálogos 1 y 2 en detalle visual
- **WalletViewModel.kt**: `connectWallet()`, `onConnectWithAddress()`, `checkPendingConnection()`
- **WalletDeepLinkHandler.kt**: Construcción deep link `signMessage`
- **authRouter.js**: `/nonce`, `/verify-wallet`, `findAllBittickInscriptions()`
- **bittickCollection.js**: `INSCRIPTION_ID_SET` (100 IDs fuente de verdad)

---

## 2. Sesión 7 Días con Auditoría Semanal (7-Day Session + Weekly Audit)

### Propósito
Mantener al usuario autenticado 7 días sin fricción, pero **verificar semanalmente que sigue poseyendo la inscripción** que usó para conectarse. Si la vendió/transfirió, la sesión se invalida automáticamente.

### Por Qué Es Esencial
- **Licencia = posesión de inscripción** — si vende el Agent, pierde acceso premium
- **7 días = balance UX vs seguridad** — no molestar cada apertura, pero verificar periódico
- **Auditoría server-side** — consulta ordinals.com (fuente de verdad), no cache local

### Flujo Esencial (Código Clave)

```kotlin
// BittickPreferences.kt — Sesión con expiración
data class WalletSession(
    val address: String,
    val selectedInscriptionId: String,
    val botNumber: Int,
    val tier: String,
    val botImageBase64: String,
    val expiresAt: Long
) {
    val isExpired: Boolean get() = expiresAt < System.currentTimeMillis()
    val daysUntilExpiry: Long get() = maxOf(0, (expiresAt - System.currentTimeMillis()) / MS_PER_DAY)
}

fun saveWalletSession(address, inscriptionId, botNum, tier, botImageBase64) {
    val expiresAt = System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000L
    // ... guarda JSON en SharedPreferences
}

fun extendSessionExpiry(days: Int = 7) {
    // Actualiza expiresAt = now + 7 días
}
```

```kotlin
// WalletSessionManager.kt — Auditoría semanal
suspend fun auditSelectedInscription(address: String, inscriptionId: String): AuditResult {
    val response = api.fetchInscriptions(address)  // GET /api/auth/fetch-inscriptions
    val stillHasIt = response.body()?.data?.inscriptions?.any { it.inscriptionId == inscriptionId } == true
    
    return if (stillHasIt) {
        prefs.extendSessionExpiry(7)
        AuditResult.Success
    } else {
        prefs.clearWalletSession()
        AuditResult.InscriptionSold
    }
}
```

```kotlin
// WalletViewModel.kt — Restauración al inicio + auditoría
fun restoreSessionIfValid() {
    val session = preferences.getWalletSession()
    if (session != null) {
        if (!session.isExpired) {
            _state.value = sessionManager.restoreSession(session)  // WalletState completo
        } else {
            viewModelScope.launch { runWeeklyAudit() }  // Expirada → auditar
        }
    } else {
        checkPendingConnection()  // Retorno manual Unisat
    }
}

suspend fun runWeeklyAudit(): Boolean {
    val session = preferences.getWalletSession() ?: return false
    val result = sessionManager.auditSelectedInscription(session.address, session.selectedInscriptionId)
    when (result) {
        AuditResult.Success -> _state.value = _state.value.copy(showTemporaryMessage = "Wallet verificada: 7 días más")
        AuditResult.InscriptionSold -> { preferences.clearWalletSession(); _state.value = WalletState().copy(showTemporaryMessage = "Inscripción vendida: reconecte wallet") }
        AuditResult.NetworkError -> _state.value = _state.value.copy(showTemporaryMessage = "Error de red en verificación")
    }
    return result == AuditResult.Success
}
```

### Coordinación con Otros Patrones

| Patrón | Relación |
|--------|----------|
| **Nonce Verification (1)** | Crea la sesión inicial (`saveWalletSession` tras `verifyWallet`) |
| **Caché Dual (4)** | `botImageBase64` viaja dentro de `WalletSession` — sobrevive reinicios sin re-descargar |
| **Refresh Inscriptions** | Usa mismo endpoint `/fetch-inscriptions` que la auditoría |
| **Free-Tier (5)** | Si auditoría falla (inscripción vendida) → sesión limpia → próximo trading load da 300 |

### Qué Pasa Si No Se Usa
- Sesión infinita → usuario vende Agent pero sigue teniendo acceso premium
- Sin verificación periódica → modelo de licencia (1 Agent = 1 acceso) se rompe
- Cache local no detecta transferencias on-chain

### Referencias
- **WalletSessionManager.kt**: `auditSelectedInscription()`, `restoreSession()`
- **BittickPreferences.kt**: `WalletSession`, `saveWalletSession()`, `extendSessionExpiry()`, `clearWalletSession()`
- **WalletViewModel.kt**: `restoreSessionIfValid()`, `runWeeklyAudit()`
- **authRouter.js**: `/fetch-inscriptions` (endpoint compartido con refresh)

---

## 3. Polling Dual Independiente (Dual Independent Polling)

### Propósito
Mantener datos de trading frescos sin bloquear UI. **Tres jobs independientes** corren cada 60s: oportunidades, klines (velas), y ticker (precio actual). Cambio de intervalo de gráfico reinicia solo el polling de klines.

### Por Qué Es Esencial
- **Aislamiento de fallos** — fallo en klines no detiene ticker ni oportunidades
- **Cambio de intervalo limpio** — `changeChartInterval()` cancela job anterior e inicia nuevo
- **Separación de concerns** — `chartStatus` (estado gráfico) ≠ `error` (error global)

### Flujo Esencial (Código Clave)

```kotlin
// TradingViewModel.kt — Tres jobs independientes
private var pollingJob: Job? = null
private var klinesPollingJob: Job? = null
private var tickerPollingJob: Job? = null

init {
    loadAll()
    startPolling()        // Oportunidades + positions + bot status
    startKlinesPolling()  // Velas + zonas
    startTickerPolling()  // Precio actual
}

private fun startKlinesPolling(interval: String = _state.value.chartInterval) {
    klinesPollingJob?.cancel()
    klinesPollingJob = viewModelScope.launch {
        while (isActive) {
            loadKlines(interval)
            delay(60_000L)
        }
    }
}

// CAMBIO DE INTERVALO — reinicio limpio
fun changeChartInterval(interval: String) {
    if (interval != _state.value.chartInterval) {
        startKlinesPolling(interval)  // Cancela anterior, inicia nuevo
    }
}

// loadKlines() NO toca error global — solo chartStatus
private suspend fun loadKlines(interval: String) {
    _state.value = _state.value.copy(chartLoading = true, chartStatus = "cargando velas $interval...")
    try {
        val response = api.getKlines(interval, 500)
        if (response.isSuccessful && response.body()?.exito == true) {
            _state.value = _state.value.copy(
                klines = response.body()!!.data,
                chartInterval = interval,
                chartLoading = false,
                chartStatus = "${response.body()!!.data.size} velas recibidas"
            )
            loadZones(interval, response.body()!!.data)
        } else {
            // SOLO chartStatus — NO error global
            _state.value = _state.value.copy(
                chartLoading = false,
                chartStatus = "error servidor: ${response.code()}"
            )
        }
    } catch (e: Exception) {
        _state.value = _state.value.copy(
            chartLoading = false,
            chartStatus = "error conexion: ${e.localizedMessage}"
        )
    }
}
```

```kotlin
// Detección Free-Tier en polling de oportunidades
private suspend fun fetchNewOpportunities() {
    val response = api.getTradingOpportunities(walletAddress = getWalletAddress())
    if (response.isSuccessful || response.code() == 300) {
        val isFreeTier = response.code() == 300
        if (isFreeTier) _state.value = _state.value.copy(isFreeTier = true)  // HTTP 300 = free tier
        // ... procesa oportunidades
    }
}
```

### Coordinación con Otros Patrones

| Patrón | Relación |
|--------|----------|
| **Free-Tier (5)** | `fetchNewOpportunities()` detecta HTTP 300 → setea `isFreeTier` |
| **Nonce Verification (1)** | `getWalletAddress()` para headers `x-wallet-address` viene de sesión (patrón 2) |
| **Caché Dual (4)** | `botImageUrl` en TopAppBar viene de `WalletViewModel` via `MainActivity` |

### Qué Pasa Si No Se Usa
- Gráfico estático (velas viejas) o vacío si falló carga inicial
- Precio no actualiza → PnL incorrecto
- Oportunidades no aparecen → usuario no ve señales
- Si `loadKlines()` escribiera en `error` global: **banner de error tapa la card de free-tier**

### Referencias
- **TradingViewModel.kt**: `startPolling()`, `startKlinesPolling()`, `startTickerPolling()`, `changeChartInterval()`, `loadKlines()`, `fetchNewOpportunities()`
- **TradingScreen.kt**: Chips de intervalo llaman `changeChartInterval()`, muestra `chartStatus` y `isFreeTier`
- **Doc 04**: `LicenseTokenManager` verifica tier cada 24h (WorkManager) — complementa este polling

---

## 4. Caché Dual de Imágenes (Dual Image Cache)

### Propósito
Dos fuentes de imágenes, dos estrategias de caché, un acceso unificado:
- **Inscripciones** → `ordinals.com/content/{id}` (rate limited, cache obligatorio)
- **Bots** → `server/api/auth/bot-image/{NN}` (directo, cache para sesión)

Ambas guardan **Base64 en SharedPreferences** para acceso instantáneo offline.

### Por Qué Es Esencial
- **ordinals.com**: Sin API key, rate limit estricto (~10 req/min), HTML parsing frágil
- **Server bots**: Imágenes pequeñas (PNG ~5KB), acceso directo, cache para 7 días
- **Base64 en SharedPreferences**: Simple, persistente, sin Room, lectura instantánea

### Flujo Esencial (Código Clave)

```kotlin
// BittickImageCache.kt — Inscripciones (ordinals.com)
suspend fun getImage(inscriptionId: String): Result<String> = withContext(Dispatchers.IO) {
    // 1. Memoria
    cache[inscriptionId]?.let { return@withContext Result.success(it) }
    // 2. Disco (SharedPreferences)
    prefs.getString(inscriptionId, null)?.let { cache[inscriptionId] = it; return@withContext Result.success(it) }
    // 3. Red
    val url = "https://ordinals.com/content/$inscriptionId"
    val bytes = client.newCall(Request.Builder().url(url).build()).execute().body?.bytes()
    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    val base64 = bitmapToBase64(bitmap)
    cache[inscriptionId] = base64
    prefs.edit().putString(inscriptionId, base64).apply()
    Result.success(base64)
}

// BittickImageCache.kt — Bots (servidor)
suspend fun getBotImage(botNum: Int): Result<String> = withContext(Dispatchers.IO) {
    val key = "bot_$botNum"
    cache[key]?.let { return@withContext Result.success(it) }
    prefs.getString(key, null)?.let { cache[key] = it; return@withContext Result.success(it) }
    
    val url = "$serverBaseUrl/api/auth/bot-image/${botNum.toString().padStart(2, '0')}"
    val bytes = serverClient.newCall(Request.Builder().url(url).build()).execute().body?.bytes()
    val base64 = bitmapToBase64(BitmapFactory.decodeByteArray(bytes, 0, bytes.size))
    cache[key] = base64
    prefs.edit().putString(key, base64).apply()
    Result.success(base64)
}
```

```kotlin
// WalletViewModel.kt — Descarga imagen bot al verificar wallet
private suspend fun downloadAndCacheBotImage(botNum: Int): String? = withContext(Dispatchers.IO) {
    val baseUrl = ApiClient.BASE_URL.trimEnd('/')
    val url = "$baseUrl/api/auth/bot-image/${botNum.toString().padStart(2, '0')}"
    val bitmap = BitmapFactory.decodeStream(URL(url).openStream())
    bitmap?.let { bitmapToBase64(it) }  // Se guarda en WalletSession (7 días)
}
```

### Coordinación con Otros Patrones

| Patrón | Relación |
|--------|----------|
| **Nonce Verification (1)** | `downloadAndCacheBotImage()` se llama **tras** `verifyWallet` exitoso |
| **Sesión 7 Días (2)** | `botImageBase64` viaja dentro de `WalletSession` — persiste 7 días |
| **Polling (3)** | `botImageUrl` (Base64) se pasa a `TradingScreen` via `MainActivity` |

### Qué Pasa Si No Se Usa
- Cada apertura → descarga de ordinals.com (lento, rate limited, falla offline)
- TradingScreen sin imagen de bot en TopAppBar hasta descarga
- UX degradada: placeholders grises, parpadeo

### Referencias
- **BittickImageCache.kt**: `getImage()`, `getBotImage()`, `bitmapToBase64()`, `clearCache()`
- **WalletViewModel.kt**: `downloadAndCacheBotImage()`, `loadBotImage()`
- **Doc 07**: Flujo completo imágenes + parallel download con Semaphore
- **authRouter.js**: `/api/auth/bot-image/{NN}` endpoint server

---

## 5. Detección de Free-Tier vía HTTP 300 (Free-Tier Detection via HTTP 300)

### Propósito
El servidor usa **HTTP 300 (Multiple Choices)** como **señal semántica**, no error, para indicar: "wallet conectada pero sin inscripción Bittick" → free tier. La app interpreta 300 como estado válido y muestra UI limitada.

### Por Qué Es Esencial
- **No es error** — 300 no rompe la app, es un estado de negocio
- **Semántica HTTP correcta** — 300 = "múltiples opciones" (premium vs free)
- **Separación de concerns** — `isFreeTier` drive UI, `error` drive banners de red

### Flujo Esencial (Código Clave)

```javascript
// tradingRouter.js — servidor
router.get('/opportunities', async (req, res) => {
    const address = req.headers['x-wallet-address'];
    const verified = tradingStore.isVerifiedOwner(address);  // tradingStore.setVerifiedOwner() en verify-wallet
    
    if (!verified) {
        // HTTP 300 = Free Tier (wallet conectada pero sin bot)
        return res.status(300).json({ exito: true, data: [] });
    }
    
    // Premium: retorna oportunidades completas
    res.json({ exito: true, data: opportunities });
});
```

```kotlin
// TradingViewModel.kt — cliente
private suspend fun fetchNewOpportunities() {
    val response = api.getTradingOpportunities(walletAddress = getWalletAddress())
    if (response.isSuccessful || response.code() == 300) {
        val isFreeTier = response.code() == 300
        if (isFreeTier) {
            _state.value = _state.value.copy(isFreeTier = true)  // Estado de negocio
        }
        // ... procesa oportunidades
    }
    // NOTA: NO setea error global para 300
}

fun loadAll() {
    val oppResponse = api.getTradingOpportunities(walletAddress = addr)
    val isFreeTier = oppResponse.code() == 300
    _state.value = _state.value.copy(
        isFreeTier = isFreeTier,
        isPremium = !isFreeTier && addr != null
    )
}
```

```kotlin
// TradingScreen.kt — UI
@Composable
fun TradingScreen(...) {
    val isFreeTier = tradingState.isFreeTier
    
    if (isFreeTier) {
        FreeTierCard(onUpgradeClick = { /* navega a WalletScreen */ })
    }
    
    // Gráfico usa chartStatus, NO error global
    ChartSection(
        chartStatus = tradingState.chartStatus,
        // error global solo para errores de red reales
    )
}
```

### Coordinación con Otros Patrones

| Patrón | Relación |
|--------|----------|
| **Polling (3)** | `fetchNewOpportunities()` cada 60s re-verifica free-tier |
| **Nonce Verification (1)** | Wallet verificada pero sin bot → free tier (no error) |
| **Sesión 7 Días (2)** | Si auditoría falla (inscripción vendida) → sesión limpia → próximo load da 300 |
| **Gráfico (3)** | `loadKlines()` **no toca `error` global** → free-tier card sigue visible aunque fallen velas |

### Qué Pasa Si No Se Usa
- HTTP 300 tratado como error → banner de error rojo tapa UI
- Usuario no sabe que está en free tier ni cómo upgradear
- `isPremium`/`isFreeTier` inconsistentes → features premium rotas

### Referencias
- **TradingViewModel.kt**: `fetchNewOpportunities()`, `loadAll()`, `loadKlines()` (no escribe error)
- **TradingScreen.kt**: `FreeTierCard`, `ChartSection` usa `chartStatus`
- **tradingRouter.js**: `/opportunities` retorna 300 si no verificado
- **authRouter.js**: `tradingStore.setVerifiedOwner()` en `/verify-wallet` y `/select-inscription`

---

## Matriz de Coordinación entre Patrones

| | Nonce Verify (1) | Sesión 7d (2) | Polling (3) | Caché Dual (4) | Free-Tier (5) |
|---|---|---|---|---|---|
| **Nonce Verify (1)** | — | Crea sesión | Provee wallet address | Descarga bot image | Verified sin bot = free |
| **Sesión 7d (2)** | Inicia sesión | — | Header x-wallet-address | botImageBase64 en sesión | Expira/vendida → 300 |
| **Polling (3)** | Usa address | Usa sesión | — | botImageUrl en TopAppBar | Detecta 300 cada 60s |
| **Caché Dual (4)** | Post-verificación | Persiste en sesión | — | — | — |
| **Free-Tier (5)** | Sin bot = 300 | Vendida = 300 | Re-verifica 60s | — | chartStatus ≠ error |

---

## Referencias a Archivos Clave

| Patrón | Archivos Principales |
|--------|---------------------|
| 1. Nonce Verify | `WalletViewModel.kt`, `WalletDeepLinkHandler.kt`, `authRouter.js`, `bittickCollection.js` |
| 2. Sesión 7d | `WalletSessionManager.kt`, `BittickPreferences.kt`, `WalletViewModel.kt` |
| 3. Polling | `TradingViewModel.kt`, `TradingScreen.kt` |
| 4. Caché Dual | `BittickImageCache.kt`, `WalletViewModel.kt` |
| 5. Free-Tier | `TradingViewModel.kt`, `TradingScreen.kt`, `tradingRouter.js`, `authRouter.js` |

---

## Notas de Mantenimiento

- **Si cambias el flujo de wallet**: Actualiza Patrones 1 y 2 juntos (están acoplados por `WalletSession`)
- **Si cambias endpoints de trading**: Verifica Patrón 5 (códigos HTTP) y Patrón 3 (polling)
- **Si agregas nueva fuente de imágenes**: Extiende Patrón 4, no crees caché separada
- **Docs 05/06/07/04** referencian estos patrones — mantener sincronizados