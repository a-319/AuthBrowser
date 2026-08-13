package com.example.restrictedauthbrowser

import android.net.Uri
import org.junit.Assert.assertEquals
import org.junit.Test

class NavigationPolicyTest {
    private val policy = DefaultNavigationPolicy()

    @Test fun allowsHttpAndHttps() {
        assertEquals(NavigationDecision.ALLOW, policy.evaluate(Uri.parse("http://example.com")))
        assertEquals(NavigationDecision.ALLOW, policy.evaluate(Uri.parse("https://example.com/login")))
    }

    @Test fun routesCustomSchemesExternally() {
        assertEquals(NavigationDecision.OPEN_EXTERNAL, policy.evaluate(Uri.parse("myapp://callback")))
        assertEquals(NavigationDecision.OPEN_EXTERNAL, policy.evaluate(Uri.parse("intent://callback#Intent;scheme=myapp;end")))
    }

    @Test fun blocksDangerousLocalSchemes() {
        assertEquals(NavigationDecision.BLOCK, policy.evaluate(Uri.parse("file:///data/secret")))
        assertEquals(NavigationDecision.BLOCK, policy.evaluate(Uri.parse("javascript:alert(1)")))
    }
}
