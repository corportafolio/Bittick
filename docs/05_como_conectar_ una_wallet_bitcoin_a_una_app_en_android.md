# Cómo Conectar una Wallet Bitcoin (UniSat) a Bittick — Flujo Real Android

> **Documenta la habilidad esencial de conexión de wallet** — no es nuevo, es cómo funciona la app hoy (commit `c6bc510`).
> 
> **Patrones de arquitectura involucrados**: Ver [Skill 03: Patrones de Arquitectura](../.opencode/skills/03_patrones-arquitectura.md) — Patrones 1 (Nonce Verification), 2 (Sesión 7 Días), 4 (Caché Dual), 5 (Free-Tier HTTP 300).

---

## Resumen Ejecutivo

Bittick conecta wallets **UniSat** en Android usando **nonces del servidor + firma manual + dirección pegada a mano**. El callback `unisat://response` **nunca se dispara** en Unisat móvil (limitación conocida, 33 intentos documentados). Por tanto:

1. App pide nonce al servidor (`GET /api/auth/nonce`)
2. Abre Unisat via deep link `signMessage` con ese nonce
3. Usuario firma 2 veces en Unisat → **vuelve manual a la app**
4. App detecta retorno (`ON_RESUME` + `checkPendingConnection()`) → muestra **Diálogo 1: Confirmación**
5. Usuario toca **CONTINUAR** → muestra **Diálogo 2: Pegar dirección**
6. Usuario pega dirección de Unisat → toca **CONECTAR**
7. App envía dirección a `POST /api/auth/verify-wallet`
8. Servidor busca inscripciones en `ordinals.com/address/{addr}`, filtra Bittick Agents
9. Si encuentra → retorna inscripciones + bot seleccionado + tier + `botImageUrl`
10. App descarga imagen del bot, guarda **sesión 7 días** en `SharedPreferences` encriptadas

---

## Arquitectura del Flujo

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        FLUJO CONEXIÓN WALLET Bittick                          │
└─────────────────────────────────────────────────────────────────────────────┘

┌──────────────┐     Nonce      ┌──────────────┐     Deep Link      ┌──────────┐
│   App        │ ─────────────► │   Server     │ ─────────────────► │  Unisat  │
│ (WalletVM)   │  GET /nonce    │ (authRouter) │  signMessage       │ (Mobile) │
└──────────────┘                └──────────────┘                     └──────────┘
      │                                                            │
      │  Guarda nonce + "unisat" en SharedPrefs                     │
      │                            Usuario firma 2 veces            │
      │                                                            │
      │                         ┌─────────────────────────────────┘
      │                         ▼ USUARIO VUELVE MANUAL (recientes/launcher)
      │                    ┌──────────────────┐
      │                    │  ON_RESUME       │
      │                    │ checkPending()   │
      │                    └────────┬─────────┘
      │                             │ nonce existe
      │                             ▼
      │                    ┌─────────────────────┐
      │                    │  DIÁLOGO 1          │
      │                    │  Confirmación       │
      │                    │  ✓ Paso 1: Abrir    │
      │                    │  ✓ Paso 2: Firmar   │
      │                    │     [ CONTINUAR ]   │
      │                    └─────────┬───────────┘
      │                              │
      │                              ▼
      │                    ┌─────────────────────┐
      │                    │  DIÁLOGO 2          │
      │                    │  Pegar Dirección    │
      │                    │  [bc1p...] [PEGAR]  │
      │                    │     [ CONECTAR ]    │
      │                    └─────────┬───────────┘
      │                              │
      │                              ▼ POST /verify-wallet {address}
      │                    ┌─────────────────────┐
      │                    │   SERVIDOR          │
      │                    │  fetch ordinals.com │
      │                    │  filtra Bittick IDs │
      │                    └─────────┬───────────┘
      │                              │
      │                              ▼ {inscriptions[], selectedBot, tier, botImageUrl}
      │                    ┌─────────────────────┐
      │                    │   APP               │
      │                    │  downloadBotImage() │
      │                    │  saveSession(7d)    │
      │                    └─────────────────────┘
