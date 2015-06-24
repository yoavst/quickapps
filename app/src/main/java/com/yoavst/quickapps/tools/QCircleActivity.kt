package com.yoavst.quickapps.tools

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import com.lge.qcircle.template.*
import com.lge.qcircle.utils.QCircleFeature
import com.yoavst.kotlin.Bundle
import com.yoavst.kotlin.devicePolicyManager
import com.yoavst.kotlin.optionalViewById
import com.yoavst.quickapps.AdminListener
import com.yoavst.quickapps.R
import com.yoavst.quickapps.desktop.LaunchAdminActivity
import java.lang.reflect.Field
import kotlin.properties.Delegates

/**
 * A base class for all quick circle activities.
 * 1. Handle support for G2 comparability mode.
 * 2. Handle support for double tap to sleep (dt2s).
 * 3. Handle support for QCircleView.
 * 4. Handle creating of template and intent on case open.
 */
public abstract class QCircleActivity : AppCompatActivity() {
    protected val template: QCircleTemplate by Delegates.lazy { QCircleTemplate(this, TemplateType.CIRCLE_EMPTY) }
    protected val deviceAdminReceiverComponentName: ComponentName by Delegates.lazy { ComponentName(this, javaClass<AdminListener>()) }
    public val gestureDetector: GestureDetector by Delegates.lazy { GestureDetector(this, SimpleOnGestureListenerWithDoubleTapHandler()) }
    var shouldShowDeviceAdminDialog = false
    var notBecauseOfIntent = true

    /**
     * add support for not LG ROMs
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!isLGRom(this))
            QCircleFeature.setAlternativeValues(Bundle {
                putInt("config_circle_diameter", 1101)
                putInt("config_circle_window_y_pos", 0)
                putInt("config_circle_window_height", 1046)
            })
    }

    /**
     * Handle G2 support and set touch listener for double tap to sleep.
     */
    override fun onStart() {
        super.onStart()
        val circle = optionalViewById<View>(R.id.circle)
        if (circle != null && g2Mode) {
            circle.setVisibility(View.GONE)
        }
        try {
            template.registerIntentReceiver()
        } catch (ignored: Exception) {
        }

        getMainLayout().setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                return gestureDetector.onTouchEvent(event)
            }
        })
        try {
            val title = getField(javaClass<QCircleTemplate>(), "mTitle")
            title.setAccessible(true)
            (title.get(template) as? QCircleTitle)?.getView()?.setOnTouchListener { v, event ->
                gestureDetector.onTouchEvent(event)
            }
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

    /**
     * Unregister the template receiver for events.
     */
    override fun onPause() {
        try {
            super.onPause()
            template.unregisterReceiver()
        } catch (ignored: Exception) {
        }

    }

    public inner class SimpleOnGestureListenerWithDoubleTapHandler : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            return this@QCircleActivity.onSingleTapConfirmed()
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            val devicePolicyManager = devicePolicyManager()!!
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

    /**
     * Show the dialog of require permission if user did not disable it.
     */
    fun requirePermissionForLockTheScreen() {
        if (showDoubleTapDialog) {
            shouldShowDeviceAdminDialog = true
            QCircleDialog.Builder()
                    .setMode(QCircleDialog.DialogMode.YesNo)
                    .setTitle(getString(R.string.missing_permissions))
                    .setText(getString(R.string.error_no_permission_for_lock_the_screen))
                    .setPositiveButtonListener {
                        shouldShowDeviceAdminDialog = false
                    }.setPositiveButtonText(getString(android.R.string.ok))
                    .setNegativeButtonListener {
                        showDoubleTapDialog = false
                        shouldShowDeviceAdminDialog = false
                    }.setNegativeButtonText(getString(R.string.dont_show_again))
                    .create().show(this, template)
        }
    }

    /**
     * Return the intent that should be launched.
     * @return The intent that should be launched.
     */
    fun getIntentToLaunch(): Intent? {
        notBecauseOfIntent = false
        if (shouldShowDeviceAdminDialog) {
            return Intent(this, javaClass<LaunchAdminActivity>())
        } else
            return getIntentToShow()
    }

    /**
     * Returns the main layout of the template.
     * @return The main layout of the template.
     */
    protected fun getMainLayout(): RelativeLayout = template.getLayoutById(TemplateTag.CONTENT_MAIN)

    /**
     * Set content view by id to the main layout of the template.
     */
    protected fun setContentViewToMain(layout: Int) {
        setContentViewToMain(getLayoutInflater().inflate(layout, getMainLayout(), false))
    }
    /**
     * Set the view as content view to the main layout of the template.
     */
    protected fun setContentViewToMain(view: View) {
        getMainLayout().addView(view)
    }

    /**
     * Returns the intent that should be opened on case open.
     * @return The intent that should be opened on case open.
     */
    protected abstract fun getIntentToShow(): Intent?


    protected open fun onSingleTapConfirmed(): Boolean {
        return false
    }

    /**
     * Get field of class using java reflection.
     */
    throws(NoSuchFieldException::class)
    private fun getField(clazz: Class<*>, fieldName: String): Field {
        return clazz.getDeclaredField(fieldName)
    }
}