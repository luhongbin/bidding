package org.linlinjava.litemall.admin.job;


import com.alibaba.fastjson.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linlinjava.litemall.admin.service.AdminQuoteService;
import org.linlinjava.litemall.core.util.JacksonUtil;
import org.linlinjava.litemall.db.domain.LitemallCoupon;
import org.linlinjava.litemall.db.domain.LitemallCouponUser;
import org.linlinjava.litemall.db.service.LitemallCouponService;
import org.linlinjava.litemall.db.service.LitemallCouponUserService;
import org.linlinjava.litemall.db.service.LitemallAdminService;
import org.linlinjava.litemall.db.domain.LitemallQuoteBill;
import org.linlinjava.litemall.db.domain.LitemallAdmin;
import org.linlinjava.litemall.db.domain.LitemallRequote;
import org.linlinjava.litemall.db.service.LitemallQuoteBillService;
import org.linlinjava.litemall.db.service.LitemallRequoteService;
import org.linlinjava.litemall.db.util.CouponConstant;
import org.linlinjava.litemall.db.util.CouponUserConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.linlinjava.litemall.admin.util.DingtalkApi;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 检测优惠券过期情况
 */
@Component
public class CouponJob {
    private final Log logger = LogFactory.getLog(CouponJob.class);
    @Autowired
    private LitemallQuoteBillService quoteBillService;
    @Autowired
    private LitemallRequoteService reQuoteService;
    @Autowired
    private LitemallCouponService couponService;
    @Autowired
    private LitemallCouponUserService couponUserService;
    @Autowired
    private LitemallAdminService adminService;
    @Autowired
    private AdminQuoteService adminQuoteService;

