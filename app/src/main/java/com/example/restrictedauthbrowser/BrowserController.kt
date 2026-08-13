package com.example.restrictedauthbrowser

import android.net.Uri
import org.mozilla.geckoview.AllowOrDeny
import org.mozilla.geckoview.GeckoResult
import org.mozilla.geckoview.GeckoSession
import org.mozilla.geckoview.GeckoView
import org.mozilla.geckoview.WebRequestError

class BrowserController(
    private val view: GeckoView,
    private val policy: NavigationPolicy,
    private val externalIntents: ExternalIntentHandler,
    private val events: Events
) {
    interface Events {
        fun onLoadingChanged(loading: Boolean)
        fun onLocationChanged(uri: Uri)
        fun onNavigationError(message: String)
    }

    private val session = GeckoSession()
    val domInspector = DomInspector(session)
    var canGoBack: Boolean = false
        private set

    init {
        session.progressDelegate = object : GeckoSession.ProgressDelegate {
            override fun onPageStart(session: GeckoSession, url: String) {
                SafeLog.d("navigation started: ${SafeLog.sanitize(url)}")
                events.onLoadingChanged(true)
            }

            override fun onPageStop(session: GeckoSession, success: Boolean) {
                events.onLoadingChanged(false)
                if (success) {
                    domInspector.readTitle().accept(
                        { title -> SafeLog.d("page title available (${title?.toString()?.length ?: 0} chars)") },
                        { error -> SafeLog.w("title inspection failed", error) }
                    )
                }
            }
        }
        session.navigationDelegate = object : GeckoSession.NavigationDelegate {
            override fun onLoadRequest(
                session: GeckoSession,
                request: GeckoSession.NavigationDelegate.LoadRequest
            ): GeckoResult<AllowOrDeny> {
                if (request.isRedirect) SafeLog.d("redirect: ${SafeLog.sanitize(request.uri)}")
                return when (policy.evaluate(request.uri)) {
                    NavigationDecision.ALLOW -> {
                        SafeLog.d("navigation allowed: ${SafeLog.sanitize(request.uri)}")
                        if (request.target == GeckoSession.NavigationDelegate.TARGET_WINDOW_NEW) {
                            // Preserve the single-session model for target=_blank/OAuth popups.
                            session.loadUri(request.uri)
                            GeckoResult.fromValue(AllowOrDeny.DENY)
                        } else {
                            GeckoResult.fromValue(AllowOrDeny.ALLOW)
                        }
                    }
                    NavigationDecision.BLOCK -> {
                        SafeLog.w("navigation blocked: ${SafeLog.sanitize(request.uri)}")
                        events.onNavigationError("This link type is blocked for safety.")
                        GeckoResult.fromValue(AllowOrDeny.DENY)
                    }
                    NavigationDecision.OPEN_EXTERNAL -> {
                        SafeLog.d("external scheme: ${SafeLog.sanitize(request.uri)}")
                        when (val result = externalIntents.open(request.uri)) {
                            ExternalOpenResult.Opened -> Unit
                            ExternalOpenResult.NoHandler -> events.onNavigationError("No app can open this authentication callback.")
                            is ExternalOpenResult.Failed -> {
                                SafeLog.w("external intent failed", result.error)
                                events.onNavigationError("The authentication callback could not be opened.")
                            }
                        }
                        GeckoResult.fromValue(AllowOrDeny.DENY)
                    }
                }
            }

            override fun onLocationChange(
                session: GeckoSession,
                url: String?,
                perms: List<GeckoSession.PermissionDelegate.ContentPermission>,
                hasUserGesture: Boolean
            ) {
                url?.let { events.onLocationChanged(Uri.parse(it)) }
            }

            override fun onCanGoBack(session: GeckoSession, value: Boolean) {
                canGoBack = value
            }

            override fun onLoadError(
                session: GeckoSession,
                uri: String?,
                error: WebRequestError
            ): GeckoResult<String>? {
                SafeLog.w("page load error: ${SafeLog.sanitize(uri)} category=${error.category} code=${error.code}")
                events.onLoadingChanged(false)
                events.onNavigationError("This page could not be loaded.")
                return null
            }

            override fun onNewSession(session: GeckoSession, uri: String): GeckoResult<GeckoSession>? {
                // Keep target=_blank and OAuth popup navigation in the one restricted session.
                load(uri)
                return null
            }
        }
    }

    fun open() {
        if (!session.isOpen) session.open(GeckoRuntimeProvider.get(view.context))
        view.setSession(session)
    }

    fun load(rawUri: String) {
        when (policy.evaluate(rawUri)) {
            NavigationDecision.ALLOW -> session.loadUri(rawUri)
            else -> events.onNavigationError("No valid HTTP or HTTPS URL was supplied.")
        }
    }

    fun goBack() = session.goBack()

    fun close() {
        view.releaseSession()
        if (session.isOpen) session.close()
    }
}
