package com.haizhi.empower.service;

import com.haizhi.empower.base.BaseService;
import com.haizhi.empower.base.resp.BaseResponse;
import com.haizhi.empower.entity.po.JudgeTaskCollaborator;
import com.haizhi.empower.mapper.JudgeTaskCollaboratorMapper;
import org.springframework.stereotype.Service;


/**
 * 研判任务协作者表
 *
 * @author CristianWindy
 * @email 393371775@qq.com
 * @date 2022-03-30 18:31:34
 */
@Service
public class JudgeTaskCollaboratorService extends BaseService<JudgeTaskCollaboratorMapper, JudgeTaskCollaborator> {

    public BaseResponse add() {
        return new BaseResponse();
    }

    public BaseResponse delete() {
        return new BaseResponse();
    }

    public BaseResponse update() {
        return new BaseResponse();
    }

    public BaseResponse list() {
        return new BaseResponse();
    }
}