package com.yoavst.quickapps.toggles.toggles

import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.drawable.LayerDrawable
import android.os.BatteryManager
import android.os.PowerManager
import com.yoavst.kotlin.broadcastReceiver
import com.yoavst.kotlin.colorResource
import com.yoavst.kotlin.stringResource
import com.yoavst.kotlin.systemService
import com.yoavst.quickapps.R
import com.yoavst.quickapps.toggles.ToggleFragment
import kotlinx.android.synthetic.toggles_toggle_fragment.image
import kotlinx.android.synthetic.toggles_toggle_fragment.text
import kotlin.properties.Delegates

/**
 * Created by yoavst.
 */
public class BatteryFragment : ToggleFragment() {
    val Charging by stringResource(R.string.charging)
    val BatterySaverColor by colorResource(R.color.battery_saver_color)
    val powerManager: PowerManager by systemService()
    val batteryDrawable by Delegates.lazy {
        val resource = systemUiResources.getIdentifier("stat_sys_battery", "drawable", "com.android.systemui")
        systemUiResources.getDrawable(resource)
    }
    val chargeDrawable by Delegates.lazy {
        val resource = systemUiResources.getIdentifier("stat_sys_battery_light", "drawable", "com.android.systemui")
        systemUiResources.getDrawable(resource)
    }
    var receiver = broadcastReceiver { context, intent ->
        if (intent != null)
            setToggleData(intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1), intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0))
    }
    var oldBatteryLevel = -1
    var oldCharging = -1
    var isPowerSaveOn = false

    override fun onToggleButtonClicked() {

    }

    override fun getIntentForLaunch(): Intent = Intent(Intent.ACTION_POWER_USAGE_SUMMARY)

    override fun init() {
        setToggleBackgroundOn()
        val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val intent = getActivity().registerReceiver(receiver, intentFilter)
        setToggleData(if (intent == null) -2 else intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1),
                if (intent == null) -2 else intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0))
    }

    fun setToggleData(percents: Int, charging: Int) {
        // Do something only if it is changed
        if (percents != oldBatteryLevel || charging != oldCharging || isPowerSaveOn != powerManager.isPowerSaveMode()) {
            if (powerManager.isPowerSaveMode())
                setToggleBackground(BatterySaverColor)
            else setToggleBackgroundOn()
            if (charging != 0) {
                image.setImageDrawable(LayerDrawable(arrayOf(batteryDrawable, chargeDrawable)))
            } else image.setImageDrawable(batteryDrawable)
            image.setImageLevel(percents)
            text.setText(percents.toString() + "% ")
            if (charging != 0) {
                text.append(Charging)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        getActivity().unregisterReceiver(receiver)
    }

    override fun getTitle(): CharSequence = getString(R.string.battery)

    val systemUiResources: Resources by Delegates.lazy {
        val pm = getActivity().getPackageManager()
        try {
            val applicationInfo = pm.getApplicationInfo("com.android.systemui", PackageManager.GET_META_DATA)
            pm.getResourcesForApplication(applicationInfo);
        } catch (e: PackageManager.NameNotFoundException) {
            // Congratulations user, you are so dumb that there is no system ui...
            e.printStackTrace();
            throw IllegalStateException()
        }
    }
}