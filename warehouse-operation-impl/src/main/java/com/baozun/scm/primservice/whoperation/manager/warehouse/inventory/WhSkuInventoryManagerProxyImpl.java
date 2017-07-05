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
package com.baozun.scm.primservice.whoperation.manager.warehouse.inventory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.baozun.scm.baseservice.sac.manager.CodeManager;
import com.baozun.scm.primservice.whoperation.command.pda.rcvd.RcvdWorkFlow;
import com.baozun.scm.primservice.whoperation.command.sku.SkuRedisCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.conf.basis.WarehouseDefectTypeCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.excel.ExcelContext;
import com.baozun.scm.primservice.whoperation.excel.ExcelImport;
import com.baozun.scm.primservice.whoperation.excel.exception.ExcelException;
import com.baozun.scm.primservice.whoperation.excel.exception.RootExcelException;
import com.baozun.scm.primservice.whoperation.excel.result.ExcelImportResult;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.bi.OutPutStreamToServersManager;
import com.baozun.scm.primservice.whoperation.manager.redis.SkuRedisManager;
import com.baozun.scm.primservice.whoperation.manager.system.SysDictionaryManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.ContainerManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.CustomerManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.InventoryStatusManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.LocationManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.StoreDefectReasonsManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.StoreDefectTypeManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.StoreManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WarehouseDefectReasonsManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WarehouseDefectTypeManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WarehouseManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhSkuLocationManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhSkuManager;
import com.baozun.scm.primservice.whoperation.model.ResponseMsg;
import com.baozun.scm.primservice.whoperation.model.bi.ImportExcel;
import com.baozun.scm.primservice.whoperation.model.system.SysDictionary;
import com.baozun.scm.primservice.whoperation.model.warehouse.Customer;
import com.baozun.scm.primservice.whoperation.model.warehouse.InventoryStatus;
import com.baozun.scm.primservice.whoperation.model.warehouse.Location;
import com.baozun.scm.primservice.whoperation.model.warehouse.Store;
import com.baozun.scm.primservice.whoperation.model.warehouse.Warehouse;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhSku;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhSkuLocation;
import com.baozun.scm.primservice.whoperation.model.warehouse.conf.basis.WarehouseDefectReasons;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventory;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventorySn;
import com.baozun.scm.primservice.whoperation.util.SkuInventoryUuid;

@Service("whSkuInventoryManagerProxy")
public class WhSkuInventoryManagerProxyImpl implements WhSkuInventoryManagerProxy {

    @Autowired
    private OutPutStreamToServersManager outPutStreamToServersManager;
    @Autowired
    private InventoryStatusManager inventoryStatusManager;
    @Autowired
    private SysDictionaryManager sysDictionaryManager;
    @Autowired
    private SkuRedisManager skuRedisManager;
    @Autowired
    private WhSkuManager whSkuManager;
    @Autowired
    private LocationManager locationManager;
    @Autowired
    private WhSkuLocationManager whSkulocationManager;
    @Autowired
    private ContainerManager containerManager;
    @Autowired
    private WhSkuInventoryManager whSkuInventoryManager;
    @Autowired
    private WarehouseManager warehouseManager;
    @Autowired
    private StoreManager storeManager;
    @Autowired
    private CustomerManager customerManager;
    @Autowired
    private StoreDefectReasonsManager storeDefectReasonsManager;
    @Autowired
    private WarehouseDefectReasonsManager warehouseDefectReasonsManager;
    @Autowired
    private StoreDefectTypeManager storeDefectTypeManager;
    @Autowired
    private WarehouseDefectTypeManager warehouseDefectTypeManager;
    @Autowired
    private CodeManager codeManager;

