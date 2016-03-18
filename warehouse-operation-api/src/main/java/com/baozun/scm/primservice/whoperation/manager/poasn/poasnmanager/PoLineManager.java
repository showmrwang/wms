package com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager;

import java.util.List;
import java.util.Map;

import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import com.baozun.scm.primservice.whoperation.command.poasn.WhPoLineCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPoLine;

public interface PoLineManager extends BaseManager {

    void createPoLineSingleToInfo(WhPoLine whPoLine);

    void createPoLineSingleToShare(WhPoLine whPoLine);

    Pagination<WhPoLineCommand> findListByQueryMapWithPageExtByInfo(Page page, Sort[] sorts, Map<String, Object> params);

    Pagination<WhPoLineCommand> findListByQueryMapWithPageExtByShard(Page page, Sort[] sorts, Map<String, Object> params);

    void deletePoLineByUuidToInfo(WhPoLineCommand WhPoLine);

    void deletePoLineByUuidToShard(WhPoLineCommand WhPoLine);

    void deletePoLineByUuidNotNullToInfo(Long poid, Long ouid);

    void deletePoLineByUuidNotNullToShard(Long poid, Long ouid);

    void editPoLineToInfo(WhPoLine whPoLine);

    void editPoLineToShare(WhPoLine whPoLine);

    WhPoLineCommand findPoLinebyIdToInfo(WhPoLineCommand command);

    WhPoLineCommand findPoLinebyIdToShard(WhPoLineCommand command);

    int editPoLineStatusToInfo(WhPoLineCommand command);

    int editPoLineStatusToShard(WhPoLineCommand command);

    WhPoLine findPoLineByAddPoLineParamToInfo(WhPoLine line, Boolean type);

    WhPoLine findPoLineByAddPoLineParamToShare(WhPoLine line, Boolean type);

    void updatePoLineSingleToInfo(WhPoLine whPoLine);

    void updatePoLineSingleToShare(WhPoLine whPoLine);

    void createPoLineBatchToInfo(WhPoLineCommand whPoLine);

    void createPoLineBatchToShare(WhPoLineCommand whPoLine);

    List<WhPoLine> findWhPoLineListByPoIdToInfo(Long poid, Long ouid);

    List<WhPoLine> findWhPoLineListByPoIdToShard(Long poid, Long ouid);

    void saveOrUpdateByVersionToInfo(WhPoLine o);

    void saveOrUpdateByVersionToShard(WhPoLine o);

    int deletePoLinesToInfo(List<WhPoLine> lineList);

    int deletePoLinesToShard(List<WhPoLine> lineList);

    void batchUpdatePoLine(List<WhPoLine> polineList);
}
