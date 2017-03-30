# tom-crawler
**spring整合webmagic，mybatis，dungproxy**,你也可以自行整合spring mvc  

---
**webmaigc**：官方网站[http://webmagic.io](http://webmagic.io)
> webmagic是一个开源的Java垂直爬虫框架，目标是简化爬虫的开发流程，让开发者专注于逻辑功能的开发。webmagic的核心非常简单，但是覆盖爬虫的整个流程，也是很好的学习爬虫开发的材料。

**DungProxy项目主页**
- oshina http://git.oschina.net/virjar/proxyipcenter
- github https://github.com/virjar/dungproxy
> DungProxy是一个代理IP服务,他包括一个代理IP资源server端和一系列适配中心IP资源得客户端。server负责代理IP资源的收集维护。client则是一系列方便用户使用得API,他屏蔽了代理IP下载、代理IP选取、IP绑定、IP切换等比较复杂逻辑。用户只需要引入client即可方便使用代理IP服务

---
# tom-crawler的主要特色：
- 使用spring来管理整个项目的bean
- 只需实现DungProxyDownloader即可使用DungProxy，自动切换IP，绕过反爬虫检测
- 整合mybatis，方便将爬取的数据存进数据库
