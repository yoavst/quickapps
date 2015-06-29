package com.yoavst.quickapps.desktop.modules

import android.content.Context
import android.util.AttributeSet
import com.yoavst.quickapps.R
import com.yoavst.quickapps.desktop.BaseModuleView
import com.yoavst.quickapps.tools.amPmInCalendar
import com.yoavst.quickapps.tools.showLocation


public class CalendarModuleView : BaseModuleView {
    public constructor(context: Context) : super(context) {
    }

    public constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    }

    public constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    }

    override fun init() {
        super.init()
        if (!isInEditMode()) {
            addSettingView(R.string.location_title, R.string.location_subtitle, getContext().showLocation) {
                getContext().showLocation = it!!
                toastSuccess()
            }
            addSettingView(R.string.am_pm_title, R.string.am_pm_subtitle, getContext().amPmInCalendar) {
                getContext().amPmInCalendar = it!!
                toastSuccess()
            }
        }
    }

    override fun getName(): Int = R.string.calendar_module_name

    override fun getIcon(): Int = R.drawable.qcircle_icon_calendar
}