package com.baozun.scm.primservice.whinterface.manager;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whinterface.model.WmsInBound;
import com.baozun.scm.primservice.whinterface.model.outbound.WmsOutBoundLocked;
import com.baozun.scm.primservice.whinterface.msg.WmsResponse;
import com.baozun.scm.primservice.whinterface.service.WmsService;

@Service("wmsService")
@Transactional
public class WmsServiceImpl implements WmsService {

    @Override
    public WmsResponse wmsInBound(WmsInBound inBound) {
        return null;
    }

    @Override
    public WmsResponse wmsOutBoundLocked(WmsOutBoundLocked outBoundLocked) {
        return null;
    }

}
