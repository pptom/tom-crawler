package com.tom.crawler.service;

/**
 * Created by Tom on 2017/3/27.
 */
public interface SpiderService {
    /**
     *
     * @param urls 起始urls
     * @param threads 线程数
     */
    void startSpider(String[] urls, int threads);

}
