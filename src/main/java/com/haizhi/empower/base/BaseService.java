package com.haizhi.empower.base;

import cn.bdp.joif.base.exception.JoifUcException;
import cn.bdp.joif.base.threadlocal.ThreadLocalToken;
import cn.bdp.joif.base.threadlocal.ThreadLocalUserId;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.haizhi.empower.entity.bo.CurrentUserShowBo;
import com.haizhi.empower.entity.dto.UcApiResult;
import com.haizhi.empower.entity.dto.UcUserInfo;
import com.haizhi.empower.entity.vo.RoleVo;
import com.haizhi.empower.service.*;
import com.haizhi.empower.util.okhttp.EasyOkClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import tk.mybatis.mapper.common.Mapper;

import java.util.*;
import java.util.stream.Collectors;

import static cn.bdp.joif.conststant.ConstantPool.UcConsts.UC_SUC_CODE;
import static com.haizhi.empower.base.AppConstants.Uc.DEV_HEADER;
import static com.haizhi.empower.base.AppConstants.Uc.USER_INFO;

/**
 * 持久层基于mybatis的业务代码框架
 *
 * @author CristianWindy
 * @date: 2020年02月12日21:04:33
 */
@Slf4j
public abstract class BaseService<M extends Mapper<T>, T> {

    @Value("${minio.accessAddressPrefix}")
    private String accessAddressPrefix;

    @Value("${joif.uc.ucUrl}")
    private String ucHost;

    @Value("${ucenter.haizhiToken}")
    private String haizhiToken;

    @Autowired
    public M mapper;

    @Autowired
    public UserInfoService userInfoService;

    @Autowired
    public OfficeInfoService officeInfoService;

    @Autowired
    public UserRoleService userRoleService;

    @Autowired
    public UserOfficeService userOfficeService;

    @Autowired
    public RoleService roleService;

    @Autowired
    public PermissionService permissionService;

    @Autowired
    public DicItemService dicItemService;

    @Autowired
    public RolePermissionService rolePermissionService;

    @Autowired
    public MinioService minioService;

    @Autowired
    private EasyOkClient easyOkClient;

    public void setMapper(M mapper) {
        this.mapper = mapper;
    }

    /**
     * 根据实体主键查询
     *
     * @param entity 表对应实体
     * @return
     */
    public T selectOne(T entity) {
        return mapper.selectOne(entity);
    }

    /**
     * 根据主键获取实体
     *
     * @param id
     * @return
     */
    public T selectById(Object id) {
        return mapper.selectByPrimaryKey(id);
    }

    /**
     * @param entity
     * @return
     */
    public List<T> selectList(T entity) {
        return mapper.select(entity);
    }

    public List<T> selectListAll() {
        return mapper.selectAll();
    }

    public Long selectCount(T entity) {
        return new Long(mapper.selectCount(entity));
    }

    public int insert(T entity) {
        return mapper.insert(entity);
    }

    public int insertSelective(T entity) {
        return mapper.insertSelective(entity);
    }

    public void delete(T entity) {
        mapper.delete(entity);
    }

    public void deleteById(Object id) {
        mapper.deleteByPrimaryKey(id);
    }

    public int updateById(T entity) {
        return mapper.updateByPrimaryKey(entity);
    }

    public int updateSelectiveById(T entity) {
        return mapper.updateByPrimaryKeySelective(entity);
    }

    public List<T> selectByExample(Object example) {
        return mapper.selectByExample(example);
    }

    public int selectCountByExample(Object example) {
        return mapper.selectCountByExample(example);
    }

