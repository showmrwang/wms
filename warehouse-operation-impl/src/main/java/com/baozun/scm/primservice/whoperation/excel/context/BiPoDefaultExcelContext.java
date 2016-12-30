package com.baozun.scm.primservice.whoperation.excel.context;

public class BiPoDefaultExcelContext extends ExcelContext {

    private static BiPoDefaultExcelContext context = new BiPoDefaultExcelContext("excel-config.xml");
    public BiPoDefaultExcelContext(String location) {
        super(location);
    }

    public static ExcelContext getContext() {
        return context;
    }
}