```

---

## Componentes Principales (Código Real)

| Archivo | Función Clave | Qué Hace |
|---------|---------------|----------|
| `WalletViewModel.kt` | `connectWallet()` | Inicia flujo: pide nonce, guarda pending, abre Unisat |
| `WalletViewModel.kt` | `onConnectWithAddress()` | Envía dirección a `/verify-wallet`, descarga imagen bot, guarda sesión |
| `WalletViewModel.kt` | `checkPendingConnection()` | Al `ON_RESUME`: si hay nonce pendiente → muestra Diálogo 1 |
| `WalletViewModel.kt` | `downloadAndCacheBotImage(botNum)` | Fetch `BASE_URL/api/auth/bot-image/{NN}` → Base64 → sesión |
| `WalletDeepLinkHandler.kt` | `requestSignature()` | Construye deep link `unisat://request?method=signMessage...` |
| `BittickPreferences.kt` | `setPendingNonce()`, `saveWalletSession()` | Persistencia estado pendiente + sesión 7 días |
| `authRouter.js` | `GET /nonce`, `POST /verify-wallet` | Genera nonce, valida firma (opcional), busca inscripciones en ordinals.com |
| `WalletSessionManager.kt` | `auditSelectedInscription()` | Auditoría semanal: verifica si inscripción sigue en wallet |

---

## Estado de la Conexión (WalletState)

```kotlin
// WalletViewModel.kt líneas 27-46
data class WalletState(
    val isConnecting: Boolean = false,
    val connectedAddress: String? = null,
    val inscriptions: List<InscriptionInfo> = emptyList(),
    val selectedInscription: InscriptionInfo? = null,
    val botImageUrl: String? = null,        // Base64 de la imagen del bot
    val error: String? = null,
    val isPremium: Boolean = false,
    val tier: String? = null,
    val botNumber: Int? = null,
    val verified: Boolean = false,
    // Flujo manual Unisat (2 diálogos)
    val showConfirmationDialog: Boolean = false,   // Diálogo 1
    val showAddressInputDialog: Boolean = false,   // Diálogo 2
    val pendingNonce: String? = null,
    val pendingSignature: String? = null,
    val tempAddressInput: String = "",
    val showTemporaryMessage: String? = null
)
```

**Transiciones clave:**
- `connectWallet()` → `isConnecting=true`, `pendingNonce=nonce`, `showConfirmationDialog=false`
- Unisat firma → callback success → `pendingSignature=sig`, `showConfirmationDialog=true`
- `onContinueConfirmation()` → `showConfirmationDialog=false`, `showAddressInputDialog=true`
- `onConnectWithAddress()` → `isConnecting=true`, POST `/verify-wallet`
- Éxito → `verified=true`, `connectedAddress=addr`, `inscriptions=list`, `botImageUrl=base64`, `showAddressInputDialog=false`
- Cancelar → `clearPendingConnection()`, todo a `false`

---

## Diálogo 1: Confirmación de Conexión

**Propósito**: Checkpoint visual — confirma que el usuario ya firmó en Unisat antes de pedir la dirección.

**Ubicación**: `WalletScreen.kt` líneas 214-230 (renderizado condicional por `showConfirmationDialog`)

**Estructura**:
```
┌─────────────────────────────────────┐
│  Confirmar Conexión                 │
├─────────────────────────────────────┤
│  ✅  Paso 1: Abrir Unisat          │
│  ✅  Paso 2: Firmas completadas    │
│                                     │
│           [ CONTINUAR ]             │
└─────────────────────────────────────┘
```

**Lógica** (`WalletViewModel.kt:118-124`):
```kotlin
fun onContinueConfirmation() {
    _state.value = _state.value.copy(
        showConfirmationDialog = false,
        showAddressInputDialog = true
    )
}
```
**NO hace**: validar firma, consultar blockchain, leer portapapeles. **Solo** cambia UI al siguiente diálogo.

---

## Diálogo 2: Pegar Dirección de Wallet

**Propósito**: **Único mecanismo** para obtener la dirección pública del usuario. Sin este paso, **la wallet NO queda conectada**.

