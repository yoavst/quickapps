package com.yoavst.quickapps.desktop.modules

import com.yoavst.quickapps.desktop.BaseModuleFragment
import com.yoavst.quickapps.R
import android.widget.CompoundButton
import com.yoavst.kotlin.toast

/**
 * Created by Yoav.
 */
public class CalculatorFragment: BaseModuleFragment() {
    override val CompoundButtonIds: IntArray = intArray(R.id.qslide_checkbox)
    override val rowsIds: IntArray = intArray(R.id.qslide_row)
    override val layoutId: Int = R.layout.desktop_module_calculator

    override fun shouldCheck(id: Int): Boolean {
        return prefs.calculatorForceFloating().getOr(false)
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        prefs.calculatorForceFloating().put(isChecked).apply()
        toast(R.string.changed_successfully)
    }
}