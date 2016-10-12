package com.baozun.scm.primservice.whoperation.manager.odo.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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

import com.baozun.scm.primservice.whoperation.command.odo.OdoCommand;
import com.baozun.scm.primservice.whoperation.command.odo.OdoResultCommand;
import com.baozun.scm.primservice.whoperation.command.odo.OdoSearchCommand;
import com.baozun.scm.primservice.whoperation.command.odo.wave.OdoWaveGroupResultCommand;
import com.baozun.scm.primservice.whoperation.command.odo.wave.OdoWaveGroupSearchCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.UomCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.constant.OdoStatus;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoAddressDao;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoDao;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoLineDao;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoTransportMgmtDao;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoVasDao;
import com.baozun.scm.primservice.whoperation.dao.odo.wave.WhWaveDao;
import com.baozun.scm.primservice.whoperation.dao.odo.wave.WhWaveLineDao;
import com.baozun.scm.primservice.whoperation.dao.odo.wave.WhWaveMasterDao;
import com.baozun.scm.primservice.whoperation.dao.sku.SkuDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.UomDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoAddress;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoLine;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoTransportMgmt;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoVas;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWave;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWaveLine;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWaveMaster;
import com.baozun.scm.primservice.whoperation.model.sku.Sku;
import com.baozun.scm.primservice.whoperation.model.system.SysDictionary;
import com.baozun.scm.primservice.whoperation.model.warehouse.Customer;
import com.baozun.scm.primservice.whoperation.model.warehouse.Store;

