package com.phoenix.job;

import com.phoenix.entity.JobEntity;
import com.phoenix.service.DynamicJobService;
import com.phoenix.service.JobService;
import com.phoenix.util.Constants;
import com.phoenix.util.TaskUtil;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Created by EalenXie on 2018/6/4 14:29
 * :@DisallowConcurrentExecution : 此标记用在实现Job的类上面,意思是不允许并发执行.
 * :注意org.quartz.threadPool.threadCount线程池中线程的数量至少要多个,否则@DisallowConcurrentExecution不生效
 * :假如Job的设置时间间隔为3秒,但Job执行时间是5秒,设置@DisallowConcurrentExecution以后程序会等任务执行完毕以后再去执行,否则会在3秒时再启用新的线程执行
 */
@DisallowConcurrentExecution
@Component
@Log4j2
public class DynamicJob implements Job {

    @Autowired
    private DynamicJobService dynamicJobService;
    @Autowired
    private JobService jobService;

    /**
     * 核心方法,Quartz Job真正的执行逻辑.
     *
     * @param executorContext executorContext JobExecutionContext中封装有Quartz运行所需要的所有信息
     * @throws JobExecutionException execute()方法只允许抛出JobExecutionException异常
     */
    @SneakyThrows
    @Override
    public void execute(JobExecutionContext executorContext) throws JobExecutionException {
        //JobDetail中的JobDataMap是共用的,从getMergedJobDataMap获取的JobDataMap是全新的对象
        JobDataMap map = executorContext.getMergedJobDataMap();
        JobEntity jobEntity = (JobEntity) map.get("jobEntity");
        if (Constants.Status.OPEN.equals(jobEntity.getStatus())) {
            if (StringUtils.isNotBlank(jobEntity.getExecutionClass())) {
                TaskUtil.executionLogicMethod(jobEntity);
            }
            //如果job在‘one’组中表示该job只执行一次
            if (Constants.JobGroup.ONE.equals(jobEntity.getJobGroup())) {
                dynamicJobService.removeJob(jobEntity);
                jobEntity.setStatus(Constants.Status.CLOSE);
                jobService.update(jobEntity);
            }
        }
        //秒 分 小时 日 月 星期 年
        //59 59 23 21 7 ? 2021
        //2021-7-21 23:59:59
        //00 31 19 29 6 ? 2021
    }
}
