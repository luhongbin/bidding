package org.linlinjava.litemall.admin.web;

import com.alibaba.fastjson.JSONObject;
import org.linlinjava.litemall.admin.dingservice.CallbackService;
import lombok.extern.slf4j.Slf4j;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.linlinjava.litemall.admin.constant.Constant;
import com.dingtalk.oapi.lib.aes.DingTalkEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
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
        String params = "signature:" + msgSignature + " timestamp:" + timeStamp + " nonce:" + nonce + " body:" + json;
        try {
//            log.info("begin callback:" + params);
            DingTalkEncryptor dingTalkEncryptor = new DingTalkEncryptor("lutec123456", "JEE8PmBrPlXRSPlgrGF1Ug6KUnS6AvxhsyIhkvZ3Lc8","ding8c0384f4285df03035c2f4657eb6378f");

            // 从post请求的body中获取回调信息的加密数据进行解密处理
            String encrypt = json.getString("encrypt");
            String plainText = dingTalkEncryptor.getDecryptMsg(msgSignature, timeStamp.toString(), nonce, encrypt);
            JSONObject callBackContent = JSON.parseObject(plainText);

//            // 根据回调事件类型做不同的业务处理
//            String eventType = callBackContent.getString("EventType");
//            if (EVENT_CHECK_CREATE_SUITE_URL.equals(eventType)) {
//                log.info("验证新创建的回调URL有效性: " + plainText);
//            } else if (EVENT_CHECK_UPADTE_SUITE_URL.equals(eventType)) {
//                log.info("验证更新回调URL有效性: " + plainText);
//            } else if (EVENT_SUITE_TICKET.equals(eventType)) {
//                // suite_ticket用于用签名形式生成accessToken(访问钉钉服务端的凭证)，需要保存到应用的db。
//                // 钉钉会定期向本callback url推送suite_ticket新值用以提升安全性。
//                // 应用在获取到新的时值时，保存db成功后，返回给钉钉success加密串（如本demo的return）
//                log.info("应用suite_ticket数据推送: " + plainText);
//            } else if (EVENT_TMP_AUTH_CODE.equals(eventType)) {
//                // 本事件应用应该异步进行授权开通企业的初始化，目的是尽最大努力快速返回给钉钉服务端。用以提升企业管理员开通应用体验
//                // 即使本接口没有收到数据或者收到事件后处理初始化失败都可以后续再用户试用应用时从前端获取到corpId并拉取授权企业信息，进而初始化开通及企业。
//                log.info("企业授权开通应用事件: " + plainText);
//            } else {
//                // 其他类型事件处理
//            }
            log.info("企业授权开通应用事件: " + encrypt+ ",返回内容:" + callBackContent);

            // 返回success的加密信息表示回调处理成功
//            log.info(dingTalkEncryptor.getEncryptedMap("success", timeStamp, nonce));
        } catch (Exception e) {
            //失败的情况，应用的开发者应该通过告警感知，并干预修复
            log.error("process callback fail." + params, e);
        }
        log.info("test");

//        log.info("CallbackController#callback params: msg_signature: {}, timeStamp: {}, nonce: {}, json: {}", msgSignature, timeStamp, nonce, json);
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
