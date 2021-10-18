package org.linlinjava.litemall.admin.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.linlinjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.core.validator.Order;
import org.linlinjava.litemall.core.validator.Sort;
import org.linlinjava.litemall.db.domain.LitemallAd;
import org.linlinjava.litemall.db.domain.LitemallApproveInfo;
import org.linlinjava.litemall.db.service.LitemallApproveInfoService;
//import org.linlinjava.litemall.db.dao.ApproveInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/approveinfo")
@Validated
public class AdminApproveInfoController {
    private final Log logger = LogFactory.getLog(org.linlinjava.litemall.admin.web.AdminApproveInfoController.class);
    @Autowired
    private LitemallApproveInfoService approveInfoService;

    @RequiresPermissions("admin:approveinfo:list")
    @RequiresPermissionsDesc(menu = {"系统管理", "审批记录"}, button = "查询")
    @GetMapping("/list")
    public Object list(Integer billCode, Integer sourceId,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort @RequestParam(defaultValue = "add_time") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order) {
        List<LitemallApproveInfo> ApproveInfoList = approveInfoService.querySelective(billCode, sourceId, page, limit, sort, order);
        return ResponseUtil.okList(ApproveInfoList);
    }
    @GetMapping("/detail")
    public Object detail(Integer billId, Integer receiveId, Integer adminId, Integer billCode ) {
        List<LitemallApproveInfo> ApproveInfoList = approveInfoService.queryRequoteApprove(billId, receiveId, adminId, billCode);
        return ResponseUtil.okList(ApproveInfoList);
    }

    @GetMapping("/quoteread")
    public Object quoteread(Integer billId , Integer billCode) {
        List<LitemallApproveInfo> ApproveInfoList = approveInfoService.queryQuoteApprove(billId, billCode);
        return ResponseUtil.okList(ApproveInfoList);
    }

    @PostMapping("/create")
    public Object create(@RequestBody LitemallApproveInfo ad) {
        approveInfoService.add(ad);
        return ResponseUtil.ok(ad);
    }
}