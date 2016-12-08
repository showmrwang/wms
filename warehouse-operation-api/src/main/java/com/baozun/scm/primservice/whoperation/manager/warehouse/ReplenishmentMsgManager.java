package com.baozun.scm.primservice.whoperation.manager.warehouse;

import java.util.List;

import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.ReplenishmentMsg;

public interface ReplenishmentMsgManager extends BaseManager {

    ReplenishmentMsg findMsgbyLocIdAndSkuId(Long locId, Long skuId, Long ouId);

    void deleteById(Long id, Long ouId);

    void insert(ReplenishmentMsg msg);

    void updateByVersion(ReplenishmentMsg msg);

    List<ReplenishmentMsg> findMsgByOuId(Long ouId);
}
