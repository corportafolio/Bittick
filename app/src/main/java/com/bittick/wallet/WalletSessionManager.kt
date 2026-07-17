package com.bittick.wallet

import android.util.Log
import com.bittick.data.preferences.BittickPreferences
import com.bittick.network.ApiService
import com.bittick.network.FetchInscriptionsResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WalletSessionManager @Inject constructor(
    @ApplicationContext private val context: android.content.Context,
    private val prefs: BittickPreferences,
    private val api: ApiService
) {

    private val TAG = "WalletSessionManager"

    sealed class AuditResult {
        object Success : AuditResult()
        object InscriptionSold : AuditResult()
        object NetworkError : AuditResult()
    }

    // Auditoría semanal: verifica si la wallet aún tiene la inscripción seleccionada
    suspend fun auditSelectedInscription(
        address: String,
        inscriptionId: String
    ): AuditResult {
        log("AUDITORÍA: Iniciando - address=$address, inscriptionId=$inscriptionId")
        return try {
            val response = api.fetchInscriptions(address)
            if (!response.isSuccessful) {
                log("AUDITORÍA ERROR: Server response code=${response.code()}")
                AuditResult.NetworkError
            } else {
                val body = response.body()
                val stillHasIt = body?.data?.inscriptions?.any { it.inscriptionId == inscriptionId } == true
                if (stillHasIt) {
                    log("AUDITORÍA OK: Inscripción $inscriptionId aún en wallet $address → extiende 7 días")
                    prefs.extendSessionExpiry(7)
                    AuditResult.Success
                } else {
                    log("AUDITORÍA FALLÓ: Inscripción $inscriptionId NO en wallet $address → limpia sesión")
                    prefs.clearWalletSession()
                    AuditResult.InscriptionSold
                }
            }
        } catch (e: Exception) {
            log("AUDITORÍA EXCEPTION: ${e.message}")
            AuditResult.NetworkError
        }
    }

    // Restaura sesión completa a WalletState
    fun restoreSession(session: BittickPreferences.WalletSession): WalletState {
        log("RESTAURANDO SESIÓN: address=${session.address}, bot=${session.botNumber}, tier=${session.tier}, expiresIn=${session.daysUntilExpiry}d")
        return WalletState(
            isConnecting = false,
            connectedAddress = session.address,
            inscriptions = emptyList(),
            selectedInscription = null,
            botImageUrl = session.botImageBase64,
            error = null,
            isPremium = true,
            tier = session.tier,
            botNumber = session.botNumber,
            verified = true,
            showConfirmationDialog = false,
            showAddressInputDialog = false,
            pendingNonce = null,
            pendingSignature = null,
            tempAddressInput = ""
        )
    }

    private fun log(message: String) {
        Log.d(TAG, message)
    }
}