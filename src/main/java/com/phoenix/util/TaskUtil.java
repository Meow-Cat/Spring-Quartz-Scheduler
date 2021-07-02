package com.phoenix.util;

import com.phoenix.entity.JobEntity;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.quartz.impl.triggers.CronTriggerImpl;

import java.lang.reflect.Method;
import java.util.Date;

@Log4j2
public class TaskUtil {

    //执行任务逻辑代码
    public static void executionLogicMethod(JobEntity job){
        try {
            Class<?> cls = Class.forName(job.getExecutionClass()); // 取得Class对象
            if (StringUtils.isNotBlank(job.getMethod())) {
                Method doJob = cls.getDeclaredMethod(job.getMethod(),String.class); //获得get方法
                doJob.invoke(cls.newInstance(),job.getParameter());//调用方法
            }
        }catch (Exception e){
            log.error("TaskUtils.executionLogicMethod execution failed , Because job name '{}' execution class {} not found",job.getName(),job.getExecutionClass());
        }
    }

    //cron 校验方法
    public static boolean isValidExpression(final String cronExpression,Date now){
        CronTriggerImpl trigger = new CronTriggerImpl();
        try {
            trigger.setCronExpression(cronExpression);
            Date date = trigger.computeFirstFireTime(null);
            log.info("TaskUtils.isValidExpression execution result : {}",date != null && date.after(now));
            return date != null && date.after(now);
        } catch (Exception e) {
            log.error("TaskUtils.isValidExpression execution failed. throw ex:" , e);
        }
        return false;
    }
}