**Ubicación**: `WalletScreen.kt` líneas 232-280 (renderizado por `showAddressInputDialog`)

**Estructura**:
```
┌─────────────────────────────────────┐
│  Pega tu dirección de Unisat        │
├─────────────────────────────────────┤
│  ┌─────────────────────────────┐   │
│  │ bc1p...                [PEGAR]│   │
│  └─────────────────────────────┘   │
│                                     │
│           [ CONECTAR ]              │
└─────────────────────────────────────┘
```

**Componentes**:
- **TextField**: `tempAddressInput` (estado local), validación longitud ≥ 26
- **Botón PEGAR**: Lee portapapeles Android → rellena TextField
- **Botón CONECTAR**: Habilitado solo si `tempAddressInput.isNotBlank()` (alpha 0.5 si vacío)

**Acción crítica** (`WalletViewModel.kt:130-198`):
```kotlin
fun onConnectWithAddress() {
    val address = _state.value.tempAddressInput.trim()
    // POST /api/auth/verify-wallet {address}
    // Si OK: downloadAndCacheBotImage(botNum) → saveWalletSession(7 días)
    // Estado final: verified=true, connectedAddress, inscriptions, botImageUrl=base64
}
```

---

## Verificación en Servidor (`/verify-wallet`)

**Archivo**: `bittick-server/src/auth/authRouter.js` líneas 99-148

```javascript
router.post('/verify-wallet', async (req, res) => {
  const { address, signature, nonce } = req.body;
  
  // 1. Validar nonce si vino firma (opcional en flujo manual)
  if (signature && nonce) {
    if (!validateAndConsumeNonce(address, nonce)) 
      return res.status(400).json({ exito: false, error: 'Invalid nonce' });
    if (!verifyMessageSignature(address, 'Conectar a Bittick', signature))
      return res.status(401).json({ exito: false, error: 'Invalid signature' });
  }
  
  // 2. Buscar inscripciones en ordinals.com
  const ownership = await findAllBittickInscriptions(address);
  
  // 3. Si no hay Bittick Agents → verified: false (free tier)
  if (!ownership.verified) {
    return res.json({ exito: true, data: { verified: false, inscriptions: [], ... }});
  }
  
  // 4. Hay bots → seleccionar primero, guardar en tradingStore, retornar datos
  const selected = ownership.inscriptions[0];
  await tradingStore.setUserInscriptions(address, inscriptionsWithSelected);
  await tradingStore.setVerifiedOwner(address, selected.num, selected.inscriptionId);
  res.json({
    exito: true,
    data: {
      verified: true,
      inscriptions: ownership.inscriptions,
      count: ownership.inscriptions.length,
      selectedInscriptionId: selected.inscriptionId,
      selectedBotNum: selected.num,
      tier: selected.tier,
      botImageUrl: selected.botImageUrl,
      message: `${ownership.inscriptions.length} Bittick Agent(s) verificado(s)`
    }
  });
});
```

**`findAllBittickInscriptions`** (líneas 50-87):
- Fetch `https://ordinals.com/address/${address}`
- Regex `href=\/inscription\/([a-f0-9]+i\d+)` extrae inscriptionIds
- Filtra contra `bittickCollection.hasInscriptionId()` (los 100 IDs hardcodeados)
- Retorna array con `num`, `inscriptionId`, `tier`, `botImageUrl`

---

## Sesión 7 Días y Auditoría Semanal

