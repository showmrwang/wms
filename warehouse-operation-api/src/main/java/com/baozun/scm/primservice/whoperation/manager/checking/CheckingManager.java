package com.baozun.scm.primservice.whoperation.manager.checking;


import java.util.List;

import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingLineCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOutboundFacilityCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
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
    WhOutboundFacilityCommand findOutboundFacilityByMacAddr(String macAddr, Long ouId);


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


}
