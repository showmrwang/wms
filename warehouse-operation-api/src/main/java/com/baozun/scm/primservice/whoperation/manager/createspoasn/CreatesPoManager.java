package com.baozun.scm.primservice.whoperation.manager.createspoasn;

import java.util.Map;

import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import com.baozun.scm.primservice.whoperation.command.poasn.WhPoCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.ResponseMsg;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPo;

public interface CreatesPoManager extends BaseManager {

    Pagination<WhPoCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);

    ResponseMsg createPoAndLine(WhPoCommand po, ResponseMsg rm);

    WhPo findWhPoById(Long id);
}
