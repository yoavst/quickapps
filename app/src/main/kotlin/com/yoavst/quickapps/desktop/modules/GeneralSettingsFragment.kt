package com.yoavst.quickapps.desktop.modules

import com.yoavst.quickapps.desktop.BaseModuleFragment
import com.yoavst.quickapps.R
import android.widget.CompoundButton
import android.view.View
import com.yoavst.quickapps.desktop.LaunchAdminActivity
import com.mobsandgeeks.ake.viewById
import com.mobsandgeeks.ake.startActivity
import android.app.AlertDialog
import butterknife.bindView
import android.widget.TextView
import com.yoavst.quickapps.launcher.CLauncherActivity
import com.mobsandgeeks.ake.showShortToast

/**
 * Created by Yoav.
 */
public class GeneralSettingsFragment : BaseModuleFragment() {
    override val CompoundButtonIds: IntArray = intArray(R.id.g2_checkbox, R.id.ads_checkbox)

    override val rowsIds: IntArray = intArray(R.id.g2_row, R.id.ads_row)
    override val layoutId: Int = R.layout.desktop_fragment_module_settings

    val settingsText: TextView by bindView(R.id.settings_text)

    override fun init() {
        viewById<View>(R.id.device_admin_row).setOnClickListener {
            startActivity<LaunchAdminActivity>()
        }
        settingsText.setText(settingsText())
        viewById<View>(R.id.settings_row).setOnClickListener {
            val isSuccess =
                    if (CLauncherActivity.hasSettings(getActivity()))
                        CLauncherActivity.removeSettings(getActivity())
                    else CLauncherActivity.addSettings(getActivity())
            if (isSuccess) {
                showShortToast(R.string.reboot_for_update)
                settingsText.setText(settingsText())
            } else {
                showShortToast(R.string.error)
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
