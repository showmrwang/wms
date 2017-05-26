package com.baozun.scm.primservice.whoperation.dao.odo;

import java.util.List;
import java.util.Map;

import lark.common.annotation.CommonQuery;
import lark.common.annotation.QueryPage;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.QueryCondition;
import lark.common.dao.Sort;
import lark.orm.dao.supports.BaseDao;

import org.apache.ibatis.annotations.Param;

import com.baozun.scm.primservice.whoperation.model.odo.WhOdodeliveryInfo;

public interface WhOdoDeliveryInfoDao extends BaseDao<WhOdodeliveryInfo, Long> {
    @QueryPage("findListCountByQueryMap")
    Pagination<WhOdodeliveryInfo> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    @QueryPage("queryCount")
    Pagination<WhOdodeliveryInfo> query(Page page, Sort[] sorts, QueryCondition cond);

    List<WhOdodeliveryInfo> query(QueryCondition cond);

    Long queryCount(QueryCondition cond);

    @CommonQuery
    int saveOrUpdate(WhOdodeliveryInfo o);

    @CommonQuery
    int saveOrUpdateByVersion(WhOdodeliveryInfo o);


    /**
     * 通过出库单ID查找对应数据
     * 
     * @param id
     * @param ouid
     * @return
     */
    List<WhOdodeliveryInfo> findWhOdodeliveryInfoByOdoId(@Param("id") Long id, @Param("ouid") Long ouid);

    /**
     * 通过出库单ID查找没有绑定出库箱数据
     * 
     * @param id
     * @param ouid
     * @return
     */
    List<WhOdodeliveryInfo> findByOdoIdWithoutOutboundbox(@Param("id") Long id, @Param("ouid") Long ouid);
    
    

    /**
     * 通过出库单ID且出库单号为空的数据
     * 
     * @param id
     * @param ouid
     * @return
     */
    List<WhOdodeliveryInfo> getWhOdodeliveryInfoByOdoId(@Param("odoId") Long odoId, @Param("ouid") Long ouid);



    /**
     * 查询出库单下所有的交接信息
     *
     * @param odoId
     * @param ouId
     * @return
     */
    public List<WhOdodeliveryInfo> findListByOdoId(@Param("odoId") Long odoId, @Param("ouId") Long ouId);


    /**
     * 查询出库单下可用的运单
     *
     * @param odoId
     * @param ouId
     * @return
     */
    public WhOdodeliveryInfo findUseableWaybillInfoByOdoId(@Param("odoId") Long odoId, @Param("ouId") Long ouId);

    /**
     * 运单号是否已被使用
     *
     * @param waybillCode
     * @param ouId
     * @return
     */
    public int checkUniqueWaybillCode(@Param("waybillCode") String waybillCode, @Param("ouId") Long ouId);

}
