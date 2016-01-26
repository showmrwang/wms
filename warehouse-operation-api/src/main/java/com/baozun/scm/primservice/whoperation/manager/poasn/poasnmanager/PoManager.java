package com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager;

import java.util.List;
import java.util.Map;

import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import com.baozun.scm.primservice.whoperation.command.poasn.WhPoCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.ResponseMsg;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPo;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPoLine;

public interface PoManager extends BaseManager {

    ResponseMsg createPoAndLine(WhPo po, List<WhPoLine> whPoLines, ResponseMsg rm);

    ResponseMsg createPoAndLineToInfo(WhPo po, List<WhPoLine> whPoLines, ResponseMsg rm);

    ResponseMsg createPoAndLineToShare(WhPo po, List<WhPoLine> whPoLines, ResponseMsg rm);

    WhPo findWhPoByIdByInfo(WhPoCommand whPo);

    WhPo findWhPoByIdByShard(WhPoCommand whPo);

    Pagination<WhPoCommand> findListByQueryMapWithPageExtByInfo(Page page, Sort[] sorts, Map<String, Object> params);

    Pagination<WhPoCommand> findListByQueryMapWithPageExtByShard(Page page, Sort[] sorts, Map<String, Object> params);

    int editPoStatusByInfo(WhPoCommand whPo);

    int editPoStatusByShard(WhPoCommand whPo);

    ResponseMsg insertPoByPoAndStore(String poCode, Long storeId);

    ResponseMsg insertPoByPoAndStore(String poCode, Long storeId, Long ouId);

    void editPoByInfo(WhPo whPo);

    void editPoByShard(WhPo whPo);

    void createPoLineSingleToInfo(WhPoLine whPoLine);

    void createPoLineSingleToShare(WhPoLine whPoLine);

}
