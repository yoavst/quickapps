package com.yoavst.quickapps.clock

import com.lge.qcircle.template.QCircleTemplate
import kotlin.properties.Delegates
import com.lge.qcircle.template.TemplateType
import com.yoavst.quickapps.util.QCircleActivity
import android.os.Bundle
import com.yoavst.quickapps.R
import com.lge.qcircle.template.TemplateTag
import android.content.Intent
import com.mobsandgeeks.ake.getIntent
import android.graphics.Color
import android.view.LayoutInflater
import com.yoavst.quickapps.clock.ClockFragment
import com.yoavst.quickapps.clock.StopwatchManager

/**
 * Created by Yoav.
 */
public class CStopwatchActivity : QCircleActivity() {
    override val template: QCircleTemplate by Delegates.lazy { QCircleTemplate(this, TemplateType.CIRCLE_EMPTY) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        template.setBackButton()
        template.setTitle(getString(R.string.clock_module_name), Color.WHITE, getResources().getColor(R.color.clock_theme_color))
        template.setTitleTextSize(17F)
        val main = template.getLayoutById(TemplateTag.CONTENT_MAIN)
        val layout = LayoutInflater.from(this).inflate(R.layout.stopwatch_layout, main, false)
        main.addView(layout)
        setContentView(template.getView())
        getFragmentManager().beginTransaction().replace(R.id.content, ClockFragment()).commit()
    }

    public override fun onPause() {
        super.onPause()
        StopwatchManager.runOnBackground()
    }

    protected override fun getIntentToShow(): Intent {
        return getIntent<PhoneActivity>().putExtra("com.lge.app.floating.launchAsFloating", true)
    }
}