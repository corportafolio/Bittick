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

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000L\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010\u000b\n\u0002\b,\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B\u00c1\u0001\u0012\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\u000e\b\u0002\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00060\u0003\u0012\u000e\b\u0002\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00060\u0003\u0012\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\t\u0012\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\t\u0012\u000e\b\u0002\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\f0\u0003\u0012\u000e\b\u0002\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000e0\u0003\u0012\b\b\u0002\u0010\u000f\u001a\u00020\u0010\u0012\n\b\u0002\u0010\u0011\u001a\u0004\u0018\u00010\u0012\u0012\b\b\u0002\u0010\u0013\u001a\u00020\u0014\u0012\b\b\u0002\u0010\u0015\u001a\u00020\u0014\u0012\n\b\u0002\u0010\u0016\u001a\u0004\u0018\u00010\u0010\u0012\b\b\u0002\u0010\u0017\u001a\u00020\u0010\u0012\b\b\u0002\u0010\u0018\u001a\u00020\u0014\u0012\b\b\u0002\u0010\u0019\u001a\u00020\u0014\u00a2\u0006\u0002\u0010\u001aJ\u000f\u0010-\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00c6\u0003J\t\u0010.\u001a\u00020\u0014H\u00c6\u0003J\t\u0010/\u001a\u00020\u0014H\u00c6\u0003J\u000b\u00100\u001a\u0004\u0018\u00010\u0010H\u00c6\u0003J\t\u00101\u001a\u00020\u0010H\u00c6\u0003J\t\u00102\u001a\u00020\u0014H\u00c6\u0003J\t\u00103\u001a\u00020\u0014H\u00c6\u0003J\u000f\u00104\u001a\b\u0012\u0004\u0012\u00020\u00060\u0003H\u00c6\u0003J\u000f\u00105\u001a\b\u0012\u0004\u0012\u00020\u00060\u0003H\u00c6\u0003J\u000b\u00106\u001a\u0004\u0018\u00010\tH\u00c6\u0003J\u000b\u00107\u001a\u0004\u0018\u00010\tH\u00c6\u0003J\u000f\u00108\u001a\b\u0012\u0004\u0012\u00020\f0\u0003H\u00c6\u0003J\u000f\u00109\u001a\b\u0012\u0004\u0012\u00020\u000e0\u0003H\u00c6\u0003J\t\u0010:\u001a\u00020\u0010H\u00c6\u0003J\u0010\u0010;\u001a\u0004\u0018\u00010\u0012H\u00c6\u0003\u00a2\u0006\u0002\u0010!J\u00ca\u0001\u0010<\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u000e\b\u0002\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00060\u00032\u000e\b\u0002\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00060\u00032\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\t2\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\t2\u000e\b\u0002\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\f0\u00032\u000e\b\u0002\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000e0\u00032\b\b\u0002\u0010\u000f\u001a\u00020\u00102\n\b\u0002\u0010\u0011\u001a\u0004\u0018\u00010\u00122\b\b\u0002\u0010\u0013\u001a\u00020\u00142\b\b\u0002\u0010\u0015\u001a\u00020\u00142\n\b\u0002\u0010\u0016\u001a\u0004\u0018\u00010\u00102\b\b\u0002\u0010\u0017\u001a\u00020\u00102\b\b\u0002\u0010\u0018\u001a\u00020\u00142\b\b\u0002\u0010\u0019\u001a\u00020\u0014H\u00c6\u0001\u00a2\u0006\u0002\u0010=J\u0013\u0010>\u001a\u00020\u00142\b\u0010?\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010@\u001a\u00020AH\u00d6\u0001J\t\u0010B\u001a\u00020\u0010H\u00d6\u0001R\u0011\u0010\u000f\u001a\u00020\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u001cR\u0011\u0010\u0015\u001a\u00020\u0014\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u001eR\u0011\u0010\u0017\u001a\u00020\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010\u001cR\u0015\u0010\u0011\u001a\u0004\u0018\u00010\u0012\u00a2\u0006\n\n\u0002\u0010\"\u001a\u0004\b \u0010!R\u0013\u0010\u0016\u001a\u0004\u0018\u00010\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b#\u0010\u001cR\u0013\u0010\n\u001a\u0004\u0018\u00010\t\u00a2\u0006\b\n\u0000\u001a\u0004\b$\u0010%R\u0017\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00060\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b&\u0010\'R\u0011\u0010\u0019\u001a\u00020\u0014\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001eR\u0011\u0010\u0013\u001a\u00020\u0014\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u001eR\u0011\u0010\u0018\u001a\u00020\u0014\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u001eR\u0017\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\f0\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b(\u0010\'R\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b)\u0010\'R\u0013\u0010\b\u001a\u0004\u0018\u00010\t\u00a2\u0006\b\n\u0000\u001a\u0004\b*\u0010%R\u0017\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00060\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b+\u0010\'R\u0017\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000e0\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b,\u0010\'\u00a8\u0006C"}, d2 = {"Lcom/bittick/ui/trading/TradingUiState;", "", "opportunities", "", "Lcom/bittick/ui/trading/TradingOpportunityItem;", "spotPositions", "Lcom/bittick/network/BotPosition;", "futuresPositions", "spotBotStatus", "Lcom/bittick/network/BotStatusItem;", "futuresBotStatus", "klines", "Lcom/bittick/network/Kline;", "zones", "Lcom/bittick/network/ChartZone;", "chartInterval", "", "currentPrice", "", "isLoading", "", "chartLoading", "error", "chartStatus", "isPremium", "isFreeTier", "(Ljava/util/List;Ljava/util/List;Ljava/util/List;Lcom/bittick/network/BotStatusItem;Lcom/bittick/network/BotStatusItem;Ljava/util/List;Ljava/util/List;Ljava/lang/String;Ljava/lang/Double;ZZLjava/lang/String;Ljava/lang/String;ZZ)V", "getChartInterval", "()Ljava/lang/String;", "getChartLoading", "()Z", "getChartStatus", "getCurrentPrice", "()Ljava/lang/Double;", "Ljava/lang/Double;", "getError", "getFuturesBotStatus", "()Lcom/bittick/network/BotStatusItem;", "getFuturesPositions", "()Ljava/util/List;", "getKlines", "getOpportunities", "getSpotBotStatus", "getSpotPositions", "getZones", "component1", "component10", "component11", "component12", "component13", "component14", "component15", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "(Ljava/util/List;Ljava/util/List;Ljava/util/List;Lcom/bittick/network/BotStatusItem;Lcom/bittick/network/BotStatusItem;Ljava/util/List;Ljava/util/List;Ljava/lang/String;Ljava/lang/Double;ZZLjava/lang/String;Ljava/lang/String;ZZ)Lcom/bittick/ui/trading/TradingUiState;", "equals", "other", "hashCode", "", "toString", "app_debug"})
public final class TradingUiState {
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.bittick.ui.trading.TradingOpportunityItem> opportunities = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.bittick.network.BotPosition> spotPositions = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.bittick.network.BotPosition> futuresPositions = null;
    @org.jetbrains.annotations.Nullable()
    private final com.bittick.network.BotStatusItem spotBotStatus = null;
    @org.jetbrains.annotations.Nullable()
    private final com.bittick.network.BotStatusItem futuresBotStatus = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.bittick.network.Kline> klines = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.bittick.network.ChartZone> zones = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String chartInterval = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Double currentPrice = null;
    private final boolean isLoading = false;
    private final boolean chartLoading = false;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String error = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String chartStatus = null;
    private final boolean isPremium = false;
    private final boolean isFreeTier = false;
    
