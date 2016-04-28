package com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager;

import java.util.List;
import java.util.Map;

import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import com.baozun.scm.primservice.whoperation.command.poasn.BiPoCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.poasn.BiPo;

public interface BiPoManager extends BaseManager {
    /**
     * 通用逻辑 根据字段查找集合
     * 
     * @param biPo
     * @return
     */
    List<BiPo> findListByParam(BiPo biPo);

    BiPo findBiPoById(Long id);

    BiPo findBiPoByPoCode(String poCode);

    /**
     * 根据id关联查询 关联 customer,store,sysdictionary[potype],t_wh_logistics_provider,t_wh_supplier
     * 
     * @param code
     * @return
     */
    BiPoCommand findBiPoCommandById(Long id);

    /**
     * 根据pocode关联查询 关联 customer,store,sysdictionary[potype],t_wh_logistics_provider,t_wh_supplier
     * 
     * @param code
     * @return
     */
    BiPoCommand findBiPoCommandByPoCode(String poCode);

    /**
     * BiPo一览查询
     * 
     * @param page
     * @param sorts
     * @param params
     * @return
     */
    Pagination<BiPoCommand> findListByQueryMapWithPageExtByInfo(Page page, Sort[] sorts, Map<String, Object> params);

}
