package com.api.admin.dao;

import com.api.admin.model.ApiUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author chentao.ji
 */
public interface ApiUserMapper extends BaseMapper<ApiUser> {

    /**
     * 查询用户据列表数据
     * @param offset
     * @param pagesize
     * @param userName
     * @param type
     * @return
     */
    List<ApiUser> pageList(@Param("offset") int offset,
                              @Param("pagesize") int pagesize,
                              @Param("userName") String userName,
                              @Param("type") int type);

    /**
     * 查询用户据列表数量
     * @param offset
     * @param pagesize
     * @param userName
     * @param type
     * @return
     */
    int pageListCount(@Param("offset") int offset,
                      @Param("pagesize") int pagesize,
                      @Param("userName") String userName,
                      @Param("type") int type);
}