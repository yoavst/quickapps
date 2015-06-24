package com.yoavst.quickapps.torch

import android.animation.Animator
import android.app.NotificationManager
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewAnimationUtils
import com.lge.app.floating.FloatableActivity
import com.lge.app.floating.FloatingWindow
import com.yoavst.kotlin.*
import com.yoavst.quickapps.R
import com.yoavst.quickapps.tools.BaseAnimationListener
import com.yoavst.quickapps.tools.autoStartTorch
import com.yoavst.quickapps.tools.torchForceFloating
import com.yoavst.quickapps.tools.getPressedColorRippleDrawable
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

    override fun onCreate(savedInstance: Bundle?) {
        super.onCreate(savedInstance)
        setContentView(R.layout.torch_activity)
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
        else if (!isDestroyingForFloatingMode)
            CameraManager.destroy()
    }

    override fun onResume() {
        super.onResume()
        CameraManager(this)
        CameraManager.init()
        Torch(this)
        notificationManager.cancel(NotificationReceiver.NOTIFICATION_ID)
       delegation.showCurrentMode()
    }

    override fun onDestroy() {
        super.onDestroy()
        notificationManager.cancel(NotificationReceiver.NOTIFICATION_ID)
    }
}
