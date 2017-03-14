package com.baozun.scm.primservice.whoperation.manager.warehouse;

import java.util.List;
import java.util.Map;

import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import com.baozun.scm.primservice.whoperation.command.warehouse.OutInvBoxTypeCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.OutBoundBoxType;

public interface OutBoundBoxTypeManager extends BaseManager {
    
    /**
     * 查询出库箱类型
     * @param page
     * @param sorts
     * @param param
     * @return
     */
    public Pagination<OutInvBoxTypeCommand> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> param);
    
    /**
     * 根据id 和ouId 获取出库箱类型
     * @param id
     * @param ouId
     * @return
     */
    public OutInvBoxTypeCommand findOutInventoryBoxType(Long id, Long ouId);
    
    
    /**
     * 保存或更新出库箱类型
     * @param out
     */
    public void saveOrUpdate(OutInvBoxTypeCommand command, Long userId);
    
    /**
     * 批量停用/启用
     * @param idList
     * @param lifeCycle
     * @param userId
     * @param ouId
     */
    public void updateLifeCycle(List<Long> idList, Integer lifeCycle, Long userId, Long ouId);
    
    /**
     * 根据参数获取出库箱类型
     * @param o
     * @return
     */
    public Boolean checkUnique(OutInvBoxTypeCommand o);

    List<OutBoundBoxType> findListByParamExt(OutBoundBoxType o);


}
