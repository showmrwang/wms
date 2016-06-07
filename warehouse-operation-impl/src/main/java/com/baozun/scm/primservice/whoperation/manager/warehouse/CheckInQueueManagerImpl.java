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

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import lark.common.annotation.MoreDB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.warehouse.AsnReserveCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.CheckInQueueCommand;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.constant.PoAsnStatus;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhAsnDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.AsnReserveDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.CheckInQueueDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.PlatformDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.model.BaseModel;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsn;
import com.baozun.scm.primservice.whoperation.model.warehouse.AsnReserve;
import com.baozun.scm.primservice.whoperation.model.warehouse.CheckInQueue;
import com.baozun.scm.primservice.whoperation.model.warehouse.Platform;

@Service("checkInQueueManager")
@Transactional
public class CheckInQueueManagerImpl implements CheckInQueueManager {
    public static final Logger log = LoggerFactory.getLogger(CheckInQueueManagerImpl.class);

    @Autowired
    CheckInQueueDao checkInQueueDao;

    @Autowired
    AsnReserveDao asnReserveDao;

    @Autowired
    WhAsnDao whAsnDao;

    @Autowired
    PlatformDao platformDao;

    /**
     * 根据asn预约信息查找等待队列信息
     * 
     * @author mingwei.xie
     * @param reserveId
     * @param ouId
     * @param logId
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public CheckInQueueCommand findCheckInQueueByAsnReserveId(Long reserveId, Long ouId, String logId) {
        if (log.isInfoEnabled()) {
            log.info("CheckInQueueManagerImpl.findCheckInQueueByAsnReserveId start, reserveId is:[{}], ouId is:[{}], logId is:[{}]", reserveId, ouId, logId);
        }
        if (null == reserveId || null == ouId) {
            log.error("CheckInQueueManagerImpl.findCheckInQueueByAsnReserveId param is null, param reserveId is:[{}], ouId is:[{}], logId is:[{}]", reserveId, ouId, logId);
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        if (log.isDebugEnabled()) {
            log.debug("CheckInQueueManagerImpl.findCheckInQueueByAsnReserveId -> checkInQueueDao.findCheckInQueueByAsnReserveId invoke, reserveId is:[{}], ouId is:[{}], logId is:[{}]", reserveId, ouId, logId);
        }
        CheckInQueueCommand checkInQueueCommand = checkInQueueDao.findCheckInQueueByAsnReserveId(reserveId, ouId);
        if (log.isDebugEnabled()) {
            log.debug("CheckInQueueManagerImpl.findCheckInQueueByAsnReserveId -> checkInQueueDao.findCheckInQueueByAsnReserveId result, reserveId is:[{}], ouId is:[{}], logId is:[{}], checkInQueueCommand is:[{}]", reserveId, ouId, logId, checkInQueueCommand);
        }
        if (log.isInfoEnabled()) {
            log.info("CheckInQueueManagerImpl.findCheckInQueueByAsnReserveId end, asnReserveId is:[{}], ouId is:[{}], logId is:[{}]", reserveId, ouId, logId);
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
            log.info("CheckInQueueManagerImpl.addInToCheckInQueue start, asnId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", asnId, ouId, userId, logId);
        }
        if (null == asnId || null == ouId || null == userId) {
            log.error("CheckInQueueManagerImpl.addInToCheckInQueue param is null, param asnId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", asnId, ouId, userId, logId);
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }

        if (log.isDebugEnabled()) {
            log.debug("CheckInQueueManagerImpl.addInToCheckInQueue -> whAsnDao.findWhAsnById invoke, asnId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", asnId, ouId, userId, logId);
        }
        // ASN信息
        WhAsn originWhAsn = whAsnDao.findWhAsnById(asnId, ouId);
        if (null == originWhAsn) {
            log.error("CheckInQueueManagerImpl.addInToCheckInQueue -> whAsnDao.findWhAsnById error, result is null, asnId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", asnId, ouId, userId, logId);
            throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
        }
        if (log.isDebugEnabled()) {
            log.debug("CheckInQueueManagerImpl.addInToCheckInQueue -> whAsnDao.findWhAsnById result, asnId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}], originWhAsn is:[{}]", asnId, ouId, userId, logId, originWhAsn);
        }
        // 检查ASN状态
        if (Arrays.asList(PoAsnStatus.ASN_CHECKIN, PoAsnStatus.ASN_RCVD, PoAsnStatus.ASN_RCVD_FINISH, PoAsnStatus.ASN_CANCELED, PoAsnStatus.ASN_CLOSE).contains(originWhAsn.getStatus())) {
            log.error("CheckInQueueManagerImpl.addInToCheckInQueue -> whAsnDao.findWhAsnById error, originWhAsn status error, asnId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}], originWhAsn is:[{}]", asnId, ouId, userId, logId, originWhAsn);
            throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
        }

        if (log.isDebugEnabled()) {
            log.debug("CheckInQueueManagerImpl.addInToCheckInQueue -> asnReserveDao.findAsnReserveByAsnId invoke, asnId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", asnId, ouId, userId, logId);
        }
        // ASN预约信息
        AsnReserve originAsnReserve = asnReserveDao.findAsnReserveByAsnId(asnId, ouId);
        if (null == originAsnReserve || PoAsnStatus.ASN_RESERVE_NEW != originAsnReserve.getStatus()) {
            log.error("CheckInQueueManagerImpl.addInToCheckInQueue -> asnReserveDao.findAsnReserveByAsnId error, asnId is:[{}], ouId is:[{}], logId is:[{}], originAsnReserve IS:[{}]", asnId, ouId, logId, originAsnReserve);
            throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
        }

        // 修改asn预约状态和实际到货时间
        // ASN预约改为已签入
        originAsnReserve.setStatus(PoAsnStatus.ASN_RESERVE_CHECKIN);
        originAsnReserve.setDeliveryTime(new Date());
        originAsnReserve.setModifiedId(userId);
        if (log.isDebugEnabled()) {
            log.debug("CheckInQueueManagerImpl.addInToCheckInQueue -> asnReserveDao.saveOrUpdateByVersion, update to sharedDB, originAsnReserve is:[{}], logId is:[{}]", originAsnReserve, logId);
        }
        int reserveUpdateCount = asnReserveDao.saveOrUpdateByVersion(originAsnReserve);
        if (reserveUpdateCount != 1) {
            log.debug("CheckInQueueManagerImpl.addInToCheckInQueue -> asnReserveDao.saveOrUpdateByVersion error reserveUpdateCount != 1, originAsnReserve is:[{}], logId is:[{}], reserveUpdateCount is:[{}]", originAsnReserve, logId, reserveUpdateCount);
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }

        originAsnReserve = asnReserveDao.findAsnReserveByAsnId(asnId, ouId);
        if (null == originAsnReserve) {
            log.error("CheckInQueueManagerImpl.addInToCheckInQueue -> asnReserveDao.findAsnReserveByAsnId error, asnId is:[{}], ouId is:[{}], logId is:[{}], originAsnReserve IS:[{}]", asnId, ouId, logId);
            throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
        }


        if (log.isDebugEnabled()) {
            log.debug("CheckInQueueManagerImpl.addInToCheckInQueue -> checkInQueueDao.getListOrderBySequenceAsc invoke, ouId is:[{}], logId is:[{}]", ouId, logId);
        }
        // 签入队列
        List<CheckInQueueCommand> checkInQueueCommandList = checkInQueueDao.getListOrderBySequenceAsc(ouId);
        if (log.isDebugEnabled()) {
            log.debug("CheckInQueueManagerImpl.addInToCheckInQueue -> checkInQueueDao.getListOrderBySequenceAsc result, ouId is:[{}], logId is:[{}], checkInQueueCommandList is:[{}]", ouId, logId, checkInQueueCommandList);
        }

        // 新增的需等待的签入
        CheckInQueueCommand checkInQueueCommand = new CheckInQueueCommand();
        checkInQueueCommand.setReserveId(originAsnReserve.getId());
        checkInQueueCommand.setOuId(ouId);
        checkInQueueCommand.setCreateTime(new Date());
        checkInQueueCommand.setCreatedId(userId);
        checkInQueueCommand.setModifiedId(userId);
        checkInQueueCommand.setLastModifyTime(new Date());
        checkInQueueCommand.setEta(originAsnReserve.getEta());
        checkInQueueCommand.setDeliveryTime(originAsnReserve.getDeliveryTime());
        checkInQueueCommand.setLevel(originAsnReserve.getLevel());

        // 加入等待队列
        checkInQueueCommandList.add(checkInQueueCommand);
        // 排序
        Collections.sort(checkInQueueCommandList);
        int waitNum = 0;
        if (log.isDebugEnabled()) {
            log.debug("CheckInQueueManagerImpl.addInToCheckInQueue loop checkInQueueCommandList, asnId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}], checkInQueueCommandList is:[{}]", asnId, ouId, userId, logId, checkInQueueCommandList);
        }
        for (int index = checkInQueueCommandList.size() - 1; index > 0; index--) {
            CheckInQueueCommand tempCommand = checkInQueueCommandList.get(index);
            if (null != tempCommand.getId()) {
                if (log.isDebugEnabled()) {
                    log.debug("CheckInQueueManagerImpl.addInToCheckInQueue loop checkInQueueCommandList, checkInQueue is exist, update to sharedDB, tempCommand is:[{}], logId is:[{}]", tempCommand, logId);
                }
                if (!tempCommand.getSequence().equals(index + 1)) {
                    tempCommand.setSequence(index + 1);
                    tempCommand.setModifiedId(userId);
                    if (log.isDebugEnabled()) {
                        log.debug("CheckInQueueManagerImpl.addInToCheckInQueue -> checkInQueueDao.updateByVersionExt, update checkInQueue sequence,  update to sharedDB, tempCommand is:[{}], logId is:[{}]", tempCommand, logId);
                    }
                    int count = checkInQueueDao.updateByVersionExt(tempCommand);
                    if (count != 1) {
                        log.error("CheckInQueueManagerImpl.addInToCheckInQueue -> checkInQueueDao.updateByVersionExt failed, update count != 1, asnId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}], updateCheckInQueueCommand is:[{}]", asnId, ouId,
                                userId, logId, tempCommand);
                        throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                    }
                }
            } else {
                waitNum = index;
            }
        }

        CheckInQueue checkInQueue = new CheckInQueue();
        checkInQueue.setReserveId(checkInQueueCommand.getReserveId());
        checkInQueue.setOuId(checkInQueueCommand.getOuId());
        checkInQueue.setCreateTime(checkInQueueCommand.getCreateTime());
        checkInQueue.setCreatedId(checkInQueueCommand.getCreatedId());
        checkInQueue.setModifiedId(checkInQueueCommand.getModifiedId());
        checkInQueue.setLastModifyTime(checkInQueueCommand.getLastModifyTime());
        checkInQueue.setSequence(waitNum + 1);
        if (log.isDebugEnabled()) {
            log.debug("CheckInQueueManagerImpl.addInToCheckInQueue -> checkInQueueDao.insert,insert to sharedDB, checkInQueue is:[{}], logId is:[{}]", checkInQueue, logId);
        }
        long count = checkInQueueDao.insert(checkInQueue);
        if (count != 1) {
            log.debug("CheckInQueueManagerImpl.addInToCheckInQueue -> checkInQueueDao.insert error count != 1, checkInQueue is:[{}], logId is:[{}], count is:[{}]", checkInQueue, logId, count);
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }


        /*
         * // 修改ASN状态和实际到货时间 // ASN状态分配了月台才是签入 originWhAsn.setStatus(PoAsnStatus.ASN_CHECKIN);
         * originWhAsn.setModifiedId(userId); originWhAsn.setLastModifyTime(new Date());
         * originWhAsn.setDeliveryTime(new Date()); if (log.isDebugEnabled()) { log.debug(
         * "CheckInQueueManagerImpl.addInToCheckInQueue -> whAsnDao.saveOrUpdateByVersion, update to sharedDB, originWhAsn is:[{}], logId is:[{}]"
         * , originWhAsn, logId); } int asnUpdateCount =
         * whAsnDao.saveOrUpdateByVersion(originWhAsn); if (asnUpdateCount != 1) { log.debug(
         * "CheckInQueueManagerImpl.addInToCheckInQueue -> whAsnDao.saveOrUpdateByVersion error asnUpdateCount != 1, originWhAsn is:[{}], logId is:[{}], asnUpdateCount is:[{}]"
         * , originWhAsn, logId, asnUpdateCount); throw new
         * BusinessException(ErrorCodes.UPDATE_DATA_ERROR); }
         */


