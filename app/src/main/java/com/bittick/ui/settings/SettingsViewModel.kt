package com.bittick.ui.settings

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
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

data class SettingsUiState(
    val hasNotificationPermission: Boolean = true,
    val walletAddress: String? = null,
    val selectedInscription: InscriptionInfo? = null,
    val inscriptions: List<InscriptionInfo> = emptyList(),
    val botImageUrl: String? = null,
    val isPremium: Boolean = false,
    val tier: String? = null,
    val botNumber: Int? = null,
    val spotEnabled: Boolean = true,
    val futuresEnabled: Boolean = true,
    val spotPositionSize: Double = 10.0,
    val futuresPositionSize: Double = 10.0,
    val spotMaxPositions: Int = 5,
    val futuresMaxPositions: Int = 5,
    val spotMinScore: Int = 6,
    val futuresMinScore: Int = 7,
    val spotLevels: List<com.bittick.network.LevelConfig> = defaultLevels(),
    val futuresLevels: List<com.bittick.network.LevelConfig> = defaultLevels(),
    val spotExpanded: Boolean = false,
    val futuresExpanded: Boolean = false,
    val spotApiKeyMasked: String? = null,
    val spotApiKeyHasKey: Boolean = false,
    val spotApiKeyEditing: Boolean = false,
    val spotApiKeyInput: String = "",
    val spotApiSecretInput: String = "",
    val futuresApiKeyMasked: String? = null,
    val futuresApiKeyHasKey: Boolean = false,
    val futuresApiKeyEditing: Boolean = false,
    val futuresApiKeyInput: String = "",
    val futuresApiSecretInput: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val apiKeyMessage: String? = null
)

