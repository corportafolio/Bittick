tengo otra aplicacion en android que se llama bitmapcore  esta app ya tienen una conec¿xion a una wallet bitcoin y la wallet conectada imbestiga los ordinales o actuvs de esa wallet y extrae una coleccion en especifico.

# Cómo Conectar una Wallet Bitcoin (UniSat) a una App Android e Investigar Colecciones Ordinals

## Documento Técnico Completo

---

## Índice

1. [Introducción y Arquitectura General](#1-introducción-y-arquitectura-general)
2. [Sistema de Conexión UniSat Wallet en Android](#2-sistema-de-conexión-unisat-wallet-en-android)
3. [Flujo Completo de Conexión y Obtención de Dirección](#3-flujo-completo-de-conexión-y-obtención-de-dirección)
4. [Extracción de Activos/Inscripciones desde Ordinals.com](#4-extracción-de-activosinscripciones-desde-ordinalscom)
5. [Parsing de HTML de Ordinals y Extracción de JSON](#5-parsing-de-html-de-ordinals-y-extracción-de-json)
6. [Identificación y Clasificación de Colecciones](#6-identificación-y-clasificación-de-colecciones)
7. [Investigación de una Colección Específica (Ej: NATPUNKS)](#7-investigación-de-una-colección-específica-ej-natpunks)
8. [Ejemplos Reales desde Logs de Producción](#8-ejemplos-reales-desde-logs-de-producción)
9. [Consideraciones Técnicas y Mejores Prácticas](#9-consideraciones-técnicas-y-mejores-prácticas)

---

## 1. Introducción y Arquitectura General

### 1.1 Propósito del Documento

Este documento describe el sistema completo para:
- Conectar una wallet **UniSat** (y Xverse) a una aplicación Android nativa
- Obtener la dirección Bitcoin del usuario tras la autorización
- Consultar el indexador **Ordinals.com** para obtener todas las inscripciones de esa dirección
- Parsear el HTML devuelto por Ordinals para extraer el contenido JSON de cada inscripción
- Identificar y clasificar colecciones (Bitmaps, NATPUNKS, BRC-20, TAP, etc.)
- Filtrar e investigar una colección específica (ej: `natpunks`, `4.133622.bitmap`)

### 1.2 Arquitectura de Alto Nivel

```
┌─────────────────┐     Deep Links      ┌──────────────────┐
│   App Android   │ ◄─────────────────► │   UniSat App     │
│  (BitmapCore)   │  signMessage,       │  (com.unisat.    │
│                 │  getAddresses       │   wallet)        │
└────────┬────────┘                     └──────────────────┘
         │
         │ Callback con dirección
         ▼
┌─────────────────┐     HTTPS + HTML Parsing     ┌──────────────────┐
│  ViewModel      │ ────────────────────────────► │  ordinals.com    │
│  Connection     │  1. /address/{addr}           │  (Indexador      │
│  Wallets        │  2. /output/{outputId}        │   Ordinals)      │
│                 │  3. /content/{inscriptionId}  │                  │
└────────┬────────┘  4. /inscription/{id}         └──────────────────┘
         │
         │ Parse HTML → Extraer JSON → Clasificar
         ▼
┌─────────────────┐
│  UserInscription│
│  (Modelo unificado)    Colecciones detectadas:
│  - id, number    │  • Bitmaps (N.bitmap)
│  - name          │  • Parcelas (N.N.bitmap)
│  - collectionName│  • NATPUNKS (tap/dmt-mint)
│  - protocol      │  • BRC-20 (p: brc-20)
│  - tick          │  • TAP (p: tap)
│  - isBitmap      │  • Pixels, JSON, Text, Other
│  - isParcel      │
└─────────────────┘
```

### 1.3 Componentes Principales

| Componente | Responsabilidad |
|------------|-----------------|
| `DeepLinkBuilder` | Construye URIs `unisat://request` con método, datos Base64, nonce, callback |
| `WalletDeepLinkHandler` | Verifica wallet instalada, abre deep link, fallback a Play Store |
| `ConnectionWalletsViewModel` | Orquesta conexión, obtiene dirección, carga inscripciones, clasifica |
| `NonceRepository` | Genera y guarda nonces únicos para prevenir replay attacks |
| `BitMapCoreWalletPreferences` | Persiste dirección, inscripciones cacheadas, estado de conexión |

---

## 2. Sistema de Conexión UniSat Wallet en Android

### 2.1 Protocolo de Deep Links UniSat

UniSat usa un esquema de deep links propietario: `unisat://request`

**Parámetros obligatorios:**
| Parámetro | Descripción | Ejemplo |
|-----------|-------------|---------|
| `method` | Método a invocar | `signMessage`, `getAddresses`, `signPsbt` |
| `data` | Payload en Base64 (JSON stringificado) | `WyJDb25lY3RhciBhIEJpdG1hcENvcnAiLCJlY2RzYSJd` |
| `nonce` | UUID único por sesión (anti-replay) | `a1b2c3d4-e5f6-7890-abcd-ef1234567890` |
| `callback` | URI de retorno | `unisat://response` (¡SIEMPRE este valor!) |
| `from` | Identificador de la app solicitante | `bitmapcore` |

**Regla crítica de UniSat:** **El parámetro `callback` SIEMPRE es ignorado**. UniSat responde exclusivamente a `unisat://response`. La app debe registrar este esquema en `AndroidManifest.xml`.

### 2.2 Registro del Esquema de Callback en AndroidManifest.xml

```xml
<activity
    android:name=".ui.local_bitmapcore_marketplace.activity.UnisatWalletCallbackActivity"
    android:exported="true"
    android:launchMode="singleTop">
    <intent-filter>
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />
        <!-- ESQUEMA FIJO QUE UNISAT USA PARA RESPONDER -->
        <data android:scheme="unisat" android:host="response" />
    </intent-filter>
</activity>
```

### 2.3 Construcción de Deep Links (Pseudocódigo)

```kotlin
// CONSTANTES
const val UNISAT_CALLBACK = "unisat://response"  // FIJO, no configurable
const val APP_CALLBACK_SCHEME = "BitmapCoreApp"
const val APP_CALLBACK_HOST = "wallet-callback"

// MÉTODO 1: signMessage (conexión inicial - fuerza prompt de firma)
fun buildUnisatConnectDeepLink(nonce: String): Uri {
    val message = "Conectar a BitmapCorp"
    val dataArray = "[\"$message\", \"ecdsa\"]"  // JSON array string
    val base64Data = Base64.encodeToString(dataArray.toByteArray(), Base64.NO_WRAP)
    
    val params = mapOf(
        "method" to "signMessage",
        "data" to base64Data,
        "from" to "bitmapcore",
        "nonce" to nonce,
        "callback" to UNISAT_CALLBACK  // ¡SIEMPRE ESTE VALOR!
    )
    return Uri.parse("unisat://request?${encodeParams(params)}")
}

// MÉTODO 2: getAddresses (obtener dirección tras firma exitosa)
fun buildUnisatGetAddressesDeepLink(nonce: String): Uri {
    val params = mapOf(
        "method" to "getAddresses",
        "from" to "bitmapcore",
        "nonce" to nonce,
        "callback" to UNISAT_CALLBACK
    )
    return Uri.parse("unisat://request?${encodeParams(params)}")
}

// MÉTODO 3: signPsbt (firmar transacciones PSBT)
fun buildUnisatSignPsbtDeepLink(psbtBase64: String, nonce: String): Uri {
    val dataJson = "[\"$psbtBase64\", {\"options\": \"ALL\"}]"
    val base64Data = Base64.encodeToString(dataJson.toByteArray(), Base64.NO_WRAP)
    
    val params = mapOf(
        "method" to "signPsbt",
        "data" to base64Data,
        "nonce" to nonce,
        "callback" to "$APP_CALLBACK_SCHEME://$APP_CALLBACK_HOST",
        "from" to APP_CALLBACK_SCHEME.lowercase()
    )
    return Uri.parse("unisat://request?${encodeParams(params)}")
}
```

### 2.4 Nonce: Generación y Validación

```kotlin
// Generar nonce único por sesión de conexión
fun generateNonce(): String = UUID.randomUUID().toString()

// Guardar nonce + walletType pendiente
fun savePendingConnection(nonce: String, walletType: String) {
    prefs.putString("pending_nonce", nonce)
    prefs.putString("pending_wallet_type", walletType)
}

// Validar al recibir callback
fun validateNonce(receivedNonce: String): Boolean {
    val stored = prefs.getString("pending_nonce", "")
    return receivedNonce == stored && stored.isNotBlank()
}
```

---

## 3. Flujo Completo de Conexión y Obtención de Dirección

### 3.1 Diagrama de Secuencia

```
Usuario          App Android              UniSat App              ordinals.com
  │                 │                        │                       │
  ├─ Toca "Conectar"├───────────────────────►│                       │
  │                 │   unisat://request     │                       │
  │                 │   method=signMessage   │                       │
  │                 │   data=base64(...)     │                       │
  │                 │   nonce=abc123         │                       │
  │                 │   callback=unisat://   │                       │
  │                 │                        │                       │
  │                 │                    ◄──│─ Prompt: "Firmar     │
  │                 │                        │    mensaje?"        │
  │                 │                        │                       │
  │                 │                    ───►│  Usuario acepta     │
  │                 │                        │                       │
  │                 │  unisat://response     │                       │
  │                 │  (callback automático) │                       │
  │                 │◄───────────────────────┤                       │
  │                 │                        │                       │
  │                 │  App recibe callback   │                       │
  │                 │  Valida nonce          │                       │
  │                 │                        │                       │
  ├─ "Obtener dir." ├───────────────────────►│                       │
  │                 │   unisat://request     │                       │
  │                 │   method=getAddresses  │                       │
  │                 │   nonce=abc123         │                       │
  │                 │                        │                       │
  │                 │                    ───►│  Usuario confirma   │
  │                 │                        │                       │
  │                 │  unisat://response     │                       │
  │                 │  data=base64(addr)     │                       │
  │                 │◄───────────────────────┤                       │
  │                 │                        │                       │
  │                 │  Decodifica Base64     │                       │
  │                 │  Guarda dirección      │                       │
  │                 │                        │                       │
  │                 │  loadUserInscriptions  │                       │
  │                 │───────────────────────►│                       │
  │                 │                        │   GET /address/...   │
  │                 │                        │◄─────────────────────┤
  │                 │                        │   HTML con outputs  │
  │                 │                        │                       │
  │                 │   (parsea outputs)     │                       │
  │                 │   Para cada output:    │                       │
  │                 │   GET /output/...      │                       │
  │                 │   Extrae inscriptionId │                       │
  │                 │                        │                       │
  │                 │   Para cada ID:        │                       │
  │                 │   GET /content/{id}    │                       │
  │                 │   GET /inscription/{id}│                       │
  │                 │◄───────────────────────┤   HTML + Content      │
  │                 │                        │                       │
  │                 │  Parse HTML → JSON     │                       │
  │                 │  Identifica colección  │                       │
  │                 │  Guarda UserInscription│                       │
  │                 │                        │                       │
```

### 3.2 Manejo del Callback en Android

**Activity de callback (`UnisatWalletCallbackActivity`):**

```kotlin
class UnisatWalletCallbackActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val uri = intent.data
        if (uri != null) {
            // Extraer parámetros de la respuesta
            val method = uri.getQueryParameter("method")
            val data = uri.getQueryParameter("data")  // Base64
            val nonce = uri.getQueryParameter("nonce")
            val error = uri.getQueryParameter("error")
            
            // Validar nonce contra el guardado
            if (validateNonce(nonce)) {
                when {
                    error != null -> handleError(error)
                    method == "signMessage" -> handleSignMessageResponse(data, nonce)
                    method == "getAddresses" -> handleGetAddressesResponse(data, nonce)
                    method == "signPsbt" -> handleSignPsbtResponse(data, nonce)
                }
            }
        }
        finish()
    }
    
    private fun handleGetAddressesResponse(data: String?, nonce: String?) {
        // data viene en Base64: "[\"bc1p...\"]"
        val decoded = Base64.decode(data!!, Base64.NO_WRAP).toString(Charsets.UTF_8)
        val jsonArray = JSONArray(decoded)
        val address = jsonArray.getString(0)  // Primera dirección
        
        // Comunicar al ViewModel via EventBus/SharedFlow
        WalletConnectionEvents.emit(WalletConnected(address, "unisat"))
        
        // Limpiar nonce
        clearPendingConnection()
    }
}
```

### 3.3 Estados de Conexión (State Machine)

```kotlin
sealed class WalletConnectionState {
    object Disconnected : WalletConnectionState()
    data class Connecting(val walletType: String) : WalletConnectionState()
    data class Connected(val address: String, val walletType: String) : WalletConnectionState()
    data class Error(val message: String?) : WalletConnectionState()
}

// Transiciones:
// Disconnected --connectWallet("unisat")--> Connecting("unisat")
// Connecting --callback success + getAddresses--> Connecting("unisat") [esperando dirección]
// Connecting --getAddresses success--> Connected(address, "unisat")
// Cualquier --error--> Error(message)
// Connected --disconnectWallet()--> Disconnected
```

---

## 4. Extracción de Activos/Inscripciones desde Ordinals.com

### 4.1 Visión General del Flujo de Datos

Una vez tenemos la dirección del usuario (ej: `bc1p...`), el flujo es:

```
1. GET https://ordinals.com/address/{address}
   └─► HTML con lista de outputs (UTXOs) que contienen inscripciones
   
2. Para cada outputId:
   GET https://ordinals.com/output/{outputId}
   └─► HTML con lista de inscriptionIds en ese output
   
3. Para cada inscriptionId:
   GET https://ordinals.com/content/{inscriptionId}
   └─► CONTENIDO RAW de la inscripción (JSON, texto, imagen, HTML, etc.)
   
   GET https://ordinals.com/inscription/{inscriptionId}
   └─► HTML con metadatos (número de inscripción, content-type, etc.)
```

### 4.2 Paso 1: Obtener Outputs de la Dirección

**Request:**
```
GET https://ordinals.com/address/bc1pXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
Headers: 
  User-Agent: Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36
  Accept: text/html
```

**Response (HTML fragment):**
```html
<dl>
  <dt>sat balance</dt>
  <dd>123456789</dd>
  
  <dt>outputs</dt>
  <dd>
    <a class="monospace" href="/output/abc123...def456">abc123...def456</a>
    <a class="monospace" href="/output/789xyz...uvw012">789xyz...uvw012</a>
  </dd>
  
  <dt>runes</dt>
  <dd>
    <a class="monospace" href="/rune/ABC...XYZ">TOKEN_NAME</a> : 1000000
  </dd>
</dl>
```

**Extracción (Regex):**
```kotlin
// Output IDs (href=/output/{id}>)
val outputRegex = Regex("""href=/output/([^ >]+)>""")
val outputIds = outputRegex.findAll(html).map { it.groupValues[1] }.toList()

// Sat balance
val satRegex = Regex("""<dt>sat balance</dt>\s*<dd>(\d+)</dd>""", RegexOption.DOT_MATCHES_ALL)
val satBalance = satRegex.find(html)?.groupValues?.get(1)?.toLongOrNull()

// Runes
val runeRegex = Regex("""<a class=monospace href=/rune/([^>]+)>([^<]+)</a>\s*:\s*([^<\s<]+)""")
val runes = runeRegex.findAll(html).map { match ->
    RuneBalance(
        name = match.groupValues[2],
        balance = match.groupValues[3].toLongOrNull() ?: 0,
        formattedName = null
    )
}.toList()
```

### 4.3 Paso 2: Obtener Inscription IDs de Cada Output

**Request (por cada outputId, en chunks de 5 para rate limiting):**
```
GET https://ordinals.com/output/{outputId}
Headers: User-Agent, Accept: text/html
```

**Response (HTML):**
```html
<div class="inscriptions">
  <a href="/inscription/abc123...def456i0">#12345678</a>
  <a href="/inscription/789xyz...uvw012i1">#87654321</a>
</div>
```

**Extracción:**
```kotlin
val inscriptionIdRegex = Regex("""/inscription/([a-f0-9]{64}i\d+)""")
val inscriptionIds = inscriptionIdRegex.findAll(outputHtml).map { it.groupValues[1] }.toList()
```

### 4.4 Paso 3: Obtener Contenido y Metadatos de Cada Inscripción

**3a. Contenido RAW (`/content/{id}`):**
```
GET https://ordinals.com/content/abc123...def456i0
Headers: Accept: */*
```
**Respuesta:** El contenido **exacto** de la inscripción:
- JSON puro: `{"p":"tap","op":"dmt-mint","tick":"natpunks","blk":"43087"}`
- Texto plano: `123.456.bitmap`
- HTML: `<script src="/content/..."></script>`
- Imagen binaria: `�PNG...` (bytes raw)
- Cualquier otro mime type

**3b. Metadatos (`/inscription/{id}`):**
```
GET https://ordinals.com/inscription/abc123...def456i0
Headers: Accept: text/html
```
**Respuesta (HTML):**
```html
<h1>Inscription #12345678</h1>
<dl>
  <dt>id</dt><dd>abc123...def456i0</dd>
  <dt>number</dt><dd>12345678</dd>
  <dt>content-type</dt><dd>text/plain;charset=utf-8</dd>
  ...
</dl>
```

**Extracción del número de inscripción:**
```kotlin
val numberRegex = Regex("""Inscription\s*#?\s*(\d+)""")
val inscriptionNumber = numberRegex.find(html)?.groupValues?.get(1)?.toIntOrNull()
```

### 4.5 Estrategia de Rate Limiting y Concurrencia

```kotlin
// Configuración
val CHUNK_SIZE_CONTENT = 3      // 3 requests paralelos para /content
val CHUNK_SIZE_DETAIL = 3       // 3 requests paralelos para /inscription
val DELAY_BETWEEN_CHUNKS_MS = 200
val MAX_RETRIES = 3
val BACKOFF_BASE_MS = 1000  // 1s, 2s, 4s (exponential backoff)

// Semáforo para limitar concurrencia global
val semaphore = Semaphore(3)

// Fetch con retry y backoff
suspend fun fetchWithRetry(url: String, acceptHeader: String): ContentResult {
    var attempt = 0
    while (attempt < MAX_RETRIES) {
        semaphore.withPermit {
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", USER_AGENT)
                .header("Accept", acceptHeader)
                .build()
            
            val response = client.newCall(request).execute()
            
            if (response.code == 429) {  // Rate limited
                attempt++
                if (attempt < MAX_RETRIES) {
                    val delayMs = (Math.pow(2.0, (attempt - 1).toDouble()) * BACKOFF_BASE_MS).toLong()
                    delay(delayMs)
                    continue
                }
            }
            
            return ContentResult(
                body = response.body?.string(),
                contentType = response.header("Content-Type"),
                statusCode = response.code
            )
        }
    }
    return ContentResult(null, null, -1)
}
```

---

## 5. Parsing de HTML de Ordinals y Extracción de JSON

### 5.1 Concepto Clave: Ordinals Devuelve HTML, No JSON

**IMPORTANTE:** `ordinals.com` **no tiene API JSON pública** para consultar inscripciones por dirección. Todos los endpoints devuelven **HTML**.

Sin embargo, el endpoint `/content/{inscriptionId}` devuelve el **contenido raw original** de la inscripción, que **SÍ puede ser JSON válido**.

### 5.2 Flujo de Extracción

```
HTML /address/{addr}
    │
    ├─► Extraer outputIds (regex href=/output/...)
    │
    ▼
HTML /output/{outputId}  (por cada output)
    │
    ├─► Extraer inscriptionIds (regex /inscription/{id})
    │
    ▼
PARALELO: /content/{id} + /inscription/{id}
    │
    ├─► /content/{id} → CONTENIDO RAW (JSON, texto, imagen, etc.)
    │       │
    │       ├─► Si es JSON válido → Parsear directamente
    │       ├─► Si es texto plano → Buscar patrones (bitmap, parcel, etc.)
    │       └─► Si es HTML/imagen → Solo metadatos
    │
    └─► /inscription/{id} → HTML con metadatos
            │
            └─► Extraer inscriptionNumber (regex "Inscription #N")
```

### 5.3 Detección y Parseo de JSON en el Contenido

```kotlin
fun extractJsonFromContent(rawContent: String?, contentType: String?): JsonObject? {
    val trimmed = rawContent?.trim() ?: return null
    
    // 1. JSON object: empieza con {
    if (trimmed.startsWith("{")) {
        try {
            return JsonParser.parseString(trimmed).asJsonObject
        } catch (e: JsonSyntaxException) {
            // No es JSON válido, continuar
        }
    }
    
    // 2. JSON array: empieza con [
    if (trimmed.startsWith("[")) {
        try {
            return JsonParser.parseString(trimmed).asJsonObject  // o JsonArray
        } catch (e: JsonSyntaxException) { }
    }
    
    // 3. String JSON escapado: empieza con "
    if (trimmed.startsWith("\"")) {
        try {
            val unescaped = JsonParser.parseString(trimmed).asString
            // Recursivamente intentar parsear el string desescapado
            return extractJsonFromContent(unescaped, contentType)
        } catch (e: JsonSyntaxException) { }
    }
    
    return null
}
```

### 5.4 Identificación de Protocolos desde JSON

Una vez tenemos el `JsonObject`, extraemos el campo `"p"` (protocolo):

```kotlin
fun identifyProtocol(json: JsonObject): ProtocolInfo {
    val protocol = json.get("p")?.asString?.lowercase()
    val tick = json.get("tick")?.asString?.uppercase()
    val op = json.get("op")?.asString?.lowercase()
    
    return when (protocol) {
        "brc-20" -> ProtocolInfo(
            protocol = "brc-20",
            collectionName = tick ?: "BRC20",
            tick = tick,
            operation = op  // mint, transfer, deploy
        )
        "tap" -> {
            // TAP puede ser: token-mint, dmt-mint, token-transfer, dmt-transfer
            val collectionName = when {
                tick != null -> tick.uppercase()
                op == "dmt-mint" -> json.get("tick")?.asString?.uppercase() ?: "TAP-DMT"
                else -> "TAP"
            }
            ProtocolInfo(
                protocol = "tap",
                collectionName = collectionName,
                tick = tick,
                operation = op,
                deployment = json.get("dep")?.asString,
                block = json.get("blk")?.asString
            )
        }
        "sns" -> ProtocolInfo(
            protocol = "sns",
            collectionName = "SNS",
            operation = op
        )
        "vord" -> ProtocolInfo(
            protocol = "vord",
            collectionName = json.get("iid")?.asString ?: "VORD",
            collectionId = json.get("col")?.asString
        )
        else -> ProtocolInfo(protocol = protocol ?: "unknown")
    }
}
```

### 5.5 Extracción de JSON desde HTML (Casos Edge)

Algunas inscripciones devuelven HTML que **contiene** JSON embebido:

```html
<script id="inscription-data" type="application/json">
{"p":"tap","op":"dmt-mint","tick":"natpunks","blk":"43087","dep":"..."}
</script>
```

O en atributos data:
```html
<div data-content='{"p":"brc-20","op":"mint","tick":"sats","amt":"1000"}'>...</div>
```

**Extractor robusto:**
```kotlin
fun extractJsonFromHtml(html: String): List<JsonObject> {
    val results = mutableListOf<JsonObject>()
    
    // 1. Buscar en <script type="application/json">...</script>
    val scriptRegex = Regex("""<script[^>]*type=["']application/json["'][^>]*>([^<]+)</script>""")
    scriptRegex.findAll(html).forEach { match ->
        try {
            val json = JsonParser.parseString(match.groupValues[1]).asJsonObject
            results.add(json)
        } catch (e: Exception) { }
    }
    
    // 2. Buscar en data-content, data-json, etc.
    val dataRegex = Regex("""data-(?:content|json|inscription)=["']([^"']+)["']""")
    dataRegex.findAll(html).forEach { match ->
        try {
            val decoded = URLDecoder.decode(match.groupValues[1], "UTF-8")
            val json = JsonParser.parseString(decoded).asJsonObject
            results.add(json)
        } catch (e: Exception) { }
    }
    
    // 3. Buscar JSON-like patterns en el HTML (fallback agresivo)
    val jsonLikeRegex = Regex("""(\{[^{}]*"p"\s*:\s*"[^"]+"[^{}]*\})""")
    jsonLikeRegex.findAll(html).forEach { match ->
        try {
            val json = JsonParser.parseString(match.groupValues[1]).asJsonObject
            results.add(json)
        } catch (e: Exception) { }
    }
    
    return results
}
```

---

## 6. Identificación y Clasificación de Colecciones

### 6.1 Jerarquía de Prioridades para Clasificación

```kotlin
fun classifyInscription(
    content: String?,           // Raw content from /content/{id}
    contentType: String?,       // Content-Type header
    metaprotocol: String?,      // From /inscription/{id} HTML parsing
    inscriptionNumber: Int
): CollectionClassification {
    
    val trimmed = content?.trim() ?: ""
    
    // PRIORIDAD 1: Análisis de contenido raw (más confiable)
    if (trimmed.isNotEmpty()) {
        // 1a. Parcelas: N.N.bitmap (dos puntos antes de .bitmap)
        val parcelMatch = Regex("""^(\d+)\.(\d+)\.bitmap\s*$""").find(trimmed)
        if (parcelMatch != null) {
            return CollectionClassification(
                collectionName = "Parcelas",
                isBitmap = true,
                isParcel = true,
                displayName = trimmed,  // "1748.672865.bitmap"
                protocol = "bitmap",
                tick = null
            )
        }
        
        // 1b. Full Block Bitmaps: N.bitmap (un punto antes de .bitmap)
        val fullBlockMatch = Regex("""^(\d+)\.bitmap\s*$""").find(trimmed)
        if (fullBlockMatch != null) {
            return CollectionClassification(
                collectionName = "Bitmaps",
                isBitmap = true,
                isParcel = false,
                displayName = trimmed,  // "200254.bitmap"
                protocol = "bitmap",
                tick = null
            )
        }
        
        // 1c. JSON Protocols (BRC-20, TAP, etc.)
        if (trimmed.startsWith("{")) {
            val json = extractJsonFromContent(trimmed, contentType)
            if (json != null) {
                val protocolInfo = identifyProtocol(json)
                return CollectionClassification(
                    collectionName = protocolInfo.collectionName,
                    isBitmap = false,
                    isParcel = false,
                    displayName = "${protocolInfo.collectionName} #$inscriptionNumber",
                    protocol = protocolInfo.protocol,
                    tick = protocolInfo.tick,
                    extraData = protocolInfo  // Guardar deployment, block, etc.
                )
            }
        }
    }
    
    // PRIORIDAD 2: Metaprotocol del indexador (fallback)
    if (collectionName == null && metaprotocol != null) {
        return when (metaprotocol.lowercase()) {
            "brc-20" -> CollectionClassification(
                collectionName = "BRC20", protocol = "brc-20", ...
            )
            "tap" -> CollectionClassification(
                collectionName = "TAP", protocol = "tap", ...
            )
            else -> CollectionClassification(collectionName = metaprotocol.uppercase())
        }
    }
    
    // PRIORIDAD 3: Content-Type header
    return when {
        contentType?.startsWith("image/") == true -> "Pixels"
        contentType?.startsWith("text/") == true -> "Text"
        contentType?.contains("json") == true -> "JSON"
        else -> "Other"
    }
}
```

### 6.2 Colecciones Detectadas en Producción (Logs Reales)

Basado en los logs proporcionados, estas son las colecciones identificadas:

| Colección | Patrón de Contenido | Protocolo | Ejemplo |
|-----------|---------------------|-----------|---------|
| **Bitmaps** | `N.bitmap` | bitmap | `200254.bitmap` |
| **Parcelas** | `N.N.bitmap` | bitmap | `1748.672865.bitmap` |
| **NATPUNKS** | `{"p":"tap","op":"dmt-mint","tick":"natpunks","blk":"43087"}` | tap (DMT) | `#62494421` |
| **NAT** | `{"p":"tap","op":"dmt-mint","tick":"nat","blk":"717093"}` | tap (DMT) | `#44275615` |
| **NATPEPES** | `{"p":"tap","op":"dmt-mint","tick":"natpepes","blk":"440820"}` | tap (DMT) | `#62812183` |
| **NATIMATEDWIZARDS** | `{"p":"tap","op":"dmt-mint","tick":"natimatedwizards"}` | tap (DMT) | `#71730124` |
| **NATDOGOOOOO** | `{"p":"tap","op":"dmt-mint","tick":"natdogooooo"}` | tap (DMT) | `#71810794` |
| **CHAMP** | `{"p":"tap","op":"dmt-mint","tick":"champ"}` | tap (DMT) | `#64089666` |
| **VESSELS** | `{"p":"tap","op":"dmt-mint","tick":"vessels"}` | tap (DMT) | `#72286867` |
| **BITLAND** | `{"p":"tap","op":"dmt-mint","tick":"bitland","coord":"-3,-7"}` | tap (DMT) | `#71468817` |
| **SATS (BRC-20)** | `{"p":"brc-20","op":"transfer","tick":"sats","amt":"100000000"}` | brc-20 | `#38206971` |
| **$BMP** | `{"p":"brc-20","op":"transfer","tick":"$bmp","amt":"6000"}` | brc-20 | `#44824274` |
| **EORB** | `{"p":"brc-20","op":"mint","tick":"eorb","amt":"10"}` | brc-20 | `#58402991` |
| **DOGE** | `{"p":"brc-20","op":"mint","tick":"doge","amt":"4200"}` | brc-20 | `#65761096` |
| **SOLS** | `{"p":"brc-20","op":"mint","tick":"sols","amt":"1"}` | brc-20 | `#50816725` |
| **HAMS** | `{"p":"brc-20","op":"mint","tick":"HAMS","amt":"4200"}` | brc-20 | `#66034723` |
| **MICE** | `{"p":"brc-20","op":"tranfer","tick":"mice","amt":"2000"}` | brc-20 | `#56315626` |
| **PIZZA** | `{"p":"brc-20","op":"transfer","tick":"pizza","amt":"100"}` | brc-20 | `#71244174` |
| **C0FFEE** | `{"p":"tap","op":"dmt-mint","tick":"c0ffee"}` | tap (DMT) | `#64351151` |
| **TIKABIT** | `{"p":"tap","op":"dmt-mint","tick":"tikabit"}` | tap (DMT) | `#71803774` |
| **MCROWN** | `{"p":"tap","op":"dmt-mint","tick":"mcrown"}` | tap (DMT) | `#71347740` |
| **REDACTEDTEST** | `{"p":"tap","op":"dmt-mint","tick":"redactedtest"}` | tap (DMT) | `#70363925` |
| **TAPZERO** | `{"p":"tap","op":"token-mint","tick":"tapzero"}` | tap (token) | `#71845433` |
| **DMT-NAT** | `{"p":"tap","op":"token-transfer","tick":"DMT-NAT"}` | tap (token) | `#56320647` |
| **Bitman** | `{"Bitman ID":12332,"Birthdate":"2009-04-26...` | custom JSON | `#56118441` |
| **Pixels/WEBP** | Binario (PNG/WEBP) | image | `#56933022` |
| **GLTF** | `model/gltf-binary` | 3D model | `#50978893` |
| **Runemap** | `N.runemap` | custom | `394858.runemap` |

---

## 7. Investigación de una Colección Específica (Ej: NATPUNKS)

### 7.1 Caso de Uso: Filtrar NATPUNKS de una Wallet

El usuario quiere encontrar todas las inscripciones de la colección **NATPUNKS** (tick: `natpunks`, protocolo: `tap` con op `dmt-mint`).

### 7.2 Algoritmo de Filtrado

```kotlin
// Modelo unificado de inscripción del usuario
data class UserInscription(
    val id: String,                    // inscriptionId (64 hex + iN)
    val number: Int,                   // inscription number
    val address: String,               // owner address
    val name: String?,                 // display name (ej: "NATPUNKS #1")
    val collectionName: String?,       // "NATPUNKS", "Bitmaps", "Parcelas", etc.
    val protocol: String?,             // "tap", "brc-20", "bitmap"
    val tick: String?,                 // "natpunks", "sats", "nat"
    val isBitmap: Boolean,
    val isParcel: Boolean,
    val InscriptionNumber: Int?,       // Número real de inscripción
    val extraData: Map<String, Any>?   // deployment, block, etc.
)

// Filtrar por colección específica
fun filterCollection(inscriptions: List<UserInscription>, collectionKey: String): List<UserInscription> {
    val normalizedKey = collectionKey.lowercase().trim()
    
    return inscriptions.filter { inscription ->
        when {
            // Por nombre de colección exacto
            inscription.collectionName?.lowercase() == normalizedKey -> true
            
            // Por tick (para BRC-20, TAP)
            inscription.tick?.lowercase() == normalizedKey -> true
            
            // Por protocolo + tick combinado (ej: "tap:natpunks")
            "${inscription.protocol}:${inscription.tick}".lowercase() == normalizedKey -> true
            
            // Por nombre display (para bitmaps/parcelas)
            inscription.name?.lowercase()?.contains(normalizedKey) == true -> true
            
            else -> false
        }
    }
}

// Uso:
// val natpunks = filterCollection(allInscriptions, "natpunks")
// val natpunks = filterCollection(allInscriptions, "tap:natpunks")
// val bitmap4133622 = filterCollection(allInscriptions, "4.133622.bitmap")
```

### 7.3 Extracción de Metadatos Específicos de NATPUNKS

```kotlin
// NATPUNKS usa TAP DMT (Discrete Mint Tokens)
// Contenido típico: {"p":"tap","op":"dmt-mint","dep":"<deployment_id>","tick":"natpunks","blk":"43087"}

fun extractNatpunksMetadata(inscription: UserInscription): NatpunksMetadata? {
    return if (inscription.tick?.lowercase() == "natpunks" && inscription.protocol == "tap") {
        val extra = inscription.extraData ?: emptyMap()
        NatpunksMetadata(
            inscriptionId = inscription.id,
            inscriptionNumber = inscription.InscriptionNumber ?: inscription.number,
            deploymentId = extra["deployment"] as? String,
            blockHeight = extra["block"] as? String,
            operation = extra["operation"] as? String ?: "dmt-mint",
            // Para obtener atributos del punk, necesitarías indexar el deployment
            // y consultar los metadatos off-chain (IPFS, etc.)
        )
    } else null
}

data class NatpunksMetadata(
    val inscriptionId: String,
    val inscriptionNumber: Int,
    val deploymentId: String?,      // dep field - ID del deployment TAP
    val blockHeight: String?,       // blk field - bloque de minteo
    val operation: String?          // dmt-mint, dmt-transfer, etc.
)
```

### 7.4 Búsqueda de Inscripción Específica: `4.133622.bitmap`

Este es un **Full Block Bitmap** (número de bloque 4, parcela 133622).

```kotlin
// Buscar bitmap específico por nombre exacto
fun findSpecificBitmap(inscriptions: List<UserInscription>, bitmapName: String): UserInscription? {
    return inscriptions.find { it.name == bitmapName }
}

// Uso:
val bitmap = findSpecificBitmap(allInscriptions, "4.133622.bitmap")
// Resultado esperado:
// UserInscription(
//     id = "abc123...i0",
//     number = 12345678,
//     name = "4.133622.bitmap",
//     collectionName = "Bitmaps",
//     isBitmap = true,
//     isParcel = false,
//     protocol = "bitmap"
// )
```

### 7.5 Búsqueda de Parcela Específica: `1748.672865.bitmap`

```kotlin
// Buscar parcela específica
fun findSpecificParcel(inscriptions: List<UserInscription>, parcelName: String): UserInscription? {
    return inscriptions.find { it.name == parcelName && it.isParcel }
}

// Uso:
val parcela = findSpecificParcel(allInscriptions, "1748.672865.bitmap")
// Resultado:
// UserInscription(
//     name = "1748.672865.bitmap",
//     collectionName = "Parcelas",
//     isBitmap = true,
//     isParcel = true
// )
```

---

## 8. Ejemplos Reales desde Logs de Producción

### 8.1 Logs de Carga de Inscripciones (Extracto Real)

```
2026-07-08 22:48:45.850  [Wallet] 📄 InscriptionNumber #56195342 | id=fccf7d20e0b78c...751d8aa342b4b02i0 | ct=text/plain;charset=utf-8 | status=200
{"Bitman ID":10423,"Birthdate":"2009-04-10 07:56","Species":"0x00000001","Size":216,"Weight":864,"Wealth":0,"Wisdom":1}

2026-07-08 22:48:45.850  [Wallet] 📄 InscriptionNumber #12968348 | id=[PUBKEY_REDACTED]i0 | ct=text/plain | status=200
217057.bitmap

2026-07-08 22:48:45.850  [Wallet] 📄 InscriptionNumber #70421736 | id=989...25a...i0 | ct=text/html;charset=utf-8 | status=200
<script src="/content/[PUBKEY_REDACTED]i0"></script>

2026-07-08 22:48:45.850  [Wallet] 📄 InscriptionNumber #58897787 | id=7bf69...1fad77eac6cf4b9e351506c6i0 | ct=text/plain;charset=utf-8 | status=200
{"p":"brc-20","op":"mint","tick":"eorb","amt":"10"}

2026-07-08 22:48:45.851  [Wallet] 📄 InscriptionNumber #64011887 | id=[PUBKEY_REDACTED]i0 | ct=text/plain;charset=utf-8 | status=200
{"p":"tap","op":"dmt-mint","dep":"[PUBKEY_REDACTED]i0","tick":"champ","blk":"444494"}

2026-07-08 22:48:45.851  [Wallet] 📄 InscriptionNumber #44275706 | id=84c01dbefcbe7ef70855f2667a...0c8i0 | ct=text/plain;charset=utf-8 | status=200
{"p": "tap","op": "dmt-mint","dep": "4d967af...8031fed5403a99ac57fe67i0","tick": "nat","blk": "717093"}

2026-07-08 22:48:45.863  [Wallet] 📄 InscriptionNumber #62498848 | id=[PUBKEY_REDACTED]i12 | ct=text/plain;charset=utf-8 | status=200
{"p":"tap","op":"dmt-mint","dep":"[PUBKEY_REDACTED]i0","tick":"natpunks","blk":"43087"}

2026-07-08 22:48:45.875  [Wallet] 📄 InscriptionNumber #62497729 | id=df968393cfe376c40bd823856a66d9a3e13096fc... | ct=text/plain;charset=utf-8 | status=200
{"p":"tap","op":"dmt-mint","dep":"[PUBKEY_REDACTED]i0","tick":"natpunks","blk":"42960"}
```

### 8.2 Resumen de Colecciones Detectadas (Log Final)

```
2026-07-08 22:48:45.952  [Wallet] 📊 Se encontraron 38 colecciones
2026-07-08 22:48:45.952  [Wallet]   📁 'Text': 114 inscripciones, #56118441 → {"Bitman ID":12332,"Birthdate":"2009-04-26 20:30","Species":"0x00000001","Size":216,"Weight":864,"Wealth":0,"Wisdom":1}
2026-07-08 22:48:45.952  [Wallet]   📁 'NAT': 7 inscripciones, #44275615 → {"p": "tap","op": "dmt-mint","dep": "4d967af...8031fed5403a99ac57fe67i0","tick": "nat","blk": "717091"}
2026-07-08 22:48:45.952  [Wallet]   📁 'Parcelas': 37 inscripciones, #57105065 → 1748.672865.bitmap
2026-07-08 22:48:45.953  [Wallet]   📁 'SCAT': 3 inscripciones, #56489039 → {"p":"brc-20","op":"mint","tick":"SCAT","amt":"1000"}
2026-07-08 22:48:45.953  [Wallet]   📁 '$BMP': 10 inscripciones, #62279658 → {"p":"brc-20","op":"transfer","tick":"$BMP","amt":"1000"}
2026-07-08 22:48:45.953  [Wallet]   📁 'SOLS': 17 inscripciones, #50816725 → {"p":"brc-20","op":"mint","tick":"sols","amt":"1"}
2026-07-08 22:48:45.953  [Wallet]   📁 'SATS': 25 inscripciones, #38206971 → {"p":"brc-20","op":"transfer","tick":"sats","amt":"300000000"}
2026-07-08 22:48:45.953  [Wallet]   📁 'C0FFEE': 5 inscripciones, #64351151 → {"p":"tap","op":"dmt-mint","dep":"[PUBKEY_REDACTED]i0","tick":"c0ffee","blk":"299990"}
2026-07-08 22:48:45.953  [Wallet]   📁 'TAPZERO': 1 inscripciones, #71845433 → {"p":"tap","op":"token-mint","tick":"tapzero","amt":1,"prv":{...}}
2026-07-08 22:48:45.954  [Wallet]   📁 'EORB': 19 inscripciones, #65713103 → {"p":"brc-20","op":"transfer","tick":"EORB","amt":"30"}
2026-07-08 22:48:45.954  [Wallet]   📁 'DMT-NAT': 1 inscripciones, #56320647 → {"p":"tap","op":"token-transfer","tick":"DMT-NAT","amt":"400000000"}
2026-07-08 22:48:45.954  [Wallet]   📁 'BEAK': 2 inscripciones, #50640498 → {"p":"brc-20","op":"mint","tick":"BEAK","amt":"1000"}
2026-07-08 22:48:45.954  [Wallet]   📁 'NATPEPES': 1 inscripciones, #62812183 → {"p": "tap", "op": "dmt-mint", "dep": "f04...0dd96af16432b319b577ff5032eae2cci0", "tick": "natpepes", "blk": "440820"}
2026-07-08 22:48:45.955  [Wallet]   📁 'Bitmaps': 7 inscripciones, #12757923 → 200254.bitmap
2026-07-08 22:48:45.955  [Wallet]   📁 'TAPWALLETEST1': 1 inscripciones, #71809985 → {"p":"tap","op":"token-mint","tick":"tapwalletest1","amt":1,"prv":{...}}
2026-07-08 22:48:45.955  [Wallet]   📁 'VESSELS': 6 inscripciones, #72286867 → {"p":"tap","op":"dmt-mint","tick":"vessels","blk":"595352","dep":"9b7f4326b66782207fc8eb84f68...cfi0","prv":{...}}
2026-07-08 22:48:45.956  [Wallet]   📁 'Other': 6 inscripciones, #51183488 → /content/[PUBKEY_REDACTED]i0
2026-07-08 22:48:45.956  [Wallet]   📁 'NATIMATEDWIZARDS': 1 inscripciones, #71730124 → {"p":"tap","op":"dmt-mint","tick":"natimatedwizards","blk":"44045","dep":"[PUBKEY_REDACTED]i0","prv":{...}}
2026-07-08 22:48:45.956  [Wallet]   📁 'Pixels': 9 inscripciones, #70645800 → RIFF@?????WEBPVP8X...
2026-07-08 22:48:45.957  [Wallet]   📁 'JSON': 7 inscripciones, #70645801 → {"p":"vord","v":1,"ty":"insc","col":"[PUBKEY_REDACTED]","iid":"Bitcoin Mutant Apes","publ":"[ADDRESS_REDACTED]","nonce":8,"minter":"bc1ph...
2026-07-08 22:48:45.957  [Wallet]   📁 'TIKABIT': 1 inscripciones, #71803774 → {"p":"tap","op":"dmt-mint","tick":"tikabit","blk":"200900","dep":"[PUBKEY_REDACTED]i0","prv":{...}}
2026-07-08 22:48:45.957  [Wallet]   📁 'UCAT': 2 inscripciones, #44824345 → {"p":"brc-20","op":"mint","tick":"UCAT","amt":"1000000"}
2026-07-08 22:48:45.957  [Wallet]   📁 'HAMS': 5 inscripciones, #66034723 → {"p":"brc-20","op":"mint","tick":"HAMS","amt":"4200"}
2026-07-08 22:48:45.958  [Wallet]   📁 'MCROWN': 1 inscripciones, #71347740 → {"p":"tap","op":"dmt-mint","tick":"mcrown","blk":"40008","dep":"[PUBKEY_REDACTED]i0","prv":{...}}
2026-07-08 22:48:45.958  [Wallet]   📁 'BITLAND': 20 inscripciones, #71468817 → {"p":"tap","op":"dmt-mint","dep":"acf58eb708498...0689ec9be423b29e41i0","tick":"bitland","blk":"178","coord":"-3,-7"}
2026-07-08 22:48:45.958  [Wallet]   📁 'PUMA': 2 inscripciones, #66819879 → {"p":"brc-20","op":"mint","tick":"PUMA","amt":"2100"}
2026-07-08 22:48:45.958  [Wallet]   📁 'OXOX': 2 inscripciones, #50816860 → {"p":"brc-20","op":"mint","tick":"oxox","amt":"1000"}
2026-07-08 22:48:45.958  [Wallet]   📁 'NATPUNKS': 5 inscripciones, #62494421 → {"p":"tap","op":"dmt-mint","dep":"[PUBKEY_REDACTED]i0","tick":"natpunks","blk":"624087"}
2026-07-08 22:48:45.959  [Wallet]   📁 'CHAMP': 5 inscripciones, #64089666 → {"p":"tap","op":"dmt-mint","dep":"[PUBKEY_REDACTED]i0","tick":"champ","blk":"200054"}
2026-07-08 22:48:45.959  [Wallet]   📁 'MICE': 5 inscripciones, #56315626 → {"p":"brc-20","op":"tranfer","tick":"mice","amt":"2000"}
2026-07-08 22:48:45.959  [Wallet]   📁 'PRAY': 1 inscripciones, #50816879 → {"p":"brc-20","op":"mint","tick":"pray","amt":"1000"}
2026-07-08 22:48:45.959  [Wallet]   📁 'PIZZA': 1 inscripciones, #71244174 → {"p":"brc-20","op":"transfer","tick":"pizza","amt":"100"}
2026-07-08 22:48:45.959  [Wallet]   📁 'MASK': 1 inscripciones, #65761097 → {"p":"brc-20","op":"mint","tick":"MASK","amt":"10"}
2026-07-08 22:48:45.960  [Wallet]   📁 'HUHU': 1 inscripciones, #65761105 → {"p":"brc-20","op":"mint","tick":"HUHU","amt":"100"}
2026-07-08 22:48:45.960  [Wallet]   📁 'NATDOGOOOOO': 1 inscripciones, #71810794 → {"p":"tap","op":"dmt-mint","tick":"natdogooooo","blk":"116960","dep":"[PUBKEY_REDACTED]i0","prv":{...}}
2026-07-08 22:48:45.960  [Wallet]   📁 'DOGE': 2 inscripciones, #65761096 → {"p":"brc-20","op":"mint","tick":"doge","amt":"4200"}
2026-07-08 22:48:45.961  [Wallet]   📁 'REDACTEDTEST': 1 inscripciones, #70363925 → {"p":"tap","op":"dmt-mint","tick":"redactedtest","blk":"43153","dep":"[ADDRESS_REDACTED]0eb782f15b170f2363158a36b3a9accci0","prv":{...}}
2026-07-08 22:48:45.961  [Wallet]   📁 'BT0S': 1 inscripciones, #56489144 → {"p":"brc-20","op":"mint","tick":"BT0s","amt":"10000"}
2026-07-08 22:48:45.976  [Wallet] ✅ 336 inscripciones cargadas para [ADDRESS_REDACTED]
```

### 8.3 Análisis de los Datos Reales

| Métrica | Valor |
|---------|-------|
| **Total inscripciones** | 336 |
| **Total colecciones únicas** | 38 |
| **Colección más grande** | Text (114) - Majority Bitman JSON |
| **NATPUNKS** | 5 inscripciones (tap/dmt-mint) |
| **NAT** | 7 inscripciones (tap/dmt-mint) |
| **Bitmaps (full blocks)** | 7 inscripciones |
| **Parcelas** | 37 inscripciones |
| **BRC-20 tokens** | SATS, $BMP, EORB, SOLS, DOGE, HAMS, MICE, PIZZA, MASK, HUHU, SCAT, UCAT, PUMA, OXOX, BEAK, PRAY, BT0S |
| **TAP DMT collections** | NAT, NATPUNKS, NATPEPES, NATIMATEDWIZARDS, NATDOGOOOOO, CHAMP, VESSELS, BITLAND, C0FFEE, TIKABIT, MCROWN, REDACTEDTEST, TAPZERO, TAPWALLETEST1 |
| **Otros protocolos** | VORD (JSON), SNS, DMT-NAT (token-transfer) |
| **Medios** | Pixels (9 WEBP/PNG), GLTF (1 modelo 3D) |

---

## 9. Consideraciones Técnicas y Mejores Prácticas

### 9.1 Rate Limiting en Ordinals.com

| Endpoint | Límite Observado | Estrategia |
|----------|------------------|------------|
| `/address/{addr}` | ~30 req/min | Cache 5 min, single request |
| `/output/{id}` | ~20 req/min | Chunk 5, delay 150ms |
| `/content/{id}` | ~10 req/min | Chunk 3, delay 200ms, retry 429 |
| `/inscription/{id}` | ~15 req/min | Chunk 3, delay 200ms |

**Implementación robusta:**
```kotlin
class OrdinalsRateLimiter {
    private val semaphore = Semaphore(3)
    private val lastRequestTime = AtomicLong(0)
    private const val MIN_INTERVAL_MS = 100
    
    suspend fun <T> execute(request: suspend () -> T): T {
        semaphore.withPermit {
            val now = System.currentTimeMillis()
            val elapsed = now - lastRequestTime.get()
            if (elapsed < MIN_INTERVAL_MS) {
                delay(MIN_INTERVAL_MS - elapsed)
            }
            lastRequestTime.set(System.currentTimeMillis())
            return@withPermit request()
        }
    }
}
```

### 9.2 Manejo de Errores Comunes

| Error | Causa | Solución |
|-------|-------|----------|
| `429 Too Many Requests` | Rate limit | Exponential backoff (1s, 2s, 4s), reducir chunk size |
| `404 Not Found` | Inscripción quemada/inexistente | Ignorar, continuar con siguiente |
| `5xx Server Error` | Problema en ordinals.com | Retry con backoff, fallback a cache |
| HTML malformed | Cambios en UI de ordinals.com | Regex flexible, logging para debug |
| JSON parse error | Contenido no es JSON | Verificar content-type, fallback a text parsing |

### 9.3 Cacheo y Persistencia

```kotlin
// Estructura de cache recomendada
data class CachedWalletInscriptions(
    val address: String,
    val inscriptions: List<UserInscription>,
    val fetchedAt: Long,
    val blockHeight: Long?  // Para invalidación
)

// Clave de cache: "wallet_inscriptions_${address}"
// TTL: 5 minutos para balance, 1 hora para inscripciones
// Invalidar al: conectar wallet distinta, pull-to-refresh, nuevo bloque detectado
```

### 9.4 Seguridad y Privacidad

1. **Nonce único por sesión** - Previene replay attacks
2. **Validar callback origin** - Verificar que el deep link viene de UniSat
3. **No loguear direcciones completas** - Usar `address.takeLast(4)` o `[ADDRESS_REDACTED]` en logs
4. **Sanitizar contenido HTML** - Antes de mostrar en WebView o parsear
5. **Timeouts agresivos** - 30s connect, 30s read para evitar ANR

### 9.5 Testing y Debugging

```kotlin
// Modo debug para inspeccionar HTML real
const val DEBUG_SAVE_HTML = true

fun saveDebugHtml(endpoint: String, html: String) {
    if (!DEBUG_SAVE_HTML) return
    val file = File(context.cacheDir, "ordinals_debug_${endpoint.replace("/", "_")}.html")
    file.writeText(html)
    Log.d("OrdinalsDebug", "Saved HTML to ${file.absolutePath}")
}

// Endpoints de prueba conocidos
val TEST_ADDRESSES = listOf(
    "bc1p..."  // Tu propia wallet de test
)
```

---

## Apéndice A: Referencia Rápida de Regex

```kotlin
// Direcciones Bitcoin (taproot, native segwit, etc.)
val ADDRESS_REGEX = Regex("""(bc1|tb1)[a-zA-HJ-NP-Z0-9]{39,59}""")

// Output ID en HTML de /address
val OUTPUT_HREF_REGEX = Regex("""href=/output/([^ >]+)>""")

// Inscription ID en HTML de /output
val INSCRIPTION_ID_REGEX = Regex("""/inscription/([a-f0-9]{64}i\d+)""")

// Inscription Number en HTML de /inscription
val INSCRIPTION_NUMBER_REGEX = Regex("""Inscription\s*#?\s*(\d+)""")

// Sat balance
val SAT_BALANCE_REGEX = Regex("""<dt>sat balance</dt>\s*<dd>(\d+)</dd>""", RegexOption.DOT_MATCHES_ALL)

// Runes
val RUNE_REGEX = Regex("""<a class=monospace href=/rune/([^>]+)>([^<]+)</a>\s*:\s*([^<\s<]+)""")

// Bitmap full block: N.bitmap
val BITMAP_FULL_REGEX = Regex("""^(\d+)\.bitmap\s*$""")

// Parcela: N.N.bitmap
val PARCEL_REGEX = Regex("""^(\d+)\.(\d+)\.bitmap\s*$""")

// JSON content detection
val JSON_OBJECT_REGEX = Regex("""^\{.*\}$""", RegexOption.DOT_MATCHES_ALL)
val JSON_ARRAY_REGEX = Regex("""^\[.*\]$""", RegexOption.DOT_MATCHES_ALL)

// Protocol field in JSON
val PROTOCOL_FIELD_REGEX = Regex(""""p"\s*:\s*"([^"]+)""")
val TICK_FIELD_REGEX = Regex(""""tick"\s*:\s*"([^"]+)""")
val OP_FIELD_REGEX = Regex(""""op"\s*:\s*"([^"]+)""")
```

---

## Apéndice B: Estructura de Datos Final (UserInscription)

```kotlin
data class UserInscription(
    // Identificadores
    val id: String,                    // inscriptionId: "abc123...def456i0"
    val number: Int,                   // Índice en array local (0-based)
    val InscriptionNumber: Int?,       // Número real en cadena (desde /inscription)
    
    // Propiedad
    val address: String,               // Dirección owner
    
    // Contenido y metadatos
    val name: String?,                 // Display name: "NATPUNKS #1", "4.133622.bitmap"
    val contentType: String?,          // "text/plain", "image/png", "application/json"
    val metaprotocol: String?,         // "brc-20", "tap", "bitmap" (desde indexador)
    
    // Clasificación
    val collectionName: String?,       // "NATPUNKS", "Bitmaps", "Parcelas", "SATS"
    val protocol: String?,             // "tap", "brc-20", "bitmap"
    val tick: String?,                 // "natpunks", "sats", "nat"
    
    // Flags bitmap
    val isBitmap: Boolean = false,
    val isParcel: Boolean = false,
    
    // Datos extra para protocolos complejos
    val extraData: Map<String, Any>? = null  // deployment, block, coord, etc.
)
```

---

## Apéndice C: Checklist de Implementación

- [ ] Registrar `unisat://response` en AndroidManifest
- [ ] Implementar `NonceRepository` con SecureSharedPreferences
- [ ] Construir deep links con `DeepLinkBuilder`
- [ ] Manejar callback en `UnisatWalletCallbackActivity`
- [ ] Validar nonce en respuesta
- [ ] Decodificar Base64 de `getAddresses`
- [ ] Guardar dirección en repository/preferences
- [ ] Implementar `loadUserInscriptions(address)`
- [ ] Fetch `/address/{addr}` con parsing de outputs
- [ ] Fetch `/output/{id}` en chunks para inscriptionIds
- [ ] Fetch paralelo `/content/{id}` + `/inscription/{id}`
- [ ] Parsear contenido raw → detectar JSON/bitmap/parcela/text
- [ ] Extraer inscriptionNumber del HTML de detalle
- [ ] Clasificar con `identifyCollection()` (prioridad: contenido > metaprotocol > content-type)
- [ ] Agrupar por `collectionName` para UI
- [ ] Cachear en preferences/Room
- [ ] Implementar pull-to-refresh e invalidación de cache
- [ ] Tests con direcciones conocidas (mainnet/testnet)
- [ ] Logs sanitizados (sin direcciones completas)
- [ ] Manejo de errores y timeouts

---

**Documento generado:** 2026-07-09  
**Versión:** 1.0  
**Basado en:** BitmapCoreApp (com.bitmapcore.bitmapcore) - Logs de producción 2026-07-08
