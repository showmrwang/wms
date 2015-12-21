package com.baozun.scm.primservice.whoperation.model;

public class BaseModel extends lark.common.model.BaseModel {
	
    /**
     * 
     */
    private static final long serialVersionUID = -1989798619170800263L;

    /**
	 * 正常
	 */
	public static final Integer LIFECYCLE_NORMAL=1;
	
	/**
	 * 禁用
	 */
	public static final Integer LIFECYCLE_DISABLE=0;
	
	/**
	 * 已删除
	 */
	public static final Integer LIFECYCLE_DELETED=2;
}
