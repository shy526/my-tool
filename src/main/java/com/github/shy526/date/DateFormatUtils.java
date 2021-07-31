package com.github.shy526.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

/**
 * 时间格式化通用
 *
 * @author shy526
 */
public final class DateFormatUtils {
    private final static String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private final static String DATE_FORMAT = "yyyy-MM-dd";
    private final static String TIME_FORMAT = "HH:mm:ss";
    private final static SimpleDateFormat DATE_DEFAULT_FORMAT = new SimpleDateFormat(DEFAULT_FORMAT);
    private final static SimpleDateFormat DATE_DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT);
    private final static SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat(TIME_FORMAT);
    private final static DateTimeFormatter LOCAL_DATE_DEFAULT_FORMAT = DateTimeFormatter.ofPattern(DEFAULT_FORMAT);
    private final static DateTimeFormatter LOCAL_DATE_DATE_FORMAT = DateTimeFormatter.ofPattern(DATE_FORMAT);
    private final static DateTimeFormatter LOCAL_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern(TIME_FORMAT);

    /**
     * 转换为默认时间
     *
     * @param date 事件
     * @return String
     */
    public static String defaultFormat(Date date) {
        return DATE_DEFAULT_FORMAT.format(date);
    }

    /**
     * 转换时间
     *
     * @param dateStr dateStr
     * @return Date
     */
    public static Date defaultParse(String dateStr) {
        try {
            return DATE_DEFAULT_FORMAT.parse(dateStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 转换为默认时间
     *
     * @param date 事件
     * @return String
     */
    public static String dateFormat(Date date) {
        return DATE_DATE_FORMAT.format(date);
    }

    /**
     * 转换时间
     *
     * @param dateStr dateStr
     * @return Date
     */
    public static Date dateParse(String dateStr) {
        try {
            return DATE_DATE_FORMAT.parse(dateStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 转换为默认时间
     *
     * @param date 事件
     * @return String
     */
    public static String timeFormat(Date date) {
        return DATE_TIME_FORMAT.format(date);
    }

    /**
     * 转换时间
     *
     * @param dateStr dateStr
     * @return Date
     */
    public static Date timeParse(String dateStr) {
        try {
            return DATE_TIME_FORMAT.parse(dateStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 自定义foramt
     *
     * @param date   时间
     * @param format 格式
     * @return String
     */
    public static String format(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * 自定义解析
     *
     * @param dataStr 时间字符串
     * @param format  格式
     * @return Date
     */
    public static Date parse(String dataStr, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            return sdf.parse(dataStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 默认实现
     *
     * @param localDateTime 时间
     * @return String
     */
    public static String defaultFormat(TemporalAccessor localDateTime) {
        return LOCAL_DATE_DEFAULT_FORMAT.format(localDateTime);
    }

    /**
     * 默认实现 Date
     *
     * @param localDateTime 时间
     * @return String
     */
    public static String dateFormat(TemporalAccessor localDateTime) {
        return LOCAL_DATE_DATE_FORMAT.format(localDateTime);
    }

    /**
     * 默认实现 time
     *
     * @param localDateTime 时间
     * @return String
     */
    public static String timeFormat(TemporalAccessor localDateTime) {

        return LOCAL_DATE_TIME_FORMAT.format(localDateTime);
    }

    /**
     * 自定义format
     *
     * @param localDateTime 时间
     * @param format        格式
     * @return String
     */
    public static String format(TemporalAccessor localDateTime, String format) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format);
        return dateTimeFormatter.format(localDateTime);
    }

}