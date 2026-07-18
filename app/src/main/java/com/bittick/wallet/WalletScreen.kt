package com.bittick.wallet

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.View
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bittick.network.InscriptionInfo
import com.bittick.wallet.WalletState

@Composable
private fun ConfirmationDialog(
    onContinue: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirmar Conexión", fontWeight = FontWeight.Bold, color = Color.White) },
        text = {
            Column(Modifier.padding(16.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Paso 1 completado",
                        tint = Color(0xFF00D4AA)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Paso 1: Abrir UniSat", fontSize = 16.sp, color = Color.White)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Paso 2 completado",
                        tint = Color(0xFF00D4AA)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Paso 2: Firmas completadas en UniSat", fontSize = 16.sp, color = Color.White)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF7931A))
            ) {
                Text("CONTINUAR", fontWeight = FontWeight.Bold, color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text("Cancelar", fontWeight = FontWeight.Medium, color = Color.Gray)
            }
        }
    )
}

@Composable
private fun AddressInputDialog(
    currentAddress: String,
    onAddressChange: (String) -> Unit,
    onConnect: () -> Unit,
    onDismiss: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Pega tu dirección de UniSat", fontWeight = FontWeight.Bold, color = Color.White) },
        text = {
            Column(Modifier.padding(16.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                TextField(
                    value = currentAddress,
                    onValueChange = onAddressChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("bc1p...", color = Color.Gray) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Done)
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = {
                        clipboardManager.getText()?.let { text -> onAddressChange(text.toString()) }
                    }) {
                        Text("PEGAR", fontWeight = FontWeight.Bold, color = Color(0xFFF7931A))
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConnect,
                modifier = Modifier.fillMaxWidth(),
                enabled = currentAddress.trim().isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (currentAddress.trim().isNotBlank()) Color(0xFFF7931A) else Color.Gray.copy(alpha = 0.5f)
                )
            ) {
                Text("CONECTAR", fontWeight = FontWeight.Bold, color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text("Cancelar", fontWeight = FontWeight.Medium, color = Color.Gray)
            }
        }
    )
}

@Composable
fun WalletScreen(
    walletState: WalletState,
    onConnectWallet: () -> Unit,
    onPreviewInscription: (InscriptionInfo) -> Unit,
    onConfirmSelection: () -> Unit,
    onDisconnectWallet: () -> Unit,
    onDismiss: () -> Unit,
    onContinueConfirmation: () -> Unit = {},
    onAddressInputChange: (String) -> Unit = {},
    onConnectWithAddress: () -> Unit = {},
    onDismissDialogs: () -> Unit = {},
    onRefreshInscriptions: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Cuenta Bittick",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                IconButton(onClick = onDismiss) {
                    Text("✕", color = Color.Gray, fontSize = 20.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (walletState.connectedAddress == null) {
                ConnectWalletSection(
                    isConnecting = walletState.isConnecting,
                    error = walletState.error,
                    onConnectWallet = onConnectWallet
                )
} else {
                ConnectedWalletSection(
                    address = walletState.connectedAddress,
                    inscriptions = walletState.inscriptions,
                    selectedInscription = walletState.selectedInscription,
                    previewInscription = walletState.previewInscription,
                    previewBotImageUrl = walletState.previewBotImageUrl,
                    botImageUrl = walletState.botImageUrl,
                    isPremium = walletState.isPremium,
                    tier = walletState.tier,
                    botNumber = walletState.botNumber,
                    onPreviewInscription = onPreviewInscription,
                    onConfirmSelection = onConfirmSelection,
                    onDisconnectWallet = onDisconnectWallet,
                    onRefreshInscriptions = onRefreshInscriptions
                )
            }
        }

        // Dialog 1: Confirmación de conexión (aparece al volver de UniSat con nonce pendiente)
        if (walletState.showConfirmationDialog) {
            ConfirmationDialog(
                onContinue = onContinueConfirmation,
                onDismiss = onDismissDialogs
            )
        }

        // Dialog 2: Pegar dirección de wallet (aparece al tocar CONTINUAR en Dialog 1)
        if (walletState.showAddressInputDialog) {
            AddressInputDialog(
                currentAddress = walletState.tempAddressInput,
                onAddressChange = onAddressInputChange,
                onConnect = onConnectWithAddress,
                onDismiss = onDismissDialogs
            )
        }
    }
}

