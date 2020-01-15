package com.api.admin.enums;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * 请求配置
 *
 * @author chentao.ji
 */
public class RequestConfig {

    /**
     * Request Method
     */
    public enum RequestMethodEnum {
        POST, GET, PUT, DELETE, HEAD, OPTIONS, PATCH;
    }

    /**
     * Request Headers
     */
    public static List<String> requestHeadersEnum = new LinkedList<String>();

    static {
        requestHeadersEnum.add("Accept");
        requestHeadersEnum.add("Accept-Charset");
        requestHeadersEnum.add("Accept-Encoding");
        requestHeadersEnum.add("Accept-Language");
        requestHeadersEnum.add("Accept-Ranges");
        requestHeadersEnum.add("Authorization");
        requestHeadersEnum.add("Cache-Control");
        requestHeadersEnum.add("Connection");
        requestHeadersEnum.add("Cookie");
        requestHeadersEnum.add("Content-Length");
        requestHeadersEnum.add("Content-Type");
        requestHeadersEnum.add("Date");
        requestHeadersEnum.add("Expect");
        requestHeadersEnum.add("From");
        requestHeadersEnum.add("Host");
        requestHeadersEnum.add("If-Match");
        requestHeadersEnum.add("If-Modified-Since");
        requestHeadersEnum.add("If-None-Match");
        requestHeadersEnum.add("If-Range");
        requestHeadersEnum.add("If-Unmodified-Since");
        requestHeadersEnum.add("Max-Forwards");
        requestHeadersEnum.add("Pragma");
        requestHeadersEnum.add("Proxy-Authorization");
        requestHeadersEnum.add("Range");
        requestHeadersEnum.add("Referer");
        requestHeadersEnum.add("TE");
        requestHeadersEnum.add("Upgrade");
        requestHeadersEnum.add("User-Agent");
        requestHeadersEnum.add("Via");
        requestHeadersEnum.add("Warning");
    }

    /**
     * Query Param, Type
     */
    public enum QueryParamTypeEnum {

        STRING("string"),
        BOOLEAN("boolean"),
        SHORT("short"),
        INT("int"),
        LONG("long"),
        FLOAT("float"),
        DOUBLE("double"),
        DATE("date"),
        DATETIME("datetime"),
        JSON("json"),
        BYTE("byte");

        private String title;

        QueryParamTypeEnum(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

    /**
     * Reponse Headers, Content-Type
     */
    public enum ResponseContentType {
        JSON("application/json;charset=UTF-8"),
        XML("text/xml"),
        HTML("text/html;"),
        TEXT("text/plain"),
        JSONP("application/javascript");

        public final String type;

        ResponseContentType(String type) {
            this.type = type;
        }

        public static ResponseContentType match(String name) {
            return Arrays.asList(ResponseContentType.values()).stream().filter(item -> item.name().equals(name)).findFirst().orElseThrow(() -> new RuntimeException("未查询到"));
        }
    }


}
