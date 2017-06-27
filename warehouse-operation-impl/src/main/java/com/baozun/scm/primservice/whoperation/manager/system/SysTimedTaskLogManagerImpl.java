package com.baozun.scm.primservice.whoperation.manager.system;

import java.util.Date;

import lark.common.annotation.MoreDB;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.system.SysTimedTaskLogDao;
import com.baozun.scm.primservice.whoperation.model.system.SysTimedTaskLog;
import com.baozun.scm.primservice.whoperation.util.DateUtil;

@Transactional
@Service("sysTimedTaskLogManager")
public class SysTimedTaskLogManagerImpl implements SysTimedTaskLogManager {

    @Autowired
    private SysTimedTaskLogDao sysTimedTaskLogDao;

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
    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public SysTimedTaskLog beginSysTimedTaskLog(String beanName, String methodName, String whCode, String documentCode, Long documentId, Integer handleQty) {
        SysTimedTaskLog log = new SysTimedTaskLog();
        log.setSysDate(DateUtil.getSysDate());
        log.setTaskBeanName(beanName);
        log.setTaskMethodName(methodName);
        log.setOuCode(whCode);
        log.setDocumentCode(documentCode);
        log.setDocumentId(documentId);
        log.setHandleQty(handleQty);
        log.setTimeStart(new Date());
        log.setIsSuccess(0);
        sysTimedTaskLogDao.insert(log);
        return log;
    }


    /***
     * 定时任务结束记录Log
     * 
     * @param log log对象 记录开始后返回
     * @param isError 如果报错给true 将记录失败 如果没报错记录成功
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public void endSysTimedTaskLog(SysTimedTaskLog log, Boolean isError) {
        Date beginDate = log.getTimeStart();
        Date endDate = new Date();
        // 计算开始-结束时间差距
        String be = getDistanceTime(beginDate, endDate);
        log.setTimeEnd(endDate);
        log.setTimeSpent(be);
        log.setIsSuccess(1);
        if (isError) {
            // 如果有报错 记录失败
            log.setIsSuccess(2);
        }
        sysTimedTaskLogDao.update(log);
    }

    /**
     * 计算时间差距
     * 
     * @param begin
     * @param end
     * @return
     */
    private String getDistanceTime(Date begin, Date end) {
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        long time1 = begin.getTime();
        long time2 = end.getTime();
        long diff;
        if (time1 < time2) {
            diff = time2 - time1;
        } else {
            diff = time1 - time2;
        }
        sec = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        return String.valueOf(sec);
    }


}
