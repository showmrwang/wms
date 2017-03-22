package com.baozun.scm.primservice.whoperation.manager.odo.manager;

import java.util.List;

import com.baozun.scm.primservice.whoperation.command.odo.WhOdoOutBoundBoxCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoOutBoundBox;

public interface OdoOutBoundBoxMapper extends BaseManager {
    /**
     * [业务方法] 波次中创拣货工作-获取波次中的所有小批次
     * @param waveId
     * @param ouId
     * @return
     */
    List<WhOdoOutBoundBox> getPickingWorkWhOdoOutBoundBox(Long waveId, Long ouId);
    
    /**
     * [业务方法] 波次中创拣货工作-根据批次查询小批次分组数据
     * @param WhOdoOutBoundBox
     * @return
     */
    List<WhOdoOutBoundBox> getOdoOutBoundBoxForGroup(WhOdoOutBoundBox whOdoOutBoundBox);
    
    /**
     * [业务方法] 波次中创拣货工作-根据小批次分组查询出所有出库箱/容器信息
     * @param WhOdoOutBoundBox
     * @return
     */
    List<WhOdoOutBoundBoxCommand> getOdoOutBoundBoxListByGroup(WhOdoOutBoundBox whOdoOutBoundBox);
    
    /**
     * [业务方法] 波次中创拣货工作-查询对应的耗材
     * @param outbounxboxTypeId
     * @param outbounxboxTypeCode
     * @return
     */
    Long findOutboundboxType(Long outbounxboxTypeId, String outbounxboxTypeCode, Long ouId);
    
    /**
     * [业务方法] 波次中创拣货工作-根据id查询数据
     * @param id
     * @param ouId
     * @return
     */
    WhOdoOutBoundBoxCommand findWhOdoOutBoundBoxCommandById(Long id, Long ouId);
    
    /**
     * [业务方法] 波次中创拣货工作-设置出库箱行标识 
     * @param id
     * @param ouId
     * @return
     */
    Boolean saveOrUpdate(WhOdoOutBoundBoxCommand whOdoOutBoundBoxCommand);
    
    /**
     * 查询波次中的所有小批次 -- 捡货工作
     * 
     * @param waveId
     * @param ouId
     * @return
     */
    List<WhOdoOutBoundBox> getBoxBatchsForPicking(Long waveId, Long ouId);
    
}
