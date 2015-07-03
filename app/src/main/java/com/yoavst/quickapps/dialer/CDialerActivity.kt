package com.yoavst.quickapps.dialer

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v4.view.ViewPager
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.lge.qcircle.template.QCircleBackButton
import com.lge.qcircle.template.QCircleTitle
import com.yoavst.kotlin.*
import com.yoavst.quickapps.R
import com.yoavst.quickapps.tools.QCircleActivity
import kotlinx.android.synthetic.dialer_activity.pager
import kotlinx.android.synthetic.dialer_activity.progress
import kotlin.properties.Delegates

public class CDialerActivity : QCircleActivity() {
    public var phones: Array<PhoneNumberWrapper> by Delegates.notNull()

    var hasInitButtons = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentViewToMain(R.layout.dialer_activity)
        setContentView(template.getView())
        pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageSelected(position: Int) {
                if (position == 0) {
                    if (hasInitButtons) {
                        template.getView().findViewById(R.id.backButton).hide()
                        template.getView().findViewById(R.id.title).hide()
                    }
                } else {
                    if (!hasInitButtons) {
                        initButtons()
                        hasInitButtons = true
                    } else {
                        template.getView().findViewById(R.id.backButton).show()
                        template.getView().findViewById(R.id.title).show()
                    }
                }
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }
        })
        async { loadContacts() }

    }

    fun initButtons() {
        if (!hasInitButtons) {
            template.addElement(QCircleBackButton(this))
            val title = QCircleTitle(this, getString(R.string.contacts), Color.WHITE, colorRes(R.color.md_amber_500))
            title.setTextSize(17F)
            template.addElement(title)
            hasInitButtons = true
        }
    }

    fun loadContacts() {
        val cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER, "display_name"), null, null, null)
        cursor.moveToFirst()
        val size = cursor.getCount()
        if (size != 0) {
            val countryCode = telephonyManager().getSimCountryIso().toUpperCase().trim()
            val util = PhoneNumberUtil.getInstance()
            val temp: Array<PhoneNumberWrapper?> = arrayOfNulls(size)
            var i = 0
            do {
                val raw = cursor.getString(0)
                try {
                    val number = util.parse(raw, countryCode)
                    temp[i] = PhoneNumberWrapper(cursor.getString(1), parsed = number)
                } catch (e: Exception) {
                    temp[i] = PhoneNumberWrapper(cursor.getString(1), raw.replace("[-\\(\\)]".toRegex(), ""))
                }
                i++
            } while (cursor.moveToNext())
            phones = temp as Array<PhoneNumberWrapper>
            phones = phones.sortBy(comparator { l, r -> l.name compareTo r.name }).toTypedArray()
        } else phones = arrayOf()
        cursor.close()
        mainThread {
            progress.hide()
            pager.show()
            pager.setAdapter(DialerAdapter(getFragmentManager()))
        }
    }

    override fun getIntentToShow(): Intent? {
        return Intent().setClassName("com.android.contacts", "alias.PeopleFloatingActivity").putExtra("com.lge.app.floating.launchAsFloating", true)
    }


}