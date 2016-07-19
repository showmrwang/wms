/**
 * Copyright (c) 2013 Baozun All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Baozun. You shall not disclose
 * such Confidential Information and shall use it only in accordance with the terms of the license
 * agreement you entered into with Baozun.
 * 
 * BAOZUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. BAOZUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES.
 */

package com.baozun.scm.primservice.whoperation.manager.warehouse;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import lark.common.annotation.MoreDB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.warehouse.AsnReserveCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.CheckInQueueCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.constant.PoAsnStatus;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhAsnDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.AsnReserveDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.CheckInQueueDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.PlatformDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.StoreDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.BaseModel;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsn;
import com.baozun.scm.primservice.whoperation.model.warehouse.AsnReserve;
import com.baozun.scm.primservice.whoperation.model.warehouse.CheckInQueue;
import com.baozun.scm.primservice.whoperation.model.warehouse.Platform;
import com.baozun.scm.primservice.whoperation.model.warehouse.Store;

@Service("checkInQueueManager")
@Transactional
public class CheckInQueueManagerImpl extends BaseManagerImpl implements CheckInQueueManager {
    public static final Logger log = LoggerFactory.getLogger(CheckInQueueManagerImpl.class);

    @Autowired
    CheckInQueueDao checkInQueueDao;

    @Autowired
    AsnReserveDao asnReserveDao;

    @Autowired
    WhAsnDao whAsnDao;

    @Autowired
    PlatformDao platformDao;

    @Autowired
    StoreDao storeDao;

