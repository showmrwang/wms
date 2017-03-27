package com.baozun.scm.primservice.whoperation.manager.odo.wave;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.baseservice.sac.manager.CodeManager;
import com.baozun.scm.primservice.whoperation.command.warehouse.InWarehouseMoveWorkCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.LocationCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOperationCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOperationLineCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhWorkCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhWorkLineCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryAllocatedCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.OperationStatus;
import com.baozun.scm.primservice.whoperation.constant.WorkStatus;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.AreaDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhLocationDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOperationDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOperationLineDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhWorkDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhWorkLineDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WorkTypeDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryAllocatedDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryTobefilledDao;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.BaseModel;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWave;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWaveMaster;
import com.baozun.scm.primservice.whoperation.model.warehouse.Area;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOperation;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOperationLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhWork;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhWorkLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.WorkType;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventory;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventoryTobefilled;

@Service("createInWarehouseMoveWorkManager")
@Transactional
public class CreateInWarehouseMoveWorkManagerImpl extends BaseManagerImpl implements CreateInWarehouseMoveWorkManager {

    public static final Logger log = LoggerFactory.getLogger(CreateInWarehouseMoveWorkManagerImpl.class);
    
    @Autowired
    private CodeManager codeManager;
    @Autowired
    private WhSkuInventoryDao whSkuInventoryDao;
    @Autowired
    private WorkTypeDao workTypeDao;
    @Autowired
    private WhWorkDao workDao;
    @Autowired
    private WhWorkLineDao workLineDao;
    @Autowired
    private WhOperationDao operationDao;
    @Autowired
    private WhOperationLineDao operationLineDao;
    @Autowired
    private WhOdoDao odoDao;
    @Autowired
    private ContainerDao containerDao;
    @Autowired
    private WhSkuInventoryDao skuInventoryDao;
    @Autowired
    private WhSkuInventoryAllocatedDao skuInventoryAllocatedDao;
    @Autowired
    private WhSkuInventoryTobefilledDao skuInventoryTobefilledDao;
    @Autowired
    private AreaDao areaDao;
    @Autowired
    private WhLocationDao locationDao;
    
    
    /**
     * 库存分配（生成分配库存与待移入库存）
     * 
     * @param whSkuInventoryCommandLst
     * @param userId
     * @return
     */
    @Override
    public InWarehouseMoveWorkCommand saveAllocatedAndTobefilled(List<WhSkuInventoryCommand> whSkuInventoryCommandLst, Long ouId, Long userId) {
        // TODO        
        return null;
    }

    /**
     * 创建库内移动工作头
     * 
     * @param whSkuInventoryCommand
     * @param userId
     * @return
     */
    @Override
    public void createInWarehouseMoveWork(InWarehouseMoveWorkCommand inWarehouseMoveWorkCommand, Long ouId, Long userId) {
        try {
            WhSkuInventoryAllocatedCommand whSkuInventoryAllocatedCommand = inWarehouseMoveWorkCommand.getWhSkuInventoryAllocatedCommandLst().get(0);
            // 6.创建库内移动工作头
            String inWarehouseMoveWorkCode = this.saveInWarehouseMoveWork(whSkuInventoryAllocatedCommand, ouId, userId);
            // 7.创建库内移动工作明细
            for(WhSkuInventoryAllocatedCommand skuInventoryAllocatedCommand : inWarehouseMoveWorkCommand.getWhSkuInventoryAllocatedCommandLst()){
                this.saveInWarehouseMoveWorkLine(inWarehouseMoveWorkCode, skuInventoryAllocatedCommand, userId);
            }
            // 8.更新工作头
            this.updateInWarehouseMoveWork(inWarehouseMoveWorkCode, whSkuInventoryAllocatedCommand);
            // 9.根据工作头和明细生成作业
            String inWarehouseMoveWorkOperationCode = this.saveInWarehouseMoveOperation(inWarehouseMoveWorkCode, whSkuInventoryAllocatedCommand);
            this.saveInWarehouseMoveWorkOperationLine(inWarehouseMoveWorkCode, inWarehouseMoveWorkOperationCode, whSkuInventoryAllocatedCommand);
        } catch (Exception e) {
            log.error("", e);
        }    
    }
    
