package com.baozun.scm.primservice.whoperation.manager.system;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.system.SysDictionaryCommand;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.system.SysDictionaryDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.model.system.SysDictionary;

import lark.common.annotation.MoreDB;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

@Transactional
@Service("sysDictionaryManager")
public class SysDictionaryManagerImpl implements SysDictionaryManager {
    public static final Logger log = LoggerFactory.getLogger(SysDictionaryManager.class);
    @Autowired
    private SysDictionaryDao sysDictionaryDao;

    // check 2016-03-03 13:42
    /**
     * 根据 groupValue,lifecycle查询数据
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_GLOBALSOURCE)
    public List<SysDictionary> getListByGroup(String code, Integer lifecycle) {
        log.info("SysDictionaryManager.getListByGroup begin");
        if (log.isDebugEnabled()) {
            log.debug("Param groupValue is {}", code);
            log.debug("Param lifecycle is {}", lifecycle);
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("groupValue", code);
        params.put("lifecycle", lifecycle);
        log.info("SysDictionaryManager.getListByGroup end");
        return sysDictionaryDao.findListByQueryMap(params);
    }
    
    /**
     * @author lichuan
     * @param code
     * @param lifecycle
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_GLOBALSOURCE)
    public List<SysDictionary> getListByGroupAndDicValue(String code, String dicValue, Integer lifecycle) {
        log.info("SysDictionaryManager.getListByGroupAndDicValue begin");
        if (log.isDebugEnabled()) {
            log.debug("Param groupValue is {}", code);
            log.debug("Param lifecycle is {}", lifecycle);
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("groupValue", code);
        params.put("dicValue", dicValue);
        params.put("lifecycle", lifecycle);
        log.info("SysDictionaryManager.getListByGroupAndDicValue end");
        return sysDictionaryDao.findListByQueryMap(params);
    }

    // check 2016-03-03 13:42
    @Override
    @MoreDB(DbDataSource.MOREDB_GLOBALSOURCE)
    public List<SysDictionary> getAllList() {
        log.info("SysDictionaryManager.getAllList begin");
        log.info("SysDictionaryManager.getAllList end");
        return sysDictionaryDao.findListByQueryMap(new HashMap<String, Object>());
    }

    // check 2016-03-03 13:42
    /**
     * 系统配置参数信息查询一览：分页获得系统配置参数列表
     * 
     * @author yimin.lu 2015/12/7
     * @param page
     * @param sorts
     * @param param
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_GLOBALSOURCE)
    public Pagination<SysDictionaryCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params) {
        log.info("SysDictionaryManager.findListByQueryMapWithPageExt begin");
        if (log.isDebugEnabled()) {
            log.debug("Param page is {}", page);
            log.debug("Param params is {}", params);
        }
        log.info("SysDictionaryManager.findListByQueryMapWithPageExt end");
        return this.sysDictionaryDao.findListByQueryMapWithPageExt(page, sorts, params);
    }

    // check 2016-03-03 13:42
    @Override
    @MoreDB(DbDataSource.MOREDB_GLOBALSOURCE)
    public List<SysDictionaryCommand> findEditableGroup() {
        log.info("SysDictionaryManager.findEditableGroup begin");
        if (log.isDebugEnabled()) {
            log.debug("Param isSys default {}", 0);
        }
        SysDictionaryCommand command = new SysDictionaryCommand();
        command.setIsSys(0);
        log.info("SysDictionaryManager.findEditableGroup end");
        return this.sysDictionaryDao.findGroupListbyParams(command);
    }

    // check 2016-03-03 13:42
    @Override
    @MoreDB(DbDataSource.MOREDB_GLOBALSOURCE)
    public List<SysDictionaryCommand> findAllGroup() {
        log.info("SysDictionaryManager.findAllGroup begin");
        log.info("SysDictionaryManager.findAllGroup end");
        return this.sysDictionaryDao.findGroupListbyParams(new SysDictionaryCommand());
    }

    // check 2016-03-03 13:42
    @Override
    @MoreDB(DbDataSource.MOREDB_GLOBALSOURCE)
    public List<SysDictionaryCommand> findGroupByParam(SysDictionaryCommand command) {
        log.info("SysDictionaryManager.findGroupByParam begin");
        if (log.isDebugEnabled()) {
            log.debug("Param SysDictionaryCommand is {}", command);
        }
        log.info("SysDictionaryManager.findGroupByParam end");
        return this.sysDictionaryDao.findGroupListbyParams(command);
    }

    // check 2016-03-03 13:42
    @Override
    @MoreDB(DbDataSource.MOREDB_GLOBALSOURCE)
    public SysDictionaryCommand get(Long id) {
        log.info("SysDictionaryManager.get begin");
        if (log.isDebugEnabled()) {
            log.debug("Param id is {}", id);
        }
        SysDictionary dictionary = this.sysDictionaryDao.findById(id);
        SysDictionaryCommand command = new SysDictionaryCommand();
        BeanUtils.copyProperties(dictionary, command);
        log.info("SysDictionaryManager.get end");
        return command;
    }

    // check 2016-03-03 13:42
    @Override
    @MoreDB(DbDataSource.MOREDB_GLOBALSOURCE)
    public List<SysDictionary> batchUpdateLifecycle(List<Long> ids, Integer lifecycle, Long operatorId, Date modifyDate) {
        log.info("SysDictionaryManager.batchUpdateLifecycle begin");
        List<SysDictionary> sysDicList = new ArrayList<SysDictionary>();
        if (log.isDebugEnabled()) {
            log.debug("Param ids is {}", ids);
            log.debug("Param lifecycle is {}", lifecycle);
            log.debug("Param operatorId is {}", operatorId);
            log.debug("Param modifyDate is {}", modifyDate);
        }
        // 批量更新
        for (Long id : ids) {
            SysDictionary sys = this.sysDictionaryDao.findById(id);
            sys.setUpdateDate(modifyDate);
            sys.setLifecycle(lifecycle);
            sys.setOperatorId(operatorId);
            int updateRows = this.sysDictionaryDao.saveOrUpdateByVersion(sys);
            if (log.isDebugEnabled()) {
                log.debug("if condition [updateRows <= 0] is {}", updateRows <= 0);
            }
            if (updateRows <= 0) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
            sysDicList.add(sys);
        }
        log.info("SysDictionaryManager.batchUpdateLifecycle end");
        return sysDicList;
    }

    // check 2016-03-03 13:42
    @Override
    @MoreDB(DbDataSource.MOREDB_GLOBALSOURCE)
    public SysDictionary saveOrUpdate(SysDictionaryCommand command, Long userId) {
        log.info("SysDictionaryManager.saveOrUpdate begin");
        if (log.isDebugEnabled()) {
            log.debug("Param SysDictionaryCommand is {}", command);
            log.debug("Param userId is {}", userId);
        }
        // 将数据组装到需要保存的实体对象中去
        SysDictionary dictionary = copyProperties(command, userId);
        if (null == dictionary.getId()) {
            long updateRows = sysDictionaryDao.insert(dictionary);
            if (log.isDebugEnabled()) {
                log.debug("if condition [updateRows <= 0] is {}", updateRows <= 0);
            }
            if (updateRows <= 0) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
        } else {
            // 保存数据
            int updateRows = sysDictionaryDao.saveOrUpdateByVersion(dictionary);
            if (log.isDebugEnabled()) {
                log.debug("if condition [updateRows <= 0] is {}", updateRows <= 0);
            }
            if (updateRows <= 0) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
        }
        log.info("SysDictionaryManager.saveOrUpdate begin");
        return dictionary;
    }

    // check 2016-03-03 13:42
    /**
     * dictionary保存/更新共通操作
     * 
     * @author yimin.lu 2015/12/8
     * @param dictionary
     * @param operatorId
     */
    private void setOperateParams(SysDictionary dictionary, Long operatorId) {
        if (dictionary != null) {
            if (null == dictionary.getCreateTime()) {
                dictionary.setCreateTime(new Date());
                dictionary.setLastModifyTime(new Date());
                dictionary.setUpdateDate(new Date());
            }
            if (operatorId != null) {
                dictionary.setOperatorId(operatorId);
                dictionary.setUpdateDate(new Date());
            }
        }
    }

