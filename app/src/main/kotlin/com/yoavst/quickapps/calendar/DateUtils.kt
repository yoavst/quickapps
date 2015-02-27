package com.yoavst.quickapps.calendar

import java.util.Calendar
import java.util.Date

/**
 * <p>Checks if two dates are on the same day ignoring time.</p>
 *
 * @param this the first date, not altered, not null
 * @param date2 the second date, not altered, not null
 * @return true if they represent the same day
 * @throws IllegalArgumentException if either date is <code>null</code>
 */
public fun Date?.isSameDay(date2: Date?): Boolean {
    if (this == null || date2 == null) {
        throw IllegalArgumentException("The dates must not be null")
    }
    val cal1 = Calendar.getInstance()
    cal1.setTime(this)
    val cal2 = Calendar.getInstance()
    cal2.setTime(date2)
    return cal1.isSameDay(cal2)
}

/**
 * <p>Checks if two calendars represent the same day ignoring time.</p>
 *
 * @param this the first calendar, not altered, not null
 * @param cal2 the second calendar, not altered, not null
 * @return true if they represent the same day
 * @throws IllegalArgumentException if either calendar is <code>null</code>
 */
public fun Calendar?.isSameDay(cal2: Calendar?): Boolean {
    if (this == null || cal2 == null) {
        throw IllegalArgumentException("The dates must not be null")
    }
    return (this.get(Calendar.ERA) == cal2.get(Calendar.ERA) && this.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && this.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR))
}

/**
 * <p>Checks if a date is today.</p>
 *
 * @param this the date, not altered, not null.
 * @return true if the date is today.
 * @throws IllegalArgumentException if the date is <code>null</code>
 */
public fun Date.isToday(): Boolean {
    return this.isSameDay(Calendar.getInstance().getTime())
}

/**
 * <p>Checks if a calendar date is today.</p>
 *
 * @param this the calendar, not altered, not null
 * @return true if cal date is today
 * @throws IllegalArgumentException if the calendar is <code>null</code>
 */
public fun Calendar.isToday(): Boolean {
    return this.isSameDay(Calendar.getInstance())
}

/**
 * <p>Checks if this is before the second date ignoring time.</p>
 *
 * @param this the first date, not altered, not null
 * @param date2 the second date, not altered, not null
 * @return true if the first date day is before the second date day.
 * @throws IllegalArgumentException if the date is <code>null</code>
 */
public fun Date?.isBeforeDay(date2: Date?): Boolean {
    if (this == null || date2 == null) {
        throw IllegalArgumentException("The dates must not be null")
    }
    val cal1 = Calendar.getInstance()
    cal1.setTime(this)
    val cal2 = Calendar.getInstance()
    cal2.setTime(date2)
    return cal1.isBeforeDay(cal2)
}

/**
 * <p>Checks if the this is before the second calendar date ignoring time.</p>
 *
 * @param this the first calendar, not altered, not null.
 * @param cal2 the second calendar, not altered, not null.
 * @return true if cal1 date is before cal2 date ignoring time.
 * @throws IllegalArgumentException if either of the calendars are <code>null</code>
 */
public fun Calendar?.isBeforeDay(cal2: Calendar?): Boolean {
    if (this == null || cal2 == null) {
        throw IllegalArgumentException("The dates must not be null")
    }
    return this.get(Calendar.ERA) < cal2.get(Calendar.ERA) ||
            this.get(Calendar.ERA) <= cal2.get(Calendar.ERA) &&
                    (this.get(Calendar.YEAR) < cal2.get(Calendar.YEAR)
                            || this.get(Calendar.YEAR) <= cal2.get(Calendar.YEAR) &&
                            this.get(Calendar.DAY_OF_YEAR) < cal2.get(Calendar.DAY_OF_YEAR))
}

/**
 * <p>Checks if the first date is after the second date ignoring time.</p>
 *
 * @param date1 the first date, not altered, not null
 * @param date2 the second date, not altered, not null
 * @return true if the first date day is after the second date day.
 * @throws IllegalArgumentException if the date is <code>null</code>
 */
public fun isAfterDay(date1: Date?, date2: Date?): Boolean {
    if (date1 == null || date2 == null) {
        throw IllegalArgumentException("The dates must not be null")
    }
    val cal1 = Calendar.getInstance()
    cal1.setTime(date1)
    val cal2 = Calendar.getInstance()
    cal2.setTime(date2)
    return isAfterDay(cal1, cal2)
}

/**
 * <p>Checks if the first calendar date is after the second calendar date ignoring time.</p>
 *
 * @param cal1 the first calendar, not altered, not null.
 * @param cal2 the second calendar, not altered, not null.
 * @return true if cal1 date is after cal2 date ignoring time.
 * @throws IllegalArgumentException if either of the calendars are <code>null</code>
 */
