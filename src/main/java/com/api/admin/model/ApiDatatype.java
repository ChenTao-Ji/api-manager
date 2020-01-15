package com.api.admin.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author chentao.ji
 */
@Data
@TableName("api_datatype")
public class ApiDatatype implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 数据类型名称
     */
    private String name;

    /**
     * 数据类型描述
     */
    private String about;

    /**
     * 业务线ID，为0表示公共
     */
    private Integer bizId;

    /**
     * 负责人
     */
    private String owner;

    /**
     * 参数列表
     */
    @TableField(exist = false)
    private List<ApiDatatypeFileds> fieldList;
}