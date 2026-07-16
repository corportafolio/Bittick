# MASTER PROMPT v6.0 — BITMAPCORE AI TRADING AGENTS
## Bitcoin Ordinals Premium License — BitmapCore

**Versión:** 6.0  
**Estado:** Prompt para generación de imágenes con IA (Qwen)  
**Pipeline:** PNG (IA) → PNG 512×512 → SVG (potrace) → palette 256 → oxipng → ≤5,120 bytes  
**Workflow:** Usted genera PNG → Yo proceso PNG→SVG→compresión

---

## ESPECIFICACIÓN MAESTRA — APLICA A TODOS LOS AGENTES

### Estilo visual
- Robot **humanoide elegante** con bordes suaves y curvos (NO figuras geométricas abstractas)
- Diseño premium, minimalista, profesional — parece un asistente IA de una empresa de tecnología
- Sin esquinas afiladas, sin formas de bloque, sin polígonos
- El robot ocupa **65-70% del canvas** centrado
- **Silueta única** por agente — reconocible incluso en negro total
- Fondo oscuro tecnológico (`#0D1117`) con paneles sutiles, líneas de grid o marcos

### Paleta (4 colores exactos)
| Color | Hex | Uso |
|-------|-----|-----|
| Fondo | `#0D1117` | Background |
| Azul | `#58A6FF` | Cuerpo principal del robot, estructura, glow |
| Verde | `#3FB950` | Sensores, indicadores, valor |
| Rojo/Naranja | `#F78166` | Alertas, eventos críticos, volumen |

### Partes del robot (todos los agentes)
1. **Cabeza**: Forma elegante, ligeramente alargada, con visor/sensor frontal (NO ojos)
2. **Torso**: Armadura elegante, con detalles técnicos integrados
3. **Hombros/Brazos**: Opcional — si se incluyen, deben ser sutiles y elegantes
4. **Sensores**: Elementos verdes o azules tipo barra de scan, puntos de luz, líneas de estado
5. **Herramienta de trading**: 1-2 elementos integrados (vela, gráfico, línea de tendencia, etc.)

### Elementos obligatorios
- **Glow perimetral**: Iluminación azul suave rodeando el contorno del robot
- **Sensores**: Mínimo 1 indicador verde o azul (tipo scan, barra, punto)
- **Herramienta de trading**: Integrada en el cuerpo o cerca de él
- **Fondo tecnológico**: No sólido — incluir grid, paneles, o líneas de circuito

### Logo BitmapCore (SOLO FOUNDERS)
- Archivo: `BitmapcoreOrdinalBot.png`
- Tamaño: ~90×90px (17.6% del canvas 512×512)
- Posición: esquina inferior derecha
- **El robot debe estar DELANTE del logo** — el logo va detrás
- Solo en los 10 Founders, no en Standards

### Prohibido
- ❌ Figuras geométricas abstractas como protagonistas (cuadrados, hexágonos, pentágonos, escudos)
- ❌ Ojos redondos, caras cartoon, expresiones humanas
- ❌ Micro-textos, hashes, etiquetas
- ❌ Más de 5 elementos visuales
- ❌ Líneas menores a 2px
- ❌ Degradados, sombras, metalizados

---

## 10 FOUNDER AGENTS — PROMPTS PARA QWEN

---

### FB#01 — GENESIS
**Bloque:** 0 | **Fecha:** 2009-01-03 | **Evento:** Bloque génesis — Origen de Bitcoin

**Prompt:**
```
Robot humanoide elegante y minimalista, cuerpo azul (#58A6FF) sobre fondo oscuro (#0D1117) con cuadrícula tecnológica sutil. El robot tiene forma estilizada con cabeza ligeramente alargada y torso aerodinámico con bordes suaves y curvos. En el pecho lleva un sensor verde (#3FB950) rectangular horizontal que late. Una línea de tendencia ascendente verde cruza desde la cintura hacia el hombro derecho. Un glow azul suave rodea todo el contorno del robot. En la esquina inferior derecha, detrás del robot, un logo circular naranja con una letra C blanca (~90px). Fondo con paneles rectangulares y líneas de circuito simples. Diseño profesional, premium, oscuro. Sin ojos ni cara.
```

