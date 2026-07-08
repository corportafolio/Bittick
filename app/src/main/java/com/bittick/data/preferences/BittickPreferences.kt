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

    companion object {
        private const val PREFS_NAME = "bittick_prefs"
        private const val KEY_TRADING_LAST_CREATED = "trading_last_created_at"
    }
}
