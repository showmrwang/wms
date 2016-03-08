package com.baozun.scm.primservice.whoperation.zk;

import org.springframework.beans.factory.annotation.Value;

import com.baozun.zkpro.bean.ZkDateChangeManager;

public class ZkDateChangeManagerImpl implements ZkDateChangeManager {

    @Value("${zk.config.app.root}")
    private String sysconfigPath;


    // @Autowired
    // private SystemConfigMangaer systemConfigMangaer;



    boolean isCheckTask = false;

    @Override
    public void changeData(String dataPath, Object data) {


        if (dataPath.equals(sysconfigPath)) {
            // systemConfigMangaer.init();
        }

    }

    @Override
    public void deleteData(String dataPath) {

    }



}
