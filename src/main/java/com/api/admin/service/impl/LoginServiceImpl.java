package com.api.admin.service.impl;

import com.api.admin.dao.ApiUserMapper;
import com.api.admin.model.ApiUser;
import com.api.admin.model.ReturnT;
import com.api.admin.service.LoginService;
import com.api.admin.util.CookieUtil;
import com.api.admin.util.JacksonUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;

/**
 * @author chentao.ji
 */
@Service
public class LoginServiceImpl implements LoginService {

    public static final String LOGIN_IDENTITY = "API_LOGIN_IDENTITY";

    @Autowired
    private ApiUserMapper apiUserMapper;

    @Override
    public ReturnT<String> login(HttpServletResponse response, String usernameParam, String passwordParam, boolean ifRemember) {
        ApiUser query = new ApiUser();
        query.setUsername(usernameParam);
        ApiUser apiUser = apiUserMapper.selectOne(Wrappers.query(query));
        if (apiUser == null) {
            return new ReturnT<String>(500, "账号或密码错误");
        }
        String passwordParamMd5 = DigestUtils.md5DigestAsHex(passwordParam.getBytes());
        if (!apiUser.getPassword().equals(passwordParamMd5)) {
            return new ReturnT<String>(500, "账号或密码错误");
        }
        String loginToken = makeToken(apiUser);
        CookieUtil.set(response, LOGIN_IDENTITY, loginToken, ifRemember);
        return ReturnT.SUCCESS;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        CookieUtil.remove(request, response, LOGIN_IDENTITY);
    }

    @Override
    public ApiUser ifLogin(HttpServletRequest request) {
        String cookieToken = CookieUtil.getValue(request, LOGIN_IDENTITY);
        if (cookieToken != null) {
            ApiUser cookieUser = parseToken(cookieToken);
            if (cookieUser != null) {
                ApiUser query = new ApiUser();
                query.setUsername(cookieUser.getUsername());
                ApiUser dbUser = apiUserMapper.selectOne(Wrappers.query(query));
                if (dbUser != null) {
                    if (cookieUser.getPassword().equals(dbUser.getPassword())) {
                        return dbUser;
                    }
                }
            }
        }
        return null;
    }

    private String makeToken(ApiUser apiUser) {
        String tokenJson = JacksonUtil.writeValueAsString(apiUser);
        String tokenHex = new BigInteger(tokenJson.getBytes()).toString(16);
        return tokenHex;
    }

    private ApiUser parseToken(String tokenHex) {
        ApiUser apiUser = null;
        if (tokenHex != null) {
            String tokenJson = new String(new BigInteger(tokenHex, 16).toByteArray());
            apiUser = JacksonUtil.readValue(tokenJson, ApiUser.class);
        }
        return apiUser;
    }
}
