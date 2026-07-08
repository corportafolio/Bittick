package com.bittick.network;

import retrofit2.Response;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000T\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\bf\u0018\u00002\u00020\u0001J\u001e\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\u0007J\u001e\u0010\b\u001a\b\u0012\u0004\u0012\u00020\t0\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\u0007J(\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u000b0\u00032\b\b\u0003\u0010\f\u001a\u00020\r2\b\b\u0003\u0010\u000e\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\u000fJ\u0014\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00110\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u0012J\u0014\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00140\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u0012J4\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00160\u00032\b\b\u0003\u0010\u000e\u001a\u00020\u00062\b\b\u0003\u0010\u0017\u001a\u00020\u00062\n\b\u0003\u0010\u0018\u001a\u0004\u0018\u00010\rH\u00a7@\u00a2\u0006\u0002\u0010\u0019J,\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u001b0\u00032\n\b\u0003\u0010\u001c\u001a\u0004\u0018\u00010\r2\n\b\u0003\u0010\u001d\u001a\u0004\u0018\u00010\rH\u00a7@\u00a2\u0006\u0002\u0010\u001eJ(\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020 0\u00032\b\b\u0003\u0010\f\u001a\u00020\r2\b\b\u0003\u0010\u000e\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\u000f\u00a8\u0006!"}, d2 = {"Lcom/bittick/network/ApiService;", "", "cancelTradingPosition", "Lretrofit2/Response;", "Lcom/bittick/network/CancelPositionResponse;", "id", "", "(ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteTradingOpportunity", "Lcom/bittick/network/DeleteOpportunityResponse;", "getKlines", "Lcom/bittick/network/KlinesResponse;", "interval", "", "limit", "(Ljava/lang/String;ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getTicker", "Lcom/bittick/network/TickerResponse;", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getTradingBotStatus", "Lcom/bittick/network/BotStatusResponse;", "getTradingOpportunities", "Lcom/bittick/network/TradingOpportunitiesResponse;", "offset", "since", "(IILjava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getTradingPositions", "Lcom/bittick/network/PositionsResponse;", "type", "status", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getZones", "Lcom/bittick/network/ZonesResponse;", "app_debug"})
public abstract interface ApiService {
    
    @retrofit2.http.GET(value = "api/trading/opportunities")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getTradingOpportunities(@retrofit2.http.Query(value = "limit")
    int limit, @retrofit2.http.Query(value = "offset")
    int offset, @retrofit2.http.Query(value = "since")
    @org.jetbrains.annotations.Nullable()
    java.lang.String since, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.bittick.network.TradingOpportunitiesResponse>> $completion);
    
    @retrofit2.http.DELETE(value = "api/trading/opportunities/{id}")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteTradingOpportunity(@retrofit2.http.Path(value = "id")
    int id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.bittick.network.DeleteOpportunityResponse>> $completion);
    
    @retrofit2.http.GET(value = "api/trading/positions")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getTradingPositions(@retrofit2.http.Query(value = "type")
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
    public abstract java.lang.Object getTradingBotStatus(@org.jetbrains.annotations.NotNull()
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
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 3, xi = 48)
    public static final class DefaultImpls {
    }
}