package com.baozun.scm.primservice.whoperation;

/**
 * Copyright (c) 2010 Jumbomart All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Jumbomart. You shall not
 * disclose such Confidential Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Jumbo.
 * 
 * JUMBOMART MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. JUMBOMART SHALL NOT BE LIABLE FOR ANY
 * DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES.
 * 
 */
/**
 * Copyright (c) 2010 Jumbomart All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Jumbomart. You shall not
 * disclose such Confidential Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Jumbo.
 * 
 * JUMBOMART MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. JUMBOMART SHALL NOT BE LIABLE FOR ANY
 * DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES.
 * 
 */



import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.baozun.scm.primservice.whoperation.manager.init.InitManager;


@ContextConfiguration(locations = {"classpath*:spring.xml"})
public class initTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    private InitManager initManager;

    @Test
    public void testExecute() {
        try {
            initOdoCollect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化出库单历史收集数据
     */
    private void initOdoCollect() {
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            String str = "";
            fis = new FileInputStream("C:\\Users\\bin.hu\\Desktop\\snOrValidDateSkuFlow.txt");
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);
            Set<String> ec = new HashSet<String>();
            int i = 1;
            String odoCode = "201705000000" + i;
            while ((str = br.readLine()) != null) {
                String[] s = str.trim().split(",");
                System.out.println(s[0] + " " + str);
                boolean flag = ec.add(s[0].trim());
                if (!flag) {
                    // 增加odoArchivLine
                    initManager.initOdoCollect(str, true, odoCode);
                } else {
                    // 新建odoArchiv+odoArchivLine
                    initManager.initOdoCollect(str, false, odoCode);
                }
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
                isr.close();
                fis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
