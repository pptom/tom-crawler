package com.tom.crawler.service;

import us.codecraft.webmagic.Request;

public interface RedisService {

    /**
     * 将url放到队列
     *
     * @param queueKey
     * @param url
     * @param itemKey
     * @param field
     * @param value
     * @param request
     */
    void addTaskToQueue(String queueKey, String url, String itemKey, String field, String value, Request request);


    /**
     * 获取一个Url进行抓取
     *
     * @param queueKey
     * @param itemKey
     * @return
     */
    Request pollRequest(String queueKey, String itemKey);


    /**
     * 获取队列长度
     *
     * @param key
     * @return
     */
    Long getLeftRequestsCount(String key);
}
