package com.baozun.scm.primservice.whoperation.manager.pda.concentration;

import com.baozun.scm.primservice.whoperation.command.pda.collection.WorkCollectionCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;

/**
 * PDA-集货
 * @author jumbo
 *
 */
public interface PdaConcentrationManager extends BaseManager {
    /**
     * [业务方法] 获取推荐路径
     * @param workCollectionCommand
     * @return RecFacilityPathCommand
     */
    WorkCollectionCommand recommendSeedingWall(WorkCollectionCommand workCollectionCommand);

    /**
     * [业务方法] 获取目标位置
     * @param command
     * @return
     */
    String findTargetPos(WorkCollectionCommand command);

    /**
     * [业务方法] 校验并且移动容器
     * @param workCollectionCommand
     * @return targetPos$containerCode
     */
    Boolean checkAndMoveContainer(WorkCollectionCommand workCollectionCommand);

    /**
     * [通用方法] 清理缓存: cache+userId, batch
     * @param workCollectionCommand
     */
    void cleanCache(WorkCollectionCommand workCollectionCommand);
}
