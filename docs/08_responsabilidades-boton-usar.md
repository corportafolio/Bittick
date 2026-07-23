# Pantalla Conectar Wallet y Botón USAR — Diseño y Responsabilidades

> **Documenta la habilidad de la pantalla WalletScreen y las responsabilidades del botón USAR.**
> 
> **Relación con otros documentos:**
> - [Doc 05: Cómo Conectar una Wallet Bitcoin](05_como_conectar_%20una_wallet_bitcoin_a_una_app_en_android.md) — Flujo completo de conexión (nonce, firma, diálogos). El USAR es el paso siguiente.
> - [Doc 06: Flujo de Conexión Unisat](06_Flujo-Conexion-Unisat.md) — Detalle del flujo manual Unisat. El USAR se ejecuta DESPUÉS de que la wallet está conectada.
> - [Doc 03: IDs de Colección Bittick Agent](03_IDs-coleccion-Bittick-Agent.md) — Lista de los 100 bots disponibles para seleccionar.
> - [Doc 04: Licencia Premium Ordinals](04_licencia-premium-ORDINALS_Bittick-agents.md) — Sistema de licencias que determina tier (FOUNDER/STANDARD).

---

## Resumen Ejecutivo

La pantalla **WalletScreen** es la interfaz donde el usuario conecta su wallet Bitcoin, visualiza sus inscripciones de Bittick Agents y selecciona qué bot usar. El botón **USAR** es la acción principal que activa un bot premium, desbloqueando las oportunidades de trading, la gráfica de velas y los bots SPOT/FUTUROS con sus posiciones abiertas.

---

## 1. Diseño de la Pantalla WalletScreen

### 1.1 Estados de la Pantalla

```
┌─────────────────────────────────────────────────────┐
│  ESTADO 1: Sin wallet conectada                      │
│  ┌─────────────────────────────────────────────┐     │
│  │  [Unisat]    [Otras Wallets]                │     │
│  │  "Conecta una wallet para ver inscripciones"│     │
│  └─────────────────────────────────────────────┘     │
├─────────────────────────────────────────────────────┤
│  ESTADO 2: Wallet conectada, sin inscripciones       │
│  ┌─────────────────────────────────────────────┐     │
│  │  Wallet: bc1pha4hfr...q30dn5w               │     │
│  │  [Recargar]  [Desconectar]                  │     │
│  │  "No se encontraron Bittick Agents"         │     │
│  └─────────────────────────────────────────────┘     │
├─────────────────────────────────────────────────────┤
│  ESTADO 3: Wallet conectada con inscripciones        │
│  ┌─────────────────────────────────────────────┐     │
│  │  Wallet: bc1pha4hfr...q30dn5w               │     │
│  │  [Recargar]  [Desconectar]                  │     │
│  │                                              │     │
│  │  ┌─────────────────────────────────────┐     │     │
│  │  │ 🤖 Bot #88                          │     │     │
│  │  │ FOUNDER                             │     │     │
│  │  │                    [PREMIUM] [USAR] │     │     │
│  │  └─────────────────────────────────────┘     │     │
│  │  ┌─────────────────────────────────────┐     │     │
│  │  │ 🤖 Bot #73                          │     │     │
│  │  │ STANDARD                            │     │     │
│  │  │                    [SELECCIONADO]   │     │     │
│  │  └─────────────────────────────────────┘     │     │
│  └─────────────────────────────────────────────┘     │
└─────────────────────────────────────────────────────┘
```

### 1.2 Lógica del Botón USAR vs SELECCIONADO

```kotlin
// WalletScreen.kt — SelectedInscriptionCard
val isAlreadySelected = selectedInscription?.inscriptionId == inscription.inscriptionId

if (isAlreadySelected) {
    Text("SELECCIONADO")  // No es clickeable
} else {
    Button(onClick = onConfirmSelection) {
        Text("USAR")
    }
}
```

