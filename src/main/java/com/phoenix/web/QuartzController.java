package com.phoenix.web;

import com.phoenix.entity.JobEntity;
import com.phoenix.entity.Result;
import com.phoenix.service.DynamicJobService;
import com.phoenix.service.JobService;
import com.phoenix.util.Constants;
import com.phoenix.util.TaskUtil;
import lombok.extern.log4j.Log4j2;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;

@Controller
@Log4j2
@RequestMapping("/quartz")
public class QuartzController {

    @Autowired
    private JobService jobService;
    @Autowired
    private DynamicJobService dynamicJobService;

    @RequestMapping(value = {""})
    private String main(){
        return "main";
    }

    @RequestMapping(value = {"index"})
    private String index(){
        return "task/index";
    }

    @RequestMapping(value = {"cron"})
    public String cron(){
        return "task/cron";
    }

    @RequestMapping(value = {"form/{mode}"})
    private String form(@PathVariable String mode){
        return "task/add";
    }

    @PostMapping(value = {"list"})
    @ResponseBody
    private Result list(JobEntity jobEntity,Integer pageNo,Integer pageSize){
        List<JobEntity> list = jobService.findList(jobEntity);
        return Result.ok(list);
    }

    /**
     * 新增任务
     * @param jobEntity
     * @return
     */
    @PostMapping("/add")
    @ResponseBody
    private Result add(JobEntity jobEntity){
        try {
            if (!validCron(jobEntity)){
                log.info("Job jump name : {} , Because {} cron {} is invalid", jobEntity.getName(), jobEntity.getName(), jobEntity.getCron());
                return Result.error("无效执行时间");
            }
            jobService.save(jobEntity);
            dynamicJobService.addJob(jobEntity);
        }catch (Exception e){
            log.error("add job {} failed ! throw ex: {}",jobEntity.getName(),e);
            return Result.error();
        }
        return Result.ok();
    }

    /**
     * 执行任务
     * @param jobEntity
     * @return
     */
    @PostMapping("/trigger")
    @ResponseBody
    private Result trigger(JobEntity jobEntity){
        try {
            jobEntity = jobService.getById(jobEntity.getId());
            TaskUtil.executionLogicMethod(jobEntity);
        }catch (Exception e){
            log.error("trigger job {} failed ! throw ex: {}",jobEntity.getName(),e);
            return Result.error();
        }
        return Result.ok();
    }

    /**
     * 移除任务
     * @param jobEntity
     * @return
     */
    @PostMapping("/remove")
    @ResponseBody
    public Result remove(JobEntity jobEntity){
        try {
            jobEntity = jobService.getById(jobEntity.getId());
            dynamicJobService.removeJob(jobEntity);
            jobService.delete(jobEntity.getId());
        }catch (Exception e){
            log.error("remove job {} failed ! throw ex: {}",jobEntity.getName(),e);
            return Result.error();
        }
        return Result.ok();
    }

    /**
     * 停止任务
     * @param jobEntity
     * @return
     */
    @PostMapping("/pause")
    @ResponseBody
    public Result pause(JobEntity jobEntity){
        try {
            jobEntity = jobService.getById(jobEntity.getId());
            jobEntity.setStatus(Constants.Status.CLOSE);
            jobService.update(jobEntity);
            dynamicJobService.removeJob(jobEntity);
        }catch (Exception e){
            log.error("remove job {} failed ! throw ex: {}",jobEntity.getName(),e);
            return Result.error();
        }
        return Result.ok();
    }

    /**
     * 恢复任务
     * @param jobEntity
     * @return
     */
    @PostMapping("/resume")
    @ResponseBody
    public Result resume(JobEntity jobEntity){
        try {
            jobEntity = jobService.getById(jobEntity.getId());
            if (!validCron(jobEntity)){
                log.info("Job jump name : {} , Because {} cron {} is invalid", jobEntity.getName(), jobEntity.getName(), jobEntity.getCron());
                return Result.error("无效执行时间");
            }
            jobEntity.setStatus(Constants.Status.OPEN);
            jobService.update(jobEntity);
            dynamicJobService.removeJob(jobEntity);
            dynamicJobService.addJob(jobEntity);
        }catch (Exception e){
            log.error("remove job {} failed ! throw ex: {}",jobEntity.getName(),e);
            return Result.error();
        }
        return Result.ok();
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
