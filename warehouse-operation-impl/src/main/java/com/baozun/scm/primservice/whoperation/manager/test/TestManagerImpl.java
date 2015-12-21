package com.baozun.scm.primservice.whoperation.manager.test;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.dao.test.TestDao;
import com.baozun.scm.primservice.whoperation.model.test.Test;

@Service("testManager")
@Transactional
public class TestManagerImpl implements TestManager {

	@Autowired
	private TestDao testDao;
	@Override
	@Transactional(readOnly=true)
	public List<Test> getList(Long cid) {
		// TODO Auto-generated method stub
		return testDao.findListByQueryMap(new HashMap<String,Object>());
	}
	
	public void insert(Test test){
		testDao.insert(test);
		
		System.out.println(test.getId());
	}
	
	public void timer(String code){
		
		
		System.out.println("timer:"+code);
	}



}
