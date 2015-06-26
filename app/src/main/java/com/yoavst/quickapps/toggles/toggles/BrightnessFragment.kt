package com.yoavst.quickapps.toggles.toggles

import android.content.Intent
import android.provider.Settings
import com.yoavst.kotlin.stringResource
import com.yoavst.quickapps.R
import com.yoavst.quickapps.toggles.ToggleFragment
import kotlinx.android.synthetic.toggles_toggle_fragment.image
import kotlinx.android.synthetic.toggles_toggle_fragment.text

public class BrightnessFragment : ToggleFragment() {
    val Medium by stringResource(R.string.brightness_med)
    val Auto by stringResource(R.string.brightness_auto)
    val Max by stringResource(R.string.brightness_max)
    val Low by stringResource(R.string.brightness_low)
    /**
     * 0 is Auto
     * 1 is Low
     * 2 is Medium
     * 3 is Max
     */
    var selectedMode = -1

    override fun onToggleButtonClicked() {
        selectedMode++
        if (selectedMode == 4) selectedMode = 0
        if (selectedMode == 0)
            setBrightnessMode(true)
        else if (selectedMode == 1)
            setBrightness(110) // 30%
        else if (selectedMode == 2)
            setBrightness(175) // 60%
        else
            setBrightness(229) // 90%
        showBrightness(getBrightness())
    }


    override fun init() {
        image.setImageResource(R.drawable.ic_brightness)
        setToggleBackgroundOn()
        val brightness = getBrightness()
        if (brightness == 9999)
            selectedMode = 0
        else if (brightness < 115)
            selectedMode = 1
        else if (brightness < 180)
            selectedMode = 2
        else
            selectedMode = 3
        showBrightness(brightness)
    }

    private fun showBrightness(level: Int) {
        image.setImageLevel(level)
        if (selectedMode == 0) {
            text.setText(Auto)
        } else if (selectedMode == 1) {
            text.setText(Low)
        } else if (selectedMode == 2) {
            text.setText(Medium)
        } else {
            text.setText(Max)
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
                return 9999
            return Settings.System.getInt(getActivity().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS)
        } catch (e: Settings.SettingNotFoundException) {
            e.printStackTrace()
        }

        return 0
    }

    override fun getTitle(): CharSequence = getString(R.string.brightness)
    override fun getIntentForLaunch(): Intent = Intent(Settings.ACTION_DISPLAY_SETTINGS)

}