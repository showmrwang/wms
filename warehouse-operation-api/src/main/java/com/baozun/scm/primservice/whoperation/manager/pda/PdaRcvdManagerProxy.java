package com.baozun.scm.primservice.whoperation.manager.pda;

import com.baozun.scm.primservice.whoperation.manager.BaseManager;

public interface PdaRcvdManagerProxy extends BaseManager {

    /**
     * 通用收货，扫描ASN，初始化缓存
     */
    void initAsnCacheForGeneralReceiving(Long occupationId, Long ouId);
}
