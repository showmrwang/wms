package com.baozun.scm.primservice.whoperation.util;

public class HashUtil {

    /**
     * 根据单据code 计算对应表序号
     * 
     * @param code
     * @return
     */
    public static final String serialNumberByHashCode(String code) {
        Integer trackHash = code.hashCode(); // 取单据code的哈希值
        trackHash = Math.abs(trackHash % 200) + 1;
        return trackHash.toString();
    }

}
