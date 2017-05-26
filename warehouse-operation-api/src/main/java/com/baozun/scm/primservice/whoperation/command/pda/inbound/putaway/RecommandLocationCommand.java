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
package com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway;

import java.util.Map;
import java.util.Set;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

/**
 * @author lichuan
 *
 */
public class RecommandLocationCommand extends BaseCommand {

    private static final long serialVersionUID = -1337015683150827800L;
    /** 推荐库位Id */
    private Long locationId;
    /** 商品Id */
    private Set<Long> skuIds;
    /** 唯一商品 */
    private Set<String> skuAttrIds;
    /** 唯一商品数量 */
    private Map<String, Double> skuAttrIdQtys;
    /** 序列号 */
    private Map<String, Set<String>> snDefects;
    /** 已推荐体积 */
    private Double volumes = new Double(0);
    /** 已推荐重量 */
    private Double weighs = new Double(0);

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public Set<Long> getSkuIds() {
        return skuIds;
    }

    public void setSkuIds(Set<Long> skuIds) {
        this.skuIds = skuIds;
    }

    public Set<String> getSkuAttrIds() {
        return skuAttrIds;
    }

    public void setSkuAttrIds(Set<String> skuAttrIds) {
        this.skuAttrIds = skuAttrIds;
    }

    public Map<String, Double> getSkuAttrIdQtys() {
        return skuAttrIdQtys;
    }

    public void setSkuAttrIdQtys(Map<String, Double> skuAttrIdQtys) {
        this.skuAttrIdQtys = skuAttrIdQtys;
    }

    public Map<String, Set<String>> getSnDefects() {
        return snDefects;
    }

    public void setSnDefects(Map<String, Set<String>> snDefects) {
        this.snDefects = snDefects;
    }

    public Double getVolumes() {
        return volumes;
    }

    public void setVolumes(Double volumes) {
        this.volumes = volumes;
    }

    public Double getWeighs() {
        return weighs;
    }

    public void setWeighs(Double weighs) {
        this.weighs = weighs;
    }


}
