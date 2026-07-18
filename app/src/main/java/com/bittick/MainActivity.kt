package com.bittick

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bittick.data.preferences.BittickPreferences
import com.bittick.ui.settings.SettingsScreen
import com.bittick.ui.trading.TradingScreen
import com.bittick.ui.theme.BittickTheme
import com.bittick.wallet.InscriptionPickerScreen
import com.bittick.wallet.WalletScreen
import com.bittick.wallet.WalletViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var walletViewModel: WalletViewModel? = null
    @Inject lateinit var preferences: BittickPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BittickTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val walletViewModel: WalletViewModel = hiltViewModel()
                    this.walletViewModel = walletViewModel
                    val walletState by walletViewModel.state.collectAsState()
                    val tradingViewModel: com.bittick.ui.trading.TradingViewModel = hiltViewModel()

                    // Detectar retorno de UniSat (manual) en cada ON_RESUME
                    DisposableEffect(Unit) {
                        val observer = LifecycleEventObserver { _, event ->
                            if (event == Lifecycle.Event.ON_RESUME) {
                                walletViewModel.restoreSessionIfValid()
                            }
                        }
                        lifecycle.addObserver(observer)
                        onDispose { lifecycle.removeObserver(observer) }
                    }

                    NavHost(navController = navController, startDestination = "trading") {
                        composable("trading") {
                            TradingScreen(
                                onSettingsClick = { navController.navigate("settings") },
                                onWalletClick = { navController.navigate("wallet") },
                                walletAddress = walletState.connectedAddress ?: preferences.getWalletAddress(),
                                botImageUrl = walletState.botImageUrl
                            )
                        }
                        composable("settings") {
                            SettingsScreen(
                                onBack = { navController.popBackStack() },
                                onNavigateToWallet = { navController.navigate("wallet") }
                            )
                        }
                        composable("wallet") {
                            WalletScreen(
                                walletState = walletState,
                                onConnectWallet = { walletViewModel.connectWallet() },
                                onPreviewInscription = { walletViewModel.previewInscription(it) },
                                onConfirmSelection = { 
                                    walletViewModel.confirmSelection()
                                    tradingViewModel.refreshPremiumStatus()
                                },
                                onDisconnectWallet = { walletViewModel.disconnectWallet() },
                                onDismiss = { navController.popBackStack() },
                                onContinueConfirmation = { walletViewModel.onContinueConfirmation() },
                                onAddressInputChange = { walletViewModel.onAddressInputChange(it) },
                                onConnectWithAddress = { walletViewModel.onConnectWithAddress() },
                                onDismissDialogs = { walletViewModel.onDismissDialogs() },
                                onRefreshInscriptions = { walletViewModel.refreshInscriptions() }
                            )
                        }
                        composable("inscription_picker") {
                            InscriptionPickerScreen(
                                inscriptions = walletState.inscriptions,
                                selectedInscription = walletState.selectedInscription,
                                onSelectInscription = { walletViewModel.selectInscription(it) },
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent.data?.let { uri ->
            if (uri.scheme == "unisat" && uri.host == "response") {
                walletViewModel?.checkPendingConnection()
            }
        }
    }
}
