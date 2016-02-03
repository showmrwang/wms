package com.baozun.scm.primservice.whoperation.manager.poasn.poasnproxy;

import java.util.List;
import java.util.Map;

import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhPoCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhPoLineCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;

public interface SelectPoAsnManagerProxy extends BaseManager {

    Pagination<WhPoCommand> findWhPoListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params, Integer sourceType);

    List<WhAsnCommand> findWhAsnListByAsnCode(String asnCode, Integer status, Long ouid);

    WhPoCommand findWhPoById(WhPoCommand whPoCommand);

    Pagination<WhPoLineCommand> findPoLineListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params, Integer sourceType);

    WhPoLineCommand findWhPoLineById(WhPoLineCommand Command);

    Pagination<WhAsnCommand> findWhAsnListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params, Integer sourceType);

    List<WhPoCommand> findWhPoListByPoCode(String poCode, List<Integer> status, Long ouid);

    String getAsnExtCode();
}
