package com.baozun.scm.primservice.whoperation.manager.warehouse;

import java.util.Date;
import java.util.List;
import java.util.Map;

import lark.common.annotation.MoreDB;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.baseservice.sac.manager.CodeManager;
import com.baozun.scm.primservice.whoperation.command.warehouse.AsnReserveCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.constant.PoAsnStatus;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhAsnDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.AsnReserveDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsn;
import com.baozun.scm.primservice.whoperation.model.warehouse.AsnReserve;

@Service("asnReserveManager")
@Transactional
public class AsnReserveManagerImpl extends BaseManagerImpl implements AsnReserveManager {
    private static final Logger log = LoggerFactory.getLogger(AsnReserveManagerImpl.class);

    @Autowired
    private AsnReserveDao asnReserveDao;

    @Autowired
    private CodeManager codeManager;

    @Autowired
    private WhAsnDao whAsnDao;

    /**
     * 列表页查询
     *
     * @author mingwei.xie
     * @param page
     * @param sorts
     * @param param
     * @param logId
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Pagination<AsnReserveCommand> getListByParams(Page page, Sort[] sorts, Map<String, Object> param, String logId) {
        if (log.isInfoEnabled()) {
            log.info("AsnReserveManagerImpl.getListByParams start, logId is:[{}], page is:[{}], sorts is:[{}], param is:[{}]", logId, page, sorts, param);
        }
        if (log.isDebugEnabled()) {
            log.debug("AsnReserveManagerImpl.getListByParams-> asnReserveDao.findListByQueryMapWithPageExt invoke, logId is:[{}], params are:[{}]", logId, param);
        }
        Pagination<AsnReserveCommand> asnReserveList = asnReserveDao.findListByQueryMapWithPageExt(page, sorts, param);
        if (log.isDebugEnabled()) {
            log.debug("AsnReserveManagerImpl.getListByParams-> asnReserveDao.findListByQueryMapWithPageExt result, logId is:[{}], asnReserveList is:[{}]", logId, asnReserveList);
        }
        if (log.isInfoEnabled()) {
            log.info("AsnReserveManagerImpl.getListByParams end, logId is:[{}], asnReserveList is:[{}]", logId, asnReserveList);
        }
        return asnReserveList;
    }

    /**
     * 根据ID查找预约信息
     *
     * @author mingwei.xie
     * @param asnReserveId
     * @param ouId
     * @param logId
     * @return
     */
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    @Override
    public AsnReserveCommand getAsnReserveCommandById(Long asnReserveId, Long ouId, String logId) {
        if (log.isInfoEnabled()) {
            log.info("AsnReserveManagerImpl.getAsnReserveCommandById start, ouId is:[{}], logId is:[{}], asnReserveId is:[{}]", ouId, logId, asnReserveId);
        }
        if (log.isDebugEnabled()) {
            log.debug("AsnReserveManagerImpl.getAsnReserveCommandById-> asnReserveDao.getAsnReserveCommandById invoke, ouId, logId is:[{}], asnReserveId is:[{}]", ouId, logId, asnReserveId);
        }
        AsnReserveCommand asnReserveCommand = asnReserveDao.getAsnReserveCommandById(asnReserveId, ouId);
        if (log.isDebugEnabled()) {
            log.debug("AsnReserveManagerImpl.getAsnReserveCommandById-> asnReserveDao.getAsnReserveCommandById result, logId is:[{}], asnReserveCommand is:[{}]", logId, asnReserveCommand);
        }
        if (log.isInfoEnabled()) {
            log.info("AsnReserveManagerImpl.getAsnReserveCommandById end, ouId is:[{}], logId is:[{}], asnReserveCommand is:[{}]", ouId, logId, asnReserveCommand);
        }
        return asnReserveCommand;
    }


