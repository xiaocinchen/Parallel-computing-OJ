package com.offer.oj.util;

import java.text.*;
import java.util.Calendar;

public class TimeUtil {

    private final static FieldPosition HELPER_POSITION = new FieldPosition(0);

    private final static NumberFormat NUMBER_FORMAT = new DecimalFormat("0000");

    private final static Format DATE_FORMAT = new SimpleDateFormat("MMddHHmmssS");

    private static int seq = 0;

    private static final int MAX = 9999;

    public static synchronized String getUniqueSequence() {
        Calendar rightNow = Calendar.getInstance();
        StringBuffer s = new StringBuffer();
        DATE_FORMAT.format(rightNow.getTime(), s, HELPER_POSITION);
        NUMBER_FORMAT.format(seq, s, HELPER_POSITION);
        if (seq == MAX) {
            seq = 0;
        } else {
            seq++;
        }
        return s.toString();
    }
}
