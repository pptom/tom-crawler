package com.tom.crawler.task;

import com.virjar.dungproxy.client.ippool.PreHeater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by Tom on 2017/3/27.
 * spring task 定时任务测试，适用于单系统
 * 注意：不适合用于集群
 */
@Component
public class HelloTask {
    //添加日志
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Scheduled(cron = "0 1/1 * * * ?")
    public void helloTest() {
        logger.info("现在时间为:{}", new Date());

    }
}
