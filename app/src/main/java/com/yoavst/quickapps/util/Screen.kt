package com.yoavst.qslidewidget

import android.content.Context
import android.graphics.Point
import android.app.Activity
import android.util.TypedValue
import android.util.DisplayMetrics

/**
 * Created by Yoav.
 */
public object Screen {
    fun width(activity: Activity): Int {
        val size = Point()
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return size.x
    }

    fun height(activity: Activity): Int {
        val size = Point()
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return size.x
    }

    fun dpToPx(context: Context, px: Int): Int  = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, px.toFloat(), context.getResources().getDisplayMetrics()).toInt()
    fun pxToDp(activity: Activity, px: Int): Int  {
        val metrics = DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return (px / metrics.density).toInt();
    }

}