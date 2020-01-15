package com.api.admin.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author chentao.ji
 */
@Data
@TableName("api_test_history")
public class ApiTestHistory implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 接口ID
     */
    private Integer documentId;

    private Date addTime;

    private Date updateTime;

    /**
     * Request URL：绝对地址
     */
    private String requestUrl;

    /**
     * Request Method：如POST、GET
     */
    private String requestMethod;

    /**
     * Response Content-type：如JSON
     */
    private String respType;

    /**
     * Request Headers：Map-JSON格式字符串
     */
    private String requestHeaders;

    /**
     * Query String Parameters：VO-JSON格式字符串
     */
    private String queryParams;
}