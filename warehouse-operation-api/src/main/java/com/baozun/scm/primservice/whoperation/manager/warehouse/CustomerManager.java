package com.baozun.scm.primservice.whoperation.manager.warehouse;

import java.util.List;
import java.util.Map;

import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import com.baozun.scm.primservice.whoperation.command.warehouse.CustomerCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.Customer;

public interface CustomerManager extends BaseManager {

    /**
     * @author yimin.lu 2016/1/7 查找有效的客户
     * @param userId
     * @param lifecycle
     * @return
     */
    public List<Customer> findListByLifecycle(Long userId, Integer lifecycle);

    /**
     * 根据id获取客户
     * 
     * @author mingwei.xie
     * @param id
     */
    Customer getCustomerById(Long id);

    /**
     * 新建或保存客户信息
     * 
     * @author mingwei.xie
     * @param customer
     * @param userId
     * @return
     */
    Customer saveOrUpdate(Customer customer, Long userId);

    /**
     * 通过客户名称和客户编号检验客户是否存在
     * 
     * @author mingwei.xie
     * @param customer
     * @return
     */
    Boolean checkUnique(Customer customer);

    /**
     * 查询所有客户
     * 
     * @param customer
     * @return
     */
    List<Customer> findAllCustomerCategory(Customer customer);

    /**
     * 通过参数查询客户分页列表
     * 
     * @author mingwei.xie
     * @param page
     * @param sorts
     * @param param
     * @return
     */
    Pagination<CustomerCommand> getListByParams(Page page, Sort[] sorts, Map<String, Object> param);

    /**
     * 启用/停用客户
     * 
     * @author mingwei.xie
     * @param ids
     * @param lifeCycle
     * @param userid
     * @return
     */
    void updateLifeCycle(List<Long> ids, Integer lifeCycle, Long userid);

    /**
     * @author yimin.lu
     * @param ids
     * @return
     */
    Map<Long, String> getNameIdMapByIds(List<Long> ids);

    /**
     * 数量查找
     * 
     * @param customer
     * @return
     */
    long getListCountByParams(Customer customer);


    /**
     * 根据客户id查询客户名称
     * 
     * @author shenlijun
     * @param customerparam
     * @return
     */
    public Customer getCustomerByCustomerId(Map<String, Object> customerparam);

    /**
     * 根据客户id查询客户信息
     * 
     * @param list
     * @param lifecycle
     * @return
     */
    public List<Customer> getCustomerById(List<Long> list, Integer lifecycle);

    public Long insertCustomer(Customer customer);

    /**
     * 查询该用户权限下的客户集合
     * 
     * @author shenlijun
     * @param lifecycleNormal
     * @param customeridList
     * @return
     */
    public List<Customer> findCustomerList(Integer lifecycle, List<Long> customeridList);

    List<Customer> findCustomerAllList();


}
