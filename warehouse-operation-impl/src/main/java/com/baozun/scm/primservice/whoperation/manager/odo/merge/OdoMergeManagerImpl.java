package com.baozun.scm.primservice.whoperation.manager.odo.merge;

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

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.baozun.scm.baseservice.sac.manager.CodeManager;
import com.baozun.scm.primservice.whoperation.command.odo.OdoCommand;
import com.baozun.scm.primservice.whoperation.command.odo.OdoLineCommand;
import com.baozun.scm.primservice.whoperation.command.odo.OdoLineInfoCommand;
import com.baozun.scm.primservice.whoperation.command.odo.OdoMergeCommand;
import com.baozun.scm.primservice.whoperation.command.odo.OdoResultCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.constant.OdoStatus;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoAddressDao;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoAttrDao;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoDao;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoLineAttrDao;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoLineDao;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoTransportMgmtDao;
import com.baozun.scm.primservice.whoperation.dao.odo.wave.WhWaveDao;
import com.baozun.scm.primservice.whoperation.dao.odo.wave.WhWaveLineDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoAddress;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoAttr;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoLine;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoLineAttr;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoTransportMgmt;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWave;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWaveLine;
import com.baozun.scm.primservice.whoperation.model.system.SysDictionary;
import com.baozun.scm.primservice.whoperation.model.warehouse.Customer;
import com.baozun.scm.primservice.whoperation.model.warehouse.Store;
import com.baozun.scm.primservice.whoperation.util.StringUtil;

@Service("odoMergeManager")
@Transactional
public class OdoMergeManagerImpl extends BaseManagerImpl implements OdoMergeManager {

    @Autowired
    private WhOdoDao whOdoDao;
    @Autowired
    private WhOdoAttrDao whOdoAttrDao;
    @Autowired
    private WhOdoLineDao whOdoLineDao;
    @Autowired
    private WhOdoLineAttrDao whOdoLineAttrDao;
    @Autowired
    private WhOdoAddressDao whOdoAddressDao;
    @Autowired
    private WhOdoTransportMgmtDao whOdoTransportMgmtDao;
    @Autowired
    private WhWaveLineDao whWaveLineDao;
    @Autowired
    private WhWaveDao whWaveDao;

