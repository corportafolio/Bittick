package com.bittick.ui.settings;

import android.Manifest;
import android.os.Build;
import androidx.activity.result.contract.ActivityResultContracts;
import android.graphics.BitmapFactory;
import android.util.Base64;
import androidx.compose.foundation.layout.*;
import androidx.compose.material.icons.Icons;
import androidx.compose.material3.*;
import androidx.compose.runtime.*;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.layout.ContentScale;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.text.style.TextAlign;
import com.bittick.network.InscriptionInfo;
import android.content.Intent;
import android.net.Uri;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000Z\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u000e\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u001aU\u0010\u0000\u001a\u00020\u00012\b\u0010\u0002\u001a\u0004\u0018\u00010\u00032\b\u0010\u0004\u001a\u0004\u0018\u00010\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u00032\u0006\u0010\u0007\u001a\u00020\b2\b\u0010\t\u001a\u0004\u0018\u00010\u00032\b\u0010\n\u001a\u0004\u0018\u00010\u000b2\f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00010\rH\u0003\u00a2\u0006\u0002\u0010\u000e\u001a\u0082\u0002\u0010\u000f\u001a\u00020\u00012\u0006\u0010\u0010\u001a\u00020\u00032\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\u0011\u001a\u00020\b2\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00140\u00132\u0006\u0010\u0015\u001a\u00020\b2\b\u0010\u0016\u001a\u0004\u0018\u00010\u00032\u0006\u0010\u0017\u001a\u00020\b2\u0006\u0010\u0018\u001a\u00020\b2\u0006\u0010\u0019\u001a\u00020\u00032\u0006\u0010\u001a\u001a\u00020\u00032\u0012\u0010\u001b\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00010\u001c2\f\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u00010\r2\u001e\u0010\u001e\u001a\u001a\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020 \u0012\u0004\u0012\u00020\u00010\u001f2\f\u0010!\u001a\b\u0012\u0004\u0012\u00020\u00010\r2\f\u0010\"\u001a\b\u0012\u0004\u0012\u00020\u00010\r2\u0012\u0010#\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00010\u001c2\u0012\u0010$\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00010\u001c2\f\u0010%\u001a\b\u0012\u0004\u0012\u00020\u00010\r2\f\u0010&\u001a\b\u0012\u0004\u0012\u00020\u00010\rH\u0003\u001a,\u0010\'\u001a\u00020\u00012\u0006\u0010(\u001a\u00020\b2\f\u0010)\u001a\b\u0012\u0004\u0012\u00020\u00010\r2\f\u0010*\u001a\b\u0012\u0004\u0012\u00020\u00010\rH\u0003\u001a.\u0010+\u001a\u00020\u00012\f\u0010,\u001a\b\u0012\u0004\u0012\u00020\u00010\r2\f\u0010-\u001a\b\u0012\u0004\u0012\u00020\u00010\r2\b\b\u0002\u0010.\u001a\u00020/H\u0007\u001a\u0012\u00100\u001a\u0004\u0018\u0001012\u0006\u00102\u001a\u00020\u0003H\u0002\u00a8\u00063"}, d2 = {"AccountSection", "", "walletAddress", "", "selectedInscription", "Lcom/bittick/network/InscriptionInfo;", "botImageUrl", "isPremium", "", "tier", "botNumber", "", "onClick", "Lkotlin/Function0;", "(Ljava/lang/String;Lcom/bittick/network/InscriptionInfo;Ljava/lang/String;ZLjava/lang/String;Ljava/lang/Integer;Lkotlin/jvm/functions/Function0;)V", "BotCard", "label", "enabled", "levels", "", "Lcom/bittick/network/LevelConfig;", "expanded", "apiKeyMasked", "apiKeyHasKey", "apiKeyEditing", "apiKeyInput", "apiSecretInput", "onToggleEnabled", "Lkotlin/Function1;", "onToggleExpanded", "onUpdateLevel", "Lkotlin/Function3;", "", "onSave", "onToggleApiKeyEditing", "onApiKeyInputChanged", "onApiSecretInputChanged", "onSaveApiKey", "onDeleteApiKey", "PermissionsSection", "hasNotificationPermission", "onRequestPermission", "onTestNotification", "SettingsScreen", "onBack", "onNavigateToWallet", "viewModel", "Lcom/bittick/ui/settings/SettingsViewModel;", "base64ToBitmap", "Landroid/graphics/Bitmap;", "base64", "app_debug"})
public final class SettingsScreenKt {
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void SettingsScreen(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onBack, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onNavigateToWallet, @org.jetbrains.annotations.NotNull()
    com.bittick.ui.settings.SettingsViewModel viewModel) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void AccountSection(java.lang.String walletAddress, com.bittick.network.InscriptionInfo selectedInscription, java.lang.String botImageUrl, boolean isPremium, java.lang.String tier, java.lang.Integer botNumber, kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void PermissionsSection(boolean hasNotificationPermission, kotlin.jvm.functions.Function0<kotlin.Unit> onRequestPermission, kotlin.jvm.functions.Function0<kotlin.Unit> onTestNotification) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void BotCard(java.lang.String label, int botNumber, boolean enabled, java.util.List<com.bittick.network.LevelConfig> levels, boolean expanded, java.lang.String apiKeyMasked, boolean apiKeyHasKey, boolean apiKeyEditing, java.lang.String apiKeyInput, java.lang.String apiSecretInput, kotlin.jvm.functions.Function1<? super java.lang.Boolean, kotlin.Unit> onToggleEnabled, kotlin.jvm.functions.Function0<kotlin.Unit> onToggleExpanded, kotlin.jvm.functions.Function3<? super java.lang.Integer, ? super java.lang.String, java.lang.Object, kotlin.Unit> onUpdateLevel, kotlin.jvm.functions.Function0<kotlin.Unit> onSave, kotlin.jvm.functions.Function0<kotlin.Unit> onToggleApiKeyEditing, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onApiKeyInputChanged, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onApiSecretInputChanged, kotlin.jvm.functions.Function0<kotlin.Unit> onSaveApiKey, kotlin.jvm.functions.Function0<kotlin.Unit> onDeleteApiKey) {
    }
    
    private static final android.graphics.Bitmap base64ToBitmap(java.lang.String base64) {
        return null;
    }
}