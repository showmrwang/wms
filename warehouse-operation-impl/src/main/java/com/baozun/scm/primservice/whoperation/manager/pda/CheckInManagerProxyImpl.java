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
 * 
 */

package com.baozun.scm.primservice.whoperation.manager.pda;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baozun.scm.baseservice.print.command.PrintDataCommand;
import com.baozun.scm.baseservice.print.manager.printObject.PrintObjectManagerProxy;
import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnCommand;
import com.baozun.scm.primservice.whoperation.command.rule.RuleAfferCommand;
import com.baozun.scm.primservice.whoperation.command.rule.RuleExportCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.AsnReserveCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.CheckInQueueCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.RecommendPlatformCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnproxy.SelectPoAsnManagerProxy;
import com.baozun.scm.primservice.whoperation.manager.rule.RuleManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.AsnReserveManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.CheckInQueueManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.PlatformManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.StoreManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WarehouseManager;
import com.baozun.scm.primservice.whoperation.model.BaseModel;
import com.baozun.scm.primservice.whoperation.model.warehouse.AsnReserve;
import com.baozun.scm.primservice.whoperation.model.warehouse.Platform;
import com.baozun.scm.primservice.whoperation.model.warehouse.Store;
import com.baozun.scm.primservice.whoperation.model.warehouse.Warehouse;

@Service("checkInManagerProxy")
public class CheckInManagerProxyImpl extends BaseManagerImpl implements CheckInManagerProxy {
    public static final Logger log = LoggerFactory.getLogger(CheckInManagerProxyImpl.class);

    @Autowired
    private PlatformManager platformManager;

    @Autowired
    private SelectPoAsnManagerProxy selectPoAsnManagerProxy;

    @Autowired
    private WarehouseManager warehouseManager;

    @Autowired
    private StoreManager storeManager;

    @Autowired
    private AsnReserveManager asnReserveManager;

    @Autowired
    private RuleManager ruleManager;

    @Autowired
    private CheckInQueueManager checkInQueueManager;

    @Autowired
    private PrintObjectManagerProxy printObjectManagerProxy;

