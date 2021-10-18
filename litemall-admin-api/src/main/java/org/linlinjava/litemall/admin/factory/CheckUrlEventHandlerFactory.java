package org.linlinjava.litemall.admin.factory;

import org.linlinjava.litemall.admin.config.ApplicationContextHolder;
import org.linlinjava.litemall.admin.handler.EventHandler;
import org.linlinjava.litemall.admin.handler.impl.CheckUrlEventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 回调测试事件工厂
 */
@Component
public class CheckUrlEventHandlerFactory extends AbstractEventHandlerFactory {

    @Autowired
    private ApplicationContextHolder applicationContextHolder;

    @Override
    public EventHandler getEventHandler(String eventType) {
        return applicationContextHolder.getApplicationContext().getBean(CheckUrlEventHandler.class);
    }
}
