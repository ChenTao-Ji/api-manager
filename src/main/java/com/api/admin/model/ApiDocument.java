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
@TableName("api_document")
public class ApiDocument implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 项目ID
     */
    private Integer projectId;

    /**
     * 分组ID
     */
    private Integer groupId;

    /**
     * 接口名称
     */
    private String name;

    /**
     * 状态：0-启用、1-维护、2-废弃
     */
    private Byte status;

    /**
     * 星标等级：0-普通接口、1-一星接口
     */
    private Integer starLevel;

    /**
     * Request URL：相对地址
     */
    private String requestUrl;

    /**
     * Request Method：如POST、GET
     */
    private String requestMethod;

    /**
     * 响应数据类型ID
     */
    private Integer responseDatatypeId;

    /**
     * Response Content-type：成功接口，如JSON、XML、HTML、TEXT、JSONP
     */
    private String successRespType;

    /**
     * Response Content-type：失败接口
     */
    private String failRespType;

    /**
     * 创建时间
     */
    private Date addTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * Request Headers：Map-JSON格式字符串
     */
    private String requestHeaders;

    /**
     * Query String Parameters：VO-JSON格式字符串
     */
    private String queryParams;

    /**
     * Response Parameters：VO-JSON格式字符串
     */
    private String responseParams;

    /**
     * Response Content：成功接口
     */
    private String successRespExample;

    /**
     * Response Content：失败接口
     */
    private String failRespExample;

    /**
     * 备注
     */
    private String remark;
}