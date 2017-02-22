package com.baozun.scm.primservice.whoperation.command.warehouse;

import java.util.Date;
import java.util.List;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;
import com.baozun.scm.primservice.whoperation.command.odo.OdoCommand;
import com.baozun.scm.primservice.whoperation.command.odo.WhOdoOutBoundBoxCommand;

public class Container2ndCategoryCommand extends BaseCommand {


    /**
     * 
     */
    private static final long serialVersionUID = -8615840384131603857L;

    private Long id;
    /** 类别编码 */
    private String categoryCode;
    /** 类别名称 */
    private String categoryName;
    /** 对应一级类型ID */
    private String oneLevelType;
    /** 编码生成器CODE */
    private String codeGenerator;
    /** 长 */
    private Double length;
    /** 宽 */
    private Double width;
    /** 高 */
    private Double high;
    /** 体积 */
    private Double volume;
    /** 重量 */
    private Double weight;
    /** 长边货格数量 */
    private Integer lengthGridNum;
    /** 宽边货格数量 */
    private Integer widthGridNum;
    /** 高边货格数量 */
    private Integer highGridNum;
    /** 总货格数量 */
    private Integer totalGridNum;
    /** 货格长度 */
    private Double gridLength;
    /** 货格宽度 */
    private Double gridWidth;
    /** 货格高度 */
    private Double gridHigh;
    /** 货格体积 */
    private Double gridVolume;
    /** 拣货模式 */
    private String pickingMode;
    /** 前缀 */
    private String prefix;
    /** 后缀 */
    private String suffix;
    /** 所属组织ID */
    private Long ouId;
    /** 创建时间 */
    private java.util.Date createTime;
    /** 最后修改时间 */
    private java.util.Date lastModifyTime;
    /** 操作人ID */
    private Long operatorId;
    /** 1.可用;2.已删除;0.禁用 */
    private Integer lifecycle;

    private String oneLevelTypeName;

    /** 长度单位 */
    private String lengthUom;
    /** 体积单位 */
    private String volumeUom;
    /** 重量单位 */
    private String weightUom;

    /** 货格长度单位 */
    private String gridLengthUom;
    /** 货格体积单位 */
    private String gridVolumeUom;
    /** 货格重量单位 */
    private String gridWeightUom;

    /** 出库单列表 */
    private List<OdoCommand> odoCommandList;
    /** 已分配货格数 */
    private int assignedGridNum;

    private List<WhOdoOutBoundBoxCommand> odoOutBoundBoxCommandList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getOneLevelType() {
        return oneLevelType;
    }

    public void setOneLevelType(String oneLevelType) {
        this.oneLevelType = oneLevelType;
    }

    public String getCodeGenerator() {
        return codeGenerator;
    }

    public void setCodeGenerator(String codeGenerator) {
        this.codeGenerator = codeGenerator;
    }

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public Double getHigh() {
        return high;
    }

    public void setHigh(Double high) {
        this.high = high;
    }

    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Integer getLengthGridNum() {
        return lengthGridNum;
    }

    public void setLengthGridNum(Integer lengthGridNum) {
        this.lengthGridNum = lengthGridNum;
    }

    public Integer getWidthGridNum() {
        return widthGridNum;
    }

    public void setWidthGridNum(Integer widthGridNum) {
        this.widthGridNum = widthGridNum;
    }

    public Integer getHighGridNum() {
        return highGridNum;
    }

    public void setHighGridNum(Integer highGridNum) {
        this.highGridNum = highGridNum;
    }

    public Integer getTotalGridNum() {
        return totalGridNum;
    }

    public void setTotalGridNum(Integer totalGridNum) {
        this.totalGridNum = totalGridNum;
    }

    public Double getGridLength() {
        return gridLength;
    }

    public void setGridLength(Double gridLength) {
        this.gridLength = gridLength;
    }

    public Double getGridWidth() {
        return gridWidth;
    }

    public void setGridWidth(Double gridWidth) {
        this.gridWidth = gridWidth;
    }

    public Double getGridHigh() {
        return gridHigh;
    }

    public void setGridHigh(Double gridHigh) {
        this.gridHigh = gridHigh;
    }

    public Double getGridVolume() {
        return gridVolume;
    }

    public void setGridVolume(Double gridVolume) {
        this.gridVolume = gridVolume;
    }

    public String getPickingMode() {
        return pickingMode;
    }

    public void setPickingMode(String pickingMode) {
        this.pickingMode = pickingMode;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
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

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public Integer getLifecycle() {
        return lifecycle;
    }

    public void setLifecycle(Integer lifecycle) {
        this.lifecycle = lifecycle;
    }

    public String getOneLevelTypeName() {
        return oneLevelTypeName;
    }

    public void setOneLevelTypeName(String oneLevelTypeName) {
        this.oneLevelTypeName = oneLevelTypeName;
    }

    public String getLengthUom() {
        return lengthUom;
    }

    public void setLengthUom(String lengthUom) {
        this.lengthUom = lengthUom;
    }

    public String getVolumeUom() {
        return volumeUom;
    }

    public void setVolumeUom(String volumeUom) {
        this.volumeUom = volumeUom;
    }

    public String getWeightUom() {
        return weightUom;
    }

    public void setWeightUom(String weightUom) {
        this.weightUom = weightUom;
    }

    public String getGridLengthUom() {
        return gridLengthUom;
    }

    public void setGridLengthUom(String gridLengthUom) {
        this.gridLengthUom = gridLengthUom;
    }

    public String getGridVolumeUom() {
        return gridVolumeUom;
    }

    public void setGridVolumeUom(String gridVolumeUom) {
        this.gridVolumeUom = gridVolumeUom;
    }

    public String getGridWeightUom() {
        return gridWeightUom;
    }

    public void setGridWeightUom(String gridWeightUom) {
        this.gridWeightUom = gridWeightUom;
    }

    public List<OdoCommand> getOdoCommandList() {
        return odoCommandList;
    }

    public void setOdoCommandList(List<OdoCommand> odoCommandList) {
        this.odoCommandList = odoCommandList;
    }

    public int getAssignedGridNum() {
        return assignedGridNum;
    }

    public void setAssignedGridNum(int assignedGridNum) {
        this.assignedGridNum = assignedGridNum;
    }

    public List<WhOdoOutBoundBoxCommand> getOdoOutBoundBoxCommandList() {
        return odoOutBoundBoxCommandList;
    }

    public void setOdoOutBoundBoxCommandList(List<WhOdoOutBoundBoxCommand> odoOutBoundBoxCommandList) {
        this.odoOutBoundBoxCommandList = odoOutBoundBoxCommandList;
    }
}
