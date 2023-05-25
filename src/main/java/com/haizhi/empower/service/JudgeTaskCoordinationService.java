package com.haizhi.empower.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.haizhi.empower.base.BaseService;
import com.haizhi.empower.base.resp.BaseResponse;
import com.haizhi.empower.base.resp.TableResultResponse;
import com.haizhi.empower.entity.bo.JudgeTaskCoordinationBo;
import com.haizhi.empower.entity.po.JudgeTaskAttachment;
import com.haizhi.empower.entity.po.JudgeTaskCoordination;
import com.haizhi.empower.entity.po.UserInfo;
import com.haizhi.empower.entity.vo.AttachmentVo;
import com.haizhi.empower.entity.vo.JudgeTaskCoordinationVo;
import com.haizhi.empower.enums.DeleteStatusEnum;
import com.haizhi.empower.mapper.JudgeTaskAttachmentMapper;
import com.haizhi.empower.mapper.JudgeTaskCoordinationMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 研判任务协作者表
 *
 * @author CristianWindy
 * @email 393371775@qq.com
 * @date 2022-03-30 18:31:34
 */
@Service
public class JudgeTaskCoordinationService extends BaseService<JudgeTaskCoordinationMapper, JudgeTaskCoordination> {

    @Autowired
    private AttachmentService attachmentService;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private OfficeInfoService officeInfoService;

    @Autowired
    private JudgeTaskAttachmentMapper judgeTaskAttachmentMapper;

    /**
     * 研判协同新增
     *
     * @param bo
     * @return
     */
    @Transactional(rollbackFor = Exception.class, transactionManager = "mysqlTransactionManager")
    public BaseResponse add(JudgeTaskCoordinationBo bo) {
        JudgeTaskCoordination judgeTaskCoordination = new JudgeTaskCoordination();
        BeanUtils.copyProperties(bo, judgeTaskCoordination);
        insertSelective(judgeTaskCoordination);

        List<JudgeTaskAttachment> judgeTaskAttachments = bo.getJudgeTaskAttachments().stream().peek(obj -> {
            obj.setTableId(judgeTaskCoordination.getId());
            obj.setTableName("t_judge_task_coordination");
        }).collect(Collectors.toList());//设置附件对应表和表主键

        if (judgeTaskAttachments.size() > 0)
            judgeTaskAttachmentMapper.insertList(judgeTaskAttachments);
        return new BaseResponse(0, "新增成功！");
    }

    public BaseResponse delete(int id) {
        JudgeTaskCoordination judgeTaskCoordination = JudgeTaskCoordination.builder()
                .id(id)
                .dr(DeleteStatusEnum.DEL_YES.getCode())
                .build();
        updateSelectiveById(judgeTaskCoordination);
        return new BaseResponse();
    }

    /**
     * 研判协同列表查询
     *
     * @param pageNum
     * @param pageSize
     * @param taskId   任务id
     * @return
     */
    public BaseResponse list(Integer pageNum, Integer pageSize, int taskId) {
        /*
        查询一级协同
         */
        Map<String, UserInfo> userInfoMap = new HashMap<>();//缓存集合
        Page<Object> page = PageHelper.startPage(pageNum, pageSize);
        List<JudgeTaskCoordinationVo> judgeTaskCoordinationOnes = mapper.selectByParentId(0, taskId);
        judgeTaskCoordinationOnes.forEach(one -> {
            UserInfo userInfo1 = userInfoMap.get(one.getCreateUser());
            if (userInfo1 == null) {
                userInfo1 = userInfoService.getUserInfoByUserCode(one.getCreateUser());
                if (userInfo1.getUserCode() != null)
                    userInfoMap.put(one.getCreateUser(), userInfo1);
            }
            one.setCreateUserName(userInfo1.getName());//获取创建人姓名
            one.setUserPicture(userInfo1.getUserPicture());//获取创建人照片
            UserInfo replyUserInfo1 = userInfoMap.get(one.getReplyName());
            if (replyUserInfo1 == null) {
                replyUserInfo1 = userInfoService.getUserInfoByUserCode(one.getReplyName());
                if (replyUserInfo1.getUserCode() != null)
                    userInfoMap.put(one.getReplyName(), replyUserInfo1);
            }
            one.setReplyRealName(replyUserInfo1.getName());//获取回复人姓名
            one.setCreateUnitName(officeInfoService.getOfficeInfoByOfficeCode(one.getCreateUnit()).getOfficeName());//获取组织单位姓名
            List<Long> attachmentIds = judgeTaskAttachmentMapper.selectAttachmentIds("t_judge_task_coordination", one.getId());//查询附件id
            List<AttachmentVo> attachmentVos1 = new ArrayList<>();
            if (attachmentIds.size() > 0)
                attachmentVos1 = attachmentService.selectByIds(attachmentIds);//查询附件详情
            one.setAttachmentVos(attachmentService.conversionLongAddress(attachmentVos1));//附件地址转换为长地址
            /*
            查询二级协同
             */
            List<JudgeTaskCoordinationVo> judgeTaskCoordinationTwos = mapper.selectByParentId(one.getId(), taskId);
            judgeTaskCoordinationTwos.forEach(two -> {
                UserInfo userInfo2 = userInfoMap.get(two.getCreateUser());
                if (userInfo2 == null) {
                    userInfo2 = userInfoService.getUserInfoByUserCode(two.getCreateUser());
                    if (userInfo2.getUserCode() != null)
                        userInfoMap.put(two.getCreateUser(), userInfo2);
                }
                two.setCreateUserName(userInfo2.getName());//获取创建人姓名
                two.setUserPicture(userInfo2.getUserPicture());//获取创建人照片
                UserInfo replyUserInfo2 = userInfoMap.get(two.getReplyName());
                if (replyUserInfo2 == null) {
                    replyUserInfo2 = userInfoService.getUserInfoByUserCode(two.getReplyName());
                    if (replyUserInfo2.getUserCode() != null)
                        userInfoMap.put(two.getReplyName(), replyUserInfo2);
                }
                two.setReplyRealName(replyUserInfo2.getName());//获取回复人姓名
                two.setCreateUnitName(officeInfoService.getOfficeInfoByOfficeCode(two.getCreateUnit()).getOfficeName());//获取组织单位姓名
                List<Long> twoAttachmentIds = judgeTaskAttachmentMapper.selectAttachmentIds("t_judge_task_coordination", two.getId());//查询附件id
                List<AttachmentVo> attachmentVos2 = new ArrayList<>();
                if (twoAttachmentIds.size() > 0)
                    attachmentVos2 = attachmentService.selectByIds(twoAttachmentIds);//查询附件详情
                two.setAttachmentVos(attachmentService.conversionLongAddress(attachmentVos2));//附件地址转换为长地址
            });
            one.setJudgeTaskCoordinationTwos(judgeTaskCoordinationTwos);
        });
        return new TableResultResponse<>(page.getPages(), page.getTotal(), judgeTaskCoordinationOnes);
    }
}