package com.tom.crawler.service.impl;

import com.tom.crawler.service.TomService;
import com.virjar.dungproxy.client.httpclient.HeaderBuilder;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Site;

/**
 * Created by Tom on 2017/3/28.
 */
@Service
public class TomServiceImpl implements TomService {
    @Override
    public void changeUserAgent(Site site) {
        String randomUserAgent = HeaderBuilder.randomUserAgent();
        site.setUserAgent(randomUserAgent);
    }
}
