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
package com.baozun.scm.primservice.whoperation.dao.warehouse;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import lark.common.annotation.CommonQuery;
import lark.common.annotation.QueryPage;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;
import lark.orm.dao.supports.BaseDao;

import com.baozun.scm.primservice.whoperation.command.warehouse.WhSeedingWallRuleCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhSeedingWallRule;

public interface WhSeedingWallRuleDao extends BaseDao<WhSeedingWallRule, Long>{

	@QueryPage("findListCountByQueryMap")
	Pagination<WhSeedingWallRule> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);
	
	@CommonQuery
	int saveOrUpdate(WhSeedingWallRule o);
	
	@CommonQuery
	int saveOrUpdateByVersion(WhSeedingWallRule o);
	
	/**
	 * 获取分页数据
	 * @param page
	 * @param sorts
	 * @param param
	 * @return
	 */
	@QueryPage("findListCountByQueryMapExt")
	Pagination<WhSeedingWallRuleCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> param);
	
	WhSeedingWallRule findByIdAndOuId(@Param("id") Long id, @Param("ouId") Long ouId);
	
	WhSeedingWallRuleCommand findByIdExt(@Param("id") Long id, @Param("ouId") Long ouId);
	
	/**
     * 验证规则名称、编号、优先级是否唯一
     *
     * @param checkOperationsAreaRuleCommand
     * @return
     */
    int checkUnique(WhSeedingWallRuleCommand whSeedingWallRuleCommand);

    /**
     * 检查规则是否可用
     *
     * @param ouId
     * @param ruleSql
     * @return
     */
    List<Long> executeRuleSql(@Param("ruleSql") String ruleSql, @Param("ouid") Long ouId);
    
    /**
     * 查找仓库下所有可用的规则
     * @param ouId
     * @return
     */
	List<WhSeedingWallRuleCommand> findAllSeedingWallRules(Long ouId);
}
