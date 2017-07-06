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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.warehouse.WhOdoPackageInfoCommand;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOdoPackageInfoDao;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOdoPackageInfo;

@Service("whOdoPackageInfoManager")
@Transactional
public class WhOdoPackageInfoManagerImpl extends BaseManagerImpl implements WhOdoPackageInfoManager {
    
    public static final Logger log = LoggerFactory.getLogger(WhOdoPackageInfoManagerImpl.class);
    
    @Autowired
    private WhOdoPackageInfoDao whOdoPackageInfoDao;

    @Override
    public void saveOrUpdate(WhOdoPackageInfoCommand whOdoPackageInfoCommand) {
        WhOdoPackageInfo whOdoPackageInfo = new WhOdoPackageInfo();
        //复制数据        
        BeanUtils.copyProperties(whOdoPackageInfoCommand, whOdoPackageInfo);
        if(null != whOdoPackageInfoCommand.getId() ){
            whOdoPackageInfoDao.saveOrUpdate(whOdoPackageInfo);
        }else{
            whOdoPackageInfoDao.insert(whOdoPackageInfo);
        }
    }
    
    /**
     * [通用方法] 通过出出库箱编码, 组织id查找出库单打包信息
     * 
     * @param outboundBoxCode
     * @param ouId
     * @return
     */
    public WhOdoPackageInfo findByOutboundBoxCode(String outboundboxCode,Long ouId){
        return whOdoPackageInfoDao.findByOutboundBoxCode(outboundboxCode,ouId);
    }

}
