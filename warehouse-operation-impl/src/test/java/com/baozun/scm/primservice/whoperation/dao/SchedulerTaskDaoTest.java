package com.baozun.scm.primservice.whoperation.dao;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath*:mybatis-config.xml",
			"classpath*:lark-aop-context.xml",
			"classpath*:spring-test.xml"})
@ActiveProfiles("dev")
public class SchedulerTaskDaoTest extends AbstractTransactionalJUnit4SpringContextTests{
//	@Autowired(required=false)
//	private SysSchedulerTaskDao sysSchedulerTaskDao;
	

	
	private static int i=16;
	
	private static Long currendId=2l;
	
	@BeforeClass
	public static void init(){
//		ProfileConfigUtil.setMode("dev");
	}
	
	
	@Rollback(false)
	public void testInsert(){
		
	}
	
	

	
	
	
}
