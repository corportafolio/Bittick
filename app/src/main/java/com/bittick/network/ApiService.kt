package com.bittick.network

import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("api/trading/opportunities")
    suspend fun getTradingOpportunities(
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0,
        @Query("since") since: String? = null
    ): Response<TradingOpportunitiesResponse>

    @DELETE("api/trading/opportunities/{id}")
    suspend fun deleteTradingOpportunity(@Path("id") id: Int): Response<DeleteOpportunityResponse>

    @GET("api/trading/positions")
    suspend fun getTradingPositions(
        @Query("type") type: String? = null,
        @Query("status") status: String? = "open"
    ): Response<PositionsResponse>

    @POST("api/trading/positions/{id}/cancel")
    suspend fun cancelTradingPosition(@Path("id") id: Int): Response<CancelPositionResponse>

    @GET("api/trading/bot/status")
    suspend fun getTradingBotStatus(): Response<BotStatusResponse>

    @GET("api/chart/klines")
    suspend fun getKlines(
        @Query("interval") interval: String = "1h",
        @Query("limit") limit: Int = 200
    ): Response<KlinesResponse>

    @GET("api/chart/ticker")
    suspend fun getTicker(): Response<TickerResponse>

    @GET("api/chart/zones")
    suspend fun getZones(
        @Query("interval") interval: String = "1h",
        @Query("limit") limit: Int = 200
    ): Response<ZonesResponse>
}
