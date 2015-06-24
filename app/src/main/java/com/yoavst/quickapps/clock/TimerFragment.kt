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
import com.yoavst.kotlin.*
import com.yoavst.quickapps.R
import com.yoavst.quickapps.tools.stopwatchShowMillis
import kotlinx.android.synthetic.clock_timer_fragment.*
import java.text.MessageFormat
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

public class TimerFragment : Fragment() {
    var secondNumber = 0
    var minutesNumber = 0
    var hoursNumber = 0
    val showMillis by Delegates.lazy { getActivity().stopwatchShowMillis }
    var handler: Handler = Handler()
    val RESUME by stringResource(R.string.resume)
    val PAUSE by stringResource(R.string.pause)
    var callback: () -> Unit = {
        handler.post {
            var millis = TimerManager.getMillis()
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
                val ringtone = RingtoneManager.getRingtone(getActivity(), alarmUri)
                ringtone.play()
                //FIXME show finish view
                Handler().postDelayed(3000) { ringtone.stop()}

            }

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.clock_timer_fragment, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateHours()
        updateMinutes()
        updateSeconds()
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
            if (TimerManager.isRunning())
                TimerManager.PauseTimer()
            else
                TimerManager.ResumeTimer()
            setLookForPauseOrResume()
        }
        stop.setOnClickListener {
            TimerManager.stopTimer()
            handler.postDelayed(100) {
                runningLayout.hide()
                secondNumber = 0
                minutesNumber = 0
                hoursNumber = 0
                updateHours()
                updateMinutes()
                updateSeconds()
                settingLayout.show()
                pause.setText(PAUSE)
                setText(Html.fromHtml(DEFAULT_TIMER))
            }
        }
        if (time.getText().toString().isEmpty()) setText(Html.fromHtml(if (showMillis) DEFAULT_TIMER else DEFAULT_TIMER_NO_MILLIS))
        start.setOnClickListener { startTimer() }
        //endregion
        if (TimerManager.hasOldData()) {
            Handler().postDelayed({
                callback()
                TimerManager.runOnUi(callback)
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
            runningLayout.show()
            TimerManager.startTimer(TimeUnit.SECONDS.toMillis(seconds.toLong()), 10, callback)
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
        if (TimerManager.isRunning())
            pause.setText(PAUSE)
        else
            pause.setText(RESUME)
    }

    companion object {
        private val TIME_FORMATTING = "<big>{0}:{1}:{2}</big><small>.{3}</small>"
        private val TIME_FORMATTING_NO_MILLIS = "<big>{0}:{1}:{2}</big>"
        var DEFAULT_TIMER = "<big>00:00:00</big><small>.00</small>"
        var DEFAULT_TIMER_NO_MILLIS = "<big>00:00:00</big>"

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