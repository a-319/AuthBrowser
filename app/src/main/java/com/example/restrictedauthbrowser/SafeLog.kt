package com.example.restrictedauthbrowser

import android.util.Log
import java.net.URI

object SafeLog {
    private const val TAG = "RestrictedAuthBrowser"

    /** Removes query parameters, fragments, user info and paths before logging. */
    fun sanitize(raw: String?): String {
        if (raw.isNullOrBlank()) return "<empty>"
        return try {
            val uri = URI(raw)
            val scheme = uri.scheme ?: return "<invalid-uri>"
            if (scheme.equals("http", true) || scheme.equals("https", true)) {
                val host = uri.host ?: return "${scheme.lowercase()}://<invalid-host>"
                val port = if (uri.port != -1) ":${uri.port}" else ""
                "${scheme.lowercase()}://$host$port/…"
            } else {
                "${scheme.lowercase()}:<redacted>"
            }
        } catch (_: RuntimeException) {
            "<invalid-uri>"
        }
    }

    fun d(message: String) = Log.d(TAG, message)
    fun w(message: String, error: Throwable? = null) = Log.w(TAG, message, error)
}
