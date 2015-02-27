package com.yoavst.quickapps.torch

import com.yoavst.quickapps.util.QCircleActivity
import com.lge.qcircle.template.QCircleTemplate
import android.content.Intent
import android.app.NotificationManager
import kotlin.properties.Delegates
import com.mobsandgeeks.ake.getColor
import com.yoavst.quickapps.R
import android.widget.TextView
import com.mobsandgeeks.ake.notificationManager
import android.os.Bundle
import com.lge.qcircle.template.TemplateType
import com.mobsandgeeks.ake.getIntent
import com.lge.qcircle.template.TemplateTag
import com.mobsandgeeks.ake.shortToast
import android.view.Gravity
/**
 * Created by Yoav.
 */
public class CTorchActivity : QCircleActivity() {
    override val template: QCircleTemplate by Delegates.lazy { QCircleTemplate(this, TemplateType.CIRCLE_EMPTY) }
    private val notificationManager: NotificationManager by Delegates.lazy { notificationManager() }
    private val colorBackgroundOn by Delegates.lazy { getColor(R.color.torch_background_color_on) }
    private val colorBackgroundOff by Delegates.lazy { getColor(R.color.torch_background_color_off) }
    private val colorTorchOn by Delegates.lazy { getColor(R.color.torch_color_on) }
    private val colorTorchOff by Delegates.lazy { getColor(R.color.torch_color_off) }
    private var icon: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val main = template.getLayoutById(TemplateTag.CONTENT_MAIN)
        icon = getLayoutInflater().inflate(R.layout.torch_layout, main, false) as TextView
        main.addView(icon)
        template.setBackButton {(view) ->
            CameraManager.disableTorch()
            CameraManager.destroy()
            notificationManager.cancel(NotificationReceiver.NOTIFICATION_ID)
        }
        setContentView(template.getView())
    }

    public override fun onResume() {
        super.onResume()
        try {
            CameraManager.init(this)
        } catch (e: RuntimeException) {

            val toast = shortToast("Error connect camera");
            toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 0)
            toast.show()
        }
        if (CameraManager.isTorchOn()) {
            showTorchOn()
        } else showTorchOff()
    }

    private fun showTorchOn() {
        icon!!.setText(PhoneActivity.TORCH_ON)
        icon!!.setTextColor(colorTorchOn)
        template.setBackButtonTheme(false)
        template.setBackgroundColor(getResources().getColor(R.color.torch_background_color_on), true)
    }

    private fun showTorchOff() {
        icon!!.setText(PhoneActivity.TORCH_OFF)
        icon!!.setTextColor(colorTorchOff)
        template.setBackButtonTheme(true)
        template.setBackgroundColor(getResources().getColor(R.color.torch_background_color_off), true)
    }
    public fun toggleTorch() {
        if (CameraManager.toggleTorch()) {
            showTorchOn()
        } else {
            showTorchOff()
        }
    }

    protected override fun onSingleTapConfirmed(): Boolean {
        toggleTorch()
        return true
    }

    override fun getIntentToShow(): Intent = getIntent<PhoneActivity>()
}