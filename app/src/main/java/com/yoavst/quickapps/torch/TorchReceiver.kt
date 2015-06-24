package com.yoavst.quickapps.torch

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.yoavst.kotlin.notificationManager

/**
 * Receive `com.yoavst.toggletorch` broadcast and toggle the torch. It also responsible for showing/hiding the notification.
 */
public class TorchReceiver : BroadcastReceiver() {
    var mNotificationManager: NotificationManager? = null

    override fun onReceive(context: Context, intent: Intent) {
        CameraManager(context)
        if (mNotificationManager == null)
            mNotificationManager = context.notificationManager()
        Torch(context)
        if (CameraManager.toggleTorch()) {
            mNotificationManager!!.notify(NotificationReceiver.NOTIFICATION_ID, Torch.notification)
        } else {
            mNotificationManager!!.cancel(NotificationReceiver.NOTIFICATION_ID)
            CameraManager.destroy()
        }
    }
}