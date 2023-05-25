package com.haizhi.empower.enums;

/**
 * @author by fuhanchao
 * @date 2022/12/9.
 */
public enum OperationTypeEnum {
    UNKNOWN("unknown"),
    DELETE("delete"),
    SELECT("select"),
    UPDATE("update"),
    INSERT("insert");

    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    OperationTypeEnum(String s) {
        this.value = s;
    }
}
