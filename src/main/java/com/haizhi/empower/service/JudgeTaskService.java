package com.haizhi.empower.service;

import cn.bdp.joif.base.threadlocal.ThreadLocalUserId;
import cn.bdp.joif.entity.JoifUcAccount;
import cn.bdp.joif.entity.JoifUcGroup;
import cn.bdp.joif.service.JoifUcService;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.haizhi.empower.base.BaseService;
import com.haizhi.empower.base.JudgeTaskConstants;
import com.haizhi.empower.base.resp.BaseResponse;
import com.haizhi.empower.base.resp.ObjectRestResponse;
import com.haizhi.empower.entity.bo.JudgeTaskBo;
import com.haizhi.empower.entity.bo.JudgeTaskCollaboratorBo;
import com.haizhi.empower.entity.bo.JudgeTaskEventBo;
import com.haizhi.empower.entity.dto.JudgeTaskDetailDto;
import com.haizhi.empower.entity.dto.JudgeTaskManagePageDto;
import com.haizhi.empower.entity.po.*;
import com.haizhi.empower.entity.vo.*;
import com.haizhi.empower.exception.ParamException;
import com.haizhi.empower.exception.ServiceException;
import com.haizhi.empower.mapper.*;
import lombok.extern.slf4j.Slf4j;
import com.haizhi.empower.util.DateUtil;
import com.haizhi.empower.util.RedisUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 字典子表
 *
 * @author CristianWindy
 * @email 393371775@qq.com
 * @date 2022-03-30 18:31:34
 */
@Slf4j
@Service
public class JudgeTaskService extends BaseService<JudgeTaskMapper, JudgeTask> {
    private final String TASK_NUM = "ZXQW";
    private final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Autowired
    private JudgeTaskMapper judgeTaskMapper;
    @Autowired
    private JudgeTaskCollaboratorMapper judgeTaskCollaboratorMapper;
    @Autowired
    private JudgeTaskEventMapper judgeTaskEventMapper;
    @Autowired
    private JudgeTaskAttachmentMapper judgeTaskAttachmentMapper;
    @Autowired
    private JudgeTaskConclusionAttachmentMapper judgeTaskConclusionAttachmentMapper;
    @Autowired
    private JudgeTaskCoordinationMapper judgeTaskCoordinationMapper;
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private OfficeInfoService officeInfoService;
    @Autowired
    private AttachmentService attachmentService;
    @Autowired
    private ApiService apiService;
    @Autowired
    private JoifUcService joifUcService;
    @Autowired
    private RedisUtils redisUtils;
    @Value("${mindName}")
    String mindName;

    /**
     * 研判任务新增
     *
     * @param bo
     * @return
     */
    @Transactional(rollbackFor = Exception.class, transactionManager = "mysqlTransactionManager")
    public BaseResponse save(JudgeTaskBo bo) {
        if (bo.getId() == null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            List<JudgeTask> judgeTasks = judgeTaskMapper.selectByTaskNo(TASK_NUM + sdf.format(new Date()));//获取本日最新编号
            String taskNo = "";
            if (Objects.nonNull(judgeTasks) && judgeTasks.size() > 0) {
                taskNo = addNum(judgeTasks.get(0).getTaskNo());//最新编号加一
            } else {
                taskNo = TASK_NUM + sdf.format(new Date()) + "01";//生成本日首个编号
            }
            JudgeTask judgeTask = new JudgeTask();
            BeanUtils.copyProperties(bo, judgeTask);
            judgeTask.setTaskNo(taskNo);
            judgeTask.setTaskMind(JSON.toJSONString(bo.getTaskMind()));
            mapper.insertSelective(judgeTask);
        /*
        设置协同列表、事件列表、附件列表对应的研判任务主键
         */
            if (bo.getJudgeTaskCollaboratorBos() != null) {
                List<JudgeTaskCollaborator> judgeTaskCollaborators = new ArrayList<>();
                for (JudgeTaskCollaboratorBo judgeTaskCollaboratorBo : bo.getJudgeTaskCollaboratorBos()) {
                    judgeTaskCollaboratorBo.getCollaborator().forEach(collaborator -> {
                        judgeTaskCollaborators.add(JudgeTaskCollaborator.builder()
                                .taskId(judgeTask.getId())
                                .collaborator(collaborator)
                                .createUser(bo.getCreateUser())
                                .build());
                    });
                }
                if (judgeTaskCollaborators.size() > 0)
                    judgeTaskCollaboratorMapper.insertList(judgeTaskCollaborators);
            }
            if (bo.getJudgeTaskEventBos() != null) {
                List<JudgeTaskEvent> judgeTaskEvents = new ArrayList<>();
                for (JudgeTaskEventBo judgeTaskEventBo : bo.getJudgeTaskEventBos()) {
                    judgeTaskEventBo.getEventId().forEach(eventId -> {
                        judgeTaskEvents.add(JudgeTaskEvent.builder()
                                .taskId(judgeTask.getId())
                                .eventId(eventId)
                                .eventType(judgeTaskEventBo.getEventType())
                                .createUser(bo.getCreateUser())
                                .build());
                    });
                }
                if (judgeTaskEvents.size() > 0)
                    judgeTaskEventMapper.insertList(judgeTaskEvents);
            }
            if (bo.getJudgeTaskAttachments() != null) {
                List<JudgeTaskAttachment> judgeTaskAttachments = bo.getJudgeTaskAttachments().stream().peek(obj -> {
                            obj.setTableId(judgeTask.getId());
                            obj.setTableName("t_judge_task");
                        }
                ).collect(Collectors.toList());
                if (judgeTaskAttachments.size() > 0)
                    judgeTaskAttachmentMapper.insertList(judgeTaskAttachments);
            }
            redisUtils.save(mindName + ":" + judgeTask.getId(), "0", DateUtil.MILLIS_PER_HOUR);
            return new ObjectRestResponse<>(0, "新增成功！").data(judgeTask.getId());
        } else {
            return update(bo);
        }
    }

    /**
     * 研判任务完结
     *
     * @return
     */
    @Transactional(rollbackFor = Exception.class, transactionManager = "mysqlTransactionManager")
    public BaseResponse finish(int id) {
        updateSelectiveById(JudgeTask.builder()
                .id(id)
                .taskStatus("1")
                .build());
        return new BaseResponse(0, "研判任务完结成功");
    }

    /**
     * 研判任务更新
     *
     * @param bo
     * @return
     */
    @Transactional(rollbackFor = Exception.class, transactionManager = "mysqlTransactionManager")
    public BaseResponse update(JudgeTaskBo bo) {
        JudgeTask judgeTask = new JudgeTask();
        BeanUtils.copyProperties(bo, judgeTask);
        judgeTask.setTaskMind(JSON.toJSONString(bo.getTaskMind()));
        mapper.updateByPrimaryKeySelective(judgeTask);
        /*
        设置协同列表、事件列表、附件列表、研判任务结论附件列表对应的研判任务主键
         */
        if (bo.getJudgeTaskCollaboratorBos() != null) {
            List<JudgeTaskCollaborator> judgeTaskCollaborators = new ArrayList<>();
            for (JudgeTaskCollaboratorBo judgeTaskCollaboratorBo : bo.getJudgeTaskCollaboratorBos()) {
                judgeTaskCollaboratorBo.getCollaborator().forEach(collaborator -> {
                    judgeTaskCollaborators.add(JudgeTaskCollaborator.builder()
                            .taskId(judgeTask.getId())
                            .collaborator(collaborator)
                            .createUser(judgeTask.getCreateUser())
                            .build());
                });
            }
            //更新协作者
            judgeTaskCollaboratorMapper.deleteByTaskId(judgeTask.getId());
            if (judgeTaskCollaborators.size() > 0)
                judgeTaskCollaboratorMapper.insertList(judgeTaskCollaborators);
        }
        if (bo.getJudgeTaskEventBos() != null) {
            List<JudgeTaskEvent> judgeTaskEvents = new ArrayList<>();
            for (JudgeTaskEventBo judgeTaskEventBo : bo.getJudgeTaskEventBos()) {
                judgeTaskEventBo.getEventId().forEach(eventId -> {
                    judgeTaskEvents.add(JudgeTaskEvent.builder()
                            .taskId(judgeTask.getId())
                            .eventId(eventId)
                            .eventType(judgeTaskEventBo.getEventType())
                            .createUser(judgeTask.getCreateUser())
                            .build());
                });
            }
            judgeTaskEventMapper.deleteByTaskId(judgeTask.getId());
            if (judgeTaskEvents.size() > 0)

        }
        if (bo.getJudgeTaskAttachments() != null) {
            List<JudgeTaskAttachment> judgeTaskAttachments = bo.getJudgeTaskAttachments().stream().peek(obj -> {
                        obj.setTableId(judgeTask.getId());
                        obj.setTableName("t_judge_task");
                    }
            ).collect(Collectors.toList());
            //更新任务附件
            judgeTaskAttachmentMapper.deleteByTableName("t_judge_task", judgeTask.getId());
            if (judgeTaskAttachments.size() > 0)
                judgeTaskAttachmentMapper.insertList(judgeTaskAttachments);
        }
        if (bo.getJudgeTaskConclusionAttachments() != null) {
            List<JudgeTaskConclusionAttachment> judgeTaskConclusionAttachments = bo.getJudgeTaskConclusionAttachments().stream().peek(obj -> obj.setTaskId(judgeTask.getId())).collect(Collectors.toList());
            //更新任务结论
            judgeTaskConclusionAttachmentMapper.deleteByTaskId(judgeTask.getId());
            if (judgeTaskConclusionAttachments.size() > 0)
                judgeTaskConclusionAttachmentMapper.insertList(judgeTaskConclusionAttachments);
        }
        redisUtils.save(mindName + ":" + judgeTask.getId(), "0", DateUtil.MILLIS_PER_HOUR);
        return new BaseResponse(0, "保存成功！");
    }

