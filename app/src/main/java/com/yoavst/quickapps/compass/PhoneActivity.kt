package com.yoavst.quickapps.compass

import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.lge.app.floating.FloatableActivity
import com.lge.app.floating.FloatingWindow
import com.yoavst.quickapps.R
import kotlinx.android.synthetic.compass_qslide_activity.needle
import kotlinx.android.synthetic.compass_qslide_activity.windowBackground
import kotlin.properties.Delegates

public class PhoneActivity : FloatableActivity() {
    val compass: Compass by Delegates.lazy { Compass(this, needle) }

    override fun onCreate(savedInstance: Bundle?) {
        super.onCreate(savedInstance)
        setContentView(R.layout.compass_qslide_activity)
    }

    override fun onAttachedToFloatingWindow(w: FloatingWindow) {
        super.onAttachedToFloatingWindow(w)
        val window = getFloatingWindow()
        val layoutParams = window.getLayoutParams()
        layoutParams.width = 720
        layoutParams.height = 720
        layoutParams.resizeOption = FloatingWindow.ResizeOption.PROPORTIONAL
        window.updateLayoutParams(layoutParams)
        val titleText = w.findViewWithTag(FloatingWindow.Tag.TITLE_TEXT) as TextView
        titleText.setText(getString(R.string.compass_module_name))
        val titleBackground = window.findViewWithTag(FloatingWindow.Tag.TITLE_BACKGROUND)
        if (titleBackground != null) {
            windowBackground.setBackground(titleBackground.getBackground().getConstantState().newDrawable())
        }
        val fullscreenButton = w.findViewWithTag(FloatingWindow.Tag.FULLSCREEN_BUTTON) as? ImageButton
        if (fullscreenButton != null) {
            fullscreenButton.getParent() as ViewGroup removeView fullscreenButton
        }
    }

    override fun onPause() {
        super.onPause()
        if (!isSwitchingToFloatingMode())
            compass.unregisterService()
    }

    override fun onResume() {
        super.onResume()
        compass.registerService()
    }
}
