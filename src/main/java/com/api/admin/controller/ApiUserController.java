package com.api.admin.controller;

import com.api.admin.config.PermessionLimit;
import com.api.admin.dao.ApiBizMapper;
import com.api.admin.dao.ApiUserMapper;
import com.api.admin.model.ApiBiz;
import com.api.admin.model.ApiUser;
import com.api.admin.model.ReturnT;
import com.api.admin.service.impl.LoginServiceImpl;
import com.api.admin.util.StringTool;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chentao.ji
 */
@Controller
@RequestMapping("/user")
public class ApiUserController {

    @Autowired
    private ApiUserMapper apiUserMapper;

    @Autowired
    private ApiBizMapper apiBizMapper;

    @RequestMapping
    @PermessionLimit(superUser = true)
    public String index(Model model) {
        List<ApiBiz> bizList = apiBizMapper.selectList(Wrappers.query());
        model.addAttribute("bizList", bizList);
        return "user/user.list";
    }

    @RequestMapping("/pageList")
    @ResponseBody
    @PermessionLimit(superUser = true)
    public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "0") int start,
                                        @RequestParam(required = false, defaultValue = "10") int length,
                                        String userName, int type) {
        List<ApiUser> list = apiUserMapper.pageList(start, length, userName, type);
        int listCount = apiUserMapper.pageListCount(start, length, userName, type);
        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("recordsTotal", listCount);
        maps.put("recordsFiltered", listCount);
        maps.put("data", list);
        return maps;
    }

    @RequestMapping("/add")
    @ResponseBody
    @PermessionLimit(superUser = true)
    public ReturnT<String> add(ApiUser apiUser) {
        if (StringTool.isBlank(apiUser.getUsername())) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "请输入登录账号");
        }
        if (StringTool.isBlank(apiUser.getPassword())) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "请输入密码");
        }
        ApiUser apiUserQuery = new ApiUser();
        apiUserQuery.setUsername(apiUser.getUsername());
        ApiUser existUser = apiUserMapper.selectOne(Wrappers.query(apiUserQuery));
        if (existUser != null) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "“登录账号”重复，请更换");
        }
        String md5Password = DigestUtils.md5DigestAsHex(apiUser.getPassword().getBytes());
        apiUser.setPassword(md5Password);
        int ret = apiUserMapper.insert(apiUser);
        return (ret > 0) ? ReturnT.SUCCESS : ReturnT.FAIL;
    }

    @RequestMapping("/update")
    @ResponseBody
    @PermessionLimit(superUser = true)
    public ReturnT<String> update(HttpServletRequest request, ApiUser apiUser) {
        ApiUser loginUser = (ApiUser) request.getAttribute(LoginServiceImpl.LOGIN_IDENTITY);
        if (loginUser.getUsername().equals(apiUser.getUsername())) {
            return new ReturnT<String>(ReturnT.FAIL.getCode(), "禁止操作当前登录账号");
        }
        ApiUser apiUserQuery = new ApiUser();
        apiUserQuery.setUsername(loginUser.getUsername());
        ApiUser existUser = apiUserMapper.selectOne(Wrappers.query(apiUserQuery));
        if (existUser == null) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "更新失败，登录账号非法");
        }
        if (StringTool.isNotBlank(apiUser.getPassword())) {
            if (!(apiUser.getPassword().length() >= 4 && apiUser.getPassword().length() <= 50)) {
                return new ReturnT<String>(ReturnT.FAIL.getCode(), "密码长度限制为4~50");
            }
            String md5Password = DigestUtils.md5DigestAsHex(apiUser.getPassword().getBytes());
            existUser.setPassword(md5Password);
        }
        existUser.setType(apiUser.getType());
        int ret = apiUserMapper.updateById(existUser);
        return (ret > 0) ? ReturnT.SUCCESS : ReturnT.FAIL;
    }

    @RequestMapping("/delete")
    @ResponseBody
    @PermessionLimit(superUser = true)
    public ReturnT<String> delete(HttpServletRequest request, int id) {
        ApiUser delUser = apiUserMapper.selectById(id);
        if (delUser == null) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "拒绝删除，用户ID非法");
        }
        ApiUser loginUser = (ApiUser) request.getAttribute(LoginServiceImpl.LOGIN_IDENTITY);
        if (loginUser.getUsername().equals(delUser.getUsername())) {
            return new ReturnT<String>(ReturnT.FAIL.getCode(), "禁止操作当前登录账号");
        }
        int ret = apiUserMapper.deleteById(id);
        return (ret > 0) ? ReturnT.SUCCESS : ReturnT.FAIL;
    }

    @RequestMapping("/updatePwd")
    @ResponseBody
    public ReturnT<String> updatePwd(HttpServletRequest request, String password) {
        if (StringTool.isBlank(password)) {
            return new ReturnT<String>(ReturnT.FAIL.getCode(), "密码不可为空");
        }
        if (!(password.length() >= 4 && password.length() <= 100)) {
            return new ReturnT<String>(ReturnT.FAIL.getCode(), "密码长度限制为4~50");
        }
        String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());
        ApiUser loginUser = (ApiUser) request.getAttribute(LoginServiceImpl.LOGIN_IDENTITY);
        ApiUser apiUserQuery = new ApiUser();
        apiUserQuery.setUsername(loginUser.getUsername());
        ApiUser existUser = apiUserMapper.selectOne(Wrappers.query(apiUserQuery));
        existUser.setPassword(md5Password);
        apiUserMapper.updateById(existUser);
        return ReturnT.SUCCESS;
    }

    @RequestMapping("/updatePermissionBiz")
    @ResponseBody
    @PermessionLimit(superUser = true)
    public ReturnT<String> updatePermissionBiz(int id,
                                               @RequestParam(required = false) String[] permissionBiz) {
        String permissionProjectsStr = StringTool.join(permissionBiz, ",");
        ApiUser existUser = apiUserMapper.selectById(id);
        if (existUser == null) {
            return new ReturnT<String>(ReturnT.FAIL.getCode(), "参数非法");
        }
        existUser.setPermissionBiz(permissionProjectsStr);
        apiUserMapper.updateById(existUser);
        return ReturnT.SUCCESS;
    }
}
