package com.baozun.scm.primservice.whoperation.manager;

import lark.common.annotation.MoreDB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.warehouse.WhDistributionPatternRuleCommand;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhDistributionPatternRuleDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhDistributionPatternRuleManager;


@Service("whDistributionPatternRuleManager")
@Transactional
public class WhDistributionPatternRuleManagerImpl extends BaseManagerImpl implements WhDistributionPatternRuleManager {

    public static final Logger log = LoggerFactory.getLogger(WhDistributionPatternRuleManagerImpl.class);
    
    @Autowired
    private WhDistributionPatternRuleDao whDistributionPatternRuleDao;

    /**
     * 根据code查找配货模式规则
     *
     * @author qiming.liu
     * @param distributionPatternCode
     * @param ouId
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhDistributionPatternRuleCommand findRuleByCode(String distributionPatternCode, Long ouId) {
        if (log.isInfoEnabled()) {
            log.info("WhDistributionPatternRuleManagerImpl findRuleByCode is start");
        }
        if (log.isDebugEnabled()) {
            log.debug("findRuleByCode param [id:{}, ouId:{}", distributionPatternCode, ouId);
        }
        
        if (null == distributionPatternCode || null == ouId) {
            log.error("WhDistributionPatternRuleManagerImpl findRuleByCode failed, param is null, param [distributionPatternCode:{}, ouId:{}", distributionPatternCode, ouId);
            throw new BusinessException(ErrorCodes.PARAM_IS_NULL);
        }
        // 根据id查找配货模式规则对象
        WhDistributionPatternRuleCommand ruleCommand = whDistributionPatternRuleDao.findRuleByCode(distributionPatternCode, ouId);
        
        if (null == ruleCommand) {
            log.error("PlatformRecommendRuleManagerImpl findRuleByCode failed, select result is null, param [distributionPatternCode:{}, ouId:{}", distributionPatternCode, ouId);
            throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
        }
        
        if (log.isInfoEnabled()) {
            log.info("WhDistributionPatternRuleManagerImpl findRuleByCode is end");
        }
        
        return ruleCommand;
    }

}
