package com.baozun.scm.primservice.whoperation.manager.pda;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.baozun.redis.manager.CacheManager;
import com.baozun.scm.primservice.whoperation.command.pda.rcvd.RcvdCacheCommand;
import com.baozun.scm.primservice.whoperation.command.pda.rcvd.RcvdContainerAttrCommand;
import com.baozun.scm.primservice.whoperation.command.pda.rcvd.RcvdSnCacheCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnLineCommand;
import com.baozun.scm.primservice.whoperation.command.sku.skucommand.SkuCommand;
import com.baozun.scm.primservice.whoperation.command.sku.skucommand.SkuStandardPackingCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.constant.CacheKeyConstant;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.PoAsnStatus;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.pda.inbound.rcvd.GeneralRcvdManager;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.AsnLineManager;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.AsnManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhFunctionManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhFunctionRcvdManager;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsn;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsnLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFunctionRcvd;

@Service("pdaContainerRcvdManagerProxy")
public class PdaContainerRcvdManagerProxyImpl extends BaseManagerImpl implements PdaContainerRcvdManagerProxy {

    @Autowired
    private AsnManager asnManager;

    @Autowired
    private AsnLineManager asnLineManager;

    @Autowired
    private GeneralRcvdManager generalRcvdManager;

    @Autowired
    private WhFunctionManager whFunctionManager;

    @Autowired
    private WhFunctionRcvdManager whFunctionRcvdManager;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private PdaRcvdManagerProxy pdaRcvdManagerProxy;



    /* 封装asn */

    /**
    * 通过扫描asn的extcode获取asn中的所有商品明细
    */
    // TODO 检验扫描模式 库存类型 库存状态 等
    @Override
    public void initAsnForContainerReceiving(WhSkuInventoryCommand command, Long ouId) {
        if (null == command || null == ouId || null == command.getOccupationId()) {
            throw new BusinessException("参数为空");
        }
        Long asnId = command.getOccupationId();
        WhAsn asn = this.asnManager.findWhAsnByIdToShard(asnId, ouId);
        if (null == asn) {
            throw new BusinessException("没有asn");
        }

        /* 以下asn不能收货 */
        Integer status = asn.getStatus();
        if (PoAsnStatus.ASN_CANCELED == status || PoAsnStatus.ASN_CLOSE == status /*
                                                                                   * || PoAsnStatus.
                                                                                   * ASN_RCVD_FINISH
                                                                                   * == status
                                                                                   */|| PoAsnStatus.ASN_DELETE == status) {
            throw new BusinessException("不能收货");
        }
        /* 开始处理asn,需要先上锁 */
        // Map<Long, Double> qty = new HashMap<Long, Double>();
        try {
            int updateCount = this.asnManager.updateByVersionForLock(asn.getId(), ouId, asn.getLastModifyTime());
            if (1 == updateCount) {
                /* 上锁成功 */
                WhAsnLineCommand wac = new WhAsnLineCommand();
                wac.setAsnId(asn.getId());
                wac.setOuId(ouId);
                WhAsnLine asnDetail = new WhAsnLine();
                BeanUtils.copyProperties(wac, asnDetail);

                List<WhAsnLine> asnLineList = this.asnLineManager.findListByShard(asnDetail);
                if (null == asnLineList || asnLineList.isEmpty()) {
                    throw new BusinessException("没有asn明细");
                }

                Map<Long, Integer> skuMap = new HashMap<Long, Integer>();
                for (WhAsnLine asnline : asnLineList) {// 缓存ASN明细信息
                    /* key field value seconds */
                    cacheManager.setMapObject(CacheKeyConstant.CACHE_ASNLINE_PREFIX + asnId, asnline.getId().toString(), asnline, 24 * 60 * 60);
                    int count = asnline.getQtyPlanned().intValue() - asnline.getQtyRcvd().intValue();// 未收货数量
                    if (0 > count) {
                        throw new BusinessException("数量不正确");
                    }
                    // 缓存ASN-商品数量
                    long asnLineSku = cacheManager.incr(CacheKeyConstant.CACHE_ASNLINE_SKU_PREFIX + asn.getId() + "_" + asnline.getId() + "_" + asnline.getSkuId());
                    cacheManager.decrBy(CacheKeyConstant.CACHE_ASNLINE_SKU_PREFIX + asn.getId() + "_" + asnline.getId() + "_" + asnline.getSkuId(), (int) asnLineSku);
                    cacheManager.incrBy(CacheKeyConstant.CACHE_ASNLINE_SKU_PREFIX + asn.getId() + "_" + asnline.getId() + "_" + asnline.getSkuId(), count);
                    if (skuMap.containsKey(asnline.getSkuId())) {
                        /* 合并相同sku */
                        skuMap.put(asnline.getSkuId(), skuMap.get(asnline.getSkuId()) + count);
                    } else {
                        skuMap.put(asnline.getSkuId(), count);
                    }
                }

                // Asn商品缓存列表
                Iterator<Entry<Long, Integer>> it = skuMap.entrySet().iterator();
                while (it.hasNext()) {
                    Entry<Long, Integer> skuEntry = it.next();
                    long asnSku = cacheManager.incr(CacheKeyConstant.CACHE_ASN_SKU_PREFIX + asn.getId() + "_" + skuEntry.getKey());
                    cacheManager.decrBy(CacheKeyConstant.CACHE_ASN_SKU_PREFIX + asn.getId() + "_" + skuEntry.getKey(), (int) asnSku);
                    cacheManager.incrBy(CacheKeyConstant.CACHE_ASN_SKU_PREFIX + asn.getId() + "_" + skuEntry.getKey(), skuEntry.getValue());
                }
                cacheManager.setObject(CacheKeyConstant.CACHE_ASN_PREFIX + asnId, asn, 24 * 60 * 60);
                this.asnManager.updateByVersionForUnLock(asnId, ouId);
            } else {
                throw new BusinessException(ErrorCodes.ASN_CACHE_ERROR);
            }
        } catch (Exception e) {
            cacheManager.remove(CacheKeyConstant.CACHE_ASN_PREFIX + asnId);
            throw e;
        }
    }

