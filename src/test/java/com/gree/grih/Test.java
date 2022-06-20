package com.gree.grih;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Test {
    private static final Logger logger = LoggerFactory.getLogger(Test.class);

    @org.junit.Test
    public void test(){
            logger.info("This is [INFO].");
            logger.warn("This is [WARN].");
            logger.error("This is [ERROR].");
    }
}
