package com.api.admin.dao;

import com.api.admin.model.ApiDatatype;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author chentao.ji
 */
public interface ApiDatatypeMapper extends BaseMapper<ApiDatatype> {

    int pageListCount(@Param("offset") int offset,
                      @Param("pagesize") int pagesize,
                      @Param("bizId") int bizId,
                      @Param("name") String name);

    List<ApiDatatype> pageList(@Param("offset") int offset,
                                  @Param("pagesize") int pagesize,
                                  @Param("bizId") int bizId,
                                  @Param("name") String name);
}