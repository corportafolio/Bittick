# Cómo traer imágenes y detectar inscripciones de Bittick Agents desde ordinals.com

> **Documenta la habilidad esencial de imágenes e inscripciones** — no es nueva, es cómo funciona la app hoy (commit `c6bc510`).
> 
> **Patrones de arquitectura**: Ver [Skill 03: Patrones de Arquitectura](../.opencode/skills/03_patrones-arquitectura.md) — Patrones 4 (Caché Dual) y 1 (Nonce Verification).

---

## Fuente de la Verdad

**La fuente oficial y definitiva de los 100 IDs de Bittick Agents es:**

```
docs/03_IDs-coleccion-Bittick-Agent.md
```

Este documento contiene los 100 bots (Bot #00 a Bot #99) con su número de inscripción, ID de inscripción, tx genesis y altura genesis. **Si hay alguna duda sobre un ID, siempre consultar el documento 03.**

---

## Dónde está la lista en el código

La lista de los 100 IDs está hardcodeada en el servidor Node.js:

```
bittick-server/src/auth/bittickCollection.js
```

Este archivo contiene:
- `BOTS` — Array con los 100 objetos bot (num, inscriptionId, txGenesis, blockHeight, tier)
- `INSCRIPTION_ID_SET` — Set de los 100 inscriptionId para búsqueda rápida O(1)
- Funciones helper: `getBotByInscriptionId()`, `hasInscriptionId()`, `getAllInscriptionIds()`, `getAllInscriptionsWithInfo()`
- `FOUNDER_NUMS = [0, 11, 22, 33, 44, 55, 66, 77, 88, 99]` — Los 10 bots FOUNDER

**IMPORTANTE:** Este archivo JS es la versión en código de la lista del documento 03. Deben estar sincronizados. Si se agrega o modifica un bot en el doc 03, se debe actualizar también en `bittickCollection.js`.

---

## Dónde está la lista en la documentación

| Archivo | Contenido |
|---------|-----------|
| `docs/03_IDs-coleccion-Bittick-Agent.md` | **FUENTE DE LA VERDAD** — Los 100 bots completos con número, ID, tx genesis, altura |
| `docs/07_como-traer-imagenes-Bittick-agents-desde-el-servidor-ordinals.md` (este archivo) | Referencia y flujo de obtención de imágenes |
| `bittick-server/docs/04_IDs-coleccion-Bittick-Agent.md` | Copia del doc 03 en el directorio del servidor |

---

## URL base para imágenes

```
https://ordinals.com/content/{inscriptionId}
```

Retorna los bytes PNG directamente. No requiere API key. No requiere autenticación.

---

## Cómo obtener las inscripciones de una wallet

### Fuente: ordinals.com

Para saber qué inscripciones posee una wallet, se consulta:

```
https://ordinals.com/address/{bitcoin-address}
```

Esta página retorna un HTML con todas las inscripciones de la wallet. Las inscripciones están en los atributos `href` de los enlaces `<a href=/inscription/{id}>`.

### Flujo en el servidor (`authRouter.js`)

1. El servidor recibe la dirección de la wallet
2. Hace fetch de `https://ordinals.com/address/{address}`
3. Parsea el HTML y extrae los inscription IDs de los href `/inscription/{id}`
4. Compara contra los 100 IDs de `bittickCollection.js` usando `hasInscriptionId()`
3. Retorna solo las que coinciden con la colección Bittick Agent

```javascript
// authRouter.js líneas 50-87
async function findAllBittickInscriptions(address) {
  const response = await fetch(`https://ordinals.com/address/${address}`, {
    headers: { 'User-Agent': 'Bittick-Server/1.0' }
  });
  const html = await response.text();
  const regex = /href=\/inscription\/([a-f0-9]+i\d+)/g;
  const userInscriptionIds = [];
  let match;
  while ((match = regex.exec(html)) !== null) {
    userInscriptionIds.push(match[1]);
  }
  const found = [];
  for (const userId of userInscriptionIds) {
    if (hasInscriptionId(userId)) {
      const bot = getBotByInscriptionId(userId);
      found.push({
        num: bot.num,
        inscriptionId: userId,
        tier: bot.tier,
        botImageUrl: `/api/auth/bot-image/${bot.num.toString().padStart(2, '0')}`
      });
    }
  }
  if (found.length === 0) {
    return { verified: false, inscriptions: [], error: 'NO_BOT_FOUND' };
  }
  return { verified: true, inscriptions: found, error: null };
}
```

### Flujo en la app Android

1. Al conectar wallet → llamar `GET /api/auth/wallet-inscriptions` con header `x-wallet-address`
2. El servidor retorna las inscripciones que coinciden con la colección
3. La app muestra las inscripciones en la UI (`InscriptionList` en `WalletScreen.kt`)
4. El usuario selecciona una → llamar `POST /api/auth/select-inscription`

---

## Refrescar Inscripciones (`refreshInscriptions()`)

**Nueva función en `WalletViewModel.kt` (líneas 325-341):**

```kotlin
fun refreshInscriptions() {
    val address = _state.value.connectedAddress
    if (address != null && address.isNotBlank()) {
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.fetchInscriptions(address)
                if (response.isSuccessful && response.body()?.data != null) {
                    val inscriptions = response.body()!!.data!!.inscriptions
                    _state.value = _state.value.copy(inscriptions = inscriptions)
                    log("INSCRIPCIONES REFRESCADAS: ${inscriptions.size}")
                }
            } catch (e: Exception) {
                log("ERROR refrescando inscripciones: ${e.message}")
            }
        }
    }
}
```

**Cuándo se usa:**
- Usuario toca botón "Refrescar" en `WalletScreen` (icono refresh en lista de inscripciones)
- Tras seleccionar otra inscripción y volver
- Para detectar si el usuario compró/vendió bots sin cerrar la app

**Endpoint servidor:** `GET /api/auth/fetch-inscriptions` (mismo que usa la auditoría semanal — ver Skill 03, Patrón 2)

---

## Conteo de Inscripciones en UI

**En `WalletScreen.kt` (línea ~200):**

```kotlin
Text(
    text = "Seleccionar inscripción (${walletState.inscriptions.size} inscripciones)",
    // ...
)
```

**Por qué es importante:**
- Feedback visual inmediato: usuario ve cuántos bots tiene
- Si es 0 → free tier (no debería pasar si pasó verify-wallet, pero puede si vendió todos)
- Coherencia con servidor: `count` en `VerifyWalletResponse.data.count`

---

## Código Kotlin para traer UNA imagen (inscripción)

```kotlin
import android.graphics.BitmapFactory
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

