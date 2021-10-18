package org.linlinjava.litemall.admin.factory;


import org.linlinjava.litemall.admin.handler.EventHandler;

/**
 * 事件处理抽象工厂
 */
public abstract class AbstractEventHandlerFactory {

    public abstract EventHandler getEventHandler(String eventType);

}
