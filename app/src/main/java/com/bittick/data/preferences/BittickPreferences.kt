package com.bittick.data.preferences

import android.content.Context
import com.bittick.network.InscriptionInfo
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BittickPreferences @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs = context.getSharedPreferences("bittick_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val inscriptionListType = object : TypeToken<List<InscriptionInfo>>() {}.type

    companion object {
        private const val SESSION_DAYS = 7L
        private const val MS_PER_DAY = 24 * 60 * 60 * 1000L
        
        private const val PREFS_NAME = "bittick_prefs"
        private const val KEY_TRADING_LAST_CREATED = "trading_last_created_at"
        private const val KEY_WALLET_ADDRESS = "wallet_address"
        private const val KEY_SELECTED_INSCRIPTION = "selected_inscription_id"
        private const val KEY_IS_PREMIUM = "is_premium"
        private const val KEY_BOT_NUMBER = "bot_number"
        private const val KEY_WALLET_SESSION = "wallet_session"
        private const val KEY_PENDING_NONCE = "pending_nonce"
        private const val KEY_PENDING_WALLET_TYPE = "pending_wallet_type"
    }

    fun getTradingLastCreatedAt(): String? = prefs.getString(KEY_TRADING_LAST_CREATED, null)

    fun setTradingLastCreatedAt(value: String) {
        prefs.edit().putString(KEY_TRADING_LAST_CREATED, value).apply()
    }

    // Wallet preferences
    fun getWalletAddress(): String? = prefs.getString(KEY_WALLET_ADDRESS, null)

    fun setWalletAddress(address: String?) {
        if (address == null) {
            prefs.edit().remove(KEY_WALLET_ADDRESS).apply()
        } else {
            prefs.edit().putString(KEY_WALLET_ADDRESS, address).apply()
        }
    }

    fun getSelectedInscriptionId(): String? = prefs.getString(KEY_SELECTED_INSCRIPTION, null)

    fun setSelectedInscriptionId(inscriptionId: String?) {
        if (inscriptionId == null) {
            prefs.edit().remove(KEY_SELECTED_INSCRIPTION).apply()
        } else {
            prefs.edit().putString(KEY_SELECTED_INSCRIPTION, inscriptionId).apply()
        }
    }

    fun getIsPremium(): Boolean = prefs.getBoolean(KEY_IS_PREMIUM, false)

    fun setIsPremium(isPremium: Boolean) {
        prefs.edit().putBoolean(KEY_IS_PREMIUM, isPremium).apply()
    }

    fun getBotNumber(): Int? {
        val value = prefs.getInt(KEY_BOT_NUMBER, -1)
        return if (value == -1) null else value
    }

    fun setBotNumber(botNumber: Int?) {
        if (botNumber == null) {
            prefs.edit().remove(KEY_BOT_NUMBER).apply()
        } else {
            prefs.edit().putInt(KEY_BOT_NUMBER, botNumber).apply()
        }
    }

    fun clearWalletData() {
        prefs.edit()
            .remove(KEY_WALLET_ADDRESS)
            .remove(KEY_SELECTED_INSCRIPTION)
            .remove(KEY_IS_PREMIUM)
            .remove(KEY_BOT_NUMBER)
            .apply()
    }

    // ===== WALLET SESSION (7 días) =====
    data class WalletSession(
        val address: String,
        val selectedInscriptionId: String,
        val botNumber: Int,
        val tier: String,
        val botImageBase64: String,
        val expiresAt: Long
    ) {
        val isExpired: Boolean
            get() = expiresAt < System.currentTimeMillis()

        val daysUntilExpiry: Long
            get() = maxOf(0, (expiresAt - System.currentTimeMillis()) / MS_PER_DAY)
    }

    fun saveWalletSession(
        address: String,
        selectedInscriptionId: String,
        botNumber: Int,
        tier: String,
        botImageBase64: String
    ) {
        val expiresAt = System.currentTimeMillis() + SESSION_DAYS * MS_PER_DAY
        val session = WalletSession(
            address = address,
            selectedInscriptionId = selectedInscriptionId,
            botNumber = botNumber,
            tier = tier,
            botImageBase64 = botImageBase64,
            expiresAt = expiresAt
        )
        val sessionJson = gson.toJson(session)
        prefs.edit().putString(KEY_WALLET_SESSION, sessionJson).apply()
        
        // Mantener compatibilidad hacia atrás
        setWalletAddress(address)
        setSelectedInscriptionId(selectedInscriptionId)
        setIsPremium(true)
        setBotNumber(botNumber)
    }

    fun getWalletSession(): WalletSession? {
        val sessionJson = prefs.getString(KEY_WALLET_SESSION, null) ?: return null
        val session = gson.fromJson(sessionJson, WalletSession::class.java)
        if (session.isExpired) {
            clearWalletSession()
            return null
        }
        return session
    }

    fun clearWalletSession() {
        prefs.edit()
            .remove(KEY_WALLET_SESSION)
            .remove(KEY_WALLET_ADDRESS)
            .remove(KEY_SELECTED_INSCRIPTION)
            .remove(KEY_IS_PREMIUM)
            .remove(KEY_BOT_NUMBER)
            .apply()
    }

    fun extendSessionExpiry(days: Int = 7) {
        val sessionJson = prefs.getString(KEY_WALLET_SESSION, null) ?: return
        val session = gson.fromJson(sessionJson, WalletSession::class.java)
        val newExpiresAt = System.currentTimeMillis() + days.toLong() * MS_PER_DAY
        val extendedSession = WalletSession(
            address = session.address,
            selectedInscriptionId = session.selectedInscriptionId,
            botNumber = session.botNumber,
            tier = session.tier,
            botImageBase64 = session.botImageBase64,
            expiresAt = newExpiresAt
        )
        val updatedJson = gson.toJson(extendedSession)
        prefs.edit().putString(KEY_WALLET_SESSION, updatedJson).apply()
    }

    fun getPendingNonce(): String? = prefs.getString(KEY_PENDING_NONCE, null)

    fun setPendingNonce(nonce: String?) {
        if (nonce == null) {
            prefs.edit().remove(KEY_PENDING_NONCE).apply()
        } else {
            prefs.edit().putString(KEY_PENDING_NONCE, nonce).apply()
        }
    }

    fun getPendingWalletType(): String? = prefs.getString(KEY_PENDING_WALLET_TYPE, null)

    fun setPendingWalletType(type: String?) {
        if (type == null) {
            prefs.edit().remove(KEY_PENDING_WALLET_TYPE).apply()
        } else {
            prefs.edit().putString(KEY_PENDING_WALLET_TYPE, type).apply()
        }
    }

    fun clearPendingConnection() {
        prefs.edit()
            .remove(KEY_PENDING_NONCE)
            .remove(KEY_PENDING_WALLET_TYPE)
            .apply()
    }

    fun updateSelectedInscription(
        selectedInscriptionId: String,
        botNumber: Int,
        tier: String
    ) {
        val sessionJson = prefs.getString(KEY_WALLET_SESSION, null) ?: return
        val session = gson.fromJson(sessionJson, WalletSession::class.java)
        val updated = WalletSession(
            address = session.address,
            selectedInscriptionId = selectedInscriptionId,
            botNumber = botNumber,
            tier = tier,
            botImageBase64 = session.botImageBase64,
            expiresAt = session.expiresAt
        )
        prefs.edit().putString(KEY_WALLET_SESSION, gson.toJson(updated)).apply()
        setSelectedInscriptionId(selectedInscriptionId)
        setIsPremium(tier == "FOUNDER")
        setBotNumber(botNumber)
    }

    fun updateSessionImage(botImageBase64: String) {
        val sessionJson = prefs.getString(KEY_WALLET_SESSION, null) ?: return
        val session = gson.fromJson(sessionJson, WalletSession::class.java)
        val updated = WalletSession(
            address = session.address,
            selectedInscriptionId = session.selectedInscriptionId,
            botNumber = session.botNumber,
            tier = session.tier,
            botImageBase64 = botImageBase64,
            expiresAt = session.expiresAt
        )
        prefs.edit().putString(KEY_WALLET_SESSION, gson.toJson(updated)).apply()
    }
}
