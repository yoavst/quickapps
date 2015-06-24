package com.yoavst.quickapps.clock

import android.app.Fragment
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.yoavst.kotlin.*
import com.yoavst.quickapps.R
import com.yoavst.quickapps.tools.stopwatchShowMillis
import kotlinx.android.synthetic.clock_stopwatch_fragment.*
import java.text.MessageFormat
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import kotlin.properties.Delegates

/**
 * Created by Yoav.
 */
public open class StopwatchFragment : Fragment() {
    var handler: Handler = Handler()
    val RESUME by stringResource(R.string.resume)
    val PAUSE by stringResource(R.string.pause)
    val showMillis by Delegates.lazy { getActivity().stopwatchShowMillis }
    var callback: () -> Unit = {
        handler.post {
            val millis = StopwatchManager.getMillis()
            val num = (millis % 1000 / 10).toInt()
            if (showMillis) {
                setText(Html.fromHtml(MessageFormat.format(TIME_FORMATTING,
                        getFromMilli(millis, TimeUnit.HOURS).format(),
                        getFromMilli(millis, TimeUnit.MINUTES).format(),
                        getFromMilli(millis, TimeUnit.SECONDS).format(),
                        num.format())))
            } else {
                setText(Html.fromHtml(MessageFormat.format(TIME_FORMATTING_NO_MILLIS, getFromMilli(millis, TimeUnit.HOURS).format(),
                        getFromMilli(millis, TimeUnit.MINUTES).format(),
                        getFromMilli(millis, TimeUnit.SECONDS).format())))
            }
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.clock_stopwatch_fragment, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
            start.setOnClickListener {
                setLookRunning()
                StopwatchManager.startTimer(10, callback)
            }
            pause.setOnClickListener {
                if (StopwatchManager.isRunning())
                    StopwatchManager.PauseTimer()
                else
                    StopwatchManager.ResumeTimer()
                setLookForPauseOrResume()
            }
            stop.setOnClickListener {
                StopwatchManager.stopTimer()
                handler.postDelayed(100) {
                    running.hide()
                    start.show()
                    setText(Html.fromHtml(DEFAULT_STOPWATCH))
                }
            }
            if (time.getText().toString().isEmpty()) setText(Html.fromHtml(if (showMillis) DEFAULT_STOPWATCH else DEFAULT_STOPWATCH_NO_MILLIS))
            handler = Handler()
            if (StopwatchManager.hasOldData()) {
                Handler().postDelayed({
                    callback()
                    StopwatchManager.runOnUi(callback)
                    setLookRunning()
                    setLookForPauseOrResume()
                }, 100)
            }
    }

    fun setLookRunning() {
        start.hide()
        running.show()
        pause.setText(PAUSE)
    }

    fun setLookForPauseOrResume() {
        if (StopwatchManager.isRunning())
            pause.setText(PAUSE)
        else
            pause.setText(RESUME)
    }

    fun setText(text: CharSequence) {
        time.setText(text)
    }

    companion object {
        private val TIME_FORMATTING = "<big>{0}:{1}:{2}</big><small>.{3}</small>"
        private val TIME_FORMATTING_NO_MILLIS = "<big>{0}:{1}:{2}</big>"
        var DEFAULT_STOPWATCH = "<big>00:00:00</big><small>.00</small>"
        var DEFAULT_STOPWATCH_NO_MILLIS = "<big>00:00:00</big>"

        public fun getFromMilli(millis: Long, timeUnit: TimeUnit): Int {
            when (timeUnit) {
                TimeUnit.SECONDS -> // Number of seconds % 60
                    return (millis / 1000).toInt() % 60
                TimeUnit.MINUTES -> // Number of minutes % 60
                    return (millis / 60000).toInt() % 60
                TimeUnit.HOURS -> // Number of hours (can be more then 24)
                    return (millis / 1440000).toInt()
            }
            return 0
        }

        public fun Int.format(): String {
            return if (this < 10) "0" + this else Integer.toString(this)
        }
    }
}
