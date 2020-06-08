package com.tom.crawler.config;

import com.tom.crawler.bloomfilter.BaseBitSet;
import com.tom.crawler.bloomfilter.BloomFilter;
import com.tom.crawler.bloomfilter.RedisBitSet;
import com.tom.crawler.constant.RedisConstant;
import com.tom.crawler.scheduler.CrawlerScheduler;
import com.tom.crawler.service.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

@Configuration
public class CrawlerConfig {
    private final static Logger logger = LoggerFactory.getLogger(CrawlerConfig.class);

    private static final double FALSE_POSITIVE_PROBABILITY = 0.000001;

    private static final int EXPECTED_NUMBER_OF_ELEMENTS = 5000000;

    @Bean
    public Spider createSpider(PageProcessor pageProcessor) {
        logger.info("Spider init...");
        return Spider.create(pageProcessor);
    }

    @Bean
    public CrawlerScheduler scheduler(RedisService redisService, RedisTemplate<String, String> redisTemplate,
                                      Spider spider) {
        BloomFilter<String> bloomFilter = new BloomFilter<>(FALSE_POSITIVE_PROBABILITY, EXPECTED_NUMBER_OF_ELEMENTS);
        //初始化redisBitSet
        BaseBitSet bitSet = new RedisBitSet(redisTemplate, RedisConstant.getBitKey(spider));
        //绑定实现
        bloomFilter.bind(bitSet);
        logger.info("bloomFilter bind complete!");
        return new CrawlerScheduler(redisService, bloomFilter);
    }
}