    /**
    * 处理每个sku数量
    * @param asnLine
    */
    private void handleQtyPerAsnLine(WhAsnLine asnLine, Map<Long, Double> qty) {
        Double cnt = qty.get(asnLine.getSkuId());
        if (null == cnt) {
            // 如果map中没有这个sku
            qty.put(asnLine.getSkuId(), asnLine.getQtyPlanned());
        } else {
            // 如果map中有sku
            cnt += asnLine.getQtyPlanned();
            qty.put(asnLine.getSkuId(), cnt);
        }
    }

    @Override
    public List<SkuStandardPackingCommand> getContainerNumber(String skuBarCode, Double qty, Long ouId) {
        List<SkuStandardPackingCommand> list = this.generalRcvdManager.findSkuStandardPacking(skuBarCode, ouId, null);
        for (SkuStandardPackingCommand command : list) {
            /* 向上取整数, 得出至少需要多少容器数 */
            Double res = Math.ceil(qty / command.getQuantity());
            command.setCount(res.longValue());
        }
        return list;
    }

    @Override
    public SkuCommand checkAsnSku(Long asnId, String occupationCode, String skuCode, Long ouId) {

        WhAsn asn = cacheManager.getObject(CacheKeyConstant.CACHE_ASN_PREFIX + asnId);
        Long customerId = asn.getCustomerId();
        SkuCommand sku = this.generalRcvdManager.findSkuBySkuCodeOuId(skuCode, ouId, customerId);
        if (null == sku) {
            throw new BusinessException("没有找到sku");
        }
        // String asnSkuCount = cacheManager.getValue(CacheKeyConstant.CACHE_ASN_SKU_PREFIX + asnId
        // + "_" + sku.getId());
        // if (StringUtils.isEmpty(asnSkuCount)) {
        // throw new BusinessException(ErrorCodes.SKU_CACHE_ERROR);
        // }
        // List<SkuStandardPackingCommand> sspList =
        // this.generalRcvdManager.checkSkuStandardPacking(sku.getBarCode(), ouId, null);
        //
        // for (SkuStandardPackingCommand ssp : sspList) {
        // /* 把每个sku需要的箱数放在skustandardpacking中 */
        // if (0 != ssp.getQuantity()) {
        // Double res = Math.ceil(Double.parseDouble(asnSkuCount) / ssp.getQuantity());
        // ssp.setCount(res.longValue());
        // }
        // // ssp.setAsnSkuCount(Long.parseLong(asnSkuCount));
        // }
        return sku;

        /* 扫描的sku在asn中 */


        // String lineIdListStr = "";
        // try {
        // lineIdListStr = this.pdaRcvdManagerProxy.getMatchLineListStr(command);
        // } catch (BusinessException e) {
        // throw e;
        // }
        // /* 可能匹配的asnline ids */
        // command.setLineIdListString(lineIdListStr.substring(0, lineIdListStr.length() - 1));

        // boolean flag = this.asnLineManager.checkAsnSku(occupationCode, skuCode, ouId);
        // cacheManager.getValue(CacheKeyConstant.CACHE_ASN_SKU_PREFIX + asnId + "_" +
        // command.getSkuId());
        // return flag;
    }

