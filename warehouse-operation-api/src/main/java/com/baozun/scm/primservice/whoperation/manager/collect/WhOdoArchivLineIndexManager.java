package com.baozun.scm.primservice.whoperation.manager.collect;

import java.util.List;

import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.collect.WhOdoArchivLineIndex;

public interface WhOdoArchivLineIndexManager extends BaseManager {

    WhOdoArchivLineIndex findByIdToShard(Long id, Long ouId);

    void executeReturns(List<WhOdoArchivLineIndex> list);
}
