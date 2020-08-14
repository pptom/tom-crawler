package com.tom.crawler.pipeline;

import com.tom.crawler.task.DownloadTask;
import com.tom.crawler.util.CrawlerConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Created by Tom on 2017/3/22.
 */
@Component
public class TomPipeline implements Pipeline {

    private static final Logger log = LoggerFactory.getLogger(TomPipeline.class);

    @Autowired
    private ExecutorService executorService;

    public void process(ResultItems resultItems, Task task) {
        //取出item
        String filePath = resultItems.get(CrawlerConstant.ResultItemKey.FILE_PATH);
        String fileUrl = resultItems.get(CrawlerConstant.ResultItemKey.FILE_URL);
        String fileName = resultItems.get(CrawlerConstant.ResultItemKey.FILE_NAME);
        try {
            download(fileUrl, fileName, filePath, 5);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //下载文件
//        DownUtil downUtil = new DownUtil(fileUrl, filePath, 3);
//        try {
//            downUtil.download();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        while (downUtil.getCompleteRate() < 1) {
//            log.info("文件[{}]下载中:{}", fileName, downUtil.getCompleteRate());
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        log.info("文件[{}]已完成!", fileName);
    }


    public void download(String fileUrl, String fileName, String filePath, int threadNum)
            throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        log.info("文件[{}],开始下载...", fileName);
        URL url = new URL(fileUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5 * 1000);
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept",
                "image/gif, image/jpeg, image/pjpeg, image/pjpeg, "
                        + "application/x-shockwave-flash, application/xaml+xml, "
                        + "application/vnd.ms-xpsdocument, application/x-ms-xbap, "
                        + "application/x-ms-application, application/vnd.ms-excel, "
                        + "application/vnd.ms-powerpoint, application/msword, */*");
        conn.setRequestProperty("Accept-Language", "zh-CN");
        conn.setRequestProperty("Charset", "UTF-8");
        conn.setRequestProperty("Connection", "Keep-Alive");
        // 得到文件大小
        int fileSize = conn.getContentLength();
        conn.disconnect();
        int currentPartSize = fileSize / threadNum + 1;//这里不必一定要加1,不加1也可以
        RandomAccessFile file = new RandomAccessFile(filePath, "rw");
        // 设置本地文件的大小
        file.setLength(fileSize);
        file.close();
        ExecutorCompletionService<Integer> executorCompletionService = new ExecutorCompletionService<>(executorService);
        for (int i = 0; i < threadNum; i++) {
            // 计算每条线程的下载的开始位置
            int startPos = i * currentPartSize;
            DownloadTask downloadTask = new DownloadTask(startPos, currentPartSize, fileUrl, filePath);
            executorCompletionService.submit(downloadTask);
        }
        //
        int completionTask = 0;
        int totalSize = 0;
        while (completionTask < threadNum) {
            //如果完成队列中没有数据, 则阻塞; 否则返回队列中的数据
            Future<Integer> resultHolder = executorCompletionService.take();
            int result = resultHolder.get();
            totalSize += result;
            completionTask++;
        }
        stopWatch.stop();
        log.info("文件[{}],大小[{}],下载完成!总耗时[{}]ms", fileName, fileSize, stopWatch.getLastTaskTimeMillis());
    }
}
