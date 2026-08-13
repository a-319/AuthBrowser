package com.example.restrictedauthbrowser

import org.mozilla.geckoview.GeckoResult
import org.mozilla.geckoview.GeckoSession

class DomInspector(private val session: GeckoSession) {
    /** Safe proof-of-concept; does not mutate the document or expose a JS bridge. */
    fun readTitle(): GeckoResult<Any> = session.evaluateJS("document.title")
}
