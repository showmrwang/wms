package com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager;

import java.util.List;

import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsnSn;

public interface AsnSnManager extends BaseManager {

    List<WhAsnSn> findListByParamToShard(WhAsnSn sn);
}
