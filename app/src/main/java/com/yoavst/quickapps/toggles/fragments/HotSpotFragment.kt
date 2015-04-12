package com.yoavst.quickapps.toggles.fragments

import android.content.ComponentName
import android.content.Intent
import android.content.res.Resources
import com.yoavst.kotlin.stringResource

import com.yoavst.quickapps.R
import com.yoavst.quickapps.toggles.Connectivity
import com.yoavst.quickapps.toggles.ToggleFragment
import com.yoavst.quickapps.toggles.CTogglesActivity
import kotlin.properties.Delegates

/**
 * Created by Yoav.
 */
public class HotSpotFragment : ToggleFragment() {
    val HOT_SPOT by stringResource(R.string.hotSpot)
    val OFF by stringResource(R.string.hotSpot_off)
    val ON by stringResource(R.string.hotSpot_on)
    val systemUiResources: Resources by Delegates.lazy { (getActivity() as CTogglesActivity).getSystemUiResource() }
    val enabled: Boolean = false

    override fun init() {
        toggleTitle.setText(HOT_SPOT)
        if (onIcon == -1 || offIcons == -1) {
            offIcons = systemUiResources.getIdentifier("indi_noti_hotspot_off", "drawable", "com.android.systemui")
            onIcon = systemUiResources.getIdentifier("indi_noti_hotspot_on", "drawable", "com.android.systemui")
        }
        setToggleData(enabled = Connectivity.isApOn(getActivity()))
    }

    fun setToggleData(enabled: Boolean) {
        toggleIcon.setImageDrawable(systemUiResources.getDrawable(if (enabled) onIcon else offIcons))
        toggleText.setText(if (enabled) ON else OFF)

    }

    override fun onToggleButtonClicked() {
        setToggleData(enabled = !enabled)
        Connectivity.configApState(getActivity())

    }

    override fun getIntentForLaunch(): Intent {
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val cn = ComponentName("com.android.settings", "com.android.settings.TetherSettings")
        intent.setComponent(cn)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return intent
    }

    class object {
        // resources id of system ui stuff
        var onIcon = -1
        var offIcons = -1
    }
}
