package com.api.admin.controller;

import com.api.admin.dao.ApiDocumentMapper;
import com.api.admin.dao.ApiProjectMapper;
import com.api.admin.dao.ApiTestHistoryMapper;
import com.api.admin.enums.RequestConfig;
import com.api.admin.model.ApiDocument;
import com.api.admin.model.ApiProject;
import com.api.admin.model.ApiTestHistory;
import com.api.admin.model.ReturnT;
import com.api.admin.util.JacksonUtil;
import com.api.admin.util.StringTool;
import com.api.admin.util.ThrowableUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chentao.ji
 */
@Controller
@RequestMapping("/test")
public class ApiTestController {

    private static Logger logger = LoggerFactory.getLogger(ApiTestController.class);

    @Autowired
    private ApiDocumentMapper apiDocumentMapper;
    @Autowired
    private ApiTestHistoryMapper apiTestHistoryMapper;
    @Autowired
    private ApiProjectMapper apiProjectMapper;

    /**
     * @return
     */
    @RequestMapping
    public String index(Model model,
                        int documentId,
                        @RequestParam(required = false, defaultValue = "0") int testId) {


        // params
        ApiDocument document = document = apiDocumentMapper.selectById(documentId);
        if (document == null) {
            throw new RuntimeException("接口ID非法");
        }
        ApiProject project = apiProjectMapper.selectById(document.getProjectId());
        List<Map<String, String>> requestHeaders = null;
        List<Map<String, String>> queryParams = null;
        if (testId > 0) {
            ApiTestHistory testHistory = apiTestHistoryMapper.selectById(testId);
            if (testHistory == null) {
                throw new RuntimeException("测试用例ID非法");
            }
            model.addAttribute("testHistory", testHistory);
            requestHeaders = (StringTool.isNotBlank(testHistory.getRequestHeaders())) ? JacksonUtil.readValue(testHistory.getRequestHeaders(), List.class) : null;
            queryParams = (StringTool.isNotBlank(testHistory.getQueryParams())) ? JacksonUtil.readValue(testHistory.getQueryParams(), List.class) : null;
        } else {
            requestHeaders = (StringTool.isNotBlank(document.getRequestHeaders())) ? JacksonUtil.readValue(document.getRequestHeaders(), List.class) : null;
            queryParams = (StringTool.isNotBlank(document.getQueryParams())) ? JacksonUtil.readValue(document.getQueryParams(), List.class) : null;
        }
        model.addAttribute("document", document);
        model.addAttribute("project", project);
        model.addAttribute("requestHeaders", requestHeaders);
        model.addAttribute("queryParams", queryParams);
        model.addAttribute("documentId", documentId);
        model.addAttribute("testId", testId);
        model.addAttribute("RequestMethodEnum", RequestConfig.RequestMethodEnum.values());
        model.addAttribute("requestHeadersEnum", RequestConfig.requestHeadersEnum);
        model.addAttribute("QueryParamTypeEnum", RequestConfig.QueryParamTypeEnum.values());
        model.addAttribute("ResponseContentType", RequestConfig.ResponseContentType.values());
        return "test/test.index";
    }

    @RequestMapping("/add")
    @ResponseBody
    public ReturnT<Integer> add(ApiTestHistory apiTestHistory) {
        int ret = apiTestHistoryMapper.insert(apiTestHistory);
        return ret > 0 ? new ReturnT<Integer>(apiTestHistory.getId()) : new ReturnT<Integer>(ReturnT.FAIL_CODE, null);
    }

    @RequestMapping("/update")
    @ResponseBody
    public ReturnT<String> update(ApiTestHistory apiTestHistory) {
        int ret = apiTestHistoryMapper.updateById(apiTestHistory);
        return ret > 0 ? ReturnT.SUCCESS : ReturnT.FAIL;
    }

    @RequestMapping("/delete")
    @ResponseBody
    public ReturnT<String> delete(int id) {
        int ret = apiTestHistoryMapper.deleteById(id);
        return ret > 0 ? ReturnT.SUCCESS : ReturnT.FAIL;
    }

