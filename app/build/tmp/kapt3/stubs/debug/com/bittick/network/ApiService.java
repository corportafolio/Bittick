package com.bittick.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.*;
import java.util.concurrent.TimeUnit;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u00d2\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J\u001e\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\u0007J2\u0010\b\u001a\b\u0012\u0004\u0012\u00020\t0\u00032\b\b\u0001\u0010\n\u001a\u00020\u000b2\b\b\u0001\u0010\f\u001a\u00020\u000b2\b\b\u0001\u0010\r\u001a\u00020\u000bH\u00a7@\u00a2\u0006\u0002\u0010\u000eJ\u001e\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00100\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\u0007J\u001e\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00120\u00032\b\b\u0001\u0010\u0013\u001a\u00020\u000bH\u00a7@\u00a2\u0006\u0002\u0010\u0014J2\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00160\u00032\b\b\u0001\u0010\n\u001a\u00020\u000b2\b\b\u0001\u0010\f\u001a\u00020\u000b2\b\b\u0001\u0010\r\u001a\u00020\u000bH\u00a7@\u00a2\u0006\u0002\u0010\u000eJ\u001e\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00180\u00032\b\b\u0001\u0010\f\u001a\u00020\u000bH\u00a7@\u00a2\u0006\u0002\u0010\u0014J(\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u001a0\u00032\b\b\u0003\u0010\u001b\u001a\u00020\u000b2\b\b\u0003\u0010\u001c\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\u001dJ(\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u001f0\u00032\b\b\u0001\u0010\f\u001a\u00020\u000b2\b\b\u0001\u0010\r\u001a\u00020\u000bH\u00a7@\u00a2\u0006\u0002\u0010 J\u001e\u0010!\u001a\b\u0012\u0004\u0012\u00020\"0\u00032\b\b\u0001\u0010\u0013\u001a\u00020\u000bH\u00a7@\u00a2\u0006\u0002\u0010\u0014J\u0014\u0010#\u001a\b\u0012\u0004\u0012\u00020$0\u0003H\u00a7@\u00a2\u0006\u0002\u0010%J,\u0010&\u001a\b\u0012\u0004\u0012\u00020\'0\u00032\n\b\u0003\u0010\n\u001a\u0004\u0018\u00010\u000b2\n\b\u0003\u0010\f\u001a\u0004\u0018\u00010\u000bH\u00a7@\u00a2\u0006\u0002\u0010 JL\u0010(\u001a\b\u0012\u0004\u0012\u00020)0\u00032\n\b\u0003\u0010\n\u001a\u0004\u0018\u00010\u000b2\b\b\u0003\u0010\u001c\u001a\u00020\u00062\b\b\u0003\u0010*\u001a\u00020\u00062\n\b\u0003\u0010+\u001a\u0004\u0018\u00010\u000b2\n\b\u0003\u0010,\u001a\u0004\u0018\u00010\u000bH\u00a7@\u00a2\u0006\u0002\u0010-J8\u0010.\u001a\b\u0012\u0004\u0012\u00020/0\u00032\n\b\u0003\u0010\n\u001a\u0004\u0018\u00010\u000b2\n\b\u0003\u00100\u001a\u0004\u0018\u00010\u000b2\n\b\u0003\u00101\u001a\u0004\u0018\u00010\u000bH\u00a7@\u00a2\u0006\u0002\u0010\u000eJ \u00102\u001a\b\u0012\u0004\u0012\u0002030\u00032\n\b\u0003\u00104\u001a\u0004\u0018\u000105H\u00a7@\u00a2\u0006\u0002\u00106J\u001e\u00107\u001a\b\u0012\u0004\u0012\u0002080\u00032\b\b\u0001\u0010\u0013\u001a\u00020\u000bH\u00a7@\u00a2\u0006\u0002\u0010\u0014J(\u00109\u001a\b\u0012\u0004\u0012\u00020:0\u00032\b\b\u0003\u0010\u001b\u001a\u00020\u000b2\b\b\u0003\u0010\u001c\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\u001dJ(\u0010;\u001a\b\u0012\u0004\u0012\u00020<0\u00032\b\b\u0001\u0010\n\u001a\u00020\u000b2\b\b\u0001\u0010=\u001a\u00020>H\u00a7@\u00a2\u0006\u0002\u0010?J\u001e\u0010@\u001a\b\u0012\u0004\u0012\u00020A0\u00032\b\b\u0001\u0010=\u001a\u00020BH\u00a7@\u00a2\u0006\u0002\u0010CJ\u001e\u0010D\u001a\b\u0012\u0004\u0012\u00020E0\u00032\b\b\u0001\u0010=\u001a\u00020FH\u00a7@\u00a2\u0006\u0002\u0010GJ(\u0010H\u001a\b\u0012\u0004\u0012\u00020I0\u00032\b\b\u0001\u0010\u0013\u001a\u00020\u000b2\b\b\u0001\u0010=\u001a\u00020JH\u00a7@\u00a2\u0006\u0002\u0010KJ\u001e\u0010L\u001a\b\u0012\u0004\u0012\u00020M0\u00032\b\b\u0001\u0010\u0013\u001a\u00020\u000bH\u00a7@\u00a2\u0006\u0002\u0010\u0014J\u001e\u0010N\u001a\b\u0012\u0004\u0012\u00020M0\u00032\b\b\u0001\u0010=\u001a\u00020OH\u00a7@\u00a2\u0006\u0002\u0010P\u00a8\u0006Q"}, d2 = {"Lcom/bittick/network/ApiService;", "", "cancelTradingPosition", "Lretrofit2/Response;", "Lcom/bittick/network/CancelPositionResponse;", "id", "", "(ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteBotApiKey", "Lcom/bittick/network/DeleteBotApiKeyResponse;", "walletAddress", "", "inscriptionId", "mode", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteTradingOpportunity", "Lcom/bittick/network/DeleteOpportunityResponse;", "fetchInscriptions", "Lcom/bittick/network/FetchInscriptionsResponse;", "address", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getBotApiKey", "Lcom/bittick/network/BotApiKeyResponse;", "getInscriptionPreferences", "Lcom/bittick/network/BotPreferencesResponse;", "getKlines", "Lcom/bittick/network/KlinesResponse;", "interval", "limit", "(Ljava/lang/String;ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getLevelConfigs", "Lcom/bittick/network/LevelConfigsResponse;", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getNonce", "Lcom/bittick/network/NonceResponse;", "getTicker", "Lcom/bittick/network/TickerResponse;", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getTradingBotStatus", "Lcom/bittick/network/BotStatusResponse;", "getTradingOpportunities", "Lcom/bittick/network/TradingOpportunitiesResponse;", "offset", "since", "botType", "(Ljava/lang/String;IILjava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getTradingPositions", "Lcom/bittick/network/PositionsResponse;", "type", "status", "getTradingZones", "Lcom/bittick/network/TradingZonesResponse;", "price", "", "(Ljava/lang/Double;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getWalletInscriptions", "Lcom/bittick/network/WalletInscriptionsResponse;", "getZones", "Lcom/bittick/network/ZonesResponse;", "saveBotApiKey", "Lcom/bittick/network/SaveBotApiKeyResponse;", "body", "Lcom/bittick/network/BotApiKeyRequest;", "(Ljava/lang/String;Lcom/bittick/network/BotApiKeyRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "saveInscriptionPreferences", "Lcom/bittick/network/SavePreferencesResponse;", "Lcom/bittick/network/SavePreferencesRequest;", "(Lcom/bittick/network/SavePreferencesRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "saveLevelConfigs", "Lcom/bittick/network/SaveLevelConfigsResponse;", "Lcom/bittick/network/LevelConfigsRequest;", "(Lcom/bittick/network/LevelConfigsRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "selectInscription", "Lcom/bittick/network/SelectInscriptionResponse;", "Lcom/bittick/network/SelectInscriptionRequest;", "(Ljava/lang/String;Lcom/bittick/network/SelectInscriptionRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "verifyStatus", "Lcom/bittick/network/VerifyWalletResponse;", "verifyWallet", "Lcom/bittick/network/VerifyWalletRequest;", "(Lcom/bittick/network/VerifyWalletRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public abstract interface ApiService {
    
    @retrofit2.http.GET(value = "api/trading/opportunities")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getTradingOpportunities(@retrofit2.http.Header(value = "x-wallet-address")
    @org.jetbrains.annotations.Nullable()
    java.lang.String walletAddress, @retrofit2.http.Query(value = "limit")
    int limit, @retrofit2.http.Query(value = "offset")
    int offset, @retrofit2.http.Query(value = "since")
    @org.jetbrains.annotations.Nullable()
    java.lang.String since, @retrofit2.http.Query(value = "bot_type")
    @org.jetbrains.annotations.Nullable()
    java.lang.String botType, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.bittick.network.TradingOpportunitiesResponse>> $completion);
    
    @retrofit2.http.DELETE(value = "api/trading/opportunities/{id}")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteTradingOpportunity(@retrofit2.http.Path(value = "id")
    int id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.bittick.network.DeleteOpportunityResponse>> $completion);
    
    @retrofit2.http.GET(value = "api/trading/positions")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getTradingPositions(@retrofit2.http.Header(value = "x-wallet-address")
    @org.jetbrains.annotations.Nullable()
    java.lang.String walletAddress, @retrofit2.http.Query(value = "type")
    @org.jetbrains.annotations.Nullable()
    java.lang.String type, @retrofit2.http.Query(value = "status")
    @org.jetbrains.annotations.Nullable()
    java.lang.String status, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.bittick.network.PositionsResponse>> $completion);
    
    @retrofit2.http.POST(value = "api/trading/positions/{id}/cancel")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object cancelTradingPosition(@retrofit2.http.Path(value = "id")
    int id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.bittick.network.CancelPositionResponse>> $completion);
    
    @retrofit2.http.GET(value = "api/trading/bot/status")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getTradingBotStatus(@retrofit2.http.Header(value = "x-wallet-address")
    @org.jetbrains.annotations.Nullable()
    java.lang.String walletAddress, @retrofit2.http.Query(value = "inscriptionId")
    @org.jetbrains.annotations.Nullable()
    java.lang.String inscriptionId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.bittick.network.BotStatusResponse>> $completion);
    
    @retrofit2.http.GET(value = "api/chart/klines")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getKlines(@retrofit2.http.Query(value = "interval")
    @org.jetbrains.annotations.NotNull()
    java.lang.String interval, @retrofit2.http.Query(value = "limit")
    int limit, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.bittick.network.KlinesResponse>> $completion);
    
    @retrofit2.http.GET(value = "api/chart/ticker")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getTicker(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.bittick.network.TickerResponse>> $completion);
    
    @retrofit2.http.GET(value = "api/chart/zones")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getZones(@retrofit2.http.Query(value = "interval")
    @org.jetbrains.annotations.NotNull()
    java.lang.String interval, @retrofit2.http.Query(value = "limit")
    int limit, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.bittick.network.ZonesResponse>> $completion);
    
    @retrofit2.http.GET(value = "api/chart/trading-zones")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getTradingZones(@retrofit2.http.Query(value = "price")
    @org.jetbrains.annotations.Nullable()
    java.lang.Double price, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.bittick.network.TradingZonesResponse>> $completion);
    
    @retrofit2.http.GET(value = "api/auth/nonce")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getNonce(@retrofit2.http.Query(value = "address")
    @org.jetbrains.annotations.NotNull()
    java.lang.String address, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.bittick.network.NonceResponse>> $completion);
    
    @retrofit2.http.POST(value = "api/auth/verify-wallet")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object verifyWallet(@retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    com.bittick.network.VerifyWalletRequest body, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.bittick.network.VerifyWalletResponse>> $completion);
    
    @retrofit2.http.GET(value = "api/auth/verify-status")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object verifyStatus(@retrofit2.http.Query(value = "address")
    @org.jetbrains.annotations.NotNull()
    java.lang.String address, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.bittick.network.VerifyWalletResponse>> $completion);
    
    @retrofit2.http.POST(value = "api/auth/select-inscription")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object selectInscription(@retrofit2.http.Header(value = "x-wallet-address")
    @org.jetbrains.annotations.NotNull()
    java.lang.String address, @retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    com.bittick.network.SelectInscriptionRequest body, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.bittick.network.SelectInscriptionResponse>> $completion);
    
    @retrofit2.http.GET(value = "api/auth/wallet-inscriptions")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getWalletInscriptions(@retrofit2.http.Header(value = "x-wallet-address")
    @org.jetbrains.annotations.NotNull()
    java.lang.String address, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.bittick.network.WalletInscriptionsResponse>> $completion);
    
    @retrofit2.http.GET(value = "api/auth/fetch-inscriptions")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object fetchInscriptions(@retrofit2.http.Header(value = "x-wallet-address")
    @org.jetbrains.annotations.NotNull()
    java.lang.String address, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.bittick.network.FetchInscriptionsResponse>> $completion);
    
    @retrofit2.http.GET(value = "api/trading/preferences/{inscriptionId}")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getInscriptionPreferences(@retrofit2.http.Path(value = "inscriptionId")
    @org.jetbrains.annotations.NotNull()
    java.lang.String inscriptionId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.bittick.network.BotPreferencesResponse>> $completion);
    
    @retrofit2.http.POST(value = "api/trading/preferences")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object saveInscriptionPreferences(@retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    com.bittick.network.SavePreferencesRequest body, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.bittick.network.SavePreferencesResponse>> $completion);
    
    @retrofit2.http.GET(value = "api/trading/strategies/levels/{inscriptionId}/{mode}")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getLevelConfigs(@retrofit2.http.Path(value = "inscriptionId")
    @org.jetbrains.annotations.NotNull()
    java.lang.String inscriptionId, @retrofit2.http.Path(value = "mode")
    @org.jetbrains.annotations.NotNull()
    java.lang.String mode, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.bittick.network.LevelConfigsResponse>> $completion);
    
    @retrofit2.http.POST(value = "api/trading/strategies/levels")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object saveLevelConfigs(@retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    com.bittick.network.LevelConfigsRequest body, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.bittick.network.SaveLevelConfigsResponse>> $completion);
    
    @retrofit2.http.GET(value = "api/trading/bot-apikey/{inscriptionId}/{mode}/raw")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getBotApiKey(@retrofit2.http.Header(value = "x-wallet-address")
    @org.jetbrains.annotations.NotNull()
    java.lang.String walletAddress, @retrofit2.http.Path(value = "inscriptionId")
    @org.jetbrains.annotations.NotNull()
    java.lang.String inscriptionId, @retrofit2.http.Path(value = "mode")
    @org.jetbrains.annotations.NotNull()
    java.lang.String mode, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.bittick.network.BotApiKeyResponse>> $completion);
    
    @retrofit2.http.POST(value = "api/trading/bot-apikey")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object saveBotApiKey(@retrofit2.http.Header(value = "x-wallet-address")
    @org.jetbrains.annotations.NotNull()
    java.lang.String walletAddress, @retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    com.bittick.network.BotApiKeyRequest body, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.bittick.network.SaveBotApiKeyResponse>> $completion);
    
    @retrofit2.http.DELETE(value = "api/trading/bot-apikey/{inscriptionId}/{mode}")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteBotApiKey(@retrofit2.http.Header(value = "x-wallet-address")
    @org.jetbrains.annotations.NotNull()
    java.lang.String walletAddress, @retrofit2.http.Path(value = "inscriptionId")
    @org.jetbrains.annotations.NotNull()
    java.lang.String inscriptionId, @retrofit2.http.Path(value = "mode")
    @org.jetbrains.annotations.NotNull()
    java.lang.String mode, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.bittick.network.DeleteBotApiKeyResponse>> $completion);
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 3, xi = 48)
    public static final class DefaultImpls {
    }
}