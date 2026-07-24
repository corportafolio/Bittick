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
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
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
import android.content.Intent
import android.net.Uri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onNavigateToWallet: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(true) {
        viewModel.refreshWalletState()
    }

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    LaunchedEffect(state.apiKeyMessage) {
        state.apiKeyMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearApiKeyMessage()
        }
    }

    LaunchedEffect(state.levelConfigsMessage) {
        state.levelConfigsMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearLevelConfigsMessage()
        }
    }

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
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                    },
                    onTestNotification = { viewModel.testNotification() }
                )
            }

            item { Spacer(modifier = Modifier.height(12.dp)) }

            // 2.5 API Keys — burbuja unificada
            item {
                AllApiKeysSection(
                    spotHasKey = state.spotApiKeyHasKey,
                    spotMasked = state.spotApiKeyMasked,
                    futuresHasKey = state.futuresApiKeyHasKey,
                    futuresMasked = state.futuresApiKeyMasked,
                    editing = state.allApiKeysEditing,
                    saving = state.allApiKeysSaving,
                    spotKeyInput = state.allSpotKey,
                    spotSecretInput = state.allSpotSecret,
                    futuresKeyInput = state.allFuturesKey,
                    futuresSecretInput = state.allFuturesSecret,
                    onToggleEditing = viewModel::toggleAllApiKeysEditing,
                    onSpotKeyChanged = viewModel::updateAllSpotKey,
                    onSpotSecretChanged = viewModel::updateAllSpotSecret,
                    onFuturesKeyChanged = viewModel::updateAllFuturesKey,
                    onFuturesSecretChanged = viewModel::updateAllFuturesSecret,
                    onSave = viewModel::saveAllApiKeys,
                    onDeleteSpotKey = { viewModel.deleteApiKey("spot") },
                    onDeleteFuturesKey = { viewModel.deleteApiKey("futures") }
                )
            }
            item { Spacer(modifier = Modifier.height(12.dp)) }

            // 3. Configuración de Bots
            item {
                BotCard(
                    label = "SPOT",
                    botNumber = state.botNumber ?: 0,
                    enabled = state.spotEnabled,
                    levels = state.spotLevels,
                    expanded = state.spotExpanded,
                    saving = state.levelConfigsSaving,
                    onToggleEnabled = viewModel::updateSpotEnabled,
                    onToggleExpanded = viewModel::toggleSpotExpanded,
                    onUpdateLevel = viewModel::updateSpotLevel,
                    onSave = { viewModel.saveLevelConfigs("spot") }
                )
            }
            item { Spacer(modifier = Modifier.height(12.dp)) }
            item {
                BotCard(
                    label = "FUTUROS",
                    botNumber = state.botNumber ?: 0,
                    enabled = state.futuresEnabled,
                    levels = state.futuresLevels,
                    expanded = state.futuresExpanded,
                    saving = state.levelConfigsSaving,
                    onToggleEnabled = viewModel::updateFuturesEnabled,
                    onToggleExpanded = viewModel::toggleFuturesExpanded,
                    onUpdateLevel = viewModel::updateFuturesLevel,
                    onSave = { viewModel.saveLevelConfigs("futures") }
                )
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
                Column {
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
    onRequestPermission: () -> Unit,
    onTestNotification: () -> Unit
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
                        Spacer(Modifier.width(8.dp))
                    }
                    OutlinedButton(
                        onClick = onTestNotification,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Probar")
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
                        color = Secondary,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedButton(
                        onClick = onTestNotification,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Probar")
                    }
                }
            }
        }
    }
}

