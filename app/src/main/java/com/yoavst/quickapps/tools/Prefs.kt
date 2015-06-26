package com.yoavst.quickapps.tools

import android.content.Context
import android.content.SharedPreferences
import com.yoavst.kotlin.putBoolean
import com.yoavst.kotlin.putInt
import com.yoavst.kotlin.putString
import kotlin.properties.ReadWriteProperty


//region Torch
public var Context.autoStartTorch: Boolean by BooleanPref("autoStartTorch", false)

public var Context.torchForceFloating: Boolean by BooleanPref("torchForceFloating", true)

public var Context.torchShowInLauncher: Boolean by BooleanPref("torchShowInLauncher", true)
//endregion

//region General
public var Context.g2Mode: Boolean by BooleanPref("g2Mode", false)

public var Context.showDoubleTapDialog: Boolean by BooleanPref("showDoubleTapDialog", true)
//endregion

//region Notifications
public var Context.startActivityOnNotification: Boolean by BooleanPref("startActivityOnNotification", false)
public var Context.notificationShowContent: Boolean by BooleanPref("notificationShowContent", false)
//endregion

public var Context.highScoreInSimon: Int by IntPref("highScoreInSimon", 0)

public var Context.stopwatchShowMillis: Boolean by BooleanPref("stopwatchShowMillis", true)

public var Context.showBatteryToggle: Boolean by BooleanPref("showBatteryToggle", true)
public var Context.togglesItems: String by StringPref("togglesItemsNew", "-1")

private fun Context.getPrefs(): SharedPreferences {
    return getSharedPreferences("preferences", Context.MODE_PRIVATE)
}

private class BooleanPref(val key: String, val default: Boolean) : ReadWriteProperty<Context, Boolean> {
    override fun get(thisRef: Context, desc: PropertyMetadata): Boolean {
        return thisRef.getPrefs().getBoolean(key, default)
    }

    override fun set(thisRef: Context, desc: PropertyMetadata, value: Boolean) {
        return thisRef.getPrefs().putBoolean(key, value)
    }

}

private class IntPref(val key: String, val default: Int) : ReadWriteProperty<Context, Int> {
    override fun get(thisRef: Context, desc: PropertyMetadata): Int {
        return thisRef.getPrefs().getInt(key, default)
    }

    override fun set(thisRef: Context, desc: PropertyMetadata, value: Int) {
        thisRef.getPrefs().putInt(key, value)
    }
}

private class StringPref(val key: String, val default: String) : ReadWriteProperty<Context, String> {
    override fun get(thisRef: Context, desc: PropertyMetadata): String {
        return thisRef.getPrefs().getString(key, default)
    }

    override fun set(thisRef: Context, desc: PropertyMetadata, value: String) {
        thisRef.getPrefs().putString(key, value)
    }

}
