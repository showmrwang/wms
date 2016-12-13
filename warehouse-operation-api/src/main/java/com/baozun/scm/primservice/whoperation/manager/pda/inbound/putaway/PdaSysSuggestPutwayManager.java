package com.baozun.scm.primservice.whoperation.manager.pda.inbound.putaway;

import java.util.Map;

import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.ScanResultCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSkuCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.Warehouse;

/**
 * pda ，系统推荐上架
 * @author tangming
 *
 */
public interface PdaSysSuggestPutwayManager extends BaseManager{

    /**
     * 整托容器上架
     * @param code
     */
    public ScanResultCommand sysSuggestScanContainer(String containerCode,Long ouId,String logId,Long userId,Integer putawayPatternDetailType,Long funcId,String outerContainerCode,Warehouse wareCommand);
    
    /***
     * 整托上架使用推荐库位上架
     * @param locationCode
     * @param isCaselevelScanSku
     * @param isNotcaselevelScanSku
     * @param containerCode
     * @param userId
     * @param ouId
     * @param srCmd
     * @return
     */
    public ScanResultCommand  palletIsUserSuggestLocation(String locationCode,String containerCode,Long userId,Long ouId,Integer putawayPatternDetailType,Long functionId,Warehouse warehouse,String locBarCode);
    
    /***
     * 整箱上架使用推荐库位上架
     * @param locationCode
     * @param isCaselevelScanSku
     * @param isNotcaselevelScanSku
     * @param containerCode
     * @param userId
     * @param ouId
     * @param srCmd
     * @return
     */
    public ScanResultCommand  contianerUserSuggestLocation(String locationCode, Long functionId, String outerContainerCode, String insideContainerCode,Long userId, Long ouId,Integer putawayPatternDetailType,Warehouse warehouse,String locBarCode);
    
    
    /***
     * 拆箱上架使用推荐库位上架
     * @param locationCode
     * @param containerCode
     * @param userId
     * @param ouId
     * @param srCmd
     * @return
     */
    public ScanResultCommand  splitUserSuggestLocation(String outContainerCode,String locationCode,String containerCode,Long userId,Long ouId,String locBarCode);
    
    
    
    
    /**
     * 整托上架:扫描sku商品
     * @param barCode
     * @param insideContainerCode
     * @param sRCommand
     * @return
     */
    public ScanResultCommand palletPutwayScanSku(WhSkuCommand skuCmd,String containerCode,Long ouId,Long userId,String locationCode,String insideContainerCode,Long functionId,Warehouse warehouse,Integer putawayPatternDetailType);
    
    /**
     * 整箱上架:扫描sku商品
     * @param barCode
     * @param insideContainerCode
     * @param sRCommand
     * @return
     */
    public ScanResultCommand containerPutwayScanSku(WhSkuCommand skuCmd,String containerCode,Long ouId,Long userId,String locationCode,String insideContainerCode,Long functionId,Warehouse warehouse,Integer putawayPatternDetailType);
    
    
  /***
   * 拆箱箱上架:扫描sku商品
   * @param barCode
   * @param containerCode
   * @param ouId
   * @param insideContainerId
   * @param userId
   * @param locationCode
   * @param scanPattern
   * @param countSku
   * @param skuQuantity
   * @param finish
   * @return
   */
    public ScanResultCommand splitPutwayScanSku(String outerContainerCode,String insideContainerCode,WhSkuCommand skuCmd, String locationCode, Long funcId, Long ouId, Long userId, String logId,Integer putawayPatternDetailType,Warehouse warehouse);
    

    /***
     * 整托:人工上架分支
     * @param outerContainerCode
     * @param insideContainerCode
     * @param funcId
     * @param ouId
     * @param userId
     * @param putawayPatternDetailType
     * @return
     */
    public ScanResultCommand pallentManPutwayFlow(String invAttrMgmtHouse,String locationCode,String locBarCode,String outerContainerCode,String insideContainerCode,Long funcId,Long ouId,Long userId,Integer putawayPatternDetailType,Warehouse warehouse);
    
    
    /***
     * 整箱:人工上架分支
     * @param outerContainerCode
     * @param insideContainerCode
     * @param funcId
     * @param ouId
     * @param userId
     * @param putawayPatternDetailType
     * @return
     */
    public ScanResultCommand containerManPutwayFlow(String invAttrMgmtHouse,String locationCode,String locBarCode,String outerContainerCode,String insideContainerCode,Long funcId,Long ouId,Long userId,Integer putawayPatternDetailType,Warehouse warehouse);
    
    
    /***
     * 拆箱:人工上架分支
     * @param outerContainerCode
     * @param insideContainerCode
     * @param funcId
     * @param ouId
     * @param userId
     * @param putawayPatternDetailType
     * @return
     */
    public ScanResultCommand splitContaienrManPutwayFlow(String invAttrMgmtHouse,String locationCode,String locBarCode,String outerContainerCode,String insideContainerCode,Long funcId,Long ouId,Long userId,Integer putawayPatternDetailType,Warehouse warehouse);
    
    /***
     * 整托上架:扫描内部容器
     * @param insideContainerCode
     */
    public void sysSuggestScanInsideContainer(String insideContainerCode,Long ouId);
    
}