    @Override
    public ResponseMsg initSkuInv(String url, String fileName, Long userImportExcelId, Locale locale, Long ouId, Long userId, String logId) {

        File importExcelFile = new File(url, fileName);
        if (!importExcelFile.exists()) {
            throw new BusinessException(ErrorCodes.IMPORT_ERROR_FILE_NOT_EXISTS);
        }
        try {
            // 创建excel上下文实例,它的构成需要配置文件的路径
            ExcelContext context = new ExcelContext("excel-config.xml");

            List<WhSkuInventoryCommand> skuInvCommandList = new ArrayList<WhSkuInventoryCommand>();

            // 入库单头信息
            ExcelImportResult skuInvExcelImportResult = this.readSheetFromExcel(context, importExcelFile, locale, Constants.IMPORT_SKUINV_INIT_EXCEL_CONFIG_ID, Constants.IMPORT_SKUINV_INIT_TITLE_INDEX);
            if (ExcelImportResult.READ_STATUS_SUCCESS == skuInvExcelImportResult.getReadstatus()) {
                // 验证商品信息完整及是否存在重复商品
                // this.validateSkuInv(skuInvExcelImportResult, skuInvList, locale, userId, logId,
                // ouId);
                this.validateSkuInvSimple(skuInvExcelImportResult, skuInvCommandList, locale, userId, logId, ouId);
            }

            if (ExcelImportResult.READ_STATUS_FAILED == skuInvExcelImportResult.getReadstatus()) {
                Workbook workbook = skuInvExcelImportResult.getWorkbook();
                ExcelImport.exportImportErroeMsg(workbook, skuInvExcelImportResult.getRootExcelException());

                ImportExcel importExcel = new ImportExcel();
                importExcel.setImportType("SKUINV_INIT");
                importExcel.setWorkbook(workbook);
                importExcel.setUserImportExcelId(userImportExcelId);
                importExcel.setUserId(userId);
                importExcel.setOuId(ouId);

                // 调用异常输出接口，生成错误信息
                String errorFileName = outPutStreamToServersManager.uploadImportFileErrorToShard(importExcel);

                ResponseMsg msg = new ResponseMsg();
                msg.setResponseStatus(ResponseMsg.STATUS_ERROR);
                msg.setMsg(errorFileName);
                return msg;
            }

            Map<String,WhSkuInventory> uuidInvMap=new HashMap<String,WhSkuInventory>();
            Map<String, List<WhSkuInventorySn>> uuidSnMap = new HashMap<String, List<WhSkuInventorySn>>();
            for (WhSkuInventoryCommand command : skuInvCommandList) {
                WhSkuInventory inv = new WhSkuInventory();
                BeanUtils.copyProperties(command, inv);
                inv.setOuId(ouId);
                String uuid = SkuInventoryUuid.invUuid(inv);
                if(StringUtils.isEmpty(uuid)){
                    throw new BusinessException(ErrorCodes.UUID_GENERATE_ERROR);
                }
                if(uuidInvMap.containsKey(uuid)){
                    WhSkuInventory uuidInv=uuidInvMap.get(uuid);
                    uuidInv.setOnHandQty(uuidInv.getOnHandQty() + inv.getOnHandQty());
                    uuidInvMap.put(uuid, uuidInv);
                } else {
                    inv.setUuid(uuid);
                    uuidInvMap.put(uuid, inv);
                }
                if (command.getDefectTypeId() != null || StringUtils.hasText(command.getSkuSn())) {
                    if (StringUtils.hasText(command.getSkuSn())) {
                        WhSkuInventorySn sn = new WhSkuInventorySn();
                        sn.setSkuId(inv.getSkuId());
                        sn.setSn(command.getSkuSn());
                        sn.setDefectSource(Constants.SKU_SN_DEFECT_SOURCE_WH);
                        sn.setDefectTypeId(command.getDefectTypeId());
                        sn.setDefectReasonsId(command.getDefectReasonsId());
                        sn.setDefectWareBarcode(this.codeManager.generateCode(Constants.WMS, Constants.INVENTORY_DEFECT_WARE_BARCODE, null, null, null));
                        sn.setStatus(1);
                        sn.setUuid(uuid);
                        sn.setOuId(ouId);
                        if (uuidSnMap.containsKey(uuid)) {
                            List<WhSkuInventorySn> snList = uuidSnMap.get(uuid);
                            snList.add(sn);
                            uuidSnMap.put(uuid, snList);
                        } else {
                            List<WhSkuInventorySn> snList = new ArrayList<WhSkuInventorySn>();
                            snList.add(sn);
                            uuidSnMap.put(uuid, snList);
                        }
                    } else {
                        List<WhSkuInventorySn> snList = new ArrayList<WhSkuInventorySn>();
                        if (uuidSnMap.containsKey(uuid)) {
                            snList = uuidSnMap.get(uuid);
                        }
                        List<String> barCodeList = this.codeManager.generateCodeList(Constants.WMS, Constants.INVENTORY_DEFECT_WARE_BARCODE, null, null, null, command.getOnHandQty().intValue()).toArray();
                        for (int i = 0; i < command.getOnHandQty(); i++) {
                            WhSkuInventorySn sn = new WhSkuInventorySn();
                            sn.setSkuId(inv.getSkuId());
                            sn.setDefectSource(Constants.SKU_SN_DEFECT_SOURCE_WH);
                            sn.setDefectTypeId(command.getDefectTypeId());
                            sn.setDefectReasonsId(command.getDefectReasonsId());
                            sn.setDefectWareBarcode(barCodeList.get(i));
                            sn.setStatus(1);
                            sn.setUuid(uuid);
                            sn.setOuId(ouId);
                            snList.add(sn);
                        }
                        uuidSnMap.put(uuid, snList);
                    }
                }

                
            }

            Warehouse wh = this.warehouseManager.findWarehouseById(ouId);
            this.whSkuInventoryManager.batchInsert(uuidInvMap, uuidSnMap, wh, userId);

            ResponseMsg msg = new ResponseMsg();
            msg.setResponseStatus(ResponseMsg.STATUS_SUCCESS);
            msg.setMsg("SUCCESS");
            return msg;
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(ErrorCodes.IMPORT_ERROR);
        }

    }

