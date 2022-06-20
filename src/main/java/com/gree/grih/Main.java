package com.gree.grih;


import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.status.OnConsoleStatusListener;
import ch.qos.logback.core.status.StatusManager;
import ch.qos.logback.core.util.StatusPrinter;
import com.gree.grih.vo.UserVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        //打印 logback 查找并解析配置文件 logback.xml 的状态信息
        StatusPrinter.print(loggerContext);
        StatusManager statusManager = loggerContext.getStatusManager();
        //注册 StatusListener
        statusManager.add(new OnConsoleStatusListener());

        UserVO userVO = new UserVO();
        while (true){
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(System.currentTimeMillis());
            userVO.getById(1);
            userVO.add((int)System.currentTimeMillis()/1000,"abc");
            logger.trace("TRACE level message.");
            logger.info("This is main.");
            logger.error("This is for test.");
        }
    }
}
