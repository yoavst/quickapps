package com.yoavst.quickapps.news

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import at.markushi.ui.CircleButton
import com.lge.qcircle.template.QCircleBackButton
import com.lge.qcircle.template.QCircleTitle
import com.lge.qcircle.template.TemplateTag
import com.malinskiy.materialicons.IconDrawable
import com.malinskiy.materialicons.Iconify
import com.yoavst.kotlin.*
import com.yoavst.quickapps.R
import com.yoavst.quickapps.news.types.Entry
import com.yoavst.quickapps.tools.QCircleActivity
import com.yoavst.quickapps.tools.qCircleToast
import kotlinx.android.synthetic.news_activity.*
import java.util.ArrayList
import kotlin.properties.Delegates

/**
 * Created by Yoav.
 */
public class CNewsActivity : QCircleActivity(), DownloadManager.DownloadingCallback {
    val manager: DownloadManager by Delegates.lazy { DownloadManager(this) }
    var shouldOpenLogin = false
    public var entries: ArrayList<Entry>? = null
    var firstTime = true

    protected override fun onCreate(savedInstanceState: Bundle?) {
        super<QCircleActivity>.onCreate(savedInstanceState)
        template.addElement(QCircleBackButton(this))
        val title = QCircleTitle(this, getString(R.string.news_module_name), Color.WHITE, colorRes(R.color.md_teal_900))
        title.setTextSize(17F)
        template.addElement(title)
        template.getLayoutById(TemplateTag.CONTENT_MAIN)
                .addView(LayoutInflater.from(this).inflate(R.layout.news_activity, template.getLayoutById(TemplateTag.CONTENT_MAIN), false))
        setContentView(template.getView())
        init()
    }

    public fun getEntry(id: Int): Entry? {
        if (entries == null) return null
        else if (entries!!.size() <= id) return null
        else return entries!![id]
    }

    protected override fun getIntentToShow(): Intent? {
        if (shouldOpenLogin)
            return intent<LoginActivity>()
        else if (entries == null || entries!!.size() == 0)
            return null
        else {
            val id = ((getFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager + ":" + pager.getCurrentItem())) as NewsFragment).entryNumber
            return Intent(Intent.ACTION_VIEW, Uri.parse(entries!![id].getAlternate().get(0).getHref()))
        }
    }

    fun init() {
        pager.setOffscreenPageLimit(20)
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
        mainThread {
            qCircleToast(R.string.no_connection)
        }
    }

    override fun onSuccess(entries: ArrayList<Entry>) {
        this.entries = entries
        showEntries()
    }

    enum class Error {
        Login,
        Internet,
        Empty
    }

    fun showEntries() {
        mainThread {
            loading.hide()
            errorLayout.hide()
            if (entries == null || entries!!.size() == 0) showError(Error.Empty)
            else {
                if (firstTime) {
                    try {
                        pager.setAdapter(NewsAdapter(getFragmentManager(), entries!!.size()))
                        firstTime = false
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else pager.getAdapter().notifyDataSetChanged()
            }
        }
    }

    fun downloadEntries() {
        mainThread {
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
        mainThread {
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