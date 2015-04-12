/*
 * Copyright 2014 Bademus
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *   Contributors:
 *                Bademus
 */

package com.yoavst.quickapps.news

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.yoavst.kotlin.toast
import com.yoavst.mashov.AsyncJob
import com.yoavst.quickapps.NewsPrefManager
import com.yoavst.quickapps.R
import com.yoavst.quickapps.URLEncodedUtils
import org.scribe.exceptions.OAuthException
import org.scribe.model.Token
import org.scribe.model.Verifier
import java.net.URI
import java.net.URISyntaxException
import kotlin.properties.Delegates

public class LoginActivity : Activity() {
    val prefs: NewsPrefManager by Delegates.lazy { NewsPrefManager(this) }
    val manager: DownloadManager by Delegates.lazy { DownloadManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val url = manager.getService().getAuthorizationUrl(EMPTY_TOKEN)
        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage(getString(R.string.loading))
        val webView = createWebView(this)
        setContentView(webView)
        webView.loadUrl(url)
    }

    SuppressLint("SetJavaScriptEnabled")
    private fun createWebView(context: Context): WebView {
        val webView = WebView(context)
        webView.setWebViewClient(createWebViewClient())
        webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY)
        webView.setVisibility(View.VISIBLE)
        webView.getSettings().setJavaScriptEnabled(true)
        webView.getSettings().setLoadWithOverviewMode(true)
        webView.getSettings().setSupportZoom(true)
        webView.getSettings().setBuiltInZoomControls(false)
        return webView
    }

    private fun createWebViewClient(): WebViewClient {
        return object : WebViewClient() {
            override fun onPageStarted(view: WebView, url: String, fav: Bitmap?) {
                progressDialog!!.show()
            }

            override fun onPageFinished(view: WebView, url: String) {
                progressDialog!!.dismiss()
            }

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                if (url.startsWith(REDIRECT_URI_LOCAL) || url.startsWith(REDIRECT_URN)) {
                    progressDialog!!.show()
                    handleUrl(url)
                    return true
                }
                progressDialog!!.dismiss()
                return false
            }
        }
    }

    fun handleUrl(url: String) {
        try {
            val uri = URI(url)
            val parameters = URLEncodedUtils.parse(uri, "UTF-8")
            for (pair in parameters) {
                if (pair.getName() == "error") {
                    handleLoginError("Error: " + pair.getValue())
                    return
                } else if (pair.getName() == "code") {
                    handleCode(pair.getValue())
                    return
                }
            }
            handleLoginError("Error! please try again")
        } catch (e: URISyntaxException) {
            // Impossible, I think
            e.printStackTrace()
        }

    }

    fun handleCode(code: String) {
        AsyncJob.doInBackground {
            try {
                val verifier = Verifier(code)
                val accessToken = manager.getService().getAccessToken(EMPTY_TOKEN, verifier)
                manager.saveToken(accessToken)
                success()
            } catch (exception: OAuthException) {
                handleLoginError("Error! please try again")
            }
        }

    }

    fun handleLoginError(textToShow: String) {
        AsyncJob.doOnMainThread {
            progressDialog!!.hide()
            Toast.makeText(this, textToShow, Toast.LENGTH_SHORT).show()
            recreate()
        }
    }

    fun success() {
        AsyncJob.doOnMainThread {
            progressDialog!!.hide()
            toast(R.string.login_to_feedly)
            finish()
        }
    }

    private var progressDialog: ProgressDialog? = null

    companion object {
        private val EMPTY_TOKEN: Token? = null

        public val REDIRECT_URI_LOCAL: String = "http://localhost"

        public val REDIRECT_URN: String = "urn:ietf:wg:oauth:2.0:oob"
    }
}
