package com.yoavst.quickapps.clock

import java.util.Timer
import java.util.TimerTask

/**
 * Created by Yoav.
 */
public object StopwatchManager {
    public abstract class Stopwatch : TimerTask() {
        public synchronized var isRunning: Boolean = true

        override fun run() {
            if (isRunning) runCode()
        }

        protected abstract fun runCode()
    }

    private var runnable: (() -> Unit)? = null
    private var stopwatch: Stopwatch? = null
    private var milliseconds: Long = 0
    private var period: Long = 0

    public fun startTimer(period: Long, callback: () -> Unit) {
        runnable = callback
        StopwatchManager.period = period
        initStopwatch()
        startTimer()
    }

    private fun startTimer() {
        Timer().schedule(stopwatch, 10, period)
    }

    private fun initStopwatch() {
        stopwatch = object : Stopwatch() {
            override fun runCode() {
                milliseconds += period
                runnable?.invoke()
            }
        }
    }

    /**
     * Make the timer to run on background, with no callback
     */
    public fun runOnBackground() {
        runnable = null
        // If the stopwatch is paused, it will just save its data, and will not run.
        if (!(stopwatch == null || stopwatch!!.isRunning)) {
            stopwatch!!.cancel()
            stopwatch = null
        }
    }

    public fun runOnUi(callback: () -> Unit) {
        runnable = callback
        if (hasOldData() && stopwatch == null) {
            initStopwatch()
            stopwatch!!.isRunning = false
            startTimer()
            runnable?.invoke()
        }
    }

    public fun stopTimer() {
        runnable = null
        stopwatch?.cancel()
        stopwatch = null
        milliseconds = 0
        period = 0
    }

    public fun pauseTimer() {
        stopwatch?.isRunning = false
    }

    public fun resumeTimer() {
        stopwatch?.isRunning = true
    }

    public fun getMillis(): Long {
        return milliseconds
    }

    public fun isRunning(): Boolean {
        return stopwatch != null && stopwatch!!.isRunning
    }

    public fun hasOldData(): Boolean {
        return milliseconds != 0L && period != 0L
    }
}
