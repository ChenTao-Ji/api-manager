package com.api.admin.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;

/**
 * @author chentao.ji
 */
@Data
@TableName("api_user")
public class ApiUser implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 账号
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 用户类型：0-普通用户、1-超级管理员
     */
    private Byte type;

    /**
     * 业务线权限，多个逗号分隔
     */
    private String permissionBiz;
}