    /**
     * 根据日期查询当天的预约信息
     *
     * @author mingwei.xie
     * @param reserveDate
     * @param ouId
     * @param logId
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<AsnReserveCommand> findAsnReserveCommandByEtaDate(Date reserveDate, Long ouId, String logId) {
        if (log.isInfoEnabled()) {
            log.info("AsnReserveManagerImpl.findAsnReserveCommandByEtaDate start, ouId is:[{}], logId is:[{}], reserveDate is:[{}]", ouId, logId, reserveDate);
        }
        if (log.isDebugEnabled()) {
            log.debug("AsnReserveManagerImpl.findAsnReserveCommandByEtaDate-> asnReserveDao.findAsnReserveCommandByEtaDate invoke, ouId, logId is:[{}], reserveDate is:[{}]", ouId, logId, reserveDate);
        }
        List<AsnReserveCommand> asnReserveCommandList = asnReserveDao.findAsnReserveCommandByEtaDate(reserveDate, ouId);
        if (log.isDebugEnabled()) {
            log.debug("AsnReserveManagerImpl.findAsnReserveCommandByEtaDate-> asnReserveDao.findAsnReserveCommandByEtaDate result, logId is:[{}], asnReserveCommandList is:[{}]", logId, asnReserveCommandList);
        }
        if (log.isInfoEnabled()) {
            log.info("AsnReserveManagerImpl.findAsnReserveCommandByEtaDate end, ouId is:[{}], logId is:[{}], asnReserveCommandList is:[{}]", ouId, logId, asnReserveCommandList);
        }
        return asnReserveCommandList;
    }

    /**
     * 生成Asn预约号
     *
     * @param ouId
     * @param logId
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public String createAsnReserveCode(Long ouId, String logId) {
        if (log.isInfoEnabled()) {
            log.info("AsnReserveManagerImpl.createAsnReserveCode start, ouId is:[{}], logId is:[{}]", ouId, logId);
        }
        String code = null;
        boolean isUnique = false;
        // 在失败的情况下，最多尝试三次
        for (int i = 0; i < 3; i++) {
            if (log.isDebugEnabled()) {
                log.debug("AsnReserveManagerImpl.createAsnReserveCode-> codeManager.generateCode invoke, ouId, logId is:[{}]", ouId, logId);
            }
            // 创建预约号
            code = codeManager.generateCode(Constants.WMS, Constants.ASN_RESERVE_MODEL_URL, null, null, null);
            if (log.isDebugEnabled()) {
                log.debug("AsnReserveManagerImpl.createAsnReserveCode-> asnReserveDao.generateCode result, ouId, logId is:[{}], code is:[{}]", ouId, logId, code);
            }
            if (null != code) {
                // 检查新生成的预约号是否已存在
                if (log.isDebugEnabled()) {
                    log.debug("AsnReserveManagerImpl.createAsnReserveCode-> codeManager.checkAsnReserveCodeUnique invoke, ouId, logId is:[{}], code is:[{}]", ouId, logId, code);
                }
                int count = asnReserveDao.checkAsnReserveCodeUnique(code, ouId);
                if (log.isDebugEnabled()) {
                    log.debug("AsnReserveManagerImpl.createAsnReserveCode-> asnReserveDao.checkAsnReserveCodeUnique result, ouId, logId is:[{}], count is:[{}]", ouId, logId, count);
                }
                if (count != 0) {
                    if (log.isDebugEnabled()) {
                        log.debug("AsnReserveManagerImpl.createAsnReserveCode code is not unique, continue loop, ouId, logId is:[{}], count is:[{}]", ouId, logId, count);
                    }
                    continue;
                }
                isUnique = true;
                break;
            }
        }
        if (null == code || !isUnique) {
            throw new BusinessException(ErrorCodes.CODE_INTERFACE_REEOR);
        }
        if (log.isInfoEnabled()) {
            log.info("AsnReserveManagerImpl.createAsnReserveCode end, ouId is:[{}], logId is:[{}], code is:[{}]", ouId, logId, code);
        }
        return code;
    }



    /**
     * 取消预约
     *
     * @author mingwei.xie
     * @param asnReserveIdList
     * @param logId
     * @return
     */
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    @Override
    public void removeAsnReserve(List<Long> asnReserveIdList, Long userId, Long ouId, String logId) {
        if (log.isInfoEnabled()) {
            log.info("AsnReserveManagerImpl.removeAsnReserve start, ouId is:[{}], logId is:[{}], asnReserveIdList is:[{}]", ouId, logId, asnReserveIdList);
        }
        if (null == asnReserveIdList || null == ouId) {
            log.error("AsnReserveManagerImpl.removeAsnReserve param is null, ouId is:[{}], logId is:[{}], asnReserveIdList is:[{}], userId is:[{}]", ouId, logId, asnReserveIdList, userId);
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        if (log.isDebugEnabled()) {
            log.debug("AsnReserveManagerImpl.removeAsnReserve loop asnReserveIdList,ouId is:[{}], logId is:[{}], asnReserveIdList is:[{}], userId is:[{}]", ouId, logId, asnReserveIdList, userId);
        }
        for (Long asnReserveId : asnReserveIdList) {
            if (log.isDebugEnabled()) {
                log.debug("AsnReserveManagerImpl.removeAsnReserve-> this.deleteAsnReserve invoke, ouId is:[{}], logId is:[{}], asnReserveId is:[{}], userId is:[{}]", ouId, logId, asnReserveId, userId);
            }
            // 删除预约信息
            Long asnId = this.deleteAsnReserve(asnReserveId, userId, ouId, logId);
            if (log.isDebugEnabled()) {
                log.debug("AsnReserveManagerImpl.removeAsnReserve-> this.deleteAsnReserve result, ouId is:[{}], logId is:[{}], asnReserveId is:[{}], asnId is:[{}]", ouId, logId, asnReserveId, asnId);
            }
            if (log.isDebugEnabled()) {
                log.debug("AsnReserveManagerImpl.removeAsnReserve-> this.updateWhAsnStatusFromReserveToNew invoke, ouId is:[{}], logId is:[{}], asnReserveId is:[{}], asnId is:[{}]", ouId, logId, asnReserveId, asnId);
            }
            // 更新asn状态
            this.updateWhAsnStatusFromReserveToNew(asnId, userId, ouId, logId);
            if (log.isDebugEnabled()) {
                log.debug("AsnReserveManagerImpl.removeAsnReserve-> this.updateWhAsnStatusFromReserveToNew result, ouId is:[{}], logId is:[{}], asnReserveId is:[{}], asnId is:[{}]", ouId, logId, asnReserveId, asnId);
            }
        }
        if (log.isInfoEnabled()) {
            log.info("AsnReserveManagerImpl.removeAsnReserve end, ouId is:[{}], logId is:[{}]", ouId, logId);
        }
    }


    /**
     * 新建/修改预约信息
     *
     * @author mingwei.xie
     * @param asnReserveCommand
     * @param userId
     * @param ouId
     * @param logId
     */
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    @Override
    public void saveAsnReserve(AsnReserveCommand asnReserveCommand, Long userId, Long ouId, String logId) {
        if (log.isInfoEnabled()) {
            log.info("AsnReserveManagerImpl.saveAsnReserve start, ouId is:[{}], logId is:[{}], asnReserveCommand is:[{}]", ouId, logId, asnReserveCommand);
        }
        if (null == asnReserveCommand || null == userId || null == ouId) {
            log.error("AsnReserveManagerImpl.saveAsnReserve param is null, ouId is:[{}], logId is:[{}], asnReserveCommand is:[{}]", ouId, logId, asnReserveCommand);
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        if (null != asnReserveCommand.getId()) {
            if (log.isDebugEnabled()) {
                log.debug("AsnReserveManagerImpl.saveAsnReserve asnReserve is exist, update asnReserve");
            }
            if (log.isDebugEnabled()) {
                log.debug("AsnReserveManagerImpl.saveAsnReserve-> this.updateAsnReserve invoke, ouId is:[{}], logId is:[{}], asnReserveCommand is:[{}]", ouId, logId, asnReserveCommand);
            }
            this.updateAsnReserve(asnReserveCommand, userId, ouId, logId);
        } else {
            if (log.isDebugEnabled()) {
                log.debug("AsnReserveManagerImpl.saveAsnReserve asnReserve is not exist, create asnReserve");
            }
            if (log.isDebugEnabled()) {
                log.debug("AsnReserveManagerImpl.saveAsnReserve-> this.createAsnReserve invoke, ouId is:[{}], logId is:[{}], asnReserveCommand is:[{}]", ouId, logId, asnReserveCommand);
            }
            this.createAsnReserve(asnReserveCommand, userId, ouId, logId);
            // 更新asn状态
            if (log.isDebugEnabled()) {
                log.debug("AsnReserveManagerImpl.saveAsnReserve-> this.updateWhAsnStatusFromNewToReserve invoke, ouId is:[{}], logId is:[{}], asnId is:[{}]", ouId, logId, asnReserveCommand.getAsnId());
            }
            this.updateWhAsnStatusFromNewToReserve(asnReserveCommand.getAsnId(), userId, ouId, logId);
        }
        if (log.isInfoEnabled()) {
            log.info("AsnReserveManagerImpl.saveAsnReserve end, ouId is:[{}], logId is:[{}]", ouId, logId);
        }
    }

    /**
     * 创建预约信息
     *
     * @author mingwei.xie
     * @param asnReserveCommand
     * @param userId
     * @param ouId
     * @param logId
     */
    private void createAsnReserve(AsnReserveCommand asnReserveCommand, Long userId, Long ouId, String logId) {
        if (log.isInfoEnabled()) {
            log.info("AsnReserveManagerImpl.createAsnReserve start, ouId is:[{}], logId is:[{}], asnReserveCommand is:[{}]", ouId, logId, asnReserveCommand);
        }
        AsnReserve asnReserve = new AsnReserve();
        asnReserve.setCode(asnReserveCommand.getCode());
        asnReserve.setAsnId(asnReserveCommand.getAsnId());
        asnReserve.setEta(asnReserveCommand.getEta());
        asnReserve.setStatus(PoAsnStatus.ASN_RESERVE_NEW);
        asnReserve.setLevel(asnReserveCommand.getLevel());
        asnReserve.setOuId(ouId);
        asnReserve.setDeliveryTime(null);
        asnReserve.setEstParkingTime(asnReserveCommand.getEstParkingTime());
        asnReserve.setCreatedId(userId);
        asnReserve.setCreateTime(new Date());
        asnReserve.setModifiedId(userId);
        asnReserve.setLastModifyTime(new Date());
        if (log.isDebugEnabled()) {
            log.debug("AsnReserveManagerImpl.createAsnReserve-> asnReserveDao.insert invoke, ouId is:[{}], logId is:[{}], asnReserve is:[{}]", ouId, logId, asnReserve);
        }
        asnReserveDao.insert(asnReserve);
        if (log.isDebugEnabled()) {
            log.debug("AsnReserveManagerImpl.createAsnReserve ->insertGlobalLog invoke,ouId is:[{}], logId is:[{}]", ouId, logId);
        }
        insertGlobalLog(Constants.GLOBAL_LOG_INSERT, asnReserve, ouId, userId, null, null);
        if (log.isInfoEnabled()) {
            log.info("AsnReserveManagerImpl.createAsnReserve end, ouId is:[{}], logId is:[{}]", ouId, logId);
        }
    }

    /**
     * 更新预约信息
     *
     * @author mingwei.xie
     * @param asnReserveCommand
     * @param userId
     * @param ouId
     * @param logId
     */
    private void updateAsnReserve(AsnReserveCommand asnReserveCommand, Long userId, Long ouId, String logId) {
        if (log.isInfoEnabled()) {
            log.info("AsnReserveManagerImpl.updateAsnReserve start, ouId is:[{}], logId is:[{}], asnReserveCommand is:[{}]", ouId, logId, asnReserveCommand);
        }
        if (log.isDebugEnabled()) {
            log.debug("AsnReserveManagerImpl.updateAsnReserve-> asnReserveDao.getAsnReserveById invoke, ouId is:[{}], logId is:[{}], asnReserveId is:[{}]", ouId, logId, asnReserveCommand.getId());
        }
        AsnReserve asnReserve = asnReserveDao.getAsnReserveById(asnReserveCommand.getId(), ouId);
        if (log.isDebugEnabled()) {
            log.debug("AsnReserveManagerImpl.updateAsnReserve-> asnReserveDao.getAsnReserveById result, ouId is:[{}], logId is:[{}], asnReserve is:[{}]", ouId, logId, asnReserve);
        }
        if (null == asnReserve) {
            log.error("AsnReserveManagerImpl.updateAsnReserve-> asnReserveDao.getAsnReserveById, result is null, ouId is:[{}], logId is:[{}], asnReserveId is:[{}]", ouId, logId, asnReserveCommand.getId());
            throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
        }
        asnReserve.setEta(asnReserveCommand.getEta());
        asnReserve.setLevel(asnReserveCommand.getLevel());
        asnReserve.setDeliveryTime(null);
        asnReserve.setEstParkingTime(asnReserveCommand.getEstParkingTime());
        asnReserve.setModifiedId(userId);
        if (log.isDebugEnabled()) {
            log.debug("AsnReserveManagerImpl.updateAsnReserve-> asnReserveDao.saveOrUpdateByVersion invoke, ouId is:[{}], logId is:[{}], asnReserve is:[{}]", ouId, logId, asnReserve);
        }
        int updateCount = asnReserveDao.saveOrUpdateByVersion(asnReserve);
        if (1 != updateCount) {
            log.error("AsnReserveManagerImpl.updateAsnReserve-> asnReserveDao.saveOrUpdateByVersion error, update count != 1, ouId is:[{}], logId is:[{}], asnReserve is:[{}]", ouId, logId, asnReserve);
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
        if (log.isDebugEnabled()) {
            log.debug("AsnReserveManagerImpl.updateAsnReserve ->insertGlobalLog invoke,ouId is:[{}], logId is:[{}]", ouId, logId);
        }
        insertGlobalLog(Constants.GLOBAL_LOG_UPDATE, asnReserve, ouId, userId, null, null);
        if (log.isInfoEnabled()) {
            log.info("AsnReserveManagerImpl.updateAsnReserve end, ouId is:[{}], logId is:[{}]", ouId, logId);
        }
    }



    /**
     * 取消预约，asn状态修改为新建
     *
     * @author mingwei.xie
     * @param asnId
     * @param userId
     * @param ouId
     * @param logId
     */
    private void updateWhAsnStatusFromReserveToNew(Long asnId, Long userId, Long ouId, String logId) {
        if (log.isInfoEnabled()) {
            log.info("AsnReserveManagerImpl.updateWhAsnStatusFromReserveToNew start, ouId is:[{}], logId is:[{}], asnId is:[{}]", ouId, logId, asnId);
        }
        if (log.isDebugEnabled()) {
            log.debug("AsnReserveManagerImpl.updateWhAsnStatusFromReserveToNew-> whAsnDao.findWhAsnById invoke, ouId is:[{}], logId is:[{}], asnId is:[{}]", ouId, logId, asnId);
        }
        WhAsn whAsn = whAsnDao.findWhAsnById(asnId, ouId);
        if (log.isDebugEnabled()) {
            log.debug("AsnReserveManagerImpl.updateWhAsnStatusFromReserveToNew-> whAsnDao.findWhAsnById result, ouId is:[{}], logId is:[{}], whAsn is:[{}]", ouId, logId, whAsn);
        }
        if (null == whAsn || PoAsnStatus.ASN_RESERVE != whAsn.getStatus()) {
            log.error("AsnReserveManagerImpl.updateWhAsnStatusFromReserveToNew-> whAsnDao.findWhAsnById error, result is null, ouId is:[{}], logId is:[{}], asnId is:[{}]", ouId, logId, asnId);
            throw new BusinessException(ErrorCodes.CHECK_DATA_ERROR);
        }
        if (log.isDebugEnabled()) {
            log.debug("AsnReserveManagerImpl.updateWhAsnStatusFromReserveToNew-> this.updateWhAsnStatus invoke, ouId is:[{}], logId is:[{}], asnId is:[{}]", ouId, logId, asnId);
        }
        this.updateWhAsnStatus(whAsn, PoAsnStatus.ASN_NEW, userId, ouId, logId);
        if (log.isInfoEnabled()) {
            log.info("AsnReserveManagerImpl.updateWhAsnStatusFromReserveToNew end, ouId is:[{}], logId is:[{}]", ouId, logId);
        }
    }

    /**
     * 新建预约，asn状态修改为预约
     *
     * @author mingwei.xie
     * @param asnId
     * @param userId
     * @param ouId
     * @param logId
     */
    private void updateWhAsnStatusFromNewToReserve(Long asnId, Long userId, Long ouId, String logId) {
        if (log.isInfoEnabled()) {
            log.info("AsnReserveManagerImpl.updateWhAsnStatusFromNewToReserve start, ouId is:[{}], logId is:[{}], asnId is:[{}]", ouId, logId, asnId);
        }
        if (log.isDebugEnabled()) {
            log.debug("AsnReserveManagerImpl.updateWhAsnStatusFromNewToReserve-> whAsnDao.findWhAsnById invoke, ouId is:[{}], logId is:[{}], asnId is:[{}]", ouId, logId, asnId);
        }
        WhAsn whAsn = whAsnDao.findWhAsnById(asnId, ouId);
        if (log.isDebugEnabled()) {
            log.debug("AsnReserveManagerImpl.updateWhAsnStatusFromNewToReserve-> whAsnDao.findWhAsnById result, ouId is:[{}], logId is:[{}], whAsn is:[{}]", ouId, logId, whAsn);
        }
        if (null == whAsn || PoAsnStatus.ASN_NEW != whAsn.getStatus()) {
            log.error("AsnReserveManagerImpl.updateWhAsnStatusFromNewToReserve-> whAsnDao.findWhAsnById error, result is null, ouId is:[{}], logId is:[{}], asnId is:[{}]", ouId, logId, asnId);
            throw new BusinessException(ErrorCodes.CHECK_DATA_ERROR);
        }
        if (log.isDebugEnabled()) {
            log.debug("AsnReserveManagerImpl.updateWhAsnStatusFromNewToReserve-> this.updateWhAsnStatus invoke, ouId is:[{}], logId is:[{}], asnId is:[{}]", ouId, logId, asnId);
        }
        this.updateWhAsnStatus(whAsn, PoAsnStatus.ASN_RESERVE, userId, ouId, logId);
        if (log.isInfoEnabled()) {
            log.info("AsnReserveManagerImpl.updateWhAsnStatusFromNewToReserve end, ouId is:[{}], logId is:[{}], asnId is:[{}]", ouId, logId, asnId);
        }
    }



    /**
     * 取消预约,执行物理删除
     *
     * @author mingwei.xie
     * @param asnReserveId
     * @param userId
     * @param ouId
     * @param logId
     */
    private Long deleteAsnReserve(Long asnReserveId, Long userId, Long ouId, String logId) {
        if (log.isInfoEnabled()) {
            log.info("AsnReserveManagerImpl.deleteAsnReserve start, ouId is:[{}], logId is:[{}], asnReserveId is:[{}]", ouId, logId, asnReserveId);
        }
        if (log.isDebugEnabled()) {
            log.debug("AsnReserveManagerImpl.deleteAsnReserve -> asnReserveDao.getAsnReserveById invoke, ouId is:[{}], logId is:[{}], asnReserveId is:[{}]", ouId, logId, asnReserveId);
        }
        AsnReserve asnReserve = asnReserveDao.getAsnReserveById(asnReserveId, ouId);
        if (log.isDebugEnabled()) {
            log.debug("AsnReserveManagerImpl.deleteAsnReserve -> asnReserveDao.getAsnReserveById result, ouId is:[{}], logId is:[{}], asnReserve is:[{}]", ouId, logId, asnReserve);
        }
        if (null == asnReserve || PoAsnStatus.ASN_RESERVE_NEW != asnReserve.getStatus()) {
            log.error("AsnReserveManagerImpl.deleteAsnReserve -> asnReserveDao.getAsnReserveById error, result is null, ouId is:[{}], logId is:[{}], asnReserveId is:[{}]", ouId, logId, asnReserveId);
            throw new BusinessException(ErrorCodes.CHECK_DATA_ERROR);
        }
        if (log.isDebugEnabled()) {
            log.debug("AsnReserveManagerImpl.deleteAsnReserve -> asnReserveDao.removeAsnReserve invoke, ouId is:[{}], logId is:[{}], asnReserveId is:[{}]", ouId, logId, asnReserveId);
        }
        int updateCount = asnReserveDao.removeAsnReserve(asnReserve.getId(), ouId);
        if (1 != updateCount) {
            log.error("AsnReserveManagerImpl.deleteAsnReserve -> asnReserveDao.removeAsnReserve error, updateCount != 1, ouId is:[{}], logId is:[{}], asnReserveId is:[{}]", ouId, logId, asnReserveId);
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
        if (log.isDebugEnabled()) {
            log.debug("AsnReserveManagerImpl.deleteAsnReserve ->insertGlobalLog invoke,ouId is:[{}], logId is:[{}]", ouId, logId);
        }
        insertGlobalLog(Constants.GLOBAL_LOG_DELETE, asnReserve, ouId, userId, null, null);
        if (null == asnReserve.getAsnId()) {
            log.error("AsnReserveManagerImpl.deleteAsnReserve error, asnReserve.getAsnId() is null, ouId is:[{}], logId is:[{}], asnReserveId is:[{}]", ouId, logId, asnReserveId);
            throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
        }
        if (log.isInfoEnabled()) {
            log.info("AsnReserveManagerImpl.deleteAsnReserve end, ouId is:[{}], logId is:[{}]", ouId, logId);
        }
        return asnReserve.getAsnId();
    }

    /**
     * 取消预约同步更新asn状态
     *
     * @author mingwei.xie
     * @param whAsn
     * @param status
     * @param userId
     * @param ouId
     * @param logId
     */
    private void updateWhAsnStatus(WhAsn whAsn, int status, Long userId, Long ouId, String logId) {
        if (log.isInfoEnabled()) {
            log.info("AsnReserveManagerImpl.updateWhAsnStatus start, ouId is:[{}], logId is:[{}], whAsn is:[{}]", ouId, logId, whAsn);
        }
        if (null == whAsn || null == userId || null == ouId) {
            log.error("AsnReserveManagerImpl.updateWhAsnStatus param is null, ouId is:[{}], logId is:[{}], whAsn is:[{}]", ouId, logId, whAsn);
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        whAsn.setStatus(status);
        whAsn.setModifiedId(userId);
        if (log.isDebugEnabled()) {
            log.debug("AsnReserveManagerImpl.updateWhAsnStatus -> whAsnDao.saveOrUpdateByVersion invoke, ouId is:[{}], logId is:[{}], whAsn is:[{}]", ouId, logId, whAsn);
        }
        int asnUpdateCount = whAsnDao.saveOrUpdateByVersion(whAsn);
        if (1 != asnUpdateCount) {
            log.error("AsnReserveManagerImpl.updateWhAsnStatus -> whAsnDao.saveOrUpdateByVersion error, updateCount != 1, ouId is:[{}], logId is:[{}], whAsn is:[{}]", ouId, logId, whAsn);
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
        if (log.isDebugEnabled()) {
            log.debug("AsnReserveManagerImpl.updateWhAsnStatus ->insertGlobalLog invoke,ouId is:[{}], logId is:[{}]", ouId, logId);
        }
        insertGlobalLog(Constants.GLOBAL_LOG_UPDATE, whAsn, ouId, userId, null, null);
        if (log.isInfoEnabled()) {
            log.info("AsnReserveManagerImpl.updateWhAsnStatus end, ouId is:[{}], logId is:[{}]", ouId, logId);
        }
    }


    /**
     * 根据asnId查找预约信息
     *
     * @author mingwei.xie
     * @param asnId
     * @param ouId
     * @param logId
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public AsnReserve findAsnReserveByAsnId(Long asnId, Long ouId, String logId) {
        if (log.isInfoEnabled()) {
            log.info("AsnReserveManagerImpl.findAsnReserveByAsnId start, ouId is:[{}], logId is:[{}], asnId is:[{}]", ouId, logId, asnId);
        }
        if (null == asnId || null == ouId) {
            log.error("AsnReserveManagerImpl.findAsnReserveByAsnId error, param is null,  ouId is:[{}], logId is:[{}], asnId is:[{}]", ouId, logId, asnId);
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        if (log.isDebugEnabled()) {
            log.debug("AsnReserveManagerImpl.findAsnReserveByAsnId -> asnReserveDao.findAsnReserveByAsnId invoke, ouId is:[{}], logId is:[{}], asnId is:[{}]", ouId, logId, asnId);
        }
        AsnReserve asnReserve = asnReserveDao.findAsnReserveByAsnId(asnId, ouId);
        if (log.isDebugEnabled()) {
            log.debug("AsnReserveManagerImpl.findAsnReserveByAsnId -> asnReserveDao.findAsnReserveByAsnId result, ouId is:[{}], logId is:[{}], asnReserve is:[{}]", ouId, logId, asnReserve);
        }
        if (log.isInfoEnabled()) {
            log.info("AsnReserveManagerImpl.findAsnReserveByAsnId end, ouId is:[{}], logId is:[{}], asnReserve is:[{}]", ouId, logId, asnReserve);
        }
        return asnReserve;
    }
}
