package com.bittick.network

// API Response wrappers
data class ApiResponse<T>(val exito: Boolean, val data: T?, val message: String?, val error: String?)

data class TradingOpportunitiesResponse(val exito: Boolean, val data: List<TradingOpportunity>)
data class PositionsResponse(val exito: Boolean, val data: List<BotPosition>)
data class BotStatusResponse(val exito: Boolean, val data: BotStatusData?)
data class BotStatusData(val spot: BotStatusItem?, val futures: BotStatusItem?)
data class DeleteOpportunityResponse(val exito: Boolean, val message: String?)
data class CancelPositionResponse(val exito: Boolean, val message: String?, val data: Any?)
data class KlinesResponse(val exito: Boolean, val data: List<Kline>)
data class TickerResponse(val exito: Boolean, val data: Ticker?)

// Trading
data class TradingOpportunity(
    val id: Int,
    val asset: String,
    val strategy_type: String,
    val price: Double,
    val entry_zone: String?,
    val target: Double?,
    val stop_loss: Double?,
    val score: Double,
    val confidence: Double,
    val ai_explanation: String?,
    val factors: String?,
    val risks: String?,
    val signals: String?,
    val horizonte: String?,
    val status: String?,
    val created_at: String?
)

data class BotPosition(
    val id: Int,
    val bot_type: String,
    val strategy_type: String,
    val asset: String,
    val entry_price: Double,
    val current_price: Double?,
    val quantity: Double,
    val order_id: String?,
    val target: Double?,
    val stop_loss: Double?,
    val score: Double,
    val confidence: Double,
    val ai_explanation: String?,
    val horizonte: String?,
    val usd_amount: Double?,
    val status: String?,
    val pnl: Double,
    val pnl_percent: Double
)

data class BotStatusItem(
    val type: String,
    val enabled: Boolean,
    val maxPositions: Int,
    val positionSizeUsdt: Double,
    val minConfidence: Int,
    val openPositions: Int,
    val totalPnl: Double,
    val balance: Balance?
)

data class Balance(
    val total: Double,
    val available: Double
)

// Chart
data class Kline(
    val openTime: Long,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val volume: Double,
    val closeTime: Long
)

data class Ticker(
    val symbol: String,
    val price: Double,
    val priceChange: Double?,
    val priceChangePercent: Double?,
    val highPrice: Double?,
    val lowPrice: Double?,
    val volume: Double?,
    val quoteVolume: Double?
)

// Chart Zones (Avizor strategy)
data class ZonesResponse(val exito: Boolean, val data: ZonesData?)
data class ZonesData(val zones: List<ChartZone>, val atr: Double)
data class ChartZone(
    val startPrice: Double,
    val endPrice: Double,
    val midPrice: Double,
    val strength: Int,
    val zoneType: String,
    val type: String,
    val label: String
)
