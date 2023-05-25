package com.haizhi.empower.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author by fuhanchao
 * @date 2023/4/5.
 */
@Data
public class JudgeTaskReportVo {

    private String createName;

    private String createUnit;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    private String taskName;

    private String taskDescription;

    private String taskCollaborators;

    private List<Collaboration> taskCollaboration;

    private String taskConclusion;

    @Data
    public static class Collaboration{
        private String collaborator;

        private String collaboratorUnit;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        private Date collaboratorTime;

        private String content;

        private List<Collaboration> taskCollaboration;
    }

}
