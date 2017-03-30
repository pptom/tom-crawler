package com.tom.crawler.downloader;

import com.virjar.dungproxy.client.webmagic.DungProxyDownloader;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.annotation.ThreadSafe;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;

@Component
@ThreadSafe
public class TomDownloader extends DungProxyDownloader {
    @Override
    protected boolean needOfflineProxy(Page page) {
        if( super.needOfflineProxy(page)){//父类默认下线 401和403,你也可以不调用
            return true;
        }else{
            //检测到到对应字符串则下线ip
            return StringUtils.containsIgnoreCase(page.getRawText(), "检测的字符串");
        }
    }
}
