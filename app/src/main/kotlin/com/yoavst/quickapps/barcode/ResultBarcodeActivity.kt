package com.yoavst.quickapps.barcode

import android.support.v7.app.ActionBarActivity
import android.os.Bundle
import com.google.gson.Gson
import android.content.Intent
import com.google.zxing.Result
import com.yoavst.util.typeToken
import com.yoavst.quickapps.R
import android.widget.TextView
import butterknife.bindView
import android.widget.Button
import com.google.zxing.client.result.ResultParser
import com.google.zxing.client.result.ParsedResultType.*
import com.mobsandgeeks.ake.hide
import com.yoavst.util.init
import android.app.SearchManager
import com.mobsandgeeks.ake.clipboardManager
import android.content.ClipData
import android.net.Uri
import com.google.zxing.client.result.TelParsedResult
import android.telephony.PhoneNumberUtils
import com.google.zxing.client.result.WifiParsedResult
import com.mobsandgeeks.ake.wifiManager
import android.os.AsyncTask
import com.google.zxing.client.result.ParsedResult
import com.google.zxing.client.result.SMSParsedResult
import com.google.zxing.client.result.GeoParsedResult
import com.google.zxing.client.result.EmailAddressParsedResult
import com.google.zxing.client.result.CalendarParsedResult
import java.util.Date
import android.content.ActivityNotFoundException
import com.mobsandgeeks.ake.w
import com.google.zxing.client.result.AddressBookParsedResult

/**
 * Created by yoavst.
 */
