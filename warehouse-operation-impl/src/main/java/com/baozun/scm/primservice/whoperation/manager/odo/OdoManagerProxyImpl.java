package com.baozun.scm.primservice.whoperation.manager.odo;

import java.util.Map;

import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baozun.scm.primservice.whoperation.command.odo.OdoResultCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.odo.manager.OdoManager;

@Service("odoManagerProxy")
public class OdoManagerProxyImpl extends BaseManagerImpl implements OdoManagerProxy {
    @Autowired
    private OdoManager odoManager;
    @Override
    public Pagination<OdoResultCommand> findOdoListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params) {
        return this.odoManager.findListByQueryMapWithPageExt(page, sorts, params);
    }

}
