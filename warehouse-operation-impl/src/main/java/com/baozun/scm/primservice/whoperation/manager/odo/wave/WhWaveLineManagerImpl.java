package com.baozun.scm.primservice.whoperation.manager.odo.wave;

import java.util.List;

import lark.common.annotation.MoreDB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.constant.WaveStatus;
import com.baozun.scm.primservice.whoperation.dao.odo.wave.WhWaveLineDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.BaseModel;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWaveLine;

@Service("whWaveLineManager")
@Transactional
public class WhWaveLineManagerImpl extends BaseManagerImpl implements WhWaveLineManager {

    public static final Logger log = LoggerFactory.getLogger(WhWaveLineManagerImpl.class);

    @Autowired
    private WhWaveLineDao whWaveLineDao;

    /**
     * 得到所有硬阶段的波次名次行集合
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhWaveLine> getHardAllocationWhWaveLine(Integer allocatePhase, Long ouId) {
        log.info("WhWaveLineManagerImpl getHardAllocationWhWaveLine is start");
        List<WhWaveLine> datas = whWaveLineDao.getNeedAllocationRuleWhWaveLine(allocatePhase, ouId);
        log.info("WhWaveLineManagerImpl getHardAllocationWhWaveLine is end");
        return datas;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhWaveLine> getWaveLineByParam(WhWaveLine whWaveLine) {
        List<WhWaveLine> whWaveLineList = whWaveLineDao.findListByParam(whWaveLine);
        return whWaveLineList;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhWaveLine> getSoftAllocationWhWaveLine(Long waveId, Long ouId) {
        List<WhWaveLine> whWaveLineList = this.whWaveLineDao.findSoftAllocationWhWaveLine(waveId, ouId, WaveStatus.WAVE_EXECUTING, BaseModel.LIFECYCLE_NORMAL);
        return whWaveLineList;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhWaveLine getWaveLineByIdAndOuId(Long waveLineId, Long ouId) {
        WhWaveLine whWaveLine = new WhWaveLine();
        whWaveLine.setId(waveLineId);
        whWaveLine.setOuId(ouId);
        List<WhWaveLine> whWaveLineList = this.whWaveLineDao.findListByParam(whWaveLine);
        if (null == whWaveLineList || 1 != whWaveLineList.size()) {
            throw new BusinessException("多个波次明细");
        }
        return whWaveLineList.get(0);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhWaveLine> findWaveLineListByWaveId(Long waveId, Long ouId) {
        WhWaveLine whWaveLine = new WhWaveLine();
        whWaveLine.setWaveId(waveId);
        whWaveLine.setOuId(ouId);
        List<WhWaveLine> whWaveLineList = this.whWaveLineDao.findListByParam(whWaveLine);
        return whWaveLineList;
    }

}
