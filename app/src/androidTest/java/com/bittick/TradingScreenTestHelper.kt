package com.bittick

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bittick.network.BotPosition
import com.bittick.network.BotStatusItem
import com.bittick.ui.trading.TradingUiState
import com.bittick.ui.theme.BittickColor
import com.bittick.ui.theme.OnPrimary
import com.bittick.ui.theme.Primary
import com.bittick.ui.theme.Secondary
import com.bittick.ui.theme.Surface

@Composable
fun BoxTradingScreenWrapper(state: TradingUiState) {
    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        val error = state.error
        if (error != null) {
            Text(error, color = Color.Red)
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                if (!state.isFreeTier) {
                    if (state.spotBotStatus != null) {
                        BotSectionWrapper(
                            label = "SPOT",
                            status = state.spotBotStatus,
                            positions = state.spotPositions
                        )
                    }
                    if (state.futuresBotStatus != null) {
                        BotSectionWrapper(
                            label = "FUTUROS",
                            status = state.futuresBotStatus,
                            positions = state.futuresPositions
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BotSectionWrapper(
    label: String,
    status: BotStatusItem?,
    positions: List<BotPosition>
) {
    val enabled = status?.enabled == true
    val expanded = remember { mutableStateOf(true) }
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded.value = !expanded.value },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "BOT $label BTC",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    if (enabled) "ACTIVO" else "INACTIVO",
                    color = if (enabled) Color(0xFF1B5E20) else Color(0xFFB71C1C),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelSmall
                )
                Icon(
                    imageVector = if (expanded.value) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded.value) "Colapsar" else "Expandir",
                    tint = Secondary
                )
            }
            AnimatedVisibility(
                visible = expanded.value,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                if (status != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    val balance = status.balance
                    if (balance != null) {
                        Text(
                            "Balance: \$${"%.2f".format(balance.total)} (disponible: \$${"%.2f".format(balance.available)})",
                            style = MaterialTheme.typography.bodySmall,
                            color = Secondary
                        )
                    }
                    Text(
                        "Posiciones abiertas: ${status.openPositions}/${status.maxPositions}  PNL Total: \$${"%.2f".format(status.totalPnl)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Secondary.copy(alpha = 0.7f)
                    )
                }
                if (positions.isEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Sin posiciones abiertas",
                        style = MaterialTheme.typography.bodySmall,
                        color = Secondary.copy(alpha = 0.4f)
                    )
                } else {
                    positions.forEach { pos ->
                        PositionCardWrapper(pos)
                    }
                }
            }
        }
    }
}

@Composable
private fun PositionCardWrapper(pos: BotPosition) {
    val typeLabel = when (pos.bot_type) {
        "spot" -> "SPOT"
        "futures" -> if (pos.strategy_type == "long") "LONG" else "SHORT"
        else -> pos.strategy_type.uppercase()
    }
    val isLong = pos.strategy_type == "long"
    val signalColor = when (pos.bot_type) {
        "spot" -> Color(0xFFF57C00)
        "futures" -> if (isLong) Color(0xFF1B5E20) else Color(0xFFB71C1C)
        else -> if (isLong) Color(0xFF1B5E20) else Color(0xFFB71C1C)
    }
    val pnlColor = if (pos.pnl >= 0) Color(0xFF1B5E20) else Color(0xFFB71C1C)

    Card(
        modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Primary),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(typeLabel, color = signalColor, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                Spacer(modifier = Modifier.width(6.dp))
                Text(pos.asset, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    "\$${"%.2f".format(pos.pnl)} (${"%.2f".format(pos.pnl_percent)}%)",
                    color = pnlColor, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}
