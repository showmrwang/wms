package com.baozun.scm.primservice.whoperation.dao;

import java.util.Date;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.baozun.scm.primservice.whoperation.dao.poasn.WhAsnDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhPoLineDao;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsn;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath*:mybatis-config.xml",
			"classpath*:lark-aop-context.xml",
			"classpath*:spring.xml"})
@ActiveProfiles("dev")
public class PoAsnDaoTest extends AbstractTransactionalJUnit4SpringContextTests{
//	@Autowired(required=false)
//	private SysSchedulerTaskDao sysSchedulerTaskDao;
	

	@Autowired
	private WhPoLineDao whPoLineDao;
	
	@Autowired
	private WhAsnDao whAsnDao;

	
	private static int i=16;
	
	private static Long currendId=2l;
	
	@BeforeClass
	public static void init(){
//		ProfileConfigUtil.setMode("dev");
		
		
	}
	
	
	@Test
	@Rollback(false)
	public void testInsert() {
	
		
		
		WhAsn wp=whAsnDao.findById(2l);
		Date date=new Date();
		
		if(date.getMinutes()<41){
			System.out.println("sleep..");
			try {
				Thread.sleep(120000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			wp.setAsnCode("sleep");
		}
		else{
			wp.setAsnCode("no sleep");
		}
		
		int count=whAsnDao.saveOrUpdateByVersion(wp);
		System.out.println("test1:"+count);
	}
	
	

	
	
	
}
