package com.api.admin.interceptor;

import com.api.admin.config.PermessionLimit;
import com.api.admin.model.ApiUser;
import com.api.admin.service.LoginService;
import com.api.admin.service.impl.LoginServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author chentao.ji
 */
@Component
public class PermissionInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private LoginService loginService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return super.preHandle(request, response, handler);
        }
        boolean needLogin = true;
        boolean needAdminuser = false;
        HandlerMethod method = (HandlerMethod) handler;
        PermessionLimit permission = method.getMethodAnnotation(PermessionLimit.class);
        if (permission != null) {
            needLogin = permission.limit();
            needAdminuser = permission.superUser();
        }
        if (needLogin) {
            ApiUser loginUser = loginService.ifLogin(request);
            if (loginUser == null) {
                response.sendRedirect(request.getContextPath() + "/toLogin");
                return false;
            }
            if (needAdminuser && loginUser.getType() != 1) {
                throw new RuntimeException("权限拦截");
            }
            request.setAttribute(LoginServiceImpl.LOGIN_IDENTITY, loginUser);
        }
        return super.preHandle(request, response, handler);
    }

}