    /**
    * 返回容器装箱数
    */
    @Override
    public ContainerCommand checkContainer(WhSkuInventoryCommand command) {
        /* sku支持的容器类型以'/'划分 从页面传到后台 */
        // String[] types = containerTypes.split("/");
        // List<String> typeList = new ArrayList<String>();
        // for (String type : types) {
        // typeList.add(type);
        // }

        ContainerCommand containerCommand = this.generalRcvdManager.findContainer(command.getSkuId(), command.getInsideContainerCode(), command.getOuId(), command.getContainerType());
        // 初始化容器缓存
        // this.pdaRcvdManagerProxy.initSkuAttrFromInventoryForCacheContainer(command, 119L);

        return containerCommand;
    }

    @Override
    public boolean checkFunc(Long funcId, Long ouId) {
        WhFunctionRcvd wfr = whFunctionRcvdManager.findwFunctionRcvdByFunctionId(funcId, ouId);
        boolean isPoint = wfr.getIsInvattrAsnPointoutUser();
        return isPoint;
    }

    @Override
    public void doReceive(WhSkuInventoryCommand command, Long number) {
        // TODO Auto-generated method stub

    }

    @Override
    public String handleUrl(int index) {
        // 指针列表：
        // 0：是否管理效期
        // 1:是否管理批次号
        // 2:是否管理原产地
        // 3:是否管理库存类型
        // 4:是否管理库存属性1
        // 5:是否管理库存属性2
        // 6:是否管理库存属性3
        // 7:是否管理库存属性4
        // 8:是否管理库存属性5
        // 9:是否管理库存状态
        // 10:残次品类型及残次原因
        // 11:是否管理序列号
        String url = Constants.ATTR_CONTROL_HEADER;
        switch (index) {
            case 0:
                url += Constants.VALID_DATE;
                break;
            case 1:
                url += Constants.BATCH_NO;
                break;
            case 2:
                url += Constants.COUNTRY;
                break;
            case 3:
                url += Constants.INV_TYPE;
                break;
            case 4:
                url += Constants.INV_ATTR_1;
                break;
            case 5:
                url += Constants.INV_ATTR_2;
                break;
            case 6:
                url += Constants.INV_ATTR_3;
                break;
            case 7:
                url += Constants.INV_ATTR_4;
                break;
            case 8:
                url += Constants.INV_ATTR_5;
                break;
            case 9:
                url += Constants.INV_STATUS;
                break;
            case 10:
                url += Constants.DEFECTIVE;
                break;
            case 11:
                url += Constants.SN;
                break;
            default:
                url += Constants.VALID_DATE;
        }
        return url;
    }

