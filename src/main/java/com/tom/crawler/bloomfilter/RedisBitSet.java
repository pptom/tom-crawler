package com.tom.crawler.bloomfilter;

import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author pptom
 * @date 2020/6/8
 * @email 424222352@qq.com
 * @description Implement bloom filter on redis bitset.
 * @since 2020/6/8
 */
public class RedisBitSet implements BaseBitSet {

    private RedisTemplate<String, String> redisTemplate;

    private String name;

    public RedisBitSet(RedisTemplate<String, String> redisTemplate, String name) {
        this.redisTemplate = redisTemplate;
        this.name = name;
    }

    @Override
    public void set(int bitIndex) {
        redisTemplate.opsForValue().setBit(this.name, bitIndex, true);
    }

    @Override
    public void set(int bitIndex, boolean value) {
        redisTemplate.opsForValue().setBit(this.name, bitIndex, value);
    }

    @Override
    public boolean get(int bitIndex) {
        return redisTemplate.opsForValue().getBit(this.name, bitIndex);
    }

    @Override
    public void clear(int bitIndex) {
        redisTemplate.opsForValue().setBit(this.name, bitIndex, false);
    }

    @Override
    public void clear() {
        redisTemplate.delete(this.name);
    }

    @Override
    public long size() {
        return redisTemplate.opsForValue().size(this.name);
    }

    @Override
    public boolean isEmpty() {
        return size() <= 0;
    }
}
