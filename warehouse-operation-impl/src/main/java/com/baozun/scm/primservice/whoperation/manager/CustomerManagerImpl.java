package com.baozun.scm.primservice.whoperation.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lark.common.annotation.MoreDB;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.warehouse.CustomerCommand;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.warehouse.CustomerDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.warehouse.CustomerManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.Customer;
import com.baozun.scm.primservice.whoperation.util.ParamsUtil;

@Service("customerManager")
@Transactional
public class CustomerManagerImpl implements CustomerManager {
    public static final Logger log = LoggerFactory.getLogger(CustomerManagerImpl.class);

    @Autowired
    private CustomerDao customerDao;


    @Override
    public List<Customer> findAllCustomerCategory(Customer customer) {
        if (log.isInfoEnabled()) {
            log.info("CustomerManagerImpl findAllCustomerCategory is start");
        }
        if (log.isDebugEnabled()) {
            log.debug("findAllCustomerCategory param [customer:{}] ", customer);
        }
        List<Customer> customerList = new ArrayList<>();
        customerList = customerDao.findListByParam(customer);
        if (log.isInfoEnabled()) {
            log.info("CustomerManagerImpl findAllCustomerCategory is end");
        }
        return customerList;
    }

    @Override
    public Customer getCustomerById(Long id) {
        if (log.isInfoEnabled()) {
            log.info("CustomerManagerImpl getCustomerById is start");
        }
        if (log.isDebugEnabled()) {
            log.debug("getCustomerById param [id:{}] ", id);
        }
        Customer customer = null;
        if (null != id) {
            customer = customerDao.findById(id);
        }
        if (log.isInfoEnabled()) {
            log.info("CustomerManagerImpl getCustomerById is end");
        }
        return customer;
    }

    @Override
    public List<Customer> findListByLifecycle(Long userId, Integer lifecycle) {
        if (log.isInfoEnabled()) {
            log.info("CustomerManagerImpl findListByLifecycle is start");
        }
        if (log.isDebugEnabled()) {
            log.debug("findListByLifecycle param [userId:{}, lifecycle:{}] ", userId, lifecycle);
        }
        Customer customer = new Customer();
        customer.setLifecycle(lifecycle);
        List<Customer> customerList = customerDao.findListByParam(customer);
        if (log.isInfoEnabled()) {
            log.info("CustomerManagerImpl findListByLifecycle is end");
        }
        return customerList;
    }

