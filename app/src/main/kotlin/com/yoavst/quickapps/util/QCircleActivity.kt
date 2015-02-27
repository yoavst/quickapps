package com.yoavst.quickapps.util

import android.app.Activity
import com.lge.qcircle.template.QCircleTemplate
import android.content.ComponentName
import com.yoavst.quickapps.PrefManager
import android.view.GestureDetector
import com.yoavst.quickapps.AdminListener
import android.view.View
import android.view.MotionEvent
import com.lge.qcircle.template.QCircleTitle
import android.content.Intent
import android.app.admin.DevicePolicyManager
import android.widget.Button
import com.yoavst.quickapps.desktop.LaunchAdminActivity
import java.lang.reflect.Field
import com.yoavst.quickapps.R
import com.lge.qcircle.template.TemplateTag
import android.util.Log
import android.content.Context
import com.lge.qcircle.template.QCircleDialog
import android.util.TypedValue
import android.view.Gravity
import kotlin.properties.Delegates
import com.mobsandgeeks.ake.optionalViewById
import com.mobsandgeeks.ake.hide
import com.mobsandgeeks.ake.devicePolicyManager

/**
 * Created by Yoav.
 */
public abstract class QCircleActivity : Activity() {
    protected abstract val template: QCircleTemplate
    protected val deviceAdminReceiverComponentName: ComponentName by Delegates.lazy { ComponentName(this, javaClass<AdminListener>()) }
    protected val preferences: PrefManager by Delegates.lazy { PrefManager(this) }
    public val gestureDetector: GestureDetector by Delegates.lazy { GestureDetector(this, SimpleOnGestureListenerWithDoubleTapHandler()) }
    var shouldShowDeviceAdminDialog = false

    override fun onStart() {
        super.onStart()
        val circle = optionalViewById<View>(R.id.circle)
        if (circle != null && PrefManager(this).g2Mode().getOr(false))
            circle.hide()
        try {
            template.registerIntentReceiver()
        } catch (ignored: Exception) {
        }

        template.getLayoutById(TemplateTag.CONTENT_MAIN).setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                return gestureDetector.onTouchEvent(event)
            }
        })
        try {
            val title = getField(javaClass<QCircleTemplate>(), "mTitle")
            title.setAccessible(true)
            val qCircleTitle = (title.get(template) as? QCircleTitle)
            if (qCircleTitle != null)
                qCircleTitle.getView().setOnTouchListener(object : View.OnTouchListener {
                    override fun onTouch(v: View, event: MotionEvent): Boolean {
                        return gestureDetector.onTouchEvent(event)
                    }
                })
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }

        template.setFullscreenIntent(object : QCircleTemplate.IntentCreatorAsync {
            override fun getIntent(): Intent? {
                return getIntentToLaunch()
            }
        })

    }

    override fun onPause() {
        try {
            super.onPause()
            template.unregisterReceiver()
        } catch (ignored: Exception) {
        }

    }

    public inner class SimpleOnGestureListenerWithDoubleTapHandler : GestureDetector.SimpleOnGestureListener() {
        private val TAG = javaClass<SimpleOnGestureListenerWithDoubleTapHandler>().getSimpleName()

        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            return this@QCircleActivity.onSingleTapConfirmed()
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            Log.d(TAG, "onDoubleTap")
            val devicePolicyManager = devicePolicyManager() as DevicePolicyManager
            if (devicePolicyManager.isAdminActive(deviceAdminReceiverComponentName)) {
                devicePolicyManager.lockNow()
                finish()
            } else {
                requirePermissionForLockTheScreen()
            }
            return true
        }

        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onDoubleTapEvent(e: MotionEvent): Boolean {
            return false
        }
    }

    fun requirePermissionForLockTheScreen() {
        if (preferences.showDoubleTapDialog().getOr(true)) {
            shouldShowDeviceAdminDialog = true
            val builder = QCircleDialog.Builder().setMode(QCircleDialog.DialogMode.YesNo).setTitle(getString(R.string.missing_permissions)).setText(getString(R.string.error_no_permission_for_lock_the_screen))
            builder.setPositiveButtonListener(object : View.OnClickListener {
                override fun onClick(v: View) {
                    shouldShowDeviceAdminDialog = false
                }
            }).setPositiveButtonText(getString(android.R.string.ok))
            builder.setNegativeButtonListener(object : View.OnClickListener {
                override fun onClick(v: View) {
                    preferences.showDoubleTapDialog().put(false)
                    shouldShowDeviceAdminDialog = false
                }
            }).setNegativeButtonText(getString(R.string.dont_show_again))
            builder.create().show(this, template)
            val negative = (findViewById(R.id.negative) as Button)
            val params = (negative.getParent() as View).getLayoutParams()
            params.height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80F, getResources().getDisplayMetrics()).toInt()
            (negative.getParent() as View).setLayoutParams(params)
            negative.setGravity(Gravity.START or Gravity.CENTER_VERTICAL)
            (findViewById(R.id.positive) as Button).setGravity(Gravity.CENTER)
        }
    }

    fun getIntentToLaunch(): Intent? {
        if (shouldShowDeviceAdminDialog) {
            return Intent(this, javaClass<LaunchAdminActivity>())
        } else
            return getIntentToShow()
    }

    protected abstract fun getIntentToShow(): Intent?

    protected open fun onSingleTapConfirmed(): Boolean {
        return false
    }

    throws(javaClass<NoSuchFieldException>())
    private fun getField(clazz: Class<*>, fieldName: String): Field {
            return clazz.getDeclaredField(fieldName)
    }
}