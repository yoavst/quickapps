package com.mobsandgeeks.ake

import android.content.Context
import android.content.res.Resources
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.app.Fragment
import android.util.TypedValue


public fun Context.getColor(colorResId: Int): Int {
    return resources().getColor(colorResId)
}

public fun Context.getInt(intResId: Int): Int {
    return resources().getInteger(intResId)
}

public fun Context.getDimen(dimenResId: Int): Int {
    return resources().getDimensionPixelSize(dimenResId)
}

public fun Context.getStringArray(arrayResId: Int): Array<String> {
    return resources().getStringArray(arrayResId)
}

public fun Context.getIntArray(arrayResId: Int): IntArray {
    return resources().getIntArray(arrayResId)
}

public fun Context.getDrawableResource(drawableResId: Int): Drawable {
    return resources().getDrawable(drawableResId)
}

public fun Context.typefaceFromAssets(assetPathResId: Int): Typeface {
    return typefaceFromAssets(getString(assetPathResId))
}

public fun Context.typefaceFromAssets(assetPath: String): Typeface {
    return Typeface.createFromAsset(getAssets(), assetPath)
}

public fun Context.getPxFromDp(size: Int): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,size.toFloat(),getResources().getDisplayMetrics()).toInt()
}

public fun Fragment.getColor(colorResId: Int): Int {
    return resources().getColor(colorResId)
}

public fun Fragment.getInt(intResId: Int): Int {
    return resources().getInteger(intResId)
}

public fun Fragment.getDimen(dimenResId: Int): Int {
    return resources().getDimensionPixelSize(dimenResId)
}

public fun Fragment.getStringArray(arrayResId: Int): Array<String> {
    return resources().getStringArray(arrayResId)
}

public fun Fragment.getIntArray(arrayResId: Int): IntArray {
    return resources().getIntArray(arrayResId)
}

public fun Fragment.getDrawableResource(drawableResId: Int): Drawable {
    return resources().getDrawable(drawableResId)
}

public fun Fragment.typefaceFromAssets(assetPathResId: Int): Typeface {
    return typefaceFromAssets(getString(assetPathResId))
}

public fun Fragment.typefaceFromAssets(assetPath: String): Typeface {
    return Typeface.createFromAsset(getActivity().getAssets(), assetPath)
}

public fun Fragment.getPxFromDp(size: Int): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,size.toFloat(),getResources().getDisplayMetrics()).toInt()
}

/*
 * -----------------------------------------------------------------------------
 *  Private methods
 * -----------------------------------------------------------------------------
 */
private fun Context.resources(): Resources {
    return this.getResources()
}

private fun Fragment.resources(): Resources {
    return this.getResources()
}