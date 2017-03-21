package com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import org.springframework.util.StringUtils;

import com.baozun.scm.baseservice.sac.manager.CodeManager;
import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnLineCommand;
import com.baozun.scm.primservice.whoperation.command.system.GlobalLogCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.command.whinterface.inbound.WhInboundConfirmCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.ContainerStatus;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.constant.PoAsnStatus;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhAsnDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhAsnLineDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhAsnSnDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhPoDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhPoLineDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.Container2ndCategoryDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.StoreDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.carton.WhCartonDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.system.GlobalLogManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.inbound.WhInboundManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.inventory.WhSkuInventoryManager;
import com.baozun.scm.primservice.whoperation.model.ResponseMsg;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsn;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsnLine;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsnSn;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPo;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPoLine;
import com.baozun.scm.primservice.whoperation.model.system.SysDictionary;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container2ndCategory;
import com.baozun.scm.primservice.whoperation.model.warehouse.Customer;
import com.baozun.scm.primservice.whoperation.model.warehouse.Store;
import com.baozun.scm.primservice.whoperation.model.warehouse.carton.WhCarton;
import com.baozun.scm.primservice.whoperation.util.StringUtil;


/**
 * 创建ASN单据
 * 
 * @author bin.hu
 * 
 */
@Service("asnManager")
@Transactional
public class AsnManagerImpl extends BaseManagerImpl implements AsnManager {
    protected static final Logger log = LoggerFactory.getLogger(AsnManager.class);
    @Autowired
    private WhAsnDao whAsnDao;
    @Autowired
    private WhPoLineDao whPoLineDao;
    @Autowired
    private WhAsnLineDao whAsnLineDao;
    @Autowired
    private WhPoDao whPoDao;
    @Autowired
    private GlobalLogManager globalLogManager;
    @Autowired
    private CodeManager codeManager;
    @Autowired
    private ContainerDao containerDao;
    @Autowired
    private Container2ndCategoryDao container2ndCategoryDao;
    @Autowired
    private WhCartonDao whCartonDao;
    @Autowired
    private WhAsnSnDao whAsnSnDao;
    @Autowired
    private StoreDao storeDao;
    @Autowired
    private WhInboundManager whInboundManager;
    @Autowired
    private WhSkuInventoryManager whSkuInventoryManager;


