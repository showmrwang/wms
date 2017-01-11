package com.baozun.scm.primservice.whoperation.manager.pda.work;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.redis.manager.CacheManager;
import com.baozun.scm.primservice.whoperation.command.pda.work.OperatioExecLineStatisticsCommand;
import com.baozun.scm.primservice.whoperation.command.pda.work.ReplenishmentPutawayCommand;
import com.baozun.scm.primservice.whoperation.command.pda.work.ReplenishmentScanResultComamnd;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhWorkCommand;
import com.baozun.scm.primservice.whoperation.constant.CacheConstants;
import com.baozun.scm.primservice.whoperation.constant.ContainerStatus;
import com.baozun.scm.primservice.whoperation.constant.WorkStatus;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhLocationDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOperationDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhWorkDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.warehouse.inventory.WhSkuInventoryManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container;
import com.baozun.scm.primservice.whoperation.model.warehouse.Location;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOperation;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhWork;
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
    
    @Override
    public ReplenishmentPutawayCommand putawayTipLocation(ReplenishmentPutawayCommand command) {
        // TODO Auto-generated method stub
        log.info("PdaReplenishmentPutawayManagerImpl putawayTipLocation is start");
        Long operationId = command.getOperationId();
        Long ouId = command.getOuId();
        OperatioExecLineStatisticsCommand opExecLineCmd = cacheManager.getObject(CacheConstants.CACHE_OPERATION_EXEC_LINE + operationId.toString());
        if(null == opExecLineCmd){
            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
        }
        List<Long> locationIds = opExecLineCmd.getLocationIds();
        ReplenishmentScanResultComamnd  sRCmd = pdaReplenishmentPutawayCacheManager.tipLocation(locationIds, operationId);
        Long tipLocationId = sRCmd.getLocationId();
        Location location = whLocationDao.findByIdExt(tipLocationId, ouId);
        if(null == location) {
            throw new BusinessException(ErrorCodes.TIP_LOCATION_FAIL);
        }
        command.setTipLcoationBarCode(location.getBarCode());
        command.setTipLocationCode(location.getCode());
        command.setLcoationId(location.getId()); 
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
        Long locationId = command.getLcoationId();
        OperatioExecLineStatisticsCommand opExecLineCmd = cacheManager.getObject(CacheConstants.CACHE_OPERATION_EXEC_LINE + operationId.toString());
        if(null == opExecLineCmd){
            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
        }
        Map<Long, Set<Long>> locToTurnoverBoxIds = opExecLineCmd.getLocationToTurnoverBoxIds();
        Set<Long> turnoverBoxIds = locToTurnoverBoxIds.get(locationId);
        ReplenishmentScanResultComamnd  sRCmd = pdaReplenishmentPutawayCacheManager.tipTurnoverBox(turnoverBoxIds, operationId);
        Long turnoverBoxId = sRCmd.getTurnoverBoxId();
        String containerCode = this.judeContainer(turnoverBoxId, ouId);
        command.setTipTurnoverBoxCode(containerCode);
        command.setIsNeedScanTurnoverBox(true);
        log.info("PdaReplenishmentPutawayManagerImpl putawayScanLocation is end");
        return command;
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

    
    @Override
    public ReplenishmentPutawayCommand putawayScanTurnoverBox(ReplenishmentPutawayCommand command,Boolean isTabbInvTotal) {
        // TODO Auto-generated method stub
        log.info("PdaReplenishmentPutawayManagerImpl putawayScanTurnoverBox is start");
        Long operationId = command.getOperationId();
        Long ouId = command.getOuId();
        Long locationId = command.getLcoationId();
        Long userId = command.getUserId();
        String workCode = command.getWorkBarCode();
        OperatioExecLineStatisticsCommand opExecLineCmd = cacheManager.getObject(CacheConstants.CACHE_OPERATION_EXEC_LINE + operationId.toString());
        if(null == opExecLineCmd){
            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
        }
        Map<Long, Set<Long>> locToTurnoverBoxIds = opExecLineCmd.getLocationToTurnoverBoxIds();
        Set<Long> turnoverBoxIds = locToTurnoverBoxIds.get(locationId);
        List<Long> locationIds = opExecLineCmd.getLocationIds();
        ReplenishmentScanResultComamnd  sRCmd = pdaReplenishmentPutawayCacheManager.tipTurnoverBox(turnoverBoxIds, operationId);
        if(sRCmd.getIsNeedScanTurnoverBox()) {  //当前库位对应的周转箱扫描完毕
            Long turnoverBoxId = sRCmd.getTurnoverBoxId();
            String containerCode = this.judeContainer(turnoverBoxId, ouId);
            command.setTipTurnoverBoxCode(containerCode);
            command.setIsNeedScanTurnoverBox(true);
        }else{//继续扫描下一个库位
            ReplenishmentScanResultComamnd  rishSRCmd =  pdaReplenishmentPutawayCacheManager.tipLocation(locationIds, operationId);
            if(rishSRCmd.getIsNeedScanLocation()) { //还有库位没有扫描，继续扫描库位
                Long locId = rishSRCmd.getLocationId();
                Location location = whLocationDao.findByIdExt(locId, ouId);
                if(null == location) {
                    throw new BusinessException(ErrorCodes.TIP_LOCATION_FAIL);
                }
                command.setTipLcoationBarCode(location.getBarCode());
                command.setTipLocationCode(location.getCode());
                command.setLcoationId(location.getId()); 
                command.setIsNeedScanLocation(true);
            }else{ //库位已经扫描完毕
                command.setIsScanFinsh(true);
                whSkuInventoryManager.replenishmentPutaway(operationId, ouId, isTabbInvTotal, userId, workCode);
                //更新工作及作业状态
                this.updateStatus(operationId, workCode, ouId, userId);
                //清除所有缓存
                pdaReplenishmentPutawayCacheManager.pdaReplenishPutwayRemoveAllCache(operationId);
            }
        }
        log.info("PdaReplenishmentPutawayManagerImpl putawayScanTurnoverBox is end");
        return command;
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

}
