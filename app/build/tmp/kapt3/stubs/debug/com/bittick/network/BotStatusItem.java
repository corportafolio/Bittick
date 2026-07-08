package com.bittick.network;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u001c\b\u0086\b\u0018\u00002\u00020\u0001BG\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\u0007\u0012\u0006\u0010\u000b\u001a\u00020\u0007\u0012\u0006\u0010\f\u001a\u00020\t\u0012\b\u0010\r\u001a\u0004\u0018\u00010\u000e\u00a2\u0006\u0002\u0010\u000fJ\t\u0010\u001d\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001e\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u001f\u001a\u00020\u0007H\u00c6\u0003J\t\u0010 \u001a\u00020\tH\u00c6\u0003J\t\u0010!\u001a\u00020\u0007H\u00c6\u0003J\t\u0010\"\u001a\u00020\u0007H\u00c6\u0003J\t\u0010#\u001a\u00020\tH\u00c6\u0003J\u000b\u0010$\u001a\u0004\u0018\u00010\u000eH\u00c6\u0003J[\u0010%\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\u00072\b\b\u0002\u0010\u000b\u001a\u00020\u00072\b\b\u0002\u0010\f\u001a\u00020\t2\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\u000eH\u00c6\u0001J\u0013\u0010&\u001a\u00020\u00052\b\u0010\'\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010(\u001a\u00020\u0007H\u00d6\u0001J\t\u0010)\u001a\u00020\u0003H\u00d6\u0001R\u0013\u0010\r\u001a\u0004\u0018\u00010\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0011\u0010\n\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0015R\u0011\u0010\u000b\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0015R\u0011\u0010\b\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0019R\u0011\u0010\f\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u0019R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u001c\u00a8\u0006*"}, d2 = {"Lcom/bittick/network/BotStatusItem;", "", "type", "", "enabled", "", "maxPositions", "", "positionSizeUsdt", "", "minConfidence", "openPositions", "totalPnl", "balance", "Lcom/bittick/network/Balance;", "(Ljava/lang/String;ZIDIIDLcom/bittick/network/Balance;)V", "getBalance", "()Lcom/bittick/network/Balance;", "getEnabled", "()Z", "getMaxPositions", "()I", "getMinConfidence", "getOpenPositions", "getPositionSizeUsdt", "()D", "getTotalPnl", "getType", "()Ljava/lang/String;", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "copy", "equals", "other", "hashCode", "toString", "app_debug"})
public final class BotStatusItem {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String type = null;
    private final boolean enabled = false;
    private final int maxPositions = 0;
    private final double positionSizeUsdt = 0.0;
    private final int minConfidence = 0;
    private final int openPositions = 0;
    private final double totalPnl = 0.0;
    @org.jetbrains.annotations.Nullable()
    private final com.bittick.network.Balance balance = null;
    
    public BotStatusItem(@org.jetbrains.annotations.NotNull()
    java.lang.String type, boolean enabled, int maxPositions, double positionSizeUsdt, int minConfidence, int openPositions, double totalPnl, @org.jetbrains.annotations.Nullable()
    com.bittick.network.Balance balance) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getType() {
        return null;
    }
    
    public final boolean getEnabled() {
        return false;
    }
    
    public final int getMaxPositions() {
        return 0;
    }
    
    public final double getPositionSizeUsdt() {
        return 0.0;
    }
    
    public final int getMinConfidence() {
        return 0;
    }
    
    public final int getOpenPositions() {
        return 0;
    }
    
    public final double getTotalPnl() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.bittick.network.Balance getBalance() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component1() {
        return null;
    }
    
    public final boolean component2() {
        return false;
    }
    
    public final int component3() {
        return 0;
    }
    
    public final double component4() {
        return 0.0;
    }
    
    public final int component5() {
        return 0;
    }
    
    public final int component6() {
        return 0;
    }
    
    public final double component7() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.bittick.network.Balance component8() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.bittick.network.BotStatusItem copy(@org.jetbrains.annotations.NotNull()
    java.lang.String type, boolean enabled, int maxPositions, double positionSizeUsdt, int minConfidence, int openPositions, double totalPnl, @org.jetbrains.annotations.Nullable()
    com.bittick.network.Balance balance) {
        return null;
    }
    
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String toString() {
        return null;
    }
}