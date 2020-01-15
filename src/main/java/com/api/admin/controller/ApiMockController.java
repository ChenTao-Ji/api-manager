package com.api.admin.controller;

import com.api.admin.config.PermessionLimit;
import com.api.admin.dao.ApiDocumentMapper;
import com.api.admin.dao.ApiMockMapper;
import com.api.admin.enums.RequestConfig;
import com.api.admin.model.ApiDocument;
import com.api.admin.model.ApiMock;
import com.api.admin.model.ReturnT;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.UUID;

/**
 * @author chentao.ji
 */
@Controller
@RequestMapping("/mock")
public class ApiMockController {

    private static Logger logger = LoggerFactory.getLogger(ApiMockController.class);

    @Autowired
    private ApiMockMapper apiMockMapper;

    @Autowired
    private ApiDocumentMapper apiDocumentMapper;

    /**
     * 保存Mock数据
     */
    @RequestMapping("/add")
    @ResponseBody
    public ReturnT<String> add(ApiMock apiMock) {

        ApiDocument document = apiDocumentMapper.selectById(apiMock.getDocumentId());
        if (document == null) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "保存Mock数据失败，接口ID非法");
        }
        String uuid = UUID.randomUUID().toString();
        apiMock.setUuid(uuid);
        int ret = apiMockMapper.insert(apiMock);
        return (ret > 0) ? ReturnT.SUCCESS : ReturnT.FAIL;
    }

    @RequestMapping("/delete")
    @ResponseBody
    public ReturnT<String> delete(int id) {
        int ret = apiMockMapper.deleteById(id);
        return (ret > 0) ? ReturnT.SUCCESS : ReturnT.FAIL;
    }

    @RequestMapping("/update")
    @ResponseBody
    public ReturnT<String> update(ApiMock apiMock) {
        int ret = apiMockMapper.updateById(apiMock);
        return (ret > 0) ? ReturnT.SUCCESS : ReturnT.FAIL;
    }

    @RequestMapping("/run/{uuid}")
    @PermessionLimit(limit = false)
    public void run(@PathVariable("uuid") String uuid, HttpServletResponse response) {
        ApiMock apiMockQuery = new ApiMock();
        apiMockQuery.setUuid(uuid);
        ApiMock apiMock = apiMockMapper.selectOne(Wrappers.query(apiMockQuery));
        if (apiMock == null) {
            throw new RuntimeException("Mock数据ID非法");
        }
        RequestConfig.ResponseContentType contentType = RequestConfig.ResponseContentType.match(apiMock.getRespType());
        if (contentType == null) {
            throw new RuntimeException("Mock数据响应数据类型(MIME)非法");
        }
        try {
            response.setCharacterEncoding("UTF-8");
            response.setContentType(contentType.type);
            PrintWriter writer = response.getWriter();
            writer.write(apiMock.getRespExample());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
