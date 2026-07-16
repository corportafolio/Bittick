package com.bittick.data.cache

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.ByteArrayOutputStream
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BittickImageCache @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val cache = ConcurrentHashMap<String, String>()
    private val client = OkHttpClient()

    suspend fun getImage(inscriptionId: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val cached = cache[inscriptionId]
            if (cached != null) return@withContext Result.success(cached)

            val prefs = context.getSharedPreferences("bittick_image_cache", Context.MODE_PRIVATE)
            val saved = prefs.getString(inscriptionId, null)
            if (saved != null) {
                cache[inscriptionId] = saved
                return@withContext Result.success(saved)
            }

            val url = "https://ordinals.com/content/$inscriptionId"
            val request = Request.Builder()
                .url(url)
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("Error descargando imagen: ${response.code}"))
            }

            val bytes = response.body?.bytes() ?: return@withContext Result.failure(Exception("Respuesta vacía"))
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                ?: return@withContext Result.failure(Exception("Error decodificando imagen"))

            val base64 = bitmapToBase64(bitmap)
            cache[inscriptionId] = base64
            prefs.edit().putString(inscriptionId, base64).apply()

            Result.success(base64)
        } catch (e: Exception) {
            Result.failure(Exception("Error de red: ${e.message}"))
        }
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val bytes = stream.toByteArray()
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    fun getCached(inscriptionId: String): String? {
        return cache[inscriptionId] ?: context.getSharedPreferences("bittick_image_cache", Context.MODE_PRIVATE)
            .getString(inscriptionId, null)
    }

    fun clearCache() {
        cache.clear()
        context.getSharedPreferences("bittick_image_cache", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .apply()
    }
}
