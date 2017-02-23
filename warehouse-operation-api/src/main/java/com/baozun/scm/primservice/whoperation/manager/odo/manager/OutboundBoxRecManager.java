/**
 * Copyright (c) 2013 Baozun All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Baozun.
 * You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Baozun.
 * <p/>
 * BAOZUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. BAOZUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 */

package com.baozun.scm.primservice.whoperation.manager.odo.manager;

import java.util.Collection;
import java.util.List;

import com.baozun.scm.primservice.whoperation.command.odo.OdoCommand;
import com.baozun.scm.primservice.whoperation.command.odo.WhOdoOutBoundBoxCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.Container2ndCategoryCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.UomCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container2ndCategory;
import com.baozun.scm.primservice.whoperation.model.warehouse.ContainerAssist;
import com.baozun.scm.primservice.whoperation.model.warehouse.Warehouse;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutInventoryboxRelationship;

public interface OutboundBoxRecManager extends BaseManager {

    /**
     * 获取批次号
     *
     * @author mingwei.xie
     * @param ouId
     * @return
     */
    public String getBatchNo(Long ouId);

    public void saveRecOutboundBoxByOdo(OdoCommand odoCommand, Container newContainer);

    public void saveRecOutboundBoxForTrolleyPackedOdo(List<WhOdoOutBoundBoxCommand> odoOutBoundBoxCommandList, Container trolleyContainer);

    public void saveRecOutboundBoxByContainer(List<Container2ndCategoryCommand> containerList, List<Container> newOutboundBoxContainerList);

    public void saveRecOutboundBoxForSeedBatch(List<Container2ndCategoryCommand> turnoverBoxList, List<ContainerCommand> odoPackedWholeCaseList, List<ContainerCommand> odoPackedWholeTrayList, List<Container> newOutboundBoxContainerList);

    public List<ContainerAssist> findContainerAssistById(List<Long> containerIdList, Long ouId);

    public ContainerCommand findContainerById(Long containerId, Long ouId);

    public List<Container2ndCategoryCommand> getTrolleyListOrderByGridNumDesc(Long ouId);

    public List<Container2ndCategoryCommand> getTurnoverBoxByOuIdOrderByVolumeDesc(Long ouId);

    public List<UomCommand> findUomByGroupCode(String groupCode,Integer lifecycle);

    public Container2ndCategory findContainer2ndCategoryById(Long id, Long ouId);

    public Long createContainer(Container container, Long ouId);

    public List<WhOutInventoryboxRelationship> getOutInvBoxRelationshipByType(String type, Long relationshipId, Long ouId);

    public List<WhOutInventoryboxRelationship> getGeneralRelationship(Long ouId);

    public void releaseOdoFromWave(Long waveId, Collection<Long> odoIds, String reason, Warehouse wh, String logId);
}
