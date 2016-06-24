/**
 * Copyright (c) 2013 Baozun All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Baozun. You shall not disclose
 * such Confidential Information and shall use it only in accordance with the terms of the license
 * agreement you entered into with Baozun.
 * 
 * BAOZUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. BAOZUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES.
 */

package com.baozun.scm.primservice.whoperation.manager.pda;

import java.util.List;
import java.util.Map;

import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.AsnReserveCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.CheckInQueueCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.RecommendPlatformCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.AsnReserve;
import com.baozun.scm.primservice.whoperation.model.warehouse.Platform;
import com.baozun.scm.primservice.whoperation.model.warehouse.Store;
import com.baozun.scm.primservice.whoperation.model.warehouse.Warehouse;

public interface CheckInManagerProxy extends BaseManager {

    /**
     * 查询仓库信息
     *
     * @author mingwei.xie
     * @param ouId
     * @param logId
     * @return
     */
    Warehouse findWarehouseById(Long ouId, String logId);

    /**
     * 模糊查询方法。 根据asnExtCode,asn状态，仓库查找asn
     *
     * @author mingwei.xie
     * @param asnExtCode
     * @param status
     * @param ouId
     * @param logId
     * @return
     */
    List<WhAsnCommand> findWhAsnListByAsnExtCode(String asnExtCode, Integer[] status, Long ouId, String logId);

    /**
     * 根据ASN的ID,OUID查找ASN
     *
     * @param whAsnCommand
     * @param logId
     * @return
     */
    WhAsnCommand findWhAsnById(WhAsnCommand whAsnCommand, String logId);

    /**
     * 根据Id获取店铺
     * 
     * @author mingwei.xie
     * @param storeId
     * @param logId
     * @return
     */
    Store getStoreById(Long storeId, String logId);

    /**
     * 更具asnId查询asn预约信息
     *
     * @author mingwei.xie
     * @param asnId
     * @param ouId
     * @param logId
     * @return
     */
    AsnReserve findAsnReserveByAsnId(Long asnId, Long ouId, String logId);


    /**
     * 根据asn预约信息查找等待队列信息
     *
     * @author mingwei.xie
     * @param asnId
     * @param ouId
     * @param logId
     * @return
     */
    CheckInQueueCommand findCheckInQueueByAsnId(Long asnId, Long ouId, String logId);



    /**
     * 根据asn预约号匹配月台推荐规则
     *
     * @author mingwei.xie
     * @param asnId
     * @param ouId
     * @param logId
     * @return
     */
    Map<Integer, RecommendPlatformCommand> matchPlatformRule(Long asnId, Long ouId, String logId);

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
    int addToCheckInQueue(Long asnId, Long ouId, Long userId, String logId);

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
     * 获取asn预约号
     *
     * @author mingwei.xie
     * @param logId
     * @return
     */
    String getAsnReserveCode(String logId);

    /**
     * 查找空闲月台
     *
     * @author mingwei.xie
     * @param ouId
     * @param lifecycle
     * @param logId
     * @return
     */
    List<Platform> findVacantPlatform(Long ouId, Integer lifecycle, String logId);

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
     * @author mingwei.xie
     * @param ouId
     * @param lifecycle
     * @param logId
     * @return
     */
    List<Platform> findOccupiedPlatform(Long ouId, Integer lifecycle, String logId);

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
    Long freePlatform(Long platformId, Long ouId, Long userId, String logId);

    /**
     * 释放月台
     *
     * @author mingwei.xie
     * @param logId
     * @param asnId
     * @param ouId
     * @param userId
     * @return
     */
    void freePlatformByRcvdFinish(Long asnId, Long ouId, Long userId, String logId);


}
