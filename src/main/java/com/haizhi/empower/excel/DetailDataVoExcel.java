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
public class DetailDataVoExcel implements Serializable {

    @ExcelProperty(value = "投票序号", index = 0)
    private Integer number;//投票序号

    @ExcelProperty(value = "投票标题", index = 1)
    private String title;//标题

    @ExcelProperty(value = "题目名称", index = 2)
    private String subjectName;//标题

    @ExcelProperty(value = "投票时间", index = 3)
    private String time;//投票时间

    @ExcelProperty(value = "投票人警号", index = 4)
    private String copCode;//警号

    @ExcelProperty(value = "投票人姓名", index = 5)
    private String name;//姓名

    @ExcelProperty(value = "选项", index = 6)
    private String option;//选项

    @ExcelProperty(value = "选项类型", index = 7)
    private String type;//选项类型

    @ExcelProperty(value = "小计", index = 8)
    private String result;//小计

    @ExcelProperty(value = "备注", index = 9)
    private String note;//备注
}
