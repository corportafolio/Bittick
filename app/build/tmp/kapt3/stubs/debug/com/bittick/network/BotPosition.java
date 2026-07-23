package com.bittick.network;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u0006\n\u0002\bC\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0086\b\u0018\u00002\u00020\u0001B\u00c9\u0001\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\u0005\u0012\u0006\u0010\b\u001a\u00020\t\u0012\b\u0010\n\u001a\u0004\u0018\u00010\t\u0012\u0006\u0010\u000b\u001a\u00020\t\u0012\b\u0010\f\u001a\u0004\u0018\u00010\u0005\u0012\b\u0010\r\u001a\u0004\u0018\u00010\t\u0012\b\u0010\u000e\u001a\u0004\u0018\u00010\t\u0012\u0006\u0010\u000f\u001a\u00020\t\u0012\u0006\u0010\u0010\u001a\u00020\t\u0012\b\u0010\u0011\u001a\u0004\u0018\u00010\u0005\u0012\b\u0010\u0012\u001a\u0004\u0018\u00010\u0005\u0012\b\u0010\u0013\u001a\u0004\u0018\u00010\t\u0012\b\u0010\u0014\u001a\u0004\u0018\u00010\u0005\u0012\u0006\u0010\u0015\u001a\u00020\t\u0012\u0006\u0010\u0016\u001a\u00020\t\u0012\n\b\u0002\u0010\u0017\u001a\u0004\u0018\u00010\u0005\u0012\n\b\u0002\u0010\u0018\u001a\u0004\u0018\u00010\u0005\u0012\n\b\u0002\u0010\u0019\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\u0002\u0010\u001aJ\t\u00105\u001a\u00020\u0003H\u00c6\u0003J\u0010\u00106\u001a\u0004\u0018\u00010\tH\u00c6\u0003\u00a2\u0006\u0002\u0010$J\t\u00107\u001a\u00020\tH\u00c6\u0003J\t\u00108\u001a\u00020\tH\u00c6\u0003J\u000b\u00109\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\u000b\u0010:\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\u0010\u0010;\u001a\u0004\u0018\u00010\tH\u00c6\u0003\u00a2\u0006\u0002\u0010$J\u000b\u0010<\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\t\u0010=\u001a\u00020\tH\u00c6\u0003J\t\u0010>\u001a\u00020\tH\u00c6\u0003J\u000b\u0010?\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\t\u0010@\u001a\u00020\u0005H\u00c6\u0003J\u000b\u0010A\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\u000b\u0010B\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\t\u0010C\u001a\u00020\u0005H\u00c6\u0003J\t\u0010D\u001a\u00020\u0005H\u00c6\u0003J\t\u0010E\u001a\u00020\tH\u00c6\u0003J\u0010\u0010F\u001a\u0004\u0018\u00010\tH\u00c6\u0003\u00a2\u0006\u0002\u0010$J\t\u0010G\u001a\u00020\tH\u00c6\u0003J\u000b\u0010H\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\u0010\u0010I\u001a\u0004\u0018\u00010\tH\u00c6\u0003\u00a2\u0006\u0002\u0010$J\u00f6\u0001\u0010J\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0007\u001a\u00020\u00052\b\b\u0002\u0010\b\u001a\u00020\t2\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\t2\b\b\u0002\u0010\u000b\u001a\u00020\t2\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\t2\n\b\u0002\u0010\u000e\u001a\u0004\u0018\u00010\t2\b\b\u0002\u0010\u000f\u001a\u00020\t2\b\b\u0002\u0010\u0010\u001a\u00020\t2\n\b\u0002\u0010\u0011\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010\u0012\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010\u0013\u001a\u0004\u0018\u00010\t2\n\b\u0002\u0010\u0014\u001a\u0004\u0018\u00010\u00052\b\b\u0002\u0010\u0015\u001a\u00020\t2\b\b\u0002\u0010\u0016\u001a\u00020\t2\n\b\u0002\u0010\u0017\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010\u0018\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010\u0019\u001a\u0004\u0018\u00010\u0005H\u00c6\u0001\u00a2\u0006\u0002\u0010KJ\u0013\u0010L\u001a\u00020M2\b\u0010N\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010O\u001a\u00020\u0003H\u00d6\u0001J\t\u0010P\u001a\u00020\u0005H\u00d6\u0001R\u0013\u0010\u0011\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u001cR\u0011\u0010\u0007\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u001cR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u001cR\u0013\u0010\u0019\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010\u001cR\u0013\u0010\u0018\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b \u0010\u001cR\u0011\u0010\u0010\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b!\u0010\"R\u0015\u0010\n\u001a\u0004\u0018\u00010\t\u00a2\u0006\n\n\u0002\u0010%\u001a\u0004\b#\u0010$R\u0011\u0010\b\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b&\u0010\"R\u0013\u0010\u0012\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\'\u0010\u001cR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b(\u0010)R\u0013\u0010\u0017\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b*\u0010\u001cR\u0013\u0010\f\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b+\u0010\u001cR\u0011\u0010\u0015\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b,\u0010\"R\u0011\u0010\u0016\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b-\u0010\"R\u0011\u0010\u000b\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b.\u0010\"R\u0011\u0010\u000f\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b/\u0010\"R\u0013\u0010\u0014\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b0\u0010\u001cR\u0015\u0010\u000e\u001a\u0004\u0018\u00010\t\u00a2\u0006\n\n\u0002\u0010%\u001a\u0004\b1\u0010$R\u0011\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b2\u0010\u001cR\u0015\u0010\r\u001a\u0004\u0018\u00010\t\u00a2\u0006\n\n\u0002\u0010%\u001a\u0004\b3\u0010$R\u0015\u0010\u0013\u001a\u0004\u0018\u00010\t\u00a2\u0006\n\n\u0002\u0010%\u001a\u0004\b4\u0010$\u00a8\u0006Q"}, d2 = {"Lcom/bittick/network/BotPosition;", "", "id", "", "bot_type", "", "strategy_type", "asset", "entry_price", "", "current_price", "quantity", "order_id", "target", "stop_loss", "score", "confidence", "ai_explanation", "horizonte", "usd_amount", "status", "pnl", "pnl_percent", "opened_at", "closed_at", "close_reason", "(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;DLjava/lang/Double;DLjava/lang/String;Ljava/lang/Double;Ljava/lang/Double;DDLjava/lang/String;Ljava/lang/String;Ljava/lang/Double;Ljava/lang/String;DDLjava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", "getAi_explanation", "()Ljava/lang/String;", "getAsset", "getBot_type", "getClose_reason", "getClosed_at", "getConfidence", "()D", "getCurrent_price", "()Ljava/lang/Double;", "Ljava/lang/Double;", "getEntry_price", "getHorizonte", "getId", "()I", "getOpened_at", "getOrder_id", "getPnl", "getPnl_percent", "getQuantity", "getScore", "getStatus", "getStop_loss", "getStrategy_type", "getTarget", "getUsd_amount", "component1", "component10", "component11", "component12", "component13", "component14", "component15", "component16", "component17", "component18", "component19", "component2", "component20", "component21", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;DLjava/lang/Double;DLjava/lang/String;Ljava/lang/Double;Ljava/lang/Double;DDLjava/lang/String;Ljava/lang/String;Ljava/lang/Double;Ljava/lang/String;DDLjava/lang/String;Ljava/lang/String;Ljava/lang/String;)Lcom/bittick/network/BotPosition;", "equals", "", "other", "hashCode", "toString", "app_debug"})
public final class BotPosition {
    private final int id = 0;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String bot_type = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String strategy_type = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String asset = null;
    private final double entry_price = 0.0;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Double current_price = null;
    private final double quantity = 0.0;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String order_id = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Double target = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Double stop_loss = null;
    private final double score = 0.0;
    private final double confidence = 0.0;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String ai_explanation = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String horizonte = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Double usd_amount = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String status = null;
    private final double pnl = 0.0;
    private final double pnl_percent = 0.0;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String opened_at = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String closed_at = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String close_reason = null;
    
