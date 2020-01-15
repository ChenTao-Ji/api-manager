package com.api.admin.dao;

import com.api.admin.model.ApiProject;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author chentao.ji
 */
public interface ApiProjectMapper extends BaseMapper<ApiProject> {

    /**
     * 查询项目列表数据
     * @param offset
     * @param pagesize
     * @param name
     * @param bizId
     * @return
     */
    List<ApiProject> pageList(@Param("offset") int offset,
                                 @Param("pagesize") int pagesize,
                                 @Param("name") String name,
                                 @Param("bizId") int bizId);

    /**
     * 查询项目列表数量
     * @param offset
     * @param pagesize
     * @param name
     * @param bizId
     * @return
     */
    int pageListCount(@Param("offset") int offset,
                      @Param("pagesize") int pagesize,
                      @Param("name") String name,
                      @Param("bizId") int bizId);
}