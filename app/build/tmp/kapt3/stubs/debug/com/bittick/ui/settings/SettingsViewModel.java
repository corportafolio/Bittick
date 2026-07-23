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

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u000e\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0006\n\u0002\u0010\u0006\n\u0002\b\b\b\u0007\u0018\u00002\u00020\u0001B!\b\u0007\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bJ\u0006\u0010\u0010\u001a\u00020\u0011J\u000e\u0010\u0012\u001a\u00020\u00112\u0006\u0010\u0013\u001a\u00020\u0014J\u0006\u0010\u0015\u001a\u00020\u0011J\u0006\u0010\u0016\u001a\u00020\u0017J\u0010\u0010\u0018\u001a\u00020\u00112\u0006\u0010\u0013\u001a\u00020\u0014H\u0002J\u000e\u0010\u0019\u001a\u00020\u00112\u0006\u0010\u001a\u001a\u00020\u0014J\u0010\u0010\u001b\u001a\u00020\u00112\u0006\u0010\u001c\u001a\u00020\u0014H\u0002J\u0010\u0010\u001d\u001a\u00020\u00112\u0006\u0010\u001c\u001a\u00020\u0014H\u0002J\b\u0010\u001e\u001a\u00020\u0011H\u0002J\u0006\u0010\u001f\u001a\u00020\u0011J\u0006\u0010 \u001a\u00020\u0011J\u000e\u0010!\u001a\u00020\u00112\u0006\u0010\u0013\u001a\u00020\u0014J\u000e\u0010\"\u001a\u00020\u00112\u0006\u0010\u0013\u001a\u00020\u0014J\u0006\u0010#\u001a\u00020\u0011J\u000e\u0010$\u001a\u00020\u00112\u0006\u0010%\u001a\u00020&J\u0006\u0010\'\u001a\u00020\u0011J\u0006\u0010(\u001a\u00020\u0011J\u0006\u0010)\u001a\u00020\u0011J\u0006\u0010*\u001a\u00020\u0011J\u0006\u0010+\u001a\u00020\u0011J\u000e\u0010,\u001a\u00020\u00112\u0006\u0010-\u001a\u00020\u0014J\u000e\u0010.\u001a\u00020\u00112\u0006\u0010-\u001a\u00020\u0014J\u000e\u0010/\u001a\u00020\u00112\u0006\u00100\u001a\u00020\u0017J\u001e\u00101\u001a\u00020\u00112\u0006\u00102\u001a\u0002032\u0006\u00104\u001a\u00020\u00142\u0006\u0010-\u001a\u000205J\u000e\u00106\u001a\u00020\u00112\u0006\u00107\u001a\u000203J\u000e\u00108\u001a\u00020\u00112\u0006\u00109\u001a\u000203J\u000e\u0010:\u001a\u00020\u00112\u0006\u0010;\u001a\u00020<J\u000e\u0010=\u001a\u00020\u00112\u0006\u0010-\u001a\u00020\u0014J\u000e\u0010>\u001a\u00020\u00112\u0006\u0010-\u001a\u00020\u0014J\u000e\u0010?\u001a\u00020\u00112\u0006\u00100\u001a\u00020\u0017J\u001e\u0010@\u001a\u00020\u00112\u0006\u00102\u001a\u0002032\u0006\u00104\u001a\u00020\u00142\u0006\u0010-\u001a\u000205J\u000e\u0010A\u001a\u00020\u00112\u0006\u00107\u001a\u000203J\u000e\u0010B\u001a\u00020\u00112\u0006\u00109\u001a\u000203J\u000e\u0010C\u001a\u00020\u00112\u0006\u0010;\u001a\u00020<R\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000b0\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000f\u00a8\u0006D"}, d2 = {"Lcom/bittick/ui/settings/SettingsViewModel;", "Landroidx/lifecycle/ViewModel;", "context", "Landroid/content/Context;", "preferences", "Lcom/bittick/data/preferences/BittickPreferences;", "imageCache", "Lcom/bittick/data/cache/BittickImageCache;", "(Landroid/content/Context;Lcom/bittick/data/preferences/BittickPreferences;Lcom/bittick/data/cache/BittickImageCache;)V", "_state", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/bittick/ui/settings/SettingsUiState;", "state", "Lkotlinx/coroutines/flow/StateFlow;", "getState", "()Lkotlinx/coroutines/flow/StateFlow;", "clearApiKeyMessage", "", "deleteApiKey", "mode", "", "disconnectWallet", "hasNotificationPermission", "", "loadApiKey", "loadInscriptions", "address", "loadLevelConfigs", "inscriptionId", "loadPreferences", "loadWalletState", "refreshPermissions", "refreshWalletState", "saveApiKey", "saveLevelConfigs", "savePreferences", "selectInscription", "inscription", "Lcom/bittick/network/InscriptionInfo;", "testNotification", "toggleFuturesApiKeyEditing", "toggleFuturesExpanded", "toggleSpotApiKeyEditing", "toggleSpotExpanded", "updateFuturesApiKeyInput", "value", "updateFuturesApiSecretInput", "updateFuturesEnabled", "enabled", "updateFuturesLevel", "level", "", "field", "", "updateFuturesMaxPositions", "max", "updateFuturesMinScore", "score", "updateFuturesPositionSize", "size", "", "updateSpotApiKeyInput", "updateSpotApiSecretInput", "updateSpotEnabled", "updateSpotLevel", "updateSpotMaxPositions", "updateSpotMinScore", "updateSpotPositionSize", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class SettingsViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    private final com.bittick.data.preferences.BittickPreferences preferences = null;
    @org.jetbrains.annotations.NotNull()
    private final com.bittick.data.cache.BittickImageCache imageCache = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.bittick.ui.settings.SettingsUiState> _state = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.bittick.ui.settings.SettingsUiState> state = null;
    
    @javax.inject.Inject()
    public SettingsViewModel(@dagger.hilt.android.qualifiers.ApplicationContext()
    @org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    com.bittick.data.preferences.BittickPreferences preferences, @org.jetbrains.annotations.NotNull()
    com.bittick.data.cache.BittickImageCache imageCache) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.bittick.ui.settings.SettingsUiState> getState() {
        return null;
    }
    
    public final void refreshWalletState() {
    }
    
    public final boolean hasNotificationPermission() {
        return false;
    }
    
    public final void refreshPermissions() {
    }
    
    public final void testNotification() {
    }
    
    private final void loadWalletState() {
    }
    
    public final void loadInscriptions(@org.jetbrains.annotations.NotNull()
    java.lang.String address) {
    }
    
    public final void selectInscription(@org.jetbrains.annotations.NotNull()
    com.bittick.network.InscriptionInfo inscription) {
    }
    
    private final void loadPreferences(java.lang.String inscriptionId) {
    }
    
    public final void savePreferences() {
    }
    
    public final void updateSpotEnabled(boolean enabled) {
    }
    
    public final void updateFuturesEnabled(boolean enabled) {
    }
    
    public final void updateSpotPositionSize(double size) {
    }
    
    public final void updateFuturesPositionSize(double size) {
    }
    
    public final void updateSpotMaxPositions(int max) {
    }
    
    public final void updateFuturesMaxPositions(int max) {
    }
    
    public final void updateSpotMinScore(int score) {
    }
    
    public final void updateFuturesMinScore(int score) {
    }
    
    public final void toggleSpotExpanded() {
    }
    
    public final void toggleFuturesExpanded() {
    }
    
    public final void updateSpotLevel(int level, @org.jetbrains.annotations.NotNull()
    java.lang.String field, @org.jetbrains.annotations.NotNull()
    java.lang.Object value) {
    }
    
    public final void updateFuturesLevel(int level, @org.jetbrains.annotations.NotNull()
    java.lang.String field, @org.jetbrains.annotations.NotNull()
    java.lang.Object value) {
    }
    
    public final void saveLevelConfigs(@org.jetbrains.annotations.NotNull()
    java.lang.String mode) {
    }
    
    private final void loadLevelConfigs(java.lang.String inscriptionId) {
    }
    
    private final void loadApiKey(java.lang.String mode) {
    }
    
    public final void toggleSpotApiKeyEditing() {
    }
    
    public final void toggleFuturesApiKeyEditing() {
    }
    
    public final void updateSpotApiKeyInput(@org.jetbrains.annotations.NotNull()
    java.lang.String value) {
    }
    
    public final void updateSpotApiSecretInput(@org.jetbrains.annotations.NotNull()
    java.lang.String value) {
    }
    
    public final void updateFuturesApiKeyInput(@org.jetbrains.annotations.NotNull()
    java.lang.String value) {
    }
    
    public final void updateFuturesApiSecretInput(@org.jetbrains.annotations.NotNull()
    java.lang.String value) {
    }
    
    public final void saveApiKey(@org.jetbrains.annotations.NotNull()
    java.lang.String mode) {
    }
    
    public final void deleteApiKey(@org.jetbrains.annotations.NotNull()
    java.lang.String mode) {
    }
    
    public final void clearApiKeyMessage() {
    }
    
    public final void disconnectWallet() {
    }
}