    public BotPosition(int id, @org.jetbrains.annotations.NotNull()
    java.lang.String bot_type, @org.jetbrains.annotations.NotNull()
    java.lang.String strategy_type, @org.jetbrains.annotations.NotNull()
    java.lang.String asset, double entry_price, @org.jetbrains.annotations.Nullable()
    java.lang.Double current_price, double quantity, @org.jetbrains.annotations.Nullable()
    java.lang.String order_id, @org.jetbrains.annotations.Nullable()
    java.lang.Double target, @org.jetbrains.annotations.Nullable()
    java.lang.Double stop_loss, double score, double confidence, @org.jetbrains.annotations.Nullable()
    java.lang.String ai_explanation, @org.jetbrains.annotations.Nullable()
    java.lang.String horizonte, @org.jetbrains.annotations.Nullable()
    java.lang.Double usd_amount, @org.jetbrains.annotations.Nullable()
    java.lang.String status, double pnl, double pnl_percent, @org.jetbrains.annotations.Nullable()
    java.lang.String opened_at, @org.jetbrains.annotations.Nullable()
    java.lang.String closed_at, @org.jetbrains.annotations.Nullable()
    java.lang.String close_reason) {
        super();
    }
    
    public final int getId() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getBot_type() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getStrategy_type() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getAsset() {
        return null;
    }
    
