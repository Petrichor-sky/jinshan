package com.haizhi.empower.service;

import com.haizhi.empower.base.BaseService;
import com.haizhi.empower.base.resp.BaseResponse;
import com.haizhi.empower.base.resp.ObjectRestResponse;
import com.haizhi.empower.entity.po.TbApkVersion;
import com.haizhi.empower.mapper.TbApkVersionMapper;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * @author by fuhanchao
 * @date 2022/12/9.
 */
@Service
public class TbApkVersionService extends BaseService<TbApkVersionMapper, TbApkVersion> {

    public BaseResponse getApkInfo(HttpServletRequest request){
        String appName=request.getHeader("appName");
        TbApkVersion tbApkVersion=this.selectOne(new TbApkVersion(appName));
        return new ObjectRestResponse<>().data(tbApkVersion);
    }
}
