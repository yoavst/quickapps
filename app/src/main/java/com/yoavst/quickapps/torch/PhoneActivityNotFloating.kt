package com.yoavst.quickapps.torch

import android.app.Activity
import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import com.yoavst.kotlin.systemService
import com.yoavst.quickapps.R
import kotlinx.android.synthetic.torch_activity.offIcon
import kotlinx.android.synthetic.torch_activity.offIconAnimation
import kotlinx.android.synthetic.torch_activity.offLayout
import kotlinx.android.synthetic.torch_activity.onLayout
import kotlin.properties.Delegates

/**
 * The regular torch activity.
 */
public class PhoneActivityNotFloating : Activity() {
    val notificationManager: NotificationManager by systemService()
    val delegation: TorchDelegate by Delegates.lazy { TorchDelegate(this, offIcon, offIconAnimation, offLayout, onLayout) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.torch_activity)
        delegation.init()
    }

    override fun onDestroy() {
        super.onDestroy()
        CameraManager.destroy()
    }

    override fun onBackPressed() {
        val startMain = Intent(Intent.ACTION_MAIN)
        startMain.addCategory(Intent.CATEGORY_HOME)
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(startMain)
    }

    override fun onPause() {
        super.onPause()
        if (CameraManager.isTorchOn())
            notificationManager.notify(NotificationReceiver.NOTIFICATION_ID, Torch.notification)
        else
            CameraManager.destroy()
    }

    override fun onResume() {
        super.onResume()
        CameraManager(this)
        CameraManager.init()
        Torch(this)
        notificationManager.cancel(NotificationReceiver.NOTIFICATION_ID)
        delegation.showCurrentMode()
    }
}