---

### FB#02 — FIRST TRANSACTION
**Bloque:** 170 | **Fecha:** 2009-01-12 | **Evento:** Primera transacción P2P — Satoshi → Hal Finney

**Prompt:**
```
Robot humanoide elegante en postura de transmisión, cuerpo azul (#58A6FF) sobre fondo oscuro (#0D1117). Brazos sutiles extendidos hacia adelante como enviando datos. Cabeza con visor frontal verde (#3FB950) tipo scan. Del pecho sale una barra de volumen verde que asciende. Torso con bordes curvos suaves, diseño aerodinámico. Glow azul perimetral suave. Flecha de transferencia naranja (#F78166) pequeña entre las manos. Fondo con líneas de grid y paneles tecnológicos. En esquina inferior derecha, detrás del robot, logo naranja con C blanca (~90px). Diseño profesional, minimalista. Sin ojos ni cara.
```

---

### FB#03 — EPIC HALVING
**Bloque:** 210,000 | **Fecha:** 2012-11-28 | **Evento:** Primer halving — 50→25 BTC

**Prompt:**
```
Robot humanoide elegante con torso partido en dos mitades verticales: mitad superior azul (#58A6FF), mitad inferior ligeramente más oscura. Cabeza estilizada con visor verde (#3FB950). En el centro del pecho, un diamante verde que se divide en dos. Brazos hacia abajo en posición de entrega. Glow azul suave alrededor. Una línea horizontal naranja (#F78166) cruza el torso marcando el punto de división. Fondo oscuro (#0D1117) con paneles rectangulares y líneas. En esquina inferior derecha, detrás del robot, logo naranja con C blanca (~90px). Diseño profesional, limpio, bordes curvos. Sin ojos ni cara.
```

---

### FB#04 — BLOCK 9
**Bloque:** 9 | **Fecha:** 2009-01-09 | **Evento:** Bloque temprano — época de Satoshi

**Prompt:**
```
Robot humanoide elegante y delgado, diseño simple y puro, cuerpo azul (#58A6FF). Cabeza pequeña y alargada con un punto sensor verde (#3FB950) en la frente. Torso alargado y estilizado con líneas horizontales finas. Brazos pegados al cuerpo. Un panel de 3 barras verticales pequeñas verdes en el pecho. Glow azul suave. Fondo oscuro (#0D1117) con líneas de grid simples. En esquina inferior derecha, detrás del robot, logo naranja con C blanca (~90px). Diseño minimalista, profesional, elegante. Sin ojos ni cara.
```

---

### FB#05 — BLOCK 78
**Bloque:** 78 | **Fecha:** 2009-01-16 | **Evento:** UTXOs origen de la primera transacción P2P

**Prompt:**
```
Robot humanoide elegante con torso ancho y sólido, cuerpo azul (#58A6FF). Cabeza rectangular con bordes redondeados. Dos barras sensoras verdes (#3FB950) verticales paralelas en el pecho, como marcando origen. Brazos robustos pero elegantes, curvos. Glow azul perimetral. Una pequeña flecha naranja (#F78166) apuntando hacia arriba desde el hombro. Fondo oscuro (#0D1117) con paneles tecnológicos rectangulares. En esquina inferior derecha, detrás del robot, logo naranja con C blanca (~90px). Diseño profesional, premium. Sin ojos ni cara.
```

---

### FB#06 — PIZZA DAY
**Bloque:** 57,043 | **Fecha:** 2010-05-22 | **Evento:** Laszlo — 10,000 BTC por 2 pizzas

**Prompt:**
```
Robot humanoide elegante con torso con forma de V invertida, cuerpo azul (#58A6FF). Cabeza inclinada ligeramente hacia la derecha. En el pecho, dos rectángulos verdes (#3FB950) horizontales apilados. Brazos abiertos en gesto de intercambio. Línea de tendencia naranja (#F78166) ascendente desde la cadera. Un cuadrado naranja pequeño en el centro del pecho. Glow azul suave. Fondo oscuro (#0D1117) con cuadrícula y marcos. En esquina inferior derecha, detrás del robot, logo naranja con C blanca (~90px). Diseño profesional, bordes curvos. Sin ojos ni cara.
```

