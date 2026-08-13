package com.example.restrictedauthbrowser

enum class NavigationDecision { ALLOW, BLOCK, OPEN_EXTERNAL }

/** Central policy point for every top-level navigation. */
interface NavigationPolicy {
    fun evaluate(rawUri: String): NavigationDecision
}

class DefaultNavigationPolicy : NavigationPolicy {
    override fun evaluate(rawUri: String): NavigationDecision = when (schemeOf(rawUri)) {
        "http", "https" -> NavigationDecision.ALLOW
        "file", "content", "data", "javascript", null -> NavigationDecision.BLOCK
        else -> NavigationDecision.OPEN_EXTERNAL
    }

    private fun schemeOf(rawUri: String): String? {
        val separator = rawUri.indexOf(':')
        if (separator <= 0) return null
        val scheme = rawUri.substring(0, separator)
        if (!scheme.matches(Regex("[A-Za-z][A-Za-z0-9+.-]*"))) return null
        return scheme.lowercase()
    }
}
