package org.linlinjava.litemall.admin.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.subject.Subject;
import org.linlinjava.litemall.admin.service.LogHelper;
import org.linlinjava.litemall.admin.util.Permission;
import org.linlinjava.litemall.admin.util.PermissionUtil;
import org.linlinjava.litemall.admin.vo.CategoryVo;
import org.linlinjava.litemall.core.util.IpUtil;
import org.linlinjava.litemall.core.util.JacksonUtil;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.core.util.bcrypt.BCryptPasswordEncoder;
import org.linlinjava.litemall.db.domain.LitemallAdmin;
import org.linlinjava.litemall.db.domain.LitemallCategory;
import org.linlinjava.litemall.db.domain.LitemallUser;
import org.linlinjava.litemall.db.service.LitemallAdminService;
import org.linlinjava.litemall.db.service.LitemallPermissionService;
import org.linlinjava.litemall.db.service.LitemallRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;

import static org.linlinjava.litemall.admin.util.AdminResponseCode.ADMIN_INVALID_ACCOUNT;
import static org.linlinjava.litemall.admin.util.AdminResponseCode.ADMIN_NAME_EXIST;

@RestController
@RequestMapping("/admin/auth")
@Validated
public class AdminAuthController {
    private final Log logger = LogFactory.getLog(AdminAuthController.class);

    @Autowired
    private LitemallAdminService adminService;
    @Autowired
    private LitemallRoleService roleService;
    @Autowired
    private LitemallPermissionService permissionService;
    @Autowired
    private LogHelper logHelper;

    /*
     *  { username : value, password : value }
     */
    @PostMapping("/login")
    public Object login(@RequestBody String body, HttpServletRequest request) {
        String username = JacksonUtil.parseString(body, "username");
        String password = JacksonUtil.parseString(body, "password");

        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            return ResponseUtil.badArgument();
        }

        Subject currentUser = SecurityUtils.getSubject();
        try {
            currentUser.login(new UsernamePasswordToken(username, password));
        } catch (UnknownAccountException uae) {
            logHelper.logAuthFail("登录", "用户帐号或密码不正确");
            return ResponseUtil.fail(ADMIN_INVALID_ACCOUNT, "用户帐号或密码不正确");
        } catch (LockedAccountException lae) {
            logHelper.logAuthFail("登录", "用户帐号已锁定不可用");
            return ResponseUtil.fail(ADMIN_INVALID_ACCOUNT, "用户帐号已锁定不可用");

        } catch (AuthenticationException ae) {
            logHelper.logAuthFail("登录", "认证失败");
//            List<LitemallAdmin> adminList = adminService.findAdmin(username);
//            Integer id = 0;
//            LitemallAdmin adminInfo = new LitemallAdmin();
//            for (LitemallAdmin admin : adminList) {
//                adminInfo.setId(admin.getId());
//                adminInfo.setUsername(admin.getUsername());
//                adminInfo.setAvatar(admin.getAvatar());
//                id = admin.getId();
//            }
//            logger.info("系统id--" + id);
//            String token = AdminTokenManager.generateToken(id);
//
//            // token
//
//            Map<Object, Object> result = new HashMap<Object, Object>();
//            logger.info("token--" + token);
//
//            result.put("token", token);
//            result.put("adminInfo", adminInfo);
//            return ResponseUtil.ok(result);
            return ResponseUtil.fail(ADMIN_INVALID_ACCOUNT, "认证失败");
        }

        currentUser = SecurityUtils.getSubject();
        LitemallAdmin admin = (LitemallAdmin) currentUser.getPrincipal();
        admin.setLastLoginIp(IpUtil.getIpAddr(request));
        admin.setLastLoginTime(LocalDateTime.now());
        adminService.updateById(admin);

        logHelper.logAuthSucceed("登录");

        // userInfo
        Map<String, Object> adminInfo = new HashMap<String, Object>();
        adminInfo.put("nickName", admin.getUsername());
        adminInfo.put("avatar", admin.getAvatar());

        Map<Object, Object> result = new HashMap<Object, Object>();
        result.put("token", currentUser.getSession().getId());
        result.put("adminInfo", adminInfo);
        return ResponseUtil.ok(result);
    }
