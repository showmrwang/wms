/**
 * Copyright (c) 2013 Baozun All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Baozun. You shall not disclose
 * such Confidential Information and shall use it only in accordance with the terms of the license
 * agreement you entered into with Baozun.
 * <p/>
 * BAOZUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. BAOZUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES.
 */

package com.baozun.scm.primservice.whoperation.manager.odo.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import lark.common.annotation.MoreDB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.baseservice.sac.manager.CodeManager;
import com.baozun.scm.primservice.whoperation.command.odo.OdoCommand;
import com.baozun.scm.primservice.whoperation.command.odo.WhOdoOutBoundBoxCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.Container2ndCategoryCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.OutInvBoxTypeCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.UomCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.constant.WavePhase;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoOutBoundBoxDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.Container2ndCategoryDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerAssistDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.UomDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOutInventoryboxRelationshipDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryTobefilledDao;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.odo.wave.WhWaveManager;
import com.baozun.scm.primservice.whoperation.manager.system.GlobalLogManager;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoOutBoundBox;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container2ndCategory;
import com.baozun.scm.primservice.whoperation.model.warehouse.ContainerAssist;
import com.baozun.scm.primservice.whoperation.model.warehouse.Warehouse;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutInventoryboxRelationship;

@Service("outboundBoxRecManager")
@Transactional
public class OutboundBoxRecManagerImpl extends BaseManagerImpl implements OutboundBoxRecManager {
    public static final Logger log = LoggerFactory.getLogger(OutboundBoxRecManagerImpl.class);

    @Autowired
    private WhWaveManager whWaveManager;

    @Autowired
    private GlobalLogManager globalLogManager;

    @Autowired
    private ContainerDao containerDao;

    @Autowired
    private Container2ndCategoryDao container2ndCategoryDao;

    @Autowired
    private CodeManager codeManager;

    @Autowired
    private UomDao uomDao;

    @Autowired
    private ContainerAssistDao containerAssistDao;

    @Autowired
    private WhOutInventoryboxRelationshipDao whOutInventoryboxRelationshipDao;

    @Autowired
    private WhOdoOutBoundBoxDao odoOutBoundBoxDao;

    @Autowired
    private WhSkuInventoryDao whSkuInventoryDao;

    @Autowired
    private WhSkuInventoryTobefilledDao whSkuInventoryTobefilledDao;


