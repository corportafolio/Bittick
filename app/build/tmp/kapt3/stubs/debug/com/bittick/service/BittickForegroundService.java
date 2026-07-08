package com.bittick.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import com.bittick.MainActivity;
import com.bittick.data.ai.NotificationHelper;
import com.bittick.data.preferences.BittickPreferences;
import com.bittick.network.ApiService;
import dagger.hilt.android.AndroidEntryPoint;
import kotlinx.coroutines.Dispatchers;
import javax.inject.Inject;

@dagger.hilt.android.AndroidEntryPoint()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000^\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010#\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0007\b\u0007\u0018\u0000 ,2\u00020\u0001:\u0001,B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u001f\u001a\u00020 H\u0002J\u0014\u0010!\u001a\u0004\u0018\u00010\"2\b\u0010#\u001a\u0004\u0018\u00010$H\u0016J\b\u0010%\u001a\u00020&H\u0016J\b\u0010\'\u001a\u00020&H\u0016J\"\u0010(\u001a\u00020\u00052\b\u0010#\u001a\u0004\u0018\u00010$2\u0006\u0010)\u001a\u00020\u00052\u0006\u0010*\u001a\u00020\u0005H\u0016J\b\u0010+\u001a\u00020&H\u0002R\u0014\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001e\u0010\u0006\u001a\u00020\u00078\u0006@\u0006X\u0087.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\b\u0010\t\"\u0004\b\n\u0010\u000bR\u001e\u0010\f\u001a\u00020\r8\u0006@\u0006X\u0087.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u000e\u0010\u000f\"\u0004\b\u0010\u0010\u0011R\u001e\u0010\u0012\u001a\u00020\u00138\u0006@\u0006X\u0087.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0014\u0010\u0015\"\u0004\b\u0016\u0010\u0017R\u000e\u0010\u0018\u001a\u00020\u0019X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u001a\u001a\u0004\u0018\u00010\u001bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u001c\u001a\b\u0018\u00010\u001dR\u00020\u001eX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006-"}, d2 = {"Lcom/bittick/service/BittickForegroundService;", "Landroid/app/Service;", "()V", "announcedTradingIds", "", "", "api", "Lcom/bittick/network/ApiService;", "getApi", "()Lcom/bittick/network/ApiService;", "setApi", "(Lcom/bittick/network/ApiService;)V", "notifier", "Lcom/bittick/data/ai/NotificationHelper;", "getNotifier", "()Lcom/bittick/data/ai/NotificationHelper;", "setNotifier", "(Lcom/bittick/data/ai/NotificationHelper;)V", "prefs", "Lcom/bittick/data/preferences/BittickPreferences;", "getPrefs", "()Lcom/bittick/data/preferences/BittickPreferences;", "setPrefs", "(Lcom/bittick/data/preferences/BittickPreferences;)V", "scope", "Lkotlinx/coroutines/CoroutineScope;", "tradingPollingJob", "Lkotlinx/coroutines/Job;", "wakeLock", "Landroid/os/PowerManager$WakeLock;", "Landroid/os/PowerManager;", "buildNotification", "Landroid/app/Notification;", "onBind", "Landroid/os/IBinder;", "intent", "Landroid/content/Intent;", "onCreate", "", "onDestroy", "onStartCommand", "flags", "startId", "startTradingPolling", "Companion", "app_debug"})
public final class BittickForegroundService extends android.app.Service {
    @javax.inject.Inject()
    public com.bittick.data.preferences.BittickPreferences prefs;
    @javax.inject.Inject()
    public com.bittick.data.ai.NotificationHelper notifier;
    @javax.inject.Inject()
    public com.bittick.network.ApiService api;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.CoroutineScope scope = null;
    @org.jetbrains.annotations.Nullable()
    private kotlinx.coroutines.Job tradingPollingJob;
    @org.jetbrains.annotations.Nullable()
    private android.os.PowerManager.WakeLock wakeLock;
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "BittickFgService";
    private static final int NOTIF_ID = 1001;
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String CHANNEL_ID = "bittick_service";
    private static final long TRADING_POLL_INTERVAL = 60000L;
    @org.jetbrains.annotations.NotNull()
    private final java.util.Set<java.lang.Integer> announcedTradingIds = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.bittick.service.BittickForegroundService.Companion Companion = null;
    
    public BittickForegroundService() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.bittick.data.preferences.BittickPreferences getPrefs() {
        return null;
    }
    
    public final void setPrefs(@org.jetbrains.annotations.NotNull()
    com.bittick.data.preferences.BittickPreferences p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.bittick.data.ai.NotificationHelper getNotifier() {
        return null;
    }
    
    public final void setNotifier(@org.jetbrains.annotations.NotNull()
    com.bittick.data.ai.NotificationHelper p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.bittick.network.ApiService getApi() {
        return null;
    }
    
    public final void setApi(@org.jetbrains.annotations.NotNull()
    com.bittick.network.ApiService p0) {
    }
    
    @java.lang.Override()
    public void onCreate() {
    }
    
    @java.lang.Override()
    public int onStartCommand(@org.jetbrains.annotations.Nullable()
    android.content.Intent intent, int flags, int startId) {
        return 0;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public android.os.IBinder onBind(@org.jetbrains.annotations.Nullable()
    android.content.Intent intent) {
        return null;
    }
    
    @java.lang.Override()
    public void onDestroy() {
    }
    
    private final android.app.Notification buildNotification() {
        return null;
    }
    
    private final void startTradingPolling() {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\n"}, d2 = {"Lcom/bittick/service/BittickForegroundService$Companion;", "", "()V", "CHANNEL_ID", "", "NOTIF_ID", "", "TAG", "TRADING_POLL_INTERVAL", "", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}