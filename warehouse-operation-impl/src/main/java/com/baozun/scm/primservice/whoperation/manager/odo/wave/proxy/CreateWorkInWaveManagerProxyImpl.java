package com.baozun.scm.primservice.whoperation.manager.odo.wave.proxy;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.baseservice.sac.manager.CodeManager;
import com.baozun.scm.primservice.whoperation.command.odo.OdoCommand;
import com.baozun.scm.primservice.whoperation.command.odo.WhOdoOutBoundBoxCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.LocationCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ReplenishmentRuleCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOperationCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOperationLineCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhWorkCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhWorkLineCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryAllocatedCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.WavePhase;
import com.baozun.scm.primservice.whoperation.constant.WaveStatus;
import com.baozun.scm.primservice.whoperation.constant.WorkStatus;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoDao;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoOutBoundBoxDao;
import com.baozun.scm.primservice.whoperation.dao.odo.wave.WhWaveDao;
import com.baozun.scm.primservice.whoperation.dao.odo.wave.WhWaveMasterDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.AreaDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ReplenishmentRuleDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ReplenishmentTaskDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhLocationDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOperationDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOperationLineDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhWorkDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhWorkLineDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WorkTypeDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryAllocatedDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryTobefilledDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.model.BaseModel;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoOutBoundBox;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWave;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWaveMaster;
import com.baozun.scm.primservice.whoperation.model.warehouse.Area;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container;
import com.baozun.scm.primservice.whoperation.model.warehouse.ReplenishmentTask;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOperation;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOperationLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhWork;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhWorkLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.WorkType;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventory;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventoryTobefilled;

@Service("createWorkInWaveManagerProxy")
@Transactional
public class CreateWorkInWaveManagerProxyImpl implements CreateWorkInWaveManagerProxy {

    @Autowired
    private CodeManager codeManager;
    
    @Autowired
    private WhWaveDao waveDao;
    
    @Autowired
    private WhOdoOutBoundBoxDao odoOutBoundBoxDao;
    
    @Autowired
    private WhWorkDao workDao;
    
    @Autowired
    private WhWorkLineDao workLineDao;
    
    @Autowired
    private WhOperationDao operationDao;
    
    @Autowired
    private WhOperationLineDao operationLineDao;
    
    @Autowired
    private WorkTypeDao workTypeDao;
    
    @Autowired
    private WhSkuInventoryDao skuInventoryDao;
    
    @Autowired
    private WhOdoDao odoDao;
    
    @Autowired
    private ContainerDao containerDao;
    
    @Autowired
    private WhWaveMasterDao waveMasterDao;
    
    @Autowired
    private AreaDao areaDao;
    
    @Autowired
    private WhLocationDao locationDao;
    
    @Autowired
    private ReplenishmentRuleDao replenishmentRuleDao;
    
    @Autowired
    private WhSkuInventoryAllocatedDao skuInventoryAllocatedDao;
    
    @Autowired
    private WhSkuInventoryTobefilledDao skuInventoryTobefilledDao;
    
    @Autowired
    private ReplenishmentTaskDao replenishmentTaskDao;
    
    
    
    
    /**
     * 创建拣货工作和作业
     * 
     * @param whOdoOutBoundBox
     * @param userId
     * @return
     */
    @Override
    public void createWorkInWave(Long waveId, Long ouId, Long userId) {
        // 查询补货工作释放及拆分条件分组 -- 补货工作
        List<ReplenishmentRuleCommand> replenishmentRuleCommands = this.getReplenishmentConditionGroup(waveId, ouId);
        // 循环补货工作释放及拆分条件分组        
        for(ReplenishmentRuleCommand replenishmentRuleCommand : replenishmentRuleCommands){
            // 根据补货工作拆分条件统计分析补货数据 
            Map<String, List<WhSkuInventoryAllocatedCommand>> siacMap = getSkuInventoryAllocatedCommandForGroup(replenishmentRuleCommand);
            // 循环统计的分组信息分别创建工作           
            for(String key : siacMap.keySet()){
                // 创建拣货工作--创建工作头信息
                String replenishmentWorkCode = this.saveReplenishmentWork(waveId, siacMap.get(key).get(0), userId);
                int rWorkLineTotal = 0;
                // 循环统计的分组补货信息列表
                for(WhSkuInventoryAllocatedCommand skuInventoryAllocatedCommand : siacMap.get(key)){
                    // 判断分配量与待移入量是否相等
                    if(skuInventoryAllocatedCommand.getQty() != skuInventoryAllocatedCommand.getToQty()){
                        throw new BusinessException("分配量与待移入量不相等");
                    }
                    // 创建补货工作明细
                    this.saveReplenishmentWorkLine(key, replenishmentWorkCode, userId, skuInventoryAllocatedCommand);
                    rWorkLineTotal = rWorkLineTotal + 1;
                }
                // 校验工作明细数量是否正确
                if (rWorkLineTotal != siacMap.get(key).size()) {
                    throw new BusinessException("工作明细数量不正确");
                }
                // 更新补货工作头信息
                this.updateReplenishmentWork(waveId, replenishmentWorkCode, siacMap.get(key).get(0));
                // 生成作业头
                String replenishmentOperationCode = this.saveReplenishmentOperation(key, replenishmentWorkCode, siacMap.get(key).get(0));
                // 判断补货工作释放方式是否是按照需求量释放
                if(1 == replenishmentRuleCommand.getReleaseWorkWay()){
                    // 基于工作明细生成作业明细
                    int replenishmentOperationLineCount = this.saveReplenishmentOperationLine(replenishmentWorkCode, replenishmentOperationCode, ouId);
                    // 校验作业明细
                    if (replenishmentOperationLineCount != rWorkLineTotal) {
                        throw new BusinessException("创建的作业明细不正确");
                    }
                }else{
                    // 计算目标库位容器-- TODO --明天问是否有共同方法
                    // 基于目标库位容器及工作明细生成作业明细                    
                }
            }
        }
        
        // 查询出小批次列表 -- 捡货工作
        List<WhOdoOutBoundBox> whOdoOutBoundBoxList = this.getBoxBatchsForPicking(waveId, ouId);
        if (null == whOdoOutBoundBoxList || whOdoOutBoundBoxList.isEmpty()) {
            throw new BusinessException("找不到波次明细");
        }
        // 循环小批次        
        for (WhOdoOutBoundBox whOdoOutBoundBox : whOdoOutBoundBoxList) {
            //根据批次查询小批次分组数据            
            whOdoOutBoundBox.setOuId(ouId);
            List<WhOdoOutBoundBox> odoOutBoundBoxForGroup = this.getOdoOutBoundBoxForGroup(whOdoOutBoundBox);
            //循环小批次下所有分组信息分别创建工作 和作业         
            for(WhOdoOutBoundBox whOdoOutBoundBoxGroup : odoOutBoundBoxForGroup){
                //2.1.1 根据小批次分组查询出所有出库箱/容器信息
                List<WhOdoOutBoundBoxCommand> whOdoOutBoundBoxCommandList = this.getOdoOutBoundBoxListByGroup(whOdoOutBoundBoxGroup);
                //2.1.2 创建拣货工作--创建工作头信息
                String workCode = this.savePickingWork(whOdoOutBoundBoxGroup,userId);
                int workLineTotal = 0;
                //2.1.3 循环出库箱/容器信息列表创建工作明细
                for(WhOdoOutBoundBoxCommand whOdoOutBoundBoxCommand : whOdoOutBoundBoxCommandList){
                    //2.1.3.1 判断库位占用量是否满足 
                    //根据占用单据号和占用单据明细行ID查询库存列表                        
                    List<WhSkuInventory> whSkuInventoryList = this.getSkuInventory(whOdoOutBoundBoxCommand);
                    //初始化占用库存量
                    Double onHandQty =  0.0 ;
                    //计算占用库存量                       
                    for(WhSkuInventory whSkuInventory : whSkuInventoryList){
                        onHandQty =  whSkuInventory.getOnHandQty() + onHandQty;
                    }
                    //如果不满足，则抛出异常 
                    if(!onHandQty.equals(whOdoOutBoundBoxCommand.getQty())){
                        throw new BusinessException("库位占用量不满足");
                    }
                    //2.1.3.2 创建工作明细
                    int workLineCount = this.savePickingWorkLine(whOdoOutBoundBoxCommand, whSkuInventoryList, userId, workCode);
                    //2.1.3.3 设置出库箱行标识  
                    this.updateWhOdoOutBoundBoxCommand(whOdoOutBoundBoxCommand);
                    //2.1.3.4 校验工作明细是否正确
                    if (workLineCount != whSkuInventoryList.size()) {
                        throw new BusinessException("创建工作明细数量不正确");
                    }else{
                        workLineTotal = workLineCount + workLineTotal;
                    }
                }
                //2.1.4 更新工作头信息
                this.updatePickingWork(workCode, whOdoOutBoundBoxGroup);
                //2.1.5 创建作业头
                String operationCode = this.savePickingOperation(workCode, whOdoOutBoundBoxGroup);
                //2.1.6 创建作业明细
                int whOperationLineCount = this.savePickingOperationLine(workCode, operationCode, whOdoOutBoundBox.getOuId());
                //2.1.10 作业明细校验
                if (whOperationLineCount != workLineTotal) {
                    throw new BusinessException("创建的作业明细不正确");
                }
            }
        }
    }

