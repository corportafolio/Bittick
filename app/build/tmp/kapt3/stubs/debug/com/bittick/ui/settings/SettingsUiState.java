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

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u0006\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b`\b\u0086\b\u0018\u00002\u00020\u0001B\u00f3\u0002\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u0012\u000e\b\u0002\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\t\u0012\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u0005\u0012\b\b\u0002\u0010\u000b\u001a\u00020\u0003\u0012\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\u0005\u0012\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\u000e\u0012\b\b\u0002\u0010\u000f\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0010\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0011\u001a\u00020\u0012\u0012\b\b\u0002\u0010\u0013\u001a\u00020\u0012\u0012\b\b\u0002\u0010\u0014\u001a\u00020\u000e\u0012\b\b\u0002\u0010\u0015\u001a\u00020\u000e\u0012\b\b\u0002\u0010\u0016\u001a\u00020\u000e\u0012\b\b\u0002\u0010\u0017\u001a\u00020\u000e\u0012\u000e\b\u0002\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00190\t\u0012\u000e\b\u0002\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u00190\t\u0012\b\b\u0002\u0010\u001b\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u001c\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u001d\u001a\u0004\u0018\u00010\u0005\u0012\b\b\u0002\u0010\u001e\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u001f\u001a\u00020\u0003\u0012\b\b\u0002\u0010 \u001a\u00020\u0005\u0012\b\b\u0002\u0010!\u001a\u00020\u0005\u0012\n\b\u0002\u0010\"\u001a\u0004\u0018\u00010\u0005\u0012\b\b\u0002\u0010#\u001a\u00020\u0003\u0012\b\b\u0002\u0010$\u001a\u00020\u0003\u0012\b\b\u0002\u0010%\u001a\u00020\u0005\u0012\b\b\u0002\u0010&\u001a\u00020\u0005\u0012\b\b\u0002\u0010\'\u001a\u00020\u0003\u0012\n\b\u0002\u0010(\u001a\u0004\u0018\u00010\u0005\u0012\n\b\u0002\u0010)\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\u0002\u0010*J\t\u0010R\u001a\u00020\u0003H\u00c6\u0003J\t\u0010S\u001a\u00020\u0003H\u00c6\u0003J\t\u0010T\u001a\u00020\u0012H\u00c6\u0003J\t\u0010U\u001a\u00020\u0012H\u00c6\u0003J\t\u0010V\u001a\u00020\u000eH\u00c6\u0003J\t\u0010W\u001a\u00020\u000eH\u00c6\u0003J\t\u0010X\u001a\u00020\u000eH\u00c6\u0003J\t\u0010Y\u001a\u00020\u000eH\u00c6\u0003J\u000f\u0010Z\u001a\b\u0012\u0004\u0012\u00020\u00190\tH\u00c6\u0003J\u000f\u0010[\u001a\b\u0012\u0004\u0012\u00020\u00190\tH\u00c6\u0003J\t\u0010\\\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010]\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\t\u0010^\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010_\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\t\u0010`\u001a\u00020\u0003H\u00c6\u0003J\t\u0010a\u001a\u00020\u0003H\u00c6\u0003J\t\u0010b\u001a\u00020\u0005H\u00c6\u0003J\t\u0010c\u001a\u00020\u0005H\u00c6\u0003J\u000b\u0010d\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\t\u0010e\u001a\u00020\u0003H\u00c6\u0003J\t\u0010f\u001a\u00020\u0003H\u00c6\u0003J\t\u0010g\u001a\u00020\u0005H\u00c6\u0003J\u000b\u0010h\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\t\u0010i\u001a\u00020\u0005H\u00c6\u0003J\t\u0010j\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010k\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\u000b\u0010l\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\u000f\u0010m\u001a\b\u0012\u0004\u0012\u00020\u00070\tH\u00c6\u0003J\u000b\u0010n\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\t\u0010o\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010p\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\u0010\u0010q\u001a\u0004\u0018\u00010\u000eH\u00c6\u0003\u00a2\u0006\u0002\u0010/J\t\u0010r\u001a\u00020\u0003H\u00c6\u0003J\u00fc\u0002\u0010s\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u00072\u000e\b\u0002\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\t2\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u00052\b\b\u0002\u0010\u000b\u001a\u00020\u00032\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\u000e2\b\b\u0002\u0010\u000f\u001a\u00020\u00032\b\b\u0002\u0010\u0010\u001a\u00020\u00032\b\b\u0002\u0010\u0011\u001a\u00020\u00122\b\b\u0002\u0010\u0013\u001a\u00020\u00122\b\b\u0002\u0010\u0014\u001a\u00020\u000e2\b\b\u0002\u0010\u0015\u001a\u00020\u000e2\b\b\u0002\u0010\u0016\u001a\u00020\u000e2\b\b\u0002\u0010\u0017\u001a\u00020\u000e2\u000e\b\u0002\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00190\t2\u000e\b\u0002\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u00190\t2\b\b\u0002\u0010\u001b\u001a\u00020\u00032\b\b\u0002\u0010\u001c\u001a\u00020\u00032\n\b\u0002\u0010\u001d\u001a\u0004\u0018\u00010\u00052\b\b\u0002\u0010\u001e\u001a\u00020\u00032\b\b\u0002\u0010\u001f\u001a\u00020\u00032\b\b\u0002\u0010 \u001a\u00020\u00052\b\b\u0002\u0010!\u001a\u00020\u00052\n\b\u0002\u0010\"\u001a\u0004\u0018\u00010\u00052\b\b\u0002\u0010#\u001a\u00020\u00032\b\b\u0002\u0010$\u001a\u00020\u00032\b\b\u0002\u0010%\u001a\u00020\u00052\b\b\u0002\u0010&\u001a\u00020\u00052\b\b\u0002\u0010\'\u001a\u00020\u00032\n\b\u0002\u0010(\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010)\u001a\u0004\u0018\u00010\u0005H\u00c6\u0001\u00a2\u0006\u0002\u0010tJ\u0013\u0010u\u001a\u00020\u00032\b\u0010v\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010w\u001a\u00020\u000eH\u00d6\u0001J\t\u0010x\u001a\u00020\u0005H\u00d6\u0001R\u0013\u0010)\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b+\u0010,R\u0013\u0010\n\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b-\u0010,R\u0015\u0010\r\u001a\u0004\u0018\u00010\u000e\u00a2\u0006\n\n\u0002\u00100\u001a\u0004\b.\u0010/R\u0013\u0010(\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b1\u0010,R\u0011\u0010$\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b2\u00103R\u0011\u0010#\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b4\u00103R\u0011\u0010%\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b5\u0010,R\u0013\u0010\"\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b6\u0010,R\u0011\u0010&\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b7\u0010,R\u0011\u0010\u0010\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b8\u00103R\u0011\u0010\u001c\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b9\u00103R\u0017\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u00190\t\u00a2\u0006\b\n\u0000\u001a\u0004\b:\u0010;R\u0011\u0010\u0015\u001a\u00020\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b<\u0010=R\u0011\u0010\u0017\u001a\u00020\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b>\u0010=R\u0011\u0010\u0013\u001a\u00020\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\b?\u0010@R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\bA\u00103R\u0017\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\t\u00a2\u0006\b\n\u0000\u001a\u0004\bB\u0010;R\u0011\u0010\'\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\'\u00103R\u0011\u0010\u000b\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u00103R\u0013\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\bC\u0010DR\u0011\u0010\u001f\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\bE\u00103R\u0011\u0010\u001e\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\bF\u00103R\u0011\u0010 \u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\bG\u0010,R\u0013\u0010\u001d\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\bH\u0010,R\u0011\u0010!\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\bI\u0010,R\u0011\u0010\u000f\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\bJ\u00103R\u0011\u0010\u001b\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\bK\u00103R\u0017\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00190\t\u00a2\u0006\b\n\u0000\u001a\u0004\bL\u0010;R\u0011\u0010\u0014\u001a\u00020\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\bM\u0010=R\u0011\u0010\u0016\u001a\u00020\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\bN\u0010=R\u0011\u0010\u0011\u001a\u00020\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\bO\u0010@R\u0013\u0010\f\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\bP\u0010,R\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\bQ\u0010,\u00a8\u0006y"}, d2 = {"Lcom/bittick/ui/settings/SettingsUiState;", "", "hasNotificationPermission", "", "walletAddress", "", "selectedInscription", "Lcom/bittick/network/InscriptionInfo;", "inscriptions", "", "botImageUrl", "isPremium", "tier", "botNumber", "", "spotEnabled", "futuresEnabled", "spotPositionSize", "", "futuresPositionSize", "spotMaxPositions", "futuresMaxPositions", "spotMinScore", "futuresMinScore", "spotLevels", "Lcom/bittick/network/LevelConfig;", "futuresLevels", "spotExpanded", "futuresExpanded", "spotApiKeyMasked", "spotApiKeyHasKey", "spotApiKeyEditing", "spotApiKeyInput", "spotApiSecretInput", "futuresApiKeyMasked", "futuresApiKeyHasKey", "futuresApiKeyEditing", "futuresApiKeyInput", "futuresApiSecretInput", "isLoading", "error", "apiKeyMessage", "(ZLjava/lang/String;Lcom/bittick/network/InscriptionInfo;Ljava/util/List;Ljava/lang/String;ZLjava/lang/String;Ljava/lang/Integer;ZZDDIIIILjava/util/List;Ljava/util/List;ZZLjava/lang/String;ZZLjava/lang/String;Ljava/lang/String;Ljava/lang/String;ZZLjava/lang/String;Ljava/lang/String;ZLjava/lang/String;Ljava/lang/String;)V", "getApiKeyMessage", "()Ljava/lang/String;", "getBotImageUrl", "getBotNumber", "()Ljava/lang/Integer;", "Ljava/lang/Integer;", "getError", "getFuturesApiKeyEditing", "()Z", "getFuturesApiKeyHasKey", "getFuturesApiKeyInput", "getFuturesApiKeyMasked", "getFuturesApiSecretInput", "getFuturesEnabled", "getFuturesExpanded", "getFuturesLevels", "()Ljava/util/List;", "getFuturesMaxPositions", "()I", "getFuturesMinScore", "getFuturesPositionSize", "()D", "getHasNotificationPermission", "getInscriptions", "getSelectedInscription", "()Lcom/bittick/network/InscriptionInfo;", "getSpotApiKeyEditing", "getSpotApiKeyHasKey", "getSpotApiKeyInput", "getSpotApiKeyMasked", "getSpotApiSecretInput", "getSpotEnabled", "getSpotExpanded", "getSpotLevels", "getSpotMaxPositions", "getSpotMinScore", "getSpotPositionSize", "getTier", "getWalletAddress", "component1", "component10", "component11", "component12", "component13", "component14", "component15", "component16", "component17", "component18", "component19", "component2", "component20", "component21", "component22", "component23", "component24", "component25", "component26", "component27", "component28", "component29", "component3", "component30", "component31", "component32", "component33", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "(ZLjava/lang/String;Lcom/bittick/network/InscriptionInfo;Ljava/util/List;Ljava/lang/String;ZLjava/lang/String;Ljava/lang/Integer;ZZDDIIIILjava/util/List;Ljava/util/List;ZZLjava/lang/String;ZZLjava/lang/String;Ljava/lang/String;Ljava/lang/String;ZZLjava/lang/String;Ljava/lang/String;ZLjava/lang/String;Ljava/lang/String;)Lcom/bittick/ui/settings/SettingsUiState;", "equals", "other", "hashCode", "toString", "app_debug"})
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
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.bittick.network.LevelConfig> spotLevels = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.bittick.network.LevelConfig> futuresLevels = null;
    private final boolean spotExpanded = false;
    private final boolean futuresExpanded = false;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String spotApiKeyMasked = null;
    private final boolean spotApiKeyHasKey = false;
    private final boolean spotApiKeyEditing = false;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String spotApiKeyInput = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String spotApiSecretInput = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String futuresApiKeyMasked = null;
    private final boolean futuresApiKeyHasKey = false;
    private final boolean futuresApiKeyEditing = false;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String futuresApiKeyInput = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String futuresApiSecretInput = null;
    private final boolean isLoading = false;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String error = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String apiKeyMessage = null;
    
    public SettingsUiState(boolean hasNotificationPermission, @org.jetbrains.annotations.Nullable()
    java.lang.String walletAddress, @org.jetbrains.annotations.Nullable()
    com.bittick.network.InscriptionInfo selectedInscription, @org.jetbrains.annotations.NotNull()
    java.util.List<com.bittick.network.InscriptionInfo> inscriptions, @org.jetbrains.annotations.Nullable()
    java.lang.String botImageUrl, boolean isPremium, @org.jetbrains.annotations.Nullable()
    java.lang.String tier, @org.jetbrains.annotations.Nullable()
    java.lang.Integer botNumber, boolean spotEnabled, boolean futuresEnabled, double spotPositionSize, double futuresPositionSize, int spotMaxPositions, int futuresMaxPositions, int spotMinScore, int futuresMinScore, @org.jetbrains.annotations.NotNull()
    java.util.List<com.bittick.network.LevelConfig> spotLevels, @org.jetbrains.annotations.NotNull()
    java.util.List<com.bittick.network.LevelConfig> futuresLevels, boolean spotExpanded, boolean futuresExpanded, @org.jetbrains.annotations.Nullable()
    java.lang.String spotApiKeyMasked, boolean spotApiKeyHasKey, boolean spotApiKeyEditing, @org.jetbrains.annotations.NotNull()
    java.lang.String spotApiKeyInput, @org.jetbrains.annotations.NotNull()
    java.lang.String spotApiSecretInput, @org.jetbrains.annotations.Nullable()
    java.lang.String futuresApiKeyMasked, boolean futuresApiKeyHasKey, boolean futuresApiKeyEditing, @org.jetbrains.annotations.NotNull()
    java.lang.String futuresApiKeyInput, @org.jetbrains.annotations.NotNull()
    java.lang.String futuresApiSecretInput, boolean isLoading, @org.jetbrains.annotations.Nullable()
    java.lang.String error, @org.jetbrains.annotations.Nullable()
    java.lang.String apiKeyMessage) {
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
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.bittick.network.LevelConfig> getSpotLevels() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.bittick.network.LevelConfig> getFuturesLevels() {
        return null;
    }
    
    public final boolean getSpotExpanded() {
        return false;
    }
    
    public final boolean getFuturesExpanded() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getSpotApiKeyMasked() {
        return null;
    }
    
    public final boolean getSpotApiKeyHasKey() {
        return false;
    }
    
    public final boolean getSpotApiKeyEditing() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getSpotApiKeyInput() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getSpotApiSecretInput() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getFuturesApiKeyMasked() {
        return null;
    }
    
    public final boolean getFuturesApiKeyHasKey() {
        return false;
    }
    
    public final boolean getFuturesApiKeyEditing() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getFuturesApiKeyInput() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getFuturesApiSecretInput() {
        return null;
    }
    
    public final boolean isLoading() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getError() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getApiKeyMessage() {
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
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.bittick.network.LevelConfig> component17() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.bittick.network.LevelConfig> component18() {
        return null;
    }
    
    public final boolean component19() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component2() {
        return null;
    }
    
    public final boolean component20() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component21() {
        return null;
    }
    
    public final boolean component22() {
        return false;
    }
    
    public final boolean component23() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component24() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component25() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component26() {
        return null;
    }
    
    public final boolean component27() {
        return false;
    }
    
    public final boolean component28() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component29() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.bittick.network.InscriptionInfo component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component30() {
        return null;
    }
    
    public final boolean component31() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component32() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component33() {
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
    java.lang.Integer botNumber, boolean spotEnabled, boolean futuresEnabled, double spotPositionSize, double futuresPositionSize, int spotMaxPositions, int futuresMaxPositions, int spotMinScore, int futuresMinScore, @org.jetbrains.annotations.NotNull()
    java.util.List<com.bittick.network.LevelConfig> spotLevels, @org.jetbrains.annotations.NotNull()
    java.util.List<com.bittick.network.LevelConfig> futuresLevels, boolean spotExpanded, boolean futuresExpanded, @org.jetbrains.annotations.Nullable()
    java.lang.String spotApiKeyMasked, boolean spotApiKeyHasKey, boolean spotApiKeyEditing, @org.jetbrains.annotations.NotNull()
    java.lang.String spotApiKeyInput, @org.jetbrains.annotations.NotNull()
    java.lang.String spotApiSecretInput, @org.jetbrains.annotations.Nullable()
    java.lang.String futuresApiKeyMasked, boolean futuresApiKeyHasKey, boolean futuresApiKeyEditing, @org.jetbrains.annotations.NotNull()
    java.lang.String futuresApiKeyInput, @org.jetbrains.annotations.NotNull()
    java.lang.String futuresApiSecretInput, boolean isLoading, @org.jetbrains.annotations.Nullable()
    java.lang.String error, @org.jetbrains.annotations.Nullable()
    java.lang.String apiKeyMessage) {
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