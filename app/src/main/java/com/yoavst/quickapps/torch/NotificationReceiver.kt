package com.yoavst.quickapps.torch

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Receive `com.yoavst.notificationtorch` broadcast and disables the torch and frees the camera.
 */
public class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // Turn on the torch and disable all
        CameraManager.disableTorch()
        CameraManager.destroy()
    }

    companion object {
        public val NOTIFICATION_ID: Int = 1423
    }
}