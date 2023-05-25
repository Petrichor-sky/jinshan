package com.haizhi.empower.entity.bo;

import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author CristianWindy
 * @Description:
 * @date 2022/4/26
 * @Copyright (c) 2009-2022 All rights reserved.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DicTypeBo {
    private Integer id;
    @NotEmpty(message = "字典名称不能为空")
    private String name;
    @NotEmpty(message = "字典Code不能为空")
    private String code;
    private String description;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DicItemBo {
        @NotEmpty(message = "字典名称不能为空")
        private String name;
        @NotEmpty(message = "字典Code不能为空")
        private String code;
        @NotEmpty(message = "字典父类Code不能为空")
        private String parentCode;
        private String description;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DicTypeModifiedBo {
        private Integer id;
        private String name;
        private String description;
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DicItemModifiedBo {
        private Long id;
        private String name;
        private String description;
        private Integer status;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BatchDeleteBo {
        private String parentCode;
        private List<Integer> ids;
    }
}
