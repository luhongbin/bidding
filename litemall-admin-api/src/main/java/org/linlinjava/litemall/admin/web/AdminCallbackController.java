package org.linlinjava.litemall.admin.web;

import com.alibaba.fastjson.JSONObject;
import org.linlinjava.litemall.admin.dingservice.callback.CallbackService;
import lombok.extern.slf4j.Slf4j;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


/**
 * ISV 小程序回调信息处理
 */
@RestController
@RequestMapping("/admin/callback")
@Slf4j
public class AdminCallbackController {
    @Autowired
    private CallbackService callbackService;

    @PostMapping("/dingtalk")
    public Map<String, String> callback(@RequestParam(value = "msg_signature") String msgSignature,
                                        @RequestParam(value = "timestamp") String timeStamp,
                                        @RequestParam(value = "nonce") String nonce,
                                        @RequestBody JSONObject json) {
        return callbackService.callback(msgSignature, timeStamp, nonce, json);
    }
    /**
     * 欢迎页面
     */
    @RequestMapping(value = "/welcome", method = RequestMethod.GET)
    public Object welcome() {
        return ResponseUtil.ok();
    }
}
