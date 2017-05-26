package com.baozun.scm.primservice.whoperation.manager.handover;

import java.util.List;

import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.handover.WhOutboundDeliveryConfirm;

public interface WhOutboundDeliveryConfirmManager extends BaseManager {

    List<WhOutboundDeliveryConfirm> findAll();

    void saveOrUpdate(WhOutboundDeliveryConfirm whOutboundDeliveryConfirm);

}