@Service("odoManager")
@Transactional
public class OdoManagerImpl extends BaseManagerImpl implements OdoManager {
    protected static final Logger log = LoggerFactory.getLogger(OdoManager.class);
    @Autowired
    private WhOdoDao whOdoDao;
    @Autowired
    private WhOdoLineDao whOdoLineDao;
    @Autowired
    private WhOdoTransportMgmtDao whOdoTransportMgmtDao;
    @Autowired
    private WhOdoAddressDao whOdoAddressDao;
    @Autowired
    private WhOdoVasDao whOdoVasDao;
    @Autowired
    private UomDao uomDao;
    @Autowired
    private SkuDao skuDao;
    @Autowired
    private WhWaveMasterDao whWaveMasterDao;
    @Autowired
    private WhWaveDao whWaveDao;
    @Autowired
    private WhWaveLineDao whWaveLineDao;
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Pagination<OdoResultCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params) {
        Pagination<OdoResultCommand> pages = this.whOdoDao.findListByQueryMapWithPageExt(page, sorts, params);
        try {

            if (pages != null) {
                List<OdoResultCommand> list = pages.getItems();
                Set<String> dic1 = new HashSet<String>();
                Set<String> dic3 = new HashSet<String>();
                Set<String> dic4 = new HashSet<String>();
                Set<String> dic5 = new HashSet<String>();
                Set<String> dic6 = new HashSet<String>();
                Set<String> dic7 = new HashSet<String>();
                Set<String> dic8 = new HashSet<String>();
                Set<String> dic9 = new HashSet<String>();
                Set<String> dic10 = new HashSet<String>();
                Set<String> dic11 = new HashSet<String>();
                Set<String> dic12 = new HashSet<String>();
                Set<Long> customerIdSet = new HashSet<Long>();
                Set<Long> storeIdSet = new HashSet<Long>();
                Set<Long> userIdSet = new HashSet<Long>();
                if (list != null && list.size() > 0) {
                    for (OdoResultCommand command : list) {
                        if (StringUtils.hasText(command.getIsWholeOrderOutbound())) {
                            dic1.add(command.getIsWholeOrderOutbound());
                        }
                        if (StringUtils.hasText(command.getCrossDockingSysmbol())) {

                            dic3.add(command.getCrossDockingSysmbol());
                        }
                        if (StringUtils.hasText(command.getModeOfTransport())) {

                            dic4.add(command.getModeOfTransport());
                        }
                        if (StringUtils.hasText(command.getEpistaticSystemsOrderType())) {

                            dic5.add(command.getEpistaticSystemsOrderType());
                        }
                        if (StringUtils.hasText(command.getOdoType())) {

                            dic6.add(command.getOdoType());
                        }
                        if (StringUtils.hasText(command.getDistributeMode())) {

                            dic7.add(command.getDistributeMode());
                        }
                        if (StringUtils.hasText(command.getOdoStatus())) {
                            dic8.add(command.getOdoStatus());

                        }
                        if (StringUtils.hasText(command.getOutboundCartonType())) {

                            dic9.add(command.getOutboundCartonType());
                        }
                        if (StringUtils.hasText(command.getDeliverGoodsTimeMode())) {

                            dic10.add(command.getDeliverGoodsTimeMode());
                        }
                        if (StringUtils.hasText(command.getIncludeFragileCargo())) {

                            dic11.add(command.getIncludeFragileCargo());
                        }
                        if (StringUtils.hasText(command.getIncludeHazardousCargo())) {

                            dic12.add(command.getIncludeHazardousCargo());
                        }
                        if (StringUtils.hasText(command.getCustomerId())) {
                            customerIdSet.add(Long.parseLong(command.getCustomerId()));
                        }
                        if (StringUtils.hasText(command.getStoreId())) {
                            storeIdSet.add(Long.parseLong(command.getStoreId()));
                        }
                        if (StringUtils.hasText(command.getCreateId())) {
                            userIdSet.add(Long.parseLong(command.getCreateId()));
                        }
                        if (StringUtils.hasText(command.getModifiedId())) {
                            userIdSet.add(Long.parseLong(command.getModifiedId()));
                        }

                    }
                    Map<String, List<String>> map = new HashMap<String, List<String>>();
                    map.put(Constants.IS_WHOLE_ORDER_OUTBOUND, new ArrayList<String>(dic1));
                    map.put(Constants.ODO_CROSS_DOCKING_SYSMBOL, new ArrayList<String>(dic3));
                    map.put(Constants.TRANSPORT_MODE, new ArrayList<String>(dic4));
                    map.put(Constants.ODO_PRE_TYPE, new ArrayList<String>(dic5));
                    map.put(Constants.ODO_TYPE, new ArrayList<String>(dic6));
                    map.put(Constants.DISTRIBUTE_MODE, new ArrayList<String>(dic7));
                    map.put(Constants.ODO_STATUS, new ArrayList<String>(dic8));
                    map.put(Constants.ODO_AIM_TYPE, new ArrayList<String>(dic9));
                    map.put(Constants.ODO_DELIVER_GOODS_TIME_MODE, new ArrayList<String>(dic10));
                    map.put(Constants.INCLUDE_FRAGILE_CARGO, new ArrayList<String>(dic11));
                    map.put(Constants.INCLUDE_HAZARDOUS_CARGO, new ArrayList<String>(dic12));

                    Map<String, SysDictionary> dicMap = this.findSysDictionaryByRedis(map);
                    Map<Long, Customer> customerMap = this.findCustomerByRedis(new ArrayList<Long>(customerIdSet));
                    Map<Long, Store> storeMap = this.findStoreByRedis(new ArrayList<Long>(storeIdSet));
                    for (OdoResultCommand command : list) {
                        if (StringUtils.hasText(command.getIsWholeOrderOutbound())) {
                            SysDictionary sys = dicMap.get(Constants.IS_WHOLE_ORDER_OUTBOUND + "_" + command.getIsWholeOrderOutbound());
                            command.setIsWholeOrderOutboundName(sys == null ? command.getIsWholeOrderOutbound() : sys.getDicLabel());
                        }
                        if (StringUtils.hasText(command.getCrossDockingSysmbol())) {
                            SysDictionary sys = dicMap.get(Constants.ODO_CROSS_DOCKING_SYSMBOL + "_" + command.getCrossDockingSysmbol());
                            command.setCrossDockingSysmbolName(sys == null ? command.getCrossDockingSysmbol() : sys.getDicLabel());
                        }
                        if (StringUtils.hasText(command.getModeOfTransport())) {
                            SysDictionary sys = dicMap.get(Constants.TRANSPORT_MODE + "_" + command.getModeOfTransport());
                            command.setModeOfTransportName(sys == null ? command.getModeOfTransport() : sys.getDicLabel());
                        }
                        if (StringUtils.hasText(command.getEpistaticSystemsOrderType())) {
                            SysDictionary sys = dicMap.get(Constants.ODO_PRE_TYPE + "_" + command.getEpistaticSystemsOrderType());
                            command.setEpistaticSystemsOrderTypeName(sys == null ? command.getEpistaticSystemsOrderType() : sys.getDicLabel());
                        }
                        if (StringUtils.hasText(command.getOdoType())) {
                            SysDictionary sys = dicMap.get(Constants.ODO_TYPE + "_" + command.getOdoType());
                            command.setOdoTypeName(sys == null ? command.getOdoType() : sys.getDicLabel());
                        }
                        if (StringUtils.hasText(command.getDistributeMode())) {
                            SysDictionary sys = dicMap.get(Constants.DISTRIBUTE_MODE + "_" + command.getDistributeMode());
                            command.setDistributeModeName(sys == null ? command.getDistributeMode() : sys.getDicLabel());
                        }
                        if (StringUtils.hasText(command.getOdoStatus())) {
                            SysDictionary sys = dicMap.get(Constants.ODO_STATUS + "_" + command.getOdoStatus());
                            command.setOdoStatusName(sys == null ? command.getOdoStatus() : sys.getDicLabel());

                        }
                        if (StringUtils.hasText(command.getOutboundCartonType())) {
                            SysDictionary sys = dicMap.get(Constants.ODO_AIM_TYPE + "_" + command.getOutboundCartonType());
                            command.setOutboundCartonTypeName(sys == null ? command.getOutboundCartonType() : sys.getDicLabel());
                        }
                        if (StringUtils.hasText(command.getDeliverGoodsTimeMode())) {
                            SysDictionary sys = dicMap.get(Constants.ODO_DELIVER_GOODS_TIME_MODE + "_" + command.getDeliverGoodsTimeMode());
                            command.setDeliverGoodsTimeModeName(sys == null ? command.getDeliverGoodsTimeMode() : sys.getDicLabel());
                        }
                        if (StringUtils.hasText(command.getIncludeFragileCargo())) {
                            SysDictionary sys = dicMap.get(Constants.INCLUDE_FRAGILE_CARGO + "_" + command.getIncludeFragileCargo());
                            command.setIncludeFragileCargoName(sys == null ? command.getIncludeFragileCargo() : sys.getDicLabel());
                        }
                        if (StringUtils.hasText(command.getIncludeHazardousCargo())) {
                            SysDictionary sys = dicMap.get(Constants.INCLUDE_HAZARDOUS_CARGO + "_" + command.getIncludeHazardousCargo());
                            command.setIncludeHazardousCargoName(sys == null ? command.getIncludeHazardousCargo() : sys.getDicLabel());
                        }
                        if (StringUtils.hasText(command.getCustomerId())) {
                            Customer customer = customerMap.get(Long.parseLong(command.getCustomerId()));
                            command.setCustomerName(customer == null ? command.getCustomerId() : customer.getCustomerName());
                        }
                        if (StringUtils.hasText(command.getStoreId())) {
                            Store store = storeMap.get(Long.parseLong(command.getStoreId()));
                            command.setStoreName(store == null ? command.getStoreId() : store.getStoreName());
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

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void createOdo(WhOdo odo, WhOdoTransportMgmt transportMgmt) {
        try {
            this.whOdoDao.insert(odo);
            transportMgmt.setOdoId(odo.getId());
            this.whOdoTransportMgmtDao.insert(transportMgmt);
        } catch (Exception e) {
            log.error(e + "");
            throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
        }
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhOdo findOdoByIdOuId(Long id, Long ouId) {
        return this.whOdoDao.findByIdOuId(id, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public int existsSkuInOdo(Long odoId, Long skuId, Long ouId) {
        return this.whOdoDao.existsSkuInOdo(odoId, skuId, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void saveUnit(WhOdoLine line, WhOdo odo) {
        try {

            this.whOdoLineDao.insert(line);
        } catch (Exception ex) {
            log.error("" + ex);
            throw new BusinessException(ErrorCodes.INSERT_DATA_ERROR);
        }
        try {
            int updateCount = this.whOdoDao.saveOrUpdateByVersion(odo);
            if (updateCount <= 0) {
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
    public void saveAddressUnit(WhOdoAddress odoAddress, WhOdo odo) {
        try {
            if (odoAddress.getId() != null) {
                int updateAddressCount = this.whOdoAddressDao.saveOrUpdate(odoAddress);
                if (updateAddressCount <= 0) {
                    throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                }
            } else {
                this.whOdoAddressDao.insert(odoAddress);
            }
            int updateOdoCount = this.whOdoDao.saveOrUpdateByVersion(odo);
            if (updateOdoCount <= 0) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception e) {
            log.error(e + "");
            throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
        }
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void deleteOdo(Long id, Long ouId, String logId) {
        try {
            // 增值服务删除
            List<WhOdoVas> vasList = this.whOdoVasDao.findOdoVasByOdoIdOdoLineIdType(id, null, null, ouId);
            if (vasList != null && vasList.size() > 0) {
                for (WhOdoVas vas : vasList) {
                    int delVasCount = this.whOdoVasDao.deleteByIdOuId(vas.getId(), ouId);
                    if (delVasCount <= 0) {
                        throw new BusinessException(ErrorCodes.DELETE_DATA_ERROR);
                    }
                }
            }
            // 明细删除
            List<WhOdoLine> lineList = this.whOdoLineDao.findOdoLineListByOdoIdOuId(id, ouId);
            if (lineList != null && lineList.size() > 0) {
                for (WhOdoLine line : lineList) {
                    int delLineCount = this.whOdoLineDao.deleteByIdOuId(line.getId(), ouId);
                    if (delLineCount <= 0) {
                        throw new BusinessException(ErrorCodes.DELETE_DATA_ERROR);
                    }
                }
            }
            // 运输服务删除
            WhOdoTransportMgmt transMgmt = this.whOdoTransportMgmtDao.findTransportMgmtByOdoIdOuId(id, ouId);
            if (transMgmt != null) {
                int delTransCount = this.whOdoTransportMgmtDao.deleteByIdOuId(transMgmt.getId(), ouId);
                if (delTransCount <= 0) {
                    throw new BusinessException(ErrorCodes.DELETE_DATA_ERROR);
                }
            }
            // 配货人信息删除
            WhOdoAddress address = this.whOdoAddressDao.findOdoAddressByOdoId(id, ouId);
            if (address != null) {
                int delAdCount = this.whOdoAddressDao.deleteByIdOuId(address.getId(), ouId);
                if (delAdCount <= 0) {
                    throw new BusinessException(ErrorCodes.DELETE_DATA_ERROR);
                }
            }
            WhOdo odo = this.whOdoDao.findByIdOuId(id, ouId);
            if (odo == null) {
                throw new BusinessException(ErrorCodes.PARAMS_ERROR);
            }
            int delOdoCount = this.whOdoDao.deleteByIdOuId(id, ouId);
            if (delOdoCount <= 0) {
                throw new BusinessException(ErrorCodes.DELETE_DATA_ERROR);
            }

        } catch (BusinessException e) {
            throw e;
        } catch (Exception ex) {
            log.error("" + ex);
        }
    }

    @Override
    public Integer getSkuNumberAwayFormSomeLines(List<Long> idArray, Long ouId) {
        return null;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Pagination<OdoWaveGroupResultCommand> findOdoListForWaveByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params) {

        Pagination<OdoWaveGroupResultCommand> pages = this.whOdoDao.findOdoListForWaveByQueryMapWithPageExt(page, sorts, params);
        try {

            if (pages != null) {
                Set<String> dic1 = new HashSet<String>();
                Set<Long> customerIdSet = new HashSet<Long>();
                Set<Long> storeIdSet = new HashSet<Long>();
                List<OdoWaveGroupResultCommand> list = pages.getItems();
                if (list != null && list.size() > 0) {
                    for (OdoWaveGroupResultCommand command : list) {
                        if (StringUtils.hasText(command.getOdoStatus())) {
                            dic1.add(command.getOdoStatus());
                        }
                        if (command.getCustomerId() != null) {
                            customerIdSet.add(command.getCustomerId());
                        }
                        if (command.getStoreId() != null) {
                            storeIdSet.add(command.getStoreId());
                        }
                    }
                    Map<String, List<String>> map = new HashMap<String, List<String>>();
                    if (dic1.size() > 0) {
                        map.put(Constants.ODO_STATUS, new ArrayList<String>(dic1));
                    }
                    Map<String, SysDictionary> dicMap = map.size() > 0 ? this.findSysDictionaryByRedis(map) : null;

                    Map<Long, Customer> customerMap = customerIdSet.size() > 0 ? this.findCustomerByRedis(new ArrayList<Long>(customerIdSet)) : null;
                    Map<Long, Store> storeMap = storeIdSet.size() > 0 ? this.findStoreByRedis(new ArrayList<Long>(storeIdSet)) : null;
                    for (OdoWaveGroupResultCommand command : list) {
                        String groupName = "";
                        if (command.getCustomerId() != null) {
                            Customer customer = customerMap.get(command.getCustomerId());
                            command.setCustomerName(customer == null ? command.getCustomerId().toString() : customer.getCustomerName());
                            groupName += "$" + command.getCustomerName();
                        }
                        if (command.getStoreId() != null) {
                            Store store = storeMap.get(command.getStoreId());
                            command.setStoreName(store == null ? command.getStoreId().toString() : store.getStoreName());
                            groupName += "$" + command.getStoreName();
                        }
                        if (StringUtils.hasText(command.getOdoStatus())) {
                            SysDictionary sys = dicMap.get(Constants.ODO_STATUS + "_" + command.getOdoStatus());
                            command.setOdoStatusName(sys.getDicLabel());
                            groupName += "$" + command.getOdoStatusName();
                        }
                        command.setGroupName(groupName);
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

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<OdoResultCommand> findOdoCommandListForWave(OdoSearchCommand command) {

        return this.whOdoDao.findCommandListForWave(command);
    }

    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public OdoWaveGroupResultCommand findOdoSummaryForWave(OdoWaveGroupSearchCommand command) {
        return this.whOdoDao.findOdoSummaryForWave(command);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhOdo> findOdoListForWave(OdoSearchCommand search) {
        return this.whOdoDao.findListForWave(search);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_GLOBALSOURCE)
    public List<UomCommand> findUomByGroupCode(String groupCode, Integer lifecycle) {
        return this.uomDao.findUomByGroupCode(groupCode, lifecycle);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Sku findSkuByIdToShard(Long id, Long ouId) {
        return this.skuDao.findByIdShared(id, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhWaveMaster findWaveMasterByIdouId(Long waveMasterId, Long ouId) {
        return this.whWaveMasterDao.findByIdExt(waveMasterId, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public OdoCommand findOdoCommandByIdOuId(Long id, Long ouId) {
        return this.whOdoDao.findCommandByIdOuId(id, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void createOdoWave(WhWave wave, List<WhWaveLine> waveLineList, Map<Long, WhOdo> odoMap, List<WhOdoLine> odolineList, Long userId, String logId) {
        this.whWaveDao.insert(wave);
        for (WhWaveLine waveLine : waveLineList) {
            waveLine.setWaveId(wave.getId());
            this.whWaveLineDao.insert(waveLine);
        }
        Iterator<Entry<Long, WhOdo>> odoIt = odoMap.entrySet().iterator();
        while (odoIt.hasNext()) {
            Entry<Long, WhOdo> entry = odoIt.next();
            WhOdo odo = entry.getValue();
            odo.setLastModifyTime(new Date());
            odo.setModifiedId(userId);
            odo.setWaveCode(wave.getCode());
            odo.setOdoStatus(OdoStatus.ODO_WAVE);
            this.whOdoDao.saveOrUpdateByVersion(odo);
        }
        for (WhOdoLine line : odolineList) {
            line.setLastModifyTime(new Date());
            line.setModifiedId(userId);
            line.setWaveCode(wave.getCode());
            line.setOdoLineStatus(OdoStatus.ODOLINE_WAVE);
            this.whOdoLineDao.saveOrUpdateByVersion(line);
        }
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Boolean updateOdoStatus(Long odoId, Long odoLineId, Long ouId, String status) {
        if (null == ouId || null == status) {
            throw new BusinessException("没有数据");
        }
        if (null != odoId) {
            // 需要更新出库单头状态
            WhOdo odo = this.whOdoDao.findByIdOuId(odoId, ouId);
            WhOdo whOdo = new WhOdo();
            BeanUtils.copyProperties(odo, whOdo);
            whOdo.setOdoStatus(status);
            int cnt = whOdoDao.saveOrUpdateByVersion(whOdo);
            if (cnt <= 0) {
                throw new BusinessException("更新出库单头状态失败");
            }
        }
        if (null != odoLineId) {
            // 需要更新出库单明细行状态
            WhOdoLine odoLine = this.whOdoLineDao.findOdoLineById(odoLineId, ouId);
            WhOdoLine whOdoLine = new WhOdoLine();
            BeanUtils.copyProperties(odoLine, whOdoLine);
            whOdoLine.setOdoLineStatus(status);
            int cnt = whOdoLineDao.saveOrUpdateByVersion(whOdoLine);
            if (cnt <= 0) {
                throw new BusinessException("更新出库单明细状态失败");
            }
        }
        return true;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void removeOdoAndLineWhole(Long waveId, Long odoId, List<Long> odoLineIds, Long ouId) {
        // 1.剔除出库单明细行
        removeOdoLine(odoLineIds, ouId);
        // 2.剔除出库单头
        removeOdo(odoId, ouId);
        this.removeWaveLineWhole(waveId, odoId, ouId);
    }

    /**
     * 剔除出库单明细行
     * @param whWaveLineIds
     * @param ouId
     */
    private void removeOdoLine(List<Long> odoLineIds, Long ouId) {
        if (null != odoLineIds && !odoLineIds.isEmpty()) {
            for (Long odoLineId : odoLineIds) {
                WhOdoLine whOdoLine = this.whOdoLineDao.findOdoLineById(odoLineId, ouId);
                whOdoLine.setWaveCode(null);
                whOdoLine.setOdoLineStatus(OdoStatus.ODOLINE_NEW);
                int cnt = this.whOdoLineDao.saveOrUpdateByVersion(null);
                if (cnt <= 0) {
                    throw new BusinessException("剔除逻辑-更新出库单明细-失败");
                }
            }
        }
    }

    /**
     * 剔除出库单头
     * @param odoId
     * @param ouId
     */
    private void removeOdo(Long odoId, Long ouId) {
        WhOdoLine whOdoLine = new WhOdoLine();
        whOdoLine.setOdoId(odoId);
        whOdoLine.setOuId(ouId);
        whOdoLine.setOdoLineStatus(OdoStatus.ODOLINE_NEW);
        WhOdo whOdo = this.whOdoDao.findByIdOuId(odoId, ouId);
        Long cnt = this.whOdoLineDao.findListCountByParam(whOdoLine);
        if (0 == cnt) {
            whOdo.setOdoStatus(OdoStatus.ODO_NEW);
        }
        whOdo.setWaveCode(null);
        int count = this.whOdoDao.saveOrUpdateByVersion(whOdo);
        if (count <= 0) {
            throw new BusinessException("剔除逻辑-更新出库单头-失败");
        }

    }
}
