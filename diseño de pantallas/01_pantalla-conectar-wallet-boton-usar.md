# Pantalla 01 — Conectar Wallet — Diseño Visual y Botón USAR

> **Relación con otros documentos:**
> - `docs/05_como_conectar_ una_wallet_bitcoin_a_una_app_en_android.md` — Flujo de conexión de wallet
> - `docs/06_Flujo-Conexion-Unisat.md` — Flujo manual Unisat
> - `docs/03_IDs-coleccion-Bittick-Agent.md` — IDs de los 100 bots
> - `docs/04_licencia-premium-ORDINALS_Bittick-agents.md` — Sistema de licencias
> - `WalletScreen.kt` — Código fuente de la pantalla

---

## 1. Layout General de la Pantalla

La pantalla se compone de 4 elementos apilados verticalmente:

```
┌─────────────────────────────────────────┐
│  TÍTULO: "Cuenta Bittick"        [ ✕ ]  │  ← Fijo arriba
├─────────────────────────────────────────┤
│  BURBUJA 1: Wallet Conectada            │  ← Card fija
├─────────────────────────────────────────┤
│  BURBUJA 2: Botón USAR (temporal)       │  ← Card condicional
├─────────────────────────────────────────┤
│  BURBUJA 3: Lista de Inscripciones      │  ← Lista scrollable
│  ┌─────────────────────────────────┐    │
│  │  Bot #88  FOUNDER          ✓   │    │
│  │  Bot #73  STANDARD              │    │
│  │  Bot #44  FOUNDER               │    │
│  │  ...                            │    │
│  └─────────────────────────────────┘    │
└─────────────────────────────────────────┘
```

**Código:** `WalletScreen.kt` función `WalletScreen()` línea ~153

---

## 2. Burbuja 1 — Wallet Conectada

### Aspecto Visual

```
┌─────────────────────────────────────────────┐
│  Wallet conectada          PREMIUM  🤖 Bot 88│
│  bc1pha4hf...q30dn5w                        │
│                                              │
│  [Desconectar]   [Ver todos los bots...]     │
└─────────────────────────────────────────────┘
```

### Elementos

| Elemento | Tipo | Descripción |
|----------|------|-------------|
| Título | Text | "Wallet conectada" (blanco, bold) |
| Badge tier | Text | "PREMIUM" naranja o "GRATIS" gris |
| Bot number | Text | "Bot 88" si existe `botNumber` |
| Bot image | Image (CircleShape) | 60dp, borde naranja 2dp, imagen Base64 del bot |
| Address | Text | `bc1pha4hf...q30dn5w` (truncada 8+8 chars) |
| Desconectar | Button | Gris oscuro (`#2A2A2A`), texto gris |
| Ver bots | Button | Naranja (`#F7931A`), texto blanco bold |

### Lógica

- **Solo se muestra** si `walletState.connectedAddress != null`
- **Badge PREMIUM** aparece si `isPremium = true` (tier FOUNDER)
- **Bot image** se descarga de `GET /api/auth/bot-image/{NN}.png` y se guarda en SharedPreferences como Base64

**Código:** `ConnectedWalletSection()` línea ~330

---

## 3. Burbuja 2 — Botón USAR (Temporal)

### Aspecto Visual

```
┌─────────────────────────────────────────────┐
│  🤖  Bot #88                                │
│      FOUNDER                                │
│                   [PREMIUM]     [ USAR ]    │
└─────────────────────────────────────────────┘
```

Si el bot ya está seleccionado, en vez de `[ USAR ]` muestra:

```
│                   [PREMIUM]   SELECCIONADO  │
```

### Elementos

| Elemento | Tipo | Descripción |
|----------|------|-------------|
| Bot image | Image (CircleShape) | 64dp, borde naranja 2dp |
| Bot number | Text | "Bot #88" (blanco, 18sp, bold) |
| Tier | Text | "FOUNDER" naranja o tier en gris |
| Badge | Box | "PREMIUM" naranja fondo, texto negro |
| Botón USAR | Button | Cyan (`#00BCD4`), texto negro bold |
| Texto SELECCIONADO | Text | Cyan, bold, no es clickeable |

### Lógica

- **Aparece solo** si `previewInscription != null` (usuario tocó una inscripción de la lista)
- **Se oculta** cuando `previewInscription` se pone en `null` (después de USAR exitoso o al cancelar)
- **Condición USAR vs SELECCIONADO:**

```kotlin
val isAlreadySelected = selectedInscription?.inscriptionId == inscription.inscriptionId
```

Si `isAlreadySelected = true` → muestra "SELECCIONADO". Si `false` → muestra botón USAR.

**Código:** `SelectedInscriptionCard()` línea ~508

---

## 4. Burbuja 3 — Lista de Inscripciones

