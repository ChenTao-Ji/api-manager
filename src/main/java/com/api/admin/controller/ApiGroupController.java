package com.api.admin.controller;

import com.api.admin.dao.ApiDocumentMapper;
import com.api.admin.dao.ApiGroupMapper;
import com.api.admin.dao.ApiProjectMapper;
import com.api.admin.model.*;
import com.api.admin.service.impl.LoginServiceImpl;
import com.api.admin.util.ArrayTool;
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
@RequestMapping("/group")
public class ApiGroupController {

    @Autowired
    private ApiProjectMapper apiProjectMapper;

    @Autowired
    private ApiGroupMapper apiGroupMapper;

    @Autowired
    private ApiDocumentMapper apiDocumentMapper;

    @RequestMapping
    public String index(HttpServletRequest request,
                        Model model,
                        int projectId,
                        @RequestParam(required = false, defaultValue = "-1") int groupId) {
        ApiProject ApiProject = apiProjectMapper.selectById(projectId);
        if (ApiProject == null) {
            throw new RuntimeException("系统异常，项目ID非法");
        }
        model.addAttribute("projectId", projectId);
        model.addAttribute("project", ApiProject);
        ApiGroup apiGroupQuery = new ApiGroup();
        apiGroupQuery.setProjectId(projectId);
        List<ApiGroup> groupList = apiGroupMapper.selectList(Wrappers.query(apiGroupQuery));
        model.addAttribute("groupList", groupList);
        ApiGroup groupInfo = null;
        if (groupList != null && groupList.size() > 0) {
            for (ApiGroup groupItem : groupList) {
                if (groupId == groupItem.getId()) {
                    groupInfo = groupItem;
                }
            }
        }
        if (groupId != 0 && groupInfo == null) {
            groupId = -1;
        }
        model.addAttribute("groupId", groupId);
        model.addAttribute("groupInfo", groupInfo);
        List<ApiDocument> documentList = apiDocumentMapper.loadAll(projectId, groupId);
        model.addAttribute("documentList", documentList);
        model.addAttribute("hasBizPermission", hasBizPermission(request, ApiProject.getBizId()));
        return "group/group.list";
    }

    @RequestMapping("/add")
    @ResponseBody
    public ReturnT<String> add(HttpServletRequest request, ApiGroup apiGroup) {
        if (StringTool.isBlank(apiGroup.getName())) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "请输入“分组名称”");
        }
        ApiProject ApiProject = apiProjectMapper.selectById(apiGroup.getProjectId());
        if (!hasBizPermission(request, ApiProject.getBizId())) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "您没有相关业务线的权限,请联系管理员开通");
        }
        int ret = apiGroupMapper.insert(apiGroup);
        return (ret > 0) ? ReturnT.SUCCESS : ReturnT.FAIL;
    }

    @RequestMapping("/update")
    @ResponseBody
    public ReturnT<String> update(HttpServletRequest request, ApiGroup apiGroup) {
        ApiGroup existGroup = apiGroupMapper.selectById(apiGroup.getId());
        if (existGroup == null) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "更新失败，分组ID非法");
        }
        ApiProject ApiProject = apiProjectMapper.selectById(existGroup.getProjectId());
        if (!hasBizPermission(request, ApiProject.getBizId())) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "您没有相关业务线的权限,请联系管理员开通");
        }
        if (StringTool.isBlank(apiGroup.getName())) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "请输入“分组名称”");
        }
        int ret = apiGroupMapper.updateById(apiGroup);
        return (ret > 0) ? ReturnT.SUCCESS : ReturnT.FAIL;
    }

    @RequestMapping("/delete")
    @ResponseBody
    public ReturnT<String> delete(HttpServletRequest request, int id) {
        ApiGroup existGroup = apiGroupMapper.selectById(id);
        if (existGroup == null) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "更新失败，分组ID非法");
        }
        ApiProject ApiProject = apiProjectMapper.selectById(existGroup.getProjectId());
        if (!hasBizPermission(request, ApiProject.getBizId())) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "您没有相关业务线的权限,请联系管理员开通");
        }
        ApiDocument apiDocumentQuery = new ApiDocument();
        apiDocumentQuery.setGroupId(id);
        List<ApiDocument> documentList = apiDocumentMapper.selectList(Wrappers.query(apiDocumentQuery));
        if (documentList != null && documentList.size() > 0) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "拒绝删除，分组下存在接口，不允许强制删除");
        }
        int ret = apiGroupMapper.deleteById(id);
        return (ret > 0) ? ReturnT.SUCCESS : ReturnT.FAIL;
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
