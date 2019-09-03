package org.texttechnologylab.utilities.helper;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Created by abrami on 14.10.16.
 */
public class DateUtils {

    public static String getTime(long millis){

        long days   =   MILLISECONDS.toDays(millis) % 365;
        long hrs    =   MILLISECONDS.toHours(millis) % 24;
        long min    =   MILLISECONDS.toMinutes(millis) % 60;
        long sec    =   MILLISECONDS.toSeconds(millis) % 60;
        long mls    =   millis % 1000;

        return ((days==0) ? "" : (days<10 ? "0"+days : days)+ " days ")+(hrs<10 ? "0"+hrs : hrs) + ":" + (min<10 ? "0"+min : min) +":"+ (sec<10 ? "0"+sec : sec)+":"+mls;

    }

    public static String getTime(Timestamp pTimestamp){
        return getTime(pTimestamp.getTime());
    }

    public static String longToDate(long date){
        return longToDate(date, "dd.MM.yyy HH:mm:ss.SSSZ");
    }

    public static String longToDate(long date, String format){

        SimpleDateFormat df2 = new SimpleDateFormat(format);
        String dateText = df2.format(date);
        return dateText;

    }

    public static String longToDate(Timestamp pTimestamp, String format){
        return longToDate(pTimestamp.getTime(), format);
    }

    public static String longToDate(Timestamp pTimestamp){
        return longToDate(pTimestamp.getTime());
    }

}
