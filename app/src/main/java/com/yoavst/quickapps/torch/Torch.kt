package com.yoavst.quickapps.torch

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.yoavst.quickapps.R

/**
 * Created by yoavst.
 */
public object Torch {
    public val TORCH_OFF: String = "{md-flash-off}"
    public val TORCH_ON: String = "{md-flash-on}"
    public var notification: Notification? = null

    public fun createNotification(context: Context) {
        if (notification == null) {
            val intent = Intent("com.yoavst.notificationtorch")
            val pIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
            notification = Notification.Builder(context)
                    .setContentTitle(context.getString(R.string.torch_is_on))
                    .setContentText(context.getString(R.string.touch_to_turn_off))
                    .setSmallIcon(R.drawable.ic_noti_torch)
                    .setAutoCancel(true)
                    .setContentIntent(pIntent)
                    .build()
            notification!!.flags = notification!!.flags or Notification.FLAG_ONGOING_EVENT
        }
    }
}