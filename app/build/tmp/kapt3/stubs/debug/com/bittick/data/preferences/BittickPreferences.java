package com.bittick.data.preferences;

import android.content.Context;
import com.bittick.network.InscriptionInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dagger.hilt.android.qualifiers.ApplicationContext;
import javax.inject.Inject;
import javax.inject.Singleton;

@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000L\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0018\b\u0007\u0018\u0000 42\u00020\u0001:\u000245B\u0011\b\u0007\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0006\u0010\f\u001a\u00020\rJ\u0006\u0010\u000e\u001a\u00020\rJ\u0006\u0010\u000f\u001a\u00020\rJ\u0010\u0010\u0010\u001a\u00020\r2\b\b\u0002\u0010\u0011\u001a\u00020\u0012J\r\u0010\u0013\u001a\u0004\u0018\u00010\u0012\u00a2\u0006\u0002\u0010\u0014J\u0006\u0010\u0015\u001a\u00020\u0016J\b\u0010\u0017\u001a\u0004\u0018\u00010\u0018J\b\u0010\u0019\u001a\u0004\u0018\u00010\u0018J\b\u0010\u001a\u001a\u0004\u0018\u00010\u0018J\b\u0010\u001b\u001a\u0004\u0018\u00010\u0018J\b\u0010\u001c\u001a\u0004\u0018\u00010\u0018J\b\u0010\u001d\u001a\u0004\u0018\u00010\u001eJ.\u0010\u001f\u001a\u00020\r2\u0006\u0010 \u001a\u00020\u00182\u0006\u0010!\u001a\u00020\u00182\u0006\u0010\"\u001a\u00020\u00122\u0006\u0010#\u001a\u00020\u00182\u0006\u0010$\u001a\u00020\u0018J\u0015\u0010%\u001a\u00020\r2\b\u0010\"\u001a\u0004\u0018\u00010\u0012\u00a2\u0006\u0002\u0010&J\u000e\u0010\'\u001a\u00020\r2\u0006\u0010(\u001a\u00020\u0016J\u0010\u0010)\u001a\u00020\r2\b\u0010*\u001a\u0004\u0018\u00010\u0018J\u0010\u0010+\u001a\u00020\r2\b\u0010,\u001a\u0004\u0018\u00010\u0018J\u0010\u0010-\u001a\u00020\r2\b\u0010.\u001a\u0004\u0018\u00010\u0018J\u000e\u0010/\u001a\u00020\r2\u0006\u00100\u001a\u00020\u0018J\u0010\u00101\u001a\u00020\r2\b\u0010 \u001a\u0004\u0018\u00010\u0018J\u001e\u00102\u001a\u00020\r2\u0006\u0010!\u001a\u00020\u00182\u0006\u0010\"\u001a\u00020\u00122\u0006\u0010#\u001a\u00020\u0018J\u000e\u00103\u001a\u00020\r2\u0006\u0010$\u001a\u00020\u0018R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0007\u001a\n \t*\u0004\u0018\u00010\b0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\n\u001a\n \t*\u0004\u0018\u00010\u000b0\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u00066"}, d2 = {"Lcom/bittick/data/preferences/BittickPreferences;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "gson", "Lcom/google/gson/Gson;", "inscriptionListType", "Ljava/lang/reflect/Type;", "kotlin.jvm.PlatformType", "prefs", "Landroid/content/SharedPreferences;", "clearPendingConnection", "", "clearWalletData", "clearWalletSession", "extendSessionExpiry", "days", "", "getBotNumber", "()Ljava/lang/Integer;", "getIsPremium", "", "getPendingNonce", "", "getPendingWalletType", "getSelectedInscriptionId", "getTradingLastCreatedAt", "getWalletAddress", "getWalletSession", "Lcom/bittick/data/preferences/BittickPreferences$WalletSession;", "saveWalletSession", "address", "selectedInscriptionId", "botNumber", "tier", "botImageBase64", "setBotNumber", "(Ljava/lang/Integer;)V", "setIsPremium", "isPremium", "setPendingNonce", "nonce", "setPendingWalletType", "type", "setSelectedInscriptionId", "inscriptionId", "setTradingLastCreatedAt", "value", "setWalletAddress", "updateSelectedInscription", "updateSessionImage", "Companion", "WalletSession", "app_debug"})
public final class BittickPreferences {
    private final android.content.SharedPreferences prefs = null;
    @org.jetbrains.annotations.NotNull()
    private final com.google.gson.Gson gson = null;
    private final java.lang.reflect.Type inscriptionListType = null;
    private static final long SESSION_DAYS = 7L;
    private static final long MS_PER_DAY = 86400000L;
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String PREFS_NAME = "bittick_prefs";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_TRADING_LAST_CREATED = "trading_last_created_at";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_WALLET_ADDRESS = "wallet_address";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_SELECTED_INSCRIPTION = "selected_inscription_id";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_IS_PREMIUM = "is_premium";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_BOT_NUMBER = "bot_number";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_WALLET_SESSION = "wallet_session";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_PENDING_NONCE = "pending_nonce";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_PENDING_WALLET_TYPE = "pending_wallet_type";
    @org.jetbrains.annotations.NotNull()
    public static final com.bittick.data.preferences.BittickPreferences.Companion Companion = null;
    
    @javax.inject.Inject()
    public BittickPreferences(@dagger.hilt.android.qualifiers.ApplicationContext()
    @org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getTradingLastCreatedAt() {
        return null;
    }
    
    public final void setTradingLastCreatedAt(@org.jetbrains.annotations.NotNull()
    java.lang.String value) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getWalletAddress() {
        return null;
    }
    
    public final void setWalletAddress(@org.jetbrains.annotations.Nullable()
    java.lang.String address) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getSelectedInscriptionId() {
        return null;
    }
    
    public final void setSelectedInscriptionId(@org.jetbrains.annotations.Nullable()
    java.lang.String inscriptionId) {
    }
    
    public final boolean getIsPremium() {
        return false;
    }
    
    public final void setIsPremium(boolean isPremium) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer getBotNumber() {
        return null;
    }
    
    public final void setBotNumber(@org.jetbrains.annotations.Nullable()
    java.lang.Integer botNumber) {
    }
    
    public final void clearWalletData() {
    }
    
    public final void saveWalletSession(@org.jetbrains.annotations.NotNull()
    java.lang.String address, @org.jetbrains.annotations.NotNull()
    java.lang.String selectedInscriptionId, int botNumber, @org.jetbrains.annotations.NotNull()
    java.lang.String tier, @org.jetbrains.annotations.NotNull()
    java.lang.String botImageBase64) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.bittick.data.preferences.BittickPreferences.WalletSession getWalletSession() {
        return null;
    }
    
    public final void clearWalletSession() {
    }
    
    public final void extendSessionExpiry(int days) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getPendingNonce() {
        return null;
    }
    
    public final void setPendingNonce(@org.jetbrains.annotations.Nullable()
    java.lang.String nonce) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getPendingWalletType() {
        return null;
    }
    
    public final void setPendingWalletType(@org.jetbrains.annotations.Nullable()
    java.lang.String type) {
    }
    
    public final void clearPendingConnection() {
    }
    
    public final void updateSelectedInscription(@org.jetbrains.annotations.NotNull()
    java.lang.String selectedInscriptionId, int botNumber, @org.jetbrains.annotations.NotNull()
    java.lang.String tier) {
    }
    
    public final void updateSessionImage(@org.jetbrains.annotations.NotNull()
    java.lang.String botImageBase64) {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\b\n\u0002\u0010\t\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\rX\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0010"}, d2 = {"Lcom/bittick/data/preferences/BittickPreferences$Companion;", "", "()V", "KEY_BOT_NUMBER", "", "KEY_IS_PREMIUM", "KEY_PENDING_NONCE", "KEY_PENDING_WALLET_TYPE", "KEY_SELECTED_INSCRIPTION", "KEY_TRADING_LAST_CREATED", "KEY_WALLET_ADDRESS", "KEY_WALLET_SESSION", "MS_PER_DAY", "", "PREFS_NAME", "SESSION_DAYS", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u000b\n\u0002\u0010\u000b\n\u0002\b\u000f\b\u0086\b\u0018\u00002\u00020\u0001B5\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\u0003\u0012\u0006\u0010\b\u001a\u00020\u0003\u0012\u0006\u0010\t\u001a\u00020\n\u00a2\u0006\u0002\u0010\u000bJ\t\u0010\u001a\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001b\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001c\u001a\u00020\u0006H\u00c6\u0003J\t\u0010\u001d\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001e\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001f\u001a\u00020\nH\u00c6\u0003JE\u0010 \u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\u00032\b\b\u0002\u0010\b\u001a\u00020\u00032\b\b\u0002\u0010\t\u001a\u00020\nH\u00c6\u0001J\u0013\u0010!\u001a\u00020\u00162\b\u0010\"\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010#\u001a\u00020\u0006H\u00d6\u0001J\t\u0010$\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\b\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\rR\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0011\u001a\u00020\n8F\u00a2\u0006\u0006\u001a\u0004\b\u0012\u0010\u0013R\u0011\u0010\t\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0013R\u0011\u0010\u0015\u001a\u00020\u00168F\u00a2\u0006\u0006\u001a\u0004\b\u0015\u0010\u0017R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\rR\u0011\u0010\u0007\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\r\u00a8\u0006%"}, d2 = {"Lcom/bittick/data/preferences/BittickPreferences$WalletSession;", "", "address", "", "selectedInscriptionId", "botNumber", "", "tier", "botImageBase64", "expiresAt", "", "(Ljava/lang/String;Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;J)V", "getAddress", "()Ljava/lang/String;", "getBotImageBase64", "getBotNumber", "()I", "daysUntilExpiry", "getDaysUntilExpiry", "()J", "getExpiresAt", "isExpired", "", "()Z", "getSelectedInscriptionId", "getTier", "component1", "component2", "component3", "component4", "component5", "component6", "copy", "equals", "other", "hashCode", "toString", "app_debug"})
    public static final class WalletSession {
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String address = null;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String selectedInscriptionId = null;
        private final int botNumber = 0;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String tier = null;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String botImageBase64 = null;
        private final long expiresAt = 0L;
        
        public WalletSession(@org.jetbrains.annotations.NotNull()
        java.lang.String address, @org.jetbrains.annotations.NotNull()
        java.lang.String selectedInscriptionId, int botNumber, @org.jetbrains.annotations.NotNull()
        java.lang.String tier, @org.jetbrains.annotations.NotNull()
        java.lang.String botImageBase64, long expiresAt) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getAddress() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getSelectedInscriptionId() {
            return null;
        }
        
        public final int getBotNumber() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getTier() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getBotImageBase64() {
            return null;
        }
        
        public final long getExpiresAt() {
            return 0L;
        }
        
        public final boolean isExpired() {
            return false;
        }
        
        public final long getDaysUntilExpiry() {
            return 0L;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component2() {
            return null;
        }
        
        public final int component3() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component4() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component5() {
            return null;
        }
        
        public final long component6() {
            return 0L;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.bittick.data.preferences.BittickPreferences.WalletSession copy(@org.jetbrains.annotations.NotNull()
        java.lang.String address, @org.jetbrains.annotations.NotNull()
        java.lang.String selectedInscriptionId, int botNumber, @org.jetbrains.annotations.NotNull()
        java.lang.String tier, @org.jetbrains.annotations.NotNull()
        java.lang.String botImageBase64, long expiresAt) {
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
}