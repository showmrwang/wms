package com.baozun.scm.primservice.whoperation.manager.collect;

import java.util.List;

import com.baozun.scm.primservice.whoperation.command.collect.WhOdoArchivLineIndexCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.collect.WhOdoArchivIndex;
import com.baozun.scm.primservice.whoperation.model.collect.WhOdoArchivLineIndex;

public interface WhOdoArchivIndexManager extends BaseManager {

    /**
     * 保存仓库出库单归档索引数据
     * 
     * @param whOdoArchivIndex
     * @return
     */
    int saveWhOdoArchivIndex(WhOdoArchivIndex whOdoArchivIndex);

    /**
     * 通过电商平台订单号(NOT NULL) or 数据来源(DEFAULT NULL) or 仓库组织ID(DEFAULT NULL) or wms出库单号(DEFAULT NULL)
     * 查询仓库出库单归档索引数据
     * 
     * @param ecOrderCode
     * @param dataSource
     * @return
     */
    List<WhOdoArchivIndex> findWhOdoArchivIndexByEcOrderCode(String ecOrderCode, String dataSource, String wmsOdoCode, Long ouid);
    
    /**
     * 保存仓库出库单归档索引数据
     *   添加前增加了是否已存在判断
     * 
     * @author kai.zhu
     * @version 2017年3月29日
     */
	void saveWhOdoArchivIndexExt(WhOdoArchivIndex whOdoArchivIndex);
	
	/**
	 * 查找出归档到collect库的出库单数据
	 * @author kai.zhu
	 * @version 2017年3月29日
	 */
	List<WhOdoArchivIndex> findWhOdoArchivIndexData(Long ouId);
	
	/**
	 * 删除仓库出库单归档索引数据
	 * @author kai.zhu
	 * @version 2017年3月29日
	 */
	void deleteWhOdoArchivIndex(WhOdoArchivIndex index);
	
	/**
	 * 保存出库明细到collect库
	 * @author kai.zhu
	 * @version 2017年3月30日
	 */
	List<WhOdoArchivLineIndex> saveWhOdoLineArchivListIntoCollect(WhOdoArchivIndex odoArchivIndex, List<WhOdoArchivLineIndex> whOdoArchivLineIndexList);
	
	/**
	 * 判断归档明细中是否有数据
	 * @author kai.zhu
	 * @version 2017年3月31日
	 */
    boolean checkWhOdoArchivLineIndexExsits(String ecOrderCode, String dataSource, Long ouId);

    /**
     * 根据ASNId查找归档数据
     * 
     * @param asnId
     * @param ouId
     * @return
     */
    List<WhOdoArchivLineIndex> findWhOdoArchivLineIndexListByAsnId(Long asnId, Long ouId);

    List<WhOdoArchivLineIndexCommand> findWhOdoArchivLineIndexCommandListByAsnId(Long asnId, Long ouId);
}