### Aspecto Visual

```
  Seleccionar inscripción         10 inscripciones

┌─────────────────────────────────────────────┐
│  ┌────┐                                     │
│  │ #88│  Bot #88              ✓  (seleccionado)│
│  └────┘  FOUNDER                              │
├─────────────────────────────────────────────┤
│  ┌────┐                                     │
│  │ #73│  Bot #73                             │
│  └────┘  STANDARD                            │
├─────────────────────────────────────────────┤
│  ┌────┐                                     │
│  │ #44│  Bot #44                             │
│  └────┘  FOUNDER                             │
├─────────────────────────────────────────────┤
│  ...                                         │
└─────────────────────────────────────────────┘
```

### Elementos por Card de Inscripción

| Elemento | Tipo | Descripción |
|----------|------|-------------|
| Number circle | Box (CircleShape) | 48dp, fondo gris oscuro, texto "#88" blanco bold |
| Bot name | Text | "Bot #88" (blanco, 16sp, bold) |
| Tier | Text | "FOUNDER" naranja o "STANDARD" gris |
| Check mark | Text | "✓" naranja, solo si está seleccionada |

### Lógica

- **Header** muestra "Seleccionar inscripción" + cantidad de inscripciones
- **Card seleccionada** tiene fondo naranja transparente (`#F7931A` 20% alpha) + borde
- **Al tocar** una inscripción → `onPreviewInscription(inscription)` → muestra la Burbuja 2 (USAR)
- **LazyColumn** con scroll vertical, spacing 8dp entre cards
- Si no hay inscripciones → `EmptyInscriptionsSection()` con texto "No se encontraron inscripciones"

**Código:** `InscriptionList()` línea ~667, `InscriptionCard()` línea ~688

---

## 5. Botón USAR — Responsabilidades

### Qué hace

1. **POST /api/auth/select-inscription** — Registra en el servidor qué bot seleccionó
2. **Actualizar SharedPreferences** — Guarda sesión del bot (inscriptionId, botNumber, tier) con 7 días de expiración
3. **Navegar de vuelta** — `tradingRefreshTrigger++` + `popBackStack()`
4. **Cargar datos frescos** — `LaunchedEffect(refreshTrigger)` ejecuta `loadAll()` en el ViewModel correcto

### APIs que se invocan después

| Endpoint | Propósito | Premium (200) | Free (300) |
|----------|-----------|---------------|------------|
| `GET /api/trading/opportunities` | Oportunidades | Datos completos | Limitado 3 días |
| `GET /api/trading/positions` | Posiciones | Posiciones abiertas | Array vacío |
| `GET /api/trading/bot/status` | Estado bots | SPOT/FUTUROS con estado | Todo en 0 |

### Qué se actualiza en TradingScreen

- **Gráfica** — Klines + zonas soporte/resistencia
- **Oportunidades** — Lista filtrada (score≥5, confidence≥5)
- **Bot SPOT** — ACTIVO/INACTIVO, balance, posiciones, PNL
- **Bot FUTUROS** — ACTIVO/INACTIVO, balance, posiciones, PNL
- **Posiciones** — LONG/SHORT, activo, precio, PNL, PNL%

### Logging

| Evento | Tag | Mensaje |
|--------|-----|---------|
| Toca USAR | `WalletVM` | `USAR presionado: Bot #X \| tier=Y \| inscriptionId=Z` |
| Server OK | `WalletVM` | `Bot #X seleccionado exitosamente` |
| loadAll | `TradingVM` | `loadAll() addr=... \| oppCode=... \| posCode=... \| botCode=...` |
| Datos | `TradingVM` | `loadAll() spotPos=N futuresPos=N \| spotEnabled=... futuresEnabled=...` |

---

## 6. Referencia de Código

| Archivo | Función | Línea | Elemento |
|---------|---------|-------|----------|
| `WalletScreen.kt` | `WalletScreen()` | ~153 | Layout general |
| `WalletScreen.kt` | `ConnectedWalletSection()` | ~330 | Burbuja 1 |
| `WalletScreen.kt` | `SelectedInscriptionCard()` | ~508 | Burbuja 2 (USAR) |
| `WalletScreen.kt` | `InscriptionList()` | ~667 | Burbuja 3 |
| `WalletScreen.kt` | `InscriptionCard()` | ~688 | Card individual |
| `WalletViewModel.kt` | `confirmSelection()` | ~318 | Lógica USAR |
| `MainActivity.kt` | `onConfirmSelection` | ~84 | Refresh + navigate |
| `TradingScreen.kt` | `LaunchedEffect(refreshTrigger)` | ~104 | Carga datos |
| `TradingViewModel.kt` | `loadAll()` | ~185 | APIs trading |