    @Override
    public WhSkuInventoryCommand returnCommand(WhSkuInventoryCommand command) {
        WhSkuInventoryCommand wsic = compileCommand(command);
        String lineIdListStr = pdaRcvdManagerProxy.getMatchLineListStr(wsic);
        command.setLineIdListString(lineIdListStr);
        return command;
    }

    @Override
    public WhSkuInventoryCommand dispatchAttrCheck(RcvdContainerAttrCommand rca) {
        Integer currUrl = rca.getCurrUrl();
        WhSkuInventoryCommand command = new WhSkuInventoryCommand();
        if (null != rca.getInvStatus() && !StringUtils.isEmpty(rca.getInvStatus())) {
            command.setInvStatus(Long.parseLong(rca.getInvStatus()));
        }
        BeanUtils.copyProperties(rca, command);
        switch (currUrl) {
            case Constants.RCVD_ISVALID:
                // 管理效期
                command = this.checkValidDate(command);
                break;
            case Constants.RCVD_ISBATCHNO:
                // 管理批次号
                command = this.checkBatchNo(command);
                break;
            case Constants.RCVD_ISCOUNTRYOFORIGIN:
                // 管理原产地
                command = this.checkCountry(command);
                break;
            case Constants.RCVD_ISINVTYPE:
                // 管理库存类型
                command = this.checkInvType(command);
                break;
            case Constants.RCVD_INVATTR1:
                // 管理库存属性1
                // command = this.checkInvAttr1(command);
                break;
            case Constants.RCVD_INVATTR2:
                // 管理库存属性2
                // command = this.checkInvAttr2(command);
                break;
            case Constants.RCVD_INVATTR3:
                // 管理库存属性3
                // command = this.checkInvAttr3(command);
                break;
            case Constants.RCVD_INVATTR4:
                // 管理库存属性4
                // command = this.checkInvAttr4(command);
                break;
            case Constants.RCVD_INVATTR5:
                // 管理库存属性5
                command = this.checkInvAttr(command);
                break;
            case Constants.RCVD_ISINVSTATUS:
                // 管理库存状态
                command = this.checkInvStatus(command);
                break;
            case Constants.RCVD_ISDEFEAT:
                // 残次品类型及残次原因
                command = this.checkDefective(command);
                break;
            case Constants.RCVD_ISSERIALNUMBER:
                // 管理序列号
                // 如果有管理序列号,在方法中会判断是否有序列号缓存
                command = this.checkSn(command, rca.getSn());
                break;
            default:
                // 默认暂时用这个
                command = this.checkValidDate(command);
                break;
        }
        // 匹配明细行
        // 根据容器属性和asnline的属性做比较 匹配的记入list
        // 取消获取明细行 start
        WhSkuInventoryCommand wsic = compileCommand(command);
        wsic.setSkuUrlOperator(rca.getCurrUrl());
        wsic.setSkuUrl(rca.getSkuUrl());
        String lineIdListStr = pdaRcvdManagerProxy.getMatchLineListStr(wsic);
        command.setLineIdListString(lineIdListStr);
        // 取消获取明细行 end
        // 把匹配到的明细行放到对象
        // 对每个属性管控进行校验
        // 匹配明细行
        // 先把容器放到缓存,再把扫描的属性放到容器属性中
        // cacheScanedSkuSnWhenGeneralRcvd 初始化容器和sn缓存
        // 这里不应该进行缓存的更新
        // this.pdaRcvdManagerProxy.cacheScanedSkuWhenGeneralRcvd(command);
        // 收货完成一次
        return command;
    }

    @Override
    public WhSkuInventoryCommand checkValidDate(WhSkuInventoryCommand command) {
        String mfgDate = command.getMfgDateStr();
        String expDate = command.getExpDateStr();
        Boolean res = this.generalRcvdManager.skuDateCheck(command.getSkuId(), command.getOuId(), mfgDate, expDate);
        if (!res) {
            throw new BusinessException("输入的日期不能收货");
        }
        return command;
    }

