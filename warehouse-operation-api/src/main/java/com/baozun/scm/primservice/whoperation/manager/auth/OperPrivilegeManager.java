package com.baozun.scm.primservice.whoperation.manager.auth;

import java.util.Map;

import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import com.baozun.scm.primservice.whoperation.command.warehouse.WhWorkCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;

public interface OperPrivilegeManager extends BaseManager {
    public Pagination<WhWorkCommand> findWork(Page page, Sort[] sorts, Map<String, Object> param);

}
