package com.yoavst.quickapps.music

import android.content.Intent
import android.graphics.Bitmap
import android.os.Binder
import android.os.IBinder
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

/**
 * Base class for the remote control service.
 */
public abstract class AbstractRemoteControlService : NotificationListenerService() {
    private val mBinder = RCBinder()
    protected var callback: Callback? = null

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

    /**
     * Return the intent of the current client.
     */
    abstract fun getCurrentClientIntent(): Intent?

    /**
     * Enables the remote controller
     */
    abstract fun setRemoteControllerEnabled(): Boolean

    /**
     * Disables the remote controller
     */
    abstract fun setRemoteControllerDisabled()

    /**
     * Tells the client to go the next song
     */
    abstract fun sendNextKey()

    /**
     * Tells the client to pause the song
     */
    abstract fun sendPauseKey()

    /**
     * Tells the client to resume the song
     */
    abstract fun sendPlayKey()

    /**
     * Tells the client to go back to the previous song.
     */
    abstract fun sendPreviousKey()

    /**
     * Returns true if playing.
     * @return True if playing
     */
    abstract fun isPlaying(): Boolean

    /**
     * Returns position and duration.
     * @return Position and duration
     */
    abstract fun getPosition(): Int

    /**
     * We do not use notification listening, so it is ignored
     */
    override fun onNotificationPosted(notification: StatusBarNotification) {
    }

    /**
     * We do not use notification listening, so it is ignored
     */
    override fun onNotificationRemoved(notification: StatusBarNotification) {
    }

    /**
     * Disable the remote controller on destroy of the service
     */
    override fun onDestroy() {
        setRemoteControllerDisabled()
    }

    protected fun millisToSeconds(value: Long): Int {
        if (value < 0) return -1
        else return value.toInt() / 1000
    }


    public interface Callback {
        public fun onMediaMetadataChanged(artist: String, title: String, duration: Int, albumArt: Bitmap?)
        public fun onPlaybackStateChanged(state: Int)
        public fun onClientChange(clearing: Boolean)

    }

    public open fun setListener(callback: Callback?) {
        this.callback = callback
    }

    companion object {
        protected val BITMAP_HEIGHT: Int = 1100
        protected val BITMAP_WIDTH: Int = 1100
    }
}