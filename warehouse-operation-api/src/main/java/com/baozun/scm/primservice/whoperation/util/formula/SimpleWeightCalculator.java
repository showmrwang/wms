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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author lichuan
 *
 */
public class SimpleWeightCalculator {
    /**
     * 简单重量计算器
     */

    public static final String SYS_UOM = "1";
    private boolean isInit = false;
    private boolean isInitStuffWeight = false;
    private Double _weight;
    private Double _rawWeight;
    private Double weight = 0.0;
    private Double currentStuffWeight = 0.0;
    private Double rawWeight;
    // 已放入重量
    private Double usedWeight;
    // 已放入原始重量
    private Double rawUsedWeight;
    private String _uom;
    private String uom;
    private static final String sysUom = SYS_UOM;
    private static final Double sysUomValue = 1.0;
    private static String defaultUom = sysUom;
    @SuppressWarnings("unused")
    private static Double defaultUomConversion = 1.0;
    private static Double defaultUomValue = 1.0;
    private static String[] uomCache = new String[] {sysUom};
    private static int uomSize = 1;
    private static Map<String, Double> uomConversion = new HashMap<String, Double>();
    private Double availability = 1.0;
    private Double availableWeight;
    private boolean isWeightAvailable;

    private void preInit(Map<String, Double> uomConversionRate, String dUom) {
        uomConversion.put(sysUom, sysUomValue);
        if (null != uomConversionRate) {
            uomConversion.putAll(uomConversionRate);
            Set<String> uoms = new HashSet<String>();
            Set<String> keys = uomConversionRate.keySet();
            uoms.addAll(keys);
            uoms.add(sysUom);
            uomCache = new String[] {};
            uomCache = uoms.toArray(uomCache);
        }
        defaultUom = dUom;
        defaultUomConversion = uomConversion.get(dUom);
        defaultUomValue = (uomConversion.get(dUom) * sysUomValue);
        uomSize = uomCache.length;
        setAvailability(1.0);
    }

    public SimpleWeightCalculator(Map<String, Double> uomConversionRate) {
        preInit(uomConversionRate, defaultUom);
    }

    public SimpleWeightCalculator(Map<String, Double> uomConversionRate, String defaultUom) {
        preInit(uomConversionRate, defaultUom);
    }

    public SimpleWeightCalculator(Double _weight, String _uom, Map<String, Double> uomConversionRate) {
        preInit(uomConversionRate, defaultUom);
        init(_weight, _uom);
        setInit(true);
    }

    public SimpleWeightCalculator(Double _weight, String _uom, Map<String, Double> uomConversionRate, String defaultUom) {
        preInit(uomConversionRate, defaultUom);
        init(_weight, _uom);
        setInit(true);
    }
    
    public SimpleWeightCalculator(Double _weight, String _uom, Map<String, Double> uomConversionRate, Double usedWeight) {
        preInit(uomConversionRate, defaultUom);
        init(_weight, _uom, usedWeight);
        setInit(true);
    }

    public Double getTotalWeight() {
        isInitialization();
        return get_weight();
    }

    public Double getTotalAvailableWeight() {
        isInitialization();
        return getAvailableWeight();
    }

    public Double addStuffWeight(Double w) {
        Double weight = getStuffWeight();
        weight += w;
        setWeight(weight);
        return weight;
    }
    
    public Double subtractStuffWeight(Double w) {
        Double weight = getStuffWeight();
        weight -= w;
        setWeight(weight);
        return weight;
    }

    public Double getStuffWeight() {
        return getWeight();
    }

    public Double calculateStuffWeight(Double actualWeight) {
        return calculateStuffWeight(actualWeight, defaultUom);
    }

    public Double calculateStuffWeight(Double actualWeight, String actualUom) {
        Double rw = uomConversion(actualUom, actualWeight);
        return rw;
    }

    public Double accumulationStuffWeight(Double actualWeight) {
        return accumulationStuffWeight(actualWeight, defaultUom);
    }

    public Double accumulationStuffWeight(Double actualWeight, String actualUom) {
        if (false == isInitStuffWeight()) {
            initStuffWeight(0.0, defaultUom);
        }
        Double rw = uomConversion(actualUom, actualWeight);
        setCurrentStuffWeight(rw);
        addStuffWeight(rw);
        return getStuffWeight();
    }

