package com.api.admin.enums;

import java.util.Arrays;

/**
 * 字段类型
 *
 * @author chentao.ji
 */
public enum FieldTypeEnum {

    DEFAULT(0, "默认"),
    ARRAY(1, "数组");

    private int value;
    private String title;

    FieldTypeEnum(int value, String title) {
        this.value = value;
        this.title = title;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public static FieldTypeEnum match(int value) {
        return Arrays.asList(FieldTypeEnum.values()).stream().filter(x -> x.getValue() == value).findFirst().orElseThrow(() -> new RuntimeException("未查询到对应字段类型"));
    }
}