El botón USAR **solo aparece** en inscripciones que NO son la actualmente seleccionada. Si ya está seleccionada, muestra "SELECCIONADO" en texto plano.

---

## 2. Botón USAR — Flujo Completo

### 2.1 Diagrama de Secuencia

```
┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐
│  User    │    │WalletVM  │    │ Server   │    │TradingVM │
└────┬─────┘    └────┬─────┘    └────┬─────┘    └────┬─────┘
     │ Toca USAR     │               │               │
     │──────────────►│               │               │
     │               │ POST /select- │               │
     │               │ inscription   │               │
     │               │──────────────►│               │
     │               │  200 OK       │               │
     │               │◄──────────────│               │
     │               │               │               │
     │               │ updatePrefs() │               │
     │               │ (sesión 7d)   │               │
     │               │               │               │
     │               │ tradingRefresh│               │
     │               │ Trigger++     │               │
     │               │               │               │
     │               │ popBackStack()│               │
     │               │──────────►trading             │
     │               │               │  LaunchedEffect│
     │               │               │  (trigger)    │
     │               │               │──────────────►│
     │               │               │               │
     │               │               │  loadAll()    │
     │               │               │  ┌────────────┤
     │               │               │  │ GET /opp   │
     │               │               │  │ GET /pos   │
     │               │               │  │ GET /bot   │
     │               │               │  └────────────┤
     │               │               │               │
     │               │               │  State update │
     │               │               │◄──────────────│
     │               │               │               │
     │               │               │  UI renders:  │
     │               │               │  - Chart ✅   │
     │               │               │  - Bots ✅    │
     │               │               │  - Positions ✅│
```

### 2.2 Paso 1: POST /api/auth/select-inscription

**Propósito:** Registrar en el servidor qué bot seleccionó el usuario.

**Request:**
```http
POST /api/auth/select-inscription
Header: x-wallet-address: bc1pha4hfr...q30dn5w
Body: { "inscriptionId": "7dc12cfc..." }
```

**Response (200):**
```json
{
  "exito": true,
  "data": {
    "selectedInscriptionId": "7dc12cfc...",
    "selectedBotNum": 88,
    "tier": "FOUNDER",
    "botImageUrl": "/api/auth/bot-image/088.png"
  }
}
```

**Qué hace en el servidor:** Marca la inscripción como seleccionada en la DB (`user_inscriptions`), establece el owner activo para esa dirección.

### 2.3 Paso 2: Actualizar SharedPreferences

**Propósito:** Guardar la sesión del bot seleccionado para que persista entre reinicios.

```kotlin
// WalletViewModel.kt — confirmSelection()
preferences.updateSelectedInscription(
    selectedInscriptionId = preview.inscriptionId,
    botNumber = preview.num,
    tier = preview.tier
)
```

