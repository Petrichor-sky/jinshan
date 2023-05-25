package com.haizhi.empower.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class UserVo {
    private String userName;
    private String userCode;
    private String policeId;
    private String officeCode;
    private String officeName;
}
