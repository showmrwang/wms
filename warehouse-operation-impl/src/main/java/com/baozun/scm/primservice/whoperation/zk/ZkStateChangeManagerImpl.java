package com.baozun.scm.primservice.whoperation.zk;

import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.baozun.task.bean.TaskLockCustomStateManager;
import com.baozun.zkpro.bean.ZkStateChangeManager;

public class ZkStateChangeManagerImpl implements ZkStateChangeManager {

	private static final Logger LOGGER=LoggerFactory.getLogger(ZkStateChangeManagerImpl.class);
	
	@Autowired
	private TaskLockCustomStateManager taskLockCustomStateManager;
	@Override
	public void handleNewSession() throws Exception {
		
		//断后重连需要再次注册
		//ZkClientInitBean.afterPropertiesSet();
		
		taskLockCustomStateManager.handleNewSession();
		
		LOGGER.info("zk new Session create");
	}

	@Override
	public void handleStateChanged(KeeperState arg0) throws Exception {
		taskLockCustomStateManager.handleStateChanged(arg0);
		LOGGER.info("zk stateChange:"+arg0);
	}

	@Override
	public void handlerDisconnected() throws Exception {
		LOGGER.info("zk Disconnected");
	}

	@Override
	public void handlerExpired() throws Exception {
		
		LOGGER.info("zk Expired");
	}

	@Override
	public void handlerSyncConnected() throws Exception {
		LOGGER.info("zk SyncConnected");
		
		
	}

}