**Qué actualiza:**
- `selected_inscription_id` → ID de la inscripción
- `bot_number` → Número del bot (#88) ← **SE GUARDA AQUÍ**
- `is_premium` → `true` si tier es FOUNDER
- `wallet_session` JSON → Actualiza el objeto completo con el nuevo bot

### 2.4 Paso 3: Navegar de Vuelta + Refresh

**Propósito:** Cargar datos frescos del servidor en el ViewModel correcto de TradingScreen.

```kotlin
// MainActivity.kt — onConfirmSelection
walletViewModel.confirmSelection {
    tradingRefreshTrigger++    // Incrementa trigger
    navController.popBackStack()  // Vuelve a TradingScreen
}
```

**Por qué se usa `tradingRefreshTrigger` en vez de llamar `loadAll()` directamente:**

Exiten **dos instancias** de `TradingViewModel`:
1. **Activity-scoped** (creada en `MainActivity.setContent`) — se usa para el callback de USAR
2. **NavBackStackEntry-scoped** (creada dentro de `composable("trading")`) — la que TradingScreen observa

Si se llamara `loadAll()` directamente, actualizaría la instancia Activity-scoped, pero TradingScreen leería de la instancia NavBackStackEntry-scoped. El refreshTrigger hace que `LaunchedEffect` en TradingScreen llame `loadAll()` en la instancia correcta.

### 2.5 Paso 4: LaunchedEffect(refreshTrigger)

**Propósito:** Ejecutar `loadAll()` en el ViewModel que TradingScreen realmente usa.

```kotlin
// TradingScreen.kt
LaunchedEffect(refreshTrigger) {
    viewModel.loadAll()
}
```

Cuando `refreshTrigger` cambia (de 0 a 1, de 1 a 2, etc.), Compose relanza el effect y `loadAll()` se ejecuta con la dirección de wallet correcta.

---

## 3. APIs que se Invocan después de USAR

### 3.1 GET /api/trading/opportunities

**Propósito:** Obtener las oportunidades de trading premium (todas las que superen score≥5 y confidence≥5).

**Parámetros:**
| Ubicación | Parámetro | Requerido | Descripción |
|-----------|-----------|-----------|-------------|
| Header | `x-wallet-address` | Sí | Dirección de wallet |
| Query | `limit` | No | Máximo resultados (default: 50) |
| Query | `offset` | No | Paginación |
| Query | `since` | No | Filtrar por fecha |

**Response Premium (200):** Retorna TODAS las oportunidades ordenadas por `created_at DESC`.

**Response Free (300):** Solo retorna oportunidades de los últimos 3 días con score/confidence entre 5-6.

### 3.2 GET /api/trading/positions

**Propósito:** Obtener las posiciones abiertas del bot seleccionado.

**Parámetros:**
| Ubicación | Parámetro | Requerido | Descripción |
|-----------|-----------|-----------|-------------|
| Header | `x-wallet-address` | Sí | Dirección de wallet |
| Query | `type` | No | Filtrar por `spot` o `futures` |
| Query | `status` | No | Default: `open` |

**Response Premium (200):** Retorna posiciones con `bot_type`, `strategy_type`, `pnl`, `pnl_percent`, etc.

**Response Free (300):** Array vacío.

### 3.3 GET /api/trading/bot/status

**Propósito:** Obtener el estado de los bots SPOT y FUTUROS (habilitados/deshabilitados, posiciones abiertas, PNL total, balance).

**Response Premium (200):**
```json
{
  "data": {
    "spot": { "enabled": true, "openPositions": 2, "totalPnl": 150.50, "balance": { "total": 1000, "available": 600 } },
    "futures": { "enabled": false, "openPositions": 0, "totalPnl": 0, "balance": { "total": 500, "available": 500 } }
  }
}
```

**Response Free (300):** Todos los campos en 0, `enabled: false`, `balance: null`.

---

## 4. Qué se Actualiza en Pantalla después de USAR

| Elemento | Datos que muestra | Fuente |
|----------|-------------------|--------|
| **Gráfica de velas** | Klines + zonas de soporte/resistencia | `GET /api/chart/klines` + `GET /api/chart/zones` |
| **Ticker** | Precio BTC/USDT actual | `GET /api/chart/ticker` |
| **Oportunidades** | Lista filtrada (score≥5, confidence≥5) con tipo, precio, target, stop loss, explicación IA | `GET /api/trading/opportunities` |
| **Bot SPOT** | ACTIVO/INACTIVO, balance disponible, posiciones abiertas/max, PNL total | `GET /api/trading/bot/status` |
| **Bot FUTUROS** | ACTIVO/INACTIVO, balance disponible, posiciones abiertas/max, PNL total | `GET /api/trading/bot/status` |
| **Posiciones abiertas** | Tipo (LONG/SHORT), activo, precio entrada, PNL, PNL% | `GET /api/trading/positions` |

### 4.1 Condiciones de Visibilidad

```kotlin
// TradingScreen.kt
if (!state.isFreeTier) {  // isFreeTier = oppResponse.code() == 300
    BotSection("SPOT", state.spotBotStatus, state.spotPositions, viewModel, state.botNumber)
    BotSection("FUTUROS", state.futuresBotStatus, state.futuresPositions, viewModel, state.botNumber)
}
```

La sección de bots **solo se muestra** si `isFreeTier = false`, lo cual ocurre cuando el servidor retorna HTTP 200 para opportunities (wallet verificada con inscripción seleccionada).

Dentro de `BotSection`, el título se renderiza como:
```kotlin
Text("BOT $botNumber $label BTC")  // Ej: "BOT 88 SPOT BTC"
```

### 4.2 Lógica de ACTIVO vs INACTIVO

```kotlin
// TradingScreen.kt — BotSection
val enabled = status?.enabled == true
Text(if (enabled) "ACTIVO" else "INACTIVO")
```

- **ACTIVO:** `bot/status` retorna `enabled: true` — el bot está habilitado y ejecutando operaciones
- **INACTIVO:** `enabled: false` o `status` es null — el bot está deshabilitado o no hay datos

---

## 5. Logging del Flujo USAR

| Evento | Tag | Mensaje |
|--------|-----|---------|
| Toca USAR | `WalletVM` | `USAR presionado: Bot #88 \| tier=FOUNDER \| inscriptionId=7dc12cfc...` |
| Server responde OK | `WalletVM` | `Bot #88 seleccionado exitosamente` |
| loadAll ejecuta | `TradingVM` | `loadAll() addr=bc1pha4... \| oppCode=200 \| posCode=200 \| botCode=200` |
| Datos cargados | `TradingVM` | `loadAll() spotPos=2 futuresPos=0 \| spotEnabled=true futuresEnabled=false \| isFreeTier=false` |

---

## 6. Relación con Otras Habilidades

### 6.1 Con Doc 05 (Conexión de Wallet)

El USAR es el **paso final** del flujo de conexión. La secuencia es:
1. Conectar wallet (Doc 05) → wallet guardada en SharedPreferences
2. Ver inscripciones → lista de bots aparece
3. Seleccionar bot → preview con imagen y tier
4. **Presionar USAR** →激活 el bot y carga datos premium

Sin el paso 4, la wallet está conectada pero el bot no está activo.

### 6.2 Con Doc 06 (Flujo Unisat)

El USAR se ejecuta **después** de que el usuario completó los 2 diálogos (Confirmación + Pegar Dirección) y la wallet ya está verificada en el servidor.

### 6.3 Con Doc 03 (IDs de Colección)

Cada bot seleccionado tiene un `inscriptionId` que debe existir en la lista de los 100 IDs de `bittickCollection.js`. El servidor valida esta pertenencia antes de aceptar la selección.

### 6.4 Con Doc 04 (Licencia Premium)

El tier determinado por la inscripción (FOUNDER/STANDARD) controla:
- **FOUNDER:** Acceso completo a oportunidades, gráfica, bots activos
- **STANDARD:** Acceso limitado según configuración del servidor

### 6.5 Patrón de Arquitectura: ViewModel Scoping

El mecanismo `refreshTrigger` es una solución al patrón de **ViewModel scoping en Compose Navigation**:
- Cada `composable()` tiene su propio `ViewModelStoreOwner` (NavBackStackEntry)
- `hiltViewModel()` dentro de un composable crea una instancia scoped a ese entry
- Llamar `loadAll()` desde MainActivity (Activity scope) actualiza una instancia diferente
- Solución: usar `LaunchedEffect(refreshTrigger)` para ejecutar en la instancia correcta

---

## 7. Referencia de Código

| Archivo | Función/Componente | Línea | Responsabilidad |
|---------|-------------------|-------|-----------------|
| `WalletScreen.kt` | `SelectedInscriptionCard` | ~508 | Renderiza botón USAR / SELECCIONADO |
| `WalletViewModel.kt` | `confirmSelection()` | ~318 | POST select-inscription + actualizar prefs |
| `MainActivity.kt` | `onConfirmSelection` | ~84 | refreshTrigger++ + popBackStack |
| `TradingScreen.kt` | `LaunchedEffect(refreshTrigger)` | ~104 | Llama loadAll() en ViewModel correcto |
| `TradingViewModel.kt` | `loadAll()` | ~185 | Carga opportunities + positions + bot/status + botNumber desde prefs |
| `BittickPreferences.kt` | `updateSelectedInscription()` | ~194 | Guarda bot seleccionado en SharedPreferences |
| `BittickPreferences.kt` | `setBotNumber()` | ~72 | Escribe `bot_number` en SharedPreferences |
| `BittickPreferences.kt` | `getBotNumber()` | ~67 | Lee `bot_number` de SharedPreferences |
| `TradingScreen.kt` | `BotSection()` | ~350 | Muestra "BOT $num $label BTC" con el número del bot |
| `ApiService.kt` | `selectInscription()` | ~71 | Definición Retrofit del endpoint |

---

## 8. Bot Number — Cadena Completa USAR → TradingScreen

### 8.1 Flujo del Número de Bot

El número del bot se propaga desde que el usuario presiona USAR hasta que se renderiza en la pantalla de trading:

```
USAR presionado
    │
    ▼
WalletViewModel.confirmSelection()
    │  preview.num = 88
    │
    ▼
preferences.updateSelectedInscription(selectedInscriptionId, botNumber=88, tier)
    │
    ├──▶ WalletSession.botNumber = 88  (JSON en prefs)
    ├──▶ setBotNumber(88)              (KEY_BOT_NUMBER en prefs)
    │
    ▼
tradingRefreshTrigger++
    │
    ▼
TradingScreen: LaunchedEffect(refreshTrigger)
    │
    ▼
TradingViewModel.loadAll()
    │  val botNum = prefs.getBotNumber() ?: 0  // → 88
    │
    ▼
_state.copy(botNumber = 88)
    │
    ▼
BotSection("SPOT", ..., botNumber=88)
    │
    ▼
Text("BOT 88 SPOT BTC")
```

### 8.2 Funciones Involucradas

| Función | Archivo | Línea | Qué hace |
|---------|---------|-------|----------|
| `confirmSelection()` | `WalletViewModel.kt` | 318 | POST al server + llama `updateSelectedInscription()` |
| `updateSelectedInscription()` | `BittickPreferences.kt` | 194 | Actualiza `WalletSession` + llama `setBotNumber()` |
| `setBotNumber()` | `BittickPreferences.kt` | 72 | Escribe `KEY_BOT_NUMBER` en SharedPreferences |
| `getBotNumber()` | `BittickPreferences.kt` | 67 | Lee `KEY_BOT_NUMBER` de SharedPreferences |
| `loadAll()` | `TradingViewModel.kt` | 186 | Lee `getBotNumber()` y lo guarda en `TradingUiState.botNumber` |
| `BotSection()` | `TradingScreen.kt` | 350 | Recibe `botNumber` y renderiza "BOT $num $label BTC" |

### 8.3 Por qué es Necesario `botNumber` en TradingUiState

El número del bot se usa para:
1. **Identificación visual** — El usuario ve "BOT 88 SPOT BTC" en vez de solo "BOT SPOT BTC"
2. **Múltiples bots** — Cada inscripción tiene un número único (1-100). Si el usuario cambia de bot, el número actualiza
3. **Persistencia** — Si el usuario cierra y reabre la app, `loadAll()` lee el botNumber de SharedPreferences

### 8.4 Independencia del Bot Number

El `botNumber` es **solo visual**. El servidor identifica al usuario por la dirección de wallet (`x-wallet-address`), no por el número del bot. El número se usa para:
- Mostrar en la UI qué bot está activo
- Loggear en el server qué bot ejecutó cada operación (`bot_manager.js` línea ~63: `bot=${context.botNum || '?'}`)
- El server usa `inscription_id` internamente, no `bot_num`
