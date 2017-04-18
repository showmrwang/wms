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
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsnLine;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPo;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPoLine;

public interface AsnManager extends BaseManager {

    /**
     * [业务方法]ASN模糊查询使用:ASNEXTCODE模糊查询仓库下处于某个些状态的ASN单
     * 
     * @param asnCode @required
     * @param status @required
     * @param ouid @required
     * @return
     */
    List<WhAsnCommand> findWhAsnListByAsnExtCode(String asnCode, Integer[] status, Long ouid);

    /**
     * [通用方法]编辑ASN状态
     * 
     * @param asnIds @required
     * @param ouId @required
     * @param status @required
     * @param userId @required
     */
    void editAsnStatusByShard(List<Long> asnIds, Long ouId, Integer status, Long userId);

    /**
     * [业务方法]ASN分页查询
     * 
     * @param page
     * @param sorts
     * @param params
     * @return
     */
    Pagination<WhAsnCommand> findListByQueryMapWithPageExtByShard(Page page, Sort[] sorts, Map<String, Object> params);

    /**
     * [业务方法]
     * 
     * @param whAsn
     * @param asnLineList
     * @param whPo
     * @param poLineMap
     * @param rm
     * @return
     */
    ResponseMsg createAsnAndLineToShare(WhAsn whAsn, List<WhAsnLineCommand> asnLineList, WhPo whPo, Map<Long, WhPoLine> poLineMap, ResponseMsg rm);

    /**
     * [业务方法]
     * 
     * @param whAsn
     * @param asnLineList
     * @param whPo
     * @param poLineMap
     * @param rm
     * @return
     */
    ResponseMsg insertAsnWithOuId(WhAsn whAsn, List<WhAsnLineCommand> asnLineList, WhPo whPo, Map<Long, WhPoLine> poLineMap, ResponseMsg rm);

    /**
     * [通用方法]乐观锁更新ASN数据
     * 
     * @param whasn
     */
    void saveOrUpdateByVersionToShard(WhAsn whasn);

    /**
     * [业务方法]一键创建ASN
     * 
     * @param asn
     * @param whpo
     * @param whPoLines
     */
    Long createAsnBatch(WhAsnCommand asn, WhPo whpo, List<WhPoLine> whPoLines);

    /**
     * [通用方法]根据poId,ouId查询asn
     * 
     * @param poId
     * @param ouId
     * @return
     */
    List<WhAsn> findWhAsnByPoIdOuIdToShard(Long poId, Long ouId);

    /**
     * [业务方法]删除ASN和ASNLINE
     * 
     * @param whAsnCommand
     * @param whpo
     * @param polineList
     */
    void deleteAsnAndAsnLineToShard(WhAsnCommand whAsnCommand, WhPo whpo, List<WhPoLine> polineList);

    /**
     * [通用方法]根据ID和OUID查找WHASN
     * 
     * @param id
     * @param ouId
     * @return
     */
    WhAsn findWhAsnByIdToShard(Long id, Long ouId);

    /**
     * [通用方法]根据ID和OUID查找WHASN
     * 
     * @param id
     * @param ouId
     * @return
     */
    WhAsnCommand findWhAsnCommandByIdToShard(Long id, Long ouId);

    /**
     * 缓存锁使用【业务方法】
     * 
     * @param id
     * @param ouid
     * @param lastModifyTime
     * @return
     */
    int updateByVersionForLock(Long id, Long ouid, Date lastModifyTime);

    /**
     * 释放缓存锁使用【业务方法】
     * 
     * @param id
     * @param ouid
     * @param lastModifyTime
     * @return
     */
    int updateByVersionForUnLock(Long id, Long ouid);

    /**
     * 查询【通用方法】
     *
     * @param asn
     * @return
     */
    List<WhAsnCommand> findListByParamsExt(WhAsnCommand asn);

    /**
     * 查询【通用方法】
     *
     * @param checkAsn
     */
    long findListCountByParamsExt(WhAsnCommand checkAsn);

    /**
     * WMS系统创建Asn分支一:创建带UUID的Asn【业务方法】
     *
     * @param asn
     * @param lineList
     */
    void createAsnAndLineWithUuidToShard(WhAsn asn, List<WhAsnLine> lineList);

    /**
     * WMS系统创建Asn分支一：撤销带UUID的Asn
     *
     * @param asn
     * @param lineList
     */
    void revokeAsnWithUuidToShard(WhAsnCommand command);

    /**
     * WMS系统创建Asn分支一：保存带UUID的Asn
     * 
     * @param asn
     * @param saveAsnLineList
     * @param delAsnLineList
     * @param po
     * @param savePoLineList
     */
    void saveTempAsnWithUuidToShard(WhAsn asn, List<WhAsnLine> saveAsnLineList, List<WhAsnLine> delAsnLineList, WhPo po, List<WhPoLine> savePoLineList);
    
    /**
     * 根据客户id集合，店铺id集合查询asn信息
     * @param customerList
     * @param storeList
     * @return
     */
    public List<Long> getWhAsnCommandByCustomerId(List<Long> customerList,List<Long> storeList);

    /**
     * [业务方法]根据POID,OUID查找uuid不为空【且uuid不为此uuid】的ASN
     */
    WhAsn findTempAsnByPoIdOuIdAndLineNotUuid(Long poId, Long ouId, String uuid);

    /**
     * [业务方法]根据POID,OUID查找UUID不为空【且uuid等与此uuid】的asn
     * 
     * @param poId @required
     * @param ouId @required
     * @param uuid
     * @return
     */
    WhAsn findTempAsnByPoIdOuIdUuid(Long poId, Long ouId, String uuid);

    /**
     * [业务方法]删除ASN和ASNLINE
     * 
     * @param poId
     * @param ouId
     * @param uuid
     */
    void deleteAsnAndLine(WhAsn asn, List<WhAsnLine> lineList);

    /**
     * [业务方法]创建ASN
     * 
     * @param asn
     * @param asnLineList
     */
    void createAsn(WhAsn asn, List<WhAsnLineCommand> asnLineList);

    /**
     * [业务方法]查询仓库下指定ASN列表中处于某种状态的ASN单集合
     * 
     * @param status
     * @param ouId
     * @param asnList
     * @return
     */
    List<WhAsnCommand> findAsnListByStatus(int status, Long ouId,  List<Long> customerList,List<Long> storeList);

    /**
     * 审核关闭ASN单据
     * 
     * @param id
     * @param ouId
     * @param userId
     */
    void closeAsn(Long id, Long ouId, Long userId);
    
    /**
     * [业务方法]外部单据指定仓库创建ASN
     * 
     * @param asn
     * @param whpo
     * @param whPoLines
     */
	void createAsnByVmi(WhPo po, List<WhPoLine> whPoLines);

    /**
     * 查询退换货数据
     * 
     * @param command
     * @return
     */
    List<WhAsnCommand> findReturns(WhAsnCommand command);

}
