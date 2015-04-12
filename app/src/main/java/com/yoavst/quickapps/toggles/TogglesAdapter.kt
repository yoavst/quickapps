package com.yoavst.quickapps.toggles

import android.app.Fragment
import android.app.FragmentManager
import android.content.Context
import android.os.Build
import android.support.v13.app.FragmentPagerAdapter

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yoavst.kotlin.lollipopOrNewer
import com.yoavst.quickapps.R

import java.lang.reflect.Type
import java.util.ArrayList
import java.util.Collections
import kotlin.properties.Delegates
import com.yoavst.quickapps.PrefManager
import com.yoavst.quickapps.toggles.fragments.BatteryFragment
import com.yoavst.quickapps.toggles.fragments.DataFragment
import com.yoavst.quickapps.toggles.fragments.BrightnessFragment
import com.yoavst.quickapps.toggles.fragments.SoundFragment
import com.yoavst.quickapps.toggles.fragments.BluetoothFragment
import com.yoavst.quickapps.toggles.fragments.HotSpotFragment
import com.yoavst.quickapps.toggles.fragments.WifiFragment
import com.yoavst.util.typeToken

/**
 * Created by Yoav.
 */
public class TogglesAdapter(fm: FragmentManager, context: Context) : FragmentPagerAdapter(fm) {
    val items: ArrayList<ToggleItem> by Delegates.lazy {
        val prefs = PrefManager(context)
        val localItems = if (prefs.togglesItems().getOr("-1") != "-1")
            Gson().fromJson(prefs.togglesItems().getOr("[]"), listType)
        else
            initDefaultToggles(context)
        if (lollipopOrNewer()) {
            for (i in localItems.indices) {
                if (localItems.get(i).id == 1) {
                    localItems.remove(i)
                    break
                }
            }
        }
        localItems
    }
    val showBattery  by Delegates.lazy { PrefManager(context).showBatteryToggle().getOr(true) }


    override fun getItem(i: Int): Fragment {
        if ((i == 0) and showBattery)
            return BatteryFragment()
        else
            return getOriginalItem(items.get(if (showBattery) i - 1 else i).id + 1)
    }

    public fun getOriginalItem(i: Int): Fragment {
        when (i) {
            2 -> return DataFragment()
            3 -> return BrightnessFragment()
            4 -> return SoundFragment()
            5 -> return BluetoothFragment()
            6 -> return HotSpotFragment()
            else -> return WifiFragment()
        }
    }

    override fun getCount(): Int {
        return if (showBattery) items.size() + 1 else items.size()
    }

    public class ToggleItem(public var name: String, public var id: Int)

    class object {
        public var listType: Type = typeToken<ArrayList<ToggleItem>>()

        public fun initDefaultToggles(context: Context): ArrayList<ToggleItem> {
            val toggles = context.getResources().getStringArray(R.array.toggles)
            val items = ArrayList<ToggleItem>(toggles.size)
            for (i in toggles.indices) {
                items.add(ToggleItem(toggles[i], i))
            }
            PrefManager(context).togglesItems().put(Gson().toJson(items, listType))
            return items
        }

        public fun addNewToggles(context: Context, vararg items: ToggleItem): ArrayList<ToggleItem> {
            val prefs = PrefManager(context)
            val listItems: ArrayList<ToggleItem>
            if (prefs.togglesItems().getOr("-1") == "-1")
                listItems = initDefaultToggles(context)
            else
                listItems = Gson().fromJson(prefs.togglesItems().getOr("[]"), listType)
            if (items.size() != 0) {
                Collections.addAll(listItems, *items)
                prefs.togglesItems().put(Gson().toJson(items, listType))
            }
            return listItems
        }
    }
}
