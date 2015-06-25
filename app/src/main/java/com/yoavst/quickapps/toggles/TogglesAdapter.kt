package com.yoavst.quickapps.toggles

import android.app.Fragment
import android.app.FragmentManager
import android.content.Context
import android.support.v13.app.FragmentPagerAdapter
import com.yoavst.quickapps.tools.showBatteryToggle


public class TogglesAdapter(fm: FragmentManager, context: Context) : FragmentPagerAdapter(fm) {
    val showBattery = context.showBatteryToggle
    val items: List<ToggleItem> = arrayListOf()

    override fun getItem(position: Int): Fragment {
        return getFragment(0)
    }

    fun getFragment(id: Int): Fragment {
        throw Exception()
    }

    override fun getCount(): Int = items.size() + if (showBattery) 1 else 0


}