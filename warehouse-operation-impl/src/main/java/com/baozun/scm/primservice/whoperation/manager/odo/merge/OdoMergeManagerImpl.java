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
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoAddress;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoAttr;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoLine;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoLineAttr;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoTransportMgmt;
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
    private CodeManager codeManager;

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Pagination<OdoResultCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params) {
        String pageOption = (String) params.get("pageOption");
        if(StringUtils.hasText(pageOption)){
            page.setSize(Integer.parseInt(pageOption));
        }
        String ids = this.findOdoMergableIds(params);
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
            Set<String> dic2 = new HashSet<String>();
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
            if (list != null && list.size() > 0) {
                for (OdoResultCommand command : list) {
                    if (StringUtils.hasText(command.getIsWholeOrderOutbound())) {
                        dic1.add(command.getIsWholeOrderOutbound());
                    }
                    if (StringUtils.hasText(command.getPartOutboundStrategy())) {

                        dic2.add(command.getPartOutboundStrategy());
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
                }
                Map<String, List<String>> map = new HashMap<String, List<String>>();
                map.put(Constants.IS_WHOLE_ORDER_OUTBOUND, new ArrayList<String>(dic1));
                map.put(Constants.PART_OUTBOUND_STRATEGY, new ArrayList<String>(dic2));
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
                for (OdoResultCommand command : list) {
                    if (StringUtils.hasText(command.getIsWholeOrderOutbound())) {
                        SysDictionary sys = dicMap.get(Constants.IS_WHOLE_ORDER_OUTBOUND + "_" + command.getIsWholeOrderOutbound());
                        command.setIsWholeOrderOutboundName(sys == null ? command.getIsWholeOrderOutbound() : sys.getDicLabel());
                    }
                    if (StringUtils.hasText(command.getPartOutboundStrategy())) {
                        SysDictionary sys = dicMap.get(Constants.PART_OUTBOUND_STRATEGY + "_" + command.getPartOutboundStrategy());
                        command.setPartOutboundStrategyName(sys == null ? command.getPartOutboundStrategy() : sys.getDicLabel());
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
                }
                pages.setItems(list);
            }
        }
        return pages;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Map<String, String> doOdoMerge(List<String> odoIds, List<String> options, Long ouId, Long userId) {
        Map<String, String> response = new HashMap<String, String>();
        String odoIdString = StringUtil.listToString(odoIds, ',');
        List<String> optionList = convertOptions(options);
        List<OdoMergeCommand> list = this.whOdoDao.odoMerge(odoIdString, ouId, optionList.get(0), optionList.get(1), optionList.get(2), optionList.get(3));
        if (!list.isEmpty() && list.size() > 0) {
            /* 合并订单 */
            response = createNewOdoInfo(list, ouId, userId);
        }
        return response;
    }

    /**
     * 1,新建新合并订单主档
     * 2,更新原订单状态
     * 3,新建新合并订单属性表以及配送对象表和运输商管理表
     * 4,新建新合并订单明细行以及属性对象
     * 5,更新原订单明细行以及属性
     * 6,插入新建合并订单明细行及属性
     * @param odoMergeCommandList
     * @param ouId
     * @param userId
     * @return
     */
    private Map<String, String> createNewOdoInfo(List<OdoMergeCommand> odoMergeCommandList, Long ouId, Long userId) {
        // List<OdoInfoCommand> newOdoInfoCommandList = new
        // ArrayList<OdoInfoCommand>(odoMergeCommandList.size());
        // 合并失败订单
        String failMergeOdoIds = "";
        // 合并成功订单
        String successMergeOdoIds = "";
        Integer count = 0;
        Map<String, String> response = new HashMap<String, String>();
        for (OdoMergeCommand odoMergeCommand : odoMergeCommandList) {
            List<OdoLineInfoCommand> whOdoLineInfoCommandList = new ArrayList<OdoLineInfoCommand>();
            count = 0;
            if (null == odoMergeCommand.getOdoId() || !StringUtils.hasText(odoMergeCommand.getOdoId())) {
                throw new BusinessException("没有合并订单号");
            }
            // 可合并订单主档id列表
            List<String> odoIds = Arrays.asList(odoMergeCommand.getOdoId().split(","));
            // 比较可合并订单数量:数量不匹配抛出异常
            Integer size = odoIds.size();
            if (1 == size) {
                // 只有一个可合并出库订单, 不进行合并且记录到合并失败列表返回
                failMergeOdoIds += Long.parseLong(odoIds.get(0)) + ",";
                continue;
            }

            if (null != odoMergeCommand.getCount()) {
                count = odoMergeCommand.getCount().intValue();
            }
            if (size != count) {
                throw new BusinessException("数量不对");
            }
            /** 1.生成合并订单主档 start */
            WhOdo whOdo = this.createNewOdo(odoIds, odoMergeCommand, ouId, userId);
            if (null == whOdo) {
                throw new BusinessException("生成合并订单主档失败");
            }
            /** end */
            // 合并订单code
            String newOdoCode = whOdo.getOdoCode();
            Long newOdoId = whOdo.getId();
            successMergeOdoIds += newOdoId + ",";
            for (int i = 0; i < size; i++) {
                // 一个可合并订单的主档id
                Long whOdoId = Long.parseLong(odoIds.get(i));
                /** 2.更新可合并订单主档状态 start */
                updateOriginalOdo(newOdoCode, whOdoId, ouId, userId);
                /** end */

                /** 3.新建新合并订单属性表以及配送对象表 start */
                if (i == 0) {
                    // 在第一次执行新建合并订单属性及配送对象表
                    createNewOdoAddress(newOdoId, whOdoId, ouId);
                    createNewOdoAttr(newOdoId, whOdoId, ouId);
                    createNewTransportMgmt(newOdoId, whOdoId, ouId);
                }
                /** end */
                /** 4,新建新合并订单明细行以及属性对象 5,更新原订单明细行以及属性 start */
                whOdoLineInfoCommandList = this.createAndUpdateOdoLineObject(whOdoLineInfoCommandList, newOdoId, whOdoId, ouId, userId);
                /** end */
            }
            /** 6,插入新建合并订单明细行及属性 start */
            if (null != whOdoLineInfoCommandList && !whOdoLineInfoCommandList.isEmpty()) {
                this.insertOdoLineObject(whOdoLineInfoCommandList);
            }
            /** end */
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

    /**
     * 创建合并订单主档
     * @param odoIds 合并订单ids
     * @param odoMergeCommand 合并订单对象
     * @param ouId 组织id
     * @param userId 操作员id
     * @return
     */
    private WhOdo createNewOdo(List<String> odoIds, OdoMergeCommand odoMergeCommand, Long ouId, Long userId) {
        Long odoId = Long.parseLong(odoIds.get(0));
        WhOdo odo = whOdoDao.findByIdOuId(odoId, ouId);
        if (null == odo) {
            throw new BusinessException("创建合并订单主档失败1");
        }
        WhOdo whOdo = new WhOdo();
        BeanUtils.copyProperties(odo, whOdo);
        whOdo.setId(null);
        whOdo.setOdoCode(codeManager.generateCode(Constants.WMS, Constants.WHODO_MODEL_URL, Constants.WMS_ODO_INNER, "ODO", null));
        whOdo.setExtCode(codeManager.generateCode(Constants.WMS, Constants.WHODO_MODEL_URL, Constants.WMS_ODO_EXT, null, null));
        whOdo.setOdoStatus(OdoStatus.ODO_NEW);
        whOdo.setCreateTime(new Date());
        whOdo.setLastModifyTime(new Date());
        whOdo.setCreatedId(userId);
        whOdo.setModifiedId(userId);
        whOdo.setQty(odoMergeCommand.getQuantity());
        whOdo.setAmt(odoMergeCommand.getSum());
        whOdo.setOriginalOdoCode(odoMergeCommand.getOriginalOdoCode());
        Long cnt = whOdoDao.insert(whOdo);
        if (0 >= cnt) {
            throw new BusinessException("创建合并订单主档失败2");
        }
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
        odo.setOdoStatus(OdoStatus.ODO_MERGE);
        odo.setModifiedId(userId);
        odo.setGroupOdoCode(newWhOdoCode);
        Integer cnt = whOdoDao.update(odo);
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
    private List<OdoLineInfoCommand> createAndUpdateOdoLineObject(List<OdoLineInfoCommand> whOdoLineInfoCommandList, Long newOdoId, Long whOdoId, Long ouId, Long userId) {
        List<OdoLineCommand> whOdoLineList = findOdoLine(whOdoId, ouId);
        OdoLineInfoCommand whOdoLineInfoCommand = new OdoLineInfoCommand();
        if (null != whOdoLineList && !whOdoLineList.isEmpty()) {
            for (OdoLineCommand whOdoLine : whOdoLineList) {
                WhOdoLine newWhOdoLine = new WhOdoLine();
                Long whOdoLineId = whOdoLine.getId();
                String whOdoCode = whOdoLine.getOdoCode();
                BeanUtils.copyProperties(whOdoLine, newWhOdoLine);
                // 新建合并订单明细行对象:缺少行id
                newWhOdoLine = this.createNewOdoLine(newWhOdoLine, whOdoCode, newOdoId, userId);
                // 更新合并订单明细行
                this.updateOdoLine(whOdoLine, userId);
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
            }
        }
        return whOdoLineInfoCommandList;
    }

    /**
     * 插入新建出库单明细行
     * @param whOdoLineInfoCommandList 出库单明细行列表
     * @return
     */
    private List<OdoLineInfoCommand> insertOdoLineObject(List<OdoLineInfoCommand> whOdoLineInfoCommandList) {
        Integer size = whOdoLineInfoCommandList.size();
        for (int i = 0; i < size; i++) {
            OdoLineInfoCommand whOdoLineInfoCommand = whOdoLineInfoCommandList.get(i);
            WhOdoLine whOdoLine = whOdoLineInfoCommand.getWhOdoLine();
            WhOdoLineAttr whOdoLineAttr = whOdoLineInfoCommand.getWhOdoLineAttr();
            whOdoLine.setLinenum(i + 1);
            Long cnt = this.whOdoLineDao.insert(whOdoLine);
            if (0 >= cnt) {
                throw new BusinessException("插入新建出库单明细行失败");
            }
            Long whOdoLineId = whOdoLine.getId();
            if (null != whOdoLineAttr) {
                whOdoLineAttr.setOdoLineId(whOdoLineId);
                Long count = this.whOdoLineAttrDao.insert(whOdoLineAttr);
                if (0 >= count) {
                    throw new BusinessException("插入新建出库单明细行属性失败");
                }
            }
        }
        // for (OdoLineInfoCommand whOdoLineInfoCommand : whOdoLineInfoCommandList) {
        // for (int i = 0; i < size; i++) {
        // WhOdoLine whOdoLine = whOdoLineInfoCommand.getWhOdoLine();
        // WhOdoLineAttr whOdoLineAttr = whOdoLineInfoCommand.getWhOdoLineAttr();
        // whOdoLine.setLinenum(i + 1);
        // this.whOdoLineDao.insert(whOdoLine);
        // Long whOdoLineId = whOdoLine.getId();
        // if (null != whOdoLineAttr) {
        // whOdoLineAttr.setOdoLineId(whOdoLineId);
        // this.whOdoLineAttrDao.insert(whOdoLineAttr);
        // }
        // }
        // }
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
        whOdoLine.setOdoLineStatus(OdoStatus.ODOLINE_NEW);
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
        Integer cnt = whOdoLineDao.update(whOdoLine);
        if (0 >= cnt) {
            throw new BusinessException("更新合并前订单明细行失败");
        }
        return whOdoLine;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<OdoCommand> findOdoList(String ids, Long ouId, String odoStatus) {
        if (!StringUtils.hasText(ids)) {
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
}
