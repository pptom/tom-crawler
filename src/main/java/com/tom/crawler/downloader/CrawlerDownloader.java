package com.tom.crawler.downloader;

import org.apache.commons.io.IOUtils;
import org.apache.http.*;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.AbstractDownloader;
import us.codecraft.webmagic.downloader.HttpClientRequestContext;
import us.codecraft.webmagic.downloader.HttpUriRequestConverter;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.ProxyProvider;
import us.codecraft.webmagic.selector.PlainText;
import us.codecraft.webmagic.utils.CharsetUtils;
import us.codecraft.webmagic.utils.HttpClientUtils;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

@Contract(threading = ThreadingBehavior.SAFE)
public class CrawlerDownloader extends AbstractDownloader {

    private final Logger logger = LoggerFactory.getLogger(CrawlerDownloader.class);

    private final TomHttpClientGenerator tomHttpClientGenerator = new TomHttpClientGenerator();

    private final HttpUriRequestConverter httpUriRequestConverter = new HttpUriRequestConverter();

    private final Map<String, CloseableHttpClient> httpClients = new HashMap<>();

    private ProxyProvider proxyProvider;

    private CloseableHttpClient getHttpClient(Site site) {
        if (site == null) {
            return tomHttpClientGenerator.getClient(null);
        }
        String domain = site.getDomain();
        CloseableHttpClient httpClient = httpClients.get(domain);
        synchronized (this) {
            if (httpClient == null) {
                httpClient = httpClients.get(domain);
                if (httpClient == null) {
                    httpClient = tomHttpClientGenerator.getClient(site);
                    httpClients.put(domain, httpClient);
                }
            }
        }
        return httpClient;
    }

    @Override
    public Page download(Request request, Task task) {
        if (task == null || task.getSite() == null) {
            throw new NullPointerException("task or site can not be null");
        }
        CloseableHttpResponse httpResponse = null;
        CloseableHttpClient httpClient = getHttpClient(task.getSite());
        Proxy proxy = proxyProvider != null ? proxyProvider.getProxy(task) : null;
        HttpClientRequestContext requestContext = httpUriRequestConverter.convert(request, task.getSite(), proxy);
        Page page = Page.fail();
        try {
            httpResponse = httpClient.execute(requestContext.getHttpUriRequest(), requestContext.getHttpClientContext());
            page = handleResponse(request, request.getCharset() != null ? request.getCharset() : task.getSite().getCharset(), httpResponse, task);
            onSuccess(request);
            logger.debug("downloading page success {}", request.getUrl());
        } catch (IOException e) {
            if (e instanceof ConnectionClosedException) {
                logger.error("Premature end of chunk coded message body: {}", request.getUrl());
            } else if (e instanceof SSLHandshakeException) {
                logger.error("Remote host closed connection during handshake: {}", request.getUrl());
            } else if (e instanceof SSLException) {
                logger.error("SSL peer shut down incorrectly:[HttpClient]  {}", request.getUrl());
            } else if (e instanceof SocketTimeoutException) {
                logger.error("download page time out:{}", request.getUrl());
            } else if (e instanceof NoHttpResponseException) {
                logger.error("failed to respond:{}", request.getUrl());
            } else if (e instanceof HttpHostConnectException) {
                logger.error("Connect to proxy timed out:{}", request.getUrl());
            } else if (e instanceof TruncatedChunkException) {
                logger.error("TruncatedChunkException:{}, msg:{}", request.getUrl(), e.getMessage());
            } else {
                logger.error("download page error:{} ", request.getUrl(), e);
            }
            onError(request);
        } finally {
            if (httpResponse != null) {
                //ensure the connection is released back to pool
                EntityUtils.consumeQuietly(httpResponse.getEntity());
            }
            if (proxyProvider != null && proxy != null) {
                proxyProvider.returnProxy(proxy, page, task);
            }
        }
        return page;
    }

    protected Page handleResponse(Request request, String charset, HttpResponse httpResponse, Task task) throws IOException {
        HttpEntity entity = httpResponse.getEntity();
        if (entity != null) {
            entity = new BufferedHttpEntity(entity);
        }
        byte[] bytes = IOUtils.toByteArray(entity.getContent());
        String contentType = entity.getContentType() == null ? "" : entity.getContentType().getValue();
        Page page = new Page();
        page.setBytes(bytes);
        if (!request.isBinaryContent()){
            if (charset == null) {
                charset = getHtmlCharset(contentType, bytes);
            }
            page.setCharset(charset);
            page.setRawText(new String(bytes, charset));
        }
        page.setUrl(new PlainText(request.getUrl()));
        page.setRequest(request);
        page.setStatusCode(httpResponse.getStatusLine().getStatusCode());
        page.setDownloadSuccess(true);
        page.setHeaders(HttpClientUtils.convertHeaders(httpResponse.getAllHeaders()));
        return page;
    }

    private String getHtmlCharset(String contentType, byte[] contentBytes) throws IOException {
        String charset = CharsetUtils.detectCharset(contentType, contentBytes);
        if (charset == null) {
            charset = Charset.defaultCharset().name();
            logger.warn("Charset autodetect failed, use {} as charset. Please specify charset in Site.setCharset()", Charset.defaultCharset());
        }
        return charset;
    }

    @Override
    public void setThread(int threadNum) {
        tomHttpClientGenerator.setPoolSize(threadNum);
    }
}