    /**
     * 查询出波次头信息
     * 
     * @param waveId
     * @param ouId
     * @return
     */
    @Override
    public WhWave getWhWaveHead(Long waveId, Long ouId) {
        // 获取波次头并校验波次信息
        if (null == waveId || null == ouId) {
            throw new BusinessException("创工作 : 没有参数");
        }
        WhWave oldWhWave = new WhWave();
        oldWhWave.setId(waveId);
        oldWhWave.setOuId(ouId);
        oldWhWave.setLifecycle(BaseModel.LIFECYCLE_NORMAL);
        List<WhWave> whWaveList = this.waveDao.findListByParam(oldWhWave);
        if (null == whWaveList || 1 != whWaveList.size()) {
            throw new BusinessException("多个波次");
        }
        WhWave whWave = whWaveList.get(0);
        
        if (null == whWave) {
            throw new BusinessException("没有波次头信息");
        }
        if (BaseModel.LIFECYCLE_NORMAL != whWave.getLifecycle() || WaveStatus.WAVE_EXECUTING != whWave.getStatus() || !WavePhase.CREATE_WORK.equals(whWave.getPhaseCode())) {
            throw new BusinessException("波次头不可用或波次状态不为运行中或波次阶段不为创建工作");
        }
        return whWave;
    }
    
    /**
     * 查询补货工作释放及拆分条件分组 -- 补货工作
     * 
     * @param waveId
     * @param ouId
     * @return
     */
    @Override
    public List<ReplenishmentRuleCommand> getReplenishmentConditionGroup(Long waveId, Long ouId) {
        // 查询补货工作释放及拆分条件分组
        List<ReplenishmentRuleCommand> replenishmentRuleCommands = this.replenishmentRuleDao.getReplenishmentConditionGroup(waveId, ouId);
        return replenishmentRuleCommands;
    }
    
    /**
     * 根据补货工作释放及拆分条件获取所有补货数据
     * 
     * @param WhSkuInventoryAllocatedCommand
     * @return
     */
    @Override
    public List<WhSkuInventoryAllocatedCommand> getAllReplenishmentLst(ReplenishmentRuleCommand replenishmentRuleCommand) {
        // 根据补货工作释放及拆分条件获取所有补货数据
        List<WhSkuInventoryAllocatedCommand> skuInventoryAllocatedCommandLst = this.skuInventoryAllocatedDao.getAllReplenishmentLst(replenishmentRuleCommand);
        return skuInventoryAllocatedCommandLst;
    }
    
    /**
     * 查询波次中的所有小批次 -- 捡货工作
     * 
     * @param waveId
     * @param ouId
     * @return
     */
    @Override
    public List<WhOdoOutBoundBox> getBoxBatchsForPicking(Long waveId, Long ouId) {
        // 查询波次中的所有小批次
        List<WhOdoOutBoundBox> whOdoOutBoundBoxList = this.odoOutBoundBoxDao.findPickingWorkWhOdoOutBoundBox(waveId, ouId);
        return whOdoOutBoundBoxList;
    }
    
    
    /**
     * 根据批次查询小批次分组数据
     * 
     * @param whOdoOutBoundBox
     * @return
     */
    @Override
    public List<WhOdoOutBoundBox> getOdoOutBoundBoxForGroup(WhOdoOutBoundBox whOdoOutBoundBox) {
        List<WhOdoOutBoundBox> whOdoOutBoundBoxList = this.odoOutBoundBoxDao.getOdoOutBoundBoxForGroup(whOdoOutBoundBox);
        return whOdoOutBoundBoxList;
    }

    /**
     * 根据小批次分组查询出所有出库箱/容器信息
     * 
     * @param whOdoOutBoundBox
     * @return
     */
    @Override
    public List<WhOdoOutBoundBoxCommand> getOdoOutBoundBoxListByGroup(WhOdoOutBoundBox whOdoOutBoundBox) {
        List<WhOdoOutBoundBoxCommand> whOdoOutBoundBoxCommandList = this.odoOutBoundBoxDao.getOdoOutBoundBoxListByGroup(whOdoOutBoundBox);
        return whOdoOutBoundBoxCommandList;
    }

    /**
     *  创建工作头信息
     * 
     * @param whOdoOutBoundBox
     * @param userId
     * @return
     */
    @Override
    public String savePickingWork(WhOdoOutBoundBox whOdoOutBoundBox, Long userId) {
        //查询波次头信息     
        if (null == whOdoOutBoundBox.getWaveId() || null == whOdoOutBoundBox.getOuId()) {
            throw new BusinessException("没有参数");
        }
        WhWave oldWhWave = new WhWave();
        oldWhWave.setId(whOdoOutBoundBox.getWaveId());
        oldWhWave.setOuId(whOdoOutBoundBox.getOuId());
        oldWhWave.setLifecycle(BaseModel.LIFECYCLE_NORMAL);
        List<WhWave> whWaveList = this.waveDao.findListByParam(oldWhWave);
        if (null == whWaveList || 1 != whWaveList.size()) {
            throw new BusinessException("多个波次");
        }
        WhWave whWave = whWaveList.get(0);
        //查询波次主档信息     
        WhWaveMaster whWaveMaster = waveMasterDao.findByIdExt(whWave.getWaveMasterId(), whWave.getOuId());
        //获取工作类型      
        WorkType workType = this.workTypeDao.findWorkTypeByworkCategory("PICKING", whOdoOutBoundBox.getOuId());
        //根据容器ID获取容器CODE      
        Container container = new Container();
        if(whOdoOutBoundBox.getOuterContainerId() != null){
            container = containerDao.findByIdExt(whOdoOutBoundBox.getOuterContainerId(),whOdoOutBoundBox.getOuId());
        }else{
            container = containerDao.findByIdExt(whOdoOutBoundBox.getContainerId(),whOdoOutBoundBox.getOuId());
        }
        //调编码生成器工作头实体标识        
        String workCode = codeManager.generateCode(Constants.WMS, Constants.WHWORK_MODEL_URL, "", "WORK", null);
        
        //所有值为null的字段，将会在更新工作头信息时获取，如果到时候还没有值，那就是被骗了        
        WhWorkCommand whWorkCommand = new WhWorkCommand();
        //工作号        
        whWorkCommand.setCode(workCode);
        //工作状态，系统常量        
        whWorkCommand.setStatus(WorkStatus.NEW);
        //仓库组织ID        
        whWorkCommand.setOuId(whOdoOutBoundBox.getOuId());
        //工作类型编码
        whWorkCommand.setWorkType(null == workType ? null : workType.getCode());
        //工作类别编码 
        whWorkCommand.setWorkCategory("PICKING");
        //是否锁定 默认值：1
        whWorkCommand.setIsLocked(true);
        //是否已迁出        
        whWorkCommand.setIsAssignOut(false);
        //当前工作明细设计到的所有库区编码信息列表--更新时获取数据      
        whWorkCommand.setWorkArea(null);
        //工作优先级     
        whWorkCommand.setWorkPriority(whWaveMaster.getPickingWorkPriority());
        //小批次
        whWorkCommand.setBatch(whOdoOutBoundBox.getBoxBatch());
        //操作开始时间
        whWorkCommand.setStartTime(new Date());
        //操作结束时间
        whWorkCommand.setFinishTime(new Date());
        //波次ID
        whWorkCommand.setWaveId(whOdoOutBoundBox.getWaveId());
        //波次号
        whWorkCommand.setWaveCode(whWave.getCode());
        //订单号--更新时获取数据
        whWorkCommand.setOrderCode(null);
        //库位--更新时获取数据
        whWorkCommand.setLocationCode(null);
        //托盘--更新时获取数据
        whWorkCommand.setOuterContainerCode(null);
        //容器
        whWorkCommand.setContainerCode(null == container ? null : container.getCode());
        //创建时间
        whWorkCommand.setCreateTime(new Date());
        //最后操作时间
        whWorkCommand.setLastModifyTime(new Date());
        //创建人ID
        whWorkCommand.setCreatedId(userId);
        //修改人ID
        whWorkCommand.setModifiedId(userId);
        //操作人ID
        whWorkCommand.setOperatorId(userId);
        //是否启用 1:启用 0:停用
        whWorkCommand.setLifecycle(0);
        
        WhWork whWork = new WhWork();
        //复制数据        
        BeanUtils.copyProperties(whWorkCommand, whWork);
        if(null != whWorkCommand.getId() ){
            workDao.saveOrUpdateByVersion(whWork);
        }else{
            workDao.insert(whWork);
        }
        
        return workCode;
    }

    /**
     * 查询库存信息
     * 
     * @param whOdoOutBoundBoxCommand
     * @return
     */
    @Override
    public List<WhSkuInventory> getSkuInventory(WhOdoOutBoundBoxCommand whOdoOutBoundBoxCommand) {
        WhOdo whOdo = this.odoDao.findByIdOuId(whOdoOutBoundBoxCommand.getOdoId(), whOdoOutBoundBoxCommand.getOuId());
        WhSkuInventory whSkuInventory = new WhSkuInventory();
        whSkuInventory.setOccupationCode(whOdo.getOdoCode());
        whSkuInventory.setOccupationLineId(whOdoOutBoundBoxCommand.getOdoLineId());
        whSkuInventory.setIsLocked(null);
        List<WhSkuInventory> SkuInventoryList = this.skuInventoryDao.getSkuInvListGroupUuid(whSkuInventory);
        return SkuInventoryList;
    }
    
