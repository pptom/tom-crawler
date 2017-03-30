package com.tom.crawler;

import com.tom.crawler.service.SpiderService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by Tom on 2017/3/24.
 */
public class TomStart {

    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring-config.xml");
        SpiderService spiderService = applicationContext.getBean(SpiderService.class);
        String[] urls = {"http://www.tangzhijie.com"};
        spiderService.startSpider(urls, 5);
    }
}
