package com.haizhi.empower.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author CristianWindy
 * @Description:
 * @date 2022/4/14
 * @Copyright (c) 2009-2022 All rights reserved.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OfficeVo {
    private String officeCode;
    private String officeName;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OfficeTreeNode {
        private Long id;
        private String officeCode;
        private String officeName;
        private Integer level;
        private Integer isTemp;
        private List<OfficeTreeNode> subs;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OfficeUserTreeNode {
        private Long id;
        private String officeCode;
        private String officeName;
        private Integer level;
        private Integer isTemp;
        private List<OfficeUserTreeNode> children;
        private List<UserVo> userVos;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OfficeDetailVo {
        private Long id;
        private String officeName;
        private String officeCode;
        private String superiorOfficeCode;
        private String superiorOfficeName;
        private Integer isTemp;
        private Integer level;
        private ItemVo administrative;
        private Integer officeType;
        private List<ItemVo> suppression;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ItemVo {
        private Long itemId;
        private String itemName;
    }
}
