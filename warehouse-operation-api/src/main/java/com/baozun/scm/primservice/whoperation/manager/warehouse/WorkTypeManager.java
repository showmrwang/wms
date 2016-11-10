package com.baozun.scm.primservice.whoperation.manager.warehouse;

import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.WorkType;

public interface WorkTypeManager extends BaseManager {

    /**
     * 根据工作类别获取工作类型编码
     * @param List<WhOdoOutBoundBox>
     * @return
     */
    public WorkType findWorkType(String workCategory, Long ouId);

}
