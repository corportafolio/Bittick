package com.bittick

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.bittick.network.InscriptionInfo
import com.bittick.wallet.WalletScreen
import com.bittick.wallet.WalletState
import com.bittick.ui.theme.BittickTheme
import org.junit.Rule
import org.junit.Test

class WalletScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun walletScreen_showsTitle() {
        composeTestRule.setContent {
            BittickTheme {
                WalletScreen(
                    walletState = WalletState(),
                    onConnectWallet = {},
                    onPreviewInscription = {},
                    onConfirmSelection = {},
                    onDisconnectWallet = {},
                    onDismiss = {}
                )
            }
        }
        composeTestRule.onNodeWithText("Cuenta Bittick").assertIsDisplayed()
    }

    @Test
    fun walletScreen_showsConnectButton_whenDisconnected() {
        composeTestRule.setContent {
            BittickTheme {
                WalletScreen(
                    walletState = WalletState(),
                    onConnectWallet = {},
                    onPreviewInscription = {},
                    onConfirmSelection = {},
                    onDisconnectWallet = {},
                    onDismiss = {}
                )
            }
        }
        composeTestRule.onNodeWithText("CONECTAR WALLET").assertIsDisplayed()
    }

    @Test
    fun walletScreen_connectButton_isClickable() {
        var connectClicked = false
        composeTestRule.setContent {
            BittickTheme {
                WalletScreen(
                    walletState = WalletState(),
                    onConnectWallet = { connectClicked = true },
                    onPreviewInscription = {},
                    onConfirmSelection = {},
                    onDisconnectWallet = {},
                    onDismiss = {}
                )
            }
        }
        composeTestRule.onNodeWithText("CONECTAR WALLET").performClick()
        assert(connectClicked) { "Connect button should be clickable" }
    }

    @Test
    fun walletScreen_showsAddress_whenConnected() {
        val address = "bc1pha4hfrgkjqrr6mj26zvrkyflcexkyxstmq30dn5w0l9afekcdunqc60nyz"
        composeTestRule.setContent {
            BittickTheme {
                WalletScreen(
                    walletState = WalletState(
                        connectedAddress = address,
                        verified = true,
                        isPremium = true,
                        tier = "FOUNDER",
                        botNumber = 88
                    ),
                    onConnectWallet = {},
                    onPreviewInscription = {},
                    onConfirmSelection = {},
                    onDisconnectWallet = {},
                    onDismiss = {}
                )
            }
        }
        composeTestRule.onNodeWithText(address).assertIsDisplayed()
    }

    @Test
    fun walletScreen_showsDisconnectButton_whenConnected() {
        composeTestRule.setContent {
            BittickTheme {
                WalletScreen(
                    walletState = WalletState(
                        connectedAddress = "bc1ptest123",
                        verified = true
                    ),
                    onConnectWallet = {},
                    onPreviewInscription = {},
                    onConfirmSelection = {},
                    onDisconnectWallet = {},
                    onDismiss = {}
                )
            }
        }
        composeTestRule.onNodeWithText("DESCONECTAR").assertIsDisplayed()
    }

    @Test
    fun walletScreen_disconnectButton_isClickable() {
        var disconnectClicked = false
        composeTestRule.setContent {
            BittickTheme {
                WalletScreen(
                    walletState = WalletState(
                        connectedAddress = "bc1ptest123",
                        verified = true
                    ),
                    onConnectWallet = {},
                    onPreviewInscription = {},
                    onConfirmSelection = {},
                    onDisconnectWallet = { disconnectClicked = true },
                    onDismiss = {}
                )
            }
        }
        composeTestRule.onNodeWithText("DESCONECTAR").performClick()
        assert(disconnectClicked) { "Disconnect button should be clickable" }
    }

    @Test
    fun walletScreen_showsSelectedBot_whenSelected() {
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
                        selectedInscription = inscription,
                        botNumber = 88,
                        tier = "FOUNDER",
                        isPremium = true
                    ),
                    onConnectWallet = {},
                    onPreviewInscription = {},
                    onConfirmSelection = {},
                    onDisconnectWallet = {},
                    onDismiss = {}
                )
            }
        }
        composeTestRule.onNodeWithText("Bot #88").assertIsDisplayed()
    }

    @Test
    fun walletScreen_showsInscriptions_whenLoaded() {
        val inscriptions = listOf(
            InscriptionInfo(inscriptionId = "insc-001", num = 1, tier = "COMMUNITY", botImageUrl = null),
            InscriptionInfo(inscriptionId = "insc-002", num = 42, tier = "FOUNDER", botImageUrl = null),
            InscriptionInfo(inscriptionId = "insc-003", num = 88, tier = "FOUNDER", botImageUrl = null)
        )
        composeTestRule.setContent {
            BittickTheme {
                WalletScreen(
                    walletState = WalletState(
                        connectedAddress = "bc1ptest123",
                        verified = true,
                        inscriptions = inscriptions
                    ),
                    onConnectWallet = {},
                    onPreviewInscription = {},
                    onConfirmSelection = {},
                    onDisconnectWallet = {},
                    onDismiss = {}
                )
            }
        }
        composeTestRule.onNodeWithText("Bot #1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Bot #42").assertIsDisplayed()
        composeTestRule.onNodeWithText("Bot #88").assertIsDisplayed()
    }

    @Test
    fun walletScreen_showsPreview_whenInscriptionPreviewed() {
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
    fun walletScreen_usarButton_isClickable() {
        val inscription = InscriptionInfo(
            inscriptionId = "insc-001", num = 88, tier = "FOUNDER",
            botImageUrl = null
        )
        var confirmClicked = false
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
                    onConfirmSelection = { confirmClicked = true },
                    onDisconnectWallet = {},
                    onDismiss = {}
                )
            }
        }
        composeTestRule.onNodeWithText("USAR").performClick()
        assert(confirmClicked) { "USAR button should be clickable" }
    }

    @Test
    fun walletScreen_showsCloseButton() {
        var dismissed = false
        composeTestRule.setContent {
            BittickTheme {
                WalletScreen(
                    walletState = WalletState(),
                    onConnectWallet = {},
                    onPreviewInscription = {},
                    onConfirmSelection = {},
                    onDisconnectWallet = {},
                    onDismiss = { dismissed = true }
                )
            }
        }
        composeTestRule.onNodeWithText("✕").performClick()
        assert(dismissed) { "Close button should call onDismiss" }
    }
}
