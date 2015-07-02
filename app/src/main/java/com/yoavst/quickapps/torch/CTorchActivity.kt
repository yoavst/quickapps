package com.yoavst.quickapps.torch

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.RelativeLayout
import com.lge.qcircle.template.ButtonTheme
import com.lge.qcircle.template.QCircleBackButton
import com.yoavst.kotlin.async
import com.yoavst.kotlin.broadcastReceiver
import com.yoavst.kotlin.intent
import com.yoavst.kotlin.notificationManager
import com.yoavst.quickapps.R
import com.yoavst.quickapps.tools.QCircleActivity
import com.yoavst.quickapps.tools.isLGRom
import com.yoavst.quickapps.tools.qCircleToast
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
        container.addView(getLayoutInflater().inflate(R.layout.torch_activity, container, false), 1)
        delegation.init()
        sendBroadcast(Intent(Torch.killAllInstances))
        offLayout.setOnClickListener { delegation.toggleTorch() }
        onLayout.setOnClickListener { delegation.toggleTorch() }
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