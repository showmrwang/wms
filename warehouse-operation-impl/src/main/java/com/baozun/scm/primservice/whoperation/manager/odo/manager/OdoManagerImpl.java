package com.baozun.scm.primservice.whoperation.manager.odo.manager;

import java.util.ArrayList;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.baozun.scm.primservice.whoperation.command.odo.OdoResultCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoAddressDao;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoDao;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoLineDao;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoTransportMgmtDao;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoVasDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoAddress;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoLine;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoTransportMgmt;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoVas;
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
                            command.setStoreId(store == null ? command.getStoreId() : store.getStoreName());
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
        try{
            this.whOdoDao.insert(odo);
            transportMgmt.setOdoId(odo.getId());
            this.whOdoTransportMgmtDao.insert(transportMgmt);
        }catch(Exception e){
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

}
