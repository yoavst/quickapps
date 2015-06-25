package com.yoavst.quickapps.torch

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.yoavst.quickapps.R

/**
 * Helper class for notification creating. Needed to be initialized in order to work, using `Torch(context)`.
 */
public object Torch {
    public var notification: Notification? = null
    public val killAllInstances: String = "com.yoavst.torch.killAll"
    public fun invoke(context: Context) {
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
