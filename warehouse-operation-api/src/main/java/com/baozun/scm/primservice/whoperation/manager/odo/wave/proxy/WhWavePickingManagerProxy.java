package com.baozun.scm.primservice.whoperation.manager.odo.wave.proxy;

import java.util.List;

import com.baozun.scm.primservice.whoperation.command.odo.WhOdoOutBoundBoxCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoOutBoundBox;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventory;


/**
 * @author qiming.liu
 *
 * 2016年11月02日 上午11:07:20
 */
public interface WhWavePickingManagerProxy extends BaseManager {

    /**
     * [业务方法] 创建拣货工作-返回小批次列表给上层服务
     * @param waveId
     * @param ouId
     * @return
     */
    public List<WhOdoOutBoundBox> getOdoOutBoundBoxForPicking(Long waveId, Long ouId);
    
    /**
     * [业务方法] 创建拣货工作-返回小批次分组数据列表给上层服务
     * @param WhOdoOutBoundBox
     * @return
     */
    public List<WhOdoOutBoundBox> getOdoOutBoundBoxForGroup(WhOdoOutBoundBox whOdoOutBoundBox);
    
    
    
    /**
     * [业务方法] 创建拣货工作-返回出库箱/容器信息列表给上层服务
     * @param WhOdoOutBoundBox
     * @return
     */
    public List<WhOdoOutBoundBoxCommand> getOdoOutBoundBoxListByGroup(WhOdoOutBoundBox whOdoOutBoundBox);
    
    
    
    /**
     * [业务方法] 创建拣货工作-创建工作头信息
     * @param WhOdoOutBoundBox
     * @param waveId
     * @param ouId
     * @return
     */
    public String saveWhWork(WhOdoOutBoundBox whOdoOutBoundBox, Long userId);
    
    /**
     * [业务方法] 创建拣货工作-查询占用的库存信息
     * @param WhOdoOutBoundBox
     * @return
     */
    public List<WhSkuInventory> getSkuInventory(WhOdoOutBoundBoxCommand whOdoOutBoundBoxCommand);
    
    /**
     * [业务方法] 创建拣货工作-创建工作明细信息
     * @param WhOdoOutBoundBox
     * @param waveId
     * @param ouId
     * @return
     */
    public int saveWorkLine(WhOdoOutBoundBoxCommand whOdoOutBoundBoxCommand, List<WhSkuInventory> whSkuInventoryList, Long userId, String workCode);
    
    /**
     * [业务方法] 创建拣货工作-更新工作头信息
     * @param WhOdoOutBoundBox
     * @return
     */
    public void updateWhWork(String workCode, WhOdoOutBoundBox whOdoOutBoundBox);
    
    /**
     * [业务方法] 创建拣货工作-创建作业头
     * @param WhOdoOutBoundBox
     * @return
     */
    public String saveWhOperation(String workCode, Long ouId);
    
    /**
     * [业务方法] 创建拣货工作-创建作业明细
     * @param List<WhOdoOutBoundBox>
     * @return
     */
    public int saveWhOperationLine(String workCode, String operationCode, Long ouId);
    
    /**
     * [业务方法] 创建拣货工作-设置出库箱行标识
     * @param WhOdoOutBoundBoxCommand
     * @return
     */
    public void updateWhOdoOutBoundBoxCommand (WhOdoOutBoundBoxCommand whOdoOutBoundBoxCommand);
    

}
