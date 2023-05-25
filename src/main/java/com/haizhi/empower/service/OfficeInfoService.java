package com.haizhi.empower.service;

import cn.bdp.joif.entity.JoifUcGroup;
import cn.bdp.joif.entity.JoifUcOfficeInfo;
import cn.bdp.joif.service.JoifUcService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.haizhi.empower.base.BaseService;
import com.haizhi.empower.base.resp.BaseResponse;
import com.haizhi.empower.base.resp.ObjectRestResponse;
import com.haizhi.empower.base.resp.TableResultResponse;
import com.haizhi.empower.entity.bo.CurrentUserShowBo;
import com.haizhi.empower.entity.bo.OfficeInfoBo;
import com.haizhi.empower.entity.po.DicItem;
import com.haizhi.empower.entity.po.OfficeInfo;
import com.haizhi.empower.entity.po.UserInfo;
import com.haizhi.empower.entity.po.UserOffice;
import com.haizhi.empower.entity.vo.OfficeVo;
import com.haizhi.empower.entity.vo.UserInfoVo;
import com.haizhi.empower.entity.vo.UserVo;
import com.haizhi.empower.enums.DeleteStatusEnum;
import com.haizhi.empower.exception.RestException;
import com.haizhi.empower.mapper.OfficeInfoMapper;
import com.haizhi.empower.util.DateFormatUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * 组织信息表
 *
 * @author CristianWindy
 * @email 393371775@qq.com
 * @date 2022-03-29 20:37:48
 */
@Service
@Slf4j
public class OfficeInfoService extends BaseService<OfficeInfoMapper, OfficeInfo> {

    @Autowired
    DicItemService dicItemService;

    @Autowired
    private JoifUcService joifUcService;

    public String getOfficeNameById(String code) {

        OfficeInfo officeInfo = selectOne(OfficeInfo.builder()
                .dr(DeleteStatusEnum.DEL_NO.getCode())
                .officeCode(code)
                .build());
        return officeInfo == null ? "" : officeInfo.getOfficeName();
    }

    public List<CurrentUserShowBo.OfficeVo> getUserOfficesByUserId(String userId) {

        List<CurrentUserShowBo.OfficeVo> offices = mapper.getOffices(userId);

        return offices;
    }

    /**
     * @param officeType
     * @param officeValue officeType为0时officeValue是组织机构Code，为1时officeValue是派出所名称  精确查询  智能匹配
     * @return
     */
    public OfficeVo selectOfficeByOfficeValue(Integer officeType, String officeValue) {
        //OfficeVo officeVo = null;
       /* if (useExtra) {
            officeVo = sysGroupService.selectOfficeByOfficeValue(officeType, officeValue);
        } else {*/
        OfficeVo officeVo = mapper.selectOfficeByOfficeValue(officeType, officeValue);
        //}
        return officeVo;
    }

    /**
     * @param officeType
     * @param officeValue officeType为0时officeValue是组织机构Code，为1时officeValue是派出所名称  模糊查询  智能匹配
     * @return
     */
    public List<OfficeVo> selectOfficesByOfficeValue(Integer officeType, String officeValue) {

        List<OfficeVo> officeVos = mapper.selectOfficesByOfficeValue(officeType, officeValue);

        return officeVos;
    }

    public BaseResponse getPermissionOffices(String parentCode) {
        OfficeVo.OfficeTreeNode officeTreeNode;

        OfficeInfo officeInfo = selectOne(OfficeInfo.builder().dr(DeleteStatusEnum.DEL_NO.getCode())
                .officeCode(parentCode).build());
        if (officeInfo != null) {
            officeTreeNode = OfficeVo.OfficeTreeNode.builder()
                    .id(officeInfo.getId())
                    .officeCode(officeInfo.getOfficeCode())
                    .officeName(officeInfo.getOfficeName())
                    .level(officeInfo.getLevel())
                    .subs(selectOfficeTreeByParentCode(parentCode, officeInfo.getLevel() + 1))
                    .build();
        } else {
            officeTreeNode = OfficeVo.OfficeTreeNode.builder().build();
        }
        //}
        return new ObjectRestResponse<>().data(officeTreeNode);
    }

