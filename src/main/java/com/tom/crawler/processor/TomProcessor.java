package com.tom.crawler.processor;

import com.tom.crawler.service.TomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * Created by Tom on 2017/3/22.
 */
@Component
public class TomProcessor implements PageProcessor {

    //添加日志
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private TomService tomService;

    //设置请求头，重试次数，超时时间
    private Site site = Site.me()
            .setRetryTimes(3)
            .setSleepTime(100)
            .setTimeOut(30000)
            .setCycleRetryTimes(3);


    public void process(Page page) {
        //输出一下site的值
        logger.info("site info:{}", site.getUserAgent());
        //修改下一次useragent的值
        tomService.changeUserAgent(site);
        //获得当前请求的url
        String url = page.getRequest().getUrl();
        //Todo

    }

    public Site getSite() {
        return site;
    }
}