    /**
     * 根据asn预约信息查找等待队列信息
     * 
     * @author mingwei.xie
     * @param asnId
     * @param ouId
     * @param logId
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public CheckInQueueCommand findCheckInQueueByAsnId(Long asnId, Long ouId, String logId) {
        if (log.isInfoEnabled()) {
            log.info("CheckInQueueManagerImpl.findCheckInQueueCommandByAsnId start, asnId is:[{}], ouId is:[{}], logId is:[{}]", asnId, ouId, logId);
        }
        if (null == asnId || null == ouId) {
            log.error("CheckInQueueManagerImpl.findCheckInQueueCommandByAsnId param is null, param asnId is:[{}], ouId is:[{}], logId is:[{}]", asnId, ouId, logId);
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        if (log.isDebugEnabled()) {
            log.debug("CheckInQueueManagerImpl.findCheckInQueueCommandByAsnId -> checkInQueueDao.findCheckInQueueCommandByAsnId invoke, asnId is:[{}], ouId is:[{}], logId is:[{}]", asnId, ouId, logId);
        }
        CheckInQueueCommand checkInQueueCommand = checkInQueueDao.findCheckInQueueCommandByAsnId(asnId, ouId);
        if (log.isDebugEnabled()) {
            log.debug("CheckInQueueManagerImpl.findCheckInQueueCommandByAsnId -> checkInQueueDao.findCheckInQueueCommandByAsnId result, asnId is:[{}], ouId is:[{}], logId is:[{}], checkInQueueCommand is:[{}]", asnId, ouId, logId, checkInQueueCommand);
        }
        if (log.isInfoEnabled()) {
            log.info("CheckInQueueManagerImpl.findCheckInQueueCommandByAsnId end, asnReserveId is:[{}], ouId is:[{}], logId is:[{}]", asnId, ouId, logId);
        }

        return checkInQueueCommand;
    }

    /**
     * 查询所有排队中的ASN，按照先后顺序排序
     * 
     * @author mingwei.xie
     * @param ouId
     * @param logId
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<CheckInQueueCommand> getListOrderBySequenceAsc(Long ouId, String logId) {
        if (log.isInfoEnabled()) {
            log.info("CheckInQueueManagerImpl.getListOrderBySequenceAsc start, ouId is:[{}], logId is:[{}]", ouId, logId);
        }
        if (null == ouId) {
            log.error("CheckInQueueManagerImpl.getListOrderBySequenceAsc param is null, param  ouId is:[{}], logId is:[{}]", ouId, logId);
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        if (log.isDebugEnabled()) {
            log.debug("CheckInQueueManagerImpl.getListOrderBySequenceAsc -> checkInQueueDao.getListOrderBySequenceAsc invoke, ouId is:[{}], logId is:[{}]", ouId, logId);
        }
        List<CheckInQueueCommand> checkInQueueCommandList = checkInQueueDao.getListOrderBySequenceAsc(ouId);
        if (log.isDebugEnabled()) {
            log.debug("CheckInQueueManagerImpl.getListOrderBySequenceAsc -> checkInQueueDao.getListOrderBySequenceAsc result, ouId is:[{}], logId is:[{}], checkInQueueCommandList is:[{}]", ouId, logId, checkInQueueCommandList);
        }
        if (log.isInfoEnabled()) {
            log.info("CheckInQueueManagerImpl.getListOrderBySequenceAsc end, ouId is:[{}], logId is:[{}], checkInQueueCommandList is:[{}]", ouId, logId, checkInQueueCommandList);
        }

        return checkInQueueCommandList;
    }

    /**
     * 加入asn签入等待序列
     * 
     * @author mingwei.xie
     * @param asnId
     * @param ouId
     * @param userId
     * @param logId
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public int addInToCheckInQueue(Long asnId, Long ouId, Long userId, String logId) {
        if (log.isInfoEnabled()) {
            log.info("CheckInQueueManagerImpl.addToCheckInQueue start, asnId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", asnId, ouId, userId, logId);
        }
        if (null == asnId || null == ouId || null == userId) {
            log.error("CheckInQueueManagerImpl.addToCheckInQueue param is null, param asnId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", asnId, ouId, userId, logId);
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }

        if (log.isDebugEnabled()) {
            log.debug("CheckInQueueManagerImpl.addToCheckInQueue -> whAsnDao.findWhAsnById invoke, asnId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", asnId, ouId, userId, logId);
        }
        // 查询原始ASN信息
        WhAsn originWhAsn = whAsnDao.findWhAsnById(asnId, ouId);
        // 不是新建和预约的asn不允许签入
        if (null == originWhAsn || (PoAsnStatus.ASN_NEW != originWhAsn.getStatus() && PoAsnStatus.ASN_RESERVE != originWhAsn.getStatus())) {
            log.error("CheckInQueueManagerImpl.addToCheckInQueue -> whAsnDao.findWhAsnById error, asnId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}], originWhAsn is:[{}]", asnId, ouId, userId, logId, originWhAsn);
            throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
        }
        if (log.isDebugEnabled()) {
            log.debug("CheckInQueueManagerImpl.addToCheckInQueue -> whAsnDao.findWhAsnById result, asnId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}], originWhAsn is:[{}]", asnId, ouId, userId, logId, originWhAsn);
        }

        if (log.isDebugEnabled()) {
            log.debug("CheckInQueueManagerImpl.addToCheckInQueue -> checkInManagerProxy.getStoreById invoke, ouId is:[{}], logId is:[{}], storeId is:[{}]", ouId, logId, originWhAsn.getStoreId());
        }
        // 查询店铺信息
        Store store = storeDao.findById(originWhAsn.getStoreId());
        // 店铺强制预约asn未预约的不允许签入
        if (null == store || (store.getIsMandatorilyReserved() && PoAsnStatus.ASN_RESERVE != originWhAsn.getStatus())) {
            log.error("CheckInQueueManagerImpl.addToCheckInQueue -> checkInManagerProxy.getStoreById error, ouId is:[{}], logId is:[{}], storeId is:[{}], store is:[{}]", ouId, logId, originWhAsn.getStoreId(), store);
            throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
        }
        if (log.isDebugEnabled()) {
            log.debug("CheckInQueueManagerImpl.addToCheckInQueue -> checkInManagerProxy.getStoreById result, ouId is:[{}], logId is:[{}], storeId is:[{}], store is:[{}]", ouId, logId, originWhAsn.getStoreId(), store);
        }


        if (log.isDebugEnabled()) {
            log.debug("CheckInQueueManagerImpl.addToCheckInQueue -> checkInQueueDao.getListOrderBySequenceAsc invoke, ouId is:[{}], logId is:[{}]", ouId, logId);
        }
        // 签入队列
        List<CheckInQueueCommand> checkInQueueCommandList = checkInQueueDao.getListOrderBySequenceAsc(ouId);
        if (log.isDebugEnabled()) {
            log.debug("CheckInQueueManagerImpl.addToCheckInQueue -> checkInQueueDao.getListOrderBySequenceAsc result, ouId is:[{}], logId is:[{}], checkInQueueCommandList is:[{}]", ouId, logId, checkInQueueCommandList);
        }

        CheckInQueue originCheckInQueue = checkInQueueDao.findCheckInQueueByAsnId(originWhAsn.getId(), originWhAsn.getOuId());

        // 修改实际到货时间
        // ASN状态分配了月台才是签入
        // originWhAsn.setStatus(PoAsnStatus.ASN_CHECKIN);
        originWhAsn.setModifiedId(userId);
        if (null == originCheckInQueue) {
            originWhAsn.setDeliveryTime(new Date());
        }
        // 保存更新asn信息
        originWhAsn = this.updateWhAsn(asnId, ouId, userId, logId, originWhAsn);

        // 新增的asn签入队列信息
        CheckInQueueCommand newCheckInQueueCommand = new CheckInQueueCommand();
        if (null == originCheckInQueue) {
            newCheckInQueueCommand.setCreateTime(new Date());
            newCheckInQueueCommand.setCreatedId(userId);
            newCheckInQueueCommand.setLastModifyTime(new Date());
            checkInQueueCommandList.add(newCheckInQueueCommand);
        } else {
            for (CheckInQueueCommand checkInQueueCommand : checkInQueueCommandList) {
                if (checkInQueueCommand.getAsnId().equals(originCheckInQueue.getAsnId())) {
                    newCheckInQueueCommand = checkInQueueCommand;
                }
            }
        }
        newCheckInQueueCommand.setAsnId(originWhAsn.getId());
        newCheckInQueueCommand.setEta(originWhAsn.getEta());
        newCheckInQueueCommand.setDeliveryTime(originWhAsn.getDeliveryTime());
        newCheckInQueueCommand.setOuId(ouId);
        newCheckInQueueCommand.setModifiedId(userId);


        if (log.isDebugEnabled()) {
            log.debug("CheckInQueueManagerImpl.addToCheckInQueue -> asnReserveDao.findAsnReserveByAsnId invoke, asnId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", asnId, ouId, userId, logId);
        }
        // 查询原始ASN预约信息
        AsnReserve originAsnReserve = asnReserveDao.findAsnReserveByAsnId(asnId, ouId);
        if (log.isDebugEnabled()) {
            log.debug("CheckInQueueManagerImpl.addToCheckInQueue -> asnReserveDao.findAsnReserveByAsnId result, asnId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}], originAsnReserve is:[{}]", asnId, ouId, userId, logId, originAsnReserve);
        }
        if (store.getIsMandatorilyReserved() && null == originAsnReserve || (null != originAsnReserve && PoAsnStatus.ASN_RESERVE_NEW != originAsnReserve.getStatus())) {
            // 店铺强制预约，asn预约状态不是新建状态不允许签入
            log.error("CheckInQueueManagerImpl.addToCheckInQueue -> asnReserveDao.findAsnReserveByAsnId error, asnId is:[{}], ouId is:[{}], logId is:[{}], originAsnReserve is:[{}]", asnId, ouId, logId, originAsnReserve);
            throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
        }

        if (null != originAsnReserve) {
            // 修改asn预约状态和实际到货时间
            // ASN预约改为已签入
            originAsnReserve.setStatus(PoAsnStatus.ASN_RESERVE_CHECKIN);
            originAsnReserve.setDeliveryTime(originWhAsn.getDeliveryTime());
            originAsnReserve.setModifiedId(userId);
            // 更新asn预约信息
            this.updateAsnReserve(ouId, userId, logId, originAsnReserve);

            // asn签入队列信息添加预约优先级信息
            newCheckInQueueCommand.setReserveId(originAsnReserve.getId());
            newCheckInQueueCommand.setLevel(originAsnReserve.getLevel());
        }


        // 重新排序
        Collections.sort(checkInQueueCommandList);
        if (log.isDebugEnabled()) {
            log.debug("CheckInQueueManagerImpl.addToCheckInQueue loop checkInQueueCommandList, asnId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}], checkInQueueCommandList is:[{}]", asnId, ouId, userId, logId, checkInQueueCommandList);
        }
        for (int index = checkInQueueCommandList.size() - 1; index >= 0; index--) {
            CheckInQueueCommand originCheckInQueueCommand = checkInQueueCommandList.get(index);
            // 不能先修改,需要使用原先的序号判断是否需要更新
            // originCheckInQueueCommand.setSequence(index + 1);
            if (null != originCheckInQueueCommand.getId()) {
                if (log.isDebugEnabled()) {
                    log.debug("CheckInQueueManagerImpl.addToCheckInQueue loop checkInQueueCommandList, checkInQueue is exist, update to sharedDB, originCheckInQueueCommand is:[{}], logId is:[{}]", originCheckInQueueCommand, logId);
                }
                // 根据序号是否变化判断是否需要更新
                if (!originCheckInQueueCommand.getSequence().equals(index + 1)) {
                    CheckInQueue originCheckInQueueTemp = checkInQueueDao.findByIdExt(originCheckInQueueCommand.getId(), originCheckInQueueCommand.getOuId());
                    originCheckInQueueTemp.setSequence(index + 1);
                    originCheckInQueueTemp.setModifiedId(userId);
                    if (log.isDebugEnabled()) {
                        log.debug("CheckInQueueManagerImpl.addToCheckInQueue -> checkInQueueDao.updateByVersionExt, update originCheckInQueueTemp sequence,  update to sharedDB, originCheckInQueueTemp is:[{}], logId is:[{}]", originCheckInQueueTemp, logId);
                    }
                    int checkInQueueUpdateCount = checkInQueueDao.updateByVersionExt(originCheckInQueueTemp);
                    if (checkInQueueUpdateCount != 1) {
                        log.error("CheckInQueueManagerImpl.addToCheckInQueue -> checkInQueueDao.updateByVersionExt failed, update checkInQueueUpdateCount != 1, asnId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}], originCheckInQueueTemp is:[{}]",
                                asnId, ouId, userId, logId, originCheckInQueueTemp);
                        throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                    }
                    // 插入系统日志
                    insertGlobalLog(Constants.GLOBAL_LOG_UPDATE, originCheckInQueueTemp, ouId, userId, null, null);
                }
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("CheckInQueueManagerImpl.addToCheckInQueue loop checkInQueueCommandList, checkInQueue isn't exist, cache index to insert checkInQueue, originCheckInQueueCommand is:[{}], logId is:[{}]", originCheckInQueueCommand, logId);
                }
                // TODO 确认ASN的计划到货时间和预约的计划到货时间一致
                CheckInQueue checkInQueue = new CheckInQueue();
                checkInQueue.setAsnId(newCheckInQueueCommand.getAsnId());
                checkInQueue.setOuId(newCheckInQueueCommand.getOuId());
                checkInQueue.setCreateTime(new Date());
                checkInQueue.setCreatedId(userId);
                checkInQueue.setModifiedId(userId);
                checkInQueue.setLastModifyTime(new Date());
                checkInQueue.setSequence(index + 1);
                if (log.isDebugEnabled()) {
                    log.debug("CheckInQueueManagerImpl.addToCheckInQueue -> checkInQueueDao.insert,insert to sharedDB, checkInQueue is:[{}], logId is:[{}]", checkInQueue, logId);
                }
                long checkInQueueInsertCount = checkInQueueDao.insert(checkInQueue);
                if (checkInQueueInsertCount != 1) {
                    log.debug("CheckInQueueManagerImpl.addToCheckInQueue -> checkInQueueDao.insert error checkInQueueInsertCount != 1, checkInQueue is:[{}], logId is:[{}], checkInQueueInsertCount is:[{}]", checkInQueue, logId, checkInQueueInsertCount);
                    throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                }
                insertGlobalLog(Constants.GLOBAL_LOG_INSERT, checkInQueue, ouId, userId, null, null);
            }
            // 不能先修改,需要使用原先的序号判断是否需要更新
            originCheckInQueueCommand.setSequence(index + 1);
        }

        if (log.isInfoEnabled()) {
            log.info("CheckInQueueManagerImpl.addToCheckInQueue end, asnId is:[{}], ouId is:[{}], logId is:[{}]", asnId, ouId, logId);
        }
        return newCheckInQueueCommand.getSequence() - 1;
    }

    /**
     * 完成签入
     * 
     * @author mingwei.xie
     * @param asnId
     * @param platformId
     * @param ouId
     * @param userId
     * @param logId
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void finishCheckIn(Long asnId, Long platformId, Long ouId, Long userId, String logId) {
        if (log.isInfoEnabled()) {
            log.info("CheckInQueueManagerImpl.finishCheckIn start, asnId is:[{}], platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", asnId, platformId, ouId, userId, logId);
        }
        if (null == asnId || null == ouId || null == userId) {
            log.error("CheckInQueueManagerImpl.finishCheckIn param is null, param asnId is:[{}], platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", asnId, platformId, ouId, userId, logId);
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }

        if (log.isDebugEnabled()) {
            log.debug("CheckInQueueManagerImpl.finishCheckIn -> whAsnDao.findWhAsnById invoke, asnId is:[{}], platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", asnId, platformId, ouId, userId, logId);
        }
        // 查询原始asn信息
        WhAsn originWhAsn = whAsnDao.findWhAsnById(asnId, ouId);
        if (null == originWhAsn || (PoAsnStatus.ASN_NEW != originWhAsn.getStatus() && PoAsnStatus.ASN_RESERVE != originWhAsn.getStatus())) {
            log.error("CheckInQueueManagerImpl.finishCheckIn -> whAsnDao.findWhAsnById error, originWhAsn status error, asnId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}], originWhAsn is:[{}]", asnId, ouId, userId, logId, originWhAsn);
            throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
        }
        if (log.isDebugEnabled()) {
            log.debug("CheckInQueueManagerImpl.finishCheckIn -> whAsnDao.findWhAsnById result, asnId is:[{}], platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}], originWhAsn is:[{}]", asnId, platformId, ouId, userId, logId, originWhAsn);
        }

        if (log.isDebugEnabled()) {
            log.debug("CheckInQueueManagerImpl.finishCheckIn -> checkInManagerProxy.getStoreById invoke, ouId is:[{}], logId is:[{}], storeId is:[{}]", ouId, logId, originWhAsn.getStoreId());
        }
        Store store = storeDao.findById(originWhAsn.getStoreId());
        // 店铺强制预约asn未预约的不允许签入
        if (null == store || (store.getIsMandatorilyReserved() && PoAsnStatus.ASN_RESERVE != originWhAsn.getStatus())) {
            log.error("CheckInQueueManagerImpl.finishCheckIn -> checkInManagerProxy.getStoreById error, result is null,  ouId is:[{}], logId is:[{}], storeId is:[{}]", ouId, logId, originWhAsn.getStoreId());
            throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
        }
        if (log.isDebugEnabled()) {
            log.debug("CheckInQueueManagerImpl.finishCheckIn -> checkInManagerProxy.getStoreById result, ouId is:[{}], logId is:[{}], storeId is:[{}], store is:[{}]", ouId, logId, originWhAsn.getStoreId(), store);
        }

        // 更新签入队列
        boolean isInCheckInQueue = this.updateByDeleteCheckInQueue(asnId, ouId, userId, logId);

        // 修改ASN状态和实际到货时间，不在签入队列的应该是即时签入的，否则签入时间在加入队列时已更新
        if (!isInCheckInQueue) {
            originWhAsn.setDeliveryTime(new Date());
        }
        originWhAsn.setStatus(PoAsnStatus.ASN_CHECKIN);
        originWhAsn.setModifiedId(userId);
        // 更新whAsn信息
        originWhAsn = this.updateWhAsn(asnId, ouId, userId, logId, originWhAsn);

        if (log.isDebugEnabled()) {
            log.debug("CheckInQueueManagerImpl.finishCheckIn -> asnReserveDao.findAsnReserveByAsnId invoke, asnId is:[{}], platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", asnId, ouId, userId, logId);
        }
        // ASN预约信息
        AsnReserve originAsnReserve = asnReserveDao.findAsnReserveByAsnId(asnId, ouId);
        if (store.getIsMandatorilyReserved() && null == originAsnReserve || (null != originAsnReserve && PoAsnStatus.ASN_RESERVE_NEW != originAsnReserve.getStatus())) {
            log.error("CheckInQueueManagerImpl.finishCheckIn -> asnReserveDao.findAsnReserveByAsnId error, originAsnReserve status error,  asnId is:[{}], platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}], originAsnReserve is:[{}]",
                    asnId, platformId, ouId, userId, logId, originAsnReserve);
            throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
        }
        if (log.isDebugEnabled()) {
            log.debug("CheckInQueueManagerImpl.finishCheckIn -> asnReserveDao.findAsnReserveByAsnId result, asnId is:[{}], platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}], originAsnReserve is:[{}]", asnId, ouId, userId, logId,
                    originAsnReserve);
        }
        if (null != originAsnReserve) {
            // 不在签入队列的应该是即时签入的，否则签入时间在加入队列时已更新
            if (!isInCheckInQueue) {
                originAsnReserve.setDeliveryTime(originWhAsn.getDeliveryTime());
            }
            // 修改ASN预约状态和实际到货时间
            originAsnReserve.setStatus(PoAsnStatus.ASN_RESERVE_FINISH);
            originAsnReserve.setModifiedId(userId);
            // 更新asn预约信息
            this.updateAsnReserve(ouId, userId, logId, originAsnReserve);
        }

        // 分配月台
        this.assignPlatform(platformId, ouId, userId, logId, originWhAsn);

        if (log.isInfoEnabled()) {
            log.info("CheckInQueueManagerImpl.finishCheckIn end, asnId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", asnId, ouId, userId, logId);
        }

    }

    /**
     * 简易预约
     *
     * @param asnReserveCommand
     * @param ouId
     * @param userId
     * @param logId
     * @author mingwei.xie
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void simpleReserve(AsnReserveCommand asnReserveCommand, Long ouId, Long userId, String logId) {
        if (log.isInfoEnabled()) {
            log.info("CheckInQueueManagerImpl.simpleReserve start, asnReserveCommand is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", asnReserveCommand, ouId, userId, logId);
        }
        if (null == asnReserveCommand || null == asnReserveCommand.getAsnId() || null == asnReserveCommand.getLevel() || null == asnReserveCommand.getCode() || null == ouId || null == userId) {
            log.error("CheckInQueueManagerImpl.simpleReserve param is null, asnReserveCommand is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", asnReserveCommand, ouId, userId, logId);
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        if (log.isDebugEnabled()) {
            log.debug("CheckInQueueManagerImpl.simpleReserve -> whAsnDao.findWhAsnById invoke, asnReserveCommand is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", asnReserveCommand, ouId, userId, logId);
        }
        WhAsn originWhAsn = whAsnDao.findWhAsnById(asnReserveCommand.getAsnId(), ouId);
        // 不是新建状态的asn不允许预约
        if (null == originWhAsn || PoAsnStatus.ASN_NEW != originWhAsn.getStatus()) {
            log.error("CheckInQueueManagerImpl.simpleReserve -> whAsnDao.findWhAsnById error, asnReserveCommand is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}], originWhAsn is:[{}]", asnReserveCommand, ouId, userId, logId, originWhAsn);
            throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
        }
        if (log.isDebugEnabled()) {
            log.debug("CheckInQueueManagerImpl.simpleReserve -> whAsnDao.findWhAsnById result, asnReserveCommand is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}], originWhAsn is:[{}]", asnReserveCommand, ouId, userId, logId, originWhAsn);
        }

        // ASN改为已预约
        // 修改预约时间， 实际到货时间
        originWhAsn.setStatus(PoAsnStatus.ASN_RESERVE);
        originWhAsn.setEta(new Date());

        CheckInQueue originCheckInQueue = checkInQueueDao.findCheckInQueueByAsnId(originWhAsn.getId(), originWhAsn.getOuId());
        if (null == originCheckInQueue) {
            originWhAsn.setDeliveryTime(null);
        }
        originWhAsn.setModifiedId(userId);
        // 更新whAsn信息
        originWhAsn = updateWhAsn(asnReserveCommand.getAsnId(), ouId, userId, logId, originWhAsn);

        if (log.isDebugEnabled()) {
            log.debug("CheckInQueueManagerImpl.simpleReserve -> asnReserveDao.findAsnReserveByAsnId invoke, asnReserveCommand is:[{}], asnId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", asnReserveCommand, asnReserveCommand.getAsnId(), ouId,
                    userId, logId);
        }
        // 查询已取消的ASN预约信息
        AsnReserve originAsnReserve = asnReserveDao.findAsnReserveByAsnId(asnReserveCommand.getAsnId(), ouId);
        if (log.isDebugEnabled()) {
            log.debug("CheckInQueueManagerImpl.simpleReserve -> asnReserveDao.findAsnReserveByAsnId result, asnReserveCommand is:[{}], asnId is:[{}], originAsnReserve is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", asnReserveCommand,
                    asnReserveCommand.getAsnId(), originAsnReserve, ouId, userId, logId);
        }
        if (null != originAsnReserve) {
            // 不是取消状态的预约信息不允许重新预约
            if (PoAsnStatus.ASN_RESERVE_CANCELED != originAsnReserve.getStatus()) {
                log.error(
                        "CheckInQueueManagerImpl.simpleReserve -> asnReserveDao.findAsnReserveByAsnId error, originAsnReserve status is not PoAsnStatus.ASN_RESERVE_CANCELED , asnReserveCommand is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}], originAsnReserve is:[{}]",
                        asnReserveCommand, ouId, userId, logId, originAsnReserve);
                throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
            }

            originAsnReserve.setLevel(asnReserveCommand.getLevel());
            originAsnReserve.setDeliveryTime(null);
            originAsnReserve.setModifiedId(userId);
            originAsnReserve.setStatus(PoAsnStatus.ASN_RESERVE_NEW);
            originAsnReserve.setEta(originWhAsn.getEta());
            originAsnReserve.setEstParkingTime(null);

            int asnReserveUpdateCount = asnReserveDao.saveOrUpdateByVersion(originAsnReserve);
            if (asnReserveUpdateCount != 1) {
                log.error("CheckInQueueManagerImpl.simpleReserve -> asnReserveDao.saveOrUpdateByVersion  update to sharedDB failed, update asnReserveUpdateCount != 1, logId is:[{}], originAsnReserve is:[{}], asnReserveUpdateCount is:[{}]", logId,
                        originAsnReserve, asnReserveUpdateCount);
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
            insertGlobalLog(Constants.GLOBAL_LOG_UPDATE, originAsnReserve, ouId, userId, null, null);

        } else {
            AsnReserve asnReserve = new AsnReserve();
            asnReserve.setCode(asnReserveCommand.getCode());
            asnReserve.setAsnId(originWhAsn.getId());
            asnReserve.setEta(originWhAsn.getEta());
            asnReserve.setDeliveryTime(null);
            asnReserve.setLevel(asnReserveCommand.getLevel());
            asnReserve.setOuId(ouId);
            asnReserve.setStatus(PoAsnStatus.ASN_RESERVE_NEW);
            asnReserve.setCreateTime(new Date());
            asnReserve.setCreatedId(userId);
            asnReserve.setModifiedId(userId);
            asnReserve.setLastModifyTime(new Date());

            long count = asnReserveDao.insert(asnReserve);
            if (count != 1) {
                log.error("CheckInQueueManagerImpl.simpleReserve -> asnReserveDao.insert,  insert to sharedDB failed, update asnUpdateCount != 1, logId is:[{}], asnReserve is:[{}], asnUpdateCount is:[{}]", logId, asnReserve, count);
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
            insertGlobalLog(Constants.GLOBAL_LOG_INSERT, asnReserve, ouId, userId, null, null);
        }
        // TODO 更新预约排序

        if (log.isInfoEnabled()) {
            log.info("CheckInQueueManagerImpl.simpleReserve end, asnReserveCommand is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", asnReserveCommand, ouId, userId, logId);
        }
    }

    /**
     * 手工指定月台
     * 
     * @author mingwei.xie
     * @param asnId
     * @param platformId
     * @param ouId
     * @param userId
     * @param logId
     * @return
     */
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    @Override
    public Long assignPlatform(Long asnId, Long platformId, Long ouId, Long userId, String logId) {
        if (log.isInfoEnabled()) {
            log.info("CheckInQueueManagerImpl.assignPlatform start, asnId is:[{}], platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", asnId, platformId, ouId, userId, logId);
        }
        if (null == asnId || null == platformId || null == ouId || null == userId) {
            log.error("CheckInQueueManagerImpl.assignPlatform param is null, asnId is:[{}], platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", asnId, platformId, ouId, userId, logId);
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }

        if (log.isDebugEnabled()) {
            log.debug("CheckInQueueManagerImpl.assignPlatform -> whAsnDao.findWhAsnById invoke, asnId is:[{}], platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", asnId, platformId, ouId, userId, logId);
        }
        // 查询原始ASN信息
        WhAsn originWhAsn = whAsnDao.findWhAsnById(asnId, ouId);
        if (null == originWhAsn || (PoAsnStatus.ASN_NEW != originWhAsn.getStatus() && PoAsnStatus.ASN_RESERVE != originWhAsn.getStatus())) {
            log.error("CheckInQueueManagerImpl.assignPlatform -> whAsnDao.findWhAsnById error, asnId is:[{}], platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}], originWhAsn is:[{}]", asnId, platformId, ouId, userId, logId, originWhAsn);
            throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
        }
        if (log.isDebugEnabled()) {
            log.debug("CheckInQueueManagerImpl.assignPlatform -> whAsnDao.findWhAsnById result, asnId is:[{}], platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}], originWhAsn is:[{}]", asnId, platformId, ouId, userId, logId, originWhAsn);
        }

        // 更新签入队列
        boolean isInCheckInQueue = this.updateByDeleteCheckInQueue(asnId, ouId, userId, logId);
        // 修改asn实际到货时间
        // ASN改为已签入
        originWhAsn.setStatus(PoAsnStatus.ASN_CHECKIN);
        originWhAsn.setModifiedId(userId);
        if (!isInCheckInQueue) {
            originWhAsn.setDeliveryTime(new Date());
        }
        // 更新whAsn信息
        originWhAsn = this.updateWhAsn(asnId, ouId, userId, logId, originWhAsn);

        if (log.isDebugEnabled()) {
            log.debug("CheckInQueueManagerImpl.assignPlatform -> asnReserveDao.findAsnReserveByAsnId invoke, asnId is:[{}], platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", asnId, platformId, ouId, userId, logId);
        }
        // 查询原始ASN预约信息
        AsnReserve originAsnReserve = asnReserveDao.findAsnReserveByAsnId(asnId, ouId);
        if (log.isDebugEnabled()) {
            log.debug("CheckInQueueManagerImpl.assignPlatform -> asnReserveDao.findAsnReserveByAsnId result, asnId is:[{}], platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}], originAsnReserve is:[{}]", asnId, ouId, userId, logId,
                    originAsnReserve);
        }
        if (null != originAsnReserve) {
            // 修改asn预约实际到货时间
            originAsnReserve.setDeliveryTime(originWhAsn.getDeliveryTime());
            // ASN预约改为已完成
            originAsnReserve.setStatus(PoAsnStatus.ASN_RESERVE_FINISH);
            originAsnReserve.setModifiedId(userId);
            // 更新asn预约信息
            this.updateAsnReserve(ouId, userId, logId, originAsnReserve);
        }

        // 分配月台
        Long resultCount = this.assignPlatform(platformId, ouId, userId, logId, originWhAsn);

        if (log.isInfoEnabled()) {
            log.info("CheckInQueueManagerImpl.assignPlatform end, asnId is:[{}], platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", asnId, platformId, ouId, userId, logId);
        }

        return resultCount;
    }

