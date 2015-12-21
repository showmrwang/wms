package com.baozun.scm.primservice.whoperation.manager;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;



public class SystemInitBean implements InitializingBean{
	
//	@Autowired
//	private WatchControl watchControl;
	
	private String val;


	public String getVal() {
		return val;
	}

	public void setVal(String val) {
		 
		this.val = val;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
//		watchControl.initWatch();
//		System.out.println("-------------------------run:"+watchControl);
	}
	
	
}
