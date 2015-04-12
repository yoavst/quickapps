/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yoavst.quickapps.barcode

import android.provider.ContactsContract

/**
 * The set of constants to use when sending Barcode Scanner an Intent which requests a barcode
 * to be encoded.

 * @author dswitkin@google.com (Daniel Switkin)
 */
public object Contents {

    public object Type {
        /**
         * Plain text. Use Intent.putExtra(DATA, string). This can be used for URLs too, but string
         * must include "http://" or "https://".
         */
        public val TEXT: String = "TEXT_TYPE"

        /**
         * An email type. Use Intent.putExtra(DATA, string) where string is the email address.
         */
        public val EMAIL: String = "EMAIL_TYPE"

        /**
         * Use Intent.putExtra(DATA, string) where string is the phone number to call.
         */
        public val PHONE: String = "PHONE_TYPE"

        /**
         * An SMS type. Use Intent.putExtra(DATA, string) where string is the number to SMS.
         */
        public val SMS: String = "SMS_TYPE"

        /**
         * A contact. Send a request to encode it as follows:
         * `import android.provider.Contacts;

         * Intent intent = new Intent(Intents.Encode.ACTION);
         * intent.putExtra(Intents.Encode.TYPE, CONTACT);
         * Bundle bundle = new Bundle();
         * bundle.putString(ContactsContract.Intents.Insert.NAME, &quot;Jenny&quot;);
         * bundle.putString(ContactsContract.Intents.Insert.PHONE, &quot;8675309&quot;);
         * bundle.putString(ContactsContract.Intents.Insert.EMAIL, &quot;jenny@the80s.com&quot;);
         * bundle.putString(ContactsContract.Intents.Insert.POSTAL, &quot;123 Fake St. San Francisco, CA 94102&quot;);
         * intent.putExtra(Intents.Encode.DATA, bundle);
        ` *
         */
        public val CONTACT: String = "CONTACT_TYPE"

        /**
         * A geographic location. Use as follows:
         * Bundle bundle = new Bundle();
         * bundle.putFloat("LAT", latitude);
         * bundle.putFloat("LONG", longitude);
         * intent.putExtra(Intents.Encode.DATA, bundle);
         */
        public val LOCATION: String = "LOCATION_TYPE"
    }

    public val URL_KEY: String = "URL_KEY"

    public val NOTE_KEY: String = "NOTE_KEY"

    /**
     * When using Type.CONTACT, these arrays provide the keys for adding or retrieving multiple
     * phone numbers and addresses.
     */
    public val PHONE_KEYS: Array<String> = array(ContactsContract.Intents.Insert.PHONE, ContactsContract.Intents.Insert.SECONDARY_PHONE, ContactsContract.Intents.Insert.TERTIARY_PHONE)

    public val PHONE_TYPE_KEYS: Array<String> = array(ContactsContract.Intents.Insert.PHONE_TYPE, ContactsContract.Intents.Insert.SECONDARY_PHONE_TYPE, ContactsContract.Intents.Insert.TERTIARY_PHONE_TYPE)

    public val EMAIL_KEYS: Array<String> = array(ContactsContract.Intents.Insert.EMAIL, ContactsContract.Intents.Insert.SECONDARY_EMAIL, ContactsContract.Intents.Insert.TERTIARY_EMAIL)

    public val EMAIL_TYPE_KEYS: Array<String> = array(ContactsContract.Intents.Insert.EMAIL_TYPE, ContactsContract.Intents.Insert.SECONDARY_EMAIL_TYPE, ContactsContract.Intents.Insert.TERTIARY_EMAIL_TYPE)

}