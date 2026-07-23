package com.bittick.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface ApiService {

    @GET("api/trading/opportunities")
    suspend fun getTradingOpportunities(
        @Header("x-wallet-address") walletAddress: String? = null,
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0,
        @Query("since") since: String? = null,
        @Query("bot_type") botType: String? = null
    ): Response<TradingOpportunitiesResponse>

    @DELETE("api/trading/opportunities/{id}")
    suspend fun deleteTradingOpportunity(@Path("id") id: Int): Response<DeleteOpportunityResponse>

    @GET("api/trading/positions")
    suspend fun getTradingPositions(
        @Header("x-wallet-address") walletAddress: String? = null,
        @Query("type") type: String? = null,
        @Query("status") status: String? = "open"
    ): Response<PositionsResponse>

    @POST("api/trading/positions/{id}/cancel")
    suspend fun cancelTradingPosition(@Path("id") id: Int): Response<CancelPositionResponse>

    @GET("api/trading/bot/status")
    suspend fun getTradingBotStatus(
        @Header("x-wallet-address") walletAddress: String? = null,
        @Query("inscriptionId") inscriptionId: String? = null
    ): Response<BotStatusResponse>

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

    @GET("api/chart/trading-zones")
    suspend fun getTradingZones(
        @Query("price") price: Double? = null
    ): Response<TradingZonesResponse>

    // Auth
    @GET("api/auth/nonce")
    suspend fun getNonce(
        @Query("address") address: String
    ): Response<NonceResponse>

    @POST("api/auth/verify-wallet")
    suspend fun verifyWallet(
        @Body body: VerifyWalletRequest
    ): Response<VerifyWalletResponse>

    @GET("api/auth/verify-status")
    suspend fun verifyStatus(
        @Query("address") address: String
    ): Response<VerifyWalletResponse>

    @POST("api/auth/select-inscription")
    suspend fun selectInscription(
        @Header("x-wallet-address") address: String,
        @Body body: SelectInscriptionRequest
    ): Response<SelectInscriptionResponse>

    @GET("api/auth/wallet-inscriptions")
    suspend fun getWalletInscriptions(
        @Header("x-wallet-address") address: String
    ): Response<WalletInscriptionsResponse>

    @GET("api/auth/fetch-inscriptions")
    suspend fun fetchInscriptions(
        @Header("x-wallet-address") address: String
    ): Response<FetchInscriptionsResponse>

    // Preferences
    @GET("api/trading/preferences/{inscriptionId}")
    suspend fun getInscriptionPreferences(
        @Path("inscriptionId") inscriptionId: String
    ): Response<BotPreferencesResponse>

    @POST("api/trading/preferences")
    suspend fun saveInscriptionPreferences(
        @Body body: SavePreferencesRequest
    ): Response<SavePreferencesResponse>

    // Level configs per bot
    @GET("api/trading/strategies/levels/{inscriptionId}/{mode}")
    suspend fun getLevelConfigs(
        @Path("inscriptionId") inscriptionId: String,
        @Path("mode") mode: String
    ): Response<LevelConfigsResponse>

    @POST("api/trading/strategies/levels")
    suspend fun saveLevelConfigs(
        @Body body: LevelConfigsRequest
    ): Response<SaveLevelConfigsResponse>

    // Bot API Key (per-bot per-mode Binance credentials)
    @GET("api/trading/bot-apikey/{inscriptionId}/{mode}/raw")
    suspend fun getBotApiKey(
        @Header("x-wallet-address") walletAddress: String,
        @Path("inscriptionId") inscriptionId: String,
        @Path("mode") mode: String
    ): Response<BotApiKeyResponse>

    @POST("api/trading/bot-apikey")
    suspend fun saveBotApiKey(
        @Header("x-wallet-address") walletAddress: String,
        @Body body: BotApiKeyRequest
    ): Response<SaveBotApiKeyResponse>

    @POST("api/trading/bot-apikey/all")
    suspend fun saveAllBotApiKeys(
        @Header("x-wallet-address") walletAddress: String,
        @Body body: BotApiKeyAllRequest
    ): Response<SaveBotApiKeyResponse>

    @DELETE("api/trading/bot-apikey/{inscriptionId}/{mode}")
    suspend fun deleteBotApiKey(
        @Header("x-wallet-address") walletAddress: String,
        @Path("inscriptionId") inscriptionId: String,
        @Path("mode") mode: String
    ): Response<DeleteBotApiKeyResponse>
}

object ApiClient {
    const val BASE_URL = "https://bittick.net/"
    private const val TIMEOUT = 30L

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(TIMEOUT, TimeUnit.SECONDS)
        .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
}