---

### FB#07 — RARE (DIFFICULTY ADJUSTMENT)
**Bloque:** 2,016 | **Fecha:** 2010-01-09 | **Evento:** Primer ajuste de dificultad

**Prompt:**
```
Robot humanoide elegante con torso hexagonal con bordes redondeados, cuerpo azul (#58A6FF). Cabeza con un gran sensor verde (#3FB950) tipo diamante en la frente. El torso tiene un mecanismo de ajuste: tres líneas verdes horizontales que aumentan de grosor hacia abajo. Brazos rectos a los lados. Glow azul perimetral. Fondo oscuro (#0D1117) con líneas de circuito y paneles. En esquina inferior derecha, detrás del robot, logo naranja con C blanca (~90px). Diseño profesional, simétrico, elegante. Sin ojos ni cara.
```

---

### FB#08 — 5M OUT
**Bloque:** 394,736 | **Fecha:** 2016-02-17 | **Evento:** Primer bloque con 5M BTC en salidas

**Prompt:**
```
Robot humanoide elegante y masivo, torso ancho y poderoso, cuerpo azul (#58A6FF). Cabeza grande con visor verde (#3FB950) ancho. Una gran barra de volumen verde vertical en el centro del pecho. Hombros anchos y redondeados. Glow azul intenso. Dos pequeñas barras naranjas (#F78166) a los lados del torso. Fondo oscuro (#0D1117) con cuadrícula tecnológica. En esquina inferior derecha, detrás del robot, logo naranja con C blanca (~90px). Diseño profesional, imponente. Sin ojos ni cara.
```

---

### FB#09 — TX MULTIMILLONARIA
**Bloque:** 104,770 | **Fecha:** 2011-04-23 | **Evento:** Transacción de 400,000 BTC

**Prompt:**
```
Robot humanoide elegante y estilizado, cuerpo azul (#58A6FF) con torso alargado. Cabeza pequeña con un punto verde (#3FB950) brillante. Brazos cruzados sobre el pecho. Una vela japonesa verde gigante (cuerpo + mecha) asciende desde la base del robot hasta el hombro. Glow azul suave. Partículas naranjas (#F78166) pequeñas alrededor. Fondo oscuro (#0D1117) con paneles tecnológicos en degradado sutil de líneas. En esquina inferior derecha, detrás del robot, logo naranja con C blanca (~90px). Diseño profesional, elegante. Sin ojos ni cara.
```

---

### FB#10 — 21e8
**Bloque:** 1,345 | **Fecha:** 2009-08-02 | **Evento:** Hash con patrón 21e8 — oferta máxima de 21M

**Prompt:**
```
Robot humanoide elegante y futurista, cuerpo azul (#58A6FF). Cabeza con forma de diamante suave. En el pecho, el número "21" formado por dos barras verdes (#3FB950) y una barra naranja (#F78166). Brazos elegantemente doblados. Un círculo verde detrás de la cabeza como halo tecnológico. Glow azul perimetral. Fondo oscuro (#0D1117) con cuadrícula y paneles. En esquina inferior derecha, detrás del robot, logo naranja con C blanca (~90px). Diseño profesional, premium, simétrico. Sin ojos ni cara.
```

---

## 15 STANDARD ARCHETYPES — PROMPTS BASE

Cada arquetipo tiene 6 variantes (A-F). Use el prompt base y aplique la variación indicada.

---

### SA#01 — ANALYST
**Prompt base:**
```
Robot humanoide elegante sentado frente a un panel de datos, cuerpo azul (#58A6FF). Cabeza inclinada ligeramente. Brazos frente al pecho. Barras de datos verdes (#3FB950) y naranjas (#F78166) en el torso. Glow azul. Fondo oscuro con grid.
```
**Variantes:** A) 3 barras | B) 4 barras | C) barras + línea | D) panel doble | E) barra única grande | F) barras asimétricas

