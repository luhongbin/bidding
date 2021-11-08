package org.linlinjava.litemall.admin.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.db.domain.LitemallAdmin;
import org.linlinjava.litemall.db.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/dashboard")
@Validated
public class AdminDashbordController {
    private final Log logger = LogFactory.getLog(AdminDashbordController.class);

    @Autowired
    private LitemallUserService userService;
    @Autowired
    private LitemallGoodsService goodsService;
    @Autowired
    private LitemallGoodsProductService productService;
    @Autowired
    private LitemallOrderService orderService;
    @Autowired
    private LitemallQuoteBillService quoteBillService;
    @Autowired
    private LitemallRequoteService requoteService;

    @GetMapping("/info")
    public Object info() {
        int userTotal = userService.count();
        int goodsTotal = goodsService.count();
        int productTotal = productService.count();
        int orderTotal = orderService.count();
        Map<String, Integer> data = new HashMap<>();
        data.put("userTotal", userTotal);
        data.put("goodsTotal", goodsTotal);
        data.put("productTotal", productTotal);
        data.put("orderTotal", orderTotal);

        return ResponseUtil.ok(data);
    }
    @GetMapping("/quote")
    public Object quote() {
        Subject currentUser = SecurityUtils.getSubject();
        LitemallAdmin admin = (LitemallAdmin) currentUser.getPrincipal();
        Integer adminid = admin.getId();
        int totalCount = quoteBillService.totalCount();
        int overCount = quoteBillService.overCount();
        int purTotalCount = quoteBillService.purTotalCount(adminid);
        int purOverCount = quoteBillService.purOverCount(adminid);

        int ruserTotal = requoteService.totalCount();
        int rgoodsTotal = requoteService.overCount();
        int rproductTotal = requoteService.purTotalCount(adminid);
        int rorderTotal = requoteService.purOverCount(adminid);
        int waitTotalCount = requoteService.waitTotalCount(adminid);
        int waitpurTotalCount = requoteService.waitpurTotalCount(adminid);
        int mywaitTotalCount = requoteService.mywaitTotalCount(adminid);


        Map<String, Integer> value = new HashMap<>();
        value.put("totalCount", totalCount);
        value.put("overCount", overCount);
        value.put("purTotalCount", purTotalCount);
        value.put("purOverCount", purOverCount);
        value.put("ruserTotal", ruserTotal);
        value.put("rgoodsTotal", rgoodsTotal);
        value.put("rproductTotal", rproductTotal);
        value.put("rorderTotal", rorderTotal);
        value.put("waitTotalCount", waitTotalCount);
        value.put("mywaitTotalCount", waitTotalCount);
        value.put("waitpurTotalCount", waitpurTotalCount);

        Map<String, Object> data = new HashMap<>();

        data.put("value", value);
        data.put("currentUser", admin);
        return ResponseUtil.ok(data);
    }

}
