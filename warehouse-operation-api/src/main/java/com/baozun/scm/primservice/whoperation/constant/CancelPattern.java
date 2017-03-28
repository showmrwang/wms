/**
 * Copyright (c) 2013 Baozun All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Baozun.
 * You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Baozun.
 *
 * BAOZUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. BAOZUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 *
 */
package com.baozun.scm.primservice.whoperation.constant;

import java.io.Serializable;

/**
 * @author TANGMING
 *
 */
public class CancelPattern implements Serializable {

    private static final long serialVersionUID = -7666851291432416397L;
    /**上架(建议上架和人工上架)*/
    public static final int  PUTAWAY_SKU_CANCEL = 1; // sku取消
    
    public static final int  PUTAWAY_SCAN_LOCATION_CANCEL = 2; // 扫描库位取消
    
    public static final int  PUTAWAY_CONTAINER_CACNCEL = 3; //容器取消
    
    public static final int  PUTAWAY_INSIDECONTAINER_CANCEL= 4;//内部容器取消
    
    public static final int  PUTAWAY_TIP_LOCATION_CANCEL = 5; // 提示库位取消
    
    public static final int  PUTAWAY_OUTERCONTAINER_CANCEL= 6;//外部容器取消
    
    public static final int  PUTAWAY_MAN_SPILIT_SN_CANCEL = 7;  //人工上架拆箱上架(sn.残次信息取消)
    
    public static final int  PUTAWAY_MAN_SPILIT_SKU_DETIAL_CANCEL = 8;  //人工上架拆箱上架(sku库存属性取消)
    
    /**拣货流程*/
    
    public static final int PICKING_TIP_CAR_CANCEL = 1; //提示小车取消
    
    
    public static final int PICKING_SCAN_LOC_CANCEL = 4;//  扫描库位取消
    
    
    public static final int PICKING_SCAN_OUTCONTAINER_CANCEL = 6;  //扫描外部容器取消
    
    
    public static final int PICKING_SCAN_INSIDECONTAINER_CANCEL = 8;  //扫描内部容器
    
    public static final int PICKING_SCAN_SKU_SCANCEL = 9;  //sku取消
    
    public static final int PICKING_SCAN_SKU_DETAIL = 10;   //sku库存属性取消
    
    public static final int PICKING_SCAN_OUT_BOUNX_BOX = 11;  //有小车情况下出库箱取消
    
    public static final int PICKING_SCAN_LATTICE_NO = 12;  //货格号取消
    
    
}
