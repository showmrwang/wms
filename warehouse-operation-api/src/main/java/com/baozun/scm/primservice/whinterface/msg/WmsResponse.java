package com.baozun.scm.primservice.whinterface.msg;

import java.io.Serializable;

/**
 * WmsResponse 反馈消息
 */
public class WmsResponse implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -7281587794764524020L;

    /**
     * 成功
     */
    public static final int STATUS_SUCCESS = 1;

    /**
     * 失败
     */
    public static final int STATUS_ERROR = 0;

    /**
     * 状态 1：成功 0：失败
     */
    private int status;

    /**
     * 异常编码
     */
    private String errorCode;

    /**
     * 备注
     */
    private String msg;
    
    /**
     * 上位系统单据号
     */
    private String orderCode;

    public WmsResponse(int status, String errorCode, String msg) {
        this.status = status;
        this.errorCode = errorCode;
        this.msg = msg;
    }

    public WmsResponse(int status, String orderCode, String errorCode, String msg) {
        this.status = status;
        this.orderCode = orderCode;
        this.errorCode = errorCode;
        this.msg = msg;
    }
    
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


	public String getOrderCode() {
		return orderCode;
	}


	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}



}
