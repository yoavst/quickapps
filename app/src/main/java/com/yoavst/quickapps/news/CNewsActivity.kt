package com.yoavst.quickapps.news

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView

import com.malinskiy.materialicons.IconDrawable
import com.malinskiy.materialicons.Iconify
import com.yoavst.quickapps.R
import com.yoavst.quickapps.news.types.Entry

import java.util.ArrayList

import at.markushi.ui.CircleButton
import com.lge.qcircle.template.*
import com.yoavst.kotlin.colorRes
import com.yoavst.kotlin.hide
import com.yoavst.quickapps.util.QCircleActivity
import kotlin.properties.Delegates
import com.yoavst.kotlin.intent
import com.yoavst.kotlin.show
import com.yoavst.mashov.AsyncJob
import com.yoavst.util.qCircleToast
import kotlinx.android.synthetic.news_circle_container_layout.*

/**
 * Created by Yoav.
 */
public class CNewsActivity : QCircleActivity(), DownloadManager.DownloadingCallback {
    override val template: QCircleTemplate by Delegates.lazy { QCircleTemplate(this, TemplateType.CIRCLE_EMPTY) }
    val manager: DownloadManager by Delegates.lazy { DownloadManager(this) }
    var shouldOpenLogin = false
    var entries: ArrayList<Entry>? = null

    protected override fun onCreate(savedInstanceState: Bundle?) {
        super<QCircleActivity>.onCreate(savedInstanceState)
        template.addElement(QCircleBackButton(this))
        val title = QCircleTitle(this, getString(R.string.news_module_name), Color.WHITE, colorRes(R.color.md_teal_900))
        title.setTextSize(17F)
        template.addElement(title)
        template.getLayoutById(TemplateTag.CONTENT_MAIN)
                .addView(LayoutInflater.from(this).inflate(R.layout.news_circle_container_layout, template.getLayoutById(TemplateTag.CONTENT_MAIN), false))
        setContentView(template.getView())
        init()
    }

    protected override fun getIntentToShow(): Intent? {
        if (shouldOpenLogin)
            return intent<LoginActivity>()
        else if (entries == null || entries!!.size() == 0)
            return null
        else {
            val id = ((getFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager + ":" + pager.getCurrentItem())) as NewsFragment).entryNumber
            return Intent(Intent.ACTION_VIEW, Uri.parse(NewsAdapter.getEntry(id)!!.getAlternate().get(0).getHref()))
        }
    }

    fun init() {
        val refresh = findViewById(R.id.refresh) as CircleButton
        refresh.setImageDrawable(IconDrawable(this, Iconify.IconValue.md_refresh).sizeDp(24).color(Color.WHITE))
        refresh.setOnClickListener { v -> downloadEntries() }
        val token = manager.getTokenFromPrefs()
        if (token == null) {
            // User not login in
            showError(Error.Login)
        } else {
            entries = manager.getFeedFromPrefs()
            if (entries != null) showEntries()
            downloadEntries()
        }
    }

    override fun onFail(error: DownloadManager.DownloadError) {
        when (error) {
            DownloadManager.DownloadError.Login -> showError(Error.Login)
            DownloadManager.DownloadError.Internet, DownloadManager.DownloadError.Other -> {
                if (entries == null || entries!!.size() == 0)
                    showError(Error.Internet)
                // Else show toast
                noConnectionToast()
            }
        }
    }

    fun noConnectionToast() {
        AsyncJob.doOnMainThread {
            qCircleToast(R.string.no_connection)
        }
    }

    override fun onSuccess(entries: ArrayList<Entry>) {
        this.entries = entries
        showEntries()
    }

    enum class Error {
        Login
        Internet
        Empty
    }

    fun showEntries() {
        AsyncJob.doOnMainThread {
            loading.hide()
            errorLayout.hide()
            if (entries == null || entries!!.size() == 0) showError(Error.Empty);
            else {
                pager.setAdapter(NewsAdapter(getFragmentManager(), entries!!));
            }
        }
    }

    fun downloadEntries() {
        AsyncJob.doOnMainThread {
            errorLayout.hide()
            if (manager.isNetworkAvailable()) {
                if (entries == null || entries!!.size() == 0) {
                    // Show loading
                    loading.show()
                } else {
                    qCircleToast(R.string.start_downloading)
                }// Else inform the user we start Downloading but still show content
                manager.download(this@CNewsActivity)
            } else {
                if (entries == null || entries!!.size() == 0) {
                    // Show internet error
                    showError(Error.Internet)
                } else {
                    qCircleToast(R.string.no_connection)
                }// Else inform the user that he has no connection
            }
        }
    }

    fun showError(error: Error) {
        AsyncJob.doOnMainThread {
            errorLayout.show()
            when (error) {
                Error.Login -> {
                    titleError.setText(R.string.news_should_login)
                    extraError.setText(R.string.news_should_login_subtext)
                    loading.hide()
                    shouldOpenLogin = true
                }
                Error.Internet -> {
                    titleError.setText(R.string.news_network_error)
                    extraError.setText(R.string.news_network_error_subtext)
                }
                Error.Empty -> {
                    titleError.setText(R.string.news_no_content)
                    titleError.setText(R.string.news_no_content_subtext)
                }
            }
        }
    }
}
