package com.baozun.scm.primservice.whoperation.manager.pda.inbound.rcvd;

import java.util.List;

import com.baozun.scm.primservice.whoperation.command.pda.rcvd.RcvdCacheCommand;
import com.baozun.scm.primservice.whoperation.command.pda.rcvd.RcvdContainerCacheCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsn;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsnLine;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPo;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPoLine;
import com.baozun.scm.primservice.whoperation.model.sku.Sku;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container;
import com.baozun.scm.primservice.whoperation.model.warehouse.StoreDefectReasons;
import com.baozun.scm.primservice.whoperation.model.warehouse.StoreDefectType;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhAsnRcvdLog;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhAsnRcvdSnLog;
import com.baozun.scm.primservice.whoperation.model.warehouse.carton.WhCarton;
import com.baozun.scm.primservice.whoperation.model.warehouse.conf.basis.WarehouseDefectReasons;
import com.baozun.scm.primservice.whoperation.model.warehouse.conf.basis.WarehouseDefectType;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventory;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventorySn;

/**
 * @author yimin.lu
 *
 */
public interface GeneralRcvdManager extends BaseManager {

    /**
     * 通用收货：将扫描的商品数据从缓存中推送到数据库中
     * 
     * @param commmand
     */
    @Deprecated
    void saveScanedSkuWhenGeneralRcvdForPda(List<RcvdCacheCommand> commmandList);

    /**
     * 根据内部容器号查询容器是否有库存
     * 
     * @param insideContainerId
     * @return
     */
    long findContainerListCountByInsideContainerIdFromSkuInventory(Long insideContainerId, Long ouId);

    /**
     * 获取容器的去重后的商品库存属性。多重库存属性用，分隔
     * 
     * @param insideContainerId
     * @return
     */
    RcvdContainerCacheCommand getUniqueSkuAttrFromWhSkuInventory(Long insideContainerId, Long ouId);

    /**
     * 查找商品信息
     */
    Sku findSkuByIdToShard(Long id, Long ouId);

    /**
     * 查找容器信息
     */
    Container findContainerByIdToShard(Long id, Long ouId);

    /**
     * 店铺的残次类型
     */
    StoreDefectType findStoreDefectTypeByIdToGlobal(Long id);
    /**
     * 店铺的残次原因
     */
    StoreDefectReasons findStoreDefectReasonsByIdToGlobal(Long id);
    /**
     * 仓库的残次类型
     */
    WarehouseDefectType findWarehouseDefectTypeByIdToShard(Long id, Long ouId);
    /**
     * 仓库的残次原因
     */
    WarehouseDefectReasons findWarehouseDefectReasonsByIdToShard(Long id, Long ouId);

    /**
     * 通用收货：将扫描的商品数据从缓存中推送到数据库中【业务方法】
     * 
     * @author yimin.lu
     * @param saveSnList
     * @param saveSnLogList
     * @param saveInvList
     * @param saveInvLogList
     * @param saveAsnLineList
     * @param asn
     * @param savePoLineList
     * @param po
     * @param saveWhCartonList
     */
    void saveScanedSkuWhenGeneralRcvdForPda(List<WhSkuInventorySn> saveSnList, List<WhAsnRcvdSnLog> saveSnLogList, List<WhSkuInventory> saveInvList, List<WhAsnRcvdLog> saveInvLogList, List<WhAsnLine> saveAsnLineList, WhAsn asn,
            List<WhPoLine> savePoLineList, WhPo po, Container container, List<WhCarton> saveWhCartonList);

    /**
     * version更新容器
     * 
     * @param container
     * @return
     */
    int updateContainerByVersion(Container container);

    /**
     * 根据容器编码查找容器 无判断
     */
    ContainerCommand findContainerByCode(String code, Long ouId);

    /**
     * 返回插入数据库对象的ID
     * 
     * @param container
     * @param userId
     * @param ouId
     * @return
     */
    Container insertByCode(ContainerCommand container, Long userId, Long ouId);
}