    public BaseResponse list() {
        return new BaseResponse();
    }

    /**
     * 编号增加
     *
     * @param str 最新编号
     * @return 编号
     */
    public static String addNum(String str) {
        String numStr = str.substring(str.length() - 2); //取出最后两位数字
        if (!StringUtils.isEmpty(numStr)) { //如果最后两位不是数字，抛NumberFormatException异常
            int n = numStr.length(); //取出字符串的长度
            int num = Integer.parseInt(numStr) + 1; //将该数字加一
            String added = String.valueOf(num);
            n = Math.min(n, added.length());
            //拼接字符串
            return str.subSequence(0, str.length() - n) + added;
        } else {
            throw new NumberFormatException();
        }
    }


    /**
     * 研判中心-首页待办任务列表
     * @return
     */
    public ObjectRestResponse<JudgeTaskHomePagePendShowVo> selectHomePagePendList() {

        /*
        * 获取当前登录人的唯一标识
        * 登录人唯一标识校验
        */
        String userCode = ThreadLocalUserId.get();
        log.info("研判中心-首页待办任务列表-当前登录人的唯一标识{}",userCode);
        if (StringUtils.isBlank(userCode)) {
            throw new ServiceException(JudgeTaskConstants.ServiceError.LOGIN_USER_CODE_ERROR);
        }

        /*
         * 调用封装方法: 根据登录人唯一标识和任务状态(进行中),获取登录人创建和协作的任务
         * 获取登录人创建和协助的任务数量
         * 如果登录人创建和协作的任务总数大于等于7个,取前7个
         */
        List<JudgeTask> pendList = this.getCreateAndCollaboratorTask(userCode,JudgeTaskConstants.ServiceParam.PROGRESS_TASK_STATUS);
        int pendCount = JudgeTaskConstants.ServiceParam.PEND_TASK_INIT_SHOW_COUNT;
        if (Objects.nonNull(pendList)) {
            pendCount = pendList.size();
            log.info("研判中心-首页待办任务列表-当前登录人创建和待办的总任务数{}",pendCount);
            if (pendCount >= JudgeTaskConstants.ServiceParam.PEND_TASK_LIST_SHOW_ROW) {
                pendList = pendList.stream()
                        .limit(JudgeTaskConstants.ServiceParam.PEND_TASK_LIST_SHOW_ROW)
                        .collect(Collectors.toList());
            }
            log.info("研判中心-首页待办任务列表-当前登录人创建和待办的展示任务数{}",pendCount);
        }

        /*
        * 获取所有用户
        * 获取所有组织
        * 用创建人姓名替换创建人唯一标识
        * 用创建组织名称替换创建组织唯一标识
        */
        if (!CollectionUtils.isEmpty(pendList)) {
            List<JoifUcAccount> allUserList = joifUcService.getAllUsers();
            List<JoifUcGroup> allGroupList = joifUcService.getAllGroups();
            if (!CollectionUtils.isEmpty(allUserList)) {
                pendList = pendList.stream()
                        .peek(judgeTask -> {
                            String createUser = judgeTask.getCreateUser();
                            for (JoifUcAccount joifUcAccount : allUserList) {
                                String userId = joifUcAccount.getUser_id();
                                if (StringUtils.equals(createUser,userId)) {
                                    judgeTask.setCreateUser(joifUcAccount.getName());
                                    log.info("研判中心-首页待办任务列表-任务创建人姓名填充成功");
                                    break;
                                }
                            }
                        })
                        .collect(Collectors.toList());
            }
            if (!CollectionUtils.isEmpty(allGroupList)) {
                pendList = pendList.stream()
                        .peek(judgeTask -> {
                            String createUnit = judgeTask.getCreateUnit();
                            for (JoifUcGroup joifUcGroup : allGroupList) {
                                String groupId = joifUcGroup.getGroup_id();
                                if (StringUtils.equals(createUnit,groupId)) {
                                    judgeTask.setCreateUnit(joifUcGroup.getGroup_name());
                                    log.info("研判中心-首页待办任务列表-任务创建组织名称填充成功");
                                    break;
                                }
                            }
                        })
                        .collect(Collectors.toList());
            }
        }

        /*
         * 封装响应数据
         * 响应前端数据
         */
        List<JudgeTaskHomePagePendVo> judgeTaskHomePagePendVoList = null;
        if (Objects.nonNull(pendList)) {
            judgeTaskHomePagePendVoList = pendList.stream()
                    .map(judgeTask -> {
                        JudgeTaskHomePagePendVo judgeTaskHomePagePendVo = JudgeTaskHomePagePendVo.builder().build();
                        if (Objects.nonNull(judgeTask)) {
                            BeanUtils.copyProperties(judgeTask, judgeTaskHomePagePendVo);
                        }
                        return judgeTaskHomePagePendVo;
                    })
                    .collect(Collectors.toList());
        }
        JudgeTaskHomePagePendShowVo judgeTaskHomePagePendShowVo = JudgeTaskHomePagePendShowVo.builder()
                .pendList(judgeTaskHomePagePendVoList)
                .pendCount(pendCount)
                .build();
        ObjectRestResponse<JudgeTaskHomePagePendShowVo> objectRestResponse = new ObjectRestResponse<>();
        objectRestResponse.setData(judgeTaskHomePagePendShowVo);
        log.info("研判中心-首页待办任务列表-封装响应数据成功");
        return objectRestResponse;
    }

