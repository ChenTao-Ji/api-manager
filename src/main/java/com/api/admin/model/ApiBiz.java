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
@TableName("api_biz")
public class ApiBiz implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 业务线名称
     */
    private String bizName;

    /**
     * 排序
     */
    private Integer orders;
}