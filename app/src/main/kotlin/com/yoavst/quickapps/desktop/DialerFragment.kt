package com.yoavst.quickapps.desktop

import android.app.Fragment
import android.view.View
import android.os.Bundle
import android.view.ViewGroup
import android.view.LayoutInflater
import com.yoavst.quickapps.R
import java.util.HashMap
import com.yoavst.quickapps.PrefManager
import java.lang.reflect.Type
import com.google.gson.reflect.TypeToken
import android.content.Intent
import android.provider.ContactsContract
import kotlin.properties.Delegates
import com.google.gson.Gson
import android.app.Activity
import android.app.AlertDialog
import android.widget.CheckBox
import com.yoavst.util.typeToken
import com.mobsandgeeks.ake.showShortToast

/**
 * Created by Yoav.
 */
public class DialerFragment : Fragment() {
    val prefs: PrefManager by Delegates.lazy { PrefManager(getActivity()) }
    var quickNumbers: HashMap<Int, Pair<String, String>> = HashMap(10)
    var lastNum = -1
    public override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.desktop_module_dialer, container, false)
        for (id in intArray(R.id.digit1, R.id.digit2, R.id.digit3, R.id.digit4, R.id.digit5, R.id.digit6, R.id.digit7, R.id.digit8, R.id.digit9)) {
            val digit = v.findViewById(id)
            digit.setOnClickListener { view ->
                lastNum = Integer.parseInt(view.getTag() as String)
                startActivityForResult(Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), PICK_CONTACT_REQUEST)
            }
            digit.setOnLongClickListener { view ->
                lastNum = Integer.parseInt(view.getTag() as String)
                if (quickNumbers.containsKey(lastNum)) {
                    val number = quickNumbers.get(lastNum)
                    showShortToast(number.first + " " + number.second)
                } else
                    showShortToast(R.string.empty_speed_dial)
                true
            }
        }
        quickNumbers = Gson().fromJson(prefs.quickDials().getOr("[]"), QUICK_NUMBERS_TYPE)
        val startWithZeroCheckbox: CheckBox = v.findViewById(R.id.zero_checkbox) as CheckBox
        startWithZeroCheckbox.setChecked(prefs.dialerStartWithZero().getOr(true))
        startWithZeroCheckbox.setOnCheckedChangeListener {(compoundButton, isChecked) ->
            prefs.dialerStartWithZero().put(isChecked).apply()
            showShortToast(R.string.changed_successfully)
        }
        v.findViewById(R.id.zero_row).setOnClickListener { v -> startWithZeroCheckbox.toggle() }
        return v
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null && requestCode == PICK_CONTACT_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                val contentUri = data.getData()
                val contactId = contentUri.getLastPathSegment()
                val cursor = getActivity().getContentResolver()
                        .query(ContactsContract.Data.CONTENT_URI, array("display_name"),
                                "contact_id" + " = ?", array(contactId),
                                "contact_id" + " ASC")
                if (cursor.moveToFirst()) {
                    val name = cursor.getString(0)
                    val numberCursor = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            "contact_id" + " = ?", array(contactId), "contact_id" + " ASC")
                    numberCursor.moveToFirst()
                    val count = numberCursor.getCount()
                    if (count != 0) {
                        if (count == 1)
                            putNumber(name, numberCursor.getString(numberCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)))
                        else {
                            val phones = arrayOfNulls<String>(count)
                            val index = numberCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                            for (i in 0..count - 1) {
                                phones[i] = numberCursor.getString(index)
                                numberCursor.moveToNext()
                            }
                            AlertDialog.Builder(getActivity())
                                    .setTitle(R.string.choose_number)
                                    .setNegativeButton(android.R.string.no) {(dialog, which) -> dialog.dismiss() }
                                    .setItems(phones) {(dialog, which) -> putNumber(name, phones[which]!!) }
                                    .show()
                        }
                    } else
                        showShortToast(android.R.string.emptyPhoneNumber)
                }
            }
        }
    }

    fun putNumber(name: String, number: String) {
        quickNumbers.put(lastNum, name.to(number))
        prefs.quickDials().put(Gson().toJson(quickNumbers, QUICK_NUMBERS_TYPE)).apply()
        lastNum = -1
    }

    class object {
        public val PICK_CONTACT_REQUEST: Int = 42
        public val QUICK_NUMBERS_TYPE: Type = typeToken<HashMap<Int, Pair<String, String>>>()
    }
}