    /**
     *  创建工作明细信息
     * 
     * @param whOdoOutBoundBox
     * @param whSkuInventoryList
     * @param userId
     * @return
     */
    @Override
    public int savePickingWorkLine(WhOdoOutBoundBoxCommand whOdoOutBoundBoxCommand, List<WhSkuInventory> whSkuInventoryList, Long userId, String workCode) {
        //获取工作头信息        
        WhWorkCommand whWorkCommand = this.workDao.findWorkByWorkCode(workCode, whOdoOutBoundBoxCommand.getOuId());
        //查询对应的耗材        
        Long skuId = odoOutBoundBoxDao.findOutboundboxType(whOdoOutBoundBoxCommand.getOutbounxboxTypeId(), whOdoOutBoundBoxCommand.getOutbounxboxTypeCode(), whOdoOutBoundBoxCommand.getOuId());
        //调编码生成器工作明细实体标识
        String workLineCode = codeManager.generateCode(Constants.WMS, Constants.WHWORKLINE_MODEL_URL, "", "WORKLINE", null);
        int count = 0;
        
        for(WhSkuInventory whSkuInventory : whSkuInventoryList){
            
            WhWorkLineCommand whWorkLineCommand = new WhWorkLineCommand();
            
            //工作明细号  
            whWorkLineCommand.setLineCode(workLineCode);
            //工作ID            
            whWorkLineCommand.setWorkId(whWorkCommand.getId());
            //仓库组织ID 
            whWorkLineCommand.setOuId(whOdoOutBoundBoxCommand.getOuId());
            //操作开始时间 
            whWorkLineCommand.setStartTime(null);
            //操作结束时间 
            whWorkLineCommand.setFinishTime(null);
            //商品ID 
            whWorkLineCommand.setSkuId(whSkuInventory.getSkuId());
            //计划量 
            if(whSkuInventoryList.size() == 1){
                whWorkLineCommand.setQty(whOdoOutBoundBoxCommand.getQty());
            }else{
                whWorkLineCommand.setQty(whSkuInventory.getOnHandQty());
            }
            //执行量/完成量 
            whWorkLineCommand.setCompleteQty(null);
            //取消量 
            whWorkLineCommand.setCancelQty(null);
            //库存状态 
            whWorkLineCommand.setInvStatus(whSkuInventory.getInvStatus());
            //库存类型  
            whWorkLineCommand.setInvType(whSkuInventory.getInvType());
            //批次号 
            whWorkLineCommand.setBatchNumber(whSkuInventory.getBatchNumber());
            //生产日期 
            whWorkLineCommand.setMfgDate(whSkuInventory.getMfgDate());
            //失效日期 
            whWorkLineCommand.setExpDate(whSkuInventory.getExpDate());
            //最小失效日期 
            whWorkLineCommand.setMinExpDate(null);
            //最大失效日期 
            whWorkLineCommand.setMaxExpDate(null);
            //原产地 
            whWorkLineCommand.setCountryOfOrigin(whSkuInventory.getCountryOfOrigin());
            //库存属性1 
            whWorkLineCommand.setInvAttr1(whSkuInventory.getInvAttr1());
            //库存属性2 
            whWorkLineCommand.setInvAttr2(whSkuInventory.getInvAttr2());
            //库存属性3 
            whWorkLineCommand.setInvAttr3(whSkuInventory.getInvAttr3());
            //库存属性4 
            whWorkLineCommand.setInvAttr4(whSkuInventory.getInvAttr4());
            //库存属性5 
            whWorkLineCommand.setInvAttr5(whSkuInventory.getInvAttr5());
            //内部对接码 
            whWorkLineCommand.setUuid(whSkuInventory.getUuid());
            //原始库位 
            whWorkLineCommand.setFromLocationId(whSkuInventory.getLocationId());
            //原始库位外部容器 
            whWorkLineCommand.setFromOuterContainerId(whSkuInventory.getOuterContainerId());
            //原始库位内部容器 
            whWorkLineCommand.setFromInsideContainerId(whSkuInventory.getInsideContainerId());
            //使用出库箱，耗材ID
            whWorkLineCommand.setUseOutboundboxId(skuId);
            //使用出库箱编码 
            whWorkLineCommand.setUseOutboundboxCode(whOdoOutBoundBoxCommand.getOutbounxboxTypeCode());
            //使用容器 
            whWorkLineCommand.setUseContainerId(whOdoOutBoundBoxCommand.getContainerId());
            //使用外部容器，小车 
            whWorkLineCommand.setUseOuterContainerId(whOdoOutBoundBoxCommand.getOuterContainerId());
            //使用货格编码数
            whWorkLineCommand.setUseContainerLatticeNo(whOdoOutBoundBoxCommand.getContainerLatticeNo());
            //目标库位 --捡货模式没有
            whWorkLineCommand.setToLocationId(null);
            //目标库位外部容器 --捡货模式没有
            whWorkLineCommand.setToOuterContainerId(null);
            //目标库位内部容器 --捡货模式没有
            whWorkLineCommand.setToInsideContainerId(null);
            if(null != whOdoOutBoundBoxCommand.getWholeCase()){
                //是否整托整箱
                whWorkLineCommand.setIsWholeCase(true);  
            }else{
                //是否整托整箱
                whWorkLineCommand.setIsWholeCase(false);  
            }
            //出库单ID 
            whWorkLineCommand.setOdoId(whOdoOutBoundBoxCommand.getOdoId());
            //出库单明细ID 
            whWorkLineCommand.setOdoLineId(whOdoOutBoundBoxCommand.getOdoLineId());
            //创建时间 
            whWorkLineCommand.setCreateTime(new Date());
            //最后操作时间 
            whWorkLineCommand.setLastModifyTime(new Date());
            //操作人ID 
            whWorkLineCommand.setOperatorId(userId);
            
            WhWorkLine whWorkLine = new WhWorkLine();
            //复制数据        
            BeanUtils.copyProperties(whWorkLineCommand, whWorkLine);
            if(null != whWorkLineCommand.getId() ){
                workLineDao.saveOrUpdateByVersion(whWorkLine);
            }else{
                workLineDao.insert(whWorkLine);
            }
            
            count = count +1;
            
        }
        return count;
    }
    
    /**
     * 更新工作头信息
     * @param WhOdoOutBoundBox
     * @return
     */
    @Override
    public void updatePickingWork(String workCode, WhOdoOutBoundBox odoOutBoundBox) {
        //获取工作头信息        
        WhWorkCommand whWorkCommand = this.workDao.findWorkByWorkCode(workCode, odoOutBoundBox.getOuId());
        //获取工作明细信息列表        
        List<WhWorkLineCommand> whWorkLineCommandList = this.workLineDao.findWorkLineByWorkId(whWorkCommand.getId(), odoOutBoundBox.getOuId());
        //查询波次头信息     
        if (null == odoOutBoundBox.getWaveId() || null == odoOutBoundBox.getOuId()) {
            throw new BusinessException("查询波次头信息  : 没有参数");
        }
        WhWave oldWhWave = new WhWave();
        oldWhWave.setId(odoOutBoundBox.getWaveId());
        oldWhWave.setOuId(odoOutBoundBox.getOuId());
        oldWhWave.setLifecycle(BaseModel.LIFECYCLE_NORMAL);
        List<WhWave> whWaveList = this.waveDao.findListByParam(oldWhWave);
        if (null == whWaveList || 1 != whWaveList.size()) {
            throw new BusinessException("多个波次");
        }
        WhWave whWave = whWaveList.get(0);
        //查询波次主档信息     
        WhWaveMaster whWaveMaster = waveMasterDao.findByIdExt(whWave.getWaveMasterId(), whWave.getOuId());
        String workArea = "" ;
        int count = 0;
        Boolean isFromLocationId = true;
        Boolean isUseOuterContainerId = true;
        Boolean isUseContainerId = true;
        Boolean isOdoId = true;
        
        for(WhWorkLineCommand whWorkLineCommand : whWorkLineCommandList){
            if(count !=  0){
                //获取上一次循环的实体类            
                WhWorkLineCommand whWorkLineCommandBefor = whWorkLineCommandList.get(count-1);
                
                if(whWorkLineCommandBefor.getFromLocationId() != whWorkLineCommand.getFromLocationId()){
                    isFromLocationId = false;
                }
                if(whWorkLineCommandBefor.getUseOuterContainerId() != whWorkLineCommand.getUseOuterContainerId()){
                    isUseOuterContainerId = false;
                }
                if(whWorkLineCommandBefor.getUseContainerId() != whWorkLineCommand.getUseContainerId()){
                    isUseContainerId = false;
                }
                if(whWorkLineCommandBefor.getOdoId() != whWorkLineCommand.getOdoId()){
                    isOdoId = false;
                }
            }
            
            LocationCommand locationCommand = locationDao.findLocationCommandByParam(whWorkLineCommand.getFromLocationId(), whWorkLineCommand.getOuId());
            if(null != locationCommand){
                Area area = areaDao.findByIdExt(locationCommand.getWorkAreaId(),locationCommand.getOuId());
                if(workArea == ""){
                    workArea = area.getAreaCode();
                }else{
                    workArea = workArea +","+area.getAreaCode();
                }
            }
            //索引自增            
            count++;
        }
        //判断工作明细是否只有唯一库位
        if(isFromLocationId == true){
            //获取库位表数据                              
            LocationCommand locationCommand = locationDao.findLocationCommandByParam(whWorkLineCommandList.get(0).getFromLocationId(), whWorkLineCommandList.get(0).getOuId());
            if(null != locationCommand){
              //设置库位      
              whWorkCommand.setLocationCode(locationCommand.getCode());
            }
        }
        //判断工作明细是否只有唯一外部容器
        if(isUseOuterContainerId == true){
            //根据容器ID获取容器CODE      
            Container containerOut = new Container();
            containerOut = containerDao.findByIdExt(whWorkLineCommandList.get(0).getUseOuterContainerId(),odoOutBoundBox.getOuId());
            if(null != containerOut){
                //设置外部容器
                whWorkCommand.setOuterContainerCode(containerOut.getCode());
            }
        }
        //判断据工作明细是否只有唯一内部容器
        if(isUseContainerId == true){
            //根据容器ID获取容器CODE      
            Container containerIn = new Container();
            containerIn = containerDao.findByIdExt(whWorkLineCommandList.get(0).getUseContainerId(),odoOutBoundBox.getOuId());
            if(null != containerIn){
                //设置内部容器
                whWorkCommand.setContainerCode(containerIn.getCode());
            }
        }
        //判断据工作明细是否只有唯一出库单
        if(isOdoId == true){
            OdoCommand odoCommand = this.odoDao.findCommandByIdOuId(whWorkLineCommandList.get(0).getOdoId(), odoOutBoundBox.getOuId());
            if(null != odoCommand){
                //设置订单号
                whWorkCommand.setOrderCode(odoCommand.getEcOrderCode());
            }
        }
        
        //当前工作明细设计到的所有库区编码信息列表
        whWorkCommand.setWorkArea(workArea);
        //是否锁定 默认值：1
        whWorkCommand.setIsLocked(whWaveMaster.getIsAutoReleaseWork());
        //工作优先级     
        whWorkCommand.setWorkPriority(whWaveMaster.getPickingWorkPriority());
        
        WhWork whWork = new WhWork();
        //复制数据        
        BeanUtils.copyProperties(whWorkCommand, whWork);
        if(null != whWorkCommand.getId() ){
            workDao.saveOrUpdateByVersion(whWork);
        }else{
            workDao.insert(whWork);
        }
    }