    // check 2016-03-03 13:42
    /**
     * 保存系统参数表时候设置默认值
     * 
     * @author yimin.lu 2015/12/11
     * @param dictionary
     */
    private void setDefaultParams(SysDictionary dictionary) {
        dictionary.setIsEdit(1);
        SysDictionaryCommand command = this.sysDictionaryDao.getGroupbyId(dictionary.getGroupId());
        dictionary.setGroupName(command.getGroupCode());
        dictionary.setGroupValue(command.getGroupCode());
        // 设置排序号
        setSortNum(dictionary);
    }

    // check 2016-03-03 13:42
    /**
     * 保存系统参数表时候设置排序号
     * 
     * @author yimin.lu 2015/12/10
     * @param dictionary
     */
    private void setSortNum(SysDictionary dictionary) {
        // 设置sortNum
        SysDictionary sd = new SysDictionary();
        sd.setGroupId(dictionary.getGroupId());
        Long count = this.sysDictionaryDao.findListCountByParam(sd);
        dictionary.setOrderNum(count.intValue() + 1);
    }

    // check 2016-03-03 13:42
    /**
     * 校验同一个组别中参数名称的唯一性
     * 
     * @author yimin.lu 2015/12/8
     * @param command
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_GLOBALSOURCE)
    public void checkUnique(SysDictionaryCommand command) {
        log.info("SysDictionaryManager.checkUnique begin");
        if (log.isDebugEnabled()) {
            log.debug("Param SysDictionaryCommand is {}", command);
        }
        // 校验同一个组别中参数名称和参数编码的唯一性
        int count = this.sysDictionaryDao.checkUnique(command);
        if (log.isDebugEnabled()) {
            log.debug("if condition [count != 0] is {}", count != 0);
        }
        if (count != 0) {
            throw new BusinessException(ErrorCodes.UPDATE_UOM_CODENAME_ERROR, new Object[] {"参数名称或编码"});
        }
        log.info("SysDictionaryManager.checkUnique end");
    }

    // check 2016-03-03 13:42
    @Override
    @MoreDB(DbDataSource.MOREDB_GLOBALSOURCE)
    public List<SysDictionary> getListByParam(SysDictionaryCommand command) {
        log.info("SysDictionaryManager.getListByParam begin");
        if (log.isDebugEnabled()) {
            log.debug("Param SysDictionaryCommand is {}", command);
        }
        SysDictionary sysDic = new SysDictionary();
        BeanUtils.copyProperties(command, sysDic);
        log.info("SysDictionaryManager.getListByParam end");
        return this.sysDictionaryDao.findListByParam(sysDic);
    }

    // check 2016-03-03 13:42
    /**
     * 组装保存操作或者更新操作保存到数据库中的数据
     * 
     * @author yimin.lu 2015/12/8
     * @param command
     * @return
     */
    private SysDictionary copyProperties(SysDictionaryCommand command, Long userId) {
        SysDictionary dictionary = null;
        // 创建
        if (null == command.getId()) {
            dictionary = new SysDictionary();
            BeanUtils.copyProperties(command, dictionary);
            // 设置默认参数
            setDefaultParams(dictionary);
        } else {
            // 编辑
            dictionary = this.sysDictionaryDao.findById(command.getId());
            dictionary.setDicLabel(command.getDicLabel());
            dictionary.setLifecycle(command.getLifecycle());
        }
        // 设置基本参数
        setOperateParams(dictionary, userId);
        return dictionary;
    }

}
