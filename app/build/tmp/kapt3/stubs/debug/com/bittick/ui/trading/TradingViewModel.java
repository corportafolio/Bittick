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
import com.bittick.network.TradingZone;
import com.bittick.network.Kline;
import com.bittick.network.TradingOpportunity;
import com.bittick.network.TradingOpportunitiesResponse;
import com.bittick.network.PositionsResponse;
import com.bittick.network.BotStatusResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import retrofit2.Response;
import dagger.hilt.android.lifecycle.HiltViewModel;
import dagger.hilt.android.qualifiers.ApplicationContext;
import kotlinx.coroutines.flow.StateFlow;
import javax.inject.Inject;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000n\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0018\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B)\b\u0007\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u00a2\u0006\u0002\u0010\nJ\u000e\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u0013J\u000e\u0010\u001d\u001a\u00020\u001b2\u0006\u0010\u001e\u001a\u00020\u001fJ\u000e\u0010 \u001a\u00020\u001b2\u0006\u0010!\u001a\u00020\u001fJ\u000e\u0010\"\u001a\u00020\u001b2\u0006\u0010\u001e\u001a\u00020\u001fJ\u000e\u0010#\u001a\u00020\u001bH\u0082@\u00a2\u0006\u0002\u0010$J\n\u0010%\u001a\u0004\u0018\u00010\u0013H\u0002J\u0006\u0010&\u001a\u00020\u001bJ\u0016\u0010\'\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u0013H\u0082@\u00a2\u0006\u0002\u0010(J\u0010\u0010)\u001a\u00020\u001b2\b\b\u0002\u0010\u001c\u001a\u00020\u0013J\u0006\u0010*\u001a\u00020\u001bJ\u000e\u0010+\u001a\u00020\u001bH\u0082@\u00a2\u0006\u0002\u0010$J\b\u0010,\u001a\u00020\u001bH\u0014J\u0006\u0010-\u001a\u00020\u001bJ\u0012\u0010.\u001a\u00020\u001b2\b\b\u0002\u0010\u001c\u001a\u00020\u0013H\u0002J\b\u0010/\u001a\u00020\u001bH\u0002J\b\u00100\u001a\u00020\u001bH\u0002J\b\u00101\u001a\u00020\u001bH\u0002J\b\u00102\u001a\u00020\u001bH\u0002J\b\u00103\u001a\u00020\u001bH\u0002J\u0006\u00104\u001a\u00020\u001bJ\u0006\u00105\u001a\u00020\u001bJ\u0016\u00106\u001a\u00020\u001b2\f\u00107\u001a\b\u0012\u0004\u0012\u00020908H\u0002J\"\u0010:\u001a\u0004\u0018\u0001H;\"\u0006\b\u0000\u0010;\u0018\u0001*\b\u0012\u0004\u0012\u0002H;0<H\u0082\b\u00a2\u0006\u0002\u0010=R\u0014\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0010\u001a\u0004\u0018\u00010\u0011X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0012\u001a\u0004\u0018\u00010\u0013X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0014\u001a\u0004\u0018\u00010\u0011X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\r0\u0016\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u0010\u0010\u0019\u001a\u0004\u0018\u00010\u0011X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006>"}, d2 = {"Lcom/bittick/ui/trading/TradingViewModel;", "Landroidx/lifecycle/ViewModel;", "context", "Landroid/content/Context;", "api", "Lcom/bittick/network/ApiService;", "notifier", "Lcom/bittick/data/ai/NotificationHelper;", "prefs", "Lcom/bittick/data/preferences/BittickPreferences;", "(Landroid/content/Context;Lcom/bittick/network/ApiService;Lcom/bittick/data/ai/NotificationHelper;Lcom/bittick/data/preferences/BittickPreferences;)V", "_state", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/bittick/ui/trading/TradingUiState;", "gson", "Lcom/google/gson/Gson;", "klinesPollingJob", "Lkotlinx/coroutines/Job;", "lastCreatedAt", "", "pollingJob", "state", "Lkotlinx/coroutines/flow/StateFlow;", "getState", "()Lkotlinx/coroutines/flow/StateFlow;", "tickerPollingJob", "changeChartInterval", "", "interval", "closePosition", "positionId", "", "deleteOpportunity", "opportunityId", "dismissPosition", "fetchNewOpportunities", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getWalletAddress", "loadAll", "loadAutoZones", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "loadKlines", "loadTicker", "loadTradingZones", "onCleared", "refreshPremiumStatus", "startKlinesPolling", "startPolling", "startTickerPolling", "stopKlinesPolling", "stopPolling", "stopTickerPolling", "toggleChartExpanded", "toggleZonesVisible", "updateLastCreatedAt", "items", "", "Lcom/bittick/ui/trading/TradingOpportunityItem;", "parsedBody", "T", "Lretrofit2/Response;", "(Lretrofit2/Response;)Ljava/lang/Object;", "app_debug"})
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
    @org.jetbrains.annotations.Nullable()
    private kotlinx.coroutines.Job pollingJob;
    @org.jetbrains.annotations.Nullable()
    private kotlinx.coroutines.Job klinesPollingJob;
    @org.jetbrains.annotations.Nullable()
    private kotlinx.coroutines.Job tickerPollingJob;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String lastCreatedAt;
    @org.jetbrains.annotations.NotNull()
    private final com.google.gson.Gson gson = null;
    
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
    
    private final void startKlinesPolling(java.lang.String interval) {
    }
    
    private final void stopKlinesPolling() {
    }
    
    private final void startTickerPolling() {
    }
    
    private final void stopTickerPolling() {
    }
    
    private final java.lang.Object fetchNewOpportunities(kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    private final void updateLastCreatedAt(java.util.List<com.bittick.ui.trading.TradingOpportunityItem> items) {
    }
    
    private final java.lang.String getWalletAddress() {
        return null;
    }
    
    public final void loadAll() {
    }
    
    public final void refreshPremiumStatus() {
    }
    
    public final void loadKlines(@org.jetbrains.annotations.NotNull()
    java.lang.String interval) {
    }
    
    public final void changeChartInterval(@org.jetbrains.annotations.NotNull()
    java.lang.String interval) {
    }
    
    public final void toggleChartExpanded() {
    }
    
    public final void toggleZonesVisible() {
    }
    
    private final java.lang.Object loadAutoZones(java.lang.String interval, kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    private final java.lang.Object loadTradingZones(kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    public final void loadTicker() {
    }
    
    public final void closePosition(int positionId) {
    }
    
    public final void dismissPosition(int positionId) {
    }
    
    public final void deleteOpportunity(int opportunityId) {
    }
}