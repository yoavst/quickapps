package com.yoavst.quickapps.compass

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.RelativeLayout
import com.lge.qcircle.template.ButtonTheme
import com.lge.qcircle.template.QCircleBackButton
import com.lge.qcircle.template.TemplateTag
import com.yoavst.kotlin.drawableRes
import com.yoavst.kotlin.intent
import com.yoavst.quickapps.R
import com.yoavst.quickapps.tools.QCircleActivity
import com.yoavst.quickapps.tools.isLGRom
import kotlin.properties.Delegates

/**
 * Created by Yoav.
 */
public class CCompassActivity : QCircleActivity() {
    var compass: Compass by Delegates.notNull()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val backButton = QCircleBackButton(this, ButtonTheme.DARK, true)
        template.addElement(backButton)
        template.setBackgroundDrawable(drawableRes(R.drawable.compass_back))
        val mainLayout = template.getLayoutById(TemplateTag.CONTENT).getParent() as RelativeLayout
        val needle = getLayoutInflater().inflate(R.layout.compass_activity, mainLayout, false) as ImageView
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