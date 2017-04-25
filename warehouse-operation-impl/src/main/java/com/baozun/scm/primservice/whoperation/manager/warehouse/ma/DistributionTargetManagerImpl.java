/**
 * Copyright (c) 2013 Baozun All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Baozun.
 * You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Baozun.
 * <p/>
 * BAOZUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. BAOZUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 */

package com.baozun.scm.primservice.whoperation.manager.warehouse.ma;

import java.util.Date;
import java.util.List;
import java.util.Map;

import lark.common.annotation.MoreDB;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.warehouse.ma.DistributionTargetCommand;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ma.DistributionTargetDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.warehouse.ma.DistributionTarget;

@Service("distributionTargetManager")
@Transactional
public class DistributionTargetManagerImpl extends BaseManagerImpl implements DistributionTargetManager {
    public static final Logger log = LoggerFactory.getLogger(DistributionTargetManagerImpl.class);

    @Autowired
    private DistributionTargetDao distributionTargetDao;

    /**
     * 通过参数查询配送对象列表
     *
     * @author mingwei.xie
     * @param page
     * @param sorts
     * @param params
     * @return
     */
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    @Override
    public Pagination<DistributionTargetCommand> getListByParams(Page page, Sort[] sorts, Map<String, Object> params) {
        Pagination<DistributionTargetCommand> pagination = distributionTargetDao.findListByQueryMapWithPageExt(page, sorts, params);
        return pagination;
    }

    /**
     * 根据id查找配送对象
     *
     * @param id
     * @return
     * @author mingwei.xie
     */
    @MoreDB(DbDataSource.MOREDB_GLOBALSOURCE)
    @Override
    public DistributionTarget findDistributionTargetById(Long id) {
        return distributionTargetDao.findById(id);
    }

    /**
     * 根据id查找配送对象
     *
     * @author mingwei.xie
     * @param id
     * @return
     */
    @MoreDB(DbDataSource.MOREDB_GLOBALSOURCE)
    @Override
    public DistributionTargetCommand findDistributionTargetCommandById(Long id) {
        return distributionTargetDao.findCommandById(id);
    }

    /**
     * 验证配送对象名称编码是否唯一
     *
     * @author mingwei.xie
     * @param distributionTargetCommand
     * @return
     */
    @MoreDB(DbDataSource.MOREDB_GLOBALSOURCE)
    @Override
    public Boolean checkUnique(DistributionTargetCommand distributionTargetCommand) {
        int count = distributionTargetDao.checkUnique(distributionTargetCommand);
        boolean isUnique = true;
        if(count > 0){
            isUnique = false;
        }
        return isUnique;
    }

