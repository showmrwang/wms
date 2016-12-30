package com.baozun.scm.primservice.whoperation.excel.exception;

import java.util.ArrayList;
import java.util.List;

/**
 * Excel异常
 * 
 * @author lisuo
 *
 */
public class RootExcelException extends RuntimeException {

	private static final long serialVersionUID = -7112791673075023782L;

	private String sheetName;
	private int cellNum = 0;
	private List<ExcelException> excelExceptions = new ArrayList<ExcelException>();

	public RootExcelException(String message) {
		super(message);
	}

	public RootExcelException(String message, String sheetName) {
		super(message);
		this.sheetName = sheetName;
	}
	
	public RootExcelException(String message, String sheetName,int cellNum) {
		super(message);
		this.sheetName = sheetName;
		this.cellNum = cellNum;
	}

	public String getSheetName() {
		return sheetName;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

	public List<ExcelException> getExcelExceptions() {
		return excelExceptions;
	}

	public void setExcelExceptions(List<ExcelException> excelExceptions) {
		this.excelExceptions = excelExceptions;
	}

	public boolean isException() {
		if (excelExceptions.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	public int getCellNum() {
		return cellNum;
	}

	public void setCellNum(int cellNum) {
		this.cellNum = cellNum;
	}
}
