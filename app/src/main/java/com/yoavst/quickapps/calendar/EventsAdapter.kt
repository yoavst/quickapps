package com.yoavst.quickapps.calendar

import android.app.Fragment
import android.app.FragmentManager
import android.support.v13.app.FragmentStatePagerAdapter


public class EventsAdapter(fm: FragmentManager, val size: Int) : FragmentStatePagerAdapter(fm) {

    override fun getItem(i: Int): Fragment {
        return CalendarFragment.newInstance(i)
    }

    override fun getCount(): Int = size
}

