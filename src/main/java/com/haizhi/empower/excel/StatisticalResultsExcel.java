package com.haizhi.empower.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Description
 * @Author zhang jia hao
 * @Date 2023/3/21
 */
@Data
public class StatisticalResultsExcel implements Serializable {

    @ExcelProperty(value = "投票序号", index = 0)
    private Integer number;//投票序号

    @ExcelProperty(value = "投票标题", index = 1)
    private String title;//标题

    @ExcelProperty(value = "题目名称", index = 2)
    private String subjectName;//题目名称

    @ExcelProperty(value = "题目类型", index = 3)
    private String type;//题目类型

    @ExcelProperty(value = "选项", index = 4)
    private String option;//选项

    @ExcelProperty(value = "小计", index = 5)
    private String result;//小计

    @ExcelProperty(value = "比例/平均分", index = 6)
    private String percent;

    @ExcelProperty(value = "有效填写人数", index = 7)
    private Integer count;//有效填写人数
}
