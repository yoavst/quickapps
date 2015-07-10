package com.yoavst.quickapps.calendar;

import android.annotation.SuppressLint;
import android.content.Context;

import com.yoavst.quickapps.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static com.yoavst.quickapps.calendar.CalendarPackage.isSameDay;
import static com.yoavst.quickapps.calendar.CalendarPackage.isToday;
import static com.yoavst.quickapps.calendar.CalendarPackage.isTomorrow;
import static com.yoavst.quickapps.calendar.CalendarPackage.isWithinDaysFuture;

/**
 * Created by Yoav.
 */
@SuppressLint("SimpleDateFormat")
public class CalendarUtil {
    private CalendarUtil() {
    }

    private static final SimpleDateFormat dayFormatter = new SimpleDateFormat(
            "EEE, MMM d, yyyy");
    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat(
            "EEE, MMM d, HH:mm");
    private static final SimpleDateFormat dateFormatterAmPm = new SimpleDateFormat(
            "EEE, MMM d, hh:mm");
    private static final SimpleDateFormat dateFormatterAmPmNotSame = new SimpleDateFormat(
            "EEE, MMM d, hh:mm a", Locale.US);
    private static final SimpleDateFormat hourFormatter = new SimpleDateFormat("HH:mm");
    private static final SimpleDateFormat hourFormatterAmPmSame = new SimpleDateFormat("hh:mm", Locale.US);
    private static final SimpleDateFormat hourFormatterAmPm = new SimpleDateFormat("hh:mm a", Locale.US);
    private static final SimpleDateFormat fullDateFormat = new SimpleDateFormat("EEE, MMM d");
    private static final SimpleDateFormat otherDayFormatter = new SimpleDateFormat("MMM d, HH:mm");
    private static final SimpleDateFormat otherDayFormatterAmPm = new SimpleDateFormat("MMM d, hh:mm a");

    public static String getDateFromEvent(Event event, boolean useAmPm) {
        if (event.allDay) {
            Calendar startPlusOneDay = calendarByMillis(event.getStartMillis());
            startPlusOneDay.add(Calendar.DAY_OF_YEAR, 1);
            Calendar endTime = calendarByMillis(event.getEndMillis());
            if (isSameDay(startPlusOneDay, endTime)) {
                startPlusOneDay.add(Calendar.DAY_OF_YEAR, -1);
                if (isToday(startPlusOneDay))
                    return CalendarResources.today + " " + CalendarResources.allDay;
                else if (isTomorrow(startPlusOneDay))
                    return CalendarResources.tomorrow + " " + CalendarResources.allDay;
                return dayFormatter.format(new Date(event.getStartMillis()));
            } else {
                endTime.add(Calendar.DAY_OF_YEAR, -1);
                startPlusOneDay.add(Calendar.DAY_OF_YEAR, -1);
                if (isToday(startPlusOneDay)) {
                    if (isTomorrow(endTime))
                        return CalendarResources.today + " - " + CalendarResources.tomorrow;
                    else
                        return CalendarResources.today + " " + CalendarResources.allDay + " - " + fullDateFormat.format(endTime.getTime());
                } else if (isTomorrow(startPlusOneDay))
                    return CalendarResources.tomorrow + " - " + fullDateFormat.format(endTime.getTime());
                return fullDateFormat.format(new Date(event.getStartMillis())) + " - " + fullDateFormat.format(endTime.getTime());
            }
        } else {
            String text;
            Date first = new Date(event.getStartMillis());
            Date end = new Date(event.getEndMillis());
            if (isSameDay(first, end)) {
                if (isToday(first)) {
                    if (useAmPm) {
                        if (isSameAmPm(event.getStartMillis(), event.getEndMillis()))
                            text = CalendarResources.today + " " + hourFormatterAmPmSame.format(first) + " - " + hourFormatterAmPm.format(end);
                        else
                            text = CalendarResources.today + " " + hourFormatterAmPm.format(first) + " - " + hourFormatterAmPm.format(end);
                    } else
                        text = CalendarResources.today + " " + hourFormatter.format(first) + " - " + hourFormatter.format(end);
                } else if (isWithinDaysFuture(first, 1)) {
                    if (useAmPm) {
                        if (isSameAmPm(event.getStartMillis(), event.getEndMillis()))
                            text = CalendarResources.tomorrow + " " + hourFormatterAmPmSame.format(first) + " - " + hourFormatterAmPm.format(end);
                        else
                            text = CalendarResources.tomorrow + " " + hourFormatterAmPm.format(first) + " - " + hourFormatterAmPm.format(end);
                    } else
                        text = CalendarResources.tomorrow + " " + hourFormatter.format(first) + " - " + hourFormatter.format(end);
                } else {
                    if (useAmPm) {
                        if (isSameAmPm(event.getStartMillis(), event.getEndMillis()))
                            text = dateFormatterAmPm.format(first) + " - " + hourFormatterAmPm.format(end);
                        else
                            text = dateFormatterAmPmNotSame.format(first) + " - " + hourFormatterAmPm.format(end);
                    } else text = dateFormatter.format(first) + " - " + hourFormatter.format(end);
                }
            } else if (isToday(first)) {
                if (useAmPm) {
                    if (isSameAmPm(event.getStartMillis(), event.getEndMillis()))
                        text = CalendarResources.today + hourFormatterAmPmSame.format(first) + " - " + hourFormatterAmPm.format(end);
                    else
                        text = CalendarResources.today + hourFormatterAmPm.format(first) + " - " + hourFormatterAmPm.format(end);
                } else
                    text = CalendarResources.today + hourFormatter.format(first) + " - " + otherDayFormatter.format(end);
            } else
                text = getOtherDayFormatter(useAmPm).format(first) + " - " + getOtherDayFormatter(useAmPm).format(end);
            return text;
        }
    }

