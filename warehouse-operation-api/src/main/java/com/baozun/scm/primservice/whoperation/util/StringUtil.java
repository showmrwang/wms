package com.baozun.scm.primservice.whoperation.util;

import java.io.Serializable;

public class StringUtil implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 5371442966553173270L;

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

}
