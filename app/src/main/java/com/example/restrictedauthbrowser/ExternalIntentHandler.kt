package com.example.restrictedauthbrowser

import android.content.Context
import android.content.Intent
import android.net.Uri

sealed class ExternalOpenResult {
    object Opened : ExternalOpenResult()
    object NoHandler : ExternalOpenResult()
    data class Failed(val error: Throwable) : ExternalOpenResult()
}

class ExternalIntentHandler(private val context: Context) {
    fun open(rawUri: String): ExternalOpenResult = try {
        val intent = if (rawUri.startsWith("intent:", ignoreCase = true)) {
            Intent.parseUri(rawUri, Intent.URI_INTENT_SCHEME).apply {
                // Never allow a web page to target an explicit component.
                component = null
                selector = null
                addCategory(Intent.CATEGORY_BROWSABLE)
            }
        } else {
            Intent(Intent.ACTION_VIEW, Uri.parse(rawUri)).apply {
                addCategory(Intent.CATEGORY_BROWSABLE)
            }
        }
        if (intent.resolveActivity(context.packageManager) == null) {
            ExternalOpenResult.NoHandler
        } else {
            context.startActivity(intent)
            ExternalOpenResult.Opened
        }
    } catch (error: Throwable) {
        ExternalOpenResult.Failed(error)
    }
}
