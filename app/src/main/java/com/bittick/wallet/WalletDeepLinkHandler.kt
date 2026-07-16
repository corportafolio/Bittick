package com.bittick.wallet

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Base64
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class WalletDeepLinkHandler(private val context: Context) {

    private var pendingCallback: ((Result<String>) -> Unit)? = null
    private var pendingNonce: String? = null

    companion object {
        private const val UNISAT_CALLBACK = "unisat://response"
        private const val APP_FROM = "bittick"
        private const val SIGN_MESSAGE = "Conectar a Bittick"
    }

    fun requestSignature(nonce: String, callback: (Result<String>) -> Unit) {
        pendingCallback = callback
        pendingNonce = nonce

        val dataArray = JSONArray().apply {
            put(SIGN_MESSAGE)
            put("ecdsa")
        }
        val base64Data = Base64.encodeToString(dataArray.toString().toByteArray(), Base64.NO_WRAP)

        val uri = Uri.Builder()
            .scheme("unisat")
            .authority("request")
            .appendQueryParameter("method", "signMessage")
            .appendQueryParameter("data", base64Data)
            .appendQueryParameter("nonce", nonce)
            .appendQueryParameter("callback", UNISAT_CALLBACK)
            .appendQueryParameter("from", APP_FROM)
            .build()

        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            callback(Result.failure(Exception("UniSat no esta instalado. Por favor instala UniSat wallet.")))
            pendingCallback = null
            pendingNonce = null
        }
    }

    fun requestAddresses(nonce: String, callback: (Result<String>) -> Unit) {
        pendingCallback = callback
        pendingNonce = nonce

        val uri = Uri.Builder()
            .scheme("unisat")
            .authority("request")
            .appendQueryParameter("method", "getAddresses")
            .appendQueryParameter("nonce", nonce)
            .appendQueryParameter("callback", UNISAT_CALLBACK)
            .appendQueryParameter("from", APP_FROM)
            .build()

        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            callback(Result.failure(Exception("UniSat no esta instalado.")))
            pendingCallback = null
            pendingNonce = null
        }
    }

    fun handleResponse(uri: Uri): Boolean {
        val callback = pendingCallback ?: return false
        val expectedNonce = pendingNonce ?: return false

        pendingCallback = null
        pendingNonce = null

        try {
            val receivedNonce = uri.getQueryParameter("nonce")
            if (receivedNonce != expectedNonce) {
                callback(Result.failure(Exception("Nonce invalido")))
                return true
            }

            val error = uri.getQueryParameter("error")
            if (error != null) {
                callback(Result.failure(Exception(error)))
                return true
            }

            val d = uri.getQueryParameter("d") ?: run {
                val signature = uri.getQueryParameter("signature")
                val address = uri.getQueryParameter("address")
                if (signature != null) {
                    callback(Result.success(signature))
                    return true
                }
                if (address != null) {
                    callback(Result.success(address))
                    return true
                }
                callback(Result.failure(Exception("Respuesta invalida de UniSat")))
                return true
            }

            val response = JSONObject(d)
            val respObj = response.optJSONObject("response")
            if (respObj != null) {
                val status = respObj.optString("status")
                if (status == "ok") {
                    val result = respObj.optJSONObject("result")
                    val signature = result?.optString("signature")
                    val addresses = result?.optJSONArray("addresses")
                    if (signature != null) {
                        callback(Result.success(signature))
                    } else if (addresses != null && addresses.length() > 0) {
                        callback(Result.success(addresses.getString(0)))
                    } else {
                        callback(Result.failure(Exception("Respuesta vacia de UniSat")))
                    }
                } else {
                    val message = respObj.optString("message", "Operacion cancelada")
                    callback(Result.failure(Exception(message)))
                }
            } else {
                val signature = response.optString("signature")
                val address = response.optString("address")
                if (signature.isNotBlank()) {
                    callback(Result.success(signature))
                } else if (address.isNotBlank()) {
                    callback(Result.success(address))
                } else {
                    callback(Result.failure(Exception("Formato de respuesta desconocido")))
                }
            }
        } catch (e: Exception) {
            callback(Result.failure(Exception("Error procesando respuesta de UniSat: ${e.message}")))
        }
        return true
    }
}
