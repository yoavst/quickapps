package com.lge.qcircle.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.lge.qcircle.template.QCircleTemplate;

/**
 * Created by sujin.cho on 2015-03-11.
 */
public class QCircleFeature {

    private static Intent numberBadge = null;
    private static final String ACTION_UPDATE_NOTIFICATION = "com.lge.launcher.intent.action.BADGE_COUNT_UPDATE";
    private static final int G3_DIAMETER = 1046;




    public static void activateNumberBadge(Context context, int count)
    {
        if(numberBadge == null)
            numberBadge = new Intent(ACTION_UPDATE_NOTIFICATION);
        numberBadge.putExtra("badge_count_package_name", context.getPackageName());
        numberBadge.putExtra("badge_count_class_name",  context.getClass().getName());
        numberBadge.putExtra("badge_count", count);
        context.sendBroadcast(numberBadge);
    }

    public static void setNumberBadge(Context context, int count)
    {
        if(numberBadge == null)
        {
            activateNumberBadge(context,count);
            return;
        }
        numberBadge.putExtra("badge_count", count);
        context.sendBroadcast(numberBadge);
    }

    /**
     * Takes a pixel value implemented for the current model.
     * The argument value has to be a pixel values.
     * Returns a relative pixel value to support other Quick Circle models which have different densities and screen sizes.
     * @param value
     * @return
     */
    public static int getRelativePixelValue(int value)
    {
        return (int)(((double)QCircleTemplate.getDiameter()/G3_DIAMETER) * (double)(value));
    }

}