public fun isAfterDay(cal1: Calendar?, cal2: Calendar?): Boolean {
    if (cal1 == null || cal2 == null) {
        throw IllegalArgumentException("The dates must not be null")
    }
    return cal1.get(Calendar.ERA) >= cal2.get(Calendar.ERA) && (cal1.get(Calendar.ERA) > cal2.get(Calendar.ERA) || cal1.get(Calendar.YEAR) >= cal2.get(Calendar.YEAR) && (cal1.get(Calendar.YEAR) > cal2.get(Calendar.YEAR) || cal1.get(Calendar.DAY_OF_YEAR) > cal2.get(Calendar.DAY_OF_YEAR)))
}

/**
 * <p>Checks if a date is after today and within a number of days in the future.</p>
 *
 * @param date the date to check, not altered, not null.
 * @param days the number of days.
 * @return true if the date day is after today and within days in the future .
 * @throws IllegalArgumentException if the date is <code>null</code>
 */
public fun isWithinDaysFuture(date: Date?, days: Int): Boolean {
    if (date == null) {
        throw IllegalArgumentException("The date must not be null")
    }
    val cal = Calendar.getInstance()
    cal.setTime(date)
    return isWithinDaysFuture(cal, days)
}

/**
 * <p>Checks if a calendar date is after today and within a number of days in the future.</p>
 *
 * @param cal  the calendar, not altered, not null
 * @param days the number of days.
 * @return true if the calendar date day is after today and within days in the future .
 * @throws IllegalArgumentException if the calendar is <code>null</code>
 */
public fun isWithinDaysFuture(cal: Calendar?, days: Int): Boolean {
    if (cal == null) {
        throw IllegalArgumentException("The date must not be null")
    }
    val today = Calendar.getInstance()
    val future = Calendar.getInstance()
    future.add(Calendar.DAY_OF_YEAR, days)
    return (isAfterDay(cal, today) && !isAfterDay(cal, future))
}

/**
 * Returns the given date with the time set to the start of the day.
 */
public fun getStart(date: Date): Date {
    return date.clearTime() as Date
}

/**
 * Returns the given date with the time values cleared.
 */
public fun Date?.clearTime(): Date? {
    if (this == null) {
        return null
    }
    val c = Calendar.getInstance()
    c.setTime(this)
    c.set(Calendar.HOUR_OF_DAY, 0)
    c.set(Calendar.MINUTE, 0)
    c.set(Calendar.SECOND, 0)
    c.set(Calendar.MILLISECOND, 0)
    return c.getTime()
}

/**
 * Returns the given date with the time values cleared.
 */
public fun Calendar?.clearTime(): Calendar? {
    if (this == null) {
        return null
    }
    this.set(Calendar.HOUR_OF_DAY, 0)
    this.set(Calendar.MINUTE, 0)
    this.set(Calendar.SECOND, 0)
    this.set(Calendar.MILLISECOND, 0)
    return this
}
/** Determines whether or not a date has any time values (hour, minute,
 * seconds or millisecondsReturns the given date with the time values cleared. */

/**
 * Determines whether or not a date has any time values.
 *
 * @param date The date.
 * @return true iff the date is not null and any of the date's hour, minute,
 * seconds or millisecond values are greater than zero.
 */
public fun Date?.hasTime(): Boolean {
    if (this == null) {
        return false
    }
    val c = Calendar.getInstance()
    c.setTime(this)
    if (c.get(Calendar.HOUR_OF_DAY) > 0) {
        return true
    }
    if (c.get(Calendar.MINUTE) > 0) {
        return true
    }
    if (c.get(Calendar.SECOND) > 0) {
        return true
    }
    if (c.get(Calendar.MILLISECOND) > 0) {
        return true
    }
    return false
}

/**
 * Returns the given date with time set to the end of the day
 */
public fun Date?.getEnd(): Date? {
    if (this == null) {
        return null
    }
    val c = Calendar.getInstance()
    c.setTime(this)
    c.set(Calendar.HOUR_OF_DAY, 23)
    c.set(Calendar.MINUTE, 59)
    c.set(Calendar.SECOND, 59)
    c.set(Calendar.MILLISECOND, 999)
    return c.getTime()
}

/**
 * Returns the maximum of two dates. A null date is treated as being less
 * than any non-null date.
 */
public fun Date?.max(d2: Date?): Date? {
    if (this == null && d2 == null) return null
    if (this == null) return d2
    if (d2 == null) return this
    return if ((this.after(d2))) this else d2
}

/**
 * Returns the minimum of two dates. A null date is treated as being greater
 * than any non-null date.
 */
public fun Date?.min(d2: Date?): Date? {
    if (this == null && d2 == null) return null
    if (this == null) return d2
    if (d2 == null) return this
    return if ((this.before(d2))) this else d2
}

/**
 * The maximum date possible.
 */
public var MAX_DATE: Date = Date(java.lang.Long.MAX_VALUE)

public fun Calendar?.isTomorrow(): Boolean {
    val tomorrow = Calendar.getInstance()
    tomorrow.add(Calendar.DAY_OF_YEAR, 1)
    return tomorrow.isSameDay(if (this == null) Calendar.getInstance() else this)
}

public fun Calendar?.isYesterday(): Boolean {
    val yesterday = Calendar.getInstance()
    yesterday.add(Calendar.DAY_OF_YEAR, -1)
    return yesterday.isSameDay(if (this == null) Calendar.getInstance() else this)
}


