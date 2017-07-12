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
    /**
     * [业务方法] 获取工作区域id
     * @param userId
     * @param ouId
     * @param category
     * @return
     */
    String findworkAreaByUserAndOuId(@Param("userId") Long userId, @Param("ouId") Long ouId, @Param("category") String category);

    /**
     * [业务方法] 获取工作权限id
     * @param userId
     * @param ouId
     * @param category
     * @return
     */
    WhWorkCommand findworkAreaAndPrivilegeByUserAndOuId(@Param("userId") Long userId, @Param("ouId") Long ouId, @Param("category") String category);

    /**
     * [业务方法] 查找工作
     * @param userId
     * @param ouId
     * @param category
     * @param workAreaIds
     * @param maxObtainWorkQty
     * @return
     */
    WhWorkCommand findwork(@Param("userId") Long userId, @Param("ouId") Long ouId, @Param("category") String category, @Param("workAreaIds") String workAreaIds, @Param("maxObtainWorkQty") Long maxObtainWorkQty);


    @QueryPage("findWorkListCountByQueryMap")
    Pagination<WhWorkCommand> findWorkListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    @QueryPage("findWorkListCountByQueryMapExt")
    Pagination<WhWorkCommand> findWorkListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);
}
