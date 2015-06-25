package com.yoavst.quickapps.clock

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.text.TextUtils
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageButton
import com.lge.app.floating.FloatableActivity
import com.lge.app.floating.FloatingWindow
import com.yoavst.kotlin.hide
import com.yoavst.kotlin.show
import com.yoavst.quickapps.R
import kotlinx.android.synthetic.clock_qslide_activity.layout1
import kotlinx.android.synthetic.clock_qslide_activity.layout2
import kotlinx.android.synthetic.clock_qslide_activity.tabs
import kotlin.properties.Delegates


public class PhoneActivity : FloatableActivity() {
    var firstTime = true
    val showStopwatch by Delegates.lazy { getIntent().getBooleanExtra(EXTRA_SHOW_STOPWATCH, true) }
    var mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
                finishFloatingMode()
                finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.clock_qslide_activity)
        // It is critically important that the activity should NOT be finished
        // if it uses fragment. Fragments require the hosting activity not to
        // be finished. Omitting the following statement or setting it to false
        // will prevent Fragments from being shown while in the floating mode.
        setDontFinishOnFloatingMode(true)
        if (isStartedAsFloating()) {
            val filter = IntentFilter()
            filter.addAction(ACTION_FLOATING_CLOSE)
            this.registerReceiver(mReceiver, filter)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        this.unregisterReceiver(mReceiver)
    }

    override fun onAttachedToFloatingWindow(w: FloatingWindow?) {
        super.onAttachedToFloatingWindow(w)
        if (w != null) {
            val layoutParams = w.getLayoutParams()
            layoutParams.resizeOption = FloatingWindow.ResizeOption.DISABLED
            layoutParams.width = layoutParams.width + 100
            w.updateLayoutParams(layoutParams)
            val fullscreenButton = w.findViewWithTag(FloatingWindow.Tag.FULLSCREEN_BUTTON) as? ImageButton
            (fullscreenButton?.getParent() as? ViewGroup)?.removeView(fullscreenButton)
            if (tabs.getTabCount() == 0) {
                tabs.addTab(tabs.newTab().setText(R.string.stopwatch))
                tabs.addTab(tabs.newTab().setText(R.string.timer))
            }
            tabs.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabReselected(tab: TabLayout.Tab) {

                }

                override fun onTabUnselected(tab: TabLayout.Tab) {

                }

                override fun onTabSelected(tab: TabLayout.Tab) {
                    if (tab.getPosition() == 0) {
                        layout1.show()
                        layout2.hide()
                    } else {
                        layout2.show()
                        layout1.hide()
                    }
                }
            })
            if (firstTime) {
                // Note that commit() cannot be used while running in the floating mode.
                // This is because the commit() method requires the activity to be in
                // the RESUMED state. However, the activity stays at the STOPPED state
                // while the activity is in the floating mode. In order to avoid the
                // default behavior, commitAllowingStateLoss() should be used instead
                // of commit(). commitAllowingStateLoss() allows the activity to be
                // in states other than RESUMED.
                getFragmentManager().beginTransaction().replace(R.id.layout1, StopwatchFragment()).commitAllowingStateLoss()
                getFragmentManager().beginTransaction().replace(R.id.layout2, TimerFragment()).commitAllowingStateLoss()
            } else firstTime = false

            if (showStopwatch) {
                layout1.show()
                layout2.hide()
            } else {
                layout2.show()
                layout1.hide()
                tabs.getTabAt(1).select()
            }
        }
    }


    override fun onDetachedFromFloatingWindow(w: FloatingWindow, isReturningToFullScreen: Boolean): Boolean {
        if (StopwatchManager.isRunning()) {
            val alertDialog = AlertDialog.Builder(getApplicationContext()).setTitle(R.string.stopwatch_run_on_back).setMessage(R.string.stopwatch_run_on_back_message)
                    .setPositiveButton(R.string.yes) { dialog, which ->
                        StopwatchManager.runOnBackground()
                        Timer.runOnBackground(this)
                    }
                    .setNegativeButton(R.string.no) { dialog, which ->
                        StopwatchManager.stopTimer()
                        Timer.stop()
                    }.create()
            alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE)
            alertDialog.show()
        } else {
            StopwatchManager.stopTimer()
            Timer.stop()
        }
        return false
    }

    companion object {
        public val ACTION_FLOATING_CLOSE: String = "floating_close"
        public val EXTRA_SHOW_STOPWATCH: String = "extra_stopwatch_showing"
    }
}