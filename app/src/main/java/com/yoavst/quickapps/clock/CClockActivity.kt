package com.yoavst.quickapps.clock

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.RelativeLayout
import com.lge.qcircle.template.QCircleBackButton
import com.lge.qcircle.template.QCircleTitle
import com.yoavst.kotlin.toDp
import com.yoavst.kotlin.toPx
import com.yoavst.quickapps.R
import com.yoavst.quickapps.tools.QCircleActivity
import kotlinx.android.synthetic.clock_activity.pager
import kotlinx.android.synthetic.clock_activity.tabs


public class CClockActivity : QCircleActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        template.addElement(QCircleBackButton(this))
        val title = QCircleTitle(this, getString(R.string.clock_module_name), Color.WHITE, getResources().getColor(R.color.primary))
        title.setTextSize(18f)
        title.setTitleHeight(0.14f)
        template.addElement(title)
        setContentViewToMain(R.layout.clock_activity)
        setContentView(template.getView())
        getFragmentManager().beginTransaction().replace(R.id.pager, StopwatchFragment()).commit()
        pager.setAdapter(ClockAdapter(getFragmentManager(), getString(R.string.stopwatch), getString(R.string.timer)))
        tabs.setupWithViewPager(pager)
    }


    public override fun onPause() {
        super.onPause()
        StopwatchManager.runOnBackground()
        TimerManager.runOnBackground()
    }

    protected override fun getIntentToShow(): Intent? {
        return null //FIXME
        /*return if (!isLGRom(this)) null else {
            intent<PhoneActivity>().putExtra("com.lge.app.floating.launchAsFloating", true)
        }*/
    }
}