package com.yoavst.quickapps.tools

import android.app.Activity
import android.app.Fragment
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Build
import android.view.Gravity
import android.widget.Toast
import com.yoavst.kotlin.lollipopOrNewer

public fun getPressedColorRippleDrawable(normalColor: Int, pressedColor: Int): RippleDrawable {
    return RippleDrawable(getPressedColorSelector(pressedColor), ColorDrawable(normalColor), null)
}

public fun getPressedColorSelector(pressedColor: Int): ColorStateList {
    return ColorStateList(arrayOf(intArrayOf()), intArrayOf(pressedColor))
}


public fun getRegularPressedDrawable(normalColor: Int, pressedColor: Int): StateListDrawable {
    val states = StateListDrawable()
    states.addState(intArrayOf(android.R.attr.state_pressed), ColorDrawable(pressedColor))
    states.addState(intArrayOf(android.R.attr.state_focused), ColorDrawable(pressedColor))
    states.addState(intArrayOf(), ColorDrawable(normalColor))
    return states
}

public fun getBackgroundDrawable(normalColor: Int, pressedColor: Int): Drawable {
    if (normalColor.lollipopOrNewer()) return getPressedColorRippleDrawable(normalColor, pressedColor)
    else return getRegularPressedDrawable(normalColor, pressedColor)
}

public fun isLGRom(context: Context): Boolean {
    val id = context.getResources().getIdentifier("config_circle_window_y_pos", "dimen", "com.lge.internal")
    return id != 0
}

public fun Activity.qCircleToast(text: Int): Unit = qCircleToast(getString(text))
public fun Fragment.qCircleToast(text: Int): Unit = getActivity().qCircleToast(text)
public fun Fragment.qCircleToast(text: String): Unit = getActivity().qCircleToast(text)

public fun Activity.qCircleToast(text: String) {
    val toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
    toast.setText(text)
    toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 0)
    toast.show()
}

public fun Intent.createExplicit(context: Context): Intent {
    if (Build.VERSION.SDK_INT >= 21) {
        val pm = context.getPackageManager()
        val resolveInfo = pm.queryIntentServices(this, 0)
        if (resolveInfo == null || resolveInfo.size() != 1) {
            return this
        }
        val serviceInfo = resolveInfo.get(0)
        val packageName = serviceInfo.serviceInfo.packageName
        val className = serviceInfo.serviceInfo.name
        val component = ComponentName(packageName, className)
        val explicitIntent = Intent(this)
        explicitIntent.setComponent(component)
        return explicitIntent
    } else
        return this
}
