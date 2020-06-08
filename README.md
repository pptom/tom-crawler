# tom-crawler
**基于Spring Boot 2.0 整合了 WebMagic爬虫框架**

---
**WebMagic**：官方网站[http://webmagic.io](http://webmagic.io)
> webmagic是一个开源的Java垂直爬虫框架，目标是简化爬虫的开发流程，让开发者专注于逻辑功能的开发。webmagic的核心非常简单，但是覆盖爬虫的整个流程，也是很好的学习爬虫开发的材料。

**基于redis的布隆过滤器** 
- 布隆过滤器可用于重复Url的过滤，能快速检查一个元素是否存在一个集合中。
- 结合redis可以实现将布隆过滤器存放到redis中，可实现重启服务不丢失抓取URL队列
- 具体实现参考该项目https://github.com/wxisme/bloomfilter
---
# tom-crawler的主要特色：
- 使用spring来管理整个项目的bean
- 整合Spring Boot可以快速部署
- 支持亿级的URL快速去重
- 支持httpClient连接池
- TODO 整合代理配置
