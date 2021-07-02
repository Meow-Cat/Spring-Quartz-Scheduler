package com.phoenix.web;

import com.phoenix.service.DynamicJobService;
import com.phoenix.entity.JobEntity;
import com.phoenix.service.JobService;
import com.phoenix.util.Constants;
import com.phoenix.util.TaskUtil;
import lombok.extern.log4j.Log4j2;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

/**
 * Created by Phoenix on 2021/6/29 14:29
 */
@RestController
@Log4j2
@RequestMapping("/job")
public class JobController {

    @Autowired
    private JobService jobService;
    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;
    @Autowired
    private DynamicJobService dynamicJobService;

    @RequestMapping("/refresh/all")
    public String refreshAllJob() throws SchedulerException {
        reStartAllJobs();
        return "refresh all job success";
    }

    //根据ID重启某个Job
    @RequestMapping("/refresh/{id}")
    public String refresh(@PathVariable String id) throws SchedulerException {
        String result;
        JobEntity entity = dynamicJobService.getJobEntityById(id);
        if (Objects.isNull(entity))
            return "error: id :"+ id +" is not exist ";
        synchronized (log) {
            JobKey jobKey = dynamicJobService.getJobKey(entity);
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            scheduler.pauseJob(jobKey);
            scheduler.unscheduleJob(TriggerKey.triggerKey(jobKey.getName(), jobKey.getGroup()));
            scheduler.deleteJob(jobKey);
            JobDataMap map = dynamicJobService.getJobDataMap(entity);
            JobDetail jobDetail = dynamicJobService.getJobDetail(jobKey, entity.getDescription(), map);
            if (Constants.Status.OPEN.equals(entity.getStatus())) {
                scheduler.scheduleJob(jobDetail, dynamicJobService.getTrigger(entity));
                result = "Refresh Job : " + entity.getName() +"success !";
            } else {
                result = "Refresh Job : " + entity.getName() + " failed ! , " +
                        "Because the Job status is " + entity.getStatus();
            }
        }
        return result;
    }

    //初始化启动所有的Job
    @PostConstruct
    public void initialize() {
        try {
            reStartAllJobs();
            log.info("init success");
        } catch (SchedulerException e) {
            log.error("printStackTrace ", e);
        }
    }
    /**
     * 重新启动所有的job
     */
    private void reStartAllJobs() throws SchedulerException {
        synchronized (log) {                                                         //只允许一个线程进入操作
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            Set<JobKey> set = scheduler.getJobKeys(GroupMatcher.anyGroup());
            scheduler.pauseJobs(GroupMatcher.anyGroup());                               //暂停所有JOB
            for (JobKey jobKey : set) {                                                 //删除从数据库中注册的所有JOB
                scheduler.unscheduleJob(TriggerKey.triggerKey(jobKey.getName(), jobKey.getGroup()));
                scheduler.deleteJob(jobKey);
            }
            for (JobEntity job : dynamicJobService.loadJobs()) {                               //从数据库中注册的所有JOB
                log.info("Job register name : {} , group : {} , cron : {}", job.getName(), job.getJobGroup(), job.getCron());
                //判断任务cron是否有效
                if (Constants.JobGroup.ONE.equals(job.getJobGroup())) {
                    if (!validCron(job)) {
                        log.info("Job jump name : {} , Because {} cron {} is invalid", job.getName(), job.getName(), job.getCron());
                        TaskUtil.executionLogicMethod(job);
                        job.setStatus(Constants.Status.CLOSE);
                        jobService.update(job);
                        continue;
                    }
                }
                JobDataMap map = dynamicJobService.getJobDataMap(job);
                JobKey jobKey = dynamicJobService.getJobKey(job);
                JobDetail jobDetail = dynamicJobService.getJobDetail(jobKey, job.getDescription(), map);
                if (job.getStatus().equals("OPEN"))
                    scheduler.scheduleJob(jobDetail, dynamicJobService.getTrigger(job));
                else
                    log.info("Job jump name : {} , Because {} status is {}", job.getName(), job.getName(), job.getStatus());
            }
        }
    }

    /**
     * 校验cron
     * @param job
     * @return
     */
    public static boolean validCron(JobEntity job){
        return TaskUtil.isValidExpression(job.getCron(),new Date());
    }
}
