package com.baozun.scm.primservice.whoperation.excel.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

/**
 * Spring工具类,用于静态获取Spring容器中的Bean
 * @author lisuo
 *
 */
@Service(value = "springUtil")
public class SpringUtil implements ApplicationContextAware{
	
	protected static ApplicationContext ctx;
	
	public SpringUtil(){
		super();
	}
	
	/**
	 * 获取bean
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBean(String id){
		return (T) ctx.getBean(id);
	}
	
	/**
	 * 按类型获取bean
	 * @param clazz
	 * @return
	 */
	public static <T> T getBean(Class<T> clazz){
		return ctx.getBean(clazz);
	}
	
	/**
	 * 按类型及ID获取bean
	 * @param id
	 * @param clazz
	 * @return
	 */
	public static <T> T getBean(String id, Class<T> clazz){
		return ctx.getBean(id, clazz);
	}
	
	/**
	 * 
	 * 检查SpringUtil是否已完成初始化
	 * @param
	 * @return boolean
	 * @throws
	 */
	public static boolean isInited(){
		return null!=ctx;
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		SpringUtil.ctx = applicationContext;
	}
}
