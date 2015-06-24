package com.yoavst.quickapps.clock

import java.util.*

/**
 * Created by yoavst.
 */
public object TimerManager {
    public abstract class TimerExtended : TimerTask() {
        private  var isRunning = true

        synchronized public fun isRunning(running: Boolean) {
            this.isRunning = running
        }

        synchronized public fun isRunning(): Boolean {
            return isRunning
        }

        override fun run() {
            if (isRunning) runCode()
        }

        protected abstract fun runCode()
    }

    private var runnable: (() -> Unit)? = null
    private var stopwatch: TimerExtended? = null
    private var millisecondsLeft: Long = 0
    private var period: Long = 0

    public fun startTimer(totalMillis: Long, period: Long, callback: () -> Unit) {
        runnable = callback
        millisecondsLeft = totalMillis
        TimerManager.period = period
        initTimer()
        startTimer()
    }

    private fun startTimer() {
        java.util.Timer().schedule(stopwatch, 10, period)
    }

    private fun initTimer() {
        stopwatch = object : TimerExtended() {
            override fun runCode() {
                millisecondsLeft -= period
                runnable?.invoke()
                if (millisecondsLeft < 0) {
                    stopTimer()
                    //FIXME if (runnable == null)
                }
            }
        }
    }

    /**
     * Make the timer to run on background, with no callback
     */
    public fun runOnBackground() {
        runnable = null
        // If the stopwatch is paused, it will just save its data, and will not run.
        if (!(stopwatch == null || stopwatch!!.isRunning())) {
            stopwatch!!.cancel()
            stopwatch = null
        }
    }

    public fun runOnUi(callback: () -> Unit) {
        runnable = callback
        if (hasOldData() && stopwatch == null) {
            initTimer()
            stopwatch!!.isRunning(false)
            startTimer()
            runnable?.invoke()
        }
    }

    public fun stopTimer() {
        runnable = null
        stopwatch?.cancel()
        stopwatch = null
        millisecondsLeft = 0
        period = 0
    }

    public fun PauseTimer() {
        stopwatch?.isRunning(false)
    }

    public fun ResumeTimer() {
        stopwatch?.isRunning(true)
    }

    public fun getMillis(): Long {
        return millisecondsLeft
    }

    public fun isRunning(): Boolean {
        return stopwatch != null && stopwatch!!.isRunning()
    }

    public fun hasOldData(): Boolean {
        return millisecondsLeft != 0L && period != 0L
    }
}