package com.baozun.scm.primservice.whoperation.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间格式化
 * 
 * @author jumbo
 * 
 */
public class DateUtil {

    /**
     * String转date
     * 
     * @param date
     * @param format
     * @return
     * @throws ParseException
     */
    public static Date getDateFormat(String date, String format) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.parse(date);
    }

    /**
     * 获取当前时间用于判断插入月份LOG表
     * 
     * @return
     */
    public static String getSysDate() {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMM");// 设置日期格式
        return df.format(new Date());// new Date()为获取当前系统时间
    }

    public static String getSysDateFormat(String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);// 设置日期格式
        return df.format(new Date());// new Date()为获取当前系统时间
    }

}
