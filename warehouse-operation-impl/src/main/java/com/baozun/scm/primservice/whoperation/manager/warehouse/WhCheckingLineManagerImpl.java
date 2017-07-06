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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingLineCommand;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhCheckingLineDao;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhCheckingLine;

@Service("whCheckingLineManager")
@Transactional
public class WhCheckingLineManagerImpl extends BaseManagerImpl implements WhCheckingLineManager {
    
    public static final Logger log = LoggerFactory.getLogger(WhCheckingLineManagerImpl.class);

    @Autowired
    private WhCheckingLineDao whCheckingLineDao;
    
    @Override
    public List<WhCheckingLineCommand> getCheckingLineByCheckingId(Long checkingId, Long ouId) {
        List<WhCheckingLineCommand> checkingLineList = whCheckingLineDao.getCheckingLineCommandByCheckingId(checkingId, ouId);

        return checkingLineList;
    }

    @Override
    public void saveOrUpdate(WhCheckingLineCommand whCheckingLineCommand) {
        WhCheckingLine whCheckingLine = new WhCheckingLine();
        //复制数据        
        BeanUtils.copyProperties(whCheckingLineCommand, whCheckingLine);
        if(null != whCheckingLineCommand.getId() ){
            whCheckingLineDao.saveOrUpdate(whCheckingLine);
        }else{
            whCheckingLineDao.insert(whCheckingLine);
        }
    }
    
    
    /**
     * 修改、复合明细
     * @param line
     */
    public void saveOrUpdateByVersion(WhCheckingLine line){
        whCheckingLineDao.saveOrUpdateByVersion(line);
    }
    
    /**
     * 根据Id查询复合明细
     * @param id
     * @param ouId
     * @return
     */
    public WhCheckingLineCommand getCheckingLineById(Long id,Long ouId){
        
        return whCheckingLineDao.findCheckingLineById(id, ouId);
    }
    
    /***判断当前是否是最后一箱
     * 
     * @param ouId
     * @param odoId
     * @return
     */
    public Boolean judeIsLastBox(Long ouId,Long odoId){
        Boolean result = false;  //默认不是最后一箱
        WhCheckingLineCommand cmd =  whCheckingLineDao.judeIsLastBox(ouId, odoId);
        if(cmd.getQty().equals(cmd.getCheckingQty())) {
            result = true;
        }
       return result;
    }
}
