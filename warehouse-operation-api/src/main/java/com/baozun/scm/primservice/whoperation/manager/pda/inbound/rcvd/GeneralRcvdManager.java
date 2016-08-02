package com.baozun.scm.primservice.whoperation.manager.pda.inbound.rcvd;

import java.util.List;

import com.baozun.scm.primservice.whoperation.command.pda.rcvd.RcvdCacheCommand;
import com.baozun.scm.primservice.whoperation.command.pda.rcvd.RcvdContainerCacheCommand;
import com.baozun.scm.primservice.whoperation.command.sku.skucommand.SkuCommand;
import com.baozun.scm.primservice.whoperation.command.sku.skucommand.SkuStandardPackingCommand;
import com.baozun.scm.primservice.whoperation.command.sku.skushared.SkuCommand2Shared;
import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.carton.WhCartonCommand;
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
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFunctionRcvd;
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
     * 
     * @param insideContainerId
     * @param skuId
     * @param ouId
     * @return
     */
    List<RcvdContainerCacheCommand> getUniqueSkuAttrFromWhSkuInventory(Long insideContainerId, Long skuId, Long ouId);

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

    List<SkuStandardPackingCommand> findSkuStandardPacking(String skuBarCode, Long ouId, String logId);


    /**
     * 返回容器装箱数
     * @param skuId
     * @param code
     * @param ouId
     * @param lifecycle
     * @param containerTypes
     * @return
     */
    Long findContainerId(Long skuId, String code, Long ouId, Integer lifecycle, Long containerTypeId);

    ContainerCommand findContainer(Long skuId, String code, Long ouId, Long containerTypeId);

    /**
     * @author yimin.lu
     * @param id
     * @param ouId
     * @return
     */
    SkuCommand findSkuBySkuCodeOuId(String skuCode, Long ouId);

    /**
     * 校验此商品是否维护了装箱信息
     * @param skuId
     * @param ouId
     * @param logId
     * @return 
     */
    List<SkuStandardPackingCommand> checkSkuStandardPacking(String skuBarCode, Long ouId, String logId);

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

    /**
     * 容器收货
     * 获取容器类型
     * @param skuId
     * @param ouId
     * @return
     */
    List<SkuStandardPackingCommand> getContainerType(Long skuId, Long ouId);

    /**
     * 
     * @param insideContainerId
     * @param skuId
     * @param ouId
     * @return
     */
    long getUniqueSkuAttrCountFromWhSkuInventory(Long insideContainerId, Long skuId, Long ouId);

    /**
     * 托盘收货完成 【业务方法】
     * 
     * @param outerContainerId
     * @param insideContainerId
     * @param ouId
     * @param userId
     */
    void rcvdPallet(Long outerContainerId, Long insideContainerId, Long ouId, Long userId);

    /**
     * 查询功能菜单
     * 
     * @param id
     * @param ouid
     * @return
     */
    WhFunctionRcvd findwFunctionRcvdByFunctionId(Long id, Long ouid);

    /**
     * 查找商品
     * 
     * @param skuCode
     * @param customerId
     * @param ouId
     * @return
     */
    SkuCommand2Shared findSkuByBarCodeCustomerIdOuId(String skuCode, Long customerId, Long ouId);

    /**
     * 查找装箱信息
     * 
     * @param cartonCommand
     * @return
     */
    List<WhCartonCommand> findWhCartonByParamExt(WhCartonCommand cartonCommand);

    Boolean skuDateCheck(Long skuId, Long ouId, String mfgDate, String expDate);
}
