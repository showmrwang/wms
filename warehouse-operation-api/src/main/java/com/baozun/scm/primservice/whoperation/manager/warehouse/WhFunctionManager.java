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
import java.util.Map;

import com.baozun.scm.primservice.whoperation.command.warehouse.WhFunctionCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFunction;

import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

public interface WhFunctionManager extends BaseManager {

    Pagination<WhFunctionCommand> getListByParams(Page page, Sort[] sorts, Map<String, Object> params);

    WhFunction findWhFunctionById(Long id, Long ouid);

    WhFunctionCommand checkNameOrCode(String name, String code, String templet, Long ouid);

    List<WhFunction> findWhFunctionListNotIsSys(Long ouid);

    int updateLifeCycle(List<Long> ids, Integer lifeCycle, Long userid, Long ouid);

    void deleteFunction(List<Long> ids, Long userid, Long ouid);

    List<WhFunction> findWhFunctionByParam(WhFunction whFunction);

}
