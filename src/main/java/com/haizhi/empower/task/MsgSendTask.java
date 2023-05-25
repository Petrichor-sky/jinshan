package com.haizhi.empower.task;

import com.haizhi.empower.service.MsgService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author by fuhanchao
 * @date 2023/3/6.
 */
@Component
@EnableAsync
@Slf4j
public class MsgSendTask {

    @Autowired
    private MsgService msgService;

    /**
     * 消息总线发送进程
     * @param userIdList
     * @param groupIdList
     * @param content
     */
    @Async(value = "ThreadPoolExecutor")
    public void sendMessage(List<String> userIdList, List<String> groupIdList, String content){
       boolean ret= msgService.sendMsg(userIdList,groupIdList,content);
        if (ret) {

        } else {
            log.error("消息中心发送失败");
        }
    }
}