    public CurrentUserShowBo getCurrentUser() {
        CurrentUserShowBo currentUserShowBo = new CurrentUserShowBo();
        String userCode = ThreadLocalUserId.get();
        log.info("获取用户信息从用户中心获取到的id======{}", userCode);

        Map<String,Object> params = new HashMap<>();
        params.put("user_id",userCode);
        // 请求用户中心获取用户接口
        log.info("从用户中心获取用户信息详情..............");
        UcApiResult ucApiResult = callPostUc(
                USER_INFO,
                params,
                false)
                .orElseThrow(() -> new JoifUcException("查询用户信息异常！"));
        if (!UC_SUC_CODE.equals(ucApiResult.getStatus())) {
            throw new JoifUcException("查询用户信息异常-" + ucApiResult.getErrstr());
        }
        List<UcUserInfo> ucUserList = JSON.parseArray(JSON.toJSONString(ucApiResult.getResult()), UcUserInfo.class);
        UcUserInfo ucUserInfo = ucUserList.get(0);
        log.info("用户信息详情================>{}",ucUserInfo.toString());

        log.info("从用户中心获取用户角色信息..............");
        UcApiResult result = callUc(
                "/api/ucenter/user/new/info",
                params,
                true)
                .orElseThrow(() -> new JoifUcException("查询用户角色信息异常！"));
        // 2. 验证是否请求成功
        if (!UC_SUC_CODE.equals(result.getStatus())) {
            throw new JoifUcException("查询用户角色信息异常-" + ucApiResult.getErrstr());
        }
        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(result.getResult()));
        List<String> roleList = JSON.parseArray(JSON.toJSONString(jsonObject.getJSONArray("role_ids")), String.class);
        List<RoleVo> roleVos = roleList.stream().map(str -> RoleVo.builder().roleId(str).build()).collect(Collectors.toList());
        currentUserShowBo = CurrentUserShowBo.builder()
                .userPicture(jsonObject.getString("avatar_url"))
                .idCard(ucUserInfo.getCard_id())
                .policeId(ucUserInfo.getUsername())
                .sex(ucUserInfo.getSex())
                .userCode(userCode)
                .status(jsonObject.getIntValue("is_frozen") == 0 ? "使用中" : "已冻结")
                .userName(ucUserInfo.getUsername())
                .name(ucUserInfo.getName())
                .officeVos(ucUserInfo.getGroup_list().stream()
                        .map(group -> CurrentUserShowBo.OfficeVo.builder()
                                .officeCode(group.getGroup_id())
                                .officeName(group.getGroup_name())
                                .build()).collect(Collectors.toList()))
                .roleVos(roleVos)
                .permissionTreeVos(roleVos.size() > 0 ? permissionService.getPermissionTreeVosByRoleVos(roleVos, null) : new ArrayList<>())
                .preAddress(accessAddressPrefix)
                .build();
        return currentUserShowBo;
    }


    /**
     * get请求调用用户中心开放接口
     *
     * @param apiUrl
     * @param params
     * @return
     */
    public Optional<UcApiResult> callUc(String apiUrl, Map<String, Object> params,boolean takeCookie) {
        Map<String, Object> headers = Maps.newHashMap();
        if (takeCookie) {
            headers.put("Cookie", " access_token=" + ThreadLocalToken.get() + ";my_user_id=" + ThreadLocalUserId.get());
        }else {
            headers.put(DEV_HEADER, haizhiToken);
        }

        log.info("JoifUcService#callUc url={}; params={}; header={}", ucHost + apiUrl, params, headers);
        UcApiResult ucApiResult = easyOkClient.get(ucHost + apiUrl, params, headers, UcApiResult.class);
        return Optional.ofNullable(ucApiResult);
    }

    /**
     * post请求调用用户中心开放接口
     *
     * @param apiUrl
     * @param params
     * @return
     */
    public Optional<UcApiResult> callPostUc(String apiUrl, Map<String, Object> params, boolean takeCookie) {
        Map<String, Object> header = Maps.newHashMap();
        if (takeCookie) {
            header.put("Cookie", " access_token=" + ThreadLocalToken.get() + ";my_user_id=" + ThreadLocalUserId.get());
        }else {
            header.put(DEV_HEADER, haizhiToken);
        }

        log.info("JoifUcService#callUc url={}; params={}; header={}", ucHost + apiUrl, params, header);
        UcApiResult ucApiResult = easyOkClient.w3FormPost(ucHost + apiUrl, params, header, UcApiResult.class);
        return Optional.ofNullable(ucApiResult);
    }
}
