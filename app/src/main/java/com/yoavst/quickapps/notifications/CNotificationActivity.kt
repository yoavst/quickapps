package com.yoavst.quickapps.notifications

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.os.Bundle
import android.os.IBinder
import android.service.notification.StatusBarNotification
import android.view.LayoutInflater
import android.view.View
import com.lge.qcircle.template.QCircleBackButton
import com.lge.qcircle.template.QCircleTitle
import com.lge.qcircle.template.TemplateTag
import com.yoavst.kotlin.*
import com.yoavst.quickapps.R
import com.yoavst.quickapps.tools.QCircleActivity
import com.yoavst.quickapps.tools.createExplicit
import com.yoavst.quickapps.tools.qCircleToast
import kotlinx.android.synthetic.notification_activity.*
import kotlin.properties.Delegates


public class CNotificationActivity : QCircleActivity(), ServiceConnection, NotificationService.Callback {
    var service: NotificationService? = null
    var bound = false
    public var shouldRegister: Boolean = false
    var adapter: NotificationAdapter by Delegates.notNull()

    protected override fun onCreate(savedInstanceState: Bundle?) {
        super<QCircleActivity>.onCreate(savedInstanceState)
        isOpenNow = true
        val title = QCircleTitle(this, getString(R.string.notification_module_name), Color.WHITE, colorRes(R.color.md_orange_A400))
        title.setTextSize(17f)
        template.addElement(title)
        template.addElement(QCircleBackButton(this))
        val main = template.getLayoutById(TemplateTag.CONTENT_MAIN)
        main.addView(LayoutInflater.from(this).inflate(R.layout.notification_activity, main, false))
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
            if (beforeLollipop())
                service!!.cancelNotification(notification.getPackageName(), notification.getTag(), notification.getId())
            else
                service!!.cancelNotification(notification.getKey())
            qCircleToast(R.string.notification_removed)
        }
    }

    override fun onStart() {
        super<QCircleActivity>.onStart()
        bindService(intent<NotificationService>().setAction(NotificationService.NOTIFICATION_ACTION).createExplicit(this), this, Context.BIND_AUTO_CREATE)
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
        mainThread {
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
        mainThread {
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
        mainThread { errorLayout.hide() }
    }

    protected fun hideContent() {
        mainThread {
            pager.setVisibility(View.INVISIBLE)
            indicator.setVisibility(View.INVISIBLE)
        }
    }

    protected fun showContent() {
        mainThread {
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

    companion object {
        public var isOpenNow: Boolean = false
    }

}
