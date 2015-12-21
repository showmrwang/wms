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
 package com.baozun.scm.primservice.whoperation.model.test;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.List;
import java.util.Map;

import lark.common.model.BaseModel;;
/**
 * 
 * @author lark
 *
 */
public class Test extends BaseModel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6381710045803559661L;
	//alias
	public static final String TABLE_ALIAS = "Test";
	public static final String ALIAS_NAME = "name";
	public static final String ALIAS_CID = "cid";
	
	//date formats

	//columns START
	private java.lang.String name;
	private java.lang.Long cid;
	//columns END

	public Test(){
	}

	public Test(
		java.lang.Long id
	){
		this.id = id;
	}

	public void setName(java.lang.String value) {
		this.name = value;
	}
	
	public java.lang.String getName() {
		return this.name;
	}
	public void setCid(java.lang.Long value) {
		this.cid = value;
	}
	
	public java.lang.Long getCid() {
		return this.cid;
	}
    @Override
	public String toString() {
		return new ToStringBuilder(this)
		.append("Id",getId())		
		.append("Name",getName())		
		.append("Cid",getCid())		
			.toString();
	}
    @Override
	public int hashCode() {
		return new HashCodeBuilder()
		.append(getId())
		.append(getName())
		.append(getCid())
			.toHashCode();
	}
    @Override
	public boolean equals(Object obj) {
		if(obj instanceof Test == false) return false;
		if(this == obj) return true;
		Test other = (Test)obj;
		return new EqualsBuilder()
		.append(getId(),other.getId())

		.append(getName(),other.getName())

		.append(getCid(),other.getCid())

			.isEquals();
	}
}

