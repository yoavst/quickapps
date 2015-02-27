package com.yoavst.quickapps.calculator

import android.app.FragmentManager
import android.content.Context
import android.support.v13.app.FragmentPagerAdapter
import android.app.Fragment

/**
 * Created by Yoav.
 */
public class CalculatorAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getItem(i: Int): Fragment {
        return RegularFragment()
    }

    override fun getCount(): Int {
        return 1
    }
}