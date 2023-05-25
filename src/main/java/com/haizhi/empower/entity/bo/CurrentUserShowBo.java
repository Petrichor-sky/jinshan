package com.haizhi.empower.entity.bo;

import com.haizhi.empower.entity.vo.PermissionVo;
import com.haizhi.empower.entity.vo.RoleVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author CristianWindy
 * @Description:
 * @date 2022/4/1
 * @Copyright (c) 2009-2022 All rights reserved.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CurrentUserShowBo {
    private String userPicture;
    private String name;
    private String sex;
    private String policeId;
    private String idCard;
    private String userName;
    private String userCode;
    private List<OfficeVo> officeVos;
    private List<RoleVo> roleVos;
    private List<PermissionVo.PermissionTreeVo> permissionTreeVos;
    private String status;
    private String position;
    private String preAddress;


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OfficeVo {
        private String orgCode;
        private String level;
        private String officeName;
        private String officeCode;
        private boolean isDefault;
    }


}
