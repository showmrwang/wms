package com.baozun.scm.primservice.whoperation.manager.odo.manager;

import java.util.ArrayList;
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
import com.baozun.scm.primservice.whoperation.command.odo.OdoGroupCommand;
import com.baozun.scm.primservice.whoperation.command.odo.OdoLineCommand;
import com.baozun.scm.primservice.whoperation.command.odo.OdoResultCommand;
import com.baozun.scm.primservice.whoperation.command.odo.OdoSearchCommand;
import com.baozun.scm.primservice.whoperation.command.odo.OdoTransportMgmtCommand;
import com.baozun.scm.primservice.whoperation.command.odo.WhOdoLineSnCommand;
import com.baozun.scm.primservice.whoperation.command.odo.WhOdoVasCommand;
import com.baozun.scm.primservice.whoperation.command.odo.wave.OdoWaveGroupResultCommand;
import com.baozun.scm.primservice.whoperation.command.odo.wave.OdoWaveGroupSearchCommand;
import com.baozun.scm.primservice.whoperation.command.odo.wave.WaveCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.UomCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhDistributionPatternRuleCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.constant.OdoStatus;
import com.baozun.scm.primservice.whoperation.dao.localauth.OperUserDao;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoAddressDao;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoDao;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoInvoiceDao;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoInvoiceLineDao;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoLineDao;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoLineSnDao;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoTransportMgmtDao;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoVasDao;
import com.baozun.scm.primservice.whoperation.dao.odo.wave.WhWaveDao;
import com.baozun.scm.primservice.whoperation.dao.odo.wave.WhWaveLineDao;
import com.baozun.scm.primservice.whoperation.dao.odo.wave.WhWaveMasterDao;
import com.baozun.scm.primservice.whoperation.dao.sku.SkuDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.UomDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhDistributionPatternRuleDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ma.TransportProviderDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.odo.wave.proxy.DistributionModeArithmeticManagerProxy;
import com.baozun.scm.primservice.whoperation.model.localauth.OperUser;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoAddress;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoInvoice;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoInvoiceLine;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoLine;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoLineSn;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoTransportMgmt;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoVas;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWave;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWaveLine;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWaveMaster;
import com.baozun.scm.primservice.whoperation.model.sku.Sku;
import com.baozun.scm.primservice.whoperation.model.system.SysDictionary;
import com.baozun.scm.primservice.whoperation.model.warehouse.Customer;
import com.baozun.scm.primservice.whoperation.model.warehouse.Store;
import com.baozun.scm.primservice.whoperation.model.warehouse.ma.TransportProvider;

