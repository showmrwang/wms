package com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager;

import java.util.List;
import java.util.Map;

import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import com.baozun.scm.primservice.whoperation.command.poasn.PoCheckCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnLineCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhPoCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.ResponseMsg;
import com.baozun.scm.primservice.whoperation.model.poasn.CheckPoCode;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsn;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPo;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPoLine;

public interface PoManager extends BaseManager {

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

    List<WhPoCommand> findWhPoListByExtCodeToInfo(String poCode, List<Integer> status, Long ouid);

    List<WhPoCommand> findWhPoListByExtCodeToShard(String poCode, List<Integer> status, Long ouid);

    WhPo findWhAsnByIdToInfo(Long id, Long ouid);

    WhPo findWhAsnByIdToShard(Long id, Long ouid);

    void deletePoAndPoLineToInfo(List<WhPoCommand> whPoCommand);

    void deletePoAndPoLineToShard(List<WhPoCommand> whPoCommand);

    ResponseMsg updatePoStatusByAsn(WhAsn asn, List<WhAsnLineCommand> asnLineList, WhPo whPo, Map<Long, WhPoLine> poLineMap, ResponseMsg rm);

    ResponseMsg updatePoStatusByAsnBatch(WhAsnCommand asn, WhPo whPo, List<WhPoLine> whPoLines, ResponseMsg rm);

    void deleteCheckPoCodeToInfo(List<CheckPoCode> poCodeList);

    void saveOrUpdateByVersionToInfo(WhPo o);

    void saveOrUpdateByVersionToShard(WhPo o);

    void cancelPoToInfo(List<WhPo> poList);

    void cancelPoToShard(List<WhPo> poList);

    void editPoAdnPoLineWhenDeleteAsnToInfo(WhPoCommand whpo, List<WhPoLine> polineList);

}