    /**
     * @return
     */
    @RequestMapping("/run")
    @ResponseBody
    public ReturnT<String> run(ApiTestHistory apiTestHistory, HttpServletRequest request, HttpServletResponse response) {
        RequestConfig.ResponseContentType contentType = RequestConfig.ResponseContentType.match(apiTestHistory.getRespType());
        if (contentType == null) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "响应数据类型(MIME)非法");
        }
        if (StringTool.isBlank(apiTestHistory.getRequestUrl())) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "请输入接口URL");
        }
        Map<String, String> requestHeaderMap = null;
        List<Map<String, String>> requestHeaders = (StringTool.isNotBlank(apiTestHistory.getRequestHeaders())) ? JacksonUtil.readValue(apiTestHistory.getRequestHeaders(), List.class) : null;
        if (requestHeaders != null && requestHeaders.size() > 0) {
            requestHeaderMap = new HashMap<String, String>();
            for (Map<String, String> item : requestHeaders) {
                requestHeaderMap.put(item.get("key"), item.get("value"));
            }
        }
        Map<String, String> queryParamMap = null;
        List<Map<String, String>> queryParams = (StringTool.isNotBlank(apiTestHistory.getQueryParams())) ? JacksonUtil.readValue(apiTestHistory.getQueryParams(), List.class) : null;
        if (queryParams != null && queryParams.size() > 0) {
            queryParamMap = new HashMap<String, String>();
            for (Map<String, String> item : queryParams) {
                queryParamMap.put(item.get("key"), item.get("value"));
            }
        }
        HttpRequestBase remoteRequest = null;
        if (RequestConfig.RequestMethodEnum.POST.name().equals(apiTestHistory.getRequestMethod())) {
            HttpPost httpPost = new HttpPost(apiTestHistory.getRequestUrl());
            if (queryParamMap != null && !queryParamMap.isEmpty()) {
                List<NameValuePair> formParams = new ArrayList<NameValuePair>();
                for (Map.Entry<String, String> entry : queryParamMap.entrySet()) {
                    formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(formParams, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    logger.error(e.getMessage(), e);
                }
            }
            remoteRequest = httpPost;
        } else if (RequestConfig.RequestMethodEnum.GET.name().equals(apiTestHistory.getRequestMethod())) {
            remoteRequest = new HttpGet(markGetUrl(apiTestHistory.getRequestUrl(), queryParamMap));
        } else if (RequestConfig.RequestMethodEnum.PUT.name().equals(apiTestHistory.getRequestMethod())) {
            remoteRequest = new HttpPut(markGetUrl(apiTestHistory.getRequestUrl(), queryParamMap));
        } else if (RequestConfig.RequestMethodEnum.DELETE.name().equals(apiTestHistory.getRequestMethod())) {
            remoteRequest = new HttpDelete(markGetUrl(apiTestHistory.getRequestUrl(), queryParamMap));
        } else if (RequestConfig.RequestMethodEnum.HEAD.name().equals(apiTestHistory.getRequestMethod())) {
            remoteRequest = new HttpHead(markGetUrl(apiTestHistory.getRequestUrl(), queryParamMap));
        } else if (RequestConfig.RequestMethodEnum.OPTIONS.name().equals(apiTestHistory.getRequestMethod())) {
            remoteRequest = new HttpOptions(markGetUrl(apiTestHistory.getRequestUrl(), queryParamMap));
        } else if (RequestConfig.RequestMethodEnum.PATCH.name().equals(apiTestHistory.getRequestMethod())) {
            remoteRequest = new HttpPatch(markGetUrl(apiTestHistory.getRequestUrl(), queryParamMap));
        } else {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "请求方法非法");
        }
        if (requestHeaderMap != null && !requestHeaderMap.isEmpty()) {
            for (Map.Entry<String, String> entry : requestHeaderMap.entrySet()) {
                remoteRequest.setHeader(entry.getKey(), entry.getValue());
            }
        }
        String responseContent = remoteCall(remoteRequest);
        return new ReturnT<String>(responseContent);
    }

    private String markGetUrl(String url, Map<String, String> queryParamMap) {
        String finalUrl = url;
        if (queryParamMap != null && queryParamMap.size() > 0) {
            finalUrl = url + "?";
            for (Map.Entry<String, String> entry : queryParamMap.entrySet()) {
                finalUrl += entry.getKey() + "=" + entry.getValue() + "&";
            }
            finalUrl = finalUrl.substring(0, finalUrl.length() - 1);
        }
        return finalUrl;
    }

    private String remoteCall(HttpRequestBase remoteRequest) {
        String responseContent = null;
        CloseableHttpClient httpClient = null;
        try {
            org.apache.http.client.config.RequestConfig requestConfig = org.apache.http.client.config.RequestConfig.custom().setSocketTimeout(5000).setConnectTimeout(5000).build();
            remoteRequest.setConfig(requestConfig);
            httpClient = HttpClients.custom().disableAutomaticRetries().build();
            HttpResponse response = httpClient.execute(remoteRequest);
            HttpEntity entity = response.getEntity();
            if (null != entity) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    responseContent = EntityUtils.toString(entity, "UTF-8");
                } else {
                    responseContent = "请求状态异常：" + response.getStatusLine().getStatusCode();
                    if (statusCode == 302 && response.getFirstHeader("Location") != null) {
                        responseContent += "；Redirect地址：" + response.getFirstHeader("Location").getValue();
                    }
                }
                EntityUtils.consume(entity);
            }
            logger.info("http statusCode error, statusCode:" + response.getStatusLine().getStatusCode());
        } catch (Exception e) {
            responseContent = "请求异常：" + ThrowableUtil.toString(e);
        } finally {
            if (remoteRequest != null) {
                remoteRequest.releaseConnection();
            }
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return responseContent;
    }
}
