package com.yoavst.quickapps.desktop.modules

import com.yoavst.quickapps.desktop.BaseModuleFragment
import com.yoavst.quickapps.R
import android.widget.CompoundButton
import com.mobsandgeeks.ake.showShortToast


/**
 * Created by Yoav.
 */
public class StopwatchFragment: BaseModuleFragment() {
    override val CompoundButtonIds: IntArray = intArray(R.id.millis_checkbox)
    override val rowsIds: IntArray = intArray(R.id.millis_row)
    override val layoutId: Int = R.layout.desktop_module_stopwatch

    override fun shouldCheck(id: Int): Boolean {
        return prefs.stopwatchShowMillis().getOr(true)
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        prefs.stopwatchShowMillis().put(isChecked).apply()
        showShortToast(R.string.changed_successfully)
    }
}