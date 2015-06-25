package com.yoavst.quickapps.toggles

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.view.ViewPager
import com.lge.qcircle.template.QCircleBackButton
import com.lge.qcircle.template.QCircleTitle
import com.yoavst.kotlin.colorRes
import com.yoavst.quickapps.R
import com.yoavst.quickapps.tools.QCircleActivity
import kotlin.properties.Delegates

/**
 * Created by Yoav.
 */
public class CTogglesActivity : QCircleActivity() {

    val pager: ViewPager by Delegates.lazy {
        val localPager = ViewPager(this)
        localPager.setId(R.id.pager)
        localPager.setAdapter(TogglesAdapter(getFragmentManager(), this))
        localPager
    }

    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        template.addElement(QCircleBackButton(this))
        val title = QCircleTitle(this, getString(R.string.toggles_module_name), Color.WHITE, colorRes(R.color.md_indigo_700))
        title.setTextSize(17f)
        template.addElement(title)
        setContentViewToMain(pager)
        setContentView(template.getView())

    }

    override fun getIntentToShow(): Intent? {
        return (getFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager + ":" + pager.getCurrentItem()) as ToggleFragment)
                .getIntentForLaunch().addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
}
