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
@TableName("api_group")
public class ApiGroup implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 项目ID
     */
    private Integer projectId;

    /**
     * 分组名称
     */
    private String name;

    /**
     * 分组排序
     */
    private Integer orders;
}