    /**
     * 研判中心-首页消息通知
     * @return
     */
    public ObjectRestResponse<List<JudgeTaskHomePageMessageVo>> selectMessageList() {

        /*
         * 获取当前登录人的唯一标识
         * 登录人唯一标识校验
         */
        String userCode = ThreadLocalUserId.get();
        log.info("研判中心-首页消息通知-当前登录人的唯一标识{}",userCode);
        if (StringUtils.isBlank(userCode)) {
            throw new ServiceException(JudgeTaskConstants.ServiceError.LOGIN_USER_CODE_ERROR);
        }

        /*
         * 获取登录人反馈消息(我创建的任务,别人反馈的)
         * 获取登录人协作消息(别人创建的任务,需要我协作)
         * 合并登录人反馈消息和协作消息,并按创建时间降序排列
         * 如果登录人反馈消息和协作消息总数大于等于10条,取前10条
         */
        List<JudgeMessage> feedbackMessageList = mapper.selectFeedbackMessageList(userCode);
        List<JudgeMessage> cooperateMessageList = mapper.selectCooperateMessageList(userCode);
        List<JudgeMessage> messageList = new ArrayList<>(feedbackMessageList.size() + cooperateMessageList.size());
        messageList.addAll(feedbackMessageList);
        messageList.addAll(cooperateMessageList);
        log.info("研判中心-首页消息通知-当前登录人反馈和协作的总消息数{}",messageList.size());
        if (!CollectionUtils.isEmpty(messageList)) {
            messageList = messageList.stream()
                    .sorted(Comparator.comparing(JudgeMessage::getCreateTime).reversed())
                    .collect(Collectors.toList());
            if (messageList.size() >= JudgeTaskConstants.ServiceParam.MESSAGE_SHOW_ROW) {
                messageList = messageList.stream()
                        .limit(JudgeTaskConstants.ServiceParam.MESSAGE_SHOW_ROW)
                        .collect(Collectors.toList());
            }
        }
        log.info("研判中心-首页消息通知-当前登录人反馈和协作的展示消息数{}",messageList.size());

        /*
         * 封装响应数据
         * 响应前端数据
         */
        List<JudgeTaskHomePageMessageVo> judgeTaskHomePageMessageVoList = messageList.stream()
                .map(judgeMessage -> {
                    JudgeTaskHomePageMessageVo judgeTaskHomePageMessageVo = JudgeTaskHomePageMessageVo.builder().build();
                    if (Objects.nonNull(judgeMessage)) {
                        BeanUtils.copyProperties(judgeMessage, judgeTaskHomePageMessageVo);
                    }
                    return judgeTaskHomePageMessageVo;
                })
                .collect(Collectors.toList());
        ObjectRestResponse<List<JudgeTaskHomePageMessageVo>> restResponse = new ObjectRestResponse<>();
        restResponse.setData(judgeTaskHomePageMessageVoList);
        log.info("研判中心-首页消息通知-封装响应数据成功");
        return restResponse;
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
    public JudgeTaskManagePageVo<JudgeTaskManagePageDto> selectProgressJudgeTask(Integer pageNum, Integer pageSize, String taskName, String officeName, String createTime) {

        /*
         * 获取当前登录人的唯一标识
         * 登录人唯一标识校验
         */
        String userCode = ThreadLocalUserId.get();
        log.info("研判中心-进行中的任务列表-当前登录人的唯一标识{}",userCode);
        if (StringUtils.isBlank(userCode)) {
            throw new ServiceException(JudgeTaskConstants.ServiceError.LOGIN_USER_CODE_ERROR);
        }

        /*
        * 请求参数校验
        * 请求参数处理
        */
        log.info("研判中心-进行中的任务列表-获取请求参数pageNum{}",pageNum);
        log.info("研判中心-进行中的任务列表-获取请求参数pageSize{}",pageSize);
        log.info("研判中心-进行中的任务列表-获取请求参数taskName{}",taskName);
        log.info("研判中心-进行中的任务列表-获取请求参数officeName{}",officeName);
        log.info("研判中心-进行中的任务列表-获取请求参数createTime{}",createTime);
        if (Objects.isNull(pageNum)
                || Objects.isNull(pageSize)
                || pageNum <= JudgeTaskConstants.ServiceParam.PAGE_NUM_AND_PAGE_SIZE_ERROR_PARAM
                || pageSize <= JudgeTaskConstants.ServiceParam.PAGE_NUM_AND_PAGE_SIZE_ERROR_PARAM) {
            throw new ParamException();
        }
        if (StringUtils.isNotBlank(taskName)) {
            taskName = taskName.replace(JudgeTaskConstants.ServiceParam.SPACE_STRING,JudgeTaskConstants.ServiceParam.BLANK_STRING);
            log.info("研判中心-进行中的任务列表-处理请求参数taskName{}",taskName);
        }
        if (StringUtils.isNotBlank(officeName)) {
            officeName = officeName.replace(JudgeTaskConstants.ServiceParam.SPACE_STRING,JudgeTaskConstants.ServiceParam.BLANK_STRING);
            log.info("研判中心-进行中的任务列表-处理请求参数officeName{}",officeName);
        }

        /*
        * 调用封装方法: 根据登录人唯一标识和任务状态(进行中),获取登录人创建和协作的任务
        * 将登录人创建和协作的任务复制到响应对象中
        * 获取所有用户
        * 获取所有组织
        * 填充任务创建人姓名和创建组织名称
        * 根据用户输入条件筛选任务
        */
        List<JudgeTask> pendList = this.getCreateAndCollaboratorTask(userCode,JudgeTaskConstants.ServiceParam.PROGRESS_TASK_STATUS);
        List<JudgeTaskManagePageDto> judgeTaskManagePageDtoList = null;
        if (!CollectionUtils.isEmpty(pendList)) {
            judgeTaskManagePageDtoList = pendList.stream()
                    .map(judgeTask -> {
                        if (Objects.nonNull(judgeTask)) {
                            JudgeTaskManagePageDto judgeTaskManagePageDto = JudgeTaskManagePageDto.builder().build();
                            judgeTaskManagePageDto.setId(judgeTask.getId());
                            judgeTaskManagePageDto.setTaskName(judgeTask.getTaskName());
                            judgeTaskManagePageDto.setTaskNo(judgeTask.getTaskNo());
                            judgeTaskManagePageDto.setTaskDesciption(judgeTask.getTaskDesciption());
                            judgeTaskManagePageDto.setTaskStatus(judgeTask.getTaskStatus());
                            judgeTaskManagePageDto.setOfficeCode(judgeTask.getCreateUnit());
                            judgeTaskManagePageDto.setUserCode(judgeTask.getCreateUser());
                            judgeTaskManagePageDto.setCreateTime(judgeTask.getCreateTime());
                            return judgeTaskManagePageDto;
                        }
                        return new JudgeTaskManagePageDto();
                    }).collect(Collectors.toList());

            List<JoifUcAccount> allUserList = joifUcService.getAllUsers();
            List<JoifUcGroup> allGroupList = joifUcService.getAllGroups();
            if (!CollectionUtils.isEmpty(judgeTaskManagePageDtoList)) {
                if (!CollectionUtils.isEmpty(allUserList)) {
                    judgeTaskManagePageDtoList = judgeTaskManagePageDtoList.stream()
                            .peek(judgeTaskManagePageDto -> {
                                String dtoUserCode = judgeTaskManagePageDto.getUserCode();
                                for (JoifUcAccount joifUcAccount : allUserList) {
                                    String userId = joifUcAccount.getUser_id();
                                    if (StringUtils.equals(dtoUserCode,userId)) {
                                        judgeTaskManagePageDto.setCreateUser(joifUcAccount.getName());
                                        break;
                                    }
                                }
                            })
                            .collect(Collectors.toList());
                    log.info("研判中心-进行中的任务列表-任务创建人姓名填充完成");
                }
                if (!CollectionUtils.isEmpty(allGroupList)) {
                    judgeTaskManagePageDtoList = judgeTaskManagePageDtoList.stream()
                            .peek(judgeTaskManagePageDto -> {
                                String dtoOfficeCode = judgeTaskManagePageDto.getOfficeCode();
                                for (JoifUcGroup joifUcGroup : allGroupList) {
                                    String groupId = joifUcGroup.getGroup_id();
                                    if (StringUtils.equals(dtoOfficeCode,groupId)) {
                                        judgeTaskManagePageDto.setCreateUnit(joifUcGroup.getGroup_name());
                                        break;
                                    }
                                }
                            })
                            .collect(Collectors.toList());
                    log.info("研判中心-进行中的任务列表-任务创建组织名称填充完成");
                }

                if (StringUtils.isNotBlank(taskName)) {
                    String streamTaskName = taskName;
                    judgeTaskManagePageDtoList = judgeTaskManagePageDtoList.stream()
                            .filter(judgeTaskManagePageDto -> {
                                if (Objects.nonNull(judgeTaskManagePageDto)) {
                                    String dtoTaskName = judgeTaskManagePageDto.getTaskName();
                                    if (StringUtils.isNotBlank(dtoTaskName)) {
                                        return dtoTaskName.contains(streamTaskName);
                                    }
                                }
                                return false;
                            }).collect(Collectors.toList());
                    log.info("研判中心-进行中的任务列表-根据任务名称筛选完成");
                }
                if (StringUtils.isNotBlank(officeName)) {
                    String streamOfficeName = officeName;
                    judgeTaskManagePageDtoList = judgeTaskManagePageDtoList.stream()
                            .filter(judgeTaskManagePageDto -> {
                                if (Objects.nonNull(judgeTaskManagePageDto)) {
                                    String dtoCreateUnit = judgeTaskManagePageDto.getCreateUnit();
                                    if (StringUtils.isNotBlank(dtoCreateUnit)) {
                                        return dtoCreateUnit.contains(streamOfficeName);
                                    }
                                }
                                return false;
                            }).collect(Collectors.toList());
                    log.info("研判中心-进行中的任务列表-根据组织名称筛选完成");
                }
                if (StringUtils.isNotBlank(createTime)) {
                    judgeTaskManagePageDtoList = judgeTaskManagePageDtoList.stream()
                            .filter(judgeTaskManagePageDto -> {
                                if (Objects.nonNull(judgeTaskManagePageDto)) {
                                    Date dtoCreateTime = judgeTaskManagePageDto.getCreateTime();
                                    if (Objects.nonNull(dtoCreateTime)) {
                                        return StringUtils.equals(createTime,SIMPLE_DATE_FORMAT.format(dtoCreateTime));
                                    }
                                }
                                return false;
                            }).collect(Collectors.toList());
                    log.info("研判中心-进行中的任务列表-根据创建时间筛选完成");
                }
            }
        }

        /*
        * 计算总条数
        * 计算总页数
        * 指定数据分页
        */
        PageInfo<JudgeTaskManagePageDto> pageInfo = null;
        if (!CollectionUtils.isEmpty(judgeTaskManagePageDtoList)) {
            Page<JudgeTaskManagePageDto> page = new Page<>(pageNum,pageSize);
            long total = judgeTaskManagePageDtoList.size();
            page.setTotal(total);
            int startIndex = (pageNum - 1) * pageSize;
            int endIndex = (int) Math.min(startIndex + pageSize,total);
            page.addAll(judgeTaskManagePageDtoList.subList(startIndex,endIndex));
            pageInfo = new PageInfo<>(page);
            log.info("研判中心-进行中的任务列表-任务分页完成");
        }

        /*
        * 填充是否是当前登录人创建的任务
        */
        List<JudgeTaskManagePageDto> pageDataList = null;
        if (Objects.nonNull(pageInfo)) {
            pageDataList = pageInfo.getList();
            if (!CollectionUtils.isEmpty(pageDataList)) {
                pageDataList = pageDataList.stream().peek(judgeTaskManagePageDto -> {
                    if (Objects.nonNull(judgeTaskManagePageDto)) {
                        String createUserCode = judgeTaskManagePageDto.getUserCode();
                        if (StringUtils.equals(userCode, createUserCode)) {
                            judgeTaskManagePageDto.setLoginUserType(JudgeTaskConstants.ServiceParam.IS_LOGIN_USER_CREATE_TASK);
                        } else {
                            judgeTaskManagePageDto.setLoginUserType(JudgeTaskConstants.ServiceParam.NOT_LOGIN_USER_CREATE_TASK);
                        }
                    }
                }).collect(Collectors.toList());
            }
            log.info("研判中心-进行中的任务列表-填充是否是当前登录人创建的任务完成");
        }

        /*
        * 调用封装方法: 基于当前登录人,获取进行中,已完成,我创建的任务数
        */
        Map<String, Integer> countMap = this.getProgressAndFinishAndMyCreateTaskCount(userCode);
        Integer progressCount = JudgeTaskConstants.ServiceParam.TASK_DEFAULT_COUNT;
        Integer finishCount = JudgeTaskConstants.ServiceParam.TASK_DEFAULT_COUNT;
        Integer myCreateCount = JudgeTaskConstants.ServiceParam.TASK_DEFAULT_COUNT;
        if (!CollectionUtils.isEmpty(countMap)) {
            progressCount = countMap.get(JudgeTaskConstants.ServiceParam.PROGRESS_COUNT_KEY);
            finishCount = countMap.get(JudgeTaskConstants.ServiceParam.FINISH_COUNT_KEY);
            myCreateCount = countMap.get(JudgeTaskConstants.ServiceParam.MY_CREATE_COUNT);
        }
        log.info("研判中心-进行中的任务列表-当前登录人进行中的任务数{}",progressCount);
        log.info("研判中心-进行中的任务列表-当前登录人已完成的任务数{}",finishCount);
        log.info("研判中心-进行中的任务列表-当前登录人创建的任务数{}",myCreateCount);


        /*
        * 封装响应数据
        * 响应前端数据
        */
        JudgeTaskManagePageVo<JudgeTaskManagePageDto> judgeTaskManagePageVo = new JudgeTaskManagePageVo<>();
        if (Objects.nonNull(pageInfo)) {
            judgeTaskManagePageVo.setPages(pageInfo.getPages());
            judgeTaskManagePageVo.setTotal(pageInfo.getTotal());
            judgeTaskManagePageVo.setData(pageDataList);
            log.info("研判中心-进行中的任务列表-响应任务封装完成");
        }
        judgeTaskManagePageVo.setProgressCount(progressCount);
        judgeTaskManagePageVo.setFinishCount(finishCount);
        judgeTaskManagePageVo.setMyCreateCount(myCreateCount);
        log.info("研判中心-进行中的任务列表-响应数量封装完成");
        return judgeTaskManagePageVo;
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
    public JudgeTaskManagePageVo<JudgeTaskManagePageDto> selectFinishJudgeTask(Integer pageNum, Integer pageSize, String taskName, String officeName, String createTime) {

        /*
         * 获取当前登录人的唯一标识
         * 登录人唯一标识校验
         */
        String userCode = ThreadLocalUserId.get();
        log.info("研判中心-已完成的任务列表-当前登录人的唯一标识{}",userCode);
        if (StringUtils.isBlank(userCode)) {
            throw new ServiceException(JudgeTaskConstants.ServiceError.LOGIN_USER_CODE_ERROR);
        }

        /*
         * 请求参数校验
         * 请求参数处理
         */
        log.info("研判中心-已完成的任务列表-获取请求参数pageNum{}",pageNum);
        log.info("研判中心-已完成的任务列表-获取请求参数pageSize{}",pageSize);
        log.info("研判中心-已完成的任务列表-获取请求参数taskName{}",taskName);
        log.info("研判中心-已完成的任务列表-获取请求参数officeName{}",officeName);
        log.info("研判中心-已完成的任务列表-获取请求参数createTime{}",createTime);
        if (Objects.isNull(pageNum)
                || Objects.isNull(pageSize)
                || pageNum <= JudgeTaskConstants.ServiceParam.PAGE_NUM_AND_PAGE_SIZE_ERROR_PARAM
                || pageSize <= JudgeTaskConstants.ServiceParam.PAGE_NUM_AND_PAGE_SIZE_ERROR_PARAM) {
            throw new ParamException();
        }
        if (StringUtils.isNotBlank(taskName)) {
            taskName = taskName.replace(JudgeTaskConstants.ServiceParam.SPACE_STRING,JudgeTaskConstants.ServiceParam.BLANK_STRING);
            log.info("研判中心-已完成的任务列表-处理请求参数taskName{}",taskName);
        }
        if (StringUtils.isNotBlank(officeName)) {
            officeName = officeName.replace(JudgeTaskConstants.ServiceParam.SPACE_STRING,JudgeTaskConstants.ServiceParam.BLANK_STRING);
            log.info("研判中心-已完成的任务列表-处理请求参数officeName{}",officeName);
        }

        /*
         * 调用封装方法: 根据登录人唯一标识和任务状态(已完成),获取登录人创建和协作的任务
         * 将登录人创建和协作的任务复制到响应对象中
         * 获取所有用户
         * 获取所有组织
         * 填充任务创建人姓名和创建组织名称
         * 根据用户输入条件筛选任务
         */
        List<JudgeTask> pendList = this.getCreateAndCollaboratorTask(userCode,JudgeTaskConstants.ServiceParam.FINISH_TASK_STATUS);
        List<JudgeTaskManagePageDto> judgeTaskManagePageDtoList = null;
        if (!CollectionUtils.isEmpty(pendList)) {
            judgeTaskManagePageDtoList = pendList.stream()
                    .map(judgeTask -> {
                        if (Objects.nonNull(judgeTask)) {
                            JudgeTaskManagePageDto judgeTaskManagePageDto = JudgeTaskManagePageDto.builder().build();
                            judgeTaskManagePageDto.setId(judgeTask.getId());
                            judgeTaskManagePageDto.setTaskName(judgeTask.getTaskName());
                            judgeTaskManagePageDto.setTaskNo(judgeTask.getTaskNo());
                            judgeTaskManagePageDto.setTaskDesciption(judgeTask.getTaskDesciption());
                            judgeTaskManagePageDto.setTaskStatus(judgeTask.getTaskStatus());
                            judgeTaskManagePageDto.setOfficeCode(judgeTask.getCreateUnit());
                            judgeTaskManagePageDto.setUserCode(judgeTask.getCreateUser());
                            judgeTaskManagePageDto.setCreateTime(judgeTask.getCreateTime());
                            return judgeTaskManagePageDto;
                        }
                        return new JudgeTaskManagePageDto();
                    }).collect(Collectors.toList());

            List<JoifUcAccount> allUserList = joifUcService.getAllUsers();
            List<JoifUcGroup> allGroupList = joifUcService.getAllGroups();
            if (!CollectionUtils.isEmpty(judgeTaskManagePageDtoList)) {
                if (!CollectionUtils.isEmpty(allUserList)) {
                    judgeTaskManagePageDtoList = judgeTaskManagePageDtoList.stream()
                            .peek(judgeTaskManagePageDto -> {
                                String dtoUserCode = judgeTaskManagePageDto.getUserCode();
                                for (JoifUcAccount joifUcAccount : allUserList) {
                                    String userId = joifUcAccount.getUser_id();
                                    if (StringUtils.equals(dtoUserCode,userId)) {
                                        judgeTaskManagePageDto.setCreateUser(joifUcAccount.getName());
                                        break;
                                    }
                                }
                            })
                            .collect(Collectors.toList());
                    log.info("研判中心-已完成的任务列表-任务创建人姓名填充完成");
                }
                if (!CollectionUtils.isEmpty(allGroupList)) {
                    judgeTaskManagePageDtoList = judgeTaskManagePageDtoList.stream()
                            .peek(judgeTaskManagePageDto -> {
                                String dtoOfficeCode = judgeTaskManagePageDto.getOfficeCode();
                                for (JoifUcGroup joifUcGroup : allGroupList) {
                                    String groupId = joifUcGroup.getGroup_id();
                                    if (StringUtils.equals(dtoOfficeCode,groupId)) {
                                        judgeTaskManagePageDto.setCreateUnit(joifUcGroup.getGroup_name());
                                        break;
                                    }
                                }
                            })
                            .collect(Collectors.toList());
                    log.info("研判中心-已完成的任务列表-任务创建组织名称填充完成");
                }

                if (StringUtils.isNotBlank(taskName)) {
                    String streamTaskName = taskName;
                    judgeTaskManagePageDtoList = judgeTaskManagePageDtoList.stream()
                            .filter(judgeTaskManagePageDto -> {
                                if (Objects.nonNull(judgeTaskManagePageDto)) {
                                    String dtoTaskName = judgeTaskManagePageDto.getTaskName();
                                    if (StringUtils.isNotBlank(dtoTaskName)) {
                                        return dtoTaskName.contains(streamTaskName);
                                    }
                                }
                                return false;
                            }).collect(Collectors.toList());
                    log.info("研判中心-已完成的任务列表-根据任务名称筛选完成");
                }
                if (StringUtils.isNotBlank(officeName)) {
                    String streamOfficeName = officeName;
                    judgeTaskManagePageDtoList = judgeTaskManagePageDtoList.stream()
                            .filter(judgeTaskManagePageDto -> {
                                if (Objects.nonNull(judgeTaskManagePageDto)) {
                                    String dtoCreateUnit = judgeTaskManagePageDto.getCreateUnit();
                                    if (StringUtils.isNotBlank(dtoCreateUnit)) {
                                        return dtoCreateUnit.contains(streamOfficeName);
                                    }
                                }
                                return false;
                            }).collect(Collectors.toList());
                    log.info("研判中心-已完成的任务列表-根据组织名称筛选完成");
                }
                if (StringUtils.isNotBlank(createTime)) {
                    judgeTaskManagePageDtoList = judgeTaskManagePageDtoList.stream()
                            .filter(judgeTaskManagePageDto -> {
                                if (Objects.nonNull(judgeTaskManagePageDto)) {
                                    Date dtoCreateTime = judgeTaskManagePageDto.getCreateTime();
                                    if (Objects.nonNull(dtoCreateTime)) {
                                        return StringUtils.equals(createTime,SIMPLE_DATE_FORMAT.format(dtoCreateTime));
                                    }
                                }
                                return false;
                            }).collect(Collectors.toList());
                    log.info("研判中心-已完成的任务列表-根据创建时间筛选完成");
                }
            }
        }

        /*
         * 计算总条数
         * 计算总页数
         * 指定数据分页
         */
        PageInfo<JudgeTaskManagePageDto> pageInfo = null;
        if (!CollectionUtils.isEmpty(judgeTaskManagePageDtoList)) {
            Page<JudgeTaskManagePageDto> page = new Page<>(pageNum,pageSize);
            long total = judgeTaskManagePageDtoList.size();
            page.setTotal(total);
            int startIndex = (pageNum - 1) * pageSize;
            int endIndex = (int) Math.min(startIndex + pageSize,total);
            page.addAll(judgeTaskManagePageDtoList.subList(startIndex,endIndex));
            pageInfo = new PageInfo<>(page);
            log.info("研判中心-已完成的任务列表-任务分页完成");
        }

        /*
         * 填充是否是当前登录人创建的任务
         */
        List<JudgeTaskManagePageDto> pageDataList = null;
        if (Objects.nonNull(pageInfo)) {
            pageDataList = pageInfo.getList();
            if (!CollectionUtils.isEmpty(pageDataList)) {
                pageDataList = pageDataList.stream().peek(judgeTaskManagePageDto -> {
                    if (Objects.nonNull(judgeTaskManagePageDto)) {
                        String createUserCode = judgeTaskManagePageDto.getUserCode();
                        if (StringUtils.equals(userCode, createUserCode)) {
                            judgeTaskManagePageDto.setLoginUserType(JudgeTaskConstants.ServiceParam.IS_LOGIN_USER_CREATE_TASK);
                        } else {
                            judgeTaskManagePageDto.setLoginUserType(JudgeTaskConstants.ServiceParam.NOT_LOGIN_USER_CREATE_TASK);
                        }
                    }
                }).collect(Collectors.toList());
            }
            log.info("研判中心-已完成的任务列表-填充是否是当前登录人创建的任务完成");
        }

        /*
         * 调用封装方法: 基于当前登录人,获取进行中,已完成,我创建的任务数
         */
        Map<String, Integer> countMap = this.getProgressAndFinishAndMyCreateTaskCount(userCode);
        Integer progressCount = JudgeTaskConstants.ServiceParam.TASK_DEFAULT_COUNT;
        Integer finishCount = JudgeTaskConstants.ServiceParam.TASK_DEFAULT_COUNT;
        Integer myCreateCount = JudgeTaskConstants.ServiceParam.TASK_DEFAULT_COUNT;
        if (!CollectionUtils.isEmpty(countMap)) {
            progressCount = countMap.get(JudgeTaskConstants.ServiceParam.PROGRESS_COUNT_KEY);
            finishCount = countMap.get(JudgeTaskConstants.ServiceParam.FINISH_COUNT_KEY);
            myCreateCount = countMap.get(JudgeTaskConstants.ServiceParam.MY_CREATE_COUNT);
        }
        log.info("研判中心-已完成的任务列表-当前登录人进行中的任务数{}",progressCount);
        log.info("研判中心-已完成的任务列表-当前登录人已完成的任务数{}",finishCount);
        log.info("研判中心-已完成的任务列表-当前登录人创建的任务数{}",myCreateCount);


        /*
         * 封装响应数据
         * 响应前端数据
         */
        JudgeTaskManagePageVo<JudgeTaskManagePageDto> judgeTaskManagePageVo = new JudgeTaskManagePageVo<>();
        if (Objects.nonNull(pageInfo)) {
            judgeTaskManagePageVo.setPages(pageInfo.getPages());
            judgeTaskManagePageVo.setTotal(pageInfo.getTotal());
            judgeTaskManagePageVo.setData(pageDataList);
            log.info("研判中心-已完成的任务列表-响应任务封装完成");
        }
        judgeTaskManagePageVo.setProgressCount(progressCount);
        judgeTaskManagePageVo.setFinishCount(finishCount);
        judgeTaskManagePageVo.setMyCreateCount(myCreateCount);
        log.info("研判中心-已完成的任务列表-响应数量封装完成");
        return judgeTaskManagePageVo;
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
    public JudgeTaskManagePageVo<JudgeTaskManagePageDto> selectMyCreateJudgeTask(Integer pageNum, Integer pageSize, String taskName, String officeName, String createTime) {

        /*
         * 获取当前登录人的唯一标识
         * 登录人唯一标识校验
         */
        String userCode = ThreadLocalUserId.get();
        log.info("研判中心-我创建的任务列表-当前登录人的唯一标识{}",userCode);
        if (StringUtils.isBlank(userCode)) {
            throw new ServiceException(JudgeTaskConstants.ServiceError.LOGIN_USER_CODE_ERROR);
        }

        /*
         * 请求参数校验
         * 请求参数处理
         */
        log.info("研判中心-我创建的任务列表-获取请求参数pageNum{}",pageNum);
        log.info("研判中心-我创建的任务列表-获取请求参数pageSize{}",pageSize);
        log.info("研判中心-我创建的任务列表-获取请求参数taskName{}",taskName);
        log.info("研判中心-我创建的任务列表-获取请求参数officeName{}",officeName);
        log.info("研判中心-我创建的任务列表-获取请求参数createTime{}",createTime);
        if (Objects.isNull(pageNum)
                || Objects.isNull(pageSize)
                || pageNum <= JudgeTaskConstants.ServiceParam.PAGE_NUM_AND_PAGE_SIZE_ERROR_PARAM
                || pageSize <= JudgeTaskConstants.ServiceParam.PAGE_NUM_AND_PAGE_SIZE_ERROR_PARAM) {
            throw new ParamException();
        }
        if (StringUtils.isNotBlank(taskName)) {
            taskName = taskName.replace(JudgeTaskConstants.ServiceParam.SPACE_STRING,JudgeTaskConstants.ServiceParam.BLANK_STRING);
            log.info("研判中心-我创建的任务列表-处理请求参数taskName{}",taskName);
        }
        if (StringUtils.isNotBlank(officeName)) {
            officeName = officeName.replace(JudgeTaskConstants.ServiceParam.SPACE_STRING,JudgeTaskConstants.ServiceParam.BLANK_STRING);
            log.info("研判中心-我创建的任务列表-处理请求参数officeName{}",officeName);
        }

        /*
         * 调用封装方法: 根据登录人唯一标识,获取创建任务
         * 将登录人创建任务复制到响应对象中
         * 获取所有用户
         * 获取所有组织
         * 填充任务创建人姓名和创建组织名称
         * 根据用户输入条件筛选任务
         */
        List<JudgeTask> pendList = this.getCreateTask(userCode);
        List<JudgeTaskManagePageDto> judgeTaskManagePageDtoList = null;
        if (!CollectionUtils.isEmpty(pendList)) {
            judgeTaskManagePageDtoList = pendList.stream()
                    .map(judgeTask -> {
                        if (Objects.nonNull(judgeTask)) {
                            JudgeTaskManagePageDto judgeTaskManagePageDto = JudgeTaskManagePageDto.builder().build();
                            judgeTaskManagePageDto.setId(judgeTask.getId());
                            judgeTaskManagePageDto.setTaskName(judgeTask.getTaskName());
                            judgeTaskManagePageDto.setTaskNo(judgeTask.getTaskNo());
                            judgeTaskManagePageDto.setTaskDesciption(judgeTask.getTaskDesciption());
                            judgeTaskManagePageDto.setTaskStatus(judgeTask.getTaskStatus());
                            judgeTaskManagePageDto.setOfficeCode(judgeTask.getCreateUnit());
                            judgeTaskManagePageDto.setUserCode(judgeTask.getCreateUser());
                            judgeTaskManagePageDto.setCreateTime(judgeTask.getCreateTime());
                            return judgeTaskManagePageDto;
                        }
                        return new JudgeTaskManagePageDto();
                    }).collect(Collectors.toList());

            List<JoifUcAccount> allUserList = joifUcService.getAllUsers();
            List<JoifUcGroup> allGroupList = joifUcService.getAllGroups();
            if (!CollectionUtils.isEmpty(judgeTaskManagePageDtoList)) {
                if (!CollectionUtils.isEmpty(allUserList)) {
                    judgeTaskManagePageDtoList = judgeTaskManagePageDtoList.stream()
                            .peek(judgeTaskManagePageDto -> {
                                String dtoUserCode = judgeTaskManagePageDto.getUserCode();
                                for (JoifUcAccount joifUcAccount : allUserList) {
                                    String userId = joifUcAccount.getUser_id();
                                    if (StringUtils.equals(dtoUserCode,userId)) {
                                        judgeTaskManagePageDto.setCreateUser(joifUcAccount.getName());
                                        break;
                                    }
                                }
                            })
                            .collect(Collectors.toList());
                    log.info("研判中心-我创建的任务列表-任务创建人姓名填充完成");
                }
                if (!CollectionUtils.isEmpty(allGroupList)) {
                    judgeTaskManagePageDtoList = judgeTaskManagePageDtoList.stream()
                            .peek(judgeTaskManagePageDto -> {
                                String dtoOfficeCode = judgeTaskManagePageDto.getOfficeCode();
                                for (JoifUcGroup joifUcGroup : allGroupList) {
                                    String groupId = joifUcGroup.getGroup_id();
                                    if (StringUtils.equals(dtoOfficeCode,groupId)) {
                                        judgeTaskManagePageDto.setCreateUnit(joifUcGroup.getGroup_name());
                                        break;
                                    }
                                }
                            })
                            .collect(Collectors.toList());
                    log.info("研判中心-我创建的任务列表-任务创建组织名称填充完成");
                }

                if (StringUtils.isNotBlank(taskName)) {
                    String streamTaskName = taskName;
                    judgeTaskManagePageDtoList = judgeTaskManagePageDtoList.stream()
                            .filter(judgeTaskManagePageDto -> {
                                if (Objects.nonNull(judgeTaskManagePageDto)) {
                                    String dtoTaskName = judgeTaskManagePageDto.getTaskName();
                                    if (StringUtils.isNotBlank(dtoTaskName)) {
                                        return dtoTaskName.contains(streamTaskName);
                                    }
                                }
                                return false;
                            }).collect(Collectors.toList());
                    log.info("研判中心-我创建的任务列表-根据任务名称筛选完成");
                }
                if (StringUtils.isNotBlank(officeName)) {
                    String streamOfficeName = officeName;
                    judgeTaskManagePageDtoList = judgeTaskManagePageDtoList.stream()
                            .filter(judgeTaskManagePageDto -> {
                                if (Objects.nonNull(judgeTaskManagePageDto)) {
                                    String dtoCreateUnit = judgeTaskManagePageDto.getCreateUnit();
                                    if (StringUtils.isNotBlank(dtoCreateUnit)) {
                                        return dtoCreateUnit.contains(streamOfficeName);
                                    }
                                }
                                return false;
                            }).collect(Collectors.toList());
                    log.info("研判中心-我创建的任务列表-根据组织名称筛选完成");
                }
                if (StringUtils.isNotBlank(createTime)) {
                    judgeTaskManagePageDtoList = judgeTaskManagePageDtoList.stream()
                            .filter(judgeTaskManagePageDto -> {
                                if (Objects.nonNull(judgeTaskManagePageDto)) {
                                    Date dtoCreateTime = judgeTaskManagePageDto.getCreateTime();
                                    if (Objects.nonNull(dtoCreateTime)) {
                                        return StringUtils.equals(createTime,SIMPLE_DATE_FORMAT.format(dtoCreateTime));
                                    }
                                }
                                return false;
                            }).collect(Collectors.toList());
                    log.info("研判中心-我创建的任务列表-根据创建时间筛选完成");
                }
            }
        }

        /*
         * 计算总条数
         * 计算总页数
         * 指定数据分页
         */
        PageInfo<JudgeTaskManagePageDto> pageInfo = null;
        if (!CollectionUtils.isEmpty(judgeTaskManagePageDtoList)) {
            Page<JudgeTaskManagePageDto> page = new Page<>(pageNum,pageSize);
            long total = judgeTaskManagePageDtoList.size();
            page.setTotal(total);
            int startIndex = (pageNum - 1) * pageSize;
            int endIndex = (int) Math.min(startIndex + pageSize,total);
            page.addAll(judgeTaskManagePageDtoList.subList(startIndex,endIndex));
            pageInfo = new PageInfo<>(page);
            log.info("研判中心-我创建的任务列表-任务分页完成");
        }

        /*
         * 填充是否是当前登录人创建的任务
         */
        List<JudgeTaskManagePageDto> pageDataList = null;
        if (Objects.nonNull(pageInfo)) {
            pageDataList = pageInfo.getList();
            if (!CollectionUtils.isEmpty(pageDataList)) {
                pageDataList = pageDataList.stream().peek(judgeTaskManagePageDto -> {
                    if (Objects.nonNull(judgeTaskManagePageDto)) {
                        String createUserCode = judgeTaskManagePageDto.getUserCode();
                        if (StringUtils.equals(userCode, createUserCode)) {
                            judgeTaskManagePageDto.setLoginUserType(JudgeTaskConstants.ServiceParam.IS_LOGIN_USER_CREATE_TASK);
                        } else {
                            judgeTaskManagePageDto.setLoginUserType(JudgeTaskConstants.ServiceParam.NOT_LOGIN_USER_CREATE_TASK);
                        }
                    }
                }).collect(Collectors.toList());
            }
            log.info("研判中心-我创建的任务列表-填充是否是当前登录人创建的任务完成");
        }

        /*
         * 调用封装方法: 基于当前登录人,获取进行中,已完成,我创建的任务数
         */
        Map<String, Integer> countMap = this.getProgressAndFinishAndMyCreateTaskCount(userCode);
        Integer progressCount = JudgeTaskConstants.ServiceParam.TASK_DEFAULT_COUNT;
        Integer finishCount = JudgeTaskConstants.ServiceParam.TASK_DEFAULT_COUNT;
        Integer myCreateCount = JudgeTaskConstants.ServiceParam.TASK_DEFAULT_COUNT;
        if (!CollectionUtils.isEmpty(countMap)) {
            progressCount = countMap.get(JudgeTaskConstants.ServiceParam.PROGRESS_COUNT_KEY);
            finishCount = countMap.get(JudgeTaskConstants.ServiceParam.FINISH_COUNT_KEY);
            myCreateCount = countMap.get(JudgeTaskConstants.ServiceParam.MY_CREATE_COUNT);
        }
        log.info("研判中心-我创建的任务列表-当前登录人进行中的任务数{}",progressCount);
        log.info("研判中心-我创建的任务列表-当前登录人已完成的任务数{}",finishCount);
        log.info("研判中心-我创建的任务列表-当前登录人创建的任务数{}",myCreateCount);


        /*
         * 封装响应数据
         * 响应前端数据
         */
        JudgeTaskManagePageVo<JudgeTaskManagePageDto> judgeTaskManagePageVo = new JudgeTaskManagePageVo<>();
        if (Objects.nonNull(pageInfo)) {
            judgeTaskManagePageVo.setPages(pageInfo.getPages());
            judgeTaskManagePageVo.setTotal(pageInfo.getTotal());
            judgeTaskManagePageVo.setData(pageDataList);
            log.info("研判中心-我创建的任务列表-响应任务封装完成");
        }
        judgeTaskManagePageVo.setProgressCount(progressCount);
        judgeTaskManagePageVo.setFinishCount(finishCount);
        judgeTaskManagePageVo.setMyCreateCount(myCreateCount);
        log.info("研判中心-我创建的任务列表-响应数量封装完成");
        return judgeTaskManagePageVo;
    }

    /**
     * 研判中心-研判任务详情
     * @param taskId
     * @return
     */
    public ObjectRestResponse<JudgeTaskDetailDto> selectTaskDetail(Integer taskId) {

        /*
         * 请求参数校验
         */
        if (Objects.isNull(taskId) || taskId <= JudgeTaskConstants.ServiceError.TASK_PRIMARY_KEY_ERROR_PARAM) {
            throw new ParamException();
        }

        /*
        * 获取研判任务详情:
        * 创建单位唯一标识,创建人唯一标识,创建时间,任务名称,任务描述,研判脑图,任务结论
        * 填充单位名称和创建人姓名
        */
        JudgeTaskDetailDto judgeTaskDetailDto = mapper.selectTaskBaseInfo(taskId);
        UserInfo user = userInfoService.getUserInfoByUserCode(judgeTaskDetailDto.getCreateCode());
        if (Objects.nonNull(user)) {
            judgeTaskDetailDto.setCreateUser(user.getName());
        }
        OfficeInfo office = officeInfoService.getOfficeInfoByOfficeCode(judgeTaskDetailDto.getOfficeCode());
        if (Objects.nonNull(office)) {
            judgeTaskDetailDto.setCreateOffice(office.getOfficeName());
        }

        /*
        * 获取研判任务详情:
        * 任务附件
        */
        List<Long> taskAttachmentIdList = judgeTaskAttachmentMapper.selectAttachmentIds(JudgeTaskConstants.ServiceParam.JUDGE_TASK_TABLE_NAME, taskId);
        if (!CollectionUtils.isEmpty(taskAttachmentIdList)) {
            List<AttachmentVo> attachmentVoList = attachmentService.selectByIds(taskAttachmentIdList);
            attachmentVoList = attachmentService.conversionLongAddress(attachmentVoList);
            judgeTaskDetailDto.setTaskAttachment(attachmentVoList);
        }

        /*
         * 获取研判任务详情:
         * 结论附件
         */
        List<Long> conclusionAttachmentIdList = judgeTaskConclusionAttachmentMapper.selectAttachmentIds(taskId);
        if (!CollectionUtils.isEmpty(conclusionAttachmentIdList)) {
            List<AttachmentVo> attachmentVoList = attachmentService.selectByIds(conclusionAttachmentIdList);
            attachmentVoList = attachmentService.conversionLongAddress(attachmentVoList);
            judgeTaskDetailDto.setConclusionAttachment(attachmentVoList);
        }

        /*
         * 获取研判任务详情:
         * 任务协作者唯一标识
         * 从组织用户树结构中获取任务协作者具体信息
         */
        List<String> collaboratorCodeList = judgeTaskCollaboratorMapper.selectTaskCollaboratorCodeList(taskId);
        List<UserVo> taskCollaboratorList = new ArrayList<>();
        ObjectRestResponse<List<OfficeVo.OfficeUserTreeNode>> officeUserTree = (ObjectRestResponse<List<OfficeVo.OfficeUserTreeNode>>) userInfoService.getOfficeUserTree();
        if (Objects.nonNull(officeUserTree)) {
            List<OfficeVo.OfficeUserTreeNode> officeUserTreeNodeList = officeUserTree.getData();
            if (!CollectionUtils.isEmpty(officeUserTreeNodeList)) {
                for (String collaboratorCode : collaboratorCodeList) {
                    if (StringUtils.isNotBlank(collaboratorCode)) {
                        UserVo taskCollaborator =  this.recurrenceGetCollaboratorFromTree(collaboratorCode,officeUserTreeNodeList);
                        taskCollaboratorList.add(taskCollaborator);
                    }
                }
            }
        }
        judgeTaskDetailDto.setTaskCollaborator(taskCollaboratorList);

        /*
        * 获取研判任务详情:
        * 任务所有关联要素(事件)
        * 将警情,案件,人员,车辆的事件标识提取,并分流存储
        * 分别获取警情,案件,人员,车辆详细信息,并分流存储
        * 封装任务关联要素(事件)
        */
        List<JudgeTaskEvent> taskEventList = judgeTaskEventMapper.selectEventByTaskId(taskId);
        List<String> informingCodeList = new ArrayList<>();
        List<String> caseCodeList = new ArrayList<>();
        List<String> manCodeList = new ArrayList<>();
        List<String> vehicleCodeList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(taskEventList)) {
            for (JudgeTaskEvent judgeTaskEvent : taskEventList) {
                if (Objects.nonNull(judgeTaskEvent)) {
                    String JjdEventType = judgeTaskEvent.getEventType();
                    if (StringUtils.equals(JjdEventType,JudgeTaskConstants.ServiceParam.INFORMING_EVENT_TYPE)) {
                        String dbInformingCode = judgeTaskEvent.getEventId();
                        if (StringUtils.isNotBlank(dbInformingCode)) {
                            informingCodeList.add(dbInformingCode);
                        }
                    }
                    if (StringUtils.equals(JjdEventType,JudgeTaskConstants.ServiceParam.CASE_EVENT_TYPE)) {
                        String dbCaseCode = judgeTaskEvent.getEventId();
                        if (StringUtils.isNotBlank(dbCaseCode)) {
                            caseCodeList.add(dbCaseCode);
                        }
                    }
                    if (StringUtils.equals(JjdEventType,JudgeTaskConstants.ServiceParam.MAN_EVENT_TYPE)) {
                        String dbManCode = judgeTaskEvent.getEventId();
                        if (StringUtils.isNotBlank(dbManCode)) {
                            manCodeList.add(dbManCode);
                        }
                    }
                    if (StringUtils.equals(JjdEventType,JudgeTaskConstants.ServiceParam.VEHICLE_EVENT_TYPE)) {
                        String dbVehicleCode = judgeTaskEvent.getEventId();
                        if (StringUtils.isNotBlank(dbVehicleCode)) {
                            vehicleCodeList.add(dbVehicleCode);
                        }
                    }
                }
            }
        }
        List<JjdInfoVo> jjdInfoVoList = new ArrayList<>();
        List<CaseInfoVo> caseInfoVoList = new ArrayList<>();
        List<PersonVo> personVoList = new ArrayList<>();
        List<CarVo> carVoList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(informingCodeList)) {
            for (String informingCode : informingCodeList) {
                JjdInfoVo jjdInfo = apiService.getJjdInfo(informingCode);
                if (Objects.nonNull(jjdInfo)) {
                    jjdInfoVoList.add(jjdInfo);
                }
            }
        }
        if (!CollectionUtils.isEmpty(caseCodeList)) {
            for (String caseCode : caseCodeList) {
                CaseInfoVo caseInfo = apiService.getCaseInfo(caseCode);
                if (Objects.nonNull(caseInfo)) {
                    caseInfoVoList.add(caseInfo);
                }
            }
        }
        if (!CollectionUtils.isEmpty(manCodeList)) {
            for (String manCode : manCodeList) {
                PersonVo personInfo = apiService.getPersonInfo(manCode);
                if (Objects.nonNull(personInfo)) {
                    personVoList.add(personInfo);
                }
            }
        }
        if (!CollectionUtils.isEmpty(vehicleCodeList)) {
            for (String vehicleCode : vehicleCodeList) {
                CarVo carInfo = apiService.getCarInfo(vehicleCode);
                if (Objects.nonNull(carInfo)) {
                    carVoList.add(carInfo);
                }
            }
        }
        Map<String,List<JjdInfoVo>> informingMap = new HashMap<>(JudgeTaskConstants.ServiceParam.TASK_EVENT_MAP_DEFAULT_INIT_VOLUME);
        informingMap.put(JudgeTaskConstants.ServiceParam.INFORMING_EVENT_TYPE,jjdInfoVoList);
        Map<String,List<CaseInfoVo>> caseMap = new HashMap<>(JudgeTaskConstants.ServiceParam.TASK_EVENT_MAP_DEFAULT_INIT_VOLUME);
        caseMap.put(JudgeTaskConstants.ServiceParam.CASE_EVENT_TYPE,caseInfoVoList);
        Map<String,List<PersonVo>> manMap = new HashMap<>(JudgeTaskConstants.ServiceParam.TASK_EVENT_MAP_DEFAULT_INIT_VOLUME);
        manMap.put(JudgeTaskConstants.ServiceParam.MAN_EVENT_TYPE,personVoList);
        Map<String,List<CarVo>> vehicleMap = new HashMap<>(JudgeTaskConstants.ServiceParam.TASK_EVENT_MAP_DEFAULT_INIT_VOLUME);
        vehicleMap.put(JudgeTaskConstants.ServiceParam.VEHICLE_EVENT_TYPE,carVoList);

        judgeTaskDetailDto.setInformingMap(informingMap);
        judgeTaskDetailDto.setCaseMap(caseMap);
        judgeTaskDetailDto.setManMap(manMap);
        judgeTaskDetailDto.setVehicleMap(vehicleMap);

        /*
        * 获取研判任务详情:
        * 获取任务的一级协同
        * 获取任务的二级协同
        * 将二级协同人封装到对应的一级协同人里
        * 封装一级协同人和二级协同人
        */
        List<JudgeTaskCoordinationVo> oneLevelCoordinationList = judgeTaskCoordinationMapper.selectByParentId(JudgeTaskConstants.ServiceParam.FIRST_ORDER_COORDINATION_PARENT_ID,taskId);
        if (!CollectionUtils.isEmpty(oneLevelCoordinationList)) {
            for (JudgeTaskCoordinationVo judgeTaskCoordinationVo : oneLevelCoordinationList) {
                if (Objects.nonNull(judgeTaskCoordinationVo)) {
                    String createUser = judgeTaskCoordinationVo.getCreateUser();
                    if (StringUtils.isNotBlank(createUser)) {
                        UserInfo oneCoordinationUser = userInfoService.getUserInfoByUserCode(createUser);
                        if (Objects.nonNull(oneCoordinationUser)) {
                            judgeTaskCoordinationVo.setCreateUserName(oneCoordinationUser.getName());
                            judgeTaskCoordinationVo.setCopCode(oneCoordinationUser.getCopCode());
                        }
                    }
                    String createUnit = judgeTaskCoordinationVo.getCreateUnit();
                    if (StringUtils.isNotBlank(createUnit)) {
                        OfficeInfo oneCoordinationOffice = officeInfoService.getOfficeInfoByOfficeCode(createUnit);
                        if (Objects.nonNull(oneCoordinationOffice)) {
                            judgeTaskCoordinationVo.setCreateUnitName(oneCoordinationOffice.getOfficeName());
                        }
                    }
                }
            }
            oneLevelCoordinationList = oneLevelCoordinationList.stream()
                    .peek(judgeTaskCoordinationVo -> {
                        if (Objects.nonNull(judgeTaskCoordinationVo)) {
                            Integer oneLevelCoordinationId = judgeTaskCoordinationVo.getId();
                            if (Objects.nonNull(oneLevelCoordinationId)) {
                                List<Long> coordinationAttachmentIdList = judgeTaskAttachmentMapper.selectAttachmentIds(JudgeTaskConstants.ServiceParam.JUDGE_TASK_COORDINATION_TABLE_NAME, oneLevelCoordinationId);
                                if (!CollectionUtils.isEmpty(coordinationAttachmentIdList)) {
                                    List<AttachmentVo> attachmentVoList = attachmentService.selectByIds(coordinationAttachmentIdList);
                                    attachmentVoList = attachmentService.conversionLongAddress(attachmentVoList);
                                    judgeTaskCoordinationVo.setAttachmentVos(attachmentVoList);
                                }
                            }
                        }
                    })
                    .collect(Collectors.toList());
        }

        List<JudgeTaskCoordinationVo> twoLevelCoordinationList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(oneLevelCoordinationList)) {
            List<Integer> oneLevelCoordinationIdList = oneLevelCoordinationList.stream()
                    .map(JudgeTaskCoordinationVo::getId)
                    .collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(oneLevelCoordinationIdList)) {
                twoLevelCoordinationList = judgeTaskCoordinationMapper.selectTwoLevelCoordinationByIds(oneLevelCoordinationIdList,taskId);
            }
        }

