package com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager;

import java.util.List;
import java.util.Map;

import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import com.baozun.scm.primservice.whoperation.command.poasn.WhPoLineCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPoLine;

public interface PoLineManager extends BaseManager {
    /**
     * [通用方法]插入INFO.WHPOLINE
     * 
     * @param whPoLine
     */
    void insertToInfo(WhPoLine whPoLine);

    /**
     * [通用方法]插入SHARD.WHPOLINE
     * 
     * @param whPoLine
     */
    void insertToShard(WhPoLine whPoLine);

    Pagination<WhPoLineCommand> findListByQueryMapWithPageExtByInfo(Page page, Sort[] sorts, Map<String, Object> params);

    Pagination<WhPoLineCommand> findListByQueryMapWithPageExtByShard(Page page, Sort[] sorts, Map<String, Object> params);

    void deletePoLineByUuidToInfo(WhPoLineCommand WhPoLine);

    void deletePoLineByUuidToShard(WhPoLineCommand WhPoLine);

    void deletePoLineByUuidNotNullToInfo(Long poid, Long ouid);

    void deletePoLineByUuidNotNullToShard(Long poid, Long ouid);

    void editPoLineToInfo(WhPoLine whPoLine);

    void editPoLineToShare(WhPoLine whPoLine);

    WhPoLineCommand findPoLineCommandbyIdToInfo(Long id, Long ouId);

    WhPoLineCommand findPoLineCommandbyIdToShard(Long id, Long ouId);

    WhPoLine findPoLineByAddPoLineParamToInfo(WhPoLine line, Boolean type);

    WhPoLine findPoLineByAddPoLineParamToShare(WhPoLine line, Boolean type);

    List<WhPoLine> findWhPoLineListByPoIdToInfo(Long poid, Long ouid);

    List<WhPoLine> findWhPoLineListByPoIdToShard(Long poid, Long ouid);

    /**
     * [通用方法]乐观锁更新INFO.WHPOLINE
     * 
     * @param o
     */
    void saveOrUpdateByVersionToInfo(WhPoLine o);

    /**
     * [通用方法]乐观锁更新SHARD.WHPOLINE
     * 
     * @param o
     */
    void saveOrUpdateByVersionToShard(WhPoLine o);

    void deletePoLinesToInfo(List<WhPoLine> lineList);

    void deletePoLinesToShard(List<WhPoLine> lineList);

    void batchUpdatePoLine(List<WhPoLine> polineList);

    /**
     * 新的保存整单的逻辑分支：将PO单整单数据保存到仓库
     * 
     * @param extCode
     * @param storeId
     * @param ouId
     * @param infoPolineList
     */
    void createPoLineBatchToShareNew(String extCode, Long storeId, Long ouId, List<WhPoLine> infoPolineList);

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

    List<WhPoLine> findWhPoLineByPoIdOuIdUuIdToInfo(Long poid, Long ouId, String uuid);

    void deletePoLineByPoIdOuIdAndUuidNotNullNotEqual(Long poid, Long ouId, String uuid);

    /**
     * 拆分PO时uuid明细的分页查询
     * 
     * @param page
     * @param sorts
     * @param params
     * @return
     */
    Pagination<WhPoLineCommand> findPoLineListByQueryMapWithPageExtForCreateSubPoToInfo(Page page, Sort[] sorts, Map<String, Object> paraMap);

    /**
     * 业务方法：创建ASN时候分页查询
     *
     * @param page
     * @param sorts
     * @param params
     * @return
     */
    Pagination<WhPoLineCommand> findPoLineListByQueryMapWithPageExtForCreateAsnToShard(Page page, Sort[] sorts, Map<String, Object> paraMap);

    /**
     * 查找PO单指定状态下的明细
     * 
     * @param id
     * @param ouId
     * @param integers
     * @return
     */
    List<WhPoLine> findWhPoLineListByPoIdOuIdStatusListToInfo(Long poId, Long ouId, List<Integer> statusList);

    /**
     * [通用方法]根据ID,OUID查找SHARD.WHPOLINE
     * 
     * @param id
     * @param ouId
     * @return
     */
    WhPoLine findWhPoLineByIdOuIdToShard(Long id, Long ouId);

    /**
     * [通用方法]根据ID,OUID查找INFO.WHPOLINE
     * 
     * @param id
     * @param ouId
     * @return
     */
    WhPoLine findWhPoLineByIdOuIdToInfo(Long id, Long ouId);

    /**
     * 
     * @param searchPoLine
     * @return
     */
    long findListCountByParamToShard(WhPoLine searchPoLine);

    List<WhPoLine> findWhPoLineByPoIdOuIdWhereHasAvailableQtyToShard(Long id, Long ouId);

    /**
     * 
     * @param extCode @required
     * @param storeId @required
     * @param ouId @required
     * @param statusList @required
     * @return
     */
    List<WhPoLine> findInfoPoLineByExtCodeStoreIdOuIdStatusToInfo(String extCode, Long storeId, Long ouId, List<Integer> statusList);


}
