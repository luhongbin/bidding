package org.linlinjava.litemall.admin.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.linlinjava.litemall.db.dao.LitemallQuoteBillMapper;
import org.linlinjava.litemall.db.dao.LitemallRequoteMapper;
import org.linlinjava.litemall.db.dao.LitemallQuoteDieCastingMapper;
import org.linlinjava.litemall.db.dao.LitemallQuoteRubberMapper;
import org.linlinjava.litemall.db.dao.LitemallQuoteHardwareMapper;
import org.linlinjava.litemall.db.dao.LitemallQuoteElectronicMapper;

import org.linlinjava.litemall.db.domain.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linlinjava.litemall.core.util.JacksonUtil;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.db.service.*;
import org.linlinjava.litemall.db.service.LitemallSystemConfigService;
import org.linlinjava.litemall.db.service.LitemallApproveInfoService;
import org.linlinjava.litemall.db.service.LitemallRequoteService;
import org.linlinjava.litemall.db.util.AftersaleConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.linlinjava.litemall.db.domain.LitemallAdmin;
import org.linlinjava.litemall.db.domain.LitemallQuoteBill;
import org.linlinjava.litemall.db.domain.LitemallRequote;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.linlinjava.litemall.admin.util.DingtalkApi;
import java.awt.Robot;
import java.util.Map;
/*
                   _ooOoo_
                  o8888888o
                  88" . "88
                  (| -_- |)
                  O\  =  /O
               ____/`---'\____
             .'  \\|     |//  `.
            /  \\|||  :  |||//  \
           /  _||||| -:- |||||-  \
           |   | \\\  -  /// |   |
           | \_|  ''\---/''  |   |
           \  .-\__  `-`  ___/-. /
         ___`. .'  /--.--\  `. . __
      ."" '<  `.___\_<|>_/___.'  >'"".
     | | :  `- \`.;`\ _ /`;.`/ - ` : | |
     \  \ `-.   \_ __\ /__ _/   .-` /  /
======`-.____`-.___\_____/___.-`____.-'======
                   `=---='
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
            佛祖保佑       永无BUG
*/

@Service
public class AdminQuoteService {
    private final Log logger = LogFactory.getLog(AdminQuoteService.class);
    @Autowired
    private LitemallQuoteDieCastingService quoteDieCastingService;
    @Autowired
    private LitemallQuoteElectronicService quoteElectronicService;
    @Autowired
    private LitemallQuoteHardwareService quoteHardwareService;
    @Autowired
    private LitemallQuoteRubberService quoteRubberService;
    @Autowired
    private LitemallRequoteService reQuoteService;
    @Autowired
    private LitemallQuoteBillService QuoteService;
    @Autowired
    private LitemallApproveInfoService approveInfoService;
    @Autowired
    private LitemallSystemConfigService systemConfigService;
    @Autowired
    private LitemallAdminService adminService;
    @Resource
    private LitemallQuoteBillMapper quoteBillMapper;
    @Resource
    private LitemallRequoteMapper requoteMapper;
    @Resource
    private LitemallQuoteDieCastingMapper quoteDieCastingMapper;
    @Resource
    private LitemallQuoteElectronicMapper quoteElectronicMapper;
    @Resource
    private LitemallQuoteHardwareMapper quoteHardwareMapper;
    @Resource
    private LitemallQuoteRubberMapper quoteRubberMapper;

    public Object cancelById(String body) {
        Integer id = JacksonUtil.parseInteger(body, "id");
        Integer quoteId = JacksonUtil.parseInteger(body, "quoteId");
        Integer adminId = JacksonUtil.parseInteger(body, "adminId");
        Integer billcode = JacksonUtil.parseInteger(body, "billcode");
        String billname = JacksonUtil.parseString(body, "billname");
        String nextaction = JacksonUtil.parseString(body, "nextaction");
        String action = JacksonUtil.parseString(body, "action");
        String approveNote = JacksonUtil.parseString(body, "approveNote");
        List<Integer> supplyCode = JacksonUtil.parseIntegerList(body, "quoteSupplyCode");
        Short setstatus = JacksonUtil.parseShort(body, "setstatus");

        if (id == null) {
            return ResponseUtil.badArgument();
        }
        LitemallQuoteBill quoteBill = new LitemallQuoteBill();
        quoteBill.setStatus(setstatus);
        LitemallQuoteBillExample example = new LitemallQuoteBillExample();
        example.or().andIdEqualTo(id);
        quoteBillMapper.updateByExampleSelective(quoteBill, example);
        logger.info("询价单");

        LitemallRequote reQuote = new LitemallRequote();

        List<LitemallRequote> reQuo = reQuoteService.queryAll();

        for(int i=0; i < reQuo.size(); i++ ) {
            if (reQuo.get(i).getQuoteId() == quoteId) { reQuoteService.deleteById(reQuo.get(i).getId()); }
        }
        LitemallApproveInfo approveInfo = new LitemallApproveInfo();
        approveInfo.setBillCode(billcode);
        approveInfo.setNote(approveNote);
        approveInfo.setBillName(billname);
        approveInfo.setAction(action);
        approveInfo.setNextAction(nextaction);
        approveInfo.setAdminId(adminId);
        approveInfo.setBillCode(billcode);
        approveInfo.setBillId(quoteId);
        logger.info("报价单");

        for (int code:supplyCode) {
            logger.info(code);
            Integer qid = code;
            approveInfo.setReceiver(code);
            approveInfoService.add(approveInfo);
        }
        return ResponseUtil.ok();
    }

