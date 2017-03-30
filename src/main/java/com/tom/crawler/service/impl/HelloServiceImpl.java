package com.tom.crawler.service.impl;

import com.tom.crawler.mapper.HelloMapper;
import com.tom.crawler.model.Hello;
import com.tom.crawler.service.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Mr Tom on 2017/3/25.
 */
@Service
public class HelloServiceImpl implements HelloService {
    @Autowired
    private HelloMapper helloMapper;
    @Override
    public Hello findById(Integer id) {
        return helloMapper.findById(id);
    }
}
