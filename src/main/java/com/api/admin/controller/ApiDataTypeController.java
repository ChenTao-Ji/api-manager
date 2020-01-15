package com.api.admin.controller;

import com.api.admin.dao.ApiBizMapper;
import com.api.admin.dao.ApiDatatypeFiledsMapper;
import com.api.admin.dao.ApiDatatypeMapper;
import com.api.admin.dao.ApiDocumentMapper;
import com.api.admin.enums.FieldTypeEnum;
import com.api.admin.model.*;
import com.api.admin.service.ApiDataTypeService;
import com.api.admin.service.impl.LoginServiceImpl;
import com.api.admin.util.ApiDataTypeToCode;
import com.api.admin.util.ArrayTool;
import com.api.admin.util.JacksonUtil;
import com.api.admin.util.StringTool;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chentao.ji
 */
@Controller
@RequestMapping("/datatype")
public class ApiDataTypeController {

    @Autowired
    private ApiDatatypeMapper apiDatatypeMapper;

    @Autowired
    private ApiDatatypeFiledsMapper apiDatatypeFiledsMapper;

    @Autowired
    private ApiBizMapper apiBizMapper;

    @Autowired
    private ApiDocumentMapper apiDocumentMapper;

    @Autowired
    private ApiDataTypeService apiDataTypeService;

    @RequestMapping
    public String index(Model model) {
        model.addAttribute("bizList", apiBizMapper.selectList(Wrappers.query()));
        return "datatype/datatype.list";
    }

