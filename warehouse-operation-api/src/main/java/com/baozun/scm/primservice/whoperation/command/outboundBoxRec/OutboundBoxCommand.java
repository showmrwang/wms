/**
 * Copyright (c) 2013 Baozun All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Baozun.
 * You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Baozun.
 * <p/>
 * BAOZUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. BAOZUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 */

package com.baozun.scm.primservice.whoperation.command.outboundBoxRec;

import java.util.List;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;
import com.baozun.scm.primservice.whoperation.command.odo.WhOdoOutBoundBoxCommand;

public class OutboundBoxCommand extends BaseCommand{

    private static final long serialVersionUID = 7155091168401463198L;

    /** 容器中的包裹列表，按照商品分，每种商品会记录数量 */
    private List<WhOdoOutBoundBoxCommand> odoOutBoundBoxCommandList;
    /** 容器类型：出库箱、托盘、周转箱、整箱容器、小车 */
    private String boxType;
    /** 长度 */
    private Double length;
    /** 宽度 */
    private Double width;
    /** 高度 */
    private Double height;
    /** 容器ID */
    private Long containerId;
    /** 外部容器ID */
    private Long outContainerId;
    /** 出库箱ID */
    private Long outboundBoxId;



}
