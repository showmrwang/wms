package com.baozun.scm.primservice.whoperation.dao.auth;

import java.util.Map;

import lark.common.annotation.QueryPage;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;
import lark.orm.dao.supports.BaseDao;

import org.apache.ibatis.annotations.Param;

import com.baozun.scm.primservice.whoperation.command.warehouse.WhWorkCommand;

public interface OperPrivilegeDao extends BaseDao {
    String findworkAreaByUserAndOuId(@Param("userId") Long userId, @Param("ouId") Long ouId, @Param("category") String category);

    WhWorkCommand findwork(@Param("userId") Long userId, @Param("ouId") Long ouId, @Param("category") String category, @Param("workAreaIds") String workAreaIds, @Param("maxObtainWorkQty") Long maxObtainWorkQty);


    @QueryPage("findWorkListCountByQueryMap")
    Pagination<WhWorkCommand> findWorkListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);
}
