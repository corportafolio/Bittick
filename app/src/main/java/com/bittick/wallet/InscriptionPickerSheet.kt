package com.bittick.wallet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bittick.network.InscriptionInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InscriptionPickerSheet(
    inscriptions: List<InscriptionInfo>,
    selectedInscription: InscriptionInfo?,
    onSelectInscription: (InscriptionInfo) -> Unit,
    onDismiss: () -> Unit,
    onShowAll: () -> Unit
) {
    val maxVisible = 3
    val visibleInscriptions = inscriptions.take(maxVisible)
    val hasMore = inscriptions.size > maxVisible

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Seleccionar inscripción",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(visibleInscriptions) { inscription ->
                    InscriptionPickerItem(
                        inscription = inscription,
                        isSelected = inscription.inscriptionId == selectedInscription?.inscriptionId,
                        onClick = {
                            onSelectInscription(inscription)
                            onDismiss()
                        }
                    )
                }

                if (hasMore) {
                    item {
                        TextButton(
                            onClick = {
                                onDismiss()
                                onShowAll()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Ver todas las inscripciones (${inscriptions.size})",
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
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
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Bot #${inscription.num}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = inscription.tier,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (isSelected) {
                Text(
                    text = "✓",
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
