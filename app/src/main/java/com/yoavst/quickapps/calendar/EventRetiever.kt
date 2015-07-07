package com.yoavst.quickapps.calendar

import android.content.Context
import android.text.format.Time
import java.util.ArrayList

/**
 * Created by yoavst.
 */
public object EventRetiever {
    public fun getEvents(context: Context, days: Int): List<Event> {
        var temp = ArrayList<Event>(days * 2) // Let's hope user has less then 2 events per days for performance.
        val time = Time(Time.getCurrentTimezone());
        Event.loadEvents(context, temp, Time.getJulianDay(System.currentTimeMillis(), time.gmtoff), 30)
        val now = System.currentTimeMillis()
        return temp filter { now < it.endMillis } sortBy(comparator { l, r ->
            val start = (l.startMillis - r.startMillis).toInt()
            if (start != 0) start
            else {
                val end = (l.endMillis - r.endMillis).toInt()
                if (end != 0) end
                else l.title.toString().compareTo(r.title.toString(), ignoreCase = true)
            }
        })
    }
}