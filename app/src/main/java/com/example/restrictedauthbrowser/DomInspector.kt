package com.example.restrictedauthbrowser

class DomInspector {
    @Volatile private var title: String? = null

    /** Safe proof-of-concept equivalent to reading document.title. */
    fun onTitleChanged(value: String?) {
        title = value
    }

    fun readTitle(): String? = title
}
