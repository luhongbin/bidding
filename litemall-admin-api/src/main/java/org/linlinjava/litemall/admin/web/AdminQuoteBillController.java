package org.linlinjava.litemall.admin.web;

import com.github.pagehelper.PageHelper;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.linlinjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.linlinjava.litemall.admin.dto.Quoteinone;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.core.validator.Order;
import org.linlinjava.litemall.core.validator.Sort;
import org.linlinjava.litemall.db.domain.*;
import org.linlinjava.litemall.db.service.*;
import org.linlinjava.litemall.admin.service.AdminQuoteService;
import org.linlinjava.litemall.admin.service.AdminQuoteBillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.linlinjava.litemall.db.domain.LitemallQuoteModel;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/quoteBill")
@Validated
public class AdminQuoteBillController {
    private final Log logger = LogFactory.getLog(AdminQuoteBillController.class);

    @Autowired
    private LitemallQuoteBillService quoteBillService;
    @Autowired
    private AdminQuoteService adminQuoteService;
    @Autowired
    private LitemallAdminService adminService;
    @Autowired
    private LitemallQuoteModelService quoteModelService;
    @Autowired
    private AdminQuoteBillService adminQuoteBillService;
    @Autowired
    private LitemallAdminService AdminService;
    @Autowired
    private ImaalTService T100Service;
    @Autowired
    private LitemallQuoteDieCastingService quoteDieCastingService;
    @Autowired
    private LitemallQuoteElectronicService quoteElectronicService;
    @Autowired
    private LitemallQuoteHardwareService quoteHardwareService;
    @Autowired
    private LitemallQuoteRubberService quoteRubberService;
    /**
     * ???????????????
     *
     * @param id
     * @param dutyid
     * @param status
     * @param page
     * @param limit
     * @param sort
     * @param order
     * @return
     */
    @RequiresPermissions("admin:quoteBill:list")
    @RequiresPermissionsDesc(menu = {"????????????", "?????????????????????"}, button = "??????")
    @GetMapping("/list")

    public Object list(Integer id, Integer dutyid,
                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
                       @RequestParam(required = false) List<Short> status,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort @RequestParam(defaultValue = "add_time") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order) {

        LitemallAdmin adminList = (LitemallAdmin) SecurityUtils.getSubject().getPrincipal();
//        System.out.println(start);
        Integer adminId = adminList.getId();
        return getList(id, adminId, dutyid, start, end, status,page, limit, sort, order);
    }

    @GetMapping("/listSupply")
    public Object listSupply(String name,
                             @RequestParam(defaultValue = "1") Integer page,
                             @RequestParam(defaultValue = "10") Integer limit,
                             @Sort @RequestParam(defaultValue = "add_time") String sort,
                             @Order @RequestParam(defaultValue = "desc") String order) {
//        if (name == null || name.isEmpty()) { name = "1"; }
        List<Map<String, Object>> data = new ArrayList<>();
//        System.out.println(name);
        List logLists  = T100Service.querySupply(name,page, limit, sort, order);
//        List logListtot  = T100Service.queryTotalSupply(name);
//        String total1 = "";
//        for(int i=0; i<logListtot.size(); i++) {
//            Map userMap= (Map) logListtot.get(i);
//            total1 = userMap.get("total").toString();
//        }
        for(int i=0; i<logLists.size(); i++) {
            Map<String, Object> adminnames = new HashMap<>();
            Map userMap= (Map) logLists.get(i);


            String simname = (String) userMap.get("pmaal004");
            String classid = (String) userMap.get("pmab030");
            String code = (String) userMap.get("pmaa001");

            List<LitemallAdmin> adminList = AdminService.findJobnumber(code);
            LitemallAdmin adminInfo = new LitemallAdmin();

            for (LitemallAdmin admin : adminList) {
//                System.out.println(admin.getMobile());
//                System.out.println(phone);
//                System.out.println(classid+"????????????["+simname+"]");
              adminInfo.setId(admin.getId());
              adminInfo.setJobnumber(code);
//                    adminInfo.setDept(logList.getImaal003());
              adminInfo.setCapacity(classid + "????????????[" + simname + "]");
              AdminService.updateById(adminInfo);
              adminnames.put("phone", admin.getMobile());
              adminnames.put("nickname", admin.getNickname());
              adminnames.put("name", simname);
              adminnames.put("note", classid + "????????????[" + simname + "]");
              adminnames.put("id", admin.getId());
//                adminnames.put("total", total1);
              data.add(adminnames);
            }
        }

//        for (ImaalT logList : logLists) {
//            String phone = logList.getImaal005();
//            List<LitemallAdmin> adminList = AdminService.findPhone(phone);
//            LitemallAdmin adminInfo = new LitemallAdmin();
//            String classid = logList.getImaal004();
//            if (logList.getImaal004() == null) { classid = "-"; }
//            System.out.println(phone);
//
//            for (LitemallAdmin admin : adminList) {
//                if (admin.getMobile() == phone) {
//                    adminInfo.setId(admin.getId());
//                    adminInfo.setJobnumber(logList.getImaal001());
////                    adminInfo.setDept(logList.getImaal003());
//                    adminInfo.setCapacity(classid+"????????????["+logList.getImaal003()+"]");
//                    AdminService.updateById(adminInfo);
//                    data.put("phone", phone);
//                    data.put("nickname", admin.getNickname());
//                    data.put("name", logList.getImaal002());
//                    data.put("note", classid+"????????????["+logList.getImaal003()+"]");
//                    data.put("id", admin.getId());
//                }
//            }
//        }
        PageHelper.startPage(page, limit);
        return ResponseUtil.okList(data);
    }
    @RequiresPermissions("admin:quoteBill:listCeo")
    @RequiresPermissionsDesc(menu = {"????????????", "?????????????????????"}, button = "????????????")
    @GetMapping("/listCeo")