    @Override
    public WhSkuInventoryCommand checkBatchNo(WhSkuInventoryCommand command) {
        // if (!command.getIsInvattrDiscrepancyAllowrcvd()) {
        // boolean flag = discrepancyNoAllowrcvd(command);
        // if (!flag) {
        // throw new BusinessException("缓存错误");
        // }
        // }
        return command;
    }

    @Override
    public WhSkuInventoryCommand checkCountry(WhSkuInventoryCommand command) {
        // if (!command.getIsInvattrDiscrepancyAllowrcvd()) {
        // boolean flag = discrepancyNoAllowrcvd(command);
        // if (!flag) {
        // throw new BusinessException("缓存错误");
        // }
        // }
        return command;
    }

    @Override
    public WhSkuInventoryCommand checkInvType(WhSkuInventoryCommand command) {
        // if (!command.getIsInvattrDiscrepancyAllowrcvd()) {
        // boolean flag = discrepancyNoAllowrcvd(command);
        // if (!flag) {
        // throw new BusinessException("缓存错误");
        // }
        // }
        return command;
    }

    @Override
    public WhSkuInventoryCommand checkInvAttr(WhSkuInventoryCommand command) {
        // if (!command.getIsInvattrDiscrepancyAllowrcvd()) {
        // boolean flag = discrepancyNoAllowrcvd(command);
        // if (!flag) {
        // throw new BusinessException("缓存错误");
        // }
        // }
        return command;
    }

    @Override
    public WhSkuInventoryCommand checkInvStatus(WhSkuInventoryCommand command) {
        // if (!command.getIsInvattrDiscrepancyAllowrcvd()) {
        // boolean flag = discrepancyNoAllowrcvd(command);
        // if (!flag) {
        // throw new BusinessException("缓存错误");
        // }
        // }
        return command;
    }

    @Override
    public WhSkuInventoryCommand checkDefective(WhSkuInventoryCommand command) {
        // if (!command.getIsInvattrDiscrepancyAllowrcvd()) {
        // boolean flag = discrepancyNoAllowrcvd(command);
        // if (!flag) {
        // throw new BusinessException("缓存错误");
        // }
        // }
        return command;
    }

    /**
    * 管理sn号 需要先计算一共要收多少货 逐件扫描sn号 如果sn号重复就报错
    */
    @Override
    public WhSkuInventoryCommand checkSn(WhSkuInventoryCommand command, String sn) {

        // if (!command.getIsInvattrDiscrepancyAllowrcvd()) {
        // boolean flag = discrepancyNoAllowrcvd(command);
        // if (!flag) {
        // throw new BusinessException("缓存错误");
        // }
        // }
        // // 如果是管控sn属性商品,需要把sn号放进缓存
        // // 把sn号放入RcvdSnCacheCommand对象
        // command.getSn().setSn(sn);
        String serialNumberType = command.getSerialNumberType();
        String serialNumber = command.getSerialNumber();
        if (null != command.getSn()) {
            throw new BusinessException("sn error");
        }
        RcvdSnCacheCommand rcvdCommand = new RcvdSnCacheCommand();
        rcvdCommand.setSerialNumberType(serialNumberType);
        rcvdCommand.setSn(serialNumber);
        command.setSn(rcvdCommand);
        // pdaRcvdManagerProxy.cacheScanedSkuSnWhenGeneralRcvd(command, 1);
        // cacheSnForContainerRcvd(command);
        return command;
    }

    public void cacheSnForContainerRcvd(RcvdContainerAttrCommand command) {
        List<RcvdSnCacheCommand> cacheSn = this.cacheManager.getObject(CacheKeyConstant.CACHE_RCVD_SN_PREFIX + command.getUserId());
        if (null == cacheSn) {
            cacheSn = new ArrayList<RcvdSnCacheCommand>();
        }
        WhSkuInventoryCommand wsic = new WhSkuInventoryCommand();
        BeanUtils.copyProperties(command, wsic);
        // cacheSn.add(rcvdSn);
        this.cacheManager.setObject(CacheKeyConstant.CACHE_RCVD_SN_PREFIX + command.getUserId(), cacheSn, 60 * 60);
    }

