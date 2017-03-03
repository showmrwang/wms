package com.baozun.scm.primservice.whoperation.manager.auth;

import java.util.Map;

import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baozun.scm.primservice.whoperation.command.warehouse.WhWorkCommand;
import com.baozun.scm.primservice.whoperation.dao.auth.OperPrivilegeDao;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;

@Service("operPrivilegeManager")
public class OperPrivilegeManagerImpl extends BaseManagerImpl implements OperPrivilegeManager {

    @Autowired
    private OperPrivilegeDao operPrivilegeDao;

    public Pagination<WhWorkCommand> findWork(Page page, Sort[] sorts, Map<String, Object> param) {
        Long userId = (Long) param.get("userId");
        Long ouId = (Long) param.get("ouId");
        param.put("category", "REPLENISHMENT");
        String workAreaIds = "(" + operPrivilegeDao.findworkAreaByUserAndOuId(userId, ouId, "REPLENISHMENT") + ")";
        param.put("workAreaIds", workAreaIds);
        Pagination<WhWorkCommand> commandList = this.operPrivilegeDao.findWorkListByQueryMapWithPage(page, sorts, param);
        return commandList;
    }

}
