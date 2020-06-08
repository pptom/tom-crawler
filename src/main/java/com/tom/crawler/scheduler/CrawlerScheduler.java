package com.tom.crawler.scheduler;

import com.alibaba.fastjson.JSON;
import com.tom.crawler.bloomfilter.BloomFilter;
import com.tom.crawler.constant.RedisConstant;
import com.tom.crawler.service.RedisService;
import org.apache.commons.codec.digest.DigestUtils;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.DuplicateRemovedScheduler;
import us.codecraft.webmagic.scheduler.MonitorableScheduler;
import us.codecraft.webmagic.scheduler.component.DuplicateRemover;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author pptom
 * @date 2020/6/8
 * @email 424222352@qq.com
 * @description Implement bloom filter on redis bitset.
 * @since 2020/6/8
 */
public class CrawlerScheduler extends DuplicateRemovedScheduler implements MonitorableScheduler, DuplicateRemover {

    private final RedisService redisService;

    private final BloomFilter<String> bloomFilter;

    private AtomicInteger counter;

    public CrawlerScheduler(RedisService redisService, BloomFilter<String> bloomFilter) {
        //绑定操作url队列的redisService
        this.redisService = redisService;
        //初始化布隆过滤器
        this.bloomFilter = bloomFilter;
        //去重使用本类中的实现
        setDuplicateRemover(this);
        counter = new AtomicInteger(0);
    }

    @Override
    public void resetDuplicateCheck(Task task) {
        this.bloomFilter.clear();
        counter = new AtomicInteger(0);
    }

    @Override
    public boolean isDuplicate(Request request, Task task) {
        String url = request.getUrl();
        boolean isDuplicate = mightContain(url);
        if (!isDuplicate) {
            counter.incrementAndGet();
        }
        return isDuplicate;
    }

    @Override
    protected void pushWhenNoDuplicate(Request request, Task task) {
        String field = DigestUtils.sha1Hex(request.getUrl());
        String value = JSON.toJSONString(request);
        redisService.addTaskToQueue(RedisConstant.getQueueKey(task), request.getUrl(), RedisConstant.getItemKey(task), field, value, request);
    }

    @Override
    public synchronized Request poll(Task task) {
        return redisService.pollRequest(RedisConstant.getQueueKey(task), RedisConstant.getItemKey(task));
    }

    @Override
    public int getLeftRequestsCount(Task task) {
        Long size = redisService.getLeftRequestsCount(RedisConstant.getQueueKey(task));
        return size.intValue();
    }

    @Override
    public int getTotalRequestsCount(Task task) {
        return counter.get();
    }

    /**
     * 判断是否可能包含某个url
     *
     * @param str
     * @return true url可能已经存在
     * false url一定不存在
     */
    public boolean mightContain(String str) {
        boolean isDuplicate;
        synchronized (this) {
            isDuplicate = bloomFilter.contains(str);
            if (!isDuplicate) {
                bloomFilter.add(str);
            }
        }
        return isDuplicate;
    }

}
