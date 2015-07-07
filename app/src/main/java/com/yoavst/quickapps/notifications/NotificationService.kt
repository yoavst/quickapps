package com.yoavst.quickapps.notifications

import android.app.Notification
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.provider.Settings
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.yoavst.kotlin.startActivity
import com.yoavst.quickapps.CoverReceiver
import com.yoavst.quickapps.tools.startActivityOnNotification
import java.util.ArrayList
import java.util.Arrays

/**
 * Created by Yoav.
 */
public class NotificationService : NotificationListenerService() {
    private val mBinder = LocalBinder()
    private var callback: Callback? = null

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        if (callback != null)
            callback!!.onNotificationPosted(sbn)
        else if (CoverReceiver.isCoverInUse && !CNotificationActivity.isOpenNow && startActivityOnNotification) {
            if (sbn.getNotification().getGroup() == Notification.CATEGORY_TRANSPORT) return
            for (ignoredPopPackage in IGNORED_POP_PACKAGES) {
                if (ignoredPopPackage == sbn.getPackageName()) return
            }
            startActivity<CNotificationActivity>()
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        if (callback != null) callback!!.onNotificationRemoved(sbn)
    }

    override fun onBind(intent: Intent): IBinder {
        if (intent.getAction() == NOTIFICATION_ACTION) {
            return mBinder
        } else {
            return super.onBind(intent)
        }
    }

    override fun onUnbind(intent: Intent): Boolean {
        callback = null
        return super.onUnbind(intent)
    }

    public fun setCallback(callback: Callback?, runnable: (() -> Unit)?) {
        this.callback = callback
        if (this.callback != null) {
            val contentResolver = getContentResolver()
            val enabledNotificationListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners")
            val packageName = getPackageName()
            // check to see if the enabledNotificationListeners String contains our package name
            if (enabledNotificationListeners == null || !enabledNotificationListeners.contains(packageName) || !enabledNotificationListeners.contains("NotificationService")) {
                this.callback!!.noPermissionForNotifications()
            } else
                if (runnable != null) runnable()
        }
    }

    public fun setActiveNotifications() {
        try {
            NotificationsManager.notifications = ArrayList(Arrays.asList<StatusBarNotification>(*getActiveNotifications()))
        } catch (exception: Exception) {
            exception.printStackTrace()
            callback!!.noPermissionForNotifications()
        }

    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public inner class LocalBinder : Binder() {
        fun getService(): NotificationService {
            // Return this instance of LocalService so clients can call public methods
            return this@NotificationService
        }
    }

    public interface Callback {
        public fun onNotificationPosted(statusBarNotification: StatusBarNotification)

        public fun onNotificationRemoved(statusBarNotification: StatusBarNotification)

        public fun noPermissionForNotifications()
    }

    companion object {
        public val NOTIFICATION_ACTION: String = "notification_action"
        private val IGNORED_POP_PACKAGES = arrayOf("com.android.incallui")
    }
}
