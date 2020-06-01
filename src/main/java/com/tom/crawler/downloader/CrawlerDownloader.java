package com.tom.crawler.downloader;

import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.AbstractDownloader;

@Contract(threading = ThreadingBehavior.SAFE)
public class CrawlerDownloader extends AbstractDownloader {


    @Override
    public Page download(Request request, Task task) {
        return null;
    }

    @Override
    public void setThread(int threadNum) {

    }
}
