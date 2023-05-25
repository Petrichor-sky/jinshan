package com.haizhi.empower.service;

import cn.bdp.joif.base.threadlocal.ThreadLocalUserId;
import com.haizhi.empower.base.resp.BaseResponse;
import com.haizhi.empower.base.resp.ObjectRestResponse;
import com.haizhi.empower.entity.bean.msg.MsgResultBean;
import com.haizhi.empower.entity.vo.JudgeTaskMessageVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author by fuhanchao
 * @date 2023/4/5.
 */
@Service
@Slf4j
public class JudgeTaskMessageService {
    @Autowired
    MsgService msgService;

    public static  final  Integer totalSize=10;


    public static  final  Integer pageSize=10;

    public static  final  String SOURCE_NAME="分析研判中心";

    public BaseResponse getJudgeTaskMessage() {

        List<JudgeTaskMessageVo> data=new ArrayList<>();
        String userId= ThreadLocalUserId.get();
        data.addAll(getMessageResult(userId));


        return new ObjectRestResponse<>().data(data);
    }

    public List<JudgeTaskMessageVo> getMessageResult(String userId){
        List<JudgeTaskMessageVo> data=new ArrayList<>();

        MsgResultBean msgResult;
        msgResult=msgService.listMsg(userId,0,1,pageSize);
        int total=msgResult.getTotal();
        try {


            if (total > pageSize) {
                int totalPage = (total + pageSize - 1) / pageSize;   //计算出需要分页的数量
                for (int i = 1; i <= totalPage; i++) {
                    if (data.size() < totalSize) {
                        msgResult = msgService.listMsg(userId, 0, i, pageSize);
                        getMessageData(msgResult).forEach(msgListBean -> {
                            if (data.size() < totalSize) {
                                data.add(msgListBean);
                            } else {
                                throw new RuntimeException("数据量过大");
                            }
                        });
                    } else {
                        break;
                    }
                }

            } else {
                data.addAll(getMessageData(msgResult));
            }
        }catch (Exception e) {
            log.error(e.getMessage());
        }

        return data;
    }

    public List<JudgeTaskMessageVo> getMessageData(MsgResultBean msgResult) {
        List<JudgeTaskMessageVo> data = new ArrayList<>();
        try {
            if (CollectionUtils.isNotEmpty(msgResult.getMsgList())) {
                msgResult.getMsgList().stream().filter(msgListBean -> SOURCE_NAME.equals(msgListBean.getSource_name())).forEach(s -> {
                    JudgeTaskMessageVo judgeTaskMessageVo = new JudgeTaskMessageVo();
                    judgeTaskMessageVo.setId(s.getInfo_id());
                    judgeTaskMessageVo.setType(s.getType_name());
                    judgeTaskMessageVo.setContent(s.getContent());
                    if (data.size() < totalSize) {
                        data.add(judgeTaskMessageVo);
                    } else {
                        throw new RuntimeException("数据量过大");

                    }
                });
            }
        } catch (Exception e) {
        }
        return data;
    }
}
