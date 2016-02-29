package com.baozun.scm.primservice.whoperation.manager.system;

import java.util.Date;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.system.GlobalLogCommand;
import com.baozun.scm.primservice.whoperation.dao.system.GlobalLogDao;
import com.baozun.scm.primservice.whoperation.model.system.GlobalLog;


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
        globalLogDao.insert(gl);
    }

    /**
     * 对象转换成json格式
     * 
     * @param object
     * @return
     */
    private static String objectToJson(Object object) {
        JSONObject jsonObject = JSONObject.fromObject(object);
        return jsonObject.toString();
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
