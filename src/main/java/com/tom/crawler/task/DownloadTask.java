package com.tom.crawler.task;

import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

public class DownloadTask implements Callable<Integer> {

    // 当前线程的下载位置
    private final int startPos;
    // 定义当前线程负责下载的文件大小
    private final int currentPartSize;
    //下载路径
    private final String fileUrl;
    //文件路径
    private final String filePath;

    public DownloadTask(int startPos, int currentPartSize, String fileUrl, String filePath) {
        this.startPos = startPos;
        this.currentPartSize = currentPartSize;
        this.fileUrl = fileUrl;
        this.filePath = filePath;
    }

    @Override
    public Integer call() throws Exception {
        // 每个线程使用一个RandomAccessFile进行下载
        RandomAccessFile currentPart = new RandomAccessFile(filePath, "rw");
        // 定位该线程的下载位置
        currentPart.seek(startPos);
        URL url = new URL(fileUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5 * 1000);
        conn.setRequestMethod("GET");
        conn.setRequestProperty(
                "Accept",
                "image/gif, image/jpeg, image/pjpeg, image/pjpeg, "
                        + "application/x-shockwave-flash, application/xaml+xml, "
                        + "application/vnd.ms-xpsdocument, application/x-ms-xbap, "
                        + "application/x-ms-application, application/vnd.ms-excel, "
                        + "application/vnd.ms-powerpoint, application/msword, */*");
        conn.setRequestProperty("Accept-Language", "zh-CN");
        conn.setRequestProperty("Charset", "UTF-8");
        InputStream inStream = conn.getInputStream();
        // 跳过startPos个字节，表明该线程只下载自己负责哪部分文件。
        inStream.skip(this.startPos);
        byte[] buffer = new byte[1024];
        int hasRead = 0;
        //定义已经该线程已下载的字节数
        int length = 0;
        // 读取网络数据，并写入本地文件
        while (length < currentPartSize && (hasRead = inStream.read(buffer)) != -1) {
            currentPart.write(buffer, 0, hasRead);
            // 累计该线程下载的总大小
            length += hasRead;
        }
        currentPart.close();
        inStream.close();
        return currentPartSize;
    }
}
