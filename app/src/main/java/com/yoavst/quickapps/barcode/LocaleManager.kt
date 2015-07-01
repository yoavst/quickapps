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

import android.content.Context
import java.util.Arrays
import java.util.HashMap
import java.util.Locale

/**
 * Handles any locale-specific logic for the client.
 * @author Sean Owen
 */
public object LocaleManager {

private val DEFAULT_TLD = "com"
private val DEFAULT_COUNTRY = "US"
private val DEFAULT_LANGUAGE = "en"

/**
 * Locales (well, countries) where Google web search is available.
 * These should be kept in sync with our translations.
 */
private val GOOGLE_COUNTRY_TLD: MutableMap<String, String>

init {
        GOOGLE_COUNTRY_TLD = HashMap<String, String>()
        GOOGLE_COUNTRY_TLD.put("AR", "com.ar") // ARGENTINA
        GOOGLE_COUNTRY_TLD.put("AU", "com.au") // AUSTRALIA
        GOOGLE_COUNTRY_TLD.put("BR", "com.br") // BRAZIL
        GOOGLE_COUNTRY_TLD.put("BG", "bg") // BULGARIA
        GOOGLE_COUNTRY_TLD.put(Locale.CANADA.getCountry(), "ca")
        GOOGLE_COUNTRY_TLD.put(Locale.CHINA.getCountry(), "cn")
        GOOGLE_COUNTRY_TLD.put("CZ", "cz") // CZECH REPUBLIC
        GOOGLE_COUNTRY_TLD.put("DK", "dk") // DENMARK
        GOOGLE_COUNTRY_TLD.put("FI", "fi") // FINLAND
        GOOGLE_COUNTRY_TLD.put(Locale.FRANCE.getCountry(), "fr")
        GOOGLE_COUNTRY_TLD.put(Locale.GERMANY.getCountry(), "de")
        GOOGLE_COUNTRY_TLD.put("GR", "gr") // GREECE
        GOOGLE_COUNTRY_TLD.put("HU", "hu") // HUNGARY
        GOOGLE_COUNTRY_TLD.put("ID", "co.id") // INDONESIA
        GOOGLE_COUNTRY_TLD.put("IL", "co.il") // ISRAEL
        GOOGLE_COUNTRY_TLD.put(Locale.ITALY.getCountry(), "it")
        GOOGLE_COUNTRY_TLD.put(Locale.JAPAN.getCountry(), "co.jp")
        GOOGLE_COUNTRY_TLD.put(Locale.KOREA.getCountry(), "co.kr")
        GOOGLE_COUNTRY_TLD.put("NL", "nl") // NETHERLANDS
        GOOGLE_COUNTRY_TLD.put("PL", "pl") // POLAND
        GOOGLE_COUNTRY_TLD.put("PT", "pt") // PORTUGAL
        GOOGLE_COUNTRY_TLD.put("RO", "ro") // ROMANIA
        GOOGLE_COUNTRY_TLD.put("RU", "ru") // RUSSIA
        GOOGLE_COUNTRY_TLD.put("SK", "sk") // SLOVAK REPUBLIC
        GOOGLE_COUNTRY_TLD.put("SI", "si") // SLOVENIA
        GOOGLE_COUNTRY_TLD.put("ES", "es") // SPAIN
        GOOGLE_COUNTRY_TLD.put("SE", "se") // SWEDEN
        GOOGLE_COUNTRY_TLD.put("CH", "ch") // SWITZERLAND
        GOOGLE_COUNTRY_TLD.put(Locale.TAIWAN.getCountry(), "tw")
        GOOGLE_COUNTRY_TLD.put("TR", "com.tr") // TURKEY
        GOOGLE_COUNTRY_TLD.put(Locale.UK.getCountry(), "co.uk")
        GOOGLE_COUNTRY_TLD.put(Locale.US.getCountry(), "com")
        }

/**
 * Google Product Search for mobile is available in fewer countries than web search. See here:
 * http://support.google.com/merchants/bin/answer.py?hl=en-GB&answer=160619
 */
private val GOOGLE_PRODUCT_SEARCH_COUNTRY_TLD: MutableMap<String, String>

init {
        GOOGLE_PRODUCT_SEARCH_COUNTRY_TLD = HashMap<String, String>()
        GOOGLE_PRODUCT_SEARCH_COUNTRY_TLD.put("AU", "com.au") // AUSTRALIA
        GOOGLE_PRODUCT_SEARCH_COUNTRY_TLD.put(Locale.FRANCE.getCountry(), "fr")
        GOOGLE_PRODUCT_SEARCH_COUNTRY_TLD.put(Locale.GERMANY.getCountry(), "de")
        GOOGLE_PRODUCT_SEARCH_COUNTRY_TLD.put(Locale.ITALY.getCountry(), "it")
        GOOGLE_PRODUCT_SEARCH_COUNTRY_TLD.put(Locale.JAPAN.getCountry(), "co.jp")
        GOOGLE_PRODUCT_SEARCH_COUNTRY_TLD.put("NL", "nl") // NETHERLANDS
        GOOGLE_PRODUCT_SEARCH_COUNTRY_TLD.put("ES", "es") // SPAIN
        GOOGLE_PRODUCT_SEARCH_COUNTRY_TLD.put("CH", "ch") // SWITZERLAND
        GOOGLE_PRODUCT_SEARCH_COUNTRY_TLD.put(Locale.UK.getCountry(), "co.uk")
        GOOGLE_PRODUCT_SEARCH_COUNTRY_TLD.put(Locale.US.getCountry(), "com")
        }

/**
 * Book search is offered everywhere that web search is available.
 */
private val GOOGLE_BOOK_SEARCH_COUNTRY_TLD = GOOGLE_COUNTRY_TLD

private val TRANSLATED_HELP_ASSET_LANGUAGES = Arrays.asList<String>("de", "en", "es", "fr", "it", "ja", "ko", "nl", "pt", "ru", "zh-rCN", "zh-rTW", "zh-rHK")

/**
 * @param context application's [Context]
 * *
 * @return country-specific TLD suffix appropriate for the current default locale
 * *  (e.g. "co.uk" for the United Kingdom)
 */
public fun getCountryTLD(): String {
        return doGetTLD(GOOGLE_COUNTRY_TLD)
        }

/**
 * The same as above, but specifically for Google Product Search.
 * @param context application's [Context]
 * *
 * @return The top-level domain to use.
 */
public fun getProductSearchCountryTLD(): String {
        return doGetTLD(GOOGLE_PRODUCT_SEARCH_COUNTRY_TLD)
        }

/**
 * The same as above, but specifically for Google Book Search.
 * @param context application's [Context]
 * *
 * @return The top-level domain to use.
 */
public fun getBookSearchCountryTLD(): String {
        return doGetTLD(GOOGLE_BOOK_SEARCH_COUNTRY_TLD)
        }

/**
 * Does a given URL point to Google Book Search, regardless of domain.
 * @param url The address to check.
 * *
 * @return True if this is a Book Search URL.
 */
public fun isBookSearchUrl(url: String): Boolean {
        return url.startsWith("http://google.com/books") || url.startsWith("http://books.google.")
        }

private fun getSystemCountry(): String {
        val locale = Locale.getDefault()
        return if (locale == null) DEFAULT_COUNTRY else locale.getCountry()
        }

private fun getSystemLanguage(): String {
        val locale = Locale.getDefault() ?: return DEFAULT_LANGUAGE
    val language = locale.getLanguage()
        // Special case Chinese
        if (Locale.SIMPLIFIED_CHINESE.getLanguage() == language) {
        return language + "-r" + getSystemCountry()
        }
        return language
        }

public fun getTranslatedAssetLanguage(): String {
        val language = getSystemLanguage()
        return if (TRANSLATED_HELP_ASSET_LANGUAGES.contains(language)) language else DEFAULT_LANGUAGE
        }

private fun doGetTLD(map: Map<String, String>): String {
    return map.get(getCountry()) ?: DEFAULT_TLD
        }

public fun getCountry(): String {
        return getSystemCountry()
        }
        }
