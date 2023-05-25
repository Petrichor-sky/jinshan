package com.haizhi.empower.enums;

import org.springframework.util.ObjectUtils;

/**
 * 菜单（权限）中的类型枚举
 *
 * @author chenb
 * @date 2020年01月06日20:24:42
 */
public enum PermissionResTypeEnum {
    /**
     * 目录(第1级)
     */
    CATALOGUE(0, "catalogue_", "目录(第1级)"),
    /**
     * 菜单(第2级)
     */
    MENU(1, "menu_", "菜单(第2级)"),
    ;

    private Integer type;
    private String pre;
    private String desc;

    PermissionResTypeEnum(Integer type, String pre, String desc) {
        this.type = type;
        this.pre = pre;
        this.desc = desc;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getPre() {
        return pre;
    }

    public void setPre(String pre) {
        this.pre = pre;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static String getPre(Integer resType) {
        if (ObjectUtils.isEmpty(resType)) {
            return CATALOGUE.getPre();
        }

        for (PermissionResTypeEnum enu : PermissionResTypeEnum.values()) {
            if (enu.getType().equals(resType)) {
                return enu.getPre();
            }
        }
        return CATALOGUE.getPre();
    }
}
