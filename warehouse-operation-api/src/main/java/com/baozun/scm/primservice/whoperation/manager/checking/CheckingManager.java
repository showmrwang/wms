package com.baozun.scm.primservice.whoperation.manager.checking;


import java.util.List;
import java.util.Map;
import java.util.Set;

import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingCollectionCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingLineCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOdoPackageInfoCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOutboundFacilityCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOutboundboxCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventorySnCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutboundbox;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutboundboxLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventory;

public interface CheckingManager extends BaseManager {

    /**
     * 打印装箱清单
     * 
     * @param userId 用户Id
     * @param ouId 仓库Id
     */
    void printPackingList(List<Long> facilityIdsList, Long userId, Long ouId);

    /**
     * 打印销售清单
     * 
     * @param userId 用户Id
     * @param ouId 仓库Id
     */
    void printSalesList(List<Long> facilityIdsList, Long userId, Long ouId);

    /**
     * 打印面单
     * 
     * @param userId 用户Id
     * @param ouId 仓库Id
     */
    void printSinglePlane(List<Long> facilityIdsList, Long userId, Long ouId);

    /**
     * 打印箱标签
     * 
     * @param userId 用户Id
     * @param ouId 仓库Id
     */
    void printBoxLabel(List<Long> facilityIdsList, Long userId, Long ouId);

    /**
     * 打印发票（复核）
     * 
     * @param userId 用户Id
     * @param ouId 仓库Id
     */
    void printInvoiceReview(List<Long> facilityIdsList, Long userId, Long ouId);

    /**
     * 生成出库箱库存
     * 
     * @param userId 用户Id
     * @param ouId 仓库Id
     */
    String createOutboundboxInventory(WhCheckingCommand checkingCommand, List<WhSkuInventory> whSkuInventoryLst);

    /**
     * 根据id查询播种墙
     * 
     * @param id Id
     * @param ouId 仓库Id
     */
    WhOutboundFacilityCommand findOutboundFacilityById(Long id, Long ouId);

    /**
     * 根据绑定的MAC地址查询复核台
     *
     * @param macAddr
     * @param ouId
     * @return
     */
    WhOutboundFacilityCommand findOutboundFacilityByMacAddr(String ipAddr, String macAddr, Long ouId);


    List<WhCheckingCommand> findCheckingBySourceCode(String checkingSourceCode, Long ouId);

    List<WhCheckingCommand> findCheckingByBoxCode(String checkingSourceCode, String checkingBoxCode, Long ouId);

    /**
     * 根据条件查找复核头
     *
     * @author mingwei.xie
     * @param checkingCommand
     * @return
     */
    WhCheckingCommand findCheckingByParam(WhCheckingCommand checkingCommand);


    /**
     * 根据ID查找复核头
     *
     * @author mingwei.xie
     * @param checkingId
     * @param ouId
     * @return
     */
    WhCheckingCommand findCheckingById(Long checkingId, Long ouId);

    /**
     * 查找批次下所有的复核箱信息
     *
     * @author mingwei.xie
     * @param batchNo
     * @param ouId
     * @return
     */
    List<WhCheckingCommand> findCheckingByBatch(String batchNo, Long ouId);

    /**
     * 统计批次下待复核总单数
     *
     * @param batchNo
     * @param ouId
     * @return
     */
    int getCheckingOdoQtyByBatch(String batchNo, Long ouId);


    /**
     * 复核 占用耗材库存
     *
     * @param skuInventoryCommand
     * @param outboundBoxCode
     * @param ouId
     * @param logId
     */
    void occupationConsumableSkuInventory(WhSkuInventoryCommand skuInventoryCommand, String outboundBoxCode, Long ouId, String logId);

    /**
     * 复核 释放耗材库存
     *
     * @param outboundboxList
     * @param ouId
     * @param logId
     */
    void releaseConsumableSkuInventory(List<WhOutboundboxCommand> outboundboxList, Long ouId, String logId);


    /**
     * 查询批次下的所有复核集货
     *
     * @param batchNo
     * @param ouId
     * @return
     */
    List<WhCheckingCollectionCommand> findCheckingCollectionByBatch(String batchNo, Long ouId);

    /**
     * 查询批次下复核集货小车的集货数据
     *
     * @param batchNo
     * @param containerCode
     * @param ouId
     * @return
     */
    List<WhCheckingCollectionCommand> findCheckingCollectionByBatchTrolley(String batchNo, String containerCode, Long ouId);

    /**
     * 查询复核出库单的原始明细库存
     *
     * @param odoLineId
     * @param ouId
     * @param uuid
     * @return
     */
    public List<WhSkuInventory> findCheckingOdoSkuInvByOdoLineIdUuid(Long odoLineId, Long ouId, String uuid);


    public void finishedChecking(WhCheckingCommand orgChecking, Set<WhCheckingLineCommand> toUpdateCheckingLineSet, List<WhOutboundbox> whOutboundboxList, Map<WhOutboundbox, List<WhOutboundboxLine>> outboundboxLineListMap,
            Map<WhOutboundbox, List<WhSkuInventory>> outboundboxSkuInvListMap, Set<WhSkuInventory> toUpdateOdoOrgSkuInvSet, List<WhOdoPackageInfoCommand> odoPackageInfoList, List<WhSkuInventorySnCommand> checkedSnInvList, String checkingType,
            Boolean isTabbInvTotal, Long userId, Long ouId, String logId);
}