//    /**
//     * 钉钉登录
//     *
//     * @param body 请求内容，{ userid: xxx, name: xxx, mobile: xxx, avatar: xxx }
//     * @param request     请求对象
//     * @return 登录结果
//     */
//    @PostMapping("login_by_dingtalk")
//    public Object loginByDingtalk(@RequestBody String body, HttpServletRequest request) {
//        logHelper.logAuthFail("登录", "用户帐号或密码不正确");
//        String openId = JacksonUtil.parseString(body, "userid");
//        String name = JacksonUtil.parseString(body, "name");
//        String mobile = JacksonUtil.parseString(body, "mobile");
//        String avatar = JacksonUtil.parseString(body, "avatar");
//        logger.info(String.format("%s %s %s %s", openId, name, mobile, avatar));
//
//        String sessionKey = "dingtalk";
//
//        LitemallUser user = userService.queryByOid(openId);
//        if (user == null  || openId == null) {
//            user = new LitemallUser();
//            user.setUsername(name);
//            user.setPassword(openId);
//            user.setWeixinOpenid(openId);
//            user.setAvatar(avatar);
//            user.setNickname(name);
//            user.setMobile(mobile);
//            user.setUserLevel((byte) 0);
//            user.setStatus((byte) 0);
//            user.setLastLoginTime(LocalDateTime.now());
//            user.setLastLoginIp(IpUtil.getIpAddr(request));
//            user.setSessionKey(sessionKey);
//
//            userService.add(user);
//        } else {
//            user.setLastLoginTime(LocalDateTime.now());
//            user.setLastLoginIp(IpUtil.getIpAddr(request));
//            user.setSessionKey(sessionKey);
//            if (userService.updateById(user) == 0) {
//                return ResponseUtil.updatedDataFailed();
//            }
//        }
//        // userInfo
//        Subject currentUser = SecurityUtils.getSubject();
//
//        // token
//
//        // userInfo
//        Map<String, Object> userInfo = new HashMap<String, Object>();
//        userInfo.put("nickName", user.getUsername());
//        userInfo.put("avatar", user.getAvatar());
//
//        Map<Object, Object> result = new HashMap<Object, Object>();
//        result.put("token", currentUser.getSession().getId());
//        result.put("userInfo", userInfo);
//
//        return ResponseUtil.ok(result);
//    }
    /*
     *
     */
    @RequiresAuthentication
    @PostMapping("/logout")
    public Object logout() {
        Subject currentUser = SecurityUtils.getSubject();

        logHelper.logAuthSucceed("退出");
        currentUser.logout();
        return ResponseUtil.ok();
    }


    @RequiresAuthentication
    @GetMapping("/info")
    public Object info() {
        Subject currentUser = SecurityUtils.getSubject();
        LitemallAdmin admin = (LitemallAdmin) currentUser.getPrincipal();

        Map<String, Object> data = new HashMap<>();
        data.put("name", admin.getUsername());
        data.put("avatar", admin.getAvatar());

        Integer[] roleIds = admin.getRoleIds();
        Set<String> roles = roleService.queryByIds(roleIds);
        Set<String> permissions = permissionService.queryByRoleIds(roleIds);
        data.put("roles", roles);
        // NOTE
        // 这里需要转换perms结构，因为对于前端而已API形式的权限更容易理解
        data.put("perms", toApi(permissions));
        return ResponseUtil.ok(data);
    }

    @Autowired
    private ApplicationContext context;
    private HashMap<String, String> systemPermissionsMap = null;

    private Collection<String> toApi(Set<String> permissions) {
        if (systemPermissionsMap == null) {
            systemPermissionsMap = new HashMap<>();
            final String basicPackage = "org.linlinjava.litemall.admin";
            List<Permission> systemPermissions = PermissionUtil.listPermission(context, basicPackage);
            for (Permission permission : systemPermissions) {
                String perm = permission.getRequiresPermissions().value()[0];
                String api = permission.getApi();
                systemPermissionsMap.put(perm, api);
            }
        }

        Collection<String> apis = new HashSet<>();
        for (String perm : permissions) {
            String api = systemPermissionsMap.get(perm);
            apis.add(api);

            if (perm.equals("*")) {
                apis.clear();
                apis.add("*");
                return apis;
                //                return systemPermissionsMap.values();

            }
        }
        return apis;
    }

    @GetMapping("/401")
    public Object page401() {
        return ResponseUtil.unlogin();
    }

    @GetMapping("/index")
    public Object pageIndex() {
        return ResponseUtil.ok();
    }

    @GetMapping("/403")
    public Object page403() {
        return ResponseUtil.unauthz();
    }
}
