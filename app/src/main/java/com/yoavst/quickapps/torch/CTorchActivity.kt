package com.yoavst.quickapps.torch

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.RelativeLayout
import com.lge.qcircle.template.ButtonTheme
import com.lge.qcircle.template.QCircleBackButton
import com.yoavst.kotlin.*
import com.yoavst.quickapps.R
import com.yoavst.quickapps.tools.*
import kotlinx.android.synthetic.torch_activity.offIcon
import kotlinx.android.synthetic.torch_activity.offIconAnimation
import kotlinx.android.synthetic.torch_activity.offLayout
import kotlinx.android.synthetic.torch_activity.onLayout
import kotlin.properties.Delegates

/**
 * QCircle torch Activity.
 */
public class CTorchActivity : QCircleActivity() {
    val delegation: TorchDelegate by Delegates.lazy { TorchDelegate(this, offIcon, offIconAnimation, offLayout, onLayout) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val backButton = QCircleBackButton(this, ButtonTheme.DARK, true) {
            async {
                notificationManager().cancel(NotificationReceiver.NOTIFICATION_ID)
                CameraManager.disableTorch()
                CameraManager.destroy()
            }
        }
        template.addElement(backButton)
        setContentView(template.getView())
        val container = getMainLayout().getParent().getParent() as RelativeLayout
       container.addView(getLayoutInflater().inflate(R.layout.torch_activity, container, false),0)
        delegation.init()
    }

    /**
     * Toggles the torch on tap confirmed.
     */
    override fun onSingleTapConfirmed(): Boolean {
        delegation.toggleTorch()
        return true
    }

    public override fun onResume() {
        super.onResume()
        try {
            CameraManager(this)
            CameraManager.init()
        } catch (e: RuntimeException) {
            qCircleToast("Error connect camera");
        }
        delegation.showCurrentMode()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!CameraManager.isTorchOn() && notBecauseOfIntent)
            CameraManager.destroy()
    }

    /**
     * Returns regular activity if not LG ROM, else return the QSlide activity.
     * @return Regular activity if not LG ROM, else return the QSlide activity.
     */
    override fun getIntentToShow(): Intent {
        if (!isLGRom(this))
        return intent<PhoneActivityNotFloating>()
        else return intent<PhoneActivity>()
    }
}