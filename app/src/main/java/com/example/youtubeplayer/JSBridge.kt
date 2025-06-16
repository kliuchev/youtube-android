package com.example.youtubeplayer
import android.util.Log
import org.json.JSONObject

class JSBridge(private val listener: JSBridgeListener) {

    @android.webkit.JavascriptInterface
    fun postMessage(message: String) {
        try {
            val json = JSONObject(message)
            val type = json.getString("type")

            when (type) {
                "onProgress" -> {
                    val current = json.getDouble("current")
                    val total = json.getDouble("total")
                    val buffered = json.getDouble("buffered")
                    listener.onProgress(current, total, buffered)
                }

                "onReady" -> {
                    listener.onReady()
                }

                "onStateChange" -> {
                    val state = json.getInt("state")
                    listener.onStateChange(state)
                }

                "onError" -> {
                    val error = json.getInt("error")
                    val errorMessage = json.getString("message")
                    listener.onError(error, errorMessage)
                }

                else -> {
                    Log.w("JSBridge", "Unknown message type: $type")
                }
            }

        } catch (e: Exception) {
            Log.e("JSBridge", "Failed to parse message: $message", e)
        }
    }

    interface JSBridgeListener {
        fun onProgress(current: Double, total: Double, buffered: Double)
        fun onReady()
        fun onStateChange(state: Int)
        fun onError(code: Int, message: String)
    }
}