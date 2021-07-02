package com.phoenix.service;

import com.phoenix.entity.JobEntity;
import com.phoenix.job.DynamicJob;
import com.phoenix.service.JobService;
import com.phoenix.util.Constants;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Phoenix on 2021/6/29 14:29
 */
@Service
public class DynamicJobService {

    @Autowired
    private JobService jobService;

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    //通过Id获取Job
    public JobEntity getJobEntityById(String id) {
        return jobService.getById(id);
    }

    //从数据库中加载获取到OPEN状态的Job
    public List<JobEntity> loadJobs() {
        JobEntity jobEntity = new JobEntity();
        jobEntity.setStatus(Constants.Status.OPEN);
        return jobService.findList(jobEntity);
    }

    //获取JobDataMap.(Job参数对象)
    public JobDataMap getJobDataMap(JobEntity job) {
        JobDataMap map = new JobDataMap();
        map.put("jobEntity",job);
        return map;
    }

    //获取JobDetail,JobDetail是任务的定义,而Job是任务的执行逻辑,JobDetail里会引用一个Job Class来定义
    public JobDetail getJobDetail(JobKey jobKey, String description, JobDataMap map) {
        return JobBuilder.newJob(DynamicJob.class)
                .withIdentity(jobKey)
                .withDescription(description)
                .setJobData(map)
                .storeDurably()
                .build();
    }

    //获取Trigger (Job的触发器,执行规则)
    public Trigger getTrigger(JobEntity job) {
        return TriggerBuilder.newTrigger()
                .withIdentity(job.getName(), job.getJobGroup())
                .withSchedule(CronScheduleBuilder.cronSchedule(job.getCron()))
                .build();
    }

    //获取JobKey,包含Name和Group
    public JobKey getJobKey(JobEntity job) {
        return JobKey.jobKey(job.getName(), job.getJobGroup());
    }

    //获取TriggerKey,包含Name和Group
    public TriggerKey getTriggerKey(JobEntity job){
        return TriggerKey.triggerKey(job.getName(), job.getJobGroup());
    }

    //新增任务
    public void addJob(JobEntity jobEntity) throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        JobDetail jobDetail = getJobDetail(getJobKey(jobEntity),jobEntity.getDescription(),getJobDataMap(jobEntity));
        scheduler.scheduleJob(jobDetail, getTrigger(jobEntity));
    }

    //修改任务
    public void updateJob(JobEntity jobEntity) throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        //暂停任务
        scheduler.pauseJob(getJobKey(jobEntity));
        scheduler.unscheduleJob(getTriggerKey(jobEntity));
        //删除任务
        scheduler.deleteJob(getJobKey(jobEntity));
        //重启任务
        JobDetail jobDetail = getJobDetail(getJobKey(jobEntity),jobEntity.getDescription(),getJobDataMap(jobEntity));
        scheduler.scheduleJob(jobDetail, getTrigger(jobEntity));
    }

    //删除任务
    public void removeJob(JobEntity jobEntity) throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        //暂停任务
        scheduler.pauseJob(getJobKey(jobEntity));
        scheduler.unscheduleJob(getTriggerKey(jobEntity));
        //删除任务
        scheduler.deleteJob(getJobKey(jobEntity));
    }
}
