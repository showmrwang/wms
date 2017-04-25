package com.baozun.scm.primservice.whoperation.manager.warehouse.ma;

import java.util.List;
import java.util.Map;

import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import com.baozun.scm.primservice.whoperation.command.warehouse.ma.DistributionTargetCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.ma.DistributionTarget;


public interface DistributionTargetManager extends BaseManager {

    /**
     * 通过参数查询配送对象列表
     *
     * @author mingwei.xie
     * @param page
     * @param sorts
     * @param params
     * @return
     */
    Pagination<DistributionTargetCommand> getListByParams(Page page, Sort[] sorts, Map<String, Object> params);


    /**
     * 根据id查找配送对象
     *
     * @author mingwei.xie
     * @param id
     * @return
     */
    DistributionTarget findDistributionTargetById(Long id);

    /**
     * 根据id查找配送对象
     *
     * @author mingwei.xie
     * @param id
     * @return
     */
    DistributionTargetCommand findDistributionTargetCommandById(Long id);

    /**
     * 验证配送对象名称编码是否唯一
     *
     * @author mingwei.xie
     * @param distributionTargetCommand
     * @return
     */
    Boolean checkUnique(DistributionTargetCommand distributionTargetCommand);

    /**
     * 新建/修改配送对象信息
     * @author mingwei.xie
     * @param distributionTargetCommand
     * @param userId
     * @return
     */
    DistributionTarget saveOrUpdate(DistributionTargetCommand distributionTargetCommand, Long userId);

    /**
     * 启用/停用配送对象
     *
     * @author mingwei.xie
     * @param ids
     * @param lifeCycle
     * @param userId
     * @return
     */
    void updateLifeCycle(List<Long> ids, Integer lifeCycle, Long userId);


    List<DistributionTarget> findDistributionTargetByParams(DistributionTarget search);
}
