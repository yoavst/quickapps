package com.yoavst.quickapps.toggles

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.provider.Settings
import android.telephony.TelephonyManager
import com.yoavst.kotlin.connectivityManager
import com.yoavst.kotlin.wifiManager

/**
 * Check device's network connectivity and speed
 *
 * @author emil http://stackoverflow.com/users/220710/emil
 */
public object Connectivity {

    public fun getNetworkInfo(context: Context): NetworkInfo? = context.connectivityManager().getActiveNetworkInfo()

    public fun isConnected(context: Context): Boolean {
        val info = Connectivity.getNetworkInfo(context)
        return (info != null && info.isConnected())
    }

    public fun isConnectedWifi(context: Context): Boolean {
        val info = Connectivity.getNetworkInfo(context)
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_WIFI)
    }

    public fun isConnectedMobile(context: Context): Boolean {
        val info = Connectivity.getNetworkInfo(context)
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_MOBILE)
    }

    public fun isConnectedFast(context: Context): Boolean {
        val info = Connectivity.getNetworkInfo(context)
        return (info != null && info.isConnected() && Connectivity.isConnectionFast(info.getType(), info.getSubtype()))
    }

    public fun isConnectionFast(type: Int, subType: Int): Boolean {
        if (type == ConnectivityManager.TYPE_WIFI) {
            return true
        } else if (type == ConnectivityManager.TYPE_MOBILE) {
            when (subType) {
                TelephonyManager.NETWORK_TYPE_1xRTT -> return false // ~ 50-100 kbps
                TelephonyManager.NETWORK_TYPE_CDMA -> return false // ~ 14-64 kbps
                TelephonyManager.NETWORK_TYPE_EDGE -> return false // ~ 50-100 kbps
                TelephonyManager.NETWORK_TYPE_EVDO_0 -> return true // ~ 400-1000 kbps
                TelephonyManager.NETWORK_TYPE_EVDO_A -> return true // ~ 600-1400 kbps
                TelephonyManager.NETWORK_TYPE_GPRS -> return false // ~ 100 kbps
                TelephonyManager.NETWORK_TYPE_HSDPA -> return true // ~ 2-14 Mbps
                TelephonyManager.NETWORK_TYPE_HSPA -> return true // ~ 700-1700 kbps
                TelephonyManager.NETWORK_TYPE_HSUPA -> return true // ~ 1-23 Mbps
                TelephonyManager.NETWORK_TYPE_UMTS -> return true // ~ 400-7000 kbps
                TelephonyManager.NETWORK_TYPE_EHRPD -> return true // ~ 1-2 Mbps
                TelephonyManager.NETWORK_TYPE_EVDO_B -> return true // ~ 5 Mbps
                TelephonyManager.NETWORK_TYPE_HSPAP -> return true // ~ 10-20 Mbps
                TelephonyManager.NETWORK_TYPE_IDEN -> return false // ~25 kbps
                TelephonyManager.NETWORK_TYPE_LTE -> return true // ~ 10+ Mbps
                else -> return false
            }
        } else {
            return false
        }
    }

    public fun isAirplaneMode(context: Context): Boolean {
        return Settings.Global.getString(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON).toInt() != 0
    }

    public fun isApOn(context: Context): Boolean {
        val wifiManager = context.wifiManager()
        try {
            val method = javaClass<WifiManager>().getDeclaredMethod("isWifiApEnabled")
            method.setAccessible(true)
            return method.invoke(wifiManager) as Boolean
        } catch (ignored: Throwable) {
        }
        return false
    }

    //turn on/off wifi hotspot as toggle
    public fun configApState(context: Context): Boolean {
        val wifiManager = context.wifiManager()
        val wifiConfiguration: WifiConfiguration? = null
        val isApOn = isApOn(context)
        try {
            if (!isApOn) {
                //turn off whether wifi is on
                wifiManager.setWifiEnabled(false)
            }
            val method = wifiManager.javaClass.getMethod("setWifiApEnabled", javaClass<WifiConfiguration>(), java.lang.Boolean.TYPE)
            method.invoke(wifiManager, wifiConfiguration, !isApOn)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false

    }

}