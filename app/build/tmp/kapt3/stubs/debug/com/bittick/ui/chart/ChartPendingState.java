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

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\b\u000b\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002R\u001a\u0010\u0003\u001a\u00020\u0004X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0005\u0010\u0006\"\u0004\b\u0007\u0010\bR\u001c\u0010\t\u001a\u0004\u0018\u00010\nX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\f\"\u0004\b\r\u0010\u000eR\u001c\u0010\u000f\u001a\u0004\u0018\u00010\nX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0010\u0010\f\"\u0004\b\u0011\u0010\u000eR\u001c\u0010\u0012\u001a\u0004\u0018\u00010\nX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0013\u0010\f\"\u0004\b\u0014\u0010\u000e\u00a8\u0006\u0015"}, d2 = {"Lcom/bittick/ui/chart/ChartPendingState;", "", "()V", "pageLoaded", "", "getPageLoaded", "()Z", "setPageLoaded", "(Z)V", "pendingData", "", "getPendingData", "()Ljava/lang/String;", "setPendingData", "(Ljava/lang/String;)V", "pendingTradingZones", "getPendingTradingZones", "setPendingTradingZones", "pendingZones", "getPendingZones", "setPendingZones", "app_debug"})
public final class ChartPendingState {
    private boolean pageLoaded = false;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String pendingData;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String pendingZones;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String pendingTradingZones;
    
    public ChartPendingState() {
        super();
    }
    
    public final boolean getPageLoaded() {
        return false;
    }
    
    public final void setPageLoaded(boolean p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getPendingData() {
        return null;
    }
    
    public final void setPendingData(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getPendingZones() {
        return null;
    }
    
    public final void setPendingZones(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getPendingTradingZones() {
        return null;
    }
    
    public final void setPendingTradingZones(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
}