        if (log.isInfoEnabled()) {
            log.info("CheckInQueueManagerImpl.addInToCheckInQueue end, asnId is:[{}], ouId is:[{}], logId is:[{}]", asnId, ouId, logId);
        }
        return waitNum;
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
        // ASN信息
        WhAsn originWhAsn = whAsnDao.findWhAsnById(asnId, ouId);
        if (null == originWhAsn) {
            log.error("CheckInQueueManagerImpl.finishCheckIn -> whAsnDao.findWhAsnById error, result is null, asnId is:[{}], platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", asnId, platformId, ouId, userId, logId);
            throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
        }
        if (log.isDebugEnabled()) {
            log.debug("CheckInQueueManagerImpl.finishCheckIn -> whAsnDao.findWhAsnById result, asnId is:[{}], platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}], originWhAsn is:[{}]", asnId, platformId, ouId, userId, logId, originWhAsn);
        }
        // 检查ASN状态
        if (Arrays.asList(PoAsnStatus.ASN_CHECKIN, PoAsnStatus.ASN_RCVD, PoAsnStatus.ASN_RCVD_FINISH, PoAsnStatus.ASN_CANCELED, PoAsnStatus.ASN_CLOSE).contains(originWhAsn.getStatus())) {
            log.error("CheckInQueueManagerImpl.finishCheckIn -> whAsnDao.findWhAsnById error, originWhAsn status error, asnId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}], originWhAsn is:[{}]", asnId, ouId, userId, logId, originWhAsn);
            throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
        }

