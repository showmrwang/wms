package com.baozun.scm.primservice.whoperation.manager.archiv;

import com.baozun.scm.primservice.whoperation.manager.BaseManager;

public interface OdoArchivManager extends BaseManager {

    /***
     * 归档Odo信息
     * 
     * @param odoid
     * @param ouid
     * @return
     */
    int archivOdo(Long odoid, Long ouid);

    /**
     * 删除Odo信息
     * @param odoid
     * @param ouid
     * @return
     */
    int deleteOdo(Long odoid, Long ouid);

}
