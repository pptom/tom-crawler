package com.tom.crawler.processor;

import com.tom.crawler.util.CrawlerConstant;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.io.File;
import java.util.Map;

/**
 * Created by Tom on 2017/3/22.
 */
@Component
public class TomProcessor implements PageProcessor {

    //添加日志
    private static final Logger log = LoggerFactory.getLogger(TomProcessor.class);


    //设置请求头，重试次数，超时时间
    private final Site site = Site.me()
            .setRetryTimes(3)
            .setSleepTime(100)
            .setTimeOut(30000)
            .setDomain("")
            .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.100 Safari/537.36")
            .setCycleRetryTimes(3);

    public void process(Page page) {
        //输出一下site的值
        log.info("site info:{}", site.getUserAgent());
        Document document = Jsoup.parse(page.getRawText());
        Map<String, Object> extras = page.getRequest().getExtras();
        String path = CrawlerConstant.DOWNLOAD_PATH;
        if (extras != null) {
            path = (String) extras.getOrDefault(CrawlerConstant.CURRENT_PATH_KEY, path);
        }
        if (document != null) {
            Elements h1Tag = document.body().getElementsByTag("h1");
            if (h1Tag != null && h1Tag.size() > 0) {
                log.info("正在获取:{}", h1Tag.first().text());
            }
            Elements elements = document.select("body > table > tbody > tr > td:nth-child(1) > a");
            if (elements != null && elements.size() > 0) {
                for (int i = 1; i < elements.size(); i++) {
                    Element element = elements.get(i);
                    String href = element.attr("href");
                    String text = element.text();
                    log.info("-->text:{},url:{}", text, href);
                    //如果是目录
                    if (text.endsWith("/")) {
                        String urlPath = path + File.separator + text.substring(0, text.length() - 1);
                        File file = new File(urlPath);
                        //创建目录
                        if (!file.exists()) {
                            file.mkdirs();
                        }
                        //
                        Request request = new Request(href);
                        request.putExtra(CrawlerConstant.CURRENT_PATH_KEY, urlPath);
                        page.addTargetRequest(request);
                    } else {
                        String filePath = path + File.separator + text;
                        page.putField(CrawlerConstant.ResultItemKey.FILE_NAME, text);
                        page.putField(CrawlerConstant.ResultItemKey.FILE_URL, href);
                        page.putField(CrawlerConstant.ResultItemKey.FILE_PATH, filePath);
                    }
                }

            }
        }
    }

    public Site getSite() {
        return site;
    }
}
