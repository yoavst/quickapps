package com.yoavst.quickapps.toggles.fragments

import android.content.Intent
import android.content.res.Resources
import android.provider.Settings
import com.yoavst.kotlin.stringResource

import com.yoavst.quickapps.R
import com.yoavst.quickapps.toggles.ToggleFragment
import com.yoavst.quickapps.toggles.CTogglesActivity
import kotlin.properties.Delegates

/**
 * Created by Yoav.
 */
public class BrightnessFragment : ToggleFragment() {
    val BRIGHTNESS by stringResource(R.string.brightness)
    val MED by stringResource(R.string.brightness_med)
    val AUTO by stringResource(R.string.brightness_auto)
    val MAX by stringResource(R.string.brightness_max)
    val LOW by stringResource(R.string.brightness_low)
    val systemUiResources: Resources by Delegates.lazy { (getActivity() as CTogglesActivity).getSystemUiResource() }
    /**
     * 0 is Auto
     * 1 is Low
     * 2 is Medium
     * 3 is Max
     */
    var selectedMode = -1

    override fun init() {
        toggleTitle.setText(BRIGHTNESS)
        if (maxBrightnessIcon == -1 || medBrightnessIcon == -1 || autoBrightnessIcon == -1) {
            maxBrightnessIcon = systemUiResources.getIdentifier("indi_noti_brightness_max_on", "drawable", "com.android.systemui")
            autoBrightnessIcon = systemUiResources.getIdentifier("indi_noti_brightness_auto_on", "drawable", "com.android.systemui")
            medBrightnessIcon = systemUiResources.getIdentifier("indi_noti_brightness_mid_on", "drawable", "com.android.systemui")
        }
        val brightness = getBrightness()
        if (brightness == -1)
            selectedMode = 0
        else if (brightness < 115)
            selectedMode = 1
        else if (brightness < 180)
            selectedMode = 2
        else
            selectedMode = 3
        showBrightness()
    }

    override fun onToggleButtonClicked() {
        selectedMode++
        if (selectedMode == 4) selectedMode = 0
        if (selectedMode == 0)
            setBrightnessMode(true)
        else if (selectedMode == 1)
            setBrightness(110) // 30$
        else if (selectedMode == 2)
            setBrightness(175) // 60%
        else
            setBrightness(229) // 90 %
        showBrightness()
    }

    override fun getIntentForLaunch(): Intent {
        return Intent(Settings.ACTION_DISPLAY_SETTINGS)
    }

    private fun showBrightness() {
        if (selectedMode == 0) {
            toggleIcon.setImageDrawable(systemUiResources.getDrawable(autoBrightnessIcon))
            toggleText.setText(AUTO)
        } else if (selectedMode == 1) {
            toggleIcon.setImageResource(R.drawable.indi_noti_brightness_low)
            toggleText.setText(LOW)
        } else if (selectedMode == 2) {
            toggleIcon.setImageDrawable(systemUiResources.getDrawable(medBrightnessIcon))
            toggleText.setText(MED)
        } else {
            toggleIcon.setImageDrawable(systemUiResources.getDrawable(maxBrightnessIcon))
            toggleText.setText(MAX)
        }
    }

    private fun setBrightnessMode(auto: Boolean) {
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, if (auto) 1 else 0)
    }

    private fun setBrightness(level: Int) {
        setBrightnessMode(false)
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, level)
    }

    private fun getBrightness(): Int {
        try {
            if (Settings.System.getInt(getActivity().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE) == 1)
                return -1
            return Settings.System.getInt(getActivity().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS)
        } catch (e: Settings.SettingNotFoundException) {
            e.printStackTrace()
        }

        return 0
    }

    class object {
        // resources id of system ui stuff
        var maxBrightnessIcon = -1
        var medBrightnessIcon = -1
        var autoBrightnessIcon = -1
    }
}
