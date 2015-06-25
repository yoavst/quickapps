package com.yoavst.quickapps.clock

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.RingtoneManager
import android.os.IBinder
import com.yoavst.kotlin.Bundle
import com.yoavst.kotlin.notificationManager
import com.yoavst.kotlin.startActivity
import com.yoavst.quickapps.CoverReceiver
import com.yoavst.quickapps.R

/**
 * Created by yoavst.
 */
public class TimerIntentService : Service() {
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Timer.runOnServer {
            if (Timer.timeLeft == 0L) {
                var alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                if (alarmUri == null) {
                    alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
                }
                if (CoverReceiver.isCoverInUse) {
                    startActivity<CClockActivity>(Bundle { putBoolean(CClockActivity.TimerShowFinishing, true)})
                } else {
                    val pIntent = PendingIntent.getBroadcast(this, 0, Intent("com.yoavst.empty"), 0)
                    val notification = Notification.Builder(this)
                            .setContentTitle(getString(R.string.timer_finished))
                            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                            .setSound(alarmUri)
                            .setContentIntent(pIntent)
                            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
                            .setAutoCancel(true)
                            .build()
                    notificationManager().notify(NotificationId, notification)
                }
                stopSelf()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    companion object {
        public val NotificationId: Int = 424242
    }
}