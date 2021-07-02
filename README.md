# Spring-Quartz-Scheduler
基于spring-boot+quartz的CRUD动态任务管理系统，适用于中小项目。
# 开发环境
JDK1.8、Maven、idea
# 技术栈
SpringBoot1.5.2、thymeleaf、quartz2.3.0、vue、layer、bootstrap
启动说明
- 项目使用的数据库为MySql，选择resources/sql中的quartz.sql文件初始化数据库信息。
- 在resources/application.properties 以及quartz.properties文件中替换为自己的数据源。
- 运行Application main方法，启动项目，项目启动会初始化一个定时任务，见：
```
https://github.com/Meow-Cat/Spring-Quartz-Scheduler/blob/master/src/main/java/com/phoenix/QuartzApplication.java
```
