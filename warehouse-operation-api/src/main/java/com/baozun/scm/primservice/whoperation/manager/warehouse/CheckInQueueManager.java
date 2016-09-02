package com.baozun.scm.primservice.whoperation.manager.warehouse;

import java.util.List;

import com.baozun.scm.primservice.whoperation.command.warehouse.AsnReserveCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.CheckInQueueCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;


public interface CheckInQueueManager extends BaseManager {

    /**
     * 根据asn预约信息查找等待队列信息
     *
     * @author mingwei.xie
     * @param reserveId
     * @param ouId
     * @param logId
     * @return
     */
    CheckInQueueCommand findCheckInQueueByAsnId(Long reserveId, Long ouId, String logId);

    /**
     * 查询所有排队中的ASN，按照
     *
     * @author mingwei.xie
     * @param ouId
     * @param logId
     * @return
     */
    List<CheckInQueueCommand> getListOrderBySequenceAsc(Long ouId, String logId);


    /**
     * 加入asn签入等待序列
     *
     * @author mingwei.xie
     * @param asnId
     * @param ouId
     * @param userId
     * @param logId
     * @return
     */
    int addInToCheckInQueue(Long asnId, Long ouId, Long userId, String logId);


    /**
     * 完成签入
     *
     * @author mingwei.xie
     * @param asnId
     * @param platformId
     * @param ouId
     * @param userId
     * @param logId
     */
    void finishCheckIn(Long asnId, Long platformId, Long ouId, Long userId, String logId);

    /**
     * 简易预约
     *
     * @author mingwei.xie
     * @param asnReserveCommand
     * @param ouId
     * @param userId
     * @param logId
     */
    void simpleReserve(AsnReserveCommand asnReserveCommand, Long ouId, Long userId, String logId);

    /**
     * 手工指定月台
     *
     * @author mingwei.xie
     * @param asnId
     * @param platformId
     * @param ouId
     * @param userId
     * @param logId
     * @return
     */
    Long assignPlatform(Long asnId, Long platformId, Long ouId, Long userId, String logId);


    /**
     * 释放月台
     *
     * @author mingwei.xie
     * @param platformId
     * @param ouId
     * @param userId
     * @param logId
     * @return
     */
    Long releasePlatform(Long platformId, Long ouId, Long userId, String logId);

}
