package com.baozun.scm.primservice.whoperation.manager.init;

import com.baozun.scm.primservice.whoperation.manager.BaseManager;


public interface InitManager extends BaseManager {

    /**
     * 初始化出库单历史收集数据
     * 
     * @param data
     * @param isExist
     */
    void initOdoCollect(String data, Boolean isExist, String odoCode);

}
