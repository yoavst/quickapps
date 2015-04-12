package com.yoavst.quickapps.calendar

import android.app.Fragment
import android.view.View
import android.widget.TextView

import com.yoavst.quickapps.R

import kotlin.properties.Delegates
import com.yoavst.quickapps.PrefManager
import com.yoavst.quickapps.util.QCircleActivity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import com.yoavst.kotlin.hide
import com.yoavst.kotlin.show

/**
 * Created by Yoav.
 */
public class EventsFragment : Fragment() {
    var event: Event by Delegates.notNull()
    val prefs by Delegates.lazy { PrefManager(getActivity()) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.calendar_circle_layout, container, false)
        event = getArguments().getSerializable(EVENT) as Event
        if (UNKNOWN == null) UNKNOWN = getString(R.string.unknown)
        view.setOnTouchListener { v, e -> (getActivity() as QCircleActivity).gestureDetector.onTouchEvent(e) }
        CalendarUtil.CalendarResources.init(getActivity())
        (view.findViewById(R.id.title) as TextView).setText(event.getTitle())
        val location = view.findViewById(R.id.location) as TextView
        if (!prefs.showLocation().getOr(true) || event.getLocation() == null || event.getLocation().length() == 0)
            location.hide()
        else {
            location.show()
            location.setText("At " + event.getLocation())
        }
        (view.findViewById(R.id.date) as TextView).setText(CalendarUtil.getDateFromEvent(event))
        (view.findViewById(R.id.time_left) as TextView).setText(CalendarUtil.getTimeToEvent(event))
        return view
    }

    companion object {
        var UNKNOWN: String? = null
        var EVENT: String = "event"

        public fun newInstance(event: Event): EventsFragment {
            var fragment = EventsFragment()
            var args = Bundle()
            args.putSerializable(EVENT, event)
            fragment.setArguments(args)
            return fragment;
        }

    }
}
