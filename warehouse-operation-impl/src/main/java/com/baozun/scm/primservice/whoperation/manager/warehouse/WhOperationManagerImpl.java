package com.baozun.scm.primservice.whoperation.manager.warehouse;

import lark.common.annotation.MoreDB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.warehouse.WhOperationCommand;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOperationDao;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOperation;

@Service("whOperationManager")
@Transactional
public class WhOperationManagerImpl extends BaseManagerImpl implements WhOperationManager {
    
    public static final Logger log = LoggerFactory.getLogger(WhOperationManagerImpl.class);

    @Autowired
    private WhOperationDao whOperationDao;
    
    /**
     * [通用方法] 创建作业头信息
     * @param WhOperationCommand
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    @Transactional(propagation = Propagation.SUPPORTS)
    public Boolean saveOrUpdate(WhOperationCommand whOperationCommand) {
        WhOperation whOperation = new WhOperation();
        //复制数据        
        BeanUtils.copyProperties(whOperationCommand, whOperation);
        if(null != whOperationCommand.getId() ){
            whOperationDao.saveOrUpdateByVersion(whOperation);
        }else{
            whOperationDao.insert(whOperation);
        }
        return null;
    }

    /**
     * [通用方法] 根据作业号查询作业头信息
     * @param operationCode
     * @param ouId
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhOperationCommand findOperationByCode(String operationCode, Long ouId) {
        WhOperationCommand whOperationCommand = this.whOperationDao.findOperationByCode(operationCode, ouId);
        return whOperationCommand;
    }

    /**
     * [通用方法] 根据workId获取作业信息
     * @param workId
     * @param ouId
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhOperationCommand findOperationByWorkId(Long workId, Long ouId) {
        WhOperationCommand whOperationCommand = this.whOperationDao.findOperationCommandByWorkId(workId, ouId);
        return whOperationCommand;
    }

    /**
     * [通用方法] 根据id获取作业信息
     * @param id
     * @param ouId
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhOperationCommand findOperationById(Long id, Long ouId) {
        WhOperationCommand whOperationCommand = this.whOperationDao.findOperationCommandById(id, ouId);
        return whOperationCommand;
    }
    
}