    private static boolean isSameAmPm(long start, long end) {
        Calendar starting = calendarByMillis(start);
        Calendar ending = calendarByMillis(end);
        return starting.get(Calendar.AM_PM) == ending.get(Calendar.AM_PM);
    }

    private static Calendar calendarByMillis(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return calendar;
    }

    private static SimpleDateFormat getOtherDayFormatter(boolean useAmPm) {
        return useAmPm ? otherDayFormatterAmPm : otherDayFormatter;
    }

    public static String getTimeToEvent(Event event) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(event.getStartMillis());
        Calendar now = Calendar.getInstance();
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);
        if (calendar.getTimeInMillis() <= now.getTimeInMillis()) return CalendarResources.now;
        else {
            long secondsLeft = (calendar.getTimeInMillis() - now.getTimeInMillis()) / 1000;
            if (secondsLeft < 60) return CalendarResources.in + " 1 " + CalendarResources.minute;
            long minutesLeft = TimeUnit.SECONDS.toMinutes(secondsLeft);
            if (minutesLeft < 60)
                return CalendarResources.in + " " + minutesLeft + " " + (minutesLeft > 1 ? CalendarResources.minutes : CalendarResources.minute);
            long hoursLeft = TimeUnit.MINUTES.toHours(minutesLeft);
            if (hoursLeft < 24)
                return CalendarResources.in + " " + hoursLeft + " " + (hoursLeft > 1 ? CalendarResources.hours : CalendarResources.hour);
            int days = (int) TimeUnit.HOURS.toDays(hoursLeft);
            if (days < 30)
                return CalendarResources.in + " " + days + " " + (days > 1 ? CalendarResources.days : CalendarResources.day);
            int months = days / 30;
            if (months < 12)
                return CalendarResources.in + " " + months + " " + (months > 1 ? CalendarResources.months : CalendarResources.month);
            else return CalendarResources.moreThenAYearLeft;
        }
    }

    public static class CalendarResources {
        public static String today;
        public static String tomorrow;
        public static String allDay;
        public static String now;
        public static String in;
        public static String minute;
        public static String minutes;
        public static String hour;
        public static String hours;
        public static String day;
        public static String days;
        public static String week;
        public static String weeks;
        public static String month;
        public static String months;
        public static String moreThenAYearLeft;

        public static void init(Context context) {
            context = context.getApplicationContext();
            if (today == null || moreThenAYearLeft == null) {
                today = context.getString(R.string.today);
                tomorrow = context.getString(R.string.tomorrow);
                allDay = context.getString(R.string.all_day);
                now = context.getString(R.string.now);
                in = context.getString(R.string.in);
                minute = context.getResources().getQuantityString(R.plurals.minute, 1);
                minutes = context.getResources().getQuantityString(R.plurals.minute, 2);
                hour = context.getResources().getQuantityString(R.plurals.hour, 1);
                hours = context.getResources().getQuantityString(R.plurals.hour, 2);
                day = context.getResources().getQuantityString(R.plurals.day, 1);
                days = context.getResources().getQuantityString(R.plurals.day, 2);
                week = context.getResources().getQuantityString(R.plurals.week, 1);
                weeks = context.getResources().getQuantityString(R.plurals.week, 2);
                month = context.getResources().getQuantityString(R.plurals.month, 1);
                months = context.getResources().getQuantityString(R.plurals.month, 2);
                moreThenAYearLeft = context.getString(R.string.more_then_year);
            }
        }
    }

}