    /**
     * 无库位混放校验逻辑的库存初始化导入
     * 
     * @param excelImportResult
     * @param skuInvList
     * @param locale
     * @param userId
     * @param logId
     * @param ouId
     */
    private void validateSkuInvSimple(ExcelImportResult excelImportResult, List<WhSkuInventoryCommand> skuInvList, Locale locale, Long userId, String logId, Long ouId) {
        List<WhSkuInventoryCommand> readList = excelImportResult.getListBean();
        RootExcelException rootExcelException = new RootExcelException("", excelImportResult.getSheetName(), excelImportResult.getTitleSize());

        Map<Long, String> invMap = this.getInvStatusMap();
        Map<String, Long> locationMap = new HashMap<String, Long>();// 【locationCode-location序列】集合
        Map<String, Long> skuMap = new HashMap<String, Long>();// 【skuCode-skuId】集合

        List<WarehouseDefectTypeCommand> defectTypes = this.warehouseDefectTypeManager.findWarehouseDefectTypeByOuId(ouId, Constants.LIFECYCLE_START);

        Map<String, Long> deTypeMap = new HashMap<String, Long>();// 残次类型
        if (defectTypes != null && defectTypes.size() > 0) {
            for (WarehouseDefectTypeCommand whDefectType : defectTypes) {
                deTypeMap.put(whDefectType.getCode(), whDefectType.getId());
            }
        }

        for (int index = 0; index < readList.size(); index++) {
            int rowNum = index + Constants.IMPORT_SKUINV_INIT_TITLE_INDEX + 1;
            WhSkuInventoryCommand lineCommand = readList.get(index);
            if (StringUtils.isEmpty(lineCommand.getSkuCode())) {
                rootExcelException.getExcelExceptions().add(new ExcelException("商品编码不能为空", null, rowNum, null));
            } else {
                // 数量校验
                if (lineCommand.getOnHandQty() == null) {
                    rootExcelException.getExcelExceptions().add(new ExcelException("在库库存不能为空", null, rowNum, null));
                } else {
                    if (lineCommand.getOnHandQty() <= 0) {
                        rootExcelException.getExcelExceptions().add(new ExcelException("在库库存必须大于0", null, rowNum, null));
                    }
                }
                // 库位校验逻辑
                if (locationMap.containsKey(lineCommand.getLocationCode())) {
                    Long locationId = locationMap.get(lineCommand.getLocationCode());
                    lineCommand.setLocationId(locationId);
                } else {
                    Location l = this.locationManager.findLocationByCode(lineCommand.getLocationCode(), ouId);
                    if (l == null) {
                        rootExcelException.getExcelExceptions().add(new ExcelException("库位编码找不到对应的库位", null, rowNum, null));
                    } else {
                        lineCommand.setLocationId(l.getId());
                        locationMap.put(lineCommand.getLocationCode(), l.getId());
                    }

                }
                // 容器校验
                if (StringUtils.hasText(lineCommand.getOuterContainerCode())) {
                    ContainerCommand oc = this.containerManager.findContainerByCode(lineCommand.getOuterContainerCode(), ouId);
                    if (oc == null) {
                        rootExcelException.getExcelExceptions().add(new ExcelException("外部容器编码找不到对应的容器", null, rowNum, null));
                    } else {
                        lineCommand.setOuterContainerId(oc.getId());
                    }
                    if (StringUtils.hasText(lineCommand.getInsideContainerCode())) {

                        ContainerCommand ic = this.containerManager.findContainerByCode(lineCommand.getInsideContainerCode(), ouId);
                        if (ic == null) {
                            rootExcelException.getExcelExceptions().add(new ExcelException("内部容器编码找不到对应的容器", null, rowNum, null));
                        } else {
                            lineCommand.setInsideContainerId(ic.getId());
                        }
                    } else {
                        rootExcelException.getExcelExceptions().add(new ExcelException("有外部容器时候，内部容器不能为空", null, rowNum, null));
                    }

                } else {
                    if (StringUtils.hasText(lineCommand.getInsideContainerCode())) {

                        ContainerCommand ic = this.containerManager.findContainerByCode(lineCommand.getInsideContainerCode(), ouId);
                        if (ic == null) {
                            rootExcelException.getExcelExceptions().add(new ExcelException("内部容器编码找不到对应的容器", null, rowNum, null));
                        } else {
                            lineCommand.setInsideContainerId(ic.getId());
                        }
                    }
                }
                // 店铺客户校验
                if (StringUtils.isEmpty(lineCommand.getCustomerCode())) {
                    rootExcelException.getExcelExceptions().add(new ExcelException("入库单客户编码不能为空", null, rowNum, null));
                }
                Customer customer = this.customerManager.findCustomerbyCode(lineCommand.getCustomerCode());
                if (customer == null) {
                    rootExcelException.getExcelExceptions().add(new ExcelException("客户编码有误，找不到对应的客户信息", null, rowNum, null));
                } else {

                    Long customerId = customer.getId();
                    lineCommand.setCustomerId(customerId);

                    if (StringUtils.isEmpty(lineCommand.getStoreCode())) {
                        rootExcelException.getExcelExceptions().add(new ExcelException("店铺编码不能为空", null, rowNum, null));
                    } else {
                        Store store = this.storeManager.findStoreByCode(lineCommand.getStoreCode());
                        if (store == null) {
                            rootExcelException.getExcelExceptions().add(new ExcelException("店铺编码找不到对应的店铺信息", null, rowNum, null));
                        } else {
                            if (!Constants.LIFECYCLE_START.equals(store.getLifecycle())) {
                                rootExcelException.getExcelExceptions().add(new ExcelException("店铺无效", null, rowNum, null));
                            }
                            if (!customer.getId().equals(store.getCustomerId())) {
                                rootExcelException.getExcelExceptions().add(new ExcelException("客户-店铺不对应", null, rowNum, null));
                            }
                            Long storeId = store.getId();
                            lineCommand.setStoreId(storeId);
                            // Boolean customerStoreUserFlag =
                            // this.storeManager.checkCustomerStoreUser(customerId, storeId,
                            // userId);
                            // if (!customerStoreUserFlag) {
                            // rootExcelException.getExcelExceptions().add(new
                            // ExcelException("用户不具有此客户-店铺权限", null, rowNum, null));
                            // }

                        }
                    }
                }
                // 商品校验逻辑
                if (skuMap.containsKey(lineCommand.getSkuCode())) {
                    lineCommand.setSkuId(skuMap.get(lineCommand.getSkuCode()));
                } else {
                    // WhSku sku = this.whSkuManager.getSkuBySkuCodeOuId(lineCommand.getSkuCode(),
                    // ouId);
                    WhSku sku = this.whSkuManager.getSkuByExtCodeOuId(lineCommand.getSkuCode(), ouId);
                    if (sku == null) {
                        rootExcelException.getExcelExceptions().add(new ExcelException("商品编码找不到对应的商品", null, rowNum, null));
                    } else {
                        lineCommand.setSkuId(sku.getId());
                        skuMap.put(lineCommand.getSkuCode(), lineCommand.getSkuId());
                    }
                }
                if (lineCommand.getInvStatus() == null) {
                    rootExcelException.getExcelExceptions().add(new ExcelException("库存状态不能为空", null, rowNum, null));
                } else {
                    if (!invMap.containsKey(lineCommand.getInvStatus())) {
                        rootExcelException.getExcelExceptions().add(new ExcelException("库存状态编码错误", null, rowNum, null));
                    }
                }
                if (StringUtils.hasText(lineCommand.getInvType())) {
                    SysDictionary dic = this.sysDictionaryManager.getGroupbyGroupValueAndDicValue(Constants.INVENTORY_TYPE, lineCommand.getInvType());
                    if (dic == null) {
                        rootExcelException.getExcelExceptions().add(new ExcelException("库存类型编码错误", null, rowNum, null));
                    }
                }
                if (StringUtils.hasText(lineCommand.getInvAttr1())) {
                    SysDictionary dic = this.sysDictionaryManager.getGroupbyGroupValueAndDicValue(Constants.INVENTORY_ATTR_1, lineCommand.getInvAttr1());
                    if (dic == null) {
                        rootExcelException.getExcelExceptions().add(new ExcelException("库存属性1编码错误", null, rowNum, null));
                    }
                }
                if (StringUtils.hasText(lineCommand.getInvAttr2())) {
                    SysDictionary dic = this.sysDictionaryManager.getGroupbyGroupValueAndDicValue(Constants.INVENTORY_ATTR_2, lineCommand.getInvAttr2());
                    if (dic == null) {
                        rootExcelException.getExcelExceptions().add(new ExcelException("库存属性2编码错误", null, rowNum, null));
                    }
                }
                if (StringUtils.hasText(lineCommand.getInvAttr3())) {
                    SysDictionary dic = this.sysDictionaryManager.getGroupbyGroupValueAndDicValue(Constants.INVENTORY_ATTR_3, lineCommand.getInvAttr3());
                    if (dic == null) {
                        rootExcelException.getExcelExceptions().add(new ExcelException("库存属性3编码错误", null, rowNum, null));
                    }
                }
                if (StringUtils.hasText(lineCommand.getInvAttr4())) {
                    SysDictionary dic = this.sysDictionaryManager.getGroupbyGroupValueAndDicValue(Constants.INVENTORY_ATTR_4, lineCommand.getInvAttr4());
                    if (dic == null) {
                        rootExcelException.getExcelExceptions().add(new ExcelException("库存属性4编码错误", null, rowNum, null));
                    }
                }
                if (StringUtils.hasText(lineCommand.getInvAttr5())) {
                    SysDictionary dic = this.sysDictionaryManager.getGroupbyGroupValueAndDicValue(Constants.INVENTORY_ATTR_5, lineCommand.getInvAttr5());
                    if (dic == null) {
                        rootExcelException.getExcelExceptions().add(new ExcelException("库存属性5编码错误", null, rowNum, null));
                    }
                }
                // sn与残次
                if (StringUtils.hasText(lineCommand.getSkuSn())) {
                    if (lineCommand.getOnHandQty() != 1) {
                        rootExcelException.getExcelExceptions().add(new ExcelException("序列号商品对应的在库库存必须为1", null, rowNum, null));
                    }
                }
                //@mender yimin.lu 2017/5/19 初始化时候，残次原因和类型来自于仓库
                if(StringUtils.hasText(lineCommand.getDefectType())){
                    if (!deTypeMap.containsKey(lineCommand.getDefectType())) {
                        rootExcelException.getExcelExceptions().add(new ExcelException("残次类型编码错误", null, rowNum, null));
                    } else {
                        lineCommand.setSnSource(Constants.SKU_SN_DEFECT_SOURCE_WH);
                        lineCommand.setDefectTypeId(deTypeMap.get(lineCommand.getDefectType()));
                        if (StringUtils.hasText(lineCommand.getDefectReasons())) {
                            WarehouseDefectReasons defectReasons = this.warehouseDefectReasonsManager.findWarehouseDefectReasonsByTypeIdAndReasonCode(lineCommand.getDefectTypeId(), lineCommand.getDefectReasons(), ouId);
                            if (defectReasons == null) {
                                rootExcelException.getExcelExceptions().add(new ExcelException("残次原因编码错误", null, rowNum, null));
                            } else {
                                lineCommand.setDefectReasonsId(defectReasons.getId());
                            }
                        } else {
                            rootExcelException.getExcelExceptions().add(new ExcelException("残次原因不能为空", null, rowNum, null));
                        }
                    }

                } else {
                    if (StringUtils.hasText(lineCommand.getDefectReasons())) {
                        rootExcelException.getExcelExceptions().add(new ExcelException("残次原因不为空时，残次类型不能为空", null, rowNum, null));
                    }
                }
                
            }

            skuInvList.add(lineCommand);
        }

        if (rootExcelException.isException()) {
            excelImportResult.setRootExcelException(rootExcelException);
            excelImportResult.setReadstatus(ExcelImportResult.READ_STATUS_FAILED);
        }
    }

