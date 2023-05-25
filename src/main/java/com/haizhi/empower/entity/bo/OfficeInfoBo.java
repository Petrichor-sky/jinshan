package com.haizhi.empower.entity.bo;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author CristianWindy
 * @Description:
 * @date 2022/4/27
 * @Copyright (c) 2009-2022 All rights reserved.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OfficeInfoBo {
    private Integer id;
    @NotEmpty(message = "组织名称不能为空")
    private String officeName;
    @NotEmpty(message = "组织代码不能为空")
    private String officeCode;
    private Integer isTemp;
    private String parentCode;
    private Integer administrative;
    @NotNull(message = "组织类型不能为空")
    private Integer officeType;
    private List<Integer> suppression;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpdateBo {
        private Integer id;
        @NotEmpty(message = "组织名称不能为空")
        private String officeName;
        private Integer isTemp;
        private String parentCode;
        private Integer administrative;
        @NotNull(message = "组织类型不能为空")
        private Integer officeType;
        private List<Integer> suppression;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BatchUserBo {
        private String officeCode;
        private List<String> userCodes;
    }
}
