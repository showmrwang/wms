package com.baozun.scm.primservice.whoperation.util;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

/**
 * 时间格式化
 * 
 * @author jumbo
 * 
 */
public class DateUtil {


    /** yyyy-MM-dd */
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    /** yyyyMMdd */
    public static final String DATE_FORMAT = "yyyyMMdd";
    /** HH:mm:ss */
    public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";
    /** yyyy-MM-dd HH:mm:ss */
    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    /** yyyy年MM月dd日 */
    public static final String CN_DATE_FORMAT = "yyyy年MM月dd日";

    /**
     * <p>
     * Parses a string representing a date by trying a variety of different parsers.
     * </p>
     * 
     * <p>
     * The parse will try each parse pattern in turn. A parse is only deemed successful if it parses
     * the whole of the input string. If no parse patterns match, a ParseException is thrown.
     * </p>
     * The parser will be lenient toward the parsed date.
     * 
     * @param str the date to parse, not null
     * @param parsePatterns the date format patterns to use, see SimpleDateFormat, not null
     * @return the parsed date,when prase exceprion,then reun null.
     * 
     */
    public static Date parseDate(final String str, final String... parsePatterns) {
        try {
            return DateUtils.parseDate(str, parsePatterns);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * <p>
     * Formats a date/time into a specific pattern.
     * </p>
     * 
     * @param date the date to format, not null
     * @param pattern the pattern to use to format the date, not null
     * @return the formatted date
     */
    public static String formatDate(Date date, String pattern) {
        return DateFormatUtils.format(date, pattern);
    }

    /**
     * <p>
     * Format current date/time
     * </p>
     * 
     * @param pattern the pattern to use to format the date, not null
     * @return the formatted date
     */
    public static String getSysDateFormat(String pattern) {
        return formatDate(new Date(), pattern);
    }

    /**
     * 获取当前时间用于判断插入月份LOG表
     * 
     * @return
     */
    public static String getSysDate() {
        return getSysDateFormat("yyyyMM");
    }

    /**
     * String转date
     * 
     * @param date
     * @param format
     * @return
     * @throws ParseException
     */
    public static Date getDateFormat(String date, String format) throws ParseException {
        return parseDate(date, format);
    }
}
