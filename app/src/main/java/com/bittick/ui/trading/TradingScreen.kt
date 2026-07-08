package com.bittick.ui.trading

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bittick.network.BotPosition
import com.bittick.network.BotStatusItem
import com.bittick.ui.chart.CandleChartView
import com.bittick.ui.theme.BittickColor
import com.bittick.ui.theme.OnPrimary
import com.bittick.ui.theme.OnSecondary
import com.bittick.ui.theme.Primary
import com.bittick.ui.theme.Secondary
import com.bittick.ui.theme.Surface
import kotlinx.coroutines.launch

private val INTERVALS = listOf("1m", "5m", "15m", "30m", "1h", "4h", "1d", "1w", "1M")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TradingScreen(
    onSettingsClick: () -> Unit = {},
    viewModel: TradingViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(drawerContainerColor = Surface) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "bittick",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Secondary,
                    modifier = Modifier.padding(horizontal = 28.dp, vertical = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                NavigationDrawerItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = Secondary) },
                    label = { Text("Trading", color = OnPrimary) },
                    selected = true,
                    onClick = { scope.launch { drawerState.close() } },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = Primary,
                        unselectedContainerColor = Color.Transparent
                    )
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = null, tint = Secondary) },
                    label = { Text("Ajustes", color = OnPrimary) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onSettingsClick()
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = Primary,
                        unselectedContainerColor = Color.Transparent
                    )
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("bittick", fontWeight = FontWeight.Bold, color = BittickColor) },
                    actions = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Abrir menú", tint = BittickColor)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Primary)
                )
            },
            containerColor = Primary
        ) { padding ->
            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                if (state.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = BittickColor)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Surface),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        INTERVALS.forEach { interval ->
                                            FilterChip(
                                                selected = state.chartInterval == interval,
                                                onClick = { viewModel.loadKlines(interval) },
                                                label = { Text(interval, style = MaterialTheme.typography.labelSmall) },
                                                colors = FilterChipDefaults.filterChipColors(
                                                    selectedContainerColor = BittickColor,
                                                    selectedLabelColor = OnSecondary
                                                )
                                            )
                                        }
                                    }
                                    Box {
                                        CandleChartView(klines = state.klines, zones = state.zones)
                                        if (state.chartLoading) {
                                            Box(
                                                modifier = Modifier.matchParentSize(),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                CircularProgressIndicator(color = BittickColor)
                                            }
                                        }
                                    }
                                    Text(
                                        state.chartStatus,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (state.chartStatus.contains("error")) Color(0xFFFF5252)
                                            else if (state.chartStatus.contains("OK")) Color(0xFF4CAF50)
                                            else Secondary.copy(alpha = 0.6f)
                                    )
                                    state.currentPrice?.let { price ->
                                        Text(
                                            "BTC/USDT $${"%.2f".format(price)}",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = BittickColor,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        item { BotSection("SPOT", state.spotBotStatus, state.spotPositions, viewModel) }
                        item { BotSection("FUTUROS", state.futuresBotStatus, state.futuresPositions, viewModel) }

                        state.error?.let { err ->
                            item {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(err, color = Color(0xFFB71C1C), modifier = Modifier.padding(12.dp))
                                }
                            }
                        }

                        item {
                            Text(
                                "Oportunidades detectadas",
                                fontWeight = FontWeight.Bold,
                                color = Secondary,
                                style = MaterialTheme.typography.titleSmall
                            )
                        }

                        if (state.opportunities.isEmpty()) {
                            item {
                                Text(
                                    "No hay oportunidades aun.",
                                    color = Secondary.copy(alpha = 0.6f),
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        } else {
                            items(state.opportunities, key = { it.id }) { op ->
                                OpportunityCard(op, onDelete = { viewModel.deleteOpportunity(it) })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BotSection(
    label: String,
    status: BotStatusItem?,
    positions: List<BotPosition>,
    viewModel: TradingViewModel
) {
    val enabled = status?.enabled == true
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = if (enabled) BittickColor else Secondary, modifier = Modifier.height(20.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("BOT $label BTC", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.weight(1f))
                Text(if (enabled) "ACTIVO" else "INACTIVO",
                    color = if (enabled) Color(0xFF1B5E20) else Color(0xFFB71C1C),
                    fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
            }

            if (status != null) {
                Spacer(modifier = Modifier.height(4.dp))
                val balance = status.balance
                if (balance != null) {
                    Text("Balance: $${"%.2f".format(balance.total)} (disponible: $${"%.2f".format(balance.available)})",
                        style = MaterialTheme.typography.bodySmall, color = Secondary)
                }
                Text("Posiciones abiertas: ${status.openPositions}/${status.maxPositions}  PNL Total: $${"%.2f".format(status.totalPnl)}",
                    style = MaterialTheme.typography.bodySmall, color = Secondary.copy(alpha = 0.7f))
            }

            if (positions.isEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text("Sin posiciones abiertas", style = MaterialTheme.typography.bodySmall, color = Secondary.copy(alpha = 0.4f))
            } else {
                positions.forEach { pos -> PositionCard(pos, viewModel) }
            }
        }
    }
}

@Composable
private fun PositionCard(pos: BotPosition, viewModel: TradingViewModel) {
    val isLong = pos.strategy_type == "long"
    val signalColor = if (isLong) Color(0xFF1B5E20) else Color(0xFFB71C1C)
    val signalBg = if (isLong) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
    val icon = if (isLong) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown
    val typeLabel = if (isLong) "LONG" else "SHORT"
    val pnlColor = if (pos.pnl >= 0) Color(0xFF1B5E20) else Color(0xFFB71C1C)

    Card(
        modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Primary),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Card(colors = CardDefaults.cardColors(containerColor = signalBg), shape = RoundedCornerShape(6.dp)) {
                    Row(modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(icon, contentDescription = null, tint = signalColor, modifier = Modifier.height(14.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(typeLabel, color = signalColor, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                    }
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(pos.asset, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.weight(1f))
                Text("$${"%.2f".format(pos.pnl)} (${"%.2f".format(pos.pnl_percent)}%)",
                    color = pnlColor, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
            }

            Spacer(modifier = Modifier.height(4.dp))
            Row {
                Text("Entrada: $${"%.2f".format(pos.entry_price)}", style = MaterialTheme.typography.bodySmall, color = Secondary)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Actual: $${"%.2f".format(pos.current_price ?: pos.entry_price)}", style = MaterialTheme.typography.bodySmall, color = Secondary)
            }

            if (pos.target != null || pos.stop_loss != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Row {
                    if (pos.target != null) {
                        Text("Objetivo: $${"%.2f".format(pos.target)}", style = MaterialTheme.typography.bodySmall, color = Secondary.copy(alpha = 0.6f))
                    }
                    if (pos.stop_loss != null) {
                        Text("  Stop: $${"%.2f".format(pos.stop_loss)}", style = MaterialTheme.typography.bodySmall, color = Color(0xFFB71C1C).copy(alpha = 0.6f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Button(
                onClick = { viewModel.cancelPosition(pos.id) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C), contentColor = Color.White),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Cancel, contentDescription = null, modifier = Modifier.height(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("CANCELAR OPERACION", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
private fun OpportunityCard(op: TradingOpportunityItem, onDelete: (Int) -> Unit) {
    val isLong = op.type == "long"
    val signalColor = if (isLong) Color(0xFF1B5E20) else Color(0xFFB71C1C)
    val signalBg = if (isLong) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
    val icon = if (isLong) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown
    val typeLabel = if (isLong) "LONG" else "SHORT"

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Card(colors = CardDefaults.cardColors(containerColor = signalBg), shape = RoundedCornerShape(8.dp)) {
                    Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(icon, contentDescription = null, tint = signalColor, modifier = Modifier.height(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(typeLabel, color = signalColor, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(op.asset, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.weight(1f))
                Text("$${op.price}", fontWeight = FontWeight.Bold, color = Secondary)
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(onClick = { onDelete(op.id) }, modifier = Modifier.height(24.dp).width(24.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar oportunidad", tint = Color(0xFFE53935), modifier = Modifier.height(18.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                InfoChip("Puntaje", "${op.score}/10")
                Spacer(modifier = Modifier.width(8.dp))
                InfoChip("Confianza", "${op.confidence}/10")
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                InfoChip("Entrada", op.entryZone)
                Spacer(modifier = Modifier.width(8.dp))
                InfoChip("Objetivo", "$${op.target}")
            }
            Spacer(modifier = Modifier.height(4.dp))
            InfoChip("Stop Loss", "$${op.stopLoss}")

            if (op.explanation.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(op.explanation, style = MaterialTheme.typography.bodySmall, color = Secondary.copy(alpha = 0.8f))
            }
            if (op.factors.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text("Factores:", fontWeight = FontWeight.Medium, style = MaterialTheme.typography.labelSmall, color = Secondary)
                op.factors.forEach { f -> Text("• $f", style = MaterialTheme.typography.bodySmall, color = Secondary.copy(alpha = 0.7f)) }
            }
            if (op.createdAt.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(formatDateTimeLocal(op.createdAt), style = MaterialTheme.typography.labelSmall, color = Secondary.copy(alpha = 0.4f))
                    Spacer(modifier = Modifier.weight(1f))
                    Text(formatDayOfWeekSpanish(op.createdAt), style = MaterialTheme.typography.labelSmall, color = Secondary.copy(alpha = 0.4f))
                }
            }
        }
    }
}

private fun formatDateTimeLocal(isoDate: String): String {
    return try {
        val instant = java.time.Instant.parse(isoDate)
        val local = instant.atZone(java.time.ZoneId.systemDefault())
        local.format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm:ss"))
    } catch (_: Exception) { isoDate }
}

private fun formatDayOfWeekSpanish(isoDate: String): String {
    return try {
        val instant = java.time.Instant.parse(isoDate)
        val zdt = instant.atZone(java.time.ZoneId.systemDefault())
        val today = java.time.LocalDate.now(java.time.ZoneId.systemDefault())
        if (zdt.toLocalDate() == today) return "hoy"
        val days = mapOf(
            java.time.DayOfWeek.MONDAY to "lunes",
            java.time.DayOfWeek.TUESDAY to "martes",
            java.time.DayOfWeek.WEDNESDAY to "miercoles",
            java.time.DayOfWeek.THURSDAY to "jueves",
            java.time.DayOfWeek.FRIDAY to "viernes",
            java.time.DayOfWeek.SATURDAY to "sabado",
            java.time.DayOfWeek.SUNDAY to "domingo"
        )
        days[zdt.dayOfWeek] ?: ""
    } catch (_: Exception) { "" }
}

@Composable
private fun InfoChip(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, color = Secondary.copy(alpha = 0.5f))
        Text(value, style = MaterialTheme.typography.bodySmall, color = Secondary, fontWeight = FontWeight.Medium)
    }
}
