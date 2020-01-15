package com.api.admin.service;

import com.api.admin.model.ApiUser;
import com.api.admin.model.ReturnT;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author chentao.ji
 */
public interface LoginService {

    ReturnT<String> login(HttpServletResponse response, String usernameParam, String passwordParam, boolean ifRemember);

    void logout(HttpServletRequest request, HttpServletResponse response);

    ApiUser ifLogin(HttpServletRequest request);
}
