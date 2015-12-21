package com.baozun.scm.primservice.whoperation.exception;

public class ValidateException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 167236735796801732L;

	private Object[] args;
	
	/**
	 * 字段名称对应的i18n key
	 */
	private String fieldNameKey;
	
	/**
	 * 错误提示对应的i18n key
	 */
	private String errorTipKey;
	
	/**
	 * 完整错误提示对应的i18n key
	 */
	private String fullKey;
	
	
	public ValidateException(String fnk,String etk){
		this.fieldNameKey=fnk;
		this.errorTipKey=etk;
	}
	
	public ValidateException(String fnk,String etk,Object[] args){
		this.args=args;
		this.fieldNameKey=fnk;
		this.errorTipKey=etk;
	}
	
	
	public ValidateException(String fullKey,Object[] args){
		this.args=args;
		this.fullKey=fullKey;

	}
	
	
	public String getFullKey() {
		return fullKey;
	}

	public void setFullKey(String fullKey) {
		this.fullKey = fullKey;
	}

	public Object[] getArgs() {
		return args;
	}


	public void setArgs(Object[] args) {
		this.args = args;
	}


	public String getFieldNameKey() {
		return fieldNameKey;
	}


	public void setFieldNameKey(String fieldNameKey) {
		this.fieldNameKey = fieldNameKey;
	}


	public String getErrorTipKey() {
		return errorTipKey;
	}


	public void setErrorTipKey(String errorTipKey) {
		this.errorTipKey = errorTipKey;
	}
	
	
}
