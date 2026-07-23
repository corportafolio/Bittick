# Pantalla 02 — Trading — Diseño Visual

> **Relación con otros documentos:**
> - `docs/08_pantalla-conectar-wallet-y-boton-usar.md` — Pantalla de conexión de wallet
> - `diseño de pantallas/01_pantalla-conectar-wallet-boton-usar.md` — Diseño de pantalla wallet
> - `TradingScreen.kt` — Código fuente de la pantalla principal
> - `TradingViewModel.kt` — ViewModel con lógica de datos
> - `Models.kt` — Modelos de datos (BotPosition, BotStatusItem, etc.)

---

## 1. Layout General de la Pantalla

La pantalla de trading es la pantalla principal de la app. Se compone de un TopAppBar, un drawer de navegación y un LazyColumn con 4 secciones principales:

```
┌─────────────────────────────────────────┐
│  TopAppBar: "bittick"        [☰ Menu]  │  ← Fijo arriba
├─────────────────────────────────────────┤
│  BURBUJA 1: Gráfica Bitcoin            │  ← Card con chart + intervalos
│  BURBUJA 2: Bot SPOT                   │  ← Card con estado + posiciones
│  BURBUJA 3: Bot FUTUROS                │  ← Card con estado + posiciones
│  BURBUJA 4: Oportunidades Detectadas   │  ← Lista de oportunidades
└─────────────────────────────────────────┘
```

**Código:** `TradingScreen.kt` función `TradingScreen()` línea ~92

### Drawer de Navegación

Al tocar el ícono de menú (☰) o la imagen del bot, se abre un drawer lateral:

```
┌──────────────────────────┐
│  bittick                 │
│                          │
│  ▸ Trading               │  ← Seleccionado
│    Ajustes               │
│    Wallet: bc1pha...60nyz│
└──────────────────────────┘
```

**Código:** `ModalNavigationDrawer` línea ~108

### LazyColumn

El contenido principal usa `LazyColumn` con `verticalArrangement = Arrangement.spacedBy(10.dp)` — 10dp entre cada sección.

**Código:** `LazyColumn` línea ~211

---

## 2. Burbuja 1 — Gráfica Bitcoin con Zonas de Demanda

### Aspecto Visual

```
┌─────────────────────────────────────────────┐
│  [1m] [5m] [15m] [30m] [1h] [4d] [1d] ...  │  ← Chips de intervalo
├─────────────────────────────────────────────┤
│                                             │
│         📊 CANDLE CHART (300dp alto)        │  ← WebView + LightweightCharts
│         Zonas de soporte/resistencia        │
│                                             │
├─────────────────────────────────────────────┤
│  Klines: OK  (verde si OK, rojo si error)   │  ← Status text
│  BTC/USDT $67,450.00                        │  ← Precio actual
└─────────────────────────────────────────────┘
```

### Elementos

| Elemento | Tipo | Descripción |
|----------|------|-------------|
| FilterChips | FilterChip | Lista horizontal scrollable de intervalos: 1m, 5m, 15m, 30m, 1h, 4h, 1d, 1w, 1M |
| Chart | Box (300dp) | CandleChartView (WebView) con klines y zonas |
| Chart status | Text | Estado de carga: "iniciando...", "Klines: OK", "Klines: error..." |
| Current price | Text | "BTC/USDT $XX,XXX.XX" — BittickColor, bold, titleMedium |

### Lógica

- **Solo para premium**: Los chips de intervalo y el chart completo solo se muestran si `!state.isFreeTier`
- **Free tier**: Muestra un placeholder de 120dp con texto "Contenido premium"
- **Cambio de intervalo**: Al tocar un chip → `viewModel.changeChartInterval(interval)` → recarga klines
- **Zonas**: Se dibujan en el chart como líneas horizontales (soporte/resistencia)

**Código:** `TradingScreen.kt` línea ~230

### Datos del Servidor

