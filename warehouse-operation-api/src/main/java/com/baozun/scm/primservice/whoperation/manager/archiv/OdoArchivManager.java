package com.baozun.scm.primservice.whoperation.manager.archiv;

import com.baozun.scm.primservice.whoperation.manager.BaseManager;

public interface OdoArchivManager extends BaseManager {

    /***
     * 备份Odo信息
     * 
     * @param odoid
     * @param ouid
     * @return
     */
    int archivOdo(Long odoid, Long ouid);

}
