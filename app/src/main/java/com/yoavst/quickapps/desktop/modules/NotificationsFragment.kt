package com.yoavst.quickapps.desktop.modules

import com.yoavst.quickapps.desktop.BaseModuleFragment
import com.yoavst.quickapps.R
import android.widget.CompoundButton

import android.content.Intent
import android.view.View
import com.yoavst.kotlin.toast

/**
 * Created by Yoav.
 */
public class NotificationsFragment: BaseModuleFragment() {
    override val CompoundButtonIds: IntArray = intArray(R.id.privacy_checkbox,R.id.am_pm_checkbox, R.id.auto_launch_checkbox)
    override val rowsIds: IntArray = intArray(R.id.privacy_row, R.id.am_pm_row, R.id.auto_launch_row)
    override val layoutId: Int = R.layout.desktop_module_notifications

    override fun shouldCheck(id: Int): Boolean {
        return when (id) {
            R.id.privacy_checkbox ->  prefs.notificationShowContent().getOr(true)
            R.id.am_pm_checkbox -> prefs.amPmInNotifications().getOr(false)
            R.id.auto_launch_checkbox -> prefs.startActivityOnNotification().getOr(false)
            else -> false
        }
    }

    override fun init() {
        getView()!!.findViewById(R.id.listener_row).setOnClickListener { v -> getActivity().startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")) };
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when (buttonView?.getId()) {
            R.id.privacy_checkbox -> prefs.notificationShowContent().put(isChecked).apply()
            R.id.am_pm_checkbox -> prefs.amPmInNotifications().put(isChecked).apply()
            R.id.auto_launch_checkbox -> prefs.startActivityOnNotification().put(isChecked).apply()
        }
        toast(R.string.changed_successfully)
    }
}