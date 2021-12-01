package org.linlinjava.litemall.admin.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.linlinjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.core.validator.Order;
import org.linlinjava.litemall.core.validator.Sort;
import org.linlinjava.litemall.db.domain.LitemallQuoteModel;
import org.linlinjava.litemall.db.service.LitemallQuoteModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/quoteModel")
@Validated
public class AdminQuoteModelController {
    private final Log logger = LogFactory.getLog(AdminQuoteModelController.class);

    @Autowired
    private LitemallQuoteModelService quoteModelService;

    @RequiresPermissions("admin:quoteModel:list")
    @RequiresPermissionsDesc(menu = {"配置管理", "询价单模板配置"}, button = "查询")
    @GetMapping("/list")

    public Object list(String name, String version,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort @RequestParam(defaultValue = "add_time") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order) {
        List<LitemallQuoteModel> quoteModelList = quoteModelService.querySelective(name, version, page, limit, sort, order);

        return ResponseUtil.okList(quoteModelList);
    }

    private Object validate(LitemallQuoteModel quoteModel) {
        String quoteModelName = quoteModel.getName();
        if (StringUtils.isEmpty(quoteModelName)) {
            return ResponseUtil.badArgument();
        }
        String version = quoteModel.getVersion();
        if (StringUtils.isEmpty(version)) {
            return ResponseUtil.badArgument();
        }
        return null;
    }
    @GetMapping("/options")
    public Object options() {
        List<LitemallQuoteModel> roleList = quoteModelService.queryAll();

        List<Map<String, Object>> options = new ArrayList<>(roleList.size());
        Map<String, Object> option = new HashMap<>();
        for (LitemallQuoteModel role : roleList) {
            option.put("value", role.getId());
            option.put("label", role.getName()+':'+role.getVersion());
            option.put("supply", role.getCode());
            option.put("notice", role.getNotice());
            option.put("approveCode", role.getApproveCode());
            option.put("ceoCode", role.getCeoCode());
            option.put("duty", role.getDuty());

            options.add(option);
        }
        return ResponseUtil.okList(options);
    }

    @RequiresPermissions("admin:quoteModel:create")
    @RequiresPermissionsDesc(menu = {"配置管理", "询价单模板配置"}, button = "新建")
    @PostMapping("/create")
    public Object create(@RequestBody LitemallQuoteModel quoteModel) {
        Object error = validate(quoteModel);
        if (error != null) {
            return error;
        }
        quoteModelService.add(quoteModel);
        return ResponseUtil.ok(quoteModel);
    }

//    @RequiresPermissions("admin:quoteModel:read")
//    @RequiresPermissionsDesc(menu = {"配置管理", "询价单模板配置"}, button = "详情")
    @GetMapping("/read")
    public Object read(@NotNull Integer id) {
        LitemallQuoteModel quoteModel = quoteModelService.findById(id);
        return ResponseUtil.ok(quoteModel);
    }

    @RequiresPermissions("admin:quoteModel:update")
    @RequiresPermissionsDesc(menu = {"配置管理", "询价单模板配置"}, button = "编辑")
    @PostMapping("/update")
    public Object update(@RequestBody LitemallQuoteModel ad) {
        Object error = validate(ad);
        if (error != null) {
            return error;
        }
        if (quoteModelService.updateById(ad) == 0) {
            return ResponseUtil.updatedDataFailed();
        }

        return ResponseUtil.ok(ad);
    }

    @RequiresPermissions("admin:quoteModel:delete")
    @RequiresPermissionsDesc(menu = {"配置管理", "询价单模板配置"}, button = "删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody LitemallQuoteModel ad) {
        Integer id = ad.getId();
        if (id == null) {
            return ResponseUtil.badArgument();
        }
        quoteModelService.deleteById(id);
        return ResponseUtil.ok();
    }

}
