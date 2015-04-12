package com.yoavst.quickapps.torch

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import com.lge.app.floating.FloatableActivity
import com.lge.app.floating.FloatingWindow
import com.yoavst.kotlin.colorResource
import com.yoavst.kotlin.hide
import com.yoavst.kotlin.systemService
import com.yoavst.kotlin.viewById
import com.yoavst.quickapps.PrefManager
import com.yoavst.quickapps.R

public class PhoneActivity : FloatableActivity() {
    private val notificationManager: NotificationManager by systemService()
    private val colorBackgroundOn by colorResource(R.color.torch_background_color_on)
    private val colorBackgroundOff by colorResource(R.color.torch_background_color_off)
    private val colorTorchOn by colorResource(R.color.torch_color_on)
    private val colorTorchOff by colorResource(R.color.torch_color_off)
    private var icon: TextView? = null

    override fun onCreate(savedInstance: Bundle?) {
        super.onCreate(savedInstance)
        setContentView(R.layout.torch_layout)
        icon = viewById<TextView>(R.id.icon)
        icon?.setOnClickListener { toggleTorch() }

        createNotification(this)
        if (PrefManager(this).torchForceFloating().getOr(false) && !isInFloatingMode())
            switchToFloatingMode()

    }

    override fun onAttachedToFloatingWindow(w: FloatingWindow) {
        super.onAttachedToFloatingWindow(w)
        icon = viewById<TextView>(R.id.icon)
        icon?.setOnClickListener { toggleTorch() }
        if (PrefManager(this).torchForceFloating().getOr(false))
            w.findViewWithTag(FloatingWindow.Tag.FULLSCREEN_BUTTON).hide()
    }

    override fun onDetachedFromFloatingWindow(w: FloatingWindow, isReturningToFullScreen: Boolean): Boolean {
        if (!isReturningToFullScreen) {
            if (CameraManager.isTorchOn())
                notificationManager.notify(NotificationReceiver.NOTIFICATION_ID, notification)
            else
                CameraManager.destroy()
        }
        return super.onDetachedFromFloatingWindow(w, isReturningToFullScreen)
    }

    fun toggleTorch() {
        if (CameraManager.toggleTorch()) {
            showTorchOn()
        } else {
            showTorchOff()
        }
    }

    private fun showTorchOn() {
        icon?.let {
            icon!!.setText(TORCH_ON)
            icon!!.setTextColor(colorTorchOn)
            icon!!.setBackgroundColor(colorBackgroundOn)
        }

    }

    private fun showTorchOff() {
        icon?.let {
            icon!!.setText(TORCH_OFF)
            icon!!.setTextColor(colorTorchOff)
            icon!!.setBackgroundColor(colorBackgroundOff)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        notificationManager.cancel(NotificationReceiver.NOTIFICATION_ID)
        CameraManager.destroy()
    }

    override fun onBackPressed() {
        val startMain = Intent(Intent.ACTION_MAIN)
        startMain.addCategory(Intent.CATEGORY_HOME)
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(startMain)
    }

    override fun onPause() {
        super.onPause()
        if (CameraManager.isTorchOn())
            notificationManager.notify(NotificationReceiver.NOTIFICATION_ID, notification)
        else
            CameraManager.destroy()
    }

    override fun onResume() {
        super.onResume()
        CameraManager.init(this)
        notificationManager.cancel(NotificationReceiver.NOTIFICATION_ID)
        if (CameraManager.isTorchOn()) {
            showTorchOn()
            CameraManager.torch()
        } else
            showTorchOff()
    }

    companion object {
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
}