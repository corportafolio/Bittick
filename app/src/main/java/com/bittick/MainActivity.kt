package com.bittick

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bittick.ui.settings.SettingsScreen
import com.bittick.ui.trading.TradingScreen
import com.bittick.ui.theme.BittickTheme
import com.bittick.wallet.InscriptionPickerScreen
import com.bittick.wallet.WalletScreen
import com.bittick.wallet.WalletViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var walletViewModel: WalletViewModel? = null

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

                    NavHost(navController = navController, startDestination = "trading") {
                        composable("trading") {
                            TradingScreen(
                                onSettingsClick = { navController.navigate("settings") }
                            )
                        }
                        composable("settings") {
                            SettingsScreen(
                                onBack = { navController.popBackStack() },
                                onNavigateToWallet = { navController.navigate("wallet") }
                            )
                        }
                        composable("wallet") {
                            val walletState by walletViewModel.state.collectAsState()
                            WalletScreen(
                                walletState = walletState,
                                onConnectWallet = { walletViewModel.connectWallet() },
                                onSelectInscription = { walletViewModel.selectInscription(it) },
                                onDisconnectWallet = { walletViewModel.disconnectWallet() },
                                onDismiss = { navController.popBackStack() }
                            )
                        }
                        composable("inscription_picker") {
                            val walletState by walletViewModel.state.collectAsState()
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
                walletViewModel?.onDeepLinkResponse(uri)
            }
        }
    }
}
