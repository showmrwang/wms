/**
 * Copyright (c) 2013 Baozun All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Baozun. You shall not disclose
 * such Confidential Information and shall use it only in accordance with the terms of the license
 * agreement you entered into with Baozun.
 * <p/>
 * BAOZUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. BAOZUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES.
 */

package com.baozun.scm.primservice.whoperation.manager.warehouse;

import java.util.List;

import lark.common.annotation.MoreDB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhPrintInfoDao;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhPrintInfo;

@Service("whPrintInfoManager")
public class WhPrintInfoManagerImpl extends BaseManagerImpl implements WhPrintInfoManager {

    public static final Logger log = LoggerFactory.getLogger(WhPrintInfoManagerImpl.class);

    @Autowired
    private WhPrintInfoDao whPrintInfoDao;

    /**
     * 打印信息表
     * 
     * @param outboundboxCode
     * @param checkingPrint
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhPrintInfo> findByOutboundboxCodeAndPrintType(String outboundboxCode, String checkingPrint, Long ouId) {
        return whPrintInfoDao.findByOutboundboxCodeAndPrintType(outboundboxCode, checkingPrint, ouId);
    }

    @Override
    public void saveOrUpdate(WhPrintInfo whPrintInfo) {
        whPrintInfoDao.insert(whPrintInfo);
    }

    @Override
    public WhPrintInfo findFromcheckingCollectionByOutboundboxCode(String outboundboxCode) {
        return whPrintInfoDao.findFromcheckingCollectionByOutboundboxCode(outboundboxCode);
    }

}
