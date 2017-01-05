package com.baozun.scm.primservice.whoperation.manager.bi;

import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.bi.ImportExcel;

public interface OutPutStreamToServersManager extends BaseManager {

    String uploadImportFileError(ImportExcel ie);

}
