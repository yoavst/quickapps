package com.lge.qcircle.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

/**
 * The {@code QCircleFeature} class provides useful methods for Quick Circle applications.
 * Created by sujin.cho on 2015-03-11.
 */
public class QCircleFeature {

	//needs to remove intent
	private static final String ACTION_UPDATE_NOTIFICATION = "com.lge.launcher.intent.action.BADGE_COUNT_UPDATE";
	private static final int G3_DIAMETER = 1046;
	private static int mFullSize = 0; // circle diameter
	private final static String TAG = "QCircleFeature";
	/**
	 * It is possible to run Quick Circle app even if no 'com.lge.internal' package.
	 * It can be also used for testing (testing LG G3 Beat circle size on LG G3).
	 */
	private static Bundle mAlternativeValues;
	private static boolean mForceAlternativeValues = false;

	/**
	 * Activates a number badge with a count.
	 * The number badge will show up on the icon.
	 *
	 * @param activity current activity
	 * @param count    a number present in the number badge. The number indicates the number of events occurred.
	 */
	public static Intent activateNumberBadge(Activity activity, int count) {
		Intent numberBadge = new Intent(ACTION_UPDATE_NOTIFICATION);
		numberBadge.putExtra("badge_count_package_name", activity.getPackageName());
		numberBadge.putExtra("badge_count_class_name", activity.getClass().getName());
		numberBadge.putExtra("badge_count", count);
		return numberBadge;
	}

	/**
	 * Changes a count number of a number badge.
	 *
	 * @param activity       current activity.
	 * @param intentForBadge intent created for the number badge. if intentForBadge is null, create a new intent for the number badge.
	 * @param count          a new number for the number badge.
	 */
	public static void setNumberBadge(Activity activity, Intent intentForBadge, int count) {
		if (intentForBadge == null) {
			intentForBadge = activateNumberBadge(activity, count);
		}
		intentForBadge.putExtra("badge_count", count);
	}

	/**
	 * Removes a number badge.
	 *
	 * @param activity       current activity
	 * @param intentForBadge intent created for number badge.
	 */
	public static void removeNumberBadge(Activity activity, Intent intentForBadge) {
		if (intentForBadge == null) {
			Log.e(TAG, "Intent is null!!");
		}
		intentForBadge.putExtra("badge_count", 0);
	}

	/**
	 * Takes a pixel value implemented for the current model.
	 * The argument value has to be a pixel values.
	 * Returns a relative pixel value to support other Quick Circle models which have different densities and screen sizes.
	 *
	 * @param value a pixel value appropriate for the current device.
	 * @return
	 */
	public static int getRelativePixelValue(Context context, int value) {
		mFullSize = getTemplateDiameter(context);
		return (int) (((double) mFullSize / G3_DIAMETER) * (double) (value));
	}

	/**
	 * Checks the availability of  Quick Circle case.
	 * <p/>
	 * Checks whether a smart case is available and if it is, check the case type.
	 *
	 * @param context current application context
	 */
	public static boolean isQuickCircleAvailable(Context context) {
		boolean smartcaseEnabled = false;
		int smartcaseType = 0;
		if (context != null) {
			ContentResolver contentResolver = context.getContentResolver();
			if (contentResolver == null) {
				Log.e(TAG, "Content Resolver is null");
				return false;
			}
			//default is 1. (LG framework setting. When user gets a phone, the case is enable as default)
			smartcaseEnabled = Settings.Global.getInt(contentResolver, "quick_view_enable", 1) == 1 ? true : false;
			if (!smartcaseEnabled) {
				Log.i(TAG, "No smart case available");
				return false;
			}
			smartcaseType = Settings.Global.getInt(contentResolver, "cover_type", 0);
			if (smartcaseType != 3) {
				Log.i(TAG, "Case type is not Quick Circle");
				return false;
			}
			return true;
		} else {
			Log.e(TAG, "Context is null!!");
			return false;
		}
	}

	/**
	 * locates the circle on the correct position. The correct position depends on phone model.
	 * <p/>
	 *
	 * @author sujin.cho
	 */
	public static int getTemplateDiameter(Context context) {
		if (context != null) {
			final String name = "config_circle_diameter";
			if (mForceAlternativeValues) {
				return getAlternativeValue(name);
			}
			int id = context.getResources().getIdentifier(
					name, "dimen", "com.lge.internal");
			if (id == 0) {
				return getAlternativeValue(name);
			} else return context.getResources().getDimensionPixelSize(id);
		}
		return -1;
	}

	/**
	 * locates the Y position for the template. The correct position depends on phone model.
	 * <p/>
	 */
	public static int getYPosition(Context context) {
		if (context != null) {
			final String name = "config_circle_window_y_pos";
			if (mForceAlternativeValues) {
				return getAlternativeValue(name);
			}
			int id = context.getResources().getIdentifier(
					name, "dimen", "com.lge.internal");
			if (id == 0) {
				return getAlternativeValue(name);
			} else return context.getResources().getDimensionPixelSize(id);
		}
		return -1;
	}

	/**
	 * Return the height of the window. The correct position depends on phone model.
	 * <p/>
	 */
	public static int getWindowHeight(Context context) {
		if (context != null) {
			final String name = "config_circle_window_height";
			if (mForceAlternativeValues) {
				return getAlternativeValue(name);
			}
			int id = context.getResources().getIdentifier(
					name, "dimen", "com.lge.internal");
			if (id == 0) {
				return getAlternativeValue(name);
			} else return context.getResources().getDimensionPixelSize(id);
		}
		return -1;
	}

	public static int getAlternativeValue(String name) {
		if (mAlternativeValues == null) return -1;
		else return mAlternativeValues.getInt(name, -1);
	}

	/**
	 * Force alternative values
	 */
	public static void forceAlternativeValues() {
		mForceAlternativeValues = true;
	}

	/**
	 * Stop forcing alternative values
	 */
	public static void StopForceAlternativeValues() {
		mForceAlternativeValues = false;
	}

	/**
	 * Set the alternative values.<br>
	 * Possible values:<br>
	 * 1. config_circle_diameter as Int. see {@link #getTemplateDiameter}<br>
	 * 2. config_circle_window_y_pos as Int. see {@link #getYPosition}<br>
	 * 3. config_circle_window_height as Int. see {@link #getWindowHeight}<br>
	 *
	 * @param alternativeValues Alternative values
	 */
	public static void setAlternativeValues(Bundle alternativeValues) {
		mAlternativeValues = alternativeValues;
	}

}
