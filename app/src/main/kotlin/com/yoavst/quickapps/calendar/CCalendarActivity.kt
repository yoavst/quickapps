package com.yoavst.quickapps.calendar

import com.yoavst.quickapps.util.QCircleActivity
import com.lge.qcircle.template.QCircleTemplate
import android.content.Intent
import kotlin.properties.Delegates
import com.lge.qcircle.template.TemplateType
import com.yoavst.quickapps.R
import android.os.Bundle
import android.graphics.Color
import com.mobsandgeeks.ake.getColor
import android.support.v4.view.ViewPager
import com.lge.qcircle.template.TemplateTag

/**
 * Created by Yoav.
 */
public class CCalendarActivity : QCircleActivity() {
    override val template: QCircleTemplate by Delegates.lazy { QCircleTemplate(this, TemplateType.CIRCLE_EMPTY) }
    val pager: ViewPager by Delegates.lazy { ViewPager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        template.setTitle(getString(R.string.calendar_module_name), Color.WHITE, getColor(R.color.md_red_500))
        template.setTitleTextSize(17F)
        template.setBackButton()
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