    /**
     * 全量同步组织信息
     */
    public void syncAllGroups() {
        List<JoifUcGroup> allGroups = null;
        try {
            allGroups = joifUcService.getAllGroups();
        } catch (Exception e) {
            throw new RuntimeException("从用户中心获取全部组织机构数据失败", e);
        }
        if (!CollectionUtils.isEmpty(allGroups)) {
            allGroups.forEach(group -> {
                OfficeInfo officeInfo = getOfficeInfo(group.getGroup_id());
                if (ObjectUtils.isEmpty(officeInfo)) {
                    officeInfo = new OfficeInfo();
                    officeInfo.setDr(DeleteStatusEnum.DEL_NO.getCode());
                    officeInfo.setOfficeCode(group.getGroup_id());
                    officeInfo.setOrgCode(group.getOrg_code());
                    officeInfo.setSuperiorOfficeCode(group.getParent_id());
                    officeInfo.setOfficeName(group.getGroup_name());
                    officeInfo.setLevel(group.getLevel());
                    insertSelective(officeInfo);
                } else {
                    //更新组织机构
                    if (!ObjectUtils.nullSafeEquals(officeInfo.getSuperiorOfficeCode(), group.getParent_id())
                            || !ObjectUtils.nullSafeEquals(officeInfo.getOfficeName(), group.getGroup_name())
                            || !ObjectUtils.nullSafeEquals(officeInfo.getLevel(), group.getLevel())) {
                        officeInfo.setOfficeName(group.getGroup_name());
                        officeInfo.setOrgCode(group.getOrg_code());
                        officeInfo.setLevel(group.getLevel());
                        officeInfo.setSuperiorOfficeCode(group.getParent_id());
                        updateSelectiveById(officeInfo);
                    }
                }
            });
        }
    }

