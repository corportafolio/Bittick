package com.bittick.wallet

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bittick.data.cache.BittickImageCache
import com.bittick.data.preferences.BittickPreferences
import com.bittick.network.ApiClient
import com.bittick.network.InscriptionInfo
import com.bittick.network.VerifyWalletRequest
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

    private var pendingNonce: String? = null
    private var pendingMessage: String? = null
    private var connectStep: ConnectStep = ConnectStep.IDLE

    private enum class ConnectStep { IDLE, SIGNING, GETTING_ADDRESS, VERIFYING }

    fun onDeepLinkResponse(uri: Uri) {
        when (connectStep) {
            ConnectStep.SIGNING, ConnectStep.GETTING_ADDRESS -> {
                deepLinkHandler.handleResponse(uri)
            }
            else -> {}
        }
    }

    fun connectWallet() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isConnecting = true, error = null)
            connectStep = ConnectStep.IDLE

            try {
                val nonceResponse = ApiClient.apiService.getNonce("pending")
                if (!nonceResponse.isSuccessful || nonceResponse.body()?.exito != true) {
                    _state.value = _state.value.copy(
                        isConnecting = false,
                        error = "Error obteniendo nonce del servidor"
                    )
                    return@launch
                }

                val nonceData = nonceResponse.body()!!.data!!
                pendingNonce = nonceData.nonce
                pendingMessage = nonceData.message

                connectStep = ConnectStep.SIGNING
                deepLinkHandler.requestSignature(nonceData.nonce) { result ->
                    result.onSuccess { signature ->
                        pendingNonce = null
                        pendingMessage = null
                        getAddressesAndVerify(signature)
                    }
                    result.onFailure { e ->
                        viewModelScope.launch {
                            connectStep = ConnectStep.IDLE
                            _state.value = _state.value.copy(
                                isConnecting = false,
                                error = e.message
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                connectStep = ConnectStep.IDLE
                _state.value = _state.value.copy(
                    isConnecting = false,
                    error = "Error de conexion: ${e.message}"
                )
            }
        }
    }

    private fun getAddressesAndVerify(signature: String) {
        viewModelScope.launch {
            try {
                val addressNonceResponse = ApiClient.apiService.getNonce("getaddr")
                if (!addressNonceResponse.isSuccessful || addressNonceResponse.body()?.exito != true) {
                    _state.value = _state.value.copy(
                        isConnecting = false,
                        error = "Error obteniendo nonce para direccion"
                    )
                    connectStep = ConnectStep.IDLE
                    return@launch
                }

                val addrNonce = addressNonceResponse.body()!!.data!!.nonce
                connectStep = ConnectStep.GETTING_ADDRESS
                deepLinkHandler.requestAddresses(addrNonce) { result ->
                    result.onSuccess { address ->
                        connectStep = ConnectStep.VERIFYING
                        viewModelScope.launch {
                            verifyOnServer(address, signature)
                        }
                    }
                    result.onFailure { e ->
                        viewModelScope.launch {
                            connectStep = ConnectStep.IDLE
                            _state.value = _state.value.copy(
                                isConnecting = false,
                                error = "Error obteniendo direccion: ${e.message}"
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                connectStep = ConnectStep.IDLE
                _state.value = _state.value.copy(
                    isConnecting = false,
                    error = "Error de conexion: ${e.message}"
                )
            }
        }
    }

    private suspend fun verifyOnServer(address: String, signature: String) {
        try {
            val verifyBody = VerifyWalletRequest(
                address = address,
                signature = signature,
                nonce = pendingNonce ?: ""
            )
            val verifyResponse = ApiClient.apiService.verifyWallet(verifyBody)
            if (!verifyResponse.isSuccessful || verifyResponse.body()?.exito != true) {
                connectStep = ConnectStep.IDLE
                _state.value = _state.value.copy(
                    isConnecting = false,
                    error = verifyResponse.body()?.error ?: "Error verificando wallet"
                )
                return
            }

            val data = verifyResponse.body()!!.data!!
            if (!data.verified) {
                connectStep = ConnectStep.IDLE
                _state.value = _state.value.copy(
                    isConnecting = false,
                    error = data.message ?: "Wallet no tiene Bittick Agent"
                )
                return
            }

            preferences.setWalletAddress(address)
            preferences.setSelectedInscriptionId(data.selectedInscriptionId)
            preferences.setIsPremium(data.tier == "FOUNDER")
            preferences.setBotNumber(data.selectedBotNum)

            connectStep = ConnectStep.IDLE
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

            data.selectedInscriptionId?.let { loadBotImage(it) }
        } catch (e: Exception) {
            connectStep = ConnectStep.IDLE
            _state.value = _state.value.copy(
                isConnecting = false,
                error = "Error de conexion: ${e.message}"
            )
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
                    error = "Error seleccionando inscripcion: ${e.message}"
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
