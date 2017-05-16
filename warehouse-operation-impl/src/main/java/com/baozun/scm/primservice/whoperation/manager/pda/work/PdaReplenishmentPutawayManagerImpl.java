package com.baozun.scm.primservice.whoperation.manager.pda.work;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
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
import org.springframework.util.StringUtils;

import com.baozun.redis.manager.CacheManager;
import com.baozun.scm.primservice.whoperation.command.pda.work.CheckScanResultCommand;
import com.baozun.scm.primservice.whoperation.command.pda.work.OperationExecStatisticsCommand;
import com.baozun.scm.primservice.whoperation.command.pda.work.ReplenishScanTipSkuCacheCommand;
import com.baozun.scm.primservice.whoperation.command.pda.work.ReplenishmentPutawayCacheCommand;
import com.baozun.scm.primservice.whoperation.command.pda.work.ReplenishmentPutawayCommand;
import com.baozun.scm.primservice.whoperation.command.pda.work.ReplenishmentScanResultComamnd;
import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOperationCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOperationLineCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSkuCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhWorkCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhWorkLineCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventorySnCommand;
import com.baozun.scm.primservice.whoperation.constant.CacheConstants;
import com.baozun.scm.primservice.whoperation.constant.CancelPattern;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.ContainerStatus;
import com.baozun.scm.primservice.whoperation.constant.WhScanPatternType;
import com.baozun.scm.primservice.whoperation.constant.WorkStatus;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhFunctionReplenishmentDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhLocationDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOperationDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOperationExecLineDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOperationLineDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhSkuDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhWorkDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhWorkLineDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventorySnDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.pda.inbound.putaway.SkuCategoryProvider;
import com.baozun.scm.primservice.whoperation.manager.pda.inbound.putaway.TipSkuDetailProvider;
import com.baozun.scm.primservice.whoperation.manager.redis.SkuRedisManager;
import com.baozun.scm.primservice.whoperation.manager.system.SysDictionaryManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.InventoryStatusManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.LocationManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhOperationManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.inventory.WhSkuInventoryManager;
import com.baozun.scm.primservice.whoperation.model.BaseModel;
import com.baozun.scm.primservice.whoperation.model.system.SysDictionary;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container;
import com.baozun.scm.primservice.whoperation.model.warehouse.InventoryStatus;
import com.baozun.scm.primservice.whoperation.model.warehouse.Location;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFunctionReplenishment;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOperation;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOperationExecLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOperationLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhWork;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhWorkLine;
import com.baozun.utilities.type.StringUtil;
/***
 * 补货中的上架
 * @author Administrator
 *
 */
@Service("pdaReplenishmentPutawayManager")
@Transactional
public class PdaReplenishmentPutawayManagerImpl extends BaseManagerImpl implements PdaReplenishmentPutawayManager{

    protected static final Logger log = LoggerFactory.getLogger(PdaReplenishmentPutawayManagerImpl.class);
    @Autowired
    private PdaReplenishmentPutawayCacheManager pdaReplenishmentPutawayCacheManager;
    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private WhLocationDao whLocationDao;
    @Autowired
    private ContainerDao containerDao;
    @Autowired
    private WhSkuInventoryManager whSkuInventoryManager;
    @Autowired
    private WhWorkDao whWorkDao;
    @Autowired
    private WhOperationDao  whOperationDao;
    @Autowired
    private WhOperationExecLineDao whOperationExecLineDao;
    @Autowired
    private PdaPickingWorkCacheManager pdaPickingWorkCacheManager;
    @Autowired
    private LocationManager locationManager;
    @Autowired
    private WhOperationManager whOperationManager;
    @Autowired
    private WhSkuInventorySnDao whSkuInventorySnDao;
    @Autowired
    private WhWorkLineDao whWorkLineDao;
    @Autowired
    private WhOperationLineDao whOperationLineDao;
    @Autowired
    private WhFunctionReplenishmentDao whFunctionReplenishmentDao;
    @Autowired
    private WhSkuInventoryDao whSkuInventoryDao;
    @Autowired
    private SysDictionaryManager sysDictionaryManager;
    @Autowired
    private InventoryStatusManager inventoryStatusManager;
    @Autowired
    private SkuRedisManager skuRedisManager;
    @Autowired
    private WhSkuDao whSkuDao;
    
    
    @Override
    public ReplenishmentPutawayCommand putawayTipLocation(ReplenishmentPutawayCommand command) {
        // TODO Auto-generated method stub
        log.info("PdaReplenishmentPutawayManagerImpl putawayTipLocation is start");
        Long operationId = command.getOperationId();
        Long ouId = command.getOuId();
        OperationExecStatisticsCommand opExecLineCmd = cacheManager.getObject(CacheConstants.OPERATIONEXEC_STATISTICS + operationId.toString());
        if(null == opExecLineCmd){
            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
        }
        List<Long> locationIds = opExecLineCmd.getLocationIds();
        Long tipLocationId = null;
        for(Long locationId:locationIds) {
            if(null != locationId) {
                tipLocationId = locationId;
            }
        }
        Location location = whLocationDao.findByIdExt(tipLocationId, ouId);
        if(null == location) {
            throw new BusinessException(ErrorCodes.TIP_LOCATION_FAIL);
        }
        command.setTipLocationBarCode(location.getBarCode());
        command.setTipLocationCode(location.getCode());
        command.setLocationId(location.getId()); 
        command.setIsNeedScanLocation(true);
        log.info("PdaReplenishmentPutawayManagerImpl putawayTipLocation is end");
        return command;
    }

