
package com.anilallewar.microservices.auth.service;

// import com.monkeyk.sos.domain.dto.UserFormDto;
// import com.monkeyk.sos.domain.dto.UserVo;
// import com.monkeyk.sos.domain.dto.UserOverviewDto;

import com.neoframework.biz.api.common.vo.UserVo;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * The interface User service.
 *
 * @author Shengzhao Li
 */
public interface UserService extends UserDetailsService {

    // /**
    // * 获取用户角色.
    // *
    // * @param user
    // * @return
    // */
    // List<Role> findUserRoles(User user);

    /**
     * Load current user json dto user vo.
     *
     * @return the user vo
     */
    UserVo loadCurrentUserJsonDto();

    // UserOverviewDto loadUserOverviewDto(UserOverviewDto overviewDto);
    //
    // boolean isExistedUsername(String username);
    //
    // String saveUser(UserFormDto formDto);
}
