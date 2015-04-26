package com.yoavst.quickapps.torch

import android.app.Activity
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
import com.yoavst.kotlin.notificationManager
import com.yoavst.kotlin.viewById
import com.yoavst.quickapps.PrefManager
import com.yoavst.quickapps.R
import kotlin.properties.Delegates

public class PhoneActivityNotFloating : Activity() {
    private val notificationManager: NotificationManager by Delegates.lazy { notificationManager() }
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
        Torch.createNotification(this)
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
            icon!!.setText(Torch.TORCH_ON)
            icon!!.setTextColor(colorTorchOn)
            icon!!.setBackgroundColor(colorBackgroundOn)
        }

    }

    private fun showTorchOff() {
        icon?.let {
            icon!!.setText(Torch.TORCH_OFF)
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
            notificationManager.notify(NotificationReceiver.NOTIFICATION_ID, Torch.notification)
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
}