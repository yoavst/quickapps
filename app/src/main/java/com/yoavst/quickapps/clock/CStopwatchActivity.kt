package com.yoavst.quickapps.clock

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import com.lge.qcircle.template.*
import com.yoavst.kotlin.intent
import com.yoavst.quickapps.R
import com.yoavst.quickapps.util.QCircleActivity
import kotlin.properties.Delegates

/**
 * Created by Yoav.
 */
public class CStopwatchActivity : QCircleActivity() {
    override val template: QCircleTemplate by Delegates.lazy { QCircleTemplate(this, TemplateType.CIRCLE_EMPTY) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        template.addElement(QCircleBackButton(this))
        val title = QCircleTitle(this, getString(R.string.clock_module_name), Color.WHITE, getResources().getColor(R.color.clock_theme_color))
        title.setTextSize(17f)
        template.addElement(title)
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
        return intent<PhoneActivity>().putExtra("com.lge.app.floating.launchAsFloating", true)
    }
}