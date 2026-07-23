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

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000T\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0010\b\n\u0002\b6\b\u0086\b\u0018\u00002\u00020\u0001B\u00ff\u0001\u0012\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\u000e\b\u0002\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\u000e\b\u0002\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00070\u0003\u0012\u000e\b\u0002\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\u0003\u0012\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\n\u0012\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\n\u0012\u000e\b\u0002\u0010\f\u001a\b\u0012\u0004\u0012\u00020\r0\u0003\u0012\u000e\b\u0002\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u000f0\u0003\u0012\u000e\b\u0002\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00110\u0003\u0012\b\b\u0002\u0010\u0012\u001a\u00020\u0013\u0012\n\b\u0002\u0010\u0014\u001a\u0004\u0018\u00010\u0015\u0012\b\b\u0002\u0010\u0016\u001a\u00020\u0017\u0012\b\b\u0002\u0010\u0018\u001a\u00020\u0017\u0012\n\b\u0002\u0010\u0019\u001a\u0004\u0018\u00010\u0013\u0012\b\b\u0002\u0010\u001a\u001a\u00020\u0013\u0012\b\b\u0002\u0010\u001b\u001a\u00020\u0017\u0012\b\b\u0002\u0010\u001c\u001a\u00020\u0017\u0012\b\b\u0002\u0010\u001d\u001a\u00020\u001e\u0012\b\b\u0002\u0010\u001f\u001a\u00020\u0017\u0012\b\b\u0002\u0010 \u001a\u00020\u0017\u00a2\u0006\u0002\u0010!J\u000f\u0010:\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00c6\u0003J\t\u0010;\u001a\u00020\u0013H\u00c6\u0003J\u0010\u0010<\u001a\u0004\u0018\u00010\u0015H\u00c6\u0003\u00a2\u0006\u0002\u0010+J\t\u0010=\u001a\u00020\u0017H\u00c6\u0003J\t\u0010>\u001a\u00020\u0017H\u00c6\u0003J\u000b\u0010?\u001a\u0004\u0018\u00010\u0013H\u00c6\u0003J\t\u0010@\u001a\u00020\u0013H\u00c6\u0003J\t\u0010A\u001a\u00020\u0017H\u00c6\u0003J\t\u0010B\u001a\u00020\u0017H\u00c6\u0003J\t\u0010C\u001a\u00020\u001eH\u00c6\u0003J\t\u0010D\u001a\u00020\u0017H\u00c6\u0003J\u000f\u0010E\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00c6\u0003J\t\u0010F\u001a\u00020\u0017H\u00c6\u0003J\u000f\u0010G\u001a\b\u0012\u0004\u0012\u00020\u00070\u0003H\u00c6\u0003J\u000f\u0010H\u001a\b\u0012\u0004\u0012\u00020\u00070\u0003H\u00c6\u0003J\u000b\u0010I\u001a\u0004\u0018\u00010\nH\u00c6\u0003J\u000b\u0010J\u001a\u0004\u0018\u00010\nH\u00c6\u0003J\u000f\u0010K\u001a\b\u0012\u0004\u0012\u00020\r0\u0003H\u00c6\u0003J\u000f\u0010L\u001a\b\u0012\u0004\u0012\u00020\u000f0\u0003H\u00c6\u0003J\u000f\u0010M\u001a\b\u0012\u0004\u0012\u00020\u00110\u0003H\u00c6\u0003J\u0088\u0002\u0010N\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u000e\b\u0002\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u000e\b\u0002\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00070\u00032\u000e\b\u0002\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\u00032\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\n2\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\n2\u000e\b\u0002\u0010\f\u001a\b\u0012\u0004\u0012\u00020\r0\u00032\u000e\b\u0002\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u000f0\u00032\u000e\b\u0002\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00110\u00032\b\b\u0002\u0010\u0012\u001a\u00020\u00132\n\b\u0002\u0010\u0014\u001a\u0004\u0018\u00010\u00152\b\b\u0002\u0010\u0016\u001a\u00020\u00172\b\b\u0002\u0010\u0018\u001a\u00020\u00172\n\b\u0002\u0010\u0019\u001a\u0004\u0018\u00010\u00132\b\b\u0002\u0010\u001a\u001a\u00020\u00132\b\b\u0002\u0010\u001b\u001a\u00020\u00172\b\b\u0002\u0010\u001c\u001a\u00020\u00172\b\b\u0002\u0010\u001d\u001a\u00020\u001e2\b\b\u0002\u0010\u001f\u001a\u00020\u00172\b\b\u0002\u0010 \u001a\u00020\u0017H\u00c6\u0001\u00a2\u0006\u0002\u0010OJ\u0013\u0010P\u001a\u00020\u00172\b\u0010Q\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010R\u001a\u00020\u001eH\u00d6\u0001J\t\u0010S\u001a\u00020\u0013H\u00d6\u0001R\u0011\u0010\u001d\u001a\u00020\u001e\u00a2\u0006\b\n\u0000\u001a\u0004\b\"\u0010#R\u0011\u0010\u001f\u001a\u00020\u0017\u00a2\u0006\b\n\u0000\u001a\u0004\b$\u0010%R\u0011\u0010\u0012\u001a\u00020\u0013\u00a2\u0006\b\n\u0000\u001a\u0004\b&\u0010\'R\u0011\u0010\u0018\u001a\u00020\u0017\u00a2\u0006\b\n\u0000\u001a\u0004\b(\u0010%R\u0011\u0010\u001a\u001a\u00020\u0013\u00a2\u0006\b\n\u0000\u001a\u0004\b)\u0010\'R\u0015\u0010\u0014\u001a\u0004\u0018\u00010\u0015\u00a2\u0006\n\n\u0002\u0010,\u001a\u0004\b*\u0010+R\u0013\u0010\u0019\u001a\u0004\u0018\u00010\u0013\u00a2\u0006\b\n\u0000\u001a\u0004\b-\u0010\'R\u0013\u0010\u000b\u001a\u0004\u0018\u00010\n\u00a2\u0006\b\n\u0000\u001a\u0004\b.\u0010/R\u0017\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b0\u00101R\u0017\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b2\u00101R\u0011\u0010\u001c\u001a\u00020\u0017\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010%R\u0011\u0010\u0016\u001a\u00020\u0017\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010%R\u0011\u0010\u001b\u001a\u00020\u0017\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010%R\u0017\u0010\f\u001a\b\u0012\u0004\u0012\u00020\r0\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b3\u00101R\u0013\u0010\t\u001a\u0004\u0018\u00010\n\u00a2\u0006\b\n\u0000\u001a\u0004\b4\u0010/R\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b5\u00101R\u0017\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00070\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b6\u00101R\u0017\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00110\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b7\u00101R\u0017\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u000f0\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b8\u00101R\u0011\u0010 \u001a\u00020\u0017\u00a2\u0006\b\n\u0000\u001a\u0004\b9\u0010%\u00a8\u0006T"}, d2 = {"Lcom/bittick/ui/trading/TradingUiState;", "", "spotOpportunities", "", "Lcom/bittick/ui/trading/TradingOpportunityItem;", "futuresOpportunities", "spotPositions", "Lcom/bittick/network/BotPosition;", "futuresPositions", "spotBotStatus", "Lcom/bittick/network/BotStatusItem;", "futuresBotStatus", "klines", "Lcom/bittick/network/Kline;", "zones", "Lcom/bittick/network/ChartZone;", "tradingZones", "Lcom/bittick/network/TradingZone;", "chartInterval", "", "currentPrice", "", "isLoading", "", "chartLoading", "error", "chartStatus", "isPremium", "isFreeTier", "botNumber", "", "chartExpanded", "zonesVisible", "(Ljava/util/List;Ljava/util/List;Ljava/util/List;Ljava/util/List;Lcom/bittick/network/BotStatusItem;Lcom/bittick/network/BotStatusItem;Ljava/util/List;Ljava/util/List;Ljava/util/List;Ljava/lang/String;Ljava/lang/Double;ZZLjava/lang/String;Ljava/lang/String;ZZIZZ)V", "getBotNumber", "()I", "getChartExpanded", "()Z", "getChartInterval", "()Ljava/lang/String;", "getChartLoading", "getChartStatus", "getCurrentPrice", "()Ljava/lang/Double;", "Ljava/lang/Double;", "getError", "getFuturesBotStatus", "()Lcom/bittick/network/BotStatusItem;", "getFuturesOpportunities", "()Ljava/util/List;", "getFuturesPositions", "getKlines", "getSpotBotStatus", "getSpotOpportunities", "getSpotPositions", "getTradingZones", "getZones", "getZonesVisible", "component1", "component10", "component11", "component12", "component13", "component14", "component15", "component16", "component17", "component18", "component19", "component2", "component20", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "(Ljava/util/List;Ljava/util/List;Ljava/util/List;Ljava/util/List;Lcom/bittick/network/BotStatusItem;Lcom/bittick/network/BotStatusItem;Ljava/util/List;Ljava/util/List;Ljava/util/List;Ljava/lang/String;Ljava/lang/Double;ZZLjava/lang/String;Ljava/lang/String;ZZIZZ)Lcom/bittick/ui/trading/TradingUiState;", "equals", "other", "hashCode", "toString", "app_debug"})
public final class TradingUiState {
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.bittick.ui.trading.TradingOpportunityItem> spotOpportunities = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.bittick.ui.trading.TradingOpportunityItem> futuresOpportunities = null;
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
    private final java.util.List<com.bittick.network.TradingZone> tradingZones = null;
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
    private final int botNumber = 0;
    private final boolean chartExpanded = false;
    private final boolean zonesVisible = false;
    