| Endpoint | Propósito |
|----------|-----------|
| `GET /api/chart/klines?interval=1h&limit=200` | Velas japonesas |
| `GET /api/chart/ticker` | Precio actual BTC/USDT |
| `GET /api/chart/zones?interval=1h&limit=200` | Zonas de soporte/resistencia + ATR |

---

## 3. Burbuja 2 — Bot SPOT

### Aspecto Visual

```
┌─────────────────────────────────────────────┐
│  ▶  BOT SPOT BTC                ACTIVO  ▼  │  ← Header expandible
├─────────────────────────────────────────────┤
│  Balance: $100.00 (disponible: $50.00)      │
│  Posiciones abiertas: 2/5                   │
│  PNL Total: $12.50  (verde si +, rojo si -) │
│                                             │
│  ┌─────────────────────────────────────┐    │
│  │ [SPOT] BTCUSDT [Abierta]   +$5.20  │    │  ← PositionCard
│  │ Puntaje: 8/10  Confianza: 7/10     │    │
│  │ Apostado: $10                       │    │
│  │ Entrada: $67500  Actual: $67600     │    │
│  │ Objetivo: $68000                    │    │
│  │ No stop en spot. Cerrar manualmente.│    │
│  │ ┌──────────┐ ┌──────────┐          │    │  ← Burbujas timestamps
│  │ │Iniciada  │ │Terminada │          │    │
│  │ │15 Jul    │ │15 Jul    │          │    │
│  │ └──────────┘ └──────────┘          │    │
│  │ [CERRAR POSICION]                  │    │
│  └─────────────────────────────────────┘    │
│                                             │
│  ┌─────────────────────────────────────┐    │
│  │ [SPOT] ETHUSDT [Cerrada]   -$1.30  │    │  ← PositionCard cerrada
│  │ Puntaje: 6/10  Confianza: 5/10     │    │
│  │ Apostado: $20                       │    │
│  │ Entrada: $3500  Cerrada: $3480      │    │
│  │ Objetivo: $3600                     │    │
│  │ ┌──────────┐ ┌──────────┐          │    │
│  │ │Iniciada  │ │Terminada │          │    │
│  │ │14 Jul    │ │15 Jul    │          │    │
│  │ └──────────┘ └──────────┘          │    │
│  │ PNL: -$1.30 (-0.37%)     [🗑]     │    │
│  └─────────────────────────────────────┘    │
└─────────────────────────────────────────────┘
```

### Elementos del Header

| Elemento | Tipo | Descripción |
|----------|------|-------------|
| PlayArrow icon | Icon | Verde si ACTIVO, gris si INACTIVO |
| Title | Text | "BOT SPOT BTC" — bold, titleMedium |
| Status badge | Text | "ACTIVO" verde o "INACTIVO" rojo — labelSmall bold |
| Expand icon | Icon | ExpandLess/ExpandMore — toggle expandir/colapsar |

### Elementos de Info (dentro de AnimatedVisibility)

| Elemento | Tipo | Descripción |
|----------|------|-------------|
| Balance | Text | "Balance: $X (disponible: $Y)" — bodySmall |
| Posiciones count | Text | "Posiciones abiertas: X/Y" — bodySmall |
| PNL Total | Text | "PNL Total: $X" — bodySmall, verde/rojo |

### Elementos de PositionCard (ver sección 5)

### Lógica

- **Expanded por defecto**: `expanded = remember { mutableStateOf(true) }`
- **Toggle**: Al tocar el header → `expanded.value = !expanded.value`
- **AnimatedVisibility**: expandVertically() / shrinkVertically()
- **Spot no tiene stop loss**: Si `bot_type == "spot"`, se muestra "No stop en spot. Cerrar la posicion manualmente." en vez de stop
- **Close position**: Botón "CERRAR POSICION" → AlertDialog → `viewModel.closePosition(pos.id)` → status = "closed"
- **Dismiss position**: Icono delete en posición cerrada → `viewModel.dismissPosition(pos.id)` → elimina de la lista

