package com.api.admin.controller;

import com.api.admin.dao.*;
import com.api.admin.enums.RequestConfig;
import com.api.admin.model.*;
import com.api.admin.service.ApiDataTypeService;
import com.api.admin.service.impl.LoginServiceImpl;
import com.api.admin.util.ArrayTool;
import com.api.admin.util.JacksonUtil;
import com.api.admin.util.StringTool;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author chentao.ji
 */
@Controller
@RequestMapping("/document")
public class ApiDocumentController {

    @Autowired
    private ApiDocumentMapper apiDocumentMapper;

    @Autowired
    private ApiProjectMapper apiProjectMapper;

    @Autowired
    private ApiGroupMapper apiGroupMapper;

    @Autowired
    private ApiMockMapper apiMockMapper;

    @Autowired
    private ApiTestHistoryMapper apiTestHistoryMapper;

    @Autowired
    private ApiDataTypeService apiDataTypeService;

    @RequestMapping("/markStar")
    @ResponseBody
    public ReturnT<String> markStar(HttpServletRequest request, int id, int starLevel) {
        ApiDocument document = apiDocumentMapper.selectById(id);
        if (document == null) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "操作失败，接口ID非法");
        }
        ApiProject apiProject = apiProjectMapper.selectById(document.getProjectId());
        if (!hasBizPermission(request, apiProject.getBizId())) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "您没有相关业务线的权限,请联系管理员开通");
        }
        document.setStarLevel(starLevel);
        int ret = apiDocumentMapper.updateById(document);
        return (ret > 0) ? ReturnT.SUCCESS : ReturnT.FAIL;
    }

    @RequestMapping("/delete")
    @ResponseBody
    public ReturnT<String> delete(HttpServletRequest request, int id) {
        ApiDocument document = apiDocumentMapper.selectById(id);
        if (document == null) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "操作失败，接口ID非法");
        }
        ApiProject apiProject = apiProjectMapper.selectById(document.getProjectId());
        if (!hasBizPermission(request, apiProject.getBizId())) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "您没有相关业务线的权限,请联系管理员开通");
        }
        ApiTestHistory apiTestHistoryQuery = new ApiTestHistory();
        apiTestHistoryQuery.setDocumentId(id);
        List<ApiTestHistory> historyList = apiTestHistoryMapper.selectList(Wrappers.query(apiTestHistoryQuery));
        if (historyList != null && historyList.size() > 0) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "拒绝删除，该接口下存在Test记录，不允许删除");
        }
        ApiMock apiMockQuery = new ApiMock();
        apiMockQuery.setDocumentId(id);
        List<ApiMock> mockList = apiMockMapper.selectList(Wrappers.query(apiMockQuery));
        if (mockList != null && mockList.size() > 0) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "拒绝删除，该接口下存在Mock记录，不允许删除");
        }
        int ret = apiDocumentMapper.deleteById(id);
        return (ret > 0) ? ReturnT.SUCCESS : ReturnT.FAIL;
    }

    /**
     * 新增，API
     *
     * @param projectId
     * @return
     */
    @RequestMapping("/addPage")
    public String addPage(HttpServletRequest request, Model model, int projectId, @RequestParam(required = false, defaultValue = "0") int groupId) {
        ApiProject project = apiProjectMapper.selectById(projectId);
        if (project == null) {
            throw new RuntimeException("操作失败，项目ID非法");
        }
        model.addAttribute("projectId", projectId);
        model.addAttribute("groupId", groupId);
        if (!hasBizPermission(request, project.getBizId())) {
            throw new RuntimeException("您没有相关业务线的权限,请联系管理员开通");
        }
        ApiGroup apiGroupQuery = new ApiGroup();
        apiGroupQuery.setProjectId(projectId);
        List<ApiGroup> groupList = apiGroupMapper.selectList(Wrappers.query(apiGroupQuery));
        model.addAttribute("groupList", groupList);
        model.addAttribute("RequestMethodEnum", RequestConfig.RequestMethodEnum.values());
        model.addAttribute("requestHeadersEnum", RequestConfig.requestHeadersEnum);
        model.addAttribute("QueryParamTypeEnum", RequestConfig.QueryParamTypeEnum.values());
        model.addAttribute("ResponseContentType", RequestConfig.ResponseContentType.values());

        return "document/document.add";
    }

    @RequestMapping("/add")
    @ResponseBody
    public ReturnT<Integer> add(HttpServletRequest request, ApiDocument apiDocument) {
        ApiProject project = apiProjectMapper.selectById(apiDocument.getProjectId());
        if (project == null) {
            return new ReturnT<Integer>(ReturnT.FAIL_CODE, "操作失败，项目ID非法");
        }
        if (!hasBizPermission(request, project.getBizId())) {
            return new ReturnT<Integer>(ReturnT.FAIL_CODE, "您没有相关业务线的权限,请联系管理员开通");
        }
        int ret = apiDocumentMapper.insert(apiDocument);
        return (ret > 0) ? new ReturnT<Integer>(apiDocument.getId()) : new ReturnT<Integer>(ReturnT.FAIL_CODE, null);
    }

    /**
     * 更新，API
     *
     * @return
     */
    @RequestMapping("/updatePage")
    public String updatePage(HttpServletRequest request, Model model, int id) {
        ApiDocument apiDocument = apiDocumentMapper.selectById(id);
        if (apiDocument == null) {
            throw new RuntimeException("操作失败，接口ID非法");
        }
        model.addAttribute("document", apiDocument);
        model.addAttribute("requestHeadersList", (StringTool.isNotBlank(apiDocument.getRequestHeaders())) ? JacksonUtil.readValue(apiDocument.getRequestHeaders(), List.class) : null);
        model.addAttribute("queryParamList", (StringTool.isNotBlank(apiDocument.getQueryParams())) ? JacksonUtil.readValue(apiDocument.getQueryParams(), List.class) : null);
        model.addAttribute("responseParamList", (StringTool.isNotBlank(apiDocument.getResponseParams())) ? JacksonUtil.readValue(apiDocument.getResponseParams(), List.class) : null);
        int projectId = apiDocument.getProjectId();
        model.addAttribute("projectId", projectId);
        ApiProject project = apiProjectMapper.selectById(apiDocument.getProjectId());
        if (!hasBizPermission(request, project.getBizId())) {
            throw new RuntimeException("您没有相关业务线的权限,请联系管理员开通");
        }
        ApiGroup apiGroupQuery = new ApiGroup();
        apiGroupQuery.setProjectId(projectId);
        List<ApiGroup> groupList = apiGroupMapper.selectList(Wrappers.query(apiGroupQuery));
        model.addAttribute("groupList", groupList);
        model.addAttribute("RequestMethodEnum", RequestConfig.RequestMethodEnum.values());
        model.addAttribute("requestHeadersEnum", RequestConfig.requestHeadersEnum);
        model.addAttribute("QueryParamTypeEnum", RequestConfig.QueryParamTypeEnum.values());
        model.addAttribute("ResponseContentType", RequestConfig.ResponseContentType.values());
        ApiDatatype responseDatatypeRet = apiDataTypeService.loadDataType(apiDocument.getResponseDatatypeId());
        model.addAttribute("responseDatatype", responseDatatypeRet);
        return "document/document.update";
    }

    @RequestMapping("/update")
    @ResponseBody
    public ReturnT<String> update(HttpServletRequest request, ApiDocument apiDocument) {
        ApiDocument oldVo = apiDocumentMapper.selectById(apiDocument.getId());
        if (oldVo == null) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "操作失败，接口ID非法");
        }
        ApiProject project = apiProjectMapper.selectById(oldVo.getProjectId());
        if (!hasBizPermission(request, project.getBizId())) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "您没有相关业务线的权限,请联系管理员开通");
        }
        apiDocument.setProjectId(oldVo.getProjectId());
        apiDocument.setStarLevel(oldVo.getStarLevel());
        apiDocument.setAddTime(oldVo.getAddTime());
        int ret = apiDocumentMapper.updateById(apiDocument);
        return (ret > 0) ? ReturnT.SUCCESS : ReturnT.FAIL;
    }

    /**
     * 详情页，API
     *
     * @return
     */
    @RequestMapping("/detailPage")
    public String detailPage(HttpServletRequest request, Model model, int id) {
        ApiDocument apiDocument = apiDocumentMapper.selectById(id);
        if (apiDocument == null) {
            throw new RuntimeException("操作失败，接口ID非法");
        }
        model.addAttribute("document", apiDocument);
        model.addAttribute("requestHeadersList", (StringTool.isNotBlank(apiDocument.getRequestHeaders())) ? JacksonUtil.readValue(apiDocument.getRequestHeaders(), List.class) : null);
        model.addAttribute("queryParamList", (StringTool.isNotBlank(apiDocument.getQueryParams())) ? JacksonUtil.readValue(apiDocument.getQueryParams(), List.class) : null);
        model.addAttribute("responseParamList", (StringTool.isNotBlank(apiDocument.getResponseParams())) ? JacksonUtil.readValue(apiDocument.getResponseParams(), List.class) : null);
        int projectId = apiDocument.getProjectId();
        ApiProject project = apiProjectMapper.selectById(projectId);
        model.addAttribute("projectId", projectId);
        model.addAttribute("project", project);
        ApiGroup apiGroupQuery = new ApiGroup();
        apiGroupQuery.setProjectId(projectId);
        List<ApiGroup> groupList = apiGroupMapper.selectList(Wrappers.query(apiGroupQuery));
        model.addAttribute("groupList", groupList);
        ApiMock apiMockQuery = new ApiMock();
        apiMockQuery.setDocumentId(id);
        List<ApiMock> mockList = apiMockMapper.selectList(Wrappers.query(apiMockQuery));
        model.addAttribute("mockList", mockList);
        ApiTestHistory apiTestHistoryQuery = new ApiTestHistory();
        apiTestHistoryQuery.setDocumentId(id);
        List<ApiTestHistory> testHistoryList = apiTestHistoryMapper.selectList(Wrappers.query(apiTestHistoryQuery));
        model.addAttribute("testHistoryList", testHistoryList);
        model.addAttribute("RequestMethodEnum", RequestConfig.RequestMethodEnum.values());
        model.addAttribute("requestHeadersEnum", RequestConfig.requestHeadersEnum);
        model.addAttribute("QueryParamTypeEnum", RequestConfig.QueryParamTypeEnum.values());
        model.addAttribute("ResponseContentType", RequestConfig.ResponseContentType.values());
        ApiDatatype responseDatatypeRet = apiDataTypeService.loadDataType(apiDocument.getResponseDatatypeId());
        model.addAttribute("responseDatatype", responseDatatypeRet);
        model.addAttribute("hasBizPermission", hasBizPermission(request, project.getBizId()));
        return "document/document.detail";
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
