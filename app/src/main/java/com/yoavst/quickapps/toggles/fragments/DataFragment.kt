package com.yoavst.quickapps.toggles.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Resources
import android.net.ConnectivityManager
import android.provider.Settings
import com.yoavst.kotlin.connectivityManager

import com.yoavst.quickapps.R
import com.yoavst.quickapps.toggles.Connectivity
import com.yoavst.quickapps.toggles.ToggleFragment
import com.yoavst.quickapps.toggles.CTogglesActivity

import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import kotlin.properties.Delegates
import com.yoavst.kotlin.stringResource
import com.yoavst.kotlin.systemService

/**
 * Created by Yoav.
 */
public class DataFragment : ToggleFragment() {
    val DATA by stringResource(R.string.data_data)
    val DATA_OFF by stringResource(R.string.data_off)
    val DATA_OFF_AIRPLANE by stringResource(R.string.data_off_airplane)
    val DATA_ON by stringResource(R.string.data_on)
    val systemUiResources: Resources by Delegates.lazy { (getActivity() as CTogglesActivity).getSystemUiResource() }
    val connectivityManager: ConnectivityManager by Delegates.lazy { getActivity().connectivityManager() }
    var mDataReceiver: BroadcastReceiver? = null

    override fun init() {
        toggleTitle.setText(DATA)
        if (dataOnIcon == -1 || dataOffIcon == -1 || dataForcedOffIcon == -1) {
            dataOnIcon = systemUiResources.getIdentifier("indi_noti_data_on", "drawable", "com.android.systemui")
            dataOffIcon = systemUiResources.getIdentifier("indi_noti_data_off", "drawable", "com.android.systemui")
            dataForcedOffIcon = systemUiResources.getIdentifier("indi_noti_data_disable_normal_vzw", "drawable", "com.android.systemui")
        }
        if (mDataReceiver == null) {
            mDataReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    setToggleData()
                }
            }
        }
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        getActivity().registerReceiver(mDataReceiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            getActivity().unregisterReceiver(mDataReceiver)
        } catch (ignored: Exception) {
            // Do nothing - receiver not registered
        }

    }

    fun setToggleData() {
        if (isMobileDataEnabled()) {
            toggleIcon.setImageDrawable(systemUiResources.getDrawable(dataOnIcon))
            toggleText.setText(DATA_ON)
        } else {
            if (Connectivity.isAirplaneMode(getActivity())) {
                toggleIcon.setImageDrawable(systemUiResources.getDrawable(dataForcedOffIcon))
                toggleText.setText(DATA_OFF_AIRPLANE)
            } else {
                toggleIcon.setImageDrawable(systemUiResources.getDrawable(dataOffIcon))
                toggleText.setText(DATA_OFF)
            }
        }
    }

    override fun onToggleButtonClicked() {
        if (!Connectivity.isAirplaneMode(getActivity())) {
            val newState = !isMobileDataEnabled()
            setMobileDataEnabled(newState)
            toggleText.setText(if (newState) DATA_ON else DATA_OFF)
            toggleIcon.setImageDrawable(if (newState) systemUiResources.getDrawable(dataOnIcon) else systemUiResources.getDrawable(dataOffIcon))
        }
    }

    override fun getIntentForLaunch(): Intent {
        return Intent(Settings.ACTION_DATA_ROAMING_SETTINGS)
    }

    private fun setMobileDataEnabled(enabled: Boolean) {
        try {
            val method = javaClass<ConnectivityManager>().getDeclaredMethod("setMobileDataEnabled", java.lang.Boolean.TYPE)
            method.setAccessible(true)
            method.invoke(connectivityManager, enabled)
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }

    }

    private fun isMobileDataEnabled(): Boolean {
        if (Connectivity.isAirplaneMode(getActivity())) return false
        var mobileDataEnabled = false // Assume disabled
        try {
            val cmClass = Class.forName(connectivityManager.javaClass.getName())
            val method = cmClass.getDeclaredMethod("getMobileDataEnabled")
            method.setAccessible(true) // Make the method callable
            // get the setting for "mobile data"
            mobileDataEnabled = method.invoke(connectivityManager) as Boolean
        } catch (e: Exception) {
            // Some problem accessible private API
            // TODO do whatever error handling you want here
        }

        return mobileDataEnabled
    }

    class object {
        // resources id of system ui stuff
        var dataOnIcon = -1
        var dataOffIcon = -1
        var dataForcedOffIcon = -1
    }

}
