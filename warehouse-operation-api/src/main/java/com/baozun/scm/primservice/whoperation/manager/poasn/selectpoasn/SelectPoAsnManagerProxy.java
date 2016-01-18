package com.baozun.scm.primservice.whoperation.manager.poasn.selectpoasn;

import java.util.List;
import java.util.Map;

import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhPoCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;

public interface SelectPoAsnManagerProxy extends BaseManager {

    Pagination<WhPoCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params, Integer sourceType);
    
    List<WhAsnCommand> findWhAsnListByAsnCode(String asnCode, Integer status, Long ouid);
}
