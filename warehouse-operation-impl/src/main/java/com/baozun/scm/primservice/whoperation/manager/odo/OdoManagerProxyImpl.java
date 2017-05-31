package com.baozun.scm.primservice.whoperation.manager.odo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.baozun.scm.baseservice.print.manager.PrintConditionManager;
import com.baozun.scm.baseservice.print.model.PrintCondition;
import com.baozun.scm.baseservice.sac.manager.CodeManager;
import com.baozun.scm.primservice.logistics.command.MailnoGetContentCommand;
import com.baozun.scm.primservice.logistics.command.SuggestTransContentCommand;
import com.baozun.scm.primservice.logistics.manager.TransServiceManager;
import com.baozun.scm.primservice.logistics.model.MailnoGetResponse;
import com.baozun.scm.primservice.logistics.model.SuggestTransResult;
import com.baozun.scm.primservice.logistics.model.SuggestTransResult.LpCodeList;
import com.baozun.scm.primservice.logistics.model.TransVasList;
import com.baozun.scm.primservice.logistics.model.VasTransResult;
import com.baozun.scm.primservice.logistics.model.VasTransResult.VasLine;
import com.baozun.scm.primservice.logistics.wms4.manager.MaTransportManager;
import com.baozun.scm.primservice.logistics.wms4.model.MaTransport;
import com.baozun.scm.primservice.whoperation.command.odo.OdoAddressCommand;
import com.baozun.scm.primservice.whoperation.command.odo.OdoCommand;
import com.baozun.scm.primservice.whoperation.command.odo.OdoGroupCommand;
import com.baozun.scm.primservice.whoperation.command.odo.OdoLineCommand;
import com.baozun.scm.primservice.whoperation.command.odo.OdoResultCommand;
import com.baozun.scm.primservice.whoperation.command.odo.OdoSearchCommand;
import com.baozun.scm.primservice.whoperation.command.odo.OdoTransportMgmtCommand;
import com.baozun.scm.primservice.whoperation.command.odo.WhOdoVasCommand;
import com.baozun.scm.primservice.whoperation.command.odo.wave.OdoGroupSearchCommand;
import com.baozun.scm.primservice.whoperation.command.odo.wave.OdoWaveGroupResultCommand;
import com.baozun.scm.primservice.whoperation.command.odo.wave.OdoWaveGroupSearchCommand;
import com.baozun.scm.primservice.whoperation.command.odo.wave.OdoWaveGroupSearchCondition;
import com.baozun.scm.primservice.whoperation.command.odo.wave.WaveCommand;
import com.baozun.scm.primservice.whoperation.command.sku.SkuRedisCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.UomCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WarehouseCommand;
import com.baozun.scm.primservice.whoperation.command.wave.WaveLineCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.OdoStatus;
import com.baozun.scm.primservice.whoperation.constant.WaveStatus;
import com.baozun.scm.primservice.whoperation.constant.WhUomType;
import com.baozun.scm.primservice.whoperation.excel.ExcelContext;
import com.baozun.scm.primservice.whoperation.excel.ExcelImport;
import com.baozun.scm.primservice.whoperation.excel.exception.ExcelException;
import com.baozun.scm.primservice.whoperation.excel.exception.RootExcelException;
import com.baozun.scm.primservice.whoperation.excel.result.ExcelImportResult;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.bi.OutPutStreamToServersManager;
import com.baozun.scm.primservice.whoperation.manager.odo.manager.OdoAddressManager;
import com.baozun.scm.primservice.whoperation.manager.odo.manager.OdoLineManager;
import com.baozun.scm.primservice.whoperation.manager.odo.manager.OdoManager;
import com.baozun.scm.primservice.whoperation.manager.odo.manager.OdoTransportMgmtManager;
import com.baozun.scm.primservice.whoperation.manager.odo.manager.OdoVasManager;
import com.baozun.scm.primservice.whoperation.manager.odo.wave.WhWaveLineManager;
import com.baozun.scm.primservice.whoperation.manager.odo.wave.WhWaveManager;
import com.baozun.scm.primservice.whoperation.manager.odo.wave.proxy.DistributionModeArithmeticManagerProxy;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.BiPoLineManager;
import com.baozun.scm.primservice.whoperation.manager.redis.SkuRedisManager;
import com.baozun.scm.primservice.whoperation.manager.system.SysDictionaryManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.CustomerManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.InventoryStatusManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.RegionManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.StoreManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.SupplierManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WarehouseManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhWorkManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.ma.DistributionTargetManager;
import com.baozun.scm.primservice.whoperation.model.BaseModel;
import com.baozun.scm.primservice.whoperation.model.ResponseMsg;
import com.baozun.scm.primservice.whoperation.model.bi.ImportExcel;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoAddress;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoInvoice;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoInvoiceLine;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoLine;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoTransportMgmt;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoTransportService;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoVas;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdodeliveryInfo;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWave;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWaveLine;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWaveMaster;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWaveMasterPrintCondition;
import com.baozun.scm.primservice.whoperation.model.sku.Sku;
import com.baozun.scm.primservice.whoperation.model.sku.SkuMgmt;
import com.baozun.scm.primservice.whoperation.model.system.SysDictionary;
import com.baozun.scm.primservice.whoperation.model.warehouse.Customer;
import com.baozun.scm.primservice.whoperation.model.warehouse.InventoryStatus;
import com.baozun.scm.primservice.whoperation.model.warehouse.Region;
import com.baozun.scm.primservice.whoperation.model.warehouse.Store;
import com.baozun.scm.primservice.whoperation.model.warehouse.Supplier;
import com.baozun.scm.primservice.whoperation.model.warehouse.Warehouse;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhWork;
import com.baozun.scm.primservice.whoperation.model.warehouse.ma.DistributionTarget;

@Service("odoManagerProxy")
public class OdoManagerProxyImpl implements OdoManagerProxy {

    protected static final Logger log = LoggerFactory.getLogger(OdoManagerProxy.class);
    @Autowired
    private OdoManager odoManager;
    @Autowired
    private WarehouseManager warehouseManager;
    @Autowired
    private CodeManager codeManager;
    @Autowired
    private OdoLineManager odoLineManager;
    @Autowired
    private OdoAddressManager odoAddressManager;
    @Autowired
    private OdoTransportMgmtManager odoTransportMgmtManager;
    @Autowired
    private OdoVasManager odoVasManager;
    @Autowired
    private InventoryStatusManager inventoryStatusManager;
    @Autowired
    private WhWaveManager waveManager;
    @Autowired
    private WhWaveLineManager waveLineManager;
    @Autowired
    private DistributionModeArithmeticManagerProxy distributionModeArithmeticManagerProxy;
    @Autowired
    private SkuRedisManager skuRedisManager;
    @Autowired
    private WhWorkManager whWorkManager;
    @Autowired
    private TransServiceManager transServiceManager;
    @Autowired
    private SupplierManager supplierManager;
    @Autowired
    private RegionManager regionManager;
    @Autowired
    private DistributionTargetManager distributionTargetManager;
    @Autowired
    private WhWaveManager whWaveManager;
    @Autowired
    private CustomerManager customerManager;
    @Autowired
    private StoreManager storeManager;
    @Autowired
    private SysDictionaryManager sysDictionaryManager;
    @Autowired
    private OutPutStreamToServersManager outPutStreamToServersManager;
    @Autowired
    private BiPoLineManager biPoLineManager;
    @Autowired
    private PrintConditionManager printConditionManager;
    @Autowired
    private MaTransportManager maTransportManager;
    @Autowired
    private WhOdoDeliveryInfoManager whOdoDeliveryInfoManager;