    /**
     * 创建作业头
     * @param workCode
     * @param WhOdoOutBoundBox
     * @return
     */
    @Override
    public String savePickingOperation(String workCode, WhOdoOutBoundBox whOdoOutBoundBox) {
      //获取工作头信息        
      WhWorkCommand whWorkCommand = this.workDao.findWorkByWorkCode(workCode, whOdoOutBoundBox.getOuId());
        
      //调编码生成器工作明细实体标识
      String operationCode = codeManager.generateCode(Constants.WMS, Constants.WHOPERATION_MODEL_URL, "", "OPERATION", null);
      WhOperationCommand WhOperationCommand = new WhOperationCommand();
      //作业号
      WhOperationCommand.setCode(operationCode);
      //工作ID
      WhOperationCommand.setWorkId(whWorkCommand.getId());
      //仓库组织ID
      WhOperationCommand.setOuId(whWorkCommand.getOuId());
      //工作类型编码
      WhOperationCommand.setWorkType(whWorkCommand.getWorkType());
      //工作类别编码 
      WhOperationCommand.setWorkCategory(whWorkCommand.getWorkCategory());
      //优先级 
      WhOperationCommand.setWorkPriority(whWorkCommand.getWorkPriority());
      //小批次
      WhOperationCommand.setBatch(whWorkCommand.getBatch());
      //操作开始时间
      WhOperationCommand.setStartTime(whWorkCommand.getStartTime());
      //操作结束时间
      WhOperationCommand.setFinishTime(whWorkCommand.getFinishTime());
      //波次ID
      WhOperationCommand.setWaveId(whWorkCommand.getWaveId());
      //波次号
      WhOperationCommand.setWaveCode(whWorkCommand.getWaveCode());
      //订单号
      WhOperationCommand.setOrderCode(whWorkCommand.getOrderCode());
      //库位 
      WhOperationCommand.setLocationCode(whWorkCommand.getLocationCode());
      //托盘
      WhOperationCommand.setOuterContainerCode(whWorkCommand.getOuterContainerCode());
      //容器
      WhOperationCommand.setContainerCode(whWorkCommand.getContainerCode());
      //是否整托整箱
      if(null != whOdoOutBoundBox.getWholeCase()){
          //是否整托整箱
          WhOperationCommand.setIsWholeCase(true);  
      }else{
          //是否整托整箱
          WhOperationCommand.setIsWholeCase(false);  
      }
      //创建时间 
      WhOperationCommand.setCreateTime(whWorkCommand.getCreateTime());
      //最后操作时间
      WhOperationCommand.setLastModifyTime(whWorkCommand.getLastModifyTime());
      //创建人ID
      WhOperationCommand.setCreatedId(whWorkCommand.getCreatedId());
      //修改人ID
      WhOperationCommand.setModifiedId(whWorkCommand.getModifiedId());
      //操作人ID
      WhOperationCommand.setOperatorId(whWorkCommand.getOperatorId());
      //是否启用 1:启用 0:停用 
      WhOperationCommand.setLifecycle(whWorkCommand.getLifecycle());
      
      WhOperation whOperation = new WhOperation();
      //复制数据        
      BeanUtils.copyProperties(WhOperationCommand, whOperation);
      if(null != WhOperationCommand.getId() ){
          operationDao.saveOrUpdateByVersion(whOperation);
      }else{
          operationDao.insert(whOperation);
      }
      
      return operationCode;
    }

    /**
     * 创建作业明细
     * @param List<WhOdoOutBoundBox>
     * @return
     */
    @Override
    public int savePickingOperationLine(String workCode, String operationCode, Long ouId) {
        //获取工作头信息        
        WhWorkCommand whWorkCommand = this.workDao.findWorkByWorkCode(workCode, ouId);
        //获取工作明细信息列表        
        List<WhWorkLineCommand> whWorkLineCommandList = this.workLineDao.findWorkLineByWorkId(whWorkCommand.getId(), ouId);
        //获取作业头信息  
        WhOperationCommand WhOperationCommand = this.operationDao.findOperationByCode(operationCode, ouId);
                
        int count = 0;
        for(WhWorkLineCommand whWorkLineCommand : whWorkLineCommandList){
            WhOperationLineCommand WhOperationLineCommand = new WhOperationLineCommand();
            //作业ID
            WhOperationLineCommand.setOperationId(WhOperationCommand.getId());
            //工作明细ID
            WhOperationLineCommand.setWorkLineId(whWorkLineCommand.getId());
            //仓库组织ID
            WhOperationLineCommand.setOuId(whWorkLineCommand.getOuId());
            //操作开始时间
            WhOperationLineCommand.setStartTime(whWorkLineCommand.getStartTime());
            //操作结束时间
            WhOperationLineCommand.setFinishTime(whWorkLineCommand.getFinishTime());
            //商品ID
            WhOperationLineCommand.setSkuId(whWorkLineCommand.getSkuId());
            //计划量
            WhOperationLineCommand.setQty(whWorkLineCommand.getQty());
            //库存状态
            WhOperationLineCommand.setInvStatus(whWorkLineCommand.getInvStatus());
            //库存类型
            WhOperationLineCommand.setInvType(whWorkLineCommand.getInvType());
            //批次号
            WhOperationLineCommand.setBatchNumber(whWorkLineCommand.getBatchNumber());
            //生产日期
            WhOperationLineCommand.setMfgDate(whWorkLineCommand.getMfgDate());
            //失效日期 
            WhOperationLineCommand.setExpDate(whWorkLineCommand.getExpDate());
            //最小失效日期
            WhOperationLineCommand.setMinExpDate(whWorkLineCommand.getMinExpDate());
            //最大失效日期
            WhOperationLineCommand.setMaxExpDate(whWorkLineCommand.getMaxExpDate());
            //原产地
            WhOperationLineCommand.setCountryOfOrigin(whWorkLineCommand.getCountryOfOrigin());
            //库存属性1
            WhOperationLineCommand.setInvAttr1(whWorkLineCommand.getInvAttr1());
            //库存属性2
            WhOperationLineCommand.setInvAttr2(whWorkLineCommand.getInvAttr2());
            //库存属性3
            WhOperationLineCommand.setInvAttr3(whWorkLineCommand.getInvAttr3());
            //库存属性4
            WhOperationLineCommand.setInvAttr4(whWorkLineCommand.getInvAttr4());
            //库存属性5
            WhOperationLineCommand.setInvAttr5(whWorkLineCommand.getInvAttr5());
            //内部对接码
            WhOperationLineCommand.setUuid(whWorkLineCommand.getUuid());
            //原始库位
            WhOperationLineCommand.setFromLocationId(whWorkLineCommand.getFromLocationId());
            //原始库位外部容器
            WhOperationLineCommand.setFromOuterContainerId(whWorkLineCommand.getFromOuterContainerId());
            //原始库位内部容器
            WhOperationLineCommand.setFromInsideContainerId(whWorkLineCommand.getFromInsideContainerId());
            //使用出库箱，耗材ID
            WhOperationLineCommand.setUseOutboundboxId(whWorkLineCommand.getUseOutboundboxId());
            //使用出库箱编码
            WhOperationLineCommand.setUseOutboundboxCode(whWorkLineCommand.getUseOutboundboxCode());
            //使用容器 
            WhOperationLineCommand.setUseContainerId(whWorkLineCommand.getUseContainerId());
            //使用外部容器，小车
            WhOperationLineCommand.setUseOuterContainerId(whWorkLineCommand.getUseOuterContainerId());
            //使用货格编码数
            WhOperationLineCommand.setUseContainerLatticeNo(whWorkLineCommand.getUseContainerLatticeNo());
            //目标库位
            WhOperationLineCommand.setToLocationId(whWorkLineCommand.getToLocationId());
            //目标库位外部容器
            WhOperationLineCommand.setToOuterContainerId(whWorkLineCommand.getToOuterContainerId());
            //目标库位内部容器
            WhOperationLineCommand.setToInsideContainerId(whWorkLineCommand.getToInsideContainerId());
            //出库单ID
            WhOperationLineCommand.setOdoId(whWorkLineCommand.getOdoId());
            //出库单明细ID
            WhOperationLineCommand.setOdoLineId(whWorkLineCommand.getOdoLineId());
            //创建时间
            WhOperationLineCommand.setCreateTime(new Date());
            //最后操作时间
            WhOperationLineCommand.setLastModifyTime(whWorkLineCommand.getLastModifyTime());
            //操作人ID
            WhOperationLineCommand.setOperatorId(whWorkLineCommand.getOperatorId());
            
            WhOperationLine whOperationLine = new WhOperationLine();
            //复制数据        
            BeanUtils.copyProperties(WhOperationLineCommand, whOperationLine);
            if(null != WhOperationLineCommand.getId() ){
                operationLineDao.saveOrUpdateByVersion(whOperationLine);
            }else{
                operationLineDao.insert(whOperationLine);
            }
            
            count = count + 1;
        }
        return count;
    }

    
    /**
     * 设置出库箱行标识
     * @param whOdoOutBoundBoxCommand
     * @return
     */
    @Override
    public void updateWhOdoOutBoundBoxCommand(WhOdoOutBoundBoxCommand whOdoOutBoundBoxCommand) {
        WhOdoOutBoundBoxCommand odoOutBoundBoxCommand = this.odoOutBoundBoxDao.findWhOdoOutBoundBoxCommandById(whOdoOutBoundBoxCommand.getId(),whOdoOutBoundBoxCommand.getOuId());
        odoOutBoundBoxCommand.setIsCreateWork(true);
        WhOdoOutBoundBox whOdoOutBoundBox = new WhOdoOutBoundBox();
        //复制数据        
        BeanUtils.copyProperties(odoOutBoundBoxCommand, whOdoOutBoundBox);
        if(null != odoOutBoundBoxCommand.getId() ){
            odoOutBoundBoxDao.saveOrUpdateByVersion(whOdoOutBoundBox);
        }else{
            odoOutBoundBoxDao.insert(whOdoOutBoundBox);
        }
    }
    
