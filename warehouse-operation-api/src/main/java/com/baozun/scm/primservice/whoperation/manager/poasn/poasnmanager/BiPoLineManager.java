package com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager;

import java.util.List;
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

    void createPoLineBatchToInfo(BiPoLineCommand biPoLineCommand);

    BiPoLine findBiPoLineById(Long id);

    BiPoLineCommand findBiPoLineCommandById(Long id);

    /**
     * BIPO明细界面编辑BIPO明细
     * 
     * @param biPoLine
     */
    void editBiPoLineSingle(BiPoLine biPoLine);

    /**
     * 
     * @param poId
     * @param uuid
     * @param userId
     * @return
     */
    void deleteBiPoLineByPoIdAndUuidToInfo(Long poId, String uuid, Long userId);

    /**
     * 可以拆分PO单的明细分页
     * 
     * @param page
     * @param sorts
     * @param params
     * @return
     */
    Pagination<BiPoLineCommand> findListByQueryMapWithPageExtForCreateSubPo(Page page, Sort[] sorts, Map<String, Object> params);

    /**
     * [业务方法]删除BIPOLINE集合
     * 
     * @param lineList
     * @return
     */
    void deleteList(List<BiPoLine> lineList);

}
