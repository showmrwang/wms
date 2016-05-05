package com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager;

import java.util.List;
import java.util.Map;

import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnLineCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsn;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsnLine;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPoLine;

public interface AsnLineManager extends BaseManager {

    Pagination<WhAsnLineCommand> findListByQueryMapWithPageExtByShard(Page page, Sort[] sorts, Map<String, Object> params);

    List<WhAsnLine> findListByShard(WhAsnLine asnLine);

    WhAsnLineCommand findWhAsnLineByIdToShard(WhAsnLineCommand command);

    void editAsnLineToShard(WhAsn asn, WhAsnLine asnLine);

    void editAsnLineWhenPoToShard(WhAsn asn, WhAsnLine asnLine, WhPoLine poline);

    void batchDeleteWhenPoToInfo(List<WhAsnLine> asnlineList, WhAsn whAsn);

    void batchDeleteWhenPoToShard(List<WhAsnLine> asnlineList, List<WhPoLine> polineList, WhAsn whAsn);

    List<WhAsnLineCommand> findWhAsnLineCommandDevanningList(Long asnid, Long ouid, Long skuid, Long id);

    WhAsnLineCommand findWhAsnLineCommandEditDevanning(WhAsnLineCommand whAsnLine);

    WhAsnLineCommand findWhAsnLineById(Long id, Long ouid);

}
