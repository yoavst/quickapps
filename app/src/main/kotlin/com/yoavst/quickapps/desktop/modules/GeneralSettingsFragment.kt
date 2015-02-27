package com.yoavst.quickapps.desktop.modules

import com.yoavst.quickapps.desktop.BaseModuleFragment
import com.yoavst.quickapps.R
import android.widget.CompoundButton
import android.view.View
import com.yoavst.quickapps.desktop.LaunchAdminActivity
import com.mobsandgeeks.ake.viewById
import com.mobsandgeeks.ake.startActivity
/**
 * Created by Yoav.
 */
public class GeneralSettingsFragment : BaseModuleFragment() {
    override val CompoundButtonIds: IntArray = intArray(R.id.g2_checkbox)

    override val rowsIds: IntArray = intArray(R.id.g2_row)
    override val layoutId: Int = R.layout.desktop_fragment_module_settings

    override fun init() {
        viewById<View>(R.id.device_admin_row).setOnClickListener {
            startActivity<LaunchAdminActivity>()
        }
    }

    override fun shouldCheck(id: Int): Boolean {
        return prefs.g2Mode().getOr(false)
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        prefs.g2Mode().put(isChecked).apply()
    }
}
