package com.haizhi.empower.service;

import cn.bdp.joif.entity.JoifUcAccount;
import cn.bdp.joif.entity.JoifUcGroup;
import cn.bdp.joif.service.JoifUcService;
import com.haizhi.empower.base.resp.ObjectRestResponse;
import com.haizhi.empower.config.MinioConfig;
import com.haizhi.empower.entity.dto.JudgeTaskReportDto;
import com.haizhi.empower.entity.po.JudgeTask;
import com.haizhi.empower.entity.po.JudgeTaskCollaborator;
import com.haizhi.empower.entity.po.JudgeTaskCoordination;
import com.haizhi.empower.entity.po.JudgeTaskEvent;
import com.haizhi.empower.entity.vo.*;
import com.haizhi.empower.mapper.JudgeTaskAttachmentMapper;
import com.haizhi.empower.mapper.JudgeTaskConclusionAttachmentMapper;
import com.haizhi.empower.mapper.JudgeTaskEventMapper;
import com.haizhi.empower.util.ExportData;
import com.haizhi.empower.util.FileUtils;
import com.haizhi.empower.util.SoMap;
import com.haizhi.empower.util.WordUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.haizhi.empower.service.ApiService.*;

/**
 * @author by fuhanchao
 * @date 2023/4/5.
 */
@Service
@Slf4j
public class JudgeTaskReportService {

    @Autowired
    JudgeTaskService judgeTaskService;
    @Autowired
    JoifUcService joifUcService;

    @Autowired
    JudgeTaskCoordinationService judgeTaskCoordinationService;

    @Autowired
    JudgeTaskCollaboratorService judgeTaskCollaboratorService;

    @Autowired
    JudgeTaskAttachmentMapper judgeTaskAttachmentMapper;

    @Autowired
    JudgeTaskConclusionAttachmentMapper judgeTaskConclusionAttachmentMapper;

    @Autowired
    AttachmentService attachmentService;

    @Autowired
    MinioService minioService;

    @Autowired
    MinioConfig minioConfig;

    @Autowired
    JudgeTaskEventMapper judgeTaskEventMapper;

    @Autowired
    ApiService apiService;

