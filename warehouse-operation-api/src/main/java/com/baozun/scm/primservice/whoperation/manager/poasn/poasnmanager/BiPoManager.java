package com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager;

import java.util.List;
import java.util.Map;

import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import com.baozun.scm.primservice.whoperation.command.poasn.BiPoCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.ResponseMsg;
import com.baozun.scm.primservice.whoperation.model.poasn.BiPo;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPo;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPoLine;

/**
 * @author yimin.lu
 * @author Administrator
 *
 */
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


    /**
     * 创建PO单分支一：创建INFO库PO单;@author yimin.lu 逻辑:①当创建的PO单没有仓库时候，只插入到BIPO中，不插入info库的whpo中；并不需要调用另一个分支
     * 逻辑:②当创建的PO单有仓库的时候，数据会同步到INFO库的whpo和shard库的whpo;此时不可单独使用
     * 
     * @param whPo
     * @param whPoLines
     * @param rm
     * @return
     */
    ResponseMsg createPoAndLineToInfo(WhPo whPo, List<WhPoLine> whPoLines, ResponseMsg rm);

    /**
     * 创建PO单分支二：创建shard库PO单；
     * 
     * @param whPo
     * @param whPoLines
     * @param rm
     * @return
     */
    ResponseMsg createPoAndLineToShared(WhPo whPo, List<WhPoLine> whPoLines, ResponseMsg rm);

    /**
     * 删除BIPO单操作：删除BIPO及明细
     * 
     * @param id
     * @param userId
     * @return
     */
    ResponseMsg deleteBiPoAndLine(Long id, Long userId);

    /**
     * 取消BIPO单操作：将BIPO及其明细置为取消状态
     * 
     * @param id
     * @param userId
     * @return
     */
    ResponseMsg cancelBiPo(Long id, Long userId);

    /**
     * 编辑BIPO单表头
     * 
     * @param updatePo
     * @return
     */
    ResponseMsg editBiPo(BiPo updatePo);

    void createSubPoToInfo(WhPo po, List<WhPoLine> whPoLineList);

}