    @Override
    public Pagination<CustomerCommand> getListByParams(Page page, Sort[] sorts, Map<String, Object> param) {
        if (log.isInfoEnabled()) {
            log.info("CustomerManagerImpl getListByParams is start");
        }
        if (log.isDebugEnabled()) {
            log.debug("getListByParams param [page:{}, sorts:{}, param:{}] ", ParamsUtil.page2String(page), ParamsUtil.sorts2String(sorts), param);
        }
        Pagination<CustomerCommand> pagination = customerDao.findListByQueryMapWithPageExt(page, sorts, param);

        if (log.isInfoEnabled()) {
            log.info("CustomerManagerImpl getListByParams is end");
        }
        return pagination;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_GLOBALSOURCE)
    public Customer saveOrUpdate(Customer customer, Long userId) {
        if (log.isInfoEnabled()) {
            log.info("CustomerManagerImpl saveOrUpdate is start");
        }
        if (log.isDebugEnabled()) {
            log.debug("saveOrUpdate param [customer:{}] ", customer);
        }
        if (null == customer) {
            throw new BusinessException(ErrorCodes.PARAM_IS_NULL);
        }
        // 更新
        if (null != customer.getId()) {
            if (log.isDebugEnabled()) {
                log.debug("saveOrUpdate originCustomer is exists, update to infoDB, param [customer:{}] ", customer);
            }
            Customer originCustomer = customerDao.findById(customer.getId());
            if (null == originCustomer) {
                log.error("CustomerManagerImpl saveOrUpdate failed, originCustomer is null,  param [customer:{}] ", customer);
                throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
            }
            originCustomer.setCustomerName(customer.getCustomerName());
            originCustomer.setCustomerCode(customer.getCustomerCode());
            originCustomer.setPic(customer.getPic());
            originCustomer.setPicContact(customer.getPicContact());
            originCustomer.setCustomerType(customer.getCustomerType());
            originCustomer.setInvoiceType(customer.getInvoiceType());
            originCustomer.setPaymentTerm(customer.getPaymentTerm());
            originCustomer.setDescription(customer.getDescription());
            originCustomer.setOperatorId(userId);
            originCustomer.setLifecycle(customer.getLifecycle());
            originCustomer.setGlobalLastModifyTime(new Date());
            if (log.isDebugEnabled()) {
                log.debug("saveOrUpdate customer, update to infoDB, param [customer:{}, originCustomer:{}] ", customer, originCustomer);
            }
            int count = customerDao.saveOrUpdateByVersion(originCustomer);
            // 修改失败
            if (count != 1) {
                log.error("CustomerManagerImpl.saveOrUpdate failed, update count != 1, param [customer:{}, originCustomer:{}, count:{}] ", customer, originCustomer, count);
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
            customer = originCustomer;
        } else {
            if (log.isDebugEnabled()) {
                log.debug("saveOrUpdate originCustomer is null, insert to infoDB, param [customer:{}] ", customer);
            }
            customer.setCreateTime(new Date());
            customer.setLastModifyTime(new Date());
            customer.setOperatorId(userId);

            customerDao.insert(customer);
        }

        if (log.isInfoEnabled()) {
            log.info("CustomerManagerImpl saveOrUpdate is end");
        }
        return customer;
    }


    @Override
    public Boolean checkUnique(Customer customer) {
        if (log.isInfoEnabled()) {
            log.info("CustomerManagerImpl checkUnique is start");
        }
        if (log.isDebugEnabled()) {
            log.debug("checkUnique param [customer:{}] ", customer);
        }
        boolean result = true;
        if (null != customer) {
            long count = customerDao.checkUnique(customer);
            if (0 != count) {
                result = false;
            }
        }
        if (log.isInfoEnabled()) {
            log.info("CustomerManagerImpl checkUnique is end");
        }
        return result;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_GLOBALSOURCE)
    public void updateLifeCycle(List<Long> ids, Integer lifeCycle, Long userId) {
        if (log.isInfoEnabled()) {
            log.info("CustomerManagerImpl updateLifeCycle is start");
        }
        if (log.isDebugEnabled()) {
            log.debug("updateLifeCycle param [ids:{}, lifeCycle:{}, userId:{}]", ids, lifeCycle, userId);
        }
        if (null == ids || ids.isEmpty() || null == lifeCycle || null == userId) {
            log.error("CustomerManagerImpl updateLifeCycle failed, param is null, param [ids:{}, lifeCycle:{}, userId:{}]", ids, lifeCycle, userId);
            throw new BusinessException(ErrorCodes.PARAM_IS_NULL);
        }

        if (log.isDebugEnabled()) {
            log.debug("updateLifeCycle loop param [ids:{}]", ids);
        }
        for (Long id : ids) {
            Customer originCustomer = customerDao.findById(id);
            if (null == originCustomer) {
                log.error("CustomerManagerImpl updateLifeCycle failed, originCustomer is null, param [ids:{}, lifeCycle:{}, userId:{}, id:{}]", ids, lifeCycle, userId, id);
                throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
            }
            originCustomer.setLifecycle(lifeCycle);
            originCustomer.setGlobalLastModifyTime(new Date());
            originCustomer.setOperatorId(userId);
            int count = customerDao.saveOrUpdateByVersion(originCustomer);
            if (count != 1) {
                log.error("CustomerManagerImpl.updateLifeCycle failed, update count != 1, param [ids:{}, lifeCycle:{}, userId:{}, originCustomer:{}, count:{}]", ids, lifeCycle, userId, originCustomer, count);
                throw new BusinessException(ErrorCodes.UPDATE_DATA_QUANTITYERROR);
            }
        }
        if (log.isInfoEnabled()) {
            log.info("CustomerManagerImpl updateLifeCycle is end");
        }
    }


    @Override
    public Map<Long, String> getNameIdMapByIds(List<Long> ids) {
        if (log.isInfoEnabled()) {
            log.info("CustomerManagerImpl getNameIdMapByIds is start");
        }
        if (log.isDebugEnabled()) {
            log.debug("getNameIdMapByIds param [ids:{}] ", ids);
        }
        Map<Long, String> map = new HashMap<Long, String>();
        List<Customer> customers = null;
        if (null != ids) {
            customers = this.customerDao.getByIds(ids);
            if (log.isDebugEnabled()) {
                log.debug("getNameIdMapByIds loop param [customers:{}] ", customers);
            }
            for (Customer c : customers) {
                map.put(c.getId(), c.getCustomerName());
            }
        }
        if (log.isInfoEnabled()) {
            log.info("CustomerManagerImpl getNameIdMapByIds is end");
        }
        return map;
    }

    @Override
    public long getListCountByParams(Customer customer) {
        return this.customerDao.findListCountByParam(customer);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Customer getCustomerByCustomerId(Map<String, Object> customerparam) {
        // 查询客户名称
        Customer customer = null;
        try {
            customer = customerDao.getCustomerByCustomerId(customerparam);
        } catch (Exception e) {
            BusinessException be = new BusinessException(ErrorCodes.DAO_EXCEPTION);
            BusinessException dbe = new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
            be.setLinkedException(dbe);
            throw be;
        }
        return customer;
    }

    /**
     * 根据客户id查询客户信息
     * 
     * @param list
     * @param lifecycle
     * @return
     */
    @Override
    public List<Customer> getCustomerById(List<Long> list, Integer lifecycle) {
        // TODO Auto-generated method stub
        log.info("CustomerManagerImpl getCustomerById is start");
        if (log.isDebugEnabled()) {
            log.debug("CustomerManagerImpl getCustomerById is param [list:{}],[lifecycle:{}]", list, lifecycle);
        }
        List<Customer> customerList = customerDao.getCustomerById(list, lifecycle);
        log.info("CustomerManagerImpl getCustomerById is end");
        return customerList;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Long insertCustomer(Customer customer) {
        Long count = customerDao.insertCustomer(customer);
        return count;
    }



    /**
     * 查询该用户权限下的客户集合
     * 
     * @author shenlijun
     * @param lifecycle
     * @param customeridList
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<Customer> findCustomerList(Integer lifecycle, List<Long> customeridList) {
        if (log.isInfoEnabled()) {
            log.info("CustomerManagerImpl.findCustomerList is start");
        }
        if (log.isDebugEnabled()) {
            log.debug("CustomerManagerImpl, param [customeridList:{}]", customeridList);
        }
        if (null == customeridList) {
            log.error("CustomerManagerImpl.findCustomerList failed, param customeridList is null");
            throw new BusinessException(ErrorCodes.PARAM_IS_NULL);
        }
        List<Customer> customerList = customerDao.getCustomerListByParams(lifecycle, customeridList);
        if (log.isInfoEnabled()) {
            log.info("CustomerManagerImpl.findCustomerList is end");
        }
        return customerList;
    }


}
