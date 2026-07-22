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
import com.bittick.network.TradingZone
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
    val tradingZones: List<TradingZone> = emptyList(),
    val chartInterval: String = "1h",
    val currentPrice: Double? = null,
    val isLoading: Boolean = false,
    val chartLoading: Boolean = false,
    val error: String? = null,
    val chartStatus: String = "iniciando...",
    val isPremium: Boolean = false,
    val isFreeTier: Boolean = false,
    val botNumber: Int = 0,
    val chartExpanded: Boolean = false,
    val zonesVisible: Boolean = true
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
                _state.value = _state.value.copy(isFreeTier = isFreeTier)
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
                    }
                }
            }
            val inscriptionId = prefs.getSelectedInscriptionId()
            val botStatusResponse = api.getTradingBotStatus(walletAddress = addr, inscriptionId = inscriptionId)
            if (botStatusResponse.isSuccessful || botStatusResponse.code() == 300) {
                val botStatusData = botStatusResponse.parsedBody<BotStatusResponse>()?.data
                _state.value = _state.value.copy(
                    spotBotStatus = botStatusData?.spot ?: _state.value.spotBotStatus,
                    futuresBotStatus = botStatusData?.futures ?: _state.value.futuresBotStatus
                )
            }
            val posResponse = api.getTradingPositions(walletAddress = addr)
            if (posResponse.isSuccessful || posResponse.code() == 300) {
                val allPositions = posResponse.parsedBody<PositionsResponse>()?.data ?: emptyList()
                _state.value = _state.value.copy(
                    spotPositions = allPositions.filter { it.bot_type == "spot" } + _state.value.spotPositions.filter { it.status == "closed" }.take(5),
                    futuresPositions = allPositions.filter { it.bot_type == "futures" } + _state.value.futuresPositions.filter { it.status == "closed" }.take(5)
                )
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
                val closedPosResponse = api.getTradingPositions(walletAddress = addr, status = "closed")
                val inscriptionId = prefs.getSelectedInscriptionId()
                val botStatusResponse = api.getTradingBotStatus(walletAddress = addr, inscriptionId = inscriptionId)
                val tickerResponse = api.getTicker()

                val isFreeTier = oppResponse.code() == 300

                val opportunities = if (oppResponse.isSuccessful || isFreeTier) {
                    (oppResponse.parsedBody<TradingOpportunitiesResponse>()?.data ?: emptyList()).map { it.toItem() }.filter { it.score >= 5 && it.confidence >= 5 }
                } else emptyList()

                val allPositions = if (posResponse.isSuccessful || posResponse.code() == 300) {
                    posResponse.parsedBody<PositionsResponse>()?.data ?: emptyList()
                } else emptyList()

                val allClosedPositions = if (closedPosResponse.isSuccessful || closedPosResponse.code() == 300) {
                    closedPosResponse.parsedBody<PositionsResponse>()?.data ?: emptyList()
                } else emptyList()

                val spotPos = allPositions.filter { it.bot_type == "spot" }
                val futuresPos = allPositions.filter { it.bot_type == "futures" }
                val spotClosed = allClosedPositions.filter { it.bot_type == "spot" }.take(5)
                val futuresClosed = allClosedPositions.filter { it.bot_type == "futures" }.take(5)

                val botStatusData = if (botStatusResponse.isSuccessful || botStatusResponse.code() == 300) {
                    botStatusResponse.parsedBody<BotStatusResponse>()?.data
                } else null
                val spotStatus = botStatusData?.spot
                val futuresStatus = botStatusData?.futures

                val currentPrice = if (tickerResponse.isSuccessful && tickerResponse.body()?.exito == true) {
                    tickerResponse.body()!!.data?.price
                } else null

                Log.d("TradingVM", "loadAll() addr=$addr | oppCode=${oppResponse.code()} | posCode=${posResponse.code()} | closedCode=${closedPosResponse.code()} | botCode=${botStatusResponse.code()}")
                Log.d("TradingVM", "loadAll() spotPos=${spotPos.size} futuresPos=${futuresPos.size} spotClosed=${spotClosed.size} futuresClosed=${futuresClosed.size} | spotEnabled=${spotStatus?.enabled} futuresEnabled=${futuresStatus?.enabled} | isFreeTier=$isFreeTier")

                val botNum = prefs.getBotNumber() ?: 0

                _state.value = _state.value.copy(
                    opportunities = opportunities,
                    spotPositions = spotPos + spotClosed,
                    futuresPositions = futuresPos + futuresClosed,
                    spotBotStatus = spotStatus,
                    futuresBotStatus = futuresStatus,
                    isLoading = false,
                    isFreeTier = isFreeTier,
                    isPremium = !isFreeTier && addr != null,
                    botNumber = botNum,
                    currentPrice = currentPrice
                )
                updateLastCreatedAt(opportunities)

                loadTradingZones()
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.message ?: "Error de conexion")
            }
        }
    }

    fun refreshPremiumStatus() {
        viewModelScope.launch {
            val addr = getWalletAddress()
            val oppResponse = api.getTradingOpportunities(walletAddress = addr)
            val isFreeTier = oppResponse.code() == 300
            _state.value = _state.value.copy(
                isFreeTier = isFreeTier,
                isPremium = !isFreeTier && addr != null
            )
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
                    loadAutoZones(interval)
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
            viewModelScope.launch { loadTradingZones() }
        }
    }

    fun toggleChartExpanded() {
        _state.value = _state.value.copy(chartExpanded = !_state.value.chartExpanded)
    }

    fun toggleZonesVisible() {
        _state.value = _state.value.copy(zonesVisible = !_state.value.zonesVisible)
    }

    private suspend fun loadAutoZones(interval: String) {
        try {
            _state.value = _state.value.copy(chartStatus = "cargando zonas...")
            val response = api.getZones(interval = interval, limit = 500)
            if (response.isSuccessful && response.body()?.exito == true) {
                val zones = response.body()!!.data?.zones ?: emptyList()
                _state.value = _state.value.copy(zones = zones)
            }
        } catch (e: Exception) {
            Log.e("TradingVM", "loadAutoZones error", e)
        }
    }

    private suspend fun loadTradingZones() {
        try {
            val price = _state.value.currentPrice
            val tzResponse = api.getTradingZones(price = price)
            if (tzResponse.isSuccessful && tzResponse.body()?.exito == true) {
                val tz = tzResponse.body()!!.data ?: emptyList()
                _state.value = _state.value.copy(
                    tradingZones = tz,
                    chartStatus = "${_state.value.klines.size} velas, ${_state.value.zones.size} zonas auto, ${tz.size} zonas TA"
                )
            }
        } catch (e: Exception) {
            Log.e("TradingVM", "loadTradingZones error", e)
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

    fun closePosition(positionId: Int) {
        viewModelScope.launch {
            try {
                val response = api.cancelTradingPosition(positionId)
                if (response.isSuccessful) {
                    val body = response.body()?.data
                    val closedAt = java.time.Instant.now().toString()
                    val closedPnl = try {
                        val obj = org.json.JSONObject(body.toString())
                        Pair(obj.optDouble("pnl", 0.0), obj.optDouble("pnl_percent", 0.0))
                    } catch (_: Exception) { Pair(0.0, 0.0) }
                    val closedPrice = try {
                        org.json.JSONObject(body.toString()).optDouble("current_price", 0.0)
                    } catch (_: Exception) { null }
                    _state.value = _state.value.copy(
                        spotPositions = _state.value.spotPositions.map {
                            if (it.id == positionId) it.copy(
                                status = "closed",
                                closed_at = closedAt,
                                current_price = closedPrice ?: it.current_price,
                                pnl = closedPnl.first,
                                pnl_percent = closedPnl.second,
                                close_reason = "manual"
                            ) else it
                        },
                        futuresPositions = _state.value.futuresPositions.map {
                            if (it.id == positionId) it.copy(
                                status = "closed",
                                closed_at = closedAt,
                                current_price = closedPrice ?: it.current_price,
                                pnl = closedPnl.first,
                                pnl_percent = closedPnl.second,
                                close_reason = "manual"
                            ) else it
                        }
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message ?: "Error al cerrar posicion")
            }
        }
    }

    fun dismissPosition(positionId: Int) {
        _state.value = _state.value.copy(
            spotPositions = _state.value.spotPositions.filter { it.id != positionId },
            futuresPositions = _state.value.futuresPositions.filter { it.id != positionId }
        )
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
