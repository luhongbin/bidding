package org.linlinjava.litemall.admin.dingservice.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.linlinjava.litemall.admin.util.DingCallbackCrypto;
import org.linlinjava.litemall.admin.config.AppConfig;
import org.linlinjava.litemall.admin.dingservice.CallbackService;
import org.linlinjava.litemall.admin.dingservice.HandlerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class CallbackServiceImpl implements CallbackService {

    @Autowired
    private HandlerService handlerService;

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private ThreadPoolTaskExecutor executor;

    @Override
    public Map<String, String> callback(String msgSignature, String timeStamp, String nonce, JSONObject json) {
        try {
            // 1.构建加解密方法，进行解密
            DingCallbackCrypto callbackCrypto = new DingCallbackCrypto(appConfig.getToken(), appConfig.getAesKey(), appConfig.getAppKey());
            String encryptMsg = json.getString("encrypt");
            String decryptMsg = callbackCrypto.getDecryptMsg(msgSignature, timeStamp, nonce, encryptMsg);

            // 2. 反序列化回调事件json数据
            JSONObject eventJson = JSON.parseObject(decryptMsg);
            log.info("eventJson: {}", eventJson);

            // 3. 异步处理业务逻辑
            executor.execute(() -> handlerService.handler(eventJson));


            // 4. 返回success的加密数据
            Map<String, String> successMap = callbackCrypto.getEncryptedMap("success");
            return successMap;

        } catch (DingCallbackCrypto.DingTalkEncryptException e) {
            e.printStackTrace();
            log.error("process callback failed！msg: {}", e);
            throw new RuntimeException("process callback failed！");
        }
    }

}
