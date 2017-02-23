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

import java.util.Collection;
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
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoOutBoundBoxDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.Container2ndCategoryDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerAssistDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.UomDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOutInventoryboxRelationshipDao;
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
    public void saveRecOutboundBoxByOdo(OdoCommand odoCommand, Container newContainer) {
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
                    // TODO 保存到t_wh_odo_outboundbox
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
                    // TODO 保存到t_wh_odo_outboundbox
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
                    // TODO 保存到t_wh_odo_outboundbox
                    WhOdoOutBoundBox odoOutBoundBox = new WhOdoOutBoundBox();
                    BeanUtils.copyProperties(odoOutBoundBoxCommand, odoOutBoundBox);

                    odoOutBoundBox.setCreateTime(new Date());
                    odoOutBoundBox.setLastModifyTime(new Date());
                    odoOutBoundBoxDao.insert(odoOutBoundBox);
                }
            }
        }

        if(null != newContainer){
            containerDao.insert(newContainer);
        }
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void saveRecOutboundBoxForTrolleyPackedOdo(List<WhOdoOutBoundBoxCommand> odoOutBoundBoxCommandList, Container trolleyContainer) {
        if (null != odoOutBoundBoxCommandList && !odoOutBoundBoxCommandList.isEmpty()) {
            // 出库箱中的包裹，按照商品划分
            for (WhOdoOutBoundBoxCommand odoOutBoundBoxCommand : odoOutBoundBoxCommandList) {
                // TODO 保存到t_wh_odo_outboundbox
                WhOdoOutBoundBox odoOutBoundBox = new WhOdoOutBoundBox();
                BeanUtils.copyProperties(odoOutBoundBoxCommand, odoOutBoundBox);

                odoOutBoundBox.setCreateTime(new Date());
                odoOutBoundBox.setLastModifyTime(new Date());
                odoOutBoundBoxDao.insert(odoOutBoundBox);
            }
        }

        if(null != trolleyContainer) {
            containerDao.insert(trolleyContainer);
        }
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void saveRecOutboundBoxByContainer(List<Container2ndCategoryCommand> containerList, List<Container> newOutboundBoxContainerList) {
        for (Container2ndCategoryCommand container : containerList) {
            // 整箱中的包裹
            for (WhOdoOutBoundBoxCommand odoOutBoundBoxCommand : container.getOdoOutBoundBoxCommandList()) {
                // TODO 保存到t_wh_odo_outboundbox
                WhOdoOutBoundBox odoOutBoundBox = new WhOdoOutBoundBox();
                BeanUtils.copyProperties(odoOutBoundBoxCommand, odoOutBoundBox);

                odoOutBoundBox.setCreateTime(new Date());
                odoOutBoundBox.setLastModifyTime(new Date());
                odoOutBoundBoxDao.insert(odoOutBoundBox);
            }
        }

        if(null != newOutboundBoxContainerList) {
            for (Container container : newOutboundBoxContainerList) {
                containerDao.insert(container);
            }
        }
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void saveRecOutboundBoxForSeedBatch(List<Container2ndCategoryCommand> turnoverBoxList, List<ContainerCommand> odoPackedWholeCaseList, List<ContainerCommand> odoPackedWholeTrayList, List<Container> newOutboundBoxContainerList) {
        if (null != turnoverBoxList) {
            for (Container2ndCategoryCommand container : turnoverBoxList) {
                // 整箱中的包裹
                for (WhOdoOutBoundBoxCommand odoOutBoundBoxCommand : container.getOdoOutBoundBoxCommandList()) {
                    // TODO 保存到t_wh_odo_outboundbox
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
                    // TODO 保存到t_wh_odo_outboundbox
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
                    // TODO 保存到t_wh_odo_outboundbox
                    WhOdoOutBoundBox odoOutBoundBox = new WhOdoOutBoundBox();
                    BeanUtils.copyProperties(odoOutBoundBoxCommand, odoOutBoundBox);

                    odoOutBoundBox.setCreateTime(new Date());
                    odoOutBoundBox.setLastModifyTime(new Date());
                    odoOutBoundBoxDao.insert(odoOutBoundBox);
                }
            }
        }

        if(null != newOutboundBoxContainerList) {
            for (Container container : newOutboundBoxContainerList) {
                containerDao.insert(container);
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
    public Long createContainer(Container container, Long ouId) {
        container.setOuId(ouId);
        Long count = containerDao.insert(container);
        return count;
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
        // TODO 踢出波次
        Long ouId = wh.getId();
        for (Long odoId : odoIds) {
            try {
                whWaveManager.deleteWaveLinesAndReleaseInventoryByOdoId(waveId, odoId, reason, wh);
                log.info("releaseOdoFromWave, odoId:[{}],waveId:[{}],ouId:[{}],logId:[{}]", odoId, waveId, ouId, logId);
            } catch (Exception e) {
                log.error(getLogMsg("deleteWaveLines and releaseInventoryByOdoId error! odoId:[{}],waveId:[{}],ouId:[{}]", odoId, waveId, ouId, logId), e);
            }
        }
    }
}
