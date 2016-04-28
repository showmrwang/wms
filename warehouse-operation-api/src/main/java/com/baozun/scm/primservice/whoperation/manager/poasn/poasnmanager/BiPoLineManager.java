package com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager;

import java.util.Map;

import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import com.baozun.scm.primservice.whoperation.command.poasn.BiPoLineCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.poasn.BiPoLine;

public interface BiPoLineManager extends BaseManager {

    BiPoLine findPoLineByAddPoLineParam(BiPoLine line, boolean b);

    void createPoLineSingle(BiPoLine line);

    void updatePoLineSingle(BiPoLine wpl);

    Pagination<BiPoLineCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);

}