fun fetchBittickImage(inscriptionId: String): android.graphics.Bitmap? {
    val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    val request = Request.Builder()
        .url("https://ordinals.com/content/$inscriptionId")
        .get()
        .build()

    val response = client.newCall(request).execute()
    val bytes = response.body?.bytes() ?: return null

    if (bytes.isEmpty()) return null

    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}
```

---

## Traer las 100 imágenes en paralelo (rápido)

Descargar 10 a la vez con `Semaphore` para no saturar ordinals.com:

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit

suspend fun fetchAllBittickImages(
    ids: List<String>
): Map<String, android.graphics.Bitmap> = coroutineScope {

    val semaphore = Semaphore(10) // máximo 10 descargas concurrentes
    val results = mutableMapOf<String, android.graphics.Bitmap>()

    ids.map { id ->
        async(Dispatchers.IO) {
            semaphore.withPermit {
                try {
                    val bitmap = fetchBittickImage(id)
                    if (bitmap != null) {
                        synchronized(results) {
                            results[id] = bitmap
                        }
                    }
                    delay(50) // pausa 50ms entre descargas para no bloquear
                } catch (e: Exception) {
                    // ignorar errores individuales
                }
            }
        }
    }.awaitAll()

    results
}
```

---

## Caché Dual de Imágenes (Patrón 4 — Skill 03)

