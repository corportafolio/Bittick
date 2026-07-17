package com.bittick.network

data class ApiResponse<T>(val exito: Boolean, val data: T?, val message: String?, val error: String?)

data class TradingOpportunitiesResponse(val exito: Boolean, val data: List<TradingOpportunity>)
data class PositionsResponse(val exito: Boolean, val data: List<BotPosition>)
data class BotStatusResponse(val exito: Boolean, val data: BotStatusData?)
data class BotStatusData(val spot: BotStatusItem?, val futures: BotStatusItem?)
data class DeleteOpportunityResponse(val exito: Boolean, val message: String?)
data class CancelPositionResponse(val exito: Boolean, val message: String?, val data: Any?)
data class KlinesResponse(val exito: Boolean, val data: List<Kline>)
data class TickerResponse(val exito: Boolean, val data: Ticker?)

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

// Wallet & Auth
data class NonceResponse(val exito: Boolean, val data: NonceData?)
data class NonceData(val nonce: String, val message: String)

data class VerifyWalletRequest(val address: String, val signature: String? = null, val nonce: String? = null)

data class VerifyWalletResponse(
    val exito: Boolean,
    val data: VerifyWalletData?,
    val error: String?
)

data class VerifyWalletData(
    val verified: Boolean,
    val inscriptions: List<InscriptionInfo>?,
    val count: Int?,
    val selectedInscriptionId: String?,
    val selectedBotNum: Int?,
    val tier: String?,
    val botImageUrl: String?,
    val message: String?
)

data class InscriptionInfo(
    val num: Int,
    val inscriptionId: String,
    val tier: String,
    val botImageUrl: String?,
    val isSelected: Boolean? = null,
    val selected: Boolean? = null
)

data class SelectInscriptionRequest(val inscriptionId: String)

data class SelectInscriptionResponse(
    val exito: Boolean,
    val data: SelectInscriptionData?,
    val error: String?
)

data class SelectInscriptionData(
    val selectedInscriptionId: String,
    val selectedBotNum: Int,
    val tier: String,
    val botImageUrl: String
)

data class WalletInscriptionsResponse(
    val exito: Boolean,
    val data: WalletInscriptionsData?
)

data class WalletInscriptionsData(
    val inscriptions: List<InscriptionInfo>,
    val selectedInscriptionId: String?,
    val selectedBotNum: Int?,
    val tier: String?
)

data class FetchInscriptionsResponse(
    val exito: Boolean,
    val data: FetchInscriptionsData?,
    val error: String?
)

data class FetchInscriptionsData(
    val inscriptions: List<InscriptionInfo>,
    val count: Int,
    val error: String?
)

// Preferences
data class InscriptionPreferences(
    val inscription_id: String,
    val address: String,
    val spot_enabled: Boolean = true,
    val futures_enabled: Boolean = true,
    val spot_position_size: Double = 10.0,
    val futures_position_size: Double = 10.0,
    val spot_max_positions: Int = 5,
    val futures_max_positions: Int = 5,
    val spot_min_score: Int = 6,
    val futures_min_score: Int = 7
)

data class BotPreferencesResponse(val exito: Boolean, val data: InscriptionPreferences?)

data class SavePreferencesRequest(
    val inscriptionId: String,
    val address: String,
    val spot_enabled: Boolean,
    val futures_enabled: Boolean,
    val spot_position_size: Double,
    val futures_position_size: Double,
    val spot_max_positions: Int,
    val futures_max_positions: Int,
    val spot_min_score: Int,
    val futures_min_score: Int
)

data class SavePreferencesResponse(val exito: Boolean, val message: String?)