    @RequestMapping("/pageList")
    @ResponseBody
    public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "0") int start,
                                        @RequestParam(required = false, defaultValue = "10") int length,
                                        int bizId, String name) {
        List<ApiDatatype> list = apiDatatypeMapper.pageList(start, length, bizId, name);
        int count = apiDatatypeMapper.pageListCount(start, length, bizId, name);
        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("recordsTotal", count);
        maps.put("recordsFiltered", count);
        maps.put("data", list);
        return maps;
    }

    @RequestMapping("/addDataTypePage")
    public String addDataTypePage(Model model) {
        List<ApiBiz> bizList = apiBizMapper.selectList(Wrappers.query());
        model.addAttribute("bizList", bizList);
        return "datatype/datatype.add";
    }

    @RequestMapping("/addDataType")
    @ResponseBody
    public ReturnT<Integer> addDataType(HttpServletRequest request, ApiDatatype apiDataTypeDTO, String fieldTypeJson) {
        if (StringTool.isNotBlank(fieldTypeJson)) {
            List<ApiDatatypeFileds> fieldList = JacksonUtil.readValueRefer(fieldTypeJson, new TypeReference<List<ApiDatatypeFileds>>() {
            });
            if (fieldList != null && fieldList.size() > 0) {
                apiDataTypeDTO.setFieldList(fieldList);
            }
        }
        if (StringTool.isBlank(apiDataTypeDTO.getName())) {
            return new ReturnT<Integer>(ReturnT.FAIL_CODE, "数据类型名称不可为空");
        }
        if (StringTool.isBlank(apiDataTypeDTO.getAbout())) {
            return new ReturnT<Integer>(ReturnT.FAIL_CODE, "数据类型描述不可为空");
        }
        if (!hasBizPermission(request, apiDataTypeDTO.getBizId())) {
            return new ReturnT<Integer>(ReturnT.FAIL_CODE, "您没有相关业务线的权限,请联系管理员开通");
        }
        ApiBiz apiBiz = apiBizMapper.selectById(apiDataTypeDTO.getBizId());
        if (apiBiz == null) {
            return new ReturnT<Integer>(ReturnT.FAIL_CODE, "业务线ID非法");
        }
        ApiDatatype apiDatatypeQuery = new ApiDatatype();
        apiDatatypeQuery.setName(apiDataTypeDTO.getName());
        ApiDatatype existsByName = apiDatatypeMapper.selectOne(Wrappers.query(apiDatatypeQuery));
        if (existsByName != null) {
            return new ReturnT<Integer>(ReturnT.FAIL_CODE, "数据类型名称不可重复，请更换");
        }
        if (apiDataTypeDTO.getFieldList() != null && apiDataTypeDTO.getFieldList().size() > 0) {
            for (ApiDatatypeFileds field : apiDataTypeDTO.getFieldList()) {
                if (StringTool.isBlank(field.getFieldName())) {
                    return new ReturnT<Integer>(ReturnT.FAIL_CODE, "字段名称不可为空");
                }
                ApiDatatype filedDataType = apiDataTypeService.loadDataType(field.getFieldDatatypeId());
                if (filedDataType == null) {
                    return new ReturnT<Integer>(ReturnT.FAIL_CODE, "字段数据类型ID非法");
                }
                if (FieldTypeEnum.match(field.getFieldType()) == null) {
                    return new ReturnT<Integer>(ReturnT.FAIL_CODE, "字段形式非法");
                }
            }
        }
        int addRet = apiDatatypeMapper.insert(apiDataTypeDTO);
        if (addRet < 1) {
            return new ReturnT<Integer>(ReturnT.FAIL_CODE, "数据类型新增失败");
        }
        if (apiDataTypeDTO.getFieldList() != null && apiDataTypeDTO.getFieldList().size() > 0) {
            for (ApiDatatypeFileds field : apiDataTypeDTO.getFieldList()) {
                field.setParentDatatypeId(apiDataTypeDTO.getId());
            }
            int ret = apiDatatypeFiledsMapper.add(apiDataTypeDTO.getFieldList());
            if (ret < 1) {
                return new ReturnT<Integer>(ReturnT.FAIL_CODE, "数据类型新增失败");
            }
        }
        return apiDataTypeDTO.getId() > 0 ? new ReturnT<Integer>(apiDataTypeDTO.getId()) : new ReturnT<Integer>(ReturnT.FAIL_CODE, "");
    }

    @RequestMapping("/updateDataTypePage")
    public String updateDataTypePage(HttpServletRequest request, Model model, int dataTypeId) {
        ApiDatatype apiDataType = apiDataTypeService.loadDataType(dataTypeId);
        if (apiDataType == null) {
            throw new RuntimeException("数据类型ID非法");
        }
        model.addAttribute("apiDataType", apiDataType);
        if (!hasBizPermission(request, apiDataType.getBizId())) {
            throw new RuntimeException("您没有相关业务线的权限,请联系管理员开通");
        }
        List<ApiBiz> bizList = apiBizMapper.selectList(Wrappers.query());
        model.addAttribute("bizList", bizList);
        return "datatype/datatype.update";
    }

    @RequestMapping("/updateDataType")
    @ResponseBody
    public ReturnT<String> updateDataType(HttpServletRequest request, ApiDatatype apiDataTypeDTO, String fieldTypeJson) {
        if (StringTool.isNotBlank(fieldTypeJson)) {
            List<ApiDatatypeFileds> fieldList = JacksonUtil.readValueRefer(fieldTypeJson, new TypeReference<List<ApiDatatypeFileds>>() {
            });
            if (fieldList != null && fieldList.size() > 0) {
                apiDataTypeDTO.setFieldList(fieldList);
            }
        }
        if (StringTool.isBlank(apiDataTypeDTO.getName())) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "数据类型名称不可为空");
        }
        if (StringTool.isBlank(apiDataTypeDTO.getAbout())) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "数据类型描述不可为空");
        }
        if (!hasBizPermission(request, apiDataTypeDTO.getBizId())) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "您没有相关业务线的权限,请联系管理员开通");
        }
        ApiBiz apiBiz = apiBizMapper.selectById(apiDataTypeDTO.getBizId());
        if (apiBiz == null) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "业务线ID非法");
        }
        ApiDatatype apiDatatypeQuery = new ApiDatatype();
        apiDatatypeQuery.setName(apiDataTypeDTO.getName());
        ApiDatatype existsByName = apiDatatypeMapper.selectOne(Wrappers.query(apiDatatypeQuery));
        if (existsByName != null && !existsByName.getId().equals(apiDataTypeDTO.getId())) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "数据类型名称不可重复，请更换");
        }
        if (apiDataTypeDTO.getFieldList() != null && apiDataTypeDTO.getFieldList().size() > 0) {
            for (ApiDatatypeFileds field : apiDataTypeDTO.getFieldList()) {
                if (StringTool.isBlank(field.getFieldName())) {
                    return new ReturnT<String>(ReturnT.FAIL_CODE, "字段名称不可为空");
                }
                ApiDatatype filedDataType = apiDataTypeService.loadDataType(field.getFieldDatatypeId());
                if (filedDataType == null) {
                    return new ReturnT<String>(ReturnT.FAIL_CODE, "字段数据类型ID非法");
                }
                if (FieldTypeEnum.match(field.getFieldType()) == null) {
                    return new ReturnT<String>(ReturnT.FAIL_CODE, "字段形式非法");
                }
            }
        }
        int ret1 = apiDatatypeMapper.updateById(apiDataTypeDTO);
        if (ret1 > 0) {
            ApiDatatypeFileds apiDatatypeFiledsQuery = new ApiDatatypeFileds();
            apiDatatypeFiledsQuery.setParentDatatypeId(apiDataTypeDTO.getId());
            apiDatatypeFiledsMapper.delete(Wrappers.query(apiDatatypeFiledsQuery));
            if (apiDataTypeDTO.getFieldList() != null && apiDataTypeDTO.getFieldList().size() > 0) {
                for (ApiDatatypeFileds field : apiDataTypeDTO.getFieldList()) {
                    field.setParentDatatypeId(apiDataTypeDTO.getId());
                }
                int ret = apiDatatypeFiledsMapper.add(apiDataTypeDTO.getFieldList());
                if (ret < 1) {
                    return new ReturnT<String>(ReturnT.FAIL_CODE, "数据类型新增失败");
                }
            }
        }
        return ret1 > 0 ? ReturnT.SUCCESS : ReturnT.FAIL;
    }

    @RequestMapping("/dataTypeDetail")
    public String dataTypeDetail(HttpServletRequest request, Model model, int dataTypeId) {
        ApiDatatype apiDataType = apiDataTypeService.loadDataType(dataTypeId);
        if (apiDataType == null) {
            throw new RuntimeException("数据类型ID非法");
        }
        model.addAttribute("apiDataType", apiDataType);
        List<ApiBiz> bizList = apiBizMapper.selectList(Wrappers.query());
        model.addAttribute("bizList", bizList);
        String codeContent = ApiDataTypeToCode.parseDataTypeToCode(apiDataType);
        model.addAttribute("codeContent", codeContent);
        model.addAttribute("hasBizPermission", hasBizPermission(request, apiDataType.getBizId()));
        return "datatype/datatype.detail";
    }

    @RequestMapping("/deleteDataType")
    @ResponseBody
    public ReturnT<String> deleteDataType(HttpServletRequest request, int id) {
        ApiDatatype apiDataType = apiDatatypeMapper.selectById(id);
        if (apiDataType == null) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "数据类型ID非法");
        }
        if (!hasBizPermission(request, apiDataType.getBizId())) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "您没有相关业务线的权限,请联系管理员开通");
        }
        ApiDatatypeFileds apiDatatypeFiledsQuery = new ApiDatatypeFileds();
        apiDatatypeFiledsQuery.setFieldDatatypeId(id);
        List<ApiDatatypeFileds> list = apiDatatypeFiledsMapper.selectList(Wrappers.query(apiDatatypeFiledsQuery));
        if (list != null && list.size() > 0) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "该数据类型被引用中，拒绝删除");
        }
        ApiDocument apiDocumentQuery = new ApiDocument();
        apiDocumentQuery.setResponseDatatypeId(id);
        List<ApiDocument> apiList = apiDocumentMapper.selectList(Wrappers.query(apiDocumentQuery));
        if (apiList != null && apiList.size() > 0) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "该数据类型被API引用，拒绝删除");
        }
        int ret = apiDatatypeMapper.deleteById(id);
        ApiDatatypeFileds apiDatatypeFiledsDelete = new ApiDatatypeFileds();
        apiDatatypeFiledsDelete.setParentDatatypeId(id);
        apiDatatypeFiledsMapper.delete(Wrappers.query(apiDatatypeFiledsDelete));
        return ret > 0 ? ReturnT.SUCCESS : ReturnT.FAIL;
    }

    private boolean hasBizPermission(HttpServletRequest request, int bizId) {
        ApiUser loginUser = (ApiUser) request.getAttribute(LoginServiceImpl.LOGIN_IDENTITY);
        if (loginUser.getType() == 1 || ArrayTool.contains(StringTool.split(loginUser.getPermissionBiz(), ","), String.valueOf(bizId))) {
            return true;
        } else {
            return false;
        }
    }
}
