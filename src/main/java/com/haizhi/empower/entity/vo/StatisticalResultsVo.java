package com.haizhi.empower.entity.vo;

import lombok.Data;

import java.util.List;

/**
 * @author by fuhanchao
 * @date 2023/1/29.
 */
@Data
public class StatisticalResultsVo {

    private String name;


    private List<ResultVo> result;

    @Data
    public static class ResultVo {

        private String id;

        private String name;

        private String type;

        private Integer count;

        private List<SelectResultVo> selectResult;
    }

    @Data
    public static class SelectResultVo {

        private String id;

        private String name;

        private Integer count;

        private String percent;
    }
}


