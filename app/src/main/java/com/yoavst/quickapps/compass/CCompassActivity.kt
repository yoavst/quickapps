package com.yoavst.quickapps.compass

import com.lge.qcircle.template.QCircleTemplate
import kotlin.properties.Delegates
import com.lge.qcircle.template.TemplateType
import com.yoavst.quickapps.util.QCircleActivity
import android.os.Bundle
import com.yoavst.quickapps.R
import com.lge.qcircle.template.TemplateTag
import android.widget.RelativeLayout
import android.widget.ImageView
import android.content.Intent
import com.lge.qcircle.template.QCircleBackButton
import com.lge.qcircle.utils.QCircleFeature
import com.yoavst.kotlin.drawableRes
import com.yoavst.kotlin.intent
import com.yoavst.util.isLGRom

/**
 * Created by Yoav.
 */
public class CCompassActivity: QCircleActivity() {
    override val template: QCircleTemplate by Delegates.lazy { QCircleTemplate(this, TemplateType.CIRCLE_EMPTY) }
    var compass: Compass by Delegates.notNull()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val backButton = QCircleBackButton(this)
        backButton.isDark(true)
        backButton.setBackgroundTransparent()
        template.addElement(backButton)
        template.setBackgroundDrawable(drawableRes(R.drawable.compass_back))
        val mainLayout = template.getLayoutById(TemplateTag.CONTENT).getParent() as RelativeLayout
        val needle = getLayoutInflater().inflate(R.layout.compass_circle_layout, mainLayout, false) as ImageView
        compass = Compass(this, needle)
        mainLayout.addView(needle)
        setContentView(template.getView())
    }

    protected override fun onResume() {
        super.onResume()
        compass.registerService()
    }

    protected override fun onPause() {
        super.onPause()
        compass.unregisterService()
    }

    protected override fun getIntentToShow(): Intent? {
        return if (!isLGRom(this)) null else {
            intent<PhoneActivity>().putExtra("com.lge.app.floating.launchAsFloating", true)
        }
    }
}