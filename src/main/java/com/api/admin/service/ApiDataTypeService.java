package com.api.admin.service;

import com.api.admin.model.ApiDatatype;

/**
 * @author chentao.ji
 */
public interface ApiDataTypeService {

    /**
     * 获取数据类型
     * @param dataTypeId
     * @return
     */
    ApiDatatype loadDataType(int dataTypeId);

}
