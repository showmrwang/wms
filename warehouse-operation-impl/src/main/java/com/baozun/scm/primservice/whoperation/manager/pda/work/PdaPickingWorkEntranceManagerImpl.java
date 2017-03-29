package com.baozun.scm.primservice.whoperation.manager.pda.work;

import java.util.Map;

import lark.common.annotation.MoreDB;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.baozun.scm.primservice.whoperation.command.pda.work.PickingWorkCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhWorkCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhFunctionPickingDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhLocationDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhWorkDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.auth.OperPrivilegeManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.Location;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFunctionPicking;

@Service("pdaPickingWorkEntranceManager")
@Transactional
public class PdaPickingWorkEntranceManagerImpl extends BaseManagerImpl implements PdaPickingWorkEntranceManager {

    @Autowired
    private WhFunctionPickingDao whFunctionPickingDao;

    @Autowired
    private WhLocationDao whLocationDao;

    @Autowired
    private WhWorkDao whWorkDao;

    @Autowired
    private OperPrivilegeManager operPrivilegeManager;

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public PickingWorkCommand retrievePickingWorkList(PickingWorkCommand command, Page page, Sort[] sorts, Map<String, Object> param) {
        Boolean isLastPage = false;
        Long userId = command.getUserId();
        Long ouId = command.getOuId();
        // 判断已经显示多少条工作
        Integer maxObtainWorkQty = command.getMaxObtainWorkQty();
        if (null != maxObtainWorkQty) {
            if (null == page) {
                page = new Page();
            }
            Integer cnt = maxObtainWorkQty - page.getSize() * (page.getPage() - 1);
            if (cnt <= page.getSize()) {
                page.setSize(cnt);
                isLastPage = true;
            }
        }
        param.put("category", "PICKING");
        param.put("userId", userId);
        param.put("ouId", ouId);

        if (StringUtils.hasText(command.getWorkCode())) {
            param.put("workCode", command.getWorkCode());
        }
        if (StringUtils.hasText(command.getLocCode())) {
            param.put("locCode", command.getLocCode());
        }
        if (StringUtils.hasText(command.getContainerCode())) {
            param.put("containerCode", command.getContainerCode());
        }
        if (StringUtils.hasText(command.getOutBound())) {
            param.put("batch", command.getOutBound());
        }
        if (StringUtils.hasText(command.getWaveCode())) {
            param.put("waveCode", command.getWaveCode());
        }
        if (StringUtils.hasText(command.getOutBoundBox())) {
            param.put("outBoundBox", command.getOutBoundBox());
        }
        // page.setSize(maxObtainWorkQty.intValue());
        Pagination<WhWorkCommand> workList = privilegeControl(page, sorts, param);
        if (isLastPage) {
            workList.setTotalPages(workList.getCurrentPage());
        }
        if (null != maxObtainWorkQty) {
            Long count = workList.getCount();
            if (count > maxObtainWorkQty) {
                workList.setCount(maxObtainWorkQty);
                workList.setTotalPages(new Double(Math.ceil(maxObtainWorkQty.doubleValue() / 6)).intValue());
            }
        }
        /*
         * List<WhWorkCommand> list = workList.getItems(); if (null != list && !list.isEmpty()) {
         * for (WhWorkCommand c : list) {
         * c.setUrl("/pda/warehouse/work/pdaReplenishment/isWorkStatus?workId=" + c.getId() +
         * "&functionId=" + command.getFuncId()); } }
         */

        if (null != param.get("scanLocCode")) {
            String locCode = param.get("scanLocCode").toString();
            if (null != workList && (null == workList.getItems() || workList.getItems().isEmpty())) {
                workList = compensateStep(locCode, command, page, sorts, param);
            }
        }
        command.setWorkList(workList);
        return command;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public PickingWorkCommand getObtainWorkWay(PickingWorkCommand command, Long funcId, Long ouId) {
        WhFunctionPicking picking = whFunctionPickingDao.findByFunctionIdExt(ouId, funcId);
        if (null != picking) {
            String obtainWorkWays = picking.getObtainWorkWay();
            if (StringUtils.hasText(obtainWorkWays)) {
                String[] obtainWorkWayArray = obtainWorkWays.split(",");
                for (String obtainWorkWay : obtainWorkWayArray) {
                    switch (obtainWorkWay) {
                        case Constants.WORK_CODE:
                            command.setScanWorkCode(true);
                            break;
                        case Constants.LOC_CODE:
                            command.setScanLocCode(true);
                            break;
                        case Constants.CONTAINER_CODE:
                            command.setScanContainerCode(true);
                            break;
                        case Constants.OUT_BOUND:
                            command.setScanOutBound(true);
                            break;
                        case Constants.WAVE_CODE:
                            command.setScanWaveCode(true);
                            break;
                        case Constants.OUT_BOUND_BOX:
                            command.setScanOutBoundBox(true);
                            break;
                        default:
                            throw new BusinessException("没有获取方法");
                    }
                }
            }
        } else {
            command.setScanWorkCode(true);
            command.setScanLocCode(true);
            command.setScanContainerCode(true);
            command.setScanOutBound(true);
            command.setScanWaveCode(true);
            command.setScanOutBoundBox(true);
        }
        return command;
    }

    private Pagination<WhWorkCommand> compensateStep(String locCode, PickingWorkCommand command, Page page, Sort[] sorts, Map<String, Object> param) {
        Location location = this.whLocationDao.findLocationByCode(locCode, Long.parseLong(param.get("ouId").toString()));
        Integer pickSort = Integer.parseInt(location.getPickSort());
        Long workAreaId = location.getWorkAreaId();
        Long ouId = Long.parseLong(param.get("ouId").toString());
        Location newLocation = new Location();
        newLocation.setPickSort(String.valueOf((pickSort + 1)));
        newLocation.setWorkAreaId(workAreaId);
        newLocation.setOuId(ouId);
        Location loc = this.whLocationDao.findLocationByParam(newLocation);
        String newLocCode = loc.getCode();
        param.put("locCode", newLocCode);
        Pagination<WhWorkCommand> workList = this.privilegeControl(page, sorts, param);
        return workList;
    }

    private Pagination<WhWorkCommand> privilegeControl(Page page, Sort[] sorts, Map<String, Object> param) {
        return this.operPrivilegeManager.findWork(page, sorts, param);
    }
}
