package com.haizhi.empower.rest;

import com.haizhi.empower.base.BaseController;
import com.haizhi.empower.base.resp.BaseResponse;
import com.haizhi.empower.entity.bo.JudgeTaskCoordinationBo;
import com.haizhi.empower.entity.po.JudgeTaskCoordination;
import com.haizhi.empower.entity.vo.JudgeTaskCoordinationVo;
import com.haizhi.empower.service.JudgeTaskCoordinationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/judgeTaskCoordination/")
@Api(tags = "分析研判协同")
public class JudgeTaskCoordinationController extends BaseController<JudgeTaskCoordinationService, JudgeTaskCoordination> {

    @PostMapping("add")
    @ApiOperation(value = "新增")
    public BaseResponse add(@RequestBody JudgeTaskCoordinationBo bo) {
        return baseService.add(bo);
    }

    @GetMapping("delete")
    @ApiOperation(value = "删除")
    public BaseResponse delete(@RequestParam(value = "id") int id) {
        return baseService.delete(id);
    }

    @GetMapping("list")
    @ApiOperation(value = "查询")
    @ApiResponses({@ApiResponse(code = 0, message = "OK", response = JudgeTaskCoordinationVo.class)})
    public BaseResponse list(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                             @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                             @RequestParam(value = "taskId") Integer taskId) {
        return baseService.list(pageNum, pageSize, taskId);
    }
}