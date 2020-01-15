package com.api.admin.service.impl;

import com.api.admin.dao.ApiDatatypeFiledsMapper;
import com.api.admin.dao.ApiDatatypeMapper;
import com.api.admin.model.ApiDatatype;
import com.api.admin.model.ApiDatatypeFileds;
import com.api.admin.service.ApiDataTypeService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author chentao.ji
 */
@Service
public class ApiDataTypeServiceImpl implements ApiDataTypeService {

    @Autowired
    private ApiDatatypeMapper apiDatatypeMapper;

    @Autowired
    private ApiDatatypeFiledsMapper apiDatatypeFiledsMapper;

    @Override
    public ApiDatatype loadDataType(int dataTypeId) {
        ApiDatatype dataType = apiDatatypeMapper.selectById(dataTypeId);
        if (dataType == null) {
            return dataType;
        }
        int maxRelateLevel = 5;
        return fillFileDataType(dataType, maxRelateLevel);
    }

    /**
     * parse field of datatype (注意，循环引用问题；此处显示最长引用链路长度为5；)
     */
    private ApiDatatype fillFileDataType(ApiDatatype dataType, int maxRelateLevel) {
        ApiDatatypeFileds query = new ApiDatatypeFileds();
        query.setParentDatatypeId(dataType.getId());
        List<ApiDatatypeFileds> fieldList = apiDatatypeFiledsMapper.selectList(Wrappers.query(query));
        dataType.setFieldList(fieldList);
        if (dataType.getFieldList() != null && dataType.getFieldList().size() > 0 && maxRelateLevel > 0) {
            for (ApiDatatypeFileds field : dataType.getFieldList()) {
                ApiDatatype fieldDataType = apiDatatypeMapper.selectById(field.getFieldDatatypeId());
                fieldDataType = fillFileDataType(fieldDataType, --maxRelateLevel);
                field.setFieldDatatype(fieldDataType);
            }
        }
        return dataType;
    }
}
