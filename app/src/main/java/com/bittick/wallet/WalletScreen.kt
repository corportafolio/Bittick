package com.bittick.wallet

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.bittick.network.InscriptionInfo

@Composable
fun WalletScreen(
    walletState: WalletState,
    onConnectWallet: () -> Unit,
    onSelectInscription: (InscriptionInfo) -> Unit,
    onDisconnectWallet: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
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
                botImageUrl = walletState.botImageUrl,
                isPremium = walletState.isPremium,
                tier = walletState.tier,
                botNumber = walletState.botNumber,
                onSelectInscription = onSelectInscription,
                onDisconnectWallet = onDisconnectWallet
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
    botImageUrl: String?,
    isPremium: Boolean,
    tier: String?,
    botNumber: Int?,
    onSelectInscription: (InscriptionInfo) -> Unit,
    onDisconnectWallet: () -> Unit
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
                    Text(
                        text = if (isPremium) "PREMIUM" else "GRATIS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isPremium) Color(0xFFF7931A) else Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "${address.take(8)}...${address.takeLast(8)}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onDisconnectWallet,
                    modifier = Modifier.fillMaxWidth(),
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
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedInscription != null) {
            SelectedInscriptionCard(
                inscription = selectedInscription,
                botImageUrl = botImageUrl,
                isPremium = isPremium,
                tier = tier,
                botNumber = botNumber
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Seleccionar inscripción",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (inscriptions.isEmpty()) {
            EmptyInscriptionsSection()
        } else {
            InscriptionList(
                inscriptions = inscriptions,
                selectedInscription = selectedInscription,
                onSelectInscription = onSelectInscription
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
    botNumber: Int?
) {
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
                Image(
                    bitmap = base64ToBitmap(botImageUrl).asImageBitmap(),
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
    onSelectInscription: (InscriptionInfo) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(inscriptions) { inscription ->
            InscriptionCard(
                inscription = inscription,
                isSelected = inscription.inscriptionId == selectedInscription?.inscriptionId,
                onSelectInscription = onSelectInscription
            )
        }
    }
}

@Composable
private fun InscriptionCard(
    inscription: InscriptionInfo,
    isSelected: Boolean,
    onSelectInscription: (InscriptionInfo) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelectInscription(inscription) },
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

private fun base64ToBitmap(base64: String): android.graphics.Bitmap {
    val bytes = android.util.Base64.decode(base64, android.util.Base64.NO_WRAP)
    return android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}
