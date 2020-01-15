package com.api.admin.dao;

import com.api.admin.model.ApiDatatypeFileds;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author chentao.ji
 */
public interface ApiDatatypeFiledsMapper extends BaseMapper<ApiDatatypeFileds> {

    /**
     * 批量插入数据类型字段
     * @param apiDatatypeFileds
     * @return
     */
    int add(List<ApiDatatypeFileds> apiDatatypeFileds);

}