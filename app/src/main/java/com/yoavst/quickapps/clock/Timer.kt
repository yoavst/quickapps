package com.yoavst.quickapps.clock

import android.content.Context
import android.content.Intent
import android.os.Handler
import java.util.Timer

/**
 * Created by yoavst.
 */
public object Timer {
    public var timeLeft: Long = 0
        private set
    var callback: (() -> Unit)? = null
    var stopwatch: StopwatchManager.Stopwatch? = null
    public var handler: Handler? = null

    public fun stop() {
        stopwatch?.isRunning = false
        stopwatch?.cancel()
        handler!!.postDelayed({ timeLeft = 0 }, 21)
        stopwatch = null
        callback = null
    }

    public fun start(time: Long, callback: () -> Unit) {
        this.timeLeft = time
        this.callback = callback
        initStopwatch()
        startTimer()
    }

    private fun initStopwatch() {
        stopwatch = object : StopwatchManager.Stopwatch() {
            override fun runCode() {
                timeLeft -= 10
                this@Timer.callback?.invoke()
                if (timeLeft == 0L) stop()
            }
        }
    }

    private fun startService(context: Context) {
        Exception().printStackTrace()
        context.startService(Intent().setClass(context, javaClass<TimerIntentService>()));
    }

    private fun startTimer() {
        Timer().schedule(stopwatch, 10, 10)
    }

    public fun runOnBackground(context: Context) {
        callback = null
        // If the stopwatch is paused, it will just save its data, and will not run.
        if (!(stopwatch == null || stopwatch!!.isRunning)) {
            stopwatch!!.cancel()
            stopwatch = null
        } else if (timeLeft != 0L) {
            startService(context)
        }
    }

    public fun runOnUi(context: Context, callback: () -> Unit) {
        this.callback = callback
        if (hasOldData()) {
            if (stopwatch == null) {
                initStopwatch()
                stopwatch!!.isRunning = false
                startTimer()
                callback.invoke()
            } else {
                context.stopService(Intent().setClass(context, javaClass<TimerIntentService>()));
            }
        }
    }

    public fun runOnServer(callback: () -> Unit) {
        if (timeLeft == 0L)
            callback()
        else this.callback = callback

    }

    public fun hasOldData(): Boolean {
        return timeLeft != 0L
    }

    public fun pauseTimer() {
        stopwatch?.isRunning = false
    }

    public fun resumeTimer() {
        stopwatch?.isRunning = true
    }

    public fun isRunning(): Boolean = stopwatch?.isRunning ?: false


}