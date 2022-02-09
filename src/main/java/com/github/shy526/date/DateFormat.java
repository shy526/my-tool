package com.github.shy526.date;

import com.github.shy526.poll.SimpleGenericObjPool;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

/**
 * 时间格式化通用
 *
 * @author shy526
 */
public final class DateFormat {
    /**
     * 通用格式化格式
     */
    public final static String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public final static String DATE_FORMAT = "yyyy-MM-dd";
    public final static String TIME_FORMAT = "HH:mm:ss";

    /**
     * 旧时间 类型转换的线程池
     */
    private final static SimpleGenericObjPool<SimpleDateFormat> DEFAULT_FORMAT_POLL = new SimpleGenericObjPool<>(() -> new SimpleDateFormat(DEFAULT_FORMAT));
    private final static SimpleGenericObjPool<SimpleDateFormat> DATE_FORMAT_POLL = new SimpleGenericObjPool<>(() -> new SimpleDateFormat(DATE_FORMAT));
    private final static SimpleGenericObjPool<SimpleDateFormat> TIME_FORMAT_POLL = new SimpleGenericObjPool<>(() -> new SimpleDateFormat(TIME_FORMAT));

    /**
     * 新时间格式转化
     */
    private final static DateTimeFormatter LOCAL_DATE_DEFAULT_FORMAT = DateTimeFormatter.ofPattern(DEFAULT_FORMAT);
    private final static DateTimeFormatter LOCAL_DATE_DATE_FORMAT = DateTimeFormatter.ofPattern(DATE_FORMAT);
    private final static DateTimeFormatter LOCAL_TIME_FORMAT = DateTimeFormatter.ofPattern(TIME_FORMAT);


    /**
     * 转换为默认时间
     *
     * @param date 事件
     * @return String
     */
    public static String defaultFormat(Date date) {
        return format(date, DEFAULT_FORMAT);
    }

    /**
     * 转换时间
     *
     * @param dateStr dateStr
     * @return Date
     */
    public static Date defaultParse(String dateStr) {
        return parse(dateStr, DEFAULT_FORMAT);
    }

    /**
     * 转换为默认时间
     *
     * @param date 事件
     * @return String
     */
    public static String dateFormat(Date date) {
        return format(date, DATE_FORMAT);
    }

    /**
     * 转换时间
     *
     * @param dateStr dateStr
     * @return Date
     */
    public static Date dateParse(String dateStr) {
        return parse(dateStr, DATE_FORMAT);
    }

    /**
     * 转换为默认时间
     *
     * @param date 事件
     * @return String
     */
    public static String timeFormat(Date date) {
        return format(date, TIME_FORMAT);
    }

    /**
     * 转换时间
     *
     * @param dateStr dateStr
     * @return Date
     */
    public static Date timeParse(String dateStr) {
        return parse(dateStr, TIME_FORMAT);
    }

    /**
     * 自定义format
     *
     * @param date   时间
     * @param format 格式
     * @return String
     */
    public static String format(Date date, String format) {
        SimpleGenericObjPool<SimpleDateFormat> poll = matePoll(format);
        String result = null;
        if (poll != null) {
            result = poll.leaseObject(item -> {
                return item.format(date);
            });
        } else {
            result = new SimpleDateFormat(format).format(date);
        }
        return result;
    }

    /**
     * 匹配对应的线程池对象
     *
     * @param format format
     * @return MyGenericObjectPool<SimpleDateFormat>
     */
    private static SimpleGenericObjPool<SimpleDateFormat> matePoll(String format) {
        SimpleGenericObjPool<SimpleDateFormat> result = null;
        if (DEFAULT_FORMAT.equals(format)) {
            result = DEFAULT_FORMAT_POLL;
        } else if (DATE_FORMAT.equals(format)) {
            result = DATE_FORMAT_POLL;
        } else if (TIME_FORMAT.equals(format)) {
            result = TIME_FORMAT_POLL;
        }
        return result;
    }

    /**
     * 自定义解析
     *
     * @param dataStr 时间字符串
     * @param format  格式
     * @return Date
     */
    public static Date parse(String dataStr, String format) {
        SimpleGenericObjPool<SimpleDateFormat> poll = matePoll(format);
        Date result = null;
        if (poll != null) {
            result = poll.leaseObject(item -> {
                Date date = null;
                try {
                    date = item.parse(dataStr);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                return date;
            });
        } else {
            try {
                result = new SimpleDateFormat(format).parse(dataStr);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return result;
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
        return LOCAL_TIME_FORMAT.format(localDateTime);
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
