package com.baozun.scm.primservice.whoperation.command.warehouse.inventory;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

public class WhSkuInventorySnDefectTypeReasonsCommand extends BaseCommand {

    private static final long serialVersionUID = 1362873626389368945L;

    /** 主键ID */
    private Long id;
    /** 残次(类型/原因)名称 */
    private String name;
    /** 店铺名称 */
    private String storeName;
    /** 残次类型来源 STORE店铺 WH仓库 */
    private String defectSource;
    /** ID+残次类型来源 */
    private String idDefectSource;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getDefectSource() {
        return defectSource;
    }

    public void setDefectSource(String defectSource) {
        this.defectSource = defectSource;
    }

    public String getIdDefectSource() {
        return idDefectSource;
    }

    public void setIdDefectSource(String idDefectSource) {
        this.idDefectSource = idDefectSource;
    }


}
