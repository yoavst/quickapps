package com.yoavst.quickapps.torch

import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import com.lge.qcircle.template.QCircleBackButton
import com.lge.qcircle.template.QCircleTemplate
import com.lge.qcircle.template.TemplateTag
import com.lge.qcircle.template.TemplateType
import com.yoavst.kotlin.colorResource
import com.yoavst.kotlin.intent
import com.yoavst.kotlin.systemService
import com.yoavst.quickapps.R
import com.yoavst.quickapps.util.QCircleActivity
import com.yoavst.util.qCircleToast
import kotlin.properties.Delegates

/**
 * Created by Yoav.
 */
public class CTorchActivity : QCircleActivity() {
    override val template: QCircleTemplate by Delegates.lazy { QCircleTemplate(this, TemplateType.CIRCLE_EMPTY) }
    private val notificationManager: NotificationManager by systemService()
    private val colorBackgroundOn by colorResource(R.color.torch_background_color_on)
    private val colorBackgroundOff by colorResource(R.color.torch_background_color_off)
    private val colorTorchOn by colorResource(R.color.torch_color_on)
    private val colorTorchOff by colorResource(R.color.torch_color_off)
    private var icon: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val main = template.getLayoutById(TemplateTag.CONTENT_MAIN)
        icon = getLayoutInflater().inflate(R.layout.torch_layout, main, false) as TextView
        main.addView(icon)
        val backButton = QCircleBackButton(this) {
            CameraManager.disableTorch()
            CameraManager.destroy()
            notificationManager.cancel(NotificationReceiver.NOTIFICATION_ID)
        }
        template.addElement(backButton)
        setContentView(template.getView())
    }

    public override fun onResume() {
        super.onResume()
        try {
            CameraManager.init(this)
        } catch (e: RuntimeException) {
            qCircleToast("Error connect camera");
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

    override fun getIntentToShow(): Intent = intent<PhoneActivity>()
}