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

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000\f\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\u001a\n\u0010\u0000\u001a\u00020\u0001*\u00020\u0002\u00a8\u0006\u0003"}, d2 = {"toItem", "Lcom/bittick/ui/trading/TradingOpportunityItem;", "Lcom/bittick/network/TradingOpportunity;", "app_debug"})
public final class TradingViewModelKt {
    
    @org.jetbrains.annotations.NotNull()
    public static final com.bittick.ui.trading.TradingOpportunityItem toItem(@org.jetbrains.annotations.NotNull()
    com.bittick.network.TradingOpportunity $this$toItem) {
        return null;
    }
}