    // 不允许差异收货
    @Override
    public boolean discrepancyNoAllowrcvd(WhSkuInventoryCommand command) {
        // 拿到缓存中的asn line
        Long asnId = command.getOccupationId();
        Long skuId = command.getSkuId();
        WhAsnLine line = cacheManager.getMapObject(CacheKeyConstant.CACHE_ASNLINE_PREFIX + asnId, skuId.toString());
        if (null == line) {
            throw new BusinessException("缓存错误");
        }

        // 校验生产日期
        Date mfgDate = command.getMfgDate();
        if (null != mfgDate && 0 != mfgDate.compareTo(line.getMfgDate())) {
            throw new BusinessException("缓存错误");
        }

        // 校验失效日期
        Date expDate = command.getExpDate();
        if (null != expDate && 0 != mfgDate.compareTo(line.getExpDate())) {
            throw new BusinessException("缓存错误");
        }

        // 校验批次号
        String batchNumber = command.getBatchNumber();
        if (null != batchNumber && !batchNumber.equalsIgnoreCase(line.getBatchNo())) {
            throw new BusinessException("缓存错误");
        }

        // 校验原产地
        String countryOfOrigin = command.getCountryOfOrigin();
        if (null != countryOfOrigin && !countryOfOrigin.equalsIgnoreCase(line.getCountryOfOrigin())) {
            throw new BusinessException("缓存错误");
        }

        // 校验库存类型
        String invType = command.getInvType();
        if (null != invType && !invType.equalsIgnoreCase(line.getInvType())) {
            throw new BusinessException("缓存错误");
        }

        // 校验库存属性1
        String invAttr1 = command.getInvAttr1();
        if (null != invAttr1 && !invAttr1.equalsIgnoreCase(line.getInvAttr1())) {
            throw new BusinessException("缓存错误");
        }

        // 校验库存属性2
        String invAttr2 = command.getInvAttr2();
        if (null != invAttr2 && !invAttr2.equalsIgnoreCase(line.getInvAttr2())) {
            throw new BusinessException("缓存错误");
        }

        // 校验库存属性3
        String invAttr3 = command.getInvAttr3();
        if (null != invAttr3 && !invAttr3.equalsIgnoreCase(line.getInvAttr3())) {
            throw new BusinessException("缓存错误");
        }

        // 校验库存属性4
        String invAttr4 = command.getInvAttr4();
        if (null != invAttr4 && !invAttr4.equalsIgnoreCase(line.getInvAttr4())) {
            throw new BusinessException("缓存错误");
        }

        // 校验库存属性5
        String invAttr5 = command.getInvAttr5();
        if (null != invAttr5 && !invAttr5.equalsIgnoreCase(line.getInvAttr5())) {
            throw new BusinessException("缓存错误");
        }

        // 校验库存状态
        Long invStatus = command.getInvStatus();
        if (null != invStatus && !invStatus.equals(line.getInvStatus())) {
            throw new BusinessException("缓存错误");
        }

        // 校验残次品类型及残次原因
        return true;
    }

    @Override
    public boolean cacheRcvd(RcvdContainerAttrCommand command) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public List<SkuStandardPackingCommand> getContainerType(Long skuId, Long ouId) {
        List<SkuStandardPackingCommand> list = this.generalRcvdManager.getContainerType(skuId, ouId);
        return list;
    }

    @Override
    public void completeScanning(WhSkuInventoryCommand command) {
        WhSkuInventoryCommand newNommand = compileCommand(command);
        String lineIds = pdaRcvdManagerProxy.initMatchedLineIdStr(newNommand);
        command.setLineIdListString(lineIds);
        this.pdaRcvdManagerProxy.cacheScanedSkuWhenGeneralRcvd(command);
    }

