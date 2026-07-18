package com.bittick.ui.settings;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModel;
import com.bittick.data.cache.BittickImageCache;
import com.bittick.data.preferences.BittickPreferences;
import com.bittick.network.ApiClient;
import com.bittick.network.InscriptionInfo;
import dagger.hilt.android.lifecycle.HiltViewModel;
import dagger.hilt.android.qualifiers.ApplicationContext;
import kotlinx.coroutines.flow.StateFlow;
import javax.inject.Inject;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u0006\n\u0002\b9\b\u0086\b\u0018\u00002\u00020\u0001B\u00cb\u0001\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u0012\u000e\b\u0002\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\t\u0012\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u0005\u0012\b\b\u0002\u0010\u000b\u001a\u00020\u0003\u0012\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\u0005\u0012\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\u000e\u0012\b\b\u0002\u0010\u000f\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0010\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0011\u001a\u00020\u0012\u0012\b\b\u0002\u0010\u0013\u001a\u00020\u0012\u0012\b\b\u0002\u0010\u0014\u001a\u00020\u000e\u0012\b\b\u0002\u0010\u0015\u001a\u00020\u000e\u0012\b\b\u0002\u0010\u0016\u001a\u00020\u000e\u0012\b\b\u0002\u0010\u0017\u001a\u00020\u000e\u0012\b\b\u0002\u0010\u0018\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0019\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\u0002\u0010\u001aJ\t\u00103\u001a\u00020\u0003H\u00c6\u0003J\t\u00104\u001a\u00020\u0003H\u00c6\u0003J\t\u00105\u001a\u00020\u0012H\u00c6\u0003J\t\u00106\u001a\u00020\u0012H\u00c6\u0003J\t\u00107\u001a\u00020\u000eH\u00c6\u0003J\t\u00108\u001a\u00020\u000eH\u00c6\u0003J\t\u00109\u001a\u00020\u000eH\u00c6\u0003J\t\u0010:\u001a\u00020\u000eH\u00c6\u0003J\t\u0010;\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010<\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\u000b\u0010=\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\u000b\u0010>\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\u000f\u0010?\u001a\b\u0012\u0004\u0012\u00020\u00070\tH\u00c6\u0003J\u000b\u0010@\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\t\u0010A\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010B\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\u0010\u0010C\u001a\u0004\u0018\u00010\u000eH\u00c6\u0003\u00a2\u0006\u0002\u0010\u001eJ\t\u0010D\u001a\u00020\u0003H\u00c6\u0003J\u00d4\u0001\u0010E\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u00072\u000e\b\u0002\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\t2\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u00052\b\b\u0002\u0010\u000b\u001a\u00020\u00032\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\u000e2\b\b\u0002\u0010\u000f\u001a\u00020\u00032\b\b\u0002\u0010\u0010\u001a\u00020\u00032\b\b\u0002\u0010\u0011\u001a\u00020\u00122\b\b\u0002\u0010\u0013\u001a\u00020\u00122\b\b\u0002\u0010\u0014\u001a\u00020\u000e2\b\b\u0002\u0010\u0015\u001a\u00020\u000e2\b\b\u0002\u0010\u0016\u001a\u00020\u000e2\b\b\u0002\u0010\u0017\u001a\u00020\u000e2\b\b\u0002\u0010\u0018\u001a\u00020\u00032\n\b\u0002\u0010\u0019\u001a\u0004\u0018\u00010\u0005H\u00c6\u0001\u00a2\u0006\u0002\u0010FJ\u0013\u0010G\u001a\u00020\u00032\b\u0010H\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010I\u001a\u00020\u000eH\u00d6\u0001J\t\u0010J\u001a\u00020\u0005H\u00d6\u0001R\u0013\u0010\n\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u001cR\u0015\u0010\r\u001a\u0004\u0018\u00010\u000e\u00a2\u0006\n\n\u0002\u0010\u001f\u001a\u0004\b\u001d\u0010\u001eR\u0013\u0010\u0019\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b \u0010\u001cR\u0011\u0010\u0010\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b!\u0010\"R\u0011\u0010\u0015\u001a\u00020\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b#\u0010$R\u0011\u0010\u0017\u001a\u00020\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b%\u0010$R\u0011\u0010\u0013\u001a\u00020\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\b&\u0010\'R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b(\u0010\"R\u0017\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\t\u00a2\u0006\b\n\u0000\u001a\u0004\b)\u0010*R\u0011\u0010\u0018\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\"R\u0011\u0010\u000b\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\"R\u0013\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b+\u0010,R\u0011\u0010\u000f\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b-\u0010\"R\u0011\u0010\u0014\u001a\u00020\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b.\u0010$R\u0011\u0010\u0016\u001a\u00020\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b/\u0010$R\u0011\u0010\u0011\u001a\u00020\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\b0\u0010\'R\u0013\u0010\f\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b1\u0010\u001cR\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b2\u0010\u001c\u00a8\u0006K"}, d2 = {"Lcom/bittick/ui/settings/SettingsUiState;", "", "hasNotificationPermission", "", "walletAddress", "", "selectedInscription", "Lcom/bittick/network/InscriptionInfo;", "inscriptions", "", "botImageUrl", "isPremium", "tier", "botNumber", "", "spotEnabled", "futuresEnabled", "spotPositionSize", "", "futuresPositionSize", "spotMaxPositions", "futuresMaxPositions", "spotMinScore", "futuresMinScore", "isLoading", "error", "(ZLjava/lang/String;Lcom/bittick/network/InscriptionInfo;Ljava/util/List;Ljava/lang/String;ZLjava/lang/String;Ljava/lang/Integer;ZZDDIIIIZLjava/lang/String;)V", "getBotImageUrl", "()Ljava/lang/String;", "getBotNumber", "()Ljava/lang/Integer;", "Ljava/lang/Integer;", "getError", "getFuturesEnabled", "()Z", "getFuturesMaxPositions", "()I", "getFuturesMinScore", "getFuturesPositionSize", "()D", "getHasNotificationPermission", "getInscriptions", "()Ljava/util/List;", "getSelectedInscription", "()Lcom/bittick/network/InscriptionInfo;", "getSpotEnabled", "getSpotMaxPositions", "getSpotMinScore", "getSpotPositionSize", "getTier", "getWalletAddress", "component1", "component10", "component11", "component12", "component13", "component14", "component15", "component16", "component17", "component18", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "(ZLjava/lang/String;Lcom/bittick/network/InscriptionInfo;Ljava/util/List;Ljava/lang/String;ZLjava/lang/String;Ljava/lang/Integer;ZZDDIIIIZLjava/lang/String;)Lcom/bittick/ui/settings/SettingsUiState;", "equals", "other", "hashCode", "toString", "app_debug"})
public final class SettingsUiState {
    private final boolean hasNotificationPermission = false;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String walletAddress = null;
    @org.jetbrains.annotations.Nullable()
    private final com.bittick.network.InscriptionInfo selectedInscription = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.bittick.network.InscriptionInfo> inscriptions = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String botImageUrl = null;
    private final boolean isPremium = false;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String tier = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Integer botNumber = null;
    private final boolean spotEnabled = false;
    private final boolean futuresEnabled = false;
    private final double spotPositionSize = 0.0;
    private final double futuresPositionSize = 0.0;
    private final int spotMaxPositions = 0;
    private final int futuresMaxPositions = 0;
    private final int spotMinScore = 0;
    private final int futuresMinScore = 0;
    private final boolean isLoading = false;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String error = null;
    
