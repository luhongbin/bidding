package org.linlinjava.litemall.admin.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.linlinjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.linlinjava.litemall.admin.service.AdminTeuService;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.core.validator.Order;
import org.linlinjava.litemall.core.validator.Sort;
import org.linlinjava.litemall.db.domain.LitemallTeu;
import org.linlinjava.litemall.db.service.LitemallTeuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/admin/teu")
@Validated
public class AdminTeuController {
    private final Log logger = LogFactory.getLog(AdminTeuController.class);

    @Autowired
    private AdminTeuService adminTeuService;
    @Autowired
    private LitemallTeuService teuService;

    @RequiresPermissions("admin:teu:list")
    @RequiresPermissionsDesc(menu = {"装箱管理", "集装箱规格定义"}, button = "查询")
    @GetMapping("/list")
    public Object list(String username, String mobile,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort @RequestParam(defaultValue = "add_time") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order) {
        return adminTeuService.list(username, mobile, page, limit, sort, order);
    }

//    @GetMapping("/list")
//    public Object list(String username, String teuTypeCn,String teuNameCn,
//                       @RequestParam(defaultValue = "1") Integer page,
//                       @RequestParam(defaultValue = "10") Integer limit,
//                       @Sort @RequestParam(defaultValue = "add_time") String sort,
//                       @Order @RequestParam(defaultValue = "desc") String order) {
//        List<LitemallTeu> teuList = teuService.querySelective(username, teuTypeCn, teuNameCn, page, limit, sort, order);
//        return ResponseUtil.okList(teuList);
//    }

    private Object validate(LitemallTeu teu) {
        String username = teu.getUsername();
        if (StringUtils.isEmpty(username)) {
            return ResponseUtil.badArgument();
        }
        String teuTypeCn = teu.getTeuTypeCn();
        if (StringUtils.isEmpty(teuTypeCn)) {
            return ResponseUtil.badArgument();
        }
        String teuNameCn = teu.getTeuNameCn();
        if (StringUtils.isEmpty(teuNameCn)) {
            return ResponseUtil.badArgument();
        }
        return null;
    }

    @RequiresPermissions("admin:teu:create")
    @RequiresPermissionsDesc(menu = {"装箱管理", "集装箱规格定义"}, button = "添加")
    @PostMapping("/create")
    public Object create(@RequestBody LitemallTeu teu) {
        Object error = validate(teu);
        if (error != null) {
            return error;
        }
        teuService.add(teu);
        return ResponseUtil.ok(teu);
    }


    @GetMapping("/read")
    public Object read(@NotNull Integer id) {
        LitemallTeu teu = teuService.findById(id);
        return ResponseUtil.ok(teu);
    }

    @RequiresPermissions("admin:teu:update")
    @RequiresPermissionsDesc(menu = {"装箱管理", "集装箱规格定义"}, button = "编辑")
    @PostMapping("/update")
    public Object update(@RequestBody LitemallTeu teu) {
        Object error = validate(teu);
        if (error != null) {
            return error;
        }
        if (teuService.updateById(teu) == 0) {
            return ResponseUtil.updatedDataFailed();
        }

        return ResponseUtil.ok(teu);
    }

    @RequiresPermissions("admin:teu:delete")
    @RequiresPermissionsDesc(menu = {"装箱管理", "集装箱规格定义"}, button = "删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody LitemallTeu teu) {
        Integer id = teu.getId();
        if (id == null) {
            return ResponseUtil.badArgument();
        }
        teuService.deleteById(id);
        return ResponseUtil.ok();
    }

    @RequiresPermissions("admin:teu:read")
    @RequiresPermissionsDesc(menu = {"装箱管理", "集装箱规格定义"}, button = "详情")
    @GetMapping("/detail")
    public Object detail(@NotNull Integer id) {
        return adminTeuService.detail(id);
    }

}
