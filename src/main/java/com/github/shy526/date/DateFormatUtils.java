package com.github.shy526.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 时间格式化通用
 *
 * @author shy526
 */
public final class DateFormatUtils {
    /**
     * 通用格式化格式
     */
    public final static String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public final static String DATE_FORMAT = "yyyy-MM-dd";
    public final static String TIME_FORMAT = "HH:mm:ss";

    /**
     * 提供时间与字符串转换的队列
     */
    private final static int QUEUE_SIZE = Runtime.getRuntime().availableProcessors() + 1;
    private final static BlockingQueue<SimpleDateFormat> DATE_DEFAULT_FORMAT_QUEUE = new ArrayBlockingQueue<>(QUEUE_SIZE);
    private final static BlockingQueue<SimpleDateFormat> DATE_DATE_FORMAT_QUEUE = new ArrayBlockingQueue<>(QUEUE_SIZE);
    private final static BlockingQueue<SimpleDateFormat> DATE_TIME_FORMAT_QUEUE = new ArrayBlockingQueue<>(QUEUE_SIZE);

    /**
     * 新时间格式转化
     */
    private final static DateTimeFormatter LOCAL_DATE_DEFAULT_FORMAT = DateTimeFormatter.ofPattern(DEFAULT_FORMAT);
    private final static DateTimeFormatter LOCAL_DATE_DATE_FORMAT = DateTimeFormatter.ofPattern(DATE_FORMAT);
    private final static DateTimeFormatter LOCAL_TIME_FORMAT = DateTimeFormatter.ofPattern(TIME_FORMAT);

    private final static AbstractProcessor<Date, String> FORMAT_PROCESSOR = new AbstractProcessor<Date, String>() {
        @Override
        protected String handle(Date obj, SimpleDateFormat sdf) {
            return sdf.format(obj);
        }
    };
    private final static AbstractProcessor<String, Date> PARSE_PROCESSOR = new AbstractProcessor<String, Date>() {
        @Override
        protected Date handle(String obj, SimpleDateFormat sdf) {
            try {
                return sdf.parse(obj);
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        }
    };

    static {
        for (int i = 0; i < QUEUE_SIZE; i++) {
            DATE_DEFAULT_FORMAT_QUEUE.add(new SimpleDateFormat(DEFAULT_FORMAT));
            DATE_DATE_FORMAT_QUEUE.add(new SimpleDateFormat(DATE_FORMAT));
            DATE_TIME_FORMAT_QUEUE.add(new SimpleDateFormat(TIME_FORMAT));
        }
    }


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
        return FORMAT_PROCESSOR.run(date, format);
    }

    /**
     * 自定义解析
     *
     * @param dataStr 时间字符串
     * @param format  格式
     * @return Date
     */
    public static Date parse(String dataStr, String format) {
        return PARSE_PROCESSOR.run(dataStr, format);
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

    static abstract class AbstractProcessor<J, T> {
        public T run(J obj, String format) {
            T result = null;
            BlockingQueue<SimpleDateFormat> blockingQueue = null;
            if (DEFAULT_FORMAT.equals(format)) {
                blockingQueue = DATE_DEFAULT_FORMAT_QUEUE;
            } else if (DATE_FORMAT.equals(format)) {
                blockingQueue = DATE_DATE_FORMAT_QUEUE;
            } else if (TIME_FORMAT.equals(format)) {
                blockingQueue = DATE_TIME_FORMAT_QUEUE;
            }
            SimpleDateFormat sdf = null;
            if (blockingQueue != null) {
                try {
                    sdf = blockingQueue.poll(100, TimeUnit.MILLISECONDS);
                } catch (Exception e) {
                    sdf = new SimpleDateFormat(format);
                }
                result = handle(obj, sdf);
                blockingQueue.offer(sdf);

            } else {
                sdf = new SimpleDateFormat(format);
                result = handle(obj, sdf);
            }
            return result;
        }

        /**
         * 处理方法
         *
         * @param obj              obj
         * @param sdf simpleDateFormat
         * @return T
         */
        protected abstract T handle(J obj, SimpleDateFormat sdf);

    }
}
