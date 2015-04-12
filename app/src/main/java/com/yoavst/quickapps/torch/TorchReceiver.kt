package com.yoavst.quickapps.torch

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.yoavst.kotlin.notificationManager

/**
 * Created by Yoav.
 */
public class TorchReceiver : BroadcastReceiver() {
    var mNotificationManager: NotificationManager? = null

    override fun onReceive(context: Context, intent: Intent) {
        CameraManager.init(context)
        if (mNotificationManager == null)
            mNotificationManager = context.notificationManager()
        PhoneActivity.createNotification(context)
        if (CameraManager.toggleTorch()) {
            mNotificationManager!!.notify(NotificationReceiver.NOTIFICATION_ID, PhoneActivity.notification)
        } else {
            mNotificationManager!!.cancel(NotificationReceiver.NOTIFICATION_ID)
            CameraManager.destroy()
        }
    }
}