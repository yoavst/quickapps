package com.yoavst.quickapps.toggles.toggles

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.IntentFilter
import android.provider.Settings
import com.yoavst.kotlin.*
import com.yoavst.quickapps.R
import com.yoavst.quickapps.toggles.ToggleFragment
import kotlinx.android.synthetic.toggles_toggle_fragment.image
import kotlinx.android.synthetic.toggles_toggle_fragment.imageAnimation
import kotlinx.android.synthetic.toggles_toggle_fragment.text
import kotlin.properties.Delegates

public class BluetoothFragment : ToggleFragment() {
    val BluetoothOff by stringResource(R.string.bluetooth_off)
    val BluetoothOn by stringResource(R.string.bluetooth_on)
    val BluetoothTurnOff by stringResource(R.string.bluetooth_turning_off)
    val BluetoothTurnOn by stringResource(R.string.bluetooth_turning_on)
    val bluetoothAdapter by Delegates.lazy { BluetoothAdapter.getDefaultAdapter() }
    var lock = false

    val receiver = broadcastReceiver { context, intent ->
        setToggleData()
    }

    override fun getIntentForLaunch(): Intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)

    override fun init() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        getActivity().registerReceiver(receiver, intentFilter)
        image.setImageResource(R.drawable.ic_bluetooth)
        imageAnimation?.setIndeterminateDrawable(drawableRes(R.drawable.animated_bluetooth))
        setToggleData()
    }

    override fun getTitle(): CharSequence = getString(R.string.bluetooth)

    override fun onToggleButtonClicked() {
        if (!lock) {
            lock = true
            if (imageAnimation != null) {
                image.hide()
                imageAnimation.show()
            }
            if (bluetoothAdapter.isEnabled()) {
                bluetoothAdapter.disable()
                text.setText(BluetoothTurnOff)
            } else {
                bluetoothAdapter.enable()
                text.setText(BluetoothTurnOn)
            }
        }
    }


    fun setToggleData() {
        lock = false
        if (bluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
            setToggleBackgroundOn()
            image.show()
            imageAnimation?.hide()
            text.setText(BluetoothOn)
        } else if (bluetoothAdapter.getState() == BluetoothAdapter.STATE_OFF) {
            setToggleBackgroundOff()
            image.show()
            imageAnimation?.hide()
            text.setText(BluetoothOff)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            getActivity().unregisterReceiver(receiver)
        } catch (ignored: Exception) {
            // Do nothing - receiver not registered
        }

    }
}