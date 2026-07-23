package com.bittick.ui.chart;

import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.compose.runtime.Composable;
import androidx.compose.ui.Modifier;
import com.bittick.network.ChartZone;
import com.bittick.network.Kline;
import com.bittick.network.TradingZone;
import org.json.JSONArray;
import org.json.JSONObject;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0018\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u0006H\u0007\u00a8\u0006\b"}, d2 = {"Lcom/bittick/ui/chart/ChartBridge;", "", "()V", "onChartEvent", "", "type", "", "msg", "app_debug"})
public final class ChartBridge {
    
    public ChartBridge() {
        super();
    }
    
    @android.webkit.JavascriptInterface()
    public final void onChartEvent(@org.jetbrains.annotations.NotNull()
    java.lang.String type, @org.jetbrains.annotations.NotNull()
    java.lang.String msg) {
    }
}