    /**
     * 根据补货工作拆分条件统计分析补货数据
     * @param ReplenishmentRuleCommand
     * @return
     */
    @Override
    public Map<String, List<WhSkuInventoryAllocatedCommand>> getSkuInventoryAllocatedCommandForGroup(ReplenishmentRuleCommand replenishmentRuleCommand) {
        // 初始化分组MAP
        Map<String, List<WhSkuInventoryAllocatedCommand>> rMap = new HashMap<String, List<WhSkuInventoryAllocatedCommand>>();
        // 根据补货工作释放及拆分条件获取所有补货数据      
        List<WhSkuInventoryAllocatedCommand> skuInventoryAllocatedCommandLst =  getAllReplenishmentLst(replenishmentRuleCommand);
        // 根据补货工作拆分条件统计分析补货数据
        for(WhSkuInventoryAllocatedCommand whSkuInventoryAllocatedCommand : skuInventoryAllocatedCommandLst){
            // 初始化WhSkuInventoryAllocatedCommand列表
            List<WhSkuInventoryAllocatedCommand> rList = new ArrayList<WhSkuInventoryAllocatedCommand>();
            if(null != replenishmentRuleCommand.getIsFromInsideContainerSplitWork() && null != replenishmentRuleCommand.getIsToLocationSplitWork()){
                // 分组标示 -- 配置为原始库位货箱与目标库位 
                String str = "fromInsideContainerToLocation" + whSkuInventoryAllocatedCommand.getInsideContainerId() + "-" + whSkuInventoryAllocatedCommand.getToLocationId();
                if(null != rMap && null != rMap.get(str)){
                    rList = rMap.get(str);
                }
                rList.add(whSkuInventoryAllocatedCommand);
                rMap.put(str, rList);
            }else if(null != replenishmentRuleCommand.getIsFromInsideContainerSplitWork() && null == replenishmentRuleCommand.getIsToLocationSplitWork()){
                // 分组标示 -- 配置为原始库位货箱
                String str = "fromInsideContainer" + whSkuInventoryAllocatedCommand.getInsideContainerId().toString();
                if(null != rMap && null != rMap.get(str)){
                    rList = rMap.get(str);
                }
                rList.add(whSkuInventoryAllocatedCommand);
                rMap.put(str, rList);
            }else if(null != replenishmentRuleCommand.getIsFromOuterContainerSplitWork()  && null != replenishmentRuleCommand.getIsToLocationSplitWork()){
                // 分组标示 -- 配置为原始库位托盘与目标库位
                String str = "fromOuterContainerToLocation" + whSkuInventoryAllocatedCommand.getOuterContainerId() + "-" + whSkuInventoryAllocatedCommand.getToLocationId();
                if(null != rMap && null != rMap.get(str)){
                    rList = rMap.get(str);
                }
                rList.add(whSkuInventoryAllocatedCommand);
                rMap.put(str, rList);
            }else if(null != replenishmentRuleCommand.getIsFromOuterContainerSplitWork()  && null == replenishmentRuleCommand.getIsToLocationSplitWork()){
                // 分组标示 -- 配置为原始库位托盘
                String str = "fromOuterContainer" + whSkuInventoryAllocatedCommand.getOuterContainerId().toString();
                if(null != rMap && null != rMap.get(str)){
                    rList = rMap.get(str);
                }
                rList.add(whSkuInventoryAllocatedCommand);
                rMap.put(str, rList);
            }else if(null != replenishmentRuleCommand.getIsFromLocationSplitWork() && null != replenishmentRuleCommand.getIsToLocationSplitWork()){ 
                // 分组标示 -- 配置为原始库位与目标库位
                String str = "fromLocationToLocation" + whSkuInventoryAllocatedCommand.getLocationId() + "-" + whSkuInventoryAllocatedCommand.getToLocationId();
                if(null != rMap && null != rMap.get(str)){
                    rList = rMap.get(str);
                }
                rList.add(whSkuInventoryAllocatedCommand);
                rMap.put(str, rList);
            }else if(null != replenishmentRuleCommand.getIsFromLocationSplitWork() && null == replenishmentRuleCommand.getIsToLocationSplitWork()){ 
                // 分组标示 -- 配置为原始库位
                String str = "fromLocation" + whSkuInventoryAllocatedCommand.getLocationId().toString();
                if(null != rMap && null != rMap.get(str)){
                    rList = rMap.get(str);
                }
                rList.add(whSkuInventoryAllocatedCommand);
                rMap.put(str, rList);
            }else{ 
                // 分组标示 -- 配置为目标库位
                String str = "toLocation" + whSkuInventoryAllocatedCommand.getToLocationId().toString();
                if(null != rMap && null != rMap.get(str)){
                    rList = rMap.get(str);
                }
                rList.add(whSkuInventoryAllocatedCommand);
                rMap.put(str, rList);
            }
        }
        return rMap;
    }
    