        if (log.isDebugEnabled()) {
            log.debug("CheckInQueueManagerImpl.finishCheckIn -> asnReserveDao.findAsnReserveByAsnId invoke, asnId is:[{}], platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", asnId, platformId, ouId, userId, logId);
        }
        // ASN预约信息
        AsnReserve originAsnReserve = asnReserveDao.findAsnReserveByAsnId(asnId, ouId);
        if (null == originAsnReserve || !Arrays.asList(PoAsnStatus.ASN_RESERVE_NEW, PoAsnStatus.ASN_RESERVE_CHECKIN).contains(originAsnReserve.getStatus())) {
            log.error("CheckInQueueManagerImpl.finishCheckIn -> asnReserveDao.findAsnReserveByAsnId error, originAsnReserve status error,  asnId is:[{}], platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}], originAsnReserve is:[{}]", asnId,
                    platformId, ouId, userId, logId, originAsnReserve);
            throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
        }
        if (log.isDebugEnabled()) {
            log.debug("CheckInQueueManagerImpl.finishCheckIn -> asnReserveDao.findAsnReserveByAsnId result, asnId is:[{}], platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}], originAsnReserve is:[{}]", asnId, ouId, userId, logId,
                    originAsnReserve);
        }

        if (log.isDebugEnabled()) {
            log.debug("CheckInQueueManagerImpl.finishCheckIn -> checkInQueueDao.getListOrderBySequenceAsc invoke, asnId is:[{}], platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", asnId, platformId, ouId, userId, logId);
        }
        // 签入队列
        List<CheckInQueueCommand> checkInQueueCommandList = checkInQueueDao.getListOrderBySequenceAsc(ouId);
        if (log.isDebugEnabled()) {
            log.debug("CheckInQueueManagerImpl.finishCheckIn -> checkInQueueDao.getListOrderBySequenceAsc result, asnId is:[{}], platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}], checkInQueueCommandList is:[{}]", asnId, platformId, ouId,
                    userId, logId, checkInQueueCommandList);
        }

