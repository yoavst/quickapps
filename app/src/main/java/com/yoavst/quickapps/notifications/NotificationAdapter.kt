package com.yoavst.quickapps.notifications

import android.app.Fragment
import android.app.FragmentManager
import android.support.v13.app.FragmentStatePagerAdapter
import android.util.SparseArray
import android.view.ViewGroup
import android.support.v4.view.PagerAdapter

/**
 * Created by Yoav.
 */
public class NotificationAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    private val activeFragments = SparseArray<Fragment>(3)

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }

    override fun getItem(i: Int): Fragment {
        val fragment: Fragment
        try {
            fragment = NotificationsFragment.newInstance(NotificationsManager.notifications!!.get(i))
        } catch (e: Exception) {
            fragment = NotificationsFragment()
        }

        activeFragments.put(i, fragment)
        return fragment
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        try {
            super.destroyItem(container, position, `object`)
            activeFragments.remove(position)
        } catch (ignored: Exception) {
        }

    }

    override fun getCount(): Int {
        return if (NotificationsManager.notifications == null) 0 else NotificationsManager.notifications!!.size()
    }

    public fun getActiveFragment(index: Int): Fragment? {
        return if (getCount() != 0) activeFragments.get(index) else null
    }


}