    @Override
    public Pagination<OdoResultCommand> findOdoListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params) {
        return this.odoManager.findListByQueryMapWithPageExt(page, sorts, params);
    }


    @Override
    public ResponseMsg createOdo(OdoGroupCommand odoGroup) {
        String logId = odoGroup.getLogId();
        log.info(this.getClass().getSimpleName() + ".createOdo ,logId:{},params:[odoGroupCommand:{}]", logId, odoGroup);
        ResponseMsg msg = new ResponseMsg();
        Long returnOdoId = null;
        try {
            Long ouId = odoGroup.getOuId();
            Long userId = odoGroup.getUserId();
            // 原始数据集合
            OdoCommand sourceOdo = odoGroup.getOdo();
            sourceOdo.setOuId(odoGroup.getOuId());
            // #TODO yimin.lu 设置状态逻辑 暂时放于此位置
            if (odoGroup.getIsWms() != null && odoGroup.getIsWms()) {
                sourceOdo.setOdoStatus(OdoStatus.CREATING);
            }
            List<OdoLineCommand> sourceOdoLineList = odoGroup.getOdoLineList();
            OdoTransportMgmtCommand sourceOdoTrans = odoGroup.getTransPortMgmt();
            WhOdoAddress sourceAddress = odoGroup.getWhOdoAddress();
            WhOdoInvoice sourceInvoice = odoGroup.getOdoInvoice();
            List<WhOdoInvoiceLine> sourceInvoiceLineList = odoGroup.getOdoInvoiceLineList();

            // @mender yimin.lu 2017/4/25 出库目标对象，如果有值，则需要写入配送对象地址
            sourceAddress = this.createOdoAddress(sourceAddress, sourceOdoTrans.getOutboundTargetType(), sourceOdoTrans.getOutboundTarget());

            if (sourceOdo.getId() != null) {
                this.editOdo(sourceOdo, sourceOdoTrans);
                returnOdoId = sourceOdo.getId();
            } else {
                returnOdoId = this.createOdo(sourceOdo, sourceOdoLineList, sourceOdoTrans, sourceAddress, sourceInvoice, sourceInvoiceLineList, ouId, userId);
            }
        } catch (BusinessException e) {
            msg.setResponseStatus(ResponseMsg.STATUS_ERROR);
            msg.setMsg(e.getErrorCode() + "");
            return msg;
        } catch (Exception ex) {
            log.error("" + ex);
            msg.setResponseStatus(ResponseMsg.STATUS_ERROR);
            msg.setMsg(ErrorCodes.PARAMS_ERROR + "");
            return msg;
        }
        msg.setMsg(returnOdoId + "");
        msg.setResponseStatus(ResponseMsg.STATUS_SUCCESS);
        return msg;
    }

    private WhOdoAddress createOdoAddress(WhOdoAddress sourceAddress, String outboundTargetType, String outboundTarget) {
        // @mender yimin.lu 2017/5/8 现有逻辑：地址为空的时候，才进行 出库目标类型+对象的地址赋值
        if (sourceAddress == null) {
            if (StringUtils.hasText(outboundTarget)) {
                if (Constants.AIMTYPE_1.equals(outboundTargetType)) {// 供应商
                    sourceAddress = new WhOdoAddress();
                    return this.createOdoAddressBySupplier(sourceAddress, outboundTarget);

                } else if (Constants.AIMTYPE_5.equals(outboundTargetType)) {
                    sourceAddress = new WhOdoAddress();
                    return this.createOdoAddressByWh(sourceAddress, outboundTarget);

                } else if (Constants.AIMTYPE_7.equals(outboundTargetType)) {
                    sourceAddress = new WhOdoAddress();
                    return this.createOdoAddressByDistributionTarget(sourceAddress, outboundTarget);
                }

            }
        }
        return sourceAddress;
    }


    private WhOdoAddress createOdoAddressByDistributionTarget(WhOdoAddress sourceAddress, String outboundTarget) {
        DistributionTarget search = new DistributionTarget();
        search.setCode(outboundTarget);
        List<DistributionTarget> targetList = this.distributionTargetManager.findDistributionTargetByParams(search);
        if (targetList == null || targetList.size() == 0) {
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        DistributionTarget target = targetList.get(0);

        sourceAddress.setDistributionTargetEmail(target.getEmail());
        sourceAddress.setDistributionTargetMobilePhone(target.getTelephone());
        sourceAddress.setDistributionTargetName(target.getName());
        sourceAddress.setDistributionTargetTelephone(target.getMobilePhone());
        sourceAddress.setDistributionTargetZip(target.getZip());

        // 地址
        sourceAddress.setDistributionTargetAddress(target.getAddress());

        // 国家
        if (target.getCountry() != null) {
            Region countryRegion = this.regionManager.findRegionById(target.getCountry());
            if (countryRegion != null) {
                sourceAddress.setDistributionTargetCountry(countryRegion.getRegionName());
            }
        }


        // 省
        if (target.getProvince() != null) {

            Region provinceRegion = this.regionManager.findRegionById(target.getProvince());
            if (provinceRegion != null) {
                sourceAddress.setDistributionTargetProvince(provinceRegion.getRegionName());
            }
        }

        // 市
        if (target.getCity() != null) {
            Region cityRegion = this.regionManager.findRegionById(target.getCity());
            if (cityRegion != null) {
                sourceAddress.setDistributionTargetCity(cityRegion.getRegionName());
            }
        }



        // 区
        if (target.getDistrict() != null) {
            Region districtRegion = this.regionManager.findRegionById(target.getDistrict());
            if (districtRegion != null) {
                sourceAddress.setDistributionTargetDistrict(districtRegion.getRegionName());
            }
        }


        // 乡镇
        if (target.getVillagesTowns() != null) {
            Region villageRegion = this.regionManager.findRegionById(target.getVillagesTowns());
            if (villageRegion != null) {
                sourceAddress.setDistributionTargetVillagesTowns(villageRegion.getRegionName());
            }
        }
        return sourceAddress;
    }


    private WhOdoAddress createOdoAddressByWh(WhOdoAddress sourceAddress, String outboundTarget) {
        Warehouse wh = this.warehouseManager.findWarehouseByCode(outboundTarget);

        sourceAddress.setDistributionTargetEmail(wh.getEmail());
        sourceAddress.setDistributionTargetMobilePhone(wh.getPicContact());
        sourceAddress.setDistributionTargetName(wh.getPic());
        sourceAddress.setDistributionTargetTelephone(wh.getPicContact());
        sourceAddress.setDistributionTargetZip(wh.getZipCode());

        // 地址
        sourceAddress.setDistributionTargetAddress(wh.getAddress());

        // 国家
        if (wh.getCountryId() != null) {
            Region countryRegion = this.regionManager.findRegionById(wh.getCountryId());
            if (countryRegion != null) {
                sourceAddress.setDistributionTargetCountry(countryRegion.getRegionName());
            }
        }


        // 省
        if (wh.getProvinceId() != null) {

            Region provinceRegion = this.regionManager.findRegionById(wh.getProvinceId());
            if (provinceRegion != null) {
                sourceAddress.setDistributionTargetProvince(provinceRegion.getRegionName());
            }
        }

        // 市
        if (wh.getCityId() != null) {
            Region cityRegion = this.regionManager.findRegionById(wh.getCityId());
            if (cityRegion != null) {
                sourceAddress.setDistributionTargetCity(cityRegion.getRegionName());
            }
        }



        // 区
        if (wh.getDistrictId() != null) {
            Region districtRegion = this.regionManager.findRegionById(wh.getDistrictId());
            if (districtRegion != null) {
                sourceAddress.setDistributionTargetDistrict(districtRegion.getRegionName());
            }
        }


        // 乡镇
        if (wh.getVillagesTownsId() != null) {
            Region villageRegion = this.regionManager.findRegionById(wh.getVillagesTownsId());
            if (villageRegion != null) {
                sourceAddress.setDistributionTargetVillagesTowns(villageRegion.getRegionName());
            }
        }
        return sourceAddress;
    }


    private WhOdoAddress createOdoAddressBySupplier(WhOdoAddress sourceAddress, String outboundTarget) {
        Supplier supplierSearch = new Supplier();
        supplierSearch.setCode(outboundTarget);
        List<Supplier> supplierList = this.supplierManager.findListByParam(supplierSearch);
        if (supplierList == null || supplierList.size() == 0) {
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        Supplier supplier = supplierList.get(0);

        sourceAddress.setDistributionTargetEmail(supplier.getEmail());
        sourceAddress.setDistributionTargetMobilePhone(supplier.getPicMobileTelephone());
        sourceAddress.setDistributionTargetName(supplier.getUser());
        sourceAddress.setDistributionTargetTelephone(supplier.getContact());
        sourceAddress.setDistributionTargetZip(supplier.getZipCode());

        // 地址
        sourceAddress.setDistributionTargetAddress(supplier.getAddress());

        // 国家
        if (supplier.getCountryId() != null) {
            Region countryRegion = this.regionManager.findRegionById(supplier.getCountryId());
            if (countryRegion != null) {
                sourceAddress.setDistributionTargetCountry(countryRegion.getRegionName());
            }
        }


        // 省
        if (supplier.getProvinceId() != null) {

            Region provinceRegion = this.regionManager.findRegionById(supplier.getProvinceId());
            if (provinceRegion != null) {
                sourceAddress.setDistributionTargetProvince(provinceRegion.getRegionName());
            }
        }

        // 市
        if (supplier.getCityId() != null) {
            Region cityRegion = this.regionManager.findRegionById(supplier.getCityId());
            if (cityRegion != null) {
                sourceAddress.setDistributionTargetCity(cityRegion.getRegionName());
            }
        }



        // 区
        if (supplier.getDistrictId() != null) {
            Region districtRegion = this.regionManager.findRegionById(supplier.getDistrictId());
            if (districtRegion != null) {
                sourceAddress.setDistributionTargetDistrict(districtRegion.getRegionName());
            }
        }


        // 乡镇
        if (supplier.getVillagesTownsId() != null) {
            Region villageRegion = this.regionManager.findRegionById(supplier.getVillagesTownsId());
            if (villageRegion != null) {
                sourceAddress.setDistributionTargetVillagesTowns(villageRegion.getRegionName());
            }
        }
        return sourceAddress;
    }


    /**
     * 修改出库单头信息
     * 
     * @param sourceOdo
     * @param sourceOdoTrans
     */
    private void editOdo(OdoCommand sourceOdo, OdoTransportMgmtCommand sourceOdoTrans) {
        try {

            Long ouId = sourceOdo.getOuId();
            WhOdo odo = this.odoManager.findOdoByIdOuId(sourceOdo.getId(), ouId);
            if (odo == null) {
                throw new BusinessException(ErrorCodes.PARAMS_ERROR);
            }
            if (!(OdoStatus.CREATING.equals(odo.getOdoStatus()) || OdoStatus.NEW.equals(odo.getOdoStatus()))) {
                throw new BusinessException(ErrorCodes.ODO_EDIT_ERROR);
            }
            Long odoId = odo.getId();
            // 配货模式计算
            boolean distributionModeCalcFlag = false;
            if ((null == odo.getIsLocked() || !odo.getIsLocked()) && Constants.ODO_CROSS_DOCKING_SYSMBOL_2.equals(odo.getCrossDockingSymbol())) {
                distributionModeCalcFlag = true;
            }

            odo.setEpistaticSystemsOrderType(sourceOdo.getEpistaticSystemsOrderType());
            odo.setOrderType(sourceOdo.getOrderType());
            odo.setEcOrderCode(sourceOdo.getEcOrderCode());
            odo.setOdoType(sourceOdo.getOdoType());
            WhOdoTransportMgmt trans = this.odoTransportMgmtManager.findTransportMgmtByOdoIdOuId(odoId, ouId);
            if (trans == null) {
                throw new BusinessException(ErrorCodes.PARAMS_ERROR);
            }
            trans.setPlanDeliverGoodsTime(DateUtils.parseDate(sourceOdoTrans.getPlanDeliverGoodsTimeStr(), Constants.DATE_PATTERN_YMD));
            odo.setPriorityLevel(sourceOdo.getPriorityLevel());
            odo.setIsLocked(sourceOdo.getIsLocked());
            odo.setIsWholeOrderOutbound(sourceOdo.getIsWholeOrderOutbound());
            odo.setCrossDockingSymbol(sourceOdo.getCrossDockingSymbol());
            odo.setOutboundCartonType(sourceOdo.getOutboundCartonType());
            trans.setDeliverGoodsTimeMode(sourceOdoTrans.getDeliverGoodsTimeMode());
            trans.setDeliverGoodsTime(DateUtils.parseDate(sourceOdoTrans.getDeliverGoodsTimeStr(), Constants.DATE_PATTERN_YMD));
            trans.setOutboundTargetType(sourceOdoTrans.getOutboundTargetType());
            trans.setOutboundTarget(sourceOdoTrans.getOutboundTarget());
            trans.setTransportServiceProvider(sourceOdoTrans.getTransportServiceProvider());
            trans.setModeOfTransport(sourceOdoTrans.getModeOfTransport());
            trans.setIsCod(sourceOdoTrans.getIsCod());
            trans.setCodAmt(sourceOdoTrans.getCodAmt());
            if (distributionModeCalcFlag) {
                if ((null == odo.getIsLocked() || !odo.getIsLocked()) && Constants.ODO_CROSS_DOCKING_SYSMBOL_2.equals(odo.getCrossDockingSymbol())) {
                    distributionModeCalcFlag = false;
                }
            } else {
                if ((null == odo.getIsLocked() || !odo.getIsLocked()) && Constants.ODO_CROSS_DOCKING_SYSMBOL_2.equals(odo.getCrossDockingSymbol())) {
                    distributionModeCalcFlag = true;
                }
            }
            // 保存数据
            this.odoManager.editOdo(odo, trans);
            // 计算配货模式
            if (distributionModeCalcFlag) {
                if (!OdoStatus.CREATING.equals(odo.getOdoStatus())) {

                    this.distributionModeArithmeticManagerProxy.AddToWave(odo.getCounterCode(), odoId);
                }
            }
        } catch (BusinessException ex) {
            throw ex;

        } catch (Exception e) {
            log.error(e + "");
        }
    }


    private Long createOdo(OdoCommand odo, List<OdoLineCommand> odoLineList, OdoTransportMgmtCommand transportMgmt, WhOdoAddress odoAddress, WhOdoInvoice invoice, List<WhOdoInvoiceLine> invoiceLineList, Long ouId, Long userId) {
        Long odoId = null;
        try {
            // 默认属性
            if (odo.getCurrentQty() == null) {
                odo.setCurrentQty(Constants.DEFAULT_DOUBLE);
            }
            if (odo.getActualQty() == null) {
                odo.setActualQty(Constants.DEFAULT_DOUBLE);
            }
            if (odo.getCancelQty() == null) {
                odo.setCancelQty(Constants.DEFAULT_DOUBLE);
            }
            if (null == odo.getIsWholeOrderOutbound()) {
                odo.setIsWholeOrderOutbound(true);
            }
            if (null == odo.getPriorityLevel()) {
                odo.setPriorityLevel(Constants.ODO_DEFAULT_PRIORITYLEVLE);
            }
            if (null == odo.getIncludeFragileCargo()) {
                odo.setIncludeFragileCargo(false);
            }
            if (null == odo.getIncludeHazardousCargo()) {
                odo.setIncludeHazardousCargo(false);
            }
            if (null == odo.getIsLocked()) {
                odo.setIsLocked(false);
            }
            odo.setCreatedId(userId);
            odo.setCreateTime(new Date());
            odo.setModifiedId(userId);
            odo.setLastModifyTime(new Date());
            if (null == odo.getOrderTime()) {
                odo.setOrderTime(new Date());
            }
            if (null == odo.getQty()) {
                odo.setQty(Constants.DEFAULT_DOUBLE);
            }
            if (null == odo.getSkuNumberOfPackages()) {
                odo.setSkuNumberOfPackages(Constants.DEFAULT_INTEGER);
            }
            if (null == odo.getAmt()) {
                odo.setAmt(Constants.DEFAULT_DOUBLE);
            }
            if (null == odo.getIsAllowMerge()) {
                odo.setIsAllowMerge(true);
            }
            if (StringUtils.isEmpty(odo.getOdoStatus())) {
                odo.setOdoStatus(OdoStatus.NEW);
            }
            // @mender yimin.lu 2017/4/11 领先、滞后出库单状态
            if (StringUtils.isEmpty(odo.getLagOdoStatus())) {
                odo.setLagOdoStatus(OdoStatus.NEW);
            }
            if (StringUtils.isEmpty(odo.getDataSource())) {
                odo.setDataSource(Constants.WMS4);
            }
            odo.setOuId(ouId);
            // 设置单号和外部对接编码
            String odoCode = codeManager.generateCode(Constants.WMS, Constants.WHODO_MODEL_URL, Constants.WMS_ODO_INNER, "ODO", null);
            odo.setOdoCode(odoCode);
            if (StringUtils.isEmpty(odo.getExtCode())) {
                String extCode = codeManager.generateCode(Constants.WMS, Constants.WHODO_MODEL_URL, Constants.WMS_ODO_EXT, null, null);
                odo.setExtCode(extCode);
            } else {
                WhOdo checkOdo = this.odoManager.findByExtCodeStoreIdOuId(odo.getExtCode(), odo.getStoreId(), ouId);
                if (checkOdo != null) {
                    throw new BusinessException(ErrorCodes.ODO_EXTCODE_ISEXIST);
                }
            }

            // 匹配配货模式
            transportMgmt.setOuId(ouId);
            if (transportMgmt.getPlanDeliverGoodsTime() == null) {
                transportMgmt.setPlanDeliverGoodsTime(new Date());
            }
            try {

                if (StringUtils.hasText(transportMgmt.getDeliverGoodsTimeStr())) {
                    transportMgmt.setDeliverGoodsTime(DateUtils.parseDate(transportMgmt.getDeliverGoodsTimeStr(), Constants.DATE_PATTERN_YMD));
                }
                if (StringUtils.hasText(transportMgmt.getPlanDeliverGoodsTimeStr())) {
                    transportMgmt.setPlanDeliverGoodsTime(DateUtils.parseDate(transportMgmt.getPlanDeliverGoodsTimeStr(), Constants.DATE_PATTERN_YMD));
                }
            } catch (Exception ex) {
                log.error("", ex);
                throw new BusinessException(ErrorCodes.PARAMS_ERROR);
            }
            // @mender yimin.lu 2017/04/26
            if (odoAddress != null) {
                odoAddress.setOuId(ouId);
            }
            odoId = this.odoManager.createOdo(odo, odoLineList, transportMgmt, odoAddress, invoice, invoiceLineList, ouId, userId);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception ex) {
            log.error("", ex);
            throw new BusinessException(ErrorCodes.PACKAGING_ERROR);
        }
        return odoId;
    }

    private WhOdoTransportMgmt copyTransportMgmtProperties(OdoTransportMgmtCommand transportMgmtCommand) throws ParseException {
        try {

            if (transportMgmtCommand == null) {
                return null;
            }
            WhOdoTransportMgmt transportMgmt = new WhOdoTransportMgmt();
            BeanUtils.copyProperties(transportMgmtCommand, transportMgmt);
            if (StringUtils.hasText(transportMgmtCommand.getDeliverGoodsTimeStr())) {
                transportMgmt.setDeliverGoodsTime(DateUtils.parseDate(transportMgmtCommand.getDeliverGoodsTimeStr(), Constants.DATE_PATTERN_YMD));
            }
            if (StringUtils.hasText(transportMgmtCommand.getPlanDeliverGoodsTimeStr())) {
                transportMgmt.setPlanDeliverGoodsTime(DateUtils.parseDate(transportMgmtCommand.getPlanDeliverGoodsTimeStr(), Constants.DATE_PATTERN_YMD));
            }
            return transportMgmt;
        } catch (Exception e) {
            log.error(e + "");
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
    }


    @Override
    public WhOdo findOdOById(Long id, Long ouId) {
        return this.odoManager.findOdoByIdOuId(id, ouId);
    }

    @Override
    public WhOdoTransportMgmt findTransportMgmtByOdoIdOuId(Long odoId, Long ouId) {
        return this.odoTransportMgmtManager.findTransportMgmtByOdoIdOuId(odoId, ouId);
    }

    @Override
    public WhOdoLine findOdoLineById(Long id, Long ouId) {
        return this.odoLineManager.findOdoLineById(id, ouId);
    }

    @Override
    public void saveOdoUnit(OdoLineCommand lineCommand) {
        Long ouId = lineCommand.getOuId();
        Long userId = lineCommand.getUserId();
        WhOdoLine line = new WhOdoLine();
        // @mender yimin.lu 2017/3/22 添加编辑逻辑
        if (lineCommand.getId() != null) {
            line = this.odoLineManager.findOdoLineById(lineCommand.getId(), ouId);
            if (null == line) {
                throw new BusinessException(ErrorCodes.PARAMS_ERROR);
            }
            line.setPlanQty(lineCommand.getQty());
            // @mender yimin.lu 2017/4/20 QTY
            line.setQty(lineCommand.getQty());
            line.setLinePrice(lineCommand.getLinePrice());
            line.setLineAmt(lineCommand.getLineAmt());
        } else {
            if (lineCommand.getLinenum() != null) {
                line.setLinenum(lineCommand.getLinenum());
            }
            if (lineCommand.getExtLinenum() != null) {
                line.setExtLinenum(lineCommand.getExtLinenum());
            }
            line.setSkuId(lineCommand.getSkuId());
            line.setOdoId(lineCommand.getOdoId());
            line.setOuId(ouId);
            line.setSkuBarCode(lineCommand.getSkuBarCode());
            line.setStoreId(lineCommand.getStoreId());
            line.setSkuName(lineCommand.getSkuName());
            line.setQty(lineCommand.getQty());
            // @mender yimin.lu 201 6/9/28
            line.setPlanQty(lineCommand.getQty());
            line.setLinePrice(lineCommand.getLinePrice());
            line.setLineAmt(lineCommand.getLineAmt());

            // 默认值设置
            line.setCurrentQty(Constants.DEFAULT_DOUBLE);
            line.setActualQty(Constants.DEFAULT_DOUBLE);
            line.setCancelQty(Constants.DEFAULT_DOUBLE);
            line.setAssignQty(Constants.DEFAULT_DOUBLE);
            line.setDiekingQty(Constants.DEFAULT_DOUBLE);
            line.setCreateTime(new Date());
            line.setCreatedId(userId);
            line.setLastModifyTime(new Date());
            line.setModifiedId(userId);
        }
        line.setOdoLineStatus(OdoStatus.ODOLINE_TOBECREATED);
        line.setIsCheck(lineCommand.getIsCheck());
        line.setFullLineOutbound(lineCommand.getFullLineOutbound());
        line.setPartOutboundStrategy(lineCommand.getPartOutboundStrategy());
        line.setOutboundCartonType(lineCommand.getOutboundCartonType());
        line.setMixingAttr(lineCommand.getMixingAttr());
        line.setInvStatus(lineCommand.getInvStatus());
        line.setInvType(lineCommand.getInvType());
        if (StringUtils.hasText(lineCommand.getMfgDateStr())) {

            try {
                line.setMfgDate(DateUtils.parseDate(lineCommand.getMfgDateStr(), Constants.DATE_PATTERN_YMD));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (StringUtils.hasText(lineCommand.getExpDateStr())) {

            try {
                line.setExpDate(DateUtils.parseDate(lineCommand.getExpDateStr(), Constants.DATE_PATTERN_YMD));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (StringUtils.hasText(lineCommand.getMinExpDateStr())) {

            try {
                line.setMinExpDate(DateUtils.parseDate(lineCommand.getMinExpDateStr(), Constants.DATE_PATTERN_YMD));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (StringUtils.hasText(lineCommand.getMaxExpDateStr())) {

            try {
                line.setMaxExpDate(DateUtils.parseDate(lineCommand.getMaxExpDateStr(), Constants.DATE_PATTERN_YMD));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        line.setInvAttr1(lineCommand.getInvAttr1());
        line.setInvAttr2(lineCommand.getInvAttr2());
        line.setInvAttr3(lineCommand.getInvAttr3());
        line.setInvAttr4(lineCommand.getInvAttr4());
        line.setInvAttr5(lineCommand.getInvAttr5());

        /**
         *  保存明细的增值服务：
         */
        List<WhOdoVasCommand> odoVasList = lineCommand.getOdoVasList();
        List<WhOdoVas> insertVasList = new ArrayList<WhOdoVas>();
        if (odoVasList != null && odoVasList.size() > 0) {
            for (WhOdoVasCommand vc : odoVasList) {
                WhOdoVas ov = new WhOdoVas();
                BeanUtils.copyProperties(vc, ov);
                ov.setOdoId(lineCommand.getOdoId());
                ov.setOuId(ouId);
                insertVasList.add(ov);
            }
        }


        this.odoManager.saveUnit(line, insertVasList);
    }

    @Override
    public Pagination<OdoLineCommand> findOdoLineListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params) {
        return this.odoLineManager.findOdoLineListByQueryMapWithPageExt(page, sorts, params);

    }

    @Override
    public WhOdoAddress findOdoAddressByOdoId(Long odoId, Long ouId) {
        return this.odoAddressManager.findOdoAddressByOdoId(odoId, ouId);
    }

    @Override
    public void saveDistributionUnit(OdoAddressCommand odoAddressCommand) {
        Long ouId = odoAddressCommand.getOuId();
        Long userId = odoAddressCommand.getUserId();
        Long odoId = odoAddressCommand.getOdoId();
        try {
            WhOdo odo = this.odoManager.findOdoByIdOuId(odoId, ouId);
            if (odo == null) {
                throw new BusinessException(ErrorCodes.NO_ODO_FOUND);
            }
            if (!(OdoStatus.CREATING.equals(odo.getOdoStatus()) || OdoStatus.NEW.equals(odo.getOdoStatus()))) {
                throw new BusinessException(ErrorCodes.ODO_EDIT_ERROR);
            }
            /*
             * WhOdoTransportMgmt transportMgmt =
             * this.odoTransportMgmtManager.findTransportMgmtByOdoIdOuId(odoId, ouId); if
             * (transportMgmt == null) { throw new BusinessException(ErrorCodes.NO_ODO_FOUND); }
             */
            boolean isAddToCachePool = false;

            /** 以下逻辑判断ODO状态 */
            long lineCount = this.odoLineManager.findOdoLineListCountByOdoId(odoId, ouId);
            if (lineCount > 0) {
                if (OdoStatus.CREATING.equals(odo.getOdoStatus())) {
                    odo.setOdoStatus(OdoStatus.NEW);
                    odo.setModifiedId(userId);
                    // 出库单变成新增的节点，需要将数据插入到订单池
                    isAddToCachePool = true;
                }
            }
            // transportMgmt.setOutboundTargetType(odoAddressCommand.getOutboundTargetType());

            WhOdoAddress odoAddress = this.odoAddressManager.findOdoAddressByOdoId(odoId, ouId);
            if (odoAddress == null) {
                odoAddress = new WhOdoAddress();
                odoAddress.setOdoId(odoId);
                odoAddress.setOuId(ouId);
            }
            odoAddress.setDistributionTargetName(odoAddressCommand.getDistributionTargetName());
            odoAddress.setDistributionTargetMobilePhone(odoAddressCommand.getDistributionTargetMobilePhone());
            if (StringUtils.hasText(odoAddressCommand.getDistributionTargetTelephoneNumber())) {
                String telephone = StringUtils.hasText(odoAddressCommand.getDistributionTargetTelephoneCode()) ? odoAddressCommand.getDistributionTargetTelephoneCode() + "-" : "";
                if (StringUtils.hasText(odoAddressCommand.getDistributionTargetTelephoneDivision())) {
                    telephone += odoAddressCommand.getDistributionTargetTelephoneNumber() + "-" + odoAddressCommand.getDistributionTargetTelephoneDivision();
                } else {
                    telephone += odoAddressCommand.getDistributionTargetTelephoneNumber();
                }
                odoAddress.setDistributionTargetTelephone(telephone);
            }

            odoAddress.setDistributionTargetCountry(odoAddressCommand.getDistributionTargetCountry());
            odoAddress.setDistributionTargetProvince(odoAddressCommand.getDistributionTargetProvince());
            odoAddress.setDistributionTargetCity(odoAddressCommand.getDistributionTargetCity());
            odoAddress.setDistributionTargetDistrict(odoAddressCommand.getDistributionTargetDistrict());
            odoAddress.setDistributionTargetVillagesTowns(odoAddressCommand.getDistributionTargetVillagesTowns());
            odoAddress.setDistributionTargetAddress(odoAddressCommand.getDistributionTargetAddress());
            odoAddress.setDistributionTargetEmail(odoAddressCommand.getDistributionTargetEmail());
            odoAddress.setDistributionTargetZip(odoAddressCommand.getDistributionTargetZip());

            this.odoManager.saveAddressUnit(odoAddress, odo);
            if (isAddToCachePool) {
                boolean isExists = this.distributionModeArithmeticManagerProxy.isExistsInOrderPool(odo.getCounterCode(), odoId);
                if (!isExists) {
                    this.distributionModeArithmeticManagerProxy.addToWhDistributionModeArithmeticPool(odo.getCounterCode(), odoId);
                }
            }
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception e) {
            log.error(e + "");
            throw new BusinessException(ErrorCodes.PACKAGING_ERROR);
        }

    }

    @Override
    public WhOdoAddress findOdoAddressById(Long id, Long ouId) {
        return this.odoAddressManager.findOdoAddressByIdOuId(id, ouId);
    }

    @Override
    public void saveConsigneeUnit(OdoAddressCommand odoAddressCommand) {
        Long ouId = odoAddressCommand.getOuId();
        Long userId = odoAddressCommand.getUserId();
        Long odoId = odoAddressCommand.getOdoId();
        try {
            WhOdo odo = this.odoManager.findOdoByIdOuId(odoId, ouId);
            if (odo == null) {
                throw new BusinessException(ErrorCodes.NO_ODO_FOUND);
            }
            if (!(OdoStatus.CREATING.equals(odo.getOdoStatus()) || OdoStatus.NEW.equals(odo.getOdoStatus()))) {
                throw new BusinessException(ErrorCodes.ODO_EDIT_ERROR);
            }
            /*
             * WhOdoTransportMgmt transportMgmt =
             * this.odoTransportMgmtManager.findTransportMgmtByOdoIdOuId(odoId, ouId); if
             * (transportMgmt == null) { throw new BusinessException(ErrorCodes.NO_ODO_FOUND); }
             */
            boolean isAddToCachePool = false;

            /** 以下逻辑判断ODO状态 */
            long lineCount = this.odoLineManager.findOdoLineListCountByOdoId(odoId, ouId);
            if (lineCount > 0) {
                if (OdoStatus.CREATING.equals(odo.getOdoStatus())) {
                    odo.setOdoStatus(OdoStatus.NEW);
                    odo.setModifiedId(userId);
                    // 出库单变成新增的节点，需要将数据插入到订单池
                    isAddToCachePool = true;
                }
            }
            // transportMgmt.setOutboundTargetType(odoAddressCommand.getOutboundTargetType());

            WhOdoAddress odoAddress = this.odoAddressManager.findOdoAddressByOdoId(odoId, ouId);
            if (odoAddress == null) {
                odoAddress = new WhOdoAddress();
                odoAddress.setOdoId(odoId);
                odoAddress.setOuId(ouId);
            }
            odoAddress.setConsigneeTargetName(odoAddressCommand.getConsigneeTargetName());
            odoAddress.setConsigneeTargetMobilePhone(odoAddressCommand.getConsigneeTargetMobilePhone());
            if (StringUtils.hasText(odoAddressCommand.getConsigneeTargetTelephoneNumber())) {
                String telephone = StringUtils.hasText(odoAddressCommand.getConsigneeTargetTelephoneCode()) ? odoAddressCommand.getConsigneeTargetTelephoneCode() + "-" : "";
                if (StringUtils.hasText(odoAddressCommand.getConsigneeTargetTelephoneDivision())) {
                    telephone += odoAddressCommand.getConsigneeTargetTelephoneNumber() + "-" + odoAddressCommand.getConsigneeTargetTelephoneDivision();
                } else {
                    telephone += odoAddressCommand.getConsigneeTargetTelephoneNumber();
                }
                odoAddress.setConsigneeTargetTelephone(telephone);
            }

            odoAddress.setConsigneeTargetCountry(odoAddressCommand.getConsigneeTargetCountry());
            odoAddress.setConsigneeTargetProvince(odoAddressCommand.getConsigneeTargetProvince());
            odoAddress.setConsigneeTargetCity(odoAddressCommand.getConsigneeTargetCity());
            odoAddress.setConsigneeTargetDistrict(odoAddressCommand.getConsigneeTargetDistrict());
            odoAddress.setConsigneeTargetVillagesTowns(odoAddressCommand.getConsigneeTargetVillagesTowns());
            odoAddress.setConsigneeTargetAddress(odoAddressCommand.getConsigneeTargetAddress());
            odoAddress.setConsigneeTargetEmail(odoAddressCommand.getConsigneeTargetEmail());
            odoAddress.setConsigneeTargetZip(odoAddressCommand.getConsigneeTargetZip());

            this.odoManager.saveAddressUnit(odoAddress, odo);
            if (isAddToCachePool) {
                boolean isExists = this.distributionModeArithmeticManagerProxy.isExistsInOrderPool(odo.getCounterCode(), odoId);
                if (!isExists) {
                    this.distributionModeArithmeticManagerProxy.addToWhDistributionModeArithmeticPool(odo.getCounterCode(), odoId);
                }
            }
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception e) {
            log.error(e + "");
            throw new BusinessException(ErrorCodes.PACKAGING_ERROR);
        }

    }

    @Override
    public List<WhOdoVas> findOdoVasByOdoIdOdoLineIdType(Long odoId, Long odoLineId, String vasType, Long ouId) {
        return this.odoVasManager.findOdoVasByOdoIdOdoLineIdType(odoId, odoLineId, vasType, ouId);
    }

    @Override
    public List<WhOdoVasCommand> findOdoOuVasCommandByOdoIdOdoLineIdType(Long odoId, Long odoLineId, Long ouId) {
        return this.odoVasManager.findOdoOuVasCommandByOdoIdOdoLineIdType(odoId, odoLineId, ouId);
    }

    @Override
    public void saveOdoOuVas(Long odoId, Long odoLineId, Long ouId, List<WhOdoVasCommand> odoVasList, String logId) {
        // 这边的逻辑如下
        // 1.将没有ID的作为插入的数据
        // 2.将有ID的进行校验判断是否修改，并做更新操作
        // 3.将数据库中有但是数据集合中没有的，做删除操作
        Map<Long, WhOdoVasCommand> vasMap = new HashMap<Long, WhOdoVasCommand>();
        List<WhOdoVas> insertVasList = new ArrayList<WhOdoVas>();
        List<WhOdoVas> updateVasList = new ArrayList<WhOdoVas>();
        List<WhOdoVas> delVasList = new ArrayList<WhOdoVas>();
        if (odoVasList != null && odoVasList.size() > 0) {
            for (WhOdoVasCommand vc : odoVasList) {
                if (vc.getId() == null) {
                    WhOdoVas ov = new WhOdoVas();
                    BeanUtils.copyProperties(vc, ov);
                    ov.setVasType(Constants.ODO_VAS_TYPE_WH);
                    ov.setOdoId(odoId);
                    ov.setOdoLineId(odoLineId);
                    ov.setOuId(ouId);
                    insertVasList.add(ov);
                } else {
                    vasMap.put(vc.getId(), vc);
                }
            }
        }
        List<WhOdoVas> oldVasList = this.odoVasManager.findOdoVasByOdoIdOdoLineIdType(odoId, odoLineId, Constants.ODO_VAS_TYPE_WH, ouId);

        if (oldVasList != null && oldVasList.size() > 0) {
            for (WhOdoVas odoVas : oldVasList) {
                if (vasMap.containsKey(odoVas.getId())) {
                    WhOdoVasCommand ovc = vasMap.get(odoVas.getId());
                    odoVas.setPrintTemplet(ovc.getPrintTemplet());
                    odoVas.setSkuBarCode(ovc.getSkuBarCode());
                    odoVas.setContent(ovc.getContent());
                    odoVas.setCartonNo(ovc.getCartonNo());
                    odoVas.setQty(ovc.getQty());
                    updateVasList.add(odoVas);
                } else {
                    delVasList.add(odoVas);
                }
            }
        }
        this.odoVasManager.saveOdoOuVas(insertVasList, updateVasList, delVasList);

    }

    @Override
    public List<WhOdoVasCommand> findOdoExpressVasCommandByOdoIdOdoLineId(Long odoId, Long odoLineId, Long ouId) {
        return this.odoVasManager.findOdoExpressVasCommandByOdoIdOdoLineId(odoId, odoLineId, ouId);
    }

    @Override
    public void saveOdoExpressVas(Long odoId, Long odoLineId, Long ouId, List<WhOdoVasCommand> odoVasList, String logId) {
        // 这边的逻辑如下
        // 1.将没有ID的作为插入的数据
        // 2.将有ID的进行校验判断是否修改，并做更新操作
        // 3.将数据库中有但是数据集合中没有的，做删除操作
        Map<Long, WhOdoVasCommand> vasMap = new HashMap<Long, WhOdoVasCommand>();
        List<WhOdoVas> insertVasList = new ArrayList<WhOdoVas>();
        List<WhOdoVas> updateVasList = new ArrayList<WhOdoVas>();
        List<WhOdoVas> delVasList = new ArrayList<WhOdoVas>();
        if (odoVasList != null && odoVasList.size() > 0) {
            for (WhOdoVasCommand vc : odoVasList) {
                if (vc.getId() == null) {
                    WhOdoVas ov = new WhOdoVas();
                    BeanUtils.copyProperties(vc, ov);
                    ov.setVasType(Constants.ODO_VAS_TYPE_EXPRESS);
                    ov.setOdoId(odoId);
                    ov.setOdoLineId(odoLineId);
                    ov.setOuId(ouId);
                    insertVasList.add(ov);
                } else {
                    vasMap.put(vc.getId(), vc);
                }
            }
        }
        List<WhOdoVas> oldVasList = this.odoVasManager.findOdoVasByOdoIdOdoLineIdType(odoId, odoLineId, Constants.ODO_VAS_TYPE_EXPRESS, ouId);

        if (oldVasList != null && oldVasList.size() > 0) {
            for (WhOdoVas odoVas : oldVasList) {
                if (vasMap.containsKey(odoVas.getId())) {
                    WhOdoVasCommand ovc = vasMap.get(odoVas.getId());
                    odoVas.setAmt(ovc.getAmt());
                    odoVas.setModeOfPayment(ovc.getModeOfPayment());
                    updateVasList.add(odoVas);
                } else {
                    delVasList.add(odoVas);
                }
            }
        }
        this.odoVasManager.saveOdoOuVas(insertVasList, updateVasList, delVasList);
    }


    /**
     * 出库单：整单取消/行取消 接口
     */
    @Override
    public ResponseMsg cancel(WhOdo odo, Long ouId, Boolean isOdoCancel, List<WhOdoLine> lineList, Long userId, String logId) {
        try{
            // 创建中的出库单删除操作
            if (OdoStatus.CREATING.equals(odo.getOdoStatus())) {
                try {
                    this.deleteOdoLine(lineList);
                } catch (Exception ex) {
                    throw new BusinessException(ErrorCodes.DELETE_DATA_ERROR);
                }
            } else {
                // @mender yimin.lu 2017/4/10 屏蔽部分取消接口
                if (!isOdoCancel) {
                    throw new BusinessException(ErrorCodes.ODO_CANCEL_NO_SUPPORT_LINE_ERROR);
                }
                // @mender yimin.lu 2017/5/5 当出库单大于某个状态时候，不允许取消
                Warehouse wh = this.warehouseManager.findWarehouseById(ouId);
                if (StringUtils.hasText(wh.getOdoNotCancelNode())) {
                    Long odoStatus = Constants.DEFAULT_LONG;
                    Long cancelNode = Constants.DEFAULT_LONG;
                    try {
                        odoStatus = Long.parseLong(odo.getOdoStatus());
                        cancelNode = Long.parseLong(wh.getOdoNotCancelNode());
                    } catch (Exception ex) {
                        throw new BusinessException(ErrorCodes.WAREHOUSE_CANCEL_NODE_ERROR);
                    }
                    if (odoStatus >= cancelNode) {
                        throw new BusinessException(ErrorCodes.ODO_CANCEL_ERROR);
                    }
                }
                if (isOdoCancel) {
                    this.cancelOdo(odo, ouId, logId);
                } else {
                    this.cancelLines(odo, lineList, ouId, userId, logId);
                }
            }
            ResponseMsg msg = new ResponseMsg();
            msg.setResponseStatus(ResponseMsg.STATUS_SUCCESS);
            msg.setMsg("SUCCESS");
            return msg;
        } catch (BusinessException ex) {
            log.error("", ex);
            ResponseMsg msg = new ResponseMsg();
            msg.setResponseStatus(ResponseMsg.STATUS_ERROR);
            msg.setMsg(ex.getErrorCode() + "");
            return msg;
        } catch (Exception e) {
            log.error("", e);
            ResponseMsg msg = new ResponseMsg();
            msg.setResponseStatus(ResponseMsg.STATUS_ERROR);
            msg.setMsg(ErrorCodes.SYSTEM_ERROR + "");
            return msg;
        }
    }

    private void deleteOdoLine(List<WhOdoLine> lineList) {
        this.odoLineManager.deleteLines(lineList);
    }


    private void cancelOdo(WhOdo odo, Long ouId, String logId) {
        this.odoManager.cancelOdo(odo, ouId, logId);
    }

    private void cancelLines(WhOdo odo, List<WhOdoLine> lineList, Long ouId, Long userId, String logId) {
        try {
            this.odoLineManager.cancelLines(odo, lineList, ouId, userId, logId);
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception e) {
            log.error("", e);
            throw new BusinessException(ErrorCodes.PACKAGING_ERROR);
        }
    }

    @Override
    public Pagination<OdoWaveGroupResultCommand> findOdoSummaryListForWaveByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params) {
        return this.odoManager.findOdoListForWaveByQueryMapWithPageExt(page, sorts, params);
    }

    @Override
    public List<OdoResultCommand> findOdoCommandListForWave(OdoSearchCommand command) {
        return this.odoManager.findOdoCommandListForWave(command);
    }

    @Override
    public OdoWaveGroupResultCommand findOdoSummaryForWave(OdoWaveGroupSearchCommand command) {
        return this.odoManager.findOdoSummaryForWave(command);
    }

    @Override
    public String createOdoWave(OdoGroupSearchCommand command) {
        /**
         * 校验阶段
         */
        /**
         * 校验出库单头和明细状态；以及是否处于别的波次中
         */
        String logId = command.getLogId();
        Long ouId = command.getOuId();
        Long userId = command.getUserId();
        Map<Long, WhOdo> odoMap = new HashMap<Long, WhOdo>();

        Map<Long, WhOdoTransportMgmt> transMap = new HashMap<Long, WhOdoTransportMgmt>();
        List<WhOdoLine> odolineList = new ArrayList<WhOdoLine>();
        Long waveMasterId = command.getWaveMasterId();// 波次主档信息
        WhWaveMaster master = this.odoManager.findWaveMasterByIdouId(waveMasterId, ouId);
        if (master == null) {
            throw new BusinessException(ErrorCodes.PARAM_IS_NULL);
        }
        // 全选
        if (command.getConditionList() != null && command.getConditionList().size() > 0) {
            OdoSearchCommand search = new OdoSearchCommand();
            BeanUtils.copyProperties(command, search);
            search.setLineFlag(true);
            if (StringUtils.hasText(command.getOdoStatus())) {
                search.setOdoStatus(Arrays.asList(command.getOdoStatus().split(",")));
            }
            if (StringUtils.hasText(command.getEpistaticSystemsOrderType())) {
                search.setEpistaticSystemsOrderType(Arrays.asList(command.getEpistaticSystemsOrderType().split(",")));
            }
            if (StringUtils.hasText(command.getCustomerId())) {
                search.setCustomerId(Arrays.asList(command.getCustomerId().split(",")));
            }
            if (StringUtils.hasText(command.getOutboundTargetType())) {
                search.setOutboundTargetType(Arrays.asList(command.getOutboundTargetType().split(",")));
            }
            if (StringUtils.hasText(command.getOdoType())) {
                search.setOdoType(Arrays.asList(command.getOdoType().split(",")));
            }
            if (StringUtils.hasText(command.getStoreId())) {
                search.setStoreId(Arrays.asList(command.getStoreId().split(",")));
            }
            if (StringUtils.hasText(command.getModeOfTransport())) {
                search.setModeOfTransport(Arrays.asList(command.getModeOfTransport().split(",")));
            }
            if (StringUtils.hasText(command.getTransportServiceProvider())) {
                String[] arr = command.getTransportServiceProvider().split(",");
                search.setTransportServiceProvider(Arrays.asList(arr));
            }
            if (StringUtils.hasText(command.getTransportServiceProviderType())) {
                search.setTransportServiceProviderType(Arrays.asList(command.getTransportServiceProviderType().split(",")));
            }
            if (StringUtils.hasText(command.getDistributeMode())) {
                search.setDistributeMode(Arrays.asList(command.getDistributeMode().split(",")));
            }
            if (StringUtils.hasText(command.getOutBoundCartonType())) {
                search.setOutBoundCartonType(Arrays.asList(command.getOutBoundCartonType().split(",")));
            }
            if (StringUtils.hasText(command.getLineOutboundCartonType())) {
                search.setLineOutboundCartonType(Arrays.asList(command.getLineOutboundCartonType().split(",")));
            }
            if (StringUtils.hasText(command.getInvType())) {
                search.setInvType(Arrays.asList(command.getInvType().split(",")));
            }
            if (StringUtils.hasText(command.getInvStatus())) {
                search.setInvStatus(Arrays.asList(command.getInvStatus().split(",")));
            }
            if (StringUtils.hasText(command.getInvAttr1())) {
                search.setInvAttr1(Arrays.asList(command.getInvAttr1().split(",")));
            }
            if (StringUtils.hasText(command.getInvAttr2())) {
                search.setInvAttr2(Arrays.asList(command.getInvAttr2().split(",")));
            }
            if (StringUtils.hasText(command.getInvAttr3())) {
                search.setInvAttr3(Arrays.asList(command.getInvAttr3().split(",")));
            }
            if (StringUtils.hasText(command.getInvAttr4())) {
                search.setInvAttr4(Arrays.asList(command.getInvAttr4().split(",")));
            }
            if (StringUtils.hasText(command.getInvAttr5())) {
                search.setInvAttr5(Arrays.asList(command.getInvAttr5().split(",")));
            }
            if (StringUtils.hasText(command.getWhVasType())) {
                search.setWhVasType(Arrays.asList(command.getWhVasType().split(",")));
            }
            if (StringUtils.hasText(command.getOrderType())) {
                search.setOrderType(Arrays.asList(command.getOrderType().split(",")));
            }
            if (StringUtils.hasText(command.getDeliverGoodsTimeMode())) {
                search.setDeliverGoodsTimeMode(Arrays.asList(command.getDeliverGoodsTimeMode().split(",")));
            }
            if (StringUtils.hasText(command.getOdoLineStatus())) {
                search.setOdoLineStatus(Arrays.asList(command.getOdoLineStatus().split(",")));
            }
            if (StringUtils.hasText(command.getLineOutboundCartonType())) {
                search.setLineOutboundCartonType(Arrays.asList(command.getLineOutboundCartonType().split(",")));
            }
            // 如果不选分组 默认按照客户分组
            // 如果没有选出库单状态，则默认为：新建和部分出库
            if (search.getOdoStatus() == null || search.getOdoStatus().size() == 0) {
                search.setOdoStatus(Arrays.asList(new String[] {OdoStatus.NEW, OdoStatus.PARTLY_FINISH}));
            }
            // 如果没有选出库单明细状态，则默认为新建和部分出库
            if (search.getOdoLineStatus() == null || search.getOdoLineStatus().size() == 0) {
                search.setOdoLineStatus(Arrays.asList(new String[] {OdoStatus.ODOLINE_NEW, OdoStatus.ODOLINE_OUTSTOCK}));
            }
            for (OdoWaveGroupSearchCondition gsc : command.getConditionList()) {
                search.setGroupCustomerId(gsc.getCustomerId());
                search.setGroupOdoStatus(gsc.getOdoStatus());
                search.setGroupStoreId(gsc.getStoreId());
                search.setGroupOdoType(gsc.getOdoType());
                search.setGroupDistributeMode(gsc.getDistributeMode());
                search.setGroupEpistaticSystemsOrderType(gsc.getEpistaticSystemsOrderType());
                search.setGroupTransportServiceProvider(gsc.getTransportServiceProvider());
                search.setIsEpistaticSystemsOrderType(gsc.getIsEpistaticSystemsOrderType());
                search.setIsDistributeMode(gsc.getIsDistributeMode());
                List<Long> liOdoList = this.odoManager.findOdoIdListForWave(search);
                if (liOdoList != null && liOdoList.size() > 0) {
                    for (Long liOdoId : liOdoList) {
                        WhOdo odo = this.odoManager.findOdoByIdOuId(liOdoId, ouId);
                        if (OdoStatus.NEW.equals(odo.getOdoStatus()) || OdoStatus.PARTLY_FINISH.equals(odo.getOdoStatus())) {
                            if (StringUtils.hasText(odo.getWaveCode())) {
                                throw new BusinessException(odo.getExtCode() + "已处于别的波次[波次编号：" + odo.getWaveCode() + "]中");
                            }

                            List<WhOdoLine> lineList = this.odoLineManager.findOdoLineListByOdoId(odo.getId(), odo.getOuId());
                            // 整单出库逻辑
                            if (odo.getIsWholeOrderOutbound()) {
                                // boolean isWholeOrderOutboundFlag =
                                // StringUtils.isEmpty(lineList.get(0).getWaveCode()) ? true :
                                // false;
                                for (WhOdoLine line : lineList) {
                                    odolineList.add(line);
                                }
                                // 部分出库逻辑
                            } else {
                                for (WhOdoLine line : lineList) {
                                    if (StringUtils.isEmpty(line.getWaveCode())) {
                                        odolineList.add(line);
                                    }
                                }
                            }
                            odoMap.put(odo.getId(), odo);
                            WhOdoTransportMgmt trans = this.odoTransportMgmtManager.findTransportMgmtByOdoIdOuId(odo.getId(), ouId);
                            transMap.put(odo.getId(), trans);
                        }
                    }
                }
            }
        }
        // 部分点选
        if (command.getOdoIdList() != null && command.getOdoIdList().size() > 0) {
            for (Long odoId : command.getOdoIdList()) {
                WhOdo odo = this.odoManager.findOdoByIdOuId(odoId, ouId);
                if (odo == null) {
                    throw new BusinessException(ErrorCodes.PARAMS_ERROR);
                }
                if (OdoStatus.NEW.equals(odo.getOdoStatus()) || OdoStatus.PARTLY_FINISH.equals(odo.getOdoStatus())) {

                    if (StringUtils.hasText(odo.getWaveCode())) {
                        throw new BusinessException(odo.getExtCode() + "已处于别的波次[波次编号：" + odo.getWaveCode() + "]中");
                    }

                    List<WhOdoLine> lineList = this.odoLineManager.findOdoLineListByOdoId(odo.getId(), odo.getOuId());
                    // 整单出库逻辑
                    if (odo.getIsWholeOrderOutbound()) {
                        // boolean isWholeOrderOutboundFlag =
                        // StringUtils.isEmpty(lineList.get(0).getWaveCode()) ? true : false;
                        for (WhOdoLine line : lineList) {
                            odolineList.add(line);
                        }
                        // 部分出库逻辑
                    } else {
                        for (WhOdoLine line : lineList) {
                            if (StringUtils.isEmpty(line.getWaveCode())) {
                                odolineList.add(line);
                            }
                        }
                    }
                    odoMap.put(odoId, odo);
                    WhOdoTransportMgmt trans = this.odoTransportMgmtManager.findTransportMgmtByOdoIdOuId(odo.getId(), ouId);
                    transMap.put(odo.getId(), trans);
                }
            }
        }


        /**
         * 校验波次主档信息
         */
        int odoCount = odoMap.size();// 波次出库单总单数
        int odolineCount = odolineList.size();// 波次明细数

        Map<Long, Double> skuMap = new HashMap<Long, Double>();// 商品种类数
        double totalAmt = Constants.DEFAULT_DOUBLE;// 总金额
        double totalSkuQty = Constants.DEFAULT_DOUBLE;// 商品总件数
        for (WhOdoLine line : odolineList) {
            totalAmt += line.getPlanQty() * line.getLinePrice();
            if (skuMap.containsKey(line.getSkuId())) {
                skuMap.put(line.getSkuId(), skuMap.get(line.getSkuId()) + line.getPlanQty());
            } else {

                skuMap.put(line.getSkuId(), line.getPlanQty());
            }
            totalSkuQty += line.getPlanQty();
        }
        // 商品种类数
        int skuCategoryQty = skuMap.size();
        // 总体积
        double totalVolume = Constants.DEFAULT_DOUBLE;
        // 总重量
        double totalWeight = Constants.DEFAULT_DOUBLE;

        // 体积单位转换率
        Map<String, Double> lenUomConversionRate = new HashMap<String, Double>();
        List<UomCommand> lenUomCmds = this.odoManager.findUomByGroupCode(WhUomType.LENGTH_UOM, BaseModel.LIFECYCLE_NORMAL);
        for (UomCommand lenUom : lenUomCmds) {
            String uomCode = "";
            Double uomRate = 0.0;
            if (null != lenUom) {
                uomCode = lenUom.getUomCode();
                uomRate = lenUom.getConversionRate();
                lenUomConversionRate.put(uomCode, uomRate);
            }
        }
        // 重量单位转换率
        Map<String, Double> weightUomConversionRate = new HashMap<String, Double>();
        List<UomCommand> weightUomCmds = this.odoManager.findUomByGroupCode(WhUomType.WEIGHT_UOM, BaseModel.LIFECYCLE_NORMAL);
        for (UomCommand lenUom : weightUomCmds) {
            String uomCode = "";
            Double uomRate = 0.0;
            if (null != lenUom) {
                uomCode = lenUom.getUomCode();
                uomRate = lenUom.getConversionRate();
                weightUomConversionRate.put(uomCode, uomRate);
            }
        }

        Iterator<Entry<Long, Double>> skuIt = skuMap.entrySet().iterator();
        while (skuIt.hasNext()) {
            Entry<Long, Double> entry = skuIt.next();
            Sku sku = this.odoManager.findSkuByIdToShard(entry.getKey(), ouId);
            if (sku != null) {
                totalVolume += sku.getVolume() * entry.getValue() * (StringUtils.isEmpty(sku.getVolumeUom()) ? 1 : lenUomConversionRate.get(sku.getVolumeUom()));
                totalWeight = sku.getWeight() * entry.getValue() * (StringUtils.isEmpty(sku.getWeightUom()) ? 1 : weightUomConversionRate.get(sku.getWeightUom()));
            }
        }

        if (master.getMinOdoQty() != null) {
            if (master.getMinOdoQty() > odoCount) {
                throw new BusinessException("出库单数目不满足波次最小出库单数");
            }
        }
        if (master.getMaxOdoQty() != null) {
            if (master.getMaxOdoQty() < odoCount) {
                throw new BusinessException("出库单数不满足波次最大出库单数");
            }
        }
        if (master.getMaxOdoLineQty() != null) {
            if (master.getMaxOdoLineQty() < odolineCount) {
                throw new BusinessException("出库单明细数不满足波次最大出库明细数");
            }
        }
        if (master.getMaxSkuQty() != null) {
            if (master.getMaxSkuQty() < totalSkuQty) {
                throw new BusinessException("商品数不满足波次最大出库商品数");
            }
        }
        if (master.getMaxSkuCategoryQty() != null) {
            if (master.getMaxSkuCategoryQty() < skuCategoryQty) {
                throw new BusinessException("商品种类数不满足波次最大出库商品种类数");
            }
        }
        if (master.getMaxVolume() != null) {
            if (master.getMaxVolume() < totalVolume) {
                throw new BusinessException("体积不满足波次最大出库体积");
            }
        }
        if (master.getMaxWeight() != null) {
            if (master.getMaxWeight() < totalWeight) {
                throw new BusinessException("重量不满足波次最大出库重量");
            }
        }
        /**
         * 创建波次头
         */
        WhWave wave = new WhWave();
        // a 生成波次编码，校验唯一性；补偿措施
        // #TODO 校验波次号
        String waveCode = "";
        try {
            waveCode = codeManager.generateCode(Constants.WMS, Constants.WHWAVE_MODEL_URL, "", "WAVE", null);
        } catch (Exception e) {
            throw new BusinessException(ErrorCodes.CODE_MANAGER_ERROR);
        }
        if (StringUtils.isEmpty(waveCode)) {
            throw new BusinessException(ErrorCodes.CODE_MANAGER_ERROR);
        }
        wave.setCode(waveCode);
        wave.setStatus(WaveStatus.WAVE_NEW);
        wave.setOuId(ouId);
        wave.setWaveMasterId(waveMasterId);
        wave.setTotalOdoQty(odoCount);
        wave.setTotalOdoLineQty(odolineCount);
        wave.setTotalAmount(totalAmt);
        wave.setTotalVolume(totalVolume);
        wave.setTotalWeight(totalWeight);
        wave.setTotalSkuQty(totalSkuQty);
        wave.setSkuCategoryQty(skuCategoryQty);
        wave.setIsRunWave(false);
        wave.setCreatedId(userId);
        wave.setCreateTime(new Date());
        wave.setModifiedId(userId);
        wave.setLastModifyTime(new Date());
        wave.setIsError(false);
        wave.setLifecycle(Constants.LIFECYCLE_START);

        List<WhWaveLine> waveLineList = new ArrayList<WhWaveLine>();
        for (WhOdoLine line : odolineList) {
            WhOdo odo = odoMap.get(line.getOdoId());
            WhOdoTransportMgmt trans = transMap.get(line.getOdoId());
            transMap.put(odo.getId(), trans);
            WhWaveLine waveLine = new WhWaveLine();
            waveLine.setOdoLineId(line.getId());
            waveLine.setOdoId(line.getOdoId());
            waveLine.setOdoCode(odo.getOdoCode());
            waveLine.setOdoPriorityLevel(odo.getPriorityLevel());
            waveLine.setOdoPlanDeliverGoodsTime(trans.getPlanDeliverGoodsTime());
            waveLine.setOdoOrderTime(odo.getOrderTime());
            waveLine.setIsStaticLocationAllocate(false);
            waveLine.setLinenum(line.getLinenum());
            waveLine.setStoreId(odo.getStoreId());
            waveLine.setExtLinenum(line.getExtLinenum());
            waveLine.setSkuId(line.getSkuId());
            waveLine.setSkuBarCode(line.getSkuBarCode());
            waveLine.setSkuName(line.getSkuName());
            waveLine.setQty(line.getPlanQty());
            waveLine.setAllocateQty(line.getAssignQty());
            waveLine.setIsWholeOrderOutbound(odo.getIsWholeOrderOutbound());
            waveLine.setFullLineOutbound(line.getFullLineOutbound());
            waveLine.setMfgDate(line.getMfgDate());
            waveLine.setExpDate(line.getExpDate());
            waveLine.setMinExpDate(line.getMinExpDate());
            waveLine.setMaxExpDate(line.getMaxExpDate());
            waveLine.setBatchNumber(line.getBatchNumber());
            waveLine.setCountryOfOrigin(line.getCountryOfOrigin());
            waveLine.setInvStatus(line.getInvStatus());
            waveLine.setInvType(line.getInvType());
            waveLine.setInvAttr1(line.getInvAttr1());
            waveLine.setInvAttr2(line.getInvAttr2());
            waveLine.setInvAttr3(line.getInvAttr3());
            waveLine.setInvAttr4(line.getInvAttr4());
            waveLine.setInvAttr5(line.getInvAttr5());
            waveLine.setOutboundCartonType(line.getOutboundCartonType());
            waveLine.setColor(line.getColor());
            waveLine.setStyle(line.getStyle());
            waveLine.setSize(line.getSize());
            waveLine.setOuId(ouId);
            waveLine.setCreateTime(new Date());
            waveLine.setCreatedId(userId);
            waveLine.setLastModifyTime(new Date());
            waveLine.setModifiedId(userId);
            waveLineList.add(waveLine);
        }
        this.odoManager.createOdoWave(wave, master.getWaveTemplateId(), waveLineList, odoMap, odolineList, userId, logId);
        return waveCode;
    }

    @Override
    public Pagination<WaveCommand> findWaveListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params) {
        return this.waveManager.findWaveListByQueryMapWithPageExt(page, sorts, params);
    }

    @Override
    public void deleteWave(WaveCommand waveCommand) {
        Long ouId = waveCommand.getOuId();
        Long userId = waveCommand.getUserId();
        Long waveId = waveCommand.getId();
        // 查询波次
        WhWave wave = this.waveManager.getWaveByIdAndOuId(waveId, ouId);
        List<WhWaveLine> waveLineList = this.waveLineManager.findWaveLineListByWaveId(waveId, ouId);
        // 查询波次关联的出库单
        List<WhOdo> odoList = this.odoManager.findOdoListByWaveCode(wave.getCode(), ouId);
        List<WhOdoLine> odoLineList = this.odoLineManager.findOdoLineListByWaveCode(wave.getCode(), ouId);

        this.waveManager.deleteWave(wave, waveLineList, odoList, odoLineList, userId);

    }

    @Override
    public void finishCreateOdo(OdoCommand odoCommand) {
        Long odoId = odoCommand.getId();
        Long ouId = odoCommand.getOuId();
        Long userId = odoCommand.getUserId();
        String logId = odoCommand.getLogId();
        List<WhOdoLine> lineList = this.odoLineManager.findOdoLineListByOdoId(odoId, ouId);
        List<WhOdoLine> saveLineList = new ArrayList<WhOdoLine>();// 用于保存的明细行
        if (lineList != null && lineList.size() > 0) {
            WhOdo odo = this.odoManager.findOdoByIdOuId(odoId, ouId);
            // 出库单统计数目
            double qty = Constants.DEFAULT_DOUBLE;
            int skuNumberOfPackages = Constants.DEFAULT_INTEGER;
            double amt = Constants.DEFAULT_DOUBLE;
            boolean isHazardous = odo.getIncludeHazardousCargo();
            boolean isFragile = odo.getIncludeFragileCargo();
            Set<Long> skuIdSet = new HashSet<Long>();
            for (WhOdoLine line : lineList) {
                if (OdoStatus.ODOLINE_CANCEL.equals(line.getOdoLineStatus())) {
                    continue;
                }
                if (OdoStatus.ODOLINE_TOBECREATED.equals(line.getOdoLineStatus())) {
                    SkuRedisCommand skuMaster = skuRedisManager.findSkuMasterBySkuId(line.getSkuId(), ouId, logId);
                    SkuMgmt skuMgmt = skuMaster.getSkuMgmt();
                    if (!isHazardous && skuMgmt.getIsHazardousCargo()) {
                        isHazardous = true;
                    }
                    if (!isFragile && skuMgmt.getIsFragileCargo()) {
                        isFragile = true;
                    }

                    line.setOdoLineStatus(OdoStatus.ODOLINE_NEW);
                    line.setModifiedId(userId);
                    saveLineList.add(line);
                }
                skuIdSet.add(line.getSkuId());
                amt += line.getLineAmt();
                qty += line.getQty();
            }
            odo.setQty(qty);
            odo.setAmt(amt);
            skuNumberOfPackages = skuIdSet.size();
            odo.setSkuNumberOfPackages(skuNumberOfPackages);
            odo.setIncludeFragileCargo(isFragile);
            odo.setIncludeHazardousCargo(isHazardous);
            if (OdoStatus.CREATING.equals(odo.getOdoStatus())) {
                odo.setOdoStatus(OdoStatus.NEW);
            }
            List<WhOdoVasCommand> vasList = this.odoVasManager.findOdoOuVasCommandByOdoIdOdoLineIdType(odo.getId(), null, ouId);
            // 设置允许合并与否
            // @mender yimin.lu 2017/2/10 锁定的出库单 不允许合并
            if (odo.getIsLocked() == null || odo.getIsLocked() == false) {
                if (vasList == null || vasList.size() == 0) {
                    odo.setIsAllowMerge(true);
                } else {
                    odo.setIsAllowMerge(false);
                }
            } else {
                odo.setIsAllowMerge(false);
            }
            // #TODO 现在出库单暂时不支持编辑
            String counterCode = this.distributionModeArithmeticManagerProxy.getCounterCodeForOdo(ouId, skuNumberOfPackages, qty, skuIdSet);
            odo.setCounterCode(counterCode);
            odo.setModifiedId(userId);
            boolean flag = false;
            try {
                this.odoManager.finishCreateOdo(odo, saveLineList);
                flag = true;
            } catch (Exception e) {
                throw e;
            }
            if (flag) {
                // @mender yimin.lu 锁定的出库单不参与计数器计算 2017/2/10
                // @mender yimin.lu 支持越库的出库单不参与计数器计算
                if ((odo.getIsLocked() == null || odo.getIsLocked() == false) && Constants.ODO_CROSS_DOCKING_SYSMBOL_2.equals(odo.getCrossDockingSymbol())) {

                    this.distributionModeArithmeticManagerProxy.addToWhDistributionModeArithmeticPool(counterCode, odoId);
                }
            }
        }

    }


    @Override
    public List<String> findExportExeclList(OdoSearchCommand odoSearchCommand) {
        return this.odoManager.findExportExeclList(odoSearchCommand);
    }

    @Override
    public WhWave getWaveByIdAndOuId(Long id, Long ouId) {
        return this.waveManager.findWaveByIdOuId(id, ouId);
    }

    @Override
    public Pagination<WaveLineCommand> findWaveLineListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params) {
        Pagination<WaveLineCommand> pages = this.waveLineManager.findWaveLineListByQueryMapWithPageExt(page, sorts, params);
        List<WaveLineCommand> waveLineList = pages.getItems();
        if (waveLineList != null && waveLineList.size() > 0) {
            // 库存状态
            List<InventoryStatus> invStatusList = this.inventoryStatusManager.findAllInventoryStatus();
            Map<Long, String> invStatusMap = new HashMap<Long, String>();
            for (InventoryStatus s : invStatusList) {
                invStatusMap.put(s.getId(), s.getName());
            }
            for (WaveLineCommand wave : waveLineList) {
                wave.setInvStatusName(invStatusMap.get(wave.getInvStatus()));
            }
        }
        pages.setItems(waveLineList);
        return pages;
    }

    @Override
    public void divFromWaveByOdo(WaveLineCommand waveLineCommand) {
        String logId = "";
        List<Long> odoIdList = waveLineCommand.getOdoIds();
        if (odoIdList == null || odoIdList.size() == 0) {
            throw new BusinessException(ErrorCodes.PARAM_IS_NULL);
        }
        Long ouId = waveLineCommand.getOuId();
        Long userId = waveLineCommand.getUserId();
        Long waveId = waveLineCommand.getWaveId();

        WhWave wave = this.waveManager.findWaveByIdOuId(waveId, ouId);

        List<WhWaveLine> waveLineAll = this.waveLineManager.findWaveLineListByWaveId(waveId, ouId);

        Map<Long, List<WhWaveLine>> odoIdWaveLineAllMap = new HashMap<Long, List<WhWaveLine>>();

        for (WhWaveLine waveLine : waveLineAll) {
            if (odoIdWaveLineAllMap.containsKey(waveLine.getOdoId())) {
                odoIdWaveLineAllMap.get(waveLine.getId()).add(waveLine);
            } else {
                List<WhWaveLine> waveLineList = new ArrayList<WhWaveLine>();
                waveLineList.add(waveLine);
                odoIdWaveLineAllMap.put(waveLine.getId(), waveLineList);
            }
        }



        Map<Long, List<WhWaveLine>> odoIdWaveLineMap = new HashMap<Long, List<WhWaveLine>>();

        WhWaveLine lineSearch = new WhWaveLine();
        lineSearch.setWaveId(waveId);
        lineSearch.setOuId(ouId);
        for (Long odoId : odoIdList) {
            lineSearch.setOdoId(odoId);
            List<WhWaveLine> waveLineList = this.waveLineManager.getWaveLineByParam(lineSearch);
            if (waveLineList == null || waveLineList.size() == 0) {
                continue;
            }
            odoIdWaveLineAllMap.remove(odoId);
            odoIdWaveLineMap.put(odoId, waveLineList);
        }

        if (odoIdWaveLineAllMap.size() == 0) {

            // return;
        }

        statisticsForWave(wave, odoIdWaveLineAllMap);

        this.waveManager.divFromWaveByOdo(wave, odoIdWaveLineMap, ouId, userId, logId);

    }


    private void statisticsForWave(WhWave wave, Map<Long, List<WhWaveLine>> odoIdWaveLineAllMap) {
        Long ouId = wave.getOuId();
        List<WhOdoLine> odolineList = new ArrayList<WhOdoLine>();
        Iterator<Entry<Long, List<WhWaveLine>>> it = odoIdWaveLineAllMap.entrySet().iterator();
        while (it.hasNext()) {
            Entry<Long, List<WhWaveLine>> entry = it.next();
            for (WhWaveLine line : entry.getValue()) {
                WhOdoLine odoLine = this.odoLineManager.findOdoLineById(line.getOdoLineId(), ouId);
                odolineList.add(odoLine);
            }
        }

        int odoCount = odoIdWaveLineAllMap.size();// 波次出库单总单数
        int odolineCount = odolineList.size();// 波次明细数

        Map<Long, Double> skuMap = new HashMap<Long, Double>();// 商品总件数
        double totalAmt = Constants.DEFAULT_DOUBLE;// 总金额
        double totalSkuQty = Constants.DEFAULT_DOUBLE;// 商品总件数
        for (WhOdoLine line : odolineList) {
            totalAmt += line.getPlanQty() * line.getLinePrice();
            if (skuMap.containsKey(line.getSkuId())) {
                skuMap.put(line.getSkuId(), skuMap.get(line.getSkuId()) + line.getPlanQty());
            } else {

                skuMap.put(line.getSkuId(), line.getPlanQty());
            }
            totalSkuQty += line.getPlanQty();
        }
        // 商品种类数
        int skuCategoryQty = skuMap.size();
        // 总体积
        double totalVolume = Constants.DEFAULT_DOUBLE;
        // 总重量
        double totalWeight = Constants.DEFAULT_DOUBLE;

        // 体积单位转换率
        Map<String, Double> lenUomConversionRate = new HashMap<String, Double>();
        List<UomCommand> lenUomCmds = this.odoManager.findUomByGroupCode(WhUomType.LENGTH_UOM, BaseModel.LIFECYCLE_NORMAL);
        for (UomCommand lenUom : lenUomCmds) {
            String uomCode = "";
            Double uomRate = 0.0;
            if (null != lenUom) {
                uomCode = lenUom.getUomCode();
                uomRate = lenUom.getConversionRate();
                lenUomConversionRate.put(uomCode, uomRate);
            }
        }
        // 重量单位转换率
        Map<String, Double> weightUomConversionRate = new HashMap<String, Double>();
        List<UomCommand> weightUomCmds = this.odoManager.findUomByGroupCode(WhUomType.WEIGHT_UOM, BaseModel.LIFECYCLE_NORMAL);
        for (UomCommand lenUom : weightUomCmds) {
            String uomCode = "";
            Double uomRate = 0.0;
            if (null != lenUom) {
                uomCode = lenUom.getUomCode();
                uomRate = lenUom.getConversionRate();
                weightUomConversionRate.put(uomCode, uomRate);
            }
        }

        Iterator<Entry<Long, Double>> skuIt = skuMap.entrySet().iterator();
        while (skuIt.hasNext()) {
            Entry<Long, Double> entry = skuIt.next();
            Sku sku = this.odoManager.findSkuByIdToShard(entry.getKey(), wave.getOuId());
            if (sku != null) {
                totalVolume += sku.getVolume() * entry.getValue() * (StringUtils.isEmpty(sku.getVolumeUom()) ? 1 : lenUomConversionRate.get(sku.getVolumeUom()));
                totalWeight = sku.getWeight() * entry.getValue() * (StringUtils.isEmpty(sku.getWeightUom()) ? 1 : weightUomConversionRate.get(sku.getWeightUom()));
            }
        }

        wave.setTotalOdoQty(odoCount);
        wave.setTotalOdoLineQty(odolineCount);
        wave.setTotalAmount(totalAmt);
        wave.setTotalVolume(totalVolume);
        wave.setTotalWeight(totalWeight);
        wave.setTotalSkuQty(totalSkuQty);
        wave.setSkuCategoryQty(skuCategoryQty);
    }

    @Override
    public void releaseWave(WaveCommand waveCommand) {
        Long waveId = waveCommand.getId();
        Long ouId = waveCommand.getOuId();
        Long userId = waveCommand.getUserId();
        WhWave wave = this.waveManager.findWaveByIdOuId(waveId, ouId);
        wave.setStatus(WaveStatus.WAVE_RELEASE);
        wave.setModifiedId(userId);

        List<WhWork> workList = this.whWorkManager.findWorkByWaveWithLock(wave.getCode(), ouId);

        this.waveManager.releaseWave(wave, workList, ouId, userId);



    }

    @Override
    public void cancelWave(WaveCommand waveCommand) {
        // yimin.lu 2016/12/16
        // 取消逻辑：
        // 新建状态下取消：-》取消
        // 完成状态下取消->取消处理中-》取消 @mender yimin.lu 2017/5/9 完成状态下，如果已有执行的工作，不允许取消
        Long waveId = waveCommand.getId();
        Long ouId = waveCommand.getOuId();
        Long userId = waveCommand.getUserId();
        WhWave wave = this.waveManager.findWaveByIdOuId(waveId, ouId);

        List<WhOdo> odoList = this.odoManager.findOdoListByWaveCode(wave.getCode(), ouId);

        // 新建状态下的波次
        if (WaveStatus.WAVE_NEW == wave.getStatus()) {
            wave.setModifiedId(userId);
            wave.setStatus(WaveStatus.WAVE_CANCEL);
            this.waveManager.cancelWaveForNew(wave, odoList, ouId, userId);
            return;
        }
        // 完成状态下的波次
        // 取消所有的补货任务@mender yimin.lu 2017/5/9 补货任务不取消
        // 取消所有的未执行的补货工作和拣货工作
        // 如果有正在执行的工作，波次状态置为取消处理中；否则为取消 @mender yimin.lu 2017/5/9 有正在执行的工作，不允许取消波次
        // 库存的回滚

        // @mender yimin.lu 2017/5/9 有正在执行的工作 不允许取消波次
        List<WhWork> unLockWorkList = this.whWorkManager.findWorkByWaveWithUnLock(wave.getCode(), ouId);
        if (unLockWorkList != null && unLockWorkList.size() > 0) {
            throw new BusinessException(ErrorCodes.WAVE_CANCEL_WORK_ERROR);
        }


        this.waveManager.cancelWaveWithWork(wave, odoList, ouId, userId);
        // this.waveManager.cancelWaveWithLazy(wave)
    }

    @Override
    public void runWave(WaveCommand waveCommand) {
        WhWave wave = this.waveManager.findWaveByIdOuId(waveCommand.getId(), waveCommand.getOuId());
        if (wave.getIsRunWave()) {
            throw new BusinessException("波次已在运行中");
        }
        if (WaveStatus.WAVE_NEW != wave.getStatus()) {
            throw new BusinessException("波次状态非新建");
        }
        wave.setModifiedId(waveCommand.getUserId());
        this.waveManager.startWave(wave);
    }

    @Override
    public ResponseMsg importWhOdo(String url, String fileName, Long userImportExcelId, Locale locale, Long ouId, Long userId, String logId) {


        File importExcelFile = new File(url, fileName);
        if (!importExcelFile.exists()) {
            throw new BusinessException(ErrorCodes.IMPORT_ERROR_FILE_NOT_EXISTS);
        }

        try {
            // 创建excel上下文实例,它的构成需要配置文件的路径
            ExcelContext context = new ExcelContext("excel-config.xml");


            Map<String, OdoGroupCommand> odoGroupCommandMap = new HashMap<String, OdoGroupCommand>();// 出库单

            // 出库单头信息
            ExcelImportResult odoExcelImportResult = this.readSheetFromExcel(context, importExcelFile, locale, Constants.IMPORT_WHODO_EXCEL_CONFIG_ID, Constants.IMPORT_WHODO_TITLE_INDEX);
            if (ExcelImportResult.READ_STATUS_SUCCESS == odoExcelImportResult.getReadstatus()) {
                this.validateOdo(odoExcelImportResult, odoGroupCommandMap, locale, userId, ouId);
            }
            if (odoGroupCommandMap.size() == 0) {
                Workbook workbook = odoExcelImportResult.getWorkbook();
                ExcelImport.exportImportErroeMsg(workbook, odoExcelImportResult.getRootExcelException());

                ImportExcel importExcel = new ImportExcel();
                importExcel.setImportType("WH_ODO");
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

            // 出库单明细信息
            ExcelImportResult linesExcelImportResult = this.readSheetFromExcel(context, importExcelFile, locale, Constants.IMPORT_WHODO_LINE_EXCEL_CONFIG_ID, Constants.IMPORT_WHODO_LINE_TITLE_INDEX);
            if (ExcelImportResult.READ_STATUS_SUCCESS == linesExcelImportResult.getReadstatus()) {
                this.validateLines(linesExcelImportResult, odoGroupCommandMap, locale, userId, ouId, logId);
            }

            // 出库单运输服务信息
            ExcelImportResult transExcelImportResult = this.readSheetFromExcel(context, importExcelFile, locale, Constants.IMPORT_WHODO_TRANSPORTMGMT_EXCEL_CONFIG_ID, Constants.IMPORT_WHODO_TRANSPORTMGMT_TITLE_INDEX);
            if (ExcelImportResult.READ_STATUS_SUCCESS == transExcelImportResult.getReadstatus()) {
                this.validateTrans(transExcelImportResult, odoGroupCommandMap, locale, userId, ouId, logId);
            }

            // 出库单地址信息
            ExcelImportResult addressExcelImportResult = this.readSheetFromExcel(context, importExcelFile, locale, Constants.IMPORT_WHODO_ADDRESS_EXCEL_CONFIG_ID, Constants.IMPORT_WHODO_ADDRESS_TITLE_INDEX);
            if (ExcelImportResult.READ_STATUS_SUCCESS == addressExcelImportResult.getReadstatus()) {
                this.validateAddress(addressExcelImportResult, odoGroupCommandMap, locale, userId, ouId, logId);
            }

            if (ExcelImportResult.READ_STATUS_FAILED == odoExcelImportResult.getReadstatus() || ExcelImportResult.READ_STATUS_FAILED == linesExcelImportResult.getReadstatus()
                    || ExcelImportResult.READ_STATUS_FAILED == transExcelImportResult.getReadstatus() || ExcelImportResult.READ_STATUS_FAILED == addressExcelImportResult.getReadstatus()) {
                Workbook workbook = odoExcelImportResult.getWorkbook();
                ExcelImport.exportImportErroeMsg(workbook, odoExcelImportResult.getRootExcelException());
                ExcelImport.exportImportErroeMsg(workbook, linesExcelImportResult.getRootExcelException());
                ExcelImport.exportImportErroeMsg(workbook, transExcelImportResult.getRootExcelException());
                ExcelImport.exportImportErroeMsg(workbook, addressExcelImportResult.getRootExcelException());

                ImportExcel importExcel = new ImportExcel();
                importExcel.setImportType("WH_ODO");
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
            // 如果校验成功，则进行导入
            List<OdoGroupCommand> groupList = new ArrayList<OdoGroupCommand>();

            Iterator<OdoGroupCommand> it = odoGroupCommandMap.values().iterator();
            while (it.hasNext()) {
                OdoGroupCommand entry = it.next();
                groupList.add(entry);
            }

            this.createOdo(groupList, ouId, userId);

        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception e) {
            log.error("", e);
            e.printStackTrace();
            throw new BusinessException(ErrorCodes.IMPORT_ERROR);
        }
        ResponseMsg msg = new ResponseMsg();
        msg.setResponseStatus(ResponseMsg.STATUS_SUCCESS);
        msg.setMsg("SUCCESS");
        return msg;
    }


    private void validateTrans(ExcelImportResult excelImportResult, Map<String, OdoGroupCommand> odoGroupCommandMap, Locale locale, Long userId, Long ouId, String logId) {
        List<OdoTransportMgmtCommand> transCommandList = excelImportResult.getListBean();
        RootExcelException rootExcelException = new RootExcelException("", excelImportResult.getSheetName(), excelImportResult.getTitleSize());
        for (int index = 0; index < transCommandList.size(); index++) {
            int rowNum = index + Constants.IMPORT_WHODO_TRANSPORTMGMT_TITLE_INDEX + 1;
            OdoTransportMgmtCommand trans = transCommandList.get(index);
            if (StringUtils.isEmpty(trans.getOdoExtCode())) {
                rootExcelException.getExcelExceptions().add(new ExcelException("入库单外接编码不能为空", null, rowNum, null));
            } else {
                if (!odoGroupCommandMap.containsKey(trans.getOdoExtCode())) {
                    rootExcelException.getExcelExceptions().add(new ExcelException("找不到对应的出库单或出库单有重大的错误不进行校验", null, rowNum, null));
                } else {
                    if (StringUtils.hasText(trans.getModeOfTransport())) {
                        SysDictionary dic = this.sysDictionaryManager.getGroupbyGroupValueAndDicValue(Constants.TRANSPORT_MODE, trans.getModeOfTransport());
                        if (dic == null) {
                            rootExcelException.getExcelExceptions().add(new ExcelException("运输方式编码错误", null, rowNum, null));
                        }
                    }
                    if (StringUtils.hasText(trans.getTransportServiceProvider()) && false) {
                        MaTransport port = new MaTransport();
                        port.setCode(trans.getTransportServiceProvider());
                        List<MaTransport> portList = this.maTransportManager.findMaTransport(port);
                        if (portList == null || portList.size() == 0) {
                            rootExcelException.getExcelExceptions().add(new ExcelException("运输服务商编码错误", null, rowNum, null));
                        }
                        // #TODO 快递服务类型
                    }
                    if (StringUtils.hasText(trans.getOutboundTargetType())) {
                        SysDictionary dic = this.sysDictionaryManager.getGroupbyGroupValueAndDicValue(Constants.ODO_AIM_TYPE, trans.getOutboundTargetType());
                        if (dic == null) {
                            rootExcelException.getExcelExceptions().add(new ExcelException("出库目标类型编码错误", null, rowNum, null));
                        } else {

                            if (StringUtils.isEmpty(trans.getOutboundTarget())) {
                                rootExcelException.getExcelExceptions().add(new ExcelException("出库目标不允许为空", null, rowNum, null));
                            } else {
                                if (Constants.AIMTYPE_1.equals(trans.getOutboundTargetType())) {// 供应商
                                    Supplier supplierSearch = new Supplier();
                                    supplierSearch.setCode(trans.getOutboundTarget());
                                    List<Supplier> supplierList = this.supplierManager.findListByParam(supplierSearch);
                                    if (supplierList == null || supplierList.size() == 0) {
                                        rootExcelException.getExcelExceptions().add(new ExcelException("出库目标类型为供应商时候，出库目标找不到对应的供应商", null, rowNum, null));
                                    }
                                } else if (Constants.AIMTYPE_5.equals(trans.getOutboundTargetType())) {
                                    Warehouse wh = this.warehouseManager.findWarehouseByCode(trans.getOutboundTarget());
                                    if (wh == null) {
                                        rootExcelException.getExcelExceptions().add(new ExcelException("出库目标类型为仓库时候，出库目标找不到对应的仓库", null, rowNum, null));
                                    }

                                } else if (Constants.AIMTYPE_7.equals(trans.getOutboundTargetType())) {
                                    DistributionTarget search = new DistributionTarget();
                                    search.setCode(trans.getOutboundTarget());
                                    List<DistributionTarget> targetList = this.distributionTargetManager.findDistributionTargetByParams(search);
                                    if (targetList == null || targetList.size() == 0) {
                                        rootExcelException.getExcelExceptions().add(new ExcelException("出库目标类型为收货单位时候，出库目标找不到对应的收货单位", null, rowNum, null));
                                    }
                                }
                            }
                        }
                    }
                    OdoGroupCommand group = odoGroupCommandMap.get(trans.getOdoExtCode());
                    group.setTransPortMgmt(trans);
                    odoGroupCommandMap.put(trans.getOdoExtCode(), group);
                }
            }
        }
    }


    private void validateAddress(ExcelImportResult excelImportResult, Map<String, OdoGroupCommand> odoGroupCommandMap, Locale locale, Long userId, Long ouId, String logId) {
        List<OdoAddressCommand> addressCommandList = excelImportResult.getListBean();
        RootExcelException rootExcelException = new RootExcelException("", excelImportResult.getSheetName(), excelImportResult.getTitleSize());
        for (int index = 0; index < addressCommandList.size(); index++) {
            int rowNum = index + Constants.IMPORT_WHODO_ADDRESS_TITLE_INDEX + 1;
            OdoAddressCommand addressCommand = addressCommandList.get(index);
            if (StringUtils.isEmpty(addressCommand.getOdoExtCode())) {
                rootExcelException.getExcelExceptions().add(new ExcelException("入库单外接编码不能为空", null, rowNum, null));
            } else {
                if (!odoGroupCommandMap.containsKey(addressCommand.getOdoExtCode())) {
                    rootExcelException.getExcelExceptions().add(new ExcelException("找不到对应的出库单或出库单有重大的错误不进行校验", null, rowNum, null));
                } else {
                    if (StringUtils.isEmpty(addressCommand.getDistributionTargetTelephone()) && StringUtils.isEmpty(addressCommand.getDistributionTargetTelephoneNumber())) {
                        rootExcelException.getExcelExceptions().add(new ExcelException("配送对象电话和手机必填一个", null, rowNum, null));
                    }
                    if (StringUtils.isEmpty(addressCommand.getDistributionTargetName())) {
                        rootExcelException.getExcelExceptions().add(new ExcelException("配送对象姓名不能为空", null, rowNum, null));
                    }
                    if (StringUtils.isEmpty(addressCommand.getDistributionTargetCountry())) {
                        rootExcelException.getExcelExceptions().add(new ExcelException("配送对象国家不能为空", null, rowNum, null));
                    } else {
                        Region country = this.regionManager.findRegionByNameAndParentId(addressCommand.getDistributionTargetCountry(), Constants.ROOT_REGION);
                        if (country == null) {
                            rootExcelException.getExcelExceptions().add(new ExcelException("配送对象国家名称不在配置的地址表内", null, rowNum, null));
                        } else {
                            Long countryId = country.getId();

                            if (StringUtils.isEmpty(addressCommand.getDistributionTargetAddress())) {
                                rootExcelException.getExcelExceptions().add(new ExcelException("配送对象详细地址不能为空", null, rowNum, null));
                            }

                            if (StringUtils.isEmpty(addressCommand.getDistributionTargetProvince())) {
                                rootExcelException.getExcelExceptions().add(new ExcelException("配送对象省不能为空", null, rowNum, null));
                            } else {

                                Region province = this.regionManager.findRegionByNameAndParentId(addressCommand.getDistributionTargetProvince(), countryId);
                                if (province == null) {
                                    rootExcelException.getExcelExceptions().add(new ExcelException("配送对象省名称不在配置的地址表内", null, rowNum, null));
                                } else {
                                    Long provinceId = province.getId();
                                    if (StringUtils.isEmpty(addressCommand.getDistributionTargetCity())) {
                                        rootExcelException.getExcelExceptions().add(new ExcelException("配送对象市不能为空", null, rowNum, null));
                                    } else {

                                        Region city = this.regionManager.findRegionByNameAndParentId(addressCommand.getDistributionTargetCity(), provinceId);
                                        if (city == null) {
                                            rootExcelException.getExcelExceptions().add(new ExcelException("配送对象市名称不在配置的地址表内", null, rowNum, null));
                                        } else {
                                            Long cityId = city.getId();
                                            if (StringUtils.isEmpty(addressCommand.getDistributionTargetDistrict())) {
                                                rootExcelException.getExcelExceptions().add(new ExcelException("配送对象区不能为空", null, rowNum, null));
                                            } else {
                                                Region district = this.regionManager.findRegionByNameAndParentId(addressCommand.getDistributionTargetDistrict(), cityId);
                                                if (district == null) {
                                                    rootExcelException.getExcelExceptions().add(new ExcelException("配送对象区名称不在配置的地址表内", null, rowNum, null));
                                                } else {
                                                    Long districtId = district.getId();
                                                    if (StringUtils.isEmpty(addressCommand.getDistributionTargetVillagesTowns())) {

                                                    } else {
                                                        Region town = this.regionManager.findRegionByNameAndParentId(addressCommand.getDistributionTargetDistrict(), districtId);
                                                        if (town == null) {
                                                            rootExcelException.getExcelExceptions().add(new ExcelException("配送对象乡镇名称不在配置的地址表内", null, rowNum, null));
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            if (StringUtils.isEmpty(addressCommand.getConsigneeTargetProvince())) {} else {
                                Region province = this.regionManager.findRegionByNameAndParentId(addressCommand.getConsigneeTargetProvince(), countryId);
                                if (province == null) {} else {
                                    Long provinceId = province.getId();
                                    if (StringUtils.isEmpty(addressCommand.getConsigneeTargetCity())) {} else {

                                        Region city = this.regionManager.findRegionByNameAndParentId(addressCommand.getConsigneeTargetCity(), provinceId);
                                        if (city == null) {
                                            rootExcelException.getExcelExceptions().add(new ExcelException("配送对象市名称不在配置的地址表内", null, rowNum, null));
                                        } else {
                                            Long cityId = city.getId();
                                            if (StringUtils.isEmpty(addressCommand.getConsigneeTargetDistrict())) {} else {
                                                Region district = this.regionManager.findRegionByNameAndParentId(addressCommand.getConsigneeTargetDistrict(), cityId);
                                                if (district == null) {
                                                    rootExcelException.getExcelExceptions().add(new ExcelException("配送对象区名称不在配置的地址表内", null, rowNum, null));
                                                } else {
                                                    Long districtId = district.getId();
                                                    if (StringUtils.isEmpty(addressCommand.getConsigneeTargetVillagesTowns())) {

                                                    } else {
                                                        Region town = this.regionManager.findRegionByNameAndParentId(addressCommand.getConsigneeTargetDistrict(), districtId);
                                                        if (town == null) {
                                                            rootExcelException.getExcelExceptions().add(new ExcelException("配送对象乡镇名称不在配置的地址表内", null, rowNum, null));
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    OdoGroupCommand group = odoGroupCommandMap.get(addressCommand.getOdoExtCode());
                    WhOdoAddress address = new WhOdoAddress();
                    BeanUtils.copyProperties(addressCommand, address);
                    group.setWhOdoAddress(address);
                    odoGroupCommandMap.put(addressCommand.getOdoExtCode(), group);
                }

            }
        }

    }


    private void validateLines(ExcelImportResult excelImportResult, Map<String, OdoGroupCommand> odoGroupCommandMap, Locale locale, Long userId, Long ouId, String logId) {
        List<OdoLineCommand> lineCommandList = excelImportResult.getListBean();

        RootExcelException rootExcelException = new RootExcelException("", excelImportResult.getSheetName(), excelImportResult.getTitleSize());
        Map<Long, String> invMap = this.getInvStatusMap();
        for (int index = 0; index < lineCommandList.size(); index++) {
            int rowNum = index + Constants.IMPORT_WHODO_LINE_TITLE_INDEX + 1;
            OdoLineCommand line = lineCommandList.get(index);
            if (StringUtils.isEmpty(line.getOdoExtCode())) {
                rootExcelException.getExcelExceptions().add(new ExcelException("入库单外接编码不能为空", null, rowNum, null));
            } else {
                if (!odoGroupCommandMap.containsKey(line.getOdoExtCode())) {
                    rootExcelException.getExcelExceptions().add(new ExcelException("明细行找不到对应的出库单或出库单有重大的错误不进行校验", null, rowNum, null));
                } else {
                    OdoGroupCommand group = odoGroupCommandMap.get(line.getOdoExtCode());
                    OdoCommand odo = group.getOdo();
                    if (StringUtils.isEmpty(line.getStoreCode())) {
                        line.setStoreId(odo.getStoreId());
                    } else {
                        Store store = this.storeManager.findStoreByCode(odo.getStoreCode());
                        if (store == null) {
                            rootExcelException.getExcelExceptions().add(new ExcelException("店铺编码找不到对应的店铺信息", null, rowNum, null));
                        } else {
                            if (!Constants.LIFECYCLE_START.equals(store.getLifecycle())) {
                                rootExcelException.getExcelExceptions().add(new ExcelException("店铺无效", null, rowNum, null));
                            }
                            if (!odo.getCustomerId().equals(store.getCustomerId())) {
                                rootExcelException.getExcelExceptions().add(new ExcelException("客户-店铺不对应", null, rowNum, null));
                            }
                            Long storeId = store.getId();
                            line.setStoreId(storeId);
                            Boolean customerStoreUserFlag = this.storeManager.checkCustomerStoreUser(odo.getCustomerId(), storeId, userId);
                            if (!customerStoreUserFlag) {
                                rootExcelException.getExcelExceptions().add(new ExcelException("用户不具有此客户-店铺权限", null, rowNum, null));
                            }


                        }
                    }
                    Sku sku = this.biPoLineManager.findSkuByBarCode(line.getSkuBarCode(), odo.getCustomerId(), logId);
                    if (sku == null) {
                        rootExcelException.getExcelExceptions().add(new ExcelException("条码找不到对应的商品", null, rowNum, null));
                    } else {

                        line.setSkuId(sku.getId());
                        line.setSkuBarCode(sku.getBarCode());
                        line.setSkuName(sku.getName());
                    }
                    if (line.getInvStatus() == null) {
                        rootExcelException.getExcelExceptions().add(new ExcelException("库存状态不能为空", null, rowNum, null));
                    } else {
                        if (!invMap.containsKey(line.getInvStatus())) {
                            rootExcelException.getExcelExceptions().add(new ExcelException("库存状态编码错误", null, rowNum, null));
                        }
                    }
                    if (StringUtils.hasText(line.getInvType())) {
                        SysDictionary dic = this.sysDictionaryManager.getGroupbyGroupValueAndDicValue(Constants.INVENTORY_TYPE, line.getInvType());
                        if (dic == null) {
                            rootExcelException.getExcelExceptions().add(new ExcelException("库存类型编码错误", null, rowNum, null));
                        }
                    }
                    if (StringUtils.hasText(line.getInvAttr1())) {
                        SysDictionary dic = this.sysDictionaryManager.getGroupbyGroupValueAndDicValue(Constants.INVENTORY_ATTR_1, line.getInvAttr1());
                        if (dic == null) {
                            rootExcelException.getExcelExceptions().add(new ExcelException("库存属性1编码错误", null, rowNum, null));
                        }
                    }
                    if (StringUtils.hasText(line.getInvAttr2())) {
                        SysDictionary dic = this.sysDictionaryManager.getGroupbyGroupValueAndDicValue(Constants.INVENTORY_ATTR_2, line.getInvAttr2());
                        if (dic == null) {
                            rootExcelException.getExcelExceptions().add(new ExcelException("库存属性2编码错误", null, rowNum, null));
                        }
                    }
                    if (StringUtils.hasText(line.getInvAttr3())) {
                        SysDictionary dic = this.sysDictionaryManager.getGroupbyGroupValueAndDicValue(Constants.INVENTORY_ATTR_3, line.getInvAttr3());
                        if (dic == null) {
                            rootExcelException.getExcelExceptions().add(new ExcelException("库存属性3编码错误", null, rowNum, null));
                        }
                    }
                    if (StringUtils.hasText(line.getInvAttr4())) {
                        SysDictionary dic = this.sysDictionaryManager.getGroupbyGroupValueAndDicValue(Constants.INVENTORY_ATTR_4, line.getInvAttr4());
                        if (dic == null) {
                            rootExcelException.getExcelExceptions().add(new ExcelException("库存属性4编码错误", null, rowNum, null));
                        }
                    }
                    if (StringUtils.hasText(line.getInvAttr5())) {
                        SysDictionary dic = this.sysDictionaryManager.getGroupbyGroupValueAndDicValue(Constants.INVENTORY_ATTR_5, line.getInvAttr5());
                        if (dic == null) {
                            rootExcelException.getExcelExceptions().add(new ExcelException("库存属性5编码错误", null, rowNum, null));
                        }
                    }
                    if (StringUtils.hasText(line.getPartOutboundStrategy())) {
                        SysDictionary dic = this.sysDictionaryManager.getGroupbyGroupValueAndDicValue(Constants.PART_OUTBOUND_STRATEGY, line.getInvAttr5());
                        if (dic == null) {
                            rootExcelException.getExcelExceptions().add(new ExcelException("部分出库策略编码错误", null, rowNum, null));
                        }
                    }

                    List<OdoLineCommand> lineList = group.getOdoLineList();
                    if (lineList == null) {
                        lineList = new ArrayList<OdoLineCommand>();
                    }
                    lineList.add(line);
                    group.setOdoLineList(lineList);
                    odoGroupCommandMap.put(line.getOdoExtCode(), group);
                }
            }

        }
        if (rootExcelException.isException()) {
            excelImportResult.setRootExcelException(rootExcelException);
            excelImportResult.setReadstatus(ExcelImportResult.READ_STATUS_FAILED);
        }

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


    private void validateOdo(ExcelImportResult excelImportResult, Map<String, OdoGroupCommand> odoGroupCommandMap, Locale locale, Long userId, Long ouId) {
        List<OdoCommand> lineCommandList = excelImportResult.getListBean();
        RootExcelException rootExcelException = new RootExcelException("", excelImportResult.getSheetName(), excelImportResult.getTitleSize());

        for (int index = 0; index < lineCommandList.size(); index++) {
            int rowNum = index + Constants.IMPORT_WHODO_TITLE_INDEX + 1;
            OdoCommand odo = lineCommandList.get(index);
            boolean isAdd = false;
            if (StringUtils.isEmpty(odo.getExtCode())) {
                rootExcelException.getExcelExceptions().add(new ExcelException("出库单外接编码不能为空", null, rowNum, null));
            } else if (odoGroupCommandMap.containsKey(odo.getExtCode())) {
                rootExcelException.getExcelExceptions().add(new ExcelException("excel中有重复的外接编码", null, rowNum, null));
            } else {
                if (StringUtils.isEmpty(odo.getCustomerCode())) {
                    rootExcelException.getExcelExceptions().add(new ExcelException("入库单客户编码不能为空", null, rowNum, null));
                }
                Customer customer = this.customerManager.findCustomerbyCode(odo.getCustomerCode());
                if (customer == null) {
                    rootExcelException.getExcelExceptions().add(new ExcelException("客户编码有误，找不到对应的客户信息", null, rowNum, null));
                } else {

                    Long customerId = customer.getId();
                    odo.setCustomerId(customerId);

                    if (StringUtils.isEmpty(odo.getStoreCode())) {
                        rootExcelException.getExcelExceptions().add(new ExcelException("店铺编码不能为空", null, rowNum, null));
                    } else {
                        Store store = this.storeManager.findStoreByCode(odo.getStoreCode());
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
                            odo.setStoreId(storeId);
                            Boolean customerStoreUserFlag = this.storeManager.checkCustomerStoreUser(customerId, storeId, userId);
                            if (!customerStoreUserFlag) {
                                rootExcelException.getExcelExceptions().add(new ExcelException("用户不具有此客户-店铺权限", null, rowNum, null));
                            }
                            // 校验ExtCode: ext_code与storeId 唯一性
                            WhOdo checkOdo = this.odoManager.findByExtCodeStoreIdOuId(odo.getExtCode(), storeId, ouId);
                            if (checkOdo != null) {
                                rootExcelException.getExcelExceptions().add(new ExcelException("出库单校验相关单据号失败，同一个店铺下有相同的相关单据号", null, rowNum, null));
                            } else {
                                isAdd = true;
                            }

                        }
                    }
                }

            }

            if (StringUtils.isEmpty(odo.getOdoType())) {
                rootExcelException.getExcelExceptions().add(new ExcelException("出库单类型不能为空", null, rowNum, null));
            } else {
                SysDictionary poTypeDic = this.sysDictionaryManager.getGroupbyGroupValueAndDicValue(Constants.ODO_TYPE, odo.getOdoType() + "");
                if (poTypeDic == null) {
                    rootExcelException.getExcelExceptions().add(new ExcelException("系统参数出库单类型不正确", null, rowNum, null));
                }
            }
            // 只有关键元素校验通过了才会进行后续的校验
            if (isAdd) {
                OdoGroupCommand group = new OdoGroupCommand();
                group.setOdo(odo);
                odoGroupCommandMap.put(odo.getExtCode(), group);
            }

        }
        if (rootExcelException.isException()) {
            excelImportResult.setRootExcelException(rootExcelException);
            excelImportResult.setReadstatus(ExcelImportResult.READ_STATUS_FAILED);
        }
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

    private void createOdo(List<OdoGroupCommand> groupList, Long ouId, Long userId) {

        for (OdoGroupCommand group : groupList) {
            OdoCommand odo = group.getOdo();
            OdoTransportMgmtCommand transportMgmt = group.getTransPortMgmt();
            List<OdoLineCommand> odoLineList = group.getOdoLineList();
            // 默认属性
            if (odo.getCurrentQty() == null) {
                odo.setCurrentQty(Constants.DEFAULT_DOUBLE);
            }
            if (odo.getActualQty() == null) {
                odo.setActualQty(Constants.DEFAULT_DOUBLE);
            }
            if (odo.getCancelQty() == null) {
                odo.setCancelQty(Constants.DEFAULT_DOUBLE);
            }
            if (null == odo.getIsWholeOrderOutbound()) {
                odo.setIsWholeOrderOutbound(true);
            }
            if (null == odo.getPriorityLevel()) {
                odo.setPriorityLevel(Constants.ODO_DEFAULT_PRIORITYLEVLE);
            }
            if (null == odo.getIncludeFragileCargo()) {
                odo.setIncludeFragileCargo(false);
            }
            if (null == odo.getIncludeHazardousCargo()) {
                odo.setIncludeHazardousCargo(false);
            }
            if (null == odo.getIsLocked()) {
                odo.setIsLocked(false);
            }
            odo.setCreatedId(userId);
            odo.setCreateTime(new Date());
            odo.setModifiedId(userId);
            odo.setLastModifyTime(new Date());
            if (null == odo.getOrderTime()) {
                odo.setOrderTime(new Date());
            }
            if (null == odo.getQty()) {
                odo.setQty(Constants.DEFAULT_DOUBLE);
            }
            if (null == odo.getSkuNumberOfPackages()) {
                odo.setSkuNumberOfPackages(Constants.DEFAULT_INTEGER);
            }
            if (null == odo.getAmt()) {
                odo.setAmt(Constants.DEFAULT_DOUBLE);
            }
            if (null == odo.getIsAllowMerge()) {
                odo.setIsAllowMerge(true);
            }
            if (StringUtils.isEmpty(odo.getOdoStatus())) {
                odo.setOdoStatus(OdoStatus.NEW);
            }
            odo.setOuId(ouId);
            // 设置单号和外部对接编码
            String odoCode = codeManager.generateCode(Constants.WMS, Constants.WHODO_MODEL_URL, Constants.WMS_ODO_INNER, "ODO", null);
            odo.setOdoCode(odoCode);
            if (StringUtils.isEmpty(odo.getExtCode())) {
                String extCode = codeManager.generateCode(Constants.WMS, Constants.WHODO_MODEL_URL, Constants.WMS_ODO_EXT, null, null);
                odo.setExtCode(extCode);
            }
            // @mender yimin.lu 2017/5/11 不在此处计算

            // 匹配配货模式

            transportMgmt.setOuId(ouId);
        }
        this.odoManager.createOdo(groupList, ouId, userId);

    }

    @Override
    public String createOdoWaveNew(Long waveMasterId, Long ouId, Long userId, List<Long> odoIdList, String logId) {
        /**
         * 校验阶段
         */
        /**
         * 校验出库单头和明细状态；以及是否处于别的波次中
         */

        WhWaveMaster master = this.odoManager.findWaveMasterByIdouId(waveMasterId, ouId);
        if (master == null) {
            throw new BusinessException(ErrorCodes.PARAM_IS_NULL);
        }

        // 波次出库单总单数
        int odoCount = odoIdList.size();
        if (master.getMinOdoQty() != null) {
            if (master.getMinOdoQty() > odoCount) {
                throw new BusinessException("出库单数目不满足波次最小出库单数");
            }
        }
        if (master.getMaxOdoQty() != null) {
            if (master.getMaxOdoQty() < odoCount) {
                throw new BusinessException("出库单数不满足波次最大出库单数");
            }
        }
        WaveCommand waveCommand = this.odoManager.findWaveSumDatabyOdoIdList(odoIdList, ouId);
        // 商品种类数
        int skuCategoryQty = waveCommand.getSkuCategoryQty();
        // 总体积
        double totalVolume = waveCommand.getTotalVolume();
        // 总重量
        double totalWeight = waveCommand.getTotalWeight();
        // 波次明细数
        int odolineCount = waveCommand.getTotalOdoLineQty();
        // 总金额
        double totalAmt = waveCommand.getTotalAmount();
        // 商品总件数
        double totalSkuQty = waveCommand.getTotalSkuQty();



        if (master.getMinOdoQty() != null) {
            if (master.getMinOdoQty() > odoCount) {
                throw new BusinessException("出库单数目不满足波次最小出库单数");
            }
        }
        if (master.getMaxOdoQty() != null) {
            if (master.getMaxOdoQty() < odoCount) {
                throw new BusinessException("出库单数不满足波次最大出库单数");
            }
        }
        if (master.getMaxOdoLineQty() != null) {
            if (master.getMaxOdoLineQty() < odolineCount) {
                throw new BusinessException("出库单明细数不满足波次最大出库明细数");
            }
        }
        if (master.getMaxSkuQty() != null) {
            if (master.getMaxSkuQty() < totalSkuQty) {
                throw new BusinessException("商品数不满足波次最大出库商品数");
            }
        }
        if (master.getMaxSkuCategoryQty() != null) {
            if (master.getMaxSkuCategoryQty() < skuCategoryQty) {
                throw new BusinessException("商品种类数不满足波次最大出库商品种类数");
            }
        }
        if (master.getMaxVolume() != null) {
            if (master.getMaxVolume() < totalVolume) {
                throw new BusinessException("体积不满足波次最大出库体积");
            }
        }
        if (master.getMaxWeight() != null) {
            if (master.getMaxWeight() < totalWeight) {
                throw new BusinessException("重量不满足波次最大出库重量");
            }
        }
        /**
         * 创建波次头
         */
        WhWave wave = new WhWave();
        // a 生成波次编码，校验唯一性；补偿措施
        // #TODO 校验波次号
        String waveCode = "";
        try {
            waveCode = codeManager.generateCode(Constants.WMS, Constants.WHWAVE_MODEL_URL, "", "WAVE", null);
        } catch (Exception e) {
            throw new BusinessException(ErrorCodes.CODE_MANAGER_ERROR);
        }
        if (StringUtils.isEmpty(waveCode)) {
            throw new BusinessException(ErrorCodes.CODE_MANAGER_ERROR);
        }
        wave.setCode(waveCode);
        wave.setStatus(WaveStatus.WAVE_TOBECREATED);
        wave.setOuId(ouId);
        wave.setWaveMasterId(waveMasterId);
        wave.setTotalOdoQty(Constants.DEFAULT_INTEGER);
        wave.setTotalOdoLineQty(Constants.DEFAULT_INTEGER);
        wave.setTotalAmount(Constants.DEFAULT_DOUBLE);
        wave.setTotalVolume(Constants.DEFAULT_DOUBLE);
        wave.setTotalWeight(Constants.DEFAULT_DOUBLE);
        wave.setTotalSkuQty(Constants.DEFAULT_DOUBLE);
        wave.setSkuCategoryQty(Constants.DEFAULT_INTEGER);
        wave.setIsRunWave(false);
        wave.setCreatedId(userId);
        wave.setCreateTime(new Date());
        wave.setModifiedId(userId);
        wave.setLastModifyTime(new Date());
        wave.setIsError(false);
        wave.setLifecycle(Constants.LIFECYCLE_START);


        this.odoManager.createOdoWaveNew(wave, master.getWaveTemplateId(), odoIdList);
        return waveCode;
    }

    @Override
    public List<WhWave> findWaveToBeCreated(Long ouId) {
        return this.waveManager.findWaveToBeCreated(ouId);
    }

    @Override
    public List<WhOdoLine> findOdoLineToBeAddedToWave(String waveCode, Long ouId) {
        return null;
    }

    @Override
    public List<Long> findOdoToBeAddedToWave(String waveCode, Long ouId) {
        return this.odoManager.findOdoToBeAddedToWave(waveCode, ouId);
    }

    @Override
    public void addOdoLineToWave(List<Long> odoIdList, WhWave wave) {
        try {
            this.waveManager.addOdoLineToWaveNew(odoIdList, wave);
        } catch (Exception e) {

        }

    }

    @Override
    public void finishCreateWave(WhWave wave) {
        try {
            this.waveManager.finishCreateWave(wave);
        } catch (Exception e) {

        }

    }


    @Override
    public WhOdo findByExtCodeStoreIdOuId(String extOdoCode, Long storeId, Long ouId) {
        return this.odoManager.findByExtCodeStoreIdOuId(extOdoCode, storeId, ouId);
    }


    @Override
    public List<Long> findOdoIdListForWaveByCustom(OdoGroupSearchCommand command) {
        List<Long> odoIdList = new ArrayList<Long>();
        // 全选
        if (command.getConditionList() != null && command.getConditionList().size() > 0) {
            OdoSearchCommand search = new OdoSearchCommand();
            BeanUtils.copyProperties(command, search);
            search.setLineFlag(true);
            // @mender yimin.lu 2017/2/7 非锁定的出库单
            search.setIsLocked(Constants.DEFAULT_INTEGER);
            if (StringUtils.hasText(command.getOdoStatus())) {
                search.setOdoStatus(Arrays.asList(command.getOdoStatus().split(",")));
            }
            if (StringUtils.hasText(command.getEpistaticSystemsOrderType())) {
                search.setEpistaticSystemsOrderType(Arrays.asList(command.getEpistaticSystemsOrderType().split(",")));
            }
            if (StringUtils.hasText(command.getCustomerId())) {
                search.setCustomerId(Arrays.asList(command.getCustomerId().split(",")));
            }
            if (StringUtils.hasText(command.getOutboundTargetType())) {
                search.setOutboundTargetType(Arrays.asList(command.getOutboundTargetType().split(",")));
            }
            if (StringUtils.hasText(command.getOdoType())) {
                search.setOdoType(Arrays.asList(command.getOdoType().split(",")));
            }
            if (StringUtils.hasText(command.getStoreId())) {
                search.setStoreId(Arrays.asList(command.getStoreId().split(",")));
            }
            if (StringUtils.hasText(command.getModeOfTransport())) {
                search.setModeOfTransport(Arrays.asList(command.getModeOfTransport().split(",")));
            }
            if (StringUtils.hasText(command.getTransportServiceProvider())) {
                String[] arr = command.getTransportServiceProvider().split(",");
                search.setTransportServiceProvider(Arrays.asList(arr));
            }
            if (StringUtils.hasText(command.getTransportServiceProviderType())) {
                search.setTransportServiceProviderType(Arrays.asList(command.getTransportServiceProviderType().split(",")));
            }
            if (StringUtils.hasText(command.getDistributeMode())) {
                search.setDistributeMode(Arrays.asList(command.getDistributeMode().split(",")));
            }
            if (StringUtils.hasText(command.getOutBoundCartonType())) {
                search.setOutBoundCartonType(Arrays.asList(command.getOutBoundCartonType().split(",")));
            }
            if (StringUtils.hasText(command.getLineOutboundCartonType())) {
                search.setLineOutboundCartonType(Arrays.asList(command.getLineOutboundCartonType().split(",")));
            }
            if (StringUtils.hasText(command.getInvType())) {
                search.setInvType(Arrays.asList(command.getInvType().split(",")));
            }
            if (StringUtils.hasText(command.getInvStatus())) {
                search.setInvStatus(Arrays.asList(command.getInvStatus().split(",")));
            }
            if (StringUtils.hasText(command.getInvAttr1())) {
                search.setInvAttr1(Arrays.asList(command.getInvAttr1().split(",")));
            }
            if (StringUtils.hasText(command.getInvAttr2())) {
                search.setInvAttr2(Arrays.asList(command.getInvAttr2().split(",")));
            }
            if (StringUtils.hasText(command.getInvAttr3())) {
                search.setInvAttr3(Arrays.asList(command.getInvAttr3().split(",")));
            }
            if (StringUtils.hasText(command.getInvAttr4())) {
                search.setInvAttr4(Arrays.asList(command.getInvAttr4().split(",")));
            }
            if (StringUtils.hasText(command.getInvAttr5())) {
                search.setInvAttr5(Arrays.asList(command.getInvAttr5().split(",")));
            }
            if (StringUtils.hasText(command.getWhVasType())) {
                search.setWhVasType(Arrays.asList(command.getWhVasType().split(",")));
            }
            if (StringUtils.hasText(command.getOrderType())) {
                search.setOrderType(Arrays.asList(command.getOrderType().split(",")));
            }
            if (StringUtils.hasText(command.getDeliverGoodsTimeMode())) {
                search.setDeliverGoodsTimeMode(Arrays.asList(command.getDeliverGoodsTimeMode().split(",")));
            }
            if (StringUtils.hasText(command.getOdoLineStatus())) {
                search.setOdoLineStatus(Arrays.asList(command.getOdoLineStatus().split(",")));
            }
            if (StringUtils.hasText(command.getLineOutboundCartonType())) {
                search.setLineOutboundCartonType(Arrays.asList(command.getLineOutboundCartonType().split(",")));
            }
            // 如果不选分组 默认按照客户分组
            // 如果没有选出库单状态，则默认为：新建和部分出库
            if (search.getOdoStatus() == null || search.getOdoStatus().size() == 0) {
                search.setOdoStatus(Arrays.asList(new String[] {OdoStatus.NEW, OdoStatus.PARTLY_FINISH}));
            }
            // 如果没有选出库单明细状态，则默认为新建和部分出库
            if (search.getOdoLineStatus() == null || search.getOdoLineStatus().size() == 0) {
                search.setOdoLineStatus(Arrays.asList(new String[] {OdoStatus.ODOLINE_NEW, OdoStatus.ODOLINE_OUTSTOCK}));
            }
            for (OdoWaveGroupSearchCondition gsc : command.getConditionList()) {
                search.setGroupCustomerId(gsc.getCustomerId());
                search.setGroupOdoStatus(gsc.getOdoStatus());
                search.setGroupStoreId(gsc.getStoreId());
                search.setGroupOdoType(gsc.getOdoType());
                search.setGroupDistributeMode(gsc.getDistributeMode());
                search.setGroupEpistaticSystemsOrderType(gsc.getEpistaticSystemsOrderType());
                search.setGroupTransportServiceProvider(gsc.getTransportServiceProvider());
                search.setIsEpistaticSystemsOrderType(gsc.getIsEpistaticSystemsOrderType());
                search.setIsDistributeMode(gsc.getIsDistributeMode());
                List<Long> liOdoList = this.odoManager.findOdoIdListForWave(search);
                if (liOdoList != null && liOdoList.size() > 0) {
                    odoIdList.addAll(liOdoList);
                }
            }
        }
        // 部分点选
        if (command.getOdoIdList() != null && command.getOdoIdList().size() > 0) {
            odoIdList.addAll(command.getOdoIdList());
        }

        return odoIdList;
    }


    @Override
    public WhOdodeliveryInfo getLogisticsInfoByOdoId(Long odoId, String logId, Long ouId) {
        WhOdo odo = odoManager.findOdoByIdOuId(odoId, ouId);
        WhOdoTransportMgmt transMgmt = odoTransportMgmtManager.findTransportMgmtByOdoIdOuId(odoId, ouId);
        WhOdoAddress address = odoAddressManager.findOdoAddressByOdoId(odoId, ouId);
        List<WhOdoLine> odoLineList = odoLineManager.findOdoLineListByOdoId(odoId, ouId);
        WarehouseCommand wh = warehouseManager.findWarehouseCommandById(ouId);
        boolean isInsured = transMgmt.getInsuranceCoverage() == null ? false : true;
        // 封装数据匹配物流sql推荐实体
        SuggestTransContentCommand trans = odoManager.getSuggestTransContent(odo, transMgmt, address, odoLineList, isInsured, logId, ouId);
        trans.setWhCode(wh.getCode());

        WhOdoTransportService transportService = odoTransportMgmtManager.findTransportMgmtServiceByOdoIdOuId(odoId, ouId);
        // 获取增值服务
        // 没有调用过或调用失败, 则调用物流增值服务推荐
        if (null == transportService || !transportService.getIsVasSuccess()) {
            boolean flag = this.callVasTransService(trans, transMgmt, odoId, ouId);
            if (!flag) {
                return null;
            }
        }
        // 获取推荐物流商
        // 物流商 或 时效类型 或 产品类型为空则调用
        if (StringUtils.isEmpty(transMgmt.getTransportServiceProvider()) || StringUtils.isEmpty(transMgmt.getTimeEffectType()) || StringUtils.isEmpty(transMgmt.getCourierServiceType())) {
            boolean flag = this.callSuggestTransService(trans, transMgmt, odoId, ouId);
            if (!flag) {
                return null;
            }
        } else {
            odoTransportMgmtManager.saveOrUpdateTransportService(odoId, true, 2, null, null, ouId);
        }
        // 获取运单号
        if (StringUtils.isEmpty(transMgmt.getTransportServiceProvider())) {
            odoTransportMgmtManager.saveOrUpdateTransportService(odoId, false, 3, "lpCode is null", null, ouId);
            return null;
        }
        WhOdodeliveryInfo info = this.getMailNo(odo, transMgmt, address, odoLineList, wh, isInsured, odoId, ouId);
        return info;
    }

    /**
     * 获取运单号
     */
    private WhOdodeliveryInfo getMailNo(WhOdo odo, WhOdoTransportMgmt transMgmt, WhOdoAddress address, List<WhOdoLine> odoLineList, WarehouseCommand wh, boolean isInsured, Long odoId, Long ouId) {
        try {
            MaTransport transport = maTransportManager.findMaTransportByCode(transMgmt.getTransportServiceProvider(), Constants.WMS4);
            // 纸质面单
            if (Constants.WAYBILL_TYPE_PAPER.equals(transport.getWaybillType())) {
                odoTransportMgmtManager.saveOrUpdateTransportService(odoId, true, 3, null, false, ouId);
                WhOdodeliveryInfo delivery = new WhOdodeliveryInfo();
                delivery.setTransportCode(transMgmt.getTransportServiceProvider());
                delivery.setTimeEffectType(transMgmt.getTimeEffectType());
                delivery.setTransportServiceType(transMgmt.getCourierServiceType());
                delivery.setOuId(ouId);
                delivery.setOdoId(odoId);
                return delivery;
            }
            // 电子面单,获取运单号
            MailnoGetContentCommand mailNoContent = odoManager.getMailNoContent(odo, address, transMgmt, odoLineList, isInsured, wh);
            // 循环获取5次
            MailnoGetResponse res = this.getMailnoGetResponse(mailNoContent);
            if (null != res && null != res.getStatus() && res.getStatus() == 1) {
                WhOdodeliveryInfo delivery = new WhOdodeliveryInfo();
                delivery.setOdoId(odoId);
                delivery.setCreateTime(new Date());
                delivery.setLastModifyTime(new Date());
                delivery.setLifecycle(BaseModel.LIFECYCLE_NORMAL);
                delivery.setOuId(ouId);
                delivery.setStatus(1);
                delivery.setTransportCode(transMgmt.getTransportServiceProvider());
                delivery.setTimeEffectType(transMgmt.getTimeEffectType());
                delivery.setTransportServiceType(transMgmt.getCourierServiceType());
                delivery.setWaybillCode(res.getMailno()); // 物流单号
                delivery.setTransBigWord(res.getTransBigWord()); // 运单大头笔
                delivery.setTmsCode(res.getTmsCode()); // 二级配送公司编码,用于发货回传
                delivery.setExtId(res.getExtId()); // 物流平台单号
                delivery.setLogisticsCode(res.getLogisticsCode()); // 物流公司编码,用于发货回传
                delivery.setPackageCenterCode(res.getPackageCenterCode()); // 集包地编码
                delivery.setPackageCenterName(res.getPackageCenterName()); // 集包地名称
                if (!StringUtils.isEmpty(res.getTransAccount())) {
                    transMgmt.setTransAccount(res.getTransAccount());   // 账号
                    odoTransportMgmtManager.updateOdoTransportMgmt(transMgmt);
                }
                odoTransportMgmtManager.insertDeliveryInfoExt(delivery);
                return delivery;
            } else {
                if (null == res) {
                    odoTransportMgmtManager.saveOrUpdateTransportService(odoId, false, 3, "response is null", true, ouId);
                } else {
                    odoTransportMgmtManager.saveOrUpdateTransportService(odoId, false, 3, res.getErrorCode() + "|" + res.getErrorMsg(), true, ouId);
                }
            }
        } catch (Exception e) {
            log.error("getMailNo system error, odoId:" + odoId, e);
            odoTransportMgmtManager.saveOrUpdateTransportService(odoId, false, 3, "system error", true, ouId);
        }
        return null;
    }

    /**
     * 调用物流推荐
     */
    private boolean callSuggestTransService(SuggestTransContentCommand trans, WhOdoTransportMgmt transMgmt, Long odoId, Long ouId) {
        try {
            SuggestTransResult transResult = transServiceManager.suggestTransService(trans, Constants.WMS4);
            if (null != transResult && transResult.getStatus() == 1) {
                List<LpCodeList> lpList = transResult.getLpList();
                if (null != lpList && !lpList.isEmpty()) {
                    // 默认取第一个
                    LpCodeList lp = lpList.get(0);
                    // 物流商编码
                    String lpCode = lp.getLpcode();
                    // 产品类型(物流服务类型)
                    String expressType = lp.getExpressType();
                    // 时效类型
                    String timeEffectType = lp.getCode();
                    transMgmt.setTransportServiceProvider(lpCode);
                    transMgmt.setCourierServiceType(expressType);
                    transMgmt.setTimeEffectType(timeEffectType);
                    int num = odoTransportMgmtManager.updateOdoTransportMgmtExt(transMgmt);
                    if (num < 1) {
                        throw new BusinessException(ErrorCodes.SYSTEM_EXCEPTION);
                    }
                    return true;
                } else {
                    odoTransportMgmtManager.saveOrUpdateTransportService(odoId, false, 2, "lpList is empty", null, ouId);
                    return false;
                }
            } else {
                // 失败,记录ErrorMessage
                if (null == transResult) {
                    odoTransportMgmtManager.saveOrUpdateTransportService(odoId, false, 2, "response is null", null, ouId);
                } else {
                    odoTransportMgmtManager.saveOrUpdateTransportService(odoId, false, 2, transResult.getMsg(), null, ouId);
                }
                return false;
            }
        } catch (Exception e) {
            log.error("callSuggestTransService system error, odoId:" + odoId, e);
            odoTransportMgmtManager.saveOrUpdateTransportService(odoId, false, 2, "system error", null, ouId);
            return false;
        }
    }

    /**
     * 调用增值服务
     */
    private boolean callVasTransService(SuggestTransContentCommand trans, WhOdoTransportMgmt transMgmt, Long odoId, Long ouId) {
        try {
            VasTransResult vasResult = transServiceManager.vasTransService(trans, Constants.WMS4);
            if (null != vasResult && vasResult.getStatus() == 1) {
                List<VasLine> vasList = vasResult.getVasList();
                if (null != vasList && !vasList.isEmpty()) {
                    odoVasManager.insertVasList(odoId, vasList, transMgmt, ouId);
                    List<TransVasList> transVasList = new ArrayList<TransVasList>();
                    for (VasLine vas : vasList) {
                        TransVasList transVas = new TransVasList();
                        transVas.setVasCode(vas.getCode());
                        transVasList.add(transVas);
                    }
                    trans.setTransVasList(transVasList);
                }
                return true;
            } else {
                // 失败,记录ErrorMessage
                if (null == vasResult) {
                    odoTransportMgmtManager.saveOrUpdateTransportService(odoId, false, 1, "response is null", null, ouId);
                } else {
                    odoTransportMgmtManager.saveOrUpdateTransportService(odoId, false, 1, vasResult.getErrorCode() + "|" + vasResult.getErrorMassage(), null, ouId);
                }
                return false;
            }
        } catch (Exception e) {
            log.error("callVasTransService system error, odoId:" + odoId, e);
            odoTransportMgmtManager.saveOrUpdateTransportService(odoId, false, 1, "system error", null, ouId);
            return false;
        }
    }

    private MailnoGetResponse getMailnoGetResponse(MailnoGetContentCommand mailNoContent) {
        MailnoGetResponse res = null;
        for (int i = 0; i < 5; i++) {
            List<MailnoGetResponse> matchingTransNo = transServiceManager.matchingTransNo(mailNoContent, Constants.WMS4);
            if (null != matchingTransNo && !matchingTransNo.isEmpty()) {
                res = matchingTransNo.get(0);
                if (null != res && null != res.getStatus() && res.getStatus() == 1) {
                    return res;
                } else {
                    if (i == 4) {
                        return res;
                    }
                }
            }
        }
        return res;
    }


    @Override
    public List<Long> findNewOdoIdList(List<Long> odoIdOriginalList, Long ouId) {
        return this.odoManager.findNewOdoIdList(odoIdOriginalList, ouId);
    }

    @Override
    public Map<String, List<Long>> getStoreIdMapByOdoIdListGroupByInvoice(List<Long> odoIdList, Long ouId) {
        return this.odoManager.getStoreIdMapByOdoIdListGroupByInvoice(odoIdList, ouId);
    }

    @Override
    public List<Long> findOdoIdListByStoreIdListAndOriginalIdList(List<Long> odoIdList, List<Long> storeIdList, Long ouId) {
        return this.odoManager.findOdoIdListByStoreIdListAndOriginalIdList(odoIdList, storeIdList, ouId);
    }


    @Override
    public List<Long> findPrintOdoIdList(Long waveId, Long ouId) {
        WhWave wave = whWaveManager.findWaveByIdOuId(waveId, ouId);
        if (wave == null) {
            throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
        }
        String waveCode = wave.getCode();
        // 检验odoIndex是否都有值
        long num = odoManager.countOdoIndexIsNull(waveCode, ouId);
        if (num > 0) {
            // 有null值,重新排序
            this.updateOdoIndexByWaveId(waveId, ouId);
        }
        return odoManager.findPrintOdoIdList(wave.getCode(), ouId);
    }


    @Override
    public WhWave findWaveByIdOuId(Long waveId, Long ouId) {
        return this.whWaveManager.findWaveByIdOuId(waveId, ouId);
    }

    @Override
    public void updateOdoIndexByWaveId(Long waveId, Long ouId) {
        // 查找波次中的包含的批次和对应的odoIdList
        Map<String, List<Long>> batchMap = odoManager.getBatchNoOdoIdListGroup(waveId, ouId);
        // 应用打印排序规则
        WhWaveMasterPrintCondition condition = whWaveManager.findPrintConditionByWaveId(waveId, Constants.PRINT_ORDER_TYPE_13, ouId);
        if (null != condition) {
            for (Entry<String, List<Long>> entry : batchMap.entrySet()) {
                String batchNo = entry.getKey();
                String odoIdStr = StringUtils.collectionToCommaDelimitedString(entry.getValue());
                String excuteSql = condition.getPrintSortSql();
                if (StringUtils.isEmpty(excuteSql)) {
                    log.error("excuteSql is null, batchNo:{}, odoId:{}", batchNo, odoIdStr);
                    throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
                }
                excuteSql = condition.getPrintSortSql().replace(Constants.ODOID_LIST_PLACEHOLDER, odoIdStr);
                List<Long> sortOdoList = whWaveManager.excuteSortSql(excuteSql, ouId);
                batchMap.put(batchNo, sortOdoList);
            }
            odoManager.updateOdoIndexByBatch(batchMap, ouId);
        } else {
            // 波次主档未配置打印顺序, 查找打印条件配置顺序
            boolean flag = true;
            Map<String, Map<String, List<Long>>> batchPrintConditionMap = new HashMap<String, Map<String, List<Long>>>();
            List<PrintCondition> printConditionList = printConditionManager.findConditionListByDocType(Constants.PRINT_ORDER_TYPE_13, ouId);
            for (Entry<String, List<Long>> entry : batchMap.entrySet()) {
                String batchNo = entry.getKey();
                Map<String, List<Long>> printMap = printConditionManager.getSortIdList(printConditionList, entry.getValue(), ouId);
                if (null == printMap) {
                    log.error("not have printCondition match to odoList, waveId:{}, batchNo:{}", waveId, batchNo);
                    flag = false;
                    break;
                }
                batchPrintConditionMap.put(batchNo, printMap);
            }
            if (flag) {
                odoManager.updateOdoIndexByBatchExt(batchPrintConditionMap, ouId);
            } else {
                throw new BusinessException(ErrorCodes.WAVE_ODOINDEX_SORT_ERROR);
            }
        }
    }


    @Override
    public WhWork findWorkById(Long workId, Long ouId) {
        return this.whWorkManager.findWorkByWorkId(workId, ouId);
    }


    @Override
    public WhOdodeliveryInfo getLogisticsInfoForCheck(Long odoId, String outboundboxCode, Long consumableSkuId, String logId, Long ouId) {
        WhOdodeliveryInfo info = new WhOdodeliveryInfo();
        List<WhOdodeliveryInfo> infoList = this.whOdoDeliveryInfoManager.findByOdoIdWithoutOutboundbox(odoId, ouId);
        if (null == infoList || infoList.isEmpty()) {
            info = getLogisticsInfoByOdoId(odoId, logId, ouId);
        } else {
            info = infoList.get(0);
        }
        // info.setOutboundboxCode(outboundboxCode);
        // info.setOutboundboxId(consumableSkuId);
        // info = this.whOdoDeliveryInfoManager.saveOrUpdate(info);
        return info;
    }


    @Override
    public WhOdodeliveryInfo bindkWaybillCode(Long odoId, String outboundboxCode, Long consumableSkuId, String waybillCode, String logId, Long ouId) {
        WhOdodeliveryInfo info = new WhOdodeliveryInfo();
        info.setOdoId(odoId);
        info.setWaybillCode(waybillCode);
        info.setOuId(ouId);
        List<WhOdodeliveryInfo> infoList = this.whOdoDeliveryInfoManager.findByParams(info);
        if (null != infoList && !infoList.isEmpty()) {
            info = infoList.get(0);
            info.setOutboundboxCode(outboundboxCode);
            // info.setOutboundboxId(consumableSkuId);
            info = this.whOdoDeliveryInfoManager.saveOrUpdate(info);
        } else {
            WhOdodeliveryInfo whOdodeliveryInfo = new WhOdodeliveryInfo();
            whOdodeliveryInfo.setOdoId(odoId);
            whOdodeliveryInfo.setTransportCode("SF");
            whOdodeliveryInfo.setWaybillCode(waybillCode);
            whOdodeliveryInfo.setOutboundboxCode(outboundboxCode);
            whOdodeliveryInfo.setStatus(1);
            whOdodeliveryInfo.setOuId(ouId);
            whOdodeliveryInfo.setLifecycle(1);
            whOdodeliveryInfo.setCreateTime(new Date());
            whOdodeliveryInfo.setLastModifyTime(new Date());
            this.whOdoDeliveryInfoManager.saveOrUpdate(whOdodeliveryInfo);
        }
        return info;
    }
}
