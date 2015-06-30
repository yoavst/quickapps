package com.yoavst.quickapps.calendar

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.CalendarContract
import android.view.View
import com.lge.qcircle.template.QCircleBackButton
import com.lge.qcircle.template.QCircleTitle
import com.yoavst.kotlin.*
import com.yoavst.quickapps.R
import com.yoavst.quickapps.tools.QCircleActivity
import kotlinx.android.synthetic.calendar_activity.errorLayout
import kotlinx.android.synthetic.calendar_activity.pager
import kotlinx.android.synthetic.calendar_activity.progress

/**
 * Created by yoavst.
 */
public class CCalendarActivity : QCircleActivity() {
    var events: List<Event>? = null

    public fun get(position: Int): Event = events!![position]


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val title = QCircleTitle(this, getString(R.string.calendar_module_name), Color.WHITE, colorRes(R.color.md_red_500))
        title.setTextSize(17f)
        template.addElement(title)
        template.addElement(QCircleBackButton(this))
        setContentViewToMain(R.layout.calendar_activity)
        setContentView(template.getView())
        loadEvents()
    }

    fun loadEvents() {
        async {
            events = EventRetiever.getEvents(this, 30)
            mainThread {
                progress.hide()
                if (events == null || events!!.size() == 0) {
                    errorLayout.show()
                } else {
                    pager.show()
                    pager.setAdapter(EventsAdapter(getFragmentManager(), events!!.size()))
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (events is MutableList) {
            (events as MutableList).clear()
        }
    }

    override fun getIntentToShow(): Intent? {
        if (events == null || pager.getVisibility() == View.GONE) return null
        return getIntentForEvent(events!![pager.getCurrentItem()].id)
    }

    fun getIntentForEvent(id: Long): Intent {
        val intent = Intent(Intent.ACTION_VIEW)
        val builder = CalendarContract.Events.CONTENT_URI.buildUpon().appendPath(id.toString())
        return intent.setData(builder.build())

    }
}