@Composable
private fun AllApiKeysSection(
    spotHasKey: Boolean,
    spotMasked: String?,
    futuresHasKey: Boolean,
    futuresMasked: String?,
    editing: Boolean,
    saving: Boolean,
    spotKeyInput: String,
    spotSecretInput: String,
    futuresKeyInput: String,
    futuresSecretInput: String,
    onToggleEditing: () -> Unit,
    onSpotKeyChanged: (String) -> Unit,
    onSpotSecretChanged: (String) -> Unit,
    onFuturesKeyChanged: (String) -> Unit,
    onFuturesSecretChanged: (String) -> Unit,
    onSave: () -> Unit,
    onDeleteSpotKey: () -> Unit,
    onDeleteFuturesKey: () -> Unit
) {
    var showDeleteSpotDialog by remember { mutableStateOf(false) }
    var showDeleteFuturesDialog by remember { mutableStateOf(false) }
    val context = androidx.compose.ui.platform.LocalContext.current

    Card(
        colors = CardDefaults.cardColors(containerColor = Surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = BittickColor, modifier = Modifier.height(20.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("API Keys Binance", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, color = BittickColor)
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = onToggleEditing, modifier = Modifier.size(28.dp)) {
                    Icon(
                        if (editing) Icons.Default.Clear else Icons.Default.Edit,
                        contentDescription = if (editing) "Cancelar" else "Editar API Keys",
                        tint = Secondary,
                        modifier = Modifier.size(18.dp)
                    )
}
                }

            Spacer(modifier = Modifier.height(if (!editing) 2.dp else 8.dp))

            if (!spotHasKey && !futuresHasKey) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2A1A00)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFFFA000), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Sin API Keys — Los bots automáticos están desactivados",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFFFA000)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(if (!editing) 2.dp else 8.dp))
            }

            // SPOT Section
            ApiKeySection(
                label = "SPOT",
                labelColor = Color(0xFFFF9800),
                hasKey = spotHasKey,
                maskedKey = spotMasked,
                editing = editing,
                keyInput = spotKeyInput,
                secretInput = spotSecretInput,
                onKeyChanged = onSpotKeyChanged,
                onSecretChanged = onSpotSecretChanged,
                onDelete = { showDeleteSpotDialog = true }
            )

            Spacer(modifier = Modifier.height(6.dp))

            // FUTUROS Section
            ApiKeySection(
                label = "FUTUROS",
                labelColor = Color(0xFF4CAF50),
                hasKey = futuresHasKey,
                maskedKey = futuresMasked,
                editing = editing,
                keyInput = futuresKeyInput,
                secretInput = futuresSecretInput,
                onKeyChanged = onFuturesKeyChanged,
                onSecretChanged = onFuturesSecretChanged,
onDelete = { showDeleteFuturesDialog = true }
            )

            Spacer(modifier = Modifier.height(if (!editing) 2.dp else 6.dp))

            if (editing) {
                val canSave = (spotKeyInput.isNotBlank() && spotSecretInput.isNotBlank()) ||
                        (futuresKeyInput.isNotBlank() && futuresSecretInput.isNotBlank())
                Button(
                    onClick = onSave,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (canSave) BittickColor else Color.Gray,
                        contentColor = OnSecondary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = canSave && !saving
                ) {
                    Text(if (saving) "Guardando..." else "Guardar API Keys", fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                "Solicitá tu API Key demo gratis en Binance:",
                style = MaterialTheme.typography.labelSmall,
                color = Secondary.copy(alpha = 0.6f)
            )
            Text(
                "https://www.binance.com/en/my/settings/api-management",
                style = MaterialTheme.typography.labelSmall,
                color = BittickColor,
                modifier = Modifier.clickable {
                    try {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.binance.com/en/my/settings/api-management")))
                    } catch (_: Exception) {}
                }
            )
        }
    }

    if (showDeleteSpotDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteSpotDialog = false },
            title = { Text("Eliminar API Key SPOT") },
            text = { Text("¿Seguro que querés eliminar la API Key de SPOT?") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteSpotDialog = false
                    onDeleteSpotKey()
                }) { Text("Eliminar", color = Color(0xFFE53935)) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteSpotDialog = false }) { Text("Cancelar") }
            }
        )
    }

    if (showDeleteFuturesDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteFuturesDialog = false },
            title = { Text("Eliminar API Key FUTUROS") },
            text = { Text("¿Seguro que querés eliminar la API Key de FUTUROS?") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteFuturesDialog = false
                    onDeleteFuturesKey()
                }) { Text("Eliminar", color = Color(0xFFE53935)) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteFuturesDialog = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
    private fun ApiKeySection(
        label: String,
        labelColor: Color,
        hasKey: Boolean,
        maskedKey: String?,
        editing: Boolean,
        keyInput: String,
        secretInput: String,
        onKeyChanged: (String) -> Unit,
        onSecretChanged: (String) -> Unit,
        onDelete: () -> Unit
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            if (hasKey && !editing) {
                // Visual mode: show green check + delete icon for Key
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(label, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.labelSmall, color = labelColor)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "✓ API Key guardada",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF4CAF50),
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar API Key $label", tint = Color(0xFFE53935), modifier = Modifier.size(16.dp))
                    }
                }
                // Visual mode: show green check + delete icon for Secret
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(label, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.labelSmall, color = labelColor)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "✓ API Secret guardado",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF4CAF50),
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar API Secret $label", tint = Color(0xFFE53935), modifier = Modifier.size(16.dp))
                    }
                }
            } else if (!hasKey && !editing) {
                // Visual mode: no key
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(label, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.labelSmall, color = labelColor)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sin configurar", style = MaterialTheme.typography.bodySmall, color = Secondary.copy(alpha = 0.4f), modifier = Modifier.weight(1f))
                }
            }

            if (editing) {
            Spacer(modifier = Modifier.height(8.dp))
            // Edit mode: show saved key with mask + check + delete, then input fields
            if (hasKey) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        maskedKey ?: "••••••••",
                        style = MaterialTheme.typography.bodySmall.copy(color = Secondary),
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color(0xFFE53935), modifier = Modifier.size(16.dp))
                    }
                }
            }
            OutlinedTextField(
                value = keyInput,
                onValueChange = onKeyChanged,
                placeholder = { Text("API Key $label", color = Secondary.copy(alpha = 0.5f), style = MaterialTheme.typography.bodySmall) },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                textStyle = MaterialTheme.typography.bodySmall.copy(color = Secondary),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(4.dp))
            if (hasKey) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "•••••••••••••••••••",
                        style = MaterialTheme.typography.bodySmall.copy(color = Secondary),
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color(0xFFE53935), modifier = Modifier.size(16.dp))
                    }
                }
            }
            OutlinedTextField(
                value = secretInput,
                onValueChange = onSecretChanged,
                placeholder = { Text("API Secret $label", color = Secondary.copy(alpha = 0.5f), style = MaterialTheme.typography.bodySmall) },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                textStyle = MaterialTheme.typography.bodySmall.copy(color = Secondary),
                singleLine = true
            )
        }
    }
}

