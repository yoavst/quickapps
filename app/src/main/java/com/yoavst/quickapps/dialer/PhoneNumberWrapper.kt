package com.yoavst.quickapps.dialer

import com.google.i18n.phonenumbers.Phonenumber

public data class PhoneNumberWrapper(val name: String, var number: String? = null, val parsed: Phonenumber.PhoneNumber? = null)