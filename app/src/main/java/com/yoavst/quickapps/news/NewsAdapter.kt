package com.yoavst.quickapps.news

import android.app.Fragment
import android.app.FragmentManager
import android.support.v13.app.FragmentPagerAdapter
import com.yoavst.kotlin.e

import com.yoavst.quickapps.news.types.Entry


import java.util.ArrayList

/**
 * Created by Yoav.
 */
public class NewsAdapter(fm: FragmentManager, val size: Int) : FragmentPagerAdapter(fm) {

    override fun getItem(i: Int): Fragment {
        return NewsFragment.newInstance(i)
    }

    override fun getCount(): Int = size
}
