package com.baozun.scm.primservice.whoperation.manager.system;

import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.system.SysTimedTaskLog;


public interface SysTimedTaskLogManager extends BaseManager {

    /**
     * 定时任务开始记录Log
     * 
     * @param beanName uac配置定时任务对应的beanName
     * @param methodName uac配置定时任务方法名称
     * @param whCode 仓库编码
     * @param documentCode 单据编码
     * @param documentId 单据Id
     * @param handleQty 处理数量
     * @return
     */
    SysTimedTaskLog beginSysTimedTaskLog(String beanName, String methodName, String whCode, String documentCode, Long documentId, Integer handleQty);

    /***
     * 定时任务结束记录Log
     * 
     * @param log log对象 记录开始后返回
     * @param isError 如果报错给true 将记录失败 如果没报错记录成功
     */
    void endSysTimedTaskLog(SysTimedTaskLog log, Boolean isError);
}
