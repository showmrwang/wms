package com.baozun.scm.primservice.whoperation.manager.pda.work;

import com.baozun.scm.primservice.whoperation.command.pda.work.PickingScanResultCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSkuCommand;
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
     * 统计分析工作及明细并缓存
     * 
     * @author qiming.liu
     * @param whWork
     * @param ouId
     * @return
     */
    PickingScanResultCommand getOperatioLineForGroup(WhWork whWork, Long ouId);
    
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
    public PickingScanResultCommand scanSku(PickingScanResultCommand  command,WhSkuCommand skuCmd);
    
    /***
     * 判断货箱内库存属性是否唯一
     * @param command
     * @return
     */
    public PickingScanResultCommand judgeSkuAttrIdsIsUnique(PickingScanResultCommand  command);
}
