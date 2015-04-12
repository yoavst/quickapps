package com.yoavst.quickapps.music

import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

/**
 * Created by Yoav.
 */
public abstract class AbstractRemoteControlService : NotificationListenerService() {
    private val mBinder = RCBinder()

    public inner class RCBinder : Binder() {
        public fun getService(): AbstractRemoteControlService {
            return this@AbstractRemoteControlService
        }
    }

    override fun onUnbind(intent: Intent): Boolean {
        stopSelf()
        return false
    }

    override fun onBind(intent: Intent): IBinder {
        if (intent.getAction().startsWith("com.yoavst.quickmusic.BIND_RC_CONTROL")) {
            return mBinder
        } else {
            return super.onBind(intent)
        }
    }

    abstract fun getCurrentClientIntent(): Intent?

    abstract fun setRemoteControllerEnabled(): Boolean

    abstract fun setRemoteControllerDisabled()

    abstract fun sendNextKey()

    abstract fun sendPauseKey()

    abstract fun sendPlayKey()

    abstract fun sendPreviousKey()

    override fun onNotificationPosted(notification: StatusBarNotification) {
    }

    override fun onNotificationRemoved(notification: StatusBarNotification) {
    }

    override fun onDestroy() {
        setRemoteControllerDisabled()
    }

    companion object {
        protected val BITMAP_HEIGHT: Int = 1100
        protected val BITMAP_WIDTH: Int = 1100
    }
}
