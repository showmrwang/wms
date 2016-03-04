package com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager;

import java.util.List;
import java.util.Map;

import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnLineCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhPoCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.ResponseMsg;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsn;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPo;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPoLine;

public interface AsnManager extends BaseManager {

    List<WhAsnCommand> findWhAsnListByAsnCode(String asnCode, Integer status, Long ouid);

    int editAsnStatusByInfo(WhAsnCommand whAsn);

    int editAsnStatusByShard(WhAsnCommand whAsn);

    Pagination<WhAsnCommand> findListByQueryMapWithPageExtByInfo(Page page, Sort[] sorts, Map<String, Object> params);

    Pagination<WhAsnCommand> findListByQueryMapWithPageExtByShard(Page page, Sort[] sorts, Map<String, Object> params);

    ResponseMsg createAsnAndLineToShare(WhAsn whAsn, List<WhAsnLineCommand> asnLineList, WhPo whPo, Map<Long, WhPoLine> poLineMap, ResponseMsg rm);

    ResponseMsg insertAsnWithOuId(WhAsn whAsn, List<WhAsnLineCommand> asnLineList, WhPo whPo, Map<Long, WhPoLine> poLineMap, ResponseMsg rm);

    WhAsnCommand findWhAsnByIdToInfo(WhAsnCommand whAsn);

    WhAsnCommand findWhAsnByIdToShard(WhAsnCommand whAsn);

    void editAsnToInfo(WhAsn whasn);

    void editAsnToShard(WhAsn whasn);

    ResponseMsg createAsnBatch(WhAsnCommand asn, WhPo whpo, List<WhPoLine> whPoLines, ResponseMsg rm);

    List<WhAsn> findWhAsnByPoToShard(WhAsn whAsn);

    void deleteAsnAndAsnLineToShard(WhAsnCommand asn);

    void deleteAsnAndAsnLineWhenPoOuIdNullToShard(WhAsnCommand whAsnCommand);

    void deleteAsnAndAsnLineToShard(WhAsnCommand whAsnCommand, WhPoCommand whpo, List<WhPoLine> polineList);

}
