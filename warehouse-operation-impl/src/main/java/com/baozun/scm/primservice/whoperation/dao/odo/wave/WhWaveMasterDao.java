/**
 * Copyright (c) 2013 Baozun All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Baozun.
 * You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Baozun.
 *
 * BAOZUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. BAOZUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 *
 */
package com.baozun.scm.primservice.whoperation.dao.odo.wave;

import java.util.List;
import java.util.Map;

import lark.common.annotation.CommonQuery;
import lark.common.annotation.QueryPage;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;
import lark.orm.dao.supports.BaseDao;

import org.apache.ibatis.annotations.Param;

import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWaveMaster;



public interface WhWaveMasterDao extends BaseDao<WhWaveMaster,Long>{


	@QueryPage("findListCountByQueryMap")
	Pagination<WhWaveMaster> findListByQueryMapWithPage(Page page,Sort[] sorts,Map<String, Object> params);
	
	@CommonQuery
	int saveOrUpdate(WhWaveMaster o);
	
	/**
	 * 修改波次主档
	 * @param o
	 * @return
	 */
	@CommonQuery
    int saveOrUpdateByVersion(WhWaveMaster o);
	
	/**
	 * 删除波次主档
	 * @param id
	 * @param ouId
	 * @return
	 */
	public int deleteExt(@Param("id") Long id,@Param("ouId") Long ouId);
	
	/**
	 * 根据主键查询波次主档
	 * @param id
	 * @param ouId
	 * @return
	 */
	public WhWaveMaster findByIdExt(@Param("id") Long id,@Param("ouId") Long ouId);
	
	/***
     * 判断波次主档名称和编码的唯一性
     * @param whWaveMaster
     * @return
     */
    public Long checkUnique(WhWaveMaster whWaveMaster);
    
    
    /**
     * 根据波次模板id，查询波次主档信息
     * @param waveTempalteId
     * @param ouId
     * @return
     */
    public List<WhWaveMaster> findWhWaveMasterByTemplateId(@Param("waveTemplateId") Long waveTempalteId,@Param("ouId") Long ouId);
    
    
    /**
     * 根据波次条件id，查询波次主档
     * @param waveTempalteId
     * @param ouId
     * @return
     */
    public List<WhWaveMaster> findWhWaveMasterConditionId(@Param("waveConditionRuleId") Long waveConditionRuleId,@Param("ouId") Long ouId);
	
	
}
