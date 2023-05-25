package com.haizhi.empower.service;

import cn.bdp.joif.base.chain.JoifUcSyncChain;
import cn.bdp.joif.base.threadlocal.ThreadLocalToken;
import cn.bdp.joif.base.threadlocal.ThreadLocalUserId;
import cn.bdp.joif.entity.*;
import cn.bdp.joif.service.JoifUcService;
import cn.bdp.joif.utils.okhttp.JoifOkClient;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.haizhi.empower.annotation.OperationLogDetail;
import com.haizhi.empower.base.AppConstants;
import com.haizhi.empower.base.BaseService;
import com.haizhi.empower.base.resp.BaseResponse;
import com.haizhi.empower.base.resp.ObjectRestResponse;
import com.haizhi.empower.entity.bo.UserInfoBo;
import com.haizhi.empower.entity.po.*;
import com.haizhi.empower.entity.vo.OfficeVo;
import com.haizhi.empower.entity.vo.UserInfoVo;
import com.haizhi.empower.entity.vo.UserVo;
import com.haizhi.empower.enums.DeleteStatusEnum;
import com.haizhi.empower.enums.OperationTypeEnum;
import com.haizhi.empower.enums.UserAvailableStatusEnum;
import com.haizhi.empower.exception.RestException;
import com.haizhi.empower.exception.ServiceException;
import com.haizhi.empower.mapper.UserInfoMapper;
import com.haizhi.empower.mapper.UserRoleMapper;
import com.haizhi.empower.util.AccountSecretUtil;
import com.haizhi.empower.util.DateUtil;
import com.haizhi.empower.util.RedisUtils;
import com.haizhi.empower.util.SecretUtil;
import com.haizhi.empower.util.okhttp.EasyOkClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户表
 *
 * @author CristianWindy
 * @email 393371775@qq.com
 * @date 2022-03-29 20:37:48
 */
@Slf4j
@Service
public class UserInfoService extends BaseService<UserInfoMapper, UserInfo> implements JoifUcSyncChain {


    @Value("${info.sysUrl.loginPath}")
    String loginPath;
    @Value("${login.password}")
    String loginPassWord;

    @Value("${joif.uc.ucUrl}")
    String dmcUrl;

    @Value("${login.role:3l}")
    Long defaultRoleId;

    @Autowired
    JoifUcService joifUcService;

    @Value("${domain}")
    String domain;

    @Autowired
    EasyOkClient easyOKClient;

    @Resource
    private JoifOkClient client;
    @Value("${joif.uc.ucUrl:default}")
    private String ucUrl;
    @Value("${joif.uc.ucIp:default}")
    private String ucIp;

    @Autowired
    RedisUtils redisUtils;

    @Value("${tokenName}")
    String tokenName;

    @Value("${appTokenName}")
    String appTokenName;

    @Resource
    private UserRoleMapper userRoleMapper;

    public String getUserNameByCode(String code) {
        UserInfo userInfo = selectOne(UserInfo.builder().userCode(code).dr(DeleteStatusEnum.DEL_NO.getCode()).build());
        return userInfo == null ? "" : userInfo.getName();
    }

    public BaseResponse login(UserInfoBo.UserLoginBo loginBo, HttpServletResponse response) {
        UserInfo userInfo = selectOne(UserInfo.builder().userName(loginBo.userName)
                .dr(DeleteStatusEnum.DEL_NO.getCode()).build());

        if (ObjectUtils.isEmpty(userInfo)) {
            throw new RestException("查无此账户，请检查登录用户名");
        }
        if (userInfo.getStatus().equals(UserAvailableStatusEnum.FREEZE_YES.getCode())) {
            throw new RestException("该账户已被冻结，请联系管理员");
        }

        if (!AccountSecretUtil.checkHashPassword(loginBo.password, userInfo.getPassword())) {
            throw new RestException("用户名错误或密码错误");
        }
        Cookie accessTokenCookie = new Cookie(AppConstants.Key.TOKEN_USERCODE, userInfo.getUserCode());
        accessTokenCookie.setMaxAge(12 * 60 * 60);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setHttpOnly(false);
        response.addCookie(accessTokenCookie);

        Cookie myUserIdCookie = new Cookie(AppConstants.Key.TOKEN_USERCODE_HEADER,
                userInfo.getUserCode());
        myUserIdCookie.setMaxAge(12 * 60 * 60);
        myUserIdCookie.setPath("/");
        myUserIdCookie.setHttpOnly(false);
        response.addCookie(myUserIdCookie);
        return new BaseResponse();
    }

