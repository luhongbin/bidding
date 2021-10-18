package org.linlinjava.litemall.admin.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.linlinjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.core.validator.Order;
import org.linlinjava.litemall.core.validator.Sort;
import org.linlinjava.litemall.db.domain.LitemallPackingTactics;
import org.linlinjava.litemall.db.service.LitemallTacticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/admin/tactics")
@Validated
public class AdminTacticsController {
    private final Log logger = LogFactory.getLog(AdminTacticsController.class);

    @Autowired
    private LitemallTacticsService tacticsService;

    @RequiresPermissions("admin:tactics:list")
    @RequiresPermissionsDesc(menu = {"装箱管理", "用户装箱策略"}, button = "查询")
    @GetMapping("/list")
    public Object list(Integer fileId,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort @RequestParam(defaultValue = "add_time") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order) {
        List<LitemallPackingTactics> tacticsList = tacticsService.querySelective(fileId, page, limit, sort, order);
        return ResponseUtil.okList(tacticsList);
    }

    private Object validate(LitemallPackingTactics tactics) {
        Integer fileId = tactics.getFileId();
        if (StringUtils.isEmpty(fileId)) {
            return ResponseUtil.badArgument();
        }

        return null;
    }

    @RequiresPermissions("admin:tactics:create")
    @RequiresPermissionsDesc(menu = {"装箱管理", "用户装箱策略"}, button = "添加")
    @PostMapping("/create")
    public Object create(@RequestBody LitemallPackingTactics tactics) {
        Object error = validate(tactics);
        if (error != null) {
            return error;
        }
        tacticsService.add(tactics);
        return ResponseUtil.ok(tactics);
    }

    @RequiresPermissions("admin:tactics:read")
    @RequiresPermissionsDesc(menu = {"装箱管理", "用户装箱策略"}, button = "详情")
    @GetMapping("/read")
    public Object read(@NotNull Integer id) {
        LitemallPackingTactics tactics = tacticsService.findById(id);
        return ResponseUtil.ok(tactics);
    }

    @RequiresPermissions("admin:tactics:update")
    @RequiresPermissionsDesc(menu = {"装箱管理", "用户装箱策略"}, button = "编辑")
    @PostMapping("/update")
    public Object update(@RequestBody LitemallPackingTactics tactics) {
        Object error = validate(tactics);
        if (error != null) {
            return error;
        }
        if (tacticsService.updateById(tactics) == 0) {
            return ResponseUtil.updatedDataFailed();
        }

        return ResponseUtil.ok(tactics);
    }

    @RequiresPermissions("admin:tactics:delete")
    @RequiresPermissionsDesc(menu = {"装箱管理", "用户装箱策略"}, button = "删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody LitemallPackingTactics tactics) {
        Integer id = tactics.getId();
        if (id == null) {
            return ResponseUtil.badArgument();
        }
        tacticsService.deleteById(id);
        return ResponseUtil.ok();
    }

}
