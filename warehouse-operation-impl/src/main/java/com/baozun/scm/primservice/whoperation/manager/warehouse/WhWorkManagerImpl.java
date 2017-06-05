package com.baozun.scm.primservice.whoperation.manager.warehouse;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lark.common.annotation.MoreDB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.baseservice.sac.manager.CodeManager;
import com.baozun.scm.primservice.whoperation.command.pda.work.PickingScanResultCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhWorkCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.constant.WorkStatus;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOperationDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOperationExecLineDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOperationLineDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhWorkDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhWorkLineDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.pda.work.PdaPickingWorkManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.inventory.WhSkuInventoryManager;
import com.baozun.scm.primservice.whoperation.model.ResponseMsg;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOperation;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOperationExecLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOperationLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhWork;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhWorkLine;

@Service("whWorkManager")
@Transactional
public class WhWorkManagerImpl extends BaseManagerImpl implements WhWorkManager {
    
    public static final Logger log = LoggerFactory.getLogger(WhWorkManagerImpl.class);

    @Autowired
    private WhWorkDao whWorkDao;
    @Autowired
    private WhWorkLineDao whWorkLineDao;
    @Autowired
    private CodeManager codeManager;
    @Autowired
    private WhSkuInventoryManager whSkuInventoryManager;
    @Autowired
    private PdaPickingWorkManager pdaPickingWorkManager;
    @Autowired
    private WhOperationDao whOperationDao;
    @Autowired
    private WhOperationLineDao whOperationLineDao;
    @Autowired
    private WhOperationExecLineDao whOperationExecLineDao;
    
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    @Transactional(propagation = Propagation.SUPPORTS)
    public Boolean saveOrUpdate(WhWorkCommand whWorkCommand) {
        WhWork whWork = new WhWork();
        //复制数据        
        BeanUtils.copyProperties(whWorkCommand, whWork);
        if(null != whWorkCommand.getId() ){
            whWorkDao.saveOrUpdateByVersion(whWork);
        }else{
            whWorkDao.insert(whWork);
        }
        return null;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhWorkCommand findWorkByWorkCode(String code, Long ouId) {
        WhWorkCommand whWorkCommand = this.whWorkDao.findWorkByWorkCode(code, ouId);
        return whWorkCommand;
    }
    
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhWork findWorkByWorkId(Long id) {
        WhWork whWork = this.whWorkDao.findById(id);
        return whWork;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhWork> findWorkByWaveWithLock(String waveCode, Long ouId) {
        WhWork work = new WhWork();
        work.setWaveCode(waveCode);
        work.setOuId(ouId);
        work.setLifecycle(Constants.LIFECYCLE_START);
        work.setIsLocked(true);
        return this.whWorkDao.findListByParam(work);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhWork> findWorkByWaveWithUnLock(String waveCode, Long ouId) {
        WhWork work = new WhWork();
        work.setWaveCode(waveCode);
        work.setOuId(ouId);
        work.setLifecycle(Constants.LIFECYCLE_START);
        work.setIsLocked(false);
        return this.whWorkDao.findListByParam(work);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhWork> findWorkByWave(String code, Long ouId) {
        WhWork work = new WhWork();
        work.setWaveCode(code);
        work.setOuId(ouId);
        work.setLifecycle(Constants.LIFECYCLE_START);
        return this.whWorkDao.findListByParam(work);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhWork> findWorkByWaveAndCategory(String code, String workCategory, Long ouId) {
        WhWork work = new WhWork();
        work.setWaveCode(code);
        work.setOuId(ouId);
        work.setWorkCategory(workCategory);
        work.setLifecycle(Constants.LIFECYCLE_START);
        return this.whWorkDao.findListByParam(work);
    }

    /**
     * 获取批次下的所有工作
     *
     * @param batchNo
     * @param ouId
     * @return
     */
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhWorkCommand> findWorkByBatch(String batchNo, Long ouId){
        return whWorkDao.findWorkByBatch(batchNo, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhWork findWorkByWorkId(Long workId, Long ouId) {
        return this.whWorkDao.findWorkById(workId, ouId);
    }



    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void cancelAssignOutOperation(List<Long> workIds, Long userId, Long ouId) {
        for (Long workId : workIds) {
            WhWork whWork = this.whWorkDao.findWorkById(workId, ouId);
            if (whWork.getIsAssignOut() != null && whWork.getIsAssignOut()) {
                whWork.setIsAssignOut(false);
                whWork.setOperatorId(userId);
                whWork.setAssignOutBatch(null);
                int updateCount = this.whWorkDao.saveOrUpdateByVersion(whWork);
                if (updateCount <= 0) {
                    throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                }
            } else {
                throw new BusinessException(ErrorCodes.CANCEL_ASSIGNOUT_CAPABLE);
            }


        }
    }

    /**
     * [业务方法] 签出操作
     * 
     * @param workIds
     * @param userId
     * @param ouId
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public ResponseMsg assignOutOperation(List<Long> workIds, Long userId, Long ouId) {
        List<WhWork> workList = new ArrayList<WhWork>();
        for (Long workId : workIds) {
            WhWork whWork = this.whWorkDao.findWorkById(workId, ouId);
            if (WorkStatus.NEW.equals(whWork.getStatus()) && (whWork.getIsLocked() != null && !whWork.getIsLocked())) {
                if (Constants.WORKCATEGORY_REPLENISHMENT.equals(whWork.getWorkCategory())) {
                    if ((whWork.getIsWaveReplenish() != null && !whWork.getIsWaveReplenish()) || (whWork.getIsMultiOperation() != null && whWork.getIsMultiOperation())) {
                        throw new BusinessException(ErrorCodes.ASSIGN_OUT_REPLENISHMENT_STATUS_ERROR, whWork.getCode());
                    }
                }

            } else {
                throw new BusinessException(ErrorCodes.ASSIGN_OUT_CHECK_STATUS_ERROR, whWork.getCode());
            }
            if (whWork.getIsAssignOut() != null && whWork.getIsAssignOut()) {
                throw new BusinessException(ErrorCodes.ASSIGN_OUT_CHECK_STATUS_ERROR, whWork.getCode());
            }
            workList.add(whWork);
        }
        String assignOutCode = this.codeManager.generateCode(Constants.WMS, Constants.WORK_ASSIGN_OUT_BATCH, null, null, null);
        for (WhWork work : workList) {
            work.setIsAssignOut(true);
            work.setAssignOutBatch(assignOutCode);
            work.setOperatorId(userId);
            int updateCount = this.whWorkDao.saveOrUpdateByVersion(work);
            if (updateCount <= 0) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
        }
        ResponseMsg msg = new ResponseMsg();
        msg.setResponseStatus(ResponseMsg.STATUS_SUCCESS);
        msg.setMsg(assignOutCode);
        return msg;
    }

    /**
     * [业务方法] 签入操作
     * 
     * @param workIds
     * @param userId
     * @param ouId
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public ResponseMsg assignInOperation(String batch, Long userId, Long ouId) {
        WhWork search = new WhWork();
        search.setOuId(ouId);
        search.setAssignOutBatch(batch);
        search.setIsAssignOut(true);
        List<WhWork> workList = this.whWorkDao.findListByParam(search);
        List<WhWork> lazyWorkList = new ArrayList<WhWork>();

        try {

            for (WhWork work : workList) {
                if (Constants.WORKCATEGORY_REPLENISHMENT.equals(work.getWorkCategory())) {// 补货工作优先执行
                    List<WhWorkLine> lineList = this.whWorkLineDao.findWorkLineByWorkIdOuId(work.getId(), ouId);
                    this.excuteWork(work, lineList, ouId, userId);
                    for (WhWorkLine line : lineList) {
                        this.whSkuInventoryManager.executeReplenishmentWork(line.getReplenishmentCode(), line, ouId, userId);
                    }

                } else {

                    lazyWorkList.add(work);
                }
            }
        } catch (BusinessException ex) {
            log.error("", ex);
            throw ex;
        } catch (Exception e) {
            log.error("", e);
            throw new BusinessException(ErrorCodes.REPLENISHMENT_ASSIGN_IN_ERROR);
        }

        if (lazyWorkList.size() > 0) {
            for (WhWork work : lazyWorkList) {
                if (Constants.WORKCATEGORY_PICKING.equals(work.getWorkCategory())) {// 拣货工作
                    List<WhWorkLine> lineList = this.whWorkLineDao.findWorkLineByWorkIdOuId(work.getId(), ouId);

                    for (WhWorkLine line : lineList) {
                        this.whSkuInventoryManager.executePickingWork(line, ouId, userId);
                    }


                    Long operationId = this.excuteWork(work, lineList, ouId, userId);

                    PickingScanResultCommand pickCommand = new PickingScanResultCommand();
                    pickCommand.setWorkBarCode(work.getCode());
                    // 生成集货-播种数据
                    this.pdaPickingWorkManager.insertIntoCollection(pickCommand, ouId, userId);
                    // 更新出库单状态
                    this.pdaPickingWorkManager.changeOdoStatus(work.getPickingMode(), operationId, ouId, userId);


                }
            }
        }

        ResponseMsg msg = new ResponseMsg();
        return msg;
    }

    private Long excuteWork(WhWork work, List<WhWorkLine> lineList, Long ouId, Long userId) {
        for (WhWorkLine line : lineList) {
            // 更新工作明细集合
            line.setFinishTime(new Date());
            line.setCompleteQty(line.getQty());
            int updateCount = this.whWorkLineDao.saveOrUpdateByVersion(line);
            if (updateCount <= 0) {
                log.error("work when assign in,update work line[{},{}] by version error", line.getId(), ouId);
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
        }
        work.setStatus(WorkStatus.FINISH);
        int updateCount = this.whWorkDao.saveOrUpdateByVersion(work);
        if (updateCount <= 0) {
            log.error("work when assign in,update work[{},{}] by version error", work.getId(), ouId);
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
        // 作业
        WhOperation operation = this.whOperationDao.findOperationByWorkId(work.getId(), ouId);

        operation.setStatus(WorkStatus.FINISH);
        int updateOperationCount = this.whOperationDao.saveOrUpdateByVersion(operation);
        if (updateOperationCount <= 0) {
            log.error("work when assign in,update operation[{},{}] by version error", operation.getId(), ouId);
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
        List<WhOperationLine> operationlineList = this.whOperationLineDao.findByOperationId(operation.getId(), ouId);
        for (WhOperationLine operationLine : operationlineList) {
            operationLine.setFinishTime(new Date());
            operationLine.setCompleteQty(operationLine.getQty());
            int updateOperationLineCount = this.whOperationLineDao.saveOrUpdateByVersion(operationLine);
            if (updateOperationLineCount <= 0) {
                log.error("work when assign in,update operation line[{},{}] by version error", operationLine.getId(), ouId);
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
            WhOperationExecLine operationExecLine = new WhOperationExecLine();
            BeanUtils.copyProperties(operationLine, operationExecLine);
            operationExecLine.setId(null);
            this.whOperationExecLineDao.insert(operationExecLine);


        }
        return operation.getId();
    }

}
