package com.haizhi.empower.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author by fuhanchao
 * @date 2023/1/30.
 */
@Data
public class DetailDataVo {

    private String name;

    private List<Details> details;

    @Data
    public static class Details {
        public String name;

        public String policeCode;

        public Boolean containType2;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        public Date createTime;

        public List<SubjectDataVo> subjectDataVos;
    }

    @Data
    public static class SubjectDataVo {

        private String name;

        private String result;

        private String type;

        private String note;
    }
}
