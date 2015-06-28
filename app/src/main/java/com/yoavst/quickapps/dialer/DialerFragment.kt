package com.yoavst.quickapps.dialer

import android.app.Fragment
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.yoavst.kotlin.colorResource
import com.yoavst.kotlin.mainThread
import com.yoavst.quickapps.R
import com.yoavst.quickapps.tools.QCircleActivity
import com.yoavst.quickapps.tools.quickDials
import com.yoavst.quickapps.tools.typeToken
import kotlinx.android.synthetic.dialer_fragment.*
import java.util.HashMap
import kotlin.properties.Delegates

/**
 * Created by yoavst.
 */
public class DialerFragment : Fragment() {
    val suggestionColor by colorResource(android.R.color.darker_gray)
    var quickNumbers: HashMap<Int, Pair<String, String>> by Delegates.notNull()
    var numberUtil = PhoneNumberUtil.getInstance()
    var originalOldText = ""
    var oldName = ""

    val numbers by Delegates.lazy { (getActivity() as CDialerActivity).phones }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialer_fragment, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view?.setOnTouchListener { v, e -> (getActivity() as QCircleActivity).gestureDetector.onTouchEvent(e) }
        dial.setOnClickListener {
            if (number.getText().length() >= 3)
                startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number.getText())))
        }
        delete.setOnClickListener {
            removeSuggestion()
            val text = number.getText()
            if (text.length() > 0) {
                number.setText(text.subSequence(0, text.length() - 1))
                originalOldText = number.getText().toString()
                oldName = ""
                updateSuggestion(number.getText().toString())
            }
        }
        delete.setOnLongClickListener {
            number.setText("")
            originalOldText = ""
            name.setText("")
            true
        }
        number.setOnClickListener {
            val s = SpannableString(number.getText())
            val spans = s.getSpans(0, s.length(), javaClass<ForegroundColorSpan>())
            for (span in spans) {
                s.removeSpan(span)
            }
            number.setText(s)
            originalOldText = s.toString()
        }
        back.setOnClickListener { getActivity().finish() }
        for (id in intArrayOf(R.id.digit0, R.id.digit1, R.id.digit2, R.id.digit3,
                R.id.digit4, R.id.digit5, R.id.digit6, R.id.digit7, R.id.digit8, R.id.digit9)) {
            val v = view!!.findViewById(id)
            v.setOnClickListener { onNumberClicked(it) }
            v.setOnLongClickListener { onNumberLongClicked(it) }
        }
        name.setSelected(true)
        quickNumbers = Gson().fromJson(getActivity().quickDials, typeToken<HashMap<Int, Pair<String, String>>>())
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
                number.setText(contact.second)
                originalOldText = number.getText().toString()
                name.setText(contact.first)
                oldName = contact.first
            } catch (e: NumberParseException) {
                e.printStackTrace()
            }
        }
        return true;
    }

    fun removeSuggestion() {
        number.setText(originalOldText)
        name.setText("")
        oldName = ""
    }

    fun updateSuggestion(text: String) {
        originalOldText = text
        if (originalOldText.length() >= 2) {
            for (num in numbers) {
                val number: String
                if (num.parsed != null)
                    num.number = numberUtil.format(num.parsed, PhoneNumberUtil.PhoneNumberFormat.NATIONAL).replace("[-\\(\\)]".toRegex(), "")
                if (num.number!!.startsWith(originalOldText)) {
                    setText(num)
                    return
                }
            }
        } else
            mainThread { name.setText("") }
    }

    fun setText(num: PhoneNumberWrapper) {
        mainThread {
            val text = SpannableString(num.number)
            text.setSpan(ForegroundColorSpan(suggestionColor), originalOldText.length(), text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            number.setText(text)
            if (name.getText() != num.name) {
                name.setText(num.name)
                oldName = num.name
            }
        }
    }

}