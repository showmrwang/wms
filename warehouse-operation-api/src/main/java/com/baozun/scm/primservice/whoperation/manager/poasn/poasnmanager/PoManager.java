package com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager;

import java.util.List;
import java.util.Map;

import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import com.baozun.scm.primservice.whoperation.command.poasn.PoCheckCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhPoCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.ResponseMsg;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPo;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPoLine;

public interface PoManager extends BaseManager {

    ResponseMsg createPoAndLine(WhPo po, List<WhPoLine> whPoLines, ResponseMsg rm);

    ResponseMsg createPoAndLineToShare(WhPo po, List<WhPoLine> whPoLines, ResponseMsg rm);

    WhPoCommand findWhPoByIdToInfo(WhPoCommand whPo);

    WhPoCommand findWhPoByIdToShard(WhPoCommand whPo);

    Pagination<WhPoCommand> findListByQueryMapWithPageExtByInfo(Page page, Sort[] sorts, Map<String, Object> params);

    Pagination<WhPoCommand> findListByQueryMapWithPageExtByShard(Page page, Sort[] sorts, Map<String, Object> params);

    int editPoStatusToInfo(WhPoCommand whPo);

    int editPoStatusToShard(WhPoCommand whPo);

    void editPoToInfo(WhPo whPo);

    void editPoToShard(WhPo whPo);

    ResponseMsg insertPoWithOuId(PoCheckCommand poCheckCommand);

    List<WhPoCommand> findWhPoListByPoCodeToInfo(String poCode, List<Integer> status, Long ouid);

    List<WhPoCommand> findWhPoListByPoCodeToShard(String poCode, List<Integer> status, Long ouid);

}
