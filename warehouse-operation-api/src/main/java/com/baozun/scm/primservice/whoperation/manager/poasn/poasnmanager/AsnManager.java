package com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager;

import java.util.List;
import java.util.Map;

import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import com.baozun.scm.primservice.whoperation.command.poasn.AsnCheckCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnLineCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.ResponseMsg;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsn;

public interface AsnManager extends BaseManager {

    List<WhAsnCommand> findWhAsnListByAsnCode(String asnCode, Integer status, Long ouid);

    int editAsnStatusByInfo(WhAsnCommand whAsn);

    int editAsnStatusByShard(WhAsnCommand whAsn);

    Pagination<WhAsnCommand> findListByQueryMapWithPageExtByInfo(Page page, Sort[] sorts, Map<String, Object> params);

    Pagination<WhAsnCommand> findListByQueryMapWithPageExtByShard(Page page, Sort[] sorts, Map<String, Object> params);

    ResponseMsg createAsnAndLineToShare(WhAsn whAsn, List<WhAsnLineCommand> asnLineList, ResponseMsg rm);

    ResponseMsg insertAsnWithOuId(AsnCheckCommand asnCheckCommand);

    WhAsnCommand findWhAsnByIdToInfo(WhAsnCommand whAsn);

    WhAsnCommand findWhAsnByIdToShard(WhAsnCommand whAsn);

    void editAsnToInfo(WhAsn whasn);

    void editAsnToShard(WhAsn whasn);

}
