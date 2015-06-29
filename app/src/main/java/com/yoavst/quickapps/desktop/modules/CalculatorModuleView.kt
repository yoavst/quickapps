package com.yoavst.quickapps.desktop.modules

import android.content.Context
import android.util.AttributeSet
import com.yoavst.quickapps.R
import com.yoavst.quickapps.desktop.BaseModuleView
import com.yoavst.quickapps.tools.calculatorForceFloating


public class CalculatorModuleView : BaseModuleView {
    public constructor(context: Context) : super(context) {
    }

    public constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    }

    public constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    }

    override fun init() {
        super.init()
        if (!isInEditMode()) {
            addSettingView(R.string.torch_force_floating, R.string.torch_force_floating_subtext, getContext().calculatorForceFloating) {
                getContext().calculatorForceFloating = it!!
                toastSuccess()
            }
        }
    }

    override fun getName(): Int = R.string.calculator_module_name

    override fun getIcon(): Int = R.drawable.qcircle_icon_calculator
}