    public Object listCeo(Integer id, Integer adminid, Integer dutyid,
                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
                       @RequestParam(required = false) List<Short> status,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort @RequestParam(defaultValue = "add_time") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order) {
        return getList(id, adminid, dutyid, start, end, status,page, limit, sort, order);
    }

    @RequiresPermissions("admin:quoteBill:listBrowser")
    @RequiresPermissionsDesc(menu = {"????????????", "??????????????????"}, button = "????????????")
    @GetMapping("/listBrowser")
    public Object listBrowser(Integer adminId, Integer supplyId, String codeId,  @RequestParam(required = false) List<Short> status) {
        return ResponseUtil.ok(adminQuoteBillService.totalGoogs(1, adminId, supplyId, codeId, status));
    }
    @RequiresPermissions("admin:quoteBill:listBrowserCeo")
    @RequiresPermissionsDesc(menu = {"????????????", "??????????????????"}, button = "????????????")
    @GetMapping("/listBrowserCeo")

    public Object listBrowserCeo(Integer adminId, Integer supplyId, String codeId, @RequestParam(required = false) List<Short> status) {
        return ResponseUtil.ok(adminQuoteBillService.totalGoogs(0, adminId, supplyId, codeId,status));
    }
    @RequiresPermissions("admin:quoteBill:listBrowserOK")
    @RequiresPermissionsDesc(menu = {"????????????", "??????????????????"}, button = "??????????????????")
    @GetMapping("/listBrowserOK")
    public Object listBrowserOK(Integer adminId, Integer supplyId, String codeId, @RequestParam(required = false)  List<Short> status) {
        return ResponseUtil.ok(adminQuoteBillService.totalGoogs(2,adminId, supplyId, codeId,status));
    }

    @RequiresPermissions("admin:quoteBill:detail")
    @RequiresPermissionsDesc(menu = {"????????????", "?????????????????????"}, button = "??????")
    @GetMapping("/detail")
    public Object detail(@NotNull Integer id)  {
        LitemallQuoteBill reQuote = quoteBillService.findById(id);
        return ResponseUtil.ok(reQuote);
    }

    @RequiresPermissions("admin:quoteBill:update")
    @RequiresPermissionsDesc(menu = {"????????????", "?????????????????????"}, button = "??????")
    @PostMapping("/update")
    public Object update(@RequestBody Quoteinone quoteinone) {
//        logger.info(quoteinone);

        return adminQuoteBillService.update(quoteinone);
    }

//    @RequiresPermissions("admin:quoteBill:submit")
//    @RequiresPermissionsDesc(menu = {"????????????", "?????????????????????"}, button = "???????????????")
    @PostMapping("/submit")
    public Object submit(@RequestBody String body) { return adminQuoteService.submitById(body);}
//
//    @RequiresPermissions("admin:quoteBill:resubmit")
//    @RequiresPermissionsDesc(menu = {"???????????????", "???????????????"}, button = "??????")

//    @PostMapping("/resubmit")
//    public Object resubmit(@RequestBody String body) { return adminQuoteService.reSubmitById(body);}

//    @RequiresPermissions("admin:quoteBill:cancle")
//    @RequiresPermissionsDesc(menu = {"???????????????", "???????????????"}, button = "??????")
//    @PostMapping("/cancel")
    public Object cancel(@RequestBody String body) { return adminQuoteService.cancelById(body);}

    /**
    /**
     * ????????????
     *
     * @param quote
     * @return
     */
    @RequiresPermissions("admin:quoteBill:delete")
    @RequiresPermissionsDesc(menu = {"????????????", "?????????????????????"}, button = "??????")
    @PostMapping("/delete")
    public Object delete(@RequestBody LitemallQuoteBill quote) {
        Integer id = quote.getId();
        if (id == null) {
            return ResponseUtil.badArgument();
        }
        quoteBillService.deleteById(id);
        return ResponseUtil.ok();
    }

