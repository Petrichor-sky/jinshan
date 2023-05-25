package com.haizhi.empower.service;

import cn.bdp.joif.base.threadlocal.ThreadLocalUserId;
import cn.bdp.joif.entity.JoifUcApiResult;
import com.haizhi.empower.entity.bean.msg.MsgReadBean;
import com.haizhi.empower.entity.bean.msg.MsgResultBean;
import com.haizhi.empower.exception.MsgException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author by fuhanchao
 * @date 2023/3/3.
 */
@Service
@Slf4j
public class MsgService {

    @Value("${msg.address:}")
    private String msgAddress;
    @Value("${msg.token:}")
    private String msgToken;
    @Value("${msg.typeCode:}")
    private String typeCode;


    @Resource(name = "restTemplate1")
     RestTemplate restTemplate;


    
    public boolean sendMsg(List<String> userIdList, List<String> groupIdList, String content) throws MsgException {
        if (CollectionUtils.isEmpty(userIdList) && CollectionUtils.isEmpty(groupIdList)) {
            log.error("消息推送目标不能为空");
            return false;
        }
        if (StringUtils.isEmpty(content)) {
            log.error("消息内容不能为空");
            return false;
        }

        String url = msgAddress + "/api/msg_center/msg/openapi/create";

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-HAIZHI-TOKEN", msgToken);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> forms= new LinkedMultiValueMap<String, Object>();
        forms.add("content", content);
        forms.add("user_ids",userIdList);
        forms.add("group_ids",groupIdList);
        forms.add("type_code",typeCode);
        HttpEntity<MultiValueMap<String, Object>> formEntity = new HttpEntity<>(forms, headers);

        JoifUcApiResult joifUcApiResult;
        try {
            joifUcApiResult=  restTemplate.postForObject(url, formEntity, JoifUcApiResult.class);

        } catch (Exception e) {
            log.error("消息推送失败!");
            return false;
        }
        if ("0".equals(joifUcApiResult.getStatus())) {
            log.info("MsgServiceImpl#sendMsg 消息推送成功！");
            return true;
        } else {
            log.error("MsgServiceImpl#sendMsg 消息推送失败！errstr=>{}", joifUcApiResult.getErrstr());
            return false;
        }
    }

    
    public MsgResultBean listMsg(String userId, Integer status, Integer pageNo, Integer pageSize) {
        String url = msgAddress + "/api/msg_center/msg/openapi/list";
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> body= new LinkedMultiValueMap<String, Object>();
        header.set("X-HAIZHI-TOKEN", msgToken);
        body.add("user_id", userId);
        body.add("status", 0);
        body.add("page_no", pageNo);
        body.add("page_size", pageSize);
        HttpEntity<MultiValueMap<String, Object>> formEntity = new HttpEntity<MultiValueMap<String, Object>>(body, header);

        JoifUcApiResult joifUcApiResult = null;
        try {
            joifUcApiResult=  restTemplate.postForObject(url, formEntity, JoifUcApiResult.class);
        } catch (Exception e) {
//            throw new MsgException("消息列表查询失败!");
            log.error("消息列表查询失败!");
            return null;
        }
        if ("0".equals(joifUcApiResult.getStatus())) {
            log.info("MsgServiceImpl#sendMsg 消息列表查询失败！");
            return (MsgResultBean)joifUcApiResult.getResult();
        } else {
            log.error("MsgServiceImpl#sendMsg 消息列表查询失败！errstr=>{}", joifUcApiResult.getErrstr());
            return null;
        }
    }


    
    public Long countUnRead(String userId) {
        String url = msgAddress + "/api/msg_center/msg/openapi/unread/count";
        HttpHeaders header = new HttpHeaders();
        header.set("X-HAIZHI-TOKEN", msgToken);
        header.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body= new LinkedMultiValueMap<String, Object>();
        body.add("user_id", userId);
        JoifUcApiResult joifUcApiResult = null;
        HttpEntity<MultiValueMap<String, Object>> formEntity = new HttpEntity<MultiValueMap<String, Object>>(body, header);

        try {
            joifUcApiResult=  restTemplate.postForObject(url, formEntity, JoifUcApiResult.class);
        } catch (Exception e) {
//            throw new MsgException("消息列表查询失败!");
            log.error("未读消息数查询失败!");
            return 0L;
        }
        if ("0".equals(joifUcApiResult.getStatus())) {
            log.info("MsgServiceImpl#sendMsg 消息列表查询成功！");
            try {
                return Long.valueOf(String.valueOf(joifUcApiResult.getResult()));
            } catch (Exception e) {
                log.error("未读消息数解析异常!", e);
                return 0L;
            }
        } else {
            log.error("MsgServiceImpl#sendMsg 未读消息数查询失败！errstr=>{}", joifUcApiResult.getErrstr());
            return 0L;
        }
    }

    
    public boolean readMsg(MsgReadBean bean) {
        // 是否已读全部，0 否； 1 是
        int isAll = bean.getIsAll();
        List<String> msgIds = bean.getMsgIds();
        if (isAll == 0 && CollectionUtils.isEmpty(msgIds)) {
            throw new MsgException("未选择消息");
        }

        String url = msgAddress + "/api/msg_center/msg/openapi/read";
        HttpHeaders header = new HttpHeaders();
        header.set("X-HAIZHI-TOKEN", msgToken);
        header.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body= new LinkedMultiValueMap<String, Object>();
        body.add("user_id", ThreadLocalUserId.get());
        body.add("is_all", isAll);
        if (!CollectionUtils.isEmpty(msgIds)) {
            body.add("ids", isAll);
        }
        HttpEntity<MultiValueMap<String, Object>> formEntity = new HttpEntity<MultiValueMap<String, Object>>(body, header);

        JoifUcApiResult joifUcApiResult;
        try {
            joifUcApiResult=  restTemplate.postForObject(url, formEntity, JoifUcApiResult.class);
        } catch (Exception e) {
            log.error("消息已读操作异常!");
            return false;
        }
        if ("0".equals(joifUcApiResult.getStatus())) {
            log.info("MsgServiceImpl#readMsg 消息已读操作成功！");
            return true;
        } else {
            log.error("MsgServiceImpl#sendMsg 消息已读操作失败！errstr=>{}", joifUcApiResult.getErrstr());
            return false;
        }
    }

    
    public Object listMsgType(Integer pageNum, Integer pageSize) {
        String url = msgAddress + "/api/msg_center/msg/openapi/type/list";
        HttpHeaders header = new HttpHeaders();
        header.set("X-HAIZHI-TOKEN", msgToken);
        header.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body= new LinkedMultiValueMap<String, Object>();
        body.add("page_no", pageNum);
        body.add("page_size", pageSize);
        JoifUcApiResult joifUcApiResult;
        HttpEntity<MultiValueMap<String, Object>> formEntity = new HttpEntity<MultiValueMap<String, Object>>(body, header);

        try {
            joifUcApiResult=  restTemplate.postForObject(url, formEntity, JoifUcApiResult.class);
        } catch (Exception e) {
            log.error("消息类型列表查询失败!");
            return null;
        }
        if ("0".equals(joifUcApiResult.getStatus())) {
            log.info("MsgServiceImpl#listMsgType 消息类型列表查询成功！");
            return joifUcApiResult.getResult();
        } else {
            log.error("MsgServiceImpl#listMsgType 消息类型列表查询失败！errstr=>{}", joifUcApiResult.getErrstr());
            return null;
        }
    }
}
