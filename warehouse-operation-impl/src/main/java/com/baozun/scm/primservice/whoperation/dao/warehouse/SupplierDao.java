/**
 * Copyright (c) 2013 Baozun All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Baozun. You shall not disclose
 * such Confidential Information and shall use it only in accordance with the terms of the license
 * agreement you entered into with Baozun.
 *
 * BAOZUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. BAOZUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES.
 *
 */
package com.baozun.scm.primservice.whoperation.dao.warehouse;

import java.util.Date;
import java.util.List;
import java.util.Map;

import lark.common.annotation.CommonQuery;
import lark.common.annotation.QueryPage;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;
import lark.orm.dao.supports.BaseDao;

import org.apache.ibatis.annotations.Param;

import com.baozun.scm.primservice.whoperation.command.warehouse.SupplierCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.Supplier;

public interface SupplierDao extends BaseDao<Supplier, Long> {

    /**
     * 通过参数查询供应商分页列表
     * @param page
     * @param sorts
     * @param params
     * @return
     */
    @QueryPage("findListCountByQueryMapExt")
    Pagination<SupplierCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);

    /**
     * 新增或者保存供应商信息
     * @param o
     * @return
     */
    @CommonQuery
    int saveOrUpdate(Supplier o);

    /**
     * Id存在更新记录
     * @param o
     * @return
     */
    @CommonQuery
    int saveOrUpdateByVersion(Supplier o);

    /**
     * 批量编辑供应商是否有效
     * @param ids
     * @param lifecycle
     * @param userid
     * @return
     */
    int updateLifeCycle(@Param("ids") List<Long> ids, @Param("lifecycle") Integer lifecycle, @Param("userid") Long userid, @Param("lastModifyTime") Date lastModifyTime);

    /**
     * 修改保存供应商名称时校验名称是否存在
     * @param supplier
     * @param supplierId
     * @return
     */
    long findListCountByParamAndId(Supplier supplier);


    List<Supplier> getByIds(@Param("list") List<Long> ids);

    /**
    * 通过供应商名称和编号检验供应商是否存在
    * @param container2ndCategory
    * @return
    */
    long uniqueCodeOrName(Supplier supplier);
    
    /**
     * 查询权限下，所有启用的供应商
     * @param customerList
     * @param lifecycle
     * @return
     */
    public List<Supplier> getSupplierByIds(@Param("customerList") List<Long> customerList, @Param("lifecycle") Integer lifecycle);

}
