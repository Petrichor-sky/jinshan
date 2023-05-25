package com.haizhi.empower.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class DicTypeVo {
    private Integer id;
    private String name;
    private String code;
    private Integer sysDic;
    private String description;
}
