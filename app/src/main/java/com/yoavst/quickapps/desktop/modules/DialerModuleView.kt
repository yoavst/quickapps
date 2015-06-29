package com.yoavst.quickapps.desktop.modules

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.provider.ContactsContract
import android.telephony.PhoneNumberUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import com.google.gson.Gson
import com.yoavst.kotlin.telephonyManager
import com.yoavst.kotlin.toast
import com.yoavst.quickapps.R
import com.yoavst.quickapps.desktop.BaseModuleView
import com.yoavst.quickapps.tools.quickDials
import com.yoavst.quickapps.tools.typeToken
import java.lang.reflect.Type
import java.util.HashMap
import kotlin.properties.Delegates


public class DialerModuleView : BaseModuleView {
    var quickNumbers: MutableMap<Int, Pair<String, String>>? = null
    var lastNum = -1
    val iso = getContext().telephonyManager().getSimCountryIso().toUpperCase().trim()


    public constructor(context: Context) : super(context) {
    }

    public constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    }

    public constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    }

    override fun init() {
        super.init()
        if (!isInEditMode()) {
            if (getContext().quickDials != "{}")
                quickNumbers = Gson().fromJson(getContext().quickDials, QUICK_NUMBERS_TYPE)
            else quickNumbers = HashMap(10)
            val v = LayoutInflater.from(getContext()).inflate(R.layout.desktop_view_dial, layout, true)
            for (id in intArrayOf(R.id.digit1, R.id.digit2, R.id.digit3, R.id.digit4, R.id.digit5, R.id.digit6, R.id.digit7, R.id.digit8, R.id.digit9)) {
                val digit = v.findViewById(id)
                digit.setOnClickListener { view ->
                    lastNum = Integer.parseInt(view.getTag() as String)
                    (getContext() as Activity).startActivityForResult(Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), PICK_CONTACT_REQUEST)
                }
                digit.setOnLongClickListener { view ->
                    lastNum = Integer.parseInt(view.getTag() as String)
                    if (quickNumbers!!.containsKey(lastNum)) {
                        val number = quickNumbers!!.get(lastNum)
                        AlertDialog.Builder(getContext())
                                .setTitle(R.string.contacts)
                                .setMessage(number!!.first + " " + PhoneNumberUtils.formatNumber(number.second, iso))
                                .setPositiveButton(android.R.string.ok) { x, y ->

                                }.setNegativeButton(R.string.remove_quick_dial) { x, y ->
                            quickNumbers!!.remove(lastNum)
                            update()
                        }.show()
                    } else
                        getContext()toast(R.string.empty_speed_dial)
                    true
                }
            }
        }
    }

    public fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data != null && requestCode == PICK_CONTACT_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                val contentUri = data.getData()
                val contactId = contentUri.getLastPathSegment()
                val cursor = getContext().getContentResolver()
                        .query(ContactsContract.Data.CONTENT_URI, arrayOf("display_name"),
                                "contact_id" + " = ?", arrayOf(contactId),
                                "contact_id" + " ASC")
                if (cursor.moveToFirst()) {
                    val name = cursor.getString(0)
                    val numberCursor = getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            "contact_id" + " = ?", arrayOf(contactId), "contact_id" + " ASC")
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
                            AlertDialog.Builder(getContext())
                                    .setTitle(R.string.choose_number)
                                    .setNegativeButton(android.R.string.no) { dialog, which -> dialog.dismiss() }
                                    .setItems(phones) { dialog, which -> putNumber(name, phones[which]!!) }
                                    .show()
                        }
                    } else
                        getContext().toast(android.R.string.emptyPhoneNumber)
                }
            }
        }
    }

    fun putNumber(name: String, number: String) {
        quickNumbers!!.put(lastNum, name.to(number.replace("[-\\(\\)]".toRegex(), "")))
        update()
    }

    fun update() {
        getContext().quickDials = Gson().toJson(quickNumbers, QUICK_NUMBERS_TYPE)
        lastNum = -1
    }


    override fun getName(): Int = R.string.dialer_module_name

    override fun getIcon(): Int = R.drawable.qcircle_icon_dialer

    companion object {
        public val PICK_CONTACT_REQUEST: Int = 42
        public val QUICK_NUMBERS_TYPE: Type = typeToken<HashMap<Int, Pair<String, String>>>()
    }
}