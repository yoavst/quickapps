package com.yoavst.quickapps.calendar

import com.yoavst.quickapps.util.QCircleActivity
import android.content.Intent
import kotlin.properties.Delegates
import com.yoavst.quickapps.R
import android.os.Bundle
import android.graphics.Color
import android.support.v4.view.ViewPager
import com.lge.qcircle.template.*
import com.yoavst.kotlin.colorRes

/**
 * Created by Yoav.
 */
public class CCalendarActivity : QCircleActivity() {
    override val template: QCircleTemplate by Delegates.lazy { QCircleTemplate(this, TemplateType.CIRCLE_EMPTY) }
    val pager: ViewPager by Delegates.lazy { ViewPager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        template.addElement(QCircleBackButton(this))
        val title = QCircleTitle(this, getString(R.string.calendar_module_name), Color.WHITE, colorRes(R.color.md_red_500))
        title.setTextSize(17f)
        template.addElement(title)
        pager.setId(R.id.calendar_pager)
        pager.setAdapter(EventsAdapter(getFragmentManager(), this))
        template.getLayoutById(TemplateTag.CONTENT_MAIN).addView(pager)
        setContentView(template.getView())
    }

    override fun getIntentToShow(): Intent? {
        return try {
            val id = ((getFragmentManager().findFragmentByTag("android:switcher:" + R.id.calendar_pager + ":" + pager.getCurrentItem())) as EventsFragment).event.getId()
            CalendarUtil.launchEventById(id).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        } catch (exception: Exception) {
            null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        (pager.getAdapter() as EventsAdapter).clearEventsForGc()
    }
}