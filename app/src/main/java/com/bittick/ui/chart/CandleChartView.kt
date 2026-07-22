package com.bittick.ui.chart

import android.util.Log
import android.view.View
import android.webkit.ConsoleMessage
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.bittick.network.ChartZone
import com.bittick.network.Kline
import com.bittick.network.TradingZone
import org.json.JSONArray
import org.json.JSONObject

private const val TAG = "ChartWV"

class ChartBridge {
    @JavascriptInterface
    fun onChartEvent(type: String, msg: String) {
        Log.d(TAG, "JS event: $type -> $msg")
    }
}

class ChartPendingState {
    var pageLoaded = false
    var pendingData: String? = null
    var pendingZones: String? = null
    var pendingTradingZones: String? = null
}

@Composable
fun CandleChartView(
    klines: List<Kline>,
    zones: List<ChartZone> = emptyList(),
    tradingZones: List<TradingZone> = emptyList(),
    zonesVisible: Boolean = true,
    modifier: Modifier = Modifier,
    onChartLog: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val bridge = remember { ChartBridge() }
    val pending = remember { ChartPendingState() }

    val webView = remember {
        WebView(context).apply {
            addJavascriptInterface(bridge, "AndroidBridge")

            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    pending.pageLoaded = true
                    Log.d(TAG, "onPageFinished: $url")
                    onChartLog("pagina cargada")
                    
                    // Forzar resize del chart tras layout Android completo
                    view?.evaluateJavascript(
                        "if(window.chart){var e=document.getElementById('chart');chart.resize(e.clientWidth,e.clientHeight)}"
                    ) { _ -> }
                    
                    pending.pendingData?.let {
                        view?.evaluateJavascript(it, null)
                        pending.pendingData = null
                        Log.d(TAG, "enviando data pendiente")
                    }
                    pending.pendingZones?.let {
                        view?.evaluateJavascript(it, null)
                        pending.pendingZones = null
                        Log.d(TAG, "enviando zonas pendientes")
                    }
                    pending.pendingTradingZones?.let {
                        view?.evaluateJavascript(it, null)
                        pending.pendingTradingZones = null
                        Log.d(TAG, "enviando trading zones pendientes")
                    }
                }
                override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, url: String?) {
                    super.onReceivedError(view, errorCode, description, url)
                    Log.e(TAG, "onReceivedError: $errorCode $description")
                    onChartLog("error pagina: $description")
                }
            }

            webChromeClient = object : WebChromeClient() {
                override fun onConsoleMessage(msg: ConsoleMessage): Boolean {
                    Log.d(TAG, "[JS] ${msg.message()} (${msg.lineNumber()}:${msg.sourceId()})")
                    return true
                }
            }

            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.allowFileAccess = true
            settings.allowFileAccessFromFileURLs = true
            settings.allowUniversalAccessFromFileURLs = true
            settings.mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

            layoutParams = android.view.ViewGroup.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT
            )

            setLayerType(View.LAYER_TYPE_HARDWARE, null)

            setOnTouchListener { v, event ->
                when (event.action) {
                    android.view.MotionEvent.ACTION_DOWN ->
                        v.parent.requestDisallowInterceptTouchEvent(true)
                    android.view.MotionEvent.ACTION_UP,
                    android.view.MotionEvent.ACTION_CANCEL ->
                        v.parent.requestDisallowInterceptTouchEvent(false)
                }
                false
            }

            loadUrl("file:///android_asset/chart.html")
        }
    }

    LaunchedEffect(webView, klines) {
        if (klines.isEmpty()) {
            Log.d(TAG, "skip sendData: klines vacio")
            return@LaunchedEffect
        }
        Log.d(TAG, "Enviando ${klines.size} velas al grafico")
        onChartLog("enviando ${klines.size} velas...")
        val js = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Default) { generateChartData(klines) }
        if (!pending.pageLoaded) {
            pending.pendingData = js
            Log.d(TAG, "pagina no cargada, data pendiente")
        } else {
            webView.evaluateJavascript(js) { result ->
                Log.d(TAG, "setData result: $result")
                if (result != null && !result.equals("null", ignoreCase = true)) {
                    onChartLog("${klines.size} velas enviadas OK")
                } else {
                    onChartLog("velas enviadas (sin respuesta JS)")
                }
            }
        }
    }

    LaunchedEffect(webView, zones, klines) {
        if (zones.isEmpty() || klines.isEmpty()) {
            if (zones.isEmpty()) Log.d(TAG, "skip sendZones: zonas vacio")
            return@LaunchedEffect
        }
        Log.d(TAG, "Enviando ${zones.size} zonas al grafico")
        onChartLog("enviando ${zones.size} zonas...")
        val js = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Default) { generateZonesData(zones, klines) }
        if (!pending.pageLoaded) {
            pending.pendingZones = js
            Log.d(TAG, "pagina no cargada, zonas pendientes")
        } else {
            webView.evaluateJavascript(js) { result ->
                Log.d(TAG, "setZones result: $result")
                if (result != null && !result.equals("null", ignoreCase = true)) {
                    onChartLog("${zones.size} zonas enviadas OK")
                } else {
                    onChartLog("zonas enviadas (sin respuesta JS)")
                }
            }
        }
    }

    LaunchedEffect(webView, tradingZones, klines, zonesVisible) {
        if (!pending.pageLoaded) return@LaunchedEffect
        if (!zonesVisible || tradingZones.isEmpty() || klines.isEmpty()) {
            webView.evaluateJavascript("clearTradingZones()") {}
            return@LaunchedEffect
        }
        Log.d(TAG, "Enviando ${tradingZones.size} trading zones al grafico")
        val js = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Default) { generateTradingZonesData(tradingZones, klines) }
        webView.evaluateJavascript(js) { result ->
            Log.d(TAG, "setTradingZones result: $result")
        }
    }

    AndroidView(
        factory = { webView },
        modifier = modifier.fillMaxSize()
    )
}

internal fun generateChartData(klines: List<Kline>): String {
    val data = JSONArray()
    for (k in klines) {
        data.put(JSONObject().apply {
            put("time", k.openTime / 1000)
            put("open", k.open)
            put("high", k.high)
            put("low", k.low)
            put("close", k.close)
        })
    }
    return "setData($data)"
}

internal fun generateZonesData(zones: List<ChartZone>, klines: List<Kline>): String {
    val zonesJson = JSONArray()
    for (z in zones) {
        zonesJson.put(JSONObject().apply {
            put("startPrice", z.startPrice)
            put("endPrice", z.endPrice)
            put("midPrice", z.midPrice)
            put("strength", z.strength)
            put("zoneType", z.zoneType)
            put("label", z.label)
        })
    }

    val klinesJson = JSONArray()
    for (k in klines) {
        klinesJson.put(JSONObject().apply {
            put("time", k.openTime / 1000)
            put("open", k.open)
            put("high", k.high)
            put("low", k.low)
            put("close", k.close)
        })
    }

    return "setZones($zonesJson, $klinesJson)"
}

internal fun generateTradingZonesData(tradingZones: List<TradingZone>, klines: List<Kline>): String {
    val tzJson = JSONArray()
    for (tz in tradingZones) {
        tzJson.put(JSONObject().apply {
            put("date", tz.date)
            put("type", tz.type)
            put("startPrice", tz.start_price)
            put("endPrice", tz.end_price)
            put("color", tz.color)
        })
    }
    return "setTradingZones($tzJson)"
}
