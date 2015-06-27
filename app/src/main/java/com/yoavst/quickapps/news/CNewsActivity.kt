package com.yoavst.quickapps.news

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.github.jorgecastilloprz.FABProgressCircle
import com.lge.qcircle.template.QCircleTitle
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
    var lock = false
    public var entries: ArrayList<Entry>? = null

    public fun get(position: Int): Entry = entries!![position]

    override fun onCreate(savedInstanceState: Bundle?) {
        super<QCircleActivity>.onCreate(savedInstanceState)
        val qcircleTitle = QCircleTitle(this, getString(R.string.news_module_name), Color.WHITE, colorRes(R.color.md_deep_orange_500))
        qcircleTitle.setTextSize(17F)
        template.addElement(qcircleTitle)
        setContentViewToMain(R.layout.news_activity)
        setContentView(template.getView())
        back.setOnClickListener { finish() }
        refresh.setOnClickListener { download() }
        refreshFabWrapper.attachListener {
            lock = false
            setData()
        }
        initFab()
        val token = manager.getTokenFromPrefs()
        if (token == null) {
            // User not login in
            onFail(DownloadManager.DownloadError.Login)
        } else {
            entries = manager.getFeedFromPrefs()
            if (entries != null) setData()
            download()
        }
    }

    fun download() {
        if (!lock) {
            lock = true
            errorLayout.hide()
            if (manager.isNetworkAvailable()) {
                qCircleToast(R.string.start_downloading)
                refreshFabWrapper.show()
                manager.download(this)
            } else {
                if (entries == null || entries!!.size() == 0) {
                    onFail(DownloadManager.DownloadError.Internet)
                } else {
                    qCircleToast(R.string.no_connection)
                }
            }
        }
    }

    fun setData() {
        errorLayout.hide()
        if (entries!!.size() == 0) {
            errorLayout.show()
            pager.setAdapter(null)
            title.setText(R.string.news_no_content)
            text.setText(R.string.news_no_content_subtext)
        } else {
            pager.setAdapter(NewsAdapter(getFragmentManager(), entries!!.size()))
        }
    }

    override fun onFail(error: DownloadManager.DownloadError) {
        lock = false
        mainThread {
            refreshFabWrapper.hide()
            when (error) {
                DownloadManager.DownloadError.Login -> {
                    errorLayout.show()
                    title.setText(R.string.news_should_login)
                    text.setText(R.string.news_should_login_subtext)
                    shouldOpenLogin = true
                }
                DownloadManager.DownloadError.Internet, DownloadManager.DownloadError.Other -> {
                    if (entries == null || entries!!.size() == 0) {
                        errorLayout.show()
                        title.setText(R.string.news_network_error)
                        text.setText(R.string.news_network_error_subtext)
                    }
                    qCircleToast(R.string.no_connection)
                }
            }
        }
    }

    override fun onSuccess(entries: ArrayList<Entry>) {
        this.entries = entries
        mainThread {
            refreshFabWrapper.beginFinalAnimation()
        }
    }

    protected override fun getIntentToShow(): Intent? {
        if (shouldOpenLogin)
            return intent<LoginActivity>()
        else if (entries == null || entries!!.size() == 0)
            return null
        else {
            return Intent(Intent.ACTION_VIEW, Uri.parse(entries!![pager.getCurrentItem()].getAlternate().get(0).getHref()))
        }
    }

    fun initFab() {
        // Bugfix
        refreshFabWrapper.measure(View.MeasureSpec.makeMeasureSpec(refreshFabWrapper.getMeasuredWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(refreshFabWrapper.getMeasuredHeight(), View.MeasureSpec.EXACTLY))

        refresh.setBackgroundTintList(ColorStateList.valueOf(colorRes(R.color.primary)))
    }

}