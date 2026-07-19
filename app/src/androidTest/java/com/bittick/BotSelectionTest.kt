package com.bittick

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.bittick.network.BotPosition
import com.bittick.network.BotStatusItem
import com.bittick.network.Balance
import com.bittick.network.InscriptionInfo
import com.bittick.ui.trading.TradingUiState
import com.bittick.ui.theme.BittickTheme
import com.bittick.wallet.WalletScreen
import com.bittick.wallet.WalletState
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class BotSelectionTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun botSelection_afterUsar_showsActiveBots() {
        composeTestRule.setContent {
            BittickTheme {
                val stateFlow = MutableStateFlow(
                    TradingUiState(
                        spotBotStatus = BotStatusItem(
                            type = "spot", enabled = true, maxPositions = 5,
                            positionSizeUsdt = 50.0, minConfidence = 6,
                            openPositions = 2, totalPnl = 10.0,
                            balance = Balance(total = 500.0, available = 400.0)
                        ),
                        futuresBotStatus = BotStatusItem(
                            type = "futures", enabled = true, maxPositions = 3,
                            positionSizeUsdt = 100.0, minConfidence = 7,
                            openPositions = 1, totalPnl = 25.0,
                            balance = Balance(total = 1000.0, available = 700.0)
                        ),
                        isPremium = true,
                        isFreeTier = false
                    )
                )
                val state by stateFlow.collectAsState()
                BoxTradingScreenWrapper(state = state)
            }
        }
        composeTestRule.onNodeWithText("BOT SPOT BTC").assertIsDisplayed()
        composeTestRule.onNodeWithText("ACTIVO").assertIsDisplayed()
        composeTestRule.onNodeWithText("BOT FUTUROS BTC").assertIsDisplayed()
    }

    @Test
    fun botSelection_afterUsar_showsPositions() {
        composeTestRule.setContent {
            BittickTheme {
                val stateFlow = MutableStateFlow(
                    TradingUiState(
                        spotPositions = listOf(
                            BotPosition(
                                id = 1, bot_type = "spot", strategy_type = "swing",
                                asset = "BTCUSDT", entry_price = 65000.0, current_price = 66000.0,
                                quantity = 0.001, order_id = "order-1", target = 68000.0,
                                stop_loss = 63000.0, score = 8.0, confidence = 7.0,
                                ai_explanation = "Trend bullish", horizonte = "4h",
                                usd_amount = 50.0, status = "open", pnl = 1.0, pnl_percent = 1.5
                            )
                        ),
                        spotBotStatus = BotStatusItem(
                            type = "spot", enabled = true, maxPositions = 5,
                            positionSizeUsdt = 50.0, minConfidence = 6,
                            openPositions = 1, totalPnl = 1.0,
                            balance = Balance(total = 500.0, available = 450.0)
                        ),
                        isPremium = true,
                        isFreeTier = false
                    )
                )
                val state by stateFlow.collectAsState()
                BoxTradingScreenWrapper(state = state)
            }
        }
        composeTestRule.onNodeWithText("BTCUSDT").assertIsDisplayed()
        composeTestRule.onNodeWithText("BOT SPOT BTC").assertIsDisplayed()
    }

    @Test
    fun botSelection_usarButtonAppears_onPreview() {
        val inscription = InscriptionInfo(
            inscriptionId = "insc-001", num = 88, tier = "FOUNDER",
            botImageUrl = null
        )
        composeTestRule.setContent {
            BittickTheme {
                WalletScreen(
                    walletState = WalletState(
                        connectedAddress = "bc1ptest123",
                        verified = true,
                        previewInscription = inscription
                    ),
                    onConnectWallet = {},
                    onPreviewInscription = {},
                    onConfirmSelection = {},
                    onDisconnectWallet = {},
                    onDismiss = {}
                )
            }
        }
        composeTestRule.onNodeWithText("USAR").assertIsDisplayed()
    }

    @Test
    fun botSelection_confirmSelection_callsOnConfirm() {
        val inscription = InscriptionInfo(
            inscriptionId = "insc-001", num = 88, tier = "FOUNDER",
            botImageUrl = null
        )
        var confirmCalled = false
        composeTestRule.setContent {
            BittickTheme {
                WalletScreen(
                    walletState = WalletState(
                        connectedAddress = "bc1ptest123",
                        verified = true,
                        previewInscription = inscription
                    ),
                    onConnectWallet = {},
                    onPreviewInscription = {},
                    onConfirmSelection = { confirmCalled = true },
                    onDisconnectWallet = {},
                    onDismiss = {}
                )
            }
        }
        composeTestRule.onNodeWithText("USAR").performClick()
        assert(confirmCalled) { "onConfirmSelection should be called when USAR is tapped" }
    }

    @Test
    fun botSelection_noBotsShown_whenFreeTier() {
        composeTestRule.setContent {
            BittickTheme {
                val stateFlow = MutableStateFlow(
                    TradingUiState(isPremium = false, isFreeTier = true)
                )
                val state by stateFlow.collectAsState()
                BoxTradingScreenWrapper(state = state)
            }
        }
        composeTestRule.onNodeWithText("SPOT").assertDoesNotExist()
        composeTestRule.onNodeWithText("FUTUROS").assertDoesNotExist()
    }

    @Test
    fun botSelection_showsInactive_whenBotDisabled() {
        composeTestRule.setContent {
            BittickTheme {
                val stateFlow = MutableStateFlow(
                    TradingUiState(
                        spotBotStatus = BotStatusItem(
                            type = "spot", enabled = false, maxPositions = 5,
                            positionSizeUsdt = 50.0, minConfidence = 6,
                            openPositions = 0, totalPnl = 0.0, balance = null
                        ),
                        isPremium = true,
                        isFreeTier = false
                    )
                )
                val state by stateFlow.collectAsState()
                BoxTradingScreenWrapper(state = state)
            }
        }
        composeTestRule.onNodeWithText("INACTIVO").assertIsDisplayed()
    }
}
