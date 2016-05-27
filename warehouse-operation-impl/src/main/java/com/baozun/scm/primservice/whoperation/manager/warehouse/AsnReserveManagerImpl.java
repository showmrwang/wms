package com.baozun.scm.primservice.whoperation.manager.warehouse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import lark.common.annotation.MoreDB;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.baseservice.sac.manager.CodeManager;
import com.baozun.scm.baseservice.sac.manager.PkManager;
import com.baozun.scm.primservice.whoperation.command.system.GlobalLogCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.AsnReserveCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.auth.OperationUnitDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.AsnReserveDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.system.GlobalLogManager;
import com.baozun.scm.primservice.whoperation.model.BaseModel;
import com.baozun.scm.primservice.whoperation.model.warehouse.AsnReserve;

@Service("asnReserveManager")
@Transactional
public class AsnReserveManagerImpl implements AsnReserveManager {
    private static final Logger log = LoggerFactory.getLogger(AsnReserveManagerImpl.class);

    @Autowired
    private OperationUnitDao operationUnitDao;

    @Autowired
    private AsnReserveDao asnReserveDao;

    @Autowired
    private CodeManager codeManager;

    @Autowired
    private PkManager pkManager;
    
    @Autowired
    private GlobalLogManager globalLogManager;

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Pagination<AsnReserveCommand> getListByParams(Page page, Sort[] sorts, Map<String, Object> param) {
        log.info("AsnReserveManagerImpl getListByParams is start");
        log.info("Switch to use data source {}", DbDataSource.MOREDB_SHARDSOURCE);
        if (log.isDebugEnabled()) {
            log.debug("Param param is {}", param.toString());
        }
        Pagination<AsnReserveCommand> pList = asnReserveDao.findListByQueryMapWithPageExt(page, sorts, param);
        log.info("AsnReserveManagerImpl getListByParams is end");
        return pList;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public AsnReserve getAsnReserveById(Long id) {
        log.info("AsnReserveManagerImpl getAsnReserveById is start");
        log.info("Switch to use data source {}", DbDataSource.MOREDB_SHARDSOURCE);
        if (log.isDebugEnabled()) {
            log.debug("Param id is {}", id);
        }
        AsnReserve ar = asnReserveDao.findById(id);
        log.info("AsnReserveManagerImpl getAsnReserveById is end");
        return ar;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<AsnReserveCommand> findListByQueryMapWithExt(Date eta, String groupName, int lifecycle, Long ouId) {
        log.info("AsnReserveManagerImpl findListByQueryMapWithExt is start");
        log.info("Switch to use data source {}", DbDataSource.MOREDB_SHARDSOURCE);
        if (null == eta) {
            throw new BusinessException(ErrorCodes.PARAM_IS_NULL, new Object[] {"eta"});
        }
        if (null == groupName) {
            throw new BusinessException(ErrorCodes.PARAM_IS_NULL, new Object[] {"groupName"});
        }
        if (log.isDebugEnabled()) {
            log.debug("Param eta is {}", eta);
        }
        if (log.isDebugEnabled()) {
            log.debug("Param groupName is {}", groupName);
        }
        if (log.isDebugEnabled()) {
            log.debug("Param lifecycle is {}", lifecycle);
        }
        if (log.isDebugEnabled()) {
            log.debug("Param ouId is {}", ouId);
        }
        List<AsnReserveCommand> arcList = asnReserveDao.findListByQueryMapWithExt(eta, groupName, lifecycle, ouId);
        log.info("AsnReserveManagerImpl findListByQueryMapWithExt is end");
        return arcList;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public AsnReserve saveOrUpdate(AsnReserve asnReserve) {
        log.info("AsnReserveManagerImpl saveOrUpdate is start");
        log.info("Switch to use data source {}", DbDataSource.MOREDB_SHARDSOURCE);
        // 更新
        AsnReserve s = null;
        int count = 0;
        if (log.isDebugEnabled()) {
            log.debug("if condition [null != asnReserve.getId()] is", null != asnReserve.getId());
        }
        if (null != asnReserve.getId()) {
            s = asnReserveDao.findById(asnReserve.getId());
            if (log.isDebugEnabled()) {
                log.debug("if condition [null == s] is", null == s);
            }
            if (null == s) {
                throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
            }
            asnReserve.setCreateTime(s.getCreateTime());
            asnReserve.setAsnId(s.getAsnId());
            asnReserve.setModifiedId(asnReserve.getModifiedId());
            asnReserve.setCreatedId(s.getCreatedId());
            asnReserve.setStatus(s.getStatus());
            asnReserve.setLevel(asnReserve.getLevel());
            asnReserve.setSort(s.getSort());
            asnReserve.setDeliveryTime(s.getDeliveryTime());
            asnReserve.setLastModifyTime(s.getLastModifyTime());
            // 判断预约日期是否为同一天
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date dt1 = (Date) sdf.parse(sdf.format(asnReserve.getEta()));
                Date dt2 = (Date) sdf.parse(sdf.format(s.getEta()));
                if (log.isDebugEnabled()) {
                    log.debug("if condition [dt1.getTime() !=  dt2.getTime()] is", dt1.getTime() != dt2.getTime());
                }
                if (dt1.getTime() != dt2.getTime()) {
                    asnReserve.setSort(null);
                }
            } catch (ParseException e) {
                log.error("", e);
                throw new BusinessException(ErrorCodes.PARSE_EXCEPTION_ERROR);
            }
            count = asnReserveDao.saveOrUpdateByVersion(asnReserve);
            // 修改失败
            if (log.isDebugEnabled()) {
                log.debug("if condition [count == 0] is", count == 0);
            }
            if (count == 0) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
            // 此处特殊用途，返回s对象需要有Level
            asnReserve = asnReserveDao.findByIdExt(asnReserve.getId(), asnReserve.getOuId());
            BeanUtils.copyProperties(asnReserve, s);
            insertGlobalLog(s, Constants.GLOBAL_LOG_UPDATE);// 插入全局日志
        } else {
            s = new AsnReserve();
            s.setCreateTime(new Date());
            if (null == asnReserve.getCode()) {
                throw new BusinessException(ErrorCodes.PARAM_IS_NULL, new Object[] {"asnReserve.getCode()"});
            }
            s.setCode(asnReserve.getCode());// codeManager接口
            s.setAsnCode(asnReserve.getAsnCode());
            s.setAsnId(asnReserve.getAsnId());
            s.setEta(asnReserve.getEta());// 计划到货时间
            s.setEstParkingTime(asnReserve.getEstParkingTime());// 预计停靠时间
            s.setLevel(asnReserve.getLevel());
            s.setOuId(asnReserve.getOuId());
            s.setStatus(BaseModel.LIFECYCLE_NORMAL);// 新建
            s.setCreatedId(asnReserve.getCreatedId());
            s.setModifiedId(asnReserve.getCreatedId());
            s.setSort(null);// 保存完成后进行重新排序
            s.setLastModifyTime(new Date());
            asnReserveDao.insert(s);
            insertGlobalLog(s, Constants.GLOBAL_LOG_INSERT);// 插入全局日志
        }
        log.info("AsnReserveManagerImpl saveOrUpdate is end");
        return s;
    }

    /**
     * 生成Asn预约号
     * 
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public String createAsnReserveCode() {
        log.info("Interface codeManager generateCode is start,Param 1: {},Param: 2 {}, Param 3: {}, Param 4: {}, Param 5: {}", Constants.WMS, Constants.ASN_RESERVE_MODEL_URL, null, null, null);
        String code = codeManager.generateCode(Constants.WMS, Constants.ASN_RESERVE_MODEL_URL, null, null, null);
        log.info("Interface codeManager generateCode is end,Param 1: {},Param: 2 {}, Param 3: {}, Param 4: {}, Param 5: {}", Constants.WMS, Constants.ASN_RESERVE_MODEL_URL, null, null, null);
        if (log.isDebugEnabled()) {
            log.debug("if condition [null == code] is", null == code);
        }
        if (null == code) {
            throw new BusinessException(ErrorCodes.CODE_INTERFACE_REEOR);
        }
        return code;
    };


    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public int updateLifeCycle(List<Long> ids, Integer status, Long userid, Long ouId) {
        log.info("AsnReserveManagerImpl updateLifeCycle is start");
        log.info("Switch to use data source {}", DbDataSource.MOREDB_SHARDSOURCE);
        if (log.isDebugEnabled()) {
            log.debug("Param ids is {}", Arrays.asList(ids));
        }
        if (log.isDebugEnabled()) {
            log.debug("Param status is {}", status);
        }
        if (log.isDebugEnabled()) {
            log.debug("Param userid is {}", userid);
        }
        int result = asnReserveDao.updateStatus(ids, status, userid, ouId);
        if (log.isDebugEnabled()) {
            log.debug("if condition [result <= 0] is", result <= 0);
        }
        if (result <= 0) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
        if (log.isDebugEnabled()) {
            log.debug("if condition [result != ids.size()] is", result != ids.size());
        }
        if (result != ids.size()) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_QUANTITYERROR, new Object[] {ids.size(), result});
        }
        //插入全局日志
        for (Long id : ids) {
            AsnReserve asnReserve = asnReserveDao.findByIdExt(id, ouId);
            insertGlobalLog(asnReserve, Constants.GLOBAL_LOG_UPDATE);
        }
        log.info("AsnReserveManagerImpl updateLifeCycle is end");
        return result;
    }

    /**
     * 新建Asn预约，更新排序[紧急优先级>普通]、[当日时间早>当日时间迟] Date eta, Long level,Long userId,Long asnReserveId,
     */

    public int updateAsnReserveSort(AsnReserve asnReserve, List<AsnReserveCommand> arcList,Long ouId) {
        log.info("AsnReserveManagerImpl updateAsnReserveSort is start");
        if (null == asnReserve) {
            throw new BusinessException(ErrorCodes.OBJECT_IS_NULL, new Object[] {"asnReserve"});
        }
        if (null == arcList || arcList.size() <= 0) {
            throw new BusinessException(ErrorCodes.LIST_IS_NULL, new Object[] {"arcList"});
        }
        List<AsnReserve> arList = new ArrayList<AsnReserve>();
        List<AsnReserveCommand> newAsnReserve = new ArrayList<AsnReserveCommand>();
        AsnReserve a = new AsnReserve();
        int updateCount = 0;
        // 新建Asn预约，结果集为1时，默认sort为1
        int sort = 1;
        if (log.isDebugEnabled()) {
            log.debug("if condition [arcList.size() == BaseModel.LIFECYCLE_NORMAL] is", arcList.size() == BaseModel.LIFECYCLE_NORMAL);
        }
        if (arcList.size() == BaseModel.LIFECYCLE_NORMAL) {
            asnReserve.setSort(sort);
            updateCount = asnReserveDao.saveOrUpdateByVersion(asnReserve);
            insertGlobalLog(asnReserve, Constants.GLOBAL_LOG_UPDATE);//插入全局日志，更新sort
            return updateCount;
        }
        // others
        for (AsnReserveCommand arc : arcList) {
            if (log.isDebugEnabled()) {
                log.debug("AsnReserveCommand object String is {}", arc.toString());
            }
            if (log.isDebugEnabled()) {
                log.debug("if condition [arc.getSort() == null || (arc.getId().equals(asnReserve.getId()) && arc.getSort() != null)] is", arc.getSort() == null || (arc.getId().equals(asnReserve.getId()) && arc.getSort() != null));
            }
            if (arc.getSort() == null || (arc.getId().equals(asnReserve.getId()) && arc.getSort() != null)) {
                continue;
            }
            if (log.isDebugEnabled()) {
                log.debug("if condition [null == asnReserve.getEta()] is", null == asnReserve.getEta());
            }
            if (null == asnReserve.getEta()) {
                throw new BusinessException(ErrorCodes.PARAM_IS_NULL, new Object[] {"eta"});
            }
            // Asn预约，修改任意一个先把当前的剔除来，后面排序减1，把修改的重新插入到原有队列
            if (log.isDebugEnabled()) {
                log.debug("if condition [asnReserve.getSort() != null] is", asnReserve.getSort() != null);
            }
            if (asnReserve.getSort() != null) {
                if (arc.getSort() > asnReserve.getSort()) {
                    arc.setSort(arc.getSort() - 1);
                }
            }
            newAsnReserve.add(arc);
        }
        // 新插入的Asn预约
        int newSort = compareSort(asnReserve, newAsnReserve);
        a.setId(asnReserve.getId());
        a.setSort(newSort);
        arList.add(a);
        // 插入其他Asn预约
        for (AsnReserveCommand arcn : newAsnReserve) {
            if (log.isDebugEnabled()) {
                log.debug("AsnReserveCommand object String is {}", arcn.toString());
            }
            AsnReserve areserve = compareSort(asnReserve, arcn);
            arList.add(areserve);
        }
        updateCount = asnReserveDao.updateAsnReserveSort(arList, asnReserve.getModifiedId(), asnReserve.getOuId());
        for (AsnReserve ar : arList) {
            AsnReserve ar1 = asnReserveDao.findByIdExt(ar.getId(), ouId);
            insertGlobalLog(ar1, Constants.GLOBAL_LOG_UPDATE);// 插入全局日志，批量更新sort
        }
        log.info("AsnReserveManagerImpl updateAsnReserveSort is end");
        return updateCount;
    }

    /**
     * Date eta , Long asnReserveId
     * 
     * @return
     */
    private int compareSort(AsnReserve asnReserve, List<AsnReserveCommand> newAsnReserve) {
        log.info("AsnReserveManagerImpl compareSort is start");
        if (null == asnReserve) {
            throw new BusinessException(ErrorCodes.OBJECT_IS_NULL, new Object[] {"asnReserve"});
        }
        int sort = 1;
        if (null == newAsnReserve) {
            throw new BusinessException(ErrorCodes.LIST_IS_NULL, new Object[] {"newAsnReserve"});
        }
        // 跟最高优先级的比较
        for (AsnReserveCommand arc : newAsnReserve) {
            if (log.isDebugEnabled()) {
                log.debug("AsnReserveCommand object String is {}", arc.toString());
            }
            if (log.isDebugEnabled()) {
                log.debug("if condition [null != newAsnReserve] is", null != newAsnReserve);
            }
            if (null != newAsnReserve) {
                if (log.isDebugEnabled()) {
                    log.debug("if condition [null != asnReserve.getEta()] is", null != asnReserve.getEta());
                }
                if (null != asnReserve.getEta()) {
                    SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
                    Date dt1 = null;
                    Date dt2 = null;
                    try {
                        dt1 = (Date) sdf.parse(asnReserve.getEta().toString());
                        dt2 = (Date) sdf.parse(arc.getEta().toString());
                        if (log.isDebugEnabled()) {
                            log.debug("if condition [Constants.ASN_RESERVE_URGENT.equals(asnReserve.getLevel())] is", Constants.ASN_RESERVE_URGENT.equals(asnReserve.getLevel()));
                        }
                        if (Constants.ASN_RESERVE_URGENT.equals(asnReserve.getLevel())) {
                            if (log.isDebugEnabled()) {
                                log.debug("if condition [Constants.ASN_RESERVE_URGENT.equals(arc.getLevel())] is", Constants.ASN_RESERVE_URGENT.equals(arc.getLevel()));
                            }
                            if (Constants.ASN_RESERVE_URGENT.equals(arc.getLevel())) {
                                if (log.isDebugEnabled()) {
                                    log.debug("if condition [dt1.getTime() < dt2.getTime()] is", dt1.getTime() < dt2.getTime());
                                }
                                if (dt1.getTime() < dt2.getTime()) {
                                    sort = arc.getSort();
                                    return sort;
                                } else if (dt1.getTime() >= dt2.getTime()) {
                                    sort = arc.getSort() + 1;
                                }
                            } else {
                                return sort;
                            }
                        } else {
                            if (log.isDebugEnabled()) {
                                log.debug("if condition [!Constants.ASN_RESERVE_URGENT.equals(arc.getLevel())] is", !Constants.ASN_RESERVE_URGENT.equals(arc.getLevel()));
                            }
                            if (!Constants.ASN_RESERVE_URGENT.equals(arc.getLevel())) {
                                if (log.isDebugEnabled()) {
                                    log.debug("if condition [dt1.getTime() < dt2.getTime()] is", dt1.getTime() < dt2.getTime());
                                }
                                if (dt1.getTime() < dt2.getTime()) {
                                    sort = arc.getSort();
                                    return sort;
                                } else if (dt1.getTime() >= dt2.getTime()) {
                                    sort = arc.getSort() + 1;
                                }
                            } else {
                                sort = arc.getSort() + 1;
                            }
                        }
                    } catch (ParseException e1) {
                        log.error("", e1);
                    } catch (Exception e) {
                        log.error("", e);
                    }
                }
            }
        }
        log.info("AsnReserveManagerImpl compareSort is end");
        return sort;
    }


    private AsnReserve compareSort(AsnReserve asnReserve, AsnReserveCommand arc) {
        log.info("AsnReserveManagerImpl compareSort is start");
        if (null == asnReserve) {
            throw new BusinessException(ErrorCodes.OBJECT_IS_NULL, new Object[] {"asnReserve"});
        }
        if (null == arc) {
            throw new BusinessException(ErrorCodes.OBJECT_IS_NULL, new Object[] {"arc"});
        }
        AsnReserve areserve = null;
        if (log.isDebugEnabled()) {
            log.debug("if condition [null != asnReserve.getEta()] is", null != asnReserve.getEta());
        }
        if (null != asnReserve.getEta()) {
            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
            Date dt1 = null;
            Date dt2 = null;
            try {
                dt1 = (Date) sdf.parse(asnReserve.getEta().toString());
                dt2 = (Date) sdf.parse(arc.getEta().toString());
                if (log.isDebugEnabled()) {
                    log.debug("if condition [Constants.ASN_RESERVE_URGENT.equals(asnReserve.getLevel())] is", Constants.ASN_RESERVE_URGENT.equals(asnReserve.getLevel()));
                }
                if (Constants.ASN_RESERVE_URGENT.equals(asnReserve.getLevel())) {
                    if (log.isDebugEnabled()) {
                        log.debug("if condition [Constants.ASN_RESERVE_URGENT.equals(arc.getLevel())] is", Constants.ASN_RESERVE_URGENT.equals(arc.getLevel()));
                    }
                    if (Constants.ASN_RESERVE_URGENT.equals(arc.getLevel())) {
                        if (log.isDebugEnabled()) {
                            log.debug("if condition [dt1.getTime() < dt2.getTime()] is", dt1.getTime() < dt2.getTime());
                        }
                        if (dt1.getTime() < dt2.getTime()) {
                            areserve = new AsnReserve();
                            areserve.setId(arc.getId());
                            areserve.setSort(arc.getSort() + 1);
                            return areserve;
                        } else if (dt1.getTime() >= dt2.getTime()) {
                            areserve = new AsnReserve();
                            areserve.setId(arc.getId());
                            areserve.setSort(arc.getSort());
                        }
                    } else {
                        areserve = new AsnReserve();
                        areserve.setId(arc.getId());
                        areserve.setSort(arc.getSort() + 1);
                        return areserve;
                    }
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("if condition [!Constants.ASN_RESERVE_URGENT.equals(arc.getLevel())] is", !Constants.ASN_RESERVE_URGENT.equals(arc.getLevel()));
                    }
                    if (!Constants.ASN_RESERVE_URGENT.equals(arc.getLevel())) {
                        if (log.isDebugEnabled()) {
                            log.debug("if condition [dt1.getTime() < dt2.getTime()] is", dt1.getTime() < dt2.getTime());
                        }
                        if (dt1.getTime() < dt2.getTime()) {
                            areserve = new AsnReserve();
                            areserve.setId(arc.getId());
                            areserve.setSort(arc.getSort() + 1);
                            return areserve;
                        } else if (dt1.getTime() >= dt2.getTime()) {
                            areserve = new AsnReserve();
                            areserve.setId(arc.getId());
                            areserve.setSort(arc.getSort());
                        }
                    } else {
                        areserve = new AsnReserve();
                        areserve.setId(arc.getId());
                        areserve.setSort(arc.getSort());
                        return areserve;
                    }
                }
            } catch (Exception e) {
                log.error("", e);
            }
        }
        log.info("AsnReserveManagerImpl compareSort is end");
        return areserve;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public int findListByQueryMapWithExt(AsnReserve asnReserve,Long ouId) {
        log.info("AsnReserveManagerImpl findListByQueryMapWithExt is start");
        log.info("Switch to use data source {}", DbDataSource.MOREDB_SHARDSOURCE);
        if (null == asnReserve) {
            throw new BusinessException(ErrorCodes.OBJECT_IS_NULL, new Object[] {"asnReserve"});
        }
        if (log.isDebugEnabled()) {
            log.debug("Param asnReserve is {}", asnReserve.toString());
        }
        int asnReserveSort = 0;
        List<AsnReserveCommand> arcList = findListByQueryMapWithExt(asnReserve.getEta(), Constants.ASN_RESERVE_STATUS, BaseModel.LIFECYCLE_NORMAL, asnReserve.getOuId());
        // 批量更新Asn预约顺序
        asnReserveSort = updateAsnReserveSort(asnReserve, arcList, ouId);
        // 修改失败
        if (log.isDebugEnabled()) {
            log.debug("if condition [asnReserveSort != arcList.size()] is", asnReserveSort != arcList.size());
        }
        if (asnReserveSort != arcList.size()) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
        log.info("AsnReserveManagerImpl findListByQueryMapWithExt is end");
        return asnReserveSort;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public int deleteAsnReserveById(Long id,Long ouId) {
        log.info("AsnReserveManagerImpl deleteAsnReserveById is start");
        log.info("Switch to use data source {}", DbDataSource.MOREDB_SHARDSOURCE);
        //插入全局日志
        AsnReserve asnReserve = asnReserveDao.findByIdExt(id, ouId);
        int count = asnReserveDao.deleteByIdExt(id, ouId);
        insertGlobalLog(asnReserve, Constants.GLOBAL_LOG_DELETE);
        log.info("AsnReserveManagerImpl deleteAsnReserveById is end");
        return count;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Boolean checkAsnCodeIsReserve(String asnCode, Integer status, Long ouId) {
        log.info("AsnReserveManagerImpl checkAsnCodeIsReserve is start");
        log.info("Switch to use data source {}", DbDataSource.MOREDB_SHARDSOURCE);
        if (null == asnCode) {
            throw new BusinessException(ErrorCodes.PARAM_IS_NULL, new Object[] {"asnCode"});
        }
        if (null == status) {
            throw new BusinessException(ErrorCodes.PARAM_IS_NULL, new Object[] {"status"});
        }
        if (null == ouId) {
            throw new BusinessException(ErrorCodes.PARAM_IS_NULL, new Object[] {"ouId"});
        }
        AsnReserve ar = asnReserveDao.findAsnReserveByStatusExt(asnCode, status, ouId);
        if (null == ar) {
            return true;
        }
        log.info("AsnReserveManagerImpl checkAsnCodeIsReserve is end");
        return false;
    }
    
    // check 2016-03-03 14:14
    private void insertGlobalLog(AsnReserve asnReserve, String operator) {
        GlobalLogCommand gl = new GlobalLogCommand();
        gl.setModifiedId(asnReserve.getModifiedId());
        gl.setObjectType(asnReserve.getClass().getSimpleName());
        gl.setModifiedValues(asnReserve);
        gl.setType(operator);
        gl.setOuId(asnReserve.getOuId());
        globalLogManager.insertGlobalLog(gl);
    }

}