---

### SA#02 — SCALPER
**Prompt base:**
```
Robot humanoide elegante y rápido, cuerpo azul (#58A6FF) estilizado y aerodinámico. Cabeza pequeña con sensor verde (#3FB950). Torso con velas pequeñas verdes y naranjas integradas. Brazos en posición de ataque. Glow azul. Fondo oscuro con líneas de velocidad.
```
**Variantes:** A) 2 velas verdes | B) 3 velas mixtas | C) 1 vela grande | D) velas + flecha | E) velas apiladas | F) velas invertidas

---

### SA#03 — SWING
**Prompt base:**
```
Robot humanoide elegante de pie con postura balanceada, cuerpo azul (#58A6FF). Brazos extendidos. Dos líneas de canal verdes (#3FB950) paralelas horizontales en el torso. Sensor en la cabeza. Glow azul. Fondo oscuro con paneles.
```
**Variantes:** A) canal ascendente | B) canal descendente | C) canal horizontal | D) canal ancho | E) canal estrecho | F) canal + flecha

---

### SA#04 — VOLUME
**Prompt base:**
```
Robot humanoide elegante con torso masivo, cuerpo azul (#58A6FF). Una gran barra de volumen verde (#3FB950) vertical en el centro. Cabeza ancha con visor. Hombros poderosos. Glow azul intenso. Fondo oscuro con grid.
```
**Variantes:** A) 1 barra gigante | B) 2 barras | C) 3 barras escalonadas | D) barra + corona | E) barra invertida | F) barra partida

---

### SA#05 — TREND
**Prompt base:**
```
Robot humanoide elegante con línea de tendencia ascendente verde (#3FB950) que cruza el torso diagonalmente. Cuerpo azul (#58A6FF). Cabeza siguiendo la dirección de la línea. Brazos alineados. Glow azul. Fondo oscuro con líneas direccionales.
```
**Variantes:** A) 45° ascendente | B) 30° ascendente | C) 45° descendente | D) 30° descendente | E) quebrada | F) curva suave

---

### SA#06 — MOMENTUM
**Prompt base:**
```
Robot humanoide elegante en movimiento, cuerpo azul (#58A6FF) inclinado hacia adelante. Flecha verde (#3FB950) grande en el pecho apuntando hacia arriba-derecha. Cabeza con sensor. Brazos dinámicos. Glow azul. Fondo oscuro con líneas de velocidad.
```
**Variantes:** A) flecha recta | B) flecha curva | C) doble flecha | D) flecha + impulso | E) flecha punteada | F) flecha con estela

---

### SA#07 — LIQUIDITY
**Prompt base:**
```
Robot humanoide elegante con torso fluido, cuerpo azul (#58A6FF). Ondas verdes (#3FB950) horizontales en el pecho. Cabeza con visor. Brazos abiertos. Glow azul suave. Fondo oscuro con líneas de profundidad.
```
**Variantes:** A) 3 ondas | B) 2 ondas | C) 1 onda grande | D) ondas + nivel | E) ondas verticales | F) ondas partidas

---

### SA#08 — MARKET MAKER
**Prompt base:**
```
Robot humanoide elegante simétrico, cuerpo azul (#58A6FF). Dos barras verdes (#3FB950) y naranjas (#F78166) paralelas lado a lado en el pecho (bid/ask). Cabeza centrada con sensor. Brazos equilibrados. Glow azul. Fondo oscuro con grid.
```
**Variantes:** A) bid/ask iguales | B) bid alto | C) ask alto | D) spread ancho | E) spread estrecho | F) bid/ask + libro

---

### SA#09 — RISK MANAGER
**Prompt base:**
```
Robot humanoide elegante en postura defensiva, cuerpo azul (#58A6FF). Brazos cruzados frente al pecho formando una X. Línea de stop loss naranja (#F78166) horizontal en el torso. Sensor verde (#3FB950) en cabeza. Glow azul. Fondo oscuro con paneles protectores.
```
**Variantes:** A) stop fijo | B) stop trailing | C) doble stop | D) stop + escudo | E) stop ajustable | F) stop + alerta

