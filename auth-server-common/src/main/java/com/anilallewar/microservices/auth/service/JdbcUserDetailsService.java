
package com.anilallewar.microservices.auth.service;

import java.util.LinkedList;
import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * 目前不使用这个服务.
 * ahming marks: TODO：这个例子有没有非常值得借鉴的地方？ 可以完好支持多种帐号登录？如用户名/邮箱/手机等？
 *
 * @deprecated 留待参考
 */
@Configuration
public class JdbcUserDetailsService implements UserDetailsService {

    private List<UserDetailsService> uds = new LinkedList<>();

    public JdbcUserDetailsService() {
        // Default constructor
    }

    /**
     * Add the default user detail service or any other user detail service so
     * that we can validate the user.
     *
     * @param srv the srv
     */
    public void addService(UserDetailsService srv) {
        uds.add(srv);
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        if (uds != null) {
            for (UserDetailsService srv : uds) {
                try {
                    final UserDetails details = srv.loadUserByUsername(userName);
                    if (details != null) {
                        return details;
                    }
                } catch (UsernameNotFoundException ex) {
                    assert ex != null;
                } catch (Exception ex) {
                    throw ex;
                }
            }
        }

        throw new UsernameNotFoundException("Unknown user");
    }
}
