package org.linlinjava.litemall.admin.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.linlinjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.linlinjava.litemall.admin.dto.Quoteinone;
import org.linlinjava.litemall.admin.service.AdminQuoteBillService;
import org.linlinjava.litemall.admin.service.AdminQuoteService;
import org.linlinjava.litemall.admin.util.AdminResponseCode;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.core.validator.Order;
import org.linlinjava.litemall.core.validator.Sort;
import org.linlinjava.litemall.db.domain.*;
import org.linlinjava.litemall.db.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.constraints.NotNull;
import org.linlinjava.litemall.admin.service.AdminQuoteService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/requote")
@Validated
public class AdminRequoteController {
    private final Log logger = LogFactory.getLog(AdminRequoteController.class);
    @Autowired
    private LitemallApproveInfoService approveInfoService;
    @Autowired
    private LitemallRequoteService reQuoteService;
    @Autowired
    private AdminQuoteService adminQuoteService;
    @Autowired
    private LitemallQuoteBillService quoteBillService;
    @Autowired
    private LitemallAdminService adminService;
    @Autowired
    private LitemallQuoteModelService quoteModelService;
    @Autowired
    private LitemallQuoteDieCastingService quoteDieCastingService;
    @Autowired
    private LitemallQuoteElectronicService quoteElectronicService;
    @Autowired
    private LitemallQuoteHardwareService quoteHardwareService;
    @Autowired
    private LitemallQuoteRubberService quoteRubberService;
    @Autowired
    private AdminQuoteBillService adminQuoteBillService;
    /**
     * 查询询价单
     *
     * @param orderStatusArray
     * @param page
     * @param limit
     * @param sort
     * @param order
     * @return
     */
    @RequiresPermissions("admin:requote:list")
    @RequiresPermissionsDesc(menu = {"供应商管理", "报价管理"}, button = "查询")
    @GetMapping("/list")

    public Object list(@RequestParam(required = false) List<Short> orderStatusArray,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort @RequestParam(defaultValue = "add_time") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order) {
        LitemallAdmin admint = (LitemallAdmin) SecurityUtils.getSubject().getPrincipal();
        Integer adminId = admint.getId();
        List<LitemallRequote> reQuote = reQuoteService.querySelectivelist(adminId, orderStatusArray,page, limit, sort, order);
        logger.info("test file " + String.valueOf(adminId));

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("list", ResponseUtil.okList(reQuote));

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
        data.put("currentUser", admint);
        data.put("optionsAdmin", adminlists);
        data.put("quoteModel", quoteModels);
        return ResponseUtil.ok(data);
    }

    @RequiresPermissions("admin:requote:listCeo")
    @RequiresPermissionsDesc(menu = {"供应商管理", "报价管理"}, button = "领导查询")
    @GetMapping("/listCeo")
    public Object listCeo(Integer adminId,
                       @RequestParam(required = false) List<Short> orderStatusArray,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort @RequestParam(defaultValue = "add_time") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order) {
        logger.info("test file " + String.valueOf(adminId));

        List<LitemallRequote> reQuote = reQuoteService.querySelectiveCeolist(adminId, orderStatusArray,page, limit, sort, order);
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("list", ResponseUtil.okList(reQuote));

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

    @GetMapping("/myRead")
    public Object read(Integer id) {
        logger.info("Integer id:"+String.valueOf(id));
        LitemallRequote reQuote = reQuoteService.findById(id);
        logger.info("reQuote:"+reQuote);
        LitemallQuoteBill Quote = quoteBillService.findById(reQuote.getQuoteId());
        logger.info("Quote:"+Quote);
        List<LitemallApproveInfo> ApproveInfoList = approveInfoService.queryQuoteApprove(reQuote.getQuoteId(), 2);

        Integer modelId = Quote.getModelName();
        logger.info("modelId:"+String.valueOf(modelId));
        Map<String, Object> data = new HashMap<>();

        if (modelId == 4) {
            List<LitemallQuoteDieCasting> quoteDieCasting  = quoteDieCastingService.queryByGid(id, true);
            data.put("redetail", quoteDieCasting);
        }
        if (modelId == 6) {
            List<LitemallQuoteElectronic> quoteElectronic  = quoteElectronicService.queryByGid(id, true);
            data.put("redetail", quoteElectronic);
        }
        if (modelId == 5) {
            List<LitemallQuoteHardware> quoteHardware  = quoteHardwareService.queryByGid(id, true);
            data.put("redetail", quoteHardware);
        }
        if (modelId == 3) {
            List<LitemallQuoteRubber> quoteRubber  = quoteRubberService.queryByGid(id, true);
            data.put("redetail", quoteRubber);
        }

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
        data.put("Quote", Quote);
        data.put("reQuote", reQuote);
        data.put("ApproveInfoList", ApproveInfoList);
        return ResponseUtil.ok(data);
    }
    @RequiresPermissions("admin:requote:submit")
    @RequiresPermissionsDesc(menu = {"供应商管理", "报价管理"}, button = "详情及审批")
    @PostMapping("/submit")
    public Object submit(@RequestBody String body) { return adminQuoteService.submitById(body);}

    @GetMapping("/readQuote")
    public Object readQuote(Integer quoteId, Integer billCode) {
        logger.info("quoteId file " + String.valueOf(quoteId));
        LitemallQuoteBill  Quote = quoteBillService.findById(quoteId);
        List<LitemallRequote> reQuote = reQuoteService.readQuote(quoteId);
        List<LitemallApproveInfo> ApproveInfoList = approveInfoService.queryQuoteApprove(quoteId, billCode);

        Map<String, Object> data = new HashMap<>();
        data.put("Quote", Quote);
        data.put("reQuote", reQuote);
        data.put("ApproveInfoList", ApproveInfoList);
        return ResponseUtil.ok(data);
    }
    @RequiresPermissions("admin:requote:listBrowser")
    @RequiresPermissionsDesc(menu = {"供应商管理", "报价管理"}, button = "查询")
    @GetMapping("/listBrowser")
    public Object listBrowser(Integer adminId,  String codeId, @RequestParam(required = false) List<Short> status) {
        return ResponseUtil.ok(adminQuoteBillService.supplyGoogs(1, adminId, codeId,status));
    }
    @RequiresPermissions("admin:requote:listBrowserCeo")
    @RequiresPermissionsDesc(menu = {"供应商管理", "报价管理"}, button = "全部查询")
    @GetMapping("/listBrowserCeo")

    public Object listBrowserCeo(Integer adminId,  String codeId, @RequestParam(required = false) List<Short> status) {
        return ResponseUtil.ok(adminQuoteBillService.supplyGoogs(0, adminId, codeId,status));
    }
    @RequiresPermissions("admin:requote:listBrowserOK")
    @RequiresPermissionsDesc(menu = {"供应商管理", "报价管理"}, button = "查询中标商品")
    @GetMapping("/listBrowserOK")
    public Object listBrowserOK(Integer adminId,  String codeId, @RequestParam(required = false)  List<Short> status) {
        return ResponseUtil.ok(adminQuoteBillService.supplyGoogs(2,adminId, codeId,status));
    }

    /**
     * 编辑商品
     *
     * @param quoteinone
     * @return
     */
    @RequiresPermissions("admin:requote:update")
    @RequiresPermissionsDesc(menu = {"供应商管理", "报价管理"}, button = "修改")
    @PostMapping("/update")
    public Object update(@RequestBody Quoteinone quoteinone) {
        logger.info(quoteinone);

        return adminQuoteBillService.updateRequote(quoteinone);
    }
}