    /**
     * 完整校验逻辑的库存初始化导入
     * 
     * @param excelImportResult
     * @param skuInvList
     * @param locale
     * @param userId
     * @param logId
     * @param ouId
     */
    private void validateSkuInv(ExcelImportResult excelImportResult, List<WhSkuInventory> skuInvList, Locale locale, Long userId, String logId, Long ouId) {
        List<WhSkuInventoryCommand> readList = excelImportResult.getListBean();
        RootExcelException rootExcelException = new RootExcelException("", excelImportResult.getSheetName(), excelImportResult.getTitleSize());

        Map<Long, String> invMap = this.getInvStatusMap();
        Map<String, String> locationMap = new HashMap<String, String>();// 【locationCode-location序列】集合
        Map<String, Long> skuMap = new HashMap<String, Long>();// 【skuCode-skuId】集合
        for (int index = 0; index < readList.size(); index++) {
            int rowNum = index + Constants.IMPORT_SKUINV_INIT_TITLE_INDEX + 1;
            WhSkuInventoryCommand lineCommand = readList.get(index);
            if (StringUtils.isEmpty(lineCommand.getSkuCode())) {
                rootExcelException.getExcelExceptions().add(new ExcelException("商品编码不能为空", null, rowNum, null));
            } else {
                // 库位校验逻辑
                if (locationMap.containsKey(lineCommand.getLocationCode())) {
                    String locationOpt = locationMap.get(lineCommand.getLocationCode());
                    Long locationId = Long.parseLong((locationOpt.split("%"))[0]);
                    lineCommand.setLocationId(locationId);
                } else {
                    Location l = this.locationManager.findLocationByCode(lineCommand.getLocationCode(), ouId);
                    if (l == null) {
                        rootExcelException.getExcelExceptions().add(new ExcelException("库位编码找不到对应的库位", null, rowNum, null));
                    } else {
                        String staticSku = "";
                        if (l.getIsStatic() != null && l.getIsStatic()) {
                            List<WhSkuLocation> skuLocationList = this.whSkulocationManager.findByLocationIdOuId(l.getId(), ouId);
                            if (skuLocationList == null || skuLocationList.size() == 0) {
                                rootExcelException.getExcelExceptions().add(new ExcelException("静态库位没有绑定商品，不允许放置商品", null, rowNum, null));
                            } else {
                                lineCommand.setLocationId(l.getId());
                                for (int i = 0; i < skuLocationList.size(); i++) {
                                    WhSkuLocation sl = skuLocationList.get(i);
                                    if (i == skuLocationList.size() - 1) {
                                        staticSku += sl.getSkuId();
                                    } else {
                                        staticSku += sl.getSkuId() + '|';
                                    }
                                }
                            }
                        } else {
                            lineCommand.setLocationId(l.getId());
                        }
                        if (lineCommand.getLocationId() != null) {
                            locationMap.put(lineCommand.getLocationCode(), RcvdWorkFlow.getLocationParams(l, staticSku));
                        }
                    }

                }
                // 容器校验
                if (StringUtils.hasText(lineCommand.getOuterContainerCode())) {
                    ContainerCommand oc = this.containerManager.findContainerByCode(lineCommand.getOuterContainerCode(), ouId);
                    if (oc == null) {
                        rootExcelException.getExcelExceptions().add(new ExcelException("外部容器编码找不到对应的容器", null, rowNum, null));
                    } else {
                        lineCommand.setOuterContainerId(oc.getId());
                    }
                    if (StringUtils.hasText(lineCommand.getInsideContainerCode())) {

                        ContainerCommand ic = this.containerManager.findContainerByCode(lineCommand.getInsideContainerCode(), ouId);
                        if (ic == null) {
                            rootExcelException.getExcelExceptions().add(new ExcelException("内部容器编码找不到对应的容器", null, rowNum, null));
                        } else {
                            lineCommand.setInsideContainerId(ic.getId());
                        }
                    } else {
                        rootExcelException.getExcelExceptions().add(new ExcelException("有外部容器时候，内部容器不能为空", null, rowNum, null));
                    }

                } else {
                    if (StringUtils.hasText(lineCommand.getInsideContainerCode())) {

                        ContainerCommand ic = this.containerManager.findContainerByCode(lineCommand.getInsideContainerCode(), ouId);
                        if (ic == null) {
                            rootExcelException.getExcelExceptions().add(new ExcelException("内部容器编码找不到对应的容器", null, rowNum, null));
                        } else {
                            lineCommand.setInsideContainerId(ic.getId());
                        }
                    }
                }
                // 店铺客户校验
                if (StringUtils.isEmpty(lineCommand.getCustomerCode())) {
                    rootExcelException.getExcelExceptions().add(new ExcelException("入库单客户编码不能为空", null, rowNum, null));
                }
                Customer customer = this.customerManager.findCustomerbyCode(lineCommand.getCustomerCode());
                if (customer == null) {
                    rootExcelException.getExcelExceptions().add(new ExcelException("客户编码有误，找不到对应的客户信息", null, rowNum, null));
                } else {

                    Long customerId = customer.getId();
                    lineCommand.setCustomerId(customerId);

                    if (StringUtils.isEmpty(lineCommand.getStoreCode())) {
                        rootExcelException.getExcelExceptions().add(new ExcelException("店铺编码不能为空", null, rowNum, null));
                    } else {
                        Store store = this.storeManager.findStoreByCode(lineCommand.getStoreCode());
                        if (store == null) {
                            rootExcelException.getExcelExceptions().add(new ExcelException("店铺编码找不到对应的店铺信息", null, rowNum, null));
                        } else {
                            if (!Constants.LIFECYCLE_START.equals(store.getLifecycle())) {
                                rootExcelException.getExcelExceptions().add(new ExcelException("店铺无效", null, rowNum, null));
                            }
                            if (!customer.getId().equals(store.getCustomerId())) {
                                rootExcelException.getExcelExceptions().add(new ExcelException("客户-店铺不对应", null, rowNum, null));
                            }
                            Long storeId = store.getId();
                            lineCommand.setStoreId(storeId);
                            // Boolean customerStoreUserFlag =
                            // this.storeManager.checkCustomerStoreUser(customerId, storeId,
                            // userId);
                            // if (!customerStoreUserFlag) {
                            // rootExcelException.getExcelExceptions().add(new
                            // ExcelException("用户不具有此客户-店铺权限", null, rowNum, null));
                            // }

                        }
                    }
                }
                // 商品校验逻辑
                if (skuMap.containsKey(lineCommand.getSkuCode())) {
                    lineCommand.setSkuId(skuMap.get(lineCommand.getSkuCode()));
                } else {
                    WhSku sku = this.whSkuManager.getSkuBySkuCodeOuId(lineCommand.getSkuCode(), ouId);
                    if (sku == null) {
                        rootExcelException.getExcelExceptions().add(new ExcelException("商品编码找不到对应的商品", null, rowNum, null));
                    } else {
                        lineCommand.setSkuId(sku.getId());
                        skuMap.put(lineCommand.getSkuCode(), lineCommand.getSkuId());
                    }
                }
                // 如果有商品
                if (lineCommand.getSkuId() != null) {
                    SkuRedisCommand redisSku = this.skuRedisManager.findSkuMasterBySkuId(lineCommand.getSkuId(), ouId, logId);
                    String opt = RcvdWorkFlow.getOptMapStr(redisSku);
                    if (redisSku.getSkuMgmt() != null) {
                        if (redisSku.getSkuMgmt().getIsMixAllowed() != null && redisSku.getSkuMgmt().getIsMixAllowed()) {
                            opt += '1';
                        } else {
                            opt += '0';
                        }
                    } else {
                        opt += '0';
                    }

                    if (lineCommand.getInvStatus() == null) {
                        rootExcelException.getExcelExceptions().add(new ExcelException("库存状态不能为空", null, rowNum, null));
                    } else {
                        if (!invMap.containsKey(lineCommand.getInvStatus())) {
                            rootExcelException.getExcelExceptions().add(new ExcelException("库存状态编码错误", null, rowNum, null));
                        }
                    }
                    if (opt.charAt(3) == '1') {
                        if (StringUtils.isEmpty(lineCommand.getInvType())) {
                            rootExcelException.getExcelExceptions().add(new ExcelException("库存类型不能为空", null, rowNum, null));
                        } else {
                            SysDictionary dic = this.sysDictionaryManager.getGroupbyGroupValueAndDicValue(Constants.INVENTORY_TYPE, lineCommand.getInvType());
                            if (dic == null) {
                                rootExcelException.getExcelExceptions().add(new ExcelException("库存类型编码错误", null, rowNum, null));
                            }
                        }
                    } else {
                        lineCommand.setInvType(null);
                    }
                    if (opt.charAt(4) == '1') {
                        if (StringUtils.isEmpty(lineCommand.getInvAttr1())) {
                            rootExcelException.getExcelExceptions().add(new ExcelException("库存属性1不能为空", null, rowNum, null));
                        } else {
                            SysDictionary dic = this.sysDictionaryManager.getGroupbyGroupValueAndDicValue(Constants.INVENTORY_ATTR_1, lineCommand.getInvAttr1());
                            if (dic == null) {
                                rootExcelException.getExcelExceptions().add(new ExcelException("库存属性1编码错误", null, rowNum, null));
                            }
                        }
                    } else {
                        lineCommand.setInvAttr1(null);
                    }
                    if (opt.charAt(5) == '1') {

                        if (StringUtils.isEmpty(lineCommand.getInvAttr2())) {
                            rootExcelException.getExcelExceptions().add(new ExcelException("库存属性2不能为空", null, rowNum, null));
                        } else {

                            SysDictionary dic = this.sysDictionaryManager.getGroupbyGroupValueAndDicValue(Constants.INVENTORY_ATTR_2, lineCommand.getInvAttr2());
                            if (dic == null) {
                                rootExcelException.getExcelExceptions().add(new ExcelException("库存属性2编码错误", null, rowNum, null));
                            }
                        }
                    } else {
                        lineCommand.setInvAttr2(null);
                    }
                    if (opt.charAt(6) == '1') {
                        if (StringUtils.isEmpty(lineCommand.getInvAttr3())) {
                            rootExcelException.getExcelExceptions().add(new ExcelException("库存属性3不能为空", null, rowNum, null));
                        } else {
                            SysDictionary dic = this.sysDictionaryManager.getGroupbyGroupValueAndDicValue(Constants.INVENTORY_ATTR_3, lineCommand.getInvAttr3());
                            if (dic == null) {
                                rootExcelException.getExcelExceptions().add(new ExcelException("库存属性3编码错误", null, rowNum, null));
                            }
                        }
                    } else {
                        lineCommand.setInvAttr3(null);
                    }
                    if (opt.charAt(7) == '1') {
                        if (StringUtils.isEmpty(lineCommand.getInvAttr4())) {
                            rootExcelException.getExcelExceptions().add(new ExcelException("库存属性4不能为空", null, rowNum, null));
                        } else {
                            SysDictionary dic = this.sysDictionaryManager.getGroupbyGroupValueAndDicValue(Constants.INVENTORY_ATTR_4, lineCommand.getInvAttr4());
                            if (dic == null) {
                                rootExcelException.getExcelExceptions().add(new ExcelException("库存属性4编码错误", null, rowNum, null));
                            }
                        }
                    } else {
                        lineCommand.setInvAttr4(null);
                    }
                    if (opt.charAt(8) == '1') {
                        if (StringUtils.isEmpty(lineCommand.getInvAttr5())) {
                            rootExcelException.getExcelExceptions().add(new ExcelException("库存属性5不能为空", null, rowNum, null));
                        } else {
                            SysDictionary dic = this.sysDictionaryManager.getGroupbyGroupValueAndDicValue(Constants.INVENTORY_ATTR_5, lineCommand.getInvAttr5());
                            if (dic == null) {
                                rootExcelException.getExcelExceptions().add(new ExcelException("库存属性5编码错误", null, rowNum, null));
                            }
                        }
                    } else {
                        lineCommand.setInvAttr5(null);
                    }

                    if (opt.charAt(0) == '1') {
                        if (lineCommand.getMfgDate() == null || lineCommand.getExpDate() == null) {
                            rootExcelException.getExcelExceptions().add(new ExcelException("生产日期失效日期不能为空", null, rowNum, null));
                        }
                    }else{
                        lineCommand.setMfgDate(null);
                        lineCommand.setExpDate(null);
                    }
                    
                    if (opt.charAt(1) == '1') {
                        if (StringUtils.isEmpty(lineCommand.getBatchNumber())) {
                            rootExcelException.getExcelExceptions().add(new ExcelException("批次号不能为空", null, rowNum, null));
                        }
                    } else {
                        lineCommand.setBatchNumber(null);
                    }

                    if (opt.charAt(2) == '1') {
                        if (StringUtils.isEmpty(lineCommand.getCountryOfOrigin())) {
                            rootExcelException.getExcelExceptions().add(new ExcelException("原产地不能为空", null, rowNum, null));
                        }
                    } else {
                        lineCommand.setCountryOfOrigin(null);
                    }

                    // 库位混放校验
                    if (lineCommand.getLocationId() != null && lineCommand.getSkuId() != null) {
                        Long skuId = lineCommand.getSkuId();
                        String locationOpt = locationMap.get(lineCommand.getLocationCode());

                        // 库位是否静态库位
                        String[] params = locationOpt.split("%");
                        String[] locationConfig = params[1].split("\\|");
                        String isStatic = locationConfig[0];
                        boolean isAdd = true;
                        String uuid = this.getLocationMixSkuUuid(skuId, lineCommand);
                        if (StringUtils.isEmpty(uuid)) {
                            rootExcelException.getExcelExceptions().add(new ExcelException("商品属性有不支持字符集", null, rowNum, null));
                            if (isAdd) {
                                isAdd = false;
                            }
                        } 

                        if (isStatic == "1") {
                            String staticSku = params[2].replace("$", "");
                            if (!("|" + staticSku + "|").contains("|" + skuId + "|")) {
                                rootExcelException.getExcelExceptions().add(new ExcelException("静态库位未绑定此商品，不允许放置此商品", null, rowNum, null));
                                if (isAdd) {
                                    isAdd = false;
                                }
                            }
                        } else {

                            String locationSkuListStr = params[3];
                            String[] skuUuidArray = locationSkuListStr.split("\\$");

                            if (skuUuidArray.length > 0) {
                                String isMix = locationConfig[1];

                                if (isMix == "1") {
                                    String mixSkuCount = locationConfig[2];
                                    String mixSkuAttrCount = locationConfig[3];
                                    Set<String> locationSku = new HashSet<String>();
                                    Set<String> locationUuid = new HashSet<String>();

                                    for (int i = 0; i < skuUuidArray.length; i++) {
                                        String[] skuUuid = skuUuidArray[i].split("\\|");
                                        locationSku.add(skuUuid[0]);
                                        locationUuid.add(skuUuid[1]);
                                    }
                                    locationSku.add(skuId + "");
                                    if (opt.charAt(12) == '0') {
                                        if (locationSku.size() > 1) {
                                            rootExcelException.getExcelExceptions().add(new ExcelException("商品不支持混放", null, rowNum, null));
                                            if (isAdd) {
                                                isAdd = false;
                                            }
                                        }
                                    }
                                    if (locationSku.size() > Integer.parseInt(mixSkuCount)) {
                                        rootExcelException.getExcelExceptions().add(new ExcelException("超出了库位允许的商品混放数目", null, rowNum, null));
                                        if (isAdd) {
                                            isAdd = false;
                                        }
                                    }


                                    locationUuid.add(uuid);
                                    if (locationSku.size() > Integer.parseInt(mixSkuAttrCount)) {
                                        rootExcelException.getExcelExceptions().add(new ExcelException("超出了库位允许的商品属性混放数目", null, rowNum, null));
                                        if (isAdd) {
                                            isAdd = false;
                                        }
                                    }

                                } else {
                                    if (!locationSkuListStr.contains(skuId + "|" + uuid)) {
                                        rootExcelException.getExcelExceptions().add(new ExcelException("库位不允许混放", null, rowNum, null));
                                        if (isAdd) {
                                            isAdd = false;
                                        }
                                    }

                                }

                            }
                        }
                        if (isAdd) {
                            locationMap.put(lineCommand.getLocationCode(), RcvdWorkFlow.addLocationSku(locationOpt, skuId, uuid));
                        }
                    }
                }
            }
            WhSkuInventory skuInv = new WhSkuInventory();
            BeanUtils.copyProperties(lineCommand, skuInv);
            skuInvList.add(skuInv);
        }

        if (rootExcelException.isException()) {
            excelImportResult.setRootExcelException(rootExcelException);
            excelImportResult.setReadstatus(ExcelImportResult.READ_STATUS_FAILED);
        }
    }

