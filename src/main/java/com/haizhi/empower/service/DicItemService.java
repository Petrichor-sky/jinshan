package com.haizhi.empower.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.haizhi.empower.base.BaseService;
import com.haizhi.empower.base.resp.BaseResponse;
import com.haizhi.empower.base.resp.ObjectRestResponse;
import com.haizhi.empower.base.resp.TableResultResponse;
import com.haizhi.empower.entity.bo.DicTypeBo;
import com.haizhi.empower.entity.po.DicItem;
import com.haizhi.empower.entity.vo.DicItemVo;
import com.haizhi.empower.entity.vo.SelectDictVo;
import com.haizhi.empower.enums.DeleteStatusEnum;
import com.haizhi.empower.exception.RestException;
import com.haizhi.empower.mapper.DicItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 字典子表
 *
 * @author CristianWindy
 * @email 393371775@qq.com
 * @date 2022-03-30 18:31:34
 */
@Service
public class DicItemService extends BaseService<DicItemMapper, DicItem> {
    @Autowired
    DicTypeService dicTypeService;

    public String getDicItemNameById(Long type) {
        DicItem dicItem = getDicItemById(type);
        if (dicItem == null) {
            return "";
        }
        return dicItem.getName();
    }

    public String getDicItemCodeeById(Long type) {
        DicItem dicItem = getDicItemById(type);
        if (dicItem == null) {
            return "";
        }
        return dicItem.getCode();
    }

    public DicItem getDicItemById(Long id) {
        return selectOne(DicItem.builder()
                .id(id)
                .dr(DeleteStatusEnum.DEL_NO.getCode())
                .build());
    }


    public BaseResponse getDicItemList(String parentCode, String keyword, Integer pageNum, Integer pageSize) {
        Page<DicItemVo> page = PageHelper.startPage(pageNum, pageSize);
        List<DicItemVo> vos = mapper.getDicItemList(parentCode, keyword);
        return new TableResultResponse<>(page.getTotal(), vos);
    }

    public BaseResponse getDictItemByCode(String keyword){

        List<SelectDictVo> vos=mapper.getDictItemByCode(keyword);
        return new ObjectRestResponse<>().data(vos);
    }


    public BaseResponse getDicItemByMainType(String dicTypeName) {
        List<DicItemVo> dicItemVos = selectList(DicItem.builder()
                .parentCode(dicTypeName)
                .dr(DeleteStatusEnum.DEL_NO.getCode())
                .build()).stream().map(data -> {
            return DicItemVo.builder()
                    .itemId(data.getId())
                    .itemName(data.getName())
                    .build();
        }).collect(Collectors.toList());
        return new ObjectRestResponse<>().data(dicItemVos);
    }

    public BaseResponse deleteItem(Integer id) {
        DicItem query = DicItem.builder().id(id.longValue()).dr(DeleteStatusEnum.DEL_NO.getCode()).build();
        DicItem dicItem = selectOne(query);
        if (query != null) {
            if (dicItem.getSysDic() == 1) {
                throw new RestException("系统内置字典不能删除");
            } else {
                dicItem.setDr(DeleteStatusEnum.DEL_YES.getCode());
                updateById(dicItem);
            }
        }
        return new BaseResponse();
    }

    public BaseResponse addDicItem(DicTypeBo.DicItemBo dicItemBo) {
        DicItem query = DicItem.builder()
                .parentCode(dicItemBo.getParentCode())
                .code(dicItemBo.getCode())
                .dr(DeleteStatusEnum.DEL_NO.getCode()).build();
        DicItem dicItem = selectOne(query);
        if (dicItem != null) {
            throw new RestException("已添加的字典子项无需重复添加");
        }

        List<DicItem> dicItems = selectList(DicItem.builder()
                .parentCode(dicItemBo.getParentCode())
                .name(dicItemBo.getName())
                .dr(DeleteStatusEnum.DEL_NO.getCode()).build());
        if (dicItems != null && dicItems.size() > 0) {
            throw new RestException("已添加的字典子项无需重复添加");
        }
        query.setName(dicItemBo.getName());
        query.setCreateTime(new Date());
        query.setUpdateTime(new Date());
        query.setStatus(1);
        query.setDescription(dicItemBo.getDescription());
        query.setSysDic(1);
        insert(query);
        return new BaseResponse();
    }

    public Integer getDicItemIdByName(String district) {
        List<DicItem> dicItems = selectList(DicItem.builder().dr(DeleteStatusEnum.DEL_NO.getCode()).name(district).build());

        return (dicItems != null && dicItems.size() > 0) ? dicItems.get(0).getId().intValue() : -1;
    }

    public BaseResponse batchDelete(DicTypeBo.BatchDeleteBo batchDeleteBo) {
        batchDeleteBo.getIds().forEach(data -> {
            DicItem dicItem = selectOne(DicItem.builder()
                    .parentCode(batchDeleteBo.getParentCode())
                    .id(data.longValue())
                    .dr(DeleteStatusEnum.DEL_NO.getCode())
                    .build());
            if (dicItem != null) {
                dicItem.setDr(DeleteStatusEnum.DEL_YES.getCode());
                updateById(dicItem);
            }
        });

        return new BaseResponse();
    }

    public BaseResponse modified(DicTypeBo.DicItemModifiedBo dicItemModifiedBo) {
        DicItem dicItem = selectOne(DicItem.builder()
                .dr(DeleteStatusEnum.DEL_NO.getCode())
                .id(dicItemModifiedBo.getId().longValue())
                .build());

        if (dicItem == null) {
            throw new RestException("id为：" + dicItemModifiedBo.getId() + "的字典子项不存在");
        }

        if (!StringUtils.isEmpty(dicItemModifiedBo.getName())) {
            List<DicItem> dicItems = selectList(DicItem.builder()
                    .parentCode(dicItem.getParentCode())
                    .name(dicItemModifiedBo.getName())
                    .dr(DeleteStatusEnum.DEL_NO.getCode())
                    .build());
            if (dicItems != null && dicItems.size() > 0) {
                if (dicItems.size() > 1) {
                    throw new RestException("同父级字典主项下已有同名字典");
                } else if (dicItems.size() == 1) {
                    if (!dicItems.get(0).getId().equals(dicItemModifiedBo.getId())) {
                        throw new RestException("同父级字典主项下已有同名字典");
                    }
                }
            }
            dicItem.setName(dicItemModifiedBo.getName());
        }
        if (!StringUtils.isEmpty(dicItemModifiedBo.getDescription()))
            dicItem.setDescription(dicItemModifiedBo.getDescription());
        dicItem.setUpdateTime(new Date());
        dicItem.setStatus(dicItemModifiedBo.getStatus());//0-使用，1-禁用
        updateById(dicItem);

        return new BaseResponse();
    }
}