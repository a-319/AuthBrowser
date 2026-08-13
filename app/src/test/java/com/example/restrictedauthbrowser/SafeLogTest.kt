package com.example.restrictedauthbrowser

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class SafeLogTest {
    @Test fun stripsSensitiveUrlParts() {
        val value = SafeLog.sanitize("https://user:pass@example.com/oauth/callback?code=secret#token")
        assertEquals("https://example.com/…", value)
        assertFalse(value.contains("secret"))
        assertFalse(value.contains("token"))
        assertFalse(value.contains("callback"))
    }

    @Test fun redactsExternalSchemes() {
        assertEquals("myapp:<redacted>", SafeLog.sanitize("myapp://callback?code=secret"))
    }
}