    /**
     *  创建补货工作头信息
     * 
     * @param whOdoOutBoundBox
     * @param userId
     * @return
     */
    @Override
    public String saveReplenishmentWork(Long waveId, WhSkuInventoryAllocatedCommand skuInventoryAllocatedCommand, Long userId) {
        //查询波次头信息     
        if (null == waveId || null == skuInventoryAllocatedCommand.getOuId()) {
            throw new BusinessException("没有参数");
        }
        WhWave oldWhWave = new WhWave();
        oldWhWave.setId(waveId);
        oldWhWave.setOuId(skuInventoryAllocatedCommand.getOuId());
        oldWhWave.setLifecycle(BaseModel.LIFECYCLE_NORMAL);
        List<WhWave> whWaveList = this.waveDao.findListByParam(oldWhWave);
        if (null == whWaveList || 1 != whWaveList.size()) {
            throw new BusinessException("多个波次");
        }
        WhWave whWave = whWaveList.get(0);
        //查询波次主档信息     
        WhWaveMaster whWaveMaster = waveMasterDao.findByIdExt(whWave.getWaveMasterId(), whWave.getOuId());
        //获取工作类型      
        WorkType workType = this.workTypeDao.findWorkTypeByworkCategory("REPLENISHMENT", skuInventoryAllocatedCommand.getOuId());
        
        //根据容器ID获取容器CODE
        Container outerContainer = new Container();
        Container insideContainer = new Container();
        if(skuInventoryAllocatedCommand.getOuterContainerId() != null){
            outerContainer = containerDao.findByIdExt(skuInventoryAllocatedCommand.getOuterContainerId(),skuInventoryAllocatedCommand.getOuId());
        }
        if(skuInventoryAllocatedCommand.getInsideContainerId() != null){
            insideContainer = containerDao.findByIdExt(skuInventoryAllocatedCommand.getInsideContainerId(),skuInventoryAllocatedCommand.getOuId());
        }
        
        //调编码生成器工作头实体标识        
        String workCode = codeManager.generateCode(Constants.WMS, Constants.WHWORK_MODEL_URL, "", "WORK", null);
        
        //所有值为null的字段，将会在更新工作头信息时获取，如果到时候还没有值，那就是被骗了        
        WhWorkCommand whWorkCommand = new WhWorkCommand();
        //工作号        
        whWorkCommand.setCode(workCode);
        //工作状态，系统常量        
        whWorkCommand.setStatus(WorkStatus.NEW);
        //仓库组织ID        
        whWorkCommand.setOuId(skuInventoryAllocatedCommand.getOuId());
        //工作类型编码
        whWorkCommand.setWorkType(null == workType ? null : workType.getCode());
        //工作类别编码 
        whWorkCommand.setWorkCategory("REPLENISHMENT");
        //是否锁定 默认值：1
        whWorkCommand.setIsLocked(true);
        //是否已迁出        
        whWorkCommand.setIsAssignOut(false);
        //是否短拣--执行时判断
        whWorkCommand.setIsShortPicking(null);
        //是否波次内补货
        whWorkCommand.setIsWaveReplenish(true);
        //是否拣货库存待移入
        whWorkCommand.setIsPickingTobefilled(null);
        //是否多次作业--需计算后再更新
        whWorkCommand.setIsMultiOperation(null);
        //当前工作明细设计到的所有库区编码信息列表--更新时获取数据      
        whWorkCommand.setWorkArea(null);
        //工作优先级     
        whWorkCommand.setWorkPriority(whWaveMaster.getPickingWorkPriority());
        //小批次
        whWorkCommand.setBatch(null);
        //签出批次
        whWorkCommand.setAssignOutBatch(null);
        //操作开始时间
        whWorkCommand.setStartTime(new Date());
        //操作结束时间
        whWorkCommand.setFinishTime(new Date());
        //波次ID
        whWorkCommand.setWaveId(waveId);
        //波次号
        whWorkCommand.setWaveCode(whWave.getCode());
        //订单号--更新时获取数据
        whWorkCommand.setOrderCode(null);
        //库位--更新时获取数据
        whWorkCommand.setLocationCode(null);
        //托盘--更新时获取数据
        whWorkCommand.setOuterContainerCode(null == outerContainer ? null : outerContainer.getCode());
        //容器
        whWorkCommand.setContainerCode(null == insideContainer ? null : insideContainer.getCode());
        //创建时间
        whWorkCommand.setCreateTime(new Date());
        //最后操作时间
        whWorkCommand.setLastModifyTime(new Date());
        //创建人ID
        whWorkCommand.setCreatedId(userId);
        //修改人ID
        whWorkCommand.setModifiedId(userId);
        //操作人ID
        whWorkCommand.setOperatorId(userId);
        //是否启用 1:启用 0:停用
        whWorkCommand.setLifecycle(0);
        
        WhWork whWork = new WhWork();
        //复制数据        
        BeanUtils.copyProperties(whWorkCommand, whWork);
        if(null != whWorkCommand.getId() ){
            workDao.saveOrUpdateByVersion(whWork);
        }else{
            workDao.insert(whWork);
        }
        
        return workCode;
    }

    /**
     *  创建补货工作明细
     * 
     * @param whOdoOutBoundBox
     * @param userId
     * @return
     */
    @Override
    public void saveReplenishmentWorkLine(String key, String replenishmentWorkCode, Long userId, WhSkuInventoryAllocatedCommand skuInventoryAllocatedCommand) {
        
        Boolean isWholeCase = false; 
        // 获取分组标识
        WhSkuInventory skuInventory = new WhSkuInventory();
        WhSkuInventoryAllocatedCommand allocatedCommand = new WhSkuInventoryAllocatedCommand();
        WhSkuInventoryAllocatedCommand totalCommand = new WhSkuInventoryAllocatedCommand();
        String[] containerArray = key.split("-");
        // 判断是否整托整箱        
        if(null != skuInventoryAllocatedCommand.getInsideContainerId()){
            skuInventory.setInsideContainerId(Long.valueOf(containerArray[0]));
            allocatedCommand.setInsideContainerId(Long.valueOf(containerArray[0]));
            totalCommand.setInsideContainerId(Long.valueOf(containerArray[0]));
        }else{
            if(null != skuInventoryAllocatedCommand.getOuterContainerId()){
                skuInventory.setOuterContainerId(Long.valueOf(containerArray[0]));
                allocatedCommand.setOuterContainerId(Long.valueOf(containerArray[0]));
                totalCommand.setOuterContainerId(Long.valueOf(containerArray[0]));
            }   
        }
        List<WhSkuInventory> SkuInventoryList = skuInventoryDao.getSkuInvListGroupUuid(skuInventory);
        if( null == SkuInventoryList || 0 == SkuInventoryList.size()){
            allocatedCommand.setReplenishmentCode(skuInventoryAllocatedCommand.getReplenishmentCode());
            allocatedCommand.setReplenishmentRuleId(skuInventoryAllocatedCommand.getReplenishmentRuleId());
            Double allocatedQty = skuInventoryAllocatedDao.skuInventoryAllocatedQty(allocatedCommand);
            Double totalQty = skuInventoryAllocatedDao.skuInventoryAllocatedQty(totalCommand);
            if(totalQty == allocatedQty){
                isWholeCase = true;
            }
        }
        
        // 根据出库单code获取出库单信息       
        WhOdo odo = odoDao.findOdoByCodeAndOuId(skuInventoryAllocatedCommand.getOccupationCode(), skuInventoryAllocatedCommand.getOuId());
        
        // 获取工作头信息        
        WhWorkCommand replenishmentWorkCommand = this.workDao.findWorkByWorkCode(replenishmentWorkCode, skuInventoryAllocatedCommand.getOuId());
        // 判断判断当前分组库存是否整托盘整箱分配
        
        // 调编码生成器工作明细实体标识
        String replenishmentWorkLineCode = codeManager.generateCode(Constants.WMS, Constants.WHWORKLINE_MODEL_URL, "", "WORKLINE", null);
        // 补货工作明细        
        WhWorkLineCommand whWorkLineCommand = new WhWorkLineCommand();
        //工作明细号  
        whWorkLineCommand.setLineCode(replenishmentWorkLineCode);
        //工作ID            
        whWorkLineCommand.setWorkId(replenishmentWorkCommand.getId());
        //仓库组织ID 
        whWorkLineCommand.setOuId(replenishmentWorkCommand.getOuId());
        //操作开始时间 
        whWorkLineCommand.setStartTime(null);
        //操作结束时间 
        whWorkLineCommand.setFinishTime(null);
        //商品ID 
        whWorkLineCommand.setSkuId(skuInventoryAllocatedCommand.getSkuId());
        //计划量 
        whWorkLineCommand.setQty(skuInventoryAllocatedCommand.getQty());
        //执行量/完成量 
        whWorkLineCommand.setCompleteQty(null);
        //取消量 
        whWorkLineCommand.setCancelQty(null);
        //库存状态 
        whWorkLineCommand.setInvStatus(skuInventoryAllocatedCommand.getInvStatus());
        //库存类型  
        whWorkLineCommand.setInvType(skuInventoryAllocatedCommand.getInvType());
        //批次号 
        whWorkLineCommand.setBatchNumber(skuInventoryAllocatedCommand.getBatchNumber());
        //生产日期 
        whWorkLineCommand.setMfgDate(skuInventoryAllocatedCommand.getMfgDate());
        //失效日期 
        whWorkLineCommand.setExpDate(skuInventoryAllocatedCommand.getExpDate());
        //最小失效日期 
        whWorkLineCommand.setMinExpDate(null);
        //最大失效日期 
        whWorkLineCommand.setMaxExpDate(null);
        //原产地 
        whWorkLineCommand.setCountryOfOrigin(skuInventoryAllocatedCommand.getCountryOfOrigin());
        //库存属性1 
        whWorkLineCommand.setInvAttr1(skuInventoryAllocatedCommand.getInvAttr1());
        //库存属性2 
        whWorkLineCommand.setInvAttr2(skuInventoryAllocatedCommand.getInvAttr2());
        //库存属性3 
        whWorkLineCommand.setInvAttr3(skuInventoryAllocatedCommand.getInvAttr3());
        //库存属性4 
        whWorkLineCommand.setInvAttr4(skuInventoryAllocatedCommand.getInvAttr4());
        //库存属性5 
        whWorkLineCommand.setInvAttr5(skuInventoryAllocatedCommand.getInvAttr5());
        //内部对接码 
        whWorkLineCommand.setUuid(skuInventoryAllocatedCommand.getUuid());
        //原始库位 
        whWorkLineCommand.setFromLocationId(skuInventoryAllocatedCommand.getLocationId());
        //原始库位外部容器 
        whWorkLineCommand.setFromOuterContainerId(skuInventoryAllocatedCommand.getOuterContainerId());
        //原始库位内部容器 
        whWorkLineCommand.setFromInsideContainerId(skuInventoryAllocatedCommand.getInsideContainerId());
        //使用出库箱，耗材ID
        whWorkLineCommand.setUseOutboundboxId(null);
        //使用出库箱编码 
        whWorkLineCommand.setUseOutboundboxCode(null);
        //使用容器 
        whWorkLineCommand.setUseContainerId(null);
        //使用外部容器，小车 
        whWorkLineCommand.setUseOuterContainerId(null);
        //使用货格编码数
        whWorkLineCommand.setUseContainerLatticeNo(null);
        //目标库位 --捡货模式没有
        whWorkLineCommand.setToLocationId(skuInventoryAllocatedCommand.getToLocationId());
        //目标库位外部容器 --捡货模式没有
        whWorkLineCommand.setToOuterContainerId(null);
        //目标库位内部容器 --捡货模式没有
        whWorkLineCommand.setToInsideContainerId(null);
        //是否整托整箱
        whWorkLineCommand.setIsWholeCase(isWholeCase);  
        //出库单ID 
        whWorkLineCommand.setOdoId(odo.getId());
        //出库单明细ID 
        whWorkLineCommand.setOdoLineId(skuInventoryAllocatedCommand.getOccupationLineId());
        //创建时间 
        whWorkLineCommand.setCreateTime(new Date());
        //最后操作时间 
        whWorkLineCommand.setLastModifyTime(new Date());
        //操作人ID 
        whWorkLineCommand.setOperatorId(userId);
        
        WhWorkLine whWorkLine = new WhWorkLine();
        //复制数据        
        BeanUtils.copyProperties(whWorkLineCommand, whWorkLine);
        if(null != whWorkLineCommand.getId() ){
            workLineDao.saveOrUpdateByVersion(whWorkLine);
        }else{
            workLineDao.insert(whWorkLine);
        }
        
        // 将补货任务行标识为已创建工作
        ReplenishmentTask replenishmentTask = replenishmentTaskDao.findReplenishmentTaskByCode(skuInventoryAllocatedCommand.getReplenishmentCode(), skuInventoryAllocatedCommand.getOuId());
        replenishmentTask.setIsCreateWork(true);
        replenishmentTaskDao.saveOrUpdateByVersion(replenishmentTask);
    }

