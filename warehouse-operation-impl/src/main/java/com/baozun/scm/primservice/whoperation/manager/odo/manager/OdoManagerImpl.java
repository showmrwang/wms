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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.baozun.scm.primservice.whoperation.command.odo.OdoResultCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoDao;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.system.SysDictionary;

@Service("odoManager")
@Transactional
public class OdoManagerImpl extends BaseManagerImpl implements OdoManager {
    @Autowired
    private WhOdoDao whOdoDao;

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Pagination<OdoResultCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params) {
        Pagination<OdoResultCommand> pages = this.whOdoDao.findListByQueryMapWithPageExt(page, sorts, params);
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

}
