/**
 * Copyright (c) 2019 GTech All Rights Reserved.
 *
 * This software is the confidential and proprietary information of GTech. You shall not disclose such 
 * Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with GTech.
 *
 * GTECH MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER EXPRESS 
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A 
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. GTECH SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY 
 * LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */
package com.study.spring_study.task.manager;


import io.micrometer.common.util.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 日期关联的通用Util方法
 */
public class DateUtil {

    public static final String FORMAT_UTC = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    public static final String FORMAT_DEFAULT = "yyyy-MM-dd";
    public static final String FORMAT_YYYYMMDD = "yyyyMMdd";
    public static final String FORMAT_YYYY_M_D = "yyyy/M/d";
    public static final String FORMAT_YYYY_MM_DD = "yyyy/MM/dd";
    public static final String FORMAT_YYYYMMDDHH = "yyyyMMddHH";
    public static final String FORMAT_YYYYMMDDHHMI = "yyyyMMddHHmm";
    public static final String FORMAT_YYYYMM = "yyyyMM";
    public static final String FORMAT_YYYYMM_2 = "yyyy-MM";
    public static final String FORMAT_HHMISS = "HH:mm:ss";
    public static final String FORMAT_HHMISS_6 = "HHmmss";
    public static final String FORMAT_YYYYMMDDHHMISS = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_YYYYMMDDHHMISS_2 = "yyyy/MM/dd HH:mm:ss";
    public static final String FORMAT_YYMMDDHHMISS = "yyMMddHHmmss";
    public static final String FORMAT_YYYYMMDDHHMISS_14 = "yyyyMMddHHmmss";
    public static final String FORMAT_YYMMDD = "yyMMdd";
    public static final String FORMAT_YYYY = "yyyy";
    public static final String FORMAT_YY = "yy";
    public static final String FORMAT_MM = "MM";
    public static final String FORMAT_DD = "dd";
    public static final String FORMAT_HH = "HH";
    public static final String FORMAT_MI = "mm";
    public static final String FORMAT_SS = "ss";
    public static final String FORMAT_E = "E";

    public static final ZoneId DEFAULT_ZONE_ID = ZoneId.of("Asia/Shanghai");

    private DateUtil() {
        
    }

    /**
     * 获取当前系统日期
     *
     * @return Date 当前系统日期
     */
    public static Date now() {

        return Calendar.getInstance().getTime();
    }

    /**
     * 获取当前系统日期
     *
     * @param pattern 日期格式描述
     * @return String 当前系统日期
     */
    public static String getCurrentDateAsString(String pattern) {

        return format(Calendar.getInstance().getTime(), pattern);
    }

    /**
     * 获取指定日期字符串的年信息
     *
     * @param text 日期字符串
     * @return 年信息
     */
    public static String getYear(String text) {

        return getYear(text, FORMAT_DEFAULT);
    }

    /**
     * 获取指定日期字符串的年信息
     *
     * @param text 日期字符串
     * @param pattern 日期格式描述
     * @return 年信息
     */
    public static String getYear(String text, String pattern) {

        return format(parseDate(text, pattern), FORMAT_YYYY);
    }

    /**
     * 获取指定日期字符串的月信息
     *
     * @param text 日期字符串
     * @return 月信息
     */
    public static String getMonth(String text) {

        return getMonth(text, FORMAT_DEFAULT);
    }

    /**
     * 获取指定日期字符串的月信息
     *
     * @param text 日期字符串
     * @param pattern 日期格式描述
     * @return 月信息
     */
    public static String getMonth(String text, String pattern) {

        return format(parseDate(text, pattern), FORMAT_MM);
    }

    /**
     * 获取指定日期字符串的日信息
     *
     * @param text 日期字符串
     * @return 日信息
     */
    public static String getDay(String text) {

        return getDay(text, FORMAT_DEFAULT);
    }

