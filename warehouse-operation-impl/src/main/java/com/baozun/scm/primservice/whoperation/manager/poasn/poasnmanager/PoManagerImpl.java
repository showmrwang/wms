package com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import com.baozun.scm.primservice.whoperation.command.poasn.WhPoCommand;
import com.baozun.scm.primservice.whoperation.command.system.GlobalLogCommand;
import com.baozun.scm.primservice.whoperation.command.whinterface.inbound.WhInboundConfirmCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.constant.PoAsnStatus;
import com.baozun.scm.primservice.whoperation.dao.poasn.BiPoDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.BiPoLineDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhPoDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhPoLineDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.StoreDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.system.GlobalLogManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.inbound.WhInboundManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.inventory.WhSkuInventoryManager;
import com.baozun.scm.primservice.whoperation.model.poasn.BiPo;
import com.baozun.scm.primservice.whoperation.model.poasn.BiPoLine;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPo;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPoLine;
import com.baozun.scm.primservice.whoperation.model.system.SysDictionary;
import com.baozun.scm.primservice.whoperation.model.warehouse.Customer;
import com.baozun.scm.primservice.whoperation.model.warehouse.Store;


/**
 * 创建PO单据
 * 
 * @author bin.hu
 * 
 */
@Service("poManager")
@Transactional
public class PoManagerImpl extends BaseManagerImpl implements PoManager {
    protected static final Logger log = LoggerFactory.getLogger(PoManager.class);
    @Autowired
    private WhPoDao whPoDao;
    @Autowired
    private WhPoLineDao whPoLineDao;
    @Autowired
    private GlobalLogManager globalLogManager;
    @Autowired
    private BiPoDao biPoDao;
    @Autowired
    private BiPoLineDao biPoLineDao;
    @Autowired
    private WhInboundManager whInboundManager;
    @Autowired
    private WhSkuInventoryManager whSkuInventoryManager;
    @Autowired
    private StoreDao storeDao;
    /**
     * 读取拆分库PO单数据
     * 
     * @param page
     * @param sorts
     * @param params
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Pagination<WhPoCommand> findListByQueryMapWithPageExtByShard(Page page, Sort[] sorts, Map<String, Object> params) {
        Pagination<WhPoCommand> pages = this.whPoDao.findListByQueryMapWithPageExt(page, sorts, params);
        try {
            if (null != pages) {
                List<WhPoCommand> list = pages.getItems();
                Set<String> dic1 = new HashSet<String>();
                Set<String> dic2 = new HashSet<String>();
                Set<Long> customerIdSet = new HashSet<Long>();
                Set<Long> storeIdSet = new HashSet<Long>();
                if (null != list && list.size() > 0) {
                    for (WhPoCommand command : list) {
                        if (StringUtils.hasText(command.getPoType().toString())) {
                            dic1.add(command.getPoType().toString());
                        }
                        if (StringUtils.hasText(command.getStatus().toString())) {
                            dic2.add(command.getStatus().toString());
                        }
                        // 客户
                        if (StringUtils.hasText(command.getCustomerId().toString())) {
                            customerIdSet.add(command.getCustomerId());
                        }
                        // 店铺
                        if (StringUtils.hasText(command.getStoreId().toString())) {
                            storeIdSet.add(command.getStoreId());
                        }
                    }
                    Map<String, List<String>> map = new HashMap<String, List<String>>();
                    map.put(Constants.PO_TYPE, new ArrayList<String>(dic1));
                    map.put(Constants.POSTATUS, new ArrayList<String>(dic2));
                    // 调用系统参数redis缓存方法获取对应数据
                    Map<String, SysDictionary> dicMap = this.findSysDictionaryByRedis(map);
                    // 调用客户redis缓存方法获取对应数据
                    Map<Long, Customer> customerMap = findCustomerByRedis(new ArrayList<Long>(customerIdSet));
                    Map<Long, Store> storeMap = findStoreByRedis(new ArrayList<Long>(storeIdSet));
                    // 封装数据放入List
                    for (WhPoCommand command : list) {
                        if (StringUtils.hasText(command.getPoType().toString())) {
                            // 通过groupValue+divValue获取对应系统参数对象
                            SysDictionary sys = dicMap.get(Constants.PO_TYPE + "_" + command.getPoType());
                            command.setPoTypeName(sys == null ? command.getPoType().toString() : sys.getDicLabel());
                        }
                        if (StringUtils.hasText(command.getStatus().toString())) {
                            // 通过groupValue+divValue获取对应系统参数对象
                            SysDictionary sys = dicMap.get(Constants.POSTATUS + "_" + command.getStatus());
                            command.setStatusName(sys == null ? command.getStatus().toString() : sys.getDicLabel());
                        }
                        // 客户
                        if (StringUtils.hasText(command.getCustomerId().toString())) {
                            Customer customer = customerMap.get(command.getCustomerId());
                            command.setCustomerName(customer == null ? command.getCustomerId().toString() : customer.getCustomerName());
                        }
                        // 商铺  
                        if (StringUtils.hasText(command.getStoreId().toString())) {
                            Store store = storeMap.get(command.getStoreId());
                            command.setStoreName(store == null ? command.getStoreId().toString() : store.getStoreName());
                        }
                    }
                    pages.setItems(list);
                }
            }
        } catch (Exception ex) {
            log.error(ex + "");
            throw new BusinessException(ErrorCodes.PACKAGING_ERROR);
        }
        return pages;
    }

    /**
     * 通过OP单ID查询相关信息 基础表
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public WhPoCommand findWhPoCommandByIdToInfo(Long id, Long ouId) {
        WhPoCommand whPoCommand = whPoDao.findWhPoCommandById(id, ouId);
        return whPoCommand;
    }

    /**
     * 通过OP单ID查询相关信息 拆库表
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhPoCommand findWhPoCommandByIdToShard(Long id, Long ouId) {
        WhPoCommand whPoCommand = whPoDao.findWhPoCommandById(id, ouId);
        return whPoCommand;
    }

    /**
     * 通过po单code 状态 ouid 模糊查询对应po单信息 拆库
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhPoCommand> findWhPoListByExtCodeToShard(String poCode, List<Integer> status, List<Long> customerList, List<Long> storeList, Long ouid, Integer linenum) {
        return whPoDao.findWhPoListByExtCode(status, poCode, customerList, storeList, ouid, linenum);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public void deletePoAndPoLineToInfo(WhPo po, Long userId) {
        try {
            // 删除POLINE明细信息
            List<WhPoLine> lineList = this.whPoLineDao.findWhPoLineByPoIdOuIdUuid(po.getId(), po.getOuId(), null);
            if (lineList != null && lineList.size() > 0) {
                for (WhPoLine line : lineList) {
                    BiPoLine biLine = this.biPoLineDao.findById(line.getPoLineId());
                    Double qty = biLine.getAvailableQty() + line.getAvailableQty();
                    if (qty.equals(biLine.getQtyPlanned())) {
                        biLine.setStatus(PoAsnStatus.BIPOLINE_NEW);
                    }
                    biLine.setAvailableQty(qty);
                    biLine.setModifiedId(userId);
                    int updateBiLineCount = this.biPoLineDao.saveOrUpdateByVersion(biLine);
                    if (updateBiLineCount <= 0) {
                        throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                    }
                    this.whPoLineDao.deletePoLineByIdOuId(line.getId(), line.getOuId());
                }
            }
            // 删除PO表头信息
            BiPo biPo = this.biPoDao.findBiPoByExtCodeStoreId(po.getExtCode(), po.getStoreId());
            whPoDao.deleteByIdOuId(po.getId(), po.getOuId());
            List<WhPo> whPoList = this.whPoDao.findWhPoByExtCodeStoreIdToInfo(biPo.getExtCode(), biPo.getStoreId());
            if (whPoList == null || whPoList.size() == 0) {
                biPo.setStatus(PoAsnStatus.BIPO_NEW);
            }
            biPo.setModifiedId(userId);
            int updateBiCount = this.biPoDao.saveOrUpdateByVersion(biPo);
            if (updateBiCount <= 0) {
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
    public void deletePoAndPoLineToShard(WhPo po, Long userId) {
        try {
            // 删除POLINE明细信息
            List<WhPoLine> lineList = this.whPoLineDao.findWhPoLineByPoIdOuIdUuid(po.getId(), po.getOuId(), null);
            if (lineList != null && lineList.size() > 0) {
                for (WhPoLine line : lineList) {
                    int deleteCount = this.whPoLineDao.deletePoLineByIdOuId(line.getId(), line.getOuId());
                    if (deleteCount <= 0) {
                        throw new BusinessException(ErrorCodes.DELETE_DATA_ERROR);
                    }
                }
            }
            // 删除PO表头信息
            whPoDao.deleteByIdOuId(po.getId(), po.getOuId());
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception e) {
            throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
        }
    }


    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public void saveOrUpdateByVersionToInfo(WhPo o) {
        try {
            int count = this.whPoDao.saveOrUpdateByVersion(o);
            if (count <= 0) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
            // 插入操作日志
            this.insertGlobalLog(o.getModifiedId(), new Date(), o.getClass().getSimpleName(), o, Constants.GLOBAL_LOG_UPDATE, o.getOuId());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception ex) {
            log.error("" + ex);
            throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
        }
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void saveOrUpdateByVersionToShard(WhPo o) {
        try {
            int count = this.whPoDao.saveOrUpdateByVersion(o);
            if (count <= 0) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
            // 插入操作日志
            this.insertGlobalLog(o.getModifiedId(), new Date(), o.getClass().getSimpleName(), o, Constants.GLOBAL_LOG_UPDATE, o.getOuId());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception ex) {
            log.error("" + ex);
            throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
        }
    }

    /**
     * INFO库取消PO单;取消PO单的同时需要取消所有的明细；PO单状态为新建的时候可以取消
     * 
     * @author yimin.lu
     * @mender yimin.lu 2106/7/27 PO单只允许单条取消
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public void cancelPoToInfo(WhPo whPo, Boolean isPoCancel, List<Integer> extlineNumList, Long userId) {
        cancelPo(whPo, isPoCancel, extlineNumList, userId);
    }

    /**
     * INFO库取消PO单;取消PO单的同时需要取消所有的明细
     * 
     * @author yimin.lu
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void cancelPoToShard(WhPo whPo, Boolean isPoCancel, List<Integer> extlineNumList, Long userId) {
        cancelPo(whPo, isPoCancel, extlineNumList, userId);
    }

    private void cancelPo(WhPo whPo, Boolean isPoCancel, List<Integer> extlineNumList, Long userId) {
        Long ouId = whPo.getOuId();
        log.info(this.getClass().getSimpleName() + ".cancelPoToShard method begin");
        if (log.isDebugEnabled()) {
            log.debug(this.getClass().getSimpleName() + ".cancelPoToShard params:{}", whPo);
        }
        if (PoAsnStatus.PO_NEW == whPo.getStatus()) {
            // 将对应的PO单更新状态为取消
            whPo.setStatus(PoAsnStatus.PO_CANCELED);
            whPo.setModifiedId(userId);
            int poCount = this.whPoDao.saveOrUpdateByVersion(whPo);
            if (poCount == 0) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
            // 插入操作日志
            this.insertGlobalLog(userId, new Date(), whPo.getClass().getSimpleName(), whPo, Constants.GLOBAL_LOG_UPDATE, ouId);
            // 循环取消对应的Po单明细
            List<WhPoLine> lineList = this.whPoLineDao.findWhPoLineByPoIdOuIdUuid(whPo.getId(), ouId, null);
            for (WhPoLine line : lineList) {
                line.setStatus(PoAsnStatus.POLINE_CANCELED);
                line.setModifiedId(userId);
                int lineCount = this.whPoLineDao.saveOrUpdateByVersion(line);
                if (lineCount <= 0) {
                    throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                }
                // 插入操作日志
                this.insertGlobalLog(userId, new Date(), line.getClass().getSimpleName(), line, Constants.GLOBAL_LOG_UPDATE, ouId);
            }
        } else {
            throw new BusinessException(ErrorCodes.PO_DELETE_STATUS_ERROR);
        }
    }

    /**
     * 用于插入日志操作
     * 
     * @param userId
     * @param modifyTime
     * @param objectType
     * @param modifiedValues
     * @param type
     * @param ouId
     */
    private void insertGlobalLog(Long userId, Date modifyTime, String objectType, Object modifiedValues, String type, Long ouId) {
        log.info(this.getClass().getSimpleName() + ".insertGlobalLog method begin!");
        try {
            GlobalLogCommand gl = new GlobalLogCommand();
            gl.setModifiedId(userId);
            gl.setModifyTime(modifyTime);
            gl.setObjectType(objectType);
            gl.setModifiedValues(modifiedValues);
            gl.setType(type);
            gl.setOuId(ouId);
            globalLogManager.insertGlobalLog(gl);
        } catch (Exception e) {
            log.error(" insert into global log error:{}!", e);
            throw new BusinessException(ErrorCodes.INSERT_LOG_ERROR);
        }

        log.info(this.getClass().getSimpleName() + ".insertGlobalLog method end!");
    }


    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhPo findWhPoByExtCodeStoreIdOuIdToShard(String extCode, Long storeId, Long ouId) {
        return this.whPoDao.findWhPoByExtCodeStoreIdOuId(extCode, storeId, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public WhPo findWhPoByExtCodeStoreIdOuIdToInfo(String extCode, Long storeId, Long ouId) {
        return this.whPoDao.findWhPoByExtCodeStoreIdOuId(extCode, storeId, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public List<WhPo> findWhPoByExtCodeStoreIdToInfo(String extCode, Long storeId) {
        return this.whPoDao.findWhPoByExtCodeStoreIdToInfo(extCode, storeId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public WhPo findWhPoByIdToInfo(Long id, Long ouId) {
        return this.whPoDao.findWhPoById(id, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhPo findWhPoByIdToShard(Long id, Long ouId) {
        return this.whPoDao.findWhPoById(id, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public void createSubPoWithLineToInfo(WhPo po, List<WhPoLine> whPoLineList) {
        if (null == po) {
            throw new BusinessException(ErrorCodes.PACKAGING_ERROR);
        }
        if (null == po.getId()) {
            this.whPoDao.insert(po);
        } else {
            int count = this.whPoDao.saveOrUpdateByVersion(po);
            if (count <= 0) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
        }
        for (WhPoLine line : whPoLineList) {
            line.setPoId(po.getId());
            if (null == line.getId()) {
                this.whPoLineDao.insert(line);
            } else {
                int count = this.whPoLineDao.saveOrUpdateByVersion(line);
                if (count <= 0) {
                    throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                }
            }
        }
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public void revokeSubPoToInfo(List<WhPoLine> lineList) {
        log.info("begin!");
        try {

            if (null == lineList || lineList.size() == 0) {
                throw new BusinessException(ErrorCodes.PACKAGING_ERROR);
            }
            for (WhPoLine line : lineList) {
                this.whPoLineDao.delete(line.getId());
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception ex) {
            log.error("" + ex);
            throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
        }
        log.info("end!");
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void saveSubPoToShard(String extCode, Long storeId, Long ouId, Long userId, String poCode, WhPo infoPo, List<WhPoLine> infoPoLineList) {
        WhPo shardPo = this.whPoDao.findWhPoByExtCodeStoreIdOuId(extCode, storeId, ouId);
        if (null == shardPo) {
            shardPo = new WhPo();
            BeanUtils.copyProperties(infoPo, shardPo);
            shardPo.setPoCode(poCode);
            shardPo.setModifiedId(userId);
            shardPo.setCreatedId(userId);
            shardPo.setCreateTime(new Date());
            shardPo.setLastModifyTime(new Date());
            shardPo.setId(null);
            this.whPoDao.insert(shardPo);
        } else {
            shardPo.setModifiedId(userId);
            shardPo.setQtyPlanned(infoPo.getQtyPlanned());
            this.saveOrUpdateByVersionToInfo(shardPo);
        }
        List<Integer> statusList = Arrays.asList(new Integer[] {PoAsnStatus.POLINE_NEW, PoAsnStatus.POLINE_CREATE_ASN, PoAsnStatus.POLINE_RCVD});
        for (WhPoLine infoLine : infoPoLineList) {
            WhPoLine shardLine = this.whPoLineDao.findPoLineByPolineIdAndStatusListAndPoIdAndOuId(infoLine.getPoLineId(), statusList, shardPo.getId(), ouId, null);
            if (null == shardLine) {
                shardLine = new WhPoLine();
                BeanUtils.copyProperties(infoLine, shardLine);
                shardLine.setId(null);
                shardLine.setCreatedId(userId);
                shardLine.setCreateTime(new Date());
                shardLine.setModifiedId(userId);
                shardLine.setLastModifyTime(new Date());
                shardLine.setPoId(shardPo.getId());
                this.whPoLineDao.insert(shardLine);
            } else {
                shardLine.setAvailableQty(shardLine.getAvailableQty() + infoLine.getQtyPlanned() - shardLine.getQtyPlanned());
                shardLine.setQtyPlanned(infoLine.getQtyPlanned());
                shardLine.setModifiedId(userId);
                this.whPoLineDao.saveOrUpdateByVersion(shardLine);
            }
        }
    }

    /**
     * 查找示所有的分配到仓库的PO单
     * 
     * @param extCode
     * @param storeId
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public List<WhPoCommand> findPoListByExtCodeStoreId(String extCode, String storeId) {
        List<WhPoCommand> whPoCommands = this.whPoDao.findPoListByExtCodeStoreId(extCode, storeId);
        Set<String> dic1 = new HashSet<String>();
        Set<String> dic2 = new HashSet<String>();
        Set<Long> customerIdSet = new HashSet<Long>();
        Set<Long> storeIdSet = new HashSet<Long>();
        if (null != whPoCommands && whPoCommands.size() > 0) {
            for (WhPoCommand command : whPoCommands) {
                if (StringUtils.hasText(command.getPoType().toString())) {
                    dic1.add(command.getPoType().toString());
                }
                if (StringUtils.hasText(command.getStatus().toString())) {
                    dic2.add(command.getStatus().toString());
                }
                // 客户
                if (StringUtils.hasText(command.getCustomerId().toString())) {
                    customerIdSet.add(command.getCustomerId());
                }
                // 店铺
                if (StringUtils.hasText(command.getStoreId().toString())) {
                    storeIdSet.add(command.getStoreId());
                }
            }
            Map<String, List<String>> map = new HashMap<String, List<String>>();
            map.put(Constants.PO_TYPE, new ArrayList<String>(dic1));
            map.put(Constants.POSTATUS, new ArrayList<String>(dic2));
            // 调用系统参数redis缓存方法获取对应数据
            Map<String, SysDictionary> dicMap = this.findSysDictionaryByRedis(map);
            // 调用客户redis缓存方法获取对应数据
            Map<Long, Customer> customerMap = findCustomerByRedis(new ArrayList<Long>(customerIdSet));
            Map<Long, Store> storeMap = findStoreByRedis(new ArrayList<Long>(storeIdSet));
            // 封装数据放入List
            for (WhPoCommand command : whPoCommands) {
                if (StringUtils.hasText(command.getPoType().toString())) {
                    // 通过groupValue+divValue获取对应系统参数对象
                    SysDictionary sys = dicMap.get(Constants.PO_TYPE + "_" + command.getPoType());
                    command.setPoTypeName(sys == null ? command.getPoType().toString() : sys.getDicLabel());
                }
                if (StringUtils.hasText(command.getStatus().toString())) {
                    // 通过groupValue+divValue获取对应系统参数对象
                    SysDictionary sys = dicMap.get(Constants.POSTATUS + "_" + command.getStatus());
                    command.setStatusName(sys == null ? command.getStatus().toString() : sys.getDicLabel());
                }
                // 客户
                if (StringUtils.hasText(command.getCustomerId().toString())) {
                    Customer customer = customerMap.get(command.getCustomerId());
                    command.setCustomerName(customer == null ? command.getCustomerId().toString() : customer.getCustomerName());
                }
                // 商铺  
                if (StringUtils.hasText(command.getStoreId().toString())) {
                    Store store = storeMap.get(command.getStoreId());
                    command.setStoreName(store == null ? command.getStoreId().toString() : store.getStoreName());
                }
            }
        }
        return whPoCommands;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void closePoToShard(Long id, Long ouId, Long userId) {
        closePo(id, ouId, userId, true);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public void closePoToInfo(Long id, Long ouId, Long userId) {
        try {
            closePo(id, ouId, userId, false);
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception e) {
            log.error("" + e);
            throw new BusinessException(ErrorCodes.CLOSE_PO_ERROR);
        }

    }

    private void closePo(Long id, Long ouId, Long userId, boolean flag) {
        try {
            List<WhPoLine> lineList = this.whPoLineDao.findWhPoLineByPoIdOuIdUuid(id, ouId, null);
            if (lineList != null && lineList.size() > 0) {
                for (WhPoLine line : lineList) {
                    line.setModifiedId(userId);
                    line.setStatus(PoAsnStatus.POLINE_CLOSE);
                    int updateCountLine = this.whPoLineDao.saveOrUpdateByVersion(line);
                    if (updateCountLine <= 0) {
                        throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                    }
                }
            }
            WhPo po = this.whPoDao.findWhPoById(id, ouId);
            po.setModifiedId(userId);
            po.setStatus(PoAsnStatus.PO_CLOSE);
            int updateCountPo = this.whPoDao.saveOrUpdateByVersion(po);
            if (updateCountPo <= 0) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
            // zhu.kai 收货反馈
            if (flag) {
            	Store store = storeDao.findById(po.getStoreId());
            	if (null != store && null != store.getInboundConfirmOrderType() && store.getInboundConfirmOrderType().intValue() == 1) {
					// 按PO单反馈, 上位系统单据才反馈
            		if (null != po.getIsVmi() && po.getIsVmi()) {
            			this.createInBoundConfirmData(po, lineList, ouId);
            		}
				}
			}

        } catch (BusinessException e) {
            throw e;
        } catch (Exception ex) {
            log.error(ex + "");
            throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
        }
    }

	private void createInBoundConfirmData(WhPo po, List<WhPoLine> lineList, Long ouId) {
		WhInboundConfirmCommand inboundConfirmCommand = whSkuInventoryManager.findInventoryByPo(po, lineList, ouId);
		whInboundManager.insertWhInboundData(inboundConfirmCommand);
	}

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public void snycPoToInfo(String operateType, WhPo shardPo, Boolean isPo, List<WhPoLine> lineList) {
        try {
            if (operateType.equals("EIDT_HEAD")) {
                this.snycPoToInfoWhenEditHead(shardPo);
            } else if (operateType.equals("RCVD")) {
                this.snycPoToInfoWhenRcvd(shardPo, lineList);
            } else if (operateType.equals("CLOSE")) {
                this.snycPoToInfoWhenClosed(shardPo, isPo, lineList);
            } else if (operateType.equals("RCVD_FINISH")) {
                this.snycPoToInfoWhenRcvdFinished(shardPo, lineList);
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("" + e);
            throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
        }
    }

    /**
     * 仓库下编辑头信息
     * 
     * @param infoPo
     */
    private void snycPoToInfoWhenEditHead(WhPo shardPo) {
        WhPo infoPo = this.findWhPoByExtCodeStoreIdOuIdToInfo(shardPo.getExtCode(), shardPo.getStoreId(), shardPo.getOuId());
        infoPo.setEta(shardPo.getEta());
        infoPo.setLogisticsProvider(shardPo.getLogisticsProvider());
        infoPo.setOverChageRate(shardPo.getOverChageRate());
        infoPo.setIsIqc(shardPo.getIsIqc());
        infoPo.setIsAutoClose(shardPo.getIsAutoClose());
        this.saveOrUpdateByVersionToInfo(infoPo);
        BiPo bipo = this.biPoDao.findBiPoByExtCodeStoreId(infoPo.getExtCode(), infoPo.getStoreId());
        if (bipo == null) {
            log.error("pomanager.snycPoToInfo  EDIT_HEAD find no bipo by storeId:{} and extCode:{}", infoPo.getStoreId(), infoPo.getExtCode());
            throw new BusinessException(ErrorCodes.PARAM_IS_NULL);
        }
        if (infoPo.getIsAutoClose() != null && !infoPo.getIsAutoClose()) {

            bipo.setIsAutoClose(false);
            int updateCount = this.biPoDao.saveOrUpdateByVersion(bipo);
            if (updateCount <= 0) {
                log.error("pomanager.snycPoToInfo  EDIT_HEAD update bipo:{} by version error", bipo);
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
        }

    }

    private void snycPoToInfoWhenClosed(WhPo shardPo, Boolean isPo, List<WhPoLine> lineList) {
        isPo = isPo == null ? true : isPo;
        if (!isPo) {
            this.snycPoToInfoWhenRcvdFinished(shardPo, lineList);
        }
        WhPo infoPo = this.findWhPoByExtCodeStoreIdOuIdToInfo(shardPo.getExtCode(), shardPo.getStoreId(), shardPo.getOuId());
        if (infoPo == null) {
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        // 将别的明细行给关闭掉
        this.closePoToInfo(infoPo.getId(), infoPo.getOuId(), shardPo.getModifiedId());

        // 集团下PO
        BiPo bipo = this.biPoDao.findBiPoByExtCodeStoreId(shardPo.getExtCode(), shardPo.getStoreId());
        if (bipo == null) {
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }

        // 自动关单逻辑
        if (PoAsnStatus.BIPO_RCVD_FINISH == bipo.getStatus()) {
            if (bipo.getIsAutoClose() != null && bipo.getIsAutoClose()) {
                this.closeBiPo(bipo.getId());
            }
        }

    }

    // TODO
    // @author yimin.lu 2017/4/20 关闭集团下PO单 方法单元
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public void closeBiPo(Long bipoId) {
        BiPo biPo = this.biPoDao.findById(bipoId);
        biPo.setStatus(PoAsnStatus.BIPO_CLOSE);
        int updateCount = this.biPoDao.saveOrUpdateByVersion(biPo);
        if (updateCount <= 0) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
        List<BiPoLine> bipoLineList = this.biPoLineDao.findBiPoLineByPoIdAndUuid(bipoId, null);
        if (bipoLineList != null && bipoLineList.size() > 0) {
            for (BiPoLine line : bipoLineList) {
                line.setStatus(PoAsnStatus.BIPOLINE_CLOSE);
                int count = this.biPoLineDao.saveOrUpdateByVersion(line);
                if (count <= 0) {
                    throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                }
            }
        }

    }

    private void snycPoToInfoWhenRcvdFinished(WhPo shardPo, List<WhPoLine> lineList) {
        this.snycPoToInfoWhenRcvd(shardPo, lineList);
        WhPo infoPo = this.findWhPoByExtCodeStoreIdOuIdToInfo(shardPo.getExtCode(), shardPo.getStoreId(), shardPo.getOuId());
        if (infoPo == null) {
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        infoPo.setStopTime(shardPo.getStopTime());
        infoPo.setStatus(PoAsnStatus.PO_RCVD_FINISH);
        this.saveOrUpdateByVersionToInfo(infoPo);
        // 集团下PO
        BiPo bipo = this.biPoDao.findBiPoByExtCodeStoreId(shardPo.getExtCode(), shardPo.getStoreId());
        if (bipo == null) {
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        List<BiPoLine> biPoLineList = this.biPoLineDao.findNotRcvdFinshedLineList(bipo.getId());
        if (biPoLineList == null || biPoLineList.size() == 0) {
            bipo.setStopTime(shardPo.getStopTime());
            bipo.setStatus(PoAsnStatus.BIPO_RCVD_FINISH);
        }
        int updateCount = this.biPoDao.saveOrUpdateByVersion(bipo);
        if (updateCount <= 0) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
    }

    private void snycPoToInfoWhenRcvd(WhPo shardPo, List<WhPoLine> lineList) {
        WhPo infoPo = this.findWhPoByExtCodeStoreIdOuIdToInfo(shardPo.getExtCode(), shardPo.getStoreId(), shardPo.getOuId());
        if (infoPo == null) {
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }

        // 本次收货数量
        Double qtyRcvd = shardPo.getQtyRcvd() - infoPo.getQtyRcvd();
        // 本次收货箱数
        Integer ctnRcvd = shardPo.getCtnRcvd() - (infoPo.getCtnRcvd()==null?0:infoPo.getCtnRcvd());

        infoPo.setStatus(PoAsnStatus.PO_RCVD);
        if (null == infoPo.getStartTime()) {
            infoPo.setStartTime(shardPo.getStartTime());
        }
        infoPo.setQtyRcvd(shardPo.getQtyRcvd());
        // infoPo.setCtnPlanned(shardPo.getCtnPlanned());
        infoPo.setCtnRcvd(shardPo.getCtnRcvd());
        this.saveOrUpdateByVersionToInfo(infoPo);

        // 集团下PO
        BiPo bipo = this.biPoDao.findBiPoByExtCodeStoreId(shardPo.getExtCode(), shardPo.getStoreId());
        if (bipo == null) {
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        bipo.setQtyRcvd(bipo.getQtyRcvd() + qtyRcvd);
        bipo.setCtnRcvd((bipo.getCtnRcvd() == null ? 0 : bipo.getCtnRcvd()) + ctnRcvd);
        if (null == bipo.getStartTime()) {
            bipo.setStartTime(infoPo.getStartTime());
        }


        int updateCount = this.biPoDao.saveOrUpdateByVersion(bipo);
        if (updateCount <= 0) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }

        if (lineList != null && lineList.size() > 0) {
            for (WhPoLine poLine : lineList) {
                WhPoLine infoPoLine =
                        this.whPoLineDao.findPoLineByPolineIdAndStatusListAndPoIdAndOuId(poLine.getPoLineId(), Arrays.asList(new Integer[] {PoAsnStatus.POLINE_NEW, PoAsnStatus.POLINE_CREATE_ASN, PoAsnStatus.POLINE_RCVD}), infoPo.getId(),
                                infoPo.getOuId(), null);

                // 本次收货明细收货数量
                Double lineQtyRcvd = poLine.getQtyRcvd() - (infoPoLine.getQtyRcvd() == null ? 0 : infoPoLine.getQtyRcvd());
                Integer lineCtnRcvd = poLine.getCtnRcvd() - (infoPoLine.getCtnRcvd() == null ? 0 : infoPoLine.getCtnRcvd());

                infoPoLine.setStatus(PoAsnStatus.POLINE_RCVD);
                infoPoLine.setQtyRcvd(poLine.getQtyRcvd());
                infoPoLine.setCtnRcvd(poLine.getCtnRcvd());
                infoPoLine.setAvailableQty(infoPoLine.getQtyPlanned() - infoPoLine.getQtyRcvd());
                if (infoPoLine.getQtyRcvd() >= infoPoLine.getQtyPlanned()) {
                    infoPoLine.setStatus(PoAsnStatus.POLINE_RCVD_FINISH);
                }
                int updateInfoPoLineCount = this.whPoLineDao.saveOrUpdateByVersion(infoPoLine);
                if (updateInfoPoLineCount <= 0) {
                    throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                }

                BiPoLine biPoLine = this.biPoLineDao.findById(poLine.getPoLineId());
                biPoLine.setQtyRcvd(biPoLine.getQtyRcvd() + lineQtyRcvd);
                biPoLine.setStatus(PoAsnStatus.BIPOLINE_RCVD);
                biPoLine.setCtnRcvd((biPoLine.getCtnRcvd() == null ? 0 : biPoLine.getCtnRcvd()) + lineCtnRcvd);
                if (biPoLine.getQtyRcvd() >= biPoLine.getQtyPlanned()) {
                    biPoLine.setStatus(PoAsnStatus.BIPOLINE_RCVD_FINISH);
                }
                int updateBiPoLineCount = this.biPoLineDao.saveOrUpdateByVersion(biPoLine);
                if (updateBiPoLineCount <= 0) {
                    throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                }
            }
        }

    }




	
}
