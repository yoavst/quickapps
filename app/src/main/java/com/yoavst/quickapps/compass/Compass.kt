package com.yoavst.quickapps.compass

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView

/**
 * A class that get the updates on the orientation sensor and and rotate
 * the needle according to the change.
 * @author Marco Kirchner
 */
public class Compass(context: Context, var mNeedle: ImageView) : SensorEventListener {
    private var currentDegree = 0.toFloat()
    private val mSensorManager: SensorManager

    init {
        mSensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    override fun onSensorChanged(sensorEvent: SensorEvent) {
        // get the angle around the z-axis rotated
        val degree = Math.round(sensorEvent.values[0]).toFloat()
        // create a rotation animation (reverse turn degree degrees)
        val ra = RotateAnimation(currentDegree, -degree, Animation.RELATIVE_TO_SELF, 0.5F, Animation.RELATIVE_TO_SELF, 0.5F)
        // how long the animation will take place
        ra.setDuration(210)
        // set the animation after the end of the reservation status
        ra.setFillAfter(true)
        // Start the animation
        mNeedle.startAnimation(ra)
        currentDegree = -degree
    }

    public fun registerService() {
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_GAME)
    }

    public fun unregisterService() {
        mSensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor, i: Int) {
    }
}