    /**
     * 通过asnextcode查询出asn列表
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhAsnCommand> findWhAsnListByAsnExtCode(String asnCode, Integer[] statuses, Long ouid) {
        return whAsnDao.findWhAsnListByAsnExtCode(asnCode, statuses, ouid);
    }

    /**
     * 修改拆库ASN单状态
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void editAsnStatusByShard(List<Long> asnIds, Long ouId, Integer status, Long userId) {
        log.info(this.getClass().getSimpleName() + ".editAsnStatusByShard method begin!");
        if (log.isDebugEnabled()) {
            log.debug(this.getClass().getSimpleName() + ".editAsnStatusByShard method params [asnIdS:{},ouId:{},status:{},userId:{}]", asnIds, ouId, status, userId);
        }
        for (Long id : asnIds) {
            WhAsn asn = this.whAsnDao.findWhAsnById(id, ouId);
            asn.setStatus(status);
            asn.setModifiedId(userId);
            int count = this.whAsnDao.saveOrUpdateByVersion(asn);
            if (count <= 0) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
            // 插入日志
            this.insertGlobalLog(GLOBAL_LOG_UPDATE, asn, ouId, userId, null, null);
        }

        log.info(this.getClass().getSimpleName() + ".editAsnStatusByShard method end!");
    }

    /**
     * 读取拆库
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Pagination<WhAsnCommand> findListByQueryMapWithPageExtByShard(Page page, Sort[] sorts, Map<String, Object> params) {
        Pagination<WhAsnCommand> paginationInvList = whAsnDao.findListByQueryMapWithPageExt(page, sorts, params);
        List<WhAsnCommand> asnList = paginationInvList.getItems();
        Map<String, List<String>> sysMap = new HashMap<String, List<String>>();
        List<String> dic1 = new ArrayList<String>();
        List<String> dic2 = new ArrayList<String>();
        List<Long> customerIdList = new ArrayList<Long>();
        List<Long> storeIdList = new ArrayList<Long>();
        boolean b = false;
        for (WhAsnCommand command : asnList) {
            // 封装系统参数值 INVENTORY_TYPE
            if (!StringUtil.isEmpty(command.getAsnType().toString())) {
                b = dic1.contains(null == command.getAsnType() ? "" : command.getAsnType().toString());
                if (!b) {
                    dic1.add(command.getAsnType().toString());
                }
            }
            if (!StringUtil.isEmpty(command.getStatus().toString())) {
                b = dic2.contains(null == command.getStatus() ? "" : command.getStatus().toString());
                if (!b) {
                    dic2.add(command.getStatus().toString());
                }
            }
            // 客户
            b = customerIdList.contains(command.getCustomerId());
            if (!b) {
                customerIdList.add(command.getCustomerId());
            }
            // 店铺
            b = storeIdList.contains(command.getStoreId());
            if (!b) {
                storeIdList.add(command.getStoreId());
            }
        }
        if (dic1.size() > 0) {
            sysMap.put(Constants.PO_TYPE, dic1);
        }
        if (dic2.size() > 0) {
            sysMap.put(Constants.ASNSTATUS, dic2);
        }
        // 调用系统参数redis缓存方法获取对应数据
        Map<String, SysDictionary> sysDictionary = findSysDictionaryByRedis(sysMap);
        // 调用客户redis缓存方法获取对应数据
        Map<Long, Customer> customer = findCustomerByRedis(customerIdList);
        Map<Long, Store> store = findStoreByRedis(storeIdList);
        // 封装数据放入List
        for (WhAsnCommand command : paginationInvList.getItems()) {
            // 封装数据放入List
            String invStr = "";
            String invType = "";
            SysDictionary sys = null;
            Customer c = null;
            Store s = null;
            if (!StringUtil.isEmpty(command.getStatus().toString())) {
                // 通过groupValue+divValue获取对应系统参数对象
                sys = sysDictionary.get(Constants.ASNSTATUS + "_" + command.getStatus());
                if (null != sys) {
                    invStr = sys.getDicLabel();
                }
            }
            command.setStatusName(invStr);
            invStr = "";
            if (!StringUtil.isEmpty(command.getAsnType().toString())) {
                // 通过groupValue+divValue获取对应系统参数对象
                sys = sysDictionary.get(Constants.PO_TYPE + "_" + command.getAsnType());
                if (null != sys) {
                    invStr = sys.getDicLabel();
                }
            }
            command.setPoTypeName(invStr);
            c = customer.get(command.getCustomerId());
            if (null == c) {
                command.setCustomerName(command.getCustomerId().toString());
            } else {
                command.setCustomerName(c.getCustomerName());
            }
            s = store.get(command.getStoreId());
            if (null == s) {
                command.setStoreName(command.getStoreId().toString());
            } else {
                command.setStoreName(s.getStoreName());
            }
        }
        return paginationInvList;
    }

    /**
     * 保存asn单信息
     * 
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public ResponseMsg createAsnAndLineToShare(WhAsn asn, List<WhAsnLineCommand> asnLineList, WhPo whPo, Map<Long, WhPoLine> poLineMap, ResponseMsg rm) {
        // 如果有asnline信息 asn表头信息需要查询po表头信息
        // 查询对应的PO单信息
        // WhPo whPo = whPoDao.findWhPoById(asn.getPoId(), asn.getPoOuId());
        // 插入系统日志表
        GlobalLogCommand gl = new GlobalLogCommand();
        if (null != whPo.getOuId()) {
            // 如果有ouid一个事务更新PO单状态
            if (whPo.getStatus() == PoAsnStatus.PO_NEW) {
                // 如果是新建状态 改状态为已创建ASN
                whPo.setStatus(PoAsnStatus.PO_CREATE_ASN);
                whPo.setModifiedId(asn.getModifiedId());
                int result = whPoDao.saveOrUpdateByVersion(whPo);
                if (result <= 0) {
                    throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                }
                gl.setModifiedId(whPo.getModifiedId());
                gl.setOuId(whPo.getOuId());
                gl.setObjectType(whPo.getClass().getName());
                gl.setModifiedValues(whPo);
                gl.setType(Constants.GLOBAL_LOG_UPDATE);
                globalLogManager.insertGlobalLog(gl);
            }
        }
        if (null != asnLineList) {
            WhAsn whAsn = new WhAsn();
            if (null == asn.getId()) {
                // 如果asn没有id 证明没有ASN表头 需要添加
                // 如果有asnline信息的话就是PO单明细页面创建ASN单数据
                // 把PO单有的信息copy到whasn表头内
                BeanUtils.copyProperties(whPo, whAsn);
                whAsn.setId(null);
                whAsn.setPoId(whPo.getId());
                whAsn.setPoOuId(whPo.getOuId());
                whAsn.setQtyPlanned(asn.getQtyPlanned());
                whAsn.setStatus(PoAsnStatus.ASN_NEW);
                whAsn.setAsnCode(asn.getAsnCode());
                whAsn.setOuId(asn.getOuId());
                whAsn.setAsnExtCode(asn.getAsnExtCode());
                whAsn.setAsnType(whPo.getPoType());
                whAsn.setCreateTime(new Date());
                whAsn.setCreatedId(asn.getCreatedId());
                whAsn.setLastModifyTime(new Date());
                whAsn.setModifiedId(asn.getModifiedId());
                whAsnDao.insert(whAsn);
                gl.setModifiedId(whAsn.getModifiedId());
                gl.setObjectType(whAsn.getClass().getName());
                gl.setModifiedValues(whAsn);
                gl.setType(Constants.GLOBAL_LOG_INSERT);
                gl.setOuId(whAsn.getOuId());
                globalLogManager.insertGlobalLog(gl);
            } else {
                whAsn = this.whAsnDao.findWhAsnById(asn.getId(), asn.getOuId());
                whAsn.setModifiedId(asn.getModifiedId());
                whAsn.setQtyPlanned(whAsn.getQtyPlanned() + asn.getQtyPlanned());// 已有ASN时候添加明细，修改已有ASN的计划数量
                this.whAsnDao.saveOrUpdateByVersion(whAsn);
                gl.setModifiedId(whAsn.getModifiedId());
                gl.setOuId(whAsn.getOuId());
                gl.setObjectType(whAsn.getClass().getName());
                gl.setModifiedValues(whAsn);
                gl.setType(Constants.GLOBAL_LOG_UPDATE);
                globalLogManager.insertGlobalLog(gl);

            }
            for (WhAsnLineCommand asnline : asnLineList) {
                WhAsnLine asnLine = new WhAsnLine();
                // 查询对应poline信息
                WhPoLine whPoLine = poLineMap.get(asnline.getPoLineId());
                if (asnline.getQtyPlanned() > whPoLine.getAvailableQty()) {
                    // 如果asnline计划数量 > poline的可用数量抛出异常
                    throw new BusinessException(ErrorCodes.ASNLINE_QTYPLANNED_ERROR);
                }
                BeanUtils.copyProperties(whPoLine, asnLine);
                asnLine.setId(null);
                if (null == asn.getId()) {
                    asnLine.setAsnId(whAsn.getId());
                } else {
                    asnLine.setAsnId(asn.getId());
                }
                asnLine.setPoLineId(whPoLine.getId());
                asnLine.setPoLinenum(whPoLine.getLinenum());
                asnLine.setStatus(PoAsnStatus.ASNLINE_NOT_RCVD);
                asnLine.setQtyPlanned(asnline.getQtyPlanned());
                asnLine.setCreatedId(asn.getCreatedId());
                asnLine.setCreateTime(new Date());
                asnLine.setModifiedId(asn.getCreatedId());
                asnLine.setLastModifyTime(new Date());
                whAsnLineDao.insert(asnLine);
                gl.setModifiedId(asnLine.getModifiedId());
                gl.setObjectType(asnLine.getClass().getName());
                gl.setModifiedValues(asnLine);
                gl.setOuId(asnLine.getOuId());
                gl.setType(Constants.GLOBAL_LOG_INSERT);
                globalLogManager.insertGlobalLog(gl);
                if (null != whPo.getOuId()) {
                    // 修改poline的可用数量
                    whPoLine.setAvailableQty(whPoLine.getAvailableQty() - asnline.getQtyPlanned());
                    whPoLine.setModifiedId(asn.getModifiedId());
                    if (whPoLine.getStatus() == PoAsnStatus.POLINE_NEW) {
                        // 如果明细状态为新建的话 改成已创建ASN状态
                        whPoLine.setStatus(PoAsnStatus.POLINE_CREATE_ASN);
                    }
                    int result = whPoLineDao.saveOrUpdateByVersion(whPoLine);
                    if (result <= 0) {
                        throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                    }
                    gl.setModifiedId(whPoLine.getModifiedId());
                    gl.setObjectType(whPoLine.getClass().getName());
                    gl.setModifiedValues(whPoLine);
                    gl.setType(Constants.GLOBAL_LOG_UPDATE);
                    gl.setOuId(whPoLine.getOuId());
                    globalLogManager.insertGlobalLog(gl);
                }
            }
        } else {
            // 如果没有asnline信息的话就是ASN列表创建
            // 没有ASNLINE信息直接保存ASN表头
            whAsnDao.insert(asn);
            gl.setModifiedId(asn.getModifiedId());
            gl.setObjectType(asn.getClass().getName());
            gl.setModifiedValues(asn);
            gl.setType(Constants.GLOBAL_LOG_INSERT);
            gl.setOuId(asn.getOuId());
            globalLogManager.insertGlobalLog(gl);
        }
        rm.setResponseStatus(ResponseMsg.STATUS_SUCCESS);
        rm.setMsg(asn.getId() + "");
        return rm;

    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public ResponseMsg insertAsnWithOuId(WhAsn asn, List<WhAsnLineCommand> asnLineList, WhPo whPo, Map<Long, WhPoLine> poLineMap, ResponseMsg rm) {
        String asnExtCode = asn.getAsnExtCode();
        Long storeId = asn.getStoreId();
        Long ouId = asn.getOuId();
        /* 查找在性对应的拆库表中是否有此asn单信息 */
        long count = whAsnDao.findAsnByCodeAndStore(asnExtCode, storeId, ouId);
        GlobalLogCommand gl = new GlobalLogCommand();
        /* 没有此asn单信息 */
        // @mender:yimin.lu 用于添加新的逻辑：ASN页面创建明细
        if (0 == count || asn.getId() != null) {
            // 查询对应的PO单信息
            // WhPo whPo = whPoDao.findWhPoById(asn.getPoId(), asn.getOuId());
            if (null != whPo.getOuId()) {
                if (whPo.getStatus() == PoAsnStatus.PO_NEW) {
                    // 如果是新建状态 改状态为已创建ASN
                    whPo.setStatus(PoAsnStatus.PO_CREATE_ASN);
                    whPo.setModifiedId(asn.getModifiedId());
                    int result = whPoDao.saveOrUpdateByVersion(whPo);
                    if (result <= 0) {
                        throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                    }
                    gl.setModifiedId(whPo.getModifiedId());
                    gl.setOuId(whPo.getOuId());
                    gl.setObjectType(whPo.getClass().getName());
                    gl.setModifiedValues(whPo);
                    gl.setType(Constants.GLOBAL_LOG_UPDATE);
                    globalLogManager.insertGlobalLog(gl);
                }
            }
            if (null != asnLineList) {
                WhAsn whAsn = new WhAsn();
                if (null == asn.getId()) {
                    // 如果有asnline信息的话就是PO单明细页面创建ASN单数据
                    // 把PO单有的信息copy到whasn表头内
                    BeanUtils.copyProperties(whPo, whAsn);
                    whAsn.setId(null);
                    whAsn.setOuId(asn.getOuId());
                    whAsn.setPoId(whPo.getId());
                    whAsn.setPoOuId(whPo.getOuId());
                    whAsn.setQtyPlanned(asn.getQtyPlanned());
                    whAsn.setStatus(PoAsnStatus.ASN_NEW);
                    whAsn.setAsnCode(asn.getAsnCode());
                    whAsn.setAsnExtCode(asn.getAsnExtCode());
                    whAsn.setAsnType(whPo.getPoType());
                    whAsn.setCreateTime(new Date());
                    whAsn.setCreatedId(asn.getCreatedId());
                    whAsn.setLastModifyTime(new Date());
                    whAsn.setModifiedId(asn.getModifiedId());
                    whAsnDao.insert(whAsn);
                    gl.setModifiedId(whAsn.getModifiedId());
                    gl.setOuId(whAsn.getOuId());
                    gl.setObjectType(whAsn.getClass().getName());
                    gl.setModifiedValues(whAsn);
                    gl.setType(Constants.GLOBAL_LOG_INSERT);
                    globalLogManager.insertGlobalLog(gl);
                } else {
                    whAsn = this.whAsnDao.findWhAsnById(asn.getId(), asn.getOuId());
                    whAsn.setModifiedId(asn.getModifiedId());
                    whAsn.setQtyPlanned(whAsn.getQtyPlanned() + asn.getQtyPlanned());// 已有ASN时候添加明细，修改已有ASN的计划数量
                    this.whAsnDao.saveOrUpdateByVersion(whAsn);
                    gl.setModifiedId(whAsn.getModifiedId());
                    gl.setOuId(whAsn.getOuId());
                    gl.setObjectType(whAsn.getClass().getName());
                    gl.setModifiedValues(whAsn);
                    gl.setType(Constants.GLOBAL_LOG_UPDATE);
                    globalLogManager.insertGlobalLog(gl);

                }
                for (WhAsnLineCommand asnline : asnLineList) {
                    WhAsnLine asnLine = new WhAsnLine();
                    // 查询对应poline信息
                    // WhPoLine whPoLine =
                    // whPoLineDao.findWhPoLineByIdWhPoLine(asnline.getPoLineId(), asn.getOuId());
                    WhPoLine whPoLine = poLineMap.get(asnline.getPoLineId());
                    if (asnline.getQtyPlanned() > whPoLine.getAvailableQty()) {
                        // 如果asnline计划数量 > poline的可用数量抛出异常
                        throw new BusinessException(ErrorCodes.ASNLINE_QTYPLANNED_ERROR);
                    }
                    BeanUtils.copyProperties(whPoLine, asnLine);
                    asnLine.setId(null);
                    if (null == asn.getId()) {
                        asnLine.setAsnId(whAsn.getId());
                    } else {
                        asnLine.setAsnId(asn.getId());
                    }
                    asnLine.setOuId(asn.getOuId());
                    asnLine.setPoLineId(whPoLine.getId());
                    asnLine.setPoLinenum(whPoLine.getLinenum());
                    asnLine.setStatus(PoAsnStatus.ASNLINE_NOT_RCVD);
                    asnLine.setQtyPlanned(asnline.getQtyPlanned());
                    asnLine.setCreatedId(asn.getCreatedId());
                    asnLine.setCreateTime(new Date());
                    asnLine.setModifiedId(asn.getCreatedId());
                    asnLine.setLastModifyTime(new Date());
                    whAsnLineDao.insert(asnLine);
                    gl.setModifiedId(asnLine.getModifiedId());
                    gl.setOuId(asnLine.getOuId());
                    gl.setObjectType(asnLine.getClass().getName());
                    gl.setModifiedValues(asnLine);
                    gl.setType(Constants.GLOBAL_LOG_INSERT);
                    globalLogManager.insertGlobalLog(gl);
                    if (null != whPo.getOuId()) {
                        // 修改poline的可用数量
                        whPoLine.setAvailableQty(whPoLine.getAvailableQty() - asnline.getQtyPlanned());
                        whPoLine.setModifiedId(asn.getModifiedId());
                        if (whPoLine.getStatus() == PoAsnStatus.POLINE_NEW) {
                            // 如果明细状态为新建的话 改成已创建ASN状态
                            whPoLine.setStatus(PoAsnStatus.POLINE_CREATE_ASN);
                        }
                        int result = whPoLineDao.saveOrUpdateByVersion(whPoLine);
                        if (result <= 0) {
                            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                        }
                        gl.setModifiedId(whPoLine.getModifiedId());
                        gl.setObjectType(whPoLine.getClass().getName());
                        gl.setModifiedValues(whPoLine);
                        gl.setType(Constants.GLOBAL_LOG_UPDATE);
                        gl.setOuId(whPoLine.getOuId());
                        globalLogManager.insertGlobalLog(gl);
                    }
                }
            } else {
                // 没有ASNLINE信息直接保存ASN表头
                whAsnDao.insert(asn);
                gl.setModifiedId(asn.getModifiedId());
                gl.setObjectType(asn.getClass().getName());
                gl.setModifiedValues(asn);
                gl.setType(Constants.GLOBAL_LOG_INSERT);
                gl.setOuId(asn.getOuId());
                globalLogManager.insertGlobalLog(gl);
            }
        } else {
            /* 存在此asn单信息 */
            rm.setResponseStatus(ResponseMsg.STATUS_ERROR);
            rm.setMsg(ErrorCodes.ASN_EXIST + "");
            return rm;
        }
        return rm;

    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void saveOrUpdateByVersionToShard(WhAsn whasn) {
        try {
            int count = 0;
            count = whAsnDao.saveOrUpdateByVersion(whasn);
            if (count == 0) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception ex) {
            log.error("" + ex);
            throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
        }
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void createAsnBatch(WhAsnCommand asn, WhPo whpo, List<WhPoLine> whPoLines) {
        try {
            Long userId = asn.getUserId();
            Long ouId = asn.getOuId();
            WhAsn whAsn = new WhAsn();
            BeanUtils.copyProperties(whpo, whAsn);
            // 创建whasn表头信息
            whAsn.setId(null);
            whAsn.setOuId(ouId);
            whAsn.setAsnCode(asn.getAsnCode());
            whAsn.setAsnExtCode(asn.getAsnExtCode());
            whAsn.setPoId(whpo.getId());
            whAsn.setPoOuId(whpo.getOuId());
            whAsn.setAsnType(whpo.getPoType());
            whAsn.setStatus(PoAsnStatus.ASN_NEW);
            whAsn.setCreateTime(new Date());
            whAsn.setCreatedId(userId);
            whAsn.setLastModifyTime(new Date());
            whAsn.setModifiedId(userId);
            whAsnDao.insert(whAsn);
            // 插入日志
            this.insertGlobalLog(GLOBAL_LOG_INSERT, whAsn, ouId, userId, null, null);
            if (PoAsnStatus.PO_NEW == whpo.getStatus()) {
                // 如果是新建状态 改状态为已创建ASN
                whpo.setStatus(PoAsnStatus.PO_CREATE_ASN);
                whpo.setModifiedId(userId);
                int result = whPoDao.saveOrUpdateByVersion(whpo);
                if (result <= 0) {
                    throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                }
                this.insertGlobalLog(GLOBAL_LOG_UPDATE, whpo, ouId, userId, null, null);
            }
            double qty = 0.0;// 计算计划数量
            // 插入asnline明细
            // @mender yimin.lu 可用数量为零的不创建ASN明细
            // 可用数量为零的不创建ASN明细
            Container2ndCategory c2c = null;
            // 存放已生成的箱信息
            Map<String, Long> containerMap = null;
            for (WhPoLine pl : whPoLines) {
                if (pl.getQtyPlanned() >= pl.getAvailableQty() && StringUtil.isEmpty(pl.getUuid()) && pl.getAvailableQty() > 0) {
                    // 计划数量比如大于可用数量并且UUID为空才能创建
                    WhAsnLine al = new WhAsnLine();
                    BeanUtils.copyProperties(pl, al);
                    qty = qty + pl.getAvailableQty();// 计算计划数量
                    al.setId(null);
                    al.setAsnId(whAsn.getId());
                    al.setStatus(PoAsnStatus.ASNLINE_NOT_RCVD);
                    al.setPoLinenum(pl.getLinenum());
                    al.setPoLineId(pl.getId());
                    al.setQtyPlanned(pl.getAvailableQty());// 一键创建asnline的计划数量=poline的可用数量
                    al.setCreatedId(asn.getUserId());
                    al.setCreateTime(new Date());
                    al.setModifiedId(userId);
                    al.setLastModifyTime(new Date());
                    whAsnLineDao.insert(al);
                    // 插入日志
                    this.insertGlobalLog(GLOBAL_LOG_INSERT, al, ouId, userId, whAsn.getAsnCode(), null);
                    // 修改poline的可用数量
                    pl.setAvailableQty(0.0);// 一键创建asnline poline的可用数量0
                    pl.setModifiedId(asn.getModifiedId());
                    if (pl.getStatus() == PoAsnStatus.POLINE_NEW) {
                        // 如果明细状态为新建的话 改成已创建ASN状态
                        pl.setStatus(PoAsnStatus.POLINE_CREATE_ASN);
                    }
                    int result = whPoLineDao.saveOrUpdateByVersion(pl);
                    if (result <= 0) {
                        throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                    }
                    this.insertGlobalLog(GLOBAL_LOG_UPDATE, pl, ouId, userId, whpo.getPoCode(), null);
                    // 生成箱信息
                    if (StringUtils.hasText(pl.getCartonNo())) {
                    	if (null == c2c && null == containerMap) {
							c2c = container2ndCategoryDao.findByCodeAndOuId(Constants.CONTAINER_TYPE_2ND_BOX, ouId);
							containerMap = new HashMap<String, Long>();
						}
                    	this.createCartonInfo(whAsn.getId(), pl.getCartonNo(), al, c2c, containerMap, userId, ouId);
    				}
                }
            }
            // 最后修改ASN的计划数量
            whAsn.setQtyPlanned(qty);
            int result = whAsnDao.saveOrUpdateByVersion(whAsn);
            if (result <= 0) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
            this.insertGlobalLog(GLOBAL_LOG_UPDATE, whAsn, ouId, userId, null, null);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception ex) {
            log.error("" + ex);
            throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
        }
    }

	private void createCartonInfo(Long whAsnId, String cartonNo, WhAsnLine al, Container2ndCategory c2c, Map<String, Long> containerMap, Long userId, Long ouId) {
		Long containerId = containerMap.get(cartonNo);
		if (null == containerId) {
			ContainerCommand container = containerDao.getContainerByCode(cartonNo, ouId);
			Container newContainer = new Container();
			newContainer.setName(c2c == null ? "系统预定义货箱" : c2c.getCategoryName());
			newContainer.setCreateTime(new Date());
			newContainer.setLastModifyTime(new Date());
			newContainer.setStatus(ContainerStatus.CONTAINER_STATUS_RCVD);
			newContainer.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
			newContainer.setOperatorId(userId);
			newContainer.setOuId(ouId);
			if (null == container) {
				newContainer.setCode(cartonNo);
				containerDao.insert(newContainer);
				containerId = newContainer.getId();
			} else {
				String containerCode = this.getContainerCode();
				newContainer.setCode(containerCode);
				containerDao.insert(newContainer);
				containerId = newContainer.getId();
			}
			containerMap.put(cartonNo, containerId);
		}
		WhCarton carton = new WhCarton();
		carton.setAsnId(whAsnId);
		carton.setAsnLineId(al.getId());
		carton.setSkuId(al.getSkuId());
		carton.setContainerId(containerId);// 容器
		carton.setExtContainerCode(cartonNo);
		carton.setOuId(ouId);
		carton.setBatchNo(al.getBatchNo());// 批次号
		carton.setCountryOfOrigin(al.getCountryOfOrigin());// 原产地
		carton.setQuantity(al.getQtyPlanned());// 数量
		carton.setMfgDate(al.getMfgDate());
		carton.setExpDate(al.getExpDate());
		carton.setInvStatus(al.getInvStatus());// 库存状态
		carton.setInvType(al.getInvType());// 库存类型
		// 库存属性1-5
		carton.setInvAttr1(al.getInvAttr1());
		carton.setInvAttr2(al.getInvAttr2());
		carton.setInvAttr3(al.getInvAttr3());
		carton.setInvAttr4(al.getInvAttr4());
		carton.setInvAttr5(al.getInvAttr5());
		carton.setIsCaselevel(false);// 是否caselevel
		carton.setCreatedId(userId);
		carton.setModifiedId(userId);
		carton.setCreateTime(new Date());
		carton.setLastModifyTime(new Date());
		whCartonDao.insert(carton);
		// 插入系统日志表
		insertGlobalLog(GLOBAL_LOG_INSERT, carton, carton.getOuId(), carton.getCreatedId(), null, null);
	}
    
    @Override
	public void createAsnByVmi(WhPo whpo, List<WhPoLine> whPoLines) {
    	Long ouId = whpo.getOuId();
    	Long userId = whpo.getCreatedId();
    	WhAsn whAsn = new WhAsn();
        BeanUtils.copyProperties(whpo, whAsn);
        // 创建whasn表头信息
        whAsn.setId(null);
        whAsn.setOuId(ouId);
        // WMS单据号 调用HUB编码生成器获得
        String asnCode = codeManager.generateCode(Constants.WMS, Constants.WHASN_MODEL_URL, Constants.WMS_ASN_INNER, null, null);
        if (StringUtil.isEmpty(asnCode)) {
            log.warn("CreateAsnBatch warn asnCode generateCode is null");
            throw new BusinessException(ErrorCodes.GET_GENERATECODE_NULL);
        }
        whAsn.setAsnCode(asnCode);
        String asnExtCode = codeManager.generateCode(Constants.WMS, Constants.WHASN_MODEL_URL, Constants.WMS_ASN_EXT, null, null);
        if (StringUtil.isEmpty(asnExtCode)) {
            log.warn("CreateAsnBatch warn asnExtCode generateCode is null");
            throw new BusinessException(ErrorCodes.GET_GENERATECODE_NULL);
        }
        whAsn.setAsnExtCode(asnExtCode);
        whAsn.setPoId(whpo.getId());
        whAsn.setPoOuId(whpo.getOuId());
        whAsn.setAsnType(whpo.getPoType());
        whAsn.setStatus(PoAsnStatus.ASN_NEW);
        whAsn.setCreateTime(new Date());
        whAsn.setCreatedId(userId);
        whAsn.setLastModifyTime(new Date());
        whAsn.setModifiedId(userId);
        whAsnDao.insert(whAsn);
        // 插入日志
        this.insertGlobalLog(GLOBAL_LOG_INSERT, whAsn, ouId, userId, null, null);
        if (PoAsnStatus.PO_NEW == whpo.getStatus()) {
            // 如果是新建状态 改状态为已创建ASN
            whpo.setStatus(PoAsnStatus.PO_CREATE_ASN);
            whpo.setModifiedId(userId);
            int result = whPoDao.saveOrUpdateByVersion(whpo);
            if (result <= 0) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
            this.insertGlobalLog(GLOBAL_LOG_UPDATE, whpo, ouId, userId, null, null);
        }
        double qty = 0.0;// 计算计划数量
        // 插入asnline明细
        // 可用数量为零的不创建ASN明细
        Container2ndCategory c2c = container2ndCategoryDao.findByCodeAndOuId(Constants.CONTAINER_TYPE_2ND_BOX, ouId);
        // 存放已生成的箱信息
        Map<String, Long> containerMap = new HashMap<String, Long>();
        for (WhPoLine pl : whPoLines) {
            if (pl.getQtyPlanned() >= pl.getAvailableQty() && StringUtil.isEmpty(pl.getUuid()) && pl.getAvailableQty() > 0) {
                // 计划数量比如大于可用数量并且UUID为空才能创建
                WhAsnLine al = new WhAsnLine();
                BeanUtils.copyProperties(pl, al);
                qty = qty + pl.getAvailableQty();// 计算计划数量
                al.setId(null);
                al.setAsnId(whAsn.getId());
                al.setStatus(PoAsnStatus.ASNLINE_NOT_RCVD);
                al.setPoLinenum(pl.getLinenum());
                al.setPoLineId(pl.getId());
                al.setQtyPlanned(pl.getAvailableQty());// asnline的计划数量=poline的可用数量
                al.setCreatedId(userId);
                al.setCreateTime(new Date());
                al.setModifiedId(userId);
                al.setLastModifyTime(new Date());
                whAsnLineDao.insert(al);
                // 插入日志
                this.insertGlobalLog(GLOBAL_LOG_INSERT, al, ouId, userId, whAsn.getAsnCode(), null);
                // 修改poline的可用数量
                pl.setAvailableQty(0.0);// asnline poline的可用数量0
                pl.setModifiedId(userId);
                if (pl.getStatus() == PoAsnStatus.POLINE_NEW) {
                    // 如果明细状态为新建的话 改成已创建ASN状态
                    pl.setStatus(PoAsnStatus.POLINE_CREATE_ASN);
                }
                int result = whPoLineDao.saveOrUpdateByVersion(pl);
                if (result <= 0) {
                    throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                }
                this.insertGlobalLog(GLOBAL_LOG_UPDATE, pl, ouId, userId, whpo.getPoCode(), null);
                // 生成箱信息
                if (StringUtils.hasText(pl.getCartonNo())) {
                	createCartonInfo(whAsn.getId(), pl.getCartonNo(), al, c2c, containerMap, userId, ouId);
				}
            }
        }
        // 最后修改ASN的计划数量
        whAsn.setQtyPlanned(qty);
        int result = whAsnDao.saveOrUpdateByVersion(whAsn);
        if (result <= 0) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
        this.insertGlobalLog(GLOBAL_LOG_UPDATE, whAsn, ouId, userId, null, null);
	}
    
    /***
     * 通过HUB接口获取容器编码
     * 
     * @param c2c
     * @return
     */
    private String getContainerCode() {
        String code = null;
        for (int i = 1; i <= 5; i++) {
            // 每次获取5次 5次都失败直接抛错
            code = codeManager.generateCode(Constants.WMS, Constants.CONTAINER_MODEL_URL, null, null, null);
            if (null == code) {
                // 为空记录失败次数
                continue;
            } else {
                return code;
            }
        }
        // 5次获取失败
        // 编码生成器接口异常
        log.warn("addDevanningList Interface codeManager generateCode code is null count: 5");
        throw new BusinessException(ErrorCodes.CODE_INTERFACE_REEOR);
    }
    
    
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhAsn> findWhAsnByPoIdOuIdToShard(Long poId, Long ouId) {
        return this.whAsnDao.findWhAsnByPoIdOuId(poId, ouId);
    }

    /**
     * @author yimin.lu
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void deleteAsnAndAsnLineToShard(WhAsnCommand whAsnCommand, WhPo whpo, List<WhPoLine> polineList) {
        log.info(this.getClass().getSimpleName() + ".deleteAsnAndAsnLineToShard method begin!");
        Long asnId = whAsnCommand.getId();
        Long ouId = whAsnCommand.getOuId();
        Long userId = whAsnCommand.getModifiedId();
        if (log.isDebugEnabled()) {
            log.debug(this.getClass().getSimpleName() + ".deleteAsnAndAsnLineToShard method params:[asnId:{},ouId:{},operator:{},asn:{},po:{},polineList:{}]", asnId, ouId, userId, whAsnCommand, whpo, polineList);
        }

        try {
            WhAsn asn = this.whAsnDao.findWhAsnById(asnId, ouId);
            // @mender yimin.lu 删除Asn明细相关数据:SN数据和装箱信息
            // 删除装箱信息
            WhCarton cartonSearch = new WhCarton();
            cartonSearch.setIsCaselevel(null);
            cartonSearch.setQtyRcvd(null);
            cartonSearch.setAsnId(asnId);
            cartonSearch.setOuId(ouId);
            List<WhCarton> cartonList = this.whCartonDao.findListByParam(cartonSearch);
            if (cartonList != null && cartonList.size() > 0) {
                for (WhCarton carton : cartonList) {
                    this.whCartonDao.deleteCartonById(carton.getId(), ouId);
                }
            }
            // 删除AsnLINE明细信息
            List<WhAsnLine> asnLineList = this.whAsnLineDao.findWhAsnLineByAsnIdOuId(asnId, ouId);
            if (asnLineList != null && asnLineList.size() > 0) {
                for (WhAsnLine asnLine : asnLineList) {
                    //删除SN信息#TODO
                    WhAsnSn snSearch=new WhAsnSn();
                    snSearch.setOuId(ouId);
                    snSearch.setAsnLineId(asnLine.getId());
                    List<WhAsnSn> snList=this.whAsnSnDao.findListByParam(snSearch);
                    if(snList!=null&&snList.size()>0){
                        for(WhAsnSn sn:snList){
                            
                            this.whAsnSnDao.deleteByIdOuId(sn.getId(),ouId);
                        }
                    }

                    int deleteAsnlineCount = whAsnLineDao.deleteByIdOuId(asnLine.getId(), asnLine.getOuId());
                    if (deleteAsnlineCount <= 0) {
                        log.warn("method deleteAsnAndAsnLineToShard delete asnline error: delete asnline error[params:-->id:{},ouId:{},returns:{}]  ", asnLine.getId(), asnLine.getOuId(), deleteAsnlineCount);
                        throw new BusinessException(ErrorCodes.DELETE_DATA_ERROR);
                    }
                    this.insertGlobalLog(GLOBAL_LOG_DELETE, asnLine, ouId, userId, asn.getAsnCode(), null);
                }
            }
            // 删除Asn表头信息
            int deleteAsnCount = whAsnDao.deleteByIdOuId(asnId, ouId);
            if (deleteAsnCount <= 0) {
                log.warn("method deleteAsnAndAsnLineToShard delete asn error: no asn to be deleted![params:-->id:{},ouId:{},returns:{}] ", asnId, ouId, deleteAsnCount);
                throw new BusinessException(ErrorCodes.DELETE_DATA_ERROR);
            }
            // 写入日志
            this.insertGlobalLog(GLOBAL_LOG_DELETE, asn, ouId, userId, null, null);
            // 更改PO单信息
            int updatePoCount = this.whPoDao.saveOrUpdateByVersion(whpo);
            if (updatePoCount <= 0) {
                log.warn("method deleteAsnAndAsnLineToShard update po error: update po error[params:-->id:{},ouId:{},returns:{}]  ", whpo.getId(), whpo.getOuId(), updatePoCount);
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
            this.insertGlobalLog(GLOBAL_LOG_UPDATE, whpo, ouId, userId, null, null);
            // 更改POLINE单信息
            for (WhPoLine poLine : polineList) {
                int polineCount = this.whPoLineDao.saveOrUpdateByVersion(poLine);
                if (polineCount <= 0) {
                    log.warn("method deleteAsnAndAsnLineToShard update poline error: update poline error[params:-->id:{},ouId:{},returns:{}]  ", poLine.getId(), poLine.getOuId(), polineCount);
                    throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                }
                this.insertGlobalLog(GLOBAL_LOG_UPDATE, poLine, ouId, userId, whpo.getPoCode(), null);
            }
        } catch (Exception e) {
            if (e instanceof BusinessException) {
                throw e;
            } else {
                log.error(e + "");
                throw new BusinessException(ErrorCodes.DELETE_DATA_ERROR);
            }
        }
        log.info(this.getClass().getSimpleName() + ".deleteAsnAndAsnLineToShard method end!");
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhAsn findWhAsnByIdToShard(Long id, Long ouId) {
        return this.whAsnDao.findWhAsnById(id, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public int updateByVersionForLock(Long id, Long ouid, Date lastModifyTime) {
        return this.whAsnDao.updateByVersionForLock(id, ouid, lastModifyTime);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public int updateByVersionForUnLock(Long id, Long ouid) {
        return this.whAsnDao.updateByVersionForUnLock(id, ouid);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhAsnCommand> findListByParamsExt(WhAsnCommand asn) {
        return this.whAsnDao.findListByParamExt(asn);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public long findListCountByParamsExt(WhAsnCommand asnCommand) {
        return this.whAsnDao.findListCountByParamExt(asnCommand);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void createAsnAndLineWithUuidToShard(WhAsn asn, List<WhAsnLine> lineList) {
        if (null == asn.getId()) {
            this.whAsnDao.insert(asn);
        } else {
            this.whAsnDao.saveOrUpdateByVersion(asn);
        }
        for (WhAsnLine line : lineList) {
            line.setAsnId(asn.getId());
            if (null == line.getId()) {
                this.whAsnLineDao.insert(line);
            } else {
                int updateCount = this.whAsnLineDao.saveOrUpdateByVersion(line);
                if (updateCount <= 0) {
                    throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                }
            }
        }
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void revokeAsnWithUuidToShard(WhAsnCommand command) {
        try {
            for (WhAsnLineCommand lineCommand : command.getAsnLineList()) {
                WhAsnLine asnLine = this.whAsnLineDao.findWhAsnLineById(lineCommand.getId(), command.getOuId());
                int deleteLineCount = this.whAsnLineDao.deleteByIdOuId(asnLine.getId(), asnLine.getOuId());
                if (deleteLineCount <= 0) {
                    throw new BusinessException(ErrorCodes.DELETE_DATA_ERROR);
                }
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception ex) {
            throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
        }
    }

    /**
     * 根据客户id集合，店铺id集合查询asn信息
     * @param customerList
     * @param storeList
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<Long> getWhAsnCommandByCustomerId(List<Long> customerList, List<Long> storeList) {
        // TODO Auto-generated method stub
        log.info("AsnManagerImpl getWhAsnCommandByCustomerId is start");
        if (log.isDebugEnabled()) {
            log.debug("AsnManagerImpl getWhAsnCommandByCustomerId Param is [customerList {},storeList {}]", customerList, storeList);
        }
        List<Long> list = whAsnDao.getWhAsnCommandByCustomerId(customerList, storeList);
        log.info("AsnManagerImpl getWhAsnCommandByCustomerId is end");
        return list;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void saveTempAsnWithUuidToShard(WhAsn asn, List<WhAsnLine> saveAsnLineList, List<WhAsnLine> delAsnLineList, WhPo po, List<WhPoLine> savePoLineList) {
        try {

            int updateAsnCount = this.whAsnDao.saveOrUpdateByVersion(asn);
            if (updateAsnCount <= 0) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
            // 保存修改的明细
            for (WhAsnLine asnLine : saveAsnLineList) {
                int updateAsnLineCount = this.whAsnLineDao.saveOrUpdateByVersion(asnLine);
                if (updateAsnLineCount <= 0) {
                    throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                }
            }
            // 删除临时明细
            for (WhAsnLine asnLine : delAsnLineList) {
                int delAsnLineCount = this.whAsnLineDao.deleteByIdOuId(asnLine.getId(), asnLine.getOuId());
                if (delAsnLineCount <= 0) {
                    throw new BusinessException(ErrorCodes.DELETE_DATA_ERROR);
                }
            }

            int updatePoCount = this.whPoDao.saveOrUpdateByVersion(po);
            if (updatePoCount <= 0) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
            for (WhPoLine poLine : savePoLineList) {
                int updatePoLineCount = this.whPoLineDao.saveOrUpdateByVersion(poLine);
                if (updatePoLineCount <= 0) {
                    throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                }
            }
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception e) {
            throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
        }
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void deleteAsnAndLine(WhAsn asn, List<WhAsnLine> lineList) {
        if (lineList != null && lineList.size() > 0) {
            for (WhAsnLine line : lineList) {
                this.whAsnLineDao.deleteByIdOuId(line.getId(), line.getOuId());
            }
        }
        if (asn != null) {
            this.whAsnDao.deleteByIdOuId(asn.getId(), asn.getOuId());
        }
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhAsn findTempAsnByPoIdOuIdAndLineNotUuid(Long poId, Long ouId, String uuid) {
        return this.whAsnDao.findTempAsnByPoIdOuIdAndLineNotUuid(poId, ouId, uuid);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhAsn findTempAsnByPoIdOuIdUuid(Long poId, Long ouId, String uuid) {
        return this.whAsnDao.findTempAsnByPoIdOuIdUuid(poId, ouId, uuid);
    }


    /**
     * 根据状态查询所有ASN
     *
     * @author mingwei.xie
     * @param status
     * @param ouId
     * @return
     */
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    @Override
    public List<WhAsnCommand> findAsnListByStatus(int status, Long ouId, List<Long> customerList,List<Long> storeList) {
        return whAsnDao.findAsnListByStatus(status, ouId,customerList,storeList);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void createAsn(WhAsn asn, List<WhAsnLineCommand> asnLineList) {
        try {
            whAsnDao.insert(asn);
            WhPo whpo = this.whPoDao.findWhPoById(asn.getPoId(), asn.getOuId());
            // 如果ouid不为空 在一个事务里修改PO单状态
            if (whpo.getStatus() == PoAsnStatus.PO_NEW) {
                // 如果是新建状态 改状态为已创建ASN
                whpo.setStatus(PoAsnStatus.PO_CREATE_ASN);
                whpo.setModifiedId(asn.getModifiedId());
                int result = whPoDao.saveOrUpdateByVersion(whpo);
                if (result <= 0) {
                    throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                }
            }
            double qty = Constants.DEFAULT_DOUBLE;// 计算计划数量
            // 插入asnline明细
            // @mender yimin.lu 可用数量为零的不创建ASN明细
            if (null != asnLineList && asnLineList.size() > 0) {

                for (WhAsnLineCommand al : asnLineList) {
                    WhPoLine pl = this.whPoLineDao.findWhPoLineById(al.getPoLineId(), al.getOuId());
                    if (null == pl) {
                        throw new BusinessException(ErrorCodes.OCCUPATION_RCVD_GET_ERROR);
                    }
                    if (pl.getAvailableQty() >= al.getQtyPlanned()) {
                        throw new BusinessException(ErrorCodes.PO_NO_AVAILABLE_ERROR);
                    }
                    qty = qty + al.getQtyPlanned();// 计算计划数量

                    al.setId(null);
                    al.setAsnId(asn.getId());
                    al.setStatus(PoAsnStatus.ASNLINE_NOT_RCVD);
                    al.setCreatedId(asn.getModifiedId());
                    al.setCreateTime(new Date());
                    al.setModifiedId(asn.getModifiedId());
                    al.setLastModifyTime(new Date());
                    WhAsnLine line = new WhAsnLine();
                    BeanUtils.copyProperties(al, line);
                    whAsnLineDao.insert(line);
                    // 修改poline的可用数量
                    pl.setAvailableQty(pl.getAvailableQty() - al.getQtyPlanned());
                    pl.setModifiedId(asn.getModifiedId());
                    if (pl.getStatus() == PoAsnStatus.POLINE_NEW) {
                        // 如果明细状态为新建的话 改成已创建ASN状态
                        pl.setStatus(PoAsnStatus.POLINE_CREATE_ASN);
                    }
                    int result = whPoLineDao.saveOrUpdateByVersion(pl);
                    if (result <= 0) {
                        throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                    }
                }
            }
            // 最后修改ASN的计划数量
            asn.setQtyPlanned(qty);
            int result = whAsnDao.saveOrUpdateByVersion(asn);
            if (result <= 0) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
        }
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhAsnCommand findWhAsnCommandByIdToShard(Long id, Long ouId) {
        return this.whAsnDao.findWhAsnCommandById(id, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void closeAsn(Long id, Long ouId, Long userId) {
        try {

            List<WhAsnLine> lineList = this.whAsnLineDao.findWhAsnLineByAsnIdOuId(id, ouId);
            if (lineList != null && lineList.size() > 0) {
                for (WhAsnLine line : lineList) {
                    line.setStatus(PoAsnStatus.ASNLINE_CLOSE);
                    line.setModifiedId(userId);
                    int updateCountLine = this.whAsnLineDao.saveOrUpdateByVersion(line);
                    if (updateCountLine <= 0) {
                        throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                    }
                }
            }
            WhAsn asn = this.whAsnDao.findWhAsnById(id, ouId);
            asn.setModifiedId(userId);
            asn.setStatus(PoAsnStatus.ASN_CLOSE);
            int updateCountAsn = this.whAsnDao.saveOrUpdateByVersion(asn);
            if (updateCountAsn <= 0) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
            // zhu.kai 收货反馈
            Store store = storeDao.findById(asn.getStoreId());
            if (null != store && null != store.getInboundConfirmOrderType() && store.getInboundConfirmOrderType().intValue() == 2) {
				// 按ASN单反馈, 上位系统单据才反馈
            	WhPo po = whPoDao.findWhPoById(asn.getPoId(), ouId);
        		if (null != po.getIsVmi() && po.getIsVmi()) {
        			// 根据asnId查找出asnLine, 用WhPoLine封装参数
        			List<WhPoLine> asnLineList = whAsnLineDao.findAsnInboundData(asn.getId(), ouId);
        			WhInboundConfirmCommand inboundConfirmCommand = whSkuInventoryManager.findInventoryByPo(po, asnLineList, ouId);
        			whInboundManager.insertWhInboundData(inboundConfirmCommand);
        		}
			}
            
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception e) {
            log.error(e + "");
            throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
        }
    }

}
