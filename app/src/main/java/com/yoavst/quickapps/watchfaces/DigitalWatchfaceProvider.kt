package com.yoavst.quickapps.watchfaces

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import com.etiennelawlor.trestle.library.Span
import com.etiennelawlor.trestle.library.Trestle
import com.yoavst.kotlin.alarmManager
import com.yoavst.kotlin.e
import com.yoavst.quickapps.R
import com.yoavst.quickapps.tools.*
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar
import java.util.Date


public class DigitalWatchfaceProvider : AppWidgetProvider() {
    var pendingIntent: PendingIntent? = null
    var componentName: ComponentName? = null
    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        context.alarmManager().cancel(pendingIntent)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.getAction() == ACTION_UPDATE_WATCH) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(getComponentName(context))
            onUpdate(context, appWidgetManager, appWidgetIds)
        }

    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (widgetId in appWidgetIds) {
            val remoteViews = RemoteViews(context.getPackageName(), R.layout.watchface_layout)
            setBackground(context, remoteViews)
            // Date
            val dateFormat = context.digitalWatchfaceDateFormat
            remoteViews.setCharSequence(R.id.date, "setFormat12Hour", dateFormat)
            remoteViews.setCharSequence(R.id.date, "setFormat24Hour", dateFormat)
            remoteViews.setTextColor(R.id.date, context.digitalWatchfaceDateColor)
            // Hour & Minutes
            remoteViews.setTextViewText(R.id.hour, getHourText(context))
            if (context.digitalWatchfaceAmPm) {
                remoteViews.setViewVisibility(R.id.amPm, View.VISIBLE)
                remoteViews.setTextColor(R.id.amPm, context.digitalWatchfaceAmPmColor)
                remoteViews.setTextViewText(R.id.amPm, SimpleDateFormat("a").format(Date()))
            } else {
                remoteViews.setViewVisibility(R.id.amPm, View.GONE)
            }
            appWidgetManager.updateAppWidget(widgetId, remoteViews)
        }
        setAlarm(context)
    }

    public fun getHourText(context: Context): CharSequence {
        val spans = ArrayList<Span>(2)
        val calendar = Calendar.getInstance()
        if (context.digitalWatchfaceAmPm)
            spans.add(Span.Builder(toString(calendar.get(Calendar.HOUR))).foregroundColor(context.digitalWatchfaceHoursColor).build())
        else
            spans.add(Span.Builder(toString(calendar.get(Calendar.HOUR_OF_DAY))).foregroundColor(context.digitalWatchfaceHoursColor).build())
        spans.add(Span.Builder(toString(calendar.get(Calendar.MINUTE))).foregroundColor(context.digitalWatchfaceMinutesColor).build())
        return Trestle.getFormattedText(spans)
    }

    public fun toString(number: Int): String {
        if (number >= 10) {
            return Integer.toString(number)
        } else
            return "0" + Integer.toString(number)
    }

    public fun setBackground(context: Context, remoteViews: RemoteViews) {
        remoteViews.setInt(R.id.layout, "setColorFilter", context.digitalWatchfaceSecondaryBackgroundColor)
        remoteViews.setInt(R.id.circle, "setColorFilter", context.digitalWatchfaceMainBackgroundColor)
    }

    public fun setAlarm(context: Context) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        calendar.add(Calendar.MINUTE, 1)
        context.alarmManager().setExact(AlarmManager.RTC, calendar.getTimeInMillis(), getPendingIntent(context))
    }


    public fun getPendingIntent(context: Context): PendingIntent {
        if (pendingIntent == null) {
            pendingIntent = PendingIntent.getBroadcast(context, 0, Intent(context.getApplicationContext(), javaClass).setAction(ACTION_UPDATE_WATCH), 0)
        }
        return pendingIntent!!
    }

    private fun getComponentName(context: Context): ComponentName {
        if (componentName == null) {
            componentName = ComponentName(context, javaClass)
        }
        return componentName!!
    }

    companion object {
        public val ACTION_UPDATE_WATCH: String = "ACTION_UPDATE_WATCH"
    }
}