---

### SA#10 — BREAKOUT
**Prompt base:**
```
Robot humanoide elegante en explosión, cuerpo azul (#58A6FF). Una línea de resistencia naranja (#F78166) rota en el pecho con una flecha verde (#3FB950) atravesándola. Cabeza hacia arriba. Brazos extendidos. Glow azul intenso. Fondo oscuro con líneas de ruptura.
```
**Variantes:** A) resistencia horizontal | B) resistencia diagonal | C) soporte + resistencia | D) breakout + volumen | E) doble ruptura | F) canal + breakout

---

### SA#11 — ORDER FLOW
**Prompt base:**
```
Robot humanoide elegante con flujo de datos, cuerpo azul (#58A6FF). Líneas verdes (#3FB950) y naranjas (#F78166) verticales alternadas en el torso (flujo de órdenes). Cabeza con visor de datos. Brazos receptivos. Glow azul. Fondo oscuro con cuadrícula.
```
**Variantes:** A) 3 líneas | B) 5 líneas | C) líneas + volumen | D) líneas asimétricas | E) líneas acumuladas | F) líneas + delta

---

### SA#12 — ARBITRAGE
**Prompt base:**
```
Robot humanoide elegante con cuerpo partido, mitad azul (#58A6FF) y mitad más oscura. Cabeza con dos sensores verdes (#3FB950) apuntando en direcciones opuestas. Brazos separados. Flechas verdes divergentes. Glow azul. Fondo oscuro con paneles duales.
```
**Variantes:** A) izquierda-derecha | B) arriba-abajo | C) diagonal opuesta | D) triple arbitraje | E) circular | F) espejo

---

### SA#13 — HEDGE
**Prompt base:**
```
Robot humanoide elegante en postura de protección, cuerpo azul (#58A6FF). Brazos formando un escudo. Línea verde (#3FB950) y naranja (#F78166) inversas en el torso (correlación negativa). Sensor alerta en cabeza. Glow azul suave. Fondo oscuro con paneles de seguridad.
```
**Variantes:** A) correlación simple | B) doble cobertura | C) escudo + flecha | D) cruzada | E) cobertura parcial | F) cobertura total

---

### SA#14 — SENTINEL
**Prompt base:**
```
Robot humanoide elegante de guardia, cuerpo azul (#58A6FF) erguido. Cabeza con un gran sensor verde (#3FB950) de barrido horizontal. Brazos caídos pero alerta. Antenas pequeñas en hombros. Glow azul vigilante. Fondo oscuro con líneas de radar.
```
**Variantes:** A) sensor + alerta | B) doble sensor | C) sensor 360° | D) sensor + barra | E) sensor láser | F) sensor + escudo

---

### SA#15 — MINER
**Prompt base:**
```
Robot humanoide elegante robusto, cuerpo azul (#58A6FF) macizo tipo industrial. Cabeza cuadrada con bordes redondeados. En el torso, un bloque verde (#3FB950) con líneas de hash. Brazos poderosos. Glow azul. Fondo oscuro con paneles de minería y circuitos.
```
**Variantes:** A) 1 bloque | B) 2 bloques | C) bloque + recompensa | D) bloque + hash | E) bloque + nonce | F) bloque + dificultad

---

## PIPELINE DE COMPRESIÓN (para mí)

```bash
# Su PNG generado por Qwen → mi pipeline:
# 1. Redimensionar a 512×512 si es necesario
# 2. PNG → SVG (potrace)
# 3. Componer logo detrás (si es Founder)
# 4. SVG → PNG 512×512
# 5. Reducir a 256 colores
# 6. oxipng --opt max --strip all --zopfli
# 7. Verificar ≤5,120 bytes
```

---

## INSTRUCCIONES PARA USTED

