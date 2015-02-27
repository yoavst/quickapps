package com.yoavst.quickapps.toggles.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Resources
import android.net.NetworkInfo
import android.net.wifi.ScanResult
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Handler
import android.provider.Settings

import com.yoavst.quickapps.R
import com.yoavst.quickapps.toggles.CTogglesActivity
import com.yoavst.quickapps.toggles.Connectivity
import com.yoavst.quickapps.toggles.ToggleFragment
import kotlin.properties.Delegates
import com.mobsandgeeks.ake.wifiManager
import com.yoavst.util.r

/**
 * Created by Yoav.
 */
public class WifiFragment : ToggleFragment() {
    val WIFI: String by Delegates.lazy { getString(R.string.wifi) }
    val WIFI_OFF: String by Delegates.lazy { getString(R.string.wifi_off) }
    val WIFI_ON: String by Delegates.lazy { getString(R.string.wifi_on) }
    val WIFI_NETWORK_AVAILABLE: String by Delegates.lazy { getString(R.string.wifi_network_available) }
    val WIFI_CONNECTED: String by Delegates.lazy { getString(R.string.wifi_connected) }
    val WIFI_TURN_OFF: String by Delegates.lazy { getString(R.string.wifi_turning_off) }
    val WIFI_TURN_ON: String by Delegates.lazy { getString(R.string.wifi_turning_on) }
    val wifiManager: WifiManager by Delegates.lazy { getActivity().wifiManager() }
    val systemUiResources: Resources by Delegates.lazy { (getActivity() as CTogglesActivity).getSystemUiResource() }
    var mWifiReceiver: BroadcastReceiver? = null

    override fun init() {
        toggleTitle.setText(WIFI)
        if (wifiOnIcon == -1 || wifiOffIcon == -1) {
            wifiOffIcon = systemUiResources.getIdentifier("indi_noti_wifi_off", "drawable", "com.android.systemui")
            wifiOnIcon = systemUiResources.getIdentifier("indi_noti_wifi_on", "drawable", "com.android.systemui")
        }
        if (mWifiReceiver == null) {
            mWifiReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    setToggleData()
                }
            }
        }
        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
        getActivity().registerReceiver(mWifiReceiver, intentFilter)
        setToggleData()
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            getActivity().unregisterReceiver(mWifiReceiver)
        } catch (ignored: Exception) {
            // Do nothing - receiver not registered
        }

    }

    fun setToggleData() {
        if (wifiManager.isWifiEnabled()) {
            setToggleConnected()
        } else {
            setToggleDisconnected()
        }
    }

    fun setToggleConnected() {
        toggleIcon.setImageDrawable(systemUiResources.getDrawable(wifiOnIcon))
        val networkName = getWifiNetworkName()
        // Mean that it is not connected to a network, but wifi is on
        if (networkName == null || networkName == "<unknown ssid>") {
            val results = wifiManager.getScanResults()
            var bestSignalAndPublic: ScanResult? = null
            for (result in results) {
                if (result.capabilities == "[WPS][ESS]" || result.capabilities == "[ESS]" &&
                        (bestSignalAndPublic == null || WifiManager.compareSignalLevel(bestSignalAndPublic!!.level, result.level) < 0))
                    bestSignalAndPublic = result
            }
            if (bestSignalAndPublic == null) {
                toggleText.setText(WIFI_ON)
            } else
                toggleText.setText(WIFI_NETWORK_AVAILABLE)
        } else {
            toggleText.setText(WIFI_CONNECTED.format(networkName))
        }
    }

    fun setToggleDisconnected() {
        toggleIcon.setImageDrawable(systemUiResources.getDrawable(wifiOffIcon))
        toggleText.setText(WIFI_OFF)

    }

    override fun onToggleButtonClicked() {
        if (wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(false)
            toggleIcon.setImageDrawable(systemUiResources.getDrawable(wifiOffIcon))
            toggleText.setText(WIFI_TURN_OFF)
        } else {
            if (Connectivity.isApOn(getActivity())) {
                Connectivity.configApState(getActivity())
                Handler().postDelayed(r {toggleOn() } , 400)
            } else
                toggleOn()
            toggleIcon.setImageDrawable(systemUiResources.getDrawable(wifiOnIcon))
            toggleText.setText(WIFI_TURN_ON)
        }
    }

    private fun toggleOn() {
        wifiManager.setWifiEnabled(true)

    }

    override fun getIntentForLaunch(): Intent {
        return Intent(Settings.ACTION_WIFI_SETTINGS)
    }

    public fun getWifiNetworkName(): String? {
        if (wifiManager.isWifiEnabled()) {
            val wifiInfo = wifiManager.getConnectionInfo()
            if (wifiInfo != null) {
                val state = WifiInfo.getDetailedStateOf(wifiInfo.getSupplicantState())
                if (state == NetworkInfo.DetailedState.CONNECTED || state == NetworkInfo.DetailedState.OBTAINING_IPADDR) {
                    return wifiInfo.getSSID()
                }
            }
        }
        return null
    }

    class object {
        // resources id of system ui stuff
        var wifiOffIcon = -1
        var wifiOnIcon = -1
    }
}
