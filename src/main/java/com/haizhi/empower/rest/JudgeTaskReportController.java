package com.haizhi.empower.rest;

import com.haizhi.empower.service.JudgeTaskReportService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * @author by fuhanchao
 * @date 2023/4/5.
 */
@RestController
@RequestMapping("/judgeTaskReport")
public class JudgeTaskReportController {

    @Autowired
    JudgeTaskReportService judgeTaskReportService;

    @ApiOperation(value = "分析研判报告",notes = "分析研判报告")
    @PostMapping("report")
    public ResponseEntity<byte[]> report(@RequestParam(value = "file",required = false) MultipartFile file, @RequestParam("id") Integer id, HttpServletResponse response) throws Exception {

       return  judgeTaskReportService.report(file,id,response);
    }
}