    public ResponseEntity report(MultipartFile file, Integer id, HttpServletResponse response) throws Exception {
        List<JudgeTaskReportDto> reportDtoList = new ArrayList<>();
        byte[] wordData = createReport(id, file);

        JudgeTaskReportDto dto = new JudgeTaskReportDto();
        dto.setFileName("研判报告.docx");
        dto.setByteDataArr(wordData);
        reportDtoList.addAll(getAttachment(id));
        reportDtoList.add(dto);

        // FIXME 文件名最好为英文，中文的话下载的文件名会乱码，暂时未解决
        String fileName = "研判附件.zip";
        response.reset();
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes(StandardCharsets.UTF_8), "ISO8859-1"));
        byte[] dataByteArr = new byte[0];
        if (CollectionUtils.isNotEmpty(reportDtoList)) {
            try {
                dataByteArr = zipFile(reportDtoList);
                response.getOutputStream().write(dataByteArr);
                response.flushBuffer();
            } catch (Exception e) {
                log.error("压缩zip数据出现异常", e);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
        }
        return new ResponseEntity(dataByteArr, HttpStatus.OK);
    }


    public byte[] createReport(Integer id, MultipartFile file) {
        JudgeTaskReportVo judgeTaskReportVo = getTaskInfo(id);
        ExportData evaluation = WordUtil.createExportData("export/template.docx");
        evaluation.setData("model", judgeTaskReportVo);
        evaluation.setData("collaboration", judgeTaskReportVo.getTaskCollaboration());
        Map<String, List<SoMap>> eventInfo = getEvent(id);
        evaluation.setTable("person", eventInfo.get(EVENT_TYPE_MAN));
        evaluation.setTable("case", eventInfo.get(EVENT_TYPE_VEHICLE));
        evaluation.setTable("jjd", eventInfo.get(EVENT_TYPE_INFORMING));
        evaluation.setTable("car", eventInfo.get(EVENT_TYPE_CASE));

        try {
            if (file != null)
                evaluation.setImg("img", file.getInputStream());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        // 获取新生成的文件流
        byte[] data = evaluation.getByteArr();
        return data;
    }

    public JudgeTaskReportVo getTaskInfo(Integer id) {
        List<JoifUcAccount> userList = joifUcService.getAllUsers();
        List<JoifUcGroup> groupList = joifUcService.getAllGroups();
        JudgeTask task = judgeTaskService.selectById(id);

        JudgeTaskReportVo judgeTaskReportVo = new JudgeTaskReportVo();
        judgeTaskReportVo.setTaskName(task.getTaskName());
        judgeTaskReportVo.setTaskDescription(task.getTaskDesciption());
        judgeTaskReportVo.setTaskConclusion(task.getConclusion());
        judgeTaskReportVo.setCreateTime(task.getCreateTime());

        JoifUcAccount userInfo = userList.stream().filter(user -> user.getUser_id().equals(task.getCreateUser())).findFirst().orElse(null);
        judgeTaskReportVo.setCreateName(userInfo.getName() + "-" + userInfo.getPolice_id());

        JoifUcGroup groupInfo = groupList.stream().filter(group -> group.getGroup_id().equals(task.getCreateUnit())).findFirst().orElse(new JoifUcGroup());
        judgeTaskReportVo.setCreateUnit(groupInfo.getGroup_name());

        judgeTaskReportVo.setTaskCollaboration(getCollaboration(id, userList, groupList));

        List<JudgeTaskCollaborator> taskCollaboratorList = judgeTaskCollaboratorService.selectList(new JudgeTaskCollaborator().builder().taskId(id).build());
        if (CollectionUtils.isNotEmpty(taskCollaboratorList)) {
            List<String> collaborators = new ArrayList<>();
            taskCollaboratorList.forEach(taskCollaborator -> {
                JoifUcAccount userCollaboratorInfo = userList.stream().filter(user -> user.getUser_id().equals(taskCollaborator.getCollaborator())).findFirst().orElse(new JoifUcAccount());
                collaborators.add(userCollaboratorInfo.getName() + "-" + userCollaboratorInfo.getPolice_id());
            });
            judgeTaskReportVo.setTaskCollaborators(collaborators.stream().collect(Collectors.joining(",")));
        } else {
            judgeTaskReportVo.setTaskCollaborators("   ");
        }
        return judgeTaskReportVo;
    }

    public List<JudgeTaskReportVo.Collaboration> getCollaboration(Integer id, List<JoifUcAccount> userList, List<JoifUcGroup> groupList) {
        List<JudgeTaskReportVo.Collaboration> collaborationList = new ArrayList<>();
        List<JudgeTaskCoordination> coordinationList = judgeTaskCoordinationService.selectList(new JudgeTaskCoordination().builder().taskId(id).build());

        if (CollectionUtils.isNotEmpty(coordinationList)) {
            coordinationList.stream()
                    .filter(coordination -> coordination.getParentId() == 0)
                    .forEach(s -> {
                        JudgeTaskReportVo.Collaboration collaboration = new JudgeTaskReportVo.Collaboration();
                        JoifUcAccount userInfo = userList.stream().filter(user -> user.getUser_id().equals(s.getCreateUser())).findFirst().orElse(null);
                        collaboration.setCollaborator(userInfo.getName() + "-" + userInfo.getPolice_id());
                        JoifUcGroup groupInfo = groupList.stream().filter(group -> group.getGroup_id().equals(s.getCreateUnit())).findFirst().orElse(null);
                        collaboration.setCollaboratorUnit(groupInfo.getGroup_name());
                        collaboration.setCollaboratorTime(s.getCreateTime());
                        collaboration.setContent(s.getContent());
                        List<JudgeTaskCoordination> coordinationChildList = coordinationList.stream()
                                .filter(coordination -> coordination.getParentId().equals(s.getId())).collect(Collectors.toList());
                        List<JudgeTaskReportVo.Collaboration> collaborationChildList = new ArrayList<>();
                        if (CollectionUtils.isNotEmpty(coordinationChildList)) {
                            coordinationChildList.forEach(child -> {
                                JudgeTaskReportVo.Collaboration collaborationChild = new JudgeTaskReportVo.Collaboration();
                                JoifUcAccount userChildInfo = userList.stream().filter(user -> user.getUser_id().equals(child.getCreateUser())).findFirst().orElse(null);
                                collaborationChild.setCollaborator(userChildInfo.getName() + "-" + userChildInfo.getPolice_id());
                                JoifUcGroup groupChildInfo = groupList.stream().filter(group -> group.getGroup_id().equals(s.getCreateUnit())).findFirst().orElse(null);
                                collaborationChild.setCollaboratorUnit(groupChildInfo.getGroup_name());
                                collaborationChild.setCollaboratorTime(child.getCreateTime());
                                collaborationChild.setContent(child.getContent());
                                collaborationChildList.add(collaborationChild);
                            });
                        }
                        collaboration.setTaskCollaboration(collaborationChildList);
                        collaborationList.add(collaboration);
                    });
        }
        return collaborationList;
    }

    public List<JudgeTaskReportDto> getAttachment(Integer id) {

        List<Long> attachmentIds = new ArrayList<>();
        attachmentIds.addAll(judgeTaskAttachmentMapper.selectAttachmentIds("t_judge_task", id));
        attachmentIds.addAll(judgeTaskConclusionAttachmentMapper.selectAttachmentIds(id));
        List<JudgeTaskReportDto> taskReportDtos = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(attachmentIds)) {
            List<AttachmentVo> attachmentVoList = attachmentService.selectByIds(attachmentIds);
            if (CollectionUtils.isNotEmpty(attachmentVoList)) {

                attachmentVoList.forEach(attachmentVo -> {
                    try {
                        InputStream in = minioService.download(minioConfig.getWebAttachmentBucket(), attachmentVo.getAddress().split("/")[1]);
                        JudgeTaskReportDto judgeTaskReportDto = new JudgeTaskReportDto();
                        judgeTaskReportDto.setFileName(attachmentVo.getName() + "-" + System.currentTimeMillis() + "." + attachmentVo.getExtName());
                        judgeTaskReportDto.setByteDataArr(FileUtils.toByteArray(in));
                        taskReportDtos.add(judgeTaskReportDto);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }
        return taskReportDtos;
    }

    public byte[] zipFile(List<JudgeTaskReportDto> downloadFileDtoList) throws Exception {

        // 将字节写到一个字节输出流里
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream out = new ZipOutputStream(baos)) {
            // 创建zip file in memory
            for (JudgeTaskReportDto downloadFileDto : downloadFileDtoList) {
                ZipEntry entry = new ZipEntry(downloadFileDto.getFileName());
                entry.setSize(downloadFileDto.getByteDataArr().length);
                out.putNextEntry(entry);
                out.write(downloadFileDto.getByteDataArr());
                out.closeEntry();
            }
        } catch (IOException e) {
            log.error("压缩zip数据出现异常", e);
            throw new RuntimeException("压缩zip包出现异常");
        }
        return baos.toByteArray();
    }

    public Map<String, List<SoMap>> getEvent(Integer id) {
        List<JudgeTaskEvent> eventList = judgeTaskEventMapper.selectByTaskId(id);
        Map<String, Object> eventData = new HashMap<>();
        if (CollectionUtils.isNotEmpty(eventList)) {
            Map<String, List<JudgeTaskEvent>> eventMap = eventList.stream().collect(Collectors.groupingBy(JudgeTaskEvent::getEventType));
            eventMap.entrySet().forEach(e -> {
                ObjectRestResponse response = apiService.getEventDetail(e.getValue().stream()
                        .map(JudgeTaskEvent::getEventId)
                        .toArray(String[]::new), e.getKey());
                if (response.getStatus() == 0) {
                    eventData.put(e.getKey(), response.getData());
                }
            });
        }

        return dealEvent(eventData);
    }

    public Map<String, List<SoMap>> dealEvent(Map<String, Object> eventData) {
        Map<String, List<SoMap>> eventResult = new HashMap<>();
        SoMap map = new SoMap();
        if (eventData.containsKey(EVENT_TYPE_INFORMING)) {
            List<SoMap> jjdVoList = new ArrayList<>();
            for (Object jjdVo : (List) eventData.get(EVENT_TYPE_INFORMING)) {
                jjdVoList.add(new SoMap(jjdVo));
            }

            eventResult.put(EVENT_TYPE_INFORMING, jjdVoList);
        } else {
            JjdInfoVo jjdInfo = new JjdInfoVo();
            map = new SoMap(jjdInfo);
            List<SoMap> jjd = new ArrayList<>();
            jjd.add(map);
            eventResult.put(EVENT_TYPE_INFORMING, jjd);
        }
        if (eventData.containsKey(EVENT_TYPE_CASE)) {
            List<SoMap> jjdVoList = new ArrayList<>();
            for (Object jjdVo : (List) eventData.get(EVENT_TYPE_CASE)) {
                jjdVoList.add(new SoMap(jjdVo));
            }
            eventResult.put(EVENT_TYPE_CASE, jjdVoList);
        } else {
            CaseInfoVo caseInfoVo = new CaseInfoVo();
            map = new SoMap(caseInfoVo);
            List<SoMap> caseInfo = new ArrayList<>();
            caseInfo.add(map);
            eventResult.put(EVENT_TYPE_CASE, caseInfo);
        }

        if (eventData.containsKey(EVENT_TYPE_MAN)) {
            List<SoMap> jjdVoList = new ArrayList<>();
            for (Object jjdVo : (List) eventData.get(EVENT_TYPE_MAN)) {
                jjdVoList.add(new SoMap(jjdVo));
            }
            eventResult.put(EVENT_TYPE_MAN, jjdVoList);

        } else {
            PersonVo personVo = new PersonVo();
            map = new SoMap(personVo);
            List<SoMap> personInfo = new ArrayList<>();
            personInfo.add(map);
            eventResult.put(EVENT_TYPE_MAN, personInfo);

        }

        if (eventData.containsKey(EVENT_TYPE_VEHICLE)) {
            List<SoMap> jjdVoList = new ArrayList<>();
            for (Object jjdVo : (List) eventData.get(EVENT_TYPE_VEHICLE)) {
                jjdVoList.add(new SoMap(jjdVo));
            }
            eventResult.put(EVENT_TYPE_VEHICLE, jjdVoList);

        } else {
            CarVo carVo = new CarVo();
            map = new SoMap(carVo);
            List<SoMap> carInfo = new ArrayList<>();
            carInfo.add(map);
            eventResult.put(EVENT_TYPE_VEHICLE, carInfo);

        }

        return eventResult;
    }


}
