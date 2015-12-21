package com.baozun.scm.primservice.whoperation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

@ContextConfiguration({ "classpath*:spring-test.xml" })
@ActiveProfiles("dev")
public abstract class SpringTestManagerBase extends AbstractTransactionalJUnit4SpringContextTests {

    protected Logger log = LoggerFactory.getLogger(this.getClass());
    
}
