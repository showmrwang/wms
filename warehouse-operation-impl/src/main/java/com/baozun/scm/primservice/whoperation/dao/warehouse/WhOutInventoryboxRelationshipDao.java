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
package com.baozun.scm.primservice.whoperation.dao.warehouse;

import java.util.List;
import java.util.Map;

import lark.common.annotation.QueryPage;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;
import lark.orm.dao.supports.BaseDao;

import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutInventoryboxRelationship;


public interface WhOutInventoryboxRelationshipDao extends BaseDao<WhOutInventoryboxRelationship, Long> {


    @QueryPage("findListCountByQueryMap")
    Pagination<WhOutInventoryboxRelationship> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    /**
     * 保存出库箱与店铺客户的关系对象
     * 
     * @author shenlijun
     * @param inventoryboxRelationship
     * @return
     */
    int save(WhOutInventoryboxRelationship inventoryboxRelationship);


    /**
     * 查询是否有重复的关系对象
     * 
     * @author shenlijun
     * @param inventoryboxRelationship
     * @return
     */
    List<WhOutInventoryboxRelationship> findRelationList(WhOutInventoryboxRelationship inventoryboxRelationship);


    /**
     * 更新关系对象
     * 
     * @author shenlijun
     * @param inventoryboxRelationship
     * @return
     */
    int updateRelation(WhOutInventoryboxRelationship inventoryboxRelationship);


    /**
     * 根据参数查询出库箱和店铺或客户关系
     * 
     * @author shenlijun
     * @param inventoryboxRelationship
     * @return
     */
    List<WhOutInventoryboxRelationship> getListByPramas(WhOutInventoryboxRelationship inventoryboxRelationship);

    /**
     * 根据出库箱id删除该出库箱对应的店铺客户关系
     * 
     * @author shenlijun
     * @param outId
     * @return
     */
    int deleteByOutId(Long outId);

    /**
     * 根据参数删除数据
     * 
     * @author shenlijun
     * @param inventoryboxRelationship
     * @return
     */
    int deleteByPramas(WhOutInventoryboxRelationship inventoryboxRelationship);
}
