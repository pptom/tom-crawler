package com.tom.crawler.runner;

import com.tom.crawler.downloader.CrawlerDownloader;
import com.tom.crawler.pipeline.TomPipeline;
import com.tom.crawler.scheduler.CrawlerScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

@Component
public class CrawlerRunner implements CommandLineRunner {

    @Autowired
    private Spider spider;

    @Autowired
    private TomPipeline tomPipeline;

    @Autowired
    private CrawlerScheduler crawlerScheduler;

    @Autowired
    private CrawlerDownloader crawlerDownloader;


    @Override
    public void run(String... args) throws Exception {
        //启动爬虫
        Request request = new Request("");
        spider.addRequest(request);
        spider.setScheduler(crawlerScheduler)
                .addPipeline(tomPipeline)
                .setDownloader(crawlerDownloader)
                .setExitWhenComplete(false)
                .thread(5)
                .start();
    }
}
