package com.baozun.scm.primservice.whoperation.manager.system;

import java.util.Date;

import lark.common.annotation.MoreDB;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.system.GlobalLogCommand;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.system.GlobalLogDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.model.system.GlobalLog;
import com.baozun.scm.primservice.whoperation.util.DateUtil;
import com.baozun.utilities.type.JsonUtil;


@Transactional
@Service("globalLogManager")
public class GlobalLogManagerImpl implements GlobalLogManager {

    @Autowired
    private GlobalLogDao globalLogDao;

    /**
     * 添加全局日志信息
     */
    @Override
    public void insertGlobalLog(GlobalLogCommand globalLogCommand) {
        GlobalLog gl = new GlobalLog();
        gl.setModifiedValues(objectToJson(globalLogCommand.getModifiedValues()));
        gl.setModifiedId(globalLogCommand.getModifiedId());
        gl.setOuId(globalLogCommand.getOuId());
        gl.setType(globalLogCommand.getType());
        gl.setParentCode(globalLogCommand.getParentCode());
        gl.setObjectType(formatClassName(globalLogCommand.getObjectType()));
        gl.setModifyTime(new Date());
        gl.setSysDate(DateUtil.getSysDate());
        gl.setIsTranslate(false);
        globalLogDao.insert(gl);
    }

    /**
     * 将全局表数据源的日志信息插入公共库日志表
     * 
     * @author lichuan
     * @param globalLogCommand
     * @param ds
     */
    @Override
    public void insertGlobalLog(GlobalLogCommand globalLogCommand, String ds) {
        if (null == ds) {
            ds = "null";
        } else {
            ds = ds.trim();
        }
        switch (ds) {
            case "null":
            case DbDataSource.MOREDB_INFOSOURCE:
            case DbDataSource.MOREDB_SHARDSOURCE:
                // 不切换新数据源
                this.insertGlobalLog(globalLogCommand);
                break;
            case DbDataSource.MOREDB_GLOBALSOURCE:
                // 切换新数据源
                this.insertGlobalLog2Info(globalLogCommand);
                break;
            default:
                throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
    }

    /**
     * 日志插入到公共库
     * 
     * @author lichuan
     * @param globalLogCommand
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    private void insertGlobalLog2Info(GlobalLogCommand globalLogCommand) {
        GlobalLog gl = new GlobalLog();
        gl.setModifiedValues(JsonUtil.buildNormalBinder().toJson(globalLogCommand.getModifiedValues()));
        gl.setModifiedId(globalLogCommand.getModifiedId());
        gl.setOuId(globalLogCommand.getOuId());
        gl.setType(globalLogCommand.getType());
        gl.setParentCode(globalLogCommand.getParentCode());
        gl.setObjectType(formatClassName(globalLogCommand.getObjectType()));
        gl.setModifyTime(new Date());
        gl.setSysDate(DateUtil.getSysDate());
        gl.setIsTranslate(false);
        globalLogDao.insert(gl);
    }

    /**
     * 对象转换成json格式
     * 
     * @param object
     * @return
     */
    private static String objectToJson(Object object) {
        return JsonUtil.buildNormalBinder().toJson(object);
    }

    /**
     * 格式化类名
     * 
     * @param name
     * @return
     */
    private static String formatClassName(String name) {
        name = name.substring(name.lastIndexOf(".") + 1, name.length());
        return name;
    }
}
