package com.bittick

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BittickApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(
                NotificationChannel(
                    "bittick_service",
                    "bittick Servicio",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "Notificación persistente del servicio en segundo plano"
                }
            )
            nm.createNotificationChannel(
                NotificationChannel(
                    "bittick_trading",
                    "Señales de Trading",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Notificaciones de nuevas oportunidades de trading"
                }
            )
        }
    }
}
