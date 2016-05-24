package com.baozun.scm.primservice.whoperation.manager.system;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.redis.manager.CacheConfigManager;


@Service("cacheConfigManager")
@Transactional
public class CacheConfigManagerImpl implements CacheConfigManager {

	@Value("${redis.keystart}")
	private String keyStartWith;
	
	@Override
	public String getKeyStart() {
		return keyStartWith;
	}

	@Override
	public boolean hasCache() {
		return true;
	}

}
