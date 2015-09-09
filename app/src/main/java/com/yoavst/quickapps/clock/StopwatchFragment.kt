package com.yoavst.quickapps.clock

import android.app.Fragment
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.lge.app.floating.FloatableActivity
import com.yoavst.kotlin.*
import com.yoavst.quickapps.R
import com.yoavst.quickapps.tools.stopwatchShowMillis
import java.text.MessageFormat
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

/**
 * Created by Yoav.
 */
public open class StopwatchFragment : Fragment() {
    var container: ViewGroup? = null
    var layout: View by Delegates.notNull()
    var handler: Handler = Handler()
    val RESUME by stringResource(R.string.resume)
    val PAUSE by stringResource(R.string.pause)
    val showMillis by Delegates.lazy { getActivity().stopwatchShowMillis }
    val time: TextView by Delegates.lazy { layout.findViewById(R.id.time) as TextView }
    val start: Button by Delegates.lazy { layout.findViewById(R.id.start) as Button }
    val pause: Button by Delegates.lazy { layout.findViewById(R.id.pause) as Button }
    val stop: Button by Delegates.lazy { layout.findViewById(R.id.stop) as Button }
    val running: LinearLayout by Delegates.lazy { layout.findViewById(R.id.running) as LinearLayout }
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
        // For QSlide
        RESUME.length()
        PAUSE.length()

        this.container = container
        this.layout = inflater.inflate(R.layout.clock_stopwatch_fragment, container, false)
        return layout
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        start.setOnClickListener {
            setLookRunning()
            StopwatchManager.startTimer(10, callback)
        }
        pause.setOnClickListener {
            if (StopwatchManager.isRunning())
                StopwatchManager.pauseTimer()
            else
                StopwatchManager.resumeTimer()
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
        update()
    }

    fun update() {
        if (StopwatchManager.hasOldData()) {
            Handler().postDelayed({
                callback()
                StopwatchManager.runOnUi(callback)
                setLookRunning()
                setLookForPauseOrResume()
            }, 100)
        }
    }

    override fun onResume() {
        super.onResume()
        update()
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

    override fun onDestroy() {
        super.onDestroy()
        try {
            Class.forName("com.lge.app.floating.FloatableActivity")
            if (getActivity() is FloatableActivity) {
                container?.addView(layout)
                super.onViewCreated(getView(), null)
            }
        } catch(e: ClassNotFoundException) {
            //my class isn't there!
        }
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
                    return (millis / 3600000).toInt()
            }
            return 0
        }

        public fun Int.format(): String {
            return if (this < 10) "0" + this else Integer.toString(this)
        }
    }
}
