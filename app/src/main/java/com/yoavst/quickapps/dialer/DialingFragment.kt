package com.yoavst.quickapps.dialer

import android.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import com.yoavst.quickapps.util.QCircleActivity
import java.util.ArrayList
import java.util.HashMap
import kotlin.properties.Delegates
import com.yoavst.quickapps.PrefManager
import com.google.i18n.phonenumbers.PhoneNumberUtil
import android.widget.TextView
import com.lge.qcircle.template.TemplateTag
import com.google.gson.Gson
import com.yoavst.util.typeToken
import android.provider.ContactsContract
import android.database.Cursor
import com.google.i18n.phonenumbers.NumberParseException
import com.yoavst.mashov.AsyncJob
import android.content.Intent
import android.net.Uri
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.Spanned
import com.yoavst.kotlin.colorResource
import com.yoavst.quickapps.R
import kotlinx.android.synthetic.dialer_circle_layout.*
/**
 * Created by Yoav.
 */
public class DialingFragment: Fragment() {
    var phoneNumbers: ArrayList<Pair<String, String>> = ArrayList()
    var quickNumbers: HashMap<Int, Pair<String, String>> by Delegates.notNull()
    val hasLeadingZero by Delegates.lazy { PrefManager(getActivity()).dialerStartWithZero().getOr(true) }
    val suggestionColor by colorResource(android.R.color.darker_gray)
    var countryRegion: String by Delegates.notNull()
    var numberUtil = PhoneNumberUtil.getInstance()
    var originalOldText = ""
    var oldName = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialer_circle_layout, container, false)
        view.setOnTouchListener { v, e -> (getActivity() as QCircleActivity).gestureDetector.onTouchEvent(e) }
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view!!.findViewById(R.id.dial).setOnClickListener { v -> dial() }
        view.findViewById(R.id.delete).setOnClickListener { v -> onDelete() }
        view.findViewById(R.id.delete).setOnLongClickListener { v ->
            deleteAll()
            true
        }
        number.setOnClickListener { v -> onClickOnText() }
        view.findViewById(R.id.quick_circle_back_btn).setOnClickListener { v -> getActivity().finish() }
        try {
            countryRegion = numberUtil.getRegionCodeForCountryCode(Integer.parseInt(CDialerActivity.GetCountryZipCode(getActivity())))
        } catch (e: Exception) {
            countryRegion = "001"
        }
        for (id in intArray(R.id.digit0, R.id.digit1, R.id.digit2, R.id.digit3,
                R.id.digit4, R.id.digit5, R.id.digit6, R.id.digit7, R.id.digit8, R.id.digit9)) {
            val v = view.findViewById(id);
            v.setOnClickListener { v -> onNumberClicked(v) };
            v.setOnLongClickListener { v -> onNumberLongClicked(v) };
        }
        val quickDials = PrefManager(getActivity()).quickDials().getOr("[]")
        quickNumbers = Gson().fromJson(quickDials, typeToken<HashMap<Int, Pair<String, String>>>())
        name.setSelected(true)
        val phones = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, array(ContactsContract.CommonDataKinds.Phone.NUMBER, "display_name"), null, null, null)
        phones.moveToFirst()
        handleContacts(phones)
    }

    fun handleContacts(cursor: Cursor) {
        val phoneUtil = PhoneNumberUtil.getInstance()
        while (cursor.moveToNext()) {
            try {
                val phone = phoneUtil.parse(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)),
                        getResources().getConfiguration().locale.getCountry())
                phoneNumbers.add(cursor.getString(cursor.getColumnIndex("display_name")).to((if (hasLeadingZero) "0" else "") + phone.getNationalNumber()))
            } catch (e: NumberParseException) {
                break
            }

        }
        AsyncJob.doOnMainThread { cursor.close() }
    }

    fun onNumberClicked(view: View) {
        removeSuggestion()
        number.append(view.getTag() as String)
        updateSuggestion(number.getText().toString())
    }

    fun onNumberLongClicked(view: View): Boolean {
        val num = view.getTag().toString().toInt()
        if (quickNumbers.containsKey(num)) {
            val contact = quickNumbers.get(num)
            try {
                val phone = numberUtil.parse(contact.second, countryRegion)
                number.setText((if (hasLeadingZero) "0" else "") + phone.getNationalNumber())
                originalOldText = number.getText().toString()
                name.setText(contact.first)
                oldName = contact.first
            } catch (e: NumberParseException) {
                e.printStackTrace()
            }
        }
        return true;
    }

    fun dial() {
        if (number.getText().length() >= 3)
            startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number.getText())))
    }

    fun onDelete() {
        removeSuggestion()
        val text = number.getText()
        if (text.length() > 0) {
            number.setText(text.subSequence(0, text.length() - 1))
            originalOldText = number.getText().toString()
            oldName = ""
            updateSuggestion(number.getText().toString())
        }
    }

    fun onClickOnText() {
        val s = SpannableString(number.getText())
        val spans = s.getSpans(0, s.length(), javaClass<ForegroundColorSpan>())
        for (span in spans) {
            s.removeSpan(span)
        }
        number.setText(s)
        originalOldText = s.toString()
    }

    fun deleteAll() {
        number.setText("")
        originalOldText = ""
        name.setText("")
    }

    fun removeSuggestion() {
        number.setText(originalOldText)
        name.setText("")
        oldName = ""
    }

    fun updateSuggestion(text: String) {
        originalOldText = text
        if (originalOldText.length() >= 2) {
            for (num in phoneNumbers) {
                if (num.second.startsWith(originalOldText)) {
                    setText(num)
                    return
                }
            }
        } else
            AsyncJob.doOnMainThread { name.setText("") }
    }

    fun setText(num: Pair<String, String>) {
        AsyncJob.doOnMainThread {
            val text = SpannableString(num.second)
            text.setSpan(ForegroundColorSpan(suggestionColor), originalOldText.length(), text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            number.setText(text)
            if (name.getText() != num.first) {
                name.setText(num.first)
                oldName = num.first
            }
        }
    }

    public override fun onDestroy() {
        super.onDestroy()
        phoneNumbers.clear()
        phoneNumbers = ArrayList(1)
    }

}