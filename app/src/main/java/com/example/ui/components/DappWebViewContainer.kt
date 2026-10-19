package com.example.ui.components

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.http.SslError
import android.view.ViewGroup
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.core.dapp.KoroProviderScript
import com.example.core.dapp.WalletProviderBridge
import com.example.model.EvmNetworkConfig

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun DappWebViewContainer(
    urlToLoad: String,
    selectedNetwork: EvmNetworkConfig,
    activeAddress: String?,
    bridge: WalletProviderBridge,
    onWebViewCreated: (WebView) -> Unit,
    onPageStarted: (String) -> Unit,
    onPageFinished: (String, String?) -> Unit,
    onProgressChanged: (Int) -> Unit,
    onErrorReceived: (String) -> Unit,
    onSslError: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val webView = remember {
        WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            // HARDENED SECURITY SETTINGS
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.allowFileAccess = false
            settings.allowContentAccess = false
            settings.allowFileAccessFromFileURLs = false
            settings.allowUniversalAccessFromFileURLs = false
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
            settings.setSupportMultipleWindows(false)
            settings.builtInZoomControls = true
            settings.displayZoomControls = false

            // Attach Restricted JavaScript Bridge
            addJavascriptInterface(bridge, "KoroWalletBridge")

            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    onProgressChanged(newProgress)
                }

                override fun onReceivedTitle(view: WebView?, title: String?) {
                    val currentUrl = view?.url ?: ""
                    onPageFinished(currentUrl, title)
                }
            }

            webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    url?.let { validUrl ->
                        onPageStarted(validUrl)
                        // Inject EIP-1193 window.ethereum provider script before page scripts run
                        val injectionScript = KoroProviderScript.generateInjectionScript(
                            network = selectedNetwork,
                            approvedAddress = activeAddress
                        )
                        view?.evaluateJavascript(injectionScript, null)
                    }
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    url?.let {
                        onPageFinished(it, view?.title)
                        // Re-evaluate in case page replaced window.ethereum
                        val injectionScript = KoroProviderScript.generateInjectionScript(
                            network = selectedNetwork,
                            approvedAddress = activeAddress
                        )
                        view?.evaluateJavascript(injectionScript, null)
                    }
                }

                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    val targetUri = request?.url ?: return false
                    val scheme = targetUri.scheme?.lowercase() ?: return true

                    // Only permit HTTPS and local development HTTP
                    return if (scheme == "https" || (scheme == "http" && (targetUri.host == "localhost" || targetUri.host == "127.0.0.1"))) {
                        false // Allow WebView to handle navigation
                    } else {
                        // Safely block javascript:, file:, data:, intent:, and custom schemes
                        true
                    }
                }

                override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                    if (request?.isForMainFrame == true) {
                        onErrorReceived(error?.description?.toString() ?: "Failed to load page")
                    }
                }

                override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                    // STRICT SECURITY: Never bypass SSL certificate errors
                    handler?.cancel()
                    onSslError()
                }
            }

            onWebViewCreated(this)
        }
    }

    DisposableEffect(webView) {
        onDispose {
            try {
                webView.stopLoading()
                webView.removeJavascriptInterface("KoroWalletBridge")
                webView.destroy()
            } catch (_: Exception) {
            }
        }
    }

    Box(modifier = modifier.background(Color(0xFF111317))) {
        AndroidView(
            factory = { webView },
            modifier = Modifier.fillMaxSize(),
            update = { view ->
                if (urlToLoad.isNotBlank() && view.url != urlToLoad) {
                    view.loadUrl(urlToLoad)
                }
            }
        )
    }
}
