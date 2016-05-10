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

}