    private WhSkuInventoryCommand compileCommand(WhSkuInventoryCommand command) {
        String functionUrl = command.getFunctionUrl().substring(1);
        if (StringUtils.isEmpty(functionUrl)) {
            throw new BusinessException("function url error");
        }
        WhFunctionRcvd rcvd = new WhFunctionRcvd();
        String[] functionParam = functionUrl.split("-");
        if (null != functionParam[0] && !StringUtils.isEmpty(functionParam[0]) && !"null".equalsIgnoreCase(functionParam[0])) {
            rcvd.setRcvdPattern(Integer.parseInt(functionParam[0]));
        }
        if (null != functionParam[1] && !StringUtils.isEmpty(functionParam[1]) && !"null".equalsIgnoreCase(functionParam[1])) {
            rcvd.setScanPattern(Integer.parseInt(functionParam[1]));
        }
        if (null != functionParam[2] && !StringUtils.isEmpty(functionParam[2]) && !"null".equalsIgnoreCase(functionParam[2])) {
            rcvd.setInvType(functionParam[2]);
        }
        if (null != functionParam[3] && !StringUtils.isEmpty(functionParam[3]) && !"null".equalsIgnoreCase(functionParam[3])) {
            rcvd.setInvStatus(Long.parseLong(functionParam[3]));
        }
        if (null != functionParam[4] && !StringUtils.isEmpty(functionParam[4]) && !"null".equalsIgnoreCase(functionParam[4])) {
            rcvd.setNormIncPointoutRcvd(Boolean.parseBoolean(functionParam[4]));
        }
        if (null != functionParam[5] && !StringUtils.isEmpty(functionParam[5]) && !"null".equalsIgnoreCase(functionParam[5])) {
            rcvd.setIsInvattrAsnPointoutUser(Boolean.parseBoolean(functionParam[5]));
        }
        if (null != functionParam[6] && !StringUtils.isEmpty(functionParam[6]) && !"null".equalsIgnoreCase(functionParam[6])) {
            rcvd.setIsInvattrDiscrepancyAllowrcvd(Boolean.parseBoolean(functionParam[6]));
        }
        command.setRcvd(rcvd);
        return command;
    }

    @Override
    public String getAsnSkuCount(Long occupationId, Long skuId) {
        String asnSkuCount = cacheManager.getValue(CacheKeyConstant.CACHE_ASN_SKU_PREFIX + occupationId + CacheKeyConstant.CACHE_KEY_SPLIT + skuId);
        return asnSkuCount;
    }

    @Override
    public Set<String> getLineSet(Long occupationId) {
        Set<String> lineIdSet = this.cacheManager.getAllMap(CacheKeyConstant.CACHE_ASNLINE_PREFIX + occupationId).keySet();
        return lineIdSet;
    }

    @Override
    public WhAsnLine getAsnLine(Long occupationId, String lineId) {
        WhAsnLine asnLine = this.cacheManager.getMapObject(CacheKeyConstant.CACHE_ASNLINE_PREFIX + occupationId, lineId);
        return asnLine;
    }

    @Override
    public List<RcvdSnCacheCommand> getCacheSn(String userId) {
        List<RcvdSnCacheCommand> cacheSn = this.cacheManager.getObject(CacheKeyConstant.CACHE_RCVD_SN_PREFIX + userId);
        return cacheSn;
    }

    @Override
    public List<RcvdCacheCommand> getRcvdCacheCommandList(String userId) {
        List<RcvdCacheCommand> list = this.cacheManager.getObject(CacheKeyConstant.CACHE_RCVD_PREFIX + userId);
        return list;
    }

