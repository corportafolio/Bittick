package com.bittick.ui.trading;

import android.content.Context;
import android.util.Log;
import androidx.lifecycle.ViewModel;
import com.bittick.data.ai.NotificationHelper;
import com.bittick.data.preferences.BittickPreferences;
import com.bittick.network.ApiService;
import com.bittick.network.BotPosition;
import com.bittick.network.BotStatusItem;
import com.bittick.network.ChartZone;
import com.bittick.network.Kline;
import com.bittick.network.TradingOpportunity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dagger.hilt.android.lifecycle.HiltViewModel;
import dagger.hilt.android.qualifiers.ApplicationContext;
import kotlinx.coroutines.flow.StateFlow;
import javax.inject.Inject;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010#\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0018\u0002\n\u0002\b\u0007\b\u0007\u0018\u00002\u00020\u0001B)\b\u0007\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u00a2\u0006\u0002\u0010\nJ\u0016\u0010\u0019\u001a\u00020\u001a2\f\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u001d0\u001cH\u0002J\u000e\u0010\u001e\u001a\u00020\u001a2\u0006\u0010\u001f\u001a\u00020\u0010J\u000e\u0010 \u001a\u00020\u001a2\u0006\u0010!\u001a\u00020\u0010J\u000e\u0010\"\u001a\u00020\u001aH\u0082@\u00a2\u0006\u0002\u0010#J\u0006\u0010$\u001a\u00020\u001aJ\u0010\u0010%\u001a\u00020\u001a2\b\b\u0002\u0010&\u001a\u00020\u0012J\u0006\u0010\'\u001a\u00020\u001aJ$\u0010(\u001a\u00020\u001a2\u0006\u0010&\u001a\u00020\u00122\f\u0010)\u001a\b\u0012\u0004\u0012\u00020*0\u001cH\u0082@\u00a2\u0006\u0002\u0010+J\b\u0010,\u001a\u00020\u001aH\u0014J\b\u0010-\u001a\u00020\u001aH\u0002J\b\u0010.\u001a\u00020\u001aH\u0002J\u0016\u0010/\u001a\u00020\u001a2\f\u00100\u001a\b\u0012\u0004\u0012\u00020\u001d0\u001cH\u0002R\u0014\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00100\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0011\u001a\u0004\u0018\u00010\u0012X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0013\u001a\u0004\u0018\u00010\u0014X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\r0\u0016\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018\u00a8\u00061"}, d2 = {"Lcom/bittick/ui/trading/TradingViewModel;", "Landroidx/lifecycle/ViewModel;", "context", "Landroid/content/Context;", "api", "Lcom/bittick/network/ApiService;", "notifier", "Lcom/bittick/data/ai/NotificationHelper;", "prefs", "Lcom/bittick/data/preferences/BittickPreferences;", "(Landroid/content/Context;Lcom/bittick/network/ApiService;Lcom/bittick/data/ai/NotificationHelper;Lcom/bittick/data/preferences/BittickPreferences;)V", "_state", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/bittick/ui/trading/TradingUiState;", "announcedOpportunityIds", "", "", "lastCreatedAt", "", "pollingJob", "Lkotlinx/coroutines/Job;", "state", "Lkotlinx/coroutines/flow/StateFlow;", "getState", "()Lkotlinx/coroutines/flow/StateFlow;", "announceOpportunities", "", "opportunities", "", "Lcom/bittick/ui/trading/TradingOpportunityItem;", "cancelPosition", "positionId", "deleteOpportunity", "opportunityId", "fetchNewOpportunities", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "loadAll", "loadKlines", "interval", "loadTicker", "loadZones", "klines", "Lcom/bittick/network/Kline;", "(Ljava/lang/String;Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "onCleared", "startPolling", "stopPolling", "updateLastCreatedAt", "items", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class TradingViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    private final com.bittick.network.ApiService api = null;
    @org.jetbrains.annotations.NotNull()
    private final com.bittick.data.ai.NotificationHelper notifier = null;
    @org.jetbrains.annotations.NotNull()
    private final com.bittick.data.preferences.BittickPreferences prefs = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.bittick.ui.trading.TradingUiState> _state = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.bittick.ui.trading.TradingUiState> state = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.Set<java.lang.Integer> announcedOpportunityIds = null;
    @org.jetbrains.annotations.Nullable()
    private kotlinx.coroutines.Job pollingJob;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String lastCreatedAt;
    
    @javax.inject.Inject()
    public TradingViewModel(@dagger.hilt.android.qualifiers.ApplicationContext()
    @org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    com.bittick.network.ApiService api, @org.jetbrains.annotations.NotNull()
    com.bittick.data.ai.NotificationHelper notifier, @org.jetbrains.annotations.NotNull()
    com.bittick.data.preferences.BittickPreferences prefs) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.bittick.ui.trading.TradingUiState> getState() {
        return null;
    }
    
    @java.lang.Override()
    protected void onCleared() {
    }
    
    private final void startPolling() {
    }
    
    private final void stopPolling() {
    }
    
    private final java.lang.Object fetchNewOpportunities(kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    private final void updateLastCreatedAt(java.util.List<com.bittick.ui.trading.TradingOpportunityItem> items) {
    }
    
    public final void loadAll() {
    }
    
    public final void loadKlines(@org.jetbrains.annotations.NotNull()
    java.lang.String interval) {
    }
    
    private final java.lang.Object loadZones(java.lang.String interval, java.util.List<com.bittick.network.Kline> klines, kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    public final void loadTicker() {
    }
    
    public final void cancelPosition(int positionId) {
    }
    
    public final void deleteOpportunity(int opportunityId) {
    }
    
    private final void announceOpportunities(java.util.List<com.bittick.ui.trading.TradingOpportunityItem> opportunities) {
    }
}