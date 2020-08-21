package com.think.core.util;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    public static final String DATE_TIME_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd";

    public static final String TIME_FORMAT_PATTERN = "HH:mm:ss";
    /**
     * 获取现在时间
     *
     * @return 返回时间类型 Date yyyy-MM-dd HH:mm:ss
     */
    public static Date getNowDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_TIME_FORMAT_PATTERN, Locale.CHINESE);
        String dateString = formatter.format(currentTime);
        ParsePosition pos = new ParsePosition(8);
        return formatter.parse(dateString, pos);
    }


    /**
     * 获取现在时间
     *
     * @return 返回时间类型 String yyyy-MM-dd HH:mm:ss
     */
    public static String getNowDateString() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_TIME_FORMAT_PATTERN, Locale.CHINESE);
        return formatter.format(currentTime);
    }

    /**
     * 获取现在的日期 yyyy-MM-dd
     *
     * @return 返回String表示的日期
     */
    public static String getNowDateShort() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT_PATTERN,Locale.CHINESE);
        return formatter.format(currentTime);
    }

    /**
     * 获取时间 小时:分;秒 HH:mm:ss
     *
     * @return 返回String表示的时间
     */
    public static String getTimeShort() {
        SimpleDateFormat formatter = new SimpleDateFormat(TIME_FORMAT_PATTERN,Locale.CHINESE);
        Date currentTime = new Date();
        return formatter.format(currentTime);
    }

}
