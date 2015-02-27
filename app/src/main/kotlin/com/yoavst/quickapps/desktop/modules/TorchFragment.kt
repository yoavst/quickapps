package com.yoavst.quickapps.desktop.modules

import android.widget.CompoundButton
import android.content.Context
import com.yoavst.quickapps.R
import android.widget.Toast
import android.content.pm.PackageManager
import android.content.ComponentName
import com.yoavst.quickapps.desktop.BaseModuleFragment

/**
 * Created by Yoav.
 */
public class TorchFragment : BaseModuleFragment() {
    override val CompoundButtonIds: IntArray = intArray(R.id.launcher_checkbox, R.id.qslide_checkbox, R.id.floating_checkbox)
    override val rowsIds: IntArray = intArray(R.id.launcher_row, R.id.qslide_row, R.id.floating_row)
    override val layoutId: Int = R.layout.desktop_module_torch

    override fun shouldCheck(id: Int): Boolean {
        when (id) {
            R.id.launcher_checkbox -> return isActivityEnabled(getActivity(), LAUNCHER_CLASS_NAME)
            R.id.qslide_checkbox -> return isActivityEnabled(getActivity(), QSLIDE_CLASS_NAME)
            R.id.floating_checkbox -> return prefs.torchForceFloating().getOr(false)
        }
        return false
    }

    public override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        when (buttonView.getId()) {
            R.id.launcher_checkbox -> {
                setActivityEnabled(getActivity(), LAUNCHER_CLASS_NAME, isChecked)
                Toast.makeText(getActivity(), R.string.restart_launcher_for_update, Toast.LENGTH_SHORT).show()
            }
            R.id.qslide_checkbox -> {
                setActivityEnabled(getActivity(), QSLIDE_CLASS_NAME, isChecked)
                Toast.makeText(getActivity(), R.string.reboot_for_update, Toast.LENGTH_SHORT).show()
            }
            R.id.floating_checkbox -> {
                prefs.torchForceFloating().put(isChecked).apply()
                Toast.makeText(getActivity(), R.string.changed_successfully, Toast.LENGTH_SHORT).show()
            }
        }
    }

    class object {
        private val LAUNCHER_CLASS_NAME = "com.yoavst.quickapps.torch.PhoneActivityLauncher"
        private val QSLIDE_CLASS_NAME = "com.yoavst.quickapps.torch.PhoneActivity"
        public fun setActivityEnabled(context: Context, activityName: String, enable: Boolean) {
            val pm = context.getPackageManager()
            val enableFlag = if (enable) PackageManager.COMPONENT_ENABLED_STATE_ENABLED else PackageManager.COMPONENT_ENABLED_STATE_DISABLED
            pm.setComponentEnabledSetting(ComponentName(context, activityName), enableFlag, PackageManager.DONT_KILL_APP)
        }

        public fun isActivityEnabled(context: Context, activityName: String): Boolean {
            val pm = context.getPackageManager()
            val flags = pm.getComponentEnabledSetting(ComponentName(context.getPackageName(), activityName))
            return (flags and PackageManager.COMPONENT_ENABLED_STATE_DISABLED) == 0
        }
    }
}