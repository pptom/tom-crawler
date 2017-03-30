package com.tom.crawler.service.impl;

import com.tom.crawler.downloader.TomDownloader;
import com.tom.crawler.pipeline.TomPipeline;
import com.tom.crawler.processor.TomProcessor;
import com.tom.crawler.service.SpiderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Spider;

/**
 * Created by Tom on 2017/3/27.
 */
@Service
public class SpiderServiceImpl implements SpiderService {


    @Autowired
    private TomProcessor tomProcessor;

    @Autowired
    private TomPipeline tomPipeline;

    @Autowired
    private TomDownloader tomDownloader;

    @Override
    public void startSpider(String[] urls, int threads) {
        Spider.create(tomProcessor)
                .addUrl(urls)
                .addPipeline(tomPipeline)
                .setDownloader(tomDownloader)
                .thread(threads).run();
    }

}
