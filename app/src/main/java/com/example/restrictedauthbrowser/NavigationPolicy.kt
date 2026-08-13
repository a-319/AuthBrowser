package com.example.restrictedauthbrowser

import android.net.Uri

enum class NavigationDecision { ALLOW, BLOCK, OPEN_EXTERNAL }

/** Central policy point for every top-level navigation. */
interface NavigationPolicy {
    fun evaluate(uri: Uri): NavigationDecision
}

class DefaultNavigationPolicy : NavigationPolicy {
    override fun evaluate(uri: Uri): NavigationDecision = when (uri.scheme?.lowercase()) {
        "http", "https" -> NavigationDecision.ALLOW
        "file", "content", "data", "javascript", null -> NavigationDecision.BLOCK
        else -> NavigationDecision.OPEN_EXTERNAL
    }
}
