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
package com.baozun.scm.primservice.whoperation.command.warehouse;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

/**
 * 上架功能参数
 * 
 * @author larkark
 * 
 */
public class WhFunctionPutAwayCommand extends BaseCommand {



    /**
     * 
     */
    private static final long serialVersionUID = -4379944732070999323L;

    /** 主键ID */
    private Long id;
    /** 对应功能ID */
    private Long functionId;
    /** 上架模式 系统指导上架 人为指定上架 系统建议上架 */
    private Integer putawayPattern;
    /** 扫描模式 逐件扫描 数量扫描 默认数量扫描 */
    private Integer scanPattern;
    /** 是否整托上架 */
    private Boolean isEntireTrayPutaway;
    /** 是否整箱上架 */
    private Boolean isEntireBinPutaway;
    /** CASELEVEL是否需要扫描SKU */
    private Boolean isCaselevelScanSku;
    /** 非CASELEVEL是否需要扫描SKU */
    private Boolean isNotcaselevelScanSku;
    /** 对应组织ID */
    private Long ouId;



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFunctionId() {
        return functionId;
    }

    public void setFunctionId(Long functionId) {
        this.functionId = functionId;
    }

    public Integer getPutawayPattern() {
        return putawayPattern;
    }

    public void setPutawayPattern(Integer putawayPattern) {
        this.putawayPattern = putawayPattern;
    }

    public Integer getScanPattern() {
        return scanPattern;
    }

    public void setScanPattern(Integer scanPattern) {
        this.scanPattern = scanPattern;
    }

    public Boolean getIsEntireTrayPutaway() {
        return isEntireTrayPutaway;
    }

    public void setIsEntireTrayPutaway(Boolean isEntireTrayPutaway) {
        this.isEntireTrayPutaway = isEntireTrayPutaway;
    }

    public Boolean getIsEntireBinPutaway() {
        return isEntireBinPutaway;
    }

    public void setIsEntireBinPutaway(Boolean isEntireBinPutaway) {
        this.isEntireBinPutaway = isEntireBinPutaway;
    }

    public Boolean getIsCaselevelScanSku() {
        return isCaselevelScanSku;
    }

    public void setIsCaselevelScanSku(Boolean isCaselevelScanSku) {
        this.isCaselevelScanSku = isCaselevelScanSku;
    }

    public Boolean getIsNotcaselevelScanSku() {
        return isNotcaselevelScanSku;
    }

    public void setIsNotcaselevelScanSku(Boolean isNotcaselevelScanSku) {
        this.isNotcaselevelScanSku = isNotcaselevelScanSku;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }



}
