package com.baozun.scm.primservice.whoperation.manager.pda.work;

import java.util.List;

import com.baozun.scm.primservice.whoperation.command.pda.work.PickingScanResultCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOperationCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSkuCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventorySnCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
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
    public PickingScanResultCommand pdaPickingScanContainer(PickingScanResultCommand  command);
    
    /***提示外部容器(托盘)
     * @author tangming
     * @param command
     * @return
     */
    public PickingScanResultCommand tipOuterContainer(PickingScanResultCommand  command);
    
    /**
     * pda拣货整托整箱--获取缓存中的统计信息
     * @author qiming.liu
     * @param command
     * @return
     */
    public PickingScanResultCommand pdaPickingWholeCase(PickingScanResultCommand  command);
    
    /****
     * @author tangming
     * @param command
     * @return
     */
    public PickingScanResultCommand  tipInsideContainer(PickingScanResultCommand  command);
    
    /***
     * pda拣货:推荐容器提示sku
     * @author tangminmg
     * @param command
     * @return
     */
    public PickingScanResultCommand  tipSku(PickingScanResultCommand  command);
    
    /***pda扫描sku
     * @author tangminmg
     * @param command
     * @return
     */
    public PickingScanResultCommand scanSku(PickingScanResultCommand  command,WhSkuCommand skuCmd,Boolean isTabbInvTotal);
    
    /***
     * 判断货箱内库存属性是否唯一
     * @param command
     * @return
     */
    public PickingScanResultCommand judgeSkuAttrIdsIsUnique(PickingScanResultCommand  command);
    
    /***
     * 出库箱或者周转箱/满箱处理
     * @param command
     * @return
     */
    public void scanTrunkfulContainer(PickingScanResultCommand  command);
    
    /**
     * 生成作业执行明细  
     * 
     * @author qiming.liu
     * @param PickingScanResultCommand
     * @return
     */
    void operatorExecutionLines(PickingScanResultCommand command);
    
    /**
     * 循环提示内部容器--整箱整托拣货
     * 
     * @author qiming.liu
     * @param PickingScanResultCommand
     * @return PickingScanResultCommand
     */
    PickingScanResultCommand wholeCaseForTipInsideContainer(PickingScanResultCommand command);
    
    /**
     * 循环提示扫描商品--整箱整托拣货
     * 
     * @author qiming.liu
     * @param PickingScanResultCommand
     * @return PickingScanResultCommand
     */
    PickingScanResultCommand wholeCaseForTipSku(PickingScanResultCommand command);
    
    /**
     * 循环扫描当前商品--整箱整托拣货
     * 
     * @author qiming.liu
     * @param PickingScanResultCommand 
     * @return PickingScanResultCommand
     */
    PickingScanResultCommand wholeCaseForTipCurrentSku(PickingScanResultCommand command);
    
    /**
     * 提示托盘--整箱整托拣货 
     * 
     * @author qiming.liu
     * @param PickingScanResultCommand
     * @return
     */
    PickingScanResultCommand wholeCaseTipTray(PickingScanResultCommand command);
    
    /**
     * 提示内部容器--整箱整托拣货 
     * 
     * @author qiming.liu
     * @param PickingScanResultCommand
     * @return
     */
    PickingScanResultCommand wholeCaseTipInsideContainer(PickingScanResultCommand command);
    
    /**
     * 判断是否是SN/残次商品--整箱整托拣货
     * 
     * @author qiming.liu
     * @param 
     * @return
     */
    PickingScanResultCommand wholeCaseIsSn(PickingScanResultCommand command); 
    
    /**
     * 根据库存UUID查找对应SN/残次信息
     * 
     * @author qiming.liu
     * @param 
     * @return
     */
    List<WhSkuInventorySnCommand> findWhSkuInventoryByUuid(Long ouid, String uuid);
    
    /***
     * 查询库存sn残次信息
     * @param sn
     * @param defectWareBarCode
     * @return
     */
    public PickingScanResultCommand judgeIsOccupationCode(PickingScanResultCommand command);
    
}
