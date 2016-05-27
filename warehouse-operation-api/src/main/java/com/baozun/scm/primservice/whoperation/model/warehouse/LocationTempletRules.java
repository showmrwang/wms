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
public class LocationTempletRules extends BaseModel {
	
	/**
     * 
     */
    private static final long serialVersionUID = -2903208214815608709L;
    //alias
	public static final String TABLE_ALIAS = "LocationTempletRules";
	public static final String ALIAS_TEMPLET_ID = "模板ID";
	public static final String ALIAS_DIMENSION = "维度 A代表字母 N代表数字";
	public static final String ALIAS_SORT_NO = "排列顺序";
	
	//date formats
	
	//columns START
	/*模板ID*/
	private Long templetId;
	/*维度 A代表字母 N代表数字*/
	private String dimension;
	/*排列顺序*/
	private Integer sortNo;
	//columns END
	private Long ouId;

	public LocationTempletRules(){
	}

	public LocationTempletRules(
		Long id
	){
		this.id = id;
	}

	public void setTempletId(Long value) {
		this.templetId = value;
	}
	
	public Long getTempletId() {
		return this.templetId;
	}
	public void setDimension(String value) {
		this.dimension = value;
	}
	
	public String getDimension() {
		return this.dimension;
	}
	public void setSortNo(Integer value) {
		this.sortNo = value;
	}
	
	public Integer getSortNo() {
		return this.sortNo;
	}

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }
  
}

