package com.api.admin.util;

import com.api.admin.enums.RequestConfig;
import com.api.admin.model.ApiDatatype;
import com.api.admin.model.ApiDatatypeFileds;

import java.util.*;

/**
 * @author chentao.ji
 */
public class ApiDataTypeToCode {

    /**
     * 根据DataType生成代码
     */
    public static String parseDataTypeToCode(ApiDatatype apiDataTypeDTO) {
        StringBuffer sb = new StringBuffer();
        sb.append("\r\n");
        Set<String> importSet = new HashSet<String>();
        if (apiDataTypeDTO.getFieldList() != null && apiDataTypeDTO.getFieldList().size() > 0) {
            for (ApiDatatypeFileds field : apiDataTypeDTO.getFieldList()) {
                String fieldTypeImportItem = field.getFieldDatatype().getName();
                if (fieldTypeImportItem != null && fieldTypeImportItem.equalsIgnoreCase("date")) {
                    String importItem = "import java.util.Date;";
                    importSet.add(importItem);
                    sb.append(importItem + "\r\n");
                }
                if (field.getFieldType() == 1) {
                    String importItem = "import java.util.List;";
                    importSet.add(importItem);
                    sb.append(importItem + "\r\n");
                }
            }
            sb.append("\r\n");
        }
        // 类注释
        sb.append("/**\r\n");
        sb.append("*\t" + apiDataTypeDTO.getAbout() + "\r\n");
        sb.append("*\r\n");
        sb.append("*\tCreated by API on " + DateTool.formatDate(new Date()) + ".\r\n");
        sb.append("*/ \r\n");
        // 实体部分
        sb.append("public class " + upFirst(apiDataTypeDTO.getName()) + "Response {");
        sb.append("\r\n\r\n");
        // field
        if (apiDataTypeDTO.getFieldList() != null && apiDataTypeDTO.getFieldList().size() > 0) {
            for (ApiDatatypeFileds field : apiDataTypeDTO.getFieldList()) {
                String fieldTypeItem = matchJavaType(field.getFieldDatatype().getName());
                String fieldNameItem = field.getFieldName();
                if (field.getFieldType() == 1) {
                    fieldTypeItem = "List<" + fieldTypeItem + ">";
                }
                sb.append("\tprivate " + fieldTypeItem + " " + fieldNameItem + ";\r\n");
            }
            sb.append("\r\n");
        }
        // get & set
        if (apiDataTypeDTO.getFieldList() != null && apiDataTypeDTO.getFieldList().size() > 0) {
            for (ApiDatatypeFileds field : apiDataTypeDTO.getFieldList()) {
                String fieldTypeItem = matchJavaType(field.getFieldDatatype().getName());
                String fieldNameItem = field.getFieldName();
                String fieldNameItemUpFirst = upFirst(field.getFieldName());
                if (field.getFieldType() == 1) {
                    fieldTypeItem = "List<" + fieldTypeItem + ">";
                }
                sb.append("\tpublic void set" + fieldNameItemUpFirst + "(" + fieldTypeItem + " " + fieldNameItem + "){\r\n");
                sb.append("\t\tthis." + fieldNameItem + "=" + fieldNameItem + ";\r\n");
                sb.append("\t}\r\n");
                sb.append("\tpublic " + fieldTypeItem + " get" + fieldNameItemUpFirst + "(){\r\n");
                sb.append("\t\treturn this." + fieldNameItem + ";\r\n");
                sb.append("\t}\r\n");
            }
            sb.append("\r\n");
        }
        sb.append("}\r\n");
        return sb.toString();
    }


    /**
     * 首字母大写
     */
    private static String upFirst(String str) {
        char[] ch = str.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z') {
            ch[0] = (char) (ch[0] - 32);
        }
        return new String(ch);
    }

    /**
     * 匹配数据类型
     */
    private static String matchJavaType(String paramDataType) {
        if (paramDataType.equalsIgnoreCase(RequestConfig.QueryParamTypeEnum.STRING.getTitle())) {
            return "String";
        } else if (paramDataType.equalsIgnoreCase(RequestConfig.QueryParamTypeEnum.BOOLEAN.getTitle())) {
            return "boolean";
        } else if (paramDataType.equalsIgnoreCase(RequestConfig.QueryParamTypeEnum.SHORT.getTitle())) {
            return "short";
        } else if (paramDataType.equalsIgnoreCase(RequestConfig.QueryParamTypeEnum.INT.getTitle())) {
            return "int";
        } else if (paramDataType.equalsIgnoreCase(RequestConfig.QueryParamTypeEnum.LONG.getTitle())) {
            return "long";
        } else if (paramDataType.equalsIgnoreCase(RequestConfig.QueryParamTypeEnum.FLOAT.getTitle())) {
            return "float";
        } else if (paramDataType.equalsIgnoreCase(RequestConfig.QueryParamTypeEnum.DOUBLE.getTitle())) {
            return "double";
        } else if (paramDataType.equalsIgnoreCase(RequestConfig.QueryParamTypeEnum.DATE.getTitle()) || paramDataType.equalsIgnoreCase(RequestConfig.QueryParamTypeEnum.DATETIME.getTitle())) {
            return "Date";
        } else if (paramDataType.equalsIgnoreCase(RequestConfig.QueryParamTypeEnum.BYTE.getTitle())) {
            return "byte";
        }
        return paramDataType;
    }
}

