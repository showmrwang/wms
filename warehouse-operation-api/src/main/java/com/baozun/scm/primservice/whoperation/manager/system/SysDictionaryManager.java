package com.baozun.scm.primservice.whoperation.manager.system;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.baozun.scm.primservice.whoperation.command.system.SysDictionaryCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.system.SysDictionary;

import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

public interface SysDictionaryManager extends BaseManager {

    List<SysDictionary> getListByGroup(String code, Integer lifecycle);

    List<SysDictionary> getAllList();

    /**
     * 系统配置参数信息查询一览：分页获得系统配置参数列表
     * 
     * @author yimin.lu 2015/12/7
     * @param page
     * @param sorts
     * @param param
     * @return
     */
    public Pagination<SysDictionaryCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> param);

    /**
     * 查找可以编辑的系统参数配置组别
     * 
     * @author yimin.lu 2015/12/7
     * @param sysDictionaryCommand
     * @return
     */
    List<SysDictionaryCommand> findEditableGroup();

    /**
     * 查找不可以编辑的系统参数配置组别
     * 
     * @author yimin.lu 2015/12/7
     * @param sysDictionaryCommand
     * @return
     */
    List<SysDictionaryCommand> findAllGroup();

    /**
     * 查找不可以编辑的系统参数配置组别
     * 
     * @author yimin.lu 2015/12/7
     * @param sysDictionaryCommand
     * @return
     */
    List<SysDictionaryCommand> findGroupByParam(SysDictionaryCommand command);

    /**
     * 根据Id查找
     * 
     * @author yimin.lu 2015/12/8
     * @param id
     * @return
     */
    SysDictionaryCommand get(Long id);

    /**
     * 批量更新 停用/启用标识
     * 
     * @author yimin.lu 2015/12/8
     * @param ids
     * @param lifecycle
     * @param operatorId
     * @param modifyDate
     * @return
     */
    List<SysDictionary> batchUpdateLifecycle(List<Long> ids, Integer lifecycle, Long operatorId, Date modifyDate);

    /**
     * 保存或者更新
     * 
     * @author yimin.lu 2015/12/8
     * @param command
     * @param userId
     * @return
     */
    SysDictionary saveOrUpdate(SysDictionaryCommand command, Long userId);

    /**
     * 根据条件查找
     * 
     * @author yimin.lu
     * @param poTypeCommand
     * @return
     */
    List<SysDictionary> getListByParam(SysDictionaryCommand poTypeCommand);

    void checkUnique(SysDictionaryCommand command);
}
