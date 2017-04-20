package com.baozun.scm.primservice.whoperation.manager.odo.wave;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.baseservice.sac.manager.CodeManager;
import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.LocationInvVolumeWeightCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.InWarehouseMoveWorkCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.LocationCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.UomCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOperationCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOperationLineCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSkuCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhWorkCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhWorkLineCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryAllocatedCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryTobefilledCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.OperationStatus;
import com.baozun.scm.primservice.whoperation.constant.WhUomType;
import com.baozun.scm.primservice.whoperation.constant.WorkStatus;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.AreaDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.Container2ndCategoryDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.UomDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhLocationDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOperationDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOperationExecLineDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOperationLineDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhSkuDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhWorkDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhWorkLineDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WorkTypeDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryAllocatedDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryTobefilledDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.rule.WhLocationInvVolumeWieghtManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhOperationLineManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhOperationManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhWorkLineManager;
import com.baozun.scm.primservice.whoperation.model.BaseModel;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;
import com.baozun.scm.primservice.whoperation.model.warehouse.Area;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container2ndCategory;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOperation;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOperationExecLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOperationLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhWork;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhWorkLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.WorkType;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventory;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventoryAllocated;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventoryTobefilled;
import com.baozun.scm.primservice.whoperation.util.SkuInventoryUuid;
import com.baozun.scm.primservice.whoperation.util.formula.SimpleCubeCalculator;
import com.baozun.scm.primservice.whoperation.util.formula.SimpleWeightCalculator;

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
    @Autowired
    private WhWorkLineManager whWorkLineManager;
    @Autowired
    private WhOperationManager whOperationManager;
    @Autowired
    private WhOperationLineManager whOperationLineManager;
    @Autowired
    private WhOperationExecLineDao whOperationExecLineDao;
    @Autowired
    private WhLocationInvVolumeWieghtManager whLocationInvVolumeWieghtManager;
    @Autowired
    private UomDao uomDao;
    @Autowired
    private Container2ndCategoryDao container2ndCategoryDao;
    @Autowired
    private WhSkuDao whSkuDao;
    
    
    /**
     * 库存分配（生成分配库存与待移入库存）
     * 
     * @param whSkuInventoryCommandLst
     * @param userId
     * @return
     */
    @Override
    public InWarehouseMoveWorkCommand saveAllocatedAndTobefilled(InWarehouseMoveWorkCommand inWarehouseMoveWorkCommand, List<WhSkuInventoryCommand> whSkuInventoryCommandLst) {
        List<WhSkuInventoryAllocatedCommand> whSkuInventoryAllocatedCommandLst = new ArrayList<WhSkuInventoryAllocatedCommand>();
        List<WhSkuInventoryTobefilledCommand> whSkuInventoryTobefilledCommandLst = new ArrayList<WhSkuInventoryTobefilledCommand>();
        Map<Long, Double> idAndQtyMap = new HashMap<Long, Double>();
        idAndQtyMap = inWarehouseMoveWorkCommand.getIdAndQtyMap();
        for(WhSkuInventoryCommand whSkuInventoryCommand : whSkuInventoryCommandLst){
            // 分配库存            
            WhSkuInventoryAllocatedCommand whSkuInventoryAllocatedCommand = new WhSkuInventoryAllocatedCommand();
            whSkuInventoryAllocatedCommand.setSkuId(whSkuInventoryCommand.getSkuId());
            whSkuInventoryAllocatedCommand.setLocationId(whSkuInventoryCommand.getLocationId());
            whSkuInventoryAllocatedCommand.setOuterContainerId(whSkuInventoryCommand.getOuterContainerId());
            whSkuInventoryAllocatedCommand.setInsideContainerId(whSkuInventoryCommand.getInsideContainerId());
            whSkuInventoryAllocatedCommand.setCustomerId(whSkuInventoryCommand.getCustomerId());
            whSkuInventoryAllocatedCommand.setStoreId(whSkuInventoryCommand.getStoreId());
            whSkuInventoryAllocatedCommand.setOccupationCode(whSkuInventoryCommand.getOccupationCode());
            whSkuInventoryAllocatedCommand.setOccupationLineId(whSkuInventoryCommand.getOccupationLineId());
            whSkuInventoryAllocatedCommand.setReplenishmentCode(whSkuInventoryCommand.getReplenishmentCode());
            whSkuInventoryAllocatedCommand.setQty(idAndQtyMap.get(whSkuInventoryCommand.getId()));
            whSkuInventoryAllocatedCommand.setInvStatus(whSkuInventoryCommand.getInvStatus());
            whSkuInventoryAllocatedCommand.setInvType(whSkuInventoryCommand.getInvType());
            whSkuInventoryAllocatedCommand.setBatchNumber(whSkuInventoryCommand.getBatchNumber());
            whSkuInventoryAllocatedCommand.setMfgDate(whSkuInventoryCommand.getMfgDate());
            whSkuInventoryAllocatedCommand.setExpDate(whSkuInventoryCommand.getExpDate());
            whSkuInventoryAllocatedCommand.setCountryOfOrigin(whSkuInventoryCommand.getCountryOfOrigin());
            whSkuInventoryAllocatedCommand.setInvAttr1(whSkuInventoryCommand.getInvAttr1());
            whSkuInventoryAllocatedCommand.setInvAttr2(whSkuInventoryCommand.getInvAttr2());
            whSkuInventoryAllocatedCommand.setInvAttr3(whSkuInventoryCommand.getInvAttr3());
            whSkuInventoryAllocatedCommand.setInvAttr4(whSkuInventoryCommand.getInvAttr4());
            whSkuInventoryAllocatedCommand.setInvAttr5(whSkuInventoryCommand.getInvAttr4());
            whSkuInventoryAllocatedCommand.setUuid(null);
            whSkuInventoryAllocatedCommand.setOuId(whSkuInventoryCommand.getOuId());
            whSkuInventoryAllocatedCommand.setLastModifyTime(whSkuInventoryCommand.getLastModifyTime());
            whSkuInventoryAllocatedCommand.setReplenishmentRuleId(whSkuInventoryCommand.getReplenishmentRuleId());
            WhSkuInventoryAllocated whSkuInventoryAllocated = new WhSkuInventoryAllocated();
            // 复制数据        
            BeanUtils.copyProperties(whSkuInventoryAllocatedCommand, whSkuInventoryAllocated);
            // 内部对接码
            try {
                whSkuInventoryAllocatedCommand.setUuid(SkuInventoryUuid.invUuid(whSkuInventoryAllocated));
                whSkuInventoryAllocated.setUuid(whSkuInventoryAllocatedCommand.getUuid());
            } catch (Exception e) {
                log.error(getLogMsg("whSkuInventoryAllocated uuid error, logId is:[{}]", new Object[] {logId}), e);
                throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
            }
            // 插入数据            
            skuInventoryAllocatedDao.insert(whSkuInventoryAllocated);
            // 待移入库存           
            WhSkuInventoryTobefilledCommand whSkuInventoryTobefilledCommand = new WhSkuInventoryTobefilledCommand();
            whSkuInventoryTobefilledCommand.setSkuId(whSkuInventoryCommand.getSkuId());
            whSkuInventoryTobefilledCommand.setLocationId(inWarehouseMoveWorkCommand.getToLocationId());
            whSkuInventoryTobefilledCommand.setOuterContainerId(whSkuInventoryCommand.getOuterContainerId());
            whSkuInventoryTobefilledCommand.setInsideContainerId(whSkuInventoryCommand.getInsideContainerId());
            whSkuInventoryTobefilledCommand.setCustomerId(whSkuInventoryCommand.getCustomerId());
            whSkuInventoryTobefilledCommand.setStoreId(whSkuInventoryCommand.getStoreId());
            whSkuInventoryTobefilledCommand.setOccupationCode(whSkuInventoryCommand.getOccupationCode());
            whSkuInventoryTobefilledCommand.setOccupationLineId(whSkuInventoryCommand.getOccupationLineId());
            whSkuInventoryTobefilledCommand.setReplenishmentCode(whSkuInventoryCommand.getReplenishmentCode());
            whSkuInventoryTobefilledCommand.setQty(idAndQtyMap.get(whSkuInventoryCommand.getId()));
            whSkuInventoryTobefilledCommand.setInvStatus(whSkuInventoryCommand.getInvStatus());
            whSkuInventoryTobefilledCommand.setInvType(whSkuInventoryCommand.getInvType());
            whSkuInventoryTobefilledCommand.setBatchNumber(whSkuInventoryCommand.getBatchNumber());
            whSkuInventoryTobefilledCommand.setMfgDate(whSkuInventoryCommand.getMfgDate());
            whSkuInventoryTobefilledCommand.setExpDate(whSkuInventoryCommand.getExpDate());
            whSkuInventoryTobefilledCommand.setCountryOfOrigin(whSkuInventoryCommand.getCountryOfOrigin());
            whSkuInventoryTobefilledCommand.setInvAttr1(whSkuInventoryCommand.getInvAttr1());
            whSkuInventoryTobefilledCommand.setInvAttr2(whSkuInventoryCommand.getInvAttr2());
            whSkuInventoryTobefilledCommand.setInvAttr3(whSkuInventoryCommand.getInvAttr3());
            whSkuInventoryTobefilledCommand.setInvAttr4(whSkuInventoryCommand.getInvAttr4());
            whSkuInventoryTobefilledCommand.setInvAttr5(whSkuInventoryCommand.getInvAttr5());
            whSkuInventoryTobefilledCommand.setUuid(null);
            whSkuInventoryTobefilledCommand.setOuId(whSkuInventoryCommand.getOuId());
            whSkuInventoryTobefilledCommand.setLastModifyTime(whSkuInventoryCommand.getLastModifyTime());
            WhSkuInventoryTobefilled whSkuInventoryTobefilled = new WhSkuInventoryTobefilled();
            //复制数据        
            BeanUtils.copyProperties(whSkuInventoryTobefilledCommand, whSkuInventoryTobefilled);
            // 内部对接码
            try {
                whSkuInventoryTobefilledCommand.setUuid(SkuInventoryUuid.invUuid(whSkuInventoryTobefilled));
                whSkuInventoryTobefilled.setUuid(whSkuInventoryTobefilledCommand.getUuid());
            } catch (Exception e) {
                log.error(getLogMsg("whSkuInventoryAllocated uuid error, logId is:[{}]", new Object[] {logId}), e);
                throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
            }
            // 插入数据            
            skuInventoryTobefilledDao.insert(whSkuInventoryTobefilled);
            // 插入返回数据           
            whSkuInventoryAllocatedCommandLst.add(whSkuInventoryAllocatedCommand);
            whSkuInventoryTobefilledCommandLst.add(whSkuInventoryTobefilledCommand);
        }
        inWarehouseMoveWorkCommand.setWhSkuInventoryAllocatedCommandLst(whSkuInventoryAllocatedCommandLst);
        inWarehouseMoveWorkCommand.setWhSkuInventoryTobefilledCommandLst(whSkuInventoryTobefilledCommandLst);
        return inWarehouseMoveWorkCommand;
    }

    /**
     * 创建库内移动工作头
     * 
     * @param whSkuInventoryCommand
     * @param userId
     * @return
     */
    @Override
    public String createInWarehouseMoveWork(InWarehouseMoveWorkCommand inWarehouseMoveWorkCommand, Long ouId, Long userId) {
        String inWarehouseMoveWorkCode = "";
        try {
            WhSkuInventoryAllocatedCommand whSkuInventoryAllocatedCommand = inWarehouseMoveWorkCommand.getWhSkuInventoryAllocatedCommandLst().get(0);
            Long toLocationId = inWarehouseMoveWorkCommand.getToLocationId();
            // 6.创建库内移动工作头
            inWarehouseMoveWorkCode = this.saveInWarehouseMoveWork(whSkuInventoryAllocatedCommand, ouId, userId);
            // 7.创建库内移动工作明细
            for(WhSkuInventoryAllocatedCommand skuInventoryAllocatedCommand : inWarehouseMoveWorkCommand.getWhSkuInventoryAllocatedCommandLst()){
                this.saveInWarehouseMoveWorkLine(inWarehouseMoveWorkCode, skuInventoryAllocatedCommand, toLocationId, userId);
            }
            // 8.更新工作头
            this.updateInWarehouseMoveWork(inWarehouseMoveWorkCode, whSkuInventoryAllocatedCommand);
            // 9.根据工作头和明细生成作业
            String inWarehouseMoveWorkOperationCode = this.saveInWarehouseMoveOperation(inWarehouseMoveWorkCode, whSkuInventoryAllocatedCommand);
            this.saveInWarehouseMoveWorkOperationLine(inWarehouseMoveWorkCode, inWarehouseMoveWorkOperationCode, whSkuInventoryAllocatedCommand);
        } catch (Exception e) {
            log.error("", e);
        }
        return inWarehouseMoveWorkCode;    
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
        whWorkCommand.setWorkCategory("IN_WAREHOUSE_MOVE");
        //是否锁定 默认值：1
        whWorkCommand.setIsLocked(true);
        //是否已迁出        
        whWorkCommand.setIsAssignOut(false);
        //是否短拣--执行时判断
        whWorkCommand.setIsShortPicking(null);
        //是否波次内补货
        whWorkCommand.setIsWaveReplenish(null);
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
        whWorkCommand.setOuterContainerCode(null);
        //容器--更新时获取数据
        whWorkCommand.setContainerCode(null);
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
    private void saveInWarehouseMoveWorkLine(String inWarehouseMoveWorkCode, WhSkuInventoryAllocatedCommand skuInventoryAllocatedCommand, Long toLocationId, Long userId) {
        
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
        whWorkLineCommand.setToLocationId(toLocationId);
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
    public void executeInWarehouseMoveWork(String inWarehouseMoveWorkCode, Long ouId, Long userId) {
        // 获取工作头信息        
        WhWorkCommand whWorkCommand = this.workDao.findWorkByWorkCode(inWarehouseMoveWorkCode, ouId);
        WhOperationCommand whOperationCommand = whOperationManager.findOperationByWorkId(whWorkCommand.getId(), ouId);
        List<WhOperationLineCommand> whOperationLineCommandLst = whOperationLineManager.findOperationLineByOperationId(whOperationCommand.getId(), ouId);
        for(WhOperationLineCommand operationLineCommand : whOperationLineCommandLst){
            WhOperationExecLine operationExecLine = new WhOperationExecLine();
            // 将operationLineCommand基本信息复制到operationExecLine中
            BeanUtils.copyProperties(operationLineCommand, operationExecLine);
            // 是否短拣
            operationExecLine.setIsShortPicking(false);
            // 是否使用新的出库箱/周转箱
            operationExecLine.setIsUseNew(false);
            whOperationExecLineDao.insert(operationExecLine);    
        }
    }
    
    /**
     * [业务方法] 判断目标库位体积和重量
     * @param 
     * @param 
     * @return
     */
    @Override
    public Boolean calculateVolumeAndWeight(Long toLocationId, Long ouId) {
        
        Map<String, Double> lenUomConversionRate = new HashMap<String, Double>();   //长度，度量单位转换率
        Map<String, Double> weightUomConversionRate = new HashMap<String, Double>();  //重量，度量单位转换率
        List<UomCommand> lenUomCmds = null;   //长度度量单位
        List<UomCommand> weightUomCmds = null;   //重量度量单位
        lenUomCmds = uomDao.findUomByGroupCode(WhUomType.LENGTH_UOM, BaseModel.LIFECYCLE_NORMAL);
        for (UomCommand lenUom : lenUomCmds) {
            String uomCode = "";
            Double uomRate = 0.0;
            if (null != lenUom) {
                uomCode = lenUom.getUomCode();
                uomRate = lenUom.getConversionRate();
                lenUomConversionRate.put(uomCode, uomRate);
            }
        }
        weightUomCmds = uomDao.findUomByGroupCode(WhUomType.WEIGHT_UOM, BaseModel.LIFECYCLE_NORMAL);
        for (UomCommand lenUom : weightUomCmds) {
            String uomCode = "";
            Double uomRate = 0.0;
            if (null != lenUom) {
                uomCode = lenUom.getUomCode();
                uomRate = lenUom.getConversionRate();
                weightUomConversionRate.put(uomCode, uomRate);
            }
        }
        Map<String, Map<String, Double>> uomMap = new HashMap<String, Map<String, Double>>();
        uomMap.put(WhUomType.LENGTH_UOM, lenUomConversionRate);
        uomMap.put(WhUomType.WEIGHT_UOM, weightUomConversionRate);
        /*********************************************************************************************************/
        // 1.获取该库位上的所有库存
        List<WhSkuInventoryCommand> locInvs = whSkuInventoryDao.findWhSkuInventoryByLocIdAndOuId(ouId, toLocationId);
        // 2.分析库存并计算体积重量
        SimpleCubeCalculator cubeCal = new SimpleCubeCalculator(lenUomConversionRate);
        SimpleWeightCalculator weightCal = new SimpleWeightCalculator(weightUomConversionRate);
        Set<Long> outerContainerIds = new HashSet<Long>();
        Set<Long> insideContainerIds = new HashSet<Long>();
        Set<Long> skuIds = new HashSet<Long>();
        for (WhSkuInventoryCommand invCmd : locInvs) {
            Long outerContainerId = invCmd.getOuterContainerId();
            Long insideContainerId = invCmd.getInsideContainerId();
            Long skuId = invCmd.getSkuId();
            if (null != outerContainerId && !outerContainerIds.contains(outerContainerId)) {
                // 有外部容器
                Container container = containerDao.findByIdExt(outerContainerId, ouId);
                Long outerContainerCate = container.getTwoLevelType();
                Container2ndCategory outerContainer2 = container2ndCategoryDao.findByIdExt(outerContainerCate, ouId);
                if (null == outerContainer2) {
                    log.error("container2ndCategory is null error, cId is:[{}], 2endCategoryId is:[{}], logId is:[{}]", outerContainerId, outerContainerCate, logId);
                    throw new BusinessException(ErrorCodes.CONTAINER2NDCATEGORY_NULL_ERROR);
                }
                if (1 != outerContainer2.getLifecycle()) {
                    log.error("container2ndCategory lifecycle is not normal error, cId is:[{}], 2endCategoryId is:[{}], logId is:[{}]", outerContainerId, outerContainer2.getId(), logId);
                    throw new BusinessException(ErrorCodes.COMMON_CONTAINER_LIFECYCLE_IS_NOT_NORMAL);
                }
                Double ocLength = outerContainer2.getLength();
                Double ocWidth = outerContainer2.getWidth();
                Double ocHeight = outerContainer2.getHigh();
                Double ocWeight = outerContainer2.getWeight();
                if (null == ocLength || null == ocWidth || null == ocHeight) {
                    log.error("sys guide putaway inside container length、width、height is null error, ocId is:[{}], logId is:[{}]", outerContainerId, logId);
                    throw new BusinessException(ErrorCodes.CONTAINER_LENGTH_WIDTH_HIGHT_IS_NULL_ERROR, new Object[] {container.getCode()});
                }
                if (null == ocWeight) {
                    log.error("sys guide putaway inside container weight is null error, ocId is:[{}], logId is:[{}]", outerContainerId, logId);
                    throw new BusinessException(ErrorCodes.CONTAINER_WEIGHT_IS_NULL_ERROR, new Object[] {container.getCode()});
                }
                cubeCal.accumulationStuffVolume(ocLength, ocWidth, ocHeight);
                weightCal.accumulationStuffWeight(ocWeight);
            } else {
                if (null != insideContainerId && !insideContainerIds.contains(insideContainerId)) {
                    // 无外部容器但有内部容器
                    Container container = containerDao.findByIdExt(insideContainerId, ouId);
                    Long insideContainerCate = container.getTwoLevelType();
                    Container2ndCategory insideContainer2 = container2ndCategoryDao.findByIdExt(insideContainerCate, ouId);
                    if (null == insideContainer2) {
                        log.error("container2ndCategory is null error, cId is:[{}], 2endCategoryId is:[{}], logId is:[{}]", insideContainerId, insideContainerCate, logId);
                        throw new BusinessException(ErrorCodes.CONTAINER2NDCATEGORY_NULL_ERROR);
                    }
                    if (1 != insideContainer2.getLifecycle()) {
                        log.error("container2ndCategory lifecycle is not normal error, cId is:[{}], 2endCategoryId is:[{}], logId is:[{}]", insideContainerId, insideContainer2.getId(), logId);
                        throw new BusinessException(ErrorCodes.COMMON_CONTAINER_LIFECYCLE_IS_NOT_NORMAL);
                    }
                    Double icLength = insideContainer2.getLength();
                    Double icWidth = insideContainer2.getWidth();
                    Double icHeight = insideContainer2.getHigh();
                    Double icWeight = insideContainer2.getWeight();
                    if (null == icLength || null == icWidth || null == icHeight) {
                        log.error("sys guide putaway inside container length、width、height is null error, ocId is:[{}], logId is:[{}]", insideContainerId, logId);
                        throw new BusinessException(ErrorCodes.CONTAINER_LENGTH_WIDTH_HIGHT_IS_NULL_ERROR, new Object[] {container.getCode()});
                    }
                    if (null == icWeight) {
                        log.error("sys guide putaway inside container weight is null error, ocId is:[{}], logId is:[{}]", insideContainerId, logId);
                        throw new BusinessException(ErrorCodes.CONTAINER_WEIGHT_IS_NULL_ERROR, new Object[] {container.getCode()});
                    }
                    cubeCal.accumulationStuffVolume(icLength, icWidth, icHeight);
                    weightCal.accumulationStuffWeight(icWeight);
                } else {
                    if (null != skuId && !skuIds.contains(skuId)) {
                        // 既无外部容器也无内部容器
                        WhSkuCommand skuCmd = whSkuDao.findWhSkuByIdExt(skuId, ouId);
                        if (null == skuCmd) {
                            log.error("sys guide putaway sku is not exists error, skuId is:[{}], logId is:[{}]", skuId, logId);
                            throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
                        }
                        Double skuLength = skuCmd.getLength();
                        Double skuWidth = skuCmd.getWidth();
                        Double skuHeight = skuCmd.getHeight();
                        if (null == skuLength || null == skuWidth || null == skuHeight) {
                            log.error("sys guide putaway sku length、width、height is null error, skuId is:[{}], logId is:[{}]", skuId, logId);
                            throw new BusinessException(ErrorCodes.SKU_LENGTH_WIDTH_HIGHT_IS_NULL_ERROR, new Object[] {skuCmd.getBarCode()});
                        }
                        Double onHandQty = invCmd.getOnHandQty();
                        Double freezeQty = invCmd.getFrozenQty();
                        Double toBeFillQty = invCmd.getToBeFilledQty();
                        Double sVolume = cubeCal.calculateStuffVolume(skuLength, skuWidth, skuHeight);
                        if(null == toBeFillQty) {
                            toBeFillQty = 0.0;
                        }
                        cubeCal.addStuffVolume(sVolume * (onHandQty + freezeQty + toBeFillQty));
                    }
                }
            }
            // 计算库位上所有商品的重量
            Double onHandQty = invCmd.getOnHandQty();
            Double freezeQty = invCmd.getFrozenQty();
            Double toBeFillQty = invCmd.getToBeFilledQty();
            WhSkuCommand skuCmd = whSkuDao.findWhSkuByIdExt(skuId, ouId);
            if (null == skuCmd) {
                log.error("sys guide putaway sku is not exists error, skuId is:[{}], logId is:[{}]", skuId, logId);
                throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
            }
            Double skuWeight = skuCmd.getWeight();
            if (null == skuWeight) {
                log.error("sys guide putaway sku weight is null error, skuId is:[{}], logId is:[{}]", skuId, logId);
                throw new BusinessException(ErrorCodes.SKU_WEIGHT_IS_NULL_ERROR, new Object[] {skuCmd.getBarCode()});
            }
            Double sWeight = weightCal.calculateStuffWeight(skuWeight);
            if(null == toBeFillQty) {
                toBeFillQty = 0.0;
            }
            weightCal.addStuffWeight(sWeight * (onHandQty + freezeQty + toBeFillQty));
            outerContainerIds.add(outerContainerId);
            insideContainerIds.add(insideContainerId);
            skuIds.add(skuId);
        }
    
        /*********************************************************************************************************/       
        Double livwVolume = cubeCal.getStuffVolume();// 库位上已有货物总体积
        Double livwWeight = weightCal.getStuffWeight();// 库位上已有货物总重量
        return true;
    }
    
}

