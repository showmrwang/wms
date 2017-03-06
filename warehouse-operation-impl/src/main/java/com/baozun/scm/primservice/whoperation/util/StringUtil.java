package com.baozun.scm.primservice.whoperation.util;

import java.io.Serializable;
import java.util.List;

public class StringUtil implements Serializable {



    /**
     * 
     */
    private static final long serialVersionUID = -614279618073901111L;

    /**
     * 判断字符串是否为空
     * 
     * @param org
     * @return
     */
    public static Boolean isEmpty(String org) {
        Boolean flag = Boolean.FALSE;
        if (org == null || "".equals(org.trim())) {
            flag = Boolean.TRUE;
        }
        return flag;
    }

    /**
     * list转换为String
     * 
     * @param list
     * @param separator
     * @return
     */
    public static String listToString(List<? extends Object> list, char separator) {
        return "(" + org.apache.commons.lang.StringUtils.join(list.toArray(), separator) + ")";
    }

    /**
     * list转换为String
     *
     * @param list
     * @param separator
     * @return
     */
    public static String listToStringWithoutBrackets(List<Long> list, char separator) {
        return org.apache.commons.lang.StringUtils.join(list.toArray(), separator);
    }

}
