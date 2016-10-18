package com.baozun.scm.primservice.whoperation.manager.odo;

import java.util.List;
import java.util.Map;

import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baozun.scm.primservice.whoperation.command.odo.OdoCommand;
import com.baozun.scm.primservice.whoperation.command.odo.OdoResultCommand;
import com.baozun.scm.primservice.whoperation.manager.odo.merge.OdoMergeManager;

@Service("odoMergeManagerProxy")
public class OdoMergeManagerProxyImpl implements OdoMergeManagerProxy {

    @Autowired
    private OdoMergeManager odoMergeManager;

    @Override
    public Pagination<OdoResultCommand> findOdoListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params) {
        return this.odoMergeManager.findListByQueryMapWithPageExt(page, sorts, params);
    }

    @Override
    public Map<String, String> odoMerge(List<String> odoIds, List<String> options, Long ouId, Long userId) {
        Map<String, String> response = this.odoMergeManager.odoMerge(odoIds, options, ouId, userId);
        return response;
    }

    @Override
    public List<OdoCommand> findOdoList(String ids, Long ouId, String odoStatus) {
        List<OdoCommand> idList = this.odoMergeManager.findOdoList(ids, ouId, odoStatus);
        return idList;
    }

}
