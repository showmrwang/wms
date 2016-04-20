package com.baozun.scm.primservice.whoperation.manager.poasn.poasnproxy;

import java.util.List;
import java.util.Map;

import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnLineCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhPoCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhPoLineCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsn;

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
     * 模糊查询方法。 根据asnExtCode,asn状态，仓库查找asn
     * 
     * @param asnExtCode
     * @param status
     * @param ouid
     * @return
     */
    List<WhAsnCommand> findWhAsnListByAsnExtCode(String asnExtCode, Integer[] status, Long ouid);

    /**
     * 更加PO单ID，OUID查找PO
     * 
     * @param whPoCommand
     * @return
     */
    WhPoCommand findWhPoById(WhPoCommand whPoCommand);

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

}
