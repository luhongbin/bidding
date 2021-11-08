package org.linlinjava.litemall.admin.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.linlinjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.linlinjava.litemall.admin.vo.CategoryVo;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.db.domain.LitemallXmcatalog;
import org.linlinjava.litemall.db.service.LitemallXmCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/xmcategory")
@Validated
public class AdminXmCategoryController {
    private final Log logger = LogFactory.getLog(AdminCategoryController.class);
    @Autowired
    private LitemallXmCategoryService xmcategoryService;

    @RequiresPermissions("admin:xmcategory:list")
    @RequiresPermissionsDesc(menu = {"智能灯管理", "类目管理"}, button = "查询")
    @GetMapping("/list")
    public Object list() {
        List<CategoryVo> categoryVoList = new ArrayList<>();

        List<LitemallXmcatalog> categoryList = xmcategoryService.queryByPid(0);
        for (LitemallXmcatalog category : categoryList) {
            CategoryVo categoryVO = new CategoryVo();
            categoryVO.setId(category.getId());
            categoryVO.setDesc(category.getDesc());
            categoryVO.setIconUrl(category.getIconUrl());
            categoryVO.setPicUrl(category.getPicUrl());
            categoryVO.setKeywords(category.getKeywords());
            categoryVO.setName(category.getName());
            categoryVO.setLevel(category.getLevel());
            categoryVO.setSortOrder(category.getSortOrder());

            List<CategoryVo> children = new ArrayList<>();
            List<LitemallXmcatalog> subCategoryList = xmcategoryService.queryByPid(category.getId());
            for (LitemallXmcatalog subCategory : subCategoryList) {
                CategoryVo subCategoryVo = new CategoryVo();
                subCategoryVo.setId(subCategory.getId());
                subCategoryVo.setDesc(subCategory.getDesc());
                subCategoryVo.setIconUrl(subCategory.getIconUrl());
                subCategoryVo.setPicUrl(subCategory.getPicUrl());
                subCategoryVo.setKeywords(subCategory.getKeywords());
                subCategoryVo.setName(subCategory.getName());
                subCategoryVo.setLevel(subCategory.getLevel());
                subCategoryVo.setSortOrder(subCategory.getSortOrder());

                children.add(subCategoryVo);
            }

            categoryVO.setChildren(children);
            categoryVoList.add(categoryVO);
        }

        return ResponseUtil.okList(categoryVoList);
    }

    private Object validate(LitemallXmcatalog category) {
        String name = category.getName();
        if (StringUtils.isEmpty(name)) {
            return ResponseUtil.badArgument();
        }

        String level = category.getLevel();
        if (StringUtils.isEmpty(level)) {
            return ResponseUtil.badArgument();
        }
        if (!level.equals("L1") && !level.equals("L2")) {
            return ResponseUtil.badArgumentValue();
        }

        Integer pid = category.getPid();
        if (level.equals("L2") && (pid == null)) {
            return ResponseUtil.badArgument();
        }

        return null;
    }

    @RequiresPermissions("admin:xmcategory:create")
    @RequiresPermissionsDesc(menu = {"智能灯管理", "类目管理"}, button = "添加")
    @PostMapping("/create")
    public Object create(@RequestBody LitemallXmcatalog category) {
        Object error = validate(category);
        if (error != null) {
            return error;
        }
        xmcategoryService.add(category);
        return ResponseUtil.ok(category);
    }

    @RequiresPermissions("admin:xmcategory:read")
    @RequiresPermissionsDesc(menu = {"智能灯管理", "类目管理"}, button = "详情")
    @GetMapping("/read")
    public Object read(@NotNull Integer id) {
        LitemallXmcatalog category = xmcategoryService.findById(id);
        return ResponseUtil.ok(category);
    }

    @RequiresPermissions("admin:xmcategory:update")
    @RequiresPermissionsDesc(menu = {"智能灯管理", "类目管理"}, button = "编辑")
    @PostMapping("/update")
    public Object update(@RequestBody LitemallXmcatalog category) {
        Object error = validate(category);
        if (error != null) {
            return error;
        }

        if (xmcategoryService.updateById(category) == 0) {
            return ResponseUtil.updatedDataFailed();
        }
        return ResponseUtil.ok();
    }

    @RequiresPermissions("admin:xmcategory:delete")
    @RequiresPermissionsDesc(menu = {"智能灯管理", "类目管理"}, button = "删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody LitemallXmcatalog category) {
        Integer id = category.getId();
        if (id == null) {
            return ResponseUtil.badArgument();
        }
        xmcategoryService.deleteById(id);
        return ResponseUtil.ok();
    }

    @RequiresPermissions("admin:xmcategory:list")
    @GetMapping("/l1")
    public Object catL1() {
        // 所有一级分类目录
        List<LitemallXmcatalog> l1CatList = xmcategoryService.queryL1();
        List<Map<String, Object>> data = new ArrayList<>(l1CatList.size());
        for (LitemallXmcatalog category : l1CatList) {
            Map<String, Object> d = new HashMap<>(2);
            d.put("value", category.getId());
            d.put("label", category.getName());
            data.add(d);
        }
        return ResponseUtil.okList(data);
    }
}
