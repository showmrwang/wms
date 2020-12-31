package com.baozun.scm.primservice.whoperation.aop;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import lark.orm.util.ReflectionUtils;


@Aspect
public class SeqAndCodeAspect implements Ordered, InitializingBean {

    @SuppressWarnings("unused")
    @Autowired(required = false)
    private SqlSessionTemplate sqlSessionTemplate;

    @SuppressWarnings("unused")
    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    private static final String INSERT_METHOD = "insert";

    @SuppressWarnings("unused")
    private static final String SAVE_OR_UPDATE_METHOD = "saveOrUpdate";



    private Map<String, Boolean> modelMap = new HashMap<String, Boolean>();

    /**
     * 映射关系
     * 如果有配置，表示这个实体使用定制的实体标识查询主键
     */
    private Map<String, String> modelMapppingMap = new HashMap<String, String>();

    // 配置实体列表
    public void setModelList(List<String> modelList) {

        for (String str : modelList) {
            modelMap.put(str, true);
        }
    }
    
    public void setModelMapppingMap(Map<String, String> modelMapppingMap) {
		this.modelMapppingMap = modelMapppingMap;
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

    @SuppressWarnings("rawtypes")
    private String processClass(Class clazz) {
        String str = clazz.toString();

        str = str.replace("class ", "");

        return str;
    }


    @Around("this(lark.orm.dao.supports.BaseDao)")
    public Object doQuery(ProceedingJoinPoint pjp) throws Throwable {

        MethodSignature ms = (MethodSignature) pjp.getSignature();


        String method = ms.getMethod().getName();

        // 如果是插入方法，以及保存或更新方法
        if (INSERT_METHOD.equals(method)) {

            // 如果仅为一个参数
            if (pjp.getArgs().length == 1) {

                Object model = pjp.getArgs()[0];
                
                Long id = null;

                if (modelMap.get(processClass(model.getClass())) != null) {

                    id = (Long) ReflectionUtils.invokeGetterMethod(model, "id");

                    if (id == null) {
                        
                    	String classPath=processClass(model.getClass());
                    	//实体标识
                    	String pkMark=modelMapppingMap.get(classPath);
                    	//如果没有配置实体标识，则使用classpath做为实体标识
                    	if(pkMark==null)	pkMark=classPath;
                    	// 此处需要调用主键服务
                        id = pkManager.generatePk("wms",pkMark );


                        // 最后果将数据写到对象中
                        ReflectionUtils.invokeSetterMethod(model, "id", id);
                    }
                } else if (modelMapppingMap.get(processClass(model.getClass())) != null) {
                    String classPath = processClass(model.getClass());
                    // 实体标识
                    String pkMark = modelMapppingMap.get(classPath);
                    // 如果没有配置实体标识，则使用classpath做为实体标识
                    if (pkMark == null) pkMark = classPath;
                    // 此处需要调用主键服务
                    id = pkManager.generatePk("wms", pkMark);


                    // 最后果将数据写到对象中
                    ReflectionUtils.invokeSetterMethod(model, "id", id);
                }

            }


        }

        return pjp.proceed(pjp.getArgs());



    }
}
