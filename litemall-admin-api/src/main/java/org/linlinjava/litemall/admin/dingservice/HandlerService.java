package org.linlinjava.litemall.admin.dingservice;

import com.alibaba.fastjson.JSONObject;

public interface HandlerService {

    void handler(JSONObject eventJson);
}