fun defaultLevels() = listOf(
    com.bittick.network.LevelConfig(10, true, 10.0, 10, 10, 3),
    com.bittick.network.LevelConfig(9, true, 20.0, 9, 9, 3),
    com.bittick.network.LevelConfig(8, true, 40.0, 8, 8, 3),
    com.bittick.network.LevelConfig(7, true, 20.0, 7, 7, 2),
    com.bittick.network.LevelConfig(6, true, 10.0, 6, 6, 1)
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferences: BittickPreferences,
    private val imageCache: BittickImageCache
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    init {
        refreshPermissions()
        loadWalletState()
    }

    fun refreshWalletState() {
        loadWalletState()
    }

    fun hasNotificationPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun refreshPermissions() {
        _state.value = _state.value.copy(
            hasNotificationPermission = hasNotificationPermission()
        )
    }

    fun testNotification() {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(context, "bittick_trading")
            .setContentTitle("Prueba de notificacion")
            .setContentText("Si ves esto, las notificaciones funcionan correctamente")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        nm.notify(2001, notification)
    }

    private fun loadWalletState() {
        val address = preferences.getWalletAddress() ?: return
        val session = preferences.getWalletSession()
        val inscriptionId = preferences.getSelectedInscriptionId()
        val isPremium = preferences.getIsPremium()
        val botNumber = preferences.getBotNumber()

        _state.value = _state.value.copy(
            walletAddress = address,
            isPremium = isPremium,
            botNumber = botNumber,
            botImageUrl = session?.botImageBase64?.takeIf { it.isNotBlank() }
        )

        if (inscriptionId != null) {
            loadInscriptions(address)
            loadPreferences(inscriptionId)
            loadLevelConfigs(inscriptionId)
        }
    }

    fun loadInscriptions(address: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val response = ApiClient.apiService.getWalletInscriptions(address)
                if (response.isSuccessful && response.body()?.exito == true) {
                    val data = response.body()!!.data!!
                    val inscriptions = data.inscriptions
                    _state.value = _state.value.copy(
                        inscriptions = inscriptions,
                        selectedInscription = inscriptions.firstOrNull { it.selected == true },
                        tier = data.tier,
                        botNumber = data.selectedBotNum
                    )
                }
                _state.value = _state.value.copy(isLoading = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Error cargando inscripciones: ${e.message}"
                )
            }
        }
    }

    fun selectInscription(inscription: InscriptionInfo) {
        viewModelScope.launch {
            val address = _state.value.walletAddress ?: return@launch
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
                    loadPreferences(inscription.inscriptionId)
                    loadLevelConfigs(inscription.inscriptionId)
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = "Error seleccionando inscripción: ${e.message}"
                )
            }
        }
    }

    private fun loadPreferences(inscriptionId: String) {
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.getInscriptionPreferences(inscriptionId)
                if (response.isSuccessful && response.body()?.exito == true) {
                    val prefs = response.body()!!.data ?: return@launch
                    _state.value = _state.value.copy(
                        spotEnabled = prefs.spot_enabled,
                        futuresEnabled = prefs.futures_enabled,
                        spotPositionSize = prefs.spot_position_size,
                        futuresPositionSize = prefs.futures_position_size,
                        spotMaxPositions = prefs.spot_max_positions,
                        futuresMaxPositions = prefs.futures_max_positions,
                        spotMinScore = prefs.spot_min_score,
                        futuresMinScore = prefs.futures_min_score
                    )
                }
            } catch (_: Exception) {}
        }
    }

    fun savePreferences() {
        viewModelScope.launch {
            val inscriptionId = _state.value.selectedInscription?.inscriptionId ?: return@launch
            val address = _state.value.walletAddress ?: return@launch

            try {
                val body = com.bittick.network.SavePreferencesRequest(
                    inscriptionId = inscriptionId,
                    address = address,
                    spot_enabled = _state.value.spotEnabled,
                    futures_enabled = _state.value.futuresEnabled,
                    spot_position_size = _state.value.spotPositionSize,
                    futures_position_size = _state.value.futuresPositionSize,
                    spot_max_positions = _state.value.spotMaxPositions,
                    futures_max_positions = _state.value.futuresMaxPositions,
                    spot_min_score = _state.value.spotMinScore,
                    futures_min_score = _state.value.futuresMinScore
                )
                val response = ApiClient.apiService.saveInscriptionPreferences(body)
                if (response.isSuccessful && response.body()?.exito == true) {
                    _state.value = _state.value.copy(error = null)
                } else {
                    _state.value = _state.value.copy(error = "Error guardando preferencias")
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = "Error guardando preferencias: ${e.message}")
            }
        }
    }

    fun updateSpotEnabled(enabled: Boolean) {
        _state.value = _state.value.copy(spotEnabled = enabled)
        savePreferences()
    }

    fun updateFuturesEnabled(enabled: Boolean) {
        _state.value = _state.value.copy(futuresEnabled = enabled)
        savePreferences()
    }

    fun updateSpotPositionSize(size: Double) {
        _state.value = _state.value.copy(spotPositionSize = size)
        savePreferences()
    }

    fun updateFuturesPositionSize(size: Double) {
        _state.value = _state.value.copy(futuresPositionSize = size)
        savePreferences()
    }

    fun updateSpotMaxPositions(max: Int) {
        _state.value = _state.value.copy(spotMaxPositions = max)
        savePreferences()
    }

    fun updateFuturesMaxPositions(max: Int) {
        _state.value = _state.value.copy(futuresMaxPositions = max)
        savePreferences()
    }

    fun updateSpotMinScore(score: Int) {
        _state.value = _state.value.copy(spotMinScore = score)
        savePreferences()
    }

    fun updateFuturesMinScore(score: Int) {
        _state.value = _state.value.copy(futuresMinScore = score)
        savePreferences()
    }

    fun toggleSpotExpanded() {
        _state.value = _state.value.copy(spotExpanded = !_state.value.spotExpanded)
    }

    fun toggleFuturesExpanded() {
        _state.value = _state.value.copy(futuresExpanded = !_state.value.futuresExpanded)
    }

    fun updateSpotLevel(level: Int, field: String, value: Any) {
        val levels = _state.value.spotLevels.map {
            if (it.level == level) {
                when (field) {
                    "enabled" -> it.copy(enabled = value as Boolean)
                    "amount" -> it.copy(position_size_usdt = value as Double)
                    "min_score" -> it.copy(min_score = value as Int)
                    "min_confidence" -> it.copy(min_confidence = value as Int)
                    "leverage" -> it.copy(leverage = value as Int)
                    else -> it
                }
            } else it
        }
        _state.value = _state.value.copy(spotLevels = levels)
    }

    fun updateFuturesLevel(level: Int, field: String, value: Any) {
        val levels = _state.value.futuresLevels.map {
            if (it.level == level) {
                when (field) {
                    "enabled" -> it.copy(enabled = value as Boolean)
                    "amount" -> it.copy(position_size_usdt = value as Double)
                    "min_score" -> it.copy(min_score = value as Int)
                    "min_confidence" -> it.copy(min_confidence = value as Int)
                    "leverage" -> it.copy(leverage = value as Int)
                    else -> it
                }
            } else it
        }
        _state.value = _state.value.copy(futuresLevels = levels)
    }

    fun saveLevelConfigs(mode: String) {
        viewModelScope.launch {
            val inscriptionId = _state.value.selectedInscription?.inscriptionId ?: return@launch
            val levels = if (mode == "spot") _state.value.spotLevels else _state.value.futuresLevels
            try {
                val body = com.bittick.network.LevelConfigsRequest(
                    inscription_id = inscriptionId,
                    mode = mode,
                    levels = levels
                )
                val response = ApiClient.apiService.saveLevelConfigs(body)
                if (response.isSuccessful && response.body()?.exito == true) {
                    _state.value = _state.value.copy(error = null)
                } else {
                    _state.value = _state.value.copy(error = "Error guardando preferencias de $mode")
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = "Error guardando: ${e.message}")
            }
        }
    }

    private fun loadLevelConfigs(inscriptionId: String) {
        viewModelScope.launch {
            try {
                val spotResponse = ApiClient.apiService.getLevelConfigs(inscriptionId, "spot")
                if (spotResponse.isSuccessful && spotResponse.body()?.exito == true) {
                    val data = spotResponse.body()?.data
                    if (data != null && data.isNotEmpty()) {
                        _state.value = _state.value.copy(spotLevels = data)
                    }
                }
                val futuresResponse = ApiClient.apiService.getLevelConfigs(inscriptionId, "futures")
                if (futuresResponse.isSuccessful && futuresResponse.body()?.exito == true) {
                    val data = futuresResponse.body()?.data
                    if (data != null && data.isNotEmpty()) {
                        _state.value = _state.value.copy(futuresLevels = data)
                    }
                }
            } catch (_: Exception) {}
        }
        loadApiKey("spot")
        loadApiKey("futures")
    }

    private fun loadApiKey(mode: String) {
        val address = _state.value.walletAddress ?: return
        val inscriptionId = _state.value.selectedInscription?.inscriptionId ?: return
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.getBotApiKey(address, inscriptionId, mode)
                if (response.isSuccessful && response.body()?.exito == true) {
                    val data = response.body()?.data
                    if (mode == "spot") {
                        _state.value = _state.value.copy(
                            spotApiKeyMasked = if (data?.has_key == true) data.api_key else null,
                            spotApiKeyHasKey = data?.has_key == true,
                            spotApiKeyEditing = false,
                            spotApiKeyInput = "",
                            spotApiSecretInput = ""
                        )
                    } else {
                        _state.value = _state.value.copy(
                            futuresApiKeyMasked = if (data?.has_key == true) data.api_key else null,
                            futuresApiKeyHasKey = data?.has_key == true,
                            futuresApiKeyEditing = false,
                            futuresApiKeyInput = "",
                            futuresApiSecretInput = ""
                        )
                    }
                }
            } catch (_: Exception) {}
        }
    }

    fun toggleSpotApiKeyEditing() {
        val s = _state.value
        _state.value = s.copy(
            spotApiKeyEditing = !s.spotApiKeyEditing,
            spotApiKeyInput = "",
            spotApiSecretInput = ""
        )
    }

    fun toggleFuturesApiKeyEditing() {
        val s = _state.value
        _state.value = s.copy(
            futuresApiKeyEditing = !s.futuresApiKeyEditing,
            futuresApiKeyInput = "",
            futuresApiSecretInput = ""
        )
    }

    fun updateSpotApiKeyInput(value: String) {
        _state.value = _state.value.copy(spotApiKeyInput = value)
    }

    fun updateSpotApiSecretInput(value: String) {
        _state.value = _state.value.copy(spotApiSecretInput = value)
    }

    fun updateFuturesApiKeyInput(value: String) {
        _state.value = _state.value.copy(futuresApiKeyInput = value)
    }

    fun updateFuturesApiSecretInput(value: String) {
        _state.value = _state.value.copy(futuresApiSecretInput = value)
    }

    fun saveApiKey(mode: String) {
        val address = _state.value.walletAddress ?: return
        val inscriptionId = _state.value.selectedInscription?.inscriptionId ?: return
        val apiKey = if (mode == "spot") _state.value.spotApiKeyInput else _state.value.futuresApiKeyInput
        val apiSecret = if (mode == "spot") _state.value.spotApiSecretInput else _state.value.futuresApiSecretInput

        if (apiKey.isBlank() || apiSecret.isBlank()) return

        viewModelScope.launch {
            try {
                val body = com.bittick.network.BotApiKeyRequest(
                    inscription_id = inscriptionId,
                    mode = mode,
                    api_key = apiKey,
                    api_secret = apiSecret
                )
                val response = ApiClient.apiService.saveBotApiKey(address, body)
                if (response.isSuccessful && response.body()?.exito == true) {
                    _state.value = _state.value.copy(apiKeyMessage = "API key guardada para $mode")
                    loadApiKey(mode)
                    if (mode == "spot" && !_state.value.spotEnabled) {
                        _state.value = _state.value.copy(spotEnabled = true)
                        savePreferences()
                    } else if (mode == "futures" && !_state.value.futuresEnabled) {
                        _state.value = _state.value.copy(futuresEnabled = true)
                        savePreferences()
                    }
                } else {
                    _state.value = _state.value.copy(error = "Error guardando API key")
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = "Error guardando API key: ${e.message}")
            }
        }
    }

    fun deleteApiKey(mode: String) {
        val address = _state.value.walletAddress ?: return
        val inscriptionId = _state.value.selectedInscription?.inscriptionId ?: return
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.deleteBotApiKey(address, inscriptionId, mode)
                if (response.isSuccessful && response.body()?.exito == true) {
                    _state.value = _state.value.copy(apiKeyMessage = "API key eliminada de $mode")
                    loadApiKey(mode)
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = "Error eliminando API key: ${e.message}")
            }
        }
    }

    fun clearApiKeyMessage() {
        _state.value = _state.value.copy(apiKeyMessage = null)
    }

    fun disconnectWallet() {
        preferences.clearWalletData()
        imageCache.clearCache()
        _state.value = SettingsUiState(
            hasNotificationPermission = hasNotificationPermission()
        )
    }
}
