package com.baozun.scm.primservice.whoperation.generalReceiving;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.baozun.redis.manager.CacheManager;
import com.baozun.scm.baseservice.sac.manager.PkManager;
import com.baozun.scm.primservice.whoperation.command.pda.rcvd.RcvdCacheCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhAsnRcvdLogCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhAsnRcvdSnLog;

@ContextConfiguration(locations = {"classpath*:mybatis-config.xml", "classpath*:lark-aop-context.xml", "classpath*:spring-test.xml"})
@ActiveProfiles("dev")
public class GeneralReceivingManagerTest extends AbstractJUnit4SpringContextTests {
    @Autowired
    private PkManager pkManager;

    @Autowired
    private CacheManager cacheManagr;

    @Test
    public void cacheSetTest() {
        WhAsnRcvdLogCommand rl = new WhAsnRcvdLogCommand();
        WhAsnRcvdSnLog rsl = new WhAsnRcvdSnLog();
        rsl.setDefectReasons("222");
        List<WhAsnRcvdSnLog> rslList = new ArrayList<WhAsnRcvdSnLog>();
        rslList.add(rsl);
        rl.setWhAsnRcvdSnLogList(rslList);

        System.out.println(rl.getWhAsnRcvdSnLogList().size());
        WhAsnRcvdSnLog rsl1 = new WhAsnRcvdSnLog();
        rsl1.setDefectReasons("333");
        List<WhAsnRcvdSnLog> rslList1 = new ArrayList<WhAsnRcvdSnLog>();
        rslList1.add(rsl);
        rl.getWhAsnRcvdSnLogList().addAll(rslList1);
        System.out.println(rl.getWhAsnRcvdSnLogList().size());

        // RcvdCacheCommand rcvd = new RcvdCacheCommand();
        // rcvd.setInsideContainerId(1L);
        // RcvdSnCacheCommand sn = new RcvdSnCacheCommand();
        // sn.setSn("sn002");
        // List<RcvdSnCacheCommand> list = new ArrayList<RcvdSnCacheCommand>();
        // list.add(sn);
        // rcvd.setSnList(list);
        // // this.cacheManagr.setValue("CACHE_OBJECT", SerializableUtil.convert2String(rcvd));
        // this.cacheManagr.setObject("CACHE_OBJECT", rcvd, 60 * 60);
    }

    @Test
    public void cacheGetTest() {
        // String value = this.cacheManagr.getValue("CACHE_OBJECT");
        // if (StringUtils.hasText(value)) {
        // RcvdCacheCommand rcvd = (RcvdCacheCommand) SerializableUtil.convert2Object(value);
        // System.out.println(rcvd.getSnList().size());
        // } else {
        //
        // System.out.println("error");
        // }

        RcvdCacheCommand rcvd = this.cacheManagr.getObject("CACHE_OBJECT");
        if (null == rcvd) {
            System.out.println("null");
        } else {
            System.out.println(rcvd.getSnList().get(0).getSn());
        }
        // String value1 = this.cacheManagr.getObject("CACHE_MAP_SYN_TEST_1");
        // System.out.println(value1);
        // String value2 = this.cacheManagr.getObject("CACHE_MAP_SYN_TEST_2");
        // System.out.println(value2);
    }

    @Test
    public void cacheRemoveTest() {
        // boolean flag = this.cacheManagr.remonKeys("CACHE_MAP_SYN_TEST_*");
        // System.out.println(flag);
        long removecount = this.cacheManagr.remove("CACHE_OBJECT");
        System.out.println(removecount);

    }


    class CacheThread implements Runnable {
        String param = "0";

        CacheThread(String param) {
            this.param = param;
        }

        public String getParam() {
            return param;
        }


        public void setParam(String param) {
            this.param = param;
        }


        @Override
        public void run() {
            String value = cacheManagr.getValue("CACHE_MAP_SYN_TEST");
            System.out.println(value);
            cacheManagr.setValue("CACHE_MAP_SYN_TEST", param, 60 * 60);
        }

    }
}
