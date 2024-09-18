package com.sky.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 功能:
 * 作者:
 * 日期:2024/09/18 13:58
 */
@Component  // 交给spring 容器管理
@Slf4j
public class MyTask {


    // 定时任务
//    @Scheduled(cron = "0/5 * * * * ?")
//    public void executeTask() {
//        log.info("MyTask execute task,{}", new Date());
//
//    }
}
