package com.yoavst.quickapps.toggles.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Resources
import android.graphics.Color
import android.os.BatteryManager
import android.widget.Toast

import com.yoavst.quickapps.R
import com.yoavst.quickapps.toggles.ToggleFragment

import kotlin.properties.Delegates
import com.yoavst.quickapps.toggles.CTogglesActivity

/**
 * Created by Yoav.
 */
public class BatteryFragment : ToggleFragment() {
    val BATTERY: String by Delegates.lazy { getString(R.string.battery) }
    val CHARGING: String by Delegates.lazy { getString(R.string.charging) }
    var mBatteryReceiver: BroadcastReceiver? = null
    var oldBatteryLevel = -1
    var oldCharging = -1

    override fun init() {
        toggleTitle.setText(BATTERY)
        toggleIcon.setBackground(null)
        toggleIcon.setColorFilter(Color.BLACK)
        if (mBatteryReceiver == null)
            mBatteryReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    setToggleData(intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1), intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0))
                }
            }
        val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val intent = getActivity().registerReceiver(mBatteryReceiver, intentFilter)
        setToggleData(if (intent == null) -1 else intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1), if (intent == null) -1 else intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0))
    }

    fun setToggleData(percents: Int, charging: Int) {
        // Do something only if it is changed
        if (percents != oldBatteryLevel || charging != oldCharging) {
            // If charging
            val resource: String
            if (charging != 0 && percents <= 5)
                resource = "stat_sys_battery_weak_charging_05_vzw"
            else if (charging != 0 && percents > 95)
                resource = "stat_sys_battery_full_charging"
            else {
                var percent1 = ((percents + 4) / 5 * 5)
                if (percent1 > 100)
                    percent1 = 100
                else if (percent1 < 0) percent1 = 0
                resource = "stat_sys_battery_" + percent1
            }
            try {
                toggleIcon.setImageDrawable((getActivity() as CTogglesActivity).getSystemUiResource().getDrawable(
                        (getActivity() as CTogglesActivity).getSystemUiResource().getIdentifier(resource, "drawable", "com.android.systemui")))
            } catch (exception: Resources.NotFoundException) {
                Toast.makeText(getActivity(), "Error - contact developer with battery level", Toast.LENGTH_SHORT).show()
            }

            toggleText.setText(percents.toString() + "% ")
            if (charging != 0) {
                toggleText.append(CHARGING)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            getActivity().unregisterReceiver(mBatteryReceiver)
        } catch (ignored: Exception) {
            // Do nothing - receiver not registered
        }

    }

    override fun onToggleButtonClicked() {
        // Do nothing
    }

    override fun getIntentForLaunch(): Intent {
        return Intent(Intent.ACTION_POWER_USAGE_SUMMARY)
    }
}
