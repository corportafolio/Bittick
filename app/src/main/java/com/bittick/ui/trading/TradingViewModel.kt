package com.bittick.ui.trading

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bittick.data.ai.NotificationHelper
import com.bittick.data.preferences.BittickPreferences
import com.bittick.network.ApiService
import com.bittick.network.BotPosition
import com.bittick.network.BotStatusItem
import com.bittick.network.ChartZone
import com.bittick.network.Kline
import com.bittick.network.TradingOpportunity
import com.bittick.network.TradingOpportunitiesResponse
import com.bittick.network.PositionsResponse
import com.bittick.network.BotStatusResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

data class TradingUiState(
    val opportunities: List<TradingOpportunityItem> = emptyList(),
    val spotPositions: List<BotPosition> = emptyList(),
    val futuresPositions: List<BotPosition> = emptyList(),
    val spotBotStatus: BotStatusItem? = null,
    val futuresBotStatus: BotStatusItem? = null,
    val klines: List<Kline> = emptyList(),
    val zones: List<ChartZone> = emptyList(),
    val chartInterval: String = "1h",
    val currentPrice: Double? = null,
    val isLoading: Boolean = false,
    val chartLoading: Boolean = false,
    val error: String? = null,
    val chartStatus: String = "iniciando...",
    val isPremium: Boolean = false,
    val isFreeTier: Boolean = false
)

data class TradingOpportunityItem(
    val id: Int,
    val type: String,
    val asset: String,
    val price: String,
    val entryZone: String,
    val target: String,
    val stopLoss: String,
    val score: Int,
    val confidence: Int,
    val explanation: String,
    val factors: List<String>,
    val risks: List<String>,
    val signals: Map<String, String>,
    val createdAt: String
)

