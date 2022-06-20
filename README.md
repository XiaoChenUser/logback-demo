# 工程简介  
这是一个介绍 logback 的使用与核心功能的项目。  

### 基本使用
1.引入依赖  
logback 相关依赖包括：  
java8: logback 1.3.0 要求 java8.  
logback-core,logback-classic,logback-access.  
①普通 java 工程：  
```xml
<dependencies>
    <dependency>
        <groupId>ch.qos.logback</groupId>
        <artifactId>logback-core</artifactId>
        <version>1.3.0-alpha16</version>
    </dependency>
    <dependency>
        <groupId>ch.qos.logback</groupId>
        <artifactId>logback-classic</artifactId>
        <version>1.3.0-alpha16</version>
    </dependency>
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-api</artifactId>
        <version>2.0.0-alpha7</version>
    </dependency>
</dependencies>
```
②spring 相关项目，内部包含 logback 相关依赖，无需额外引入。  

2.使用
①普通 java 工程  
直接将 logback.xml 放在 classpath 目录下。  
②spring 工程  
将 logback.xml 重命名为 logback-spring.xml，放在 classpath 下。  
## 注意：  
logback.xml 中引用了 LOG_DIR 和 APP_NAME 两个变量，需要修改为需要的值！！！  

### logback 核心功能（配置）说明
logback.xml 基础结构
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<configuration>
    <appender>
        <encoder></encoder>
        <filter></filter>
    </appender>
    <logger>
        <appender-ref ref=""></appender-ref>
    </logger>
    <root>
        <appender-ref ref=""></appender-ref>
    </root>
</configuration>
```

1.<configuration>
对应于 JoranConfigurator.java，可以通过该类查看 <configuration> 标签中包含哪些元素，以及它们对应的类。  
属性：  
scan：当 logback.xml 发生改变时，自动扫描；  
scanPeriod：扫描的周期，默认 1 分钟，可用单位 milliseconds, seconds, minutes or hours，比如：scanPeriod="60 seconds"，未带单位，默认为 milliseconds;  

2.变量定义和引用  
老版本定义变量时使用 <property>，新版本 logback 推荐使用 <variable>。  
变量可以定义在 3 个不同的地方：  
①定义在 logback.xml 文件内
```xml
<variable name="LOG_DIR" value="log"/>
```
②在 classpath 下的外部资源文件中定义变量
```xml
<variable resource="resource1.properties"/>
<!-- 资源文件中定义变量：LOG_DIR=log -->
```
③在外部文件中定义变量
```xml
<variable file="src/main/java/com/gree/grih/conf/variables1.properties"/>
<!-- 外部文件中定义变量：LOG_DIR=log -->
```

3.常用 appender
![appender](https://github.com/XiaoChenUser/image-store/raw/main/LogbackTest/common-appender.PNG)  
AppenderBase: 同步 appender，其子类有 SocketAppender，用于将日志传输到 remote logger server;  

UnsynchronizedAppenderBase: 异步 appender;  

OutputStreamAppender: 一般不直接使用它，而是用它的子类。
属性：  
&emsp;&emsp; encoder  
&emsp;&emsp; immediateFlush  
ConsoleAppender: 输出到控制台；  

FileAppender：  
属性：  
&emsp;&emsp; file: 文件名  
&emsp;&emsp; encoder  
&emsp;&emsp; immediateFlush: default true，每 append 一个 event，就立即 push 到其 output stream。  
&emsp;&emsp; append：default true，event 添加到已存在日志文件的末尾，false 表示 truncate 文件再写日志；  
&emsp;&emsp; prudent：谨慎的，额外的限制；  

RollingFileAppender： 滚动日志文件  
属性：  
&emsp;&emsp; file: current active file，新的日志都写到该文件中，其它老的日志文件 archive files（存档，档案）。  
&emsp;&emsp; rollingPolicy：滚动策略（what）  
&emsp;&emsp; triggeringPolicy：滚动触发策略（when）  
rollingPolicy 和 triggeringPolicy 专为 RollingFileAppender 服务，RollingFileAppender start() 会检查 triggeringPolicy 和 rollingPolicy 是否为 null，若为 null，则无法 start.  

```xml
<appender>
    <file>ACTIVE FILE NAME</file>
    <rollingPolicy></rollingPolicy>
    <triggeringPolicy></triggeringPolicy>
    <encoder></encoder>
    <filter></filter>
