package com.baozun.scm.primservice.whoperation.manager.pda.work;

import com.baozun.scm.primservice.whoperation.command.pda.work.PickingScanResultCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOperationCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSkuCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.Location;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhWork;



public interface PdaPickingWorkManager extends BaseManager {
    
    
    /**
     * 保存工作操作员信息
     * 
     * @author qiming.liu
     * @param workId
     * @param ouId
     * @param userId
     * @return
     */
    void saveWorkOper (Long workId, Long ouId, Long userId);
    
    /**
     * [通用方法] 统计分析工作及明细并缓存
     * 
     * @author qiming.liu
     * @param WhOperationCommand
     * @return
     */
    void getOperatioLineForGroup(WhOperationCommand whOperationCommand);
    
    /**
     * 确定补货方式和占用模型
     * 
     * @author qiming.liu
     * @param whWork
     * @param ouId
     * @return
     */
    PickingScanResultCommand getPickingForGroup(WhWork whWork, Long ouId);
    
    /**
     * pda拣货推荐容器
     * @author tangming
     * @param command
     * @param pickingWay
     * @return
     */
    public PickingScanResultCommand pdaPickingRemmendContainer(PickingScanResultCommand  command);
    
    /**
     * 扫描容器
     * @author tangming
     * @param command
     * @return
     */
    public PickingScanResultCommand pdaPickingScanContainer(PickingScanResultCommand command);
    
    /***提示外部容器(托盘)
     * @author tangming
     * @param command
     * @return
     */
    public PickingScanResultCommand tipOuterContainer(PickingScanResultCommand command);
    
    /****
     * @author tangming
     * @param command
     * @return
     */
    public PickingScanResultCommand  tipInsideContainer(PickingScanResultCommand command);
    
    /***
     * pda拣货:推荐容器提示sku
     * @author tangminmg
     * @param command
     * @return
     */
    public PickingScanResultCommand  tipSku(PickingScanResultCommand  command,String operationWay);
    
    /***pda扫描sku
     * @author tangminmg
     * @param command
     * @return
     */
    public PickingScanResultCommand scanSku(PickingScanResultCommand  command,WhSkuCommand skuCmd,Boolean isTabbInvTotal,String operationWay);
    
    /***
     * 判断货箱内库存属性是否唯一
     * @param command
     * @return
     */
    public PickingScanResultCommand judgeSkuAttrIdsIsUnique(PickingScanResultCommand  command,String operationWay);
    
    /***
     * 出库箱或者周转箱/满箱处理
     * @param command
     * @return
     */
    public void scanTrunkfulContainer(PickingScanResultCommand  command);
    
    /***
     * 查询库存sn残次信息
     * @param sn
     * @param defectWareBarCode
     * @return
     */
    public PickingScanResultCommand judgeIsOccupationCode(PickingScanResultCommand command);
    
    /**
     * 整拖拣货模式生成作业执行明细
     * 
     * @author qiming.liu
     * @param PickingScanResultCommand
     * @param WhSkuCommand
     * @param isTabbInvTotal
     * @return
     */
    public PickingScanResultCommand palletPickingOperationExecLine(PickingScanResultCommand command, Boolean isTabbInvTotal);
    
    /**
     * 整箱拣货模式生成作业执行明细
     * 
     * @author qiming.liu
     * @param PickingScanResultCommand
     * @param WhSkuCommand
     * @param isTabbInvTotal
     * @return
     */
    public PickingScanResultCommand containerPickingOperationExecLine(PickingScanResultCommand command, Boolean isTabbInvTotal);
    
    /***
     * 拣货取消流程
     * @param outerContainerId
     * @param insideContainerId
     * @param cancelPattern
     * @param pickingType
     * @param locationId
     * @param ouId
     */
    public void cancelPattern(String carCode,String outerContainerCode,String insideContainerCode, int cancelPattern,int pickingWay,Long locationId,Long ouId,Long operationId,Long tipSkuId);
    
    /***
     * 缓存库位
     * @param operationId
     * @param locationCode
     * @param ouId
     */
    public void cacheLocation(Long operationId,Long locationId);
        
    /***
     * 有小车有出库箱的情况下(获取货格号)
     * @param operationId
     * @param outBounxBoxCode
     * @return
     */
    public Integer getUseContainerLatticeNo(Long operationId,String outBounxBoxCode);
    
    /***
     * 返回库位
     * @param locationCode
     * @return
     */
    public Location getLocationByCode(String locationCode,Long ouId);
    
    /***
     * 补货(拣货)取消流程
     * @param outerContainerId
     * @param insideContainerId
     * @param cancelPattern
     * @param pickingType
     * @param locationId
     * @param ouId
     */
    public void replenishCancelPattern(String outerContainerCode,String insideContainerCode, int cancelPattern,int replenishWay,Long locationId,Long ouId,Long operationId,Long tipSkuId);
    
    /**
     * 拣货完成
     * @param operationId
     */
    public void shortPickingEnd(String workCode,Long operationId,Long ouId,Long userId,String outBoundBoxCode,String turnoverBoxCode,Long outBoundBoxId);
    
    
    /***
     * 返回货格号
     * @param command
     * @param operationId
     */
    public Integer getLatticeNoBySkuAttrIds(PickingScanResultCommand  command,Long ouId);
    
    /**
     * 进入拣货作业时,如果缓存，存在先清楚
     * @param workId
     */
    public void removeCache(Long workId,Long ouId,Long operationId);
    
    /**
     * 是否继续扫描sn
     * @param insideContainerCode
     * @param skuId
     * @param ouId
     * @return
     */
    public Boolean isContainerScanSn(String insideContainerCode,Long skuId,Long ouId,Long locationId,Double scanSkuQty,Boolean isContinueScanSn,Long operationId);
    
    public PickingScanResultCommand toWholeCase(PickingScanResultCommand command, Boolean isTabbInvTotal, String operationWay);
    
    public String insertIntoCollection(PickingScanResultCommand command, Long ouId, Long userId);

    /**
     * 
     * @param pickingMode
     * @param operationId
     * @param ouId
     * @param userId
     */
    void changeOdoStatus(String pickingMode, Long operationId, Long ouId, Long userId);

    
    public void removeCache(Long operationId,Boolean isPicking, Long locationId);
    
    void createReplenishmentAfterPicking (Long operationId, Long ouId, Long userId);
    
    public void judeOutboundBoxIsUse(String outboundBoxCode,String tipOutboundBoxCode,Long ouId,Boolean isMgmtConsumableSku);
}