    @Override
    public void cancelOperation(WhSkuInventoryCommand command, List<RcvdCacheCommand> list, String userId, Long ouId) {
        // 撤销缓存
        // 1.SN缓存
        // this.cacheManager.removeMapValue(CacheKeyConstant.CACHE_RCVD_SN, userId);
        if (null != list) {
            // 发生异常抛出。回滚数据。
            for (RcvdCacheCommand rcvd : list) {
                try {
                    this.cacheManager.incrBy(CacheKeyConstant.CACHE_ASNLINE_SKU_PREFIX + rcvd.getOccupationId() + CacheKeyConstant.CACHE_KEY_SPLIT + rcvd.getLineId() + CacheKeyConstant.CACHE_KEY_SPLIT + rcvd.getSkuId(), rcvd.getSkuBatchCount());
                } catch (Exception e) {
                    this.cacheManager.decrBy(CacheKeyConstant.CACHE_ASNLINE_SKU_PREFIX + rcvd.getOccupationId() + CacheKeyConstant.CACHE_KEY_SPLIT + rcvd.getLineId() + CacheKeyConstant.CACHE_KEY_SPLIT + rcvd.getSkuId(), rcvd.getSkuBatchCount());
                    throw e;
                }
                try {
                    String asnSkuCount1 = cacheManager.getValue(CacheKeyConstant.CACHE_ASN_SKU_PREFIX + rcvd.getOccupationId() + CacheKeyConstant.CACHE_KEY_SPLIT + rcvd.getSkuId());
                    System.out.println(asnSkuCount1);
                    this.cacheManager.incrBy(CacheKeyConstant.CACHE_ASN_SKU_PREFIX + rcvd.getOccupationId() + CacheKeyConstant.CACHE_KEY_SPLIT + rcvd.getSkuId(), rcvd.getSkuBatchCount());
                    String asnSkuCount2 = cacheManager.getValue(CacheKeyConstant.CACHE_ASN_SKU_PREFIX + rcvd.getOccupationId() + CacheKeyConstant.CACHE_KEY_SPLIT + rcvd.getSkuId());
                    System.out.println(asnSkuCount2);
                } catch (Exception e) {
                    this.cacheManager.decrBy(CacheKeyConstant.CACHE_ASNLINE_SKU_PREFIX + rcvd.getOccupationId() + CacheKeyConstant.CACHE_KEY_SPLIT + rcvd.getLineId() + CacheKeyConstant.CACHE_KEY_SPLIT + rcvd.getSkuId(), rcvd.getSkuBatchCount());
                    this.cacheManager.decrBy(CacheKeyConstant.CACHE_ASN_SKU_PREFIX + rcvd.getOccupationId() + CacheKeyConstant.CACHE_KEY_SPLIT + rcvd.getSkuId(), rcvd.getSkuBatchCount());
                    throw e;
                }
            }

        }
        if (null != command.getInsideContainerId()) {
            // 释放容器
            this.pdaRcvdManagerProxy.revokeContainer(command.getInsideContainerId(), ouId, Long.parseLong(userId));
            // 清除容器-用户缓存
            this.cacheManager.remove(CacheKeyConstant.CACHE_RCVD_CONTAINER_USER_PREFIX + command.getInsideContainerId());
            // 清除容器-商品缓存
            this.cacheManager.remove(CacheKeyConstant.CACHE_RCVD_CONTAINER_PREFIX + command.getInsideContainerId());
        }
        // 2.CACHE_RCVD中UserId对应缓存
        this.cacheManager.remove(CacheKeyConstant.CACHE_RCVD_PREFIX + userId);
        this.cacheManager.remove(CacheKeyConstant.CACHE_RCVD_SN_PREFIX + userId);
        // 清除托盘-货箱缓存
        if (command.getOuterContainerId() != null) {
            this.cacheManager.popListHead(CacheKeyConstant.CACHE_RCVD_PALLET_PREFIX + command.getOuterContainerId());
        }

    }

    @Override
    public void setCacheSn(String userId, List<RcvdSnCacheCommand> cacheSn) {
        this.cacheManager.setObject(CacheKeyConstant.CACHE_RCVD_SN_PREFIX + userId, cacheSn, 24 * 60 * 60);
    }

    @Override
    public void setCache(String userId, List<RcvdCacheCommand> list) {
        this.cacheManager.setObject(CacheKeyConstant.CACHE_RCVD_PREFIX + userId, list, 24 * 60 * 60);
    }

    @Override
    public SkuStandardPackingCommand getContainerQty(Long skuId, Long ouId, Long containerType) {
        SkuStandardPackingCommand skuStandardPackingCommand = this.generalRcvdManager.getContainerQty(skuId, ouId, containerType);
        return skuStandardPackingCommand;
    }
}
