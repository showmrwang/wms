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
 *
 */
package com.baozun.scm.primservice.whoperation.constant;

import java.io.Serializable;

/**
 * @author feng.hu
 *
 */
public class WhContainerMoveType implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 5947746956182098492L;
    
    /**
     * 货箱拆分移动模式
     */
    public static final String IN_CONTAINER = "0"; //入库周转箱移动到周转箱
    public static final String OUT_CONTAINER= "1"; // 周转箱库存移动到周转箱
    public static final String OUT_FACILITY = "2"; // 播种墙库存移动到周转箱
    public static final String OUT_SMAIL_CAR= "3"; //小车库存移动到周转箱
    
    /**
     * 枪扫描模式
     */
    public static final int NUMBER_ONLY_SCAN = 1; // 数量扫描
    public static final int ONE_BY_ONE_SCAN = 2; // 逐件扫描
    
    /**
     * 货箱拆分模式
     */
    public static final int FULL_BOX_MOVE = 1; // 整箱移动
    public static final int PART_BOX_MOVE = 2; // 部分移动

}
