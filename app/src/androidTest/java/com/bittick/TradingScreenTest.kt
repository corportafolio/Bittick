package com.bittick

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.bittick.network.BotPosition
import com.bittick.network.BotStatusItem
import com.bittick.network.Balance
import com.bittick.ui.trading.TradingUiState
import com.bittick.ui.theme.BittickTheme
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class TradingScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun tradingScreen_showsTitle() {
        composeTestRule.setContent {
            BittickTheme {
                val state by MutableStateFlow(TradingUiState()).collectAsState()
                BoxTradingScreenWrapper(state = state)
            }
        }
        composeTestRule.onNodeWithText("bittick").assertIsDisplayed()
    }

    @Test
    fun tradingScreen_showsBotSections_whenPremium() {
        composeTestRule.setContent {
            BittickTheme {
                val state by MutableStateFlow(
                    TradingUiState(
                        spotBotStatus = BotStatusItem(
                            type = "spot", enabled = true, maxPositions = 5,
                            positionSizeUsdt = 50.0, minConfidence = 6,
                            openPositions = 3, totalPnl = 15.5,
                            balance = Balance(total = 500.0, available = 300.0)
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
                ).collectAsState()
                BoxTradingScreenWrapper(state = state)
            }
        }
        composeTestRule.onNodeWithText("BOT SPOT BTC").assertIsDisplayed()
        composeTestRule.onNodeWithText("BOT FUTUROS BTC").assertIsDisplayed()
    }

    @Test
    fun tradingScreen_showsBotActive_whenBotEnabled() {
        composeTestRule.setContent {
            BittickTheme {
                val state by MutableStateFlow(
                    TradingUiState(
                        spotBotStatus = BotStatusItem(
                            type = "spot", enabled = true, maxPositions = 5,
                            positionSizeUsdt = 50.0, minConfidence = 6,
                            openPositions = 2, totalPnl = 10.0,
                            balance = Balance(total = 500.0, available = 400.0)
                        ),
                        isPremium = true,
                        isFreeTier = false
                    )
                ).collectAsState()
                BoxTradingScreenWrapper(state = state)
            }
        }
        composeTestRule.onNodeWithText("ACTIVO").assertIsDisplayed()
    }

    @Test
    fun tradingScreen_showsBotInactive_whenBotDisabled() {
        composeTestRule.setContent {
            BittickTheme {
                val state by MutableStateFlow(
                    TradingUiState(
                        spotBotStatus = BotStatusItem(
                            type = "spot", enabled = false, maxPositions = 5,
                            positionSizeUsdt = 50.0, minConfidence = 6,
                            openPositions = 0, totalPnl = 0.0, balance = null
                        ),
                        isPremium = true,
                        isFreeTier = false
                    )
                ).collectAsState()
                BoxTradingScreenWrapper(state = state)
            }
        }
        composeTestRule.onNodeWithText("INACTIVO").assertIsDisplayed()
    }

    @Test
    fun tradingScreen_showsPositions_whenPositionsExist() {
        composeTestRule.setContent {
            BittickTheme {
                val state by MutableStateFlow(
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
                ).collectAsState()
                BoxTradingScreenWrapper(state = state)
            }
        }
        composeTestRule.onNodeWithText("BTCUSDT").assertIsDisplayed()
    }

    @Test
    fun tradingScreen_showsError_whenErrorExists() {
        composeTestRule.setContent {
            BittickTheme {
                val state by MutableStateFlow(
                    TradingUiState(error = "Error de conexion")
                ).collectAsState()
                BoxTradingScreenWrapper(state = state)
            }
        }
        composeTestRule.onNodeWithText("Error de conexion").assertIsDisplayed()
    }
}
