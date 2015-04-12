package com.yoavst.quickapps.toggles.fragments

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Resources
import android.provider.Settings
import com.yoavst.kotlin.stringResource

import com.yoavst.quickapps.R
import com.yoavst.quickapps.toggles.ToggleFragment
import com.yoavst.quickapps.toggles.CTogglesActivity

import kotlin.properties.Delegates

/**
 * Created by Yoav.
 */
public class BluetoothFragment : ToggleFragment() {
    val BLUETOOTH by stringResource(R.string.bluetooth)
    val BLUETOOTH_OFF by stringResource(R.string.bluetooth_off)
    val BLUETOOTH_ON by stringResource(R.string.bluetooth_on)
    val BLUETOOTH_TURN_OFF by stringResource(R.string.bluetooth_turning_off)
    val BLUETOOTH_TURN_ON by stringResource(R.string.bluetooth_turning_on)
    val systemUiResources: Resources by Delegates.lazy { (getActivity() as CTogglesActivity).getSystemUiResource() }
    var mBluetoothReceiver: BroadcastReceiver? = null

    override fun init() {
        toggleTitle.setText(BLUETOOTH)
        if (mBluetoothOnIcon == -1 || mBluetoothOffIcon == -1) {
            mBluetoothOffIcon = systemUiResources.getIdentifier("indi_noti_bluetooth_off", "drawable", "com.android.systemui")
            mBluetoothOnIcon = systemUiResources.getIdentifier("indi_noti_bluetooth_on", "drawable", "com.android.systemui")
        }
        if (mBluetoothReceiver == null) {
            mBluetoothReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    setToggleData()
                }
            }
        }
        val intentFilter = IntentFilter()
        intentFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        getActivity().registerReceiver(mBluetoothReceiver, intentFilter)
        setToggleData()
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            getActivity().unregisterReceiver(mBluetoothReceiver)
        } catch (ignored: Exception) {
            // Do nothing - receiver not registered
        }

    }

    fun setToggleData() {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter.isEnabled()) {
            setToggleConnected()
        } else {
            setToggleDisconnected()
        }
    }

    fun setToggleConnected() {
        toggleIcon.setImageDrawable(systemUiResources.getDrawable(mBluetoothOnIcon))
        toggleText.setText(BLUETOOTH_ON)
    }

    fun setToggleDisconnected() {
        toggleIcon.setImageDrawable(systemUiResources.getDrawable(mBluetoothOffIcon))
        toggleText.setText(BLUETOOTH_OFF)

    }

    override fun onToggleButtonClicked() {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.disable()
            toggleIcon.setImageDrawable(systemUiResources.getDrawable(mBluetoothOffIcon))
            toggleText.setText(BLUETOOTH_TURN_OFF)


        } else {
            bluetoothAdapter.enable()
            toggleIcon.setImageDrawable(systemUiResources.getDrawable(mBluetoothOnIcon))
            toggleText.setText(BLUETOOTH_TURN_ON)
        }
    }

    override fun getIntentForLaunch(): Intent {
        return Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
    }

    class object {
        // resources id of system ui stuff
        var mBluetoothOffIcon = -1
        var mBluetoothOnIcon = -1
    }
}