public class ResultBarcodeActivity : ActionBarActivity() {
    val btn1: Button by bindView(R.id.btn1)
    val btn2: Button by bindView(R.id.btn2)
    val btn3: Button by bindView(R.id.btn3)
    val btn4: Button by bindView(R.id.btn4)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val results: Result = Gson().fromJson(getIntent().getStringExtra(Intent.EXTRA_SUBJECT), typeToken<Result>())
        setContentView(R.layout.barcode_dialog_layout)
        setTitle(R.string.barcode_module_name)
        findViewById(R.id.text) as TextView setText display(results)
        init(results)
    }

    fun init(results: Result) {
        val parsed = ResultParser.parseResult(results)
        when (parsed.getType()) {
            ADDRESSBOOK -> {
                btn4.hide()
                val addressResult = parsed as AddressBookParsedResult
                btn1.init(R.string.add_to_contacts) {
                    val addresses = addressResult.getAddresses()
                    val address1 = if (addresses == null || addresses.size() < 1) null else addresses[0]
                    val addressTypes = addressResult.getAddressTypes()
                    val address1Type = if (addressTypes == null || addressTypes.size() < 1 ) null else addressTypes[0]
                    start(Util.addContact(addressResult.getNames(),
                            addressResult.getNicknames(),
                            addressResult.getPronunciation(),
                            addressResult.getPhoneNumbers(),
                            addressResult.getPhoneTypes(),
                            addressResult.getEmails(),
                            addressResult.getEmailTypes(),
                            addressResult.getNote(),
                            addressResult.getInstantMessenger(),
                            address1,
                            address1Type,
                            addressResult.getOrg(),
                            addressResult.getTitle(),
                            addressResult.getURLs(),
                            addressResult.getBirthday(),
                            addressResult.getGeo()))
                }
                btn2.init(R.string.dial) {
                    start(Intent(Intent.ACTION_DIAL, Uri.parse(("tel:" + addressResult.getPhoneNumbers()[0]))))

                }
                btn3.init(R.string.send_mail) {
                    val intent = Intent(Intent.ACTION_SEND, Uri.parse("mailto:"))
                    intent.putExtra(Intent.EXTRA_SUBJECT, addressResult.getEmails())
                            .setType("text/plain")
                    start(intent)
                }
            }
            CALENDAR -> {
                btn4.hide()
                btn3.hide()
                btn2.hide()
                btn1.init(R.string.add_to_calendar) {
                    val calendarResult = parsed as CalendarParsedResult
                    var description = calendarResult.getDescription();
                    var organizer = calendarResult.getOrganizer();
                    if (organizer != null) {
                        // No separate Intent key, put in description
                        if (description == null) {
                            description = organizer;
                        } else {
                            description = description + '\n' + organizer;
                        }
                    }

                    val intent = Intent(Intent.ACTION_INSERT)
                    intent.setType("vnd.android.cursor.item/event")
                    val startMilliseconds = parsed.getStart().getTime()
                    intent.putExtra("beginTime", startMilliseconds)
                    if (parsed.isStartAllDay()) {
                        intent.putExtra("allDay", true)
                    }
                    val endMilliseconds: Long
                    if (calendarResult.getEnd() == null) {
                        if (parsed.isStartAllDay()) {
                            // + 1 day
                            endMilliseconds = startMilliseconds + 24 * 60 * 60 * 1000L
                        } else {
                            endMilliseconds = startMilliseconds
                        }
                    } else {
                        endMilliseconds = parsed.getEnd().getTime()
                    }
                    intent.putExtra("endTime", endMilliseconds)
                    intent.putExtra("title", parsed.getSummary())
                    intent.putExtra("eventLocation", parsed.getLocation())
                    intent.putExtra("description", description)
                    if (parsed.getAttendees() != null) {
                        intent.putExtra(Intent.EXTRA_EMAIL, parsed.getAttendees())
                        // Documentation says this is either a String[] or comma-separated String, which is right?
                    }
                    try {
                        // Do this manually at first
                        start(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET))
                    } catch (anfe: ActivityNotFoundException) {
                        w("No calendar app available that responds to " + Intent.ACTION_INSERT)
                        // For calendar apps that don't like "INSERT":
                        intent.setAction(Intent.ACTION_EDIT)
                        start(intent) // Fail here for real if nothing can handle it
                    }

                }
            }
            EMAIL_ADDRESS -> {
                btn4.hide()
                val emailResults = (parsed as EmailAddressParsedResult)
                btn1.init(R.string.send_mail) {
                    val intent = Intent(Intent.ACTION_SEND, Uri.parse("mailto:"))
                    if (emailResults.getTos() != null && emailResults.getTos().size() != 0) {
                        intent.putExtra(Intent.EXTRA_EMAIL, emailResults.getTos())
                    }
                    if (emailResults.getCCs() != null && emailResults.getCCs().size() != 0) {
                        intent.putExtra(Intent.EXTRA_CC, emailResults.getCCs())
                    }
                    if (emailResults.getBCCs() != null && emailResults.getBCCs().size() != 0) {
                        intent.putExtra(Intent.EXTRA_BCC, emailResults.getBCCs())
                    }
                    intent.putExtra(Intent.EXTRA_SUBJECT, emailResults.getSubject())
                            .putExtra(Intent.EXTRA_TEXT, emailResults.getBody())
                            .setType("text/plain")
                    start(intent)
                }
                btn2.init(R.string.add_to_contacts) {
                    start(Util.addEmailOnlyContact(emailResults.getTos(), null))
                }
                btn3.init(R.string.copy_to_clipboard) {
                    clipboardManager().setPrimaryClip(ClipData.newPlainText("Scanned Barcode", emailResults.getTos().join(",")))
                    finish()
                }
            }
            GEO -> {
                btn4.hide()
                btn3.hide()
                btn1.init(R.string.open_in_map) {
                    start(Intent(Intent.ACTION_VIEW, Uri.parse((parsed as GeoParsedResult).getGeoURI())))
                }
                btn2.init(R.string.get_directions) {
                    val geoResults = (parsed as GeoParsedResult)
                    start(Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google." +
                            LocaleManager.getCountryTLD(this) + "/maps?f=d&daddr=" + geoResults.getLatitude() + ',' + geoResults.getLongitude())))
                }
            }
            SMS -> {
                btn4.hide()
                btn3.hide()
                btn2.hide()
                val number = parsed as SMSParsedResult
                btn1.init(R.string.send_sms) {
                    start(Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + number.getNumbers()[0])).putExtra("sms_body", number.getBody()).putExtra("compose_mode", true))
                }
            }
            TEL -> {
                btn4.hide()
                btn1.init(R.string.dial) {
                    start(Intent(Intent.ACTION_DIAL, Uri.parse((parsed as TelParsedResult).getTelURI())))
                }
                btn2.init(R.string.add_to_contacts) {
                    start(Util.addPhoneOnlyContact(array((parsed as TelParsedResult).getNumber()), null))
                }
                btn3.init(R.string.copy_to_clipboard) {
                    clipboardManager().setPrimaryClip(ClipData.newPlainText("Scanned Barcode", results.getText()))
                    finish()
                }
            }
            URI -> {
                btn4.hide()
                btn1.init(R.string.open_url) {
                    start(Intent(Intent.ACTION_VIEW, Uri.parse(results.getText())))
                }
                btn2.init(R.string.share) {
                    start(Intent.createChooser(Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, parsed.getDisplayResult()), getString(R.string.share)))
                }
                btn3.init(R.string.copy_to_clipboard) {
                    clipboardManager().setPrimaryClip(ClipData.newPlainText("Scanned Barcode", results.getText()))
                    finish()
                }
            }
            WIFI -> {
                btn4.hide()
                btn3.hide()
                btn2.hide()
                btn1.init(R.string.connect_wifi) {
                    val wifiResult = parsed as WifiParsedResult
                    val wifiManager = wifiManager()
                    WifiConfigManager(wifiManager).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, wifiResult);
                }
            }
            else -> {
                btn4.hide()
                btn1.init(R.string.share) {
                    start(Intent.createChooser(Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, results.getText()), getString(R.string.share)))
                }
                btn2.init(R.string.search) {
                    start(Intent(Intent.ACTION_WEB_SEARCH).putExtra(SearchManager.QUERY, results.getText()))
                }
                btn3.init(R.string.copy_to_clipboard) {
                    clipboardManager().setPrimaryClip(ClipData.newPlainText("Scanned Barcode", results.getText()))
                    finish()
                }
            }
        }
    }

    fun start(intent: Intent) {
        startActivity(intent)
        finish()
    }


    class object {
        public fun display(results: Result): CharSequence {
            val parsed = ResultParser.parseResult(results)
            return when (parsed.getType()) {
                ADDRESSBOOK -> {
                    return Util.formatContact(parsed)
                }
                CALENDAR -> {
                    val calResult = parsed as CalendarParsedResult
                    val result = StringBuilder(100)
                    ParsedResult.maybeAppend(calResult.getSummary(), result)
                    val start = calResult.getStart();
                    ParsedResult.maybeAppend(Util.format(calResult.isStartAllDay(), start), result)
                    var end = calResult.getEnd()
                    if (end != null) {
                        if (calResult.isEndAllDay() && !start.equals(end)) {
                            // Show only year/month/day
                            // if it's all-day and this is the end date, it's exclusive, so show the user
                            // that it ends on the day before to make more intuitive sense.
                            // But don't do it if the event already (incorrectly?) specifies the same start/end
                            end = Date(end.getTime() - 24 * 60 * 60 * 1000);
                        }
                        ParsedResult.maybeAppend(Util.format(calResult.isEndAllDay(), end), result)
                    }

                    ParsedResult.maybeAppend(calResult.getLocation(), result)
                    ParsedResult.maybeAppend(calResult.getOrganizer(), result)
                    ParsedResult.maybeAppend(calResult.getAttendees(), result)
                    ParsedResult.maybeAppend(calResult.getDescription(), result)
                    return result.toString();
                }
                SMS -> {
                    val smsResult = parsed as SMSParsedResult
                    val rawNumbers = smsResult.getNumbers()
                    val formattedNumbers = Array(rawNumbers.size()) { i ->
                        PhoneNumberUtils.formatNumber(rawNumbers[i])
                    }
                    val contents = StringBuilder(50)
                    ParsedResult.maybeAppend(formattedNumbers, contents)
                    ParsedResult.maybeAppend(smsResult.getSubject(), contents)
                    ParsedResult.maybeAppend(smsResult.getBody(), contents)
                    return contents.toString()
                }
                TEL -> {
                    val contents = parsed.getDisplayResult().replace("\r", "")
                    PhoneNumberUtils.formatNumber(contents);
                }
                WIFI -> {
                    val wifiResult = parsed as WifiParsedResult
                    wifiResult.getSsid() + " (" + wifiResult.getNetworkEncryption() + ')'
                }
                else -> {
                    parsed.getDisplayResult()
                }
            }
        }
    }

}