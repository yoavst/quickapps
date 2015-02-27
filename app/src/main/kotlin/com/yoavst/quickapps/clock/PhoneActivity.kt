package com.yoavst.quickapps.clock

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.TextUtils
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageButton

import com.lge.app.floating.FloatableActivity
import com.lge.app.floating.FloatingWindow
import com.yoavst.quickapps.R

public class PhoneActivity : FloatableActivity() {

    var DEFAULT_STOPWATCH = "<big>00:00:00</big><small>.00</small>"
    var DEFAULT_STOPWATCH_NO_MILLIS = "<big>00:00:00</big>"
    var showMillis = true

    var mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (TextUtils.equals(ACTION_FLOATING_CLOSE, intent.getAction())) {
                finishFloatingMode()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.stopwatch_layout)
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
            if (fullscreenButton != null) {
                (fullscreenButton.getParent() as ViewGroup).removeView(fullscreenButton)
            }
        }
        getFragmentManager().beginTransaction().replace(R.id.content, PhoneClockFragment()).commit()
    }

    override fun onDetachedFromFloatingWindow(w: FloatingWindow, isReturningToFullScreen: Boolean): Boolean {
        if (StopwatchManager.isRunning()) {
            val alertDialog = AlertDialog.Builder(getApplicationContext()).setTitle(R.string.stopwatch_run_on_back).setMessage(R.string.stopwatch_run_on_back_message)
                    .setPositiveButton(R.string.yes) {(dialog, which) ->
                        StopwatchManager.runOnBackground()
                    }
                    .setNegativeButton(R.string.no) {(dialog, which) ->
                        StopwatchManager.stopTimer()
                    }.create()
            alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE)
            alertDialog.show()
        }
        return false
    }

    class object {
        public val ACTION_FLOATING_CLOSE: String = "floating_close"
        private val TIME_FORMATTING = "<big>{0}:{1}:{2}</big><small>.{3}</small>"
        private val TIME_FORMATTING_NO_MILLIS = "<big>{0}:{1}:{2}</big>"
    }
}