    /**
     * 查询仓库信息
     * 
     * @author mingwei.xie
     * @param ouId
     * @param logId
     * @return
     */
    @Override
    public Warehouse findWarehouseById(Long ouId, String logId) {
        if (log.isInfoEnabled()) {
            log.info("CheckInManagerProxyImpl.findWarehouseById start, ouId is:[{}], logId is:[{}]", ouId, logId);
        }
        if (null == ouId) {
            log.error("CheckInManagerProxyImpl.findWarehouseById param is null, param ouId is:[{}], logId is:[{}]", ouId, logId);
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        if (log.isDebugEnabled()) {
            log.debug("CheckInManagerProxyImpl.findWarehouseById -> warehouseManager.findWarehouseById invoke, ouId is:[{}], logId is:[{}]", ouId, logId);
        }
        Warehouse warehouse = warehouseManager.findWarehouseById(ouId);
        if (null == warehouse) {
            log.error("CheckInManagerProxyImpl.findWarehouseById -> warehouseManager.findWarehouseById error, result is null, ouId is:[{}], logId is:[{}]", ouId, logId);
            throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
        }
        if (log.isDebugEnabled()) {
            log.debug("CheckInManagerProxyImpl.findWarehouseById -> warehouseManager.findWarehouseById result, ouId is:[{}], logId is:[{}]warehouse is:[{}]", ouId, logId, warehouse);
        }
        if (log.isInfoEnabled()) {
            log.info("CheckInManagerProxyImpl.findWarehouseById end, ouId is:[{}], logId is:[{}]", ouId, logId);
        }
        return warehouse;
    }

    /**
     * 模糊查询方法。 根据asnExtCode,asn状态，仓库查找asn
     * 
     * @author mingwei.xie
     * @param asnExtCode
     * @param status
     * @param ouId
     * @return
     */
    @Override
    public List<WhAsnCommand> findWhAsnListByAsnExtCode(String asnExtCode, Integer[] status, Long ouId, String logId) {
        if (log.isInfoEnabled()) {
            log.info("CheckInManagerProxyImpl.findWhAsnListByAsnExtCode start, asnExtCode is:[{}], status are:[{}], ouId is:[{}], logId is:[{}]", asnExtCode, status, ouId, logId);
        }
        if (null == asnExtCode || null == ouId) {
            log.error("CheckInManagerProxyImpl.findWhAsnListByAsnExtCode param is null, param  asnExtCode is:[{}], status are:[{}], ouId is:[{}], logId is:[{}]", asnExtCode, status, ouId, logId);
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        if (log.isDebugEnabled()) {
            log.debug("CheckInManagerProxyImpl.findWhAsnListByAsnExtCode -> selectPoAsnManagerProxy.findWhAsnListByAsnExtCode invoke, asnExtCode is:[{}], status are:[{}], ouId is:[{}], logId is:[{}]", asnExtCode, status, ouId, logId);
        }
        List<WhAsnCommand> asnCommandList = selectPoAsnManagerProxy.findWhAsnListByAsnExtCode(asnExtCode, status, ouId);
        if (log.isDebugEnabled()) {
            log.debug("CheckInManagerProxyImpl.findWhAsnListByAsnExtCode -> selectPoAsnManagerProxy.findWhAsnListByAsnExtCode result, asnExtCode is:[{}], status are:[{}], ouId is:[{}], logId is:[{}], asnCommandList is:[{}]", asnExtCode, status, ouId,
                    logId, asnCommandList);
        }
        if (log.isInfoEnabled()) {
            log.info("CheckInManagerProxyImpl.findWhAsnListByAsnExtCode end, asnExtCode is:[{}], status are:[{}], ouId is:[{}], logId is:[{}]", asnExtCode, status, ouId, logId);
        }

        return asnCommandList;
    }

    /**
     * 根据ASN的ID,OUID查找ASN
     * 
     * @author mingwei.xie
     * @param paramWhAsnCommand
     * @param logId
     * @return
     */
    @Override
    public WhAsnCommand findWhAsnById(WhAsnCommand paramWhAsnCommand, String logId) {
        if (log.isInfoEnabled()) {
            log.info("CheckInManagerProxyImpl.findWhAsnById start, paramWhAsnCommand is:[{}], logId is:[{}]", paramWhAsnCommand, logId);
        }
        if (null == paramWhAsnCommand || null == paramWhAsnCommand.getOuId() || null == paramWhAsnCommand.getId()) {
            log.error("CheckInManagerProxyImpl.findWhAsnById param is null, param paramWhAsnCommand is:[{}], logId is:[{}]", paramWhAsnCommand, logId);
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        if (log.isDebugEnabled()) {
            log.debug("CheckInManagerProxyImpl.findWhAsnById -> selectPoAsnManagerProxy.findWhAsnById invoke, paramWhAsnCommand is:[{}], logId is:[{}]", paramWhAsnCommand, logId);
        }
        WhAsnCommand originWhAsnCommand = selectPoAsnManagerProxy.findWhAsnCommandById(paramWhAsnCommand.getId(), paramWhAsnCommand.getOuId());
        if (null == originWhAsnCommand) {
            log.error("CheckInManagerProxyImpl.findWhAsnById -> selectPoAsnManagerProxy.findWhAsnById error, result is null, paramWhAsnCommand is:[{}], logId is:[{}]", paramWhAsnCommand, logId);
            throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
        }
        if (log.isDebugEnabled()) {
            log.debug("CheckInManagerProxyImpl.findWhAsnById -> selectPoAsnManagerProxy.findWhAsnById result, paramWhAsnCommand is:[{}], logId is:[{}], originWhAsnCommand is:[{}]", paramWhAsnCommand, logId, originWhAsnCommand);
        }
        if (log.isInfoEnabled()) {
            log.info("CheckInManagerProxyImpl.findWhAsnById end, paramWhAsnCommand is:[{}], logId is:[{}]", paramWhAsnCommand, logId);
        }
        return originWhAsnCommand;
    }

    /**
     * 根据Id获取店铺
     * 
     * @author mingwei.xie
     * @param storeId
     * @return
     */
    @Override
    public Store getStoreById(Long storeId, String logId) {
        if (log.isInfoEnabled()) {
            log.info("CheckInManagerProxyImpl.getStoreById start, storeId is:[{}], logId is:[{}]", storeId, logId);
        }
        if (null == storeId) {
            log.error("CheckInManagerProxyImpl.getStoreById param is null, param storeId is:[{}], logId is:[{}]", storeId, logId);
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        if (log.isDebugEnabled()) {
            log.debug("CheckInManagerProxyImpl.getStoreById -> storeManager.getStoreById invoke, storeId is:[{}], logId is:[{}]", storeId, logId);
        }
        Store store = storeManager.getStoreById(storeId);
        if (null == store) {
            log.error("CheckInManagerProxyImpl.getStoreById -> storeManager.getStoreById error, result is null, storeId is:[{}], logId is:[{}]", storeId, logId);
            throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
        }
        if (log.isDebugEnabled()) {
            log.debug("CheckInManagerProxyImpl.getStoreById -> storeManager.getStoreById result, storeId is:[{}], logId is:[{}], store:[{}]", storeId, logId, store);
        }
        if (log.isInfoEnabled()) {
            log.info("CheckInManagerProxyImpl.getStoreById end, storeId is:[{}], logId is:[{}]", store, logId);
        }
        return store;
    }

    /**
     * 更具asnId查询asn预约信息
     * 
     * @author mingwei.xie
     * @param asnId
     * @param ouId
     * @param logId
     * @return
     */
    @Override
    public AsnReserve findAsnReserveByAsnId(Long asnId, Long ouId, String logId) {
        if (log.isInfoEnabled()) {
            log.info("CheckInManagerProxyImpl.findAsnReserveByAsnId start, asnId is:[{}], ouId is:[{}], logId is:[{}]", asnId, ouId, logId);
        }
        if (null == asnId || null == ouId) {
            log.error("CheckInManagerProxyImpl.findAsnReserveByAsnId param is null, param asnId is:[{}], ouId is:[{}], logId is:[{}]", asnId, ouId, logId);
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        if (log.isDebugEnabled()) {
            log.debug("CheckInManagerProxyImpl.findAsnReserveByAsnId -> asnReserveManager.findAsnReserveByAsnId invoke, asnId is:[{}], ouId is:[{}], logId is:[{}]", asnId, ouId, logId);
        }
        AsnReserve asnReserve = asnReserveManager.findAsnReserveByAsnId(asnId, ouId, logId);
        if (log.isDebugEnabled()) {
            log.debug("CheckInManagerProxyImpl.findAsnReserveByAsnId -> asnReserveManager.findAsnReserveByAsnId result, asnId is:[{}], ouId is:[{}], logId is:[{}], asnReserve is:[{}]", asnId, ouId, logId, asnReserve);
        }
        if (log.isInfoEnabled()) {
            log.info("CheckInManagerProxyImpl.findAsnReserveByAsnId end, asnId is:[{}], ouId is:[{}], logId is:[{}]", asnId, ouId, logId);
        }
        return asnReserve;
    }

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
    public CheckInQueueCommand findCheckInQueueByAsnId(Long asnId, Long ouId, String logId) {
        if (log.isInfoEnabled()) {
            log.info("CheckInManagerProxyImpl.findCheckInQueueCommandByAsnId start, asnId is:[{}], ouId is:[{}], logId is:[{}]", asnId, ouId, logId);
        }
        if (null == asnId || null == ouId) {
            log.error("CheckInManagerProxyImpl.findCheckInQueueCommandByAsnId param is null, param asnId is:[{}], ouId is:[{}], logId is:[{}]", asnId, ouId, logId);
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        if (log.isDebugEnabled()) {
            log.debug("CheckInManagerProxyImpl.findCheckInQueueCommandByAsnId -> checkInQueueManager.findCheckInQueueCommandByAsnId invoke, asnId is:[{}], ouId is:[{}], logId is:[{}]", asnId, ouId, logId);
        }
        CheckInQueueCommand checkInQueueCommand = checkInQueueManager.findCheckInQueueByAsnId(asnId, ouId, logId);
        if (log.isDebugEnabled()) {
            log.debug("CheckInManagerProxyImpl.findCheckInQueueCommandByAsnId -> checkInQueueManager.findCheckInQueueCommandByAsnId result, asnId is:[{}], ouId is:[{}], logId is:[{}], checkInQueueCommand is:[{}]", asnId, ouId, logId, checkInQueueCommand);
        }
        if (log.isInfoEnabled()) {
            log.info("CheckInManagerProxyImpl.findCheckInQueueCommandByAsnId end, asnId is:[{}], ouId is:[{}], logId is:[{}]", asnId, ouId, logId);
        }
        return checkInQueueCommand;
    }

    /**
     * 根据asn预约号匹配月台推荐规则
     * 
     * @author mingwei.xie
     * @param asnId
     * @param ouId
     * @param logId
     * @return
     */
    @Override
    public Map<Integer, RecommendPlatformCommand> matchPlatformRule(Long asnId, Long ouId, String logId) {
        if (log.isInfoEnabled()) {
            log.info("CheckInManagerProxyImpl.matchPlatformRule start, asnId is:[{}], ouId is:[{}], logId is:[{}]", asnId, ouId, logId);
        }
        if (null == asnId || null == ouId) {
            log.error("CheckInManagerProxyImpl.matchPlatformRule param is null, param asnId is:[{}], ouId is:[{}], logId is:[{}]", asnId, ouId, logId);
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }

        // 匹配规则
        RuleAfferCommand ruleAfferCommand = new RuleAfferCommand();
        ruleAfferCommand.setOuid(ouId);
        ruleAfferCommand.setAfferAsnId(asnId);
        ruleAfferCommand.setRuleType(Constants.PLATFORM_RECOMMEND_RULE);
        ruleAfferCommand.setLogId(logId);
        if (log.isDebugEnabled()) {
            log.debug("CheckInManagerProxyImpl.matchPlatformRule -> ruleManager.ruleExport invoke, asnId is:[{}], ouId is:[{}], logId is:[{}], ruleAfferCommand is:[{}]", asnId, ouId, logId, ruleAfferCommand);
        }
        RuleExportCommand ruleExportCommand = ruleManager.ruleExport(ruleAfferCommand);
        if (null == ruleExportCommand) {
            log.error("CheckInManagerProxyImpl.matchPlatformRule -> ruleManager.ruleExport error, result is null, asnId is:[{}], ouId is:[{}], logId is:[{}], ruleAfferCommand is:[{}]", asnId, ouId, logId, ruleAfferCommand);
            throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
        }
        if (log.isDebugEnabled()) {
            log.debug("CheckInManagerProxyImpl.matchPlatformRule -> ruleManager.ruleExport result, asnId is:[{}], ouId is:[{}], logId is:[{}], ruleAfferCommand is:[{}], ruleExportCommand is:[{}]", asnId, ouId, logId, ruleAfferCommand, ruleExportCommand);
        }

        Map<Integer, RecommendPlatformCommand> exportPlatform = ruleExportCommand.getExportPlatform();
        if (null == exportPlatform) {
            log.error("CheckInManagerProxyImpl.matchPlatformRule -> ruleExportCommand.getExportPlatform error, result is null, asnId is:[{}], ouId is:[{}], logId is:[{}], ruleAfferCommand is:[{}], ruleExportCommand is:[{}]", asnId, ouId, logId,
                    ruleAfferCommand, ruleExportCommand);
            throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
        }

        if (log.isInfoEnabled()) {
            log.info("CheckInManagerProxyImpl.matchPlatformRule start, asnId is:[{}], ouId is:[{}], logId is:[{}]", asnId, ouId, logId);
        }
        return exportPlatform;
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
    public int addToCheckInQueue(Long asnId, Long ouId, Long userId, String logId) {
        if (log.isInfoEnabled()) {
            log.info("CheckInManagerProxyImpl.addToCheckInQueue start, asnId is:[{}], ouId is:[{}], logId is:[{}]", asnId, ouId, logId);
        }
        if (null == asnId || null == ouId) {
            log.error("CheckInManagerProxyImpl.addToCheckInQueue param is null, param asnId is:[{}], ouId is:[{}], logId is:[{}]", asnId, ouId, logId);
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }

        int waitNum = checkInQueueManager.addInToCheckInQueue(asnId, ouId, userId, logId);

        if (log.isInfoEnabled()) {
            log.info("CheckInManagerProxyImpl.addToCheckInQueue end, asnId is:[{}], ouId is:[{}], logId is:[{}]", asnId, ouId, logId);
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
    public void finishCheckIn(Long asnId, Long platformId, Long ouId, Long userId, String logId) {
        if (log.isInfoEnabled()) {
            log.info("CheckInManagerProxyImpl.finishCheckIn start, asnId is:[{}], platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", asnId, platformId, ouId, userId, logId);
        }
        if (null == asnId || null == platformId || null == ouId || null == userId) {
            log.error("CheckInManagerProxyImpl.finishCheckIn param is null, asnId is:[{}], platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", asnId, platformId, ouId, userId, logId);
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        if (log.isDebugEnabled()) {
            log.debug("CheckInManagerProxyImpl.finishCheckIn -> checkInQueueManager.finishCheckIn invoke, asnId is:[{}], platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", asnId, platformId, ouId, userId, logId);
        }
        PrintDataCommand printDataCommand = new PrintDataCommand();
        ArrayList<Long> platformIds = new ArrayList<Long>();
        platformIds.add(platformId);
        printDataCommand.setIdList(platformIds);
        printObjectManagerProxy.printCommonInterface(printDataCommand, Constants.PRINT_ORDER_TYPE_5, userId, ouId);
        checkInQueueManager.finishCheckIn(asnId, platformId, ouId, userId, logId);
        if (log.isInfoEnabled()) {
            log.info("CheckInManagerProxyImpl.finishCheckIn end, asnId is:[{}], platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", asnId, platformId, ouId, userId, logId);
        }

    }

    /**
     * 简易预约
     * 
     * @author mingwei.xie
     * @param asnReserveCommand
     * @param ouId
     * @param userId
     * @param logId
     */
    @Override
    public void simpleReserve(AsnReserveCommand asnReserveCommand, Long ouId, Long userId, String logId) {
        if (log.isInfoEnabled()) {
            log.info("CheckInManagerProxyImpl.simpleReserve start, asnReserveCommand is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", asnReserveCommand, ouId, userId, logId);
        }
        if (null == asnReserveCommand || null == asnReserveCommand.getAsnId() || null == asnReserveCommand.getLevel() || null == asnReserveCommand.getCode() || null == ouId || null == userId) {
            log.error("CheckInManagerProxyImpl.simpleReserve param is null, asnReserveCommand is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", asnReserveCommand, ouId, userId, logId);
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        if (log.isDebugEnabled()) {
            log.debug("CheckInManagerProxyImpl.simpleReserve -> checkInQueueManager.simpleReserve invoke, asnReserveCommand is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", asnReserveCommand, ouId, userId, logId);
        }
        checkInQueueManager.simpleReserve(asnReserveCommand, ouId, userId, logId);
        if (log.isInfoEnabled()) {
            log.info("CheckInManagerProxyImpl.simpleReserve end, asnReserveCommand is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", asnReserveCommand, ouId, userId, logId);
        }
    }

    /**
     * 获取asn预约号
     * 
     * @author mingwei.xie
     * @param ouId
     * @param logId
     * @return
     */
    public String getAsnReserveCode(Long ouId, String logId) {
        if (log.isInfoEnabled()) {
            log.info("CheckInManagerProxyImpl.getAsnReserveCode start, ouId is:[{}], logId is:[{}]", ouId, logId);
        }

        String asnReserveCode = asnReserveManager.createAsnReserveCode(ouId, logId);

        if (log.isInfoEnabled()) {
            log.info("CheckInManagerProxyImpl.getAsnReserveCode end, ouId is:[{}], logId is:[{}], asnReserveCode is:[{}]", ouId, logId, asnReserveCode);
        }
        return asnReserveCode;
    }

    /**
     * 查找空闲月台
     * 
     * @author mingwei.xie
     * @param ouId
     * @param lifecycle
     * @param logId
     * @return
     */
    public List<Platform> findVacantPlatform(Long ouId, Integer lifecycle, String logId) {
        if (log.isInfoEnabled()) {
            log.info("CheckInManagerProxyImpl.findVacantPlatform start, ouId is:[{}], lifecycle is:[{}], logId is:[{}]", ouId, lifecycle, logId);
        }
        if (null == ouId || null == lifecycle) {
            log.error("CheckInManagerProxyImpl.findVacantPlatform param is null, ouId is:[{}], lifecycle is:[{}], logId is:[{}]", ouId, lifecycle, logId);
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        if (log.isDebugEnabled()) {
            log.debug("CheckInManagerProxyImpl.findVacantPlatform -> platformManager.findVacantPlatform invoke, ouId is:[{}], lifecycle is:[{}], logId is:[{}]", ouId, lifecycle, logId);
        }
        List<Platform> vacantPlatformList = platformManager.findVacantPlatform(ouId, lifecycle);
        if (log.isInfoEnabled()) {
            log.info("CheckInManagerProxyImpl.findVacantPlatform end, vacantPlatformList is:[{}], ouId is:[{}], lifecycle is:[{}], logId is:[{}]", vacantPlatformList, ouId, lifecycle, logId);
        }

        return vacantPlatformList;
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
    @Override
    public Long assignPlatform(Long asnId, Long platformId, Long ouId, Long userId, String logId) {
        if (log.isInfoEnabled()) {
            log.info("CheckInManagerProxyImpl.assignPlatform start, asnId is:[{}], platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", asnId, platformId, ouId, userId, logId);
        }
        if (null == asnId || null == platformId || null == ouId || null == userId) {
            log.error("CheckInManagerProxyImpl.assignPlatform param is null, asnId is:[{}], platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", asnId, platformId, ouId, userId, logId);
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        if (log.isDebugEnabled()) {
            log.debug("CheckInManagerProxyImpl.assignPlatform -> checkInQueueManager.assignPlatform invoke, asnId is:[{}], platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", asnId, platformId, ouId, userId, logId);
        }
        long updateCount = checkInQueueManager.assignPlatform(asnId, platformId, ouId, userId, logId);
        if (log.isInfoEnabled()) {
            log.info("CheckInManagerProxyImpl.assignPlatform end, vacantPlatformList is:[{}], asnId is:[{}], platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", asnId, platformId, ouId, userId, logId);
        }
        PrintDataCommand printDataCommand = new PrintDataCommand();
        ArrayList<Long> platformIds = new ArrayList<Long>();
        platformIds.add(platformId);
        printDataCommand.setIdList(platformIds);
        printObjectManagerProxy.printCommonInterface(printDataCommand, Constants.PRINT_ORDER_TYPE_5, userId, ouId);
        return updateCount;
    }

    /**
     * 查找被占用的月台
     * 
     * @author mingwei.xie
     * @param ouId
     * @param lifecycle
     * @param logId
     * @return
     */
    public List<Platform> findOccupiedPlatform(Long ouId, Integer lifecycle, String logId) {
        if (log.isInfoEnabled()) {
            log.info("CheckInManagerProxyImpl.findOccupiedPlatform start, ouId is:[{}], lifecycle is:[{}], logId is:[{}]", ouId, lifecycle, logId);
        }
        if (null == ouId || null == lifecycle) {
            log.error("CheckInManagerProxyImpl.findOccupiedPlatform param is null, ouId is:[{}], lifecycle is:[{}], logId is:[{}]", ouId, lifecycle, logId);
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        if (log.isDebugEnabled()) {
            log.debug("CheckInManagerProxyImpl.findOccupiedPlatform -> platformManager.findOccupiedPlatform invoke, ouId is:[{}], lifecycle is:[{}], logId is:[{}]", ouId, lifecycle, logId);
        }
        List<Platform> occupiedPlatformList = platformManager.findOccupiedPlatform(ouId, lifecycle, logId);
        if (log.isInfoEnabled()) {
            log.info("CheckInManagerProxyImpl.findOccupiedPlatform end, occupiedPlatformList is:[{}], ouId is:[{}], lifecycle is:[{}], logId is:[{}]", occupiedPlatformList, ouId, lifecycle, logId);
        }

        return occupiedPlatformList;
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
    public Long releasePlatform(Long platformId, Long ouId, Long userId, String logId) {
        if (log.isInfoEnabled()) {
            log.info("CheckInManagerProxyImpl.releasePlatform start, platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", platformId, ouId, userId, logId);
        }
        if (null == platformId || null == ouId || null == userId) {
            log.error("CheckInManagerProxyImpl.releasePlatform param is null, platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", platformId, ouId, userId, logId);
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        if (log.isDebugEnabled()) {
            log.debug("CheckInManagerProxyImpl.releasePlatform -> checkInQueueManager.releasePlatform invoke, platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", platformId, ouId, userId, logId);
        }
        long resultCount = checkInQueueManager.releasePlatform(platformId, ouId, userId, logId);
        this.autoCheckIn(ouId, userId, logId);
        if (log.isInfoEnabled()) {
            log.info("CheckInManagerProxyImpl.releasePlatform end, vacantPlatformList is:[{}], platformId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", platformId, ouId, userId, logId);
        }
        return resultCount;
    }

    /**
     * 释放月台
     * 
     * @author mingwei.xie
     * @param logId
     * @param asnId
     * @param ouId
     * @param userId
     * @return
     */
    @Override
    public void releasePlatformByRcvdFinish(Long asnId, Long ouId, Long userId, String logId) {
        try {
            if (log.isInfoEnabled()) {
                log.info("CheckInManagerProxyImpl.releasePlatformByRcvdFinish start, asnId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", asnId, ouId, userId, logId);
            }
            if (null == asnId || null == ouId || null == userId) {
                log.error("CheckInManagerProxyImpl.releasePlatformByRcvdFinish param is null, asnId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", asnId, ouId, userId, logId);
                throw new BusinessException(ErrorCodes.PARAMS_ERROR);
            }


            if (log.isDebugEnabled()) {
                log.debug("CheckInManagerProxyImpl.releasePlatformByRcvdFinish -> selectPoAsnManagerProxy.findWhAsnCommandById invoke, queryWhAsnCommand is:[id:{},ouId:{}], logId is:[{}]", asnId, ouId, logId);
            }
            WhAsnCommand originWhAsnCommand = selectPoAsnManagerProxy.findWhAsnCommandById(asnId, ouId);
            if (null == originWhAsnCommand) {
                log.error("CheckInManagerProxyImpl.releasePlatformByRcvdFinish -> selectPoAsnManagerProxy.findWhAsnCommandById error, result is null, queryWhAsnCommand is:[id:{},ouId:{}], logId is:[{}]", asnId, ouId, logId);
                throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
            }
            if (log.isDebugEnabled()) {
                log.debug("CheckInManagerProxyImpl.releasePlatformByRcvdFinish -> selectPoAsnManagerProxy.findWhAsnCommandById result, queryWhAsnCommand is:[{}], logId is:[{}], originWhAsnCommand is:[id:{},ouId:{}]", asnId, ouId, logId,
                        originWhAsnCommand);
            }

            if (log.isDebugEnabled()) {
                log.debug("CheckInManagerProxyImpl.releasePlatformByRcvdFinish -> platformManager.findByOccupationCode invoke, asnCode is:[{}], ouId is:[{}], logId is:[{}]", originWhAsnCommand.getAsnCode(), ouId, logId);
            }
            Platform platform = platformManager.findByOccupationCode(originWhAsnCommand.getAsnCode(), ouId, BaseModel.LIFECYCLE_NORMAL);
            if (log.isDebugEnabled()) {
                log.debug("CheckInManagerProxyImpl.releasePlatformByRcvdFinish -> platformManager.findByOccupationCode result, asnCode is:[{}], ouId is:[{}], logId is:[{}], platform is:[{}]", originWhAsnCommand.getAsnCode(), ouId, logId, platform);
            }
            if (null != platform) {
                if (log.isDebugEnabled()) {
                    log.debug("CheckInManagerProxyImpl.releasePlatformByRcvdFinish -> checkInQueueManager.releasePlatform invoke, asnId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", asnId, ouId, userId, logId);
                }
                checkInQueueManager.releasePlatform(platform.getId(), ouId, userId, logId);
                this.autoCheckIn(ouId, userId, logId);
            }
            if (log.isInfoEnabled()) {
                log.info("CheckInManagerProxyImpl.releasePlatformByRcvdFinish end, vacantPlatformList is:[{}], asnId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", asnId, ouId, userId, logId);
            }
        } catch (Exception e) {
            log.error("CheckInManagerProxyImpl releasePlatformByRcvdFinish error, ouId is:[{}], userId is:[{}], logId is:[{}], exception is:[{}]", ouId, userId, logId, e);
            throw e;
        }
    }

    private void autoCheckIn(Long ouId, Long userId, String logId) {
        try {
            if (log.isInfoEnabled()) {
                log.info("CheckInManagerProxyImpl.autoCheckIn start, ouId is:[{}], userId is:[{}], logId is:[{}]", ouId, userId, logId);
            }
            if (log.isDebugEnabled()) {
                log.debug("CheckInManagerProxyImpl.autoCheckIn -> checkInQueueManager.getListOrderBySequenceAsc invoke, ouId is:[{}], userId is:[{}], logId is:[{}]", ouId, userId, logId);
            }
            // 签入队列
            List<CheckInQueueCommand> checkInQueueCommandList = checkInQueueManager.getListOrderBySequenceAsc(ouId, logId);
            if (log.isDebugEnabled()) {
                log.debug("CheckInManagerProxyImpl.autoCheckIn -> checkInQueueManager.getListOrderBySequenceAsc result, ouId is:[{}], userId is:[{}], logId is:[{}], checkInQueueCommandList is:[{}]", ouId, userId, logId, checkInQueueCommandList);
            }
            if (log.isDebugEnabled()) {
                log.debug("CheckInManagerProxyImpl.autoCheckIn sort checkInQueueCommandList, ouId is:[{}], userId is:[{}], logId is:[{}], checkInQueueCommandList is:[{}]", ouId, userId, logId, checkInQueueCommandList);
            }
            Collections.sort(checkInQueueCommandList);
            if (log.isDebugEnabled()) {
                log.debug("CheckInManagerProxyImpl.autoCheckIn sort checkInQueueCommandList result, ouId is:[{}], userId is:[{}], logId is:[{}], checkInQueueCommandList is:[{}]", ouId, userId, logId, checkInQueueCommandList);
            }

            if (log.isDebugEnabled()) {
                log.debug("CheckInManagerProxyImpl.autoCheckIn loop checkInQueueCommandList, ouId is:[{}], userId is:[{}], logId is:[{}], checkInQueueCommandList is:[{}]", ouId, userId, logId, checkInQueueCommandList);
            }
            for (CheckInQueueCommand checkInQueue : checkInQueueCommandList) {

                // 匹配规则
                RuleAfferCommand ruleAfferCommand = new RuleAfferCommand();
                ruleAfferCommand.setOuid(ouId);
                ruleAfferCommand.setAfferAsnId(checkInQueue.getAsnId());
                ruleAfferCommand.setRuleType(Constants.PLATFORM_RECOMMEND_RULE);
                ruleAfferCommand.setLogId(logId);

                if (log.isDebugEnabled()) {
                    log.debug("CheckInManagerProxyImpl.autoCheckIn -> ruleManager.ruleExport invoke, ouId is:[{}], userId is:[{}], logId is:[{}], ruleAfferCommand is:[{}]", ouId, userId, logId, ruleAfferCommand);
                }
                RuleExportCommand ruleExportCommand = ruleManager.ruleExport(ruleAfferCommand);

                if (null == ruleExportCommand || null == ruleExportCommand.getExportPlatform()) {
                    log.error("CheckInManagerProxyImpl.autoCheckIn -> ruleManager.ruleExport result is null, ouId is:[{}], userId is:[{}], logId is:[{}], ruleAfferCommand is:[{}], ruleExportCommand is:[{}]", ouId, userId, logId, ruleAfferCommand,
                            ruleExportCommand);
                    throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
                }
                if (log.isDebugEnabled()) {
                    log.debug("CheckInManagerProxyImpl.autoCheckIn -> ruleManager.ruleExport result, ouId is:[{}], userId is:[{}], logId is:[{}], ruleExportCommand is:[{}]", ouId, userId, logId, ruleExportCommand);
                }
                Map<Integer, RecommendPlatformCommand> exportPlatformCode = ruleExportCommand.getExportPlatform();
                if (null != exportPlatformCode.get(Constants.AVAILABLE_PLATFORM)) {
                    RecommendPlatformCommand recommendPlatformCommand = exportPlatformCode.get(Constants.AVAILABLE_PLATFORM);
                    if (log.isDebugEnabled()) {
                        log.debug("CheckInManagerProxyImpl.autoCheckIn -> checkInQueueManager.finishCheckIn invoke, ouId is:[{}], userId is:[{}], logId is:[{}], recommendPlatformCommand is:[{}]", ouId, userId, logId, recommendPlatformCommand);
                    }
                    checkInQueueManager.finishCheckIn(checkInQueue.getAsnId(), recommendPlatformCommand.getPlatformId(), ouId, userId, logId);
                    PrintDataCommand printDataCommand = new PrintDataCommand();
                    ArrayList<Long> platformIds = new ArrayList<Long>();
                    platformIds.add(recommendPlatformCommand.getPlatformId());
                    printDataCommand.setIdList(platformIds);
                    printObjectManagerProxy.printCommonInterface(printDataCommand, Constants.PRINT_ORDER_TYPE_5, userId, ouId);
                }
            }

            if (log.isInfoEnabled()) {
                log.info("CheckInManagerProxyImpl.autoCheckIn end, ouId is:[{}], userId is:[{}], logId is:[{}]", ouId, userId, logId);
            }
        } catch (Exception e) {
            log.error("CheckInManagerProxyImpl autoCheckIn error, ouId is:[{}], userId is:[{}], logId is:[{}], exception is:[{}]", ouId, userId, logId, e);
        }

    }
}
