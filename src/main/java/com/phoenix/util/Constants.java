package com.phoenix.util;

public interface Constants {

    /**
     * JobEntity status 属性值说明
     */
    class Status{
        public static final String OPEN = "OPEN"; //开
        public static final String CLOSE = "CLOSE"; //关
    }

    /**
     * JobEntity jobGroup 属性值说明
     */
    class JobGroup{
        public static final String ONE = "one";         //表示该任务只执行一次
        public static final String REPEAT = "repeat";   //表示该任务可重复执行
    }
}