    /**
     * 根据父组织机构代码搜索下级组织树
     *
     * @param parentCode
     * @param level
     * @return
     */
    public List<OfficeVo.OfficeTreeNode> selectOfficeTreeByParentCode(String parentCode, Integer level) {
        List<OfficeInfo> officeInfos = selectList(OfficeInfo.builder().superiorOfficeCode(parentCode).level(level).dr(DeleteStatusEnum.DEL_NO.getCode()).build());
        if (!CollectionUtils.isEmpty(officeInfos)) {
            return officeInfos.stream().map(officeInfo -> {
                return OfficeVo.OfficeTreeNode.builder().subs(selectOfficeTreeByParentCode(officeInfo.getOfficeCode(), officeInfo.getLevel() + 1))
                        .officeName(officeInfo.getOfficeName())
                        .officeCode(officeInfo.getOfficeCode())
                        .id(officeInfo.getId())
                        .isTemp(officeInfo.getIsTemp())
                        .level(officeInfo.getLevel()).build();
            }).collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    public BaseResponse getOfficeTree(String keyword) {
        List<OfficeVo.OfficeTreeNode> officeTreeNode = getOfficeTreeList(keyword);
        return new ObjectRestResponse<>().data(officeTreeNode);
    }

    public BaseResponse getOfficeUserTree(String keyword) {
        List<OfficeVo.OfficeUserTreeNode> officeUserTreeNodes = new ArrayList<>();

        if (StringUtils.isEmpty(keyword)) {
            List<OfficeVo.OfficeTreeNode> officeTreeNodes = getOfficeTreeList(keyword);

            officeTreeNodes.stream().forEach(data -> {
                officeUserTreeNodes.add(generateOfficeUserTreeNode(data, keyword));
            });

        } else {
            List<OfficeVo.OfficeTreeNode> officeTreeNodes = new ArrayList<>();
            List<OfficeInfo> temps = mapper.selectUserOfficeFilterByName(keyword);
            generateCompleteTree(officeTreeNodes, temps);

            officeTreeNodes.stream().forEach(data -> {
                officeUserTreeNodes.add(generateOfficeUserTreeNode(data, keyword));
            });
        }
        return new ObjectRestResponse<>().data(officeUserTreeNodes);
    }

    private OfficeVo.OfficeUserTreeNode generateOfficeUserTreeNode(OfficeVo.OfficeTreeNode data, String keyword) {
        List<UserVo> userVos = mapper.getUsersByOfficeCode(data.getOfficeCode(), keyword);
        OfficeVo.OfficeUserTreeNode officeUserTreeNode = OfficeVo.OfficeUserTreeNode.builder()
                .officeName(data.getOfficeName())
                .id(data.getId())
                .userVos(userVos)
                .officeCode(data.getOfficeCode())
                .isTemp(data.getIsTemp())
                .level(data.getLevel())
                .children(data.getSubs().stream().map(temp -> {
                    return generateOfficeUserTreeNode(temp, keyword);
                }).collect(Collectors.toList()))
                .build();
        return officeUserTreeNode;
    }

    public List<OfficeVo.OfficeTreeNode> getOfficeTreeList(String keyWord) {
        List<OfficeVo.OfficeTreeNode> officeTreeNodes = new ArrayList<>();
        if (StringUtils.isEmpty(keyWord)) {
            List<OfficeInfo> officeInfos = selectList(OfficeInfo.builder()
                    .dr(DeleteStatusEnum.DEL_NO.getCode())
                    .level(0)
                    .build());
            officeTreeNodes.addAll(officeInfos.stream().map(group -> {
                return OfficeVo.OfficeTreeNode.builder()
                        .officeCode(group.getOfficeCode())
                        .officeName(group.getOfficeName())
                        .isTemp(group.getIsTemp())
                        .level(group.getLevel())
                        .id(group.getId())
                        .subs(selectOfficeTreeByParentCode(group.getOfficeCode(), group.getLevel() + 1))
                        .build();
            }).collect(Collectors.toList()));
        } else {
            List<OfficeInfo> temps = mapper.selectOfficeFilterByName(keyWord);

            generateCompleteTree(officeTreeNodes, temps);
        }
        return officeTreeNodes;
    }

    private void generateCompleteTree(List<OfficeVo.OfficeTreeNode> officeTreeNodes, List<OfficeInfo> temps) {
        Set<OfficeInfo> set = temps.stream().map(data -> {
            return findParentNodeByChild(data, new ArrayList<>());
        }).flatMap(List::stream).distinct().collect(Collectors.toSet());
        List<OfficeInfo> collect = set.stream().filter(i -> i.getLevel() == 0).distinct().collect(Collectors.toList());
        collect.forEach(set::remove);

        officeTreeNodes.addAll(collect.stream().map(data -> {
            return OfficeVo.OfficeTreeNode.builder().subs(generateOfficeTreeByOffice(data, set))
                    .level(data.getLevel())
                    .id(data.getId())
                    .officeCode(data.getOfficeCode())
                    .officeName(data.getOfficeName())
                    .isTemp(data.getIsTemp())
                    .build();
        }).collect(Collectors.toList()));
    }

    public List<OfficeVo.OfficeTreeNode> generateOfficeTreeByOffice(OfficeInfo officeInfo, Set<OfficeInfo> officeInfos) {
        return officeInfos.stream().filter(data -> (data.getSuperiorOfficeCode().equals(officeInfo.getOfficeCode()))).map(data -> {
            return OfficeVo.OfficeTreeNode.builder()
                    .id(data.getId())
                    .officeCode(data.getOfficeCode())
                    .officeName(data.getOfficeName())
                    .level(data.getLevel())
                    .isTemp(data.getIsTemp())
                    .subs(generateOfficeTreeByOffice(data, officeInfos))
                    .build();
        }).collect(Collectors.toList());
    }

    private List<OfficeInfo> findParentNodeByChild(OfficeInfo temp, List<OfficeInfo> groups) {
        if (!groups.contains(temp)) {
            groups.add(temp);
        }
        if (temp.getSuperiorOfficeCode() == null) {
            return groups;
        }
        OfficeInfo parentGroup = selectOne(OfficeInfo.builder()
                .officeCode(temp.getSuperiorOfficeCode())
                .dr(DeleteStatusEnum.DEL_NO.getCode())
                .build());
        if (parentGroup != null) {
            if (!groups.contains(parentGroup)) {
                groups.add(parentGroup);
            }
            if (parentGroup.getLevel() < temp.getLevel()) {
                return findParentNodeByChild(parentGroup, groups);
            } else {
                return groups;
            }
        } else {
            return groups;
        }
    }

    public BaseResponse addOfficeInfo(OfficeInfoBo officeInfoBo) {
        if (ObjectUtils.isEmpty(officeInfoBo.getId())) {
            OfficeInfo query = OfficeInfo.builder()
                    .officeCode(officeInfoBo.getOfficeCode())
                    .dr(DeleteStatusEnum.DEL_NO.getCode())
                    .build();
            OfficeInfo parentOffice = selectOne(OfficeInfo.builder()
                    .officeCode(officeInfoBo.getParentCode())
                    .dr(DeleteStatusEnum.DEL_NO.getCode()).build());

            if (selectOne(query) == null) {
                query.setOfficeName(officeInfoBo.getOfficeName());
                query.setSuperiorOfficeCode(officeInfoBo.getParentCode());
                query.setIsTemp(officeInfoBo.getIsTemp());
                query.setCreateTime(new Date());
                query.setUpdateTime(new Date());
                query.setSuppression(JSON.toJSONString(officeInfoBo.getSuppression()));
                query.setOfficeType(officeInfoBo.getOfficeType());
                query.setAdministrative(officeInfoBo.getAdministrative());
                query.setLevel(parentOffice == null ? 0 : parentOffice.getLevel() + 1);
                insert(query);
            } else {
                throw new RestException("组织机构代码不能重复");
            }
        } else {
            OfficeInfo officeInfo = selectOne(OfficeInfo.builder()
                    .id(officeInfoBo.getId().longValue())
                    .dr(DeleteStatusEnum.DEL_NO.getCode())
                    .build());

            OfficeInfo parentOffice = selectOne(OfficeInfo.builder()
                    .officeCode(officeInfoBo.getParentCode())
                    .dr(DeleteStatusEnum.DEL_NO.getCode()).build());

            if (officeInfo == null) {
                throw new RestException("Id为：" + officeInfoBo.getId() + "的组织机构为空");
            }

            officeInfo.setOfficeName(officeInfoBo.getOfficeName());
            officeInfo.setSuperiorOfficeCode(officeInfoBo.getParentCode());
            officeInfo.setIsTemp(officeInfoBo.getIsTemp());
            officeInfo.setUpdateTime(new Date());
            officeInfo.setSuppression(JSON.toJSONString(officeInfoBo.getSuppression()));
            officeInfo.setOfficeType(officeInfoBo.getOfficeType());
            officeInfo.setAdministrative(officeInfoBo.getAdministrative());
            officeInfo.setLevel(parentOffice == null ? 0 : parentOffice.getLevel() + 1);
            updateById(officeInfo);
        }

        return new BaseResponse();
    }

    public BaseResponse officeDetail(Long id) {
        OfficeInfo officeInfo = selectOne(OfficeInfo.builder()
                .dr(DeleteStatusEnum.DEL_NO.getCode())
                .id(id)
                .build());

        if (officeInfo == null) {
            throw new RestException("Id为：" + id + "的单位不存在");
        }

        if (officeInfo.getAdministrative() != null) {
            DicItem dicItem1 = dicItemService.selectOne(DicItem.builder()
                    .id(officeInfo.getAdministrative().longValue())
                    .dr(DeleteStatusEnum.DEL_NO.getCode())
                    .build());


            return new ObjectRestResponse<>().data(OfficeVo.OfficeDetailVo.builder()
                    .administrative(dicItem1 == null ? null : OfficeVo.ItemVo.builder()
                            .itemName(dicItem1.getName())
                            .itemId(dicItem1.getId())
                            .build())
                    .id(officeInfo.getId())
                    .isTemp(officeInfo.getIsTemp())
                    .level(officeInfo.getLevel())
                    .officeCode(officeInfo.getOfficeCode())
                    .officeName(officeInfo.getOfficeName())
                    .officeType(officeInfo.getOfficeType())
                    .superiorOfficeCode(officeInfo.getSuperiorOfficeCode())
                    .superiorOfficeName(StringUtils.isEmpty(officeInfo.getSuperiorOfficeCode()) ? "" : getOfficeNameById(officeInfo.getSuperiorOfficeCode()))
                    .suppression(JSON.parseObject(officeInfo.getSuppression(), new TypeReference<ArrayList<Integer>>() {
                            }).stream().map(data -> {
                                DicItem dicItem = dicItemService.selectOne(DicItem.builder()
                                        .id(data.longValue())
                                        .dr(DeleteStatusEnum.DEL_NO.getCode())
                                        .build());
                                if (dicItem == null) {
                                    return null;
                                } else {
                                    return OfficeVo.ItemVo.builder()
                                            .itemId(dicItem.getId())
                                            .itemName(dicItem.getName())
                                            .build();
                                }
                            }).collect(Collectors.toList())
                    )
                    .build());
        } else {
            return new ObjectRestResponse<>().data(OfficeVo.OfficeDetailVo.builder()
                    .id(officeInfo.getId())
                    .isTemp(officeInfo.getIsTemp())
                    .level(officeInfo.getLevel())
                    .officeCode(officeInfo.getOfficeCode())
                    .officeName(officeInfo.getOfficeName())
                    .officeType(officeInfo.getOfficeType())
                    .superiorOfficeCode(officeInfo.getSuperiorOfficeCode())
                    .superiorOfficeName(StringUtils.isEmpty(officeInfo.getSuperiorOfficeCode()) ? "" : getOfficeNameById(officeInfo.getSuperiorOfficeCode()))
                    .suppression(JSON.parseObject(officeInfo.getSuppression(), new TypeReference<ArrayList<Integer>>() {
                            }).stream().map(data -> {
                                DicItem dicItem = dicItemService.selectOne(DicItem.builder()
                                        .id(data.longValue())
                                        .dr(DeleteStatusEnum.DEL_NO.getCode())
                                        .build());
                                if (dicItem == null) {
                                    return null;
                                } else {
                                    return OfficeVo.ItemVo.builder()
                                            .itemId(dicItem.getId())
                                            .itemName(dicItem.getName())
                                            .build();
                                }
                            }).collect(Collectors.toList())
                    )
                    .build());
        }
    }

    public BaseResponse deleteOffice(Long id) {
        OfficeInfo officeInfo = selectOne(OfficeInfo.builder().dr(DeleteStatusEnum.DEL_NO.getCode())
                .id(id)
                .build());

        if (officeInfo != null) {
            List<UserOffice> userOffices = userOfficeService.selectList(UserOffice.builder()
                    .dr(DeleteStatusEnum.DEL_NO.getCode())
                    .officeCode(officeInfo.getOfficeCode())
                    .build());
            if (userOffices != null && userOffices.size() > 0) {
                throw new RestException("当前组织下有用户，不可删除");
            }

            officeInfo.setDr(DeleteStatusEnum.DEL_YES.getCode());
            updateById(officeInfo);
        }

        return new BaseResponse();
    }

    public BaseResponse getOfficeUsers(String officeCode, String userName, String name, String copCode, Integer roleId,
                                       Integer status, String keyword, Integer pageNum, Integer pageSize) {
        Page<UserInfo> page = PageHelper.startPage(pageNum, pageSize);
        List<UserInfo> userInfos = userInfoService.getUserByOffice(officeCode, userName, name, copCode, roleId, status, keyword);
        List<UserInfoVo.UserOfficeVo> vos = userInfos.stream().map(data -> {
            DicItem dicItem = dicItemService.selectOne(DicItem.builder()
                    .id(data.getPosition().longValue())
                    .dr(DeleteStatusEnum.DEL_NO.getCode()).build());

            return UserInfoVo.UserOfficeVo.builder()
                    .userCode(data.getUserCode())
                    .copCode(data.getCopCode())
                    .createTime(DateFormatUtil.formatDate(DateFormatUtil.PATTERN_DEFAULT_ON_SECOND, data.getCreateTime()))
                    .id(data.getId().intValue())
                    .idCard(data.getIdCard())
                    .name(data.getName())
                    .position(dicItem == null ? "" : dicItem.getName())
                    .sex(data.getSex())
                    .status(data.getStatus())
                    .userName(data.getUserName())
                    .roleVos(roleService.selectRoleVosByUserCode(data.getUserCode()))
                    .build();
        }).collect(Collectors.toList());
        return new TableResultResponse<>(page.getTotal(), vos);
    }

    public BaseResponse deleteUsers(OfficeInfoBo.BatchUserBo batchUsersBo) {
        batchUsersBo.getUserCodes().stream().forEach(data -> {
            UserOffice userOffice = userOfficeService.selectOne(UserOffice.builder()
                    .officeCode(batchUsersBo.getOfficeCode())
                    .userCode(data)
                    .dr(DeleteStatusEnum.DEL_NO.getCode())
                    .build());
            if (userOffice != null) {
                userOffice.setDr(DeleteStatusEnum.DEL_YES.getCode());
                userOffice.setUpdateTime(new Date());
                userOfficeService.updateById(userOffice);
            }
        });

        return new BaseResponse();
    }

    public BaseResponse addUsers(OfficeInfoBo.BatchUserBo batchUsersBo) {
        batchUsersBo.getUserCodes().stream().forEach(data -> {
            UserOffice userOffice = userOfficeService.selectOne(UserOffice.builder()
                    .officeCode(batchUsersBo.getOfficeCode())
                    .userCode(data)
                    .dr(DeleteStatusEnum.DEL_NO.getCode())
                    .build());
            if (userOffice == null) {
                userOfficeService.insert(UserOffice.builder()
                        .officeCode(batchUsersBo.getOfficeCode())
                        .userCode(data)
                        .dr(DeleteStatusEnum.DEL_NO.getCode())
                        .isDefault(0)
                        .createTime(new Date())
                        .updateTime(new Date())
                        .build());
            }
        });
        return new BaseResponse();
    }

    public List<OfficeVo> getOfficesByUserCode(String userCode) {
        List<UserOffice> userOffices = userOfficeService.selectList(UserOffice.builder()
                .userCode(userCode)
                .dr(DeleteStatusEnum.DEL_NO.getCode())
                .build());

        return userOffices == null ? new ArrayList<>() : userOffices.stream().map(data -> {
            return OfficeVo.builder().officeCode(data.getOfficeCode())
                    .officeName(getOfficeNameById(data.getOfficeCode()))
                    .build();
        }).distinct().collect(Collectors.toList());
    }

    public List<UserInfoVo.UserShortVo> getManageRoleUserByOfficeCode(String officeCode) {
        List<UserInfo> userVos = userInfoService.getManageRoleUserByOfficeCode(officeCode);

        return userVos == null || userVos.size() == 0 ? new ArrayList<>() : userVos.stream().map(data -> {
            return UserInfoVo.UserShortVo.builder()
                    .userCode(data.getUserCode())
                    .userName(data.getName())
                    .build();
        }).collect(Collectors.toList());
    }

    public OfficeInfo getOfficeInfo(String officeCode) {
        OfficeInfo query = new OfficeInfo();
        query.setDr(DeleteStatusEnum.DEL_NO.getCode());
        query.setOfficeCode(officeCode);
        OfficeInfo officeInfo = null;
        try {
            officeInfo = selectOne(query);
        } catch (Exception e) {
            log.info("数据库查询组织信息异常", e);
        }
        return officeInfo;
    }

    /**
     * 同步组织信息，并返回用户组内组织层级最低的那个（用户所属组织）
     *
     * @param groups
     */
    public JoifUcOfficeInfo groupSyn(Set<JoifUcOfficeInfo> groups) {
        if (CollectionUtils.isEmpty(groups)) {
            return null;
        }
        groups.forEach(group -> {
            //确定组织是否存在
            OfficeInfo info;
            if ((info = getOfficeInfo(group.getOfficeCode())) == null) {
                OfficeInfo officeInfo = new OfficeInfo();
                BeanUtils.copyProperties(group, officeInfo);
                officeInfo.setDr(DeleteStatusEnum.DEL_NO.getCode());
                officeInfo.setIsTemp(1);
                officeInfo.setOrgCode(group.getOrgCode());
                insert(officeInfo);
            } else {//存在的处理逻辑
                if (info.getLevel().equals(group.getLevel())
                        && info.getSuperiorOfficeCode().equals(group.getSuperiorOfficeCode())
                        && info.getOfficeName().equals(group.getOfficeName())) {
                } else {
                    //执行更新
                    info.setOfficeName(group.getOfficeName());
                    info.setSuperiorOfficeCode(group.getSuperiorOfficeCode());
                    info.setLevel(group.getLevel());
                    info.setOrgCode(group.getOrgCode());
                    updateById(info);
                }
            }
        });
        return groups.stream().reduce((a, b) -> a.getLevel() > b.getLevel() ? a : b).orElseThrow(() -> new RuntimeException("没能获取最低组织"));
    }

    /**
     * 根据当前组织机构代码搜索下一级组织树
     */
    public BaseResponse querySubList(String parentCode, Integer level) {
        List<OfficeInfo> officeInfos = null;
        List<OfficeInfoVo> officeInfoVos = new ArrayList<>();
        if (parentCode == null || level == null) {
            officeInfos = selectList(OfficeInfo.builder()
                    .dr(DeleteStatusEnum.DEL_NO.getCode())
                    .level(0)
                    .build());
            officeInfos.forEach(obj -> {
                OfficeInfoVo vo = new OfficeInfoVo();
                BeanUtils.copyProperties(obj, vo);
                if (selectList(OfficeInfo.builder()
                        .superiorOfficeCode(obj.getOfficeCode())
                        .level(obj.getLevel() + 1)
                        .dr(DeleteStatusEnum.DEL_NO.getCode())
                        .build()).size() < 1) {
                    vo.setSubIs(false);
                }
                officeInfoVos.add(vo);
            });
        } else {
            officeInfos = selectList(OfficeInfo.builder()
                    .superiorOfficeCode(parentCode)
                    .level(level + 1)
                    .dr(DeleteStatusEnum.DEL_NO.getCode())
                    .build());
            officeInfos.forEach(obj -> {
                OfficeInfoVo vo = new OfficeInfoVo();
                BeanUtils.copyProperties(obj, vo);
                if (selectList(OfficeInfo.builder()
                        .superiorOfficeCode(obj.getOfficeCode())
                        .level(obj.getLevel() + 1)
                        .dr(DeleteStatusEnum.DEL_NO.getCode())
                        .build()).size() < 1) {
                    vo.setSubIs(false);
                }
                officeInfoVos.add(vo);
            });
        }
        return new ObjectRestResponse<>().data(officeInfoVos);
    }

    @Data
    public static class OfficeInfoVo {
        //分组名称
        private String officeName;
        //分组code
        private String officeCode;
        //父分组code
        private String superiorOfficeCode;
        //组织机构代码
        private String orgCode;
        //是否为临时组织(0:是；1:否)
        private Integer isTemp;
        //分组层级
        private Integer level;
        //行政区划
        private Integer administrative;
        //组织类型
        private Integer officeType;
        //警种
        private String suppression;
        //删除标志(0:未删除,1:删除)
        private Integer dr;
        //更新时间
        private Date updateTime;
        //创建时间
        private Date createTime;

        //是否还有下级单位
        private Boolean subIs = true;
    }

    /**
     * 根据组织单位唯一标识获取组织信息
     *
     * @return
     */
    public OfficeInfo getOfficeInfoByOfficeCode(String officeCode) {
        String[] subGroups = {officeCode};
        Set<JoifUcOfficeInfo> groups = joifUcService.getGroupsFromUc(subGroups);
        JoifUcOfficeInfo joifUcOfficeInfo = new JoifUcOfficeInfo();
        for (JoifUcOfficeInfo obj : groups) {
            if (obj.getOfficeCode().equals(officeCode))
                joifUcOfficeInfo = obj;
        }
        return convert2OfficeInfo(joifUcOfficeInfo);
    }

    public OfficeInfo convert2OfficeInfo(JoifUcOfficeInfo joifUcOfficeInfo) {
        OfficeInfo officeInfo = new OfficeInfo();
        BeanUtils.copyProperties(joifUcOfficeInfo, officeInfo);
        officeInfo.setDr(DeleteStatusEnum.DEL_NO.getCode());
        officeInfo.setIsTemp(1);
        officeInfo.setOrgCode(joifUcOfficeInfo.getOrgCode());
        return officeInfo;
    }
}