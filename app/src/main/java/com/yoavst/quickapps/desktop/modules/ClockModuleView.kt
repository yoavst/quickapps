package com.yoavst.quickapps.desktop.modules

import android.content.Context
import android.util.AttributeSet
import com.yoavst.quickapps.R
import com.yoavst.quickapps.desktop.BaseModuleView
import com.yoavst.quickapps.tools.stopwatchShowMillis


public class ClockModuleView : BaseModuleView {
    public constructor(context: Context) : super(context) {
    }

    public constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    }

    public constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    }

    override fun init() {
        super.init()
        if (!isInEditMode()) {
            addSettingView(R.string.show_millis_title, R.string.show_millis_subtitle, getContext().stopwatchShowMillis) {
                getContext().stopwatchShowMillis = it!!
                toastSuccess()
            }
        }
    }

    override fun getName(): Int = R.string.clock_module_name

    override fun getIcon(): Int = R.drawable.qcircle_icon_clock
}