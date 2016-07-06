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
 package com.baozun.scm.primservice.whoperation.model.warehouse;

import com.baozun.scm.primservice.whoperation.model.BaseModel;
/**
 * 
 * @author larkark
 *
 */
public class InventoryStatus extends BaseModel {
	
	/**
     * 
     */
    private static final long serialVersionUID = -3448066567106678665L;
    
    private String name;
	private Boolean isForSale;
	private Boolean isInCost;
	private String description;
	private Integer lifecycle;
    private Boolean isDefective;
	
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Boolean getIsForSale() {
        return isForSale;
    }
    public void setIsForSale(Boolean isForSale) {
        this.isForSale = isForSale;
    }
    public Boolean getIsInCost() {
        return isInCost;
    }
    public void setIsInCost(Boolean isInCost) {
        this.isInCost = isInCost;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Integer getLifecycle() {
        return lifecycle;
    }
    public void setLifecycle(Integer lifecycle) {
        this.lifecycle = lifecycle;
    }

    public Boolean getIsDefective() {
        return isDefective;
    }

    public void setIsDefective(Boolean isDefective) {
        this.isDefective = isDefective;
    }

}

