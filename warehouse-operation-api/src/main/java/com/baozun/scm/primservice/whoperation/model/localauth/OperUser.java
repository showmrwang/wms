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
package com.baozun.scm.primservice.whoperation.model.localauth;

import java.util.Date;

import com.baozun.scm.primservice.whoperation.model.BaseModel;


/**
 * @author gianni.zhang
 *
 * 2016年3月15日 下午6:46:51
 */
public class OperUser extends BaseModel {
    /**
     * 
     */
    private static final long serialVersionUID = -3132696657381936310L;

    // columns START

    /** 所属组织 */
    private Long ouId;
    /** 账号 */
    private String account;
    /** 用户姓名 */
    private String userName;
    /** 密码 */
    private String password;
    /** 用户帐号是否未过期，过期帐号无法登录系统 */
    private Boolean isAccNonExpired;
    /** 用户帐号是否未被锁定，被锁定的用户无法使用系统 */
    private Boolean isAccNonLocked;
    /** 1.可用;2.已删除;0.禁用 */
    private Integer lifecycle;
    /** 创建时间 */
    private Date createTime;
    /** 最后修改时间 */
    private Date lastModifyTime;
    /** 最后登录时间 */
    private Date lastAccessTime;
    /** 邮箱 */
    private String email;
    /** 工号 */
    private String jobNumber;
    /** 备注 */
    private String memo;
    /** 生效日期 */
    private Date timeStart;
    /** 失效日期 */
    private Date timeEnd;
    /** 操作员ID */
    private Long modifyId;
    /** UAC ID */
    private Long uacId;
    /**　全局表时间修改　*/
    private Date globalModifyTime;

    // columns END

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getIsAccNonExpired() {
        return isAccNonExpired;
    }

    public void setIsAccNonExpired(Boolean isAccNonExpired) {
        this.isAccNonExpired = isAccNonExpired;
    }

    public Boolean getIsAccNonLocked() {
        return isAccNonLocked;
    }

    public void setIsAccNonLocked(Boolean isAccNonLocked) {
        this.isAccNonLocked = isAccNonLocked;
    }

    public Integer getLifecycle() {
        return lifecycle;
    }

    public void setLifecycle(Integer lifecycle) {
        this.lifecycle = lifecycle;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(Date lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public Date getLastAccessTime() {
        return lastAccessTime;
    }

    public void setLastAccessTime(Date lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getJobNumber() {
        return jobNumber;
    }

    public void setJobNumber(String jobNumber) {
        this.jobNumber = jobNumber;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public Date getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(Date timeStart) {
        this.timeStart = timeStart;
    }

    public Date getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(Date timeEnd) {
        this.timeEnd = timeEnd;
    }

    public Long getModifyId() {
        return modifyId;
    }

    public void setModifyId(Long modifyId) {
        this.modifyId = modifyId;
    }

    public Long getUacId() {
        return uacId;
    }

    public void setUacId(Long uacId) {
        this.uacId = uacId;
    }

    public Date getGlobalModifyTime() {
        return globalModifyTime;
    }

    public void setGlobalModifyTime(Date globalModifyTime) {
        this.globalModifyTime = globalModifyTime;
    }

}