    /**
     *  更新补货工作头信息
     * 
     * @param waveId
     * @param replenishmentWorkCode
     * @param skuInventoryAllocatedCommand
     * @return
     */
    @Override
    public void updateReplenishmentWork(Long waveId, String replenishmentWorkCode, WhSkuInventoryAllocatedCommand skuInventoryAllocatedCommand) {
        //获取工作头信息        
        WhWorkCommand whWorkCommand = this.workDao.findWorkByWorkCode(replenishmentWorkCode, skuInventoryAllocatedCommand.getOuId());
        //获取工作明细信息列表        
        List<WhWorkLineCommand> whWorkLineCommandList = this.workLineDao.findWorkLineByWorkId(whWorkCommand.getId(), skuInventoryAllocatedCommand.getOuId());
        
        //查询波次头信息     
        if (null == waveId || null == skuInventoryAllocatedCommand.getOuId()) {
            throw new BusinessException("查询波次头信息  : 没有参数");
        }
        WhWave oldWhWave = new WhWave();
        oldWhWave.setId(waveId);
        oldWhWave.setOuId(skuInventoryAllocatedCommand.getOuId());
        oldWhWave.setLifecycle(BaseModel.LIFECYCLE_NORMAL);
        List<WhWave> whWaveList = this.waveDao.findListByParam(oldWhWave);
        if (null == whWaveList || 1 != whWaveList.size()) {
            throw new BusinessException("多个波次");
        }
        WhWave whWave = whWaveList.get(0);
        //查询波次主档信息     
        WhWaveMaster whWaveMaster = waveMasterDao.findByIdExt(whWave.getWaveMasterId(), whWave.getOuId());
        String workArea = "" ;
        int count = 0;
        Boolean isFromLocationId = true;
        Boolean isUseOuterContainerId = true;
        Boolean isUseContainerId = true;
        Boolean isOdoId = true;
        
        for(WhWorkLineCommand whWorkLineCommand : whWorkLineCommandList){
            if(count !=  0){
                //获取上一次循环的实体类            
                WhWorkLineCommand whWorkLineCommandBefor = whWorkLineCommandList.get(count-1);
                
                if(whWorkLineCommandBefor.getFromLocationId() != whWorkLineCommand.getFromLocationId()){
                    isFromLocationId = false;
                }
                if(whWorkLineCommandBefor.getUseOuterContainerId() != whWorkLineCommand.getUseOuterContainerId()){
                    isUseOuterContainerId = false;
                }
                if(whWorkLineCommandBefor.getUseContainerId() != whWorkLineCommand.getUseContainerId()){
                    isUseContainerId = false;
                }
                if(whWorkLineCommandBefor.getOdoId() != whWorkLineCommand.getOdoId()){
                    isOdoId = false;
                }
            }
            
            LocationCommand locationCommand = locationDao.findLocationCommandByParam(whWorkLineCommand.getFromLocationId(), whWorkLineCommand.getOuId());
            if(null != locationCommand){
                Area area = areaDao.findByIdExt(locationCommand.getWorkAreaId(),locationCommand.getOuId());
                if(workArea == ""){
                    workArea = area.getAreaCode();
                }else{
                    workArea = workArea +","+area.getAreaCode();
                }
            }
            //索引自增            
            count++;
        }
        //判断工作明细是否只有唯一库位
        if(isFromLocationId == true){
            //获取库位表数据                              
            LocationCommand locationCommand = locationDao.findLocationCommandByParam(whWorkLineCommandList.get(0).getFromLocationId(), whWorkLineCommandList.get(0).getOuId());
            if(null != locationCommand){
              //设置库位      
              whWorkCommand.setLocationCode(locationCommand.getCode());
            }
        }
        //判断工作明细是否只有唯一外部容器
        if(isUseOuterContainerId == true){
            //根据容器ID获取容器CODE      
            Container containerOut = new Container();
            containerOut = containerDao.findByIdExt(whWorkLineCommandList.get(0).getUseOuterContainerId(),skuInventoryAllocatedCommand.getOuId());
            if(null != containerOut){
                //设置外部容器
                whWorkCommand.setOuterContainerCode(containerOut.getCode());
            }
        }
        //判断据工作明细是否只有唯一内部容器
        if(isUseContainerId == true){
            //根据容器ID获取容器CODE      
            Container containerIn = new Container();
            containerIn = containerDao.findByIdExt(whWorkLineCommandList.get(0).getUseContainerId(),skuInventoryAllocatedCommand.getOuId());
            if(null != containerIn){
                //设置内部容器
                whWorkCommand.setContainerCode(containerIn.getCode());
            }
        }
        //判断据工作明细是否只有唯一出库单
        if(isOdoId == true){
            OdoCommand odoCommand = this.odoDao.findCommandByIdOuId(whWorkLineCommandList.get(0).getOdoId(), skuInventoryAllocatedCommand.getOuId());
            if(null != odoCommand){
                //设置订单号
                whWorkCommand.setOrderCode(odoCommand.getEcOrderCode());
            }
        }
        
        //当前工作明细设计到的所有库区编码信息列表
        whWorkCommand.setWorkArea(workArea);
        //是否锁定 默认值：1
        whWorkCommand.setIsLocked(whWaveMaster.getIsAutoReleaseWork());
        //工作优先级     
        whWorkCommand.setWorkPriority(whWaveMaster.getPickingWorkPriority());
        
        WhWork whWork = new WhWork();
        //复制数据        
        BeanUtils.copyProperties(whWorkCommand, whWork);
        if(null != whWorkCommand.getId() ){
            workDao.saveOrUpdateByVersion(whWork);
        }else{
            workDao.insert(whWork);
        }
    }

