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

package com.baozun.scm.primservice.whoperation.model.seeding;

import com.baozun.scm.primservice.whoperation.model.BaseModel;

public class SeedingLattice extends BaseModel{

    private static final long serialVersionUID = -4036401956207965718L;

    /** 货格号 */
    private Long latticeNo;
    /** 显示单据号 由播种功能t_wh_function_seedingwall的show_code(系统参数)决定显示WhSeedingwallLattice中的odoCode、extCode、ecOrderCode中的一个 */
    private String showCode;
    /** 提示播种数量 */
    private int seedQty;
    /** 出库单ID */
    private Long odoId;
    /** 出库单状态 待播种/取消/完成/异常 */
    private int status;

    public Long getLatticeNo() {
        return latticeNo;
    }

    public void setLatticeNo(Long latticeNo) {
        this.latticeNo = latticeNo;
    }

    public String getShowCode() {
        return showCode;
    }

    public void setShowCode(String showCode) {
        this.showCode = showCode;
    }

    public int getSeedQty() {
        return seedQty;
    }

    public void setSeedQty(int seedQty) {
        this.seedQty = seedQty;
    }

    public Long getOdoId() {
        return odoId;
    }

    public void setOdoId(Long odoId) {
        this.odoId = odoId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
