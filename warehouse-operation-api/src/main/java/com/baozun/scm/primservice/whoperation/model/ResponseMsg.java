package com.baozun.scm.primservice.whoperation.model;

import java.io.Serializable;

/**
 * PrintResponse 打印反馈消息
 */
public class ResponseMsg implements Serializable {


    private static final long serialVersionUID = -8670306471205052592L;

    /** 成功 */
    public static final int STATUS_SUCCESS = 1;

    /** 失败 */
    public static final int STATUS_ERROR = 0;

    /** 数据异常 */
    public static final int DATA_ERROR = 2;

    /**
     * 状态 1：成功 0：失败 2：数据异常
     */
    private int responseStatus;

    /**
     * 原因状态
     */
    private int reasonStatus;

    /**
     * 备注
     */
    private String msg;


    public int getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(int responseStatus) {
        this.responseStatus = responseStatus;
    }

    public int getReasonStatus() {
        return reasonStatus;
    }

    public void setReasonStatus(int reasonStatus) {
        this.reasonStatus = reasonStatus;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }



}
