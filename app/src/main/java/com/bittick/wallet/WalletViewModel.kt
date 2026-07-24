package com.bittick.wallet

import android.content.Context
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bittick.data.cache.BittickImageCache
import com.bittick.data.preferences.BittickPreferences
import com.bittick.network.ApiClient
import com.bittick.network.InscriptionInfo
import com.bittick.network.VerifyWalletRequest
import com.bittick.wallet.WalletSessionManager.AuditResult
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

private const val TAG = "WalletVM"

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
    val verified: Boolean = false,
    // Nuevos estados para flujo manual Unisat (2 diálogos)
    val showConfirmationDialog: Boolean = false,      // Dialog 1: Confirmar conexión
    val showAddressInputDialog: Boolean = false,      // Dialog 2: Pegar dirección
    val pendingNonce: String? = null,
    val pendingSignature: String? = null,
    val tempAddressInput: String = "",                // Input temporal en Dialog 2
    // Mensaje temporal para auditoría
    val showTemporaryMessage: String? = null,
    // Preview temporal (solo UI, sin server ni prefs)
    val previewInscription: InscriptionInfo? = null,
    val previewBotImageUrl: String? = null
)

@HiltViewModel
class WalletViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val imageCache: BittickImageCache,
    private val preferences: BittickPreferences,
    private val sessionManager: WalletSessionManager
) : ViewModel() {

    private val _state = MutableStateFlow(WalletState())
    val state: StateFlow<WalletState> = _state.asStateFlow()

    private val deepLinkHandler = WalletDeepLinkHandler(context)

    // FLUJO MANUAL UNISAT (2 diálogos según doc 06)

    fun connectWallet() {
        log("CONEXIÓN: Iniciando flujo Unisat manual (nonce local)")
        viewModelScope.launch {
            _state.value = _state.value.copy(isConnecting = true, error = null)

            // Generar nonce local (no requiere servidor)
            val nonce = UUID.randomUUID().toString()

            // Guardar estado pendiente en prefs
            preferences.setPendingNonce(nonce)
            preferences.setPendingWalletType("unisat")

            // Abrir Unisat via deep link directamente (sin llamar al servidor)
            log("CONEXIÓN: Abriendo Unisat para firma (nonce=$nonce)")
            _state.value = _state.value.copy(pendingNonce = nonce)
            deepLinkHandler.requestSignature(nonce) { result ->
                result.onSuccess { signature ->
                    log("CONEXIÓN: Firma recibida de Unisat")
                    _state.value = _state.value.copy(
                        pendingSignature = signature,
                        showConfirmationDialog = true,  // DIALOG 1
                        isConnecting = false
                    )
                }
                result.onFailure { e ->
                    log("CONEXIÓN ERROR: ${e.message}")
                    preferences.clearPendingConnection()
                    _state.value = _state.value.copy(
                        isConnecting = false,
                        error = e.message
                    )
                }
            }
        }
    }

    fun onContinueConfirmation() {
        log("CONEXIÓN: Usuario tocó CONTINUAR -> mostrando Dialog 2 (Pegar dirección)")
        _state.value = _state.value.copy(
            showConfirmationDialog = false,
            showAddressInputDialog = true
        )
    }

    fun onAddressInputChange(address: String) {
        _state.value = _state.value.copy(tempAddressInput = address)
    }

    fun onConnectWithAddress() {
        val address = _state.value.tempAddressInput.trim()

        if (address.isBlank()) {
            _state.value = _state.value.copy(error = "Ingresa una dirección válida")
            return
        }

        log("CONEXIÓN: Verificando wallet en servidor (address=$address)")
        _state.value = _state.value.copy(isConnecting = true, showAddressInputDialog = false)

        viewModelScope.launch {
            try {
                // Verificar wallet en servidor (firma y nonce son opcionales para flujo manual)
                val verifyResponse = ApiClient.apiService.verifyWallet(VerifyWalletRequest(
                    address = address
                ))

                if (!verifyResponse.isSuccessful || verifyResponse.body()?.exito != true) {
                    log("CONEXIÓN ERROR: Verificación falló - ${verifyResponse.body()?.error}")
                    _state.value = _state.value.copy(
                        isConnecting = false,
                        error = verifyResponse.body()?.error ?: "Error verificando wallet"
                    )
                    return@launch
                }

                val data = verifyResponse.body()!!.data!!
                log("CONEXIÓN OK: Wallet verificada, inscripciones=${data.inscriptions?.size ?: 0}")

                // Descargar imagen del bot seleccionado y convertir a Base64
                val botImageBase64 = data.selectedInscriptionId?.let { id ->
                    downloadAndCacheBotImage(data.inscriptions?.firstOrNull { it.inscriptionId == id }?.num ?: 0)
                }

                // Guardar sesión 7 días con imagen Base64
                preferences.saveWalletSession(
                    address = address,
                    selectedInscriptionId = data.selectedInscriptionId!!,
                    botNumber = data.selectedBotNum!!,
                    tier = data.tier!!,
                    botImageBase64 = botImageBase64 ?: ""
                )
                preferences.clearPendingConnection()

                log("SESION GUARDADA: 7 días, bot=${data.selectedBotNum}, tier=${data.tier}")

                _state.value = _state.value.copy(
                    isConnecting = false,
                    showAddressInputDialog = false,
                    verified = true,
                    connectedAddress = address,
                    inscriptions = data.inscriptions ?: emptyList(),
                    selectedInscription = data.inscriptions?.firstOrNull { it.inscriptionId == data.selectedInscriptionId },
                    isPremium = true,
                    tier = data.tier,
                    botNumber = data.selectedBotNum,
                    botImageUrl = botImageBase64,
                    pendingNonce = null,
                    pendingSignature = null,
                    tempAddressInput = ""
                )

            } catch (e: Exception) {
                log("CONEXIÓN EXCEPTION: ${e.message}")
                _state.value = _state.value.copy(isConnecting = false, error = "Error: ${e.message}")
            }
        }
    }

    fun onDismissDialogs() {
        log("CONEXIÓN: Usuario canceló flujo")
        preferences.clearPendingConnection()
        _state.value = _state.value.copy(
            showConfirmationDialog = false,
            showAddressInputDialog = false,
            pendingNonce = null,
            pendingSignature = null,
            tempAddressInput = "",
            isConnecting = false
        )
    }

    // AUDITORÍA SEMANAL - verifica si la inscripción sigue en la wallet
    suspend fun runWeeklyAudit(): Boolean {
        val session = preferences.getWalletSession() ?: return false
        if (session.expiresAt > System.currentTimeMillis()) return true // No expirada aún

        log("AUDITORÍA PROGRAMADA: Sesión expirada, verificando inscripción ${session.selectedInscriptionId}")
        val result = sessionManager.auditSelectedInscription(session.address, session.selectedInscriptionId)

        when (result) {
            AuditResult.Success -> {
                log("AUDITORÍA OK: Sesión extendida 7 días más")
                _state.value = _state.value.copy(showTemporaryMessage = "Wallet verificada: 7 días más")
            }
            AuditResult.InscriptionSold -> {
                log("AUDITORÍA FALLÓ: Inscripción vendida, limpiando sesión")
                preferences.clearWalletSession()
                _state.value = WalletState().copy(showTemporaryMessage = "Inscripción vendida: reconecte wallet")
            }
            AuditResult.NetworkError -> {
                log("AUDITORÍA ERROR DE RED: Reintentando después")
                _state.value = _state.value.copy(showTemporaryMessage = "Error de red en verificación")
            }
        }
        return result == AuditResult.Success
    }

    // RESTAURAR SESIÓN AL INICIO
    fun restoreSessionIfValid() {
        val session = preferences.getWalletSession()
        if (session != null) {
            log("RESTAURANDO SESIÓN EXISTENTE: address=${session.address}, expiresIn=${session.daysUntilExpiry}d")
            if (session.expiresAt > System.currentTimeMillis()) {
                _state.value = sessionManager.restoreSession(session)
            } else {
                // Expirada -> disparar auditoría
                viewModelScope.launch { runWeeklyAudit() }
            }
        } else {
            // Sin sesión -> verificar si hay conexión pendiente (retorno manual Unisat)
            checkPendingConnection()
        }
    }

    fun checkPendingConnection() {
        val nonce = preferences.getPendingNonce()
        if (nonce != null) {
            log("RETORNO DETECTADO: Nonce pendiente encontrado -> mostrando Dialog 1")
            _state.value = _state.value.copy(
                showConfirmationDialog = true,
                pendingNonce = nonce
            )
        }
    }

    private suspend fun downloadAndCacheBotImage(botNum: Int): String? {
        return withContext(Dispatchers.IO) {
            try {
                val baseUrl = ApiClient.BASE_URL.trimEnd('/')
                val url = "$baseUrl/api/auth/bot-image/${botNum.toString().padStart(2, '0')}"
                val inputStream = java.net.URL(url).openStream()
                val bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
                inputStream.close()
                bitmap?.let {
                    val byteArrayOutputStream = java.io.ByteArrayOutputStream()
                    it.compress(android.graphics.Bitmap.CompressFormat.PNG, 90, byteArrayOutputStream)
                    Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.NO_WRAP)
                }
            } catch (e: Exception) {
                log("ERROR descargando imagen bot $botNum: ${e.message}")
                null
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
                    preferences.updateSelectedInscription(
                        selectedInscriptionId = inscription.inscriptionId,
                        botNumber = inscription.num,
                        tier = inscription.tier
                    )

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

    fun previewInscription(inscription: InscriptionInfo) {
        _state.value = _state.value.copy(
            previewInscription = inscription,
            previewBotImageUrl = null
        )
        viewModelScope.launch {
            try {
                val result = imageCache.getImage(inscription.inscriptionId)
                result.onSuccess { base64 ->
                    _state.value = _state.value.copy(previewBotImageUrl = base64)
                }
            } catch (_: Exception) {}
        }
    }

    fun confirmSelection(onComplete: () -> Unit = {}) {
        val preview = _state.value.previewInscription
        val address = _state.value.connectedAddress
        if (preview == null || address == null) {
            onComplete()
            return
        }
        val previewImageUrl = _state.value.previewBotImageUrl

        Log.d(TAG, "USAR presionado: Bot #${preview.num} | tier=${preview.tier} | inscriptionId=${preview.inscriptionId}")

        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.selectInscription(
                    address = address,
                    body = com.bittick.network.SelectInscriptionRequest(preview.inscriptionId)
                )
                if (response.isSuccessful && response.body()?.exito == true) {
                    Log.d(TAG, "Bot #${preview.num} seleccionado exitosamente")
                    preferences.updateSelectedInscription(
                        selectedInscriptionId = preview.inscriptionId,
                        botNumber = preview.num,
                        tier = preview.tier
                    )

                    _state.value = _state.value.copy(
                        selectedInscription = preview,
                        isPremium = preview.tier == "FOUNDER",
                        tier = preview.tier,
                        botNumber = preview.num,
                        botImageUrl = previewImageUrl,
                        previewInscription = null,
                        previewBotImageUrl = null
                    )
                    preferences.updateSessionImage(previewImageUrl ?: "")

                    // NUEVO: Fetch e imprimir niveles del bot seleccionado
                    try {
                        val levelsResponse = ApiClient.apiService.getAllBotLevels(preview.inscriptionId)
                        if (levelsResponse.isSuccessful && levelsResponse.body()?.exito == true) {
                            val data = levelsResponse.body()?.data
                            if (data != null) {
                                Log.d(TAG, "=== NIVELES BOT #${preview.num} (inscription=${preview.inscriptionId}) ===")
                                data.spot.forEach { l ->
                                    Log.d(TAG, "SPOT L${l.level}: score=${l.minScore} conf=${l.minConfidence} amt=\$${l.positionSizeUsdt} enabled=${l.isEnabled} updated=${l.updatedAt}")
                                }
                                data.futures.forEach { l ->
                                    Log.d(TAG, "FUT L${l.level}: score=${l.minScore} conf=${l.minConfidence} amt=\$${l.positionSizeUsdt} enabled=${l.isEnabled} updated=${l.updatedAt}")
                                }
                                Log.d(TAG, "=== FIN NIVELES ===")
                            }
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error fetching levels: ${e.message}", e)
                    }
                } else {
                    Log.e(TAG, "Bot #${preview.num} falló: code=${response.code()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Bot #${preview.num} error: ${e.message}")
                _state.value = _state.value.copy(
                    error = "Error confirmando selección: ${e.message}"
                )
            }
            onComplete()
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

    fun refreshInscriptions() {
        val address = _state.value.connectedAddress
        if (address != null && address.isNotBlank()) {
            viewModelScope.launch {
                try {
                    val response = ApiClient.apiService.fetchInscriptions(address)
                    if (response.isSuccessful && response.body()?.data != null) {
                        val inscriptions = response.body()!!.data!!.inscriptions
                        _state.value = _state.value.copy(inscriptions = inscriptions)
                        log("INSCRIPCIONES REFRESCADAS: ${inscriptions.size}")
                    }
                } catch (e: Exception) {
                    log("ERROR refrescando inscripciones: ${e.message}")
                }
            }
        }
    }

    fun disconnectWallet() {
        log("DISCONNECT: Desconectando wallet")
        preferences.clearWalletSession()
        imageCache.clearCache()
        _state.value = WalletState()
    }

    private fun log(message: String) {
        Log.d(TAG, message)
    }
}
