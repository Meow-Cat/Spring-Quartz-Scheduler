# Spring-Quartz-Scheduler
基于spring-boot+quartz的CRUD动态任务管理系统，适用于中小项目。
# 开发环境
JDK1.8、Maven、idea
# 技术栈
SpringBoot 2.x、thymeleaf、quartz2.3.0、vue、layer、bootstrap
# 启动说明
- 项目使用的数据库为MySql，选择resources/sql中的quartz.sql文件初始化数据库信息。
- 在resources/application.properties 以及quartz.properties文件中替换为自己的数据源。
- 运行Application main方法，启动项目，见：
```
https://github.com/Meow-Cat/Spring-Quartz-Scheduler/blob/master/src/main/java/com/phoenix/QuartzApplication.java
```
# 已完成功能
- 任务列表、按名称检索
- 任务新增和修改
- 任务执行
- 表达式生成器(集成：https://gitee.com/finira/cronboot)
- 任务移除
- 任务暂停和恢复
# 后续待完成
- 任务的执行情况
# 友情提示
由于工作原因，项目正在完善中（仅供参考），随时更新日志。
