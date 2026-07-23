package com.bittick.service

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.bittick.MainActivity
import com.bittick.data.ai.NotificationHelper
import com.bittick.data.preferences.BittickPreferences
import com.bittick.network.ApiService
import com.bittick.ui.trading.toItem
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BittickForegroundService : Service() {

    @Inject lateinit var prefs: BittickPreferences
    @Inject lateinit var notifier: NotificationHelper
    @Inject lateinit var api: ApiService

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var tradingPollingJob: Job? = null
    private var wakeLock: PowerManager.WakeLock? = null

    companion object {
        private const val TAG = "BittickFgService"
        private const val NOTIF_ID = 1001
        private const val CHANNEL_ID = "bittick_service"
        private const val TRADING_POLL_INTERVAL = 60_000L
    }

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "onCreate")
        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Bittick:WakeLock")
        wakeLock?.acquire(10 * 60 * 1000L)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "onStartCommand")
        startForeground(NOTIF_ID, buildNotification())
        startTradingPolling()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        Log.i(TAG, "onDestroy")
        tradingPollingJob?.cancel()
        tradingPollingJob = null
        wakeLock?.let { if (it.isHeld) it.release() }
        wakeLock = null
        scope.cancel()
        super.onDestroy()
    }

    private fun buildNotification(): android.app.Notification {
        val openIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingOpen = PendingIntent.getActivity(
            this, 0, openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("bittick")
            .setContentText("Monitoreando señales BTC en segundo plano")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setContentIntent(pendingOpen)
            .build()
    }

    private val announcedTradingIds = mutableSetOf<Int>()

    private fun startTradingPolling() {
        tradingPollingJob?.cancel()
        tradingPollingJob = scope.launch {
            while (isActive) {
                try {
                    val since = prefs.getTradingLastCreatedAt()
                    val response = api.getTradingOpportunities(since = since)
                    val body = response.body()
                    if (response.isSuccessful && body != null && body.exito) {
                        val items = body.data.map { it.toItem() }
                        for (item in items) {
                            if (item.id in announcedTradingIds) continue
                            if (item.score >= 6 && item.confidence >= 6) {
                                announcedTradingIds.add(item.id)
                                notifier.notifyTradingOpportunityByScore(
                                    asset = item.asset,
                                    type = item.type,
                                    price = item.price,
                                    score = item.score,
                                    confidence = item.confidence,
                                    entryZone = item.entryZone,
                                    target = item.target,
                                    stopLoss = item.stopLoss,
                                    explanation = item.explanation,
                                    opportunityId = item.id
                                )
                                delay(3000)
                            }
                        }
                        val lastCreated = body.data.maxByOrNull { it.created_at ?: "" }?.created_at
                        if (lastCreated != null) {
                            prefs.setTradingLastCreatedAt(lastCreated)
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Trading polling error", e)
                }
                delay(TRADING_POLL_INTERVAL)
            }
        }
    }
}
