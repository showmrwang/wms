package com.baozun.scm.primservice.whoperation.manager.warehouse;

import java.util.List;

import lark.common.annotation.MoreDB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WorkTypeDao;
import com.baozun.scm.primservice.whoperation.model.BaseModel;
import com.baozun.scm.primservice.whoperation.model.warehouse.WorkType;

@Service("workTypeManager")
@Transactional
public class WorkTypeManagerImpl implements WorkTypeManager {

    @Autowired
    private WorkTypeDao workTypeDao;

    public static final Logger log = LoggerFactory.getLogger(WorkTypeManager.class);

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WorkType findWorkType(String workCategory, Long ouId) {
        
        WorkType workType = this.workTypeDao.findWorkTypeByworkCategory(workCategory, ouId);
        
        return workType;
    }

}
