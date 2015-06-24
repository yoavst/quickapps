package com.yoavst.quickapps.clock

import android.app.Fragment
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lge.app.floating.FloatableActivity
import com.yoavst.kotlin.*
import com.yoavst.quickapps.R
import com.yoavst.quickapps.tools.stopwatchShowMillis
import kotlinx.android.synthetic.clock_timer_fragment.*
import java.text.MessageFormat
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

public class TimerFragment : Fragment() {
    var container: ViewGroup? = null
    var layout: View by Delegates.notNull()
    var secondNumber = 0
    var minutesNumber = 0
    var hoursNumber = 0
    val showMillis by Delegates.lazy { getActivity().stopwatchShowMillis }
    var handler: Handler = Handler()
    val RESUME by stringResource(R.string.resume)
    val PAUSE by stringResource(R.string.pause)
    var ringtone: Ringtone? = null
    var callback: () -> Unit = {
        handler.post {
            var millis = Timer.timeLeft
            if (millis < 0) millis = 0
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

            if (millis == 0L) {
                var alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                if (alarmUri == null) {
                    alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
                }
                if (ringtone == null) {
                    ringtone = RingtoneManager.getRingtone(getActivity(), alarmUri)
                    ringtone!!.play()
                }
                runningLayout.hide()
                doneLayout.show()
            }

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        this.container = container
        this.layout = inflater.inflate(R.layout.clock_timer_fragment, container, false)
        return layout
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateHours()
        updateMinutes()
        updateSeconds()
        Timer.handler = Handler()
        //region callback timer
        subSecond.setOnClickListener {
            if (secondNumber == 0) secondNumber = 59
            else secondNumber--
            updateSeconds()
        }
        addSecond.setOnClickListener {
            if (secondNumber == 59) secondNumber = 0
            else secondNumber++
            updateSeconds()
        }

        subSecond.setOnLongClickListener {
            secondNumber -= 15
            if (secondNumber < 0) secondNumber += 60
            updateSeconds()
            true
        }
        addSecond.setOnLongClickListener {
            secondNumber += 15
            if (secondNumber >= 60) secondNumber -= 60
            updateSeconds()
            true
        }

        subMinute.setOnClickListener {
            if (minutesNumber == 0) minutesNumber = 59
            else minutesNumber--
            updateMinutes()
        }
        addMinute.setOnClickListener {
            if (minutesNumber == 59) minutesNumber = 0
            else minutesNumber++
            updateMinutes()
        }

        subMinute.setOnLongClickListener {
            minutesNumber -= 15
            if (minutesNumber < 0) minutesNumber += 60
            updateMinutes()
            true
        }
        addMinute.setOnLongClickListener {
            minutesNumber += 15
            if (minutesNumber >= 60) minutesNumber -= 60
            updateMinutes()
            true
        }

        subHour.setOnClickListener {
            if (hoursNumber == 0) hoursNumber = 23
            else hoursNumber--
            updateHours()
        }
        addHour.setOnClickListener {
            if (hoursNumber == 23) hoursNumber = 0
            else hoursNumber++
            updateHours()
        }

        subHour.setOnLongClickListener {
            hoursNumber = 0
            updateHours()
            true
        }
        //endregion
        //region callback running
        pause.setOnClickListener {
            if (Timer.isRunning())
                Timer.pauseTimer()
            else
                Timer.resumeTimer()
            setLookForPauseOrResume()
        }
        stop.setOnClickListener {
            Timer.stop()
            handler.postDelayed(100) {
                runningLayout.hide()
                settingLayout.show()
                pause.setText(PAUSE)
                setText(Html.fromHtml(DEFAULT_TIMER))
            }
        }
        if (time.getText().toString().isEmpty()) setText(Html.fromHtml(if (showMillis) DEFAULT_TIMER else DEFAULT_TIMER_NO_MILLIS))
        start.setOnClickListener { startTimer() }
        //endregion
        done.setOnClickListener {
            ringtone?.stop()
            ringtone = null
            doneLayout.hide()
            settingLayout.show()
        }
        if (Timer.hasOldData()) {
            Handler().postDelayed({
                callback()
                Timer.runOnUi(getActivity(), callback)
                settingLayout.hide()
                runningLayout.show()
                setLookForPauseOrResume()
            }, 100)
        }
    }

    fun startTimer() {
        val seconds = secondNumber + (minutesNumber * 60) + (hoursNumber * 3600)
        if (seconds != 0) {
            settingLayout.hide()
            secondNumber = 0
            minutesNumber = 0
            hoursNumber = 0
            updateHours()
            updateMinutes()
            updateSeconds()
            runningLayout.show()
            Timer.start(TimeUnit.SECONDS.toMillis(seconds.toLong()), callback)
        }
    }

    fun updateSeconds() {
        seconds.setText(secondNumber.format())
    }

    fun updateMinutes() {
        minutes.setText(minutesNumber.format() + ":")
    }

    fun updateHours() {
        hours.setText(hoursNumber.format() + ":")
    }

    fun setText(text: CharSequence) {
        time.setText(text)
    }

    fun setLookForPauseOrResume() {
        if (Timer.isRunning())
            pause.setText(PAUSE)
        else
            pause.setText(RESUME)
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
        ringtone?.stop()
    }

    companion object {
        private val TIME_FORMATTING = "<big>{0}:{1}:{2}</big><small>.{3}</small>"
        private val TIME_FORMATTING_NO_MILLIS = "<big>{0}:{1}:{2}</big>"
        var DEFAULT_TIMER = "<big>00:00:00</big><small>.00</small>"
        var DEFAULT_TIMER_NO_MILLIS = "<big>00:00:00</big>"

        public fun getFromMilli(millis: Long, timeUnit: TimeUnit): Int {
            when (timeUnit) {
                TimeUnit.SECONDS ->
                    return (millis / 1000).toInt() % 60
                TimeUnit.MINUTES ->
                    return (millis / 60000).toInt() % 60
                TimeUnit.HOURS ->
                    return (millis / 3600000).toInt()
            }
            return 0
        }

        public fun Int.format(): String {
            return if (this < 10) "0" + this else Integer.toString(this)
        }
    }
}