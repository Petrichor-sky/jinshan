package com.haizhi.empower.entity.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author by fuhanchao
 * @date 2023/4/6.
 */
@Data
public class JudgeTaskReportDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String fileName;

    private byte[] byteDataArr = new byte[0];
}