    private String getLocationMixSkuUuid(Long skuId, WhSkuInventoryCommand lineCommand) {
        String uuid = null;
        WhSkuInventory uuidInv = new WhSkuInventory();
        uuidInv.setSkuId(skuId);
        uuidInv.setBatchNumber(lineCommand.getBatchNumber());
        uuidInv.setMfgDate(lineCommand.getMfgDate());
        uuidInv.setExpDate(lineCommand.getExpDate());
        uuidInv.setInvStatus(lineCommand.getInvStatus());
        uuidInv.setInvType(lineCommand.getInvType());
        uuidInv.setCountryOfOrigin(lineCommand.getCountryOfOrigin());
        uuidInv.setInvAttr1(lineCommand.getInvAttr1());
        uuidInv.setInvAttr2(lineCommand.getInvAttr2());
        uuidInv.setInvAttr3(lineCommand.getInvAttr3());
        uuidInv.setInvAttr4(lineCommand.getInvAttr4());
        uuidInv.setInvAttr5(lineCommand.getInvAttr4());
        try {
            uuid = SkuInventoryUuid.invUuid(uuidInv);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uuid;
    }

    private ExcelImportResult readSheetFromExcel(ExcelContext context, File importExcelFile, Locale locale, String sheetName, int index) throws Exception {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(importExcelFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new BusinessException("文件读取异常");
        }
        return context.readExcel(sheetName, index, inputStream, locale);
    }

    private Map<Long, String> getInvStatusMap() {
        List<InventoryStatus> invStatusList = this.inventoryStatusManager.findAllInventoryStatus();
        Map<Long, String> invStatusMap = new HashMap<Long, String>();
        if (invStatusList != null && invStatusList.size() > 0) {
            for (InventoryStatus invStatus : invStatusList) {
                invStatusMap.put(invStatus.getId(), invStatus.getName());
            }
        }
        return invStatusMap;
    }
}
