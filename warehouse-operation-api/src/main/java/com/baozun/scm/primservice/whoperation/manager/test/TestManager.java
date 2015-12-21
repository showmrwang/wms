package com.baozun.scm.primservice.whoperation.manager.test;

import java.util.List;

import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.test.Test;

public interface TestManager extends BaseManager {

	List<Test> getList(Long cid);
	
	void insert(Test test);
	
	
	
	void timer(String code);
}