@Service("odoManager")
@Transactional
public class OdoManagerImpl extends BaseManagerImpl implements OdoManager {
    protected static final Logger log = LoggerFactory.getLogger(OdoManager.class);
    @Autowired
    private WhOdoDao whOdoDao;
    @Autowired
    private WhOdoLineDao whOdoLineDao;
    @Autowired
    private WhOdoLineSnDao whOdoLineAttrSnDao;
    @Autowired
    private WhOdoTransportMgmtDao whOdoTransportMgmtDao;
    @Autowired
    private TransportProviderDao transportProviderDao;
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
    private WhOdoInvoiceDao whOdoInvoiceDao;
    @Autowired
    private WhOdoInvoiceLineDao whOdoInvoiceLineDao;
    @Autowired
    private WhWaveLineDao whWaveLineDao;
    @Autowired
    private OperUserDao operUserDao;
    @Autowired
    private WhDistributionPatternRuleDao whDistributionPatternRuleDao;
    @Autowired
    private DistributionModeArithmeticManagerProxy distributionModeArithmeticManagerProxy;

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
                Set<String> userIdSet = new HashSet<String>();
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
                        if (StringUtils.hasText(command.getOutboundTargetType())) {

                            dic9.add(command.getOutboundTargetType());
                        }
                        if (StringUtils.hasText(command.getDeliverGoodsTimeMode())) {

                            dic10.add(command.getDeliverGoodsTimeMode());
                        }
                        if (StringUtils.hasText(command.getIncludeFragileCargo())) {

                            dic11.add(command.getIncludeFragileCargo());
                        }
                        if (StringUtils.hasText(command.getIncludeHazardousCargo())) {

                            dic11.add(command.getIncludeHazardousCargo());
                        }
                        if (StringUtils.hasText(command.getCustomerId())) {
                            customerIdSet.add(Long.parseLong(command.getCustomerId()));
                        }
                        if (StringUtils.hasText(command.getStoreId())) {
                            storeIdSet.add(Long.parseLong(command.getStoreId()));
                        }
                        if (StringUtils.hasText(command.getCreateId())) {
                            userIdSet.add(command.getCreateId());
                        }
                        if (StringUtils.hasText(command.getModifiedId())) {
                            userIdSet.add(command.getModifiedId());
                        }
                        if (StringUtils.hasText(command.getOrderType())) {
                            dic12.add(command.getOrderType());
                        }
                    }
                    // 查找用户
                    Map<String, String> userMap = new HashMap<String, String>();
                    if (userIdSet.size() > 0) {
                        Iterator<String> userIt = userIdSet.iterator();
                        while (userIt.hasNext()) {
                            String id = userIt.next();
                            OperUser user = this.operUserDao.findById(Long.parseLong(id));
                            userMap.put(id, user == null ? id : user.getUserName());
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
                    map.put(Constants.IS_NOT, new ArrayList<String>(dic11));
                    map.put(Constants.ODO_ORDER_TYPE, new ArrayList<String>(dic12));

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
                        if (StringUtils.hasText(command.getOutboundTargetType())) {
                            SysDictionary sys = dicMap.get(Constants.ODO_AIM_TYPE + "_" + command.getOutboundTargetType());
                            command.setOutboundTargetTypeName(sys == null ? command.getOutboundTargetType() : sys.getDicLabel());
                        }
                        if (StringUtils.hasText(command.getDeliverGoodsTimeMode())) {
                            SysDictionary sys = dicMap.get(Constants.ODO_DELIVER_GOODS_TIME_MODE + "_" + command.getDeliverGoodsTimeMode());
                            command.setDeliverGoodsTimeModeName(sys == null ? command.getDeliverGoodsTimeMode() : sys.getDicLabel());
                        }
                        if (StringUtils.hasText(command.getIncludeFragileCargo())) {
                            SysDictionary sys = dicMap.get(Constants.IS_NOT + "_" + command.getIncludeFragileCargo());
                            command.setIncludeFragileCargoName(sys == null ? command.getIncludeFragileCargo() : sys.getDicLabel());
                        }
                        if (StringUtils.hasText(command.getIncludeHazardousCargo())) {
                            SysDictionary sys = dicMap.get(Constants.IS_NOT + "_" + command.getIncludeHazardousCargo());
                            command.setIncludeHazardousCargoName(sys == null ? command.getIncludeHazardousCargo() : sys.getDicLabel());
                        }
                        if (StringUtils.hasText(command.getCustomerId())) {
                            Customer customer = customerMap.get(Long.parseLong(command.getCustomerId()));
                            command.setCustomerName(customer == null ? command.getCustomerId() : customer.getCustomerName());
                        }
                        if (StringUtils.hasText(command.getIsLocked())) {
                            SysDictionary sys = dicMap.get(Constants.IS_NOT + "_" + command.getIsLocked());
                            command.setIsLocked(sys == null ? command.getIsLocked() : sys.getDicLabel());
                        }
                        if (StringUtils.hasText(command.getOrderType())) {
                            SysDictionary sys = dicMap.get(Constants.ODO_ORDER_TYPE + "_" + command.getOrderType());
                            command.setOrderTypeName(sys == null ? command.getOrderType() : sys.getDicLabel());
                        }
                        if (StringUtils.hasText(command.getStoreId())) {
                            Store store = storeMap.get(Long.parseLong(command.getStoreId()));
                            command.setStoreName(store == null ? command.getStoreId() : store.getStoreName());
                        }
                        if (StringUtils.hasText(command.getCreateId())) {

                            command.setCreatedName(userMap.get(command.getCreateId()));
                        }
                        if (StringUtils.hasText(command.getModifiedId())) {

                            command.setModifiedName(userMap.get(command.getModifiedId()));
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
    public Long createOdo(OdoCommand odoCommand, List<OdoLineCommand> odoLineList, OdoTransportMgmtCommand transCommand, WhOdoAddress odoAddress, WhOdoInvoice invoice, List<WhOdoInvoiceLine> invoiceLineList, Long ouId, Long userId) {
        Long odoId = null;
        try {
            WhOdo odo = new WhOdo();
            BeanUtils.copyProperties(odoCommand, odo);
            this.whOdoDao.insert(odo);
            odoId = odo.getId();
            // 头增值服务
            if (odoCommand.getVasList() != null && odoCommand.getVasList().size() > 0) {
                for (WhOdoVasCommand vasCommand : odoCommand.getVasList()) {
                    WhOdoVas vas = new WhOdoVas();
                    BeanUtils.copyProperties(vasCommand, vas);
                    vas.setOdoId(odoId);
                    vas.setOuId(ouId);
                    this.whOdoVasDao.insert(vas);
                }
            }
            if (odoLineList != null && odoLineList.size() > 0) {
                for (OdoLineCommand lineCommand : odoLineList) {
                    WhOdoLine odoLine = new WhOdoLine();
                    BeanUtils.copyProperties(lineCommand, odoLine);
                    odoLine.setOdoId(odoId);
                    odoLine.setOuId(ouId);
                    this.whOdoLineDao.insert(odoLine);
                    Long odoLineId = odoLine.getId();
                    if (lineCommand.getVasList() != null && lineCommand.getVasList().size() > 0) {
                        for (WhOdoVasCommand vasCommand : odoCommand.getVasList()) {
                            WhOdoVas vas = new WhOdoVas();
                            BeanUtils.copyProperties(vasCommand, vas);
                            vas.setOdoId(odoId);
                            vas.setOdoLineId(odoLineId);
                            vas.setOuId(ouId);
                            this.whOdoVasDao.insert(vas);
                        }
                    }
                    if (lineCommand.getLineSnList() != null && lineCommand.getLineSnList().size() > 0) {
                        for (WhOdoLineSnCommand snCommand : lineCommand.getLineSnList()) {
                            WhOdoLineSn sn = new WhOdoLineSn();
                            BeanUtils.copyProperties(snCommand, sn);
                            sn.setOuId(ouId);
                            sn.setOdoLineId(odoId);
                            this.whOdoLineAttrSnDao.insert(sn);
                        }
                    }
                }
            }
            if (odoAddress != null) {
                this.whOdoAddressDao.insert(odoAddress);
            }

            WhOdoTransportMgmt trans = new WhOdoTransportMgmt();
            BeanUtils.copyProperties(transCommand, trans);
            trans.setOdoId(odo.getId());
            this.whOdoTransportMgmtDao.insert(trans);

            if (invoice != null) {
                invoice.setOdoId(odoId);
                invoice.setOuId(ouId);
                this.whOdoInvoiceDao.insert(invoice);
                if (invoiceLineList != null && invoiceLineList.size() > 0) {
                    for (WhOdoInvoiceLine invoiceLine : invoiceLineList) {
                        invoiceLine.setOdoInvoiceId(invoice.getId());
                        invoiceLine.setOuId(ouId);
                        this.whOdoInvoiceLineDao.insert(invoiceLine);
                    }
                }
            }
            // 汇总信息
            if (OdoStatus.ODO_NEW.equals(odo.getOdoStatus())) {
                this.getSummaryByOdolineList(odo);
                int updateCount = this.whOdoDao.saveOrUpdateByVersion(odo);
                if (updateCount <= 0) {
                    throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                }
            }
        } catch (Exception e) {
            log.error(e + "");
            throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
        }
        return odoId;
    }

    private void getSummaryByOdolineList(WhOdo odo) {
        List<WhOdoLine> lineList = this.whOdoLineDao.findOdoLineListByOdoIdOuId(odo.getId(), odo.getOuId());
        // 出库单统计数目
        double qty = Constants.DEFAULT_DOUBLE;
        int skuNumberOfPackages = Constants.DEFAULT_INTEGER;
        double amt = Constants.DEFAULT_DOUBLE;
        boolean isHazardous = odo.getIncludeHazardousCargo();
        boolean isFragile = odo.getIncludeFragileCargo();
        Set<Long> skuIdSet = new HashSet<Long>();
        boolean isAllowMerge = false;
        if (odo.getIsLocked() == null || odo.getIsLocked() == false) {
            isAllowMerge = true;
        } else {
            List<WhOdoVasCommand> ouVasList = this.whOdoVasDao.findOdoOuVasCommandByOdoIdOdoLineIdType(odo.getId(), null, odo.getOuId());
            if (ouVasList != null && ouVasList.size() > 0) {
                isAllowMerge = false;
            }
        }
        if (lineList != null && lineList.size() > 0) {
            for (WhOdoLine line : lineList) {
                if (OdoStatus.ODOLINE_CANCEL.equals(line.getOdoLineStatus())) {
                    continue;
                }
                skuIdSet.add(line.getSkuId());
                amt += line.getLineAmt();
                qty += line.getQty();

                if (isAllowMerge) {
                    List<WhOdoVasCommand> ouVasList = this.whOdoVasDao.findOdoOuVasCommandByOdoIdOdoLineIdType(odo.getId(), line.getId(), odo.getOuId());
                    if (ouVasList != null && ouVasList.size() > 0) {
                        isAllowMerge = false;
                    }
                }
            }

        }
        odo.setQty(qty);
        odo.setAmt(amt);
        skuNumberOfPackages = skuIdSet.size();
        odo.setSkuNumberOfPackages(skuNumberOfPackages);
        odo.setIncludeFragileCargo(isFragile);
        odo.setIncludeHazardousCargo(isHazardous);

        // 设置允许合并与否
        odo.setIsAllowMerge(false);
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
    public void saveUnit(WhOdoLine line, List<WhOdoVas> insertVasList) {
        // @mender yimin.lu 2017/3/22 添加编辑逻辑和增值服务编辑逻辑
        try {
            if (line.getId() == null) {
                this.whOdoLineDao.insert(line);

            } else {
                int updateCount = this.whOdoLineDao.saveOrUpdateByVersion(line);
                if (updateCount <= 0) {
                    throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                }
            }
            WhOdo odo = this.whOdoDao.findByIdOuId(line.getOdoId(), line.getOuId());
            if (odo == null) {
                throw new BusinessException(ErrorCodes.PARAM_IS_NULL);
            }
            if (OdoStatus.ODO_TOBECREATED.equals(odo.getOdoStatus())) {

            } else if (OdoStatus.ODO_NEW.equals(odo.getOdoStatus())) {
                odo.setOdoStatus(OdoStatus.ODO_TOBECREATED);
                int updateCount = this.whOdoDao.saveOrUpdateByVersion(odo);
                if (updateCount <= 0) {
                    throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                }
            } else {
                throw new BusinessException(ErrorCodes.ODO_EDIT_ERROR);
            }
            // 增值服务逻辑：先全部删除再重新添加
            List<WhOdoVas> delVasList = this.whOdoVasDao.findOdoVasByOdoIdOdoLineIdType(line.getOdoId(), line.getId(), null, line.getOuId());
            if (delVasList != null && delVasList.size() > 0) {
                for (WhOdoVas vas : insertVasList) {
                    this.whOdoVasDao.deleteByIdOuId(vas.getId(), vas.getOuId());
                }
            }
            if (insertVasList != null && insertVasList.size() > 0) {
                for (WhOdoVas vas : insertVasList) {
                    vas.setOdoId(line.getOdoId());
                    vas.setOdoLineId(line.getId());
                    this.whOdoVasDao.insert(vas);
                }
            }
        } catch (Exception ex) {
            log.error("" + ex);
            throw new BusinessException(ErrorCodes.INSERT_DATA_ERROR);
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
    public void cancelOdo(WhOdo odo, Long ouId, String logId) {
        try {
            if (odo == null) {
                throw new BusinessException(ErrorCodes.PARAMS_ERROR);
            }
            // 明细取消
            List<WhOdoLine> lineList = this.whOdoLineDao.findOdoLineListByOdoIdOuId(odo.getId(), ouId);
            if (lineList != null && lineList.size() > 0) {
                for (WhOdoLine line : lineList) {
                    line.setOdoLineStatus(OdoStatus.ODOLINE_CANCEL);
                    int updateLineCount = this.whOdoLineDao.saveOrUpdateByVersion(line);
                    if (updateLineCount <= 0) {
                        throw new BusinessException(ErrorCodes.DELETE_DATA_ERROR);
                    }
                }
            }

            odo.setOdoStatus(OdoStatus.ODO_CANCEL);
            int updateOdoCount = this.whOdoDao.saveOrUpdateByVersion(odo);
            if (updateOdoCount <= 0) {
                throw new BusinessException(ErrorCodes.DELETE_DATA_ERROR);
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
    public Pagination<OdoWaveGroupResultCommand> findOdoListForWaveByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params) {
        Pagination<OdoWaveGroupResultCommand> pages = this.whOdoDao.findOdoListForWaveByQueryMapWithPageExt(page, sorts, params);
        try {
            Long ouId = (Long) params.get("ouId");

            if (pages != null) {
                Set<String> dic1 = new HashSet<String>();// 出库单状态
                Set<String> dic2 = new HashSet<String>();// 出库单类型
                Set<String> dic3 = new HashSet<String>();// 配货模式
                Set<String> dic4 = new HashSet<String>();// 上位单据类型
                Set<String> transCodeSet = new HashSet<String>();// 运输服务商
                Set<Long> customerIdSet = new HashSet<Long>();
                Set<Long> storeIdSet = new HashSet<Long>();
                Map<String, String> distributionModeMap = new HashMap<String, String>();
                List<OdoWaveGroupResultCommand> list = pages.getItems();
                if (list != null && list.size() > 0) {
                    for (OdoWaveGroupResultCommand command : list) {
                        if (StringUtils.hasText(command.getOdoStatus())) {
                            dic1.add(command.getOdoStatus());
                        }
                        if (StringUtils.hasText(command.getOdoType())) {
                            dic2.add(command.getOdoType());
                        }
                        if (StringUtils.hasText(command.getDistributeMode())) {
                            dic3.add(command.getDistributeMode());
                        }
                        if (StringUtils.hasText(command.getEpistaticSystemsOrderType())) {
                            dic4.add(command.getEpistaticSystemsOrderType());
                        }
                        if (StringUtils.hasText(command.getTransportServiceProvider())) {
                            transCodeSet.add(command.getTransportServiceProvider());
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
                    if (dic2.size() > 0) {
                        map.put(Constants.ODO_TYPE, new ArrayList<String>(dic2));
                    }
                    // 配货模式
                    if (dic3.size() > 0) {
                        for (String ruleCode : dic3) {

                            WhDistributionPatternRuleCommand rule = this.whDistributionPatternRuleDao.findRuleByCode(ruleCode, ouId);
                            if (rule != null) {
                                distributionModeMap.put(ruleCode, rule.getDistributionPatternName());
                            }
                        }

                    }
                    if (dic4.size() > 0) {
                        map.put(Constants.ODO_PRE_TYPE, new ArrayList<String>(dic4));
                    }
                    Map<String, TransportProvider> transMap = new HashMap<String, TransportProvider>();
                    if (transCodeSet.size() > 0) {
                        for (String transCode : transCodeSet) {
                            TransportProvider tp = this.transportProviderDao.findByCode(transCode);
                            transMap.put(transCode, tp);
                        }

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
                        if (StringUtils.hasText(command.getOdoType())) {
                            SysDictionary sys = dicMap.get(Constants.ODO_TYPE + "_" + command.getOdoType());
                            command.setOdoTypeName(sys.getDicLabel());
                            groupName += "$" + command.getOdoTypeName();
                        }
                        if (StringUtils.hasText(command.getDistributeMode())) {
                            String name = distributionModeMap.get(command.getDistributeMode());
                            if (StringUtils.hasText(name)) {
                                command.setDistributeModeName(name);
                            } else {
                                command.setDistributeModeName(command.getDistributeMode());
                            }
                            groupName += "$" + command.getDistributeModeName();
                        }
                        if (StringUtils.hasText(command.getTransportServiceProvider())) {
                            if (transMap != null) {
                                if (transMap.containsKey(command.getTransportServiceProvider())) {
                                    TransportProvider tp = transMap.get(command.getTransportServiceProvider());
                                    command.setTransportServiceProviderName(tp.getName());
                                    groupName += "$" + command.getTransportServiceProviderName();
                                }
                            }
                        }
                        if (StringUtils.hasText(command.getEpistaticSystemsOrderType())) {
                            SysDictionary sys = dicMap.get(Constants.ODO_PRE_TYPE + "_" + command.getEpistaticSystemsOrderType());
                            command.setEpistaticSystemsOrderTypeName(sys.getDicLabel());
                            groupName += "$" + command.getEpistaticSystemsOrderTypeName();
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

        List<OdoResultCommand> list = this.whOdoDao.findCommandListForWave(command);
        // 出库单状态
        Set<String> dic1 = new HashSet<String>();// ODO_STATUS
        // 订单平台类型
        Set<String> dic2 = new HashSet<String>();// ODO_ORDER_TYPE
        // 上位单据类型
        Set<String> dic3 = new HashSet<String>();// ODO_PRE_TYPE
        // 出库单类型
        Set<String> dic4 = new HashSet<String>();// ODO_TYPE
        // 业务模式（配货模式）
        Set<String> dic5 = new HashSet<String>();// DISTRIBUTE_MODE
        // 整单出库标志
        Set<String> dic6 = new HashSet<String>();// IS_NOT
        // 越库标志
        Set<String> dic7 = new HashSet<String>();// ODO_CROSS_DOCKING_SYSMBOL
        // 运行标志
        // Set<String> dic8 = new HashSet<String>();//
        // 客户
        Set<Long> customerIdSet = new HashSet<Long>();
        // 店铺
        Set<Long> storeIdSet = new HashSet<Long>();
        // 用户
        Set<String> userIdSet = new HashSet<String>();
        if (list != null && list.size() > 0) {
            for (OdoResultCommand res : list) {
                if (StringUtils.hasText(res.getOdoStatus())) {
                    dic1.add(res.getOdoStatus());
                }
                if (StringUtils.hasText(res.getOrderType())) {
                    dic2.add(res.getOrderType());
                }
                if (StringUtils.hasText(res.getEpistaticSystemsOrderType())) {
                    dic3.add(res.getEpistaticSystemsOrderType());
                }
                if (StringUtils.hasText(res.getOdoType())) {
                    dic4.add(res.getOdoType());
                }
                if (StringUtils.hasText(res.getDistributeMode())) {
                    dic5.add(res.getDistributeMode());
                }
                if (StringUtils.hasText(res.getIsWholeOrderOutbound())) {
                    dic6.add(res.getIsWholeOrderOutbound());
                }
                if (StringUtils.hasText(res.getCrossDockingSysmbol())) {
                    dic7.add(res.getCrossDockingSysmbol());
                }
                customerIdSet.add(Long.parseLong(res.getCustomerId()));
                storeIdSet.add(Long.parseLong(res.getStoreId()));

                if (StringUtils.hasText(res.getCreateId())) {

                    userIdSet.add(res.getCreateId());
                }
                if (StringUtils.hasText(res.getModifiedId())) {
                    userIdSet.add(res.getModifiedId());
                }
            }
            // 用户
            // 查找用户
            Map<String, String> userMap = new HashMap<String, String>();
            if (userIdSet.size() > 0) {
                Iterator<String> userIt = userIdSet.iterator();
                while (userIt.hasNext()) {
                    String id = userIt.next();
                    OperUser user = this.operUserDao.findById(Long.parseLong(id));
                    userMap.put(id, user == null ? id : user.getUserName());
                }

            }
            Map<String, List<String>> map = new HashMap<String, List<String>>();
            map.put(Constants.ODO_STATUS, new ArrayList<String>(dic1));
            map.put(Constants.ODO_ORDER_TYPE, new ArrayList<String>(dic2));
            map.put(Constants.ODO_PRE_TYPE, new ArrayList<String>(dic3));
            map.put(Constants.ODO_TYPE, new ArrayList<String>(dic4));
            map.put(Constants.DISTRIBUTE_MODE, new ArrayList<String>(dic5));
            map.put(Constants.IS_WHOLE_ORDER_OUTBOUND, new ArrayList<String>(dic6));
            map.put(Constants.ODO_CROSS_DOCKING_SYSMBOL, new ArrayList<String>(dic7));
            Map<String, SysDictionary> dicMap = this.findSysDictionaryByRedis(map);
            Map<Long, Customer> customerMap = this.findCustomerByRedis(new ArrayList<Long>(customerIdSet));
            Map<Long, Store> storeMap = this.findStoreByRedis(new ArrayList<Long>(storeIdSet));
            for (OdoResultCommand result : list) {
                if (StringUtils.hasText(result.getOdoStatus())) {
                    SysDictionary sys = dicMap.get(Constants.ODO_STATUS + "_" + result.getOdoStatus());
                    result.setOdoStatusName(sys == null ? result.getOdoStatus() : sys.getDicLabel());
                }
                if (StringUtils.hasText(result.getOrderType())) {
                    SysDictionary sys = dicMap.get(Constants.ODO_ORDER_TYPE + "_" + result.getOrderType());
                    result.setOrderTypeName(sys == null ? result.getOrderType() : sys.getDicLabel());
                }
                if (StringUtils.hasText(result.getEpistaticSystemsOrderType())) {
                    SysDictionary sys = dicMap.get(Constants.ODO_PRE_TYPE + "_" + result.getEpistaticSystemsOrderType());
                    result.setEpistaticSystemsOrderTypeName(sys == null ? result.getEpistaticSystemsOrderType() : sys.getDicLabel());
                }
                if (StringUtils.hasText(result.getOdoType())) {
                    SysDictionary sys = dicMap.get(Constants.ODO_TYPE + "_" + result.getOdoType());
                    result.setOdoTypeName(sys == null ? result.getOdoType() : sys.getDicLabel());
                }
                if (StringUtils.hasText(result.getDistributeMode())) {
                    SysDictionary sys = dicMap.get(Constants.DISTRIBUTE_MODE + "_" + result.getDistributeMode());
                    result.setDistributeModeName(sys == null ? result.getDistributeMode() : sys.getDicLabel());
                }
                if (StringUtils.hasText(result.getIsWholeOrderOutbound())) {
                    SysDictionary sys = dicMap.get(Constants.IS_WHOLE_ORDER_OUTBOUND + "_" + result.getIsWholeOrderOutbound());
                    result.setIsWholeOrderOutboundName(sys == null ? result.getIsWholeOrderOutbound() : sys.getDicLabel());
                }
                if (StringUtils.hasText(result.getCrossDockingSysmbol())) {
                    SysDictionary sys = dicMap.get(Constants.ODO_CROSS_DOCKING_SYSMBOL + "_" + result.getCrossDockingSysmbol());
                    result.setCrossDockingSysmbolName(sys == null ? result.getCrossDockingSysmbol() : sys.getDicLabel());
                }
                Customer customer = customerMap.get(Long.parseLong(result.getCustomerId()));
                result.setCustomerName(customer == null ? result.getCustomerId() : customer.getCustomerName());
                Store store = storeMap.get(Long.parseLong(result.getStoreId()));
                result.setStoreName(store == null ? result.getStoreId() : store.getStoreName());

                if (StringUtils.hasText(result.getCreateId())) {
                    result.setCreatedName(userMap.get(result.getCreateId()));
                }
                if (StringUtils.hasText(result.getCreatedName())) {
                    result.setModifiedName(userMap.get(result.getModifiedId()));
                }
            }
        }

        return list;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<Long> findOdoIdListForWave(OdoSearchCommand command) {
        return this.whOdoDao.findOdoIdListForWave(command);
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
    public void createOdoWave(WhWave wave, Long waveTemplateId, List<WhWaveLine> waveLineList, Map<Long, WhOdo> odoMap, List<WhOdoLine> odolineList, Long userId, String logId) {
        try {
            wave.setPhaseCode(this.getWavePhaseCode(null, waveTemplateId, wave.getOuId()));
            this.whWaveDao.insert(wave);
        } catch (Exception e) {
            log.error(e + "");;
            throw new BusinessException(ErrorCodes.INSERT_DATA_ERROR);
        }
        try {
            for (WhWaveLine waveLine : waveLineList) {
                waveLine.setWaveId(wave.getId());
                this.whWaveLineDao.insert(waveLine);
            }
        } catch (Exception e) {
            log.error(e + "");;
            throw new BusinessException(ErrorCodes.INSERT_DATA_ERROR);
        }
        Iterator<Entry<Long, WhOdo>> odoIt = odoMap.entrySet().iterator();
        while (odoIt.hasNext()) {
            Entry<Long, WhOdo> entry = odoIt.next();
            WhOdo odo = entry.getValue();
            odo.setModifiedId(userId);
            odo.setWaveCode(wave.getCode());
            odo.setOdoStatus(OdoStatus.ODO_WAVE);
            int count = this.whOdoDao.saveOrUpdateByVersion(odo);
            if (count <= 0) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
        }
        for (WhOdoLine line : odolineList) {
            line.setModifiedId(userId);
            line.setWaveCode(wave.getCode());
            line.setOdoLineStatus(OdoStatus.ODOLINE_WAVE);
            int count = this.whOdoLineDao.saveOrUpdateByVersion(line);
            if (count <= 0) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
        }

        // 添加到波次中时候，计数器需要-1；
        Iterator<Entry<Long, WhOdo>> odoIt2 = odoMap.entrySet().iterator();
        while (odoIt2.hasNext()) {
            Entry<Long, WhOdo> entry = odoIt2.next();
            WhOdo odo = entry.getValue();
            this.distributionModeArithmeticManagerProxy.AddToWave(odo.getCounterCode(), odo.getId());
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
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    private void removeOdoLine(List<Long> odoLineIds, Long ouId) {
        if (null != odoLineIds && !odoLineIds.isEmpty()) {
            for (Long odoLineId : odoLineIds) {
                WhOdoLine whOdoLine = this.whOdoLineDao.findOdoLineById(odoLineId, ouId);
                whOdoLine.setWaveCode(null);
                whOdoLine.setOdoLineStatus(OdoStatus.ODOLINE_NEW);
                whOdoLine.setAssignFailReason(Constants.SOFT_ALLOCATION_FAIL);
                whOdoLine.setIsAssignSuccess(false);
                int cnt = this.whOdoLineDao.saveOrUpdateByVersion(whOdoLine);
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
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    private void removeOdo(Long odoId, Long ouId) {
        WhOdoLine whOdoLine = new WhOdoLine();
        whOdoLine.setOdoId(odoId);
        whOdoLine.setOuId(ouId);
        whOdoLine.setOdoLineStatus(OdoStatus.ODOLINE_NEW);
        WhOdo whOdo = this.whOdoDao.findByIdOuId(odoId, ouId);
        Long cnt = this.whOdoLineDao.findListCountNotNew(whOdoLine);
        if (0 == cnt) {
            whOdo.setOdoStatus(OdoStatus.ODO_NEW);
        }
        whOdo.setAssignFailReason(Constants.SOFT_ALLOCATION_FAIL);
        whOdo.setIsAssignSuccess(false);
        whOdo.setWaveCode(null);
        int count = this.whOdoDao.saveOrUpdateByVersion(whOdo);
        if (count <= 0) {
            throw new BusinessException("剔除逻辑-更新出库单头-失败");
        }

    }

    @Override
    public List<OdoCommand> getNoRuleOdoIdList(List<Long> waveIdList, Long ouId) {
        List<OdoCommand> datas = whOdoDao.getNoRuleOdoIdList(waveIdList, ouId);
        return datas;
    }

    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhOdo> findOdoListByWaveCode(String code, Long ouId) {
        WhOdo odo = new WhOdo();
        odo.setWaveCode(code);
        odo.setOuId(ouId);
        return this.whOdoDao.findListByParamExt(odo);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void finishCreateOdo(WhOdo odo, List<WhOdoLine> lineList) {
        int updatecount = this.whOdoDao.saveOrUpdateByVersion(odo);
        if (updatecount <= 0) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
        if (lineList != null && lineList.size() > 0) {
            for (WhOdoLine line : lineList) {
                int count = this.whOdoLineDao.saveOrUpdateByVersion(line);
                if (count < 1) {
                    throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                }
            }
        }

    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void updateByVersion(WhOdo odo) {
        try {

            int updateCount = this.whOdoDao.saveOrUpdateByVersion(odo);
            if (updateCount == 0) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception e) {
            throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
        }
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<String> findExportExeclList(OdoSearchCommand odoSearchCommand) {
        return this.whOdoDao.findExportExeclList(odoSearchCommand);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<String> findDistinctCounterCode(Long ouId) {
        return this.whOdoDao.findDistinctCounterCode(ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<Long> findOdoByCounterCode(String counterCode, Long ouId) {
        return this.whOdoDao.findOdoByCounterCode(counterCode, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<Long> findOdoByCounterCodeToCalcDistributeMode(String counterCode, Long ouId) {
        return this.whOdoDao.findOdoByCounterCodeToCalcDistributeMode(counterCode, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void createOdo(List<OdoGroupCommand> groupList, Long ouId, Long userId) {
        for (OdoGroupCommand group : groupList) {
            this.createOdo(group.getOdo(), group.getOdoLineList(), group.getTransPortMgmt(), group.getWhOdoAddress(), group.getOdoInvoice(), group.getOdoInvoiceLineList(), ouId, userId);
        }

    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WaveCommand findWaveSumDatabyOdoIdList(List<Long> odoIdList, Long ouId) {
        //
        int batchCount = 500;
        int totalCount = odoIdList.size();
        int ceil = (int) Math.ceil((double) totalCount / batchCount);
        //
        // 商品种类数
        int skuCategoryQty = Constants.DEFAULT_INTEGER;
        // 总体积
        double totalVolume = Constants.DEFAULT_DOUBLE;
        // 总重量
        double totalWeight = Constants.DEFAULT_DOUBLE;
        // 波次明细数
        int totalOdoLineQty = Constants.DEFAULT_INTEGER;
        // 总金额
        double totalAmount = Constants.DEFAULT_DOUBLE;
        // 商品总件数
        double totalSkuQty = Constants.DEFAULT_DOUBLE;
        for (int i = 0; i < ceil; i++) {
            List<Long> subList = null;
            if (ceil == 1) {
                subList = odoIdList;
            } else {
                int fromIndex = batchCount * i;
                int toIndex = batchCount * (i + 1);
                if (totalCount < toIndex) {
                    toIndex = totalCount;
                }
                if (fromIndex == 0) {
                    subList = odoIdList.subList(0, toIndex);
                } else {
                    subList = odoIdList.subList(fromIndex + 1, toIndex);
                }
            }
            WaveCommand waveCommand = this.whOdoDao.findWaveSumDatabyOdoIdList(subList, ouId);

            skuCategoryQty += waveCommand.getSkuCategoryQty();
            totalVolume += waveCommand.getTotalVolume();
            totalWeight += waveCommand.getTotalWeight();
            totalOdoLineQty += waveCommand.getTotalOdoLineQty();
            totalAmount += waveCommand.getTotalAmount();
            totalSkuQty += waveCommand.getTotalSkuQty();

        }
        WaveCommand waveCommand = new WaveCommand();
        waveCommand.setSkuCategoryQty(skuCategoryQty);
        waveCommand.setTotalVolume(totalVolume);
        waveCommand.setTotalWeight(totalWeight);
        waveCommand.setTotalOdoLineQty(totalOdoLineQty);
        waveCommand.setTotalAmount(totalAmount);
        waveCommand.setTotalSkuQty(totalSkuQty);
        return waveCommand;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void createOdoWaveNew(WhWave wave, Long waveTemplateId, List<Long> odoIdList) {
        int batchCount = 500;
        int totalCount = odoIdList.size();
        int ceil = (int) Math.ceil((double) totalCount / batchCount);
        Long ouId = wave.getOuId();
        for (int i = 0; i < ceil; i++) {
            List<Long> subList = null;
            if (ceil == 1) {
                subList = odoIdList;
            } else {
                int fromIndex = batchCount * i;
                int toIndex = batchCount * (i + 1);
                if (totalCount < toIndex) {
                    toIndex = totalCount;
                }
                if (fromIndex == 0) {
                    subList = odoIdList.subList(0, toIndex);
                } else {
                    subList = odoIdList.subList(fromIndex, toIndex);
                }
            }
            int updateOdoCount = this.whOdoDao.addOdoToWave(subList, wave.getOuId(), wave.getCreatedId(), wave.getCode(), OdoStatus.ODO_WAVE);

        }

        List<String> odoIdCounterCodeList = this.whOdoDao.findOdoIdCounterCodebyWaveCode(wave.getCode(), wave.getOuId());
        Map<Long, String> odoIdCounterCodeMap = new HashMap<Long, String>();
        List<Long> waveOdoIdList = new ArrayList<Long>();
        if (odoIdCounterCodeList != null && odoIdCounterCodeList.size() > 0) {
            for (String str : odoIdCounterCodeList) {
                String[] arr = str.split("_");
                odoIdCounterCodeMap.put(Long.parseLong(arr[0]), arr[1]);
                waveOdoIdList.add(Long.parseLong(arr[0]));
            }
            // 出库单头计算
            WaveCommand waveCommand = this.findWaveSumDatabyOdoIdList(waveOdoIdList, ouId);
            wave.setSkuCategoryQty(waveCommand.getSkuCategoryQty());
            wave.setTotalVolume(waveCommand.getTotalVolume());
            wave.setTotalWeight(waveCommand.getTotalWeight());
            wave.setTotalOdoLineQty(waveCommand.getTotalOdoLineQty());
            wave.setTotalAmount(waveCommand.getTotalAmount());
            wave.setTotalSkuQty(waveCommand.getTotalSkuQty());
        }
        wave.setPhaseCode(this.getWavePhaseCode(null, waveTemplateId, wave.getOuId()));
        // 插入波次
        this.whWaveDao.insert(wave);
        // 仓库中配货模式计算
        // this.distributionModeArithmeticManagerProxy.AddToWave(odoIdCounterCodeMap);

    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<Long> findOdoToBeAddedToWave(String waveCode, Long ouId) {
        return this.whOdoDao.findOdoToBeAddedToWave(waveCode, ouId);
    }


    /**
     *根据ID获取出库单列表
     *
     * @author mingwei.xie
     * @param odoIdList
     * @param ouId
     * @return
     */
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<OdoCommand> getWhOdoListById(List<Long> odoIdList, Long ouId) {
        return whOdoDao.getWhOdoListById(odoIdList, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhOdo> findByExtCodeOuIdNotCancel(String extOdoCode, Long ouId) {
        return this.whOdoDao.findByExtCodeOuIdNotCancel(extOdoCode, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhOdo findByExtCodeStoreIdOuId(String extCode, Long storeId, Long ouId) {
        WhOdo odo = new WhOdo();
        odo.setExtCode(extCode);
        odo.setStoreId(storeId);
        odo.setOuId(ouId);
        List<WhOdo> odoList = this.whOdoDao.findListByParamExt(odo);
        if (odoList == null || odoList.size() == 0) {
            return null;
        }
        if (odoList.size() > 1) {
            throw new BusinessException(ErrorCodes.ODO_EXTCODE_ISEXIST);
        }
        return odoList.get(0);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void editOdo(WhOdo odo, WhOdoTransportMgmt trans) {
       try{
           int updateCount=this.whOdoDao.saveOrUpdateByVersion(odo);
           if(updateCount<=0){
               throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
           }
           this.whOdoTransportMgmtDao.saveOrUpdate(trans);
       }catch(BusinessException e){
           throw e;
       }catch(Exception ex){
           log.error(ex+"");
            throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
       }
        
    }


}