    /**
     * 获取批次号
     *
     * @author mingwei.xie
     * @param ouId
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public String getBatchNo(Long ouId) {
        // 设置编码服务
        String batchCode = codeManager.generateCode(Constants.WMS, Constants.OUTBOUND_BOX_BATCH, null, null, null);
        return batchCode;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<ContainerAssist> findContainerAssistById(List<Long> containerIdList, Long ouId) {
        return containerAssistDao.findListByContainerId(containerIdList, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void saveRecOutboundBoxByOdo(OdoCommand odoCommand) {
        // 出库单分配的出库箱列表
        List<OutInvBoxTypeCommand> odoPackedOutboundBoxList = odoCommand.getOutboundBoxList();
        // 出库单分配的整箱容器列表
        List<ContainerCommand> odoPackedWholeCaseList = odoCommand.getWholeCaseList();
        // 出库单分配的整托容器列表
        List<ContainerCommand> odoPackedWholeTrayList = odoCommand.getWholeTrayList();

        if (null != odoPackedOutboundBoxList && !odoPackedOutboundBoxList.isEmpty()) {
            for (OutInvBoxTypeCommand outboundBoxType : odoPackedOutboundBoxList) {
                // 出库箱中的包裹，按照商品划分
                for (WhOdoOutBoundBoxCommand odoOutBoundBoxCommand : outboundBoxType.getOdoOutBoundBoxCommandList()) {
                    WhOdoOutBoundBox odoOutBoundBox = new WhOdoOutBoundBox();
                    BeanUtils.copyProperties(odoOutBoundBoxCommand, odoOutBoundBox);

                    odoOutBoundBox.setCreateTime(new Date());
                    odoOutBoundBox.setLastModifyTime(new Date());
                    odoOutBoundBoxDao.insert(odoOutBoundBox);
                }
            }
        }

        if (null != odoPackedWholeCaseList && !odoPackedWholeCaseList.isEmpty()) {
            for (ContainerCommand container : odoPackedWholeCaseList) {
                // 整箱中的包裹
                for (WhOdoOutBoundBoxCommand odoOutBoundBoxCommand : container.getOdoOutboundBoxCommandList()) {
                    WhOdoOutBoundBox odoOutBoundBox = new WhOdoOutBoundBox();
                    BeanUtils.copyProperties(odoOutBoundBoxCommand, odoOutBoundBox);

                    odoOutBoundBox.setCreateTime(new Date());
                    odoOutBoundBox.setLastModifyTime(new Date());
                    odoOutBoundBoxDao.insert(odoOutBoundBox);
                }
            }
        }

        if (null != odoPackedWholeTrayList && !odoPackedWholeTrayList.isEmpty()) {
            for (ContainerCommand container : odoPackedWholeTrayList) {
                // 整箱中的包裹
                for (WhOdoOutBoundBoxCommand odoOutBoundBoxCommand : container.getOdoOutboundBoxCommandList()) {
                    WhOdoOutBoundBox odoOutBoundBox = new WhOdoOutBoundBox();
                    BeanUtils.copyProperties(odoOutBoundBoxCommand, odoOutBoundBox);

                    odoOutBoundBox.setCreateTime(new Date());
                    odoOutBoundBox.setLastModifyTime(new Date());
                    odoOutBoundBoxDao.insert(odoOutBoundBox);
                }
            }
        }

    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void saveRecOutboundBoxForTrolleyPackedOdo(List<WhOdoOutBoundBoxCommand> odoOutBoundBoxCommandList) {
        if (null != odoOutBoundBoxCommandList && !odoOutBoundBoxCommandList.isEmpty()) {
            // 出库箱中的包裹，按照商品划分
            for (WhOdoOutBoundBoxCommand odoOutBoundBoxCommand : odoOutBoundBoxCommandList) {
                WhOdoOutBoundBox odoOutBoundBox = new WhOdoOutBoundBox();
                BeanUtils.copyProperties(odoOutBoundBoxCommand, odoOutBoundBox);

                odoOutBoundBox.setCreateTime(new Date());
                odoOutBoundBox.setLastModifyTime(new Date());
                odoOutBoundBoxDao.insert(odoOutBoundBox);
            }
        }

    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void saveRecOutboundBoxByContainer(List<Container2ndCategoryCommand> containerList) {
        for (Container2ndCategoryCommand container : containerList) {
            // 整箱中的包裹
            for (WhOdoOutBoundBoxCommand odoOutBoundBoxCommand : container.getOdoOutBoundBoxCommandList()) {
                WhOdoOutBoundBox odoOutBoundBox = new WhOdoOutBoundBox();
                BeanUtils.copyProperties(odoOutBoundBoxCommand, odoOutBoundBox);

                odoOutBoundBox.setCreateTime(new Date());
                odoOutBoundBox.setLastModifyTime(new Date());
                odoOutBoundBoxDao.insert(odoOutBoundBox);
            }
        }

    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void saveRecOutboundBoxForWholeCaseOdo(List<Container2ndCategoryCommand> turnoverBoxList, List<ContainerCommand> odoPackedWholeCaseList, List<ContainerCommand> odoPackedWholeTrayList) {
        if (null != turnoverBoxList) {
            for (Container2ndCategoryCommand container : turnoverBoxList) {
                // 整箱中的包裹
                for (WhOdoOutBoundBoxCommand odoOutBoundBoxCommand : container.getOdoOutBoundBoxCommandList()) {
                    WhOdoOutBoundBox odoOutBoundBox = new WhOdoOutBoundBox();
                    BeanUtils.copyProperties(odoOutBoundBoxCommand, odoOutBoundBox);

                    odoOutBoundBox.setCreateTime(new Date());
                    odoOutBoundBox.setLastModifyTime(new Date());
                    odoOutBoundBoxDao.insert(odoOutBoundBox);
                }
            }
        }
        if (null != odoPackedWholeCaseList && !odoPackedWholeCaseList.isEmpty()) {
            for (ContainerCommand container : odoPackedWholeCaseList) {
                // 整箱中的包裹
                for (WhOdoOutBoundBoxCommand odoOutBoundBoxCommand : container.getOdoOutboundBoxCommandList()) {
                    WhOdoOutBoundBox odoOutBoundBox = new WhOdoOutBoundBox();
                    BeanUtils.copyProperties(odoOutBoundBoxCommand, odoOutBoundBox);

                    odoOutBoundBox.setCreateTime(new Date());
                    odoOutBoundBox.setLastModifyTime(new Date());
                    odoOutBoundBoxDao.insert(odoOutBoundBox);
                }
            }
        }

        if (null != odoPackedWholeTrayList && !odoPackedWholeTrayList.isEmpty()) {
            for (ContainerCommand container : odoPackedWholeTrayList) {
                // 整箱中的包裹
                for (WhOdoOutBoundBoxCommand odoOutBoundBoxCommand : container.getOdoOutboundBoxCommandList()) {
                    WhOdoOutBoundBox odoOutBoundBox = new WhOdoOutBoundBox();
                    BeanUtils.copyProperties(odoOutBoundBoxCommand, odoOutBoundBox);

                    odoOutBoundBox.setCreateTime(new Date());
                    odoOutBoundBox.setLastModifyTime(new Date());
                    odoOutBoundBoxDao.insert(odoOutBoundBox);
                }
            }
        }

    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void saveRecOutboundBoxForSeedBatch(List<Container2ndCategoryCommand> turnoverBoxList, List<ContainerCommand> odoPackedWholeCaseList, List<ContainerCommand> odoPackedWholeTrayList) {
        if (null != turnoverBoxList) {
            for (Container2ndCategoryCommand container : turnoverBoxList) {
                // 整箱中的包裹
                for (WhOdoOutBoundBoxCommand odoOutBoundBoxCommand : container.getOdoOutBoundBoxCommandList()) {
                    WhOdoOutBoundBox odoOutBoundBox = new WhOdoOutBoundBox();
                    BeanUtils.copyProperties(odoOutBoundBoxCommand, odoOutBoundBox);

                    odoOutBoundBox.setCreateTime(new Date());
                    odoOutBoundBox.setLastModifyTime(new Date());
                    odoOutBoundBoxDao.insert(odoOutBoundBox);
                }
            }
        }
        if (null != odoPackedWholeCaseList && !odoPackedWholeCaseList.isEmpty()) {
            for (ContainerCommand container : odoPackedWholeCaseList) {
                // 整箱中的包裹
                for (WhOdoOutBoundBoxCommand odoOutBoundBoxCommand : container.getOdoOutboundBoxCommandList()) {
                    WhOdoOutBoundBox odoOutBoundBox = new WhOdoOutBoundBox();
                    BeanUtils.copyProperties(odoOutBoundBoxCommand, odoOutBoundBox);

                    odoOutBoundBox.setCreateTime(new Date());
                    odoOutBoundBox.setLastModifyTime(new Date());
                    odoOutBoundBoxDao.insert(odoOutBoundBox);
                }
            }
        }

        if (null != odoPackedWholeTrayList && !odoPackedWholeTrayList.isEmpty()) {
            for (ContainerCommand container : odoPackedWholeTrayList) {
                // 整箱中的包裹
                for (WhOdoOutBoundBoxCommand odoOutBoundBoxCommand : container.getOdoOutboundBoxCommandList()) {
                    WhOdoOutBoundBox odoOutBoundBox = new WhOdoOutBoundBox();
                    BeanUtils.copyProperties(odoOutBoundBoxCommand, odoOutBoundBox);

                    odoOutBoundBox.setCreateTime(new Date());
                    odoOutBoundBox.setLastModifyTime(new Date());
                    odoOutBoundBoxDao.insert(odoOutBoundBox);
                }
            }
        }

    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public ContainerCommand findContainerById(Long containerId, Long ouId) {
        return containerDao.findContainerCommandByIdExt(containerId, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<Container> findUseAbleContainerByContainerType(Container container) {
        return containerDao.findListByParam(container);
    }
    
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<Container> findContainerListByParam(Container container) {
        return containerDao.findContainerListByParam(container);
    }


    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<Container2ndCategoryCommand> getTrolleyListOrderByGridNumDesc(Long ouId) {
        List<Container2ndCategoryCommand> trolleyList = container2ndCategoryDao.getTrolleyListOrderByGridNumDesc(ouId);
        return trolleyList;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<Container2ndCategoryCommand> getTurnoverBoxByOuIdOrderByVolumeDesc(Long ouId) {
        List<Container2ndCategoryCommand> turnoverBoxList = container2ndCategoryDao.getTurnoverBoxByOuIdOrderByVolumeDesc(ouId);
        return turnoverBoxList;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<UomCommand> findUomByGroupCode(String groupCode, Integer lifecycle) {
        List<UomCommand> uomCmds = uomDao.findUomByGroupCode(groupCode, lifecycle);
        return uomCmds;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Container2ndCategory findContainer2ndCategoryById(Long id, Long ouId) {
        Container2ndCategory container2ndCategory = container2ndCategoryDao.findByIdExt(id, ouId);
        return container2ndCategory;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhOutInventoryboxRelationship> getOutInvBoxRelationshipByType(String type, Long relationshipId, Long ouId) {
        List<WhOutInventoryboxRelationship> outInventoryboxRelationshipList = whOutInventoryboxRelationshipDao.getRelationshipByType(type, relationshipId, ouId);
        return outInventoryboxRelationshipList;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhOutInventoryboxRelationship> getGeneralRelationship(Long ouId) {
        List<WhOutInventoryboxRelationship> outInventoryboxRelationshipList = whOutInventoryboxRelationshipDao.getGeneralRelationship(ouId);
        return outInventoryboxRelationshipList;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void releaseOdoFromWave(Long waveId, Collection<Long> odoIds, String reason, Warehouse wh, String logId) {
        // 踢出波次
        Long ouId = wh.getId();
        for (Long odoId : odoIds) {
            try {
                whWaveManager.deleteWaveLinesFromWaveByWavePhase(waveId, odoId, reason, wh, WavePhase.CREATE_OUTBOUND_CARTON_NUM, logId);

                log.info("releaseOdoFromWave, odoId:[{}],waveId:[{}],ouId:[{}],logId:[{}]", odoId, waveId, ouId, logId);
            } catch (Exception e) {
                log.error(getLogMsg("deleteWaveLines and releaseInventoryByOdoId error! odoId:[{}],waveId:[{}],ouId:[{}]", odoId, waveId, ouId, logId), e);
            }
        }
        whWaveManager.calculateWaveHeadInfo(waveId, ouId);
    }

    /**
     * 根据占用码查询库存
     *
     * @author mingwei.xie
     * @param occupationCode
     * @param ouId
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhSkuInventoryCommand> findListByOccupationCode(String occupationCode, Long ouId) {
        List<WhSkuInventoryCommand> skuInvList = whSkuInventoryDao.findListByOccupationCode(occupationCode, ouId);
        List<WhSkuInventoryCommand> skuInvToBeFillList = whSkuInventoryTobefilledDao.findListByOccupationCode(occupationCode, ouId);
        List<WhSkuInventoryCommand> allSkuInvList = new ArrayList<>();
        allSkuInvList.addAll(skuInvList);
        allSkuInvList.addAll(skuInvToBeFillList);

        return allSkuInvList;
    }
    
    /**
     * 根据占用码查询库存和分配区域
     *
     * @author kaifei.zhang
     * @param occupationCode
     * @param ouId
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhSkuInventoryCommand> findListByOccupationCodeAndAllocateArea(String occupationCode, Long ouId) {
        List<WhSkuInventoryCommand> skuInvList = whSkuInventoryDao.findListByOccupationCodeAndAllocateArea(occupationCode, ouId);
        List<WhSkuInventoryCommand> skuInvToBeFillList = whSkuInventoryTobefilledDao.findListByOccupationCodeAndAllocateArea(occupationCode, ouId);
        List<WhSkuInventoryCommand> allSkuInvList = new ArrayList<>();
        allSkuInvList.addAll(skuInvList);
        allSkuInvList.addAll(skuInvToBeFillList);

        return allSkuInvList;
    }

    /**
     * 根据外部容器查询库存
     *
     * @author mingwei.xie
     * @param outContainerIdList
     * @param ouId
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhSkuInventoryCommand> findSkuInvListByWholeTray(List<Long> outContainerIdList, Long ouId) {
        List<WhSkuInventoryCommand> skuInvList = whSkuInventoryDao.findSkuInvListByWholeTray(outContainerIdList, ouId);

        return skuInvList;
    }

    /**
     * 根据内部容器查询库存
     *
     * @author mingwei.xie
     * @param innerContainerIdList
     * @param ouId
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhSkuInventoryCommand> findSkuInvListByWholeContainer(List<Long> innerContainerIdList, Long ouId) {
        List<WhSkuInventoryCommand> skuInvList = whSkuInventoryDao.findSkuInvListByWholeContainer(innerContainerIdList, ouId);

        return skuInvList;
    }

    /**
     * 占用出库箱推荐的容器
     *
     * @param container
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public int occupationContainerByRecOutboundBox(Container container) {
        return containerDao.saveOrUpdateByVersion(container);
    }

    /**
     * 根据占用码查询库存
     *
     * @author mingwei.xie
     * @param occLineIdList
     * @param ouId
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhSkuInventoryCommand> findListByOccLineIdListOrderByPickingSort(List<Long> occLineIdList, Long ouId) {
        List<WhSkuInventoryCommand> skuInvList = whSkuInventoryDao.findListByOccLineIdListOrderByPickingSort(occLineIdList, ouId);
        List<WhSkuInventoryCommand> skuInvToBeFillList = whSkuInventoryTobefilledDao.findListByOccLineIdListOrderByPickingSort(occLineIdList, ouId);
        List<WhSkuInventoryCommand> allSkuInvList = new ArrayList<>();
        allSkuInvList.addAll(skuInvList);
        allSkuInvList.addAll(skuInvToBeFillList);

        Collections.sort(allSkuInvList, new Comparator<WhSkuInventoryCommand>() {
            @Override
            public int compare(WhSkuInventoryCommand skuInventoryCommand, WhSkuInventoryCommand t1) {
                if (null == skuInventoryCommand.getPickSort() && null == t1.getPickSort()) {
                    return 0;
                }
                if (null == skuInventoryCommand.getPickSort()) {
                    return 1;
                }
                if (null == t1.getPickSort()) {
                    return -1;
                }
                return skuInventoryCommand.getPickSort().compareTo(t1.getPickSort());
            }
        });

        return allSkuInvList;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Long createContainer(Container container, Long ouId) {
        container.setOuId(ouId);
        Long count = containerDao.insert(container);
        insertGlobalLog(GLOBAL_LOG_INSERT, container, ouId, null, null, null);
        return count;
    }
}
