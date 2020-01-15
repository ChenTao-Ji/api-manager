package com.api.admin.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author chentao.ji
 */
@Data
@TableName("api_datatype_fileds")
public class ApiDatatypeFileds implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 所属，数据类型ID
     */
    private Integer parentDatatypeId;

    /**
     * 字段名称
     */
    private String fieldName;

    /**
     * 字段描述
     */
    private String fieldAbout;

    /**
     * 字段数据类型ID
     */
    private Integer fieldDatatypeId;

    /**
     * 字段形式：0=默认、1=数组
     */
    private Integer fieldType;

    @TableField(exist = false)
    private ApiDatatype fieldDatatype;
}