**Código:** `BotSection()` línea ~348

---

## 4. Burbuja 3 — Bot FUTUROS

### Aspecto Visual

Idéntica a la Burbuja 2 pero con las diferencias:

```
┌─────────────────────────────────────────────┐
│  ▶  BOT FUTUROS BTC            ACTIVO  ▼   │
├─────────────────────────────────────────────┤
│  Balance: $200.00 (disponible: $150.00)     │
│  Posiciones abiertas: 1/3                   │
│  PNL Total: $8.40                           │
│                                             │
│  ┌─────────────────────────────────────┐    │
│  │ [LONG] BTCUSDT [Abierta]   +$8.40  │    │
│  │ Puntaje: 9/10  Confianza: 8/10     │    │
│  │ Apostado: $50                       │    │
│  │ Entrada: $67000  Actual: $67400     │    │
│  │ Objetivo: $68500  Stop: $66500      │    │  ← SÍ tiene stop
│  │ ┌──────────┐ ┌──────────┐          │    │
│  │ │Iniciada  │ │Terminada │          │    │
│  │ │15 Jul    │ │          │          │    │  ← Solo si cerrada
│  │ └──────────┘ └──────────┘          │    │
│  │ [CERRAR POSICION]                  │    │
│  └─────────────────────────────────────┘    │
└─────────────────────────────────────────────┘
```

### Diferencias con SPOT

| Aspecto | SPOT | FUTUROS |
|---------|------|---------|
| Badge tipo | `[SPOT]` naranja | `[LONG]` verde o `[SHORT]` rojo |
| Stop loss | No se muestra (aviso manual) | Se muestra stop loss |
| Colores señal | Naranja (`#F57C00`) | Verde long (`#1B5E20`) / Rojo short (`#B71C1C`) |
| Posiciones | Se holdean, no se venden en pérdidas | Se pueden cerrar por stop |

### Lógica

Igual que SPOT excepto:
- **Sí tiene stop loss**: Se muestra `Stop: $X` en rojo
- **Close position**: Mismo flujo con AlertDialog
- **Dismiss**: Mismo comportamiento

**Código:** `BotSection()` línea ~348 (misma función, diferente label)

---

## 5. PositionCard — Detalle de Posición Abierta/Cerrada

### Aspecto Visual (Posición Abierta)

```
┌─────────────────────────────────────────────┐
│ [SPOT] BTCUSDT  [Abierta]          +$5.20  │
│ Puntaje: 8/10  Confianza: 7/10  Apostado: $10│
│ Entrada: $67500   Actual: $67600            │
│ Objetivo: $68000                           │
│ No stop en spot. Cerrar la posicion        │
│ manualmente.                               │
│ ┌──────────────────┐ ┌──────────────────┐  │
│ │ Orden iniciada   │ │                  │  │
│ │ 15 Jul 2026,14:30│ │                  │  │
│ └──────────────────┘ └──────────────────┘  │
│ [CERRAR POSICION]                          │
└─────────────────────────────────────────────┘
```

### Aspecto Visual (Posición Cerrada)

```
┌─────────────────────────────────────────────┐
│ [SPOT] BTCUSDT  [Cerrada]          [🗑]    │
│ Puntaje: 8/10  Confianza: 7/10  Apostado: $10│
│ Entrada: $67500   Cerrada: $67200           │
│ Objetivo: $68000                           │
│ ┌──────────────────┐ ┌──────────────────┐  │
│ │ Orden iniciada   │ │ Orden terminada  │  │
│ │ 15 Jul 2026,14:30│ │ 15 Jul 2026,18:45│  │
│ └──────────────────┘ └──────────────────┘  │
│ PNL: -$0.44 (-0.44%)                      │
└─────────────────────────────────────────────┘
```

### Elementos

