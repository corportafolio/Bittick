package com.bittick.ui.settings

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bittick.network.InscriptionInfo
import com.bittick.ui.theme.BittickColor
import com.bittick.ui.theme.OnSecondary
import com.bittick.ui.theme.Primary
import com.bittick.ui.theme.Secondary
import com.bittick.ui.theme.Surface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onNavigateToWallet: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    val notifPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { viewModel.refreshPermissions() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ajustes", fontWeight = FontWeight.Bold, color = BittickColor) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = BittickColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Primary)
            )
        },
        containerColor = Primary
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // 1. Cuenta Bittick
            item {
                AccountSection(
                    walletAddress = state.walletAddress,
                    selectedInscription = state.selectedInscription,
                    botImageUrl = state.botImageUrl,
                    isPremium = state.isPremium,
                    tier = state.tier,
                    botNumber = state.botNumber,
                    onClick = onNavigateToWallet
                )
            }

            item { Spacer(modifier = Modifier.height(12.dp)) }

            // 2. Permisos
            item {
                PermissionsSection(
                    hasNotificationPermission = state.hasNotificationPermission,
                    onRequestPermission = {
                        notifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                )
            }

            item { Spacer(modifier = Modifier.height(12.dp)) }

            // 3. Prueba
            item {
                TestSection(
                    onTestNotification = { viewModel.testNotification() }
                )
            }

            item { Spacer(modifier = Modifier.height(12.dp)) }

            // 4. Configuración de Bots (only for premium users)
            if (state.isPremium) {
                item {
                    BotPreferencesSection(
                        spotEnabled = state.spotEnabled,
                        futuresEnabled = state.futuresEnabled,
                        spotPositionSize = state.spotPositionSize,
                        futuresPositionSize = state.futuresPositionSize,
                        spotMaxPositions = state.spotMaxPositions,
                        futuresMaxPositions = state.futuresMaxPositions,
                        spotMinScore = state.spotMinScore,
                        futuresMinScore = state.futuresMinScore,
                        onUpdateSpotEnabled = viewModel::updateSpotEnabled,
                        onUpdateFuturesEnabled = viewModel::updateFuturesEnabled,
                        onUpdateSpotPositionSize = viewModel::updateSpotPositionSize,
                        onUpdateFuturesPositionSize = viewModel::updateFuturesPositionSize,
                        onUpdateSpotMaxPositions = viewModel::updateSpotMaxPositions,
                        onUpdateFuturesMaxPositions = viewModel::updateFuturesMaxPositions,
                        onUpdateSpotMinScore = viewModel::updateSpotMinScore,
                        onUpdateFuturesMinScore = viewModel::updateFuturesMinScore
                    )
                }
            }
        }
    }
}

@Composable
private fun AccountSection(
    walletAddress: String?,
    selectedInscription: InscriptionInfo?,
    botImageUrl: String?,
    isPremium: Boolean,
    tier: String?,
    botNumber: Int?,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Surface),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (botImageUrl != null) {
                val bitmap = base64ToBitmap(botImageUrl)
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Bot Image",
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .border(2.dp, if (isPremium) Color(0xFFF7931A) else Color.Gray, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2A2A2A)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🤖",
                        fontSize = 32.sp
                    )
}
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Cuenta Bittick",
                        style = MaterialTheme.typography.titleMedium,
                        color = Secondary,
                        fontWeight = FontWeight.Bold
                    )
                    if (botNumber != null) {
                        Text(
                            text = "Bot #%02d".format(botNumber),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                if (walletAddress == null) {
                    Text(
                        text = "No conectada",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                } else {
                    Text(
                        text = if (selectedInscription != null) {
                            "Bot #${botNumber ?: selectedInscription.num} • ${tier ?: selectedInscription.tier}"
                        } else {
                            "${walletAddress.take(8)}...${walletAddress.takeLast(8)}"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isPremium) Color(0xFFF7931A) else Color.Gray
                    )
                }
            }

            Text(
                text = "→",
                fontSize = 20.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun PermissionsSection(
    hasNotificationPermission: Boolean,
    onRequestPermission: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text(
                "Permisos",
                style = MaterialTheme.typography.titleMedium,
                color = Secondary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (hasNotificationPermission) Icons.Default.CheckCircle else Icons.Default.Error,
                        contentDescription = null,
                        tint = if (hasNotificationPermission) Color(0xFF4CAF50) else Color.Red,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Notificaciones",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Secondary,
                        modifier = Modifier.weight(1f)
                    )
                    if (!hasNotificationPermission) {
                        OutlinedButton(
                            onClick = onRequestPermission,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Permitir")
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Notificaciones",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Secondary
                    )
                }
            }
        }
    }
}

