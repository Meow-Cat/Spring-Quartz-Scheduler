package com.phoenix.job;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class MyJob2 {

    public void doJob(String id){
        log.info("MyJob2执行方法doJob,参数id: {}",id);
    }
}