**Documentación completa en [Skill 03: Patrón 4](../.opencode/skills/03_patrones-arquitectura.md#4-cach%C3%A9-dual-de-im%C3%A1genes-dual-image-cache)**

### Dos Fuentes, Dos Estrategias

| Fuente | Endpoint | Método | Cache Key | Archivo |
|--------|----------|--------|-----------|---------|
| **Inscripciones** | `https://ordinals.com/content/{inscriptionId}` | `BittickImageCache.getImage()` | `inscriptionId` | `BittickImageCache.kt:36-70` |
| **Bots (sesión)** | `http://192.168.101.74:4001/api/auth/bot-image/{NN}` | `BittickImageCache.getBotImage()` | `bot_NN` | `BittickImageCache.kt:72-105` |
| **Bots (init sesión)** | Same | `WalletViewModel.downloadAndCacheBotImage()` | `bot_NN` | `WalletViewModel.kt:267-285` |

### Flujo Esencial

**INSCRIPCIONES (WalletScreen → InscriptionList):**
```
1. Usuario ve lista → para cada inscripción sin cache:
2. getImage(inscriptionId) → fetch ordinals.com → Base64 → SharedPreferences + memory
3. UI lee de cache → instantáneo
```

**BOTS (Sesión + TradingScreen):**
```
1. verify-wallet retorna botImageUrl: "/api/auth/bot-image/05"
2. downloadAndCacheBotImage(botNum=5) → fetch server → Base64 → SharedPreferences
3. botImageBase64 guardado en WalletSession (7 días)
4. TradingScreen recibe botImageUrl (Base64) via MainActivity → muestra en TopAppBar
5. WalletScreen usa getBotImage(botNum) para mostrar en lista
```

### Implementación en `BittickImageCache.kt`

```kotlin
@Singleton
class BittickImageCache @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val cache = ConcurrentHashMap<String, String>()
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()
    private val serverClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()
    private val serverBaseUrl = "http://192.168.101.74:4001"

    // Inscripciones desde ordinals.com
    suspend fun getImage(inscriptionId: String): Result<String> = withContext(Dispatchers.IO) {
        // 1. Memoria (ConcurrentHashMap)
        // 2. Disco (SharedPreferences "bittick_image_cache")
        // 3. Red → ordinals.com/content/{id}
        // 4. Bitmap → Base64 → guarda en memoria + disco
    }

    // Bots desde servidor Bittick
    suspend fun getBotImage(botNum: Int): Result<String> = withContext(Dispatchers.IO) {
        // 1. Memoria (key = "bot_$botNum")
        // 2. Disco (SharedPreferences "bittick_image_cache")
        // 3. Red → server/api/auth/bot-image/{NN}
        // 4. Bitmap → Base64 → guarda en memoria + disco
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP)
    }
}
```

---

## Imagen del Bot en TopAppBar (TradingScreen)

**En `TradingScreen.kt` (líneas ~80-100):**

```kotlin
// Recibe botImageUrl (Base64) desde MainActivity
val botImageUrl = botImageUrl  // Base64 string

TopAppBar(
    navigationIcon = {
        botImageUrl?.let { base64 ->
            val bitmap = base64ToBitmap(base64)  // Decodifica Base64 → Bitmap
            Icon(
                painter = BitmapPainter(bitmap),
                contentDescription = "Bot image",
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color(0xFFF7931A), CircleShape)
            )
        }
    },
    // ...
)

fun base64ToBitmap(base64: String): Bitmap? {
    val bytes = Base64.decode(base64, Base64.NO_WRAP)
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}
```

**En `MainActivity.kt`:** Pasa `botImageUrl` desde `WalletViewModel.state.botImageUrl` a `TradingScreen` y `WalletScreen`.

---

## Seleccionar Inscripción (`selectInscription`)

**En `WalletViewModel.kt` (líneas 287-314):**

```kotlin
fun selectInscription(inscription: InscriptionInfo) {
    viewModelScope.launch {
        val address = _state.value.connectedAddress ?: return@launch
        try {
            val response = ApiClient.apiService.selectInscription(
                address = address,
                body = SelectInscriptionRequest(inscription.inscriptionId)
            )
            if (response.isSuccessful && response.body()?.exito == true) {
                preferences.setSelectedInscriptionId(inscription.inscriptionId)
                preferences.setIsPremium(inscription.tier == "FOUNDER")
                preferences.setBotNumber(inscription.num)

                _state.value = _state.value.copy(
                    selectedInscription = inscription,
                    isPremium = inscription.tier == "FOUNDER",
                    tier = inscription.tier,
                    botNumber = inscription.num
                )
                loadBotImage(inscription.inscriptionId)  // Usa BittickImageCache.getImage()
            }
        } catch (e: Exception) {
            _state.value = _state.value.copy(
                error = "Error seleccionando inscripcion: ${e.message}"
            )
        }
    }
}
```

**En servidor (`authRouter.js:150-184`):**
```javascript
router.post('/select-inscription', async (req, res) => {
  const address = req.headers['x-wallet-address'];
  const { inscriptionId } = req.body;
  const inscriptions = tradingStore.getUserInscriptions(address);
  const match = inscriptions.find(i => i.inscription_id === inscriptionId);
  tradingStore.selectInscription(address, inscriptionId);
  await tradingStore.setVerifiedOwner(address, match.bot_num, match.inscription_id);
  res.json({
    exito: true,
    data: {
      selectedInscriptionId: match.inscription_id,
      selectedBotNum: match.bot_num,
      tier: match.tier,
      botImageUrl: `/api/auth/bot-image/${match.bot_num.toString().padStart(2, '0')}`
    }
  });
});
```

---

## Flujo Completo Resumido

```
1. Al conectar wallet → servidor fetch ordinals.com/address/{wallet} → obtiene inscripciones
2. Servidor filtra contra los 100 IDs de bittickCollection.js
3. Servidor retorna solo las que coinciden con la colección Bittick Agent
4. App muestra inscripciones en la UI (InscriptionList)
5. Para cada inscripción que NO esté en caché → descargar imagen de ordinals.com/content/{id}
6. Guardar imagen en SharedPreferences como Base64 (caché dual)
7. Para mostrar → leer de SharedPreferences (instantáneo, sin red)
8. Usuario selecciona inscripción → POST /select-inscription
9. Servidor actualiza tradingStore, retorna botImageUrl del bot seleccionado
10. App descarga imagen del bot (downloadAndCacheBotImage) → Base64 → WalletSession (7 días)
11. TradingScreen muestra imagen en TopAppBar (Base64 → Bitmap → 28dp con borde naranja)
```

---

## Notas Importantes

- **ordinals.com no tiene API key ni rate limit documentado**, pero 10 concurrentes es seguro
- **Las imágenes son PNG de ~200x200px**, pesan entre 5KB y 50KB cada una
- **SharedPreferences soporta hasta ~1MB por archivo**, 100 imágenes de 50KB = ~5MB total. Si excede, usar Room o caché en disco
- **La primera carga tarda ~5-10 segundos**. Después es instantáneo desde caché
- **La lista de IDs en `bittickCollection.js` DEBE coincidir con la del documento 03**
- **`downloadAndCacheBotImage()` usa `BASE_URL.trimEnd('/')`** para evitar doble slash (`http://...//api/...`)

---

## Archivos Clave (Referencia Rápida)

| Archivo | Responsabilidad |
|---------|-----------------|
| `WalletViewModel.kt` | `refreshInscriptions()`, `downloadAndCacheBotImage()`, `selectInscription()`, `loadBotImage()` |
| `WalletScreen.kt` | UI lista inscripciones, botón refrescar, conteo inscripciones |
| `BittickImageCache.kt` | Caché dual: ordinals.com + server, Base64 en SharedPreferences + memoria |
| `authRouter.js` | `findAllBittickInscriptions()`, `/verify-wallet`, `/fetch-inscriptions`, `/select-inscription`, `/bot-image/{NN}` |
| `bittickCollection.js` | 100 IDs Bittick Agents, `hasInscriptionId()`, `getBotByInscriptionId()` |
| `TradingScreen.kt` | Muestra bot image en TopAppBar (Base64 → Bitmap) |
| `MainActivity.kt` | Pasa `botImageUrl` a TradingScreen y WalletScreen |
| `Models.kt` | `InscriptionInfo`, `VerifyWalletResponse`, `FetchInscriptionsResponse` |

---

## Referencias Cruzadas

| Documento | Qué Documenta |
|-----------|---------------|
| **Doc 03** (`03_IDs-coleccion-Bittick-Agent.md`) | Fuente de verdad: 100 bots con ID, tx, altura |
| **Doc 05** (`05_como_conectar_...`) | Flujo conexión wallet, verificación server-side, sesión 7 días |
| **Doc 06** (`06_Flujo-Conexion-Unisat.md`) | Diálogos 1 y 2, retorno manual, pegar dirección |
| **Doc 04** (`04_licencia-premium...`) | Licencia premium, verificación 24h WorkManager |
| **Skill 03** (`03_patrones-arquitectura.md`) | Patrones 1 (Nonce Verify), 2 (Sesión 7d), 4 (Caché Dual), 5 (Free-Tier 300) |

---

**Documento actualizado para Bittick (commit `c6bc510`)**  
**Versión**: Basada en código real — `WalletViewModel.kt`, `WalletScreen.kt`, `BittickImageCache.kt`, `authRouter.js`, `bittickCollection.js`  
**Fecha**: 2026-07-17