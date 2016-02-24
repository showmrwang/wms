package com.baozun.scm.primservice.whoperation.model;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BaseModel extends lark.common.model.BaseModel {

    /**
     * 
     */
    private static final long serialVersionUID = -1989798619170800263L;

    /**
     * 正常
     */
    public static final Integer LIFECYCLE_NORMAL = 1;

    /**
     * 禁用
     */
    public static final Integer LIFECYCLE_DISABLE = 0;

    /**
     * 已删除
     */
    public static final Integer LIFECYCLE_DELETED = 2;

    @Override
    public String toString() {
        StringBuffer bf = new StringBuffer();
        bf.append(this.getClass().getSimpleName()).append("[");
        Method[] fs = this.getClass().getMethods();
        if (fs != null) {
            for (Method f : fs) {
                if (f.getName().startsWith("get") && !f.getName().equals("getClass")) {
                    try {
                        bf.append(firstLetterToUpper(f.getName().replaceFirst("get", ""))).append(":");
                        Object rs = f.invoke(this);
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
}
