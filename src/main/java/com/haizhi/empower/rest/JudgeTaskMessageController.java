package com.haizhi.empower.rest;

import com.haizhi.empower.base.resp.BaseResponse;
import com.haizhi.empower.entity.vo.JudgeTaskMessageVo;
import com.haizhi.empower.service.JudgeTaskMessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author by fuhanchao
 * @date 2023/4/5.
 */
@RestController
@Api(tags = "分析研判消息")
@RequestMapping("/judgeTaskMessage")
public class JudgeTaskMessageController {
    @Autowired
    JudgeTaskMessageService judgeTaskMessageService;



    @GetMapping("/list")
    @ApiOperation(value = "消息通知",response = JudgeTaskMessageVo.class)
    public BaseResponse getJudgeTaskMessage(){

        return judgeTaskMessageService.getJudgeTaskMessage();
    }
}
