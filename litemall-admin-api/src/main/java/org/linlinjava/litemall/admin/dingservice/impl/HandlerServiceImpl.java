package org.linlinjava.litemall.admin.dingservice.impl;

import com.alibaba.fastjson.JSONObject;
import org.linlinjava.litemall.admin.factory.EventHandlerFactoryProducer;
import org.linlinjava.litemall.admin.dingservice.HandlerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class HandlerServiceImpl implements HandlerService {

    @Autowired
    private EventHandlerFactoryProducer eventHandlerFactoryProducer;

    /**
     * 调用处理接口处理业务逻辑，重试3次
     * @param eventJson
     */
    @Retryable
    @Override
    public void handler(JSONObject eventJson) {

        log.info("开始处理业务逻辑");

        String eventType = eventJson.getString("EventType");

        // 根据EventType分类处理
//        eventHandlerFactoryProducer.getEventHandlerFactory(eventType).getEventHandler(eventType).doHandler(eventJson);

        log.info("业务逻辑处理完毕");

    }
}
