package com.haizhi.empower.rest;

import com.haizhi.empower.base.BaseController;
import com.haizhi.empower.base.resp.BaseResponse;
import com.haizhi.empower.base.resp.ObjectRestResponse;
import com.haizhi.empower.base.resp.TableResultResponse;
import com.haizhi.empower.entity.bo.JudgeTaskBo;
import com.haizhi.empower.entity.dto.JudgeTaskManagePageDto;
import com.haizhi.empower.entity.po.JudgeTask;
import com.haizhi.empower.entity.dto.JudgeTaskDetailDto;
import com.haizhi.empower.entity.vo.*;
import com.haizhi.empower.service.ApiService;
import com.haizhi.empower.service.JudgeTaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/judgeTask/")
@Api(tags = "分析研判")
public class JudgeTaskController extends BaseController<JudgeTaskService, JudgeTask> {
    @Resource
    private ApiService apiService;

    @PostMapping("save")
    @ApiOperation(value = "研判任务保存")
    public BaseResponse save(@RequestBody JudgeTaskBo bo) {
        return baseService.save(bo);
    }

    @GetMapping("finish")
    @ApiOperation(value = "研判任务完结")
    public BaseResponse finish(@RequestParam("id") int id) {
        return baseService.finish(id);
    }

    /*@PostMapping("update")
    @ApiOperation(value = "研判任务更新")
    public BaseResponse update(@RequestBody JudgeTaskBo bo) {
        return baseService.update(bo);
    }*/

    @GetMapping("list")
    @ApiOperation(value = "查询")
    public BaseResponse list() {
        return baseService.list();
    }

    /**
     * 研判中心-首页待办任务列表
     * @return
     */
    @GetMapping("homePagePendList")
    @ApiOperation("首页待办任务列表")
    public ObjectRestResponse<JudgeTaskHomePagePendShowVo> selectHomePagePendList() {
        return baseService.selectHomePagePendList();
    }

    /**
     * 研判中心-首页消息通知
     * @return
     */
    @GetMapping("messageList")
    @ApiOperation("首页消息通知")
    public ObjectRestResponse<List<JudgeTaskHomePageMessageVo>> selectMessageList() {
        return baseService.selectMessageList();
    }

    /**
     * 研判中心-进行中的任务列表
     * @param pageNum
     * @param pageSize
     * @param taskName
     * @param officeName
     * @param createTime
     * @return
     */
    @GetMapping("progressJudgeTask")
    @ApiOperation("进行中的任务列表")
    public JudgeTaskManagePageVo<JudgeTaskManagePageDto> selectProgressJudgeTask(@RequestParam(name = "pageNum",defaultValue = "1") Integer pageNum,
                                                                                 @RequestParam(name = "pageSize",defaultValue = "20") Integer pageSize,
                                                                                 @RequestParam(required = false) String taskName,
                                                                                 @RequestParam(required = false) String officeName,
                                                                                 @RequestParam(required = false) String createTime) {
        return baseService.selectProgressJudgeTask(pageNum,pageSize,taskName,officeName,createTime);
    }

    /**
     * 研判中心-已完成的任务列表
     * @param pageNum
     * @param pageSize
     * @param taskName
     * @param officeName
     * @param createTime
     * @return
     */
    @GetMapping("finishJudgeTask")
    @ApiOperation("已完成的任务列表")
    public JudgeTaskManagePageVo<JudgeTaskManagePageDto> selectFinishJudgeTask(@RequestParam(name = "pageNum",defaultValue = "1") Integer pageNum,
                                                                               @RequestParam(name = "pageSize",defaultValue = "20") Integer pageSize,
                                                                               @RequestParam(required = false) String taskName,
                                                                               @RequestParam(required = false) String officeName,
                                                                               @RequestParam(required = false) String createTime) {
        return baseService.selectFinishJudgeTask(pageNum,pageSize,taskName,officeName,createTime);
    }

    /**
     * 研判中心-我创建的任务列表
     * @param pageNum
     * @param pageSize
     * @param taskName
     * @param officeName
     * @param createTime
     * @return
     */
    @GetMapping("myCreateJudgeTask")
    @ApiOperation("我创建的任务列表")
    public JudgeTaskManagePageVo<JudgeTaskManagePageDto> selectMyCreateJudgeTask(@RequestParam(name = "pageNum",defaultValue = "1") Integer pageNum,
                                                                                 @RequestParam(name = "pageSize",defaultValue = "20") Integer pageSize,
                                                                                 @RequestParam(required = false) String taskName,
                                                                                 @RequestParam(required = false) String officeName,
                                                                                 @RequestParam(required = false) String createTime) {
        return baseService.selectMyCreateJudgeTask(pageNum,pageSize,taskName,officeName,createTime);
    }

    /**
     * 研判中心-研判任务详情
     * @param taskId
     * @return
     */
    @GetMapping("taskDetail/{taskId}")
    @ApiOperation("研判任务详情")
    public ObjectRestResponse<JudgeTaskDetailDto> selectTaskDetail(@PathVariable("taskId") Integer taskId) {
        return baseService.selectTaskDetail(taskId);
    }

    /**
     * 接警单列表查询
     * @param jjbh 接警单编号
     * @param bjnr 报警内容
     * @param jjlx 报警类型
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("jjdList")
    @ApiOperation("接警单列表查询")
    public TableResultResponse<JjdInfoVo> getJjdInfoList(String jjbh, String bjnr, String jjlx,
                                                         @RequestParam(defaultValue = "1") int pageNum,
                                                         @RequestParam(defaultValue = "10") int pageSize) {
        return apiService.getJjdInfoList(jjbh, null, bjnr, jjlx, pageNum, pageSize);
    }

    /**
     * 案件列表查询
     * @param ajbh 案件编号
     * @param ajlb 案件类别
     * @param jyaq 简要案情
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("caseList")
    @ApiOperation("案件列表查询")
    public TableResultResponse<CaseInfoVo> getCaseInfoList(String ajbh, String ajlb, String jyaq,
                                                           @RequestParam(defaultValue = "1") int pageNum,
                                                           @RequestParam(defaultValue = "10") int pageSize) {
        return apiService.getCaseInfoList(ajbh, ajlb, jyaq, pageNum, pageSize);
    }

    /**
     * 人员列表查询
     * @param gmsfzhm 身份证号码
     * @param xm 姓名
     * @param hjd 户籍地
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("personList")
    @ApiOperation("人员列表查询")
    public TableResultResponse<PersonVo> getPersonInfoList(String gmsfzhm, String xm, String hjd,
                                                           @RequestParam(defaultValue = "1") int pageNum,
                                                           @RequestParam(defaultValue = "10") int pageSize) {
        return apiService.getPersonList(gmsfzhm, xm, hjd, pageNum, pageSize);
    }

    /**
     * 车辆列表查询
     * @param carNo 车牌号
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("carList")
    @ApiOperation("车辆列表查询")
    public TableResultResponse<CarVo> getCarInfoList(String carNo,
                                                           @RequestParam(defaultValue = "1") int pageNum,
                                                           @RequestParam(defaultValue = "10") int pageSize) {
        return apiService.getCarList(carNo, pageNum, pageSize);
    }

    @GetMapping("eventDetail")
    @ApiOperation("关联信息详情查询")
    public ObjectRestResponse getEventDetail(String[] eventIds,String eventType){
        return apiService.getEventDetail(eventIds, eventType);
    }

    @GetMapping("mindOption")
    @ApiOperation("脑图操作判断")
    public BaseResponse mindOption(@RequestParam("taskId") int taskId) {
        return baseService.mindOption(taskId);
    }
}