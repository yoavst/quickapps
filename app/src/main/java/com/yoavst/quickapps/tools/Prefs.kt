package com.yoavst.quickapps.tools

import android.content.Context
import android.graphics.Color
import com.yoavst.kotlin.putBoolean
import com.yoavst.kotlin.putInt
import com.yoavst.kotlin.putLong
import com.yoavst.kotlin.putString
import kotlin.properties.ReadWriteProperty


//region Torch
public var Context.autoStartTorch: Boolean by BooleanPref("autoStartTorch", false)
public var Context.torchForceFloating: Boolean by BooleanPref("torchForceFloating", true)
public var Context.torchShowOldIcon: Boolean by BooleanPref("torchShowOldIcon", false)
//endregion

//region General
public var Context.g2Mode: Boolean by BooleanPref("g2Mode", false)

public var Context.showDoubleTapDialog: Boolean by BooleanPref("showDoubleTapDialog", true)
//endregion

//region Notifications
public var Context.startActivityOnNotification: Boolean by BooleanPref("startActivityOnNotification", false)
public var Context.notificationShowContent: Boolean by BooleanPref("notificationShowContent", true)
public var Context.amPmInNotifications: Boolean by BooleanPref("amPmInNotifications", false)
//endregion

public var Context.highScoreInSimon: Int by IntPref("highScoreInSimon", 0)

public var Context.stopwatchShowMillis: Boolean by BooleanPref("stopwatchShowMillis", true)

//region Toggles
public var Context.showBatteryToggle: Boolean by BooleanPref("showBatteryToggle", true)
public var Context.togglesItems: String by StringPref("togglesItemsNew", "-1")
//endregion

//region news
public var Context.userId: String by StringPref("userId", "", "pref")
public var Context.refreshToken: String by StringPref("refreshToken", "", "pref")
public var Context.accessToken: String by StringPref("accessToken", "", "pref")
public var Context.rawResponse: String by StringPref("rawResponse", "", "pref")
public var Context.feed: String by StringPref("feed", "", "pref")
public var Context.lastUpdateTime: Long by LongPref("lastUpdateTime", 0, "pref")
//endregion

public var Context.calculatorForceFloating: Boolean by BooleanPref("calculatorForceFloating", true)

// region Calendar
public var Context.amPmInCalendar: Boolean by BooleanPref("amPmInCalendar", true)
public var Context.showAllDayEvents: Boolean by BooleanPref("showAllDayEvents", true)
public var Context.showLocation: Boolean by BooleanPref("showLocation", true)
// endregion

public var Context.quickDials: String by StringPref("quickDials", "{}")

public var Context.hideAds: Boolean by BooleanPref("hideAds", false)

public var Context.musicOldStyle: Boolean by BooleanPref("musicShowOld", false)

// region Launcher
public var Context.launcherIsVertical: Boolean by BooleanPref("launcherIsVertical", true)
public var Context.launcherItems: String by StringPref("launcherItemsNew", "{}")
// endregion

// region Watchface
public var Context.digitalWatchfaceMainBackgroundColor: Int by IntPref("digitalWatchfaceMainBackgroundColor", Color.parseColor("#3F51B5"), "watchface")
public var Context.digitalWatchfaceSecondaryBackgroundColor: Int by IntPref("digitalWatchfaceSecondaryBackgroundColor", Color.parseColor("#303F9F"), "watchface")
public var Context.digitalWatchfaceHoursColor: Int by IntPref("digitalWatchfaceHoursColor", Color.parseColor("#009688"), "watchface")
public var Context.digitalWatchfaceMinutesColor: Int by IntPref("digitalWatchfaceMinutesColor", Color.WHITE, "watchface")
public var Context.digitalWatchfaceDateColor: Int by IntPref("digitalWatchfaceDateColor", Color.WHITE, "watchface")
public var Context.digitalWatchfaceDateFormat: String by StringPref("digitalWatchfaceDateFormat", "MMM d", "watchface")
public var Context.digitalWatchfaceAmPm: Boolean by BooleanPref("digitalWatchfaceAmPm", false, "watchface")
public var Context.digitalWatchfaceAmPmColor: Int by IntPref("digitalWatchfaceAmPmColor", Color.WHITE, "watchface")

// endregion

private class BooleanPref(val key: String, val default: Boolean, val preferenceName: String = "preferences") : ReadWriteProperty<Context, Boolean> {
    override fun get(thisRef: Context, desc: PropertyMetadata): Boolean {
        return thisRef.getSharedPreferences(preferenceName, Context.MODE_PRIVATE).getBoolean(key, default)
    }

    override fun set(thisRef: Context, desc: PropertyMetadata, value: Boolean) {
        return thisRef.getSharedPreferences(preferenceName, Context.MODE_PRIVATE).putBoolean(key, value)
    }

}

private class IntPref(val key: String, val default: Int, val preferenceName: String = "preferences") : ReadWriteProperty<Context, Int> {
    override fun get(thisRef: Context, desc: PropertyMetadata): Int {
        return thisRef.getSharedPreferences(preferenceName, Context.MODE_PRIVATE).getInt(key, default)
    }

    override fun set(thisRef: Context, desc: PropertyMetadata, value: Int) {
        thisRef.getSharedPreferences(preferenceName, Context.MODE_PRIVATE).putInt(key, value)
    }
}

private class StringPref(val key: String, val default: String, val preferenceName: String = "preferences") : ReadWriteProperty<Context, String> {
    override fun get(thisRef: Context, desc: PropertyMetadata): String {
        return thisRef.getSharedPreferences(preferenceName, Context.MODE_PRIVATE).getString(key, default)
    }

    override fun set(thisRef: Context, desc: PropertyMetadata, value: String) {
        thisRef.getSharedPreferences(preferenceName, Context.MODE_PRIVATE).putString(key, value)
    }
}

private class LongPref(val key: String, val default: Long, val  preferenceName: String = "preferences") : ReadWriteProperty<Context, Long> {
    override fun get(thisRef: Context, desc: PropertyMetadata): Long {
        return thisRef.getSharedPreferences(preferenceName, Context.MODE_PRIVATE).getLong(key, default)
    }

    override fun set(thisRef: Context, desc: PropertyMetadata, value: Long) {
        thisRef.getSharedPreferences(preferenceName, Context.MODE_PRIVATE).putLong(key, value)
    }
}
