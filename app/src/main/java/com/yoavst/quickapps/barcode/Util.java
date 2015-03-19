package com.yoavst.quickapps.barcode;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;

import com.google.zxing.client.result.AddressBookParsedResult;
import com.google.zxing.client.result.ParsedResult;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by yoavst.
 */
public class Util {
	private static final String[] EMAIL_TYPE_STRINGS = {"home", "work", "mobile"};
	private static final String[] PHONE_TYPE_STRINGS = {"home", "work", "mobile", "fax", "pager", "main"};
	private static final String[] ADDRESS_TYPE_STRINGS = {"home", "work"};
	private static final int NO_TYPE = -1;
	private static final int[] EMAIL_TYPE_VALUES = {
			ContactsContract.CommonDataKinds.Email.TYPE_HOME,
			ContactsContract.CommonDataKinds.Email.TYPE_WORK,
			ContactsContract.CommonDataKinds.Email.TYPE_MOBILE,
	};
	private static final int[] PHONE_TYPE_VALUES = {
			ContactsContract.CommonDataKinds.Phone.TYPE_HOME,
			ContactsContract.CommonDataKinds.Phone.TYPE_WORK,
			ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,
			ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK,
			ContactsContract.CommonDataKinds.Phone.TYPE_PAGER,
			ContactsContract.CommonDataKinds.Phone.TYPE_MAIN,
	};
	private static final int[] ADDRESS_TYPE_VALUES = {
			ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME,
			ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK,
	};

	public static Intent addPhoneOnlyContact(String[] phoneNumbers,String[] phoneTypes) {
		return addContact(null, null, null, phoneNumbers, phoneTypes, null, null, null, null, null, null, null, null, null, null, null);
	}

	public static Intent addEmailOnlyContact(String[] emails, String[] emailTypes) {
		return addContact(null, null, null, null, null, emails, emailTypes, null, null, null, null, null, null, null, null, null);
	}

	public static Intent addContact(String[] names,
	                      String[] nicknames,
	                      String pronunciation,
	                      String[] phoneNumbers,
	                      String[] phoneTypes,
	                      String[] emails,
	                      String[] emailTypes,
	                      String note,
	                      String instantMessenger,
	                      String address,
	                      String addressType,
	                      String org,
	                      String title,
	                      String[] urls,
	                      String birthday,
	                      String[] geo) {

		// Only use the first name in the array, if present.
		Intent intent = new Intent(Intent.ACTION_INSERT_OR_EDIT, ContactsContract.Contacts.CONTENT_URI);
		intent.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);
		putExtra(intent, ContactsContract.Intents.Insert.NAME, names != null ? names[0] : null);

		putExtra(intent, ContactsContract.Intents.Insert.PHONETIC_NAME, pronunciation);

		int phoneCount = Math.min(phoneNumbers != null ? phoneNumbers.length : 0, Contents.PHONE_KEYS.length);
		for (int x = 0; x < phoneCount; x++) {
			putExtra(intent, Contents.PHONE_KEYS[x], phoneNumbers[x]);
			if (phoneTypes != null && x < phoneTypes.length) {
				int type = toPhoneContractType(phoneTypes[x]);
				if (type >= 0) {
					intent.putExtra(Contents.PHONE_TYPE_KEYS[x], type);
				}
			}
		}

		int emailCount = Math.min(emails != null ? emails.length : 0, Contents.EMAIL_KEYS.length);
		for (int x = 0; x < emailCount; x++) {
			putExtra(intent, Contents.EMAIL_KEYS[x], emails[x]);
			if (emailTypes != null && x < emailTypes.length) {
				int type = toEmailContractType(emailTypes[x]);
				if (type >= 0) {
					intent.putExtra(Contents.EMAIL_TYPE_KEYS[x], type);
				}
			}
		}

