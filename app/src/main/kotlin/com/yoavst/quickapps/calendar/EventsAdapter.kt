package com.yoavst.quickapps.calendar

import android.app.Fragment
import android.app.FragmentManager
import android.content.Context
import android.support.v13.app.FragmentPagerAdapter

import java.util.ArrayList
import kotlin.properties.Delegates

/**
 * Created by Yoav.
 */
public class EventsAdapter(fm: FragmentManager, context: Context) : FragmentPagerAdapter(fm) {
    var events: ArrayList<Event> by Delegates.notNull();

    {
        events = CalendarUtil.getCalendarEvents(context)
    }

    public fun clearEventsForGc() {
            events.clear()
            events.trimToSize()
    }

    override fun getItem(i: Int): Fragment {
        return EventsFragment.newInstance(events.get(i))
    }

    override fun getCount(): Int {
        return events.size()
    }
}