1. Lea el prompt del agente que desea crear
2. Péguelo en Qwen (o la IA que use) y genere la imagen
3. Guarde el PNG resultante en `/home/candela/Escritorio/bitmapcore-founder/`
4. Asígnelo con el nombre: `fbXX_agent_raw.png` (Founders) o `saXX_arquetipo_variante_raw.png` (Standards)
5. Avíseme y yo proceso: PNG → SVG → compresión → verificación

---

## TABLA DE CONTENIDO

| # | Código | Nombre | Tipo |
|---|--------|--------|------|
| 01 | FB#01 | Genesis | Founder |
| 02 | FB#02 | First Transaction | Founder |
| 03 | FB#03 | Epic Halving | Founder |
| 04 | FB#04 | Block 9 | Founder |
| 05 | FB#05 | Block 78 | Founder |
| 06 | FB#06 | Pizza Day | Founder |
| 07 | FB#07 | Rare (Difficulty) | Founder |
| 08 | FB#08 | 5M Out | Founder |
| 09 | FB#09 | TX Multimillonaria | Founder |
| 10 | FB#10 | 21e8 | Founder |
| 11-16 | SA#01 | Analyst (A-F) | Standard |
| 17-22 | SA#02 | Scalper (A-F) | Standard |
| 23-28 | SA#03 | Swing (A-F) | Standard |
| 29-34 | SA#04 | Volume (A-F) | Standard |
| 35-40 | SA#05 | Trend (A-F) | Standard |
| 41-46 | SA#06 | Momentum (A-F) | Standard |
| 47-52 | SA#07 | Liquidity (A-F) | Standard |
| 53-58 | SA#08 | Market Maker (A-F) | Standard |
| 59-64 | SA#09 | Risk Manager (A-F) | Standard |
| 65-70 | SA#10 | Breakout (A-F) | Standard |
| 71-76 | SA#11 | Order Flow (A-F) | Standard |
| 77-82 | SA#12 | Arbitrage (A-F) | Standard |
| 83-88 | SA#13 | Hedge (A-F) | Standard |
| 89-94 | SA#14 | Sentinel (A-F) | Standard |
| 95-100 | SA#15 | Miner (A-F) | Standard |

---

## DATOS HISTÓRICOS VERIFICADOS

| # | Bloque | Fecha | Hash | Evento |
|---|--------|-------|------|--------|
| 01 | 0 | 2009-01-03 | `000000000019d6689c085ae165831e934ff763ae46a2a6c172b3f1b60a8ce26f` | Bloque génesis |
| 02 | 170 | 2009-01-12 | `00000000d1145790a8694403d4063f323d499e655c83426834d4ce2f8dd4a2ee` | Primera tx P2P |
| 03 | 210,000 | 2012-11-28 | `000000000000048b9534f6a9e1228adf4d1c1ee9e7c6064a6d73029d63caf04d` | Primer halving (50→25) |
| 04 | 9 | 2009-01-09 | `000000008d9dc510f23c3af54bae77d6ec5a7ac9b6b8a9d6e983063da80878bc` | Bloque temprano |
| 05 | 78 | 2009-01-16 | `00000000a2886c95400fd3baa39b77c24a18ab31b774ebd5ded0a91d79f78c0c` | UTXOs origen |
| 06 | 57,043 | 2010-05-22 | `00000000152340ca42225e9d3f294c8b64ec9dd8f7395253dcf47ada203832c5` | Pizza Day (10K BTC) |
| 07 | 2,016 | 2010-01-09 | `00000000a141216a896c655baac58962f068c7f29d5cba981a075729f60a7cec` | Primer ajuste dificultad |
| 08 | 394,736 | 2016-02-17 | `00000000000000000666a987c3d7fcab25069a4bd5a58935bd327a0273d56184` | 5M BTC en salidas |
| 09 | 104,770 | 2011-04-23 | `0000000000022043f9ea1ff7e5a074ffc1b1047d152c4d7b88d5bd7245e31233` | Tx 400,000 BTC |
| 10 | 1,345 | 2009-08-02 | `00000000fa61e838f0d4ad6413a4913d7de1eec747a0613c5f159a93f1509b43` | Hash 21e8 |

---

**FIN DEL MASTER PROMPT v6.0**