        if (!CollectionUtils.isEmpty(twoLevelCoordinationList)) {
            for (JudgeTaskCoordinationVo judgeTaskCoordinationVo : twoLevelCoordinationList) {
                if (Objects.nonNull(judgeTaskCoordinationVo)) {
                    String createUser = judgeTaskCoordinationVo.getCreateUser();
                    if (StringUtils.isNotBlank(createUser)) {
                        UserInfo oneCoordinationUser = userInfoService.getUserInfoByUserCode(createUser);
                        if (Objects.nonNull(oneCoordinationUser)) {
                            judgeTaskCoordinationVo.setCreateUserName(oneCoordinationUser.getName());
                            judgeTaskCoordinationVo.setCopCode(oneCoordinationUser.getCopCode());
                        }
                    }
                    String createUnit = judgeTaskCoordinationVo.getCreateUnit();
                    if (StringUtils.isNotBlank(createUnit)) {
                        OfficeInfo oneCoordinationOffice = officeInfoService.getOfficeInfoByOfficeCode(createUnit);
                        if (Objects.nonNull(oneCoordinationOffice)) {
                            judgeTaskCoordinationVo.setCreateUnitName(oneCoordinationOffice.getOfficeName());
                        }
                    }
                }
            }
            twoLevelCoordinationList = twoLevelCoordinationList.stream()
                    .peek(judgeTaskCoordinationVo -> {
                        if (Objects.nonNull(judgeTaskCoordinationVo)) {
                            Integer twoLevelCoordinationId = judgeTaskCoordinationVo.getId();
                            if (Objects.nonNull(twoLevelCoordinationId)) {
                                List<Long> coordinationAttachmentIdList = judgeTaskAttachmentMapper.selectAttachmentIds(JudgeTaskConstants.ServiceParam.JUDGE_TASK_COORDINATION_TABLE_NAME, twoLevelCoordinationId);
                                if (!CollectionUtils.isEmpty(coordinationAttachmentIdList)) {
                                    List<AttachmentVo> attachmentVoList = attachmentService.selectByIds(coordinationAttachmentIdList);
                                    attachmentVoList = attachmentService.conversionLongAddress(attachmentVoList);
                                    judgeTaskCoordinationVo.setAttachmentVos(attachmentVoList);
                                }
                            }
                        }
                    }).collect(Collectors.toList());
        }

