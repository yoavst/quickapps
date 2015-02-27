package com.yoavst.quickapps.notifications

import com.yoavst.quickapps.util.QCircleActivity
import com.lge.qcircle.template.QCircleTemplate
import kotlin.properties.Delegates
import com.lge.qcircle.template.TemplateType
import android.content.Intent
import android.support.v4.view.ViewPager
import android.widget.TextView
import android.widget.RelativeLayout
import com.viewpagerindicator.CirclePageIndicator
import android.content.ServiceConnection
import android.os.Bundle
import com.yoavst.quickapps.R
import android.graphics.Color
import com.mobsandgeeks.ake.getColor
import android.view.LayoutInflater
import com.lge.qcircle.template.TemplateTag
import butterknife.bindView
import com.mobsandgeeks.ake.show
import com.yoavst.mashov.AsyncJob
import com.mobsandgeeks.ake.hide
import com.mobsandgeeks.ake.getIntent
import android.view.View
import android.content.Context
import com.yoavst.util.createExplicit
import android.content.ComponentName
import android.os.IBinder
import android.service.notification.StatusBarNotification
import com.mobsandgeeks.ake.preLollipop
import android.widget.Toast
import com.yoavst.util.qCircleToast

/**
 * Created by Yoav.
 */
public class CNotificationActivity : QCircleActivity(), ServiceConnection, NotificationService.Callback {
    override val template: QCircleTemplate by Delegates.lazy { QCircleTemplate(this, TemplateType.CIRCLE_EMPTY) }

    var service: NotificationService? = null
    var bound = false
    public var shouldRegister: Boolean = false
    var adapter: NotificationAdapter by Delegates.notNull()
    val pager: ViewPager by bindView(R.id.notification_pager)
    val titleError: TextView  by bindView(R.id.title_error)
    val imageError: TextView  by bindView(R.id.image_error)
    val extraError: TextView  by bindView(R.id.extra_error)
    val errorLayout: RelativeLayout  by bindView(R.id.error_layout)
    val indicator: CirclePageIndicator  by bindView(R.id.notification_indicator)
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super<QCircleActivity>.onCreate(savedInstanceState)
        isOpenNow = true
        template.setTitle(getString(R.string.notification_module_name), Color.WHITE, getColor(R.color.md_orange_A400))
        template.setTitleTextSize(17F)
        template.setBackButton()
        val main = template.getLayoutById(TemplateTag.CONTENT_MAIN)
        main.addView(LayoutInflater.from(this).inflate(R.layout.notification_circle_container_layout, main, false))
        setContentView(template.getView())
    }


    override fun noPermissionForNotifications() {
        shouldRegister = true
        errorLayout.show()
        titleError.setText(R.string.open_the_case)
        extraError.setText(R.string.register_us_please)
        indicator.hide()
    }

    public override fun onDestroy() {
        service?.setCallback(null, null)
        unbindService(this)
        service = null
        NotificationsManager.clean()
        isOpenNow = false
        super<QCircleActivity>.onDestroy()
    }

    public fun cancelNotification(notification: StatusBarNotification?) {
        if (notification != null && bound && !shouldRegister && service != null) {
            if (preLollipop())
                service!!.cancelNotification(notification.getPackageName(), notification.getTag(), notification.getId())
            else
                service!!.cancelNotification(notification.getKey())
            qCircleToast(R.string.notification_removed)
        }
    }

    override fun onStart() {
        super<QCircleActivity>.onStart()
        bindService(getIntent<NotificationService>().setAction(NotificationService.NOTIFICATION_ACTION).createExplicit(this), this, Context.BIND_AUTO_CREATE)
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        bound = true
        this.service = (service as NotificationService.LocalBinder).getService()
        this.service!!.setCallback(this) { initNotifications() }
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        bound = false
        service = null
    }


    override fun onNotificationPosted(statusBarNotification: StatusBarNotification) {
        AsyncJob.doOnMainThread {
            if (statusBarNotification.getPackageName() == "com.whatsapp") {
                service!!.setActiveNotifications()
                NotificationsManager.whatsapp()
            } else {
                NotificationsManager.addNotification(statusBarNotification)
            }
            updateAdapter()
            hideError()
            showContent()
        }
    }

    override fun onNotificationRemoved(statusBarNotification: StatusBarNotification) {
        AsyncJob.doOnMainThread {
            if (!isDestroyed()) {
                NotificationsManager.removeNotification(statusBarNotification)
                updateAdapter()
                if (NotificationsManager.getCount() == 0) {
                    showEmpty()
                }
            }
        }
    }

    fun updateAdapter() {
        if (!isDestroyed()) {
            pager.getAdapter().notifyDataSetChanged()
            indicator.notifyDataSetChanged()
        }
    }

    public fun initNotifications() {
        if (bound && !shouldRegister) {
            service!!.setActiveNotifications()
            adapter = NotificationAdapter(getFragmentManager())
            pager.setAdapter(adapter)
            indicator.setViewPager(pager)
            if (NotificationsManager.getCount() == 0)
                showEmpty()
            else {
                hideError()
            }
        }
    }

    private fun showEmpty() {
        hideContent()
        errorLayout.show()
        titleError.setText(R.string.notification_empty)
        extraError.setText("")
    }

    protected fun hideError() {
        AsyncJob.doOnMainThread { errorLayout.hide() }
    }

    protected fun hideContent() {
        AsyncJob.doOnMainThread {
            pager.setVisibility(View.INVISIBLE)
            indicator.setVisibility(View.INVISIBLE)
        }
    }

    protected fun showContent() {
        AsyncJob.doOnMainThread {
            pager.show()
            indicator.show()
        }

    }

    private fun getActiveFragment(): NotificationsFragment? {
        try {
            return adapter.getActiveFragment(pager.getCurrentItem()) as NotificationsFragment
        } catch (e: Exception) {
            return null
        }

    }

    override fun getIntentToShow(): Intent? {
        if (shouldRegister)
            return Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
        else {
            try {
                val statusBarNotification = getActiveFragment()!!.notification
                statusBarNotification!!.getNotification().contentIntent.send()
            } catch (e: Exception) {
                // Do nothing
            }
            return null
        }
    }

    class object {
        public var isOpenNow: Boolean = false
    }

}
