package com.yoavst.quickapps.toggles

import com.yoavst.quickapps.util.QCircleActivity
import kotlin.properties.Delegates
import android.content.res.Resources
import android.content.pm.PackageManager
import android.os.Bundle
import com.yoavst.quickapps.R
import android.graphics.Color
import android.support.v4.view.ViewPager
import android.content.Intent
import com.lge.qcircle.template.*
import com.yoavst.kotlin.colorRes

/**
 * Created by Yoav.
 */
public class CTogglesActivity : QCircleActivity() {
    override val template: QCircleTemplate by Delegates.lazy { QCircleTemplate(this, TemplateType.CIRCLE_EMPTY) }

    val pager: ViewPager by Delegates.lazy {
        val localPager = ViewPager(this)
        localPager.setId(R.id.toggles_pager)
        localPager.setAdapter(TogglesAdapter(getFragmentManager(), this))
        localPager
    }
    val systemUiResources: Resources by Delegates.lazy {
        val pm = getPackageManager()
        try {
            val applicationInfo = pm.getApplicationInfo("com.android.systemui", PackageManager.GET_META_DATA)
            pm.getResourcesForApplication(applicationInfo);
        } catch (e: PackageManager.NameNotFoundException) {
            // Congratulations user, you are so dumb that there is no system ui...
            e.printStackTrace();
            throw IllegalStateException()
        }
    }

    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        template.addElement(QCircleBackButton(this))
        val title = QCircleTitle(this, getString(R.string.toggles_module_name), Color.WHITE, colorRes(R.color.md_indigo_700))
        title.setTextSize(17f)
        template.addElement(title)
        template.getLayoutById(TemplateTag.CONTENT_MAIN).addView(pager)
        setContentView(template.getView())

    }

    public fun getSystemUiResource(): Resources {
        return systemUiResources;
    }

    override fun getIntentToShow(): Intent? {
        return (getFragmentManager().findFragmentByTag("android:switcher:" + R.id.toggles_pager + ":" + pager.getCurrentItem()) as ToggleFragment)
                .getIntentForLaunch().addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
}