    public TradingUiState(@org.jetbrains.annotations.NotNull()
    java.util.List<com.bittick.ui.trading.TradingOpportunityItem> spotOpportunities, @org.jetbrains.annotations.NotNull()
    java.util.List<com.bittick.ui.trading.TradingOpportunityItem> futuresOpportunities, @org.jetbrains.annotations.NotNull()
    java.util.List<com.bittick.network.BotPosition> spotPositions, @org.jetbrains.annotations.NotNull()
    java.util.List<com.bittick.network.BotPosition> futuresPositions, @org.jetbrains.annotations.Nullable()
    com.bittick.network.BotStatusItem spotBotStatus, @org.jetbrains.annotations.Nullable()
    com.bittick.network.BotStatusItem futuresBotStatus, @org.jetbrains.annotations.NotNull()
    java.util.List<com.bittick.network.Kline> klines, @org.jetbrains.annotations.NotNull()
    java.util.List<com.bittick.network.ChartZone> zones, @org.jetbrains.annotations.NotNull()
    java.util.List<com.bittick.network.TradingZone> tradingZones, @org.jetbrains.annotations.NotNull()
    java.lang.String chartInterval, @org.jetbrains.annotations.Nullable()
    java.lang.Double currentPrice, boolean isLoading, boolean chartLoading, @org.jetbrains.annotations.Nullable()
    java.lang.String error, @org.jetbrains.annotations.NotNull()
    java.lang.String chartStatus, boolean isPremium, boolean isFreeTier, int botNumber, boolean chartExpanded, boolean zonesVisible) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.bittick.ui.trading.TradingOpportunityItem> getSpotOpportunities() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.bittick.ui.trading.TradingOpportunityItem> getFuturesOpportunities() {
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
    public final java.util.List<com.bittick.network.TradingZone> getTradingZones() {
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
    
    public final int getBotNumber() {
        return 0;
    }
    
    public final boolean getChartExpanded() {
        return false;
    }
    
    public final boolean getZonesVisible() {
        return false;
    }
    
    public TradingUiState() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.bittick.ui.trading.TradingOpportunityItem> component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component10() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Double component11() {
        return null;
    }
    
    public final boolean component12() {
        return false;
    }
    
    public final boolean component13() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component14() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component15() {
        return null;
    }
    
    public final boolean component16() {
        return false;
    }
    
    public final boolean component17() {
        return false;
    }
    
    public final int component18() {
        return 0;
    }
    
    public final boolean component19() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.bittick.ui.trading.TradingOpportunityItem> component2() {
        return null;
    }
    
    public final boolean component20() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.bittick.network.BotPosition> component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.bittick.network.BotPosition> component4() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.bittick.network.BotStatusItem component5() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.bittick.network.BotStatusItem component6() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.bittick.network.Kline> component7() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.bittick.network.ChartZone> component8() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.bittick.network.TradingZone> component9() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.bittick.ui.trading.TradingUiState copy(@org.jetbrains.annotations.NotNull()
    java.util.List<com.bittick.ui.trading.TradingOpportunityItem> spotOpportunities, @org.jetbrains.annotations.NotNull()
    java.util.List<com.bittick.ui.trading.TradingOpportunityItem> futuresOpportunities, @org.jetbrains.annotations.NotNull()
    java.util.List<com.bittick.network.BotPosition> spotPositions, @org.jetbrains.annotations.NotNull()
    java.util.List<com.bittick.network.BotPosition> futuresPositions, @org.jetbrains.annotations.Nullable()
    com.bittick.network.BotStatusItem spotBotStatus, @org.jetbrains.annotations.Nullable()
    com.bittick.network.BotStatusItem futuresBotStatus, @org.jetbrains.annotations.NotNull()
    java.util.List<com.bittick.network.Kline> klines, @org.jetbrains.annotations.NotNull()
    java.util.List<com.bittick.network.ChartZone> zones, @org.jetbrains.annotations.NotNull()
    java.util.List<com.bittick.network.TradingZone> tradingZones, @org.jetbrains.annotations.NotNull()
    java.lang.String chartInterval, @org.jetbrains.annotations.Nullable()
    java.lang.Double currentPrice, boolean isLoading, boolean chartLoading, @org.jetbrains.annotations.Nullable()
    java.lang.String error, @org.jetbrains.annotations.NotNull()
    java.lang.String chartStatus, boolean isPremium, boolean isFreeTier, int botNumber, boolean chartExpanded, boolean zonesVisible) {
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