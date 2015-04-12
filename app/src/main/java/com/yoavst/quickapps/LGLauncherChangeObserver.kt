package com.yoavst.quickapps

import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import com.yoavst.quickapps.launcher.CLauncherActivity

public class LGLauncherChangeObserver(handler: Handler) : ContentObserver(handler) {

    override fun onChange(selfChange: Boolean) {
        onChange(selfChange, null)
    }

    override fun onChange(selfChange: Boolean, uri: Uri?) {
        val context = App.instance
        if (!PrefManager(context).showAppsThatInLg().getOr(false)) {
            CLauncherActivity.updateComponents(context)
        }
        CLauncherActivity.defaultItems = CLauncherActivity.initDefaultIcons(context)
    }
}