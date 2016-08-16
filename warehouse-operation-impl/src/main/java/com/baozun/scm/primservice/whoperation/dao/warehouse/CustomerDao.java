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

import java.util.List;
import java.util.Map;

import lark.common.annotation.CommonQuery;
import lark.common.annotation.QueryPage;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;
import lark.orm.dao.supports.BaseDao;

import org.apache.ibatis.annotations.Param;

import com.baozun.scm.primservice.whoperation.command.warehouse.CustomerCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.Customer;

public interface CustomerDao extends BaseDao<Customer, Long> {

    Pagination<Customer> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    @CommonQuery
    int saveOrUpdate(Customer o);

    @CommonQuery
    int saveOrUpdateByVersion(Customer o);

    /**
     * 通过参数查询客户分页列表
     * 
     * @author mingwei.xie
     * @param page
     * @param params
     * @return
     */
    @QueryPage("findListCountByQueryMapExt")
    Pagination<CustomerCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);

    List<Customer> getByIds(@Param("list") List<Long> ids);

    /**
     * 通过客户名称和客户编号检验客户是否存在
     * 
     * @author mingwei.xie
     * @param customer
     * @return
     */
    long checkUnique(Customer customer);

    /**
     * 根据客户id查询客户名称
     * 
     * @author shenlijun
     * @param customerparam
     * @return
     */
    Customer getCustomerByCustomerId(Map<String, Object> customerparam);

    /**
     * 根据客户id查询客户信息
     * 
     * @param
     * @param lifecycle
     * @return
     */
    public List<Customer> getCustomerById(@Param("customerList") List<Long> customerList, @Param("lifecycle") Integer lifecycle);

    Long insertCustomer(Customer customer);


    /**
     * 在对应用户的权限下查询对应的客户集合
     * 
     * @author shenlijun
     * @param lifecycle
     * @param customeridList
     * @return
     */
    List<Customer> getCustomerListByParams(@Param("lifecycle") Integer lifecycle, @Param("customeridList") List<Long> customeridList);

}
