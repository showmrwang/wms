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

import com.baozun.scm.primservice.whoperation.command.warehouse.WhWorkLineCommand;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhWorkLineDao;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhWorkLine;

@Service("whWorkLineManager")
@Transactional
public class WhWorkLineManagerImpl extends BaseManagerImpl implements WhWorkLineManager {
    
    public static final Logger log = LoggerFactory.getLogger(WhWorkLineManagerImpl.class);

    @Autowired
    private WhWorkLineDao whWorkLineDao;
    
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    @Transactional(propagation = Propagation.SUPPORTS)
    public Boolean saveOrUpdate(WhWorkLineCommand whWorkLineCommand) {
        WhWorkLine whWorkLine = new WhWorkLine();
        //复制数据        
        BeanUtils.copyProperties(whWorkLineCommand, whWorkLine);
        if(null != whWorkLineCommand.getId() ){
            whWorkLineDao.saveOrUpdateByVersion(whWorkLine);
        }else{
            whWorkLineDao.insert(whWorkLine);
        }
        return null;
    }
    
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhWorkLineCommand> findWorkLineByWorkId(Long workId, Long ouId) {
        List<WhWorkLineCommand> whWorkLineCommandList = this.whWorkLineDao.findWorkLineByWorkId(workId, ouId);
        return whWorkLineCommandList;
    }

    @Override
    public List<WhWorkLine> findListByWorkId(Long id, Long ouId) {
        WhWorkLine search = new WhWorkLine();
        search.setWorkId(id);
        search.setOuId(ouId);
        return this.whWorkLineDao.findListByParam(search);
    }
    
}
