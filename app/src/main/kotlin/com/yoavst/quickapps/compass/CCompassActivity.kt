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
import com.mobsandgeeks.ake.getIntent

/**
 * Created by Yoav.
 */
public class CCompassActivity: QCircleActivity() {
    override val template: QCircleTemplate by Delegates.lazy { QCircleTemplate(this, TemplateType.CIRCLE_EMPTY) }
    var compass: Compass by Delegates.notNull()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        template.setBackButton()
        template.setBackButtonTheme(true)
        template.setBackgroundDrawable(getResources().getDrawable(R.drawable.compass_back), true)
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

    protected override fun getIntentToShow(): Intent {
        return getIntent<PhoneActivity>().putExtra("com.lge.app.floating.launchAsFloating", true)
    }
}