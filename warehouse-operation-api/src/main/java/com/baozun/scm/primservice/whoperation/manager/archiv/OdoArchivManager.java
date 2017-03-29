package com.baozun.scm.primservice.whoperation.manager.archiv;

import java.util.List;

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
    
    /**
     * 查找需要归档的出库单数据
     * @author kai.zhu
     * @version 2017年3月29日
     */
	List<Long> findOdoArchivData(Long ouId);

	void archivOdoExt(Long odoId, Long ouId);

}
