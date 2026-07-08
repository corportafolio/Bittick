package com.bittick.data.ai

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var tts: TextToSpeech? = null

    init {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale("es", "MX")
            }
        }
    }

    fun notifyTradingOpportunityByScore(
        asset: String,
        type: String,
        price: String,
        score: Int,
        confidence: Int,
        entryZone: String,
        target: String,
        stopLoss: String,
        explanation: String,
        opportunityId: Int
    ) {
        val typeLabel = if (type == "long") "LONG" else "SHORT"
        val direction = if (type == "long") "compra" else "venta"

        val title = when {
            score >= 10 -> "Excelente — $asset"
            score == 9 -> "Muy buena señal — $asset"
            score == 8 -> "Buena señal — $asset"
            score == 7 -> "Oportunidad posible — $asset"
            else -> "Oportunidad leve — $asset"
        }

        val nm = NotificationManagerCompat.from(context)
        val notification = NotificationCompat.Builder(context, "bittick_trading")
            .setContentTitle(title)
            .setContentText("Puntaje: $score/10 — Confianza: $confidence/10 — Precio: $$price")
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(
                    "$typeLabel $asset\n" +
                    "Puntaje: $score/10 | Confianza: $confidence/10\n" +
                    "Precio: $$price\n" +
                    "Entrada: $entryZone\n" +
                    "Objetivo: $target\n" +
                    "Stop Loss: $stopLoss\n\n" +
                    explanation.take(200)
                )
            )
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        nm.notify(2000 + opportunityId, notification)

        val mensaje = "Oportunidad de $direction en $asset. " +
            "Puntaje $score sobre 10, confianza $confidence sobre 10. " +
            "Precio actual $price dólares. " +
            explanation.take(150)
        speak(mensaje)
    }

    private fun speak(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_ADD, null, null)
    }

    fun destroy() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
