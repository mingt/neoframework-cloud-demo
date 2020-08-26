
package com.anilallewar.microservices.auth.dao;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.modules.sys.entity.User;
import java.util.List;

/**
 * Created by think on 2017-09-18.
 */
@MyBatisRepository
public interface UserDao extends CrudDao<User> {

    /**
     * 获取单条数据
     *
     * @param id the id
     * @return by student
     */
    public User getByStudent(String id);

    /**
     * 根据登录名称查询用户
     *
     * @param user the user
     * @return by login name simple
     */
    public User getByLoginNameSimple(User user);

    /**
     * 根据Email查询用户
     *
     * @param user the user
     * @return by email simple
     */
    public User getByEmailSimple(User user);

    /**
     * 根据登录名称查询用户
     *
     * @param user the user
     * @return by uid no simple
     */
    public User getByUidSimple(User user);

    /**
     * 根据登录名称查询用户
     *
     * @param user the user
     * @return by mobile simple
     */
    public User getByMobileSimple(User user);

    /**
     * 根据学号匹配用户
     *
     * @param user the user
     * @return by no simple
     */
    User getByNoSimple(User user);

    /**
     * 根据登录名称查询用户
     *
     * @param user the user
     * @return by login name
     */
    public User getByLoginName(User user);

    /**
     * 根据Email查询用户
     *
     * @param user the user
     * @return by email
     */
    public User getByEmail(User user);

    /**
     * 根据登录名称查询用户
     *
     * @param user the user
     * @return by uid no
     */
    public User getByUid(User user);

    /**
     * 根据登录名称查询用户
     *
     * @param user the user
     * @return by mobile
     */
    public User getByMobile(User user);

    /**
     * 通过OfficeId获取用户列表，仅返回用户id和name（树查询用户时用）
     *
     * @param user the user
     * @return list
     */
    public List<User> findUserByOfficeId(User user);

    /**
     * 查询全部用户数目
     *
     * @param user the user
     * @return long
     */
    public long findAllCount(User user);

    /**
     * 更新用户密码
     *
     * @param user the user
     * @return int
     */
    public int updatePasswordById(User user);

    /**
     * 更新登录信息，如：登录IP、登录时间
     *
     * @param user the user
     * @return int
     */
    public int updateLoginInfo(User user);

    /**
     * 删除用户角色关联数据
     *
     * @param user the user
     * @return int
     */
    public int deleteUserRole(User user);

    /**
     * 插入用户角色关联数据
     *
     * @param user the user
     * @return int
     */
    public int insertUserRole(User user);

    /**
     * 更新用户信息
     *
     * @param user the user
     * @return int
     */
    public int updateUserInfo(User user);

    /**
     * 仅仅用于激活账号的更新
     *
     * @param user the user
     * @return int
     */
    int activeAccount(User user);

    // /**
    // * 获取用户角色.
    // *
    // * @param user
    // * @return
    // */
    // List<Role> findUserRoles(User user);
    //
    // /**
    // * 根据用户ID获取推荐码关联信息.
    // *
    // * @param userId
    // * @return
    // */
    // public ReferralCode getReferralCodeUser(String userId);
}