        // 移除等待序列中的asn信息
        boolean waitingFlag = false;
        ListIterator<CheckInQueueCommand> listIterator = checkInQueueCommandList.listIterator();
        while (listIterator.hasNext()) {
            CheckInQueueCommand checkInQueueCommand = listIterator.next();
            if (checkInQueueCommand.getReserveId().equals(originAsnReserve.getId())) {
                waitingFlag = true;
                listIterator.remove();
                int count = checkInQueueDao.deleteById(checkInQueueCommand.getId(), checkInQueueCommand.getOuId());
                if (count != 1) {
                    log.error("CheckInQueueManagerImpl.finishCheckIn -> checkInQueueDao.deleteById failed, delete count != 1, asnId is:[{}], platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}], deleteCheckInQueue is:[{}]", asnId, platformId,
                            ouId, userId, logId, checkInQueueCommand);
                    throw new BusinessException(ErrorCodes.DELETE_DATA_ERROR);
                }
                continue;
            }
            /*
            if(waitingFlag){
                checkInQueueCommand.setSequence(checkInQueueCommand.getSequence() - 1);
                checkInQueueCommand.setModifiedId(userId);
                //TODO 索引唯一性
                int count = checkInQueueDao.updateByVersionExt(checkInQueueCommand);
                if (count != 1) {
                    log.error("CheckInQueueManagerImpl.finishCheckIn -> checkInQueueDao.updateByVersionExt failed, update count != 1, asnId is:[{}], platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}], updateCheckInQueueCommand is:[{}]",
                            asnId, platformId, ouId, userId, logId, checkInQueueCommand);
                    throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                }
            }
            */
        }

        // 排序
        if (log.isDebugEnabled()) {
            log.debug("CheckInQueueManagerImpl.finishCheckIn sort checkInQueueCommandList invoke, asnId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}], checkInQueueCommandList is:[{}]", asnId, ouId, userId, logId, checkInQueueCommandList);
        }
        Collections.sort(checkInQueueCommandList);
        if (log.isDebugEnabled()) {
            log.debug("CheckInQueueManagerImpl.finishCheckIn sort checkInQueueCommandList result, asnId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}], checkInQueueCommandList is:[{}]", asnId, ouId, userId, logId, checkInQueueCommandList);
        }
        for (int index = 0; index < checkInQueueCommandList.size(); index++) {
            CheckInQueueCommand tempCommand = checkInQueueCommandList.get(index);
            if (null != tempCommand.getId()) {
                if (!tempCommand.getSequence().equals(index + 1)) {
                    tempCommand.setSequence(index + 1);
                    tempCommand.setModifiedId(userId);
                    //TODO 索引唯一性
                    int count = checkInQueueDao.updateByVersionExt(tempCommand);
                    if (count != 1) {
                        log.error("CheckInQueueManagerImpl.finishCheckIn -> checkInQueueDao.updateByVersionExt failed, update count != 1, asnId is:[{}], platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}], updateCheckInQueueCommand is:[{}]",
                                asnId, platformId, ouId, userId, logId, tempCommand);
                        throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                    }
                }
            }
        }


