package com.baozun.scm.primservice.whoperation.util;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.processors.DefaultDefaultValueProcessor;

public class JsonUtil {

    /**
     * 对象转json字符串
     * 
     * @param o
     * @return
     */
    public static String beanToJson(Object o) {
        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.registerDefaultValueProcessor(Integer.class, new DefaultDefaultValueProcessor() {
            public Object getDefaultValue(@SuppressWarnings("rawtypes") Class type) {
                return null;
            }
        });
        jsonConfig.registerDefaultValueProcessor(Double.class, new DefaultDefaultValueProcessor() {
            public Object getDefaultValue(@SuppressWarnings("rawtypes") Class type) {
                return null;
            }
        });
        jsonConfig.registerDefaultValueProcessor(Long.class, new DefaultDefaultValueProcessor() {
            public Object getDefaultValue(@SuppressWarnings("rawtypes") Class type) {
                return null;
            }
        });
        JSONObject jsonObject = JSONObject.fromObject(o, jsonConfig);
        return jsonObject.toString();
    }

    /**
     * json字符串转Json对象
     * 
     * @param o
     * @return
     */
    public static JSONObject jsonToBean(String o) {
        JSONObject jsonobject = JSONObject.fromObject(o);
        return jsonobject;
    }

}
