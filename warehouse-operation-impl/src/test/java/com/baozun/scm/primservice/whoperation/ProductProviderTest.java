/**
 * Copyright (c) 2013 Jumbomart All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Jumbomart.
 * You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Jumbo.
 *
 * JUMBOMART MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. JUMBOMART SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 *
 */
package com.baozun.scm.primservice.whoperation;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import lark.tool.system.ProfileConfigUtil;
import lark.tool.zk.ZooKeeperOperator;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.xml.DOMConfigurator;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * ProductProvider
 *
 * @author: fan.chen1
 * @date: 2013年11月20日
 **/
public class ProductProviderTest {
	
	public static Resource[] getResourcesForClasspathByPath(String path) throws IOException {
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		if(StringUtils.isEmpty(path)){
			return null;
		}
		if(!path.startsWith("classpath*:")){
			path = "classpath*:"+path;
		}
		Resource[] resources = resolver.getResources(path);
		
		
		return resources;
	}
	
	
	public static URL findCurURL()throws Exception{
		
		Resource[] resources=getResourcesForClasspathByPath("log4j.xml");
    	
    	for(Resource res:resources){
    		System.out.println(res.getURL().getPath());
    		String path=res.getURL().getPath();
    		
    		String[] array=path.split("/");
    		
    		String jarName=array[array.length-2];
    		
    		if(jarName.startsWith("wms4-impl")||jarName.startsWith("classes")){
    			return res.getURL();
    		}
    	}
    	
    	return resources[0].getURL(); 
		
	}

    public static void main(String[] args) throws Exception {

    	Properties zkPro=ProfileConfigUtil.findCommonPro("config/zk.properties");
		
		String zkhost = zkPro.getProperty("zk.host");
		
		 ZooKeeperOperator.getInstance(zkhost);
		 
		System.out.println("-----------------------zhost:"+zkhost);
    	
    	URL log4jPath=findCurURL();
    	System.out.println(log4jPath.getPath());
    	System.out.println("---------------------------");
    	DOMConfigurator.configure(log4jPath);
    	//Thread.sleep(5000);

        com.alibaba.dubbo.container.Main.main(args);
    	
    	
    	
    }

}
