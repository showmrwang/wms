package com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager;

import java.util.List;
import java.util.Map;

import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import com.baozun.scm.primservice.whoperation.command.poasn.BiPoLineCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhPoLineCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPoLine;

public interface PoLineManager extends BaseManager {

    void createPoLineSingleToInfo(WhPoLine whPoLine);

    void createPoLineSingleToShare(WhPoLine whPoLine);

    Pagination<WhPoLineCommand> findListByQueryMapWithPageExtByInfo(Page page, Sort[] sorts, Map<String, Object> params);

    Pagination<WhPoLineCommand> findListByQueryMapWithPageExtByShard(Page page, Sort[] sorts, Map<String, Object> params);

    void deletePoLineByUuidToInfo(WhPoLineCommand WhPoLine);

    void deletePoLineByUuidToShard(WhPoLineCommand WhPoLine);

    void deletePoLineByUuidNotNullToInfo(Long poid, Long ouid);

    void deletePoLineByUuidNotNullToShard(Long poid, Long ouid);

    void editPoLineToInfo(WhPoLine whPoLine);

    void editPoLineToShare(WhPoLine whPoLine);

    WhPoLineCommand findPoLinebyIdToInfo(WhPoLineCommand command);

    WhPoLineCommand findPoLinebyIdToShard(WhPoLineCommand command);

    int editPoLineStatusToInfo(WhPoLineCommand command);

    int editPoLineStatusToShard(WhPoLineCommand command);

    WhPoLine findPoLineByAddPoLineParamToInfo(WhPoLine line, Boolean type);

    WhPoLine findPoLineByAddPoLineParamToShare(WhPoLine line, Boolean type);

    void updatePoLineSingleToInfo(WhPoLine whPoLine);

    void updatePoLineSingleToShare(WhPoLine whPoLine);

    /**
     * @deprecated
     * @param whPoLine
     */
    void createPoLineBatchToInfo(WhPoLineCommand whPoLine);

    void createPoLineBatchToShare(WhPoLineCommand whPoLine);

    List<WhPoLine> findWhPoLineListByPoIdToInfo(Long poid, Long ouid);

    List<WhPoLine> findWhPoLineListByPoIdToShard(Long poid, Long ouid);

    void saveOrUpdateByVersionToInfo(WhPoLine o);

    void saveOrUpdateByVersionToShard(WhPoLine o);

    int deletePoLinesToInfo(List<WhPoLine> lineList);

    int deletePoLinesToShard(List<WhPoLine> lineList);

    void batchUpdatePoLine(List<WhPoLine> polineList);

    /**
     * 同一个仓库【或OUID】下 POCODE唯一
     * 
     * @param poCode
     * @param ouId
     * @return
     */
    List<WhPoLine> findInfoPoLineByPoCodeOuId(String poCode, Long ouId);

    /**
     * 新的保存整单的逻辑分支：将PO单整单数据保存到仓库
     * 
     * @param biPoLineCommand
     * @param infoPolineList
     */
    void createPoLineBatchToShareNew(BiPoLineCommand biPoLineCommand, List<WhPoLine> infoPolineList);

    /**
     * wh_po_line状态： 1。同一个polineId，poId,ouId下每个状态最多只有一条 2.【新建、已创建ASN--收货中】、【取消】、【关闭】，每组状态只有一条
     * 
     * @param poLineId
     * @param statusList
     * @param poId
     * @param ouId
     * @return
     */
    WhPoLine findPoLineByPolineIdAndStatusListAndPoIdAndOuIdToShared(Long poLineId, List<Integer> statusList, Long poId, Long ouId);
    WhPoLine findPoLineByPolineIdAndStatusListAndPoIdAndOuIdToInfo(Long poLineId, List<Integer> statusList, Long poId, Long ouId, String uuid, boolean uuidFlag);

    List<WhPoLine> findWhPoLineByPoIdOuIdUuIdToInfo(Long id, Long ouId, String uuid);

    void deletePoLineByPoIdOuIdAndUuidNotNullNotEqual(Long id, Long ouId, String uuid);

    /**
     * 拆分PO时uuid明细的分页查询
     * 
     * @param page
     * @param sorts
     * @param params
     * @return
     */
    Pagination<WhPoLineCommand> findPoLineListByQueryMapWithPageExtForCreateSubPoToInfo(Page page, Sort[] sorts, Map<String, Object> paraMap);


}