    /**
     * 新建/修改配送对象信息
     *
     * @author mingwei.xie
     * @param distributionTargetCommand
     * @param userId
     * @return
     */
    @MoreDB(DbDataSource.MOREDB_GLOBALSOURCE)
    @Override
    public DistributionTarget saveOrUpdate(DistributionTargetCommand distributionTargetCommand, Long userId) {
        if (null == distributionTargetCommand) {
            throw new BusinessException(ErrorCodes.PARAM_IS_NULL);
        }
        DistributionTarget updateDistributionTarget;
        if(null != distributionTargetCommand.getId()){
            DistributionTarget originDistributionTarget = distributionTargetDao.findById(distributionTargetCommand.getId());
            if (null == originDistributionTarget) {
                log.error("DistributionTargetManagerImpl saveOrUpdate failed, originDistributionTarget is null,  param [distributionTargetCommand:{}] ", distributionTargetCommand);
                throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
            }
            originDistributionTarget.setType(distributionTargetCommand.getType());
            //originDistributionTarget.setCode(distributionTargetCommand.getCode());
            //originDistributionTarget.setName(distributionTargetCommand.getName());
            originDistributionTarget.setMobilePhone(distributionTargetCommand.getMobilePhone());
            originDistributionTarget.setTelephone(distributionTargetCommand.getTelephone());
            originDistributionTarget.setCountry(distributionTargetCommand.getCountry());
            originDistributionTarget.setProvince(distributionTargetCommand.getProvince());
            originDistributionTarget.setCity(distributionTargetCommand.getCity());
            originDistributionTarget.setDistrict(distributionTargetCommand.getDistrict());
            originDistributionTarget.setVillagesTowns(distributionTargetCommand.getVillagesTowns());
            originDistributionTarget.setAddress(distributionTargetCommand.getAddress());
            originDistributionTarget.setEmail(distributionTargetCommand.getEmail());
            originDistributionTarget.setZip(distributionTargetCommand.getZip());
            originDistributionTarget.setLineInfo(distributionTargetCommand.getLineInfo());
            originDistributionTarget.setLifecycle(distributionTargetCommand.getLifecycle());
            originDistributionTarget.setGlobalLastModifyTime(new Date());
            originDistributionTarget.setModifiedId(userId);
            originDistributionTarget.setDistributionTargetGroup(distributionTargetCommand.getDistributionTargetGroup());

            int count = distributionTargetDao.saveOrUpdateByVersion(originDistributionTarget);
            // 修改失败
            if (count != 1) {
                log.error("DistributionTargetManagerImpl.saveOrUpdate failed, update count != 1, param [distributionTargetCommand:{}, originDistributionTarget:{}, count:{}] ", distributionTargetCommand, originDistributionTarget, count);
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
            updateDistributionTarget = originDistributionTarget;
        }else {
            DistributionTarget distributionTarget = new DistributionTarget();
            BeanUtils.copyProperties(distributionTargetCommand, distributionTarget);
            distributionTarget.setCreateId(userId);
            distributionTarget.setCreateTime(new Date());
            distributionTarget.setLastModifyTime(new Date());
            distributionTarget.setModifiedId(userId);

            distributionTargetDao.insert(distributionTarget);
            updateDistributionTarget = distributionTarget;
        }

        return updateDistributionTarget;
    }

    /**
             * 启用/停用配送对象
             *
             * @author mingwei.xie
             * @param ids
             * @param lifeCycle
             * @param userId
             * @return
             */
    @MoreDB(DbDataSource.MOREDB_GLOBALSOURCE)
    @Override
    public void updateLifeCycle(List<Long> ids, Integer lifeCycle, Long userId) {
        if (null == ids || ids.isEmpty() || null == lifeCycle || null == userId) {
            log.error("DistributionTargetManagerImpl updateLifeCycle failed, param is null, param [ids:{}, lifeCycle:{}, userId:{}]", ids, lifeCycle, userId);
            throw new BusinessException(ErrorCodes.PARAM_IS_NULL);
        }

        for (Long id : ids) {
            DistributionTarget originDistributionTarget = distributionTargetDao.findById(id);
            if (null == originDistributionTarget) {
                log.error("DistributionTargetManagerImpl updateLifeCycle failed, originDistributionTarget is null, param [ids:{}, lifeCycle:{}, userId:{}, id:{}]", ids, lifeCycle, userId, id);
                throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
            }
            originDistributionTarget.setLifecycle(lifeCycle);
            originDistributionTarget.setGlobalLastModifyTime(new Date());
            originDistributionTarget.setModifiedId(userId);
            int count = distributionTargetDao.saveOrUpdateByVersion(originDistributionTarget);
            if (count != 1) {
                log.error("DistributionTargetManagerImpl.updateLifeCycle failed, update count != 1, param [ids:{}, lifeCycle:{}, userId:{}, originDistributionTarget:{}, count:{}]", ids, lifeCycle, userId, originDistributionTarget, count);
                throw new BusinessException(ErrorCodes.UPDATE_DATA_QUANTITYERROR);
            }
        }
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_GLOBALSOURCE)
    public List<DistributionTarget> findDistributionTargetByParams(DistributionTarget search) {
        return this.distributionTargetDao.findListByParam(search);
    }
}
