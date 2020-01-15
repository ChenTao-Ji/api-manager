package com.api.admin.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author chentao.ji
 */
@Data
@TableName("api_project")
public class ApiProject implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 项目名称
     */
    private String name;

    /**
     * 项目描述
     */
    private String description;

    /**
     * 根地址：线上环境
     */
    private String baseUrlProduct;

    /**
     * 根地址：预发布环境
     */
    private String baseUrlPpe;

    /**
     * 根地址：测试环境
     */
    private String baseUrlQa;

    /**
     * 业务线ID
     */
    private Integer bizId;
}