    /**
     * 获取指定日期字符串的日信息
     *
     * @param text 日期字符串
     * @param pattern 日期格式描述
     * @return 日信息
     */
    public static String getDay(String text, String pattern) {

        return format(parseDate(text, pattern), FORMAT_DD);
    }

    /**
     * 获取指定日期字符串的星期信息
     *
     * @param text 日期字符串
     * @return 星期信息
     */
    public static String getWeekDay(String text) {

        return getWeekDay(text, FORMAT_DEFAULT);
    }

    /**
     * 获取指定日期字符串的星期信息
     *
     * @param text 日期字符串
     * @param pattern 日期格式描述
     * @return 星期信息
     */
    public static String getWeekDay(String text, String pattern) {

        return format(parseDate(text, pattern), FORMAT_E);
    }

    /**
     * String类型转换成Date
     *
     * @param text     String型日期
     * @param patterns 格式要求
     * @return 日期文字描述
     * @author zejun.dong
     */
    public static Date parseDate(String text, String ... patterns) {

        if (StringUtils.isBlank(text) || patterns.length == 0) {
            return null;
        }

        for(String pattern : patterns) {
            if (validate(text, pattern)) {
                DateFormat formatter = getDateFormat(pattern);
                try {
                    return formatter.parse(text);
                } catch (ParseException e) {
                    // No codes
                }
            }
        }

        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(Long.parseLong(text));
        return date.getTime();
    }

