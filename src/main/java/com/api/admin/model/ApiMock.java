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
@TableName("api_mock")
public class ApiMock implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 接口ID
     */
    private Integer documentId;

    /**
     * UUID
     */
    private String uuid;

    /**
     * Response Content-type：如JSON、XML、HTML、TEXT、JSONP
     */
    private String respType;

    /**
     * Response Content
     */
    private String respExample;
}