    private void isInitialization() {
        if (false == isInit()) {
            throw new RuntimeException("not initailization error!");
        }
    }

    private boolean calculateWeightAvailable(Double weight, String uom) {
        boolean ret = false;
        isInitialization();
        if (false == isInitStuffWeight()) {
            initStuffWeight(weight, uom);
        }
        ret = ((getTotalAvailableWeight() - getStuffWeight()) >= 0 ? true : false);
        return ret;
    }

    private boolean calculateAvailable(Double weight, String uom) {
        boolean weightAvail = calculateWeightAvailable(weight, uom);
        setWeightAvailable(weightAvail);
        return isWeightAvailable();
    }

    public boolean calculateAvailable() {
        boolean ret = false;
        if (false == isInitStuffWeight()) {
            return ret;
        }
        ret = calculateAvailable(getWeight(), getUom());
        return ret;
    }

    private void init(Double _weight, String _uom) {
        set_rawWeight(_weight);
        set_uom(_uom);
        isWeightSupport(_weight);
        isUomSupport(_uom);
        Double rw = uomConversion(_uom, _weight);
        setCurrentStuffWeight(rw);
        set_weight(rw);
        setAvailability(1.0);
        setAvailableWeight(get_weight() * getAvailability());
    }
    
    private void init(Double _weight, String _uom, Double usedWeight) {
        init(_weight, _uom);
        if (null == usedWeight) usedWeight = 0.0;
        setUsedWeight(usedWeight);
        setRawUsedWeight(usedWeight);
        setAvailableWeight((get_weight() * getAvailability()) - usedWeight);
    }

    public void initStuffWeight(Double weight, String uom) {
        setRawWeight(weight);
        setUom(uom);
        isWeightSupport(weight);
        isUomSupport(uom);
        Double rw = uomConversion(uom, weight);
        setCurrentStuffWeight(rw);
        setWeight(rw);
        setInitStuffWeight(true);
    }
    
    public void initStuffWeight(Double weight, Double qty, String uom) {
        setRawWeight(weight);
        setUom(uom);
        isWeightSupport(weight);
        isUomSupport(uom);
        Double rw = uomConversion(uom, weight);
        setCurrentStuffWeight(rw * qty);
        setWeight(rw * qty);
        setInitStuffWeight(true);
    }

    @SuppressWarnings("unused")
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
        if (null == actulValue) {
            throw new IllegalArgumentException("simpleWeight uomConversion actual value is invalid error!");
        }
        actualUom = actualUom.trim();
        isUomSupport(actualUom);
        Double conversionRate = uomConversion.get(actualUom);
        ret = (ret * (conversionRate * sysUomValue) / defaultUomValue);
        return ret;
    }

    private void isWeightSupport(Double weight) {
        if (null == weight) {
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

    public boolean isInitStuffWeight() {
        return isInitStuffWeight;
    }

    public void setInitStuffWeight(boolean isInitStuffWeight) {
        this.isInitStuffWeight = isInitStuffWeight;
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
    
    public Double getCurrentStuffWeight() {
        return currentStuffWeight;
    }

    public void setCurrentStuffWeight(Double currentStuffWeight) {
        this.currentStuffWeight = currentStuffWeight;
    }

    public Double getRawWeight() {
        return rawWeight;
    }

    public void setRawWeight(Double rawWeight) {
        this.rawWeight = rawWeight;
    }

    public Double getUsedWeight() {
        return usedWeight;
    }

    public void setUsedWeight(Double usedWeight) {
        this.usedWeight = usedWeight;
    }

    public Double getRawUsedWeight() {
        return rawUsedWeight;
    }

    public void setRawUsedWeight(Double rawUsedWeight) {
        this.rawUsedWeight = rawUsedWeight;
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

    public Double getAvailability() {
        return availability;
    }

    public void setAvailability(Double availability) {
        this.availability = availability;
    }

    public Double getAvailableWeight() {
        return availableWeight;
    }

    public void setAvailableWeight(Double availableWeight) {
        this.availableWeight = availableWeight;
    }

    public boolean isWeightAvailable() {
        return isWeightAvailable;
    }

    public void setWeightAvailable(boolean isWeightAvailable) {
        this.isWeightAvailable = isWeightAvailable;
    }


}
