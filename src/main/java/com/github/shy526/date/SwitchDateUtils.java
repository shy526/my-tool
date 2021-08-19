package com.github.shy526.date;

import java.time.*;
import java.util.Date;

/**
 * 新旧Date 互相转换类
 * @author shy526
 */
public class SwitchDateUtils {
    /**
     * java.util.Date --> java.time.LocalDateTime
     * @param date date
     * @return LocalDateTime
     */
    public static LocalDateTime dateToLocalDateTime(Date date) {
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant, zone);
    }

    /**
     * java.util.Date --> java.time.LocalDate
     * @param date date
     * @return LocalDate
     */
    public static  LocalDate dateToLocalDate(Date date) {
        return dateToLocalDateTime(date).toLocalDate();
    }

    /**
     * java.util.Date --> java.time.LocalTime
     * @param date Date
     * @return LocalTime
     */
    public static  LocalTime dateToLocalTime(Date date) {
        return dateToLocalDateTime(date).toLocalTime();
    }

    /**
     * java.time.LocalDateTime --> java.util.Date
     * @param localDateTime LocalDateTime
     * @return Date
     */
    public static  Date localDateTimeToDate(LocalDateTime localDateTime ) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        return Date.from(instant);
    }


    /**
     *  java.time.LocalDate --> java.util.Date
     * @param localDate LocalDate
     * @return Date
     */
    public static  Date localDateToDate(LocalDate localDate) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
        return Date.from(instant);
    }


    /**
     * java.time.LocalTime --> java.util.Date
     * @param localTime LocalTime
     * @param localDate LocalDate
     * @return Date
     */
    public static Date localTimeToDate(LocalTime localTime, LocalDate localDate) {
        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        return Date.from(instant);
    }
}