    public TradingUiState(@org.jetbrains.annotations.NotNull()
    java.util.List<com.bittick.ui.trading.TradingOpportunityItem> opportunities, @org.jetbrains.annotations.NotNull()
    java.util.List<com.bittick.network.BotPosition> spotPositions, @org.jetbrains.annotations.NotNull()
    java.util.List<com.bittick.network.BotPosition> futuresPositions, @org.jetbrains.annotations.Nullable()
    com.bittick.network.BotStatusItem spotBotStatus, @org.jetbrains.annotations.Nullable()
    com.bittick.network.BotStatusItem futuresBotStatus, @org.jetbrains.annotations.NotNull()
    java.util.List<com.bittick.network.Kline> klines, @org.jetbrains.annotations.NotNull()
    java.util.List<com.bittick.network.ChartZone> zones, @org.jetbrains.annotations.NotNull()
    java.lang.String chartInterval, @org.jetbrains.annotations.Nullable()
    java.lang.Double currentPrice, boolean isLoading, boolean chartLoading, @org.jetbrains.annotations.Nullable()
    java.lang.String error, @org.jetbrains.annotations.NotNull()
    java.lang.String chartStatus, boolean isPremium, boolean isFreeTier) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.bittick.ui.trading.TradingOpportunityItem> getOpportunities() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.bittick.network.BotPosition> getSpotPositions() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.bittick.network.BotPosition> getFuturesPositions() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.bittick.network.BotStatusItem getSpotBotStatus() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.bittick.network.BotStatusItem getFuturesBotStatus() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.bittick.network.Kline> getKlines() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.bittick.network.ChartZone> getZones() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getChartInterval() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Double getCurrentPrice() {
        return null;
    }
    
    public final boolean isLoading() {
        return false;
    }
    
    public final boolean getChartLoading() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getError() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getChartStatus() {
        return null;
    }
    
    public final boolean isPremium() {
        return false;
    }
    
    public final boolean isFreeTier() {
        return false;
    }
    
    public TradingUiState() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.bittick.ui.trading.TradingOpportunityItem> component1() {
        return null;
    }
    
    public final boolean component10() {
        return false;
    }
    
    public final boolean component11() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component12() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component13() {
        return null;
    }
    
    public final boolean component14() {
        return false;
    }
    
    public final boolean component15() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.bittick.network.BotPosition> component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.bittick.network.BotPosition> component3() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.bittick.network.BotStatusItem component4() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.bittick.network.BotStatusItem component5() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.bittick.network.Kline> component6() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.bittick.network.ChartZone> component7() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component8() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Double component9() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.bittick.ui.trading.TradingUiState copy(@org.jetbrains.annotations.NotNull()
    java.util.List<com.bittick.ui.trading.TradingOpportunityItem> opportunities, @org.jetbrains.annotations.NotNull()
    java.util.List<com.bittick.network.BotPosition> spotPositions, @org.jetbrains.annotations.NotNull()
    java.util.List<com.bittick.network.BotPosition> futuresPositions, @org.jetbrains.annotations.Nullable()
    com.bittick.network.BotStatusItem spotBotStatus, @org.jetbrains.annotations.Nullable()
    com.bittick.network.BotStatusItem futuresBotStatus, @org.jetbrains.annotations.NotNull()
    java.util.List<com.bittick.network.Kline> klines, @org.jetbrains.annotations.NotNull()
    java.util.List<com.bittick.network.ChartZone> zones, @org.jetbrains.annotations.NotNull()
    java.lang.String chartInterval, @org.jetbrains.annotations.Nullable()
    java.lang.Double currentPrice, boolean isLoading, boolean chartLoading, @org.jetbrains.annotations.Nullable()
    java.lang.String error, @org.jetbrains.annotations.NotNull()
    java.lang.String chartStatus, boolean isPremium, boolean isFreeTier) {
        return null;
    }
    
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String toString() {
        return null;
    }
}