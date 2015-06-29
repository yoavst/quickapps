package com.yoavst.quickapps.desktop.modules

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.util.AttributeSet
import com.yoavst.quickapps.R
import com.yoavst.quickapps.desktop.BaseModuleView
import com.yoavst.quickapps.tools.autoStartTorch
import com.yoavst.quickapps.tools.torchForceFloating

public class TorchModuleView : BaseModuleView {
    public constructor(context: Context) : super(context) {
    }

    public constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    }

    public constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    }

    override fun init() {
        super.init()
        if (!isInEditMode()) {
            addSettingView(R.string.torch_launcher_shortcut, R.string.torch_launcher_shortcut_subtext,
                    isActivityEnabled(getContext(), LAUNCHER_CLASS_NAME)) {
                setActivityEnabled(getContext(), LAUNCHER_CLASS_NAME, it!!)
                toastRestartLauncher()
            }
            addSettingView(R.string.torch_qslide_shortcut, R.string.torch_qslide_shortcut_subtext,
                    isActivityEnabled(getContext(), QSLIDE_CLASS_NAME)) {
                setActivityEnabled(getContext(), QSLIDE_CLASS_NAME, it!!)
                toastRestartLauncher()
            }
            addSettingView(R.string.torch_force_floating, R.string.torch_force_floating_subtext,
                    getContext().torchForceFloating) {
                getContext().torchForceFloating = it!!
                toastSuccess()
            }
            addSettingView(R.string.torch_auto_start, R.string.torch_auto_start_subtext,
                    getContext().autoStartTorch) {
                getContext().autoStartTorch = it!!
                toastSuccess()
            }
        }
    }

    override fun getName(): Int = R.string.torch_module_name

    override fun getIcon(): Int = R.drawable.qcircle_icon_torch

    companion object {
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