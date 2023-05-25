package com.haizhi.empower.mapper;

import com.haizhi.empower.entity.po.UserInfo;
import com.haizhi.empower.entity.vo.UserVo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import java.util.List;

/**
 * 用户表
 *
 * @author CristianWindy
 * @email 393371775@qq.com
 * @date 2022-03-29 20:37:48
 */
public interface UserInfoMapper extends Mapper<UserInfo> {


    UserVo selectUserByUserValueAndOfficeValue(@Param("userValue") String userValue,
                                               @Param("userType") Integer userType,
                                               @Param("officeValue") String officeValue,
                                               @Param("officeType") Integer officeType);

    List<UserInfo> getUserByOffice(@Param("officeCode") String officeCode,
                                   @Param("userName") String userName,
                                   @Param("name") String name,
                                   @Param("copCode") String copCode,
                                   @Param("roleId") Integer roleId,
                                   @Param("status") Integer status,
                                   @Param("keyword") String keyword);

    List<UserInfo> getUserByRole(@Param("roleId") Long id, @Param("keyword") String keyword);

    List<UserInfo> getManageRoleUserByOfficeCode(@Param("officeCode") String officeCode);
    /**根据userName查询数据
     * @param userName 用户名称*/
    UserInfo selectByUserName(@Param("userName") String userName);
    /**根据userCode查询数据
     * @param userCode 唯一标识码*/
    UserInfo selectByUserCode(@Param("userCode") String userCode);
    /**根据警号查询
     * @param copCode 警号*/
    UserInfo selectOneByCopCode(@Param("copCode") String copCode);

    /**
     * 根据userInfo参数进行条件查询
     */
    List<UserInfo> selectByCondition(@Param("userInfo") UserInfo userInfo);
}
