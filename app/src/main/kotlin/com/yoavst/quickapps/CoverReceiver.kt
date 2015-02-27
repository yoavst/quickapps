package com.yoavst.quickapps

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Settings
import kotlin.platform.platformStatic

/**
 * Created by Yoav.
 */
public class CoverReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.getAction()
        if (action == null) {
            return
        }
        val quickCaseType = Settings.Global.getInt(context.getContentResolver(), "cover_type", 0)
        val quickCircleEnabled = Settings.Global.getInt(context.getContentResolver(), "quick_view_enable", 0)
        // Receives a LG QCirle intent for the cover event
        if (ACTION_ACCESSORY_COVER_EVENT == action && quickCaseType == QUICKCOVERSETTINGS_QUICKCIRCLE && quickCircleEnabled == QUICKCOVERSETTINGS_USEQUICKCIRCLE) {
            // Gets the current state of the cover
            val quickCoverState = intent.getIntExtra(EXTRA_ACCESSORY_COVER_STATE, EXTRA_ACCESSORY_COVER_OPENED)
            if (quickCoverState == EXTRA_ACCESSORY_COVER_CLOSED) {
                // closed
                isCoverInUse = true
            } else if (quickCoverState == EXTRA_ACCESSORY_COVER_OPENED) {
                // opened
                isCoverInUse = false
            }
        }
    }

    class object {
        /**
         * True if is in cover mode, false if regular.
         */
        public platformStatic var isCoverInUse: Boolean = false
        protected val EXTRA_ACCESSORY_COVER_OPENED: Int = 0
        protected val EXTRA_ACCESSORY_COVER_CLOSED: Int = 1
        protected val EXTRA_ACCESSORY_COVER_STATE: String = "com.lge.intent.extra.ACCESSORY_COVER_STATE"
        protected val ACTION_ACCESSORY_COVER_EVENT: String = "com.lge.android.intent.action.ACCESSORY_COVER_EVENT"
        protected val QUICKCOVERSETTINGS_QUICKCIRCLE: Int = 3
        protected val QUICKCOVERSETTINGS_USEQUICKCIRCLE: Int = 1

    }
}