    /**
     * 释放月台
     * 
     * @author mingwei.xie
     * @param platformId
     * @param ouId
     * @param userId
     * @param logId
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Long freePlatform(Long platformId, Long ouId, Long userId, String logId) {
        if (log.isInfoEnabled()) {
            log.info("CheckInQueueManagerImpl.freePlatform start, platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", platformId, ouId, userId, logId);
        }
        if (null == platformId || null == ouId || null == userId) {
            log.error("CheckInQueueManagerImpl.freePlatform param is null, platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", platformId, ouId, userId, logId);
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }

        Platform originPlatform = platformDao.findByIdExt(platformId, ouId);
        if (null == originPlatform) {
            log.error("CheckInQueueManagerImpl freePlatform failed, originPlatform is null, platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", platformId, ouId, userId, logId);
            throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
        }
        if (!originPlatform.getIsOccupied() || !BaseModel.LIFECYCLE_NORMAL.equals(originPlatform.getLifecycle())) {
            log.error("CheckInQueueManagerImpl freePlatform failed, originPlatform invalid, platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}], originPlatform is:[{}]", platformId, ouId, userId, logId, originPlatform);
            throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
        }
        originPlatform.setIsOccupied(false);
        originPlatform.setOccupationCode(null);
        originPlatform.setModifiedId(userId);

        if (log.isDebugEnabled()) {
            log.debug("CheckInQueueManagerImpl.freePlatform -> platformDao.freePlatform invoke, platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", platformId, ouId, userId, logId);
        }
        long resultCount = platformDao.freePlatform(originPlatform);
        if (resultCount != 1) {
            log.error("CheckInQueueManagerImpl.freePlatform -> platformDao.freePlatform  update to sharedDB failed, update count != 1, platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", platformId, ouId, userId, logId);
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
        insertGlobalLog(Constants.GLOBAL_LOG_UPDATE, originPlatform, ouId, userId, null, null);

        if (log.isInfoEnabled()) {
            log.info("CheckInQueueManagerImpl.freePlatform end, vacantPlatformList is:[{}], platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", platformId, ouId, userId, logId);
        }
        return resultCount;
    }

    /**
     * 保存分配月台
     * 
     * @author mingwei.xie
     * @param platformId
     * @param ouId
     * @param userId
     * @param logId
     * @param originWhAsn
     * @return 更新数量
     */
    private Long assignPlatform(Long platformId, Long ouId, Long userId, String logId, WhAsn originWhAsn) {
        if (log.isDebugEnabled()) {
            log.debug("CheckInQueueManagerImpl.assignPlatform -> platformDao.findByIdExt invoke, platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", platformId, ouId, userId, logId);
        }
        Platform originPlatform = platformDao.findByIdExt(platformId, ouId);
        if (null == originPlatform || originPlatform.getIsOccupied() || !BaseModel.LIFECYCLE_NORMAL.equals(originPlatform.getLifecycle())) {
            log.error("CheckInQueueManagerImpl assignPlatform failed, originPlatform is null, platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}], originPlatform is:[{}]", platformId, ouId, userId, logId, originPlatform);
            throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
        }
        if (log.isDebugEnabled()) {
            log.debug("CheckInQueueManagerImpl.assignPlatform -> platformDao.findByIdExt result, platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}], originPlatform is:[{}]", platformId, ouId, userId, logId, originPlatform);
        }

        originPlatform.setIsOccupied(true);
        // 月台占用码使用asn内部编码
        originPlatform.setOccupationCode(originWhAsn.getAsnCode());
        originPlatform.setModifiedId(userId);
        if (log.isDebugEnabled()) {
            log.debug("CheckInQueueManagerImpl.assignPlatform -> platformDao.assignPlatform  update to sharedDB, platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}], originPlatform is:[{}]", platformId, ouId, userId, logId, originPlatform);
        }
        Long resultCount = platformDao.assignPlatform(originPlatform);
        if (resultCount != 1) {
            log.error("CheckInQueueManagerImpl.assignPlatform -> platformDao.assignPlatform  update to sharedDB failed, update count != 1, platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}], originPlatform is:[{}]", platformId, ouId, userId,
                    logId, originPlatform);
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
        insertGlobalLog(Constants.GLOBAL_LOG_UPDATE, originPlatform, ouId, userId, null, null);
        return resultCount;
    }

    /**
     * 更新ASN信息
     *
     * @author mingwei.xie
     * @param asnId
     * @param ouId
     * @param userId
     * @param logId
     * @param originWhAsn
     * @return 更新后的whAsn
     */
    private WhAsn updateWhAsn(Long asnId, Long ouId, Long userId, String logId, WhAsn originWhAsn) {
        if (log.isInfoEnabled()) {
            log.debug("CheckInQueueManagerImpl.updateWhAsn start, originWhAsn is:[{}], logId is:[{}]", originWhAsn, logId);
        }
        if (log.isDebugEnabled()) {
            log.debug("CheckInQueueManagerImpl.updateWhAsn -> whAsnDao.saveOrUpdateByVersion, update to sharedDB, originWhAsn is:[{}], logId is:[{}]", originWhAsn, logId);
        }
        int asnUpdateCount = whAsnDao.saveOrUpdateByVersion(originWhAsn);
        if (asnUpdateCount != 1) {
            log.debug("CheckInQueueManagerImpl.updateWhAsn -> whAsnDao.saveOrUpdateByVersion error asnUpdateCount != 1, originWhAsn is:[{}], logId is:[{}], asnUpdateCount is:[{}]", originWhAsn, logId, asnUpdateCount);
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
        // 插入系统日志
        insertGlobalLog(Constants.GLOBAL_LOG_UPDATE, originWhAsn, ouId, userId, null, null);
        // 重新获取asn信息，获取数据库保存的预约时间和实际到货时间,Java的时间精度和数据库时间精度不同
        originWhAsn = whAsnDao.findWhAsnById(asnId, ouId);
        if (null == originWhAsn) {
            log.error("CheckInQueueManagerImpl.updateWhAsn -> whAsnDao.findWhAsnById error, result is null, asnId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", asnId, ouId, userId, logId);
            throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
        }

        if (log.isInfoEnabled()) {
            log.info("CheckInQueueManagerImpl.updateWhAsn end, originWhAsn is:[{}], logId is:[{}]", originWhAsn, logId);
        }
        return originWhAsn;
    }

    /**
     * 根系asn预约信息
     *
     * @author mingwei.xie
     * @param ouId
     * @param userId
     * @param logId
     * @param originAsnReserve
     * @return
     */
    private long updateAsnReserve(Long ouId, Long userId, String logId, AsnReserve originAsnReserve) {
        if (log.isDebugEnabled()) {
            log.debug("CheckInQueueManagerImpl.updateAsnReserve -> asnReserveDao.saveOrUpdateByVersion invoke, update to sharedDB, logId is:[{}], originAsnReserve is:[{}]", logId, originAsnReserve);
        }
        int asnReserveUpdateCount = asnReserveDao.saveOrUpdateByVersion(originAsnReserve);
        if (asnReserveUpdateCount != 1) {
            log.error("CheckInQueueManagerImpl.updateAsnReserve -> asnReserveDao.saveOrUpdateByVersion,  update to sharedDB failed, update asnReserveUpdateCount != 1, logId is:[{}], originAsnReserve is:[{}], asnReserveUpdateCount is:[{}]", logId,
                    originAsnReserve, asnReserveUpdateCount);
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }

        insertGlobalLog(Constants.GLOBAL_LOG_UPDATE, originAsnReserve, ouId, userId, null, null);
        return asnReserveUpdateCount;
    }

    /**
     * 删除更新签入队列
     *
     * @author mingwei.xie
     * @param asnId
     * @param ouId
     * @param userId
     * @param logId
     * @return asn是否存在队列中
     */
    private boolean updateByDeleteCheckInQueue(Long asnId, Long ouId, Long userId, String logId) {
        if (log.isDebugEnabled()) {
            log.debug("CheckInQueueManagerImpl.updateByDeleteCheckInQueue start,, asnId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", asnId, ouId, userId, logId);
        }
        // 移除等待序列中的asn信息
        CheckInQueueCommand originCheckInQueueCommand = checkInQueueDao.findCheckInQueueCommandByAsnId(asnId, ouId);
        if (null != originCheckInQueueCommand) {
            CheckInQueue originCheckInQueue = checkInQueueDao.findByIdExt(originCheckInQueueCommand.getId(), originCheckInQueueCommand.getOuId());
            if (null == originCheckInQueue) {
                log.error("CheckInQueueManagerImpl.updateByDeleteCheckInQueue -> checkInQueueDao.findByIdExt error, originCheckInQueue is null,   logId is:[{}], checkInQueueCommand is:[{}]", logId, originCheckInQueueCommand);
                throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
            }
            int count = checkInQueueDao.deleteById(originCheckInQueue.getId(), originCheckInQueue.getOuId());
            if (count != 1) {
                log.error("CheckInQueueManagerImpl.updateByDeleteCheckInQueue -> checkInQueueDao.deleteById failed, delete count != 1, asnId is:[{}],ouId is:[{}], userId is:[{}], logId is:[{}], deleteCheckInQueue is:[{}]", asnId, ouId, userId, logId,
                        originCheckInQueue);
                throw new BusinessException(ErrorCodes.DELETE_DATA_ERROR);
            }
            insertGlobalLog(Constants.GLOBAL_LOG_DELETE, originCheckInQueue, ouId, userId, null, null);
        }

        if (log.isDebugEnabled()) {
            log.debug("CheckInQueueManagerImpl.updateByDeleteCheckInQueue -> checkInQueueDao.getListOrderBySequenceAsc invoke, asnId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", asnId, ouId, userId, logId);
        }
        // 签入队列
        List<CheckInQueueCommand> checkInQueueCommandList = checkInQueueDao.getListOrderBySequenceAsc(ouId);
        if (log.isDebugEnabled()) {
            log.debug("CheckInQueueManagerImpl.updateByDeleteCheckInQueue -> checkInQueueDao.getListOrderBySequenceAsc result, asnId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}], checkInQueueCommandList is:[{}]", asnId, ouId, userId, logId,
                    checkInQueueCommandList);
        }
        // 排序
        Collections.sort(checkInQueueCommandList);
        for (int index = 0; index < checkInQueueCommandList.size(); index++) {
            CheckInQueueCommand tempCommand = checkInQueueCommandList.get(index);
            if (null != tempCommand.getId()) {
                if (!tempCommand.getSequence().equals(index + 1)) {
                    CheckInQueue originCheckInQueue = checkInQueueDao.findByIdExt(tempCommand.getId(), tempCommand.getOuId());
                    originCheckInQueue.setSequence(index + 1);
                    originCheckInQueue.setModifiedId(userId);
                    insertGlobalLog(Constants.GLOBAL_LOG_UPDATE, originCheckInQueue, ouId, userId, null, null);
                    // TODO 索引唯一性
                    int checkInQueueUpdateCount = checkInQueueDao.updateByVersionExt(originCheckInQueue);
                    if (checkInQueueUpdateCount != 1) {
                        log.error(
                                "CheckInQueueManagerImpl.updateByDeleteCheckInQueue -> checkInQueueDao.updateByVersionExt failed, update checkInQueueUpdateCount != 1, asnId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}], originCheckInQueue is:[{}]",
                                asnId, ouId, userId, logId, originCheckInQueue);
                        throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                    }
                }
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("CheckInQueueManagerImpl.updateByDeleteCheckInQueue end,, asnId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", asnId, ouId, userId, logId);
        }

        return null != originCheckInQueueCommand;
    }



}
