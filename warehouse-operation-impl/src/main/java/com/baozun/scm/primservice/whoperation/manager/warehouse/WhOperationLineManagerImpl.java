package com.baozun.scm.primservice.whoperation.manager.warehouse;

import java.util.List;

import lark.common.annotation.MoreDB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.warehouse.WhOperationLineCommand;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOperationLineDao;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOperationLine;

@Service("whOperationLineManager")
@Transactional
public class WhOperationLineManagerImpl extends BaseManagerImpl implements WhOperationLineManager {
    
    public static final Logger log = LoggerFactory.getLogger(WhOperationLineManagerImpl.class);
    
    @Autowired
    private WhOperationLineDao whOperationLineDao;

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    @Transactional(propagation = Propagation.SUPPORTS)
    public Boolean saveOrUpdate(WhOperationLineCommand whOperationLineCommand) {
        WhOperationLine whOperationLine = new WhOperationLine();
        //复制数据        
        BeanUtils.copyProperties(whOperationLineCommand, whOperationLine);
        if(null != whOperationLineCommand.getId() ){
            whOperationLineDao.saveOrUpdateByVersion(whOperationLine);
        }else{
            whOperationLineDao.insert(whOperationLine);
        }
        return null;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhOperationLineCommand> findOperationLineByOperationId(Long operationId, Long ouId) {
        List<WhOperationLineCommand> operationLineList = whOperationLineDao.findOperationLineByOperationId(operationId, ouId);
        return operationLineList;
    }
    
    
}
