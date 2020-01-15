package com.api.admin.dao;

import com.api.admin.model.ApiDocument;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author chentao.ji
 */
public interface ApiDocumentMapper extends BaseMapper<ApiDocument> {

    /**
     * 根据条件获取全部信息
     * @param projectId
     * @param groupId
     * @return
     */
    List<ApiDocument> loadAll(@Param("projectId") int projectId,
                                 @Param("groupId") int groupId);
}