    public SettingsUiState(boolean hasNotificationPermission, @org.jetbrains.annotations.Nullable()
    java.lang.String walletAddress, @org.jetbrains.annotations.Nullable()
    com.bittick.network.InscriptionInfo selectedInscription, @org.jetbrains.annotations.NotNull()
    java.util.List<com.bittick.network.InscriptionInfo> inscriptions, @org.jetbrains.annotations.Nullable()
    java.lang.String botImageUrl, boolean isPremium, @org.jetbrains.annotations.Nullable()
    java.lang.String tier, @org.jetbrains.annotations.Nullable()
    java.lang.Integer botNumber, boolean spotEnabled, boolean futuresEnabled, double spotPositionSize, double futuresPositionSize, int spotMaxPositions, int futuresMaxPositions, int spotMinScore, int futuresMinScore, boolean isLoading, @org.jetbrains.annotations.Nullable()
    java.lang.String error) {
        super();
    }
    
    public final boolean getHasNotificationPermission() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getWalletAddress() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.bittick.network.InscriptionInfo getSelectedInscription() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.bittick.network.InscriptionInfo> getInscriptions() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getBotImageUrl() {
        return null;
    }
    
    public final boolean isPremium() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getTier() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer getBotNumber() {
        return null;
    }
    
    public final boolean getSpotEnabled() {
        return false;
    }
    
    public final boolean getFuturesEnabled() {
        return false;
    }
    
    public final double getSpotPositionSize() {
        return 0.0;
    }
    
    public final double getFuturesPositionSize() {
        return 0.0;
    }
    
    public final int getSpotMaxPositions() {
        return 0;
    }
    
    public final int getFuturesMaxPositions() {
        return 0;
    }
    
    public final int getSpotMinScore() {
        return 0;
    }
    
    public final int getFuturesMinScore() {
        return 0;
    }
    
    public final boolean isLoading() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getError() {
        return null;
    }
    
    public SettingsUiState() {
        super();
    }
    
    public final boolean component1() {
        return false;
    }
    
    public final boolean component10() {
        return false;
    }
    
    public final double component11() {
        return 0.0;
    }
    
    public final double component12() {
        return 0.0;
    }
    
    public final int component13() {
        return 0;
    }
    
    public final int component14() {
        return 0;
    }
    
    public final int component15() {
        return 0;
    }
    
    public final int component16() {
        return 0;
    }
    
    public final boolean component17() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component18() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component2() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.bittick.network.InscriptionInfo component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.bittick.network.InscriptionInfo> component4() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component5() {
        return null;
    }
    
    public final boolean component6() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component7() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer component8() {
        return null;
    }
    
    public final boolean component9() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.bittick.ui.settings.SettingsUiState copy(boolean hasNotificationPermission, @org.jetbrains.annotations.Nullable()
    java.lang.String walletAddress, @org.jetbrains.annotations.Nullable()
    com.bittick.network.InscriptionInfo selectedInscription, @org.jetbrains.annotations.NotNull()
    java.util.List<com.bittick.network.InscriptionInfo> inscriptions, @org.jetbrains.annotations.Nullable()
    java.lang.String botImageUrl, boolean isPremium, @org.jetbrains.annotations.Nullable()
    java.lang.String tier, @org.jetbrains.annotations.Nullable()
    java.lang.Integer botNumber, boolean spotEnabled, boolean futuresEnabled, double spotPositionSize, double futuresPositionSize, int spotMaxPositions, int futuresMaxPositions, int spotMinScore, int futuresMinScore, boolean isLoading, @org.jetbrains.annotations.Nullable()
    java.lang.String error) {
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