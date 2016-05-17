package com.baozun.scm.primservice.whoperation.manager.auth;

import java.util.List;

import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.auth.OperationUnit;

public interface OperationUnitManager extends BaseManager {



    /**
     * 根据用户ID获取组织
     * 
     * @param userId
     * @return
     */
    public List<OpUnitTreeCommand> findOpUnitTreeByUserId(Long userId);

    /**
     * @author 周中波 基础信息查询
     * @param operationUnit
     * @return
     */
    List<OperationUnit> findListByParam(OperationUnit operationUnit);

    List<OpUnitTreeCommand> findListByParentId(Long parentId);

    OperationUnit findOperationUnitById(Long id);

}