    @OperationLogDetail(detail = "用户登录")
    public BaseResponse appLogin(Map params) {
        if (params != null) {
            if (params.containsKey("username")) {
                params.put("domain", domain);
            } else {
                throw new RuntimeException("账号异常！");

            }
            String accessToken = null;

            try {
                log.info("请求参数【{}】", params);
                JSONObject tokenResult = easyOKClient.w3FormPost(dmcUrl + "/mob/backdoor/login", params, Maps.newHashMap(), JSONObject.class);
                if ("0".equals(tokenResult.getString("status"))) {
                    if (!ObjectUtils.isEmpty(tokenResult.getJSONObject("result"))) {
                        accessToken = tokenResult.getJSONObject("result").getString("access_token");
                        Map<String, Object> userInfoParams = new HashMap<>();
                        userInfoParams.put("access_token", accessToken);
                        //通过 access_token 查询用户信息
                        JSONObject userResult = easyOKClient.w3FormPost(dmcUrl + "/mob/user/info", userInfoParams, Maps.newHashMap(), JSONObject.class);
                        if ("0".equals(userResult.getString("status"))) {
                            JSONObject userInfo = userResult.getJSONObject("result");
                            String userCode = userInfo.getString("user_id");
                            redisUtils.save(appTokenName + ":" + accessToken, userCode, DateUtil.MILLIS_TWO_DAY);
                            String token = tokenName + ":" + accessToken;
                            JSONObject result = tokenResult.getJSONObject("result");
                            Integer roleId = userRoleMapper.selectByUserCode(userCode);
                            result.put("role", roleId);
                            String userOfficeCode = userOfficeService.getUserOffice(userCode);
                            JSONObject userInfoJsonObject = result.getJSONObject("user_info");
                            userInfoJsonObject.put("userOfficeCode", userOfficeCode);
                            UserInfo userInfoObj = mapper.selectByUserCode(userCode);
                            userInfoJsonObject.put("copCode", userInfoObj.getCopCode());
                            redisUtils.save(token, result, DateUtil.MILLIS_TWO_DAY);
                            return new ObjectRestResponse<>().data(result);
                        } else {
                            throw new RestException("获取用户中心用户信息失败");
                        }


                    } else {
                        throw new RestException("获取用户中心token失败");
                    }
                } else {
                    throw new RestException("获取用户中心token失败");

                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new RuntimeException("系统异常！");

        }
    }

    public BaseResponse ssoLogin(HttpServletResponse response) {

        String userId = ThreadLocalUserId.get();
        log.info(">>>>>>>>用户中心获取到的用户ID: " + userId);

        /*UserInfo userInfo = selectOne(UserInfo.builder().userCode(userId)
                .dr(DeleteStatusEnum.DEL_NO.getCode()).build());


        *//*if (ObjectUtils.isEmpty(userInfo)) {
            throw new RestException("查无此账户，请检查登录用户名");
        }
        if (userInfo.getStatus().equals(UserAvailableStatusEnum.FREEZE_YES.getCode())) {
            throw new RestException("该账户已被冻结，请联系管理员");
        }*//*

//        if (!AccountSecretUtil.checkHashPassword(loginBo.password, userInfo.getPassword())) {
//            throw new RestException("用户名错误或密码错误");
//        }
        Cookie accessTokenCookie = new Cookie(AppConstants.Key.TOKEN_USERCODE, userInfo.getUserCode());
        accessTokenCookie.setMaxAge(12 * 60 * 60);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setHttpOnly(false);
        response.addCookie(accessTokenCookie);

        Cookie myUserIdCookie = new Cookie(AppConstants.Key.TOKEN_USERCODE_HEADER,
                userInfo.getUserCode());
        myUserIdCookie.setMaxAge(12 * 60 * 60);
        myUserIdCookie.setPath("/");
        myUserIdCookie.setHttpOnly(false);
        response.addCookie(myUserIdCookie);*/
        return new BaseResponse();
    }


    public BaseResponse changePassword(String userCode, String oldPass, String newPass) {
        if (oldPass.equals(newPass)) {
            throw new RestException("请勿输入与旧密码一致的新密码");
        }

        UserInfo userInfo = selectOne(UserInfo.builder().userCode(userCode)
                .dr(DeleteStatusEnum.DEL_NO.getCode()).build());
        if (userInfo == null || !AccountSecretUtil.checkHashPassword(oldPass, userInfo.getPassword())) {
            throw new RestException("用户名错误或密码错误");
        }
        if (oldPass.trim().equals(newPass.trim())) {
            throw new RestException("请勿输入与旧密码一致的新密码");
        }
        userInfo.setPassword(AccountSecretUtil.genHashPassword(newPass));
        updateById(userInfo);
        return new BaseResponse();
    }

    public void setDefaultOffice(String userCode, String officeCode, Boolean defaultOffice) {
        List<UserOffice> userOffices = userOfficeService.selectList(UserOffice.builder()
                .userCode(userCode)
                .dr(DeleteStatusEnum.DEL_NO.getCode())
                .build());
        if (defaultOffice) {
            userOffices.forEach(data -> {
                if (data.getOfficeCode().equals(officeCode)) {
                    data.setIsDefault(1);
                } else {
                    data.setIsDefault(0);
                }
                data.setUpdateTime(new Date());
                userOfficeService.updateById(data);
            });
        }
    }


    public BaseResponse logout(HttpServletResponse response) {
//        TokenInterceptor tokenHandler = new TokenInterceptor(this);
//        tokenHandler.removeToken(response);
        joifUcService.logout(response);   //调用用户中心登出接口

        return new ObjectRestResponse<>().data(loginPath);
    }

    public List<UserInfo> getUserByOffice(String officeCode, String userName, String name, String copCode,
                                          Integer roleId, Integer status, String keyword) {
        return mapper.getUserByOffice(officeCode, userName, name, copCode, roleId, status, keyword);
    }

    public BaseResponse addUserInfo(UserInfoBo.AddBo addBo) {
        if (ObjectUtils.isEmpty(addBo.getId())) {
            UserInfo query = UserInfo.builder()
                    .userName(addBo.getUserName())
                    .idCard(addBo.getIdCard()).dr(DeleteStatusEnum.DEL_NO.getCode()).build();
            UserInfo userInfo = selectOne(query);
            if (userInfo != null) {
                throw new RestException("身份证号不能重复");
            }
            query.setUserPicture(minioService.getShortAddressFromRequest(addBo.getUserPicture()));
            query.setUserName(addBo.getUserName());
            query.setName(addBo.getName());
            query.setPassword(AccountSecretUtil.genHashPassword(loginPassWord));
            query.setCopCode(addBo.getCopCode());
            query.setPosition(addBo.getPosition());
            query.setSex(addBo.getSex());
            query.setStatus(addBo.getStatus());
            query.setCreateTime(new Date());
            query.setUserCode("u_" + SecretUtil.seqUuid());
            query.setUpdateTime(new Date());
            insert(query);

            addBo.getRoleIds().forEach(data -> {
                userRoleService.insert(UserRole.builder()
                        .createTime(new Date())
                        .updateTime(new Date())
                        .dr(DeleteStatusEnum.DEL_NO.getCode())
                        .roleId(data.longValue())
                        .userCode(query.getUserCode())
                        .build());
            });
            for (int i = 0; i < addBo.getOfficeCodes().size(); i++) {
                UserOffice userOffice = UserOffice.builder()
                        .officeCode(addBo.getOfficeCodes().get(i))
                        .userCode(query.getUserCode())
                        .dr(DeleteStatusEnum.DEL_NO.getCode())
                        .updateTime(new Date())
                        .createTime(new Date())
                        .isDefault(0)
                        .build();
                if (i == 0) {
                    userOffice.setIsDefault(1);
                }
                userOfficeService.insert(userOffice);
            }
        } else {
            UserInfo userInfo = selectOne(UserInfo.builder()
                    .userName(addBo.getUserName())
                    .idCard(addBo.getIdCard()).dr(DeleteStatusEnum.DEL_NO.getCode()).build());
            if (userInfo == null) {
                throw new RestException("这个用户不存在");
            }
            userInfo.setUserPicture(minioService.getShortAddressFromRequest(addBo.getUserPicture()));
            userInfo.setPosition(addBo.getPosition());
            userInfo.setStatus(addBo.getStatus());
            userInfo.setUpdateTime(new Date());
            updateById(userInfo);

            userRoleService.selectList(UserRole.builder().userCode(userInfo.getUserCode()).dr(DeleteStatusEnum.DEL_NO.getCode()).build()).stream().forEach(data -> {
                data.setDr(DeleteStatusEnum.DEL_YES.getCode());
                data.setUpdateTime(new Date());
                userRoleService.updateById(data);
            });

            addBo.getRoleIds().forEach(data -> {
                userRoleService.insert(UserRole.builder()
                        .createTime(new Date())
                        .updateTime(new Date())
                        .dr(DeleteStatusEnum.DEL_NO.getCode())
                        .roleId(data.longValue())
                        .userCode(userInfo.getUserCode())
                        .build());
            });

            userOfficeService.selectList(UserOffice.builder()
                    .userCode(userInfo.getUserCode())
                    .dr(DeleteStatusEnum.DEL_NO.getCode())
                    .build()).stream().forEach(data -> {
                data.setDr(DeleteStatusEnum.DEL_YES.getCode());
                data.setUpdateTime(new Date());
                userOfficeService.updateById(data);
            });
            for (int i = 0; i < addBo.getOfficeCodes().size(); i++) {
                UserOffice userOffice = UserOffice.builder()
                        .officeCode(addBo.getOfficeCodes().get(i))
                        .userCode(userInfo.getUserCode())
                        .dr(DeleteStatusEnum.DEL_NO.getCode())
                        .updateTime(new Date())
                        .createTime(new Date())
                        .isDefault(0)
                        .build();
                if (i == 0) {
                    userOffice.setIsDefault(1);
                }
                userOfficeService.insert(userOffice);
            }
        }
        return new BaseResponse();
    }

    public List<UserInfo> getUserByRole(Long id, String keyword) {
        return mapper.getUserByRole(id, keyword);
    }

    public BaseResponse detail(String userCode) {
        if (StringUtils.isEmpty(userCode)) {
            throw new RestException("用户Code不能为空");
        }
        UserInfo userInfo = selectOne(UserInfo.builder().userCode(userCode).dr(DeleteStatusEnum.DEL_NO.getCode()).build());
        if (userInfo == null) {
            throw new RestException("用户Code为：" + userCode + "的用户不存在");
        }

        DicItem dicItem = dicItemService.selectOne(DicItem.builder()
                .id(userInfo.getPosition().longValue())
                .dr(DeleteStatusEnum.DEL_NO.getCode())
                .build());

        UserInfoVo.UserDetailVo userDetailVo = UserInfoVo.UserDetailVo.builder()
                .id(userInfo.getId())
                .userPicture(minioService.getAccessAddressFromQuery(userInfo.getUserPicture()))
                .position(ObjectUtils.isEmpty(dicItem) ? -1 : dicItem.getId().intValue())
                .name(userInfo.getName())
                .userCode(userCode)
                .copCode(userInfo.getCopCode())
                .sex(userInfo.getSex())
                .idCard(userInfo.getIdCard())
                .userName(userInfo.getUserName())
                .roleVos(roleService.selectRoleVosByUserCode(userCode))
                .officeVos(officeInfoService.getOfficesByUserCode(userCode))
                .status(userInfo.getStatus())
                .build();

        return new ObjectRestResponse<>().data(userDetailVo);
    }

    public BaseResponse changeStatus(String userCode, Integer status) {
        if (StringUtils.isEmpty(userCode)) {
            throw new RestException("用户Code不能为空");
        }
        UserInfo userInfo = selectOne(UserInfo.builder().userCode(userCode).dr(DeleteStatusEnum.DEL_NO.getCode()).build());
        if (userInfo == null) {
            throw new RestException("用户Code为：" + userCode + "的用户不存在");
        }

        userInfo.setStatus(status);
        updateById(userInfo);

        return new BaseResponse();
    }

    public List<UserInfo> getManageRoleUserByOfficeCode(String officeCode) {
        return mapper.getManageRoleUserByOfficeCode(officeCode);
    }

    public BaseResponse resetPassword(String userCode) {
        UserInfo userInfo = selectOne(UserInfo.builder().userCode(userCode).dr(DeleteStatusEnum.DEL_NO.getCode()).build());
        if (userInfo == null) {
            throw new RestException("用户Code为：" + userCode + "的用户不存在");
        }
        userInfo.setPassword(AccountSecretUtil.genHashPassword("123456"));
        userInfo.setUpdateTime(new Date());
        updateById(userInfo);

        return new BaseResponse();
    }

    public BaseResponse deleteUser(String userCode) {
        UserInfo userInfo = selectOne(UserInfo.builder().userCode(userCode).dr(DeleteStatusEnum.DEL_NO.getCode()).build());
        if (userInfo == null) {
            throw new RestException("用户Code为：" + userCode + "的用户不存在");
        }
        userInfo.setDr(DeleteStatusEnum.DEL_YES.getCode());
        userInfo.setUpdateTime(new Date());
        updateById(userInfo);

        return new BaseResponse();
    }

    @Override
    public void sync(JoifUcUserInfo joifUcUserInfo) {

    }

    @Transactional
    public BaseResponse syncAllUser() {
        List<JoifUcAccount> allUsers = joifUcService.getAllUsers();
        log.info("allUsers:" + allUsers.toString());
        allUsers.forEach(user -> {
            List<JoifUcAccount.Group> sub_group_list = user.getSub_group_list();
            String[] subGroups = new String[sub_group_list.size()];
            for (int i = 0; i < sub_group_list.size(); i++) {
                subGroups[i] = sub_group_list.get(i).getGroup_id();
            }
            Set<JoifUcOfficeInfo> groupsFromUc = joifUcService.getGroupsFromUc(subGroups);
            JoifUcOfficeInfo joifUcOfficeInfo = officeInfoService.groupSyn(groupsFromUc);

            UserInfo queryUserInfo = userExists(user.getUser_id());
            UserInfo userInfo = convert2UserInfo(user);
            Example e = new Example(UserOffice.class);
            Example.Criteria criteria = e.createCriteria();
            criteria.andEqualTo("userCode", user.getUser_id());
            criteria.andEqualTo("dr", DeleteStatusEnum.DEL_NO.getCode());
            List<UserOffice> userOfficeList = userOfficeService.selectByExample(e);
            String officeCode;
            if (CollectionUtils.isNotEmpty(userOfficeList)) {
                List<String> officeCodes = userOfficeList.stream().map(UserOffice::getOfficeCode).collect(Collectors.toList());
                officeCode = joifUcOfficeInfo.getOfficeCode();

                if (!officeCodes.contains(officeCode)) {

                    UserOffice userOfficeNew = UserOffice.builder()
                            .userCode(user.getUser_id())
                            .officeCode(officeCode)
                            .isDefault(1)
                            .build();
                    userOfficeService.insertSelective(userOfficeNew);
                }
            } else {
                officeCode = joifUcOfficeInfo.getOfficeCode();
                UserOffice userOfficeNew = UserOffice.builder()
                        .userCode(user.getUser_id())
                        .officeCode(officeCode)
                        .isDefault(1)
                        .build();
                userOfficeService.insertSelective(userOfficeNew);
            }

            if (!ObjectUtils.isEmpty(queryUserInfo)) {
                userInfo.setCopCode(queryUserInfo.getCopCode());
                userInfo.setBackupMobile(queryUserInfo.getBackupMobile());
                userInfo.setPosition(queryUserInfo.getPosition());
                userInfo.setId(queryUserInfo.getId());
                userInfo.setStatus(DeleteStatusEnum.DEL_NO.getCode());
                updateSelectiveById(userInfo);
            } else {
                userInfo.setStatus(DeleteStatusEnum.DEL_NO.getCode());
                userInfo.setDr(DeleteStatusEnum.DEL_NO.getCode());

                insertSelective(userInfo);

                UserRole userRole = UserRole.builder()
                        .userCode(user.getUser_id())
                        .roleId(defaultRoleId).build();
                userRoleService.insertSelective(userRole);
            }
        });
        return new ObjectRestResponse<>().data("同步成功！");
    }

    public UserInfo userExists(String userId) {
        UserInfo query = UserInfo.builder()
                .userCode(userId)
                .status(DeleteStatusEnum.DEL_NO.getCode())
                .build();
        UserInfo userInfo = selectOne(query);
        /*if (ObjectUtils.isEmpty(userInfo)) {
            return -1L;
        }
        return userInfo.getId();*/
        return userInfo;
    }

    private UserInfo convert2UserInfo(JoifUcUserInfo joifUcUserInfo) {
        return UserInfo.builder()
                .userCode(joifUcUserInfo.getUser_id())
                .userName(joifUcUserInfo.getUsername())
                .copCode(joifUcUserInfo.getPolice_id())
                .idCard(joifUcUserInfo.getCard_id())
                .email(joifUcUserInfo.getEmail())
                .mobile(joifUcUserInfo.getMobile())
                .name(joifUcUserInfo.getName())
                .position(-1)
                .sex(joifUcUserInfo.getSex())
                .password(DigestUtils.md5DigestAsHex(loginPassWord.getBytes()))
                .userPicture(joifUcUserInfo.getAvatar_url())
                //.isFrozen(joifUcUserInfo.getIs_frozen())
                .build();
    }

    private UserInfo convert2UserInfo(JoifUcAccount joifUcAccount) {
        return UserInfo.builder()
                .userCode(joifUcAccount.getUser_id())
                .userName(joifUcAccount.getUsername())
                .copCode(joifUcAccount.getPolice_id())
                .idCard(joifUcAccount.getCard_id())
                .email(joifUcAccount.getEmail())
                .mobile(joifUcAccount.getMobile())
                .name(joifUcAccount.getName())
                .position(-1)
                .sex(joifUcAccount.getSex())
                .password(DigestUtils.md5DigestAsHex(loginPassWord.getBytes()))
                //.userPicture(joifUcAccount.getAvatar_url())
                //.isFrozen(joifUcUserInfo.getIs_frozen())
                .build();
    }

    /**
     * 根据userCode查询用户信息
     *
     * @param userCode
     * @return
     */
    public UserInfo getUserInfo(String userCode) {
        try {
            return selectOne(UserInfo.builder().userCode(userCode).dr(DeleteStatusEnum.DEL_NO.getCode()).build());
        } catch (Exception ex) {
            throw new ServiceException("按userCode查询用户信息失败，usercode：" + userCode, ex);
        }
    }

    /**
     * app 根据用户userCode查询信息
     */
    @OperationLogDetail(detail = "app 根据用户userCode查询信息", operationType = OperationTypeEnum.SELECT)
    public BaseResponse appQueryUserInfo(Map params) {
        return new ObjectRestResponse<>().data(getUserInfo(params.get("userCode").toString()));
    }


    /**
     * 获取组织用户树形结构
     * @return
     */
    public BaseResponse getOfficeUserTree(){
        List<JoifUcGroup> groupList = joifUcService.getAllGroups();
        List<JoifUcAccount> ucAccountList = joifUcService.getAllUsers();
        List<OfficeVo.OfficeTreeNode> officeTreeNodes = getOfficeTreeList(groupList);
        List<OfficeVo.OfficeUserTreeNode> officeUserTreeNodes = new ArrayList<>();
        officeTreeNodes.forEach(data -> officeUserTreeNodes.add(generateOfficeUserTreeNode(data,ucAccountList)));
        return new ObjectRestResponse<>().data(officeUserTreeNodes);
    }

    private OfficeVo.OfficeUserTreeNode generateOfficeUserTreeNode(OfficeVo.OfficeTreeNode data,List<JoifUcAccount> allUcAccountList) {
        List<UserVo> userVos = allUcAccountList.stream().filter(joifUcAccount -> {
            List<JoifUcAccount.Group> list = joifUcAccount.getSub_group_list()
                    .stream()
                    .filter(subGroup -> subGroup.getGroup_id().equals(data.getOfficeCode())).collect(Collectors.toList());
            return list.size() > 0;
        }).map(account -> UserVo.builder().userCode(account.getUser_id()).userName(account.getName()).policeId(account.getPolice_id()).build()).collect(Collectors.toList());
        return OfficeVo.OfficeUserTreeNode.builder()
                .officeName(data.getOfficeName())
                .id(data.getId())
                .userVos(userVos)
                .officeCode(data.getOfficeCode())
                .isTemp(data.getIsTemp())
                .level(data.getLevel())
                .children(data.getSubs().stream().map(temp -> generateOfficeUserTreeNode(temp,allUcAccountList)).collect(Collectors.toList()))
                .build();
    }

    /**
     * 根据父组织机构代码搜索下级组织树
     * @param parentCode
     * @param level
     * @return
     */
    private List<OfficeVo.OfficeTreeNode> getOfficeTreeByParentCode(String parentCode, Integer level,List<JoifUcGroup> allGroupList) {
        List<OfficeInfo> officeInfos = allGroupList.stream()
                .filter(group -> group.getParent_id().equals(parentCode) && level.equals(group.getLevel()))
                .map(group -> OfficeInfo.builder()
                        .officeCode(group.getGroup_id())
                        .level(group.getLevel())
                        .officeName(group.getGroup_name())
                        .build())
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(officeInfos)) {
            return officeInfos.stream().map(officeInfo -> OfficeVo.OfficeTreeNode.builder().subs(getOfficeTreeByParentCode(officeInfo.getOfficeCode(), officeInfo.getLevel() + 1,allGroupList))
                    .officeName(officeInfo.getOfficeName())
                    .officeCode(officeInfo.getOfficeCode())
                    .id(officeInfo.getId())
                    .isTemp(officeInfo.getIsTemp())
                    .level(officeInfo.getLevel()).build()).collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    private List<OfficeVo.OfficeTreeNode> getOfficeTreeList(List<JoifUcGroup> allGroupList) {
        List<OfficeInfo> officeInfos = allGroupList.stream()
                .filter(group -> group.getLevel().equals(0))
                .map(group -> OfficeInfo.builder()
                        .officeCode(group.getGroup_id())
                        .level(group.getLevel())
                        .officeName(group.getGroup_name())
                        .build())
                .collect(Collectors.toList());
        return officeInfos.stream().map(group -> OfficeVo.OfficeTreeNode.builder()
                .officeCode(group.getOfficeCode())
                .officeName(group.getOfficeName())
                .isTemp(group.getIsTemp())
                .level(group.getLevel())
                .id(group.getId())
                .subs(getOfficeTreeByParentCode(group.getOfficeCode(), group.getLevel() + 1, allGroupList))
                .build()).collect(Collectors.toList());
    }

    /**
     * 根据用户唯一标识获取用户信息
     * @return
     */
    public UserInfo getUserInfoByUserCode(String userCode) {
        if (userCode == null || userCode.equals("")) {
            return new UserInfo();
        } else {
            //获取所有用户
            List<JoifUcAccount> allUsers = joifUcService.getAllUsers();
            JoifUcAccount joifUcAccount = new JoifUcAccount();
            for (JoifUcAccount obj : allUsers) {
                //判断用户唯一标识是否一致
                if (obj.getUser_id().equals(userCode))
                    joifUcAccount = obj;
            }
            //转换为userInfo
            UserInfo userInfo = convert2UserInfo(joifUcAccount);
            /**
             * 获取用户照片
             */
            Map<String, Object> params = new HashMap<>();
            params.put("user_id", userCode);
            JoifUcApiResult result = callUc("/api/ucenter/user/new/info", params);
            JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(result.getResult()));
            userInfo.setUserPicture(jsonObject.getString("avatar_url"));
            return userInfo;
        }
    }

    /**
     * 调用用户中心接口
     * @param apiUrl
     * @param params
     * @return
     */
    public JoifUcApiResult callUc(String apiUrl, Map<String, Object> params){
        Map<String, Object> headers = Maps.newHashMap();
        headers.put("Cookie", " access_token=" + ThreadLocalToken.get() + ";my_user_id=" + ThreadLocalUserId.get());
        String reqUrl = "default".equals(this.ucIp) ? this.ucUrl : this.ucIp;
        log.info("JoifUcService#callUc url={}; params={}; header={}", new Object[]{reqUrl + apiUrl, params, headers});
        JoifUcApiResult ucApiResult = easyOKClient.get(reqUrl + apiUrl, params, headers, JoifUcApiResult.class);
        return ucApiResult;
    }
}