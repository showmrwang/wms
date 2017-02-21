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

import com.baozun.scm.primservice.whoperation.command.warehouse.WhWorkCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhWorkDao;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.seeding.WhSeedingWallLattice;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhWork;

@Service("whWorkManager")
@Transactional
public class WhWorkManagerImpl extends BaseManagerImpl implements WhWorkManager {
    
    public static final Logger log = LoggerFactory.getLogger(WhWorkManagerImpl.class);

    @Autowired
    private WhWorkDao whWorkDao;
    
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    @Transactional(propagation = Propagation.SUPPORTS)
    public Boolean saveOrUpdate(WhWorkCommand whWorkCommand) {
        WhWork whWork = new WhWork();
        //复制数据        
        BeanUtils.copyProperties(whWorkCommand, whWork);
        if(null != whWorkCommand.getId() ){
            whWorkDao.saveOrUpdateByVersion(whWork);
        }else{
            whWorkDao.insert(whWork);
        }
        return null;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhWorkCommand findWorkByWorkCode(String code, Long ouId) {
        WhWorkCommand whWorkCommand = this.whWorkDao.findWorkByWorkCode(code, ouId);
        return whWorkCommand;
    }
    
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhWork findWorkByWorkId(Long id) {
        WhWork whWork = this.whWorkDao.findById(id);
        return whWork;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhWork> findWorkByWaveWithLock(String waveCode, Long ouId) {
        WhWork work = new WhWork();
        work.setWaveCode(waveCode);
        work.setOuId(ouId);
        work.setLifecycle(Constants.LIFECYCLE_START);
        work.setIsLocked(true);
        return this.whWorkDao.findListByParam(work);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhWork> findWorkByWave(String code, Long ouId) {
        WhWork work = new WhWork();
        work.setWaveCode(code);
        work.setOuId(ouId);
        work.setLifecycle(Constants.LIFECYCLE_START);
        return this.whWorkDao.findListByParam(work);
    }

    /**
     * 获取批次下的所有工作
     *
     * @param batchNo
     * @param ouId
     * @return
     */
    public List<WhWorkCommand> findWorkByBatch(String batchNo, Long ouId){
        return whWorkDao.findWorkByBatch(batchNo, ouId);
    }

    /**
     * 获取播种批次下的出库单信息，用于和播种墙货格绑定
     *
     * @param batchNo
     * @param ouId
     * @return
     */
    public List<WhSeedingWallLattice> getSeedingBatchOdoInfo(String batchNo, Long ouId){
        return whWorkDao.getSeedingBatchOdoInfo(batchNo, ouId);
    }
}
