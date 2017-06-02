package com.baozun.scm.primservice.whoperation.manager.pda.work;

import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.ScanResultCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSkuCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.Warehouse;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFunctionOutboundboxMove;
/**
 * pda ，出库箱的拆分和移动
 * @author zhaozili
 *
 */
public interface PdaOutBoundBoxMoveManager extends BaseManager{

    /**
     * 
     * @param containerCode
     * @param boxMoveFunc
     * @param ouId
     * @param logId
     * @param userId
     * @return
     */
    public ScanResultCommand scanContainer(String containerCode, Integer movePattern, Long ouId,String logId,Long userId);
    /**
     * 扫描目标容器
     * @param containerCode
     * @param boxMoveFunc
     * @param ouId
     * @param logId
     * @param userId
     * @return
     */
    public ScanResultCommand scanTargetContainer(String containerCode,Long ouId,String logId,Long userId,String targetBoxCode,Warehouse warehouse);
    
    /**
     * 出库箱拆分:扫描sku商品
     * @param isScanSkuSn
     * @param insideContainerCode
     * @param skuCmd
     * @param funcId
     * @param ouId
     * @param userId
     * @param logId
     * @param warehouse
     * @return
     */
      public ScanResultCommand boxMoveScanSku(Boolean isScanSkuSn,String insideContainerCode,WhSkuCommand skuCmd, 
    		   Long funcId, Long ouId, Long userId, String logId,Warehouse warehouse,Integer scanPattern);
    
//    /***
//     * 整托上架使用推荐库位上架
//     * @param locationCode
//     * @param isCaselevelScanSku
//     * @param isNotcaselevelScanSku
//     * @param containerCode
//     * @param userId
//     * @param ouId
//     * @param srCmd
//     * @return
//     */
//    public ScanResultCommand  palletIsUserSuggestLocation(String locationCode,String containerCode,Long userId,Long ouId,Integer putawayPatternDetailType,Long functionId,Warehouse warehouse,String locBarCode);
//    
//    /***
//     * 整箱上架使用推荐库位上架
//     * @param locationCode
//     * @param isCaselevelScanSku
//     * @param isNotcaselevelScanSku
//     * @param containerCode
//     * @param userId
//     * @param ouId
//     * @param srCmd
//     * @return
//     */
//    public ScanResultCommand  contianerUserSuggestLocation(int putawayPatternType,String locationCode, Long functionId, String outerContainerCode, String insideContainerCode,Long userId, Long ouId,Integer putawayPatternDetailType,Warehouse warehouse,String locBarCode);
//    
//    
//    /***
//     * 拆箱上架使用推荐库位上架
//     * @param locationCode
//     * @param containerCode
//     * @param userId
//     * @param ouId
//     * @param srCmd
//     * @return
//     */
//    public ScanResultCommand  splitUserSuggestLocation(int putawayPatternType,String outContainerCode,String locationCode,String containerCode,Long userId,Long ouId,String locBarCode);
//    
//    
//    
//    
//    /**
//     * 整托上架:扫描sku商品
//     * @param barCode
//     * @param insideContainerCode
//     * @param sRCommand
//     * @return
//     */
//    public ScanResultCommand palletPutwayScanSku(int putawayPatternType,WhSkuCommand skuCmd,String containerCode,Long ouId,Long userId,String locationCode,String insideContainerCode,Long functionId,Warehouse warehouse,Integer putawayPatternDetailType);
//    
//    /**
//     * 整箱上架:扫描sku商品
//     * @param barCode
//     * @param insideContainerCode
//     * @param sRCommand
//     * @return
//     */
//    public ScanResultCommand containerPutwayScanSku(int putawayPatternType,Boolean isNotUser,Boolean isRecommendFail,WhSkuCommand skuCmd,String containerCode,Long ouId,Long userId,String locationCode,String insideContainerCode,Long functionId,Warehouse warehouse,Integer putawayPatternDetailType);
//    
//    
//  /***
//   * 拆箱箱上架:扫描sku商品
//   * @param barCode
//   * @param containerCode
//   * @param ouId
//   * @param insideContainerId
//   * @param userId
//   * @param locationCode
//   * @param scanPattern
//   * @param countSku
//   * @param skuQuantity
//   * @param finish
//   * @return
//   */
//    public ScanResultCommand splitPutwayScanSku(String tipLocationCode,Boolean isCancel,int putawayPatternType,Boolean isNotUser,Boolean isScanSkuSn,Boolean isRecommendFail,String outerContainerCode,String insideContainerCode,WhSkuCommand skuCmd, String locationCode, Long funcId, Long ouId, Long userId, String logId,Integer putawayPatternDetailType,Warehouse warehouse);
//    
//
//    /***
//     * 整托:人工上架分支
//     * @param outerContainerCode
//     * @param insideContainerCode
//     * @param funcId
//     * @param ouId
//     * @param userId
//     * @param putawayPatternDetailType
//     * @return
//     */
//    public ScanResultCommand pallentManPutwayFlow(int putawayPatternType,Boolean isRecommendFail,String invAttrMgmtHouse,String locationCode,String locBarCode,String outerContainerCode,String insideContainerCode,Long funcId,Long ouId,Long userId,Integer putawayPatternDetailType,Warehouse warehouse);
//    
//    
//    /***
//     * 整箱:人工上架分支
//     * @param outerContainerCode
//     * @param insideContainerCode
//     * @param funcId
//     * @param ouId
//     * @param userId
//     * @param putawayPatternDetailType
//     * @return
//     */
//    public ScanResultCommand containerManPutwayFlow(int putawayPatternType,Boolean isRecommendFail,String invAttrMgmtHouse,String locationCode,String locBarCode,String outerContainerCode,String insideContainerCode,Long funcId,Long ouId,Long userId,Integer putawayPatternDetailType,Warehouse warehouse);
//    
//    
//    /***
//     * 拆箱:人工上架分支
//     * @param outerContainerCode
//     * @param insideContainerCode
//     * @param funcId
//     * @param ouId
//     * @param userId
//     * @param putawayPatternDetailType
//     * @return
//     */
//    public ScanResultCommand splitContaienrManPutwayFlow(String tipLocationCode,int putawayPatternType,Boolean isNotUser,Boolean isRecommendFail,String invAttrMgmtHouse,String locationCode,String locBarCode,String outerContainerCode,String insideContainerCode,Long funcId,Long ouId,Long userId,Integer putawayPatternDetailType,Warehouse warehouse);
//    
//    /***
//     * 整托上架:扫描内部容器
//     * @param insideContainerCode
//     */
//    public void sysSuggestScanInsideContainer(String insideContainerCode,Long ouId);
//    
//    /***
//     * 拆箱上架：推荐库位
//     * @param outerContainerCode
//     * @param ouId
//     * @param userId
//     * @param insideContainerCode
//     * @param functionId
//     * @param putawayPatternDetailType
//     * @param warehouse
//     * @return
//     */
//    public ScanResultCommand splitPutWayRemmendLocation(String outerContainerCode,Long ouId,Long userId,String insideContainerCode,Long functionId,Integer putawayPatternDetailType,Warehouse warehouse);
//    
//
//    /***
//     * 取消流程
//     * @param outerContainerCode
//     * @param insideContainerCode
//     * @param skuId
//     * @param cancelPattern
//     */
//    public void cancelPattern(Long sId,Boolean isRecommendFail,Boolean isCancel,String outerContainerCode,String insideContainerCode,int cancelPattern,Long ouId,String locationCode,int putawayPatternDetailType);
//    
//    /***
//     * 点击取消按钮时获取上一个扫描的货箱号
//     * @param outerContainerCode
//     * @return
//     */
//    public String getPreviousContainerCode(String outerContainerCode,Long ouId);
//    
//    /***
//     * 返回下一个容器
//     * @param outerContainerCode
//     * @return
//     */
//    public String getPalletPutawayCacheSkuOrTipContainer(String outerContainerCode,Long ouId);
//    
//    
//    
//    /**
//     * 库位解绑（生成容器库存及删除待移入库存）
//     * 
//     * @param outerContainerCode
//     * @param insideContainerCode
//     * @param locationCode
//     * @param putawayPatternDetailType
//     * @param ouId
//     * @param userId
//     * @param logId
//     */
//    public void execUnbinding(String outerContainerCode, String insideContainerCode, String locationCode, Integer putawayPatternDetailType, Long ouId, Long userId, String logId);
//    
//    /***
//     * 点上架完成时，清楚缓存
//     * @param outerContainerCode
//     * @param insideContainerCode
//     * @param locationCode
//     * @param ouId
//     */
//    public void putawayEndRemoveCache(String outerContainerCode, String insideContainerCode, String locationCode,Long ouId);
//    
}
