package com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager;

import java.util.List;
import java.util.Map;

import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import com.baozun.scm.primservice.whoperation.command.poasn.BiPoLineCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.poasn.BiPoLine;
import com.baozun.scm.primservice.whoperation.model.sku.Sku;

public interface BiPoLineManager extends BaseManager {

    /**
     * [业务方法]查询商品属性相同的BIPOLINE;第二个参数控制查询的是临时数据还是正式数据
     * 
     * @param line
     * @param b
     * @return
     */
    BiPoLine findPoLineByAddPoLineParam(BiPoLine line, boolean b);

    /**
     * [通用方法]插入BIPOLINE
     * 
     * @param line
     */
    void insert(BiPoLine line);

    /**
     * [通用方法]乐观锁更新BIPOLINE
     * 
     * @param line
     */
    void saveOrUpdateByVersion(BiPoLine line);

    /**
     * [业务方法]BIPOLINE一览查询
     * 
     * @param page
     * @param sorts
     * @param params
     * @return
     */
    Pagination<BiPoLineCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);

    /**
     * [业务方法]BIPOLINE的保存：从临时数据变成正式数据
     * 
     * @param biPoLineCommand
     */
    void createPoLineBatchToInfo(BiPoLineCommand biPoLineCommand);

    /**
     * [通用方法]根据ID查询BIPOLINE
     * 
     * @param id
     * @return
     */
    BiPoLine findBiPoLineById(Long id);

    /**
     * [通用方法]根据ID查询BIPOLINE
     * 
     * @param id
     * @return
     */
    BiPoLineCommand findBiPoLineCommandById(Long id);

    /**
     * [业务方法]编辑BIPO明细
     * 
     * @param biPoLine
     */
    void editBiPoLineSingle(BiPoLine biPoLine);

    /**
     * [业务方法]删除BIPOLINE临时数据
     * 
     * @param poId
     * @param uuid
     * @param userId
     * @return
     */
    void deleteBiPoLineByPoIdAndUuidToInfo(Long poId, String uuid, Long userId);

    /**
     * [业务方法] 创建子PO时一览查询
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

    /**
     * 查找商品信息
     * 
     * @param skuBarCode
     * @param customerId
     * @param logId
     */
    Sku findSkuByBarCode(String skuBarCode, Long customerId, String logId);

    /**
     * [业务方法]整行取消入库单
     * 
     * @param poId
     * @param lineList
     * @param userId
     */
    void cancelList(Long poId, List<BiPoLine> lineList, Long userId);

    /**
     * [通用方法]根据外接行号查找入库单明细
     * 
     * @param poId
     * @param extLinenums
     * @return
     */
    List<BiPoLine> findBiPoLineByBiPoIdAndLineNums(Long poId, List<Integer> extLinenums);

}
