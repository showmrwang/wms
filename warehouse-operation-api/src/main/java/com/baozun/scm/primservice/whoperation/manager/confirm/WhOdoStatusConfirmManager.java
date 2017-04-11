package com.baozun.scm.primservice.whoperation.manager.confirm;

import java.util.List;

import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.confirm.WhOdoStatusConfirm;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;

public interface WhOdoStatusConfirmManager extends BaseManager {

    /***
     * 生成出库单状态反馈数据
     * 
     * @param whOdo
     * @return
     */
    void saveWhOdoStatusConfirm(WhOdo whOdo);

    /**
     * 通过创建时间段+仓库ID+数据来源获取对应出库单状态反馈数据
     * 
     * @param beginTime
     * @param endTime
     * @param ouid
     * @param dataSource
     * @return
     */
    List<WhOdoStatusConfirm> findWhOdoStatusConfirmByCreateTimeAndDataSource(String beginTime, String endTime, Long ouid, String dataSource);

}