**Patrón completo en [Skill 03: Patrón 2](../.opencode/skills/03_patrones-arquitectura.md#2-sesi%C3%B3n-7-d%C3%ADas-con-auditor%C3%ADa-semanal-7-day-session--weekly-audit)**

### Guardado de Sesión (`BittickPreferences.kt:105-129`)
```kotlin
fun saveWalletSession(
    address: String,
    selectedInscriptionId: String,
    botNumber: Int,
    tier: String,
    botImageBase64: String
) {
    val expiresAt = System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000L
    val session = WalletSession(address, selectedInscriptionId, botNumber, tier, botImageBase64, expiresAt)
    prefs.edit().putString(KEY_WALLET_SESSION, gson.toJson(session)).apply()
    
    // Compatibilidad hacia atrás
    setWalletAddress(address)
    setSelectedInscriptionId(selectedInscriptionId)
    setIsPremium(true)
    setBotNumber(botNumber)
}
```

### Restauración al Inicio (`WalletViewModel.kt:240-254`)
```kotlin
fun restoreSessionIfValid() {
    val session = preferences.getWalletSession()
    if (session != null) {
        if (session.expiresAt > System.currentTimeMillis()) {
            _state.value = sessionManager.restoreSession(session)
        } else {
            // Expirada → disparar auditoría
            viewModelScope.launch { runWeeklyAudit() }
        }
    } else {
        // Sin sesión → verificar si hay conexión pendiente (retorno manual Unisat)
        checkPendingConnection()
    }
}
```

### Auditoría Semanal (`WalletSessionManager.kt:27-54`)
```kotlin
suspend fun auditSelectedInscription(address: String, inscriptionId: String): AuditResult {
    val response = api.fetchInscriptions(address)
    val stillHasIt = response.body()?.data?.inscriptions?.any { it.inscriptionId == inscriptionId } == true
    if (stillHasIt) {
        prefs.extendSessionExpiry(7)  // Extiende 7 días más
        AuditResult.Success
    } else {
        prefs.clearWalletSession()
        AuditResult.InscriptionSold
    }
}
```

---

## Detección de Retorno Manual (ON_RESUME)

**`WalletViewModel.kt:256-265`**:
```kotlin
fun checkPendingConnection() {
    val nonce = preferences.getPendingNonce()
    if (nonce != null) {
        _state.value = _state.value.copy(
            showConfirmationDialog = true,
            pendingNonce = nonce
        )
    }
}
```

**`MainActivity.kt` / `WalletScreen.kt`**: `DisposableEffect` con `LifecycleEventObserver` en `ON_RESUME` llama `walletViewModel.checkPendingConnection()`.

---

## Imagen del Bot: Descarga y Caché

**Patrón completo en [Skill 03: Patrón 4](../.opencode/skills/03_patrones-arquitectura.md#4-cach%C3%A9-dual-de-im%C3%A1genes-dual-image-cache)**

### Al Verificar Wallet (`WalletViewModel.kt:267-285`)
```kotlin
private suspend fun downloadAndCacheBotImage(botNum: Int): String? = withContext(Dispatchers.IO) {
    try {
        val baseUrl = ApiClient.BASE_URL.trimEnd('/')  // Evita doble slash
        val url = "$baseUrl/api/auth/bot-image/${botNum.toString().padStart(2, '0')}"
        val inputStream = URL(url).openStream()
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream.close()
        bitmap?.let {
            val baos = ByteArrayOutputStream()
            it.compress(Bitmap.CompressFormat.PNG, 90, baos)
            Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP)
        }
    } catch (e: Exception) {
        log("ERROR descargando imagen bot $botNum: ${e.message}")
        null
    }
}
```

### En Caché Dual (`BittickImageCache.kt:72-105`)
```kotlin
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

---

## Free-Tier vs Premium (HTTP 300)

**Patrón completo en [Skill 03: Patrón 5](../.opencode/skills/03_patrones-arquitectura.md#5-detecci%C3%B3n-de-free-tier-v%C3%ADa-http-300-free-tier-detection-via-http-300)**

- **Wallet verificada PERO sin Bittick Agent** → Servidor retorna **HTTP 300** en `/opportunities`, `/positions`, `/bot/status`
- **App interpreta 300 como estado válido** → `isFreeTier = true`, NO setea `error` global
- **UI**: `TradingScreen` muestra `FreeTierCard` con CTA "Conecta un Bittick Agent"
- **Wallet con bot** → HTTP 200 con datos completos → `isPremium = true`

---

## Checklist de Conexión Exitosa (Usuario)

- [ ] Toca "Unisat" en pantalla principal
- [ ] Unisat se abre, firma **2 veces** (signMessage)
- [ ] **Vuelve MANUAL a Bittick** (gestos/recientes/launcher)
- [ ] Ve Diálogo 1 con 2 checkmarks verdes
- [ ] Toca **"CONTINUAR"**
- [ ] Ve Diálogo 2 "Pega tu dirección de Unisat"
- [ ] **Copia dirección en Unisat** (perfil → copiar) ANTES o DURANTE
- [ ] Toca **"PEGAR"** en Diálogo 2
- [ ] Verifica que la dirección aparezca en el campo
- [ ] Toca **"CONECTAR"**
- [ ] Ve wallet conectada con dirección completa en burbuja principal

**Si omite CUALQUIER paso**: La wallet NO queda conectada. No hay atajos, no hay auto-completado, no hay callback mágico.

---

## Archivos Clave (Referencia Rápida)

| Archivo | Responsabilidad |
|---------|-----------------|
| `WalletViewModel.kt` | Orquestación completa del flujo (líneas 63-198, 240-285) |
| `WalletScreen.kt` | UI de los 2 diálogos (líneas 214-280) |
| `WalletDeepLinkHandler.kt` | Deep link Unisat `signMessage` |
| `BittickPreferences.kt` | Persistencia: pendingNonce, walletSession (7d), walletAddress |
| `WalletSessionManager.kt` | Auditoría semanal, restauración sesión |
| `authRouter.js` | `/nonce`, `/verify-wallet`, `findAllBittickInscriptions()` |
| `bittickCollection.js` | 100 IDs Bittick Agents, `hasInscriptionId()` |
| `ApiService.kt` | Endpoints Retrofit: `getNonce`, `verifyWallet`, `fetchInscriptions` |
| `Models.kt` | `VerifyWalletRequest`, `VerifyWalletResponse`, `InscriptionInfo` |

---

## Relación con Otros Documentos

| Documento | Qué Documenta |
|-----------|---------------|
| **Doc 06** (`06_Flujo-Conexion-Unisat.md`) | Detalle visual de los 2 diálogos, secuencia completa, evidencia técnica del callback fallido |
| **Doc 07** (`07_como-traer-imagenes...`) | Caché dual de imágenes (ordinals.com + server), Base64 en SharedPreferences |
| **Doc 04** (`04_licencia-premium...`) | Modelo de licencia premium, verificación 24h WorkManager, anti-tamper |
| **Skill 03** (`03_patrones-arquitectura.md`) | Patrones 1, 2, 4, 5 — arquitectura que coordina este flujo |

---

## Notas Técnicas para Desarrolladores

### Variables de Estado Críticas (SharedPreferences)
| Clave | Valor | Cuándo se Limpia |
|-------|-------|------------------|
| `pending_nonce` | UUID único | Al tocar CONECTAR en Diálogo 2 |
| `pending_wallet_type` | `"unisat"` | Al tocar CONECTAR en Diálogo 2 |
| `wallet_session` | JSON WalletSession (7d) | Al desconectar, vender inscripción, expirar sin auditoría exitosa |

### Wallet Guardada (Base de Datos Room — `WalletSession` en SharedPrefs)
```kotlin
// BittickPreferences.kt líneas 90-103
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
```

### Endpoints de Autenticación (ApiService.kt)
```kotlin
@GET("api/auth/nonce")
suspend fun getNonce(@Query("address") address: String): Response<NonceResponse>

@POST("api/auth/verify-wallet")
suspend fun verifyWallet(@Body body: VerifyWalletRequest): Response<VerifyWalletResponse>

@GET("api/auth/fetch-inscriptions")
suspend fun fetchInscriptions(@Header("x-wallet-address") address: String): Response<FetchInscriptionsResponse>

@GET("api/auth/wallet-inscriptions")
suspend fun getWalletInscriptions(@Header("x-wallet-address") address: String): Response<WalletInscriptionsResponse>

@POST("api/auth/select-inscription")
suspend fun selectInscription(@Header("x-wallet-address") address: String, @Body body: SelectInscriptionRequest): Response<SelectInscriptionResponse>
```

---

**Documento actualizado para Bittick (commit `c6bc510`)**  
**Versión**: Basada en código real — `WalletViewModel.kt`, `WalletScreen.kt`, `authRouter.js`, `bittickCollection.js`  
**Fecha**: 2026-07-17