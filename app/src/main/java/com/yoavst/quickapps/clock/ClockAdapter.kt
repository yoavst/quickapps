package com.yoavst.quickapps.clock;

import android.app.Fragment
import android.app.FragmentManager
import android.support.v13.app.FragmentPagerAdapter

/**
 * Created by Yoav.
 */
public class ClockAdapter(fm: FragmentManager, val stopwatch: String, val timer: String) : FragmentPagerAdapter(fm) {
    override fun getItem(i: Int): Fragment {
        return if (i == 0) StopwatchFragment() else TimerFragment()
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return if (position == 0) stopwatch else timer
    }

    override fun getCount(): Int = 2
}