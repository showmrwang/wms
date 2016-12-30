package com.baozun.scm.primservice.whoperation.excel.exception;

/**
 * Excel异常
 * 
 * @author lisuo
 *
 */
public class ExcelException extends RuntimeException {

	private static final long serialVersionUID = 3240288821877252548L;

	private int row;
	private String titleName;
	private String errorCode;

	public ExcelException() {
		super();
	}

	// public ExcelException(String message, Throwable cause, boolean
	// enableSuppression, boolean writableStackTrace) {
	//// super(message, cause, enableSuppression, writableStackTrace);
	// }

	public ExcelException(String message, Throwable cause) {
		super(message, cause);
	}

	public ExcelException(String message) {
		super(message);
	}

	public ExcelException(Throwable cause) {
		super(cause);
	}

	public ExcelException(String message, String errorCode, int row, String titleName) {
		super(message);
		this.errorCode = errorCode;
		this.row = row;
		this.titleName = titleName;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public String getTitleName() {
		return titleName;
	}

	public void setTitleName(String titleName) {
		this.titleName = titleName;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

}
