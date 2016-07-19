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

import java.util.Date;
import java.util.List;
import java.util.Map;

import lark.common.annotation.CommonQuery;
import lark.common.annotation.QueryPage;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;
import lark.orm.dao.supports.BaseDao;

import org.apache.ibatis.annotations.Param;

import com.baozun.scm.primservice.whoperation.command.warehouse.AsnReserveCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.AsnReserve;


public interface AsnReserveDao extends BaseDao<AsnReserve, Long> {

    /**
     * 通过参数查询ASN预约分页列表
     *
     * @param page
     * @param sorts
     * @param params
     * @return
     */
    @QueryPage("findListCountByQueryMapExt")
    Pagination<AsnReserveCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);


    @CommonQuery
    int saveOrUpdate(AsnReserve o);

    @CommonQuery
    int saveOrUpdateByVersion(AsnReserve o);

    /**
     * 根据ID查找预约信息
     *
     * @author mingwei.xie
     * @param asnReserveId
     * @param ouId
     * @return
     */
    AsnReserve getAsnReserveById(@Param("asnReserveId") Long asnReserveId, @Param("ouId") Long ouId);

    /**
     * 根据ID查找预约信息
     *
     * @author mingwei.xie
     * @param asnReserveId
     * @param ouId
     * @return
     */
    AsnReserveCommand getAsnReserveCommandById(@Param("asnReserveId") Long asnReserveId, @Param("ouId") Long ouId);

    /**
     * 根据日期查询当天的预约信息
     *
     * @author mingwei.xie
     * @param reserveDate
     * @param ouId
     * @return
     */
    List<AsnReserveCommand> findAsnReserveCommandByEtaDate(@Param("reserveDate") Date reserveDate, @Param("ouId") Long ouId);

    /**
     * 取消预约，执行物理删除
     *
     * @author mingwei.xie
     * @param asnReserveId
     * @return
     */
    int removeAsnReserve(@Param("asnReserveId") Long asnReserveId, @Param("ouId") Long ouId);

    /**
     * 验证预约号是否已存在
     *
     * @author mingwei.xie
     * @param code
     * @param ouId
     * @return
     */
    int checkAsnReserveCodeUnique(@Param("code") String code, @Param("ouId") Long ouId);

    /**
     * 根据asnId查找预约信息
     *
     * @author mingwei.xie
     * @param asnId
     * @param ouId
     * @return
     */
    AsnReserve findAsnReserveByAsnId(@Param("asnId") Long asnId, @Param("ouId") Long ouId);

}
