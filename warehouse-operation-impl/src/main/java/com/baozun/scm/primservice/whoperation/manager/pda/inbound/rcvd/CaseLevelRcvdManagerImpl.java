/**
 * Copyright (c) 2013 Baozun All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Baozun. You shall not disclose
 * such Confidential Information and shall use it only in accordance with the terms of the license
 * agreement you entered into with Baozun.
 * <p/>
 * BAOZUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. BAOZUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES.
 */

package com.baozun.scm.primservice.whoperation.manager.pda.inbound.rcvd;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import lark.common.annotation.MoreDB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.StoreDefectReasonsCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.StoreDefectTypeCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhAsnRcvdLogCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.carton.WhCartonCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.conf.basis.WarehouseDefectReasonsCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.conf.basis.WarehouseDefectTypeCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.constant.PoAsnStatus;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhAsnDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhAsnLineDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhAsnSnDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhPoDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhPoLineDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.Container2ndCategoryDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerAssistDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.InventoryStatusDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.StoreDefectReasonsDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.StoreDefectTypeDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.UomDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WarehouseDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhAsnRcvdLogDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhAsnRcvdSnLogDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.carton.WhCartonDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.conf.basis.WarehouseDefectReasonsDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.conf.basis.WarehouseDefectTypeDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryLogDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventorySnDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsn;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsnLine;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsnSn;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPo;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPoLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container2ndCategory;
import com.baozun.scm.primservice.whoperation.model.warehouse.ContainerAssist;
import com.baozun.scm.primservice.whoperation.model.warehouse.InventoryStatus;
import com.baozun.scm.primservice.whoperation.model.warehouse.StoreDefectReasons;
import com.baozun.scm.primservice.whoperation.model.warehouse.StoreDefectType;
import com.baozun.scm.primservice.whoperation.model.warehouse.Uom;
import com.baozun.scm.primservice.whoperation.model.warehouse.Warehouse;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhAsnRcvdLog;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhAsnRcvdSnLog;
import com.baozun.scm.primservice.whoperation.model.warehouse.carton.WhCarton;
import com.baozun.scm.primservice.whoperation.model.warehouse.conf.basis.WarehouseDefectReasons;
import com.baozun.scm.primservice.whoperation.model.warehouse.conf.basis.WarehouseDefectType;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventory;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventorySn;

@Service("caseLevelRcvdManager")
@Transactional
public class CaseLevelRcvdManagerImpl extends BaseManagerImpl implements CaseLevelRcvdManager {
    public static final Logger log = LoggerFactory.getLogger(CaseLevelRcvdManagerImpl.class);

    @Autowired
    private WarehouseDao warehouseDao;

    @Autowired
    private UomDao uomDao;

    @Autowired
    private WhAsnSnDao whAsnSnDao;

    @Autowired
    private WhCartonDao whCartonDao;

    @Autowired
    private ContainerDao containerDao;

    @Autowired
    private ContainerAssistDao containerAssistDao;

    @Autowired
    private InventoryStatusDao inventoryStatusDao;

    @Autowired
    private StoreDefectTypeDao storeDefectTypeDao;

    @Autowired
    private StoreDefectReasonsDao storeDefectReasonsDao;

    @Autowired
    private WarehouseDefectTypeDao warehouseDefectTypeDao;

    @Autowired
    private WarehouseDefectReasonsDao warehouseDefectReasonsDao;

    @Autowired
    private Container2ndCategoryDao container2ndCategoryDao;

    @Autowired
    private WhSkuInventoryDao whSkuInventoryDao;

    @Autowired
    private WhSkuInventorySnDao whSkuInventorySnDao;

    @Autowired
    private WhSkuInventoryLogDao whSkuInventoryLogDao;

    @Autowired
    private WhAsnRcvdLogDao whAsnRcvdLogDao;

    @Autowired
    private WhAsnRcvdSnLogDao whAsnRcvdSnLogDao;

    @Autowired
    private WhAsnLineDao whAsnLineDao;

    @Autowired
    private WhAsnDao whAsnDao;

    @Autowired
    private WhPoLineDao whPoLineDao;

    @Autowired
    private WhPoDao whPoDao;

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WarehouseDefectReasonsCommand> findWarehouseDefectReasonsListByDefectTypeId(Long typeId, Long ouId) {
        return this.warehouseDefectReasonsDao.findWarehouseDefectReasonsByTypeId(typeId, ouId);
    }

