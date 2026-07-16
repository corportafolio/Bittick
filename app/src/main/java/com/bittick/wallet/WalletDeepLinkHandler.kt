package com.bittick.wallet

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Base64
import org.json.JSONArray
import org.json.JSONObject
import java.security.MessageDigest
import java.util.UUID

class WalletDeepLinkHandler(private val context: Context) {

    private var pendingCallback: ((Result<String>) -> Unit)? = null
    private var pendingMessage: String? = null

    fun requestSignature(message: String, callback: (Result<String>) -> Unit) {
        pendingCallback = callback
        pendingMessage = message

        val requestId = UUID.randomUUID().toString()
        val request = JSONObject().apply {
            put("id", requestId)
            put("origin", "Bittick")
            put("request", JSONObject().apply {
                put("method", "signMessage")
                put("params", JSONObject().apply {
                    put("message", message)
                })
            })
        }

        val uri = Uri.Builder()
            .scheme("unisat")
            .authority("request")
            .appendQueryParameter("d", request.toString())
            .build()

        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            callback(Result.failure(Exception("UniSat no está instalado. Por favor instala UniSat wallet.")))
            pendingCallback = null
            pendingMessage = null
        }
    }

    fun handleResponse(uri: Uri) {
        val callback = pendingCallback ?: return
        val expectedMessage = pendingMessage ?: return

        pendingCallback = null
        pendingMessage = null

        try {
            val d = uri.getQueryParameter("d") ?: run {
                callback(Result.failure(Exception("Respuesta inválida de UniSat")))
                return
            }

            val response = JSONObject(d)
            val result = response.getJSONObject("response")
            val status = result.getString("status")

            if (status == "ok") {
                val content = result.getJSONObject("result")
                val signature = content.getString("signature")
                callback(Result.success(signature))
            } else {
                val message = result.optString("message", "Operación cancelada")
                callback(Result.failure(Exception(message)))
            }
        } catch (e: Exception) {
            callback(Result.failure(Exception("Error procesando respuesta de UniSat: ${e.message}")))
        }
    }

    fun getAddressFromSignature(message: String, signature: String): String? {
        return try {
            val messageBytes = message.toByteArray()
            val signatureBytes = Base64.decode(signature, Base64.DEFAULT)

            val digest = MessageDigest.getInstance("SHA-256")
            val messageHash = digest.digest(messageBytes)

            val recoveredAddress = recoverAddressFromSignature(messageHash, signatureBytes)
            recoveredAddress
        } catch (e: Exception) {
            null
        }
    }

    private fun recoverAddressFromSignature(messageHash: ByteArray, signature: ByteArray): String? {
        return try {
            val pubKey = recoverPublicKey(messageHash, signature)
            if (pubKey != null) {
                publicKeyToAddress(pubKey)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun recoverPublicKey(messageHash: ByteArray, signature: ByteArray): ByteArray? {
        return try {
            val r = signature.sliceArray(0..31)
            val s = signature.sliceArray(32..63)
            val v = signature[64].toInt()

            val publicKey = ByteArray(33)
            System.arraycopy(messageHash, 0, publicKey, 0, 32)
            publicKey[32] = (v - 27).toByte()

            publicKey
        } catch (e: Exception) {
            null
        }
    }

    private fun publicKeyToAddress(publicKey: ByteArray): String {
        val sha256 = MessageDigest.getInstance("SHA-256")
        val pubKeyHash = sha256.digest(publicKey)

        val ripemd160 = MessageDigest.getInstance("RIPEMD-160")
        val hash160 = ripemd160.digest(pubKeyHash)

        val versionedPayload = ByteArray(21)
        versionedPayload[0] = 0x00
        System.arraycopy(hash160, 0, versionedPayload, 1, 20)

        val checksum = calculateChecksum(versionedPayload)

        val fullAddress = ByteArray(25)
        System.arraycopy(versionedPayload, 0, fullAddress, 0, 21)
        System.arraycopy(checksum, 0, fullAddress, 21, 4)

        return Base32.encode(fullAddress)
    }

    private fun calculateChecksum(payload: ByteArray): ByteArray {
        val sha256 = MessageDigest.getInstance("SHA-256")
        val firstHash = sha256.digest(payload)
        val secondHash = sha256.digest(firstHash)
        return secondHash.sliceArray(0..3)
    }

    private object Base32 {
        private const val ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567"

        fun encode(input: ByteArray): String {
            val bits = input.joinToString("") { byte ->
                Integer.toBinaryString((byte.toInt() and 0xFF) or 0x100).substring(1)
            }

            val chunks = bits.chunked(5)
            return chunks.joinToString("") { chunk ->
                val padded = chunk.padEnd(5, '0')
                ALPHABET[padded.toInt(2)].toString()
            }
        }
    }
}
