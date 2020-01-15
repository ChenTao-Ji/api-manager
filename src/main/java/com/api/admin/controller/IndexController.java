package com.api.admin.controller;

import com.api.admin.config.PermessionLimit;
import com.api.admin.model.ApiUser;
import com.api.admin.model.ReturnT;
import com.api.admin.service.LoginService;
import com.api.admin.util.StringTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author chentao.ji
 */
@Controller
public class IndexController {

    @Autowired
    private LoginService loginService;

    @RequestMapping("/")
    @PermessionLimit(limit = false)
    public String index(HttpServletRequest request) {
        ApiUser loginUser = loginService.ifLogin(request);
        if (loginUser == null) {
            return "redirect:/toLogin";
        }
        return "redirect:/project";
    }

    @RequestMapping("/toLogin")
    @PermessionLimit(limit = false)
    public String toLogin(HttpServletRequest request) {
        ApiUser loginUser = loginService.ifLogin(request);
        if (loginUser != null) {
            return "redirect:/";
        }
        return "login";
    }

    @RequestMapping(value = "login", method = RequestMethod.POST)
    @ResponseBody
    @PermessionLimit(limit = false)
    public ReturnT<String> loginDo(HttpServletResponse response, String ifRemember, String userName, String password) {
        boolean ifRem = false;
        if (StringTool.isNotBlank(ifRemember) && "on".equals(ifRemember)) {
            ifRem = true;
        }
        ReturnT<String> loginRet = loginService.login(response, userName, password, ifRem);
        return loginRet;
    }

    @RequestMapping(value = "logout", method = RequestMethod.POST)
    @ResponseBody
    @PermessionLimit(limit = false)
    public ReturnT<String> logout(HttpServletRequest request, HttpServletResponse response) {
        loginService.logout(request, response);
        return ReturnT.SUCCESS;
    }
}
