package com.bittick.ui.trading;

import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.material.icons.Icons;
import androidx.compose.material3.ButtonDefaults;
import androidx.compose.material3.CardDefaults;
import androidx.compose.material3.DrawerValue;
import androidx.compose.material3.ExperimentalMaterial3Api;
import androidx.compose.material3.FilterChipDefaults;
import androidx.compose.material3.NavigationDrawerItemDefaults;
import androidx.compose.material3.TopAppBarDefaults;
import androidx.compose.runtime.Composable;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.text.font.FontWeight;
import com.bittick.network.BotPosition;
import com.bittick.network.BotStatusItem;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000B\n\u0000\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\u001a0\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00022\b\u0010\u0006\u001a\u0004\u0018\u00010\u00072\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\t0\u00012\u0006\u0010\n\u001a\u00020\u000bH\u0003\u001a\u0018\u0010\f\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00022\u0006\u0010\r\u001a\u00020\u0002H\u0003\u001a$\u0010\u000e\u001a\u00020\u00042\u0006\u0010\u000f\u001a\u00020\u00102\u0012\u0010\u0011\u001a\u000e\u0012\u0004\u0012\u00020\u0013\u0012\u0004\u0012\u00020\u00040\u0012H\u0003\u001a\u0018\u0010\u0014\u001a\u00020\u00042\u0006\u0010\u0015\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH\u0003\u001a\"\u0010\u0016\u001a\u00020\u00042\u000e\b\u0002\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00040\u00182\b\b\u0002\u0010\n\u001a\u00020\u000bH\u0007\u001a\u0010\u0010\u0019\u001a\u00020\u00022\u0006\u0010\u001a\u001a\u00020\u0002H\u0002\u001a\u0010\u0010\u001b\u001a\u00020\u00022\u0006\u0010\u001a\u001a\u00020\u0002H\u0002\"\u0014\u0010\u0000\u001a\b\u0012\u0004\u0012\u00020\u00020\u0001X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001c"}, d2 = {"INTERVALS", "", "", "BotSection", "", "label", "status", "Lcom/bittick/network/BotStatusItem;", "positions", "Lcom/bittick/network/BotPosition;", "viewModel", "Lcom/bittick/ui/trading/TradingViewModel;", "InfoChip", "value", "OpportunityCard", "op", "Lcom/bittick/ui/trading/TradingOpportunityItem;", "onDelete", "Lkotlin/Function1;", "", "PositionCard", "pos", "TradingScreen", "onSettingsClick", "Lkotlin/Function0;", "formatDateTimeLocal", "isoDate", "formatDayOfWeekSpanish", "app_debug"})
public final class TradingScreenKt {
    @org.jetbrains.annotations.NotNull()
    private static final java.util.List<java.lang.String> INTERVALS = null;
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void TradingScreen(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onSettingsClick, @org.jetbrains.annotations.NotNull()
    com.bittick.ui.trading.TradingViewModel viewModel) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void BotSection(java.lang.String label, com.bittick.network.BotStatusItem status, java.util.List<com.bittick.network.BotPosition> positions, com.bittick.ui.trading.TradingViewModel viewModel) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void PositionCard(com.bittick.network.BotPosition pos, com.bittick.ui.trading.TradingViewModel viewModel) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void OpportunityCard(com.bittick.ui.trading.TradingOpportunityItem op, kotlin.jvm.functions.Function1<? super java.lang.Integer, kotlin.Unit> onDelete) {
    }
    
    private static final java.lang.String formatDateTimeLocal(java.lang.String isoDate) {
        return null;
    }
    
    private static final java.lang.String formatDayOfWeekSpanish(java.lang.String isoDate) {
        return null;
    }
    
    @androidx.compose.runtime.Composable()
    private static final void InfoChip(java.lang.String label, java.lang.String value) {
    }
}