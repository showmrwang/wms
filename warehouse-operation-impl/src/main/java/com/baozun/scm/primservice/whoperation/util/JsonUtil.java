package com.baozun.scm.primservice.whoperation.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baozun.scm.baseservice.print.exception.BusinessException;
import com.baozun.scm.baseservice.print.exception.ErrorCodes;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtil.class);


    private static ObjectMapper mapper;
    static {
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); // 忽略没有定义在属性
    }


    // /**
    // * 对象转json字符串
    // *
    // * @param o
    // * @return
    // */
    // public static String beanToJson(Object o) {
    // JsonConfig jsonConfig = new JsonConfig();
    // jsonConfig.registerDefaultValueProcessor(Integer.class, new DefaultDefaultValueProcessor() {
    // public Object getDefaultValue(@SuppressWarnings("rawtypes") Class type) {
    // return null;
    // }
    // });
    // jsonConfig.registerDefaultValueProcessor(Double.class, new DefaultDefaultValueProcessor() {
    // public Object getDefaultValue(@SuppressWarnings("rawtypes") Class type) {
    // return null;
    // }
    // });
    // jsonConfig.registerDefaultValueProcessor(Long.class, new DefaultDefaultValueProcessor() {
    // public Object getDefaultValue(@SuppressWarnings("rawtypes") Class type) {
    // return null;
    // }
    // });
    // JSONObject jsonObject = JSONObject.fromObject(o, jsonConfig);
    // return jsonObject.toString();
    // }

    /**
     * 对象转json字符串
     * 
     * @param o
     * @return
     * @throws Exception
     */
    public static String beanToJson(Object o) {
        String jString = null;
        try {
            jString = mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            LOGGER.error("JsonUtil.beanToJson error============");
            throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
        }
        return jString;
    }

    // /**
    // * json字符串转Json对象
    // *
    // * @param o
    // * @return
    // */
    // public static JSONObject jsonToBean(String o) {
    // JSONObject jsonobject = JSONObject.fromObject(o);
    // return jsonobject;
    // }

    /**
     * json字符串转Json对象
     * 
     * @param o
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T jsonToBean(String json, Class<?> parametrized) {
        try {
            return (T) mapper.readValue(json, parametrized);
        } catch (Exception e) {
            LOGGER.error("JsonUtil.jsonToBean error============");
            throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
        }
    }
}
