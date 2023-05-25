package com.haizhi.empower.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 用心中心角色详情
 *
 * @author ：lvchengfei
 * @date ：2023/4/5 14:13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UcRoleDetailDto {

    private String role_id;

    private String role_name;

    private String role_desc;

    private Integer role_type;

    private List<Object> product_list;
}
