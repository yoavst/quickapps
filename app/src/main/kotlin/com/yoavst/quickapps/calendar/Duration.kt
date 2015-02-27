/*
** Copyright 2006, The Android Open Source Project
**
** Licensed under the Apache License, Version 2.0 (the "License");
** you may not use this file except in compliance with the License.
** You may obtain a copy of the License at
**
**     http://www.apache.org/licenses/LICENSE-2.0
**
** Unless required by applicable law or agreed to in writing, software
** distributed under the License is distributed on an "AS IS" BASIS,
** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
** See the License for the specific language governing permissions and
** limitations under the License.
*/
package com.yoavst.quickapps.calendar

import java.util.Calendar

/**
 * According to RFC2445, durations are like this:
 * WEEKS
 * | DAYS [ HOURS [ MINUTES [ SECONDS ] ] ]
 * | HOURS [ MINUTES [ SECONDS ] ]
 * it doesn't specifically, say, but this sort of implies that you can't have
 * 70 seconds.
 */
public class Duration {
    public var sign: Int = 1 // 1 or -1
    public var weeks: Int = 0
    public var days: Int = 0
    public var hours: Int = 0
    public var minutes: Int = 0
    public var seconds: Int = 0


    /**
     * Parse according to RFC2445 ss4.3.6.  (It's actually a little loose with
     * its parsing, for better or for worse)
     */
    throws(javaClass<Exception>())
    public fun parse(str: String) {
        sign = 1
        weeks = 0
        days = 0
        hours = 0
        minutes = 0
        seconds = 0
        val len = str.length()
        var index = 0
        var c: Char
        if (len < 1) {
            return
        }
        c = str.charAt(0)
        if (c == '-') {
            sign = -1
            index++
        } else if (c == '+') {
            index++
        }
        if (len < index) {
            return
        }
        c = str.charAt(index)
        if (c != 'P') {
            throw Exception("Duration.parse(str='" + str + "') expected 'P' at index=" + index)
        }
        index++
        c = str.charAt(index)
        if (c == 'T') {
            index++
        }
        var n = 0
        while (index < len) {
            c = str.charAt(index)
            if (c >= '0' && c <= '9') {
                n *= 10
                n += ((c.toInt() - '0').toInt())
            } else if (c == 'W') {
                weeks = n
                n = 0
            } else if (c == 'H') {
                hours = n
                n = 0
            } else if (c == 'M') {
                minutes = n
                n = 0
            } else if (c == 'S') {
                seconds = n
                n = 0
            } else if (c == 'D') {
                days = n
                n = 0
            } else if (c == 'T') {
            } else {
                throw Exception("Duration.parse(str='" + str + "') unexpected char '" + c + "' at index=" + index)
            }
            index++
        }
    }

    /**
     * Add this to the calendar provided, in place, in the calendar.
     */
    public fun addTo(cal: Calendar) {
        cal.add(Calendar.DAY_OF_MONTH, sign * weeks * 7)
        cal.add(Calendar.DAY_OF_MONTH, sign * days)
        cal.add(Calendar.HOUR, sign * hours)
        cal.add(Calendar.MINUTE, sign * minutes)
        cal.add(Calendar.SECOND, sign * seconds)
    }

    public fun addTo(dt: Long): Long {
        return dt + getMillis()
    }

    public fun getMillis(): Long {
        val factor = (1000 * sign).toLong()
        return factor * ((7 * 24 * 60 * 60 * weeks) + (24 * 60 * 60 * days) + (60 * 60 * hours) + (60 * minutes) + seconds).toLong()
    }
}
