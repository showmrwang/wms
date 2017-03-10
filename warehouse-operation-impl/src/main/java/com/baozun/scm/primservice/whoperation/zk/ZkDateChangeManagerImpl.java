package com.baozun.scm.primservice.whoperation.zk;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.baozun.zkpro.bean.ZkDateChangeManager;

public class ZkDateChangeManagerImpl implements ZkDateChangeManager {

    protected static final Logger log = LoggerFactory.getLogger(ZkDateChangeManagerImpl.class);
    @Value("${zk.config.app.root}")
    private String sysconfigPath;
    // @Value("${zk.notice.log.change}")
    private String logChangePath;

    // @Autowired
    // private SystemConfigMangaer systemConfigMangaer;



    boolean isCheckTask = false;

    @Override
    public void changeData(String dataPath, Object data) {


        if (dataPath.equals(sysconfigPath)) {
            // systemConfigMangaer.init();
        }

        if (dataPath.equals(logChangePath) && data != null) {

            changeLogLevel(data);

        }

    }

    /**
     * 动态调整日志
     * 
     * @param data
     */
    private void changeLogLevel(Object data) {
        String strSource = (String) data;

        String[] strTops = strSource.split(";");

        for (int i = 0; i < strTops.length; i++) {
            String[] strs = strTops[i].split("-");

            if (strs.length > 1) {
                String packagePath = strs[0];
                String strlevel = strs[1];
                Level level = Level.toLevel(strlevel);
                org.apache.log4j.Logger logger = LogManager.getLogger(packagePath);
                logger.setLevel(level);
            } else if (strs.length == 1) {
                Level level = Level.toLevel(strSource);
                LogManager.getRootLogger().setLevel(level);
            }
        }
        if (log.isInfoEnabled()) {
            log.info(" change log level :" + strSource);
        }

    }

    @Override
    public void deleteData(String dataPath) {
        if (log.isInfoEnabled()) {
            log.info(" delete zk data :" + dataPath);
        }
    }



}
