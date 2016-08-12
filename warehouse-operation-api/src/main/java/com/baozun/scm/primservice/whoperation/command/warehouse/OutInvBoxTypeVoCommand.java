package com.baozun.scm.primservice.whoperation.command.warehouse;

import java.util.List;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.OutInvBoxTypeVolume;

public class OutInvBoxTypeVoCommand extends BaseCommand {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private Long id;
    /** 出库箱类型主键 */
    private Long outId;
    /** 对应组织ID */
    private Long ouId;
    /** 货品主键 */
    private Long prvId;
    /** 创建时间 */
    private java.util.Date createTime;
    /** 最终修改时间 */
    private java.util.Date lastModifyTime;
    /** 创建人ID */
    private Long createdId;
    /** 修改人ID */
    private Long modifiedId;
    // 出库箱类型名册
    private String outName;
    // 出库箱类型编码
    private String outCode;
    // 货品类名称
    private String prvName;
    // 货品类编码
    private String prvCode;
    // 容量
    private String volume;

    private List<OutInvBoxTypeVolume> outInvBoxtypeVoList;

    private Long[] ids;

    public Long getOutId() {
        return outId;
    }

    public void setOutId(Long outId) {
        this.outId = outId;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

    public Long getPrvId() {
        return prvId;
    }

    public void setPrvId(Long prvId) {
        this.prvId = prvId;
    }


    public java.util.Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(java.util.Date createTime) {
        this.createTime = createTime;
    }

    public java.util.Date getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(java.util.Date lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public Long getCreatedId() {
        return createdId;
    }

    public void setCreatedId(Long createdId) {
        this.createdId = createdId;
    }

    public Long getModifiedId() {
        return modifiedId;
    }

    public void setModifiedId(Long modifiedId) {
        this.modifiedId = modifiedId;
    }

    public String getOutName() {
        return outName;
    }

    public void setOutName(String outName) {
        this.outName = outName;
    }

    public String getOutCode() {
        return outCode;
    }

    public void setOutCode(String outCode) {
        this.outCode = outCode;
    }

    public String getPrvName() {
        return prvName;
    }

    public void setPrvName(String prvName) {
        this.prvName = prvName;
    }

    public String getPrvCode() {
        return prvCode;
    }

    public void setPrvCode(String prvCode) {
        this.prvCode = prvCode;
    }


    public List<OutInvBoxTypeVolume> getOutInvBoxtypeVoList() {
        return outInvBoxtypeVoList;
    }

    public void setOutInvBoxtypeVoList(List<OutInvBoxTypeVolume> outInvBoxtypeVoList) {
        this.outInvBoxtypeVoList = outInvBoxtypeVoList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public Long[] getIds() {
        return ids;
    }

    public void setIds(Long[] ids) {
        this.ids = ids;
    }



}