@Composable
private fun TestSection(
    onTestNotification: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text(
                "Prueba",
                style = MaterialTheme.typography.titleMedium,
                color = Secondary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onTestNotification,
                colors = ButtonDefaults.buttonColors(
                    containerColor = BittickColor,
                    contentColor = OnSecondary
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    Icons.Default.Notifications,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("Probar notificacion", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun BotPreferencesSection(
    spotEnabled: Boolean,
    futuresEnabled: Boolean,
    spotPositionSize: Double,
    futuresPositionSize: Double,
    spotMaxPositions: Int,
    futuresMaxPositions: Int,
    spotMinScore: Int,
    futuresMinScore: Int,
    onUpdateSpotEnabled: (Boolean) -> Unit,
    onUpdateFuturesEnabled: (Boolean) -> Unit,
    onUpdateSpotPositionSize: (Double) -> Unit,
    onUpdateFuturesPositionSize: (Double) -> Unit,
    onUpdateSpotMaxPositions: (Int) -> Unit,
    onUpdateFuturesMaxPositions: (Int) -> Unit,
    onUpdateSpotMinScore: (Int) -> Unit,
    onUpdateFuturesMinScore: (Int) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text(
                "Configuración de Bots",
                style = MaterialTheme.typography.titleMedium,
                color = Secondary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Spot Bot
            Text(
                text = "Bot Spot",
                style = MaterialTheme.typography.titleSmall,
                color = BittickColor,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Habilitado",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Secondary,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = spotEnabled,
                    onCheckedChange = onUpdateSpotEnabled,
                    colors = SwitchDefaults.colors(checkedTrackColor = BittickColor)
                )
            }

            BotPreferenceRow(
                label = "Tamaño posición (USD)",
                value = "$${spotPositionSize.toInt()}",
                onValueChange = { /* Could add dialog for editing */ }
            )

            BotPreferenceRow(
                label = "Máx. posiciones",
                value = "$spotMaxPositions",
                onValueChange = { /* Could add dialog for editing */ }
            )

            BotPreferenceRow(
                label = "Score mínimo",
                value = "$spotMinScore",
                onValueChange = { /* Could add dialog for editing */ }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Futures Bot
            Text(
                text = "Bot Futures",
                style = MaterialTheme.typography.titleSmall,
                color = BittickColor,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Habilitado",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Secondary,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = futuresEnabled,
                    onCheckedChange = onUpdateFuturesEnabled,
                    colors = SwitchDefaults.colors(checkedTrackColor = BittickColor)
                )
            }

            BotPreferenceRow(
                label = "Tamaño posición (USD)",
                value = "$${futuresPositionSize.toInt()}",
                onValueChange = { /* Could add dialog for editing */ }
            )

            BotPreferenceRow(
                label = "Máx. posiciones",
                value = "$futuresMaxPositions",
                onValueChange = { /* Could add dialog for editing */ }
            )

            BotPreferenceRow(
                label = "Score mínimo",
                value = "$futuresMinScore",
                onValueChange = { /* Could add dialog for editing */ }
            )
        }
    }
}

@Composable
private fun BotPreferenceRow(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Secondary,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = BittickColor,
            fontWeight = FontWeight.Bold
        )
    }
}

private fun base64ToBitmap(base64: String): android.graphics.Bitmap? {
    return try {
        if (base64.isBlank()) return null
        val bytes = android.util.Base64.decode(base64, android.util.Base64.NO_WRAP)
        if (bytes.isEmpty()) return null
        android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    } catch (_: Exception) {
        null
    }
}