@Composable
private fun BotCard(
    label: String,
    botNumber: Int,
    enabled: Boolean,
    levels: List<com.bittick.network.LevelConfig>,
    expanded: Boolean,
    saving: Boolean,
    onToggleEnabled: (Boolean) -> Unit,
    onToggleExpanded: () -> Unit,
    onUpdateLevel: (Int, String, Any) -> Unit,
    onSave: () -> Unit
) {
    val hasInvalidLevel = levels.any { it.minScore < 6 || it.minConfidence < 6 }

    Card(
        colors = CardDefaults.cardColors(containerColor = Surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = if (enabled) BittickColor else Secondary, modifier = Modifier.height(20.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("BOT $botNumber $label BTC", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, color = BittickColor)
                Spacer(modifier = Modifier.weight(1f))
                Switch(
                    checked = enabled,
                    onCheckedChange = onToggleEnabled,
                    colors = SwitchDefaults.colors(checkedTrackColor = BittickColor)
                )
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(onClick = onToggleExpanded, modifier = Modifier.height(24.dp).width(24.dp)) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "Colapsar" else "Expandir",
                        tint = Secondary
                    )
                }
            }

            AnimatedVisibility(visible = expanded, enter = expandVertically(), exit = shrinkVertically()) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text("ID", style = MaterialTheme.typography.labelSmall, color = Secondary.copy(alpha = 0.5f), modifier = Modifier.weight(0.5f))
                        Text("Score", style = MaterialTheme.typography.labelSmall, color = Secondary.copy(alpha = 0.5f), modifier = Modifier.weight(1f))
                        Text("Conf", style = MaterialTheme.typography.labelSmall, color = Secondary.copy(alpha = 0.5f), modifier = Modifier.weight(1f))
                        Text("Monto USD", style = MaterialTheme.typography.labelSmall, color = Secondary.copy(alpha = 0.5f), modifier = Modifier.weight(1.5f))
                        Spacer(modifier = Modifier.weight(0.5f))
                    }

                    levels.forEachIndexed { index, levelConfig ->
                        val levelNum = 10 - index
                        val levelError = levelConfig.minScore < 6 || levelConfig.minConfidence < 6
                        val id = "%02d".format(index + 1)

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(id, style = MaterialTheme.typography.bodySmall, color = Secondary, modifier = Modifier.weight(0.5f))

                            var scoreText by remember(levelConfig.minScore) { mutableStateOf(levelConfig.minScore.toString()) }
                            var confText by remember(levelConfig.minConfidence) { mutableStateOf(levelConfig.minConfidence.toString()) }
                            var amountText by remember(levelConfig.positionSizeUsdt) { mutableStateOf(levelConfig.positionSizeUsdt.toInt().toString()) }

                            OutlinedTextField(
                                value = scoreText,
                                onValueChange = { newValue ->
                                    scoreText = newValue
                                    newValue.toIntOrNull()?.let { onUpdateLevel(levelNum, "min_score", it) }
                                },
                                modifier = Modifier.weight(1f).height(48.dp),
                                textStyle = MaterialTheme.typography.bodySmall.copy(color = Secondary),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = confText,
                                onValueChange = { newValue ->
                                    confText = newValue
                                    newValue.toIntOrNull()?.let { onUpdateLevel(levelNum, "min_confidence", it) }
                                },
                                modifier = Modifier.weight(1f).height(48.dp),
                                textStyle = MaterialTheme.typography.bodySmall.copy(color = Secondary),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = amountText,
                                onValueChange = { newValue ->
                                    amountText = newValue
                                    newValue.toDoubleOrNull()?.let { onUpdateLevel(levelNum, "amount", it) }
                                },
                                modifier = Modifier.weight(1.5f).height(48.dp),
                                textStyle = MaterialTheme.typography.bodySmall.copy(color = Secondary),
                                singleLine = true,
                                prefix = { Text("$", style = MaterialTheme.typography.bodySmall, color = Secondary.copy(alpha = 0.5f)) }
                            )

                            Checkbox(
                                checked = levelConfig.isEnabled,
                                onCheckedChange = { onUpdateLevel(levelNum, "enabled", it) },
                                modifier = Modifier.weight(0.5f)
                            )
                        }

                        if (levelError) {
                            Text(
                                "Nivel $levelNum: Score y Conf deben ser >= 6",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFE53935),
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = onSave,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (hasInvalidLevel) Color.Gray else BittickColor,
                            contentColor = OnSecondary
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !hasInvalidLevel && !saving
                    ) {
                        Text(if (saving) "Guardando..." else "Guardar", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
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
