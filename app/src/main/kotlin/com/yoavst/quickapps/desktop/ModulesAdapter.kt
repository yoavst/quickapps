package com.yoavst.quickapps.desktop

import com.yoavst.quickapps.R
import android.app.FragmentManager
import android.content.Context
import android.support.v13.app.FragmentPagerAdapter
import android.app.Fragment
import kotlin.properties.Delegates
import com.yoavst.quickapps.desktop.modules.GeneralSettingsFragment
import com.yoavst.quickapps.desktop.modules.TorchFragment
import com.yoavst.quickapps.desktop.modules.MusicFragment
import com.yoavst.quickapps.desktop.modules.CalendarFragment
import com.yoavst.quickapps.desktop.modules.NotificationsFragment
import com.yoavst.quickapps.desktop.modules.CompassFragment
import com.yoavst.quickapps.desktop.modules.EightBallFragment
import com.yoavst.quickapps.desktop.modules.RecorderFragment
import com.yoavst.quickapps.desktop.modules.StopwatchFragment
import com.yoavst.quickapps.desktop.modules.CalculatorFragment
import com.yoavst.quickapps.desktop.modules.NewsFragment
import com.yoavst.quickapps.desktop.modules.TogglesFragment
import com.yoavst.quickapps.desktop.modules.LauncherFragment
import com.yoavst.quickapps.desktop.modules.BarcodeFragment

public class ModulesAdapter(fm: FragmentManager, context: Context) : FragmentPagerAdapter(fm) {
    private val TITLES: Array<String> by Delegates.lazy { context.getApplicationContext().getResources().getStringArray(R.array.modules) }

    override fun getCount(): Int {
        return TITLES.size()
    }

    public override fun getItem(i: Int): Fragment {
        val fragment: Fragment
        when (i) {
            1 -> fragment = TorchFragment()
            2 -> fragment = MusicFragment()
            3 -> fragment = CalendarFragment()
            4 -> fragment = NotificationsFragment()
            5 -> fragment = TogglesFragment()
            6 -> fragment = LauncherFragment()
            7 -> fragment = StopwatchFragment()
            8 -> fragment = CalculatorFragment()
            9 -> fragment = CompassFragment()
            10 -> fragment = NewsFragment()
            11 -> fragment = DialerFragment()
            12 -> fragment = EightBallFragment()
            13 -> fragment = RecorderFragment()
            14 -> fragment = BarcodeFragment()
            else -> fragment = GeneralSettingsFragment()
        }
        return fragment
    }

    public override fun getPageTitle(position: Int): CharSequence {
        return TITLES[position]
    }
}