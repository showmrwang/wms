package com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager;

import java.util.Date;
import java.util.List;
import java.util.Map;

import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnLineCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.ResponseMsg;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsn;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPo;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPoLine;

public interface AsnManager extends BaseManager {

    /**
     * ASN模糊查询使用
     * 
     * @param asnCode
     * @param status
     * @param ouid
     * @return
     */
    List<WhAsnCommand> findWhAsnListByAsnExtCode(String asnCode, Integer[] status, Long ouid);

    /**
     * 编辑ASN状态
     * 
     * @param whAsn
     */
    void editAsnStatusByInfo(WhAsnCommand whAsn);

    void editAsnStatusByShard(WhAsnCommand whAsn);

    /**
     * ASN分页查询
     * 
     * @param page
     * @param sorts
     * @param params
     * @return
     */
    Pagination<WhAsnCommand> findListByQueryMapWithPageExtByInfo(Page page, Sort[] sorts, Map<String, Object> params);

    Pagination<WhAsnCommand> findListByQueryMapWithPageExtByShard(Page page, Sort[] sorts, Map<String, Object> params);

    ResponseMsg createAsnAndLineToShare(WhAsn whAsn, List<WhAsnLineCommand> asnLineList, WhPo whPo, Map<Long, WhPoLine> poLineMap, ResponseMsg rm);

    ResponseMsg insertAsnWithOuId(WhAsn whAsn, List<WhAsnLineCommand> asnLineList, WhPo whPo, Map<Long, WhPoLine> poLineMap, ResponseMsg rm);

    WhAsnCommand findWhAsnByIdToShard(WhAsnCommand whAsn);

    void editAsnToInfo(WhAsn whasn);

    void editAsnToShard(WhAsn whasn);

    ResponseMsg createAsnBatch(WhAsnCommand asn, WhPo whpo, List<WhPoLine> whPoLines, ResponseMsg rm);

    List<WhAsn> findWhAsnByPoToShard(WhAsn whAsn);

    ResponseMsg deleteAsnAndAsnLineToShard(WhAsnCommand asn);

    void deleteAsnAndAsnLineWhenPoOuIdNullToShard(WhAsnCommand whAsnCommand);

    void deleteAsnAndAsnLineToShard(WhAsnCommand whAsnCommand, WhPo whpo, List<WhPoLine> polineList);

    WhAsn getAsnByAsnExtCode(String asnExtCoce, Long ouId);

    /**
     * 根据ID和OUID查找WHASN
     * 
     * @param id
     * @param ouId
     * @return
     */
    WhAsn findWhAsnByIdToShard(Long id, Long ouId);

    /**
     * 缓存锁使用
     * 
     * @param id
     * @param ouid
     * @param lastModifyTime
     * @return
     */
    int updateByVersionForLock(Long id, Long ouid, Date lastModifyTime);

    /**
     * 释放缓存锁使用
     * 
     * @param id
     * @param ouid
     * @param lastModifyTime
     * @return
     */
    int updateByVersionForUnLock(Long id, Long ouid);


}