    public Object submitById(String body) {
        Integer id = JacksonUtil.parseInteger(body, "id");
        Integer quoteId = JacksonUtil.parseInteger(body, "quoteId");
        Integer adminId = JacksonUtil.parseInteger(body, "adminId");
        Integer billcode = JacksonUtil.parseInteger(body, "billcode");
        String billname = JacksonUtil.parseString(body, "billname");
        String nextaction = JacksonUtil.parseString(body, "nextaction");
        String action = JacksonUtil.parseString(body, "action");
        String approveNote = JacksonUtil.parseString(body, "approveNote");
        Short status = JacksonUtil.parseShort(body, "setstatus");
        Short idcard = JacksonUtil.parseShort(body, "idcard");
        Integer receiver = JacksonUtil.parseInteger(body, "receiver");
        List<Integer> choiceValue = JacksonUtil.parseIntegerList(body, "choicevalue");
        List<Integer> ids = JacksonUtil.parseIntegerList(body, "ids");

        logger.info("submitbody:"+body);

        if (id == null) {
            return ResponseUtil.badArgument();
        }

        LitemallApproveInfo approveInfo = new LitemallApproveInfo();

        approveInfo.setBillCode(billcode);
        approveInfo.setNote(approveNote);
        approveInfo.setBillName(billname);
        approveInfo.setAction(action);
        approveInfo.setNextAction(nextaction);
        approveInfo.setAdminId(adminId);

        logger.info("报价单");
        LitemallRequote reQuote = new LitemallRequote();

        if (idcard == 1) {
            LitemallQuoteBill quoteBill = new LitemallQuoteBill();
            quoteBill.setStatus(status);
            quoteBill.setId(id);
            LitemallQuoteBillExample example = new LitemallQuoteBillExample();
            example.or().andIdEqualTo(id);
            quoteBillMapper.updateByExampleSelective(quoteBill, example);

//            采购员提交供应商审批
            if (status == 1) {
                LitemallSystem system = systemConfigService.getHours();
                LocalDateTime dead = LocalDateTime.now().plusHours(Integer.valueOf(system.getKeyValue()));
                quoteBill.setDeadDate(dead);
                quoteBill.setSubmitDate(LocalDateTime.now());
                example.or().andIdEqualTo(id);
                quoteBillMapper.updateByExampleSelective(quoteBill, example);

                LitemallQuoteBill quote = QuoteService.findById(quoteId);
                String excelId = quote.getQuoteModelExcel();
                Integer modelId = quote.getModelName();
//                logger.info("sese"+JacksonUtil.parseString(body, "quoteSupplyCode"));
                Integer[] supplyCode = quote.getQuoteSupplyCode();
                System.out.println(supplyCode);

                reQuote.setStatus((short) 0);
                reQuote.setQuoteId(quoteId);

                reQuote.setSubmitDate(LocalDateTime.now());
                reQuote.setQuoteModelExcelSupply(excelId);
                reQuote.setDeadDate(dead);
                for (int code : supplyCode) {
                    LitemallAdmin admin = adminService.findById(code);
                    reQuote.setAdminName(admin.getDept()+':'+admin.getNickname());
                    reQuote.setAdminId(code);
                    reQuoteService.add(reQuote);
                    Integer quoteKeyId = reQuote.getId();
                    approveInfo.setAdminId(adminId);
                    approveInfo.setReceiver(code);
                    approveInfo.setBillId(id);
                    approveInfo.setChildId(quoteId);
                    approveInfoService.add(approveInfo);
                    if (modelId == 4) {
                        List<LitemallQuoteDieCasting> quoteDieCastings = quoteDieCastingService.queryByGid(quoteId, false);
                        for (LitemallQuoteDieCasting quoteDieCasting : quoteDieCastings) {
                            String appendix = quoteDieCastingService.findById(quoteDieCasting.getId()).getAppendix();
                            quoteDieCasting.setQuoteId(quoteKeyId);
                            quoteDieCasting.setBillId(true);
                            quoteDieCasting.setAppendix(appendix);
                            quoteDieCasting.setStatus((short) 3);
                            quoteDieCastingService.add(quoteDieCasting);
                        }
                    }
                    if (modelId == 6) {
                        List<LitemallQuoteElectronic> quoteElectronics = quoteElectronicService.queryByGid(quoteId, false);
                        for (LitemallQuoteElectronic quoteElectronic : quoteElectronics) {
                            String appendix = quoteElectronicService.findById(quoteElectronic.getId()).getAppendix();
                            quoteElectronic.setAppendix(appendix);
                            quoteElectronic.setQuoteId(quoteKeyId);
                            quoteElectronic.setBillId(true);
                            quoteElectronic.setStatus((short) 3);
                            quoteElectronicService.add(quoteElectronic);
                        }
                    }
                    if (modelId == 5) {
                        List<LitemallQuoteHardware> quoteHardwares = quoteHardwareService.queryByGid(quoteId, false);
                        for (LitemallQuoteHardware quoteHardware : quoteHardwares) {
                            String appendix = quoteHardwareService.findById(quoteHardware.getId()).getAppendix();
                            quoteHardware.setAppendix(appendix);
                            quoteHardware.setQuoteId(quoteKeyId);
                            quoteHardware.setBillId(true);
                            quoteHardware.setStatus((short) 3);
                            quoteHardwareService.add(quoteHardware);
                        }
                    }
                    if (modelId == 3) {
                        List<LitemallQuoteRubber> quoteRubbers = quoteRubberService.queryByGid(quoteId, false);
                        for (LitemallQuoteRubber quoteRubber : quoteRubbers) {
                            String appendix = quoteRubberService.findById(quoteRubber.getId()).getAppendix();
                            quoteRubber.setAppendix(appendix);
                            quoteRubber.setQuoteId(quoteKeyId);
                            quoteRubber.setBillId(true);
                            quoteRubber.setStatus((short) 3);
                            quoteRubberService.add(quoteRubber);
                        }
                    }
                    String msg = "["+admin.getDept()+':'+admin.getNickname()+':'+admin.getId()+"]你好:\n\n"+"第["+reQuote.getQuoteId()+"]号询价单已经发送给您,意见:"+approveNote+"\n\n请在报价截止日期之前,前去钉钉工作台的耀泰供应链平台提供报价";
                    String infoSend = "{ \"userid_list\": \""+admin.getUsername()+"\", \"agent_id\": \"1231569276\", msg:{ \"msgtype\": \"markdown\", \"markdown\": { \"title\": \"LUTEC询价单\", " +
                            "\"text\":\"" + msg + "\"} } }";
                    System.out.println(infoSend);
                    try { DingtalkApi.asyncsend(infoSend); } catch (Exception e) {  e.printStackTrace(); }
                }
            }
//            负责人提交总经理审批
            if (status == 3) {
                boolean choiceId = JacksonUtil.parseBoolean(body, "choiceid");

                quoteBill.setIsCeo(choiceId);
                Integer[] integers2 = choiceValue.toArray(new Integer[0]);
                quoteBill.setCeoChoice(integers2);

                example.or().andIdEqualTo(id);
                quoteBillMapper.updateByExampleSelective(quoteBill, example);

                LitemallQuoteBill quote = QuoteService.findById(quoteId);
                Integer modelId = quote.getModelName();

                if (modelId == 4) {
                    for(Integer idt : ids) {
                        LitemallQuoteDieCasting quoteDieCastings = quoteDieCastingService.findById(idt);
                        quoteDieCastings.setIsDuty(true);
                        quoteDieCastingService.updateById(quoteDieCastings);
                    }
                }
                if (modelId == 6) {
                    for(Integer idt : ids) {
                        LitemallQuoteElectronic quoteElectronics = quoteElectronicService.findById(idt);
                        quoteElectronics.setIsDuty(true);
                        quoteElectronicService.updateById(quoteElectronics);
                    }
                }
                if (modelId == 5) {
                    for(Integer idt : ids) {
                        LitemallQuoteHardware quoteHardwares = quoteHardwareService.findById(idt);
                        quoteHardwares.setIsDuty(true);
                        quoteHardwareService.updateById(quoteHardwares);
                    }
                }
                if (modelId == 3) {
                    for(Integer idt : ids) {
                        LitemallQuoteRubber quoteRubbers = quoteRubberService.findById(idt);
                        quoteRubbers.setIsDuty(true);
                        quoteRubberService.updateById(quoteRubbers);
                    }
                }
                approveInfo.setAdminId(adminId);
                approveInfo.setReceiver(quote.getCeoCode());
                approveInfo.setBillId(id);
                approveInfo.setChildId(quoteId);
                approveInfoService.add(approveInfo);

                LitemallAdmin admin = adminService.findById(quote.getCeoCode());
                LitemallAdmin admin1 = adminService.findById(approveInfo.getAdminId());

                String msg = "["+admin.getNickname()+"]你好:\n\n"+"第["+quote.getId()+"]号询价单,["+admin1.getNickname()+"]已经提交您审批\n\n,注意:"+approveNote+"\n\n前去钉钉工作台确定中标商家";
                String infoSend = "{ \"userid_list\": \""+admin.getUsername()+"\", \"agent_id\": \"1231569276\", msg:{ \"msgtype\": \"markdown\", \"markdown\": { \"title\": \"LUTEC询价单\", " +
                        "\"text\":\""+ msg +"\"} } }";
                System.out.println(infoSend);
                try { DingtalkApi.asyncsend(infoSend); } catch (Exception e) {  e.printStackTrace(); }
            }
            //负责人提交会审

            if (status == 4) {
                LitemallQuoteBill quote = QuoteService.findById(quoteId);
                approveInfo.setAdminId(adminId);
                approveInfo.setReceiver(adminId);
                approveInfo.setBillId(id);
                approveInfo.setChildId(quoteId);
                approveInfoService.add(approveInfo);
                for (int code : quote.getApproveCode()) {
                    logger.info(code);
                    Integer qid = code;

                    LitemallAdmin admin = adminService.findById(qid);
                    LitemallAdmin admin1 = adminService.findById(approveInfo.getAdminId());

                    String msg = "["+admin.getNickname()+"]你好:\n\n"+"第["+quote.getId()+"]号询价单,["+admin1.getNickname()+"]已经提交您参加会审,意见:"+approveNote+"\n\n前去钉钉工作台的耀泰供应链平台会审";
                    String infoSend = "{ \"userid_list\": \""+admin.getUsername()+"\", \"agent_id\": \"1231569276\", msg:{ \"msgtype\": \"markdown\", \"markdown\": { \"title\": \"LUTEC询价单\", " +
                            "\"text\":\""+ msg +"\"} } }";
                    System.out.println(infoSend);
                    try { DingtalkApi.asyncsend(infoSend); } catch (Exception e) {  e.printStackTrace(); }
                }
            }
            //            会审后 负责人提交CEO
            if (status == 5) {
                Integer[] integers2 = choiceValue.toArray(new Integer[0]);
                quoteBill.setCeoChoice(integers2);

                example.or().andIdEqualTo(id);
                quoteBillMapper.updateByExampleSelective(quoteBill, example);

                LitemallQuoteBill quote = QuoteService.findById(quoteId);
                Integer modelId = quote.getModelName();

                if (modelId == 4) {
                    LitemallQuoteDieCasting rubber = new LitemallQuoteDieCasting();
                    LitemallQuoteDieCastingExample example1 = new LitemallQuoteDieCastingExample();
                    rubber.setStatus((short) 1);
                    rubber.setIsCeo(false);
                    example1.or().andQuoteIdEqualTo(id);
                    quoteDieCastingMapper.updateByExampleSelective(rubber, example1);
                    for(Integer idt : ids) {
                        LitemallQuoteDieCasting quoteDieCastings = quoteDieCastingService.findById(idt);
                        quoteDieCastings.setIsCeo(true);
                        quoteDieCastings.setStatus((short) 0);
                        quoteDieCastingService.updateById(quoteDieCastings);
                    }
                }
                if (modelId == 6) {
                    LitemallQuoteElectronic rubber = new LitemallQuoteElectronic();
                    LitemallQuoteElectronicExample example1 = new LitemallQuoteElectronicExample();
                    rubber.setStatus((short) 1);
                    rubber.setIsCeo(false);
                    example1.or().andQuoteIdEqualTo(id);
                    quoteElectronicMapper.updateByExampleSelective(rubber, example1);
                    for(Integer idt : ids) {
                        LitemallQuoteElectronic quoteElectronics = quoteElectronicService.findById(idt);
                        quoteElectronics.setIsCeo(true);
                        quoteElectronics.setStatus((short) 0);
                        quoteElectronicService.updateById(quoteElectronics);
                    }
                }
                if (modelId == 5) {
                    LitemallQuoteHardware rubber = new LitemallQuoteHardware();
                    LitemallQuoteHardwareExample example1 = new LitemallQuoteHardwareExample();
                    rubber.setStatus((short) 1);
                    rubber.setIsCeo(false);
                    example1.or().andQuoteIdEqualTo(id);
                    quoteHardwareMapper.updateByExampleSelective(rubber, example1);
                    for(Integer idt : ids) {
                        LitemallQuoteHardware quoteHardwares = quoteHardwareService.findById(idt);
                        quoteHardwares.setIsCeo(true);
                        quoteHardwares.setStatus((short) 0);
                        quoteHardwareService.updateById(quoteHardwares);
                    }
                }
                if (modelId == 3) {
                    //把本次报价单设置成未中标
                    LitemallQuoteRubber rubber = new LitemallQuoteRubber();
                    LitemallQuoteRubberExample example1 = new LitemallQuoteRubberExample();
                    rubber.setStatus((short) 1);
                    rubber.setIsCeo(false);
                    example1.or().andQuoteIdEqualTo(id);
                    quoteRubberMapper.updateByExampleSelective(rubber, example1);
                    for(Integer idt : ids) {
                        LitemallQuoteRubber quoteRubbers = quoteRubberService.findById(idt);
                        quoteRubbers.setIsCeo(true);
                        quoteRubbers.setStatus((short) 0);
                        quoteRubberService.updateById(quoteRubbers);
                    }
                }
                approveInfo.setAdminId(adminId);
                approveInfo.setReceiver(quote.getCeoCode());
                approveInfo.setBillId(id);
                approveInfo.setChildId(quoteId);
                approveInfoService.add(approveInfo);

                LitemallAdmin admin = adminService.findById(quote.getCeoCode());
                LitemallAdmin admin1 = adminService.findById(approveInfo.getAdminId());

                String msg = "["+admin.getNickname()+"]你好:\n\n"+"第["+quote.getId()+"]号询价单,经过会审,["+admin1.getNickname()+"]已经提交您审批,意见:"+approveNote+"\n\n前去钉钉工作台的耀泰供应链平台确定中标商家";
                String infoSend = "{ \"userid_list\": \""+admin.getUsername()+"\", \"agent_id\": \"1231569276\", msg:{ \"msgtype\": \"markdown\", \"markdown\": { \"title\": \"LUTEC询价单\", " +
                        "\"text\":\""+ msg +"\"} } }";
                System.out.println(infoSend);
                try { DingtalkApi.asyncsend(infoSend); } catch (Exception e) {  e.printStackTrace(); }
            }
            //会审
            if (status == 9) {
                example.or().andIdEqualTo(id);
                quoteBillMapper.updateByExampleSelective(quoteBill, example);

                LitemallQuoteBill quote = QuoteService.findById(quoteId);

                approveInfo.setAdminId(adminId);
                approveInfo.setReceiver(quote.getDutyCode());
                approveInfo.setBillId(id);
                approveInfo.setChildId(quoteId);
                approveInfoService.add(approveInfo);

                LitemallAdmin admin = adminService.findById(quote.getCeoCode());
                LitemallAdmin admin1 = adminService.findById(approveInfo.getAdminId());

                String msg = "["+admin.getNickname()+"]你好:\n\n"+"第["+quote.getId()+"]号询价单,["+admin1.getNickname()+"]会审意见:"+approveNote;
                String infoSend = "{ \"userid_list\": \""+admin.getUsername()+"\", \"agent_id\": \"1231569276\", msg:{ \"msgtype\": \"markdown\", \"markdown\": { \"title\": \"LUTEC询价单\", " +
                        "\"text\":\""+ msg +"\"} } }";
                System.out.println(infoSend);
                try { DingtalkApi.asyncsend(infoSend); } catch (Exception e) {  e.printStackTrace(); }
            }
            //会审后CEO终审
            if (status == 6) {
                LitemallRequoteExample example2 = new LitemallRequoteExample();

                //把本次报价单设置成未中标
                reQuote.setStatus((short) 8);
                reQuote.setQuoteDate(LocalDateTime.now());
                example2.or().andQuoteIdEqualTo(id);
                requoteMapper.updateByExampleSelective(reQuote, example2);

                approveInfo.setAdminId(adminId);
                approveInfo.setReceiver(adminId);
                approveInfo.setBillId(id);
                approveInfo.setChildId(quoteId);
                approveInfoService.add(approveInfo);

                LitemallQuoteBill quote = QuoteService.findById(quoteId);
                Integer modelId = quote.getModelName();

                if (modelId == 4) {
                    List<LitemallQuoteDieCasting> quoteDieCastingsr = quoteDieCastingService.queryByGid(id, true);

                    for (LitemallQuoteDieCasting role : quoteDieCastingsr) {
                        // 把本次报价单设置成未中标
                        role.setStatus((short) 1);
                        role.setIsCeo(false);
                        if (role.getStatus()==5 || role.getStatus()==2) { quoteDieCastingService.updateById(role); }
                    }

                    for(Integer idt : ids) {
                        LitemallQuoteDieCasting quoteDieCastings = quoteDieCastingService.findById(idt);
                        quoteDieCastings.setIsCeo(true);
                        quoteDieCastings.setStatus((short) 0);
                        quoteDieCastingService.updateById(quoteDieCastings);
                        Integer code = quoteDieCastings.getQuoteId();

                        LitemallRequote requote = reQuoteService.findById(code);
                        requote.setStatus((short) 9);
                        reQuoteService.updateById(requote);

                        LitemallAdmin admin = adminService.findById(requote.getAdminId());

                        String msg = "["+admin.getNickname()+"]你好:\n\n"+"第["+ quoteDieCastings.getQuoteId() +"]号询价单的产品:"+quoteDieCastings.getCode()+":"+quoteDieCastings.getName()+":"+quoteDieCastings.getSpec()+",贵司被确认中标";
                        String infoSend = "{ \"userid_list\": \""+admin.getUsername()+"\", \"agent_id\": \"1231569276\", msg:{ \"msgtype\": \"markdown\", \"markdown\": { \"title\": \"LUTEC询价单\", " +
                                "\"text\":\""+ msg +"\"} } }";
                        try {
                            DingtalkApi.asyncsend(infoSend);
                            Robot r = new Robot();
                            r.delay(5000);
                        } catch (Exception e) {  e.printStackTrace(); }                        //把中标通知供应商
                        String noticer = "";
                        for (int noticeCode : quote.getNoticeCode() ) {
                            noticer = noticer + adminService.findById(noticeCode).getUsername()+ ',';
                        }
                        noticer = noticer + adminService.findById(quote.getAdminId()).getUsername();
                        String msg1 = "[通知]:\n\n[" + adminService.findById(requote.getAdminId()).getDept() + ':' + adminService.findById(requote.getAdminId()).getNickname() + "]第"+quote.getId()+"号询价单的产品:["+quoteDieCastings.getCode()+":"+quoteDieCastings.getName()+":"+quoteDieCastings.getSpec()+"]中标";
                        String infoSend1 = "{ \"userid_list\": \""+noticer+"\", \"agent_id\": \"1231569276\", msg:{ \"msgtype\": \"markdown\", \"markdown\": { \"title\": \"LUTEC询价单\", " +
                                "\"text\":\""+ msg1 +"\"} } }";
                        try {
                            DingtalkApi.asyncsend(infoSend1);
                            Robot r = new Robot();
                            r.delay(5000);
                        } catch (Exception e) {  e.printStackTrace(); }
                    }
                }
                if (modelId == 6) {
                    List<LitemallQuoteElectronic> quoteDieCastingsr = quoteElectronicService.queryByGid(id, true);

                    for (LitemallQuoteElectronic role : quoteDieCastingsr) {
                        // 把本次报价单设置成未中标
                        role.setStatus((short) 1);
                        role.setIsCeo(false);
                        if (role.getStatus()==5 || role.getStatus()==2) { quoteElectronicService.updateById(role); }
                    }

                    for(Integer idt : ids) {
                        LitemallQuoteElectronic quoteElectronics = quoteElectronicService.findById(idt);
                        quoteElectronics.setIsCeo(true);
                        quoteElectronics.setStatus((short) 0);
                        quoteElectronicService.updateById(quoteElectronics);

                        Integer code = quoteElectronics.getQuoteId();
                        LitemallRequote requote = reQuoteService.findById(code);
                        requote.setStatus((short) 9);
                        reQuoteService.updateById(requote);

                        LitemallAdmin admin = adminService.findById(requote.getAdminId());

                        String msg = "["+admin.getNickname()+"]你好:\n\n"+"第["+ quoteElectronics.getQuoteId() +"]号询价单的产品:"+quoteElectronics.getCode()+":"+quoteElectronics.getName()+":"+quoteElectronics.getSpec()+",贵司被确认中标";
                        String infoSend = "{ \"userid_list\": \""+admin.getUsername()+"\", \"agent_id\": \"1231569276\", msg:{ \"msgtype\": \"markdown\", \"markdown\": { \"title\": \"LUTEC询价单\", " +
                                "\"text\":\""+ msg +"\"} } }";
                        try {
                            DingtalkApi.asyncsend(infoSend);
                            Robot r = new Robot();
                            r.delay(5000);
                        } catch (Exception e) {  e.printStackTrace(); }
                        //把中标通知供应商
                        String noticer = "";
                        for (int noticeCode : quote.getNoticeCode() ) {
                            noticer = noticer + adminService.findById(noticeCode).getUsername()+ ',';
                        }
                        noticer = noticer + adminService.findById(quote.getAdminId()).getUsername();
                        String msg1 = "[通知]:\n\n[" + adminService.findById(requote.getAdminId()).getDept() + ':' + adminService.findById(requote.getAdminId()).getNickname() + "]第"+quote.getId()+"号询价单的产品:["+quoteElectronics.getCode()+":"+quoteElectronics.getName()+":"+quoteElectronics.getSpec()+"]中标";
                        String infoSend1 = "{ \"userid_list\": \""+noticer+"\", \"agent_id\": \"1231569276\", msg:{ \"msgtype\": \"markdown\", \"markdown\": { \"title\": \"LUTEC询价单\", " +
                                "\"text\":\""+ msg1 +"\"} } }";
                        try {
                            DingtalkApi.asyncsend(infoSend1);
                            Robot r = new Robot();
                            r.delay(5000);
                        } catch (Exception e) {  e.printStackTrace(); }                    }
                }
                if (modelId == 5) {
                    List<LitemallQuoteHardware> quoteDieCastingsr = quoteHardwareService.queryByGid(id, true);

                    for (LitemallQuoteHardware role : quoteDieCastingsr) {
                        // 把本次报价单设置成未中标
                        role.setStatus((short) 1);
                        role.setIsCeo(false);
                        if (role.getStatus()==5 || role.getStatus()==2) { quoteHardwareService.updateById(role); }
                    }

                    for(Integer idt : ids) {
                        LitemallQuoteHardware quoteHardwares = quoteHardwareService.findById(idt);
                        quoteHardwares.setIsCeo(true);
                        quoteHardwares.setStatus((short) 0);
                        quoteHardwareService.updateById(quoteHardwares);

                        Integer code = quoteHardwares.getQuoteId();

                        LitemallRequote requote = reQuoteService.findById(code);
                        requote.setStatus((short) 9);
                        reQuoteService.updateById(requote);

                        LitemallAdmin admin = adminService.findById(requote.getAdminId());

                        String msg = "["+admin.getNickname()+"]你好:\n\n"+"第["+ quoteHardwares.getQuoteId() +"]号询价单的产品:"+quoteHardwares.getCode()+":"+quoteHardwares.getName()+":"+quoteHardwares.getSpec()+",贵司被确认中标";
                        String infoSend = "{ \"userid_list\": \""+admin.getUsername()+"\", \"agent_id\": \"1231569276\", msg:{ \"msgtype\": \"markdown\", \"markdown\": { \"title\": \"LUTEC询价单\", " +
                                "\"text\":\""+ msg +"\"} } }";
                        System.out.println("中标"+infoSend);
                        try {
                            DingtalkApi.asyncsend(infoSend);
                            Robot r = new Robot();
                            r.delay(5000);
                        } catch (Exception e) {  e.printStackTrace(); }
                        //把中标通知供应商
                        String noticer = "";
                        for (int noticeCode : quote.getNoticeCode() ) {
                            noticer = noticer + adminService.findById(noticeCode).getUsername()+ ',';
                        }
                        noticer = noticer + adminService.findById(quote.getAdminId()).getUsername();
                        String msg1 = "[通知]:\n\n[" + adminService.findById(requote.getAdminId()).getDept() + ':' + adminService.findById(requote.getAdminId()).getNickname() + "]第"+quote.getId()+"号询价单的产品:["+quoteHardwares.getCode()+":"+quoteHardwares.getName()+":"+quoteHardwares.getSpec()+"]中标";
                        String infoSend1 = "{ \"userid_list\": \""+noticer+"\", \"agent_id\": \"1231569276\", msg:{ \"msgtype\": \"markdown\", \"markdown\": { \"title\": \"LUTEC询价单\", " +
                                "\"text\":\""+ msg1 +"\"} } }";
                        try {
                            DingtalkApi.asyncsend(infoSend1);
                            Robot r = new Robot();
                            r.delay(5000);
                        } catch (Exception e) {  e.printStackTrace(); }                    }
                }
                if (modelId == 3) {
                    List<LitemallQuoteRubber> quoteDieCastingsr = quoteRubberService.queryByGid(id, true);

                    for (LitemallQuoteRubber role : quoteDieCastingsr) {
                        // 把本次报价单设置成未中标
                        role.setStatus((short) 1);
                        role.setIsCeo(false);
                        if (role.getStatus()==5 || role.getStatus()==2) { quoteRubberService.updateById(role); }
                    }

                    for(Integer idt : ids) {
                        LitemallQuoteRubber quoteRubbers = quoteRubberService.findById(idt);
                        quoteRubbers.setIsCeo(true);
                        quoteRubbers.setStatus((short) 0);
                        quoteRubberService.updateById(quoteRubbers);

                        Integer code = quoteRubbers.getQuoteId();

                        LitemallRequote requote = reQuoteService.findById(code);
                        requote.setStatus((short) 9);
                        reQuoteService.updateById(requote);

                        // 把本次报价单设置成未中标
//
//                        quoteRubbers.setStatus((short) 1);
//                        quoteRubbers.setIsCeo(false);
//                        example1.or().andQuoteIdEqualTo(code).andStatusEqualTo((short) 5);
//                        quoteRubberMapper.updateByExampleSelective(quoteRubbers, example1);
//
//                        quoteRubbers.setStatus((short) 1);
//                        quoteRubbers.setIsCeo(false);
//                        example1.or().andQuoteIdEqualTo(code).andStatusEqualTo((short) 2);
//                        quoteRubberMapper.updateByExampleSelective(quoteRubbers, example1);

                        LitemallAdmin admin = adminService.findById(requote.getAdminId());

                        String msg = "["+admin.getNickname()+"]你好:\n\n"+"第["+ quoteRubbers.getQuoteId() +"]号询价单的产品:"+quoteRubbers.getCode()+":"+quoteRubbers.getName()+":"+quoteRubbers.getSpec()+",贵司被确认中标";
                        String infoSend = "{ \"userid_list\": \""+admin.getUsername()+"\", \"agent_id\": \"1231569276\", msg:{ \"msgtype\": \"markdown\", \"markdown\": { \"title\": \"LUTEC询价单\", " +
                                "\"text\":\""+ msg +"\"} } }";
                        System.out.println("中标"+infoSend);
                        try {
                            DingtalkApi.asyncsend(infoSend);
                            Robot r = new Robot();
                            r.delay(5000);
                        } catch (Exception e) {  e.printStackTrace(); }
                        //把中标通知供应商

                        String noticer = "";
                        for (int noticeCode : quote.getNoticeCode() ) {
                            noticer = noticer + adminService.findById(noticeCode).getUsername()+ ',';
                        }
                        noticer = noticer + adminService.findById(quote.getAdminId()).getUsername();
                        String msg1 = "[通知]:\n\n[" + adminService.findById(requote.getAdminId()).getDept() + ':' + adminService.findById(requote.getAdminId()).getNickname() + "]第"+quote.getId()+"号询价单的产品:["+quoteRubbers.getCode()+":"+quoteRubbers.getName()+":"+quoteRubbers.getSpec()+"]中标";
                        System.out.println("msg:"+msg1 + "noticer:" + noticer);
                        String infoSend1 = "{ \"userid_list\": \""+noticer+"\", \"agent_id\": \"1231569276\", msg:{ \"msgtype\": \"markdown\", \"markdown\": { \"title\": \"LUTEC询价单\", " +
                                "\"text\":\""+ msg1 +"\"} } }";
                        System.out.println("中标"+infoSend1);
                        try {
                            DingtalkApi.asyncsend(infoSend1);
                            Robot r = new Robot();
                            r.delay(5000);
                        } catch (Exception e) {  e.printStackTrace(); }                    }
                }
            }
            //            退回供应商重新议价
            if (status == 7) {
//                boolean choiceId = JacksonUtil.parseBoolean(body, "choiceid");
//                quoteBill.setIsReapprove(choiceId);
//                Integer[] integers2 = choiceValue.toArray(new Integer[0]);
//                quoteBill.setCeoChoice(integers2);
//                quoteBill.setQuoteSupplyCode(quoteBill.getDutyChoice());
//                example.or().andIdEqualTo(id);
//                quoteBillMapper.updateByExampleSelective(quoteBill, example);
                LitemallQuoteBill quote = QuoteService.findById(quoteId);
                Integer modelId = quote.getModelName();
                Integer receiver1 = quote.getAdminId();
                quote.setStatus((short) 7);
                quoteBillMapper.updateByPrimaryKey(quote);
                String dutyChoice ="";
                for(Integer idt : ids) {
                    if (modelId == 3) {
                        LitemallQuoteRubber quoteRubbers = quoteRubberService.findById(idt);
                        quoteRubbers.setStatus((short) 2);
                        quoteRubberService.updateById(quoteRubbers);
                        quoteRubberService.add(quoteRubbers);
                        quoteRubbers.setStatus((short) 3);
                        quoteRubberService.updateById(quoteRubbers);
                        quoteRubberMapper.updateByPrimaryKey(quoteRubbers);
                        LitemallRequote requote = reQuoteService.findById(quoteRubbers.getQuoteId());
                        quoteRubbers.setStatus((short) 5);
                        reQuoteService.updateById(requote);
                    }
                    if (modelId == 4) {
                        LitemallQuoteDieCasting quoteRubbers = quoteDieCastingService.findById(idt);
                        quoteRubbers.setStatus((short) 2);
                        quoteDieCastingService.updateById(quoteRubbers);
                        quoteDieCastingService.add(quoteRubbers);
                        quoteRubbers.setStatus((short) 3);
                        quoteDieCastingService.updateById(quoteRubbers);
                        quoteDieCastingMapper.updateByPrimaryKey(quoteRubbers);
                    }
                    if (modelId == 5) {
                        LitemallQuoteHardware quoteRubbers = quoteHardwareService.findById(idt);
                        quoteRubbers.setStatus((short) 2);
                        quoteHardwareService.updateById(quoteRubbers);
                        quoteHardwareService.add(quoteRubbers);
                        quoteRubbers.setStatus((short) 3);
                        quoteHardwareService.updateById(quoteRubbers);
                        quoteHardwareMapper.updateByPrimaryKey(quoteRubbers);
                    }
                    if (modelId == 6) {
                        LitemallQuoteElectronic quoteRubbers = quoteElectronicService.findById(idt);
                        quoteRubbers.setStatus((short) 2);
                        quoteElectronicService.updateById(quoteRubbers);
                        quoteElectronicService.add(quoteRubbers);
                        quoteRubbers.setStatus((short) 3);
                        quoteElectronicService.updateById(quoteRubbers);
                        quoteElectronicMapper.updateByPrimaryKey(quoteRubbers);
                    }
                }
                for (int code : quote.getDutyChoice()) {
                    LitemallAdmin admin = adminService.findById(code);
                    dutyChoice = dutyChoice + admin.getDept()+':'+admin.getNickname()+':'+admin.getId()+";";
                }
                approveInfo.setAdminId(adminId);
                approveInfo.setReceiver(receiver1);
                approveInfo.setBillId(id);
                approveInfo.setChildId(quoteId);
                approveInfoService.add(approveInfo);

                LitemallAdmin admin = adminService.findById(receiver1);
                LitemallAdmin admin1 = adminService.findById(approveInfo.getAdminId());

                String msg = "["+admin1.getNickname()+"]通知您:\n\n"+"第["+quote.getId()+"]号询价单,请修改该单据的报价供应商,要求重新提交报价\\n\\n意见:"+approveNote+"";
                String infoSend = "{ \"userid_list\": \""+admin.getUsername()+"\", \"agent_id\": \"1231569276\", msg:{ \"msgtype\": \"markdown\", \"markdown\": { \"title\": \"LUTEC询价单\", " +
                        "\"text\":\""+ msg +"\"} } }";
                System.out.println(infoSend);
                try { DingtalkApi.asyncsend(infoSend); } catch (Exception e) {  e.printStackTrace(); }
            }
            //            和供应商议价后,重新提交供应商报价
            if (status == 8) {
                LitemallQuoteBill quote = QuoteService.findById(quoteId);
                Integer modelId = quote.getModelName();
                Integer receiver1 = quote.getAdminId();
                quote.setStatus((short) 1);
                quoteBillMapper.updateByPrimaryKey(quote);

                LitemallSystem system = systemConfigService.getHours();
                LocalDateTime dead = LocalDateTime.now().plusHours(Integer.valueOf(system.getKeyValue()));
                quoteBill.setDeadDate(dead);
                quoteBill.setSubmitDate(LocalDateTime.now());
                example.or().andIdEqualTo(id);
                quoteBillMapper.updateByExampleSelective(quoteBill, example);

                List<LitemallRequote> reQuotes = reQuoteService.readQuote(id);

                for (LitemallRequote idt : reQuotes) {
                    if (modelId == 3) {
                        List<LitemallQuoteRubber> quoteRubbers = quoteRubberService.queryByGid(idt.getId(),true);
                        for (LitemallQuoteRubber rubber : quoteRubbers) {
                            if (rubber.getStatus() == 2) {
                                idt.setStatus((short) 10);
                                reQuoteService.updateById(idt);

                                LitemallAdmin admin = adminService.findById(idt.getAdminId());
                                reQuote.setAdminName(admin.getDept()+':'+admin.getNickname());

                                approveInfo.setAdminId(adminId);
                                approveInfo.setReceiver(idt.getAdminId());
                                approveInfo.setBillId(id);
                                approveInfo.setChildId(quoteId);
                                approveInfoService.add(approveInfo);

                                String msg = "["+admin.getDept()+':'+admin.getNickname()+':'+admin.getId()+"]你好:\n\n"+"第["+idt.getQuoteId()+"]号报价单 请重新报价"+"\n\n请在报价截止日期之前,前去钉钉工作台的耀泰供应链平台提供报价";
                                String infoSend = "{ \"userid_list\": \""+admin.getUsername()+"\", \"agent_id\": \"1231569276\", msg:{ \"msgtype\": \"markdown\", \"markdown\": { \"title\": \"LUTEC询价单\", " +
                                        "\"text\":\"" + msg + "\"} } }";
                                System.out.println(infoSend);
                                try {
                                    DingtalkApi.asyncsend(infoSend);
                                    Robot r = new Robot();
                                    r.delay(5000);
                                } catch (Exception e) {  e.printStackTrace(); }
                                break;
                            }
                        }
                    }
                    if (modelId == 4) {
                        List<LitemallQuoteDieCasting> quoteRubbers = quoteDieCastingService.queryByGid(idt.getId(),true);
                        for (LitemallQuoteDieCasting rubber : quoteRubbers) {
                            if (rubber.getStatus() == 2) {
                                idt.setStatus((short) 10);
                                reQuoteService.updateById(idt);

                                LitemallAdmin admin = adminService.findById(idt.getAdminId());
                                reQuote.setAdminName(admin.getDept() + ':' + admin.getNickname());

                                approveInfo.setAdminId(adminId);
                                approveInfo.setReceiver(idt.getAdminId());
                                approveInfo.setBillId(id);
                                approveInfo.setChildId(quoteId);
                                approveInfoService.add(approveInfo);

                                String msg = "[" + admin.getDept() + ':' + admin.getNickname() + ':' + admin.getId() + "]你好:\n\n" + "第[" + idt.getQuoteId() + "]号报价单 请重新报价" + "\n\n请在报价截止日期之前,前去钉钉工作台的耀泰供应链平台提供报价";
                                String infoSend = "{ \"userid_list\": \"" + admin.getUsername() + "\", \"agent_id\": \"1231569276\", msg:{ \"msgtype\": \"markdown\", \"markdown\": { \"title\": \"LUTEC询价单\", " +
                                        "\"text\":\"" + msg + "\"} } }";
                                System.out.println(infoSend);
                                try {
                                    DingtalkApi.asyncsend(infoSend);
                                    Robot r = new Robot();
                                    r.delay(5000);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            }
                        }
                    }
                    if (modelId == 5) {
                        List<LitemallQuoteHardware> quoteRubbers = quoteHardwareService.queryByGid(idt.getId(),true);
                        for (LitemallQuoteHardware rubber : quoteRubbers) {
                            if (rubber.getStatus() == 2) {
                                idt.setStatus((short) 10);
                                reQuoteService.updateById(idt);

                                LitemallAdmin admin = adminService.findById(idt.getAdminId());
                                reQuote.setAdminName(admin.getDept() + ':' + admin.getNickname());

                                approveInfo.setAdminId(adminId);
                                approveInfo.setReceiver(idt.getAdminId());
                                approveInfo.setBillId(id);
                                approveInfo.setChildId(quoteId);
                                approveInfoService.add(approveInfo);

                                String msg = "[" + admin.getDept() + ':' + admin.getNickname() + ':' + admin.getId() + "]你好:\n\n" + "第[" + idt.getQuoteId() + "]号报价单 请重新报价" + "\n\n请在报价截止日期之前,前去钉钉工作台的耀泰供应链平台提供报价";
                                String infoSend = "{ \"userid_list\": \"" + admin.getUsername() + "\", \"agent_id\": \"1231569276\", msg:{ \"msgtype\": \"markdown\", \"markdown\": { \"title\": \"LUTEC询价单\", " +
                                        "\"text\":\"" + msg + "\"} } }";
                                System.out.println(infoSend);
                                try {
                                    DingtalkApi.asyncsend(infoSend);
                                    Robot r = new Robot();
                                    r.delay(5000);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            }
                        }
                    }
                    if (modelId == 6) {
                        List<LitemallQuoteElectronic> quoteRubbers = quoteElectronicService.queryByGid(idt.getId(),true);
                        for (LitemallQuoteElectronic rubber : quoteRubbers) {
                            if (rubber.getStatus() == 2) {
                                idt.setStatus((short) 10);
                                reQuoteService.updateById(idt);

                                LitemallAdmin admin = adminService.findById(idt.getAdminId());
                                reQuote.setAdminName(admin.getDept() + ':' + admin.getNickname());

                                approveInfo.setAdminId(adminId);
                                approveInfo.setReceiver(idt.getAdminId());
                                approveInfo.setBillId(id);
                                approveInfo.setChildId(quoteId);
                                approveInfoService.add(approveInfo);

                                String msg = "[" + admin.getDept() + ':' + admin.getNickname() + ':' + admin.getId() + "]你好:\n\n" + "第[" + idt.getQuoteId() + "]号报价单 请重新报价" + "\n\n请在报价截止日期之前,前去钉钉工作台的耀泰供应链平台提供报价";
                                String infoSend = "{ \"userid_list\": \"" + admin.getUsername() + "\", \"agent_id\": \"1231569276\", msg:{ \"msgtype\": \"markdown\", \"markdown\": { \"title\": \"LUTEC询价单\", " +
                                        "\"text\":\"" + msg + "\"} } }";
                                System.out.println(infoSend);
                                try {
                                    DingtalkApi.asyncsend(infoSend);
                                    Robot r = new Robot();
                                    r.delay(5000);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
        if (idcard == 2) {
            reQuote.setStatus(status);
            reQuote.setId(id);
            reQuoteService.updateById(reQuote);
            approveInfo.setBillId(quoteId);
            approveInfo.setChildId(id);
            approveInfo.setAdminId(adminId);
            approveInfo.setReceiver(receiver);
            approveInfoService.add(approveInfo);

            LitemallAdmin admin = adminService.findById(receiver);
            LitemallAdmin admin1 = adminService.findById(adminId);
            if (status == 1) {
                String msg = "[" + admin.getNickname() + "]:\n\n" + "第[" +  String.valueOf(quoteId) + "]号询价单,供应商[" + admin1.getDept()+':'+admin1.getNickname() + "]已经签收询价单,意见:" + approveNote + "\n\n静等提交报价单";
                String infoSend = "{ \"userid_list\": \"" + admin.getUsername() + "\", \"agent_id\": \"1231569276\", msg:{ \"msgtype\": \"markdown\", \"markdown\": { \"title\": \"LUTEC询价单\", " +
                        "\"text\":\"" + msg + "\"} } }";
                System.out.println(infoSend);
                try {
                    DingtalkApi.asyncsend(infoSend);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (status == 2) {
                String msg = "[" + admin.getNickname() + "]:\n\n" + "第[" + String.valueOf(quoteId) + "]号询价单,供应商[" + admin1.getDept()+':'+admin1.getNickname() + "]提交了报价单,意见:" + approveNote + "\n\n静等系统通知您打开标书时间";
                String infoSend = "{ \"userid_list\": \"" + admin.getUsername() + "\", \"agent_id\": \"1231569276\", msg:{ \"msgtype\": \"markdown\", \"markdown\": { \"title\": \"LUTEC询价单\", " +
                        "\"text\":\"" + msg + "\"} } }";
                System.out.println(infoSend);
                try {
                    DingtalkApi.asyncsend(infoSend);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (idcard == 3) {
            reQuote.setStatus(status);
            reQuote.setId(id);
            reQuoteService.updateById(reQuote);
            approveInfo.setBillId(id);
            approveInfo.setChildId(quoteId);
            approveInfo.setAdminId(adminId);
            approveInfo.setReceiver(receiver);
            approveInfoService.add(approveInfo);

            LitemallQuoteBill quote = QuoteService.findById(id);
            Integer modelId = quote.getModelName();

            List<LitemallRequote> reQuotes = reQuoteService.readQuote(id);
            for (LitemallRequote idt : reQuotes) {
                if (modelId == 4) {
                    LitemallQuoteDieCasting rubber = new LitemallQuoteDieCasting();
                    LitemallQuoteDieCastingExample example1 = new LitemallQuoteDieCastingExample();
                    rubber.setStatus((short) 4);
                    example1.or().andQuoteIdEqualTo(id);
                    quoteDieCastingMapper.updateByExampleSelective(rubber, example1);

                    List<LitemallQuoteDieCasting> quoteRubbers = quoteDieCastingService.queryByGid(idt.getId(), true);

                    for (LitemallQuoteDieCasting idt1 : quoteRubbers) {
                        LitemallQuoteDieCasting quoteDieCastings = quoteDieCastingService.findById(idt1.getId());
                        if (quoteDieCastings.getStatus() == 5) {
                            quoteDieCastings.setStatus((short) 6);
                            quoteDieCastingService.updateById(quoteDieCastings);
                        }
                    }
                }
                if (modelId == 6) {
                    LitemallQuoteElectronic rubber = new LitemallQuoteElectronic();
                    LitemallQuoteElectronicExample example1 = new LitemallQuoteElectronicExample();
                    rubber.setStatus((short) 4);
                    example1.or().andQuoteIdEqualTo(id);

                    quoteElectronicMapper.updateByExampleSelective(rubber, example1);

                    List<LitemallQuoteElectronic> quoteRubbers = quoteElectronicService.queryByGid(idt.getId(), true);

                    for (LitemallQuoteElectronic idt1 : quoteRubbers) {
                        LitemallQuoteElectronic quoteDieCastings = quoteElectronicService.findById(idt1.getId());
                        if (quoteDieCastings.getStatus() == 5) {
                            quoteDieCastings.setStatus((short) 6);
                            quoteElectronicService.updateById(quoteDieCastings);
                        }
                    }
                }
                if (modelId == 5) {
                    LitemallQuoteHardware rubber = new LitemallQuoteHardware();
                    LitemallQuoteHardwareExample example1 = new LitemallQuoteHardwareExample();
                    rubber.setStatus((short) 4);
                    example1.or().andQuoteIdEqualTo(id);
                    quoteHardwareMapper.updateByExampleSelective(rubber, example1);

                    List<LitemallQuoteHardware> quoteRubbers = quoteHardwareService.queryByGid(idt.getId(), true);

                    for (LitemallQuoteHardware idt1 : quoteRubbers) {
                        LitemallQuoteHardware quoteDieCastings = quoteHardwareService.findById(idt1.getId());
                        if (quoteDieCastings.getStatus() == 5) {
                            quoteDieCastings.setStatus((short) 6);
                            quoteHardwareService.updateById(quoteDieCastings);
                        }
                    }
                }
                if (modelId == 3) {
                    //把本次报价单设置成未中标
                    LitemallQuoteRubber rubber = new LitemallQuoteRubber();
                    LitemallQuoteRubberExample example1 = new LitemallQuoteRubberExample();
                    rubber.setStatus((short) 4);
                    example1.or().andQuoteIdEqualTo(id);
                    quoteRubberMapper.updateByExampleSelective(rubber, example1);

                    List<LitemallQuoteRubber> quoteRubbers = quoteRubberService.queryByGid(idt.getId(), true);

                    for (LitemallQuoteRubber idt1 : quoteRubbers) {
                        LitemallQuoteRubber quoteDieCastings = quoteRubberService.findById(idt1.getId());
                        if (quoteDieCastings.getStatus() == 5) {
                            quoteDieCastings.setStatus((short) 6);
                            quoteRubberService.updateById(quoteDieCastings);
                        }
                    }
                }
            }
//            Integer userid = requote.getAdminId();
//            LitemallAdmin admin = adminService.findById(userid);
//            String msg = "["+admin.getNickname()+"]你好:\n\n你的第["+requote.getId()+"]号报价单,报价截止日期已到\n\n本次报价资格已被取消";
//
//            String msg1 = "{ \"msgtype\": \"text\", \"text\": { \"content\":\""+ msg +" \"}, \"at\": {\"atMobiles\": [\""+admin.getMobile()+"\"],\"isAtAll\":false}}";
//            System.out.println(msg1);
        }
        return ResponseUtil.ok();
    }
    /**
     * 发送通知
     *
     * @throws ApiException
     */
//    public OapiMessageCorpconversationAsyncsendV2Response sendNotification(String userIdList,String title, String content, List<OapiMessageCorpconversationAsyncsendV2Request.BtnJsonList> btnJsonListList) throws ApiException {
//        String accessToken = AccessTokenUtil.getAccessToken();
//
//        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2");
//        OapiMessageCorpconversationAsyncsendV2Request request = new OapiMessageCorpconversationAsyncsendV2Request();
//        request.setAgentId(1231569276L);
//        request.setUseridList(userIdList);
//        request.setToAllUser(false);
//
//        OapiMessageCorpconversationAsyncsendV2Request.Msg msg = new OapiMessageCorpconversationAsyncsendV2Request.Msg();
//        OapiMessageCorpconversationAsyncsendV2Request.ActionCard actionCard = new OapiMessageCorpconversationAsyncsendV2Request.ActionCard();
//        actionCard.setTitle(title);
//        actionCard.setMarkdown(content);
//        actionCard.setBtnOrientation("0");
//        actionCard.setBtnJsonList(btnJsonListList);
//
//        msg.setMsgtype("action_card");
//        msg.setActionCard(actionCard);
//        request.setMsg(msg);
//
//        OapiMessageCorpconversationAsyncsendV2Response rsp = client.execute(request, accessToken);
//        System.out.println(rsp.getBody());
//        return rsp;
//    }
}
