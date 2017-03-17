package com.baozun.scm.primservice.whoperation.manager.warehouse.inbound;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whinterface.dao.inbound.WhInboundConfirmDao;
import com.baozun.scm.primservice.whinterface.dao.inbound.WhInboundInvLineConfirmDao;
import com.baozun.scm.primservice.whinterface.dao.inbound.WhInboundLineConfirmDao;
import com.baozun.scm.primservice.whinterface.dao.inbound.WhInboundSnLineConfirmDao;
import com.baozun.scm.primservice.whoperation.command.whinterface.inbound.WhInboundConfirmCommand;
import com.baozun.scm.primservice.whoperation.command.whinterface.inbound.WhInboundInvLineConfirmCommand;
import com.baozun.scm.primservice.whoperation.command.whinterface.inbound.WhInboundLineConfirmCommand;
import com.baozun.scm.primservice.whoperation.command.whinterface.inbound.WhInboundSnLineConfirmCommand;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.model.whinterface.inbound.WhInboundConfirm;
import com.baozun.scm.primservice.whoperation.model.whinterface.inbound.WhInboundInvLineConfirm;
import com.baozun.scm.primservice.whoperation.model.whinterface.inbound.WhInboundLineConfirm;
import com.baozun.scm.primservice.whoperation.model.whinterface.inbound.WhInboundSnLineConfirm;

import lark.common.annotation.MoreDB;

@Transactional
@Service("whInboundManager")
public class WhInboundManagerImpl implements WhInboundManager {
	
	protected static final Logger log = LoggerFactory.getLogger(WhInboundManagerImpl.class);
	
	@Autowired
    private WhInboundConfirmDao whInboundConfirmDao;
    @Autowired
    private WhInboundLineConfirmDao whInboundLineConfirmDao;
    @Autowired
    private WhInboundInvLineConfirmDao whInboundInvLineConfirmDao;
    @Autowired
    private WhInboundSnLineConfirmDao whInboundSnLineConfirmDao;
    
    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void insertWhInboundData(WhInboundConfirmCommand inboundConfirmCommand) {
    	// 保存入库反馈头信息
		WhInboundConfirm inboundConfirm = new WhInboundConfirm();
		BeanUtils.copyProperties(inboundConfirmCommand, inboundConfirm);
		whInboundConfirmDao.insert(inboundConfirm);
		
		// 保存入库反馈明细信息
		List<WhInboundLineConfirmCommand> whInboundLineConfirmList = inboundConfirmCommand.getWhInboundLineConfirmList();
		if (null != whInboundLineConfirmList) {
			for (WhInboundLineConfirmCommand lineCommand : whInboundLineConfirmList) {
				WhInboundLineConfirm line = new WhInboundLineConfirm();
				BeanUtils.copyProperties(lineCommand, line);
				line.setInboundConfirmId(inboundConfirm.getId());
				whInboundLineConfirmDao.insert(line);
				
				// 保存入库反馈库存明细信息
				List<WhInboundInvLineConfirmCommand> invLineCommandList = lineCommand.getWhInBoundInvLineConfirmsList();
				if (null != invLineCommandList) {
					for (WhInboundInvLineConfirmCommand invLineCommand : invLineCommandList) {
						WhInboundInvLineConfirm invLine = new WhInboundInvLineConfirm();
						BeanUtils.copyProperties(invLineCommand, invLine);
						invLine.setInboundConfirmLineId(line.getId());
						whInboundInvLineConfirmDao.insert(invLine);
						
						// 保存入库反馈SN信息
						List<WhInboundSnLineConfirmCommand> snLineCommand = invLineCommand.getWhInBoundSnLineConfirmCommandList();
						if (null != snLineCommand) {
							for (WhInboundSnLineConfirmCommand snLineCommandList : snLineCommand) {
								WhInboundSnLineConfirm snLine = new WhInboundSnLineConfirm();
								BeanUtils.copyProperties(snLineCommandList, snLine);
								snLine.setInboundInvLineConfirmId(invLine.getId());
								whInboundSnLineConfirmDao.insert(snLine);
							}
						}
					}
				}
			}
		}
    }
}
