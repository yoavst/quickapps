package com.yoavst.quickapps.torch

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

import com.yoavst.quickapps.R

/**
 * Created by Yoav.
 */
public class WidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        val myWidget = ComponentName(context, javaClass<WidgetProvider>())
        val updateViews = RemoteViews(context.getPackageName(), R.layout.torch_widget_1x1)
        val intent = Intent("com.yoavst.toggletorch")
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
        // Create an Intent to launch Browser
        updateViews.setOnClickPendingIntent(R.id.widget, pendingIntent)
        appWidgetManager.updateAppWidget(myWidget, updateViews)
    }
}
