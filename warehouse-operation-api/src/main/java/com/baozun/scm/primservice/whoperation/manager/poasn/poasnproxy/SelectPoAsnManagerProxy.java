package com.baozun.scm.primservice.whoperation.manager.poasn.poasnproxy;

import java.util.List;
import java.util.Map;

import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

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
import com.baozun.scm.primservice.whoperation.model.poasn.WhPo;

public interface SelectPoAsnManagerProxy extends BaseManager {
    /**
     * PO的分页查询
     * 
     * @param page
     * @param sorts
     * @param params
     * @param sourceType
     * @return
     */
    Pagination<WhPoCommand> findWhPoListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params, Integer sourceType);

    /**
     * BiPO的分页查询
     * 
     * @param page
     * @param sorts
     * @param params
     * @param sourceType
     * @return
     */
    Pagination<BiPoCommand> findBiPoListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);

    /**
     * 模糊查询方法。 根据asnExtCode,asn状态，仓库查找asn
     * 
     * @param asnExtCode
     * @param status
     * @param ouid
     * @return
     */
    List<WhAsnCommand> findWhAsnListByAsnExtCode(String asnExtCode, Integer[] status, Long ouid);

    /**
     * 根据PO单ID，OUID查找PO
     * 
     * @param whPoCommand
     * @return
     */
    WhPo findWhPoById(WhPoCommand whPoCommand);

    /**
     * 根据PO单ID，OUID查找PO
     * 
     * @param whPoCommand
     * @return
     */
    WhPoCommand findWhPoCommandById(Long id, Long ouId);

    /**
     * PO单明细的分页查询
     * 
     * @param page
     * @param sorts
     * @param params
     * @param sourceType
     * @return
     */
    Pagination<WhPoLineCommand> findPoLineListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params, Integer sourceType);

    /**
     * PO单明细的分页查询
     * 
     * @param page
     * @param sorts
     * @param params
     * @param sourceType
     * @return
     */
    Pagination<BiPoLineCommand> findBiPoLineListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);

    /**
     * 根据POLINE的ID,OUID查找PO单明细
     * 
     * @param Command
     * @return
     */
    WhPoLineCommand findWhPoLineById(WhPoLineCommand Command);

    /**
     * 根据WhAsnLine的ID,OUID查找PO单明细
     * 
     * @param Command
     * @return
     */
    WhAsnLineCommand findWhAsnLineById(WhAsnLineCommand Command);

    /**
     * 根据WhAsnLine的ASNID,OUID查找ASN单明细
     * 
     * @param Command
     * @return
     */
    List<WhAsnLine> findWhAsnLineByAsnId(Long asnId, Long ouId);

    /**
     * ASN的分页查询
     * 
     * @param page
     * @param sorts
     * @param params
     * @param sourceType
     * @return
     */
    Pagination<WhAsnCommand> findWhAsnListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params, Integer sourceType);

    /**
     * 根据PO的EXTCODE,OUID,STATUSLIST模糊查找PO单明细
     * 
     * @param command
     * @return
     */
    List<WhPoCommand> findWhPoListByExtCode(WhPoCommand command);

    /**
     * 生成ASN的EXTCODE
     * 
     * @return
     */
    String getAsnExtCode();

    /**
     * 根据ASN的ID,OUID查找ASN
     * 
     * @param whAsnCommand
     * @return
     */
    WhAsnCommand findWhAsnById(WhAsnCommand whAsnCommand);

    /**
     * ASNLINE的分页查询
     * 
     * @param page
     * @param sorts
     * @param params
     * @param sourceType
     * @return
     */
    Pagination<WhAsnLineCommand> findAsnLineListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params, Integer sourceType);

    /**
     * 根据ASN的ASNEXTCODE和OUID查找
     */
    WhAsn getAsnByAsnExtCode(String asnExtCode, Long ouId);

    /**
     * 查找ASN中某个商品的数量
     * 
     * @param asnId
     * @param ouId
     * @param skuId
     * @return
     */
    long getSkuCountInAsnBySkuId(Long asnId, Long ouId, Long skuId);

    BiPo findBiPoById(Long id);

    BiPo findBiPoByPoCode(String poCode);

    BiPoCommand findBiPoCommandById(Long id);

    BiPoCommand findBiPoCommandByPoCode(String poCode);

    BiPoLineCommand findBiPoLineCommandById(Long id);

    BiPoLine findBiPoLineById(Long id);
    
    /**
     * PDA ASN预收货
     * @param whCommand
     * @return
     */
    WhAsnCommand findWhAsnCommandByAsnId(WhAsnCommand whCommand);
    
    /**
     * PDA ASN预收货,返回预收货模式
     * @param whCommand
     * @return
     */
    Integer returnReceiptMode(WhAsnCommand whCommand);

    /**
     * 分页查询可以拆分PO的明细行；可以：1.可用数量大于0；2.可用数量-已经拆分的数量>0
     * 
     * @param page
     * @param sorts
     * @param params
     * @return
     */
    Pagination<BiPoLineCommand> findListByQueryMapWithPageExtForCreateSubPo(Page page, Sort[] sorts, Map<String, Object> params);

    /**
     * 拆分PO时uuid明细的分页查询
     * 
     * @param page
     * @param sorts
     * @param params
     * @param sourceType
     * @return
     */
    Pagination<WhPoLineCommand> findPoLineListByQueryMapWithPageExtForCreateSubPo(Page page, Sort[] sorts, Map<String, Object> paraMap, Integer infoSource);
    
    /**
     * 校验此收货Asn是否存在该商品收货明细
     * @param asnId
     * @param skuId
     * @param ouId
     * @param logId
     * @return 
     */
    void checkWhAsnLineBySkuId(List<WhAsnLine> whAsnLineList, Long skuId, Long ouId, String logId);

    /**
     * 根据ID和OUID查找WHASN
     * 
     * @param occupationId
     * @param ouId
     * @return
     */
    WhAsn findWhAsnById(Long occupationId, Long ouId);

    /**
     * @author yimin.lu 创建ASN的时候查询ASN单临时数据
     * @param page
     * @param sorts
     * @param paraMap
     * @param shardSource
     * @return
     */
    Pagination<WhAsnLineCommand> findAsnLineListByQueryMapWithPageExtForCreateAsn(Page page, Sort[] sorts, Map<String, Object> paraMap, Integer shardSource);

}
