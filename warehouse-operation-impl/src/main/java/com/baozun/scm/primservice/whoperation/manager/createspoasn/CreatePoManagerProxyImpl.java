package com.baozun.scm.primservice.whoperation.manager.createspoasn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baozun.scm.primservice.whoperation.command.poasn.WhPoCommand;

@Service("createPoManagerProxy")
public class CreatePoManagerProxyImpl implements CreatePoManagerProxy {
    @Autowired
    private CreatesPoManager poManager;

    @Override
    public int saveOrUpdate(WhPoCommand command, Long userId) {
        //保存操作
        return 0;
    }

}
