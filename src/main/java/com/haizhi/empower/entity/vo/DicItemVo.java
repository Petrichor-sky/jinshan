package com.haizhi.empower.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author CristianWindy
 * @Description:
 * @date 2022/4/20
 * @Copyright (c) 2009-2022 All rights reserved.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DicItemVo {
    public String itemName;
    public Long itemId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date createTime;
    public Integer status;
    public String code;
    public String description;
}
