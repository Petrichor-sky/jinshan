package com.haizhi.empower.service;

import com.haizhi.empower.base.BaseService;
import com.haizhi.empower.base.resp.BaseResponse;
import com.haizhi.empower.base.resp.ObjectRestResponse;
import com.haizhi.empower.entity.bo.DicTypeBo;
import com.haizhi.empower.entity.po.DicType;
import com.haizhi.empower.entity.vo.DicTypeVo;
import com.haizhi.empower.enums.DeleteStatusEnum;
import com.haizhi.empower.exception.RestException;
import com.haizhi.empower.mapper.DicTypeMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * 字典表
 *
 * @author CristianWindy
 * @email 393371775@qq.com
 * @date 2022-03-29 20:37:48
 */
@Service
public class DicTypeService extends BaseService<DicTypeMapper, DicType> {

    public BaseResponse getDicTypes(String keyword) {
        List<DicTypeVo> dicTypeVos = mapper.getDicTypes(keyword);
        return new ObjectRestResponse<>().data(dicTypeVos);
    }

    public BaseResponse addDicType(DicTypeBo dicTypeBo) {
        DicType dicType = DicType.builder()
                .code(dicTypeBo.getCode())
                .dr(DeleteStatusEnum.DEL_NO.getCode())
                .build();
        if (selectOne(dicType) != null) {
            throw new RestException("已添加的主字典项无需再次添加");
        }

        List<DicType> dicTypes = selectList(DicType.builder()
                .name(dicTypeBo.getName())
                .dr(DeleteStatusEnum.DEL_NO.getCode())
                .build());
        if (dicTypes != null && dicTypes.size() > 0) {
            throw new RestException("已添加的主字典项无需再次添加");
        }

        insert(DicType.builder()
                .code(dicTypeBo.getCode())
                .dr(DeleteStatusEnum.DEL_NO.getCode())
                .name(dicTypeBo.getName())
                .createTime(new Date())
                .description(dicTypeBo.getDescription())
                .sysDic(1)
                .updateTime(new Date())
                .build());
        return new BaseResponse();
    }

    public BaseResponse deleteDicType(Integer id) {
        DicType build = DicType.builder().id(id.longValue()).dr(DeleteStatusEnum.DEL_NO.getCode()).build();
        DicType dicType = selectOne(build);
        if (dicType != null) {
            if (dicType.getSysDic() == 0) {
                throw new RestException("系统内置字典项不能删除");
            } else {
                dicType.setDr(DeleteStatusEnum.DEL_YES.getCode());
                updateById(dicType);
            }
        }
        return new BaseResponse();
    }

    public BaseResponse detail(Long id) {
        DicType dicType = selectOne(DicType.builder().id(id).dr(DeleteStatusEnum.DEL_NO.getCode()).build());
        if (dicType == null) {
            throw new RestException("Id为:" + id + "的字典主项不存在");
        }

        return new ObjectRestResponse<>().data(DicTypeVo.builder()
                .code(dicType.getCode())
                .description(dicType.getDescription())
                .id(dicType.getId().intValue())
                .name(dicType.getName())
                .sysDic(dicType.getSysDic())
                .build());
    }

    public BaseResponse modified(DicTypeBo.DicItemModifiedBo dicItemModifiedBo) {
        DicType dicType = selectOne(DicType.builder()
                .dr(DeleteStatusEnum.DEL_NO.getCode())
                .id(dicItemModifiedBo.getId().longValue())
                .build());

        if (dicType == null) {
            throw new RestException("id为：" + dicItemModifiedBo.getId() + "的字典主项不存在");
        }

        if (!StringUtils.isEmpty(dicItemModifiedBo.getName())) {
            List<DicType> dicTypes = selectList(DicType.builder()
                    .name(dicItemModifiedBo.getName())
                    .build());
            if (dicTypes != null && dicTypes.size() > 0) {
                if (dicTypes.size() > 1) {
                    throw new RestException("已有同名字典主项");
                } else if (dicTypes.size() == 1) {
                    if (!dicItemModifiedBo.getId().equals(dicTypes.get(0).getId())) {
                        throw new RestException("已有同名字典主项");
                    }
                }
            }
            dicType.setName(dicItemModifiedBo.getName());
        }
        if (!StringUtils.isEmpty(dicItemModifiedBo.getDescription()))
            dicType.setDescription(dicItemModifiedBo.getDescription());
        dicType.setUpdateTime(new Date());
        updateById(dicType);
        return new BaseResponse();
    }
}