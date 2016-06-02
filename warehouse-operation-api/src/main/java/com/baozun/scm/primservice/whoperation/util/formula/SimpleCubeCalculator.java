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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lichuan
 *
 */
public class SimpleCubeCalculator {
    /**
     * 简单立方体计算器
     */

    public static final String COORDS_X = "x";
    public static final String COORDS_Y = "y";
    public static final String COORDS_Z = "z";
    public static final String COORDS_ALL = "";
    private boolean isInit = false;
    private boolean isInitStuffCube = false;
    private Double _x;
    private Double _y;
    private Double _z;
    private Double _remainX;
    private Double _remainY;
    private Double _remainZ;
    private Double _rawX;
    private Double _rawY;
    private Double _rawZ;
    private String coordinate;
    private static final String coords_x = COORDS_X;
    private static final String coords_y = COORDS_Y;
    private static final String coords_z = COORDS_Z;
    private static final String coords_all = COORDS_ALL;
    private static final String[][] coordsCache = new String[][] {{"x", "y", "z"}, {"y", "z", "x"}, {"z", "x", "y"}};
    private static final String[][] coordsCacheAll = new String[][] {{"x", "y", "z"}, {"x", "z", "y"}, {"y", "z", "x"}, {"y", "x", "z"}, {"z", "x", "y"}, {"z", "y", "x"}};
    private Double x;
    private Double y;
    private Double z;
    private Double rawX;
    private Double rawY;
    private Double rawZ;
    private String _uom;
    private String uom;
    private static final String defaultUom = "m";
    private static final String[] uomCache = new String[] {"um", "mm", "cm", "dm", "m", "km"};
    private static int uomSize = 6;
    private static Map<String, Integer> uomConversion = new HashMap<String, Integer>();
    private Double _cubage;
    private Double _rawCubage;
    private Double cubage;
    private Double initStuffCubage;
    private Double rawCubage;
    private Double availability = 0.8;
    private Double availableCubage;
    @SuppressWarnings("unused")
    private boolean isCubageAvailable;
    private boolean isLengthAvailable;
    @SuppressWarnings("unused")
    private boolean isAvailable;

    private void preInit() {
        uomConversion.put("um", 1000);
        uomConversion.put("mm", 10);
        uomConversion.put("cm", 10);
        uomConversion.put("dm", 10);
        uomConversion.put("dm", 10);
        uomConversion.put("m", 1);
        uomConversion.put("km", 1000);
        uomSize = uomCache.length;
        isCubageAvailable = false;
        isLengthAvailable = false;
        isAvailable = false;
        coordinate = coords_all;
    }

    public SimpleCubeCalculator() {
        preInit();
    }

    public SimpleCubeCalculator(Double _x, Double _y, Double _z, String _uom, Double availability) {
        preInit();
        init(_x, _y, _z, _uom, availability);
        setInit(true);
    }

    public Double getTotalCubage() {
        isInitialization();
        return get_cubage();
    }

    public Double getTotalAvailableCubage() {
        isInitialization();
        return getAvailableCubage();
    }

    public Double addStuffCubage(Double c) {
        Double ret = getCubage();
        ret += c;
        setCubage(ret);
        return ret;
    }

    public Double getStuffCubage() {
        return getCubage();
    }

    public Double calculateStuffCubage(Double actualX, Double actualY, Double actualZ, String actualUom) {
        if (false == isInitStuffCube()) {
            setCubage(0.0);
        }
        Double cubage = getCubage();
        Double ax = uomConversion(actualUom, actualX);
        Double ay = uomConversion(actualUom, actualY);
        Double az = uomConversion(actualUom, actualZ);
        cubage += cubageFormula(ax, ay, az);
        addStuffCubage(cubage);
        return getStuffCubage();
    }

    private Double calculateRemainCubage(Double availableCubage, Double cubage) {
        return availableCubage - cubage + getInitStuffCubage();
    }

    private Double calculateRemainZ(Double remainCubage, Double x, Double y) {
        Double rz = 0.0;
        isLengthSupport(x);
        isLengthSupport(y);
        rz = (remainCubage / x / y);
        return rz;
    }