    /**
     *  创建补货作业信息
     * 
     * @param replenishmentWorkCode
     * @param skuInventoryAllocatedCommand
     * @return
     */
    @Override
    public String saveReplenishmentOperation(String key, String replenishmentWorkCode, WhSkuInventoryAllocatedCommand skuInventoryAllocatedCommand) {

        //获取工作头信息        
        WhWorkCommand whWorkCommand = this.workDao.findWorkByWorkCode(replenishmentWorkCode, skuInventoryAllocatedCommand.getOuId());
        Boolean isWholeCase = false; 
        // 获取分组标识
        WhSkuInventory skuInventory = new WhSkuInventory();
        WhSkuInventoryAllocatedCommand allocatedCommand = new WhSkuInventoryAllocatedCommand();
        WhSkuInventoryAllocatedCommand totalCommand = new WhSkuInventoryAllocatedCommand();
        String[] containerArray = key.split("-");
        // 判断是否整托整箱        
        if(null != skuInventoryAllocatedCommand.getInsideContainerId()){
            skuInventory.setInsideContainerId(Long.valueOf(containerArray[0]));
            allocatedCommand.setInsideContainerId(Long.valueOf(containerArray[0]));
            totalCommand.setInsideContainerId(Long.valueOf(containerArray[0]));
        }else{
            if(null != skuInventoryAllocatedCommand.getOuterContainerId()){
                skuInventory.setOuterContainerId(Long.valueOf(containerArray[0]));
                allocatedCommand.setOuterContainerId(Long.valueOf(containerArray[0]));
                totalCommand.setOuterContainerId(Long.valueOf(containerArray[0]));
            }   
        }
        List<WhSkuInventory> SkuInventoryList = skuInventoryDao.getSkuInvListGroupUuid(skuInventory);
        if( null == SkuInventoryList || 0 == SkuInventoryList.size()){
            allocatedCommand.setReplenishmentCode(skuInventoryAllocatedCommand.getReplenishmentCode());
            allocatedCommand.setReplenishmentRuleId(skuInventoryAllocatedCommand.getReplenishmentRuleId());
            Double allocatedQty = skuInventoryAllocatedDao.skuInventoryAllocatedQty(allocatedCommand);
            Double totalQty = skuInventoryAllocatedDao.skuInventoryAllocatedQty(totalCommand);
            if(totalQty == allocatedQty){
                isWholeCase = true;
            }
        }
          
        //调编码生成器工作明细实体标识
        String operationCode = codeManager.generateCode(Constants.WMS, Constants.WHOPERATION_MODEL_URL, "", "OPERATION", null);
        WhOperationCommand WhOperationCommand = new WhOperationCommand();
        //作业号
        WhOperationCommand.setCode(operationCode);
        //工作ID
        WhOperationCommand.setWorkId(whWorkCommand.getId());
        //仓库组织ID
        WhOperationCommand.setOuId(whWorkCommand.getOuId());
        //工作类型编码
        WhOperationCommand.setWorkType(whWorkCommand.getWorkType());
        //工作类别编码 
        WhOperationCommand.setWorkCategory(whWorkCommand.getWorkCategory());
        //优先级 
        WhOperationCommand.setWorkPriority(whWorkCommand.getWorkPriority());
        //小批次
        WhOperationCommand.setBatch(whWorkCommand.getBatch());
        //操作开始时间
        WhOperationCommand.setStartTime(whWorkCommand.getStartTime());
        //操作结束时间
        WhOperationCommand.setFinishTime(whWorkCommand.getFinishTime());
        //波次ID
        WhOperationCommand.setWaveId(whWorkCommand.getWaveId());
        //波次号
        WhOperationCommand.setWaveCode(whWorkCommand.getWaveCode());
        //订单号
        WhOperationCommand.setOrderCode(whWorkCommand.getOrderCode());
        //库位 
        WhOperationCommand.setLocationCode(whWorkCommand.getLocationCode());
        //托盘
        WhOperationCommand.setOuterContainerCode(whWorkCommand.getOuterContainerCode());
        //容器
        WhOperationCommand.setContainerCode(whWorkCommand.getContainerCode());
        //是否整托整箱
        WhOperationCommand.setIsWholeCase(isWholeCase);  
        //创建时间 
        WhOperationCommand.setCreateTime(whWorkCommand.getCreateTime());
        //最后操作时间
        WhOperationCommand.setLastModifyTime(whWorkCommand.getLastModifyTime());
        //创建人ID
        WhOperationCommand.setCreatedId(whWorkCommand.getCreatedId());
        //修改人ID
        WhOperationCommand.setModifiedId(whWorkCommand.getModifiedId());
        //操作人ID
        WhOperationCommand.setOperatorId(whWorkCommand.getOperatorId());
        //是否启用 1:启用 0:停用 
        WhOperationCommand.setLifecycle(whWorkCommand.getLifecycle());
        
        WhOperation whOperation = new WhOperation();
        //复制数据        
        BeanUtils.copyProperties(WhOperationCommand, whOperation);
        if(null != WhOperationCommand.getId() ){
            operationDao.saveOrUpdateByVersion(whOperation);
        }else{
            operationDao.insert(whOperation);
        }
        return operationCode;
    }

    /**
     * 创建补货作业明细
     * 
     * @param replenishmentWorkCode
     * @param replenishmentOperationCode
     * @param ouId
     * @return
     */
    @Override
    public int saveReplenishmentOperationLine(String replenishmentWorkCode, String replenishmentOperationCode, Long ouId) {
        //获取工作头信息        
        WhWorkCommand whWorkCommand = this.workDao.findWorkByWorkCode(replenishmentWorkCode, ouId);
        //获取工作明细信息列表        
        List<WhWorkLineCommand> whWorkLineCommandList = this.workLineDao.findWorkLineByWorkId(whWorkCommand.getId(), ouId);
        //获取作业头信息  
        WhOperationCommand WhOperationCommand = this.operationDao.findOperationByCode(replenishmentOperationCode, ouId);
                
        int count = 0;
        for(WhWorkLineCommand whWorkLineCommand : whWorkLineCommandList){
            WhOperationLineCommand WhOperationLineCommand = new WhOperationLineCommand();
            //作业ID
            WhOperationLineCommand.setOperationId(WhOperationCommand.getId());
            //工作明细ID
            WhOperationLineCommand.setWorkLineId(whWorkLineCommand.getId());
            //仓库组织ID
            WhOperationLineCommand.setOuId(whWorkLineCommand.getOuId());
            //操作开始时间
            WhOperationLineCommand.setStartTime(whWorkLineCommand.getStartTime());
            //操作结束时间
            WhOperationLineCommand.setFinishTime(whWorkLineCommand.getFinishTime());
            //商品ID
            WhOperationLineCommand.setSkuId(whWorkLineCommand.getSkuId());
            //计划量
            WhOperationLineCommand.setQty(whWorkLineCommand.getQty());
            //库存状态
            WhOperationLineCommand.setInvStatus(whWorkLineCommand.getInvStatus());
            //库存类型
            WhOperationLineCommand.setInvType(whWorkLineCommand.getInvType());
            //批次号
            WhOperationLineCommand.setBatchNumber(whWorkLineCommand.getBatchNumber());
            //生产日期
            WhOperationLineCommand.setMfgDate(whWorkLineCommand.getMfgDate());
            //失效日期 
            WhOperationLineCommand.setExpDate(whWorkLineCommand.getExpDate());
            //最小失效日期
            WhOperationLineCommand.setMinExpDate(whWorkLineCommand.getMinExpDate());
            //最大失效日期
            WhOperationLineCommand.setMaxExpDate(whWorkLineCommand.getMaxExpDate());
            //原产地
            WhOperationLineCommand.setCountryOfOrigin(whWorkLineCommand.getCountryOfOrigin());
            //库存属性1
            WhOperationLineCommand.setInvAttr1(whWorkLineCommand.getInvAttr1());
            //库存属性2
            WhOperationLineCommand.setInvAttr2(whWorkLineCommand.getInvAttr2());
            //库存属性3
            WhOperationLineCommand.setInvAttr3(whWorkLineCommand.getInvAttr3());
            //库存属性4
            WhOperationLineCommand.setInvAttr4(whWorkLineCommand.getInvAttr4());
            //库存属性5
            WhOperationLineCommand.setInvAttr5(whWorkLineCommand.getInvAttr5());
            //内部对接码
            WhOperationLineCommand.setUuid(whWorkLineCommand.getUuid());
            //原始库位
            WhOperationLineCommand.setFromLocationId(whWorkLineCommand.getFromLocationId());
            //原始库位外部容器
            WhOperationLineCommand.setFromOuterContainerId(whWorkLineCommand.getFromOuterContainerId());
            //原始库位内部容器
            WhOperationLineCommand.setFromInsideContainerId(whWorkLineCommand.getFromInsideContainerId());
            //使用出库箱，耗材ID
            WhOperationLineCommand.setUseOutboundboxId(whWorkLineCommand.getUseOutboundboxId());
            //使用出库箱编码
            WhOperationLineCommand.setUseOutboundboxCode(whWorkLineCommand.getUseOutboundboxCode());
            //使用容器 
            WhOperationLineCommand.setUseContainerId(whWorkLineCommand.getUseContainerId());
            //使用外部容器，小车
            WhOperationLineCommand.setUseOuterContainerId(whWorkLineCommand.getUseOuterContainerId());
            //使用货格编码数
            WhOperationLineCommand.setUseContainerLatticeNo(whWorkLineCommand.getUseContainerLatticeNo());
            //目标库位
            WhOperationLineCommand.setToLocationId(whWorkLineCommand.getToLocationId());
            //目标库位外部容器
            WhOperationLineCommand.setToOuterContainerId(whWorkLineCommand.getToOuterContainerId());
            //目标库位内部容器
            WhOperationLineCommand.setToInsideContainerId(whWorkLineCommand.getToInsideContainerId());
            //出库单ID
            WhOperationLineCommand.setOdoId(whWorkLineCommand.getOdoId());
            //出库单明细ID
            WhOperationLineCommand.setOdoLineId(whWorkLineCommand.getOdoLineId());
            //创建时间
            WhOperationLineCommand.setCreateTime(new Date());
            //最后操作时间
            WhOperationLineCommand.setLastModifyTime(whWorkLineCommand.getLastModifyTime());
            //操作人ID
            WhOperationLineCommand.setOperatorId(whWorkLineCommand.getOperatorId());
            
            WhOperationLine whOperationLine = new WhOperationLine();
            //复制数据        
            BeanUtils.copyProperties(WhOperationLineCommand, whOperationLine);
            if(null != WhOperationLineCommand.getId() ){
                operationLineDao.saveOrUpdateByVersion(whOperationLine);
            }else{
                operationLineDao.insert(whOperationLine);
            }
            count = count + 1;
        }
        return count;
    }
}
