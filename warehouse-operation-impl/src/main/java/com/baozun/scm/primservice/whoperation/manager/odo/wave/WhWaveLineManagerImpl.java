package com.baozun.scm.primservice.whoperation.manager.odo.wave;

import java.util.List;

import lark.common.annotation.MoreDB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.odo.wave.WhWaveLineDao;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
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

}