        // 如果是等待队列中的，则已签入
        if (!waitingFlag) {
            originAsnReserve.setDeliveryTime(new Date());
        }
        // 修改ASN预约状态和实际到货时间
        originAsnReserve.setStatus(PoAsnStatus.ASN_RESERVE_FINISH);
        originAsnReserve.setModifiedId(userId);
        int asnReserveUpdateCount = asnReserveDao.saveOrUpdateByVersion(originAsnReserve);
        if (asnReserveUpdateCount != 1) {
            log.debug("CheckInQueueManagerImpl.finishCheckIn -> asnReserveDao.saveOrUpdateByVersion error asnReserveUpdateCount != 1, originAsnReserve is:[{}], logId is:[{}], asnUpdateCount is:[{}]", originAsnReserve, logId, asnReserveUpdateCount);
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }

        // 修改ASN状态和实际到货时间
        originWhAsn.setDeliveryTime(originAsnReserve.getDeliveryTime());
        originWhAsn.setStatus(PoAsnStatus.ASN_CHECKIN);
        originWhAsn.setModifiedId(userId);
        int asnUpdateCount = whAsnDao.saveOrUpdateByVersion(originWhAsn);
        if (asnUpdateCount != 1) {
            log.debug("CheckInQueueManagerImpl.finishCheckIn -> whAsnDao.saveOrUpdateByVersion error asnReserveUpdateCount != 1, originWhAsn is:[{}], logId is:[{}], asnUpdateCount is:[{}]", originWhAsn, logId, asnUpdateCount);
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }


        Platform originPlatform = platformDao.findByIdExt(platformId, ouId);
        if (null == originPlatform) {
            log.error("PlatformManagerImpl finishCheckIn failed, originPlatform is null, asnId is:[{}], platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", asnId, platformId, ouId, userId, logId);
            throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
        }
        if (originPlatform.getIsOccupied() || !BaseModel.LIFECYCLE_NORMAL.equals(originPlatform.getLifecycle())) {
            log.error("PlatformManagerImpl finishCheckIn failed, originPlatform invalid,  asnId is:[{}], platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}], originPlatform is:[{}]", asnId, platformId, ouId, userId, logId, originPlatform);
            throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
        }

        originPlatform.setIsOccupied(true);
        // 月台占用码使用asn内部编码
        originPlatform.setOccupationCode(originWhAsn.getAsnCode());
        originPlatform.setModifiedId(userId);
        if (log.isDebugEnabled()) {
            log.debug("PlatformManagerImpl.finishCheckIn -> platformDao.assignPlatform  update to sharedDB, asnId is:[{}], platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}], originPlatform is:[{}]", asnId, platformId, ouId, userId, logId,
                    originPlatform);
        }
        Long resultCount = platformDao.assignPlatform(originPlatform);
        if (resultCount != 1) {
            log.error("PlatformManagerImpl.finishCheckIn -> platformDao.assignPlatform  update to sharedDB failed, update count != 1, asnId is:[{}], platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}], originPlatform is:[{}]", asnId,
                    platformId, ouId, userId, logId, originPlatform);
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }

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
        // ASN信息
        WhAsn originWhAsn = whAsnDao.findWhAsnById(asnReserveCommand.getAsnId(), ouId);
        if (null == originWhAsn) {
            log.error("CheckInQueueManagerImpl.simpleReserve -> whAsnDao.findWhAsnById error, result is null, asnReserveCommand is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", asnReserveCommand, ouId, userId, logId);
            throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
        }
        if (log.isDebugEnabled()) {
            log.debug("CheckInQueueManagerImpl.simpleReserve -> whAsnDao.findWhAsnById result, asnId is:[{}], asnReserveCommand is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", asnReserveCommand, ouId, userId, logId);
        }

        if (Arrays.asList(PoAsnStatus.ASN_RESERVE, PoAsnStatus.ASN_CHECKIN, PoAsnStatus.ASN_RCVD, PoAsnStatus.ASN_RCVD_FINISH, PoAsnStatus.ASN_CANCELED, PoAsnStatus.ASN_CLOSE).contains(originWhAsn.getStatus())) {
            log.error("CheckInQueueManagerImpl.simpleReserve -> whAsnDao.findWhAsnById error, asn status invalid, asnReserveCommand is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}], originWhAsn is:[{}]", asnReserveCommand, ouId, userId, logId,
                    originWhAsn);
            throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
        }

