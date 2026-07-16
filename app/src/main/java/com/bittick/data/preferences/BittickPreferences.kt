package com.bittick.data.preferences

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BittickPreferences @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs = context.getSharedPreferences("bittick_prefs", Context.MODE_PRIVATE)

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

    companion object {
        private const val PREFS_NAME = "bittick_prefs"
        private const val KEY_TRADING_LAST_CREATED = "trading_last_created_at"
        private const val KEY_WALLET_ADDRESS = "wallet_address"
        private const val KEY_SELECTED_INSCRIPTION = "selected_inscription_id"
        private const val KEY_IS_PREMIUM = "is_premium"
        private const val KEY_BOT_NUMBER = "bot_number"
    }
}
