
package com.anilallewar.microservices.auth.service.impl;

// import com.monkeyk.sos.domain.dto.UserDto;
// import com.monkeyk.sos.domain.dto.UserFormDto;
// import com.monkeyk.sos.domain.dto.UserVo;
// import com.monkeyk.sos.domain.dto.UserOverviewDto;
// import com.monkeyk.sos.domain.shared.security.CustomUserDetails;
// import com.monkeyk.sos.domain.user.User;
// import com.monkeyk.sos.domain.user.UserRepository;
// import com.monkeyk.sos.service.UserService;

import com.anilallewar.microservices.auth.config.OAuthWebFormConfiguration;
import com.anilallewar.microservices.auth.dao.UserDao;
import com.anilallewar.microservices.auth.service.UserService;
import com.neoframework.biz.api.common.vo.UserVo;
import com.neoframework.common.auth.model.CustomUserDetails;
import com.neoframework.common.utils.RegexUtils;
import com.thinkgem.jeesite.modules.sys.entity.User;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;

/**
 * 处理用户, 账号, 安全相关业务
 *
 * @author Shengzhao Li
 */
@Service("userService")
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    // @Autowired
    // private UserRepository userRepository;

    @Autowired
    private UserDao userDao;

    /**
     * 获取登录用户信息.
     *
     * <p>用户名，博学号，邮箱，手机登录策略：
     * 先判断格式，邮箱（精确）/手机/博学号（），
     * （1）邮箱格式，直接使用邮箱
     * （2）手机格式，先按手机，再按用户名
     * （3）博学号，先按博学号，再按用户名
     * （4）最后按用户名，若还未匹配，则帐号无效</p>
     *
     * <p>同时，为了准确性，在注册时也应该按情况处理一下。
     * {@link com.anilallewar.microservices.user.api.UserController#register()}</p>
     *
     * <p>See also: 密码判断见下面配置
     * {@link OAuthWebFormConfiguration.LoginConfig#configure(
     *      org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder)}</p>
     *
     * @param username 用户名
     * @return UserDetails 用户信息
     * @throws UsernameNotFoundException 查询不到用户异常
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // User user = userRepository.findByUsername(username);

        User queryInfo = new User();
        User user = null;

        if (RegexUtils.isMobile(username)) {
            logger.info("login try as mobile");
            queryInfo.setMobile(username);
            user = userDao.getByMobile(queryInfo);
            if (user == null) {
                throw new UsernameNotFoundException("Not found any user for username[" + username + "]");
            } else if (user.isDeleted()) {
                throw new UsernameNotFoundException("User for username[" + username + "] deleted");
            }
        }

        if (user == null) {
            logger.info("login try as username");
            queryInfo.setLoginName(username);
            user = userDao.getByLoginName(queryInfo);
            if (user == null || user.isDeleted()) { // user.archived()
                throw new UsernameNotFoundException("Not found any user for username[" + username + "] or deleted");
            }
        }

        return new CustomUserDetails(checkUser(user));
    }

    private User checkUser(User user) {
        // TODO: 查询角色

        // TODO: 检测账号状态：过期直接返回过期状态；未激活的激活，等等

        return user;
    }

    @Override
    public UserVo loadCurrentUserJsonDto() {
        // TODO: 待确认 SecurityContextHolder.getContext() 是否正常
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final Object principal = authentication.getPrincipal();

        if (authentication instanceof OAuth2Authentication && (principal instanceof String
                || principal instanceof org.springframework.security.core.userdetails.User)) {
            return loadOauthUserJsonDto((OAuth2Authentication) authentication);
        } else {
            final CustomUserDetails userDetails = (CustomUserDetails) principal;
            // return new UserVo(userRepository.findByGuid(userDetails.user().guid()));
            return new UserVo(userDao.get(userDetails.user().getId()));
        }
    }

    private UserVo loadOauthUserJsonDto(OAuth2Authentication oAuth2Authentication) {
        UserVo userJsonDto = new UserVo();
        userJsonDto.setName(oAuth2Authentication.getName()); // setUsername

        final Collection<GrantedAuthority> authorities = oAuth2Authentication.getAuthorities();
        for (GrantedAuthority authority : authorities) {
            // userJsonDto.getPrivileges().add(authority.getAuthority());
            userJsonDto.getPrivileges().add(authority.getAuthority());
        }

        return userJsonDto;
    }

    // /**
    // * 获取用户角色.
    // *
    // * @param user
    // * @return
    // */
    // public List<Role> findUserRoles(User user) {
    //
    // try {
    // return userDao.findUserRoles(user);
    // } catch (Exception e) {
    // logger.warn("查询角色出错", e);
    // }
    //
    // return null;
    // }

    /**
     * Gets user.
     *
     * @param id the id
     * @return the user
     */
    public User getUser(String id) {
        User user = userDao.get(id);
        if (null != user) {
            user = checkUser(user);
        }
        return user;
    }
}