    @PostMapping("/deleteRubber")
    public Object deleteRubber(@RequestBody LitemallQuoteRubber quote) {
        Integer id = quote.getId();
        if (id == null) {
            return ResponseUtil.badArgument();
        }
        quoteRubberService.deleteById(id);
        return ResponseUtil.ok();
    }
    @PostMapping("/deleteElectronic")
    public Object deleteElectronic(@RequestBody LitemallQuoteElectronic quote) {
        Integer id = quote.getId();
        if (id == null) {
            return ResponseUtil.badArgument();
        }
        quoteElectronicService.deleteById(id);
        return ResponseUtil.ok();
    }
    @PostMapping("/deleteHardware")
    public Object deleteHardware(@RequestBody LitemallQuoteHardware quote) {
        Integer id = quote.getId();
        if (id == null) {
            return ResponseUtil.badArgument();
        }
        quoteHardwareService.deleteById(id);
        return ResponseUtil.ok();
    }
    @PostMapping("/deleteDieCasting")
    public Object deleteDieCasting(@RequestBody LitemallQuoteDieCasting quote) {
        Integer id = quote.getId();
        if (id == null) {
            return ResponseUtil.badArgument();
        }
        quoteDieCastingService.deleteById(id);
        return ResponseUtil.ok();
    }
    public String sign() throws Exception {
        Long timestamp = 1577262236757L;
        String appSecret = "fXSxWtI3HAtdbmXg5E-VFPfMmnDSoHrGlT59bLxuIRzWeGvIzVSQTlSc5xN2NU93";
        String stringToSign = timestamp + "\n" + appSecret;
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(appSecret.getBytes("UTF-8"), "HmacSHA256"));
        byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
        String sign = new String(Base64.encodeBase64(signData));
        System.out.println(sign);
        return sign;
    }

    @RequiresPermissions("admin:quoteBill:create")
    @RequiresPermissionsDesc(menu = {"????????????", "?????????????????????"}, button = "??????")
    @PostMapping("/create")
    public Object create(@RequestBody Quoteinone quoteinone) {
        adminQuoteBillService.create(quoteinone);
        return ResponseUtil.ok(quoteinone);
    }

    @PostMapping("/readquote")
    public Object readquote(Integer id) {
//        logger.info("read:"+String.valueOf(id));
        return adminQuoteBillService.detail(id);
    }
    @RequiresPermissions("admin:quoteBill:find")
    @RequiresPermissionsDesc(menu = {"????????????", "?????????????????????"}, button = "??????")
    @PostMapping("/find")
    public Object find(Integer id,Integer modelId) {
//        logger.info("find:"+String.valueOf(id));
        return adminQuoteBillService.find(id, modelId);
    }
//    @PostMapping("/approve")
//    public Object approve(@RequestBody LitemallApproveInfo approveInfo) {
//        approveInfoService.add(approveInfo);
//        return ResponseUtil.ok(approveInfo);
//    }

    public Object getList(Integer id, Integer adminid, Integer dutyid,LocalDateTime start, LocalDateTime end, List<Short>  status,Integer page,Integer limit, String sort, String order) {

        List<LitemallQuoteBill> roleList = quoteBillService.querySelective(id, adminid, dutyid, start, end, status,page, limit, sort, order);

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("list", ResponseUtil.okList(roleList));
//        System.out.println(ResponseUtil.okList(roleList));

        LitemallAdmin admin = (LitemallAdmin) SecurityUtils.getSubject().getPrincipal();

        List<LitemallAdmin> adminlist = adminService.queryAll();
        List<Map<String, Object>> adminlists = new ArrayList<>(adminlist.size());
        for (LitemallAdmin adminname : adminlist) {
            Map<String, Object> adminnames = new HashMap<>();
            adminnames.put("value", adminname.getId());
            adminnames.put("label", adminname.getNickname());
            adminnames.put("username", adminname.getUsername());
            adminnames.put("dept", adminname.getDept());
            adminnames.put("deptname", adminname.getDept()+':'+adminname.getNickname()+':'+adminname.getId());
            adminnames.put("dd", adminname.getUsername());
            adminnames.put("jobNumber", adminname.getJobnumber());
            adminnames.put("capacity", adminname.getCapacity());
            adminlists.add(adminnames);
        }

        List<LitemallQuoteModel> rolist = quoteModelService.queryAll();
        List<Map<String, Object>> quoteModels = new ArrayList<>(rolist.size());
        for (LitemallQuoteModel role : rolist) {
            Map<String, Object> quoteModel = new HashMap<>();
            quoteModel.put("value", role.getId());
            quoteModel.put("label", role.getName()+':'+role.getVersion());
            quoteModel.put("supply", role.getCode());
            quoteModel.put("notice", role.getNotice());
            quoteModel.put("approveCode", role.getApproveCode());
            quoteModel.put("ceoCode", role.getCeoCode());
            quoteModel.put("duty", role.getDuty());
            quoteModels.add(quoteModel);
        }

        data.put("currentUser", admin);
        data.put("optionsAdmin", adminlists);
        data.put("quoteModel", quoteModels);
        return ResponseUtil.ok(data);
    }

}

