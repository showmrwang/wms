package com.baozun.scm.primservice.whoperation.manager.poasn.poasnproxy;

import java.util.List;
import java.util.Map;

import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import com.baozun.scm.primservice.whoperation.command.collect.WhOdoArchivLineIndexCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.BiPoCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.BiPoLineCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnLineCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhPoCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhPoLineCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.poasn.BiPo;
import com.baozun.scm.primservice.whoperation.model.poasn.BiPoLine;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsn;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsnLine;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsnSn;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPo;

public interface SelectPoAsnManagerProxy extends BaseManager {
    /**
     * [业务方法]PO的分页查询
     * 
     * @param page
     * @param sorts
     * @param params
     * @param sourceType
     * @return
     */
    Pagination<WhPoCommand> findWhPoListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);

    /**
     * [业务方法]BiPO的分页查询
     * 
     * @param page
     * @param sorts
     * @param params
     * @param sourceType
     * @return
     */
    Pagination<BiPoCommand> findBiPoListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);

    /**
     * [业务方法]模糊查询方法。 根据asnExtCode,asn状态，仓库查找asn
     * 
     * @param asnExtCode
     * @param status
     * @param ouid
     * @return
     */
    List<WhAsnCommand> findWhAsnListByAsnExtCode(String asnExtCode, Integer[] status, Long ouid);

    /**
     * [通用方法]根据PO单ID，OUID查找PO
     * 
     * @param id
     * @param ouId
     * @return
     */
    WhPo findWhPoById(Long id, Long ouId);

    /**
     * [通用方法]根据PO单ID，OUID查找PO
     * 
     * @param whPoCommand
     * @return
     */
    WhPoCommand findWhPoCommandById(Long id, Long ouId);

    /**
     * [业务方法]PO单明细的分页查询
     * 
     * @param page
     * @param sorts
     * @param params
     * @param sourceType
     * @return
     */
    Pagination<WhPoLineCommand> findPoLineListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params, Integer sourceType);

    /**
     * [业务方法]BIPO单明细的分页查询
     * 
     * @param page
     * @param sorts
     * @param params
     * @param sourceType
     * @return
     */
    Pagination<BiPoLineCommand> findBiPoLineListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);

    /**
     * [通用方法]根据POLINE的ID,OUID查找PO单明细
     * 
     * @param Command
     * @return
     */
    WhPoLineCommand findWhPoLineCommandById(Long id, Long ouId);

    /**
     * [通用方法]根据WhAsnLine的ID,OUID查找PO单明细
     * 
     * @param Command
     * @return
     */
    WhAsnLineCommand findWhAsnLineCommandById(Long id, Long ouId);

    /**
     * [通用方法]根据WhAsnLine的ASNID,OUID查找ASN单明细
     * 
     * @param Command
     * @return
     */
    List<WhAsnLine> findWhAsnLineByAsnId(Long asnId, Long ouId);

    List<WhAsnLineCommand> findWhAsnLineCommandListByAsnId(Long asnId, Long ouId);

    /**
     * [业务方法]ASN的分页查询
     * 
     * @param page
     * @param sorts
     * @param params
     * @param sourceType
     * @return
     */
    Pagination<WhAsnCommand> findWhAsnListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params, Integer sourceType);

    /**
     * [业务方法]根据PO的EXTCODE,OUID,STATUSLIST模糊查找PO单明细
     * 
     * @param extCode
     * @param status
     * @param customerList
     * @param storeList
     * @param ouid
     * @param linenum
     * @return
     */
    List<WhPoCommand> findWhPoListByExtCode(String extCode, List<Integer> statusList, List<Long> customerList, List<Long> storeList, Long ouid, Integer linenum);

    /**
     * [通用方法]根据ASN的ID,OUID查找ASN
     * 
     * @param whAsnCommand
     * @return
     */
    WhAsnCommand findWhAsnCommandById(Long id, Long ouId);

    /**
     * [业务方法]ASNLINE的分页查询
     * 
     * @param page
     * @param sorts
     * @param params
     * @param sourceType
     * @return
     */
    Pagination<WhAsnLineCommand> findAsnLineListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params, Integer sourceType);

    /**
     * [通用方法]查找ASN中某个商品的数量
     * 
     * @param asnId
     * @param ouId
     * @param skuId
     * @return
     */
    long getSkuCountInAsnBySkuId(Long asnId, Long ouId, Long skuId);

    /**
     * [通用方法]根据ID查找BIPO
     * 
     * @param id
     * @return
     */
    BiPo findBiPoById(Long id);

    /**
     * [通用方法]根据POCODE查找BIPO
     * 
     * @param poCode
     * @return
     */
    BiPo findBiPoByPoCode(String poCode);

    /**
     * [通用方法]根据ID查找BIPO
     * 
     * @param id
     * @return
     */
    BiPoCommand findBiPoCommandById(Long id);

    /**
     * [通用方法]根据POCODE查找BIPO
     * 
     * @param poCode
     * @return
     */
    BiPoCommand findBiPoCommandByPoCode(String poCode);

    /**
     * [通用方法]根据ID查找BIPOLINE
     * 
     * @param id
     * @return
     */
    BiPoLineCommand findBiPoLineCommandById(Long id);

    /**
     * [通用方法]根据ID查找BIPOLINE
     * 
     * @param id
     * @return
     */
    BiPoLine findBiPoLineById(Long id);
    
    /**
     * [业务方法]PDA ASN预收货,返回预收货模式
     * 
     * @param whCommand
     * @return
     */
    Integer returnReceiptMode(WhAsnCommand whCommand);

    /**
     * [业务方法]分页查询可以拆分PO的明细行；可以：1.可用数量大于0；2.可用数量-已经拆分的数量>0
     * 
     * @param page
     * @param sorts
     * @param params
     * @return
     */
    Pagination<BiPoLineCommand> findListByQueryMapWithPageExtForCreateSubPo(Page page, Sort[] sorts, Map<String, Object> params);

    /**
     * [业务方法]拆分PO时uuid明细的分页查询
     * 
     * @param page
     * @param sorts
     * @param params
     * @param sourceType
     * @return
     */
    Pagination<WhPoLineCommand> findPoLineListByQueryMapWithPageExtForCreateSubPo(Page page, Sort[] sorts, Map<String, Object> paraMap, Integer infoSource);
    
    /**
     * [业务方法]创建Asn时Po明细的分页查询:可用明细数量>0
     *
     * @param page
     * @param sorts
     * @param params
     * @param sourceType
     * @return
     */
    Pagination<WhPoLineCommand> findPoLineListByQueryMapWithPageExtForCreateAsn(Page page, Sort[] sorts, Map<String, Object> paraMap, Integer infoSource);

    /**
     * [业务方法]校验此收货Asn是否存在该商品收货明细
     *
     * @param asnId
     * @param skuId
     * @param ouId
     * @param logId
     * @return
     */
    void checkWhAsnLineBySkuId(List<WhAsnLine> whAsnLineList, Long skuId, Long ouId, String logId);

    /**
     * [通用方法]根据ID和OUID查找WHASN
     * 
     * @param occupationId
     * @param ouId
     * @return
     */
    WhAsn findWhAsnById(Long occupationId, Long ouId);

    /**
     * [业务方法] 创建ASN的时候查询ASN单临时数据
     * 
     * @author yimin.lu
     * @param page
     * @param sorts
     * @param paraMap
     * @param shardSource
     * @return
     */
    Pagination<WhAsnLineCommand> findAsnLineListByQueryMapWithPageExtForCreateAsn(Page page, Sort[] sorts, Map<String, Object> paraMap, Integer shardSource);

    /**
     * [业务方法]根据指定ASN列表中处于某状态的ASN列表
     *
     * @author mingwei.xie
     * @param status
     * @param ouId
     * @return
     */
    List<WhAsnCommand> findAsnListByStatus(int status, Long ouId,List<Long> customerList,List<Long> storeList);

    /**
     * [通用方法]根据EXTCODE+STOREID查找BIPO
     * 
     * @param extCode
     * @param storeId
     * @return
     */
    BiPo findBiPoByExtCodeStoreId(String extCode, Long storeId);
    
    /**
     * [通用方法]根据EXTCODE+STOREID查找WHPO列表
     * 
     * @param extCode
     * @param storeId
     * @return
     */
    List<WhPoCommand> findPoListByExtCodeStoreId(String extCode, String storeId);

    List<BiPo> findBiPoByExtCode(String extPoCode);

    /**
     * [通用方法]根据外接行号查找入库单明细
     * 
     * @param id
     * @param extLinenum
     * @return
     */
    List<BiPoLine> findBiPoLineByBiPoIdAndLineNums(Long id, List<Integer> extLinenum);

    /**
     * [业务方法] 查找新建的，单据类型为消费者退货入库的ASN单据
     * 
     * @param originalEcOrderCode
     * @param ouId
     * @return
     */
    List<WhAsnCommand> findNewReturnsAsn(String originalEcOrderCode, Long ouId);

    /**
     * 
     * @param lineId
     * @param ouId
     * @return
     */
    List<WhAsnSn> findWhAsnSnByAsnLineId(Long lineId, Long ouId);

    /**
     * 查找ASN单对应的原始出库单归档数据
     * 
     * @param asnId
     * @param ouId
     * @return
     */
    List<WhOdoArchivLineIndexCommand> findOrginalByAsnId(Long asnId, Long ouId);

    /**
     * 操作台退换货数据查询
     * 
     * @param command
     * @return
     */
    List<WhAsnCommand> findReturnsForOp(WhAsnCommand command);

}
