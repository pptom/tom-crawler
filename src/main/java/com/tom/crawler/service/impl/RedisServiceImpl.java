package com.tom.crawler.service.impl;

import com.alibaba.fastjson.JSON;
import com.tom.crawler.service.RedisService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Request;

import java.util.List;

@Service
public class RedisServiceImpl implements RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    public RedisServiceImpl(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void addTaskToQueue(String queueKey, String url, String itemKey, String field, String value, Request request) {
        List<Object> txResults = redisTemplate.execute(new SessionCallback<List<Object>>() {
            @Override
            public List<Object> execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                operations.opsForList().rightPush(queueKey, url);
                if (request != null && request.getExtras() != null) {
                    operations.opsForHash().put(itemKey, field, value);
                }
                // This will contain the results of all ops in the transaction
                return operations.exec();
            }
        });
    }

    @Override
    public Request pollRequest(String queueKey, String itemKey) {
        Request request = null;
        String url = redisTemplate.opsForList().rightPop(queueKey);
        if (url != null) {
            String field = DigestUtils.sha1Hex(url);
            String bytes = (String) redisTemplate.opsForHash().get(itemKey, field);
            if (bytes != null) {
                request = JSON.parseObject(bytes, Request.class);
                redisTemplate.opsForHash().delete(itemKey, field);
            } else {
                request = new Request(url);
            }
        }
        return request;
    }

    @Override
    public Long getLeftRequestsCount(String key) {
        return redisTemplate.opsForList().size(key);
    }
}
