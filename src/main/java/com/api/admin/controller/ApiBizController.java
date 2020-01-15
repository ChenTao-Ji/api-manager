package com.api.admin.controller;

import com.api.admin.config.PermessionLimit;
import com.api.admin.dao.ApiBizMapper;
import com.api.admin.dao.ApiDatatypeMapper;
import com.api.admin.dao.ApiProjectMapper;
import com.api.admin.model.ApiBiz;
import com.api.admin.model.ReturnT;
import com.api.admin.util.StringTool;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chentao.ji
 */
@Controller
@RequestMapping("/biz")
public class ApiBizController {

    @Autowired
    private ApiBizMapper apiBizMapper;

    @Autowired
    private ApiProjectMapper apiProjectMapper;

    @Autowired
    private ApiDatatypeMapper apiDatatypeMapper;

    @RequestMapping
    @PermessionLimit(superUser = true)
    public String index(Model model) {
        return "biz/biz.list";
    }

    @RequestMapping("/pageList")
    @ResponseBody
    @PermessionLimit(superUser = true)
    public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "0") int start,
                                        @RequestParam(required = false, defaultValue = "10") int length,
                                        String bizName) {
        List<ApiBiz> list = apiBizMapper.pageList(start, length, bizName);
        int listCount = apiBizMapper.pageListCount(start, length, bizName);
        Map<String, Object> maps = new HashMap();
        maps.put("recordsTotal", listCount);
        maps.put("recordsFiltered", listCount);
        maps.put("data", list);
        return maps;
    }

    @RequestMapping("/add")
    @ResponseBody
    @PermessionLimit(superUser = true)
    public ReturnT<String> add(ApiBiz apiBiz) {
        if (StringTool.isBlank(apiBiz.getBizName())) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "业务线名称不可为空");
        }
        int ret = apiBizMapper.insert(apiBiz);
        return ret > 0 ? ReturnT.SUCCESS : ReturnT.FAIL;
    }

    @RequestMapping("/update")
    @ResponseBody
    @PermessionLimit(superUser = true)
    public ReturnT<String> update(ApiBiz apiBiz) {
        if (StringTool.isBlank(apiBiz.getBizName())) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "业务线名称不可为空");
        }
        int ret = apiBizMapper.updateById(apiBiz);
        return ret > 0 ? ReturnT.SUCCESS : ReturnT.FAIL;
    }

    @RequestMapping("/delete")
    @ResponseBody
    @PermessionLimit(superUser = true)
    public ReturnT<String> delete(int id) {
        int count = apiProjectMapper.pageListCount(0, 10, null, id);
        if (count > 0) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "拒绝删除，业务线下存在项目");
        }
        int dtCount = apiDatatypeMapper.pageListCount(0, 10, id, null);
        if (dtCount > 0) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "拒绝删除，业务线下数据类型");
        }
        List<ApiBiz> bizList = apiBizMapper.selectList(Wrappers.query());
        if (bizList.size() <= 1) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "拒绝删除，需要至少预留一个业务线");
        }
        int ret = apiBizMapper.deleteById(id);
        return ret > 0 ? ReturnT.SUCCESS : ReturnT.FAIL;
    }
}