    private Double calculateRemainY(Double remainCubage, Double z, Double x) {
        Double rz = 0.0;
        isLengthSupport(z);
        isLengthSupport(x);
        rz = (remainCubage / z / x);
        return rz;
    }

    private Double calculateRemainX(Double remainCubage, Double y, Double z) {
        Double rz = 0.0;
        isLengthSupport(y);
        isLengthSupport(z);
        rz = (remainCubage / y / z);
        return rz;
    }

    private Double cubageFormula(Double x, Double y, Double z) {
        Double ret = 0.0;
        if (null == x) x = 0.0;
        if (null == y) y = 0.0;
        if (null == z) z = 0.0;
        isLengthSupport(x);
        isLengthSupport(y);
        isLengthSupport(z);
        ret = x * y * z;
        return ret;
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
            throw new IllegalArgumentException("simpleCube uomConversion actual value is invalid error!");
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
                    if (currentIndex > actualIndex) {
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
                    if (currentIndex > baseIndex) {
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

    @SuppressWarnings("unused")
    private String[] reverseUomCache() {
        String[] temp = new String[] {};
        List<String> uomList = new ArrayList<String>();
        List<String> reverseList = new ArrayList<String>();
        for (String u : uomCache) {
            uomList.add(u);
        }
        for (int i = uomList.size(); i > 0; i--) {
            reverseList.add(uomList.get(i - 1));
        }
        temp = (String[]) reverseList.toArray();
        return temp;
    }

    private void init(Double _x, Double _y, Double _z, String _uom, Double availability) {
        if (null == _x) _x = 0.0;
        if (null == _y) _y = 0.0;
        if (null == _z) _z = 0.0;
        if (null == availability) availability = 1.0;
        isLengthSupport(_x);
        isLengthSupport(_y);
        isLengthSupport(_z);
        isUomSupport(_uom);
        set_rawX(_x);
        set_rawY(_y);
        set_rawZ(_z);
        set_uom(_uom);
        set_rawCubage(cubageFormula(_x, _y, _z));
        Double rx = uomConversion(_uom, _x);
        Double ry = uomConversion(_uom, _y);
        Double rz = uomConversion(_uom, _z);
        set_x(rx);
        set_y(ry);
        set_z(rz);
        set_cubage(cubageFormula(rx, ry, rz));
        setAvailability(availability);
        setAvailableCubage(get_cubage() * getAvailability());
    }

    private void isLengthSupport(Double len) {
        if (null == len || 1 != len.compareTo(0.0)) {
            throw new IllegalArgumentException("isLengthSupport len is valid error!");
        }
    }

    public void initStuffCube(Double x, Double y, Double z, String uom) {
        if (null == x) x = 0.0;
        if (null == y) y = 0.0;
        if (null == z) z = 0.0;
        isLengthSupport(x);
        isLengthSupport(y);
        isLengthSupport(z);
        isUomSupport(uom);
        setRawX(x);
        setRawY(y);
        setRawZ(z);
        setRawCubage(cubageFormula(x, y, z));
        setUom(uom);
        Double rx = uomConversion(uom, x);
        Double ry = uomConversion(uom, y);
        Double rz = uomConversion(uom, z);
        setX(rx);
        setY(ry);
        setZ(rz);
        setCubage(cubageFormula(rx, ry, rz));
        setInitStuffCubage(getCubage());
        setInitStuffCube(true);
    }

    private void isInitialization() {
        if (false == isInit()) {
            throw new RuntimeException("not initailization error!");
        }
    }

    public boolean calculateCubageAvailable(Double x, Double y, Double z, String uom) {
        boolean ret = false;
        isInitialization();
        if (false == isInitStuffCube()) {
            initStuffCube(x, y, z, uom);
        }
        ret = isCubageAvailable();
        setCubageAvailable(ret);
        return ret;
    }

    private Map<String, Double> coordsValues(String[] cds, Double x, Double y, Double z) {
        Map<String, Double> values = new HashMap<String, Double>();
        int index = 1;
        for (String c : cds) {
            if (1 == index) {
                values.put(c, x);
                index++;
                continue;
            }
            if (2 == index) {
                values.put(c, y);
                index++;
                continue;
            }
            if (3 == index) {
                values.put(c, z);
                index++;
                break;
            }
        }
        return values;
    }

    private boolean permutationCompare(Double rx, Double ry, Double rz, Double ax, Double ay, Double az) {
        boolean ret = false;
        for (String[] rcds : coordsCache) {
            Map<String, Double> rValues = coordsValues(rcds, rx, ry, rz);
            Double rxVal = rValues.get(coords_x);
            Double ryVal = rValues.get(coords_y);
            Double rzVal = rValues.get(coords_z);
            for (String[] acds : coordsCacheAll) {
                Map<String, Double> aValues = coordsValues(acds, ax, ay, az);
                Double axVal = aValues.get(coords_x);
                Double ayVal = aValues.get(coords_y);
                Double azVal = aValues.get(coords_z);
                if ((1 != axVal.compareTo(rxVal)) && (1 != ayVal.compareTo(ryVal)) && (1 != azVal.compareTo(rzVal))) {
                    ret = true;
                }
            }
        }
        return ret;
    }

    private boolean calculateLengthAvailable4Coords(String coords) {
        boolean ret = false;
        if (coords_z.equals(coords)) {
            Double rx = get_x();
            Double ry = get_y();
            set_remainZ(calculateRemainZ(calculateRemainCubage(getTotalAvailableCubage(), getStuffCubage()), rx, ry));
            Double rz = get_remainZ();
            Double ax = getX();
            Double ay = getY();
            Double az = getZ();
            ret = permutationCompare(rx, ry, rz, ax, ay, az);
        } else if (coords_y.equals(coords)) {
            Double rx = get_x();
            Double rz = get_z();
            set_remainY(calculateRemainY(calculateRemainCubage(getTotalAvailableCubage(), getStuffCubage()), rz, rx));
            Double ry = get_remainY();
            Double ax = getX();
            Double ay = getY();
            Double az = getZ();
            ret = permutationCompare(rx, ry, rz, ax, ay, az);
        } else if (coords_x.equals(coords)) {
            Double ry = get_y();
            Double rz = get_z();
            set_remainX(calculateRemainX(calculateRemainCubage(getTotalAvailableCubage(), getStuffCubage()), ry, rz));
            Double rx = get_remainX();
            Double ax = getX();
            Double ay = getY();
            Double az = getZ();
            ret = permutationCompare(rx, ry, rz, ax, ay, az);
        } else if (coords_all.equals(coords)) {

        } else {
            throw new IllegalArgumentException("calculateLengthAvailable4Coords invalid coords:" + coords);
        }
        return ret;
    }

    public boolean calculateLengthAvailable(Double x, Double y, Double z, String uom) {
        boolean ret = false;
        isInitialization();
        if (false == isInitStuffCube()) {
            initStuffCube(x, y, z, uom);
        }
        if (coords_z.equals(getCoordinate())) {
            ret = calculateLengthAvailable4Coords(coords_z);
        } else if (coords_y.equals(getCoordinate())) {
            ret = calculateLengthAvailable4Coords(coords_y);
        } else if (coords_x.equals(getCoordinate())) {
            ret = calculateLengthAvailable4Coords(coords_x);
        } else {
            ret = (calculateLengthAvailable4Coords(coords_z) || calculateLengthAvailable4Coords(coords_y) || calculateLengthAvailable4Coords(coords_x));
        }
        return ret;
    }

    private boolean calculateAvailable(Double x, Double y, Double z, String uom) {
        boolean cubageAvail = calculateCubageAvailable(x, y, z, uom);
        setCubageAvailable(cubageAvail);
        boolean lenAvail = calculateLengthAvailable(x, y, z, uom);
        setLengthAvailable(lenAvail);
        return isAvailable();
    }

    public boolean calculateAvailable() {
        boolean ret = false;
        if (false == isInitStuffCube()) {
            return ret;
        }
        return calculateAvailable(getX(), getY(), getZ(), getUom());
    }

    public boolean isInit() {
        return isInit;
    }

    public void setInit(boolean isInit) {
        this.isInit = isInit;
    }

    public boolean isInitStuffCube() {
        return isInitStuffCube;
    }

    public void setInitStuffCube(boolean isInitStuffCube) {
        this.isInitStuffCube = isInitStuffCube;
    }

    public Double get_x() {
        return _x;
    }

    public void set_x(Double _x) {
        this._x = _x;
    }

    public Double get_y() {
        return _y;
    }

    public void set_y(Double _y) {
        this._y = _y;
    }

    public Double get_z() {
        return _z;
    }

    public void set_z(Double _z) {
        this._z = _z;
    }

    public Double get_remainX() {
        return _remainX;
    }

    public void set_remainX(Double _remainX) {
        this._remainX = _remainX;
    }

    public Double get_remainY() {
        return _remainY;
    }

    public void set_remainY(Double _remainY) {
        this._remainY = _remainY;
    }

    public Double get_remainZ() {
        return _remainZ;
    }

    public void set_remainZ(Double _remainZ) {
        this._remainZ = _remainZ;
    }

    public Double get_rawX() {
        return _rawX;
    }

    public void set_rawX(Double _rawX) {
        this._rawX = _rawX;
    }

    public Double get_rawY() {
        return _rawY;
    }

    public void set_rawY(Double _rawY) {
        this._rawY = _rawY;
    }

    public Double get_rawZ() {
        return _rawZ;
    }

    public void set_rawZ(Double _rawZ) {
        this._rawZ = _rawZ;
    }

    public String getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(String coordinate) {
        this.coordinate = coordinate;
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public Double getZ() {
        return z;
    }

    public void setZ(Double z) {
        this.z = z;
    }

    public Double getRawX() {
        return rawX;
    }

    public void setRawX(Double rawX) {
        this.rawX = rawX;
    }

    public Double getRawY() {
        return rawY;
    }

    public void setRawY(Double rawY) {
        this.rawY = rawY;
    }

    public Double getRawZ() {
        return rawZ;
    }

    public void setRawZ(Double rawZ) {
        this.rawZ = rawZ;
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

    public int getUomSize() {
        return uomSize;
    }

    public Double get_cubage() {
        return _cubage;
    }

    public void set_cubage(Double _cubage) {
        this._cubage = _cubage;
    }

    public Double get_rawCubage() {
        return _rawCubage;
    }

    public void set_rawCubage(Double _rawCubage) {
        this._rawCubage = _rawCubage;
    }

    public Double getCubage() {
        return cubage;
    }

    public void setCubage(Double cubage) {
        this.cubage = cubage;
    }
    
    public Double getInitStuffCubage() {
        return initStuffCubage;
    }

    public void setInitStuffCubage(Double initStuffCubage) {
        this.initStuffCubage = initStuffCubage;
    }

    public Double getRawCubage() {
        return rawCubage;
    }

    public void setRawCubage(Double rawCubage) {
        this.rawCubage = rawCubage;
    }

    public Double getAvailability() {
        return availability;
    }

    public void setAvailability(Double availability) {
        this.availability = availability;
    }

    public Double getAvailableCubage() {
        return availableCubage;
    }

    public void setAvailableCubage(Double availableCubage) {
        this.availableCubage = availableCubage;
    }

    public boolean isCubageAvailable() {
        Double availableCubage = getAvailableCubage();
        Double cubage = getCubage();
        return (availableCubage - cubage >= 0 ? true : false);
    }

    public void setCubageAvailable(boolean isCubageAvailable) {
        this.isCubageAvailable = isCubageAvailable;
    }

    public boolean isLengthAvailable() {
        return isLengthAvailable;
    }

    public void setLengthAvailable(boolean isLengthAvailable) {
        this.isLengthAvailable = isLengthAvailable;
    }

    public boolean isAvailable() {
        boolean isCubageAvailable = isCubageAvailable();
        boolean isLengthAvailable = isLengthAvailable();
        return (isCubageAvailable & isLengthAvailable);
    }

    public void setAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }


}
