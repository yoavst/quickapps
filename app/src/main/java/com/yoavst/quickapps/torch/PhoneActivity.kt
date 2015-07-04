package com.yoavst.quickapps.torch

import android.app.NotificationManager
import android.content.IntentFilter
import android.os.Bundle
import com.lge.app.floating.FloatableActivity
import com.lge.app.floating.FloatingWindow
import com.yoavst.kotlin.broadcastReceiver
import com.yoavst.kotlin.hide
import com.yoavst.kotlin.systemService
import com.yoavst.quickapps.R
import com.yoavst.quickapps.tools.torchForceFloating
import kotlinx.android.synthetic.torch_activity.offIcon
import kotlinx.android.synthetic.torch_activity.offIconAnimation
import kotlinx.android.synthetic.torch_activity.offLayout
import kotlinx.android.synthetic.torch_activity.onLayout
import kotlin.properties.Delegates

/**
 * The `QSlide` torch Activity.
 */
public class PhoneActivity : FloatableActivity() {
    val notificationManager: NotificationManager by systemService()
    val delegation: TorchDelegate by Delegates.lazy { TorchDelegate(this, offIcon, offIconAnimation, offLayout, onLayout) }
    var isDestroyingForFloatingMode = false
    var isDestroyForQCircle = false
    val receiver = broadcastReceiver { context, intent ->
        isDestroyForQCircle = true
        finish()
    }

    override fun onCreate(savedInstance: Bundle?) {
        super.onCreate(savedInstance)
        setContentView(R.layout.torch_activity)
        setDontFinishOnFloatingMode(true)
        Torch(this)
        if (torchForceFloating && !isInFloatingMode())
            switchToFloatingMode()
        else delegation.init()

    }

    override fun switchToFloatingMode() {
        isDestroyingForFloatingMode = true
        super.switchToFloatingMode()
    }

    override fun onAttachedToFloatingWindow(w: FloatingWindow) {
        super.onAttachedToFloatingWindow(w)
        if (torchForceFloating)
            w.findViewWithTag(FloatingWindow.Tag.FULLSCREEN_BUTTON).hide()
        isDestroyingForFloatingMode = false
        CameraManager.init()
        delegation.init()
    }


    override fun onDetachedFromFloatingWindow(w: FloatingWindow, isReturningToFullScreen: Boolean): Boolean {
        if (!isReturningToFullScreen) {
            if (CameraManager.isTorchOn())
                notificationManager.notify(NotificationReceiver.NOTIFICATION_ID, Torch.notification)
            else
                CameraManager.destroy()
        }
        return super.onDetachedFromFloatingWindow(w, isReturningToFullScreen)
    }


    override fun onPause() {
        super.onPause()
        if (CameraManager.isTorchOn())
            notificationManager.notify(NotificationReceiver.NOTIFICATION_ID, Torch.notification)
        else if (isDestroyForQCircle || !isDestroyingForFloatingMode)
            CameraManager.destroy()
    }

    override fun onResume() {
        super.onResume()
        CameraManager(this)
        CameraManager.init()
        Torch(this)
        notificationManager.cancel(NotificationReceiver.NOTIFICATION_ID)
        delegation.showCurrentMode()
        registerReceiver(receiver, IntentFilter(Torch.killAllInstances))
    }

    override fun onDestroy() {
        super.onDestroy()
        notificationManager.cancel(NotificationReceiver.NOTIFICATION_ID)
        unregisterReceiver(receiver)
    }
}
