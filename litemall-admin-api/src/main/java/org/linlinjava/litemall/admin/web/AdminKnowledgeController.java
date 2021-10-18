package org.linlinjava.litemall.admin.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.linlinjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.linlinjava.litemall.admin.service.AdminKnowledgeService;
import org.linlinjava.litemall.core.validator.Order;
import org.linlinjava.litemall.core.validator.Sort;
import org.linlinjava.litemall.db.domain.LitemallKnowledge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/admin/knowledge")
@Validated
public class AdminKnowledgeController {
    private final Log logger = LogFactory.getLog(AdminKnowledgeController.class);

    @Autowired
    private AdminKnowledgeService adminKnowledgeService;

    /**
     * 查询商品
     *
     * @param title
     * @param readCount
     * @param status
     * @param page
     * @param limit
     * @param sort
     * @param order
     * @return
     */
    @RequiresPermissions("admin:Knowledge:list")
    @RequiresPermissionsDesc(menu = {"智能灯管理", "知识库"}, button = "查询")
    @GetMapping("/list")
    public Object list(Integer Id, String title, Integer readCount,Integer status,Integer isPopular,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort @RequestParam(defaultValue = "add_time") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order) {
        return adminKnowledgeService.list(Id, title, readCount, status, isPopular, page, limit, sort, order);
    }

    @GetMapping("/catAndBrand")
    public Object list2() {
        return adminKnowledgeService.list2();
    }

    /**
     * 编辑商品
     *
     * @param litemallKnowledge
     * @return
     */
    @RequiresPermissions("admin:Knowledge:update")
    @RequiresPermissionsDesc(menu = {"智能灯管理", "知识库"}, button = "编辑")
    @PostMapping("/update")
    public Object update(@RequestBody LitemallKnowledge litemallKnowledge) {
        return adminKnowledgeService.update(litemallKnowledge);
    }

    /**
     * 删除商品
     *
     * @param litemallKnowledge
     * @return
     */
    @RequiresPermissions("admin:Knowledge:delete")
    @RequiresPermissionsDesc(menu = {"智能灯管理", "知识库"}, button = "删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody LitemallKnowledge litemallKnowledge) {
        return adminKnowledgeService.delete(litemallKnowledge);
    }

    /**
     * 添加商品
     *
     * @param litemallKnowledge
     * @return
     */
    @RequiresPermissions("admin:Knowledge:create")
    @RequiresPermissionsDesc(menu = {"智能灯管理", "知识库"}, button = "上架")
    @PostMapping("/create")
    public Object create(@RequestBody LitemallKnowledge litemallKnowledge) {
        return adminKnowledgeService.create(litemallKnowledge);
    }

    /**
     * 商品详情
     *
     * @param id
     * @return
     */
    @RequiresPermissions("admin:Knowledge:read")
    @RequiresPermissionsDesc(menu = {"智能灯管理", "知识库"}, button = "详情")
    @GetMapping("/read")
    public Object detail(@NotNull Integer id) {
        return adminKnowledgeService.detail(id);

    }

}
