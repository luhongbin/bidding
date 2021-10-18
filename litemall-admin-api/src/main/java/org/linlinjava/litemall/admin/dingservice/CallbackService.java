package org.linlinjava.litemall.admin.dingservice;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;

public interface CallbackService {

    Map<String, String> callback(String msgSignature, String timeStamp, String nonce, JSONObject json);
}
