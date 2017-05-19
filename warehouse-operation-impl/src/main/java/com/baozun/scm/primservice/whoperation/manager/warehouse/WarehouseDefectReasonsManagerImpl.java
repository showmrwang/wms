/**
 * Copyright (c) 2013 Baozun All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Baozun. You shall not disclose
 * such Confidential Information and shall use it only in accordance with the terms of the license
 * agreement you entered into with Baozun.
 * 
 * BAOZUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. BAOZUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES.
 * 
 */
package com.baozun.scm.primservice.whoperation.manager.warehouse;

import java.util.List;

import lark.common.annotation.MoreDB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.warehouse.conf.basis.WarehouseDefectReasonsCommand;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.warehouse.conf.basis.WarehouseDefectReasonsDao;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.warehouse.conf.basis.WarehouseDefectReasons;


@Service("warehouseDefectReasonsManager")
@Transactional
public class WarehouseDefectReasonsManagerImpl extends BaseManagerImpl implements WarehouseDefectReasonsManager {
    
    public static final Logger log = LoggerFactory.getLogger(WarehouseDefectReasonsManagerImpl.class);

    @Autowired
    private WarehouseDefectReasonsDao warehouseDefectReasonsDao;

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WarehouseDefectReasonsCommand> findWarehouseDefectReasonsByDefectTypeIds(Long typeId, Long ouId) {
        return this.warehouseDefectReasonsDao.findWarehouseDefectReasonsByTypeId(typeId, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WarehouseDefectReasons findWarehouseDefectReasonsByTypeIdAndReasonCode(Long defectTypeId, String defectReason, Long ouId) {
        WarehouseDefectReasons search = new WarehouseDefectReasons();
        search.setDefectTypeId(defectTypeId);
        search.setOuId(ouId);
        search.setCode(defectReason);
        List<WarehouseDefectReasons> result = this.warehouseDefectReasonsDao.findListByParam(search);
        if (result == null || result.size() == 0) {
            return null;
        }
        return result.get(0);
    }



}
