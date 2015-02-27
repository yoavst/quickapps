package com.yoavst.quickapps.desktop.modules

import com.yoavst.quickapps.desktop.BaseModuleFragment
import android.widget.CompoundButton
import com.yoavst.quickapps.R
import com.mobsandgeeks.ake.showShortToast

/**
 * Created by Yoav.
 */
public class CalendarFragment : BaseModuleFragment() {
    override val CompoundButtonIds: IntArray = intArray(R.id.repeating_checkbox, R.id.location_checkbox, R.id.am_pm_checkbox)
    override val rowsIds: IntArray = intArray(R.id.repeating_row, R.id.location_row, R.id.am_pm_row)
    override val layoutId: Int = R.layout.desktop_module_calendar

    override fun shouldCheck(id: Int): Boolean {
        return when (id) {
            R.id.repeating_checkbox -> prefs.showRepeatingEvents().getOr(true)
            R.id.location_checkbox -> prefs.showLocation().getOr(true);
            R.id.am_pm_checkbox -> prefs.amPmInCalendar().getOr(false)
            else -> false
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when (buttonView?.getId()) {
            R.id.repeating_checkbox -> prefs.showRepeatingEvents().put(isChecked).apply()
            R.id.location_checkbox -> prefs.showLocation().put(isChecked).apply()
            R.id.am_pm_checkbox -> prefs.amPmInCalendar().put(isChecked).apply()
        }
        showShortToast(R.string.changed_successfully)
    }
}