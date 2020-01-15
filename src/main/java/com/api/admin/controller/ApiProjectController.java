package com.api.admin.controller;

import com.api.admin.dao.ApiBizMapper;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chentao.ji
 */
@Controller
@RequestMapping("/project")
public class ApiProjectController {

    @Autowired
    private ApiProjectMapper apiProjectMapper;

    @Autowired
    private ApiGroupMapper apiGroupMapper;

    @Autowired
    private ApiBizMapper apiBizMapper;

    @Autowired
    private ApiDocumentMapper apiDocumentMapper;

    @RequestMapping
    public String index(Model model, @RequestParam(required = false, defaultValue = "0") int bizId) {
        model.addAttribute("bizId", bizId);
        model.addAttribute("bizList", apiBizMapper.selectList(Wrappers.query()));
        return "project/project.list";
    }

    @RequestMapping("/pageList")
    @ResponseBody
    public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "0") int start,
                                        @RequestParam(required = false, defaultValue = "10") int length,
                                        String name, int bizId) {
        List<ApiProject> list = apiProjectMapper.pageList(start, length, name, bizId);
        int list_count = apiProjectMapper.pageListCount(start, length, name, bizId);
        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("recordsTotal", list_count);
        maps.put("recordsFiltered", list_count);
        maps.put("data", list);
        return maps;
    }

    @RequestMapping("/add")
    @ResponseBody
    public ReturnT<String> add(HttpServletRequest request, ApiProject apiProject) {
        if (StringTool.isBlank(apiProject.getName())) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "请输入项目名称");
        }
        if (StringTool.isBlank(apiProject.getBaseUrlProduct())) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "请输入根地址(线上)");
        }
        if (!hasBizPermission(request, apiProject.getBizId())) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "您没有相关业务线的权限,请联系管理员开通");
        }
        int ret = apiProjectMapper.insert(apiProject);
        return (ret > 0) ? ReturnT.SUCCESS : ReturnT.FAIL;
    }

    @RequestMapping("/update")
    @ResponseBody
    public ReturnT<String> update(HttpServletRequest request, ApiProject apiProject) {
        ApiProject existProkect = apiProjectMapper.selectById(apiProject.getId());
        if (existProkect == null) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "更新失败，项目ID非法");
        }
        if (StringTool.isBlank(apiProject.getName())) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "请输入项目名称");
        }
        if (StringTool.isBlank(apiProject.getBaseUrlProduct())) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "请输入根地址(线上)");
        }
        if (!hasBizPermission(request, apiProject.getBizId())) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "您没有相关业务线的权限,请联系管理员开通");
        }
        int ret = apiProjectMapper.updateById(apiProject);
        return (ret > 0) ? ReturnT.SUCCESS : ReturnT.FAIL;
    }

    @RequestMapping("/delete")
    @ResponseBody
    public ReturnT<String> delete(HttpServletRequest request, int id) {
        ApiProject existProkect = apiProjectMapper.selectById(id);
        if (existProkect == null) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "项目ID非法");
        }
        if (!hasBizPermission(request, existProkect.getBizId())) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "您没有相关业务线的权限,请联系管理员开通");
        }
        ApiGroup apiGroupQuery = new ApiGroup();
        apiGroupQuery.setProjectId(id);
        List<ApiGroup> groupList = apiGroupMapper.selectList(Wrappers.query(apiGroupQuery));
        if (groupList != null && groupList.size() > 0) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "该项目下存在分组信息，拒绝删除");
        }
        List<ApiDocument> documents = apiDocumentMapper.loadAll(id, -1);
        if (documents != null && documents.size() > 0) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "该项目下存在接口信息，拒绝删除");
        }
        int ret = apiProjectMapper.deleteById(id);
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
