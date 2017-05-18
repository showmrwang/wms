package com.baozun.scm.primservice.whoperation.manager.pda.work;

import com.baozun.scm.primservice.whoperation.command.pda.work.PickingScanResultCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSkuCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhWork;



public interface PdaReplenishmentWorkManager extends BaseManager {
    
    
    /**
     * 确定补货方式和占用模型
     * 
     * @author qiming.liu
     * @param whWork
     * @param ouId
     * @return
     */
    PickingScanResultCommand getReplenishmentForGroup(WhWork whWork, Long ouId);
    
    /***
     * 提示拣货库位
     * @author tangming
     * @param operationId
     * @return
     */
    public PickingScanResultCommand replenishmentTipLocation(Long functionId,Long operationId,Long ouId);
    
//    /***
//     * 拣货完成
//     * @param command
//     */
//    public void pdaPickingFinish(PickingScanResultCommand  command,Boolean isTabbInvTotal);
    
    /***
     * 缓存库位
     * @param operationId
     * @param locationCode
     * @param ouId
     */
    public void cacheLocation(Long operationId,String locationCode,Long ouId);
    
    /***
     * 拣货取消流程
     * @param outerContainerId
     * @param insideContainerId
     * @param cancelPattern
     * @param replenishWay
     * @param locationId
     * @param ouId
     */
    public void cancelPattern(String outerContainerCode, String insideContainerCode,int cancelPattern,int replenishWay,Long locationId,Long ouId,Long operationId,Long tipSkuId);
    
    /***
     * 校验周转箱
     * @param turnoverBoxCode
     * @param ouId
     * @return
     */
    public void verificationTurnoverBox(String turnoverBoxCode,Long ouId, Long operationId);
    
    /***
     * 修改周转箱状态
     * @param turnoverBoxCode
     * @param ouId
     */
    public void updateTurnoverBox(String turnoverBoxCode,Long ouId);
    
    /***
     * 判断是否是整箱或者整托
     * @param outerContainerCode
     * @param insideCotainerCode
     * @param ouId
     * @return
     */
    public PickingScanResultCommand judgeIsPalletContainer(String outerContainerCode,String insideCotainerCode,Long ouId,Long functionId,Long operationId);
    
    
//    public PickingScanResultCommand judge
    
}