@Composable
private fun ConnectWalletSection(
    isConnecting: Boolean,
    error: String?,
    onConnectWallet: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Conecta tu wallet UniSat",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Necesitas una wallet UniSat con una inscripción de la colección Bittick Agent para acceder a funciones premium",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onConnectWallet,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            enabled = !isConnecting,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFF7931A)
            )
        ) {
            if (isConnecting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White
                )
            } else {
                Text(
                    text = "Conectar UniSat",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        if (error != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = error,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1E1E1E)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "¿Qué es una inscripción Bittick Agent?",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Las inscripciones Bittick Agent son NFTs en la red de Bitcoin que dan acceso a funciones premium del bot de trading. Cada inscripción tiene un número del 00 al 99 que representa un bot específico.",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun ConnectedWalletSection(
    address: String,
    inscriptions: List<InscriptionInfo>,
    selectedInscription: InscriptionInfo?,
    previewInscription: InscriptionInfo?,
    previewBotImageUrl: String?,
    botImageUrl: String?,
    isPremium: Boolean,
    tier: String?,
    botNumber: Int?,
    onPreviewInscription: (InscriptionInfo) -> Unit,
    onConfirmSelection: () -> Unit,
    onDisconnectWallet: () -> Unit,
    onRefreshInscriptions: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1E1E1E)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Wallet conectada",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isPremium) "PREMIUM" else "GRATIS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isPremium) Color(0xFFF7931A) else Color.Gray
                        )
                        if (botNumber != null) {
                            Text(
                                text = "Bot %02d".format(botNumber),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Box(
                            modifier = Modifier.size(60.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF2A2A2A))
                                .border(2.dp, Color(0xFFF7931A), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            botImageUrl?.takeIf { it.isNotBlank() }?.let { base64 ->
                                val bitmap = walletBase64ToBitmap(base64)
                                if (bitmap != null) {
                                    Image(
                                        bitmap = bitmap.asImageBitmap(),
                                        contentDescription = "Bot image",
                                        modifier = Modifier
                                            .size(56.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "${address.take(8)}...${address.takeLast(8)}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onDisconnectWallet,
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2A2A2A)
                        )
                    ) {
                        Text(
                            text = "Desconectar",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = onRefreshInscriptions,
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF7931A)
                        )
                    ) {
                        Text(
                            text = "Ver todos los bots de esta wallet",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (previewInscription != null) {
            SelectedInscriptionCard(
                inscription = previewInscription,
                botImageUrl = previewBotImageUrl,
                isPremium = isPremium,
                tier = tier,
                botNumber = botNumber,
                selectedInscription = selectedInscription,
                onConfirmSelection = onConfirmSelection
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Seleccionar inscripción",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            if (inscriptions.isNotEmpty()) {
                Text(
                    text = "${inscriptions.size} inscripciones",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (inscriptions.isEmpty()) {
            EmptyInscriptionsSection()
        } else {
            InscriptionList(
                inscriptions = inscriptions,
                selectedInscription = selectedInscription,
                onPreviewInscription = onPreviewInscription
            )
        }
    }
}

@Composable
private fun SelectedInscriptionCard(
    inscription: InscriptionInfo,
    botImageUrl: String?,
    isPremium: Boolean,
    tier: String?,
    botNumber: Int?,
    selectedInscription: InscriptionInfo?,
    onConfirmSelection: () -> Unit
) {
    val isAlreadySelected = selectedInscription?.inscriptionId == inscription.inscriptionId
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E1E)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (botImageUrl != null) {
                val bitmap = walletBase64ToBitmap(botImageUrl)
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Bot Image",
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color(0xFFF7931A), CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color(0xFFF7931A), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🤖", fontSize = 32.sp)
                    }
                }
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

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Bot #${botNumber ?: inscription.num}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = tier ?: inscription.tier,
                    fontSize = 14.sp,
                    color = if (isPremium) Color(0xFFF7931A) else Color.Gray
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                if (isPremium) {
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .background(Color(0xFFF7931A), RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "PREMIUM",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }

                if (isAlreadySelected) {
                    Text(
                        text = "SELECCIONADO",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00BCD4)
                    )
                } else {
                    Button(
                        onClick = onConfirmSelection,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00BCD4)
                        )
                    ) {
                        Text(
                            text = "USAR",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyInscriptionsSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E1E)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "No se encontraron inscripciones",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Esta wallet no tiene inscripciones de la colección Bittick Agent. Necesitas al menos una inscripción para acceder a funciones premium.",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun InscriptionList(
    inscriptions: List<InscriptionInfo>,
    selectedInscription: InscriptionInfo?,
    onPreviewInscription: (InscriptionInfo) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(inscriptions) { inscription ->
            InscriptionCard(
                inscription = inscription,
                isSelected = inscription.inscriptionId == selectedInscription?.inscriptionId,
                onPreviewInscription = onPreviewInscription
            )
        }
    }
}

@Composable
private fun InscriptionCard(
    inscription: InscriptionInfo,
    isSelected: Boolean,
    onPreviewInscription: (InscriptionInfo) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPreviewInscription(inscription) },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFF7931A).copy(alpha = 0.2f) else Color(0xFF1E1E1E)
        ),
        border = if (isSelected) {
            ButtonDefaults.outlinedButtonBorder
        } else {
            null
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2A2A2A)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "#${inscription.num}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Bot #${inscription.num}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = inscription.tier,
                    fontSize = 14.sp,
                    color = if (inscription.tier == "FOUNDER") Color(0xFFF7931A) else Color.Gray
                )
            }

            if (isSelected) {
                Text(
                    text = "✓",
                    fontSize = 20.sp,
                    color = Color(0xFFF7931A)
                )
            }
        }
    }
}

private fun walletBase64ToBitmap(base64: String): android.graphics.Bitmap? {
    return try {
        if (base64.isBlank()) return null
        val bytes = android.util.Base64.decode(base64, android.util.Base64.NO_WRAP)
        if (bytes.isEmpty()) return null
        android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    } catch (_: Exception) {
        null
    }
}