    /**
     * 验证指定的String类型的日期是否合法
     *
     * @param text String型日期
     * @param pattern 格式要求
     * @return 合法返回true, 否则返回false
     * @author zejun.dong
     */
    public static boolean validate(String text, String pattern) {

        if (StringUtils.isBlank(text)) {
            return false;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        try {
            formatter.parse(text);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Date类型转换成String
     *
     * @param date Date型日期
     * @param pattern 格式要求
     * @return 日期文字描述
     * @author zejun.dong
     */
    public static String format(Date date, String pattern) {

        if (date == null) {
            return null;
        }
        DateFormat formatter = getDateFormat(pattern);
        return formatter.format(date);
    }

    /**
     * Calendar类型转换成String
     *
     * @param date Calendar型日期
     * @param pattern 格式要求
     * @return 日期文字描述
     * @author zejun.dong
     */
    public static String format(Calendar date, String pattern) {

        return format(date.getTime(), pattern);
    }

    /*
     * 获取正确的DateFormat对象
     * 
     * @author zejun.dong
     */
    private static DateFormat getDateFormat(String pattern) {

        DateFormat df = new SimpleDateFormat(pattern);
        df.setTimeZone(TimeZone.getDefault());
        return df;
    }

    /**
     * 基于当前时间增加小时数
     *
     * @author Andrew.Dong
     * @Date 2022-01-19
     */
    public static Date addHours(int hours) {

        Calendar strDate = Calendar.getInstance();
        strDate.add(Calendar.HOUR, hours);
        return strDate.getTime();
    }

    /**
     * 基于当前时间增加小时数
     *
     * @author Andrew.Dong
     * @Date 2022-01-19
     */
    public static Date addMinute(int minute) {

        Calendar strDate = Calendar.getInstance();
        strDate.add(Calendar.MINUTE, minute);
        return strDate.getTime();
    }

    /**
     * 基于当前时间增加天数
     *
     * @author zejun.dong
     */
    public static Date addDay(int days) {

        Calendar strDate = Calendar.getInstance();
        strDate.add(Calendar.DATE, days);
        return strDate.getTime();
    }

    /**
     * 当前日期加减月
     *
     * @author zejun.dong
     */
    public static Date addMonth(int months) {

        Calendar strDate = Calendar.getInstance();
        strDate.add(Calendar.MONTH, months);
        return strDate.getTime();
    }

    /**
     * 指定日期加减填
     *
     * @author zejun.dong
     */
    public static Date addDay(Date date, int days) {

        if (date == null) {
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, days);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    /**
     * 根据传入的日期加减月
     *
     * @author zejun.dong
     */
    public static Date addMonth(Date date, int months) {

        if (date == null) {
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MONTH, months);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    /**
     * 获取指定日期的最晚时间
     *
     * @author dongzejun
     */
    public static Date getEndOfDay(Date date) {

        if (date == null) {
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 000);
        return c.getTime();
    }

    /**
     * 获取指定日期的最早时间
     *
     * @author zejun.dong
     */
    public static Date getStartOfDay(Date date) {

        if (date == null) {
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    /**
     * 获取指定日期的（5分钟单位）最早时间
     *
     * @author zejun.dong
     */
    public static Date getStartOfMinute(Date date, int minute) {

        if (date == null) {
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.MINUTE, c.get(Calendar.MINUTE) / minute * minute);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    /**
     * 根据field, 获取指定Date的最早时间
     * @param field -- 参考Calendar定义
     */
    public static Date getStartOfTime(Date date, int field) {

        if (date == null) {
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        switch (field) {
            case Calendar.MONTH:
                c.set(Calendar.DAY_OF_MONTH, 1);
                c.set(Calendar.HOUR_OF_DAY, 0);
                c.set(Calendar.MINUTE, 0);
                c.set(Calendar.SECOND, 0);
                c.set(Calendar.MILLISECOND, 0);
                break;
            case Calendar.DAY_OF_MONTH:
                c.set(Calendar.HOUR_OF_DAY, 0);
                c.set(Calendar.MINUTE, 0);
                c.set(Calendar.SECOND, 0);
                c.set(Calendar.MILLISECOND, 0);
                break;
            case Calendar.HOUR_OF_DAY:
            case Calendar.HOUR:
                c.set(Calendar.MINUTE, 0);
                c.set(Calendar.SECOND, 0);
                c.set(Calendar.MILLISECOND, 0);
                break;
            case Calendar.MINUTE:
                c.set(Calendar.SECOND, 0);
                c.set(Calendar.MILLISECOND, 0);
                break;
            case Calendar.SECOND:
                c.set(Calendar.MILLISECOND, 0);
                break;
            default:
                break;
        }

        return c.getTime();
    }

    /**
     * 获取指定日期的当年最后一天的日期
     *
     * @param date 指定日期
     * @return 获取指定日期的当年最后一天的日期
     * @author zejun.dong
     */
    public static Date getEndOfYear(Date date) {

        if (date == null) {
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.MONTH, 11);
        c.set(Calendar.DAY_OF_MONTH, 31);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    /**
     * 获取指定日期月份的第一天
     */
    public static Date getStartDayOfMonth(Date date) {

        if (date == null) {
            return null;
        }

        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, 0);
        c.setTime(date);
        c.set(Calendar.DAY_OF_MONTH, 1);//设置为1号,当前日期既为本月第一天 
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    /**
     * 传入一个日期和今天相比较
     * 比今天早返回-1
     * 等于今天返回0
     * 比今天晚返回1
     */
    public static String compareDateWithToday(Date date) {

        if (date == null) {
            return null;
        }
        Date today = new Date();
        today = DateUtil.getStartOfDay(today);

        date = DateUtil.getStartOfDay(date);
        if (date.getTime() > today.getTime()) {
            return "1";
        } else if (date.getTime() == today.getTime()) {
            return "0";
        } else if (date.getTime() < today.getTime()) {
            return "-1";
        }
        return null;
    }

    /**
     * Compressed date sequence.
     *
     * <p>Represents compressed date.
     * 7 bits represents year.
     * 4 bits represents month.
     * 5 bits represents day.
     *
     * <p>Sequence convert to date example:
     * <blockquote><pre>
     * int seq = 10286;
     * int year  = seq >> 9 & 0x7f;
     * int month = seq >> 5 & 0xf;
     * int day   = seq & 0x1f;
     * </pre></blockquote>
     */
    public static String compressedDate(LocalDateTime dateTime) {
        int value = (dateTime.getYear() % 100) << 9 | dateTime.getMonthValue() << 5 | dateTime.getDayOfMonth();
        return String.valueOf(value);
    }

    /**
     * Elapsed minutes in a day.
     *
     * <p>Represents elapsed minutes in a day.
     */
    public static String elapsedMinutesInDay(LocalDateTime dateTime) {
        int value = dateTime.getHour() * 60 + dateTime.getMinute();
        return String.format("%04d", value);
    }

    /**
     * Elapsed seconds in a day.
     *
     * <p>Represents elapsed seconds in a day.
     */
    public static String elapsedSecondsInDay(LocalDateTime dateTime) {
        int value = dateTime.getHour() * 60 * 60 + dateTime.getMinute() * 60 + dateTime.getSecond();
        return String.format("%05d", value);
    }

    /**
     * Get the weekofday of the specified Date time.<br>
     * 获取 Date型 时间的星期几
     * 
     * @return weekofday(1-Mon, ..., 7-Sun)
     * @author Andrew.Dong
     * @Date 2019-10-14
     */
    public static int getWeekofday(Date date) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());

        return (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7 + 1;
    }

    /**
     * Get the weekofday of the specified time.
     * 获取 String型 时间的星期几
     * 
     * @return weekofday(1-Mon, ..., 7-Sun)
     * @author Andrew.Dong
     * @Date 2019-10-14
     */
    public static int getWeekofday(String text, String pattern) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtil.parseDate(text, pattern));

        return (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7 + 1;
    }


    /**
     * 获取当前周的指定周数的时间
     * 注意:
     * 该方法将周一作为一周的开始周数
     *
     * @param week 星期枚举
     * @return java.util.Date week对应的day的0点时间
     * @author Silwings
     * @date 2022/4/11 11:18
     * @since 2.8
     */
    public static Date getDateOfWeek(final Week week) {

        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.DAY_OF_WEEK, week.calendarConstant);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    public enum Week {
        MONDAY(Calendar.MONDAY),
        TUESDAY(Calendar.TUESDAY),
        WEDNESDAY(Calendar.WEDNESDAY),
        THURSDAY(Calendar.THURSDAY),
        FRIDAY(Calendar.FRIDAY),
        SATURDAY(Calendar.SATURDAY),
        SUNDAY(Calendar.SUNDAY),
        ;

        private final int calendarConstant;

        Week(int calendarConstant) {
            this.calendarConstant = calendarConstant;
        }
    }

    /**
     * 计算俩个日期之间差多少天
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return long 相差的天数
     * @author Silwings
     * @date 2022/5/31 20:26
     * @since 2.9
     */
    public static long dayGap(Date startDate, Date endDate) {

        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateUtil.FORMAT_DEFAULT);

        final LocalDate startLocalDate = LocalDate.parse(simpleDateFormat.format(startDate));
        final LocalDate endLocalDate = LocalDate.parse(simpleDateFormat.format(endDate));

        return ChronoUnit.DAYS.between(startLocalDate, endLocalDate);
    }

    /**
     * 计算俩个日期之间差多少月
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return int 相差的月数
     * @author Andrew.Dong
     * @date 2022/07/28 16:26
     * @since 2.10
     */
    public static int monthGap(Date startDate, Date endDate) {

        Calendar calendarStart = Calendar.getInstance();
        calendarStart.setTime(startDate);

        Calendar calendarEnd = Calendar.getInstance();
        calendarEnd.setTime(endDate);

        return (calendarEnd.get(Calendar.YEAR) - calendarStart.get(Calendar.YEAR)) * 12 
            + (calendarEnd.get(Calendar.MONTH) - calendarStart.get(Calendar.MONTH));
    }

}