| Elemento | Tipo | Descripción |
|----------|------|-------------|
| Badge tipo | Card (CircleShape) | `[SPOT]` naranja / `[LONG]` verde / `[SHORT]` rojo |
| Asset name | Text | "BTCUSDT" — bold, bodyMedium |
| Badge estado | Card | `[Abierta]` verde / `[Cerrada]` rojo — labelSmall bold |
| PNL | Text | "$X.XX (X.XX%)" — verde si +, rojo si - (solo abierta) |
| Delete icon | IconButton | 🗑 rojo — solo si cerrada, elimina de la lista |
| Puntaje | Text | "Puntaje: X/10" — bodySmall |
| Confianza | Text | "Confianza: X/10" — bodySmall |
| Apostado | Text | "Apostado: $XX" — BittickColor bold (solo si > 0) |
| Entrada | Text | "Entrada: $X" — bodySmall |
| Actual/Cerrada | Text | "Actual: $X" o "Cerrada: $X" — bodySmall |
| Objetivo | Text | "Objetivo: $X" — bodySmall, alpha 0.6 |
| Stop (futures) | Text | "Stop: $X" — rojo alpha 0.6 |
| Aviso spot | Text | "No stop en spot. Cerrar la posicion manualmente." — naranja |
| Orden iniciada | Card (50% width) | Burbuja con label + fecha |
| Orden terminada | Card (50% width) | Burbuja con label + fecha (solo cerrada) |
| PNL cerrada | Text | "PNL: $X (X.XX%)" — verde/rojo bold (solo cerrada) |
| Cerrar | Button | Rojo, "CERRAR POSICION" — solo abierta |

### Lógica de Estados

#### Posición Abierta (`status != "closed"`)
- Badge `[Abierta]` verde
- Muestra PNL actual en la primera fila
- Muestra "Actual: $X" en la segunda fila
- Muestra stop loss si es futuros
- Muestra aviso "No stop en spot" si es spot
- Muestra botón "CERRAR POSICION"
- Al tocar → AlertDialog de confirmación → `viewModel.closePosition(pos.id)`

#### Posición Cerrada (`status == "closed"`)
- Badge `[Cerrada]` rojo
- Muestra icono delete en la primera fila (reemplaza PNL)
- Muestra "Cerrada: $X" en la segunda fila
- No muestra stop ni aviso
- Muestra "Orden terminada" en la segunda burbuja de timestamp
- Muestra PNL final abajo
- Al tocar delete → `viewModel.dismissPosition(pos.id)` → elimina de la lista

### Burbujas de Timestamp

Las burbujas de timestamp usan `Row` con `Arrangement.spacedBy(6.dp)`:
- Cada burbuja ocupa `Modifier.weight(1f)` (50% del ancho)
- Fondo: `Secondary.copy(alpha = 0.08f)` (gris claro transparente)
- Label: "Orden iniciada" / "Orden terminada" — labelSmall, alpha 0.5
- Fecha: `formatDateTimeLocal()` — "15 Jul 2026, 14:30:00"

**Código:** `PositionCard()` línea ~411

---

## 6. Burbuja 4 — Oportunidades Detectadas

### Aspecto Visual

```
  Oportunidades detectadas

┌─────────────────────────────────────────────┐
│  [LONG]  BTCUSDT            $67,450  [🗑]  │
│                                             │
│  Puntaje: 8/10     Confianza: 7/10         │
│  Entrada: $67,000   Objetivo: $69,000       │
│  Stop Loss: $66,000                         │
│                                             │
│  Señales tecnicas: RSI sobrevendido,        │
│  cruce de medias moviles alcista...         │
│                                             │
│  Factores:                                  │
│  • Volumen creciente                        │
│  • Soporte en $67,000                       │
│  • Tendencia alcista en 4H                  │
│                                             │
│  15 Jul 2026, 14:30:00          martes      │
└─────────────────────────────────────────────┘
```

### Elementos

