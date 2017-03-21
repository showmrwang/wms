package com.baozun.scm.primservice.whoperation.manager.collect;

import java.util.List;

import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.collect.WhOdoArchivIndex;

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

}
