package com.yoavst.quickapps.news

import android.app.Fragment
import android.app.FragmentManager
import android.support.v13.app.FragmentPagerAdapter

import com.yoavst.quickapps.news.types.Entry


import java.util.ArrayList

/**
 * Created by Yoav.
 */
public class NewsAdapter(fm: FragmentManager, entries: ArrayList<Entry>) : FragmentPagerAdapter(fm) {
    {
        NewsAdapter.entries = entries
    }

    override fun getItem(i: Int): Fragment {
        return NewsFragment.newInstance(i)
    }

    override fun getCount(): Int {
        return if (entries != null) entries!!.size() else 0
    }

    class object {
        private var entries: ArrayList<Entry>? = null

        public fun getEntry(i: Int): Entry? {
            if (entries == null || i < 0 || entries!!.size() <= i)
                return null
            else
                return entries!!.get(i)
        }
    }
}