    @Autowired
    private CodeManager codeManager;

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Pagination<OdoResultCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params) {
        String pageOption = (String) params.get("pageOption");
        if (StringUtils.hasText(pageOption)) {
            page.setSize(Integer.parseInt(pageOption));
        }
        String ids = this.findOdoMergableIds(params);
        if ("(null)".equalsIgnoreCase(ids)) {
            return null;
        }
        params.put("ids", ids);
        Pagination<OdoResultCommand> list = this.findOdoMergableInfo(page, sorts, params);
        Pagination<OdoResultCommand> pages = this.setDicLabel(list);

        return pages;
    }

    private String findOdoMergableIds(Map<String, Object> params) {
        Long ouId = (Long) params.get("ouId");
        String isNeedOutboundCartonType = ((boolean) params.get("isNeedOutboundCartonType")) ? "1" : "0";
        String isNeedEpistaticSystemsOrderType = ((boolean) params.get("isNeedEpistaticSystemsOrderType")) ? "1" : "0";
        String isNeedStore = ((boolean) params.get("isNeedStore")) ? "1" : "0";
        String isNeedDeliverGoodsTime = ((boolean) params.get("isNeedDeliverGoodsTime")) ? "1" : "0";
        String idString = this.whOdoDao.findOdoMergableIds(ouId, isNeedOutboundCartonType, isNeedEpistaticSystemsOrderType, isNeedStore, isNeedDeliverGoodsTime);
        String ids = "(" + idString + ")";
        return ids;
    }

    private List<String> convertOptions(List<String> optionsList) {
        List<String> result = new ArrayList<String>();
        if (null != optionsList) {
            result.add((optionsList.contains("needOutboundCartonType")) ? "1" : "0");
            result.add((optionsList.contains("needEpistaticSystemsOrderType")) ? "1" : "0");
            result.add((optionsList.contains("needStore")) ? "1" : "0");
            result.add((optionsList.contains("needDeliverGoodsTime")) ? "1" : "0");
        } else {
            result = Arrays.asList("0", "0", "0", "0");
        }
        return result;
    }

    private Pagination<OdoResultCommand> findOdoMergableInfo(Page page, Sort[] sorts, Map<String, Object> params) {
        Pagination<OdoResultCommand> pages = this.whOdoDao.findListByQueryMapWithPageExt(page, sorts, params);
        return pages;
    }

    private Pagination<OdoResultCommand> setDicLabel(Pagination<OdoResultCommand> pages) {
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
                        userIdSet.add(Long.parseLong(command.getCreateId()));
                    }
                    if (StringUtils.hasText(command.getModifiedId())) {
                        userIdSet.add(Long.parseLong(command.getModifiedId()));
                    }
                    if (StringUtils.hasText(command.getOrderType())) {
                        dic12.add(command.getOrderType());
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
                        command.setOutboundCartonTypeName(sys == null ? command.getOutboundTargetType() : sys.getDicLabel());
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
                }
                pages.setItems(list);
            }
        }
        return pages;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Map<String, String> odoMerge(List<String> odoIds, List<String> options, Long ouId, Long userId) {
        Map<String, String> response = new HashMap<String, String>();
        String odoIdString = StringUtil.listToString(odoIds, ',');
        List<String> optionList = convertOptions(options);
        List<OdoMergeCommand> list = this.whOdoDao.odoMerge(OdoStatus.ODO_NEW, odoIdString, ouId, optionList.get(0), optionList.get(1), optionList.get(2), optionList.get(3));
        if (!list.isEmpty() && list.size() > 0) {
            /* 合并订单 */
            response = this.startOdoMerge(list, ouId, userId);
        }
        return response;
    }

    /**
     * @param odoMergeCommandList
     * @param ouId
     * @param userId
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Map<String, String> startOdoMerge(List<OdoMergeCommand> odoMergeCommandList, Long ouId, Long userId) {
        // 合并失败订单
        String failMergeOdoIds = "";
        // 合并成功订单
        String successMergeOdoIds = "";

        Map<String, String> response = new HashMap<String, String>();
        for (OdoMergeCommand odoMergeCommand : odoMergeCommandList) {
            String odoIdString = odoMergeCommand.getOdoIds();
            Long result = this.odoMergeOperation(odoIdString, ouId, userId);
            if (-1 == result) {
                failMergeOdoIds += odoIdString + ",";
            } else {
                successMergeOdoIds += result + ",";
            }
        }
        if (StringUtils.hasText(failMergeOdoIds)) {
            failMergeOdoIds = failMergeOdoIds.substring(0, failMergeOdoIds.length() - 1);
        }
        if (StringUtils.hasText(successMergeOdoIds)) {
            successMergeOdoIds = successMergeOdoIds.substring(0, successMergeOdoIds.length() - 1);
        }
        response.put("fail", failMergeOdoIds);
        response.put("success", successMergeOdoIds);
        return response;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhOdo generalOdoMerge(String odoIdString, Long ouId, Long userId) {
        Long odoId = this.odoMergeOperation(odoIdString, ouId, userId);
        if (-1 == odoId) {
            return null;
        } else {
            WhOdo whOdo = this.whOdoDao.findByIdOuId(odoId, ouId);
            return whOdo;
        }
    }

    /**
     * 1,新建新合并订单主档
     * 2,更新原订单状态
     * 3,新建新合并订单属性表以及配送对象表和运输商管理表
     * 4,新建新合并订单明细行以及属性对象
     * 5,更新原订单明细行以及属性
     * 6,插入新建合并订单明细行及属性
     * @param odoIdString
     * @param ouId
     * @param userId
     * @return
     */
    private Long odoMergeOperation(String odoIdString, Long ouId, Long userId) {
        boolean hasMergeOdo = false;
        List<String> odoIds = Arrays.asList(odoIdString.split(","));
        Integer size = odoIds.size();
        if (1 == size) {
            // 只有一个可合并出库订单, 不进行合
            return -1L;
        }
        /* 判断带合并订单是否有已合并订单 */
        String idString = "(" + odoIdString + ")";
        List<WhOdo> odoList = this.whOdoDao.findMergeOdoListByIdOuId(idString, ouId);
        if (null != odoList && !odoList.isEmpty()) {
            hasMergeOdo = true;
        }
        /** 1.生成合并订单主档 start */
        WhOdo whOdo = this.createNewOdo(odoIdString, ouId, userId);
        String newOdoCode = whOdo.getOdoCode();
        Long newOdoId = whOdo.getId();
        if (hasMergeOdo) {
            odoIds = sortOdoIds(odoIds, newOdoId.toString());
        }
        for (int i = 0; i < size; i++) {
            // 一个可合并订单的主档id
            Long whOdoId = Long.parseLong(odoIds.get(i));
            /** 2.更新可合并订单主档状态 start */
            updateOriginalOdo(newOdoCode, whOdoId, ouId, userId);
            /** end */
            /** 3.新建新合并订单属性表以及配送对象表 start */
            if (i == 0) {
                if (!hasMergeOdo) {
                    // 在第一次执行 且没有合并订单 新建合并订单属性及配送对象表
                    createNewOdoAddress(newOdoId, whOdoId, ouId);
                    createNewOdoAttr(newOdoId, whOdoId, ouId);
                    createNewTransportMgmt(newOdoId, whOdoId, ouId);
                }
            }
            /** end */
            /** 4,创建新合并订单明细行以及属性对象 5,更新原订单明细行以及属性 start */
            // TODO
            if (!newOdoId.equals(whOdoId)) {
                // whOdoLineInfoCommandList =
                // this.createAndUpdateOdoLineObject(whOdoLineInfoCommandList, newOdoId, whOdoId,
                // ouId, userId);
                this.createAndUpdateOdoLineObject(newOdoId, whOdoId, ouId, userId);
            }
            /** end */
        }
        /** 6,插入新建合并订单明细行及属性 start */
        // if (null != whOdoLineInfoCommandList && !whOdoLineInfoCommandList.isEmpty()) {
        // this.insertOdoLineObject(whOdoLineInfoCommandList);
        // }
        return newOdoId;
    }

    /**
     * [业务方法] 合并订单-待合并的出库单主档id重新排序
     * @param odoIds
     * @param newOdoId
     * @return
     */
    private List<String> sortOdoIds(List<String> odoIds, String newOdoId) {
        int pos = odoIds.indexOf(newOdoId);
        if (-1 != pos) {
            odoIds.set(pos, odoIds.get(0));
            odoIds.set(0, newOdoId);
        }
        return odoIds;
    }

    /**
     * 创建或更新合并订单主档
     * @param odoIdString 合并订单ids
     * @param ouId 组织id
     * @param userId 操作员id
     * @return
     */
    private WhOdo createNewOdo(String odoIdString, Long ouId, Long userId) {
        List<String> odoIds = Arrays.asList(odoIdString.split(","));
        // 查找可合并订单中是否有合并订单
        String idString = "(" + odoIdString + ")";
        List<WhOdo> odoList = this.whOdoDao.findMergeOdoListByIdOuId(idString, ouId);
        WhOdo whOdo = new WhOdo();
        if (null != odoList && !odoList.isEmpty()) {
            // 有合并订单,进入修改合并订单主档逻辑
            WhOdo command = odoList.get(0);
            whOdo = modifyExistMergeOdo(command, userId);
        } else {
            Long odoId = Long.parseLong(odoIds.get(0));
            // 没有合并订单, 进入新建合并订单主档逻辑
            whOdo = createNewMergeOdo(odoId, ouId, userId);
        }
        String originalOdoCode = "";
        /* 数量设置 */
        List<OdoCommand> newOdoList = whOdoDao.findOdoListByIdOuId(idString, ouId, null);
        Double qty = 0.0;
        Double amt = 0.0;
        for (OdoCommand newOdo : newOdoList) {
            qty += newOdo.getQty();
            amt += newOdo.getAmt();
            originalOdoCode += (null == newOdo.getOriginalOdoCode()) ? newOdo.getOdoCode() + "," : newOdo.getOriginalOdoCode() + ",";
        }
        whOdo.setQty(qty);
        whOdo.setAmt(amt);
        if (!StringUtil.isEmpty(originalOdoCode)) {
            originalOdoCode = originalOdoCode.substring(0, originalOdoCode.length() - 1);
        }
        whOdo.setOriginalOdoCode(originalOdoCode);
        if (null != whOdo.getId()) {
            // 待合并订单是合并订单 更新操作
            whOdoDao.saveOrUpdateByVersion(whOdo);
        } else {
            // 待合并订单不是合并订单 插入操作
            whOdoDao.insert(whOdo);
        }
        return whOdo;
    }

    /**
     * [业务方法] 合并订单-修改合并订单逻辑
     * @param whOdo
     * @param userId
     * @return
     */
    private WhOdo modifyExistMergeOdo(WhOdo whOdo, Long userId) {
        whOdo.setModifiedId(userId);
        return whOdo;
    }

    /**
     * [业务方法] 合并订单-新建合并订单主档逻辑
     * @param odoId
     * @param ouId
     * @param userId
     * @return
     */
    private WhOdo createNewMergeOdo(Long odoId, Long ouId, Long userId) {
        WhOdo odo = whOdoDao.findByIdOuId(odoId, ouId);
        if (null == odo) {
            throw new BusinessException("创建合并订单主档失败1");
        }
        WhOdo whOdo = new WhOdo();
        BeanUtils.copyProperties(odo, whOdo);
        whOdo.setId(null);
        whOdo.setOdoCode(codeManager.generateCode(Constants.WMS, Constants.WHODO_MODEL_URL, Constants.WMS_ODO_INNER, "ODO", null));
        whOdo.setExtCode(codeManager.generateCode(Constants.WMS, Constants.WHODO_MODEL_URL, Constants.WMS_ODO_EXT, null, null));
        if (null != whOdo.getOdoStatus() && OdoStatus.ODO_WAVE.equals(whOdo.getOdoStatus())) {
            whOdo.setOdoStatus(OdoStatus.ODO_WAVE);
        } else {
            whOdo.setOdoStatus(OdoStatus.ODO_NEW);
        }
        whOdo.setCreateTime(new Date());
        whOdo.setLastModifyTime(new Date());
        whOdo.setCreatedId(userId);
        whOdo.setModifiedId(userId);
        return whOdo;
    }

    /**
     * 更新原来订单状态
     * @param newWhOdoCode 新建合并订单主档code
     * @param whOdoId 原来订单主档id
     * @param ouId 组织id
     * @param userId 操作员id
     * @return
     */
    private WhOdo updateOriginalOdo(String newWhOdoCode, Long whOdoId, Long ouId, Long userId) {
        WhOdo odo = this.whOdoDao.findByIdOuId(whOdoId, ouId);
        if (null == odo) {
            throw new BusinessException("更新原来订单状态失败1");
        }
        if (!newWhOdoCode.equalsIgnoreCase(odo.getOdoCode())) {
            // 待更新状态订单不是合并订单主档
            odo.setOdoStatus((null == odo.getOriginalOdoCode()) ? OdoStatus.ODO_MERGE : OdoStatus.ODO_CANCEL);
            odo.setGroupOdoCode(newWhOdoCode);
        }
        odo.setModifiedId(userId);
        Integer cnt = whOdoDao.saveOrUpdateByVersion(odo);
        if (0 >= cnt) {
            throw new BusinessException("更新原来订单状态失败2");
        }
        return odo;
    }

    /**
     * 新建合并订单配送对象
     * @param newOdoId 新建合并订单主档id
     * @param whOdoId 原来订单主档id
     * @param ouId 组织id
     * @return
     */
    private WhOdoAddress createNewOdoAddress(Long newOdoId, Long whOdoId, Long ouId) {
        WhOdoAddress whOdoAddressObject = new WhOdoAddress();
        whOdoAddressObject.setOuId(ouId);
        whOdoAddressObject.setOdoId(whOdoId);
        WhOdoAddress whOdoAddress = this.whOdoAddressDao.findObject(whOdoAddressObject);
        if (null == whOdoAddress) {
            throw new BusinessException("新建合并订单配送对象失败1");
        }
        BeanUtils.copyProperties(whOdoAddress, whOdoAddressObject);
        whOdoAddressObject.setId(null);
        whOdoAddressObject.setOdoId(newOdoId);
        Long cnt = whOdoAddressDao.insert(whOdoAddressObject);
        if (0 >= cnt) {
            throw new BusinessException("新建合并订单配送对象失败2");
        }
        return whOdoAddressObject;
    }

    /**
     * 新建合并订单属性
     * @param newOdoId 新建合并订单主档id
     * @param whOdoId 原来订单主档id
     * @param ouId 组织id
     * @return
     */
    private WhOdoAttr createNewOdoAttr(Long newOdoId, Long whOdoId, Long ouId) {
        WhOdoAttr whOdoAttrObject = new WhOdoAttr();
        whOdoAttrObject.setOuId(ouId);
        whOdoAttrObject.setOdoId(whOdoId);
        WhOdoAttr whOdoAttr = this.whOdoAttrDao.findObject(whOdoAttrObject);
        if (null == whOdoAttr) {
            return null;
        }
        BeanUtils.copyProperties(whOdoAttr, whOdoAttrObject);
        whOdoAttrObject.setId(null);
        whOdoAttrObject.setOdoId(newOdoId);
        Long cnt = whOdoAttrDao.insert(whOdoAttrObject);
        if (0 >= cnt) {
            throw new BusinessException("新建合并订单属性失败1");
        }
        return whOdoAttr;
    }

    /**
     * 新建合并订单运输商管理
     * @param newOdoId 新建合并订单主档id
     * @param whOdoId 原来订单主档id
     * @param ouId 组织id
     * @return
     */
    private WhOdoTransportMgmt createNewTransportMgmt(Long newOdoId, Long whOdoId, Long ouId) {
        WhOdoTransportMgmt whOdoTransportMgmtObject = new WhOdoTransportMgmt();
        whOdoTransportMgmtObject.setOuId(ouId);
        whOdoTransportMgmtObject.setOdoId(whOdoId);
        WhOdoTransportMgmt whOdoTransportMgmt = this.whOdoTransportMgmtDao.findObject(whOdoTransportMgmtObject);
        if (null == whOdoTransportMgmt) {
            throw new BusinessException("新建合并订单运输商管理失败1");
        }
        BeanUtils.copyProperties(whOdoTransportMgmt, whOdoTransportMgmtObject);
        whOdoTransportMgmtObject.setId(null);
        whOdoTransportMgmtObject.setOdoId(newOdoId);
        Long cnt = whOdoTransportMgmtDao.insert(whOdoTransportMgmtObject);
        if (0 >= cnt) {
            throw new BusinessException("新建合并订单运输商管理失败2");
        }
        return whOdoTransportMgmtObject;
    }

    /**
     * 新建与更新合并订单明细对象
     * @param whOdoLineInfoCommandList 新建出库单明细列表
     * @param newOdoId 新建合并订单主档id
     * @param whOdoId 原来订单主档id
     * @param ouId 组织id
     * @param userId 操作员id
     * @return
     */
    @SuppressWarnings("unused")
    private List<OdoLineInfoCommand> createAndUpdateOdoLineObject1(List<OdoLineInfoCommand> whOdoLineInfoCommandList, Long newOdoId, Long whOdoId, Long ouId, Long userId) {
        // TODO 如果是已合并订单 第一个合并订单不做
        List<OdoLineCommand> whOdoLineList = findOdoLine(whOdoId, ouId);
        OdoLineInfoCommand whOdoLineInfoCommand = new OdoLineInfoCommand();
        if (null != whOdoLineList && !whOdoLineList.isEmpty()) {
            for (OdoLineCommand whOdoLine : whOdoLineList) {
                if (null == whOdoLine.getOriginalOdoCode()) {
                    // 非合并的订单明细行更新
                    this.updateOdoLine(whOdoLine, userId);
                    WhOdoLine newWhOdoLine = new WhOdoLine();
                    Long whOdoLineId = whOdoLine.getId();
                    String whOdoCode = whOdoLine.getOdoCode();
                    BeanUtils.copyProperties(whOdoLine, newWhOdoLine);
                    // 新建合并订单明细行对象:缺少行id
                    newWhOdoLine = this.createNewOdoLine(newWhOdoLine, whOdoCode, newOdoId, userId);
                    whOdoLineInfoCommand.setWhOdoLine(newWhOdoLine);
                    if (null != whOdoLineId) {
                        WhOdoLineAttr whOdoLineAttr = findOdoLineAttr(whOdoLineId, ouId);
                        if (null != whOdoLineAttr) {
                            whOdoLineAttr.setOdoLineId(null);
                            whOdoLineAttr.setId(null);
                        }
                        // TODO 允许设置为null?
                        whOdoLineInfoCommand.setWhOdoLineAttr(whOdoLineAttr);
                    }
                    whOdoLineInfoCommandList.add(whOdoLineInfoCommand);
                } else {
                    // this.updateMergeOdoLine(whOdoLine, userId);
                    return null;
                }
            }
        }
        if (null != whOdoLineInfoCommandList && !whOdoLineInfoCommandList.isEmpty()) {
            this.insertOdoLineObject(whOdoLineInfoCommandList, newOdoId, ouId);
        }
        return whOdoLineInfoCommandList;
    }

    /**
     * [业务方法] 合并订单-创建并更新出库单明细行
     * @param newOdoId
     * @param whOdoId
     * @param ouId
     * @param userId
     * @return
     */
    private List<OdoLineInfoCommand> createAndUpdateOdoLineObject(Long newOdoId, Long whOdoId, Long ouId, Long userId) {
        List<OdoLineCommand> whOdoLineList = findOdoLine(whOdoId, ouId);
        // 非合并订单明细行列表
        List<OdoLineInfoCommand> normalOdoLineInfoCommandList = new ArrayList<OdoLineInfoCommand>();
        // 合并订单明细行列表
        List<OdoLineInfoCommand> mergeOdoLineInfoCommandList = new ArrayList<OdoLineInfoCommand>();
        if (null != whOdoLineList && !whOdoLineList.isEmpty()) {
            for (OdoLineCommand whOdoLine : whOdoLineList) {
                if (null == whOdoLine.getOriginalOdoCode()) {
                    // 非合并的订单明细行更新
                    OdoLineInfoCommand whOdoLineInfoCommand = this.updateNormalOdoLine(whOdoLine, newOdoId, ouId, userId);
                    normalOdoLineInfoCommandList.add(whOdoLineInfoCommand);
                } else {
                    // 合并的订单明细行更新 1:更新最原始的出库单明细行操作时间以及操作人id;2:更新合并订单明细行的行号以及出库单id为最新数据
                    OdoLineInfoCommand mergeOdoLineInfoCommand = this.updateMergeOdoLine(whOdoLine, newOdoId, ouId, userId);
                    mergeOdoLineInfoCommandList.add(mergeOdoLineInfoCommand);
                }
            }
        }
        if (null != normalOdoLineInfoCommandList && !normalOdoLineInfoCommandList.isEmpty()) {
            this.insertOdoLineObject(normalOdoLineInfoCommandList, newOdoId, ouId);
        }
        if (null != mergeOdoLineInfoCommandList && !mergeOdoLineInfoCommandList.isEmpty()) {
            this.updateOdoLineObject(mergeOdoLineInfoCommandList, newOdoId, ouId);
        }
        return normalOdoLineInfoCommandList;
    }

    private OdoLineInfoCommand updateNormalOdoLine(OdoLineCommand whOdoLine, Long newOdoId, Long ouId, Long userId) {
        OdoLineInfoCommand whOdoLineInfoCommand = new OdoLineInfoCommand();
        this.updateOdoLine(whOdoLine, userId);
        WhOdoLine newWhOdoLine = new WhOdoLine();
        Long whOdoLineId = whOdoLine.getId();
        String whOdoCode = whOdoLine.getOdoCode();
        BeanUtils.copyProperties(whOdoLine, newWhOdoLine);
        // 新建合并订单明细行对象:缺少行id
        newWhOdoLine = this.createNewOdoLine(newWhOdoLine, whOdoCode, newOdoId, userId);
        whOdoLineInfoCommand.setWhOdoLine(newWhOdoLine);
        if (null != whOdoLineId) {
            WhOdoLineAttr whOdoLineAttr = findOdoLineAttr(whOdoLineId, ouId);
            if (null != whOdoLineAttr) {
                whOdoLineAttr.setOdoLineId(null);
                whOdoLineAttr.setId(null);
            }
            // TODO 允许设置为null?
            whOdoLineInfoCommand.setWhOdoLineAttr(whOdoLineAttr);
        }
        return whOdoLineInfoCommand;
    }

    /**
     * [业务方法] 合并订单-更新合并订单明细行
     * @param whOdoLine
     * @param newOdoId
     * @param ouId
     * @param userId
     * @return
     */
    private OdoLineInfoCommand updateMergeOdoLine(OdoLineCommand whOdoLine, Long newOdoId, Long ouId, Long userId) {
        OdoLineInfoCommand whOdoLineInfoCommand = new OdoLineInfoCommand();
        // 更新最原始订单明细行操作时间
        Integer lineNum = whOdoLine.getOriginalLinenum();
        String originalOdoCode = whOdoLine.getOriginalOdoCode();
        // TODO
        WhOdoLine odoLine = this.whOdoLineDao.findByOdoCodeAndLineNum(lineNum, originalOdoCode, ouId);
        odoLine.setModifiedId(userId);
        this.whOdoLineDao.saveOrUpdateByVersion(odoLine);

        // 将合并订单明细行关联到合并订单主档
        whOdoLine.setOdoId(newOdoId);
        WhOdoLine line = new WhOdoLine();
        BeanUtils.copyProperties(whOdoLine, line);
        whOdoLineInfoCommand.setWhOdoLine(line);

        return whOdoLineInfoCommand;

    }

    /**
     * [业务方法] 合并订单-插入新建出库单明细行
     * @param whOdoLineInfoCommandList
     * @param newOdoId
     * @param ouId
     * @return
     */
    private List<OdoLineInfoCommand> insertOdoLineObject(List<OdoLineInfoCommand> whOdoLineInfoCommandList, Long newOdoId, Long ouId) {
        // TODO 需要加上原来的whOdoLine个数
        Integer size = whOdoLineInfoCommandList.size();

        WhOdoLine whOdoLine = new WhOdoLine();
        whOdoLine.setOdoId(newOdoId);
        whOdoLine.setOuId(ouId);
        Integer lineCnt = (int) this.whOdoLineDao.findListCountByParam(whOdoLine);
        for (int i = 0; i < size; i++) {
            OdoLineInfoCommand whOdoLineInfoCommand = whOdoLineInfoCommandList.get(i);
            WhOdoLine odoLine = whOdoLineInfoCommand.getWhOdoLine();
            WhOdoLineAttr whOdoLineAttr = whOdoLineInfoCommand.getWhOdoLineAttr();
            odoLine.setLinenum(lineCnt + i + 1);
            Long cnt = this.whOdoLineDao.insert(odoLine);
            if (null == cnt) {
                throw new BusinessException("插入新建出库单明细行失败");
            }
            Long whOdoLineId = odoLine.getId();
            if (null != whOdoLineAttr) {
                whOdoLineAttr.setOdoLineId(whOdoLineId);
                Long count = this.whOdoLineAttrDao.insert(whOdoLineAttr);
                if (0 >= count) {
                    throw new BusinessException("插入新建出库单明细行属性失败");
                }
            }
        }
        return whOdoLineInfoCommandList;
    }

    /**
     * [业务方法] 合并订单-插入新建出库单明细行
     * @param whOdoLineInfoCommandList
     * @param newOdoId
     * @param ouId
     * @return
     */
    private List<OdoLineInfoCommand> updateOdoLineObject(List<OdoLineInfoCommand> whOdoLineInfoCommandList, Long newOdoId, Long ouId) {
        WhOdoLine whOdoLine = new WhOdoLine();
        whOdoLine.setOdoId(newOdoId);
        whOdoLine.setOuId(ouId);
        Integer cnt = (int) this.whOdoLineDao.findListCountByParam(whOdoLine);

        for (int i = 0; i < whOdoLineInfoCommandList.size(); i++) {
            WhOdoLine odoLine = whOdoLineInfoCommandList.get(i).getWhOdoLine();
            odoLine.setLinenum(cnt + i + 1);
            this.whOdoLineDao.saveOrUpdateByVersion(odoLine);
        }
        return whOdoLineInfoCommandList;
    }

    /**
     * 根据id ouId 获取出库单明细列表
     * @param whOdoId 原来订单主档id
     * @param ouId 组织id
     * @return
     */
    private List<OdoLineCommand> findOdoLine(Long whOdoId, Long ouId) {
        WhOdoLine whOdoLineObject = new WhOdoLine();
        whOdoLineObject.setOuId(ouId);
        whOdoLineObject.setOdoId(whOdoId);
        List<OdoLineCommand> whOdoLineList = this.whOdoLineDao.findObject(whOdoLineObject);
        // if (null == whOdoLineList || whOdoLineList.isEmpty()) {
        // throw new BusinessException("出库单没有明细");
        // }
        return whOdoLineList;

    }

    /**
     * 根据id ouId 获取出库单明细属性
     * @param whOdoLineId 原来订单明细id
     * @param ouId 组织id
     * @return
     */
    private WhOdoLineAttr findOdoLineAttr(Long whOdoLineId, Long ouId) {
        WhOdoLineAttr whOdoLineAttrObject = new WhOdoLineAttr();
        whOdoLineAttrObject.setOuId(ouId);
        whOdoLineAttrObject.setOdoLineId(whOdoLineId);
        WhOdoLineAttr whOdoLineAttr = this.whOdoLineAttrDao.findObject(whOdoLineAttrObject);
        return whOdoLineAttr;
    }

    /**
     * 新建合并订单明细行
     * @param whOdoLine 出库单明细行
     * @param whOdoCode 出库单主档code
     * @param newOdoId 新建合并订单主档id
     * @param userId 操作员id
     * @return
     */
    private WhOdoLine createNewOdoLine(WhOdoLine whOdoLine, String whOdoCode, Long newOdoId, Long userId) {
        // 原来子订单更新:
        // 状态:新建; 添加合并后出库单单号;
        if (null != whOdoLine.getOdoLineStatus() && OdoStatus.ODOLINE_WAVE.equals(whOdoLine.getOdoLineStatus())) {
            whOdoLine.setOdoLineStatus(OdoStatus.ODOLINE_WAVE);
        } else {
            whOdoLine.setOdoLineStatus(OdoStatus.ODOLINE_NEW);
        }
        whOdoLine.setOdoId(newOdoId);
        whOdoLine.setOriginalOdoCode(whOdoCode);
        whOdoLine.setOriginalLinenum(whOdoLine.getLinenum());
        whOdoLine.setCreatedId(userId);
        whOdoLine.setCreateTime(new Date());
        whOdoLine.setLastModifyTime(new Date());
        whOdoLine.setLinenum(null);
        whOdoLine.setId(null);
        return whOdoLine;
    }

    /**
     * 更新合并前订单明细行
     * @param whOdoLine 出库单明细行
     * @param userId 操作员id
     * @return
     */
    private WhOdoLine updateOdoLine(OdoLineCommand command, Long userId) {
        WhOdoLine whOdoLine = new WhOdoLine();
        BeanUtils.copyProperties(command, whOdoLine);
        // 原来子订单更新:
        // 状态:新建->已合并;
        whOdoLine.setOdoLineStatus(OdoStatus.ODOLINE_MERGE);
        whOdoLine.setModifiedId(userId);
        Integer cnt = whOdoLineDao.saveOrUpdateByVersion(whOdoLine);
        if (0 >= cnt) {
            throw new BusinessException("更新合并前订单明细行失败");
        }
        return whOdoLine;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<OdoCommand> findOdoList(String ids, Long ouId, String odoStatus) {
        if (!StringUtils.hasText(ids) || "undefined".equalsIgnoreCase(ids)) {
            return null;
        }
        String idString = "(" + ids + ")";
        List<OdoCommand> OdoCommandList = this.whOdoDao.findOdoListByIdOuId(idString, ouId, odoStatus);
        OdoCommandList = setCustomerAndStore(OdoCommandList);
        return OdoCommandList;
    }

    private List<OdoCommand> setCustomerAndStore(List<OdoCommand> OdoCommandList) {
        if (null != OdoCommandList && !OdoCommandList.isEmpty()) {
            for (OdoCommand odoCommand : OdoCommandList) {
                List<Long> storeList = new ArrayList<Long>();
                List<Long> customerList = new ArrayList<Long>();
                Long storeId = odoCommand.getStoreId();
                Long customerId = odoCommand.getCustomerId();
                storeList.add(storeId);
                customerList.add(customerId);
                Map<Long, Customer> customerMap = this.findCustomerByRedis(customerList);
                if (null != customerMap && !customerMap.isEmpty()) {
                    odoCommand.setCustomerName(customerMap.get(customerId).getCustomerName());
                }
                Map<Long, Store> storeMap = this.findStoreByRedis(storeList);
                if (null != storeMap && !storeMap.isEmpty()) {
                    odoCommand.setStoreName(storeMap.get(storeId).getStoreName());
                }
            }
        }
        return OdoCommandList;
    }


    // -----------------------------------------波次中合并订单逻辑---------------------------------------
    /**
     * 波次中合并订单逻辑
     * 1.合并波次中订单
     * 2.每个原始出库单取消绑定波次编码
     * 3.每个原始出库单明细行取消绑定波次编码
     * 4.波次中剔除已经合并的出库单明细行
     * 5.新合并的订单绑定到波次
     * 6.新合并的订单明细行绑定到波次
     * 7.波次新增波次明细行
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void waveOdoMerge(WhWave wave, String odoIds, Long ouId, Long userId) {
        /** 合并波次中订单 start*/
        WhOdo whOdo = this.generalOdoMerge(odoIds, ouId, userId);
        WhWaveLine line = this.whWaveLineDao.findHighestPriorityByOdoIds(wave.getId(), odoIds, ouId);
        /** end*/
        /** 子订单操作 start*/

        String originalOdoCode = whOdo.getOriginalOdoCode(); // 子订单
        String[] odoCodeList = originalOdoCode.split(",");
        for (String odoCode : odoCodeList) {
            // 解绑原出库单
            WhOdo odo = unbindOdo(odoCode, ouId, userId);
            if (null != odo) {
                // 解绑原出库单明细行
                unbindOdoLine(odo.getId(), ouId, userId);
            }
            updateWaveLine(wave.getId(), odo.getId(), whOdo.getId(), whOdo.getOdoCode(), ouId, userId, line);
        }
        /** end*/
        /** 新订单操作*/
        whOdo.setWaveCode(wave.getCode());
        this.whOdoDao.saveOrUpdateByVersion(whOdo);
        bindOdoLine(wave.getId(), wave.getCode(), whOdo.getId(), ouId, userId);
        /** end*/
        // 新建的出库单明细 绑定

    }

    /**
     * [业务方法] 波次中合并订单-解绑原始出库单
     * @param odoCode
     * @param ouId
     * @param userId
     * @return 返回波次中原出库单
     */
    private WhOdo unbindOdo(String odoCode, Long ouId, Long userId) {
        WhOdo odo = this.whOdoDao.findOdoByCodeAndOuId(odoCode, ouId);
        if (null != odo) {
            odo.setWaveCode(null);
            odo.setModifiedId(userId);
            this.whOdoDao.saveOrUpdateByVersion(odo);
        }
        return odo;
    }

    /**
     * [业务方法] 波次中合并订单-解绑原始出库单明细行
     * @param odoId
     * @param ouId
     * @param userId
     */
    private void unbindOdoLine(Long odoId, Long ouId, Long userId) {
        List<WhOdoLine> odoLineList = this.whOdoLineDao.findOdoLineListByOdoIdOuId(odoId, ouId);
        if (null != odoLineList && !odoLineList.isEmpty()) {
            for (WhOdoLine odoLine : odoLineList) {
                odoLine.setWaveCode(null);
                odoLine.setModifiedId(userId);
                this.whOdoLineDao.saveOrUpdateByVersion(odoLine);
            }
        }
    }

    /**
     * [业务方法] 波次中合并出库单-更新波次明细行
     * @param waveId 波次id
     * @param originalOdoId 原出库单id
     * @param odoId 新出库单id
     * @param odoCode 新出库单编码
     * @param ouId 组织id
     * @param userId
     */
    private void updateWaveLine(Long waveId, Long originalOdoId, Long odoId, String odoCode, Long ouId, Long userId, WhWaveLine line) {
        // 返回原订单的所有明细
        List<WhOdoLine> odoLineList = this.whOdoLineDao.findOdoLineListByOdoIdOuId(originalOdoId, ouId);
        if (null != odoLineList && !odoLineList.isEmpty()) {
            for (WhOdoLine odoLine : odoLineList) {
                WhWaveLine whWaveLine = this.whWaveLineDao.findWaveLineByOdoLineIdAndWaveId(waveId, odoLine.getId(), ouId);
                whWaveLine.setOdoId(odoId);
                whWaveLine.setOdoCode(odoCode);
                whWaveLine.setLinenum(odoLine.getLinenum());
                whWaveLine.setOdoPriorityLevel(line.getOdoPriorityLevel());
                whWaveLine.setModifiedId(userId);
                whWaveLine.setOdoPlanDeliverGoodsTime(line.getOdoPlanDeliverGoodsTime());
                whWaveLine.setOdoOrderTime(line.getOdoOrderTime());
                this.whWaveLineDao.saveOrUpdateByVersion(whWaveLine);
            }
        }
    }

    /**
     * [业务方法] 波次中合并订单-绑定合并后的订单明细行
     * @param waveId
     * @param waveCode
     * @param odoId
     * @param ouId
     * @param userId
     */
    private void bindOdoLine(Long waveId, String waveCode, Long odoId, Long ouId, Long userId) {
        List<WhOdoLine> odoLineList = this.whOdoLineDao.findOdoLineListByOdoIdOuId(odoId, ouId);
        if (null != odoLineList && !odoLineList.isEmpty()) {
            for (WhOdoLine odoLine : odoLineList) {
                /**出库单明细行绑定到波次 start*/
                // if (null != odoLine.getWaveCode()) {
                odoLine.setWaveCode(waveCode);
                this.whOdoLineDao.saveOrUpdateByVersion(odoLine);
                // }
                /** end*/
            }
        }
    }

    /**
     * [业务方法] 合并出库单-取消合并
     * @param odoId
     * @param ouId
     * @param userId
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void odoMergeCancel(Long odoId, Long ouId, Long userId) {
        WhOdo odo = this.whOdoDao.findByIdOuId(odoId, ouId);
        // TODO 更新合并出库单状态为取消
        // TODO 更新合并出库单明细状态为取消
        // TODO 更新原始出库单状态为新建
        // TODO 更新原始出库单明细状态为新建
        // updateOdo
        String originalOdoCode = odo.getOriginalOdoCode();
        if (null != originalOdoCode && StringUtils.hasText(originalOdoCode)) {
            String[] odoCodes = originalOdoCode.split(",");
        }
    }
}
