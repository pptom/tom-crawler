package com.tom.spider.service;

import com.tom.crawler.service.SpiderService;
import com.virjar.dungproxy.client.ippool.PreHeater;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * Created by Tom on 2017/3/27.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-config.xml")
public class SpiderServiceTest {

    @Autowired
    private SpiderService spiderService;

    @Test
    public void startSpiderGetProduct() throws Exception {
        String[] urls = {"http://www.tangzhijie.com"};
        spiderService.startSpider(urls, 5);
    }

    @Before
    public void testDungProxyContext(){
        // 调用代理池初始化
        PreHeater.start();
    }
}