package com.yoavst.quickapps.toggles

import android.app.Fragment
import android.app.FragmentManager
import android.content.Context
import android.support.v13.app.FragmentPagerAdapter
import com.google.gson.Gson
import com.yoavst.quickapps.R
import com.yoavst.quickapps.toggles.toggles.*
import com.yoavst.quickapps.tools.showBatteryToggle
import com.yoavst.quickapps.tools.togglesItems
import com.yoavst.quickapps.tools.typeToken
import java.lang.reflect.Type
import java.util.ArrayList


public class TogglesAdapter(fm: FragmentManager, context: Context) : FragmentPagerAdapter(fm) {
    val showBattery = context.showBatteryToggle
    val items: List<ToggleItem>

    init {
        val localItems = if (context.togglesItems != "-1")
            Gson().fromJson(context.togglesItems, listType)
        else
            initDefaultToggles(context)
        items = localItems
    }

    override fun getItem(position: Int): Fragment {
        if (showBattery && position == 0) return BatteryFragment()
        else return getFragment(items[if (showBattery) position - 1 else position].id)
    }

    fun getFragment(id: Int): Fragment {
        return when (id) {
            ToggleItem.Bluetooth -> BluetoothFragment()
            ToggleItem.Brightness -> BrightnessFragment()
            ToggleItem.Hotspot -> HotspotFragment()
            ToggleItem.Sound -> SoundFragment()
            ToggleItem.Wifi -> WifiFragment()
            else -> Fragment()
        }
    }

    override fun getCount(): Int = items.size() + if (showBattery) 1 else 0

    companion object {
        public var listType: Type = typeToken<ArrayList<ToggleItem>>()

        public fun initDefaultToggles(context: Context): ArrayList<ToggleItem> {
            val toggles = context.getResources().getStringArray(R.array.toggles)
            val items = ArrayList<ToggleItem>(toggles.size())
            for (i in toggles.indices) {
                items.add(ToggleItem(i, toggles[i]))
            }
            context.togglesItems = Gson().toJson(items, listType)
            return items
        }
    }
}