    public final double getEntry_price() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Double getCurrent_price() {
        return null;
    }
    
    public final double getQuantity() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getOrder_id() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Double getTarget() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Double getStop_loss() {
        return null;
    }
    
    public final double getScore() {
        return 0.0;
    }
    
    public final double getConfidence() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getAi_explanation() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getHorizonte() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Double getUsd_amount() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getStatus() {
        return null;
    }
    
    public final double getPnl() {
        return 0.0;
    }
    
    public final double getPnl_percent() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getOpened_at() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getClosed_at() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getClose_reason() {
        return null;
    }
    
    public final int component1() {
        return 0;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Double component10() {
        return null;
    }
    
    public final double component11() {
        return 0.0;
    }
    
    public final double component12() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component13() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component14() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Double component15() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component16() {
        return null;
    }
    
    public final double component17() {
        return 0.0;
    }
    
    public final double component18() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component19() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component2() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component20() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component21() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component4() {
        return null;
    }
    
    public final double component5() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Double component6() {
        return null;
    }
    
    public final double component7() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component8() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Double component9() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.bittick.network.BotPosition copy(int id, @org.jetbrains.annotations.NotNull()
    java.lang.String bot_type, @org.jetbrains.annotations.NotNull()
    java.lang.String strategy_type, @org.jetbrains.annotations.NotNull()
    java.lang.String asset, double entry_price, @org.jetbrains.annotations.Nullable()
    java.lang.Double current_price, double quantity, @org.jetbrains.annotations.Nullable()
    java.lang.String order_id, @org.jetbrains.annotations.Nullable()
    java.lang.Double target, @org.jetbrains.annotations.Nullable()
    java.lang.Double stop_loss, double score, double confidence, @org.jetbrains.annotations.Nullable()
    java.lang.String ai_explanation, @org.jetbrains.annotations.Nullable()
    java.lang.String horizonte, @org.jetbrains.annotations.Nullable()
    java.lang.Double usd_amount, @org.jetbrains.annotations.Nullable()
    java.lang.String status, double pnl, double pnl_percent, @org.jetbrains.annotations.Nullable()
    java.lang.String opened_at, @org.jetbrains.annotations.Nullable()
    java.lang.String closed_at, @org.jetbrains.annotations.Nullable()
    java.lang.String close_reason) {
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