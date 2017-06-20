package com.baozun.scm.primservice.whoperation.manager.bi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import lark.common.annotation.MoreDB;

import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.bi.UserImportExcelDao;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.bi.ImportExcel;
import com.baozun.scm.primservice.whoperation.util.DateUtil;

@Service("outPutStreamToServersManager")
@Transactional
public class OutPutStreamToServersManagerImpl extends BaseManagerImpl implements OutPutStreamToServersManager {

    protected static final Logger log = LoggerFactory.getLogger(OutPutStreamToServersManager.class);

    @Autowired
    private ZkClient zkClient;
    @Autowired
    private UserImportExcelDao userImportExcelDao;

    // 获取zk信息
    @Value("${zk.cofing.root}")
    private String znode;


    /**
     * 上传导入验证失败文件到服务器 InputStream in 文件流 importType 文件类型 导入类型
     */
    public String uploadImportFileError(ImportExcel ie) {
        String returnString = "";
        String fileName = "";
        FileOutputStream os = null;
        try {
            // 获取ZK 上传URL
            Stat stat = new Stat();
            Object object = zkClient.readData(znode + Constants.IMPORT_EXCEL_ZK_ERROR_URL, stat);
            String dateString = DateUtil.getSysDateFormat("yyyyMMddHHmmss");
            String url = object.toString();
            // 文件名称格式:导入类型+当前时间+USERID
            fileName = ie.getImportType() + "_" + dateString + "_" + ie.getUserId() + "_" + Constants.ERROR + Constants.EXPORT_EXCEL_XLSX;
            returnString = fileName;
            File filedir = new File(url);
            if (!filedir.exists()) {
                filedir.mkdirs();
            }
            File file = new File(filedir, fileName);
            os = new FileOutputStream(file);
            ie.getWorkbook().write(os);
            // 更新导入用户信息表数据
            // UserImportExcel u =
            // userImportExcelDao.findUserImportExcelByIdAndOuId(ie.getUserImportExcelId(),
            // ie.getOuId());
            // if (null == u) {
            // log.error("uploadImportFileError Error UserImportExcel is null UserImportExcelId: " +
            // ie.getUserImportExcelId());
            // returnString = Constants.ERROR;
            // return returnString;
            // }
            // u.setErrorFileName(fileName);
            // userImportExcelDao.saveOrUpdate(u);
            // byte[] byteStr = new byte[2048];
            // int len = 0;
            // while ((len = in.read(byteStr)) > 0) {
            // os.write(byteStr, 0, len);
            // }
        } catch (Exception e) {
            log.error("uploadImportFileError Error：", e);
            returnString = Constants.ERROR;
        } finally {
            try {
                if (null != os) {
                    os.flush();
                    os.close();
                }
            } catch (IOException e) {
                log.error("uploadImportFileError Error：", e);
                returnString = Constants.ERROR;
            }
        }
        return returnString;
    }


    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public String uploadImportFileErrorToInfo(ImportExcel ie) {
        return this.uploadImportFileError(ie);
    }


    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public String uploadImportFileErrorToShard(ImportExcel ie) {
        return this.uploadImportFileError(ie);
    }

}