| Elemento | Tipo | Descripción |
|----------|------|-------------|
| Header title | Text | "Oportunidades detectadas" — titleSmall, bold, Secondary |
| Badge type | Card | `[LONG]` verde / `[SHORT]` rojo |
| Asset | Text | "BTCUSDT" — bold, titleMedium |
| Price | Text | "$XX,XXX" — bold, Secondary |
| Delete icon | IconButton | 🗑 rojo — elimina la oportunidad |
| Puntaje | InfoChip | "Puntaje: X/10" |
| Confianza | InfoChip | "Confianza: X/10" |
| Entrada | InfoChip | "Entrada: $X" |
| Objetivo | InfoChip | "Objetivo: $X" |
| Stop Loss | InfoChip | "Stop Loss: $X" |
| Explicación | Text | Texto largo de la IA — bodySmall |
| Factores | Text | Lista de factores con bullet points |
| Fecha | Text | Fecha de creación + día de la semana |

### InfoChip

```
┌──────────┐
│ Puntaje  │  ← labelSmall, alpha 0.5
│ 8/10     │  ← bodySmall, bold
└──────────┘
```

### Lógica

- **Filtrado**: Solo se muestran oportunidades con `score >= 5` y `confidence >= 5`
- **Orden**: Por score descendente
- **Delete**: Al tocar 🗑 → `viewModel.deleteOpportunity(id)` → elimina de la lista
- **Empty state**: "No hay oportunidades aun." — alpha 0.4
- **Notificaciones**: Oportunidades con score >= 6 y confidence >= 6 se anuncian por notificación

**Código:** `OpportunityCard()` línea ~487

---

## 7. Referencia de Código

| Archivo | Función | Línea | Elemento |
|---------|---------|-------|----------|
| `TradingScreen.kt` | `TradingScreen()` | ~92 | Layout general + drawer |
| `TradingScreen.kt` | `BotSection()` | ~348 | Burbuja 2 y 3 (bots) |
| `TradingScreen.kt` | `PositionCard()` | ~411 | Posición abierta/cerrada |
| `TradingScreen.kt` | `OpportunityCard()` | ~487 | Oportunidad detectada |
| `TradingScreen.kt` | `InfoChip()` | ~660 | Chip informativo |
| `TradingViewModel.kt` | `loadAll()` | ~185 | Carga datos de APIs |
| `TradingViewModel.kt` | `closePosition()` | ~318 | Cierra posición |
| `TradingViewModel.kt` | `dismissPosition()` | ~338 | Elimina posición de UI |
| `TradingViewModel.kt` | `deleteOpportunity()` | ~343 | Elimina oportunidad |
| `Models.kt` | `BotPosition` | ~33 | Modelo de posición |
| `Models.kt` | `BotStatusItem` | ~54 | Estado del bot |
| `CandleChartView.kt` | `CandleChartView()` | — | Gráfica WebView |

---

## 8. Estados de Carga

| Estado | Visual |
|--------|--------|
| `isLoading = true` | CircularProgressIndicator centrado |
| `isLoading = false` + datos | LazyColumn con todas las burbujas |
| `error != null` | Card roja con mensaje de error |
| `isFreeTier = true` | Card azul "Conecta una wallet...", chart placeholder |
| `isFreeTier = false` | Todo visible, chart completo, bots, oportunidades |

---

## 9. Colores Utilizados

| Color | Hex | Uso |
|-------|-----|-----|
| Primary | Theme | Fondo de pantalla, TopAppBar |
| Secondary | Theme | Texto secundario, borders |
| Surface | Theme | Fondo de cards |
| BittickColor | #F7931A | Título, precio actual, acento naranja |
| Verde | #1B5E20 | ACTIVO, Long, PNL positivo, Abierta |
| Rojo | #B71C1C | INACTIVO, Short, PNL negativo, Cerrada, Cerrar |
| Naranja | #F57C00 | SPOT badge, aviso manual close |
| Gris | #757575 | Inactivo, texto apagado |
