package org.tlc.whereat.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {

    public static String fullDate(long millis){
        return new SimpleDateFormat("MM/dd hh:mma", Locale.ENGLISH)
            .format(new Date(millis));
    }

}
