//package com.baozun.scm.primservice.whoperation.sharding;
//
//import java.util.List;
//
//import org.apache.commons.lang.builder.ToStringBuilder;
//import org.apache.commons.lang.builder.ToStringStyle;
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.annotation.Rollback;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
//
//import com.baozun.scm.primservice.whoperation.SpringTestManagerBase;
//import com.baozun.scm.primservice.whoperation.manager.system.SalesOrderManager;
//import com.baozun.scm.primservice.whoperation.model.system.SalesOrder;
//import com.baozun.scm.primservice.whoperation.model.system.SysDictionary;
//@ContextConfiguration({ "classpath*:spring-test.xml" })
//@ActiveProfiles("dev")
//public class ShardingManagerTest extends AbstractJUnit4SpringContextTests {
//
//    @Autowired
//    private SalesOrderManager salesOrderManager;
//    
//    @Test
//    public void insert() throws Exception {
//       
//    	long j=1l;
//    	for(int i=1;i<1001;i++)
//    	{
//    		j++;
//	    	SalesOrder so=new SalesOrder();
//	    	
//	    	so.setCode("s1000"+i);
//	    	so.setPrice(22l);
//	    	so.setShopId(j%50);
//	    	salesOrderManager.insert(so, j%50);
//    	}
//    }
//    
//    
//
//    @Test
//    public void query() throws Exception {
//       
//    }
//
//    
//}