    /**
     * 每隔一个小时检查
     * TODO
     * 注意，因为是相隔一个小时检查，因此导致优惠券真正超时时间可能比设定时间延迟1个小时
     */
    @Scheduled(fixedDelay = 60 * 10 * 1000)
    public void checkCouponExpired() {
//        logger.info("系统开启任务询价单是否已经过期");
        //这个是通过钉钉获取的机器人的连接，需要PC版才可以

        List<LitemallQuoteBill> quoteList = quoteBillService.queryExpired();
        for (LitemallQuoteBill quote : quoteList) {
            quote.setStatus((short) 2);
            quoteBillService.updateById(quote);

            Map<String,Object> body = new HashMap<String,Object>();
            logger.info("String" + quote.getPurchaser());

            body.put("id", quote.getId());
            body.put("quoteId", 0);
            body.put("adminId", quote.getAdminId());
            body.put("billcode", 1);
            body.put("adminName",quote.getPurchaser());
            body.put("billname", "询价单");
            body.put("nextaction", "报价单解封审核");
            body.put("action", "等待初审");
            body.put("approveNote", "系统通知");
            body.put("setstatus", 2);
            body.put("idcard", 3);
            body.put("receiver", quote.getDutyCode());

            adminQuoteService.submitById(JSON.toJSONString(body));

            Integer userid = quote.getDutyCode();
            LitemallAdmin admin = adminService.findById(userid);
            String name = admin.getUsername();
            String name1 = admin.getNickname();

            if (quote.getAdminId() != quote.getDutyCode()) {
                LitemallAdmin admin1 = adminService.findById(quote.getAdminId());
                name = admin1.getUsername() + "," + name;
            }
//            List<String> mobiles = new ArrayList<>();
//            mobiles.add(admin.getMobile());
//            String msgrobot = "{ \"msgtype\": \"text\",  \"text\": { \"content\":\""+ msg +"\" }, \"at\": {\"atMobiles\": [\""+admin.getMobile()+"\"],\"isAtAll\":false} }";
            String msg = "核价负责人["+name1+"]你好:\n\n"+quote.getPurchaser()+"的第["+quote.getId()+"]号询价单开标时间到\n\n请你前去钉钉工作台的耀泰供应链平台审核";
            String infoSend = "{ \"userid_list\": \""+name+"\", \"agent_id\": \"1231569276\", msg:{ \"msgtype\": \"markdown\", \"markdown\": { \"title\": \"LUTEC询价单\", " +
                    "\"text\":\""+ msg +"\"} } }";
            System.out.println(infoSend);
            try { DingtalkApi.asyncsend(infoSend); } catch (Exception e) {  e.printStackTrace(); }
        }

        List<LitemallRequote> requoteList = reQuoteService.queryExpired();
        for (LitemallRequote requote : requoteList) {
            requote.setStatus((short) 6);
            reQuoteService.update(requote);
            Map<String,Object> body = new HashMap<String,Object>();
            body.put("id", requote.getId());
            body.put("quoteId", requote.getQuoteId());
            body.put("adminId", requote.getAdminId());
            body.put("billcode", 2);
            body.put("adminName",requote.getAdminName());
            body.put("billname", "报价单");
            body.put("nextaction", "结束");
            body.put("action", "报价超时作废");
            body.put("approveNote", "系统通知");
            body.put("setstatus", 6);
            body.put("idcard", 3);
            body.put("receiver", requote.getAdminId());
            adminQuoteService.submitById(JSON.toJSONString(body));

            Integer userid = requote.getAdminId();
            LitemallAdmin admin = adminService.findById(userid);
            String msg = "["+admin.getNickname()+"]你好:\n\n你的第["+requote.getId()+"]号报价单,重新报价截止日期已到\n\n本次报价资格已被取消";
            String infoSend = "{ \"userid_list\": \""+admin.getUsername()+"\", \"agent_id\": \"1231569276\", msg:{ \"msgtype\": \"markdown\", \"markdown\": { \"title\": \"LUTEC询价单\", " +
                    "\"text\":\""+ msg +"\"} } }";
            System.out.println(infoSend);
            try { DingtalkApi.asyncsend(infoSend); } catch (Exception e) {  e.printStackTrace(); }
        }

        List<LitemallQuoteBill> quoteList8 = quoteBillService.query8Expired();
        for (LitemallQuoteBill quote : quoteList8) {
            quote.setStatus((short) 2);
            quoteBillService.updateById(quote);

            Map<String,Object> body = new HashMap<String,Object>();
            logger.info("String" + quote.getPurchaser());

            body.put("id", quote.getId());
            body.put("quoteId", 0);
            body.put("adminId", quote.getAdminId());
            body.put("billcode", 1);
            body.put("adminName",quote.getPurchaser());
            body.put("billname", "请求重新报价");
            body.put("nextaction", "报价单解封审核");
            body.put("action", "等待初审");
            body.put("approveNote", "系统通知");
            body.put("setstatus", 2);
            body.put("idcard", 3);
            body.put("receiver", quote.getDutyCode());

            adminQuoteService.submitById(JSON.toJSONString(body));

            Integer userid = quote.getDutyCode();
            LitemallAdmin admin = adminService.findById(userid);
            String name = admin.getUsername();
            String name1 = admin.getNickname();

            if (quote.getAdminId() != quote.getDutyCode()) {
                LitemallAdmin admin1 = adminService.findById(quote.getAdminId());
                name = admin1.getUsername() + "," + name;
            }
//            List<String> mobiles = new ArrayList<>();
//            mobiles.add(admin.getMobile());
//            String msgrobot = "{ \"msgtype\": \"text\",  \"text\": { \"content\":\""+ msg +"\" }, \"at\": {\"atMobiles\": [\""+admin.getMobile()+"\"],\"isAtAll\":false} }";
            String msg = "核价负责人["+name1+"]你好:\n\n"+quote.getPurchaser()+"的第["+quote.getId()+"]号询价单开标时间到\n\n请你前去钉钉工作台的耀泰供应链平台审核";
            String infoSend = "{ \"userid_list\": \""+name+"\", \"agent_id\": \"1231569276\", msg:{ \"msgtype\": \"markdown\", \"markdown\": { \"title\": \"LUTEC询价单\", " +
                    "\"text\":\""+ msg +"\"} } }";
            System.out.println(infoSend);
            try { DingtalkApi.asyncsend(infoSend); } catch (Exception e) {  e.printStackTrace(); }
        }

        List<LitemallRequote> requoteList10 = reQuoteService.query10Expired();
        for (LitemallRequote requote : requoteList10) {
            requote.setStatus((short) 6);
            reQuoteService.update(requote);
            Map<String,Object> body = new HashMap<String,Object>();
            body.put("id", requote.getId());
            body.put("quoteId", requote.getQuoteId());
            body.put("adminId", requote.getAdminId());
            body.put("billcode", 2);
            body.put("adminName",requote.getAdminName());
            body.put("billname", "报价单重新报价");
            body.put("nextaction", "结束");
            body.put("action", "报价超时作废");
            body.put("approveNote", "系统通知");
            body.put("setstatus", 6);
            body.put("idcard", 3);
            body.put("receiver", requote.getAdminId());
            adminQuoteService.submitById(JSON.toJSONString(body));

            Integer userid = requote.getAdminId();
            LitemallAdmin admin = adminService.findById(userid);
            String msg = "["+admin.getNickname()+"]你好:\n\n你的第["+requote.getId()+"]号报价单,重新报价截止日期已到\n\n本次报价资格已被取消";
            String infoSend = "{ \"userid_list\": \""+admin.getUsername()+"\", \"agent_id\": \"1231569276\", msg:{ \"msgtype\": \"markdown\", \"markdown\": { \"title\": \"LUTEC询价单\", " +
                    "\"text\":\""+ msg +"\"} } }";
            System.out.println(infoSend);
            try { DingtalkApi.asyncsend(infoSend); } catch (Exception e) {  e.printStackTrace(); }
        }

        List<LitemallCoupon> couponList = couponService.queryExpired();
        for (LitemallCoupon coupon : couponList) {
            coupon.setStatus(CouponConstant.STATUS_EXPIRED);
            couponService.updateById(coupon);
        }

        List<LitemallCouponUser> couponUserList = couponUserService.queryExpired();
        for (LitemallCouponUser couponUser : couponUserList) {
            couponUser.setStatus(CouponUserConstant.STATUS_EXPIRED);
            couponUserService.update(couponUser);
        }
//        logger.info("系统结束任务检查询价单是否已经过期");
    }

}
