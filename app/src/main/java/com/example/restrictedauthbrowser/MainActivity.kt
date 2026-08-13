package com.example.restrictedauthbrowser

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import org.mozilla.geckoview.GeckoView

class MainActivity : Activity(), BrowserController.Events {
    private lateinit var controller: BrowserController
    private lateinit var progress: ProgressBar
    private lateinit var hostname: TextView
    private lateinit var errorView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        progress = findViewById(R.id.progress)
        hostname = findViewById(R.id.hostname)
        errorView = findViewById(R.id.errorView)
        findViewById<View>(R.id.closeButton).setOnClickListener { finish() }

        controller = BrowserController(
            findViewById<GeckoView>(R.id.geckoView),
            DefaultNavigationPolicy(),
            ExternalIntentHandler(this),
            this
        )
        controller.open()
        if (savedInstanceState == null) handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val uri = intent?.takeIf { it.action == Intent.ACTION_VIEW }?.data
        SafeLog.d("intent URL: ${SafeLog.sanitize(uri?.toString())}")
        if (uri == null || DefaultNavigationPolicy().evaluate(uri.toString()) != NavigationDecision.ALLOW || uri.host.isNullOrBlank()) {
            onNavigationError(getString(R.string.invalid_url))
            return
        }
        errorView.visibility = View.GONE
        controller.load(uri.toString())
    }

    @Deprecated("Deprecated by Android; retained for API 21 compatibility")
    override fun onBackPressed() {
        if (controller.canGoBack) controller.goBack() else finish()
    }

    override fun onDestroy() {
        controller.close()
        super.onDestroy()
    }

    override fun onLoadingChanged(loading: Boolean) {
        progress.visibility = if (loading) View.VISIBLE else View.GONE
        if (loading) errorView.visibility = View.GONE
    }

    override fun onLocationChanged(uri: Uri) {
        hostname.text = uri.host ?: uri.scheme ?: getString(R.string.no_page)
    }

    override fun onNavigationError(message: String) {
        progress.visibility = View.GONE
        errorView.text = message
        errorView.visibility = View.VISIBLE
    }
}
