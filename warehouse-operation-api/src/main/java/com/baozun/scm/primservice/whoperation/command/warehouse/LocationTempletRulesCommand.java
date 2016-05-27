package com.baozun.scm.primservice.whoperation.command.warehouse;

import com.baozun.scm.primservice.whoperation.model.warehouse.LocationTempletRules;

public class LocationTempletRulesCommand extends LocationTempletRules{
    /**
     * 
     */
    private static final long serialVersionUID = 1905741686357316044L;
    /*库位编号开始*/
    private String dimensionFrom;
    /*库位编号结束*/
    private String dimensionTo;
    /*增量*/
    private Integer bulk;
    public String getDimensionFrom() {
        return dimensionFrom;
    }
    public void setDimensionFrom(String dimensionFrom) {
        this.dimensionFrom = dimensionFrom;
    }
    public String getDimensionTo() {
        return dimensionTo;
    }
    public void setDimensionTo(String dimensionTo) {
        this.dimensionTo = dimensionTo;
    }
    public Integer getBulk() {
        return bulk;
    }
    public void setBulk(Integer bulk) {
        this.bulk = bulk;
    }
    
}