        if (log.isDebugEnabled()) {
            log.debug("CheckInQueueManagerImpl.finishCheckIn -> asnReserveDao.findAsnReserveByAsnId invoke, asnReserveCommand is:[{}], asnId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", asnReserveCommand, asnReserveCommand.getAsnId(), ouId,
                    userId, logId);
        }
        // ASN预约信息
        AsnReserve originAsnReserve = asnReserveDao.findAsnReserveByAsnId(asnReserveCommand.getAsnId(), ouId);
        if (log.isDebugEnabled()) {
            log.debug("CheckInQueueManagerImpl.finishCheckIn -> asnReserveDao.findAsnReserveByAsnId result, asnReserveCommand is:[{}], asnId is:[{}], originAsnReserve is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", asnReserveCommand,
                    asnReserveCommand.getAsnId(), originAsnReserve, ouId, userId, logId);
        }
        if (null != originAsnReserve) {
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
            originAsnReserve.setEta(new Date());
            originAsnReserve.setEstParkingTime(null);

            int count = asnReserveDao.saveOrUpdateByVersion(originAsnReserve);
            if (count != 1) {
                log.error("CheckInQueueManagerImpl.simpleReserve -> asnReserveDao.saveOrUpdateByVersion  update to sharedDB failed, update count != 1, logId is:[{}], originAsnReserve is:[{}], count is:[{}]", logId, originAsnReserve, count);
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }

        } else {
            AsnReserve asnReserve = new AsnReserve();
            asnReserve.setCode(asnReserveCommand.getCode());
            asnReserve.setAsnCode(originWhAsn.getAsnExtCode());
            asnReserve.setAsnId(originWhAsn.getId());
            asnReserve.setEta(new Date());
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
                log.error("CheckInQueueManagerImpl.simpleReserve -> asnReserveDao.insert,  insert to sharedDB failed, update count != 1, logId is:[{}], asnReserve is:[{}], count is:[{}]", logId, asnReserve, count);
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }

        }
        // ASN改为已预约
        // 修改预约时间， 实际到货时间
        originWhAsn.setStatus(PoAsnStatus.ASN_RESERVE);
        originWhAsn.setEta(new Date());
        originWhAsn.setDeliveryTime(null);
        originWhAsn.setModifiedId(userId);
        int count = whAsnDao.saveOrUpdateByVersion(originWhAsn);
        if (count != 1) {
            log.error("CheckInQueueManagerImpl.simpleReserve -> whAsnDao.saveOrUpdateByVersion,  update to sharedDB failed, update count != 1, logId is:[{}], originWhAsn is:[{}], count is:[{}]", logId, originWhAsn, count);
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }

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
            log.info("PlatformManagerImpl.assignPlatform start, asnId is:[{}], platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", asnId, platformId, ouId, userId, logId);
        }
        if (null == asnId || null == platformId || null == ouId || null == userId) {
            log.error("PlatformManagerImpl.assignPlatform param is null, asnId is:[{}], platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", asnId, platformId, ouId, userId, logId);
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }

        if (log.isDebugEnabled()) {
            log.debug("CheckInQueueManagerImpl.assignPlatform -> whAsnDao.findWhAsnById invoke, asnId is:[{}], platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", asnId, platformId, ouId, userId, logId);
        }
        // ASN信息
        WhAsn originWhAsn = whAsnDao.findWhAsnById(asnId, ouId);
        if (null == originWhAsn) {
            log.error("CheckInQueueManagerImpl.assignPlatform -> whAsnDao.findWhAsnById error, result is null, asnId is:[{}], platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", asnId, platformId, ouId, userId, logId);
            throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
        }
        if (log.isDebugEnabled()) {
            log.debug("CheckInQueueManagerImpl.assignPlatform -> whAsnDao.findWhAsnById result, asnId is:[{}], platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}], originWhAsn is:[{}]", asnId, platformId, ouId, userId, logId, originWhAsn);
        }


        if (log.isDebugEnabled()) {
            log.debug("CheckInQueueManagerImpl.assignPlatform -> asnReserveDao.findAsnReserveByAsnId invoke, asnId is:[{}], platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", asnId, platformId, ouId, userId, logId);
        }
        // ASN预约信息
        AsnReserve originAsnReserve = asnReserveDao.findAsnReserveByAsnId(asnId, ouId);
        if (log.isDebugEnabled()) {
            log.debug("CheckInQueueManagerImpl.assignPlatform -> asnReserveDao.findAsnReserveByAsnId result, asnId is:[{}], platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}], originAsnReserve is:[{}]", asnId, ouId, userId, logId,
                    originAsnReserve);
        }

        // 已签入的asn不允许再次分配月台
        if (Arrays.asList(PoAsnStatus.ASN_CHECKIN, PoAsnStatus.ASN_RCVD, PoAsnStatus.ASN_RCVD_FINISH, PoAsnStatus.ASN_CANCELED, PoAsnStatus.ASN_CLOSE).contains(originWhAsn.getStatus())) {
            log.error("CheckInQueueManagerImpl.assignPlatform asn status error, asnId is:[{}], platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}], originWhAsn is:[{}]， originAsnReserve is:[{}]", asnId, platformId, ouId, userId, logId,
                    originWhAsn, originAsnReserve);
            throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
        }

        if (null != originAsnReserve) {
            if (log.isDebugEnabled()) {
                log.debug("CheckInQueueManagerImpl.assignPlatform -> checkInQueueDao.getListOrderBySequenceAsc invoke, asnId is:[{}], platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", asnId, platformId, ouId, userId, logId);
            }
            // 签入队列
            List<CheckInQueueCommand> checkInQueueCommandList = checkInQueueDao.getListOrderBySequenceAsc(ouId);
            if (log.isDebugEnabled()) {
                log.debug("CheckInQueueManagerImpl.assignPlatform -> checkInQueueDao.getListOrderBySequenceAsc result, asnId is:[{}], platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}], checkInQueueCommandList is:[{}]", asnId, platformId,
                        ouId, userId, logId, checkInQueueCommandList);
            }

            boolean waitingFlag = false;
            // 移除等待序列中的asn信息，更新排序
            ListIterator<CheckInQueueCommand> listIterator = checkInQueueCommandList.listIterator();
            while (listIterator.hasNext()) {
                CheckInQueueCommand checkInQueueCommand = listIterator.next();
                if (checkInQueueCommand.getReserveId().equals(originAsnReserve.getId())) {
                    waitingFlag = true;
                    listIterator.remove();
                    int count = checkInQueueDao.deleteById(checkInQueueCommand.getId(), checkInQueueCommand.getOuId());
                    if (count != 1) {
                        log.error("CheckInQueueManagerImpl.assignPlatform -> checkInQueueDao.deleteById failed, delete count != 1, asnId is:[{}], platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}], deleteCheckInQueue is:[{}]", asnId,
                                platformId, ouId, userId, logId, checkInQueueCommand);
                        throw new BusinessException(ErrorCodes.DELETE_DATA_ERROR);
                    }
                    continue;
                }
                /*
                if(waitingFlag){
                    checkInQueueCommand.setSequence(checkInQueueCommand.getSequence() - 1);
                    checkInQueueCommand.setModifiedId(userId);
                    //TODO 索引唯一性
                    int count = checkInQueueDao.updateByVersionExt(checkInQueueCommand);
                    if (count != 1) {
                        log.error(
                                "CheckInQueueManagerImpl.assignPlatform -> checkInQueueDao.updateByVersionExt failed, update count != 1, asnId is:[{}], platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}], updateCheckInQueueCommand is:[{}]",
                                asnId, platformId, ouId, userId, logId, checkInQueueCommand);
                        throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                    }
                }
                */
            }

            // 排序
            Collections.sort(checkInQueueCommandList);
            for (int index = 0; index < checkInQueueCommandList.size(); index++) {
                CheckInQueueCommand tempCommand = checkInQueueCommandList.get(index);
                if (null != tempCommand.getId()) {
                    if (!tempCommand.getSequence().equals(index + 1)) {
                        tempCommand.setSequence(index + 1);
                        tempCommand.setModifiedId(userId);
                        //TODO 索引唯一性
                        int count = checkInQueueDao.updateByVersionExt(tempCommand);
                        if (count != 1) {
                            log.error(
                                    "CheckInQueueManagerImpl.assignPlatform -> checkInQueueDao.updateByVersionExt failed, update count != 1, asnId is:[{}], platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}], updateCheckInQueueCommand is:[{}]",
                                    asnId, platformId, ouId, userId, logId, tempCommand);
                            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                        }
                    }
                }
            }

            // 修改asn预约实际到货时间
            if (!waitingFlag) {
                originAsnReserve.setDeliveryTime(new Date());
            }
            // ASN预约改为已完成
            originAsnReserve.setStatus(PoAsnStatus.ASN_RESERVE_FINISH);
            originAsnReserve.setModifiedId(userId);
            if (log.isDebugEnabled()) {
                log.debug("CheckInQueueManagerImpl.assignPlatform -> asnReserveDao.saveOrUpdateByVersion invoke, update to sharedDB, logId is:[{}], originAsnReserve is:[{}]", logId, originAsnReserve);
            }
            int count = asnReserveDao.saveOrUpdateByVersion(originAsnReserve);
            if (count != 1) {
                log.error("CheckInQueueManagerImpl.assignPlatform -> asnReserveDao.saveOrUpdateByVersion,  update to sharedDB failed, update count != 1, logId is:[{}], originAsnReserve is:[{}], count is:[{}]", logId, originAsnReserve, count);
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
        }
        // 修改asn实际到货时间
        // ASN改为已签入
        originWhAsn.setStatus(PoAsnStatus.ASN_CHECKIN);
        originWhAsn.setModifiedId(userId);
        if (null != originAsnReserve) {
            originWhAsn.setDeliveryTime(originAsnReserve.getDeliveryTime());
        } else {
            originWhAsn.setDeliveryTime(new Date());
        }
        if (log.isDebugEnabled()) {
            log.debug("CheckInQueueManagerImpl.assignPlatform -> whAsnDao.saveOrUpdateByVersion invoke, update to sharedDB, logId is:[{}], originWhAsn is:[{}]", logId, originWhAsn);
        }
        int count = whAsnDao.saveOrUpdateByVersion(originWhAsn);
        if (count != 1) {
            log.error("CheckInQueueManagerImpl.assignPlatform -> whAsnDao.saveOrUpdateByVersion,  update to sharedDB failed, update count != 1, logId is:[{}], originWhAsn is:[{}], count is:[{}]", logId, originWhAsn, count);
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }

        if (log.isDebugEnabled()) {
            log.debug("CheckInQueueManagerImpl.assignPlatform -> platformDao.findByIdExt invoke, asnId is:[{}], platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", asnId, platformId, ouId, userId, logId);
        }
        Platform originPlatform = platformDao.findByIdExt(platformId, ouId);
        if (null == originPlatform) {
            log.error("PlatformManagerImpl assignPlatform failed, originPlatform is null, asnId is:[{}], platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", asnId, platformId, ouId, userId, logId);
            throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
        }
        if (log.isDebugEnabled()) {
            log.debug("CheckInQueueManagerImpl.assignPlatform -> platformDao.findByIdExt result, asnId is:[{}], platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}], originPlatform is:[{}]", asnId, platformId, ouId, userId, logId,
                    originPlatform);
        }

        if (originPlatform.getIsOccupied() || !BaseModel.LIFECYCLE_NORMAL.equals(originPlatform.getLifecycle())) {
            log.error("PlatformManagerImpl assignPlatform failed, originPlatform invalid,  asnId is:[{}], platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}], originPlatform is:[{}]", asnId, platformId, ouId, userId, logId, originPlatform);
            throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
        }


        originPlatform.setIsOccupied(true);
        // 月台占用码使用asn内部编码
        originPlatform.setOccupationCode(originWhAsn.getAsnCode());
        originPlatform.setModifiedId(userId);
        if (log.isDebugEnabled()) {
            log.debug("PlatformManagerImpl.assignPlatform -> platformDao.assignPlatform  update to sharedDB, asnId is:[{}], platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}], originPlatform is:[{}]", asnId, platformId, ouId, userId, logId,
                    originPlatform);
        }
        Long resultCount = platformDao.assignPlatform(originPlatform);
        if (resultCount != 1) {
            log.error("PlatformManagerImpl.assignPlatform -> platformDao.assignPlatform  update to sharedDB failed, update count != 1, asnId is:[{}], platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}], originPlatform is:[{}]", asnId,
                    platformId, ouId, userId, logId, originPlatform);
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }

        if (log.isInfoEnabled()) {
            log.info("PlatformManagerImpl.assignPlatform end, asnId is:[{}], platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", asnId, platformId, ouId, userId, logId);
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
            log.info("PlatformManagerImpl.freePlatform start, platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", platformId, ouId, userId, logId);
        }
        if (null == platformId || null == ouId || null == userId) {
            log.error("PlatformManagerImpl.freePlatform param is null, platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", platformId, ouId, userId, logId);
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }

        Platform originPlatform = platformDao.findByIdExt(platformId, ouId);
        if (null == originPlatform) {
            log.error("PlatformManagerImpl assignPlatform failed, originPlatform is null, platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", platformId, ouId, userId, logId);
            throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
        }
        if (!originPlatform.getIsOccupied() || !BaseModel.LIFECYCLE_NORMAL.equals(originPlatform.getLifecycle())) {
            log.error("PlatformManagerImpl assignPlatform failed, originPlatform invalid, platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}], originPlatform is:[{}]", platformId, ouId, userId, logId, originPlatform);
            throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
        }
        originPlatform.setIsOccupied(false);
        originPlatform.setOccupationCode(null);
        originPlatform.setModifiedId(userId);

        if (log.isDebugEnabled()) {
            log.debug("PlatformManagerImpl.freePlatform -> checkInQueueManager.freePlatform invoke, platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", platformId, ouId, userId, logId);
        }
        long resultCount = platformDao.freePlatform(originPlatform);
        if (resultCount != 1) {
            log.error("PlatformManagerImpl.freePlatform -> platformDao.freePlatform  update to sharedDB failed, update count != 1, platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", platformId, ouId, userId, logId);
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
        if (log.isInfoEnabled()) {
            log.info("PlatformManagerImpl.freePlatform end, vacantPlatformList is:[{}], platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", platformId, ouId, userId, logId);
        }
        return resultCount;
    }



}
