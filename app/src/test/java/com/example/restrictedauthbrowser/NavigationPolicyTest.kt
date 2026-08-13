package com.example.restrictedauthbrowser

import org.junit.Assert.assertEquals
import org.junit.Test

class NavigationPolicyTest {
    private val policy = DefaultNavigationPolicy()

    @Test fun allowsHttpAndHttps() {
        assertEquals(NavigationDecision.ALLOW, policy.evaluate("http://example.com"))
        assertEquals(NavigationDecision.ALLOW, policy.evaluate("https://example.com/login"))
    }

    @Test fun routesCustomSchemesExternally() {
        assertEquals(NavigationDecision.OPEN_EXTERNAL, policy.evaluate("myapp://callback"))
        assertEquals(NavigationDecision.OPEN_EXTERNAL, policy.evaluate("intent://callback#Intent;scheme=myapp;end"))
    }

    @Test fun blocksDangerousLocalSchemes() {
        assertEquals(NavigationDecision.BLOCK, policy.evaluate("file:///data/secret"))
        assertEquals(NavigationDecision.BLOCK, policy.evaluate("javascript:alert(1)"))
    }
}
