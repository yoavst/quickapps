package com.yoavst.quickapps.calendar

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.yoavst.kotlin.hide
import com.yoavst.kotlin.show
import com.yoavst.quickapps.R
import com.yoavst.quickapps.tools.QCircleActivity
import com.yoavst.quickapps.tools.amPmInCalendar
import com.yoavst.quickapps.tools.showLocation
import kotlinx.android.synthetic.calendar_fragment.*

/**
 * Created by yoavst.
 */
public class CalendarFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.calendar_fragment, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val event = (getActivity() as CCalendarActivity)[getArguments().getInt(Event)]
        if (Unknown == null) Unknown = getString(R.string.unknown)
        if (At == null) At = getString(R.string.at)
        view?.setOnTouchListener { v, e -> (getActivity() as QCircleActivity).gestureDetector.onTouchEvent(e) }
        CalendarUtil.CalendarResources.init(getActivity())
        title.setText(event.title)
        if (!getActivity().showLocation || event.location == null || event.location.length() == 0)
            location.setText("")
        else {
            location.setText(At + " " + event.location)
        }
        date.setText(CalendarUtil.getDateFromEvent(event, getActivity().amPmInCalendar))
        timeLeft.setText(CalendarUtil.getTimeToEvent(event))

    }

    companion object {
        var Unknown: String? = null
        var At: String? = null
        var Event: String = "event"

        public fun newInstance(event: Int): CalendarFragment {
            var fragment = CalendarFragment()
            var args = Bundle()
            args.putInt(Event, event)
            fragment.setArguments(args)
            return fragment;
        }

    }
}