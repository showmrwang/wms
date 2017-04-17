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

    /**
     * [业务方法]明细一览
     * 
     * @param page
     * @param sorts
     * @param params
     * @return
     */
    Pagination<WhAsnLineCommand> findListByQueryMapWithPageExtByShard(Page page, Sort[] sorts, Map<String, Object> params);

    /**
     * [通用方法，不推荐使用]
     * 
     * @param asnLine
     * @return
     */
    @Deprecated
    List<WhAsnLine> findListByShard(WhAsnLine asnLine);

    /**
     * [通用方法，不建议使用]
     * 
     * @param searchAsnLine
     * @return
     */
    @Deprecated
    long findListCountByParam(WhAsnLine searchAsnLine);


    /**
     * [通用方法]根据常用的参数查询WHASNLINE
     * 
     * @param asnLine
     * @return
     */
    List<WhAsnLine> findListByParamExt(WhAsnLine asnLine);

    /**
     * [通用方法]根据ID,OUID查询WHASNLINE
     * 
     * @param id
     * @param ouId
     * @return
     */
    WhAsnLineCommand findWhAsnLineCommandByIdToShard(Long id, Long ouId);

    /**
     * [通用方法]根据ID,OUID查询WHASNLINE
     * 
     * @param id
     * @param ouid
     * @return
     */
    WhAsnLine findWhAsnLineByIdToShard(Long id, Long ouid);

    /**
     * [通用方法]乐观锁更新数据
     * 
     * @param line
     * @return
     */
    void updateByVersion(WhAsnLine line);

    /**
     * [业务方法]编辑ASNLINE单明细
     * 
     * @param asn
     * @param asnLine
     * @param poline
     */
    void editAsnLineWhenPoToShard(WhAsn asn, WhAsnLine asnLine, WhPoLine poline);

    /**
     * [业务方法]删除ASNLINE明细
     * 
     * @param asnlineList
     * @param polineList
     * @param whAsn
     */
    void batchDeleteWhenPoToShard(List<WhAsnLine> asnlineList, List<WhPoLine> polineList, WhAsn whAsn);

    /**
     * [业务方法]通过ASNID+SKUID获取ASN明细可拆商品明细
     * 
     * @param asnid
     * @param ouid
     * @param skuid
     * @param id
     * @return
     */
    List<WhAsnLineCommand> findWhAsnLineCommandDevanningList(Long asnid, Long ouid, Long skuid, Long id);

    /**
     * [业务方法]获取ASN拆箱对应商品明细
     * 
     * @param whAsnLine
     * @return
     */
    WhAsnLineCommand findWhAsnLineCommandEditDevanning(WhAsnLineCommand whAsnLine);

    /**
     * [业务方法] 创建ASN单时候临时数据查询
     * 
     * @author yimin.lu
     * @param page
     * @param sorts
     * @param paraMap
     * @return
     */
    Pagination<WhAsnLineCommand> findAsnLineListByQueryMapWithPageExtForCreateAsn(Page page, Sort[] sorts, Map<String, Object> paraMap);

    /**
     * 【业务方法】根据id,uuid,ouid查询创建asn的临时whasnline
     *
     * @param id @required
     * @param uuid @required
     * @param ouId @required
     * @return
     */
    WhAsnLine findWhAsnLineByPoLineIdAndUuidAndOuId(Long id, String uuid, Long ouId);

    /**
     * [业务方法]根据ASNID,OUID查找某一UUID的ASNLINE临时数据
     * 
     * @param asnId @required
     * @param ouId @required
     * @param uuid @required
     * @return
     */
    List<WhAsnLine> findWhAsnLineByAsnIdOuIdUuid(Long asnId, Long ouId, String uuid);

    /**
     * [通用方法]根据ID,OUID删除数据
     * 
     * @param id @required
     * @param ouId @required
     * @return
     */
    void deleteWhAsnByIdOuIdToShard(Long id, Long ouId, Long userId);

    /**
     * [业务方法]根据ASNID,OUID查找[非某一UUID]的ASNLINE临时数据
     * 
     * @param asnId @required
     * @param ouId @required
     * @param uuid
     * @return
     */
    List<WhAsnLine> findTempWhAsnLineByAsnIdOuIdNotUuid(Long asnId, Long ouId, String uuid);

    /**
     * [通用方法]根据ASNID,OUID查找明细
     */
    List<WhAsnLine> findWhAsnLineByAsnIdOuIdToShard(Long asnId, Long ouId);

    List<WhAsnLineCommand> findWhAsnLineCommandListByAsnIdOuIdToShard(Long asnId, Long ouId);

    /**
     * TODO lei.zhang
     * @param occupationCode
     * @param skuCode
     * @param ouId
     * @return
     */
    boolean checkAsnSku(String occupationCode, String skuCode, Long ouId);

    /**
     * [通用方法]根据ASNID,POLINEID,OUID[,UUID]查找对应的明细行
     * 
     * @param asnId @required
     * @param poLineId @required
     * @param ouId @required
     * @param uuid
     * @return
     */
    WhAsnLine findWhAsnLineByAsnIdPolineIdOuIdAndUuid(Long asnId, Long poLineId, Long ouId, String uuid);

}
