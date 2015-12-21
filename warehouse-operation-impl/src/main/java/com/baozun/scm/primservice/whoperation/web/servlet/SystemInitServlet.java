package com.baozun.scm.primservice.whoperation.web.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * 系统初始化时的servlet
 * @author Justin Hu
 *
 */
public class SystemInitServlet extends HttpServlet{
	
	private static final long serialVersionUID = 4724299124899039939L;

	@Override
	public void init(ServletConfig config) throws ServletException {
		initZookeeper(config);
	}
	

	
	private void initZookeeper(ServletConfig config){
		ApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(config.getServletContext());
		
//		WatchControl zo=(WatchControl)applicationContext.getBean("watchControl");
//		zo.initWatch();
	}
	
}
