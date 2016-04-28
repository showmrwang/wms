package com.baozun.scm.primservice.whoperation.command;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;



public class BaseCommand implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 580811071956562523L;

    protected String logId;

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

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
