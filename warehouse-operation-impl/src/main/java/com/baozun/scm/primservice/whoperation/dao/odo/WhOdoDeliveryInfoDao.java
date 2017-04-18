package com.baozun.scm.primservice.whoperation.dao.odo;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import lark.common.annotation.CommonQuery;
import lark.common.annotation.QueryPage;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.QueryCondition;
import lark.common.dao.Sort;
import lark.orm.dao.supports.BaseDao;

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


    /**
     * 通过出库单ID查找对应数据
     * 
     * @param id
     * @param ouid
     * @return
     */
    List<WhOdodeliveryInfo> findWhOdodeliveryInfoByOdoId(@Param("id") Long id, @Param("ouid") Long ouid);
}
