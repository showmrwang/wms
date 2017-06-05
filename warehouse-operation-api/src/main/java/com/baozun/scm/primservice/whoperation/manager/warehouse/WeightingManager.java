package com.baozun.scm.primservice.whoperation.manager.warehouse;

import com.baozun.scm.primservice.whoperation.command.warehouse.WeightingCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingByOdoResultCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;

public interface WeightingManager extends BaseManager {

    /**
     * [业务方法] 根据输入返回页面显示
     * @param command
     * @return
     */
    WeightingCommand findInfoByInput(WeightingCommand command);

    /**
     * [业务方法] 根据输入返回页面显示
     * @param command
     * @return
     */
    WeightingCommand inputResponseForChecking(WeightingCommand command);

    /**
     * [业务方法] 校验输入是否正确
     * @param command
     * @return
     */
    Boolean checkParam(WeightingCommand command);

    /**
     * [业务方法] 称重
     * @param command
     */
    WhCheckingByOdoResultCommand weighting(WeightingCommand command, Long userId);


}
