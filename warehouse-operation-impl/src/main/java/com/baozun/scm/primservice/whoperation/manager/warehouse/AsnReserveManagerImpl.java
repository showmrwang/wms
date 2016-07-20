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
        // 查询列表页数据
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
        // 校验传入参数
        if (null == asnReserveId || null == ouId) {
            log.error("AsnReserveManagerImpl.getAsnReserveCommandById param is null,  ouId is:[{}], logId is:[{}], asnReserveId is:[{}]", ouId, logId, asnReserveId);
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        if (log.isDebugEnabled()) {
            log.debug("AsnReserveManagerImpl.getAsnReserveCommandById-> asnReserveDao.getAsnReserveCommandById invoke, ouId, logId is:[{}], asnReserveId is:[{}]", ouId, logId, asnReserveId);
        }
        // 根据id查询预约信息
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
        // 校验传入参数
        if (null == asnId || null == ouId) {
            log.error("AsnReserveManagerImpl.findAsnReserveByAsnId error, param is null,  ouId is:[{}], logId is:[{}], asnId is:[{}]", ouId, logId, asnId);
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        if (log.isDebugEnabled()) {
            log.debug("AsnReserveManagerImpl.findAsnReserveByAsnId -> asnReserveDao.findAsnReserveByAsnId invoke, ouId is:[{}], logId is:[{}], asnId is:[{}]", ouId, logId, asnId);
        }
        // 根据asn查询预约信息
        AsnReserve asnReserve = asnReserveDao.findAsnReserveByAsnId(asnId, ouId);
        if (log.isDebugEnabled()) {
            log.debug("AsnReserveManagerImpl.findAsnReserveByAsnId -> asnReserveDao.findAsnReserveByAsnId result, ouId is:[{}], logId is:[{}], asnReserve is:[{}]", ouId, logId, asnReserve);
        }
        if (log.isInfoEnabled()) {
            log.info("AsnReserveManagerImpl.findAsnReserveByAsnId end, ouId is:[{}], logId is:[{}], asnReserve is:[{}]", ouId, logId, asnReserve);
        }
        return asnReserve;
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
        // 校验传入参数
        if (null == reserveDate || null == ouId) {
            log.error("AsnReserveManagerImpl.findAsnReserveCommandByEtaDate param is null,  ouId is:[{}], logId is:[{}], reserveDate is:[{}]", ouId, logId, reserveDate);
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        if (log.isDebugEnabled()) {
            log.debug("AsnReserveManagerImpl.findAsnReserveCommandByEtaDate-> asnReserveDao.findAsnReserveCommandByEtaDate invoke, ouId, logId is:[{}], reserveDate is:[{}]", ouId, logId, reserveDate);
        }
        // 根据预约日期查询预约信息列表
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
        // 校验传入参数
        if (null == ouId) {
            log.error("AsnReserveManagerImpl.findAsnReserveCommandByEtaDate param ouId is null,  logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
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
                if (log.isDebugEnabled()) {
                    log.debug("AsnReserveManagerImpl.createAsnReserveCode-> codeManager.checkAsnReserveCodeUnique invoke, ouId, logId is:[{}], code is:[{}]", ouId, logId, code);
                }
                // 检查新生成的预约号是否已存在
                int count = asnReserveDao.checkAsnReserveCodeUnique(code, ouId);
                if (log.isDebugEnabled()) {
                    log.debug("AsnReserveManagerImpl.createAsnReserveCode-> asnReserveDao.checkAsnReserveCodeUnique result, ouId, logId is:[{}], count is:[{}]", ouId, logId, count);
                }
                if (count != 0) {
                    if (log.isDebugEnabled()) {
                        log.debug("AsnReserveManagerImpl.createAsnReserveCode code is not unique, continue loop, ouId, logId is:[{}], count is:[{}]", ouId, logId, count);
                    }
                    // 已存在则继续循环
                    continue;
                }
                isUnique = true;
                break;
            }
        }
        // 校验预约号
        if (null == code || !isUnique) {
            log.error("AsnReserveManagerImpl.createAsnReserveCode error, ouId, logId is:[{}]", ouId, logId);
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
        // 校验差传入参数
        if (null == asnReserveIdList || null == ouId) {
            log.error("AsnReserveManagerImpl.removeAsnReserve param is null, ouId is:[{}], logId is:[{}], asnReserveIdList is:[{}], userId is:[{}]", ouId, logId, asnReserveIdList, userId);
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        if (log.isDebugEnabled()) {
            log.debug("AsnReserveManagerImpl.removeAsnReserve loop asnReserveIdList,ouId is:[{}], logId is:[{}], asnReserveIdList is:[{}], userId is:[{}]", ouId, logId, asnReserveIdList, userId);
        }
        // 遍历需取消的预约ID
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
            // 更新asn状态为新建，预约时间更新为null
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
        // 校验传入参数
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
            // id存在，更新预约信息
            AsnReserve asnReserve = this.updateAsnReserve(asnReserveCommand, userId, ouId, logId);
            if (log.isDebugEnabled()) {
                log.debug("AsnReserveManagerImpl.saveAsnReserve-> this.updateWhAsnEta invoke, ouId is:[{}], logId is:[{}], asnId is:[{}]", ouId, logId, asnReserve.getAsnId());
            }
            // 更新asn预约时间
            this.updateWhAsnEta(asnReserve.getAsnId(), userId, ouId, logId, asnReserve.getEta());
        } else {
            if (log.isDebugEnabled()) {
                log.debug("AsnReserveManagerImpl.saveAsnReserve asnReserve is not exist, create asnReserve");
            }
            if (log.isDebugEnabled()) {
                log.debug("AsnReserveManagerImpl.saveAsnReserve-> this.createAsnReserve invoke, ouId is:[{}], logId is:[{}], asnReserveCommand is:[{}]", ouId, logId, asnReserveCommand);
            }
            // 创建asn预约信息
            AsnReserve asnReserve = this.createAsnReserve(asnReserveCommand, userId, ouId, logId);
            if (log.isDebugEnabled()) {
                log.debug("AsnReserveManagerImpl.saveAsnReserve-> this.updateWhAsnStatusFromNewToReserve invoke, ouId is:[{}], logId is:[{}], asnId is:[{}], eta is:[{}]", ouId, logId, asnReserve.getAsnId(), asnReserve.getEta());
            }
            // 更新asn状态为预约，更新预约时间
            this.updateWhAsnStatusFromNewToReserve(asnReserve.getAsnId(), userId, ouId, logId, asnReserve.getEta());
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
    private AsnReserve createAsnReserve(AsnReserveCommand asnReserveCommand, Long userId, Long ouId, String logId) {
        if (log.isInfoEnabled()) {
            log.info("AsnReserveManagerImpl.createAsnReserve start, ouId is:[{}], logId is:[{}], asnReserveCommand is:[{}]", ouId, logId, asnReserveCommand);
        }
        // 校验传入参数
        if (null == asnReserveCommand || null == ouId) {
            log.error("AsnReserveManagerImpl.createAsnReserve param is null, ouId is:[{}], logId is:[{}], asnReserveCommand is:[{}]", ouId, logId, asnReserveCommand);
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        // 创建新增的预约信息
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
        // 创建预约信息
        asnReserveDao.insert(asnReserve);
        if (log.isDebugEnabled()) {
            log.debug("AsnReserveManagerImpl.createAsnReserve ->insertGlobalLog invoke,ouId is:[{}], logId is:[{}]", ouId, logId);
        }
        // 保存全局日志表
        insertGlobalLog(Constants.GLOBAL_LOG_INSERT, asnReserve, ouId, userId, null, null);
        if (log.isInfoEnabled()) {
            log.info("AsnReserveManagerImpl.createAsnReserve end, ouId is:[{}], logId is:[{}]", ouId, logId);
        }
        return asnReserve;
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
    private AsnReserve updateAsnReserve(AsnReserveCommand asnReserveCommand, Long userId, Long ouId, String logId) {
        if (log.isInfoEnabled()) {
            log.info("AsnReserveManagerImpl.updateAsnReserve start, ouId is:[{}], logId is:[{}], asnReserveCommand is:[{}]", ouId, logId, asnReserveCommand);
        }
        // 校验传入参数
        if (null == asnReserveCommand || null == ouId) {
            log.error("AsnReserveManagerImpl.updateAsnReserve param is null,  ouId is:[{}], logId is:[{}], reserveDate is:[{}]", ouId, logId, asnReserveCommand);
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        if (log.isDebugEnabled()) {
            log.debug("AsnReserveManagerImpl.updateAsnReserve-> asnReserveDao.getAsnReserveById invoke, ouId is:[{}], logId is:[{}], asnReserveId is:[{}]", ouId, logId, asnReserveCommand.getId());
        }
        // 根据id查询原始预约信息
        AsnReserve asnReserve = asnReserveDao.getAsnReserveById(asnReserveCommand.getId(), ouId);
        if (log.isDebugEnabled()) {
            log.debug("AsnReserveManagerImpl.updateAsnReserve-> asnReserveDao.getAsnReserveById result, ouId is:[{}], logId is:[{}], asnReserve is:[{}]", ouId, logId, asnReserve);
        }
        // 校验原始数据
        if (null == asnReserve || PoAsnStatus.ASN_RESERVE_NEW != asnReserve.getStatus()) {
            log.error("AsnReserveManagerImpl.updateAsnReserve check data error, ouId is:[{}], logId is:[{}], asnReserveCommand is:[{}], asnReserve is:[{}]", ouId, logId, asnReserveCommand, asnReserve);
            throw new BusinessException(ErrorCodes.CHECK_DATA_ERROR);
        }
        // 更新预约属性
        asnReserve.setEta(asnReserveCommand.getEta());
        asnReserve.setLevel(asnReserveCommand.getLevel());
        asnReserve.setDeliveryTime(null);
        asnReserve.setEstParkingTime(asnReserveCommand.getEstParkingTime());
        asnReserve.setModifiedId(userId);
        if (log.isDebugEnabled()) {
            log.debug("AsnReserveManagerImpl.updateAsnReserve-> asnReserveDao.saveOrUpdateByVersion invoke, ouId is:[{}], logId is:[{}], asnReserve is:[{}]", ouId, logId, asnReserve);
        }
        // 更新预约信息
        int updateCount = asnReserveDao.saveOrUpdateByVersion(asnReserve);
        if (1 != updateCount) {
            log.error("AsnReserveManagerImpl.updateAsnReserve-> asnReserveDao.saveOrUpdateByVersion error, update count != 1, ouId is:[{}], logId is:[{}], asnReserve is:[{}]", ouId, logId, asnReserve);
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
        if (log.isDebugEnabled()) {
            log.debug("AsnReserveManagerImpl.updateAsnReserve ->insertGlobalLog invoke,ouId is:[{}], logId is:[{}]", ouId, logId);
        }
        // 保存全局日志
        insertGlobalLog(Constants.GLOBAL_LOG_UPDATE, asnReserve, ouId, userId, null, null);
        if (log.isInfoEnabled()) {
            log.info("AsnReserveManagerImpl.updateAsnReserve end, ouId is:[{}], logId is:[{}]", ouId, logId);
        }
        return asnReserve;
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
    private void updateWhAsnEta(Long asnId, Long userId, Long ouId, String logId, Date eta) {
        if (log.isInfoEnabled()) {
            log.info("AsnReserveManagerImpl.updateWhAsnEta start, ouId is:[{}], logId is:[{}], asnId is:[{}], eta is:[{}]", ouId, logId, asnId, eta);
        }
        // 校验传入参数
        if (null == asnId || null == ouId || null == userId || null == eta) {
            log.error("AsnReserveManagerImpl.updateWhAsnEta param is null,, ouId is:[{}], logId is:[{}], asnId is:[{}], eta is:[{}]", ouId, logId, asnId, eta);
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        if (log.isDebugEnabled()) {
            log.debug("AsnReserveManagerImpl.updateWhAsnEta-> whAsnDao.findWhAsnById invoke, ouId is:[{}], logId is:[{}], asnId is:[{}], eta is:[{}]", ouId, logId, asnId, eta);
        }
        // 查询原始asn
        WhAsn whAsn = whAsnDao.findWhAsnById(asnId, ouId);
        if (log.isDebugEnabled()) {
            log.debug("AsnReserveManagerImpl.updateWhAsnEta-> whAsnDao.findWhAsnById result, ouId is:[{}], logId is:[{}], whAsn is:[{}]", ouId, logId, whAsn);
        }
        // 校验原始数据
        if (null == whAsn || PoAsnStatus.ASN_RESERVE != whAsn.getStatus()) {
            log.error("AsnReserveManagerImpl.updateWhAsnEta check data error, ouId is:[{}], logId is:[{}], asnId is:[{}], whAsn is:[{}]", ouId, logId, asnId, whAsn);
            throw new BusinessException(ErrorCodes.CHECK_DATA_ERROR);
        }
        if (log.isDebugEnabled()) {
            log.debug("AsnReserveManagerImpl.updateWhAsnEta-> this.updateWhAsn invoke, ouId is:[{}], logId is:[{}], asnId is:[{}], eta is:[{}]", ouId, logId, asnId, eta);
        }
        // 更新asn属性
        whAsn.setModifiedId(userId);
        whAsn.setEta(eta);
        // 更新asn预约时间
        this.updateWhAsn(whAsn, userId, ouId, logId);
        if (log.isInfoEnabled()) {
            log.info("AsnReserveManagerImpl.updateWhAsnEta end, ouId is:[{}], logId is:[{}]", ouId, logId);
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
            log.info("AsnReserveManagerImpl.updateWhAsnStatusFromReserveToNew start, ouId is:[{}], logId is:[{}], asnId is:[{}], eta is:[{}]", ouId, logId, asnId);
        }
        // 校验传入参数
        if (null == asnId || null == ouId || null == userId) {
            log.error("AsnReserveManagerImpl.updateWhAsnStatusFromReserveToNew param is null,, ouId is:[{}], logId is:[{}], asnId is:[{}]", ouId, logId, asnId);
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        if (log.isDebugEnabled()) {
            log.debug("AsnReserveManagerImpl.updateWhAsnStatusFromReserveToNew-> whAsnDao.findWhAsnById invoke, ouId is:[{}], logId is:[{}], asnId is:[{}]", ouId, logId, asnId);
        }
        // 查询原始asn信息
        WhAsn whAsn = whAsnDao.findWhAsnById(asnId, ouId);
        if (log.isDebugEnabled()) {
            log.debug("AsnReserveManagerImpl.updateWhAsnStatusFromReserveToNew-> whAsnDao.findWhAsnById result, ouId is:[{}], logId is:[{}], whAsn is:[{}]", ouId, logId, whAsn);
        }
        // 校验原始数据
        if (null == whAsn || PoAsnStatus.ASN_RESERVE != whAsn.getStatus()) {
            log.error("AsnReserveManagerImpl.updateWhAsnStatusFromReserveToNew check data error, ouId is:[{}], logId is:[{}], whAsn is:[{}], asnId is:[{}]", ouId, logId, whAsn, asnId);
            throw new BusinessException(ErrorCodes.CHECK_DATA_ERROR);
        }
        if (log.isDebugEnabled()) {
            log.debug("AsnReserveManagerImpl.updateWhAsnStatusFromReserveToNew-> this.updateWhAsn invoke, ouId is:[{}], logId is:[{}], asnId is:[{}]", ouId, logId, asnId);
        }
        // 更新asn属性
        whAsn.setStatus(PoAsnStatus.ASN_NEW);
        whAsn.setModifiedId(userId);
        whAsn.setEta(null);
        // 更新asn状态
        this.updateWhAsn(whAsn, userId, ouId, logId);
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
    private void updateWhAsnStatusFromNewToReserve(Long asnId, Long userId, Long ouId, String logId, Date eta) {
        if (log.isInfoEnabled()) {
            log.info("AsnReserveManagerImpl.updateWhAsnStatusFromNewToReserve start, ouId is:[{}], logId is:[{}], asnId is:[{}], eta is:[{}]", ouId, logId, asnId, eta);
        }
        // 校验传入参数
        if (null == asnId || null == ouId || null == userId || null == eta) {
            log.error("AsnReserveManagerImpl.updateWhAsnStatusFromNewToReserve param is null,, ouId is:[{}], logId is:[{}], asnId is:[{}], eta is:[{}]", ouId, logId, asnId, eta);
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        if (log.isDebugEnabled()) {
            log.debug("AsnReserveManagerImpl.updateWhAsnStatusFromNewToReserve-> whAsnDao.findWhAsnById invoke, ouId is:[{}], logId is:[{}], asnId is:[{}]", ouId, logId, asnId);
        }
        // 查询原始asn信息
        WhAsn whAsn = whAsnDao.findWhAsnById(asnId, ouId);
        if (log.isDebugEnabled()) {
            log.debug("AsnReserveManagerImpl.updateWhAsnStatusFromNewToReserve-> whAsnDao.findWhAsnById result, ouId is:[{}], logId is:[{}], whAsn is:[{}]", ouId, logId, whAsn);
        }
        // 校验原始数据
        if (null == whAsn || PoAsnStatus.ASN_NEW != whAsn.getStatus()) {
            if (null == whAsn || PoAsnStatus.ASN_RESERVE != whAsn.getStatus()) {
                log.error("AsnReserveManagerImpl.updateWhAsnStatusFromNewToReserve check data error, ouId is:[{}], logId is:[{}], whAsn is:[{}], asnId is:[{}]", ouId, logId, whAsn, asnId);
                throw new BusinessException(ErrorCodes.CHECK_DATA_ERROR);
            }
        }
        // 更新asn属性
        whAsn.setStatus(PoAsnStatus.ASN_RESERVE);
        whAsn.setModifiedId(userId);
        whAsn.setEta(eta);
        if (log.isDebugEnabled()) {
            log.debug("AsnReserveManagerImpl.updateWhAsnStatusFromNewToReserve-> this.updateWhAsn invoke, ouId is:[{}], logId is:[{}], asnId is:[{}], eta is:[{}]", ouId, logId, asnId, eta);
        }
        // 更新asn状态
        this.updateWhAsn(whAsn, userId, ouId, logId);
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
        // 校验传入参数
        if (null == asnReserveId || null == ouId || null == userId) {
            log.error("AsnReserveManagerImpl.deleteAsnReserve param is null,, ouId is:[{}], logId is:[{}], asnReserveId is:[{}]", ouId, logId, asnReserveId);
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        if (log.isDebugEnabled()) {
            log.debug("AsnReserveManagerImpl.deleteAsnReserve -> asnReserveDao.getAsnReserveById invoke, ouId is:[{}], logId is:[{}], asnReserveId is:[{}]", ouId, logId, asnReserveId);
        }
        // 查询原始预约信息
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
        // 删除预约信息
        int updateCount = asnReserveDao.removeAsnReserve(asnReserve.getId(), ouId);
        if (1 != updateCount) {
            log.error("AsnReserveManagerImpl.deleteAsnReserve -> asnReserveDao.removeAsnReserve error, updateCount != 1, ouId is:[{}], logId is:[{}], asnReserveId is:[{}]", ouId, logId, asnReserveId);
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
        if (log.isDebugEnabled()) {
            log.debug("AsnReserveManagerImpl.deleteAsnReserve ->insertGlobalLog invoke,ouId is:[{}], logId is:[{}]", ouId, logId);
        }
        // 保存全局日志
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
     * @param userId
     * @param ouId
     * @param logId
     */
    private void updateWhAsn(WhAsn whAsn, Long userId, Long ouId, String logId) {
        if (log.isInfoEnabled()) {
            log.info("AsnReserveManagerImpl.updateWhAsn start, ouId is:[{}], logId is:[{}], whAsn is:[{}]", ouId, logId, whAsn);
        }
        // 校验传入参数
        if (null == whAsn || null == userId || null == ouId) {
            log.error("AsnReserveManagerImpl.updateWhAsn param is null, ouId is:[{}], logId is:[{}], whAsn is:[{}]", ouId, logId, whAsn);
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        if (log.isDebugEnabled()) {
            log.debug("AsnReserveManagerImpl.updateWhAsn -> whAsnDao.saveOrUpdateByVersion invoke, ouId is:[{}], logId is:[{}], whAsn is:[{}]", ouId, logId, whAsn);
        }
        // 更新asn
        int asnUpdateCount = whAsnDao.saveOrUpdateByVersion(whAsn);
        if (1 != asnUpdateCount) {
            log.error("AsnReserveManagerImpl.updateWhAsn -> whAsnDao.saveOrUpdateByVersion error, updateCount != 1, ouId is:[{}], logId is:[{}], whAsn is:[{}]", ouId, logId, whAsn);
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
        if (log.isDebugEnabled()) {
            log.debug("AsnReserveManagerImpl.updateWhAsn ->insertGlobalLog invoke,ouId is:[{}], logId is:[{}]", ouId, logId);
        }
        // 保存全局日志
        insertGlobalLog(Constants.GLOBAL_LOG_UPDATE, whAsn, ouId, userId, null, null);
        if (log.isInfoEnabled()) {
            log.info("AsnReserveManagerImpl.updateWhAsn end, ouId is:[{}], logId is:[{}]", ouId, logId);
        }
    }

}
