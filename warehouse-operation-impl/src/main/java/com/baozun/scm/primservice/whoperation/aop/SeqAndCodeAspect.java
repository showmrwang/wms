package com.baozun.scm.primservice.whoperation.aop;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lark.orm.util.ReflectionUtils;

import org.apache.ibatis.session.SqlSessionFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;

import com.baozun.scm.baseservice.sac.manager.PkManager;


@Aspect
public class SeqAndCodeAspect implements Ordered, InitializingBean {

    @Autowired(required = false)
    private SqlSessionTemplate sqlSessionTemplate;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    private static final String INSERT_METHOD = "insert";

    private static final String SAVE_OR_UPDATE_METHOD = "saveOrUpdate";
    
    
    
    private Map<String,Boolean> modelMap=new HashMap<String,Boolean>();
    
    
  //配置实体列表
  	public void setModelList(List<String> modelList) {
  		
  		for(String str:modelList){
  			modelMap.put(str, true);
  		}
  	}

    // private NamedQueryHandler namedQueryHandler;
    // private QueryHandler queryHandler;
    // private DynamicQueryHandler dynamicQueryHandler;
    // private NativeQueryHandler nativeQueryHandler;

    public void afterPropertiesSet() throws Exception {
        // namedQueryHandler = new NamedQueryHandler(daoService);
        // queryHandler = new QueryHandler(daoService);
        // dynamicQueryHandler = new DynamicQueryHandler(daoService, templateService, dnqProvider);
        // nativeQueryHandler = new NativeQueryHandler(daoService, templateService, dnqProvider);
    }

    public int getOrder() {
        return 19;
    }

    @Autowired
    private PkManager pkManager;
    
	private String processClass(Class clazz){
		String str=clazz.toString();
		
		str=str.replace("class ", "");
		
		return str;
	}

    @Around("this(lark.orm.dao.supports.BaseDao)")
    public Object doQuery(ProceedingJoinPoint pjp) throws Throwable {

        MethodSignature ms = (MethodSignature) pjp.getSignature();


        String method = ms.getMethod().getName();

        // 如果是插入方法，以及保存或更新方法
        if (INSERT_METHOD.equals(method) ) {

            // 如果仅为一个参数
            if (pjp.getArgs().length == 1) {

                Object model = pjp.getArgs()[0];
                
                if(modelMap.get(processClass(model.getClass()))!=null) {

	                Long id = (Long) ReflectionUtils.invokeGetterMethod(model, "id");
	
	                if (id == null) {
	                    // 此处需要调用主键服务
	                    id = pkManager.generatePk("wms", "com.baozun.scm.primservice.whoperation.model.poasn");
	
	                    // 最后果将数据写到对象中
	                    ReflectionUtils.invokeSetterMethod(model, "id", id);
	                }
              }

            }


        }

        return pjp.proceed(pjp.getArgs());



    }
}