</appender>
```

4.RollingPolicy & TriggeringPolicy  
RollingPolicy: 当需要 roll over 的时候，要干些什么工作。比如：将当前正在写入日志的 active logger file 重命名，再重新创建 active logger file 等。  
TriggeringPolicy: 控制什么情况下触发 roll over。比如定时触发，日志文件到达指定大小触发等。  
经常一个类同时实现了 RollingPolicy & TriggeringPolicy.  
最常用的：TimeBasedRollingPolicy 和它的子类 SizeAndTimeBasedRollingPolicy。  

TimeBasedRollingPolicy:  
属性：  
&emsp;&emsp; file: active file name  

&emsp;&emsp; fileNamePattern: 历史日志文件（archive file，日志文件包括 active file 和 archive file）的文件名格式；    

日志文件的滚动周期：  
通过 <rollingPolicy> 下的 <fileNamePattern> 推断而来。若 fileNamePattern 字符串中包含时间 %d{yyyy-MM-dd HH:mm:ss.SSS}，根据大括号内最小时间单位来确定 roll over 周期，比如：yyyy-MM 就是每月开始的时候滚动；yyyy-MM-dd 就是每天开始的时候滚动（midnight）；以此类推，最小滚动周期为 1 小时。若 fileNamePattern 没有包含 %d{...}，则使用默认值 yyyy-MM-dd.  

日志文件自动压缩：  
如果 fileNamePattern 以 .gz 或 .zip 结尾，将被解析为自动压缩。对应 Compressor 和 CompressionMode. 非 current active file 都将被压缩。  

&emsp;&emsp; maxHistory: the maximum number of archive files to keep.配合滚动周期（天、周、月），异步删除超过 maxHistory 个滚动周期的日志文件，若日志文件夹名称中含有时间，超期的文件夹也会被删除。设置 maxHistory=0，不删除日志文件。  

&emsp;&emsp; totalSizeCap：控制 archive files 的总大小，超过 totalSizeCap，最老的日志文件先被删除。需要配合 maxHistory 使用，maxHistory 先起作用，totalSizeCap 再起作用。units of bytes, kilobytes, megabytes or gigabytes by suffixing a numeric value with KB, MB and respectively GB. For example, 5000000, 5000KB, 5MB and 2GB are all valid values。
default: 0，不限制大小。  

&emsp;&emsp; cleanHistoryOnStart：default false. 用于运行时间较短的项目。清理过期的日志文件会在每次 roll over 的时候异步执行。  

SizeAndTimeBasedRollingPolicy：继承自 TimeBasedRollingPolicy。  
属性：  
&emsp;&emsp; maxFileSize: 限制每个日志文件的大小。  
相交于 TimeBasedRollingPolicy，除了新增 maxFileSize，还对 archive logger file 文件名新增了限制，fileNamePattern 中的 %d 和 %i 是必填的，%i 表示同一个 roll over 周期内，各个日志文件的 index，从 0 开始。  

5.encoder  
encoder 用于将 ILoggingEvent 对象转化为 byte[]，再将 byte[] 传输给指定的 output stream。  
最常用的 encoder 是 PatternLayoutEncoder，其通常搭配 PatternLayout 使用。  

6.Filter  
用于过滤当前 appender 的日志输出，比如根据日志等级 level 进行限制，ThresholdFilter,大于等于指定 level 的日志都可以输出；LevelFilter,等于指定 level 的由 onMatch 判定，不等于的由 onMisMatch 判定，判定结果是 FilterReply 枚举值。  
Filter 的执行顺序是其在 appender 中的定义顺序。  
```java
public abstract class Filter<E>{
    public abstract FilterReply decide(E event);
}
public enum FilterReply {
    DENY, 
    //中立的
    NEUTRAL, 
    ACCEPT;
}
```
关于 FilterReply 的枚举值说明：  
假设有 Filter 链 A -> B -> C，现在执行到 B，根据 B 的返回值决定后续执行情况：  
①FilterReply.DENY，丢弃 event，后续不再执行；  
②FilterReply.NEUTRAL，执行过滤器 C；  
③FilterReply.ACCEPT，后面的 filter 不再执行，直接跳过。  

7.logger & root
logger(<logger>) 是控制谁（类，package）能打日志。  
属性：  
①name：必填，可以是包名或者全类名；  
②level：大于等于 level 的都可以输出；  
③additivity：true/false，<logger> 是分层的，
比如：  
<logger name="com.gree.grih"> 是 <logger name="com.gree.grih.vo"> 的上层，additivity 控制打日志的操作是否向上层 logger 传递，<root> 是顶层 <logger>，假如 additivity=true，且同一个 <appender> 在 <logger> 和 <root> 都出现了，则会造成日志重复打印，additivity=false，name 限定范围内的打印操作只会在本层级 <logger> 执行，不会向上积累。  
