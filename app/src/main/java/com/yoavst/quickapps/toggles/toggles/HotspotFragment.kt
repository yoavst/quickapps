package com.yoavst.quickapps.toggles.toggles

import android.content.ComponentName
import android.content.Intent
import com.yoavst.kotlin.drawableResource
import com.yoavst.kotlin.stringResource
import com.yoavst.quickapps.R
import com.yoavst.quickapps.toggles.Connectivity
import com.yoavst.quickapps.toggles.ToggleFragment
import kotlinx.android.synthetic.toggles_toggle_fragment.*
/**
 * Created by yoavst.
 */
public class HotspotFragment : ToggleFragment() {
    val Off by stringResource(R.string.hotSpot_off)
    val On by stringResource(R.string.hotSpot_on)
    val OnDrawable by drawableResource(R.drawable.ic_wifi_tethering)
    val OffDrawable by drawableResource(R.drawable.ic_wifi_tethering_off)
    var enabled: Boolean = false

    override fun onToggleButtonClicked() {
        enabled = !enabled
        setToggleData(enabled)
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

    fun setToggleData(enabled: Boolean) {
        if (enabled) {
            image.setImageDrawable(OnDrawable)
            setToggleBackgroundOn()
            text.setText(On)
        } else {
            image.setImageDrawable(OffDrawable)
            setToggleBackgroundOff()
            text.setText(Off)
        }
    }

    override fun init() {
        enabled = Connectivity.isApOn(getActivity())
        setToggleData(enabled)
    }

    override fun getTitle(): CharSequence = getString(R.string.hotSpot)
}