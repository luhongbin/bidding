package org.linlinjava.litemall.admin.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.linlinjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.core.validator.Order;
import org.linlinjava.litemall.core.validator.Sort;
import org.linlinjava.litemall.db.domain.LitemallFile;
import org.linlinjava.litemall.db.service.LitemallFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/admin/file")
@Validated
public class AdminFileController {
    private final Log logger = LogFactory.getLog(AdminFileController.class);

    @Autowired
    private LitemallFileService fileService;

    @RequiresPermissions("admin:file:list")
    @RequiresPermissionsDesc(menu = {"装箱管理", "装箱文件"}, button = "查询")
    @GetMapping("/list")
    public Object list(Integer userId, String packingFilename, String packingFile,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort @RequestParam(defaultValue = "add_time") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order) {
        List<LitemallFile> FileList = fileService.querySelective(userId,packingFilename, packingFile, page, limit, sort, order);
        return ResponseUtil.okList(FileList);
    }

    private Object validate(LitemallFile file) {
        Integer userId = file.getUserId();
        if (StringUtils.isEmpty(userId)) {
            return ResponseUtil.badArgument();
        }
        String packingFilename = file.getPackingFilename();
        if (StringUtils.isEmpty(packingFilename)) {
            return ResponseUtil.badArgument();
        }
        String packingFile = file.getPackingFile();
        if (StringUtils.isEmpty(packingFile)) {
            return ResponseUtil.badArgument();
        }
        return null;
    }

    @RequiresPermissions("admin:file:create")
    @RequiresPermissionsDesc(menu = {"装箱管理", "装箱文件"}, button = "添加")
    @PostMapping("/create")
    public Object create(@RequestBody LitemallFile file) {
        Object error = validate(file);
        if (error != null) {
            return error;
        }
        fileService.add(file);
        return ResponseUtil.ok(file);
    }

    @RequiresPermissions("admin:file:read")
    @RequiresPermissionsDesc(menu = {"装箱管理", "装箱文件"}, button = "详情")
    @GetMapping("/read")
    public Object read(@NotNull Integer id) {
        LitemallFile file = fileService.findById(id);
        return ResponseUtil.ok(file);
    }

    @RequiresPermissions("admin:file:update")
    @RequiresPermissionsDesc(menu = {"装箱管理", "装箱文件"}, button = "编辑")
    @PostMapping("/update")
    public Object update(@RequestBody LitemallFile file) {
        Object error = validate(file);
        if (error != null) {
            return error;
        }
        if (fileService.updateById(file) == 0) {
            return ResponseUtil.updatedDataFailed();
        }

        return ResponseUtil.ok(file);
    }

    @RequiresPermissions("admin:file:delete")
    @RequiresPermissionsDesc(menu = {"装箱管理", "装箱文件"}, button = "删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody LitemallFile file) {
        Integer id = file.getId();
        if (id == null) {
            return ResponseUtil.badArgument();
        }
        fileService.deleteById(id);
        return ResponseUtil.ok();
    }

    /**
     * 单文件上传
     * @param files 接收文件要以数组接收
     * @return
     */
    @RequiresPermissions("admin:file:uploadFile")
    @RequiresPermissionsDesc(menu = {"装箱管理", "装箱文件"}, button = "上传")
    @PostMapping(value="/uploadFile")
    public void uploadFile(@RequestBody LitemallFile[] files) {
        // TODO
    }
}
