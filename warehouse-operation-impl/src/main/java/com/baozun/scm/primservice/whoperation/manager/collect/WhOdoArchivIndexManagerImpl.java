package com.baozun.scm.primservice.whoperation.manager.collect;

import java.util.Date;
import java.util.List;

import lark.common.annotation.MoreDB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.collect.WhOdoArchivIndexDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.model.collect.WhOdoArchivIndex;
import com.baozun.scm.primservice.whoperation.util.HashUtil;
import com.baozun.scm.primservice.whoperation.util.StringUtil;

@Service("whOdoArchivIndexManager")
@Transactional
public class WhOdoArchivIndexManagerImpl implements WhOdoArchivIndexManager {

    protected static final Logger log = LoggerFactory.getLogger(WhOdoArchivIndexManager.class);

    @Autowired
    private WhOdoArchivIndexDao whOdoArchivIndexDao;

    /**
     * 保存仓库出库单归档索引数据
     * 
     * @param whOdoArchivIndex
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_COLLECTSOURCE)
    public int saveWhOdoArchivIndex(WhOdoArchivIndex whOdoArchivIndex) {
        log.info("WhOdoArchivIndexManagerImpl.saveWhOdoArchivIndex begin!");
        // 通过出库单电商平台订单号
        Long count = 0L;
        // 通过hash算法计算表序列号
        String serialNumber = HashUtil.serialNumberByHashCode(whOdoArchivIndex.getEcOrderCode());
        if (StringUtil.isEmpty(serialNumber)) {
            log.warn("WhOdoArchivIndexManagerImpl.saveWhOdoArchivIndex serialNumber is null EcOrderCode: " + whOdoArchivIndex.getEcOrderCode());
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        whOdoArchivIndex.setNum(serialNumber);
        whOdoArchivIndex.setCreateTime(new Date());
        count = whOdoArchivIndexDao.insert(whOdoArchivIndex);
        log.info("WhOdoArchivIndexManagerImpl.saveWhOdoArchivIndex end!");
        return count.intValue();
    }

    /**
     * 通过电商平台订单号(NOT NULL) or 数据来源(DEFAULT NULL) or 仓库组织ID(DEFAULT NULL) or wms出库单号(DEFAULT NULL)
     * 查询仓库出库单归档索引数据
     * 
     * @param ecOrderCode
     * @param dataSource
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_COLLECTSOURCE)
    public List<WhOdoArchivIndex> findWhOdoArchivIndexByEcOrderCode(String ecOrderCode, String dataSource, String wmsOdoCode, Long ouid) {
        log.info("WhOdoArchivIndexManagerImpl.findWhOdoArchivIndexByEcOrderCode begin!");
        // 验证电商平台订单号是否为空
        if (StringUtil.isEmpty(ecOrderCode)) {
            log.warn("WhOdoArchivIndexManagerImpl.findWhOdoArchivIndexByEcOrderCode ecOrderCode is null");
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        // 通过hash算法计算表序列号
        String serialNumber = HashUtil.serialNumberByHashCode(ecOrderCode);
        if (StringUtil.isEmpty(serialNumber)) {
            log.warn("WhOdoArchivIndexManagerImpl.findWhOdoArchivIndexByEcOrderCode serialNumber is null EcOrderCode: " + ecOrderCode);
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        List<WhOdoArchivIndex> whOdoArchivIndexList = whOdoArchivIndexDao.findWhOdoArchivIndexByEcOrderCode(ecOrderCode, dataSource, wmsOdoCode, serialNumber, ouid);
        log.info("WhOdoArchivIndexManagerImpl.findWhOdoArchivIndexByEcOrderCode end!");
        return whOdoArchivIndexList;
    }
    
    /**
     * 保存仓库出库单归档索引数据
     * 
     * @param whOdoArchivIndex
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_COLLECTSOURCE)
    public void saveWhOdoArchivIndexExt(WhOdoArchivIndex index) {
    	// 先判断是否已在表中存在
		List<WhOdoArchivIndex> odoArchivIndexList = this.findWhOdoArchivIndexByEcOrderCode(index.getEcOrderCode(), 
				index.getDataSource(), index.getWmsOdoCode(), index.getOuId());
		// 不存在则保存
        if (null == odoArchivIndexList || odoArchivIndexList.isEmpty()) {
			int insertCount = this.saveWhOdoArchivIndex(index);
			if (insertCount != 1) {
				throw new BusinessException(ErrorCodes.SYSTEM_EXCEPTION);
			}
		}
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    @Transactional(propagation=Propagation.NOT_SUPPORTED)
	public List<WhOdoArchivIndex> findWhOdoArchivIndexData(Long ouId) {
    	return whOdoArchivIndexDao.findWhOdoArchivIndexData(ouId);
	}

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
	public void deleteWhOdoArchivIndex(WhOdoArchivIndex index) {
    	int updateCount = whOdoArchivIndexDao.deleteWhOdoArchivIndexById(index.getId(), index.getOuId());
		if (updateCount != 1) {
			throw new BusinessException(ErrorCodes.SYSTEM_EXCEPTION);
		}
	}
}
