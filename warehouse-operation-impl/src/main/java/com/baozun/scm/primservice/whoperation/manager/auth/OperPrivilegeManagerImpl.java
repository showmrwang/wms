package com.baozun.scm.primservice.whoperation.manager.auth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baozun.scm.primservice.whoperation.command.warehouse.WhWorkCommand;
import com.baozun.scm.primservice.whoperation.dao.auth.OperPrivilegeDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.AreaDao;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;

@Service("operPrivilegeManager")
public class OperPrivilegeManagerImpl extends BaseManagerImpl implements OperPrivilegeManager {

    @Autowired
    private OperPrivilegeDao operPrivilegeDao;

    @Autowired
    private AreaDao areaDao;

    public Pagination<WhWorkCommand> findWork(Page page, Sort[] sorts, Map<String, Object> param) {
        Long userId = (Long) param.get("userId");
        Long ouId = (Long) param.get("ouId");
        // param.put("category", "REPLENISHMENT");
        String option = (String) param.get("category");
        String workAreaIds = "(" + operPrivilegeDao.findworkAreaByUserAndOuId(userId, ouId, option) + ")";
        param.put("maxObtainWorkQty", 6);
        param.put("workAreaIds", workAreaIds);
        Pagination<WhWorkCommand> commandList = this.operPrivilegeDao.findWorkListByQueryMapWithPage(page, sorts, param);
        if (null != commandList && null != commandList.getItems() && !commandList.getItems().isEmpty()) {
            commandList = extractWorkByWorkArea(commandList, workAreaIds);
        }
        return commandList;
    }

    private Pagination<WhWorkCommand> extractWorkByWorkArea(Pagination<WhWorkCommand> commandList, String workAreaIds) {
        List<WhWorkCommand> workList = commandList.getItems();
        // List<WhWorkCommand> newWorkList = new ArrayList<WhWorkCommand>();
        Map<String, WhWorkCommand> workMap = new HashMap<String, WhWorkCommand>();
        Set<String> workSet = new HashSet<String>();
        for (WhWorkCommand work : workList) {
            if (workSet.add(work.getCode())) {
                // 工作没有重复
                workMap.put(work.getCode(), work);
            } else {
                // 拼接工作区域
                WhWorkCommand workCommand = workMap.get(work.getCode());
                // workCommand.setWorkArea(workCommand.getWorkArea() + "," +
                // work.getWorkArea());
                workCommand.setWorkArea("跨多区域");
                workMap.put(work.getCode(), workCommand);
            }
        }
        List<WhWorkCommand> newWorkList = new ArrayList<WhWorkCommand>(workMap.values());
        commandList.setItems(newWorkList);
        return commandList;
    }
}
