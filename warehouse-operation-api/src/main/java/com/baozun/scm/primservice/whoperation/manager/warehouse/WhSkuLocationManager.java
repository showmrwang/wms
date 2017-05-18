package com.baozun.scm.primservice.whoperation.manager.warehouse;

import java.util.List;
import java.util.Map;

import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import com.baozun.scm.primservice.whoperation.command.warehouse.WhSkuLocationCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.ResponseMsg;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhSkuLocation;

public interface WhSkuLocationManager extends BaseManager {
    /**
     * 一览查询
     */
    Pagination<WhSkuLocationCommand> findWhSkuLocationListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> paraMap);

    /**
     * 根据参数查找SKU_LOCATION相关信息
     */
    List<WhSkuLocationCommand> findWhSkuLocationCommandListByParamToShard(WhSkuLocationCommand skuLocationCommand);

    /**
     * 插入操作
     * 
     * @param skuLocation
     * @return
     */
    ResponseMsg insert(WhSkuLocation skuLocation);

    /**
     * 更新操作
     * 
     * @param skuLocation
     * @return
     */
    ResponseMsg update(WhSkuLocation skuLocation);

    /**
     * 删除或批量删除
     * 
     * @param command
     * @return
     */
    ResponseMsg deleteByBatch(WhSkuLocationCommand command);

    WhSkuLocation findByIdOuId(Long id, Long ouId);

    /**
     * 根据库位查找
     * 
     * @param id
     * @param ouId
     * @return
     */
    List<WhSkuLocation> findByLocationIdOuId(Long id, Long ouId);
    
    
//    /**
//     * 查询库位绑定商品信息
//     * 
//     * @author lijun.shen
//     * @param whSkuLocation
//     * @return
//     */
//    List<WhSkuLocation> getBindingSku(WhSkuLocation whSkuLocation);

}
