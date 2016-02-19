package com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager;

import java.util.Map;

import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnLineCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;

public interface AsnLineManager extends BaseManager {

    Pagination<WhAsnLineCommand> findListByQueryMapWithPageExtByShard(Page page, Sort[] sorts, Map<String, Object> params);

}
