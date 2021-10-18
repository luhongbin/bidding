package org.linlinjava.litemall.admin.handler.impl;

import com.alibaba.fastjson.JSONObject;
import org.linlinjava.litemall.admin.handler.EventHandler;
import org.springframework.stereotype.Service;

/**
 * 回调测试url，不需要处理
 */
@Service
public class CheckUrlEventHandler implements EventHandler {

    @Override
    public void doHandler(JSONObject eventJson) {

    }
}
