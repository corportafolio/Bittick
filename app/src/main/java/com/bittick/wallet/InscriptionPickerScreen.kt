package com.bittick.wallet

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InscriptionPickerScreen(
    inscriptions: List<InscriptionInfo>,
    selectedInscription: InscriptionInfo?,
    onSelectInscription: (InscriptionInfo) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Seleccionar inscripción") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("←", fontSize = 20.sp)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Selecciona una inscripción de la colección Bittick Agent",
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(inscriptions) { inscription ->
                    InscriptionPickerItem(
                        inscription = inscription,
                        isSelected = inscription.inscriptionId == selectedInscription?.inscriptionId,
                        onClick = {
                            onSelectInscription(inscription)
                            onBack()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun InscriptionPickerItem(
    inscription: InscriptionInfo,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                Color(0xFFF7931A).copy(alpha = 0.2f)
            } else {
                Color(0xFF1E1E1E)
            }
        )
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

@Composable
private fun base64ToBitmap(base64: String): android.graphics.Bitmap {
    val bytes = android.util.Base64.decode(base64, android.util.Base64.NO_WRAP)
    return android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}
