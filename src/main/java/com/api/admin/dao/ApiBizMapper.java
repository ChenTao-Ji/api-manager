package com.api.admin.dao;

import com.api.admin.model.ApiBiz;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author chentao.ji
 */
public interface ApiBizMapper extends BaseMapper<ApiBiz> {

    /**
     * 查询业务线列表数据
     * @param offset
     * @param pagesize
     * @param bizName
     * @return
     */
    List<ApiBiz> pageList(@Param("offset") int offset,
                          @Param("pagesize") int pagesize,
                          @Param("bizName") String bizName);

    /**
     * 查询业务线列表数量
     * @param offset
     * @param pagesize
     * @param bizName
     * @return
     */
    int pageListCount(@Param("offset") int offset,
                      @Param("pagesize") int pagesize,
                      @Param("bizName") String bizName);
}