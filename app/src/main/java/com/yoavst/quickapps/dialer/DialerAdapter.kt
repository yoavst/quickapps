package com.yoavst.quickapps.dialer

import android.app.FragmentManager
import android.content.Context
import android.support.v13.app.FragmentPagerAdapter
import android.app.Fragment

/**
 * Created by Yoav.
 */
public class DialerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getItem(i: Int): Fragment {
        return if (i == 0) DialerFragment() else ContactsFragment()
    }

    override fun getCount(): Int = 2
}
