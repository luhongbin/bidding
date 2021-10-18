package org.linlinjava.litemall.admin.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.linlinjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.core.validator.Order;
import org.linlinjava.litemall.core.validator.Sort;
import org.linlinjava.litemall.db.domain.LitemallPackingSelect;
import org.linlinjava.litemall.db.service.LitemallSelectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/admin/select")
@Validated
public class AdminSelectController {
    private final Log logger = LogFactory.getLog(AdminSelectController.class);

    @Autowired
    private LitemallSelectService selectService;

    @RequiresPermissions("admin:select:read")
    @RequiresPermissionsDesc(menu = {"装箱管理", "装箱摆放细节"}, button = "详情")
    @GetMapping("/read")
    public Object read(@NotNull Integer id) {
        LitemallPackingSelect select = selectService.findById(id);
        return ResponseUtil.ok(select);
    }
    /**
     * 装箱管理
     *
     * @param fileId
     * @param fileTactics
     * @param detail
     * @param page
     * @param limit
     * @param sort
     * @param order
     * @return
     */
    @RequiresPermissions("admin:select:list")
    @RequiresPermissionsDesc(menu = {"装箱管理", "装箱摆放细节"}, button = "查询")
    @GetMapping("/list")
    public Object list(Integer fileId, Integer fileTactics, String detail,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort @RequestParam(defaultValue = "add_time") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order) {
        List<LitemallPackingSelect> SelectList = selectService.querySelective(fileId, fileTactics, detail, page, limit, sort, order);
        return ResponseUtil.okList(SelectList);
    }

//    @GetMapping("/catAndBrand")
//    public Object list2() {
//        return adminGoodsService.list2();
//    }

    /**
     * 编辑商品
     *
     * @param select
     * @return
     */
    @RequiresPermissions("admin:select:update")
    @RequiresPermissionsDesc(menu = {"装箱管理", "装箱摆放细节"}, button = "编辑")
    @PostMapping("/update")
    public Object update(@RequestBody LitemallPackingSelect select) {
        return selectService.updateById(select);
    }

    /**
     * 删除商品
     *
     * @param select
     * @return
     */
    @RequiresPermissions("admin:select:delete")
    @RequiresPermissionsDesc(menu = {"装箱管理", "装箱摆放细节"}, button = "删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody LitemallPackingSelect select) {
        Integer id = select.getId();
        selectService.deleteById(id);
        return ResponseUtil.ok();
    }

    /**
     * 添加商品
     *
     * @param select
     * @return
     */
    @RequiresPermissions("admin:select:create")
    @RequiresPermissionsDesc(menu = {"装箱管理", "装箱摆放细节"}, button = "添加")
    @PostMapping("/create")
    public Object add(@RequestBody LitemallPackingSelect select) {
        selectService.add(select);
        return ResponseUtil.ok(select);
    }


}
