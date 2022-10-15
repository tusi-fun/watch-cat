package com.cat.util;

import java.time.format.DateTimeFormatter;

/**
 * DateTimeFormatter
 * @author hudongshan
 */
public class DtfUtils {

    public static final DateTimeFormatter CRON_DTF = DateTimeFormatter.ofPattern("ss mm HH dd MM ? yyyy");
    public static final DateTimeFormatter DATETIME_DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter DATE_DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter TIME_DTF = DateTimeFormatter.ofPattern("HH:mm:ss");
}