		ArrayList<ContentValues> data = new ArrayList<>();
		if (urls != null) {
			for (String url : urls) {
				if (url != null && !url.isEmpty()) {
					ContentValues row = new ContentValues(2);
					row.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE);
					row.put(ContactsContract.CommonDataKinds.Website.URL, url);
					data.add(row);
					break;
				}
			}
		}

		if (birthday != null) {
			ContentValues row = new ContentValues(3);
			row.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE);
			row.put(ContactsContract.CommonDataKinds.Event.TYPE, ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY);
			row.put(ContactsContract.CommonDataKinds.Event.START_DATE, birthday);
			data.add(row);
		}

		if (nicknames != null) {
			for (String nickname : nicknames) {
				if (nickname != null && !nickname.isEmpty()) {
					ContentValues row = new ContentValues(3);
					row.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE);
					row.put(ContactsContract.CommonDataKinds.Nickname.TYPE,
							ContactsContract.CommonDataKinds.Nickname.TYPE_DEFAULT);
					row.put(ContactsContract.CommonDataKinds.Nickname.NAME, nickname);
					data.add(row);
					break;
				}
			}
		}

		if (!data.isEmpty()) {
			intent.putParcelableArrayListExtra(ContactsContract.Intents.Insert.DATA, data);
		}

		StringBuilder aggregatedNotes = new StringBuilder();
		if (note != null) {
			aggregatedNotes.append('\n').append(note);
		}
		if (geo != null) {
			aggregatedNotes.append('\n').append(geo[0]).append(',').append(geo[1]);
		}

		if (aggregatedNotes.length() > 0) {
			// Remove extra leading '\n'
			putExtra(intent, ContactsContract.Intents.Insert.NOTES, aggregatedNotes.substring(1));
		}

		putExtra(intent, ContactsContract.Intents.Insert.IM_HANDLE, instantMessenger);
		putExtra(intent, ContactsContract.Intents.Insert.POSTAL, address);
		if (addressType != null) {
			int type = toAddressContractType(addressType);
			if (type >= 0) {
				intent.putExtra(ContactsContract.Intents.Insert.POSTAL_TYPE, type);
			}
		}
		putExtra(intent, ContactsContract.Intents.Insert.COMPANY, org);
		putExtra(intent, ContactsContract.Intents.Insert.JOB_TITLE, title);
		return intent;
	}

	private static int toEmailContractType(String typeString) {
		return doToContractType(typeString, EMAIL_TYPE_STRINGS, EMAIL_TYPE_VALUES);
	}

	private static int toPhoneContractType(String typeString) {
		return doToContractType(typeString, PHONE_TYPE_STRINGS, PHONE_TYPE_VALUES);
	}

	private static int toAddressContractType(String typeString) {
		return doToContractType(typeString, ADDRESS_TYPE_STRINGS, ADDRESS_TYPE_VALUES);
	}

	private static int doToContractType(String typeString, String[] types, int[] values) {
		if (typeString == null) {
			return NO_TYPE;
		}
		for (int i = 0; i < types.length; i++) {
			String type = types[i];
			if (typeString.startsWith(type) || typeString.startsWith(type.toUpperCase(Locale.ENGLISH))) {
				return values[i];
			}
		}
		return NO_TYPE;
	}

	private static void putExtra(Intent intent, String key, String value) {
		if (value != null && !value.isEmpty()) {
			intent.putExtra(key, value);
		}
	}

	public static String format(boolean allDay, Date date) {
		if (date == null) {
			return null;
		}
		DateFormat format = allDay
				? DateFormat.getDateInstance(DateFormat.MEDIUM)
				: DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
		return format.format(date);
	}
	public static CharSequence formatContact(ParsedResult results) {
		AddressBookParsedResult result = (AddressBookParsedResult) results;
		StringBuilder contents = new StringBuilder(100);
		ParsedResult.maybeAppend(result.getNames(), contents);
		int namesLength = contents.length();

		String pronunciation = result.getPronunciation();
		if (pronunciation != null && !pronunciation.isEmpty()) {
			contents.append("\n(");
			contents.append(pronunciation);
			contents.append(')');
		}

		ParsedResult.maybeAppend(result.getTitle(), contents);
		ParsedResult.maybeAppend(result.getOrg(), contents);
		ParsedResult.maybeAppend(result.getAddresses(), contents);
		String[] numbers = result.getPhoneNumbers();
		if (numbers != null) {
			for (String number : numbers) {
				if (number != null) {
					ParsedResult.maybeAppend(PhoneNumberUtils.formatNumber(number), contents);
				}
			}
		}
		ParsedResult.maybeAppend(result.getEmails(), contents);
		ParsedResult.maybeAppend(result.getURLs(), contents);

		String birthday = result.getBirthday();
		if (birthday != null && !birthday.isEmpty()) {
			Date date = parseDate(birthday);
			if (date != null) {
				ParsedResult.maybeAppend(DateFormat.getDateInstance(DateFormat.MEDIUM).format(date.getTime()), contents);
			}
		}
		ParsedResult.maybeAppend(result.getNote(), contents);

		if (namesLength > 0) {
			// Bold the full name to make it stand out a bit.
			Spannable styled = new SpannableString(contents.toString());
			styled.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, namesLength, 0);
			return styled;
		} else {
			return contents.toString();
		}
	}

	private static Date parseDate(String s) {
		for (DateFormat currentFormat : DATE_FORMATS) {
			try {
				return currentFormat.parse(s);
			} catch (ParseException e) {
				// continue
			}
		}
		return null;
	}

	private static final DateFormat[] DATE_FORMATS = {
			new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH),
			new SimpleDateFormat("yyyyMMdd'T'HHmmss", Locale.ENGLISH),
			new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH),
			new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH),
	};
	static {
		for (DateFormat format : DATE_FORMATS) {
			format.setLenient(false);
		}
	}

}
