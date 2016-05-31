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
package com.baozun.scm.primservice.whoperation.util.formula;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lichuan
 *
 */
public class SimpleWeightCalculator {
    /**
     * 简单重量计算器
     */

    private boolean isInit = false;
    private Double _weight;
    private Double _rawWeight;
    private Double weight;
    private Double rawWeight;
    private String _uom;
    private String uom;
    private static final String[] uomCache = new String[] {"mg", "g", "kg", "t"};
    private static final String defaultUom = "g";
    private static int uomSize = 5;
    private static Map<String, Integer> uomConversion = new HashMap<String, Integer>();
    
    private void preInit(){
        uomConversion.put("mg", 1000);
        uomConversion.put("g", 1);
        uomConversion.put("kg", 1000);
        uomConversion.put("t", 1000);
        uomSize = uomCache.length;
    }
    
    public SimpleWeightCalculator(){
        preInit();
    }
    
    public SimpleWeightCalculator(Double _weight, String _uom){
        preInit();
        setInit(true);
        init(_weight, _uom);
    }
    
    private void init(Double _weight, String _uom){
        set_rawWeight(_weight);
        set_uom(_uom);
        isWeightSupport(_weight);
        isUomSupport(_uom);
        Double rw = uomConversion(_uom, _weight);
        set_weight(rw);
        
    }
    
    public void initStuffWeight(Double weight, String uom){
        setRawWeight(weight);
        setUom(uom);
        isWeightSupport(weight);
        isUomSupport(uom);
        Double rw = uomConversion(uom, weight);
        setWeight(rw);
    }
    
    private int getUomIndex(String actualUom) {
        int index = 1;
        if (null == actualUom || "".equals(actualUom)) throw new IllegalArgumentException("getUomIndex actual uom is null error!");
        actualUom = actualUom.trim();
        isUomSupport(actualUom);
        for (String u : uomCache) {
            if (u.equals(actualUom)) {
                break;
            } else {
                index++;
            }
        }
        return index;
    }
    
    private Double uomConversion(String actualUom, Double actulValue) {
        Double ret = 0.0;
        ret = actulValue;
        if (null == actualUom || "".equals(actualUom)) throw new IllegalArgumentException("uomConversion actual uom is null error!");
        if (null == actulValue || 1 != actulValue.compareTo(0.0)) {
            throw new IllegalArgumentException("simpleWeight uomConversion actual value is invalid error!");
        }
        actualUom = actualUom.trim();
        isUomSupport(actualUom);
        int baseIndex = getUomIndex(defaultUom);
        int actualIndex = getUomIndex(actualUom);
        if (actualIndex <= baseIndex) {
            int currentIndex = 1;
            for (String u : uomCache) {
                if (currentIndex <= baseIndex) {
                    currentIndex++;
                    if (currentIndex >= actualIndex) {
                        int conver = uomConversion.get(u);
                        ret = ret / conver;
                    } else {
                        continue;
                    }
                } else {
                    break;
                }
            }
        } else {
            // String[] reverseCahce = reverseUomCache();
            int currentIndex = 1;
            for (String u : uomCache) {
                if (currentIndex <= actualIndex) {
                    currentIndex++;
                    if (currentIndex >= baseIndex) {
                        int conver = uomConversion.get(u);
                        ret = ret * conver;
                    } else {
                        continue;
                    }
                } else {
                    break;
                }
            }
        }
        return ret;
    }
    
    private void isWeightSupport(Double weight) {
        if (null == weight || 1 != weight.compareTo(0.0)) {
            throw new IllegalArgumentException("isLengthSupport len is valid error!");
        }
    }
    
    private void isUomSupport(String actualUom) {
        boolean ret = false;
        if (null == actualUom || "".equals(actualUom)) throw new IllegalArgumentException("isUomSupport actual uom is null error!");
        actualUom = actualUom.trim();
        for (String u : uomCache) {
            if (u.equals(actualUom)) {
                ret = true;
            }
        }
        if (false == ret) {
            throw new IllegalArgumentException("isUomSupport actual uom is not support error!");
        }
    }

    public boolean isInit() {
        return isInit;
    }

    public void setInit(boolean isInit) {
        this.isInit = isInit;
    }

    public Double get_weight() {
        return _weight;
    }

    public void set_weight(Double _weight) {
        this._weight = _weight;
    }

    public Double get_rawWeight() {
        return _rawWeight;
    }

    public void set_rawWeight(Double _rawWeight) {
        this._rawWeight = _rawWeight;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getRawWeight() {
        return rawWeight;
    }

    public void setRawWeight(Double rawWeight) {
        this.rawWeight = rawWeight;
    }

    public String get_uom() {
        return _uom;
    }

    public void set_uom(String _uom) {
        this._uom = _uom;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public static int getUomSize() {
        return uomSize;
    }

    public static void setUomSize(int uomSize) {
        SimpleWeightCalculator.uomSize = uomSize;
    }


}