    /**
     * 通过OUID查询对应仓库可用残次类型
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WarehouseDefectTypeCommand> findWarehouseDefectTypeListByOuId(Long ouId, Integer lifecycle) {
        return warehouseDefectTypeDao.findWarehouseDefectTypeByOuId(ouId, lifecycle);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_GLOBALSOURCE)
    public List<StoreDefectReasonsCommand> findStoreDefectReasonsListByDefectTypeIds(List<Long> storeDefectTypeIdList) {
        List<StoreDefectReasonsCommand> storeDefectReasonsList = new ArrayList<StoreDefectReasonsCommand>();
        if (null != storeDefectTypeIdList && !storeDefectTypeIdList.isEmpty()) {
            storeDefectReasonsList = storeDefectReasonsDao.findStoreDefectReasonsByDefectTypeIds(storeDefectTypeIdList);
        }
        return storeDefectReasonsList;
    }

    /**
     * 根据店铺ID查询对应残次类型
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_GLOBALSOURCE)
    public List<StoreDefectTypeCommand> findStoreDefectTypeListByStoreId(Long storeId) {
        return storeDefectTypeDao.findStoreDefectTypesByStoreId(storeId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_GLOBALSOURCE)
    public StoreDefectType findStoreDefectTypeById(Long id) {
        return this.storeDefectTypeDao.findById(id);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_GLOBALSOURCE)
    public StoreDefectReasons findStoreDefectReasonsById(Long id) {
        return this.storeDefectReasonsDao.findById(id);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WarehouseDefectType findWarehouseDefectTypeById(Long id, Long ouId) {
        return this.warehouseDefectTypeDao.findByIdExt(id, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WarehouseDefectReasons findWarehouseDefectReasonsById(Long id, Long ouId) {
        return this.warehouseDefectReasonsDao.findByIdExt(id, ouId);
    }

    /**
     * 根据ID查询库存状态信息
     *
     * @author mingwei.xie
     * @param id
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_GLOBALSOURCE)
    public InventoryStatus getInventoryStatusById(Long id) {
        return this.inventoryStatusDao.findById(id);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_GLOBALSOURCE)
    public List<InventoryStatus> findAllInventoryStatus() {
        InventoryStatus status = new InventoryStatus();
        status.setLifecycle(1);
        return this.inventoryStatusDao.findListByParam(status);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_GLOBALSOURCE)
    public Uom getUomByCode(String uomCode, String groupCode) {
        Uom uom = uomDao.findUomByParam(uomCode, groupCode);
        return uom;
    }

    /**
     * 查询asn对应的caseLevel的货箱信息
     *
     * @author mingwei.xie
     * @param asnId
     * @param containerId
     * @param ouId
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhCartonCommand> getCaseLevelWhCartonListByContainer(Long asnId, Long containerId, Long ouId) {
        List<WhCartonCommand> whCartonList = whCartonDao.getCaseLevelWhCartonListByContainer(asnId, containerId, ouId);
        if (null == whCartonList) {
            throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
        }
        return whCartonList;
    }

    /**
     * 获取asnLine中指定商品的SN号
     *
     * @author mingwei.xie
     * @param asnLineId
     * @param ouId
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhAsnSn> findSkuSnByAsnLine(Long asnLineId, Long ouId) {
        WhAsnSn whAsnSnSearchCondition = new WhAsnSn();
        whAsnSnSearchCondition.setAsnLineId(asnLineId);
        whAsnSnSearchCondition.setOuId(ouId);
        return whAsnSnDao.findListByParam(whAsnSnSearchCondition);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Container getContainerById(Long id, Long ouId) {
        Container container = containerDao.findByIdExt(id, ouId);
        return container;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public ContainerCommand getContainerByCode(String code, Long ouId) {
        if (null == code) {
            log.error("ContainerManager.getContainerByCode,params code is null exception");
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        if (null == ouId) {
            log.error("warehouseDefectTypeManager.findDefectTypeByParams, params ouId is null exception");
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }

        return containerDao.getContainerByCode(code, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public int saveOrUpdateByVersion(Container container) {
        int updateCount = this.containerDao.saveOrUpdateByVersion(container);
        if (1 != updateCount) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
        return updateCount;
    }


    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Container2ndCategory getContainer2ndCategoryById(Long id, Long ouId) {
        return container2ndCategoryDao.findByIdExt(id, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public Warehouse getWarehouseById(Long ouId) {
        return warehouseDao.findWarehouseById(ouId);
    }

    /**
     * 判断ASN的caseLevel收货是否完成
     *
     * @author mingwei.xie
     * @param asnId
     * @param ouId
     * @return
     */
    @Override
    public boolean isAsnCaseLevelNeedToRcvd(Long asnId, Long ouId) {
        return whCartonDao.isAsnCaseLevelNeedToRcvd(asnId, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void caseLevelReceivingCompleted(List<WhCartonCommand> rcvdCartonList, List<WhSkuInventory> toSaveSkuInventoryList, Map<String, List<WhSkuInventorySn>> toSaveSkuSerialGroupInvSnMap, List<WhAsnRcvdLogCommand> toSaveWhAsnRcvdLogCommandList,
            List<WhAsnLine> toUpdateAsnLineList, WhAsn toUpdateWhAsn, List<WhPoLine> toUpdatePoLineList, List<WhPo> toUpdateWhPoList, Container toUpdateContainer, ContainerAssist toSaveContainerAssist, Boolean isTabbInvTotal, Long userId, Long ouId,
            String logId) {
        // 更新/创建 装箱信息WhCarton
        this.saveOrUpdateWhCartonToDB(rcvdCartonList, userId, ouId, logId);
        // 创建 库存信息 WhSkuInventory
        this.saveWhSkuInventoryToDB(toSaveSkuInventoryList, isTabbInvTotal, userId, ouId, logId);
        // 根据序列号管理类型 创建SN/残次库存 WhSkuInventorySn
        for (String skuSerialType : toSaveSkuSerialGroupInvSnMap.keySet()) {
            // 序列号管理类型为出入库全部管的才保存SN/残次库存信息
            if (Constants.SERIAL_NUMBER_TYPE_ALL.equals(skuSerialType)) {
                this.saveWhSkuInventorySnToDB(toSaveSkuSerialGroupInvSnMap.get(skuSerialType), userId, ouId, logId);
            }
        }
        // 创建asn收货日志 WhAsnRcvdLog 获取ID 创建asn收货SN/残次日志 WhAsnRcvdSnLog
        for (WhAsnRcvdLogCommand whAsnRcvdLogCommand : toSaveWhAsnRcvdLogCommandList) {
            WhAsnRcvdLog whAsnRcvdLog = new WhAsnRcvdLog();
            BeanUtils.copyProperties(whAsnRcvdLogCommand, whAsnRcvdLog);
            // 保存asn收货日志
            this.saveWhAsnRcvdLogToDB(whAsnRcvdLog, userId, ouId, logId);
            // 保存asn收货的SN/残次日志
            this.saveWhAsnRcvdSnLogToDB(whAsnRcvdLog, whAsnRcvdLogCommand.getWhAsnRcvdSnLogList(), userId, ouId, logId);
        }
        // 更新ASN明细信息 WhAsnLine，必须先更新明细，再更新主信息，因为要根据明细的完成情况判断主信息是否完成
        this.updateWhAsnLineToDB(toUpdateAsnLineList, userId, ouId, logId);
        // 更新ASN主信息 WhAsn
        this.updateWhAsnToDB(toUpdateWhAsn, userId, ouId, logId);
        // 更新PO明细信息 WhPoLine 必须先更新明细，再更新主信息，因为要根据明细的完成情况判断主信息是否完成
        this.updateWhPoLineToDB(toUpdatePoLineList, userId, ouId, logId);
        // 更新PO信息 WhPo
        this.updateWhPoToDB(toUpdateWhPoList, userId, ouId, logId);
        // 更新容器信息 Container
        this.updateContainerToDB(toUpdateContainer, userId, ouId, logId);
        // 保存容器辅助表数据
        this.saveContainerAssistToDB(toSaveContainerAssist, userId, ouId, logId);

    }

    /**
     * 校验ASN是否收货完成
     *
     * @author mingwei.xie
     * @param asnId
     * @param ouId
     * @param logId
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Boolean checkIsAsnRcvdFinished(Long asnId, Long ouId, String logId) {
        return whAsnDao.checkIsRcvdFinished(asnId, ouId);
    }

    /**
     * 更新/保存装箱信息carton到数据库
     *
     * @author mingwei.xie
     * @param rcvdCartonList
     * @param userId
     * @param ouId
     * @param logId
     */
    private void saveOrUpdateWhCartonToDB(List<WhCartonCommand> rcvdCartonList, Long userId, Long ouId, String logId) {
        // 更新/创建 装箱信息WhCarton,是否修改为非caseLevel箱数据
        for (WhCartonCommand rcvdCarton : rcvdCartonList) {
            WhCarton whCarton = new WhCarton();
            BeanUtils.copyProperties(rcvdCarton, whCarton);
            if (null == whCarton.getId()) {
                whCartonDao.insert(whCarton);
                log.warn("CaseLevelRcvdManagerImpl.saveOrUpdateWhCartonToDB save whCarton to share DB, whCarton is:[{}], logId is:[{}]", whCarton, logId);
                this.insertGlobalLog(GLOBAL_LOG_INSERT, whCarton, ouId, userId, null, null);
            } else {
                log.warn("CaseLevelRcvdManagerImpl.saveOrUpdateWhCartonToDB update whCarton to share DB, whCarton is:[{}], logId is:[{}]", whCarton, logId);
                int updateCount = whCartonDao.saveOrUpdateByVersion(whCarton);
                if (1 != updateCount) {
                    log.error("CaseLevelRcvdManagerImpl.saveOrUpdateWhCartonToDB save whCarton error, whCarton is:[{}], logId is:[{}]", whCarton, logId);
                    throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                }
                this.insertGlobalLog(GLOBAL_LOG_UPDATE, whCarton, ouId, userId, null, null);
            }
        }
    }

    /**
     * 保存库存记录到数据库
     *
     * @author mingwei.xie
     * @param toSaveSkuInventoryList
     * @param isTabbInvTotal
     * @param userId
     * @param ouId
     * @param logId
     */
    private void saveWhSkuInventoryToDB(List<WhSkuInventory> toSaveSkuInventoryList, Boolean isTabbInvTotal, Long userId, Long ouId, String logId) {
        // 创建 库存信息 WhSkuInventory
        for (WhSkuInventory whSkuInventory : toSaveSkuInventoryList) {
            Double originOnHandQty = 0.0;
            if (isTabbInvTotal) {
                originOnHandQty = whSkuInventoryLogDao.sumSkuInvOnHandQty(whSkuInventory.getUuid(), whSkuInventory.getOuId());
            }
            // 从仓库判断是否需要记录库存数量变化
            whSkuInventoryDao.insert(whSkuInventory);
            log.warn("CaseLevelRcvdManagerImpl.saveWhSkuInventoryToDB save whSkuInventory to share DB, whSkuInventory is:[{}], logId is:[{}]", whSkuInventory, logId);
            this.insertGlobalLog(GLOBAL_LOG_INSERT, whSkuInventory, ouId, userId, null, null);
            this.insertSkuInventoryLog(whSkuInventory.getId(), whSkuInventory.getOnHandQty(), originOnHandQty, isTabbInvTotal, ouId, userId);
        }

    }

    /**
     * 保存SN/残次信息到数据库，同时保存SN/残次信息日志到数据库，只有商品的序列号管理类型为出入库都管的才保存
     *
     * @author mingwei.xie
     * @param toSaveWhSkuInventoryList
     * @param userId
     * @param ouId
     * @param logId
     */
    private void saveWhSkuInventorySnToDB(List<WhSkuInventorySn> toSaveWhSkuInventoryList, Long userId, Long ouId, String logId) {
        if (null != toSaveWhSkuInventoryList) {
            // 创建SN/残次库存 WhSkuInventorySn
            for (WhSkuInventorySn whSkuInventorySn : toSaveWhSkuInventoryList) {
                whSkuInventorySnDao.insert(whSkuInventorySn);
                log.warn("CaseLevelRcvdManagerImpl.saveWhSkuInventorySnToDB save whSkuInventorySn to share DB, whSkuInventorySn is:[{}], logId is:[{}]", whSkuInventorySn, logId);
                this.insertGlobalLog(GLOBAL_LOG_INSERT, whSkuInventorySn, ouId, userId, null, null);
            }
            if (!toSaveWhSkuInventoryList.isEmpty()) {
                this.insertSkuInventorySnLog(toSaveWhSkuInventoryList.get(0).getUuid(), ouId);
            }
        }
    }

    /**
     * 保存ASN收货日志到数据库
     *
     * @author mingwei.xie
     * @param toSaveWhAsnRcvdLog
     * @param userId
     * @param ouId
     * @param logId
     */
    private void saveWhAsnRcvdLogToDB(WhAsnRcvdLog toSaveWhAsnRcvdLog, Long userId, Long ouId, String logId) {
        // 创建asn收货日志
        whAsnRcvdLogDao.insert(toSaveWhAsnRcvdLog);
        log.warn("CaseLevelRcvdManagerImpl.saveWhAsnRcvdLogToDB save whAsnRcvdLog to share DB, whAsnRcvdLog is:[{}], logId is:[{}]", toSaveWhAsnRcvdLog, logId);
        this.insertGlobalLog(GLOBAL_LOG_INSERT, toSaveWhAsnRcvdLog, ouId, userId, null, null);
    }

    /**
     * 保存ASN收货信息SN/残次日志到数据库
     *
     * @author mingwei.xie
     * @param whAsnRcvdLog
     * @param toSaveWhAsnRcvdSnLogList
     * @param userId
     * @param ouId
     * @param logId
     */
    private void saveWhAsnRcvdSnLogToDB(WhAsnRcvdLog whAsnRcvdLog, List<WhAsnRcvdSnLog> toSaveWhAsnRcvdSnLogList, Long userId, Long ouId, String logId) {
        // 创建asn收货SN/残次日志 WhAsnRcvdSnLog
        if (null != toSaveWhAsnRcvdSnLogList) {
            for (WhAsnRcvdSnLog whAsnRcvdSnLog : toSaveWhAsnRcvdSnLogList) {
                // 设置asn收货的SN/残次日志的关联ID
                whAsnRcvdSnLog.setAsnRcvdId(whAsnRcvdLog.getId());
                whAsnRcvdSnLogDao.insert(whAsnRcvdSnLog);
                log.warn("CaseLevelRcvdManagerImpl.saveWhAsnRcvdSnLogToDB save whAsnRcvdSnLog to share DB, whAsnRcvdSnLog is:[{}], logId is:[{}]", whAsnRcvdSnLog, logId);
                this.insertGlobalLog(GLOBAL_LOG_INSERT, whAsnRcvdSnLog, ouId, userId, null, null);
            }
        }
    }

    /**
     * 更新asnLine信息待数据库
     *
     * @author mingwei.xie
     * @param toUpdateWhAsnLineList
     * @param userId
     * @param ouId
     * @param logId
     */
    private void updateWhAsnLineToDB(List<WhAsnLine> toUpdateWhAsnLineList, Long userId, Long ouId, String logId) {
        // 更新ASN明细信息 WhAsnLine
        for (WhAsnLine whAsnLine : toUpdateWhAsnLineList) {
            log.warn("CaseLevelRcvdManagerImpl.updateWhAsnLineToDB update whAsnRcvdLog to share DB, whAsnLine is:[{}], logId is:[{}]", whAsnLine, logId);
            int updateCount = whAsnLineDao.saveOrUpdateByVersion(whAsnLine);
            if (1 != updateCount) {
                log.error("CaseLevelRcvdManagerImpl.updateWhAsnLineToDB update whAsnLine error, whAsnLine is:[{}], logId is:[{}]", whAsnLine, logId);
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
            this.insertGlobalLog(GLOBAL_LOG_UPDATE, whAsnLine, ouId, userId, null, null);
        }
    }

    /**
     * 更新whAsn信息到数据库，根据所有asnLine是否为完成状态判断ASN是否完成
     *
     * @author mingwei.xie
     * @param toUpdateWhAsn
     * @param userId
     * @param ouId
     * @param logId
     */
    private void updateWhAsnToDB(WhAsn toUpdateWhAsn, Long userId, Long ouId, String logId) {
        // 更新ASN主信息 WhAsn
        // 所有asnLine都是完成状态则asn修改为完成状态
        boolean isAsnRcvdFinished = whAsnDao.checkIsRcvdFinished(toUpdateWhAsn.getId(), ouId);
        if (isAsnRcvdFinished) {
            toUpdateWhAsn.setStatus(PoAsnStatus.ASN_RCVD_FINISH);
            toUpdateWhAsn.setStopTime(new Date());
        } else {
            toUpdateWhAsn.setStatus(PoAsnStatus.ASN_RCVD);
        }
        log.warn("CaseLevelRcvdManagerImpl.updateWhAsnToDB update whAsng to share DB, whAsn is:[{}], logId is:[{}]", toUpdateWhAsn, logId);
        int updateCount = whAsnDao.saveOrUpdateByVersion(toUpdateWhAsn);
        if (1 != updateCount) {
            log.error("CaseLevelRcvdManagerImpl.updateWhAsnToDB update whAsn error, whAsn is:[{}], logId is:[{}]", toUpdateWhAsn, logId);
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
        this.insertGlobalLog(GLOBAL_LOG_UPDATE, toUpdateWhAsn, ouId, userId, null, null);
    }

    /**
     * 更新poLin信息到数据库
     *
     * @author mingwei.xie
     * @param toUpdateWhPoLineList
     * @param userId
     * @param ouId
     * @param logId
     */
    private void updateWhPoLineToDB(List<WhPoLine> toUpdateWhPoLineList, Long userId, Long ouId, String logId) {
        // 更新PO明细信息 WhPoLine
        for (WhPoLine whPoLine : toUpdateWhPoLineList) {
            log.warn("CaseLevelRcvdManagerImpl.updateWhPoLineToDB update whPoLine to share DB, whPoLine is:[{}], logId is:[{}]", whPoLine, logId);
            int updateCount = whPoLineDao.saveOrUpdateByVersion(whPoLine);
            if (1 != updateCount) {
                log.error("CaseLevelRcvdManagerImpl.updateWhPoLineToDB update whPoLine error, whAsn is:[{}], logId is:[{}]", whPoLine, logId);
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
            this.insertGlobalLog(GLOBAL_LOG_UPDATE, whPoLine, ouId, userId, null, null);
        }
    }

    /**
     * 更新whPo信息到数据库，根据所有poLine是否为完成状态判断whPo是否已完成
     *
     * @author mingwei.xie
     * @param toUpdateWhPoList
     * @param userId
     * @param ouId
     * @param logId
     */
    private void updateWhPoToDB(List<WhPo> toUpdateWhPoList, Long userId, Long ouId, String logId) {
        for (WhPo toUpdateWhPo : toUpdateWhPoList) {
            // 更新PO信息 WhPo
            boolean isPoRcvdFinished = this.whPoDao.checkIsRcvdFinished(toUpdateWhPo.getId(), ouId);
            if (isPoRcvdFinished) {
                toUpdateWhPo.setStatus(PoAsnStatus.PO_RCVD_FINISH);
                toUpdateWhPo.setStopTime(new Date());
            } else {
                toUpdateWhPo.setStatus(PoAsnStatus.PO_RCVD);
            }
            log.warn("CaseLevelRcvdManagerImpl.updateWhPoToDB update whPo to share DB, whPo is:[{}], logId is:[{}]", toUpdateWhPo, logId);
            int updateCount = whPoDao.saveOrUpdateByVersion(toUpdateWhPo);
            if (1 != updateCount) {
                log.error("CaseLevelRcvdManagerImpl update whPo error, whAsn is:[{}], logId is:[{}]", toUpdateWhPo, logId);
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
            this.insertGlobalLog(GLOBAL_LOG_UPDATE, toUpdateWhPo, ouId, userId, null, null);
        }
    }

    /**
     * 更新容器信息到数据库
     *
     * @author mingwei.xie
     * @param toUpdateContainer
     * @param userId
     * @param ouId
     * @param logId
     */
    private void updateContainerToDB(Container toUpdateContainer, Long userId, Long ouId, String logId) {
        // 更新容器信息 Container
        log.warn("CaseLevelRcvdManagerImpl.updateContainerToDB update container to share DB, container is:[{}], logId is:[{}]", toUpdateContainer, logId);
        int updateCount = containerDao.saveOrUpdateByVersion(toUpdateContainer);
        if (1 != updateCount) {
            log.error("CaseLevelRcvdManagerImpl.updateContainerToDB update container error, whAsn is:[{}], logId is:[{}]", toUpdateContainer, logId);
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
        this.insertGlobalLog(GLOBAL_LOG_UPDATE, toUpdateContainer, ouId, userId, null, null);
    }

    /**
     * 保存容器辅助表信息到数据库
     *
     * @author mingwei.xie
     * @param toSaveContainerAssist
     * @param userId
     * @param ouId
     * @param logId
     */
    private void saveContainerAssistToDB(ContainerAssist toSaveContainerAssist, Long userId, Long ouId, String logId) {
        // 更新容器辅助表 ContainerAssist
        containerAssistDao.insert(toSaveContainerAssist);
        log.warn("CaseLevelRcvdManagerImpl.saveContainerAssistToDB save containerAssist to share DB, containerAssist is:[{}], logId is:[{}]", toSaveContainerAssist, logId);
        this.insertGlobalLog(GLOBAL_LOG_INSERT, toSaveContainerAssist, ouId, userId, null, null);
    }
}
