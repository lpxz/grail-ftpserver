package org.apache.ftpserver.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DateUtils {

    private static final TimeZone TIME_ZONE_UTC = TimeZone.getTimeZone("UTC");

    private static final String[] MONTHS = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };

    private static final ThreadLocal<DateFormat> FTP_DATE_FORMAT = new ThreadLocal<DateFormat>() {

        @Override
        protected DateFormat initialValue() {
            DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
            df.setLenient(false);
            df.setTimeZone(TimeZone.getTimeZone("GMT"));
            return df;
        }
    };

    public static final String getUnixDate(long millis) {
        if (millis < 0) {
            return "------------";
        }
        StringBuilder sb = new StringBuilder(16);
        Calendar cal = new GregorianCalendar();
        cal.setTimeInMillis(millis);
        sb.append(MONTHS[cal.get(Calendar.MONTH)]);
        sb.append(' ');
        int day = cal.get(Calendar.DATE);
        if (day < 10) {
            sb.append(' ');
        }
        sb.append(day);
        sb.append(' ');
        long sixMonth = 15811200000L;
        long nowTime = System.currentTimeMillis();
        if (Math.abs(nowTime - millis) > sixMonth) {
            int year = cal.get(Calendar.YEAR);
            sb.append(' ');
            sb.append(year);
        } else {
            int hh = cal.get(Calendar.HOUR_OF_DAY);
            if (hh < 10) {
                sb.append('0');
            }
            sb.append(hh);
            sb.append(':');
            int mm = cal.get(Calendar.MINUTE);
            if (mm < 10) {
                sb.append('0');
            }
            sb.append(mm);
        }
        return sb.toString();
    }

    public static final String getISO8601Date(long millis) {
        StringBuilder sb = new StringBuilder(19);
        Calendar cal = new GregorianCalendar();
        cal.setTimeInMillis(millis);
        sb.append(cal.get(Calendar.YEAR));
        sb.append('-');
        int month = cal.get(Calendar.MONTH) + 1;
        if (month < 10) {
            sb.append('0');
        }
        sb.append(month);
        sb.append('-');
        int date = cal.get(Calendar.DATE);
        if (date < 10) {
            sb.append('0');
        }
        sb.append(date);
        sb.append('T');
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        if (hour < 10) {
            sb.append('0');
        }
        sb.append(hour);
        sb.append(':');
        int min = cal.get(Calendar.MINUTE);
        if (min < 10) {
            sb.append('0');
        }
        sb.append(min);
        sb.append(':');
        int sec = cal.get(Calendar.SECOND);
        if (sec < 10) {
            sb.append('0');
        }
        sb.append(sec);
        return sb.toString();
    }

    public static final String getFtpDate(long millis) {
        StringBuilder sb = new StringBuilder(20);
        Calendar cal = new GregorianCalendar(TIME_ZONE_UTC);
        cal.setTimeInMillis(millis);
        sb.append(cal.get(Calendar.YEAR));
        int month = cal.get(Calendar.MONTH) + 1;
        if (month < 10) {
            sb.append('0');
        }
        sb.append(month);
        int date = cal.get(Calendar.DATE);
        if (date < 10) {
            sb.append('0');
        }
        sb.append(date);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        if (hour < 10) {
            sb.append('0');
        }
        sb.append(hour);
        int min = cal.get(Calendar.MINUTE);
        if (min < 10) {
            sb.append('0');
        }
        sb.append(min);
        int sec = cal.get(Calendar.SECOND);
        if (sec < 10) {
            sb.append('0');
        }
        sb.append(sec);
        sb.append('.');
        int milli = cal.get(Calendar.MILLISECOND);
        if (milli < 100) {
            sb.append('0');
        }
        if (milli < 10) {
            sb.append('0');
        }
        sb.append(milli);
        return sb.toString();
    }

    public static final Date parseFTPDate(String dateStr) throws ParseException {
        return FTP_DATE_FORMAT.get().parse(dateStr);
    }
}
