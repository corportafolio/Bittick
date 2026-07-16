package com.bittick.wallet

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bittick.data.cache.BittickImageCache
import com.bittick.data.preferences.BittickPreferences
import com.bittick.network.ApiClient
import com.bittick.network.InscriptionInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WalletState(
    val isConnecting: Boolean = false,
    val connectedAddress: String? = null,
    val inscriptions: List<InscriptionInfo> = emptyList(),
    val selectedInscription: InscriptionInfo? = null,
    val botImageUrl: String? = null,
    val error: String? = null,
    val isPremium: Boolean = false,
    val tier: String? = null,
    val botNumber: Int? = null,
    val verified: Boolean = false
)

@HiltViewModel
class WalletViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val imageCache: BittickImageCache,
    private val preferences: BittickPreferences
) : ViewModel() {

    private val _state = MutableStateFlow(WalletState())
    val state: StateFlow<WalletState> = _state.asStateFlow()

    private val deepLinkHandler = WalletDeepLinkHandler(context)

    fun onDeepLinkResponse(uri: Uri) {
        if (_state.value.isConnecting) {
            deepLinkHandler.handleResponse(uri)
        }
    }

    fun connectWallet() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isConnecting = true, error = null)

            try {
                val tempAddress = "pending"
                val nonceResponse = ApiClient.apiService.getNonce(tempAddress)
                if (!nonceResponse.isSuccessful || nonceResponse.body()?.exito != true) {
                    _state.value = _state.value.copy(
                        isConnecting = false,
                        error = "Error obteniendo nonce del servidor"
                    )
                    return@launch
                }

                val nonceData = nonceResponse.body()!!.data!!
                val message = nonceData.message
                val nonce = nonceData.nonce

                deepLinkHandler.requestSignature(message) { result ->
                    result.onSuccess { signature ->
                        viewModelScope.launch {
                            try {
                                val verifyBody = com.bittick.network.VerifyWalletRequest(
                                    address = tempAddress,
                                    signature = signature,
                                    nonce = nonce
                                )
                                val verifyResponse = ApiClient.apiService.verifyWallet(verifyBody)
                                if (!verifyResponse.isSuccessful || verifyResponse.body()?.exito != true) {
                                    _state.value = _state.value.copy(
                                        isConnecting = false,
                                        error = verifyResponse.body()?.error ?: "Error verificando wallet"
                                    )
                                    return@launch
                                }

                                val data = verifyResponse.body()!!.data!!
                                if (!data.verified) {
                                    _state.value = _state.value.copy(
                                        isConnecting = false,
                                        error = data.message ?: "Wallet no tiene Bittick Agent"
                                    )
                                    return@launch
                                }

                                val address = data.selectedInscriptionId?.let {
                                    preferences.getWalletAddress() ?: ""
                                } ?: ""

                                preferences.setWalletAddress(address)
                                preferences.setSelectedInscriptionId(data.selectedInscriptionId)
                                preferences.setIsPremium(data.tier == "FOUNDER")
                                preferences.setBotNumber(data.selectedBotNum)

                                _state.value = _state.value.copy(
                                    isConnecting = false,
                                    verified = true,
                                    connectedAddress = address,
                                    inscriptions = data.inscriptions ?: emptyList(),
                                    selectedInscription = data.inscriptions?.firstOrNull { it.selected == true },
                                    isPremium = data.tier == "FOUNDER",
                                    tier = data.tier,
                                    botNumber = data.selectedBotNum
                                )
                            } catch (e: Exception) {
                                _state.value = _state.value.copy(
                                    isConnecting = false,
                                    error = "Error de conexión: ${e.message}"
                                )
                            }
                        }
                    }
                    result.onFailure { e ->
                        viewModelScope.launch {
                            _state.value = _state.value.copy(
                                isConnecting = false,
                                error = e.message
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isConnecting = false,
                    error = "Error de conexión: ${e.message}"
                )
            }
        }
    }

    fun selectInscription(inscription: InscriptionInfo) {
        viewModelScope.launch {
            val address = _state.value.connectedAddress ?: return@launch
            try {
                val response = ApiClient.apiService.selectInscription(
                    address = address,
                    body = com.bittick.network.SelectInscriptionRequest(inscription.inscriptionId)
                )
                if (response.isSuccessful && response.body()?.exito == true) {
                    preferences.setSelectedInscriptionId(inscription.inscriptionId)
                    preferences.setIsPremium(inscription.tier == "FOUNDER")
                    preferences.setBotNumber(inscription.num)

                    _state.value = _state.value.copy(
                        selectedInscription = inscription,
                        isPremium = inscription.tier == "FOUNDER",
                        tier = inscription.tier,
                        botNumber = inscription.num
                    )
                    loadBotImage(inscription.inscriptionId)
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = "Error seleccionando inscripción: ${e.message}"
                )
            }
        }
    }

    private suspend fun loadBotImage(inscriptionId: String) {
        try {
            val result = imageCache.getImage(inscriptionId)
            result.onSuccess { base64 ->
                _state.value = _state.value.copy(botImageUrl = base64)
            }
        } catch (_: Exception) {}
    }

    fun disconnectWallet() {
        preferences.clearWalletData()
        imageCache.clearCache()
        _state.value = WalletState()
    }
}
