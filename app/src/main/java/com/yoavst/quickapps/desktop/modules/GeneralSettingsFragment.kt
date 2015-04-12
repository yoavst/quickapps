package com.yoavst.quickapps.desktop.modules

import android.app.AlertDialog
import android.view.View
import android.widget.CompoundButton
import android.widget.TextView
import com.yoavst.kotlin.toast
import com.yoavst.kotlin.viewById
import com.yoavst.quickapps.R
import com.yoavst.quickapps.desktop.BaseModuleFragment
import com.yoavst.quickapps.desktop.LaunchAdminActivity
import com.yoavst.quickapps.launcher.CLauncherActivity
import com.yoavst.kotlin.startActivity
import kotlinx.android.synthetic.desktop_fragment_module_settings.*
/**
 * Created by Yoav.
 */
public class GeneralSettingsFragment : BaseModuleFragment() {
    override val CompoundButtonIds: IntArray = intArray(R.id.g2_checkbox, R.id.ads_checkbox)

    override val rowsIds: IntArray = intArray(R.id.g2_row, R.id.ads_row)
    override val layoutId: Int = R.layout.desktop_fragment_module_settings


    override fun init() {
        viewById<View>(R.id.device_admin_row).setOnClickListener {
            getActivity().startActivity<LaunchAdminActivity>()
        }
        settingsText.setText(settingsText())
        viewById<View>(R.id.settings_row).setOnClickListener {
            val isSuccess =
                    if (CLauncherActivity.hasSettings(getActivity()))
                        CLauncherActivity.removeSettings(getActivity())
                    else CLauncherActivity.addSettings(getActivity())
            if (isSuccess) {
                toast(R.string.reboot_for_update)
                settingsText.setText(settingsText())
            } else {
                toast(R.string.error)
            }
        }
    }

    fun settingsText(): Int {
        return if (CLauncherActivity.hasSettings(getActivity())) R.string.hide_settings_from_quick_circle else R.string.show_settings_from_quick_circle
    }

    override fun shouldCheck(id: Int): Boolean {
        return if (id == R.id.g2_checkbox) prefs.g2Mode().getOr(false) else prefs.hideAds().getOr(false)
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if (buttonView!!.getId() == R.id.g2_checkbox)
            prefs.g2Mode().put(isChecked).apply()
        else {
            if (isChecked) {
                AlertDialog.Builder(getActivity())
                        .setTitle(R.string.hide_ads)
                        .setMessage(R.string.hide_ads_explain)
                        .setCancelable(false)
                        .setPositiveButton(android.R.string.ok) { dialog, id ->
                            dialog.dismiss()
                            prefs.hideAds().put(true).apply()
                        }
                        .setNegativeButton(android.R.string.cancel) { dialog, id ->
                            dialog.dismiss()
                            buttonView.setChecked(false)
                        }

                        .show()
            } else {
                prefs.hideAds().put(false).apply()
            }
        }
    }
}
