package com.yoavst.quickapps.desktop

import android.app.Activity
import android.os.Bundle
import com.yoavst.quickapps.AdminListener
import android.app.admin.DevicePolicyManager
import android.content.Intent
import android.content.ComponentName
import com.yoavst.quickapps.R
import com.mobsandgeeks.ake.devicePolicyManager
import android.provider.Settings

/**
 * Created by Yoav.
 */
public class LaunchAdminActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val devicePolicyManager = devicePolicyManager() as DevicePolicyManager
        if (!devicePolicyManager.isAdminActive(ComponentName(this, javaClass<AdminListener>()))) {
            startActivity(Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
                    .putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, ComponentName(this, javaClass<AdminListener>()))
                    .putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, getString(R.string.add_admin_extra_app_text)))
        } else {
            startActivity(Intent().setComponent(ComponentName("com.android.settings", "com.android.settings.DeviceAdminSettings")))
        }
        finish()
    }
}