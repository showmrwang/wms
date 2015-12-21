package com.baozun.scm.primservice.whoperation.exception;

public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 665112595508596601L;

    private int errorCode;

    private Object[] args;

    private String message;

    private BusinessException linkedException;

    public BusinessException(int errorCode) {
        super();
        this.errorCode = errorCode;
    }

    public BusinessException(String message) {
        this.message = message;
    }

    public BusinessException(int errorCode, Object[] args) {
        super();
        this.errorCode = errorCode;
        this.args = args;
    }

    public BusinessException(int errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public BusinessException getLinkedException() {
        return linkedException;
    }

    public void setLinkedException(BusinessException linkedException) {
        this.linkedException = linkedException;
    }
}