@HiltViewModel
class TradingViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val api: ApiService,
    private val notifier: NotificationHelper,
    private val prefs: BittickPreferences
) : ViewModel() {

    private val _state = MutableStateFlow(TradingUiState())
    val state: StateFlow<TradingUiState> = _state
    private val announcedOpportunityIds = mutableSetOf<Int>()
    private var pollingJob: Job? = null
    private var klinesPollingJob: Job? = null
    private var tickerPollingJob: Job? = null
    private var lastCreatedAt: String? = null
    private val gson = Gson()

    private inline fun <reified T> Response<T>.parsedBody(): T? {
        if (isSuccessful) return body()
        val raw = errorBody()?.string() ?: return null
        return try { gson.fromJson(raw, T::class.java) } catch (_: Exception) { null }
    }

    init {
        loadAll()
        startPolling()
        startKlinesPolling()
        startTickerPolling()
    }

    override fun onCleared() {
        super.onCleared()
        stopPolling()
        stopKlinesPolling()
        stopTickerPolling()
        notifier.destroy()
    }

    private fun startPolling() {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (isActive) {
                delay(60_000L)
                fetchNewOpportunities()
            }
        }
    }

    private fun stopPolling() {
        pollingJob?.cancel()
        pollingJob = null
    }

    private fun startKlinesPolling(interval: String = _state.value.chartInterval) {
        klinesPollingJob?.cancel()
        klinesPollingJob = viewModelScope.launch {
            while (isActive) {
                loadKlines(interval)
                delay(60_000L)
            }
        }
    }

    private fun stopKlinesPolling() {
        klinesPollingJob?.cancel()
        klinesPollingJob = null
    }

    private fun startTickerPolling() {
        tickerPollingJob?.cancel()
        tickerPollingJob = viewModelScope.launch {
            while (isActive) {
                loadTicker()
                delay(60_000L)
            }
        }
    }

    private fun stopTickerPolling() {
        tickerPollingJob?.cancel()
        tickerPollingJob = null
    }

    private suspend fun fetchNewOpportunities() {
        try {
            val addr = getWalletAddress()
            val since = if (_state.value.opportunities.isEmpty()) null else lastCreatedAt
            val response = api.getTradingOpportunities(walletAddress = addr, limit = 50, offset = 0, since = since)
            if (response.isSuccessful || response.code() == 300) {
                val isFreeTier = response.code() == 300
                if (isFreeTier) {
                    _state.value = _state.value.copy(isFreeTier = true)
                }
                val allItems = response.parsedBody<TradingOpportunitiesResponse>()?.data?.map { it.toItem() } ?: emptyList()
                val newItems = allItems.filter { it.score >= 5 && it.confidence >= 5 }
                if (newItems.isNotEmpty()) {
                    val existingIds = _state.value.opportunities.map { it.id }.toSet()
                    val trulyNew = newItems.filter { it.id !in existingIds }
                    if (trulyNew.isNotEmpty()) {
                        _state.value = _state.value.copy(
                            opportunities = (trulyNew + _state.value.opportunities).sortedByDescending { it.id }
                        )
                        updateLastCreatedAt(trulyNew)
                        if (!_state.value.isFreeTier) {
                            announceOpportunities(trulyNew)
                        }
                    }
                }
            }
        } catch (_: Exception) { }
    }

    private fun updateLastCreatedAt(items: List<TradingOpportunityItem>) {
        val maxCreated = items.maxByOrNull { it.createdAt }?.createdAt
        if (maxCreated != null && (lastCreatedAt == null || maxCreated > lastCreatedAt!!)) {
            lastCreatedAt = maxCreated
        }
    }

    private fun getWalletAddress(): String? = prefs.getWalletAddress()

    fun loadAll() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val addr = getWalletAddress()
                val oppResponse = api.getTradingOpportunities(walletAddress = addr)
                val posResponse = api.getTradingPositions(walletAddress = addr)
                val botStatusResponse = api.getTradingBotStatus(walletAddress = addr)

                val isFreeTier = oppResponse.code() == 300

                val opportunities = if (oppResponse.isSuccessful || isFreeTier) {
                    (oppResponse.parsedBody<TradingOpportunitiesResponse>()?.data ?: emptyList()).map { it.toItem() }.filter { it.score >= 5 && it.confidence >= 5 }
                } else emptyList()

                val spotPos = if (posResponse.isSuccessful || posResponse.code() == 300) {
                    (posResponse.parsedBody<PositionsResponse>()?.data ?: emptyList()).filter { it.bot_type == "spot" }
                } else emptyList()

                val futuresPos = if (posResponse.isSuccessful || posResponse.code() == 300) {
                    (posResponse.parsedBody<PositionsResponse>()?.data ?: emptyList()).filter { it.bot_type == "futures" }
                } else emptyList()

                val spotStatus = if (botStatusResponse.isSuccessful || botStatusResponse.code() == 300) {
                    botStatusResponse.parsedBody<BotStatusResponse>()?.data?.spot
                } else null

                val futuresStatus = if (botStatusResponse.isSuccessful || botStatusResponse.code() == 300) {
                    botStatusResponse.parsedBody<BotStatusResponse>()?.data?.futures
                } else null

                _state.value = _state.value.copy(
                    opportunities = opportunities,
                    spotPositions = spotPos,
                    futuresPositions = futuresPos,
                    spotBotStatus = spotStatus,
                    futuresBotStatus = futuresStatus,
                    isLoading = false,
                    isFreeTier = isFreeTier,
                    isPremium = !isFreeTier && addr != null
                )
                updateLastCreatedAt(opportunities)
                if (!isFreeTier) {
                    announceOpportunities(opportunities)
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.message ?: "Error de conexion")
            }
        }
    }

    fun loadKlines(interval: String = _state.value.chartInterval) {
        viewModelScope.launch {
            _state.value = _state.value.copy(chartLoading = true, chartStatus = "cargando velas $interval...")
            try {
                val response = api.getKlines(interval = interval, limit = 500)
                if (response.isSuccessful && response.body()?.exito == true) {
                    val klines = response.body()!!.data
                    _state.value = _state.value.copy(
                        klines = klines,
                        chartInterval = interval,
                        chartLoading = false,
                        chartStatus = "${klines.size} velas recibidas"
                    )
                    loadZones(interval, klines)
                } else {
                    Log.w("TradingVM", "loadKlines HTTP ${response.code()}: ${response.message()}")
                    val errBody = response.errorBody()?.string()
                    Log.w("TradingVM", "Error body: $errBody")
                    _state.value = _state.value.copy(
                        chartLoading = false,
                        chartStatus = "error servidor: ${response.code()}"
                    )
                }
            } catch (e: Exception) {
                Log.e("TradingVM", "loadKlines error", e)
                _state.value = _state.value.copy(
                    chartLoading = false,
                    chartStatus = "error conexion: ${e.localizedMessage}"
                )
            }
        }
    }

    fun changeChartInterval(interval: String) {
        if (interval != _state.value.chartInterval) {
            startKlinesPolling(interval)
        }
    }

    private suspend fun loadZones(interval: String, klines: List<Kline>) {
        try {
            _state.value = _state.value.copy(chartStatus = "cargando zonas...")
            val response = api.getZones(interval = interval, limit = 500)
            if (response.isSuccessful && response.body()?.exito == true) {
                val zones = response.body()!!.data?.zones ?: emptyList()
                val status = if (zones.isNotEmpty()) {
                    "${klines.size} velas, ${zones.size} zonas OK"
                } else {
                    "${klines.size} velas (sin zonas)"
                }
                _state.value = _state.value.copy(zones = zones, chartStatus = status)
            } else {
                _state.value = _state.value.copy(chartStatus = "${klines.size} velas (zonas error)")
            }
        } catch (e: Exception) {
            Log.e("TradingVM", "loadZones error", e)
            _state.value = _state.value.copy(chartStatus = "${klines.size} velas (zonas error: ${e.localizedMessage})")
        }
    }

    fun loadTicker() {
        viewModelScope.launch {
            try {
                val response = api.getTicker()
                if (response.isSuccessful && response.body()?.exito == true) {
                    _state.value = _state.value.copy(currentPrice = response.body()!!.data?.price)
                }
            } catch (_: Exception) { }
        }
    }

    fun cancelPosition(positionId: Int) {
        viewModelScope.launch {
            try {
                val response = api.cancelTradingPosition(positionId)
                if (response.isSuccessful) {
                    loadAll()
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message ?: "Error al cancelar")
            }
        }
    }

    fun deleteOpportunity(opportunityId: Int) {
        viewModelScope.launch {
            try {
                val response = api.deleteTradingOpportunity(opportunityId)
                if (response.isSuccessful && response.body()?.exito == true) {
                    _state.value = _state.value.copy(
                        opportunities = _state.value.opportunities.filter { it.id != opportunityId }
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message ?: "Error al eliminar oportunidad")
            }
        }
    }

    private fun announceOpportunities(opportunities: List<TradingOpportunityItem>) {
        val toAnnounce = opportunities.filter { opp ->
            opp.score >= 6 && opp.confidence >= 6 && opp.id !in announcedOpportunityIds
        }
        if (toAnnounce.isEmpty()) return
        viewModelScope.launch {
            for (opp in toAnnounce) {
                announcedOpportunityIds.add(opp.id)
                notifier.notifyTradingOpportunityByScore(
                    asset = opp.asset,
                    type = opp.type,
                    price = opp.price,
                    score = opp.score,
                    confidence = opp.confidence,
                    entryZone = opp.entryZone,
                    target = opp.target,
                    stopLoss = opp.stopLoss,
                    explanation = opp.explanation,
                    opportunityId = opp.id
                )
                delay(3000)
            }
        }
    }
}

fun TradingOpportunity.toItem(): TradingOpportunityItem {
    val parsedFactors = try {
        val raw = Gson().fromJson(factors, Array<String>::class.java)
        raw?.toList() ?: emptyList()
    } catch (_: Exception) { emptyList() }
    val parsedRisks = try {
        val raw = Gson().fromJson(risks, Array<String>::class.java)
        raw?.toList() ?: emptyList()
    } catch (_: Exception) { emptyList() }
    val parsedSignals = try {
        val map = Gson().fromJson(signals, Map::class.java) as? Map<String, Any>
        map?.mapValues { it.value.toString() } ?: emptyMap()
    } catch (_: Exception) { emptyMap() }
    return TradingOpportunityItem(
        id = id, type = strategy_type, asset = asset,
        price = "%.2f".format(price),
        entryZone = entry_zone ?: "-",
        target = target?.let { "%.2f".format(it) } ?: "-",
        stopLoss = stop_loss?.let { "%.2f".format(it) } ?: "-",
        score = score.toInt(), confidence = confidence.toInt(),
        explanation = ai_explanation ?: "",
        factors = parsedFactors, risks = parsedRisks, signals = parsedSignals,
        createdAt = created_at ?: ""
    )
}
