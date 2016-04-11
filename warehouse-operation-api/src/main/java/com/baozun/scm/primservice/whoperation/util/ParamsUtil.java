/**
 * Copyright (c) 2013 Baozun All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Baozun.
 * You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Baozun.
 *
 * BAOZUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. BAOZUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 *
 */
package com.baozun.scm.primservice.whoperation.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import lark.common.dao.Page;
import lark.common.dao.Sort;

/**
 * @author lichuan
 *
 */
public class ParamsUtil {
    private static final String NULL = "null";
    
    /**
     * Page对象toString
     * @author lichuan
     * @param page
     * @return
     */
    public static String page2String(Page page) {
        String result = NULL;
        if (null == page) return result;
        result = bean2String(page);
        return result;
    }
    
    /**
     * Sort对象toString
     * @author lichuan
     * @param sort
     * @return
     */
    public static String sort2String(Sort sort){
        String result = NULL;
        if(null == sort) return result;
        result = bean2String(sort);
        return result;
    }
    
    /**
     * Sort数组toString
     * @author lichuan
     * @param sorts
     * @return
     */
    public static String sorts2String(Sort[] sorts){
        StringBuilder result = new StringBuilder();
        if(null == sorts || 0 == sorts.length) return result.toString();
        for(Sort s : sorts){
            result.append(sort2String(s));
        }
        return result.toString();
    }
    
    /**
     * 对象toString
     * @author lichuan
     * @param obj
     * @return
     */
    public static String bean2String(Object obj) {
        StringBuffer bf = new StringBuffer();
        if (null == obj) return bf.toString();
        bf.append(obj.getClass().getSimpleName()).append("[");
        Method[] fs = obj.getClass().getMethods();
        if (fs != null) {
            for (Method f : fs) {
                if (f.getName().startsWith("get") && !f.getName().equals("getClass")) {
                    try {
                        bf.append(firstLetterToUpper(f.getName().replaceFirst("get", ""))).append(":");
                        Object rs = f.invoke(obj);
                        bf.append(rs).append(",");
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        bf.deleteCharAt(bf.length() - 1);
        bf.append("]");
        return bf.toString();
    }

    public static String firstLetterToUpper(String string) {
        char[] buffer = string.toCharArray();
        buffer[0] = Character.toLowerCase(string.charAt(0));
        return new String(buffer);
    }
    
    /**
     * Map对象toString
     * @author lichuan
     * @param map
     * @return
     */
    public static String map2String(Map<?, ?> map) {
        String result = NULL;
        if (null == map) return result;
        result = map.toString();
        return result;
    }
}
