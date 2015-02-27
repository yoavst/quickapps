package com.yoavst.quickapps

import android.app.Application
import android.net.Uri
import android.os.Handler
import kotlin.properties.Delegates

/**
 * Created by Yoav.
 */
public class App : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
        // FIXME
        observer = LGLauncherChangeObserver(Handler())
        getContentResolver().registerContentObserver(Uri.parse("content://com.lge.lockscreensettings/quickwindow"), true, observer)
    }

    class object {
        public var instance: App by Delegates.notNull()
        public var observer: LGLauncherChangeObserver by Delegates.notNull()
    }
}
