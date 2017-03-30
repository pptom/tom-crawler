package com.tom.crawler.pipeline;

import com.tom.crawler.service.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

/**
 * Created by Tom on 2017/3/22.
 */
@Component
public class TomPipeline implements Pipeline {
    @Autowired
    private HelloService helloService;

    public void process(ResultItems resultItems, Task task) {
          //取出item
//        String test = resultItems.get("test");
          //调用service 保存到数据库
//        helloService.save(xxxx);
    }
}
