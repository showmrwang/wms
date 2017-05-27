package com.baozun.scm.primservice.whoperation.manager.archiv;

import java.util.List;

import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.collect.WhOdoArchivLineIndex;

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
	
	/**
     * 归档仓库Odo信息
     */
	void archivOdoExt(Long odoId, Long ouId);
	
	/**
	 * 根据odoCode查出归档的出库明细
	 * @author kai.zhu
	 * @version 2017年3月30日
	 */
	List<WhOdoArchivLineIndex> findWhOdoLineArchivByOdoCode(String odoCode, Long ouId, String sysDate, String ecOrderCode, String dataSource);


    /**
     * 退货数据反馈
     * 
     * @param sqlList
     */
    void executeReturns(List<String> sqlList);
	
}