    /**
     * 创建库内移动工作头
     * 
     * @param whSkuInventoryCommand
     * @param userId
     * @return
     */
    private String saveInWarehouseMoveWork(WhSkuInventoryAllocatedCommand skuInventoryAllocatedCommand, Long ouId, Long userId) {
        //获取工作类型      
        WorkType workType = this.workTypeDao.findWorkTypeByworkCategory("IN_WAREHOUSE_MOVE", skuInventoryAllocatedCommand.getOuId());
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
        //封装数据        
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
        whWorkCommand.setWorkPriority(null == workType ? null : workType.getPriority());
        //小批次
        whWorkCommand.setBatch(null);
        //签出批次
        whWorkCommand.setAssignOutBatch(null);
        //操作开始时间
        whWorkCommand.setStartTime(new Date());
        //操作结束时间
        whWorkCommand.setFinishTime(new Date());
        //波次ID
        whWorkCommand.setWaveId(null);
        //波次号
        whWorkCommand.setWaveCode(null);
        //订单号--更新时获取数据
        whWorkCommand.setOrderCode(null);
        //库位--更新时获取数据
        whWorkCommand.setLocationCode(null);
        //托盘--更新时获取数据
        whWorkCommand.setOuterContainerCode(null == outerContainer ? null : outerContainer.getCode());
        //容器--更新时获取数据
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
     * 创建库内移动工作明细
     * 
     * @param inWarehouseMoveWorkCode
     * @param userId
     * @param skuInventoryCommand
     * @return
     */
    private void saveInWarehouseMoveWorkLine(String inWarehouseMoveWorkCode, WhSkuInventoryAllocatedCommand skuInventoryAllocatedCommand,  Long userId) {
        
        // 判断是否整托整箱
        Boolean isWholeCase = false; 
        
        if(null != skuInventoryAllocatedCommand.getInsideContainerId()){
            WhSkuInventory skuInventory = new WhSkuInventory();
            WhSkuInventoryAllocatedCommand allocatedCommand = new WhSkuInventoryAllocatedCommand();
            WhSkuInventoryAllocatedCommand totalCommand = new WhSkuInventoryAllocatedCommand();
            WhSkuInventoryTobefilled skuInventoryTobefilled = new WhSkuInventoryTobefilled();
            Double onHandQty = 0.00;
            Double frozenQty = 0.00;
            skuInventory.setInsideContainerId(skuInventoryAllocatedCommand.getInsideContainerId());
            allocatedCommand.setInsideContainerId(skuInventoryAllocatedCommand.getInsideContainerId());
            totalCommand.setInsideContainerId(skuInventoryAllocatedCommand.getInsideContainerId());
            skuInventoryTobefilled.setInsideContainerId(skuInventoryAllocatedCommand.getInsideContainerId());
            skuInventoryTobefilled.setLocationId(skuInventoryAllocatedCommand.getLocationId());
            allocatedCommand.setReplenishmentCode(skuInventoryAllocatedCommand.getReplenishmentCode());
            allocatedCommand.setReplenishmentRuleId(skuInventoryAllocatedCommand.getReplenishmentRuleId());
            Double allocatedQty = skuInventoryAllocatedDao.skuInventoryAllocatedQty(allocatedCommand);
            Double totalQty = skuInventoryAllocatedDao.skuInventoryAllocatedQty(totalCommand);
            Double toBeFilledQty = skuInventoryTobefilledDao.skuInventoryTobefilledQty(skuInventoryTobefilled);
            List<WhSkuInventory> skuInventoryList = skuInventoryDao.getSkuInvListGroupUuid(skuInventory);
            if(null != skuInventoryList && 0 != skuInventoryList.size()){
                for(WhSkuInventory whSkuInventory:skuInventoryList){
                    onHandQty = onHandQty + whSkuInventory.getOnHandQty();
                    frozenQty = frozenQty + whSkuInventory.getFrozenQty();
                }
            }
            double zero = 0.0; 
            int resultFrozen = frozenQty.compareTo(zero);
            int resultTo = 0;
            if(null != toBeFilledQty){
                resultTo = toBeFilledQty.compareTo(zero);    
            }
            if(totalQty.equals(allocatedQty) && onHandQty.equals(allocatedQty) && 0 == resultFrozen && 0 == resultTo){
                isWholeCase = true;
            }
        }
        
        if(null != skuInventoryAllocatedCommand.getOuterContainerId()){
            WhSkuInventory skuInventory = new WhSkuInventory();
            WhSkuInventoryAllocatedCommand allocatedCommand = new WhSkuInventoryAllocatedCommand();
            WhSkuInventoryAllocatedCommand totalCommand = new WhSkuInventoryAllocatedCommand();
            WhSkuInventoryTobefilled skuInventoryTobefilled = new WhSkuInventoryTobefilled();
            Double onHandQty = 0.00;
            Double frozenQty = 0.00;
            skuInventory.setOuterContainerId(skuInventoryAllocatedCommand.getOuterContainerId());
            allocatedCommand.setOuterContainerId(skuInventoryAllocatedCommand.getOuterContainerId());
            totalCommand.setOuterContainerId(skuInventoryAllocatedCommand.getOuterContainerId());
            skuInventoryTobefilled.setOuterContainerId(skuInventoryAllocatedCommand.getOuterContainerId());
            skuInventoryTobefilled.setLocationId(skuInventoryAllocatedCommand.getLocationId());
            allocatedCommand.setReplenishmentCode(skuInventoryAllocatedCommand.getReplenishmentCode());
            allocatedCommand.setReplenishmentRuleId(skuInventoryAllocatedCommand.getReplenishmentRuleId());
            Double allocatedQty = skuInventoryAllocatedDao.skuInventoryAllocatedQty(allocatedCommand);
            Double totalQty = skuInventoryAllocatedDao.skuInventoryAllocatedQty(totalCommand);
            Double toBeFilledQty = skuInventoryTobefilledDao.skuInventoryTobefilledQty(skuInventoryTobefilled);
            List<WhSkuInventory> skuInventoryList = skuInventoryDao.getSkuInvListGroupUuid(skuInventory);
            if(null != skuInventoryList && 0 != skuInventoryList.size()){
                for(WhSkuInventory whSkuInventory:skuInventoryList){
                    onHandQty = onHandQty + whSkuInventory.getOnHandQty();
                    frozenQty = frozenQty + whSkuInventory.getFrozenQty();
                }
            }
            double zero = 0.0; 
            int resultFrozen = frozenQty.compareTo(zero);
            int resultTo = 0;
            if(null != toBeFilledQty){
                resultTo = toBeFilledQty.compareTo(zero);    
            }
            if(totalQty.equals(allocatedQty) && onHandQty.equals(allocatedQty) && 0 == resultFrozen && 0 == resultTo){
                isWholeCase = true;
            }
        }   
        
        // 根据出库单code获取出库单信息       
        WhOdo odo = odoDao.findOdoByCodeAndOuId(skuInventoryAllocatedCommand.getOccupationCode(), skuInventoryAllocatedCommand.getOuId());
        
        // 获取工作头信息        
        WhWorkCommand replenishmentWorkCommand = this.workDao.findWorkByWorkCode(inWarehouseMoveWorkCode, skuInventoryAllocatedCommand.getOuId());
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
        whWorkLineCommand.setOdoId(odo == null ? null : odo.getId());
        //出库单明细ID 
        whWorkLineCommand.setOdoLineId(skuInventoryAllocatedCommand.getOccupationLineId());
        //补货单据号        
        whWorkLineCommand.setReplenishmentCode(skuInventoryAllocatedCommand.getReplenishmentCode());
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
    }
    
    /**
     * 更新工作头
     * 
     * @param inWarehouseMoveWorkCode
     * @return
     */
    private void updateInWarehouseMoveWork(String replenishmentWorkCode, WhSkuInventoryAllocatedCommand skuInventoryAllocatedCommand) {
        //获取工作头信息        
        WhWorkCommand whWorkCommand = this.workDao.findWorkByWorkCode(replenishmentWorkCode, skuInventoryAllocatedCommand.getOuId());
        //获取工作明细信息列表        
        List<WhWorkLineCommand> whWorkLineCommandList = this.workLineDao.findWorkLineByWorkId(whWorkCommand.getId(), skuInventoryAllocatedCommand.getOuId());
        
        //获取工作类型      
        WorkType workType = this.workTypeDao.findWorkTypeByworkCategory("REPLENISHMENT", skuInventoryAllocatedCommand.getOuId());
        String workArea = "" ;
        int count = 0;
        Boolean isFromLocationId = true;
        Boolean isFromOuterContainerId = true;
        Boolean isFromInsideContainerId = true;
        Boolean isOdoId = true;
        
        if(null != whWorkLineCommandList &&  whWorkLineCommandList.size() > 0){
            for(WhWorkLineCommand whWorkLineCommand : whWorkLineCommandList){
                if(count !=  0){
                    //获取上一次循环的实体类            
                    WhWorkLineCommand whWorkLineCommandBefor = whWorkLineCommandList.get(count-1);
                    
                    if (0 == whWorkLineCommandBefor.getFromLocationId().compareTo(whWorkLineCommand.getFromLocationId())) {
                        isFromLocationId = false;
                    }
                    if (0 == whWorkLineCommandBefor.getFromOuterContainerId().compareTo(whWorkLineCommand.getFromOuterContainerId())) {
                        isFromOuterContainerId = false;
                    }
                    if (0 == whWorkLineCommandBefor.getFromInsideContainerId().compareTo(whWorkLineCommand.getFromInsideContainerId())) {
                        isFromInsideContainerId = false;
                    }
                    if (0 == whWorkLineCommandBefor.getOdoId().compareTo(whWorkLineCommand.getOdoId())) {
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
        }else{
            LocationCommand locationCommand = locationDao.findLocationCommandByParam(whWorkLineCommandList.get(0).getFromLocationId(), whWorkLineCommandList.get(0).getOuId());
            if(null != locationCommand){
                Area area = areaDao.findByIdExt(locationCommand.getWorkAreaId(),locationCommand.getOuId());
                if(workArea == ""){
                    workArea = area.getAreaCode();
                }else{
                    workArea = workArea +","+area.getAreaCode();
                }
            }
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
        if(isFromOuterContainerId == true){
            //根据容器ID获取容器CODE      
            Container containerOut = new Container();
            containerOut = containerDao.findByIdExt(whWorkLineCommandList.get(0).getFromOuterContainerId(),skuInventoryAllocatedCommand.getOuId());
            if(null != containerOut){
                //设置外部容器
                whWorkCommand.setOuterContainerCode(containerOut.getCode());
            }
        }
        //判断据工作明细是否只有唯一内部容器
        if(isFromInsideContainerId == true){
            //根据容器ID获取容器CODE      
            Container containerIn = new Container();
            containerIn = containerDao.findByIdExt(whWorkLineCommandList.get(0).getFromInsideContainerId(),skuInventoryAllocatedCommand.getOuId());
            if(null != containerIn){
                //设置内部容器
                whWorkCommand.setContainerCode(containerIn.getCode());
            }
        }
        //判断据工作明细是否只有唯一出库单
        if(isOdoId == true){
            WhOdo whOdo = this.odoDao.findByIdOuId(whWorkLineCommandList.get(0).getOdoId(), skuInventoryAllocatedCommand.getOuId());
            if(null != whOdo){
                //设置订单号
                whWorkCommand.setOrderCode(whOdo.getEcOrderCode());
            }
        }
        
        //当前工作明细设计到的所有库区编码信息列表
        whWorkCommand.setWorkArea(workArea);
        
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
     * 创建库内移动工作明细
     * 
     * @param inWarehouseMoveWorkCode
     * @param userId
     * @param skuInventoryCommand
     * @return
     */
    private String saveInWarehouseMoveOperation(String inWarehouseMoveWorkCode, WhSkuInventoryAllocatedCommand skuInventoryAllocatedCommand) {
        Boolean isWholeCase = true; 
        // 获取工作头信息        
        WhWorkCommand whWorkCommand = this.workDao.findWorkByWorkCode(inWarehouseMoveWorkCode, skuInventoryAllocatedCommand.getOuId());
        // 获取工作明细信息列表        
        List<WhWorkLineCommand> whWorkLineCommandList = this.workLineDao.findWorkLineByWorkId(whWorkCommand.getId(), skuInventoryAllocatedCommand.getOuId());
        // 判断是否整托整箱
        for(WhWorkLineCommand whWorkLineCommand : whWorkLineCommandList){
            if(false == whWorkLineCommand.getIsWholeCase()){
                isWholeCase = false;
            }
        }
        //调编码生成器工作明细实体标识
        String operationCode = codeManager.generateCode(Constants.WMS, Constants.WHOPERATION_MODEL_URL, "", "OPERATION", null);
        
        WhOperationCommand WhOperationCommand = new WhOperationCommand();
        //作业号
        WhOperationCommand.setCode(operationCode);
        //状态        
        WhOperationCommand.setStatus(OperationStatus.NEW);
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
        //是否短拣
        WhOperationCommand.setIsShortPicking(whWorkCommand.getIsShortPicking());
        //是否拣货完成
        WhOperationCommand.setIsPickingFinish(false);
        //是否波次内补货
        WhOperationCommand.setIsWaveReplenish(whWorkCommand.getIsWaveReplenish());
        //是否拣货库存待移入
        WhOperationCommand.setIsPickingTobefilled(whWorkCommand.getIsPickingTobefilled());
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
     * 创建库内移动工作明细
     * 
     * @param inWarehouseMoveWorkCode
     * @param userId
     * @param skuInventoryCommand
     * @return
     */
    private int saveInWarehouseMoveWorkOperationLine(String inWarehouseMoveWorkCode, String inWarehouseMoveWorkOperationCode, WhSkuInventoryAllocatedCommand whSkuInventoryAllocatedCommand) {
        Long ouId = whSkuInventoryAllocatedCommand.getOuId();
        //获取工作头信息        
        WhWorkCommand whWorkCommand = this.workDao.findWorkByWorkCode(inWarehouseMoveWorkCode, ouId);
        //获取工作明细信息列表        
        List<WhWorkLineCommand> whWorkLineCommandList = this.workLineDao.findWorkLineByWorkId(whWorkCommand.getId(), ouId);
        //获取作业头信息  
        WhOperationCommand WhOperationCommand = this.operationDao.findOperationByCode(inWarehouseMoveWorkOperationCode, ouId);
                
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
            //补货单据号
            WhOperationLineCommand.setReplenishmentCode(whWorkLineCommand.getReplenishmentCode());
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
     * [业务方法] 库内移动工作执行
     * @param 
     * @param 
     * @return
     */
    @Override
    public void executeInWarehouseMoveWork(InWarehouseMoveWorkCommand inWarehouseMoveWorkCommand, Long ouId, Long userId) {
        // TODO Auto-generated method stub
    }
}