    @Override
    public ReplenishmentPutawayCommand putawayScanLocation(ReplenishmentPutawayCommand command) {
        // TODO Auto-generated method stub
        log.info("PdaReplenishmentPutawayManagerImpl putawayScanLocation is start");
        Long operationId = command.getOperationId();
        Long ouId = command.getOuId();
        Long locationId = command.getLocationId();                            
        OperationExecStatisticsCommand opExecLineCmd = cacheManager.getObject(CacheConstants.OPERATIONEXEC_STATISTICS + operationId.toString());
        if(null == opExecLineCmd){
            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
        }
        Map<Long, Set<Long>> locToTurnoverBoxIds = opExecLineCmd.getTurnoverBoxIds();
        Set<Long> turnoverBoxIds = locToTurnoverBoxIds.get(locationId);
        if(null == turnoverBoxIds || turnoverBoxIds.size() == 0){
            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
        }
        //缓存库位
        this.cacheLocation(operationId, locationId);
        ReplenishmentScanResultComamnd  sRCmd = pdaReplenishmentPutawayCacheManager.tipTurnoverBox(turnoverBoxIds, operationId,locationId);
        Long turnoverBoxId = sRCmd.getTurnoverBoxId();
        String containerCode = this.judeContainer(turnoverBoxId, ouId);
        command.setTipTurnoverBoxCode(containerCode);
        command.setIsNeedScanTurnoverBox(true);
        log.info("PdaReplenishmentPutawayManagerImpl putawayScanLocation is end");
        return command;
    }

    
    private void cacheLocation(Long operationId,Long locationId){
        ReplenishmentPutawayCacheCommand replenishment = cacheManager.getObject(CacheConstants.CACHE_PUTAWAY_LOCATION + operationId.toString());
        if(null == replenishment){
            replenishment = new ReplenishmentPutawayCacheCommand();
            ArrayDeque<Long> tipLocationIds = new ArrayDeque<Long>();
            tipLocationIds.addFirst(locationId);
            replenishment.setTipLocationIds(tipLocationIds);
        }else{
            ArrayDeque<Long> tipLocationIds = replenishment.getTipLocationIds();
            if(null == tipLocationIds || tipLocationIds.isEmpty()){
                tipLocationIds = new ArrayDeque<Long>();
                tipLocationIds.addFirst(locationId);
                replenishment.setTipLocationIds(tipLocationIds);
            }else{
                if(!tipLocationIds.contains(locationId)){
                    tipLocationIds.addFirst(locationId);
                    replenishment.setTipLocationIds(tipLocationIds);
                }
            }
        }
        cacheManager.setObject(CacheConstants.CACHE_PUTAWAY_LOCATION + operationId.toString(), replenishment, CacheConstants.CACHE_ONE_DAY);
    }
    private String judeContainer(Long turnoverBoxId,Long ouId){
        log.info("PdaReplenishmentPutawayManagerImpl judeContainer is start");
        Container container = containerDao.findByIdExt(turnoverBoxId, ouId);
        if(null == container) {
            throw new BusinessException(ErrorCodes.TIP_CONTAINER_FAIL);
        }
        // 验证容器状态是否可用
        if (!container.getLifecycle().equals(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED)) {
            log.error("container lifecycle is not normal, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER__NOT_PUTWAY);
        }
        // 验证容器状态是否是待上架
        if (!(container.getStatus().equals(ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY) || container.getStatus().equals(ContainerStatus.CONTAINER_STATUS_PUTAWAY))) {
            log.error("container lifecycle is not normal, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER__NOT_PUTWAY);
        }
        log.info("PdaReplenishmentPutawayManagerImpl judeContainer is end");
        return container.getCode();
    }

    /***
     * 扫描周转箱
     * @param command
     * @return
     */
    @Override
    public ReplenishmentPutawayCommand putawayScanTurnoverBox(ReplenishmentPutawayCommand command,Boolean isTabbInvTotal) {
        // TODO Auto-generated method stub
        log.info("PdaReplenishmentPutawayManagerImpl putawayScanTurnoverBox is start");
        Long operationId = command.getOperationId();
        String workCode = command.getWorkBarCode();
        Long userId = command.getUserId();
        Long ouId = command.getOuId();
        Long locationId = command.getLocationId();
        String turnoverBoxCode = command.getTurnoverBoxCode();
        OperationExecStatisticsCommand opExecLineCmd = cacheManager.getObject(CacheConstants.OPERATIONEXEC_STATISTICS + operationId.toString());
        if(null == opExecLineCmd){
            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
        }
        ContainerCommand cmd = containerDao.getContainerByCode(turnoverBoxCode, ouId);
        if(null == cmd) {
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
        }
        List<Long> locationIds = opExecLineCmd.getLocationIds();
        Map<Long, Set<Long>> mapTurnoverBoxIds = opExecLineCmd.getTurnoverBoxIds();
        Set<Long> turnoverBoxIds = mapTurnoverBoxIds.get(locationId);
        Long turnoverBoxId = cmd.getId();
        if(locationIds.size()==1) {//一个目标库位
            //缓存上一个周转箱
            pdaReplenishmentPutawayCacheManager.pdaReplenishPutwayCacheTurnoverBox(operationId, turnoverBoxId,locationId,ouId,true);
            ReplenishmentScanResultComamnd  sRCmd = pdaReplenishmentPutawayCacheManager.tipTurnoverBox(turnoverBoxIds, operationId,locationId);
            if(sRCmd.getIsNeedScanTurnoverBox()) {  //当前库位对应的周转箱扫描完毕
                Long tipTurnoverBoxId = sRCmd.getTurnoverBoxId();
                String containerCode = this.judeContainer(tipTurnoverBoxId, ouId);
                command.setTipTurnoverBoxCode(containerCode);
                command.setIsNeedScanTurnoverBox(true);
                //当前周转箱上架
                whSkuInventoryManager.replenishmentContianerPutaway(locationId, operationId, ouId, isTabbInvTotal, userId, workCode, turnoverBoxId);
                //判断当前库位是否有拣货工作
                this.judeLocationIsPicking(turnoverBoxId, locationId, ouId, userId);
            }else{//继续扫描下一个库位
                 command.setIsScanFinsh(true);
                 whSkuInventoryManager.replenishmentContianerPutaway(locationId, operationId, ouId, isTabbInvTotal, userId, workCode, turnoverBoxId);
                 //判断当前补货库位有没有拣货工作
                 //更新工作及作业状态
                 this.updateStatus(operationId, workCode, ouId, userId);
                 //判断当前库位是否有拣货工作
                 this.judeLocationIsPicking(turnoverBoxId, locationId, ouId, userId);
                 //清除所有缓存
                 pdaReplenishmentPutawayCacheManager.pdaReplenishPutwayRemoveAllCache(operationId,turnoverBoxId,locationId,true);
           
            }
        }else{//多个目标库位
            Map<String, Set<Long>> locSkuIds = opExecLineCmd.getSkuIds();
            Map<String, Map<String, Set<String>>> locSkuAttrIdsSnDefect = opExecLineCmd.getSkuAttrIdsSnDefect();
            Map<String, Map<Long, Map<String, Long>>> locSkuAttrIds = opExecLineCmd.getSkuAttrIds();
            String skuAttrIds = null;
            Map<String, Long> skuAttrIdQty = null;
            String key = locationId.toString()+turnoverBoxId;
            Map<String, Set<String>> skuAttrIdsSnDefect = locSkuAttrIdsSnDefect.get(key);
            Map<Long, Map<String, Long>> skuAttrIdsQty = locSkuAttrIds.get(key);
            Set<Long> skuIds  = locSkuIds.get(key);
            skuAttrIds = pdaReplenishmentPutawayCacheManager.pdaReplenishPutWayTipSku(skuIds, skuAttrIdsQty, skuAttrIdsSnDefect, locationId,turnoverBoxId);//  返回结果有sn时包含sn
            Long skuId = SkuCategoryProvider.getSkuId(skuAttrIds);
            skuAttrIdQty = skuAttrIdsQty.get(skuId);
            this.tipSkuDetailAspect(command, skuAttrIds, skuAttrIdQty, logId);
            WhSkuCommand skuCmd = whSkuDao.findWhSkuByIdExt(skuId, ouId);
            if (null == skuCmd) {
                throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
            }
            command.setTipSkuBarCode(skuCmd.getBarCode()); // 提示sku
            command.setSkuId(skuId);
            command.setIsOnlyLocation(true);
            command.setIsNeedScanSku(true);
        }
       log.info("PdaReplenishmentPutawayManagerImpl putawayScanTurnoverBox is end");
       return command;
    }

    /***
     * 
     * @param srCmd
     * @param tipSkuAttrId
     * @param locSkuAttrIds
     * @param skuAttrIdsQty
     * @param logId
     */
    private void tipSkuDetailAspect(ReplenishmentPutawayCommand srCmd, String tipSkuAttrId, Map<String, Long> skuAttrIdsQty, String logId) {
        String skuAttrId = SkuCategoryProvider.getSkuAttrId(tipSkuAttrId);
        Long qty = skuAttrIdsQty.get(skuAttrId);
        if (null == qty) {
            log.error("sku qty is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.SYSTEM_EXCEPTION);
        }
        srCmd.setTipSkuQty(qty);
        srCmd.setIsNeedScanSkuInvType(TipSkuDetailProvider.isTipSkuInvType(tipSkuAttrId)); // 是否需要扫描商品类型
        if (true == srCmd.getIsNeedScanSkuInvType()) {
            String skuInvType = TipSkuDetailProvider.getSkuInvType(tipSkuAttrId);
            List<SysDictionary> invTypeList = sysDictionaryManager.getListByGroup(Constants.INVENTORY_TYPE, BaseModel.LIFECYCLE_NORMAL);
            boolean isExists = false;
            for (SysDictionary sd : invTypeList) {
                if (sd.getDicValue().equals(skuInvType)) {
                    srCmd.setSkuInvType(sd.getDicLabel());
                    isExists = true;
                    break;
                }
            }
            if (false == isExists) {
                log.error("inv type is not found error, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.COMMON_INV_TYPE_NOT_FOUND_ERROR);
            }
        } else {
            srCmd.setSkuInvType("");
        }
        srCmd.setIsNeedScanSkuInvStatus(TipSkuDetailProvider.isTipSkuInvStatus(tipSkuAttrId));
        if (true == srCmd.getIsNeedScanSkuInvStatus()) {
            String skuInvStatus = TipSkuDetailProvider.getSkuInvStatus(tipSkuAttrId);
            List<InventoryStatus> invStatusList = inventoryStatusManager.findAllInventoryStatus();
            boolean isExists = false;
            for (InventoryStatus is : invStatusList) {
                if (is.getId().toString().equals(skuInvStatus)) {
                    srCmd.setSkuInvStatusName(is.getName());
                    isExists = true;
                    break;
                }
            }
            if (false == isExists) {
                log.error("inv status is not found error, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.COMMON_INV_STATUS_NOT_FOUND_ERROR);
            }
        } else {
            srCmd.setSkuInvStatus(null);
        }
        srCmd.setIsNeedScanBatchNumber(TipSkuDetailProvider.isTipSkuBatchNumber(tipSkuAttrId));
        if (true == srCmd.getIsNeedScanBatchNumber()) {
            String skuBatchNumber = TipSkuDetailProvider.getSkuBatchNumber(tipSkuAttrId);
            srCmd.setBatchNumber(skuBatchNumber);
        } else {
            srCmd.setBatchNumber("");
        }
        srCmd.setIsNeedScanOrigin(TipSkuDetailProvider.isTipSkuCountryOfOrigin(tipSkuAttrId));
        if (true == srCmd.getIsNeedScanOrigin()) {
            String skuCountryOfOrigin = TipSkuDetailProvider.getSkuCountryOfOrigin(tipSkuAttrId);
            srCmd.setSkuOrigin(skuCountryOfOrigin);
        } else {
            srCmd.setSkuOrigin("");
        }
        // sku不在任何容器内
        srCmd.setIsNeedScanSkuMfgDate(TipSkuDetailProvider.isTipSkuMfgDate(tipSkuAttrId));
        if (true == srCmd.getIsNeedScanSkuMfgDate()) {
            String skuMfgDate = TipSkuDetailProvider.getSkuMfgDate(tipSkuAttrId);
            srCmd.setSkuMfgDate(skuMfgDate);
        } else {
            srCmd.setSkuMfgDate("");
        }
        srCmd.setIsNeedScanSkuExpDate(TipSkuDetailProvider.isTipSkuExpDate(tipSkuAttrId));
        if (true == srCmd.getIsNeedScanSkuExpDate()) {
            String skuExpDate = TipSkuDetailProvider.getSkuExpDate(tipSkuAttrId);
            srCmd.setSkuExpDate(skuExpDate);
        } else {
            srCmd.setSkuExpDate("");
        }
        srCmd.setIsNeedScanSkuInvAttr1(TipSkuDetailProvider.isTipSkuInvAttr1(tipSkuAttrId));
        if (true == srCmd.getIsNeedScanSkuInvAttr1()) {
            String skuInvAttr1 = TipSkuDetailProvider.getSkuInvAttr1(tipSkuAttrId);
            List<SysDictionary> list = sysDictionaryManager.getListByGroup(Constants.INVENTORY_ATTR_1, BaseModel.LIFECYCLE_NORMAL);
            boolean isExists = false;
            for (SysDictionary sd : list) {
                if (sd.getDicValue().equals(skuInvAttr1)) {
                    srCmd.setSkuInvAttr1(sd.getDicLabel());
                    isExists = true;
                    break;
                }
            }
            if (false == isExists) {
                log.error("inv attr4 is not found error, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.COMMON_INV_ATTR_NOT_FOUND_ERROR);
            }
        } else {
            srCmd.setSkuInvAttr1("");
        }
        srCmd.setIsNeedScanSkuInvAttr2(TipSkuDetailProvider.isTipSkuInvAttr2(tipSkuAttrId));
        if (true == srCmd.getIsNeedScanSkuInvAttr2()) {
            String skuInvAttr2 = TipSkuDetailProvider.getSkuInvAttr2(tipSkuAttrId);
            List<SysDictionary> list = sysDictionaryManager.getListByGroup(Constants.INVENTORY_ATTR_2, BaseModel.LIFECYCLE_NORMAL);
            boolean isExists = false;
            for (SysDictionary sd : list) {
                if (sd.getDicValue().equals(skuInvAttr2)) {
                    srCmd.setSkuInvAttr2(sd.getDicLabel());
                    isExists = true;
                    break;
                }
            }
            if (false == isExists) {
                log.error("inv attr2 is not found error, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.COMMON_INV_ATTR_NOT_FOUND_ERROR);
            }
        } else {
            srCmd.setSkuInvAttr2("");
        }
        srCmd.setIsNeedScanSkuInvAttr3(TipSkuDetailProvider.isTipSkuInvAttr3(tipSkuAttrId));
        if (true == srCmd.getIsNeedScanSkuInvAttr3()) {
            String skuInvAttr3 = TipSkuDetailProvider.getSkuInvAttr3(tipSkuAttrId);
            List<SysDictionary> list = sysDictionaryManager.getListByGroup(Constants.INVENTORY_ATTR_3, BaseModel.LIFECYCLE_NORMAL);
            boolean isExists = false;
            for (SysDictionary sd : list) {
                if (sd.getDicValue().equals(skuInvAttr3)) {
                    srCmd.setSkuInvAttr3(sd.getDicLabel());
                    isExists = true;
                    break;
                }
            }
            if (false == isExists) {
                log.error("inv attr3 is not found error, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.COMMON_INV_ATTR_NOT_FOUND_ERROR);
            }
        } else {
            srCmd.setSkuInvAttr3("");
        }
        srCmd.setIsNeedScanSkuInvAttr4(TipSkuDetailProvider.isTipSkuInvAttr4(tipSkuAttrId));
        if (true == srCmd.getIsNeedScanSkuInvAttr4()) {
            String skuInvAttr4 = TipSkuDetailProvider.getSkuInvAttr4(tipSkuAttrId);
            List<SysDictionary> list = sysDictionaryManager.getListByGroup(Constants.INVENTORY_ATTR_4, BaseModel.LIFECYCLE_NORMAL);
            boolean isExists = false;
            for (SysDictionary sd : list) {
                if (sd.getDicValue().equals(skuInvAttr4)) {
                    srCmd.setSkuInvAttr4(sd.getDicLabel());
                    isExists = true;
                    break;
                }
            }
            if (false == isExists) {
                log.error("inv attr4 is not found error, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.COMMON_INV_ATTR_NOT_FOUND_ERROR);
            }
        } else {
            srCmd.setSkuInvAttr4("");
        }
        srCmd.setIsNeedScanSkuInvAttr5(TipSkuDetailProvider.isTipSkuInvAttr5(tipSkuAttrId));
        if (true == srCmd.getIsNeedScanSkuInvAttr5()) {
            String skuInvAttr5 = TipSkuDetailProvider.getSkuInvAttr5(tipSkuAttrId);
            List<SysDictionary> list = sysDictionaryManager.getListByGroup(Constants.INVENTORY_ATTR_5, BaseModel.LIFECYCLE_NORMAL);
            boolean isExists = false;
            for (SysDictionary sd : list) {
                if (sd.getDicValue().equals(skuInvAttr5)) {
                    srCmd.setSkuInvAttr5(sd.getDicLabel());
                    isExists = true;
                    break;
                }
            }
            if (false == isExists) {
                log.error("inv attr5 is not found error, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.COMMON_INV_ATTR_NOT_FOUND_ERROR);
            }
        } else {
            srCmd.setSkuInvAttr5("");
        }
        srCmd.setIsNeedScanSkuSn(TipSkuDetailProvider.isTipSkuSn(tipSkuAttrId));
        if (true == srCmd.getIsNeedScanSkuSn()) {
            String skuSn = TipSkuDetailProvider.getSkuSn(tipSkuAttrId);
            srCmd.setSkuSn(skuSn);
        } else {
            srCmd.setSkuSn("");
        }
        srCmd.setIsNeedScanSkuDefect(TipSkuDetailProvider.isTipSkuDefect(tipSkuAttrId));
        if (true == srCmd.getIsNeedScanSkuDefect()) {
            String skuDefect = TipSkuDetailProvider.getSkuDefect(tipSkuAttrId);
            srCmd.setSkuDefect(skuDefect);
        } else {
            srCmd.setSkuDefect("");
        }
    }
    
    /**
     * 扫描sku 
     * @param operationId
     * @param locationId
     * @param command
     * @return
     */
    public ReplenishmentPutawayCommand scanSku(ReplenishmentPutawayCommand command,WhSkuCommand skuCmd,Boolean isTabbInvTotal){
        Long operationId = command.getOperationId();
        Long locationId = command.getLocationId();
        Long userId = command.getUserId();
        Long ouId = command.getOuId();
        Long skuId = command.getSkuId();
        String skuBarCode = command.getSkuBarCode();
        String turnoverBoxCode = command.getTurnoverBoxCode(); // 周转箱
        String newTurnoverBoxCode = command.getNewTurnoverBoxCode();
        Long newTurnoverBoxId = null; 
        if (!StringUtils.isEmpty(newTurnoverBoxCode)) {
            ContainerCommand turnoverBoxCmd = containerDao.getContainerByCode(newTurnoverBoxCode, ouId);
            if (null == turnoverBoxCmd) {
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
            }
            newTurnoverBoxId = turnoverBoxCmd.getId();
        }
        Long turnoverBoxId = null;
        String workCode = command.getWorkBarCode();
        Integer scanPattern = command.getScanPattern();
        if (!StringUtils.isEmpty(turnoverBoxCode)) {
            ContainerCommand turnoverBoxCmd = containerDao.getContainerByCode(turnoverBoxCode, ouId);
            if (null == turnoverBoxCmd) {
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
            }
            turnoverBoxId = turnoverBoxCmd.getId();
        }
        Double scanQty = skuCmd.getScanSkuQty(); // 扫描的商品数量
        if (null == scanQty || scanQty.longValue() < 1) {
            log.error("scan sku qty is valid, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.SCAN_SKU_QTY_IS_VALID);
        }
        if (StringUtils.isEmpty(skuBarCode)) {
            log.error("sku is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
        }
        WhSkuInventoryCommand invSkuCmd = new WhSkuInventoryCommand();
        invSkuCmd.setSkuId(command.getSkuId());
        skuCmd.setId(command.getSkuId());
        // invSkuCmd.setInvType(command.getSkuInvType());
        invSkuCmd.setBatchNumber(command.getBatchNumber());
        try {
            if (!StringUtils.isEmpty(command.getSkuMfgDate())) {
                invSkuCmd.setMfgDate(new SimpleDateFormat("yyyyMMddHHmmss").parse(command.getSkuMfgDate()));
            } else {
                invSkuCmd.setMfgDate(null);
            }
            if (!StringUtils.isEmpty(command.getSkuExpDate())) {
                invSkuCmd.setExpDate(new SimpleDateFormat("yyyyMMddHHmmss").parse(command.getSkuExpDate()));
            } else {
                invSkuCmd.setExpDate(null);
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        invSkuCmd.setCountryOfOrigin(command.getSkuOrigin());
        List<SysDictionary> invTypeList = sysDictionaryManager.getListByGroup(Constants.INVENTORY_TYPE, BaseModel.LIFECYCLE_NORMAL);
        for (SysDictionary sd : invTypeList) {
            if (sd.getDicLabel().equals(command.getSkuInvType())) {
                invSkuCmd.setInvType(sd.getDicValue());
                break;
            }
        }
        List<InventoryStatus> invStatusList = inventoryStatusManager.findAllInventoryStatus();
        for (InventoryStatus is : invStatusList) {
            if (is.getName().toString().equals(command.getSkuInvStatusName())) {
                invSkuCmd.setInvStatus(is.getId());
                break;
            }
        }
        List<SysDictionary> list1 = sysDictionaryManager.getListByGroup(Constants.INVENTORY_ATTR_1, BaseModel.LIFECYCLE_NORMAL);
        for (SysDictionary sd : list1) {
            if (sd.getDicLabel().equals(command.getSkuInvAttr1())) {
                invSkuCmd.setInvAttr1(sd.getDicValue());
                break;
            }
        }
        List<SysDictionary> list2 = sysDictionaryManager.getListByGroup(Constants.INVENTORY_ATTR_2, BaseModel.LIFECYCLE_NORMAL);
        for (SysDictionary sd : list2) {
            if (sd.getDicLabel().equals(command.getSkuInvAttr2())) {
                invSkuCmd.setInvAttr2(sd.getDicValue());
                break;
            }
        }
        List<SysDictionary> list3 = sysDictionaryManager.getListByGroup(Constants.INVENTORY_ATTR_3, BaseModel.LIFECYCLE_NORMAL);
        for (SysDictionary sd : list3) {
            if (sd.getDicLabel().equals(command.getSkuInvAttr3())) {
                invSkuCmd.setInvAttr3(sd.getDicValue());
                break;
            }
        }
        List<SysDictionary> list4 = sysDictionaryManager.getListByGroup(Constants.INVENTORY_ATTR_4, BaseModel.LIFECYCLE_NORMAL);
        for (SysDictionary sd : list4) {
            if (sd.getDicLabel().equals(command.getSkuInvAttr4())) {
                invSkuCmd.setInvAttr4(sd.getDicValue());
                break;
            }
        }
        List<SysDictionary> list5 = sysDictionaryManager.getListByGroup(Constants.INVENTORY_ATTR_5, BaseModel.LIFECYCLE_NORMAL);
        for (SysDictionary sd : list5) {
            if (sd.getDicLabel().equals(command.getSkuInvAttr5())) {
                invSkuCmd.setInvAttr5(sd.getDicValue());
                break;
            }
        }
      Boolean isTipSkuSn = command.getIsNeedScanSkuSn();
      Boolean isTipSkuDefect = command.getIsNeedScanSkuDefect();
      String skuAttrId = null;
      String skuAttrIdNoSn = null;
      boolean isSnLine = false;
      if ((null != isTipSkuSn && true == isTipSkuSn) || (null != isTipSkuDefect && true == isTipSkuDefect)) {
          skuAttrIdNoSn = SkuCategoryProvider.getSkuAttrIdByInv(invSkuCmd); // 没有sn/残次信息
          skuAttrId = SkuCategoryProvider.concatSkuAttrId(skuAttrIdNoSn,command.getSkuSn(),command.getSkuDefect());
          isSnLine = true;
      } else {
          skuAttrIdNoSn = SkuCategoryProvider.getSkuAttrIdByInv(invSkuCmd); // 没有sn/残次信息
          isSnLine = false;
      }
      if(isSnLine){ //缓存扫描的sn/残次信息
          String sn = SkuCategoryProvider.concatSkuAttrId(command.getSkuSn(),command.getSkuDefect());
          this.cahceSkuSn(sn, locationId, turnoverBoxId, skuId);
      }
      //缓存sku 信息
       this.cacheSku(skuAttrId, skuAttrIdNoSn, skuId, locationId, turnoverBoxId, isSnLine);
        Map<Long, Integer> cacheSkuIdsQty = skuRedisManager.findSkuByBarCode(skuBarCode, logId); // 获取对应的商品数量,key值是sku
        OperationExecStatisticsCommand opExecLineCmd = cacheManager.getObject(CacheConstants.OPERATIONEXEC_STATISTICS + operationId.toString());
        if(null == opExecLineCmd){
            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
        }
        String key = locationId.toString()+turnoverBoxId;
        List<Long> locationIds = opExecLineCmd.getLocationIds();
        Map<String, Map<Long, Long>> skuQty = opExecLineCmd.getSkuQty();
        Map<String, Map<String, Set<String>>> locSkuAttrIdsSnDefect = opExecLineCmd.getSkuAttrIdsSnDefect();
        Map<String, Map<Long, Map<String, Long>>> locSkuAttrIdsQty = opExecLineCmd.getSkuAttrIds();
        Map<Long,Set<Long>> locTurnoverBoxIds = opExecLineCmd.getTurnoverBoxIds();
        Map<String,Set<Long>> locSkuIds = opExecLineCmd.getSkuIds();
        Set<Long> turnoverBoxIds = locTurnoverBoxIds.get(locationId);
        Set<Long> skuIds = locSkuIds.get(key);
        Map<Long, Map<String, Long>> skuAttrIdsQty = locSkuAttrIdsQty.get(key);
        Map<String, Set<String>> skuAttrIdsSnDefect = locSkuAttrIdsSnDefect.get(key);
        Map<Long, Long> turnoverBoxQty = skuQty.get(key);
        boolean isSkuExists = false;
        Integer cacheSkuQty = 1;
        Integer icSkuQty = 1;
        for (Long cacheId : cacheSkuIdsQty.keySet()) {
            if (skuIds.contains(cacheId)) {
                isSkuExists = true;
            }
            if (true == isSkuExists) {
                skuId = cacheId;
                cacheSkuQty = (null == cacheSkuIdsQty.get(cacheId) ? 1 : cacheSkuIdsQty.get(cacheId));
                icSkuQty = (null == turnoverBoxQty.get(cacheId) ? 1 : turnoverBoxQty.get(cacheId).intValue());
                break;
            }
        }
        if (false == isSkuExists) {
            log.error("scan sku is not found in current inside contianer error, ocId is:[{}], icId is:[{}], scanSkuId is:[{}], logId is:[{}]", skuId, logId);
            throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_SCAN_SKU_ERROR, new Object[] {});
        }
        if (cacheSkuQty > 1 && cacheSkuQty <= icSkuQty) {
            if (0 != (icSkuQty % cacheSkuQty)) {
                // 取模运算不为零，表示多条码配置的数量无法完成此箱中该商品的数量复合
                log.error("scan sku may be multi barcode sku, cacheSkuQty is:[{}], icSkuQty is:[{}], logId is:[{}]", cacheSkuQty, icSkuQty, logId);
                throw new BusinessException(ErrorCodes.COMMON_MULTI_BARCODE_QTY_NOT_SUITABLE);
            }
        }
        if (WhScanPatternType.ONE_BY_ONE_SCAN == scanPattern) {
            if (0 != new Double("1").compareTo(scanQty)) {
                log.error("one by one scan qty is not equals 1 error, skuBarcode is:[{}], logId is:[{}]", skuBarCode, logId);
                throw new BusinessException(ErrorCodes.COMMON_ONE_BY_ONE_SCAN_QTY_ERROR);
            }
        }
        skuCmd.setId(skuId);
        skuCmd.setScanSkuQty(scanQty * cacheSkuQty);// 可能是多条码
        skuCmd.setIsNeedTipSkuDefect(command.getIsNeedScanSkuDefect());
        skuCmd.setIsNeedTipSkuSn(command.getIsNeedScanSkuSn());
        CheckScanResultCommand csrCmd = pdaReplenishmentPutawayCacheManager.pdaReplenishPutWayTipSkuTurnoverBox(locationIds,skuId,skuCmd.getScanSkuQty(),skuAttrId, skuAttrIdNoSn,isSnLine, operationId, turnoverBoxId, turnoverBoxIds, skuIds, skuAttrIdsQty, skuAttrIdsSnDefect, locationId);
        if(csrCmd.getIsContinueScanSn()){
            command.setIsContinueScanSn(true);
            String skuAttrIds = csrCmd.getTipSkuAttrId(); // 提示唯一的sku包含唯一sku
            Long skuId1 = SkuCategoryProvider.getSkuId(skuAttrId);
            Map<String, Long> skuAttrIdsQty1 = skuAttrIdsQty.get(skuId1);
            WhSkuCommand whSkuCmd = whSkuDao.findWhSkuByIdExt(SkuCategoryProvider.getSkuId(skuAttrId), ouId);
            if (null == whSkuCmd) {
                throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
            }
            command.setTipSkuBarCode(whSkuCmd.getBarCode());
            // command.setIsNeedTipSku(true);
            command.setSkuId(skuId);
            this.tipSkuDetailAspect(command, skuAttrIds, skuAttrIdsQty1, logId);
        }else if(csrCmd.getIsNeedScanSku()){
            List<String> list = cacheManager.getObject(CacheConstants.SCAN_SKU_QUEUE_SN + locationId.toString()+turnoverBoxId.toString()+skuId.toString());
            whSkuInventoryManager.replenishmentSplitContainerPutaway(list,skuCmd.getScanSkuQty(), skuAttrIdNoSn, locationId, operationId, ouId, isTabbInvTotal, userId, workCode, turnoverBoxId, newTurnoverBoxId);
            String skuAttrIds = csrCmd.getTipSkuAttrId(); // 提示唯一的sku包含唯一sku
            // 周转箱内部所有sku扫描完毕在缓存
            pdaReplenishmentPutawayCacheManager.pdaReplenishPutwayCacheTurnoverBox(operationId, turnoverBoxId,locationId,ouId,false);
            Long skuId1 = SkuCategoryProvider.getSkuId(skuAttrIds);
            Map<String, Long> skuAttrIdsQty1 = skuAttrIdsQty.get(skuId1);
            WhSkuCommand whSkuCmd = whSkuDao.findWhSkuByIdExt(SkuCategoryProvider.getSkuId(skuAttrIds), ouId);
            if (null == whSkuCmd) {
                throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
            }
            command.setTipSkuBarCode(whSkuCmd.getBarCode());
            command.setIsNeedScanSku(true);
            command.setSkuId(skuId);
            this.tipSkuDetailAspect(command, skuAttrIds, skuAttrIdsQty1, logId);
            //删除缓存的sn
            cacheManager.remove(CacheConstants.SCAN_SKU_QUEUE_SN +locationId.toString()+ turnoverBoxId.toString() + skuId.toString());
        }else if(csrCmd.getIsNeedTipInsideContainer()){
            List<String> list = cacheManager.getObject(CacheConstants.SCAN_SKU_QUEUE_SN + locationId.toString()+turnoverBoxId.toString()+skuId.toString());
            whSkuInventoryManager.replenishmentSplitContainerPutaway(list,skuCmd.getScanSkuQty(), skuAttrIdNoSn, locationId, operationId, ouId, isTabbInvTotal, userId, workCode, turnoverBoxId, newTurnoverBoxId);
            // 周转箱内部所有sku扫描完毕在缓存
            pdaReplenishmentPutawayCacheManager.pdaReplenishPutwayCacheTurnoverBox(operationId, turnoverBoxId,locationId,ouId,false);
            pdaReplenishmentPutawayCacheManager.pdaReplenishPutwayRemoveAllCache(operationId,turnoverBoxId, locationId,false);
            //提示一下个周转箱
            Long tipTurnoverBoxId = csrCmd.getTipiInsideContainerId();
            Container c = containerDao.findByIdExt(tipTurnoverBoxId, ouId);
            if(null == c){
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
            }
            command.setIsNeedScanTurnoverBox(true);
            command.setTurnoverBoxCode(c.getCode());
        }else if(csrCmd.getIsNeedTipLoc()){
            Long tipLoctionid = csrCmd.getTipLocationId();
            Location loc = whLocationDao.findByIdExt(tipLoctionid, ouId);
            command.setTipLocationBarCode(loc.getBarCode());
            command.setTipLocationCode(loc.getCode());
            command.setIsNeedScanLocation(true);
            command.setLocationId(tipLoctionid);
            List<String> list = cacheManager.getObject(CacheConstants.SCAN_SKU_QUEUE_SN + locationId.toString()+turnoverBoxId.toString()+skuId.toString());
            whSkuInventoryManager.replenishmentSplitContainerPutaway(list,skuCmd.getScanSkuQty(), skuAttrIdNoSn, locationId, operationId, ouId, isTabbInvTotal, userId, workCode, turnoverBoxId, newTurnoverBoxId);
            // 周转箱内部所有sku扫描完毕在缓存
            pdaReplenishmentPutawayCacheManager.pdaReplenishPutwayCacheTurnoverBox(operationId, turnoverBoxId,locationId,ouId,false);
            //判断当前库位是否有拣货工作
            this.judeLocationIsPicking(turnoverBoxId, locationId, ouId, userId);
            pdaReplenishmentPutawayCacheManager.pdaReplenishPutwayRemoveAllCache(operationId,turnoverBoxId, locationId,false);
        }else if(csrCmd.getIsPutaway()){
            command.setIsScanFinsh(true);
            List<String> list = cacheManager.getObject(CacheConstants.SCAN_SKU_QUEUE_SN + locationId.toString()+turnoverBoxId.toString()+skuId.toString());
            whSkuInventoryManager.replenishmentSplitContainerPutaway(list,skuCmd.getScanSkuQty(), skuAttrIdNoSn, locationId, operationId, ouId, isTabbInvTotal, userId, workCode, turnoverBoxId, newTurnoverBoxId);
            this.updateStatus(operationId, workCode, ouId, userId);
           //判断当前库位是否有拣货工作
            this.judeLocationIsPicking(turnoverBoxId, locationId, ouId, userId);
             //清除所有缓存
            pdaReplenishmentPutawayCacheManager.pdaReplenishPutwayRemoveAllCache(operationId, turnoverBoxId, locationId,true);
        }
        return command;
    }
    
    
    private void cahceSkuSn(String sn,Long locationId,Long turnoverBoxId,Long skuId){
        List<String> list = cacheManager.getObject(CacheConstants.SCAN_SKU_QUEUE_SN + locationId.toString()+turnoverBoxId.toString()+skuId.toString());
        if(null == list) {
            list = new ArrayList<String>();
            list.add(sn);
        }else{
            if(!list.contains(sn)){
                list.add(sn);
            }else{
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_SN_DOUBLE_ERROR);
            }
        }
        cacheManager.setObject(CacheConstants.SCAN_SKU_QUEUE_SN + locationId.toString()+turnoverBoxId.toString()+skuId.toString(), list, CacheConstants.CACHE_ONE_DAY);
    }
    private void cacheSku(String skuAttrIdSn,String skuAttrIdNoSn,Long skuId,Long locationId,Long turnoverBoxId,boolean isSnLine){
        ReplenishScanTipSkuCacheCommand  scanTipSkuCmd = cacheManager.getObject(CacheConstants.PDA_REPLENISH_PUTAWAY_SCAN_SKU + locationId.toString()+turnoverBoxId.toString());
        if(null == scanTipSkuCmd){
            scanTipSkuCmd = new ReplenishScanTipSkuCacheCommand();
            ArrayDeque<Long> scanSkuIds = new ArrayDeque<Long>();
            scanSkuIds.addFirst(skuId);
            // 已复合唯一商品列表(不包含sn)
            ArrayDeque<String> scanSkuAttrIds = new ArrayDeque<String>();
            scanSkuAttrIds.addFirst(skuAttrIdNoSn);
            // 已复合唯一商品列表(包含sn,残次信息) 
            ArrayDeque<String> scanSkuAttrIdSn = new ArrayDeque<String>();
            if(isSnLine){
                scanSkuAttrIdSn.addFirst(skuAttrIdSn);
            }
            scanTipSkuCmd.setScanSkuAttrIds(scanSkuAttrIds);
            scanTipSkuCmd.setScanSkuAttrIdSn(scanSkuAttrIdSn);
            scanTipSkuCmd.setScanSkuIds(scanSkuIds);
        }else{
            ArrayDeque<Long> scanSkuIds = scanTipSkuCmd.getScanSkuIds();
            if(null == scanSkuIds) {
                scanSkuIds = new ArrayDeque<Long>();
                scanSkuIds.addFirst(skuId);
                scanTipSkuCmd.setScanSkuIds(scanSkuIds);
            }else{
                if(!scanSkuIds.contains(skuId)){
                    scanSkuIds.addFirst(skuId);
                    scanTipSkuCmd.setScanSkuIds(scanSkuIds);
                }
            }
            // 已复合唯一商品列表(不包含sn)
            ArrayDeque<String> scanSkuAttrIds = scanTipSkuCmd.getScanSkuAttrIds();
            if(null == scanSkuAttrIds) {
                scanSkuAttrIds = new ArrayDeque<String>();
                scanSkuAttrIds.addFirst(skuAttrIdNoSn);
                scanTipSkuCmd.setScanSkuAttrIds(scanSkuAttrIds);
            }else{
                if(!scanSkuAttrIds.contains(skuAttrIdNoSn)){
                    scanSkuAttrIds.addFirst(skuAttrIdNoSn);
                    scanTipSkuCmd.setScanSkuAttrIds(scanSkuAttrIds); 
                }
            }
            if(isSnLine){
                // 已复合唯一商品列表(包含sn,残次信息) 
                ArrayDeque<String> scanSkuAttrIdSn = scanTipSkuCmd.getScanSkuAttrIdSn();
                if(null == scanSkuAttrIdSn) {
                    scanSkuAttrIdSn = new ArrayDeque<String>();
                    scanSkuAttrIdSn.addFirst(skuAttrIdSn);
                    scanTipSkuCmd.setScanSkuAttrIdSn(scanSkuAttrIdSn);
                }else{
                    if(!scanSkuAttrIdSn.contains(scanSkuAttrIdSn)){
                        scanSkuAttrIdSn.addFirst(skuAttrIdSn);
                        scanTipSkuCmd.setScanSkuAttrIdSn(scanSkuAttrIdSn); 
                    }
                }
            }
        }
        cacheManager.setObject(CacheConstants.PDA_REPLENISH_PUTAWAY_SCAN_SKU + locationId.toString()+turnoverBoxId.toString(), scanTipSkuCmd, CacheConstants.CACHE_ONE_DAY);
        
    }
    
    private void judeLocationIsPicking(Long turnoverBoxId,Long locationId,Long ouId,Long userId){
            //判断目标库位上是否有拣货工作
            //更新到工作明细
            List<WhWorkLineCommand> workLineList = whWorkLineDao.findWorkLineByLocationId(locationId, ouId);
            if(null != workLineList && workLineList.size() != 0) {
                for(WhWorkLineCommand workLineCmd:workLineList) {
                    String workSkuAttrId = SkuCategoryProvider.getSkuAttrIdByWhWorkLineCommand(workLineCmd);
                    List<WhSkuInventoryCommand> skuInvCmdList = whSkuInventoryDao.findReplenishmentBylocationId(turnoverBoxId,ouId, locationId);
                    Double sum = 0.0;
                    for(WhSkuInventoryCommand invCmd:skuInvCmdList) {
                           Long insideId = invCmd.getInsideContainerId();
                           String skuAttrId = SkuCategoryProvider.getSkuAttrIdByInv(invCmd);
                           if(workSkuAttrId.equals(skuAttrId)) {
                                 Double lineQty = workLineCmd.getQty();
                                 if(workSkuAttrId.equals(skuAttrId)) {
                                     Double onHandQty = invCmd.getOnHandQty();
                                     if(lineQty.doubleValue() > onHandQty.doubleValue()) {
                                         sum += onHandQty;
                                         WhWorkLine workLine = new WhWorkLine();
                                         BeanUtils.copyProperties(workLineCmd, workLine);
                                         workLine.setFromInsideContainerId(insideId);
                                         if(lineQty.doubleValue() > sum.doubleValue()){
                                             workLine.setQty(onHandQty);
                                         }
                                         if(lineQty.doubleValue() == sum.doubleValue()){
                                             workLine.setQty(onHandQty);                                   
                                         }
                                         if(lineQty.doubleValue() < sum.doubleValue()){
                                             Double qty1 = lineQty.doubleValue() -(sum.doubleValue() -onHandQty.doubleValue());
                                             workLine.setQty(qty1);
                                         }
                                         whWorkLineDao.saveOrUpdateByVersion(workLine);
                                         insertGlobalLog(GLOBAL_LOG_UPDATE, workLine, ouId, userId, null, null);
                                         continue;
                                  }
                                  if(lineQty.doubleValue() == onHandQty.doubleValue()){
                                         WhWorkLine workLine = new WhWorkLine();
                                         BeanUtils.copyProperties(workLineCmd, workLine);
                                         workLine.setFromInsideContainerId(insideId);
                                         workLine.setQty(onHandQty);
                                         whWorkLineDao.saveOrUpdateByVersion(workLine);
                                         insertGlobalLog(GLOBAL_LOG_UPDATE, workLine, ouId, userId, null, null);
                                  }
                                  if(lineQty.doubleValue() < onHandQty.doubleValue()){
                                         WhWorkLine workLine = new WhWorkLine();
                                         BeanUtils.copyProperties(workLineCmd, workLine);
                                         workLine.setFromInsideContainerId(insideId);
                                         workLine.setQty(lineQty);
                                         whWorkLineDao.saveOrUpdateByVersion(workLine);
                                         insertGlobalLog(GLOBAL_LOG_UPDATE, workLine, ouId, userId, null, null);
                                 }
                                 }
                           }
                    }
              }
            }
            //先添加作业明细,后删除原始作业明细
            List<WhOperationLineCommand> operLineCmdList = whOperationLineDao.findOperationLineByLocationId(ouId, locationId);
            if(null != operLineCmdList && operLineCmdList.size() != 0) {
                //库位上有拣货工作
                for(WhOperationLineCommand operLineCmd:operLineCmdList){
                    Double lineQty = operLineCmd.getQty();
                    String workSkuAttrId = SkuCategoryProvider.getSkuAttrIdByOperationLine(operLineCmd);
                    List<WhSkuInventoryCommand> skuInvCmdList = whSkuInventoryDao.findReplenishmentBylocationId(turnoverBoxId,ouId, locationId);
                    if(null != skuInvCmdList && skuInvCmdList.size() != 0) {
                        Double sum = 0.0;
                        for(WhSkuInventoryCommand invCmd:skuInvCmdList) {
                            Long insideId = invCmd.getInsideContainerId();
                            String skuAttrId = SkuCategoryProvider.getSkuAttrIdByInv(invCmd);
                            if(workSkuAttrId.equals(skuAttrId)) {
                                Double onHandQty = invCmd.getOnHandQty();
                                if(lineQty.doubleValue() > onHandQty.doubleValue()) {
                                    sum += onHandQty;
                                    WhOperationLine opLine = new WhOperationLine();
                                    BeanUtils.copyProperties(operLineCmd, opLine);
                                    opLine.setFromInsideContainerId(insideId);
                                    if(lineQty.doubleValue() > sum.doubleValue()){
                                        opLine.setQty(onHandQty);
                                    }
                                    if(lineQty.doubleValue() == sum.doubleValue()){
                                        opLine.setQty(onHandQty);                                   
                                    }
                                    if(lineQty.doubleValue() < sum.doubleValue()){
                                        Double qty1 = lineQty.doubleValue() -(sum.doubleValue() -onHandQty.doubleValue());
                                        opLine.setQty(qty1);
                                    }
                                    whOperationLineDao.saveOrUpdateByVersion(opLine);
                                    insertGlobalLog(GLOBAL_LOG_UPDATE, opLine, ouId, userId, null, null);
                                    continue;
                             }
                             if(lineQty.doubleValue() == onHandQty.doubleValue()){
                                    WhOperationLine opLine = new WhOperationLine();
                                    BeanUtils.copyProperties(operLineCmd, opLine);
                                    opLine.setFromInsideContainerId(insideId);
                                    opLine.setQty(onHandQty);
                                    whOperationLineDao.saveOrUpdateByVersion(opLine);
                                    insertGlobalLog(GLOBAL_LOG_UPDATE, opLine, ouId, userId, null, null);
                             }
                             if(lineQty.doubleValue() < onHandQty.doubleValue()){
                                    WhOperationLine opLine = new WhOperationLine();
                                    BeanUtils.copyProperties(operLineCmd, opLine);
                                    opLine.setFromInsideContainerId(insideId);
                                    opLine.setQty(lineQty);
                                    whOperationLineDao.saveOrUpdateByVersion(opLine);
                                    insertGlobalLog(GLOBAL_LOG_UPDATE, opLine, ouId, userId, null, null);
                            }
                            }
                     }
                    }
                }
            }
    }
    @Override
    public void updateStatus(Long operationId, String workCode,Long ouId,Long userId) {
        // TODO Auto-generated method stub
        log.info("PdaReplenishmentPutawayManagerImpl updateStatus is start");
        WhOperation whOperation = whOperationDao.findOperationByIdExt(operationId,ouId);
        if(null == whOperation) {
            log.error("whOperation id is not normal, operationId is:[{}]", operationId);
            throw new BusinessException(ErrorCodes.OPATION_NO_EXIST);
        }
        whOperation.setStatus(WorkStatus.FINISH);
        whOperation.setModifiedId(userId);
        whOperationDao.saveOrUpdateByVersion(whOperation);
        insertGlobalLog(GLOBAL_LOG_UPDATE, whOperation, ouId, userId, null, null);
        WhWorkCommand workCmd = whWorkDao.findWorkByWorkCode(workCode, ouId);
        if(null == workCmd) {
            log.error("whOperation id is not normal, operationId is:[{}]", operationId);
            throw new BusinessException(ErrorCodes.WORK_NO_EXIST);
        }
        WhWork work = new WhWork();
        BeanUtils.copyProperties(workCmd, work);
        work.setStatus(WorkStatus.FINISH);
        whWorkDao.saveOrUpdateByVersion(work);
        insertGlobalLog(GLOBAL_LOG_UPDATE, work, ouId, userId, null, null);
        log.info("PdaReplenishmentPutawayManagerImpl updateStatus is end");
        
    }
    
    /**
     * 统计分析作业执行明细
     * 
     * @author qiming.liu
     * @param ReplenishmentPutawayCommand
     * @return
     */
    @Override
    public OperationExecStatisticsCommand getOperationExecForGroup(ReplenishmentPutawayCommand replenishmentPutawayCommand) {
        
        // 所有托盘
        Set<Long> pallets = new HashSet<Long>();
        // 所有货箱
        Set<Long> containers = new HashSet<Long>();
        // 所有目标库位
        Set<Long> locationIds = new HashSet<Long>();
        // 目标库位对应的所有周转箱        
        Map<Long, Set<Long>> turnoverBoxIds = new HashMap<Long, Set<Long>>();
        // 周转箱对应的所有sku 
        Map<String, Set<Long>> skuIds = new HashMap<String, Set<Long>>();
        // 周转箱每个sku总件数
        Map<String, Map<Long, Long>> skuQty = new HashMap<String, Map<Long, Long>>();
        // 周转箱每个sku对应的唯一sku及件数
        Map<String, Map<Long, Map<String, Long>>> skuAttrIds = new HashMap<String, Map<Long, Map<String, Long>>>();
        // 周转箱每个唯一sku对应的所有sn及残次条码
        Map<String, Map<String, Set<String>>> skuAttrIdsSnDefect = new HashMap<String, Map<String, Set<String>>>();
        // 目标库位对应的所有外部容器（整托整箱）
        Map<Long, Set<Long>> outerContainerIds = new HashMap<Long, Set<Long>>();
        // 外部容器对应所有内部容器（整托整箱）
        Map<Long, Set<Long>> outerToInside = new HashMap<Long, Set<Long>>();
        // 内部容器对应所有sku（整托整箱）
        Map<String, Set<Long>> insideSkuIds = new HashMap<String, Set<Long>>();
        // 内部容器每个sku总件数（整托整箱）
        Map<String, Map<Long, Long>> insideSkuQty = new HashMap<String, Map<Long, Long>>();
        // 内部容器每个sku对应的唯一sku及件数（整托整箱）
        Map<String, Map<Long, Map<String, Long>>> insideSkuAttrIds = new HashMap<String, Map<Long, Map<String, Long>>>();
        // 内部容器每个唯一sku对应的所有sn及残次条码
        Map<String, Map<String, Set<String>>> insideSkuAttrIdsSnDefect = new HashMap<String, Map<String, Set<String>>>();
        
        // 根据作业id获取作业信息        
        WhOperationCommand whOperationCommand = whOperationManager.findOperationById(replenishmentPutawayCommand.getOperationId(), replenishmentPutawayCommand.getOuId());
        //根据作业id获取作业明细信息  
        List<WhOperationExecLine> operationExecLineList = whOperationExecLineDao.getOperationExecLineLst(replenishmentPutawayCommand.getOperationId(), replenishmentPutawayCommand.getOuId(), false);
        for(WhOperationExecLine operationExecLine : operationExecLineList){
            // 计划量减执行量
            int lineQty = operationExecLine.getQty().compareTo(operationExecLine.getCompleteQty());
            // 如果计划量减执行量等于0，跳出循环
            if (0 == lineQty) {
                continue;
            }
            //获取内部容器唯一sku
            String onlySku = SkuCategoryProvider.getSkuAttrIdByOperationExecLine(operationExecLine);
            //根据库存UUID查找对应SN/残次信息
            List<WhSkuInventorySnCommand> skuInventorySnCommands = whSkuInventorySnDao.findWhSkuInventoryByUuid(whOperationCommand.getOuId(), operationExecLine.getUuid());
            //获取库位ID 
            locationIds.add(operationExecLine.getToLocationId());
            if(whOperationCommand.getIsWholeCase() == false){
                // 获取目标库位对应的所有周转箱
                if(null != turnoverBoxIds.get(operationExecLine.getToLocationId())){
                    turnoverBoxIds.get(operationExecLine.getToLocationId()).add(operationExecLine.getUseContainerId());
                }else{
                    Set<Long> useContainerIdSet = new HashSet<Long>();
                    useContainerIdSet.add(operationExecLine.getUseContainerId());
                    turnoverBoxIds.put(operationExecLine.getToLocationId(), useContainerIdSet);
                }
                String toLocationAndUseContainer = operationExecLine.getToLocationId().toString() + operationExecLine.getUseContainerId().toString();
                // 获取周转箱对应的所有sku
                if(null != skuIds.get(operationExecLine.getUseContainerId())){
                    skuIds.get(operationExecLine.getUseContainerId()).add(operationExecLine.getSkuId());
                }else{
                    Set<Long> skuIdSet = new HashSet<Long>();
                    skuIdSet.add(operationExecLine.getSkuId());
                    skuIds.put(toLocationAndUseContainer, skuIdSet);
                }
                // 获取周转箱每个sku总件数
                if(null != skuQty.get(operationExecLine.getUseContainerId())){
                    Map<Long, Long> useContainerIdAndSkuQtyMap = new HashMap<Long, Long>();
                    useContainerIdAndSkuQtyMap = skuQty.get(operationExecLine.getUseContainerId());
                    if(null != useContainerIdAndSkuQtyMap.get(operationExecLine.getSkuId())){
                        Long qty =  useContainerIdAndSkuQtyMap.get(operationExecLine.getSkuId()) + (long) (operationExecLine.getQty() - operationExecLine.getCompleteQty());
                        useContainerIdAndSkuQtyMap.put(operationExecLine.getSkuId(), qty);
                    }else{
                        useContainerIdAndSkuQtyMap.put(operationExecLine.getSkuId(), (long) (operationExecLine.getQty() - operationExecLine.getCompleteQty()));
                    }
                    skuQty.put(toLocationAndUseContainer, useContainerIdAndSkuQtyMap);
                }else{
                    Map<Long, Long> useContainerIdAndSkuQtyMap = new HashMap<Long, Long>();
                    useContainerIdAndSkuQtyMap.put(operationExecLine.getSkuId(), (long) (operationExecLine.getQty() - operationExecLine.getCompleteQty()));
                    skuQty.put(toLocationAndUseContainer, useContainerIdAndSkuQtyMap);
                }
                // 周转箱每个sku对应的唯一sku及件数
                if(null != skuAttrIds.get(operationExecLine.getUseContainerId())){
                    Map<Long, Map<String, Long>> useContainerIdAndOnlySku  = new HashMap<Long, Map<String, Long>>();
                    useContainerIdAndOnlySku = skuAttrIds.get(operationExecLine.getUseContainerId());
                    if(null != useContainerIdAndOnlySku.get(operationExecLine.getSkuId())){
                        Map<String, Long> skuAttrIdsQty = useContainerIdAndOnlySku.get(operationExecLine.getSkuId());
                        if (null != skuAttrIdsQty.get(onlySku)) {
                            skuAttrIdsQty.put(onlySku, skuAttrIdsQty.get(onlySku) + (long) (operationExecLine.getQty() - operationExecLine.getCompleteQty()));
                        } else {
                            skuAttrIdsQty.put(onlySku, (long) (operationExecLine.getQty() - operationExecLine.getCompleteQty()));
                        }
                        useContainerIdAndOnlySku.put(operationExecLine.getSkuId(), skuAttrIdsQty);
                     }else{
                         Map<String, Long> insideSkuAttrIdsQty = new HashMap<String, Long>();
                         insideSkuAttrIdsQty.put(onlySku, (long) (operationExecLine.getQty() - operationExecLine.getCompleteQty()));
                         useContainerIdAndOnlySku.put(operationExecLine.getSkuId(), insideSkuAttrIdsQty);
                     }
                    skuAttrIds.put(toLocationAndUseContainer, useContainerIdAndOnlySku);
                }else{
                    Map<Long, Map<String, Long>> useContainerIdAndOnlySku  = new HashMap<Long, Map<String, Long>>();
                    Map<String, Long> skuAttrIdsQty = new HashMap<String, Long>();
                    skuAttrIdsQty.put(onlySku, (long) (operationExecLine.getQty() - operationExecLine.getCompleteQty()));
                    useContainerIdAndOnlySku.put(operationExecLine.getSkuId(), skuAttrIdsQty);
                    skuAttrIds.put(toLocationAndUseContainer, useContainerIdAndOnlySku);
                }
                // 周转箱每个唯一sku对应的所有sn及残次条码
                if(null != skuAttrIdsSnDefect.get(operationExecLine.getUseContainerId())){
                    Map<String, Set<String>> onlySkuAndSn = new HashMap<String, Set<String>>();
                    Set<String> snSet = new HashSet<String>();
                    onlySkuAndSn = skuAttrIdsSnDefect.get(operationExecLine.getUseContainerId());
                    if(null != onlySkuAndSn.get(onlySku)){
                        snSet = onlySkuAndSn.get(onlySku);
                        for (int i = 0; i < operationExecLine.getQty(); i++) {
                            if(null != skuInventorySnCommands && i < skuInventorySnCommands.size() && null != skuInventorySnCommands.get(i)){
                                snSet.add(SkuCategoryProvider.concatSkuAttrId(skuInventorySnCommands.get(i).getSn(), skuInventorySnCommands.get(i).getDefectWareBarcode()));    
                            }
                        }
                        onlySkuAndSn.put(onlySku, snSet);
                    }else{
                        for (int i = 0; i < operationExecLine.getQty(); i++) {
                            if(null != skuInventorySnCommands && i < skuInventorySnCommands.size() && null != skuInventorySnCommands.get(i)){
                                snSet.add(SkuCategoryProvider.concatSkuAttrId(skuInventorySnCommands.get(i).getSn(), skuInventorySnCommands.get(i).getDefectWareBarcode()));    
                            }
                        }
                        onlySkuAndSn.put(onlySku, snSet);
                    }
                    skuAttrIdsSnDefect.put(toLocationAndUseContainer, onlySkuAndSn);
                }else{
                    Map<String, Set<String>> onlySkuAndSn = new HashMap<String, Set<String>>();
                    Set<String> snSet = new HashSet<String>();
                    for (int i = 0; i < operationExecLine.getQty(); i++) {
                        if(null != skuInventorySnCommands && i < skuInventorySnCommands.size() && null != skuInventorySnCommands.get(i)){
                            snSet.add(SkuCategoryProvider.concatSkuAttrId(skuInventorySnCommands.get(i).getSn(), skuInventorySnCommands.get(i).getDefectWareBarcode()));    
                        }
                    }
                    onlySkuAndSn.put(onlySku, snSet);
                    skuAttrIdsSnDefect.put(toLocationAndUseContainer, onlySkuAndSn);
                }
            }else{
                // 所有托盘
                pallets.add(operationExecLine.getUseOuterContainerId());
                // 所有货箱
                containers.add(operationExecLine.getUseContainerId());
                // 目标库位对应的所有外部容器（整托整箱）
                if(null != outerContainerIds.get(operationExecLine.getToLocationId())){
                    outerContainerIds.get(operationExecLine.getToLocationId()).add(operationExecLine.getUseOuterContainerId());
                }else{
                    Set<Long> useOuterContainerIdSet = new HashSet<Long>();
                    useOuterContainerIdSet.add(operationExecLine.getUseOuterContainerId());
                    outerContainerIds.put(operationExecLine.getToLocationId(), useOuterContainerIdSet);
                }
                // 外部容器对应所有内部容器（整托整箱）
                if(null != outerToInside.get(operationExecLine.getUseOuterContainerId())){
                    outerToInside.get(operationExecLine.getUseOuterContainerId()).add(operationExecLine.getUseContainerId());
                }else{
                    Set<Long> useContainerIdSet = new HashSet<Long>();
                    useContainerIdSet.add(operationExecLine.getUseContainerId());
                    outerToInside.put(operationExecLine.getUseOuterContainerId(), useContainerIdSet);
                }
                String toLocationAndUseContainer = operationExecLine.getToLocationId().toString() + operationExecLine.getUseContainerId().toString();
                // 内部容器对应所有sku（整托整箱）
                if(null != insideSkuIds.get(operationExecLine.getUseContainerId())){
                    insideSkuIds.get(operationExecLine.getUseContainerId()).add(operationExecLine.getSkuId());
                }else{
                    Set<Long> skuIdSet = new HashSet<Long>();
                    skuIdSet.add(operationExecLine.getSkuId());
                    insideSkuIds.put(toLocationAndUseContainer, skuIdSet);
                }
                // 内部容器每个sku总件数（整托整箱）
                if(null != insideSkuQty.get(operationExecLine.getUseContainerId())){
                    Map<Long, Long> skuIdAndQtyMap = new HashMap<Long, Long>();
                    skuIdAndQtyMap = insideSkuQty.get(operationExecLine.getUseContainerId());
                    if(null != skuIdAndQtyMap.get(operationExecLine.getSkuId())){
                        Long insQty =  skuIdAndQtyMap.get(operationExecLine.getSkuId()) + (long) (operationExecLine.getQty() - operationExecLine.getCompleteQty());
                        skuIdAndQtyMap.put(operationExecLine.getSkuId(), insQty);
                     }else{
                         skuIdAndQtyMap.put(operationExecLine.getSkuId(), (long) (operationExecLine.getQty() - operationExecLine.getCompleteQty()));
                     }
                    insideSkuQty.put(toLocationAndUseContainer, skuIdAndQtyMap);
                }else{
                    Map<Long, Long> skuIdAndQtyMap = new HashMap<Long, Long>();
                    skuIdAndQtyMap.put(operationExecLine.getSkuId(), (long) (operationExecLine.getQty() - operationExecLine.getCompleteQty()));
                    insideSkuQty.put(toLocationAndUseContainer, skuIdAndQtyMap);
                }
                // 内部容器每个sku对应的唯一sku及件数（整托整箱）
                if(null != insideSkuAttrIds.get(operationExecLine.getUseContainerId())){
                    Map<Long, Map<String, Long>> skuIdAndOnlySku = new HashMap<Long, Map<String, Long>>();
                    skuIdAndOnlySku = insideSkuAttrIds.get(operationExecLine.getUseContainerId());
                    if(null != skuIdAndOnlySku.get(operationExecLine.getSkuId())){
                        Map<String, Long> insideSkuAttrIdsQty = skuIdAndOnlySku.get(operationExecLine.getSkuId());
                        if (null != insideSkuAttrIdsQty.get(onlySku)) {
                            insideSkuAttrIdsQty.put(onlySku, insideSkuAttrIdsQty.get(onlySku) + (long) (operationExecLine.getQty() - operationExecLine.getCompleteQty()));
                        } else {
                            insideSkuAttrIdsQty.put(onlySku, (long) (operationExecLine.getQty() - operationExecLine.getCompleteQty()));
                        }
                        skuIdAndOnlySku.put(operationExecLine.getSkuId(), insideSkuAttrIdsQty);
                     }else{
                         Map<String, Long> insideSkuAttrIdsQty = new HashMap<String, Long>();
                         insideSkuAttrIdsQty.put(onlySku, (long) (operationExecLine.getQty() - operationExecLine.getCompleteQty()));
                         skuIdAndOnlySku.put(operationExecLine.getSkuId(), insideSkuAttrIdsQty);
                     }
                    insideSkuAttrIds.put(toLocationAndUseContainer, skuIdAndOnlySku);
                }else{
                    Map<Long, Map<String, Long>> skuIdAndOnlySku = new HashMap<Long, Map<String, Long>>();
                    Map<String, Long> insideSkuAttrIdsQty = new HashMap<String, Long>();
                    insideSkuAttrIdsQty.put(onlySku, (long) (operationExecLine.getQty() - operationExecLine.getCompleteQty()));
                    skuIdAndOnlySku.put(operationExecLine.getSkuId(), insideSkuAttrIdsQty);
                    insideSkuAttrIds.put(toLocationAndUseContainer, skuIdAndOnlySku);
                }
                // 内部容器每个唯一sku对应的所有sn及残次条码
                if(null != insideSkuAttrIdsSnDefect.get(operationExecLine.getUseContainerId())){
                    Map<String, Set<String>> onlySkuAndSnMap = new HashMap<String, Set<String>>();
                    onlySkuAndSnMap = insideSkuAttrIdsSnDefect.get(operationExecLine.getUseContainerId());
                    if(null != onlySkuAndSnMap.get(onlySku)){
                        Set<String> snSet = new HashSet<String>();
                        snSet = onlySkuAndSnMap.get(onlySku);
                        for (int i = 0; i < operationExecLine.getQty(); i++) {
                            if(null != skuInventorySnCommands && i < skuInventorySnCommands.size() && null != skuInventorySnCommands.get(i)){
                                snSet.add(SkuCategoryProvider.concatSkuAttrId(skuInventorySnCommands.get(i).getSn(), skuInventorySnCommands.get(i).getDefectWareBarcode()));    
                            }
                        }
                        onlySkuAndSnMap.put(onlySku, snSet);
                    }else{
                        Set<String> snSet = new HashSet<String>();
                        for (int i = 0; i < operationExecLine.getQty(); i++) {
                            if(null != skuInventorySnCommands && i < skuInventorySnCommands.size() && null != skuInventorySnCommands.get(i)){
                                snSet.add(SkuCategoryProvider.concatSkuAttrId(skuInventorySnCommands.get(i).getSn(), skuInventorySnCommands.get(i).getDefectWareBarcode()));    
                            }
                        }
                        onlySkuAndSnMap.put(onlySku, snSet);
                    }
                    insideSkuAttrIdsSnDefect.put(toLocationAndUseContainer, onlySkuAndSnMap);
                }else{
                    Map<String, Set<String>> onlySkuAndSnMap = new HashMap<String, Set<String>>();
                    Set<String> snSet = new HashSet<String>();
                    for (int i = 0; i < operationExecLine.getQty(); i++) {
                        if(null != skuInventorySnCommands && i < skuInventorySnCommands.size() && null != skuInventorySnCommands.get(i)){
                            snSet.add(SkuCategoryProvider.concatSkuAttrId(skuInventorySnCommands.get(i).getSn(), skuInventorySnCommands.get(i).getDefectWareBarcode()));    
                        }
                    }
                    onlySkuAndSnMap.put(onlySku, snSet);
                    insideSkuAttrIdsSnDefect.put(toLocationAndUseContainer, onlySkuAndSnMap);
                }
            }
        }
        
        //载入统计分析信息        
        OperationExecStatisticsCommand statisticsCommand = new OperationExecStatisticsCommand();
        // 是否整托整箱
        statisticsCommand.setIsWholeCase(whOperationCommand.getIsWholeCase());
        // 所有托盘
        statisticsCommand.setPallets(pallets);
        // 所有货箱
        statisticsCommand.setContainers(containers);
        // 库位排序
        List<Long> sortLocationIds = new ArrayList<Long>();
        sortLocationIds = locationManager.sortByIds(locationIds, whOperationCommand.getOuId());
        // 所有目标库位
        statisticsCommand.setLocationIds(sortLocationIds);
        // 目标库位对应的所有周转箱
        statisticsCommand.setTurnoverBoxIds(turnoverBoxIds);
        // 周转箱对应的所有sku
        statisticsCommand.setSkuIds(skuIds);
        // 周转箱每个sku总件数
        statisticsCommand.setSkuQty(skuQty);
        // 周转箱每个sku对应的唯一sku及件数
        statisticsCommand.setSkuAttrIds(skuAttrIds);
        // 周转箱每个唯一sku对应的所有sn及残次条码
        statisticsCommand.setSkuAttrIdsSnDefect(skuAttrIdsSnDefect);
        // 目标库位对应的所有外部容器（整托整箱）
        statisticsCommand.setOuterContainerIds(outerContainerIds);
        // 外部容器对应所有内部容器（整托整箱）
        statisticsCommand.setOuterToInside(outerToInside);
        // 内部容器对应所有sku（整托整箱）
        statisticsCommand.setInsideSkuIds(insideSkuIds);
        // 内部容器每个sku总件数（整托整箱）
        statisticsCommand.setInsideSkuQty(insideSkuQty);
        // 内部容器每个sku对应的唯一sku及件数（整托整箱）
        statisticsCommand.setInsideSkuAttrIds(insideSkuAttrIds);
        // 内部容器每个唯一sku对应的所有sn及残次条码
        statisticsCommand.setInsideSkuAttrIdsSnDefect(insideSkuAttrIdsSnDefect);
        //缓存统计分析结果        
        pdaPickingWorkCacheManager.operationExecStatisticsRedis(whOperationCommand.getId(), statisticsCommand);
        
        return statisticsCommand;
    }
    
//    /**
//     * 校验库位库存
//     * 
//     * @author qiming.liu
//     * @param ReplenishmentPutawayCommand
//     * @return
//     */
//    public void checkStock() {
//        whSkuInventoryManager.replenishmentPutaway(operationId, ouId, isTabbInvTotal, userId, workCode);
//        //更新工作及作业状态
//        this.updateStatus(operationId, workCode, ouId, userId);
//        //清除所有缓存
//        pdaReplenishmentPutawayCacheManager.pdaReplenishPutwayRemoveAllCache(operationId);
//    }
    
    /**
     * 获取补货功能参数
     * @param ouId
     * @param functionId
     * @return
     */
    public WhFunctionReplenishment findWhFunctionReplenishmentByfunctionId(Long ouId,Long functionId){
        WhFunctionReplenishment replenish = whFunctionReplenishmentDao.findByFunctionIdExt(ouId, functionId);
        if(null == replenish) {
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        return replenish;
    }
    
    /***
     * 补货上架取消
     * @param operationId
     */
    public void cancelPattern(Long operationId,Integer cancelPattern,Long locationId,Long turnoverBoxId){
        if(CancelPattern.PICKING_SCAN_LOC_CANCEL == cancelPattern){
            //清楚缓存计数器
            OperationExecStatisticsCommand opExecLineCmd = cacheManager.getObject(CacheConstants.OPERATIONEXEC_STATISTICS + operationId.toString());
            if(null == opExecLineCmd){
                throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
            }
            List<Long> locationIds = opExecLineCmd.getLocationIds();
            Map<Long, Set<Long>> locTurnoverBoxIds = opExecLineCmd.getTurnoverBoxIds();
            Map<String, Set<Long>> turnoverBoxSkuIds = opExecLineCmd.getSkuIds();
            for(Long locId:locationIds){
                Set<Long> turnoverBoxIds = locTurnoverBoxIds.get(locId);
                for(Long turnId:turnoverBoxIds){
                    String key = locationId.toString()+turnId;
                    Set<Long> skuIds = turnoverBoxSkuIds.get(key);
                    for(Long skuId:skuIds){
                        cacheManager.remove(CacheConstants.SCAN_SKU_QUEUE_SN_COUNT +locationId.toString()+ turnoverBoxId.toString() + skuId.toString());
                        cacheManager.remove(CacheConstants.SCAN_SKU_QUEUE + locationId.toString()+ turnoverBoxId.toString() + skuId.toString());
                        cacheManager.remove(CacheConstants.SCAN_SKU_QUEUE_SN + locationId.toString()+ turnoverBoxId.toString() + skuId.toString());
                    }
                    cacheManager.remove(CacheConstants.PDA_REPLENISH_PUTAWAY_SCAN_SKU + locationId.toString()+turnId.toString());
                }
            }
            cacheManager.remove(CacheConstants.CACHE_PUTAWAY_LOCATION+operationId.toString());
            cacheManager.remove(CacheConstants.OPERATIONEXEC_STATISTICS+operationId.toString());
        }else if(CancelPattern.PICKING_TIP_CAR_CANCEL == cancelPattern){
            String key = locationId.toString()+turnoverBoxId;
            //清楚缓存计数器
            OperationExecStatisticsCommand opExecLineCmd = cacheManager.getObject(CacheConstants.OPERATIONEXEC_STATISTICS + operationId.toString());
            if(null == opExecLineCmd){
                throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
            }
            Map<String, Set<Long>> turnoverBoxSkuIds = opExecLineCmd.getSkuIds();
            Set<Long> skuIds = turnoverBoxSkuIds.get(key);
            for(Long skuId:skuIds){
                cacheManager.remove( CacheConstants.SCAN_SKU_QUEUE_SN_COUNT +locationId.toString()+ turnoverBoxId.toString() + skuId.toString());
            }
        }
    }
    
    /***
     * 判断货箱内库存属性是否唯一
     * @param command
     * @return
     */
    public ReplenishmentPutawayCommand judgeSkuAttrIdsIsUnique(ReplenishmentPutawayCommand command) {
        log.info("PdaPickingWorkManagerImpl judgeSkuAttrIdsIsUnique is start");
        Long operationId = command.getOperationId();
        Long locationId = command.getLocationId();
        Long ouId = command.getOuId();
        Long trunoverBoxId = null;
        String trunoverBoxCode = command.getTurnoverBoxCode();
        String skuBarcode = command.getTipSkuBarCode();
        Long skuId = command.getSkuId();
        if (!StringUtil.isEmpty(trunoverBoxCode)) {
            ContainerCommand ic = containerDao.getContainerByCode(trunoverBoxCode, ouId);
            if (null == ic) {
                // 容器信息不存在
                log.error("pdaScanContainer container is null logid: " + logId);
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
            }
            trunoverBoxId = ic.getId();
        }
        String key = locationId.toString()+trunoverBoxId;
        OperationExecStatisticsCommand opExecLineCmd = cacheManager.getObject(CacheConstants.OPERATIONEXEC_STATISTICS + operationId.toString());
        if(null == opExecLineCmd){
            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
        }
        Map<String, Set<Long>> locSkuIds = opExecLineCmd.getSkuIds();
        Set<Long> icSkuIds = locSkuIds.get(key);
        Map<Long, Integer> cacheSkuIdsQty = skuRedisManager.findSkuByBarCode(skuBarcode, logId); // 获取对应的商品数量,key值是sku
        boolean isSkuExists = false;
        for (Long cacheId : cacheSkuIdsQty.keySet()) {
            if (icSkuIds.contains(cacheId)) {
                isSkuExists = true;
            }
            if (true == isSkuExists) {
                break;
            }
        }
        if (false == isSkuExists) {
            log.error("scan sku is not found in current inside contianer error, ocId is:[{}], icId is:[{}], scanSkuId is:[{}], logId is:[{}]", trunoverBoxId,  skuId, logId);
            throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_SCAN_SKU_ERROR, new Object[] {trunoverBoxCode});
        }
        List<WhSkuInventoryCommand> list = whSkuInventoryDao.findWhskuInventoryByInsideContainerId(ouId,trunoverBoxId);
        if (null == list || list.size() == 0) {
            throw new BusinessException(ErrorCodes.LOCATION_INVENTORY_IS_NO);
        }
        // 货箱内待拣货sku库位库存库存属性是否唯一
        Set<String> skuAttrIdsSet = new HashSet<String>();
        for (WhSkuInventoryCommand invSkuCmd : list) {
            String pSkuAttrId = SkuCategoryProvider.getSkuAttrIdByInv(invSkuCmd);
            skuAttrIdsSet.add(pSkuAttrId);
        }
        if (skuAttrIdsSet.size() > 1) { // 货箱内待拣货sku库存属性不唯一
            command.setIsUniqueSkuAttrInside(false); // 不唯一
        } else {// 货箱内待拣货sku库存属性唯一
            command.setIsUniqueSkuAttrInside(true); // 唯一
        }
        log.info("PdaPickingWorkManagerImpl judgeSkuAttrIdsIsUnique is end");

        return command;
    }
    
    /***
     * 删除缓存(如果存在s)
     * @param operationId
     * @param ouId
     */
     public void removeCache(Long operationId){
         OperationExecStatisticsCommand opExecLineCmd = cacheManager.getObject(CacheConstants.OPERATIONEXEC_STATISTICS + operationId.toString());
         if(null != opExecLineCmd){
             Map<String, Set<Long>> locSkuIds = opExecLineCmd.getSkuIds();
             List<Long> locationIds = opExecLineCmd.getLocationIds();
             Map<Long, Set<Long>> locTurnoverBoxIds = opExecLineCmd.getTurnoverBoxIds();
             for(Long locationId:locationIds){
                 Set<Long> turnoverBoxIds = locTurnoverBoxIds.get(locationId);
                 for(Long turnoverBoxId:turnoverBoxIds){
                     String key = locationId.toString()+turnoverBoxId;
                     Set<Long> skuIds = locSkuIds.get(key);
                     for(Long skuId:skuIds){
                         cacheManager.remove(CacheConstants.SCAN_SKU_QUEUE + locationId.toString()+ turnoverBoxId.toString() + skuId.toString());
                         cacheManager.remove(CacheConstants.SCAN_SKU_QUEUE_SN_COUNT +locationId.toString()+ turnoverBoxId.toString() + skuId.toString());
                         cacheManager.remove(CacheConstants.SCAN_SKU_QUEUE_SN +locationId.toString()+ turnoverBoxId.toString() + skuId.toString());
                     }
                     cacheManager.remove(CacheConstants.PDA_REPLENISH_PUTAWAY_SCAN_SKU + locationId.toString()+turnoverBoxId.toString());
                 }
             }
             cacheManager.remove(CacheConstants.CACHE_PUTAWAY_LOCATION+operationId.toString());
             cacheManager.remove(CacheConstants.OPERATIONEXEC_STATISTICS+operationId.toString());
         }
        
     }
     
     
     /**
      * 判断是否是单库位
      * @param operationId
      * @return
      */
     public Boolean judgeIsOnlyLocation(Long operationId){
         Boolean result = false;// 默认单库位
         OperationExecStatisticsCommand opExecLineCmd = cacheManager.getObject(CacheConstants.OPERATIONEXEC_STATISTICS + operationId.toString());
         if(null == opExecLineCmd){
             throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
         }
         List<Long> locationIds = opExecLineCmd.getLocationIds();
         if(locationIds.size() > 1){//多库位
             result = true;
         }
         return result;
     }
     
     public ContainerCommand findContainerCmdByCode(String containerCode,Long ouId){
         ContainerCommand cmd =  containerDao.getContainerByCode(containerCode, ouId);
         if(null == cmd){
             throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
         }
         return cmd;
     }
}
