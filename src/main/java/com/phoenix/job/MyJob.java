package com.phoenix.job;

import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;

@Log4j2
public class MyJob {

    public void doJob(String id){
        log.info("MyJob执行方法doJob,参数id: {}",id);
    }
}