        for (JudgeTaskCoordinationVo oneLevelCoordination : oneLevelCoordinationList) {
            List<JudgeTaskCoordinationVo> twoLevelList = new ArrayList<>();
            for (JudgeTaskCoordinationVo twoLevelCoordination : twoLevelCoordinationList) {
                if (ObjectUtils.equals(twoLevelCoordination.getParentId(),oneLevelCoordination.getId())) {
                    twoLevelList.add(twoLevelCoordination);
                }
            }
            oneLevelCoordination.setJudgeTaskCoordinationTwos(twoLevelList);
        }
        judgeTaskDetailDto.setTaskCoordination(oneLevelCoordinationList);

        /*
         * 封装响应数据
         * 响应前端数据
         */
        ObjectRestResponse<JudgeTaskDetailDto> objectRestResponse = new ObjectRestResponse<>();
        objectRestResponse.setData(judgeTaskDetailDto);
        return objectRestResponse;
    }

    /**
     * 封装方法: 根据登录人唯一标识和任务状态,获取创建和协作的任务
     * @param userCode
     * @return
     */
    private List<JudgeTask> getCreateAndCollaboratorTask(String userCode, String taskStatus) {

        /*
         * 获取登录人创建的待办任务
         * 获取登录人协作的任务id
         * 获取登录人协作的待办任务
         * 登录人创建的待办任务,登录人协作的待办任务,合并
         * 合并后,按创建时间降序排列
         * 返回登录人创建且协作的任务
         */
        List<JudgeTask> createPendList = mapper.selectCreatePendList(userCode,taskStatus);
        List<Integer> collaboratorPendTaskIdList = judgeTaskCollaboratorMapper.selectCollaboratorPendIdList(userCode);
        List<JudgeTask> collaboratorPendList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(collaboratorPendTaskIdList)) {
            collaboratorPendList = mapper.selectCollaboratorPendList(collaboratorPendTaskIdList,taskStatus);
        }
        List<JudgeTask> pendList = new ArrayList<>(createPendList.size() + collaboratorPendList.size());
        pendList.addAll(createPendList);
        pendList.addAll(collaboratorPendList);
        pendList = pendList.stream()
                .sorted(Comparator.comparing(JudgeTask::getCreateTime).reversed())
                .collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(JudgeTask::getId))), ArrayList::new));

        return pendList;
    }

    /**
     * 封装方法: 根据登录人唯一标识,获取创建的任务
     * @param userCode
     * @return
     */
    private List<JudgeTask> getCreateTask(String userCode) {
        return mapper.selectMyCreateTaskByCode(userCode);
    }

    /**
     * 封装方法: 基于当前登录人,获取进行中,已完成,我创建的任务数
     * @param userCode
     * @return
     */
    private Map<String,Integer> getProgressAndFinishAndMyCreateTaskCount(String userCode) {

        /*
        * 创建返回Map
        * 获取当前登录人进行中的任务数
        * 获取当前登录人已完成的任务数
        * 获取当前登录人创建的任务数
        * 返回Map
        */
        Map<String,Integer> countMap = new HashMap<>();
        List<Integer> progressList = mapper.selectProgressTaskCount(userCode);
        if (!CollectionUtils.isEmpty(progressList)) {
            countMap.put(JudgeTaskConstants.ServiceParam.PROGRESS_COUNT_KEY,progressList.size());
        }
        List<Integer> finishList = mapper.selectFinishTaskCount(userCode);
        if (!CollectionUtils.isEmpty(finishList)) {
            countMap.put(JudgeTaskConstants.ServiceParam.FINISH_COUNT_KEY,finishList.size());
        }
        List<Integer> myCreateList = mapper.selectMyCreateTaskCount(userCode);
        if (!CollectionUtils.isEmpty(myCreateList)) {
            countMap.put(JudgeTaskConstants.ServiceParam.MY_CREATE_COUNT,myCreateList.size());
        }
        return countMap;
    }

    /**
     * 封装方法: 递归从树中获取任务协作者
     * @param collaboratorCode
     * @return
     */
    private UserVo recurrenceGetCollaboratorFromTree(String collaboratorCode, List<OfficeVo.OfficeUserTreeNode> officeUserTreeNodeList) {

        /*
        * 遍历当前父节点
        * 获取当前父节点中的用户集合
        * 如果当前父节点中的用户集合有数据,遍历用户集合
        * 获取用户的唯一标识,与协作者唯一标识匹配
        * 匹配成功后提取该用户,并结束外层循环
        * 如果当前父节点中的用户集合没有数据,或者用户集合中没有匹配到要找的用户,获取当前父节点的子节点
        * 如果当前父节点的子节点有数据,调用自身方法,进入子节点,重复以上操作
        * 找到任务协作者后层层结束外层循环,并将任务协作者层层返回
        * 没有找到找到任务协作者,返回null
        */
        UserVo taskCollaborator = null;
        outer: for (OfficeVo.OfficeUserTreeNode officeUserTreeNode : officeUserTreeNodeList) {
            List<UserVo> userVoList = officeUserTreeNode.getUserVos();
            if (!CollectionUtils.isEmpty(userVoList)) {
                for (UserVo userVo : userVoList) {
                    if (Objects.nonNull(userVo)) {
                        String userCode = userVo.getUserCode();
                        if (StringUtils.equals(userCode,collaboratorCode)) {
                            taskCollaborator = userVo;
                            break outer;
                        }
                    }
                }
            }
            List<OfficeVo.OfficeUserTreeNode> childrenOfficeUserTreeNode = officeUserTreeNode.getChildren();
            if (!CollectionUtils.isEmpty(childrenOfficeUserTreeNode)) {
                taskCollaborator = this.recurrenceGetCollaboratorFromTree(collaboratorCode,childrenOfficeUserTreeNode);
            }
        }
        return taskCollaborator;
    }

    public BaseResponse mindOption(int taskId) {
        String mind = redisUtils.get(mindName + ":" + taskId) == null? null: redisUtils.get(mindName + ":" + taskId).toString();
        if (org.springframework.util.StringUtils.hasLength(mind)) {
            if (mind.equals("1")) {
                return new ObjectRestResponse<>().data(false);
            }
            if (mind.equals("0")) {
                redisUtils.save(mindName + ":" + taskId, "1", DateUtil.MILLIS_PER_HOUR);
                return new ObjectRestResponse<>().data(true);
            }
        }
        redisUtils.save(mindName + ":" + taskId, "1", DateUtil.MILLIS_PER_HOUR);
        return new ObjectRestResponse<>().data(true);
    }

}