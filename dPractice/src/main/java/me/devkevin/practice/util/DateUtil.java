package me.devkevin.practice.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    public static String formatDate(Date date) {
         /*
            This is ugly as fuck

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return calendar.getTime().toString();
        */

        SimpleDateFormat format = new SimpleDateFormat ("hh:mm:ss z 'on' dd//MM/yyyy");
        return format.format (date);
    }
}
