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

    WhAsnLine findWhAsnLineByIdToShard(Long id, Long ouid);

    int updateByVersion(WhAsnLine line);

    /**
     * 
     * @param searchAsnLine
     * @return
     */
    long findListCountByParam(WhAsnLine searchAsnLine);

    /**
     * @author yimin.lu 创建ASN单时候临时数据查询
     * @param page
     * @param sorts
     * @param paraMap
     * @return
     */
    Pagination<WhAsnLineCommand> findAsnLineListByQueryMapWithPageExtForCreateAsn(Page page, Sort[] sorts, Map<String, Object> paraMap);

    /**
     * 根据id,uuid,ouid查询数据【业务方法；查询创建asn的临时数据】
     *
     * @param id
     * @param uuid
     * @param ouId
     * @return
     */
    WhAsnLine findWhAsnLineByPoLineIdAndUuidAndOuId(Long id, String uuid, Long ouId);

    /**
     * 查找临时的ASN明细数据
     * 
     * @param asnId
     * @param ouId
     * @param uuid
     * @return
     */
    List<WhAsnLine> findWhAsnLineByAsnIdOuIdUuid(Long asnId, Long ouId, String uuid);

}
