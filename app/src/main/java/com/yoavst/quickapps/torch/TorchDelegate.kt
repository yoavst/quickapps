package com.yoavst.quickapps.torch

import android.animation.Animator
import android.app.Activity
import android.os.Handler
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import com.yoavst.kotlin.*
import com.yoavst.quickapps.R
import com.yoavst.quickapps.tools.BaseAnimationListener
import com.yoavst.quickapps.tools.autoStartTorch
import com.yoavst.quickapps.tools.getBackgroundDrawable
import kotlin.properties.Delegates

/**
 * Helper class for managing the user interface of the various activities of torch. All the activities has the same layout file,
 * So the helper class only needs the views. It show animations only on lollipop and above.
 */
public class TorchDelegate(private val activity: Activity, private val offIcon: ImageView, private val offIconAnimation: ProgressBar?,
                           private val offLayout: FrameLayout, private val onLayout: FrameLayout) {
    private var lock = false
    // Animation
    private val cx by Delegates.lazy { (onLayout.getLeft() + onLayout.getRight()) / 2 }
    private val cy by Delegates.lazy { (onLayout.getTop() + onLayout.getBottom()) / 2 }
    private val finalRadius by Delegates.lazy { Math.max(onLayout.getWidth(), onLayout.getHeight()) }

    /**
     * initialize the views and set the on click callback for them.
     */
    public fun init() {
        offLayout.setBackground(getBackgroundDrawable(activity.colorRes(R.color.torch_background_color_off),
                activity.colorRes(R.color.ripple_material_dark)))
        onLayout.setBackground(getBackgroundDrawable(activity.colorRes(R.color.torch_background_color_on),
                activity.colorRes(R.color.ripple_material_light)))
        offLayout.setOnClickListener { toggleTorch() }
        onLayout.setOnClickListener { toggleTorch() }
    }

    /**
     * Toggle the torch and updates the UI.
     */
    public fun toggleTorch() {
        if (!lock) {
            lock = true
            if (CameraManager.isTorchOn()) {
                CameraManager.disableTorch()
                if (beforeLollipop())
                    showTorchOff()
                else showTorchOffAnimation()
            } else {
                CameraManager.torch()
                if (beforeLollipop()) {
                    showTorchOn()
                } else showTorchOnAnimation()
            }
        }
    }

    /**
     * Updates the UI based on the current mode of the torch.
     */
    public fun showCurrentMode() {
        if (CameraManager.isTorchOn()) {
            showTorchOn()
            CameraManager.torch()
        } else {
            showTorchOff()
            if (activity.autoStartTorch)
                toggleTorch()
        }
    }

    /**
     * Animates the torch off.
     */
    private fun showTorchOffAnimation() {
        val anim = ViewAnimationUtils.createCircularReveal(onLayout, cx, cy, finalRadius.toFloat(), 0F)
        anim.addListener(object : BaseAnimationListener() {
            override fun onAnimationEnd(animation: Animator) {
                showTorchOff()
                lock = false
            }

        })
        anim.start()
    }

    /**
     * Showing the torch off.
     */
    private fun showTorchOff() {
        onLayout.setVisibility(View.INVISIBLE)
    }

    /**
     * Animates the torch on.
     */
    private fun showTorchOnAnimation() {
        // create the animator for this view (the start radius is zero)
        offIconAnimation!!.show()
        offIcon.hide()
        Handler().postDelayed(500) {
            if (onLayout.isAttachedToWindow()) {
                val anim = ViewAnimationUtils.createCircularReveal(onLayout, cx, cy, 0F, finalRadius.toFloat())
                showTorchOn()
                anim.start()
                anim.addListener(object : BaseAnimationListener() {
                    override fun onAnimationEnd(animation: Animator) {
                        offIconAnimation.hide()
                        offIcon.show()
                        lock = false
                    }
                })
            }
        }
    }

    /**
     * Show torch on.
     */
    private fun showTorchOn() {
        onLayout.show()
    }

}